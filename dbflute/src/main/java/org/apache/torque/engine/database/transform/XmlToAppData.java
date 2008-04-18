package org.apache.torque.engine.database.transform;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Index;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.Unique;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A Class that is used to parse an input xml schema file and creates an AppData java structure.
 * @author Modified by jflute
 */
public class XmlToAppData extends DefaultHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private AppData app;
    private Database currDB;
    private Table currTable;
    private Column currColumn;
    private ForeignKey currFK;
    private Index currIndex;
    private Unique currUnique;

    private boolean firstPass;
    private boolean isExternalSchema;
    private String currentPackage;
    private String currentXmlFile;
    private String defaultPackage;

    private static SAXParserFactory saxFactory;

    /** remember all files we have already parsed to detect looping. */
    private Vector<String> alreadyReadFiles;

    /** this is the stack to store parsing data */
    private Stack<ParseStackElement> parsingStack = new Stack<ParseStackElement>();

    static {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(true);
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Creates a new instance for the specified database type.
     * @param databaseType The type of database for the application.
     * @param defaultPackage the default java package used for the om
     * file, including trailing slash.
     */
    public XmlToAppData(String databaseType, String defaultPackage) {
        app = new AppData(databaseType);
        this.defaultPackage = defaultPackage;
        firstPass = true;
    }

    // ===================================================================================
    //                                                                               Parse
    //                                                                               =====
    /**
     * Parses a XML input file and returns a newly created and
     * populated AppData structure.
     * @param xmlFile The input file to parse.
     * @return AppData populated by <code>xmlFile</code>.
     */
    public AppData parseFile(String xmlFile) {
        try {
            // in case I am missing something, make it obvious
            if (!firstPass) {
                throw new Error("No more double pass");
            }
            // check to see if we alread have parsed the file
            if ((alreadyReadFiles != null) && alreadyReadFiles.contains(xmlFile)) {
                return app;
            } else if (alreadyReadFiles == null) {
                alreadyReadFiles = new Vector<String>(3, 1);
            }

            // remember the file to avoid looping
            alreadyReadFiles.add(xmlFile);

            currentXmlFile = xmlFile;

            SAXParser parser = saxFactory.newSAXParser();

            FileReader fr = null;
            try {
                fr = new FileReader(xmlFile);
            } catch (FileNotFoundException fnfe) {
                throw new FileNotFoundException(new File(xmlFile).getAbsolutePath());
            }
            BufferedReader br = new BufferedReader(fr);
            try {
                // Comment out!
                //                log.info("Parsing file: '" + (new File(xmlFile)).getName() + "'");
                InputSource is = new InputSource(br);
                parser.parse(is, this);
            } finally {
                br.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!isExternalSchema) {
            firstPass = false;
        }
        return app;
    }

    /**
     * EntityResolver implementation. Called by the XML parser
     *
     * @param publicId The public identifier of the external entity
     * @param systemId The system identifier of the external entity
     * @return an InputSource for the database.dtd file
     * @see org.apache.torque.engine.database.transform.DTDResolver#resolveEntity(String, String)
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        try {
            return new DTDResolver().resolveEntity(publicId, systemId);
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    /**
     * Handles opening elements of the xml file.
     *
     * @param uri
     * @param localName The local name (without prefix), or the empty string if
     *         Namespace processing is not being performed.
     * @param rawName The qualified name (with prefix), or the empty string if
     *         qualified names are not available.
     * @param attributes The specified or defaulted attributes
     */
    public void startElement(String uri, String localName, String rawName, Attributes attributes) {
        try {
            if (rawName.equals("database")) {
                if (isExternalSchema) {
                    currentPackage = attributes.getValue("package");
                    if (currentPackage == null) {
                        currentPackage = defaultPackage;
                    }
                } else {
                    currDB = app.addDatabase(attributes);
                    if (currDB.getPackage() == null) {
                        currDB.setPackage(defaultPackage);
                    }
                }
            } else if (rawName.equals("external-schema")) {
                String xmlFile = attributes.getValue("filename");
                if (xmlFile.charAt(0) != '/') {
                    File f = new File(currentXmlFile);
                    xmlFile = new File(f.getParent(), xmlFile).getPath();
                }

                // put current state onto the stack
                ParseStackElement.pushState(this);

                isExternalSchema = true;

                parseFile(xmlFile);
                // get the last state from the stack
                ParseStackElement.popState(this);
            } else if (rawName.equals("table")) {
                currTable = currDB.addTable(attributes);
                if (isExternalSchema) {
                    currTable.setForReferenceOnly(true);
                    currTable.setPackage(currentPackage);
                }
            } else if (rawName.equals("column")) {
                currColumn = currTable.addColumn(attributes);
            } else if (rawName.equals("inheritance")) {
                currColumn.addInheritance(attributes);
            } else if (rawName.equals("foreign-key")) {
                currFK = currTable.addForeignKey(attributes);
            } else if (rawName.equals("reference")) {
                currFK.addReference(attributes);
            } else if (rawName.equals("index")) {
                currIndex = currTable.addIndex(attributes);
            } else if (rawName.equals("index-column")) {
                currIndex.addColumn(attributes);
            } else if (rawName.equals("unique")) {
                currUnique = currTable.addUnique(attributes);
            } else if (rawName.equals("unique-column")) {
                currUnique.addColumn(attributes);
            } else if (rawName.equals("id-method-parameter")) {
                currTable.addIdMethodParameter(attributes);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles closing elements of the xml file.
     * @param uri
     * @param localName The local name (without prefix), or the empty string if
     *         Namespace processing is not being performed.
     * @param rawName The qualified name (with prefix), or the empty string if
     *         qualified names are not available.
     */
    public void endElement(String uri, String localName, String rawName) {
        // Comment out!
        //        if (log.isDebugEnabled()) {
        //            log.debug("endElement(" + uri + ", " + localName + ", " + rawName + ") called");
        //        }
    }

    /**
     * When parsing multiple files that use nested <external-schema> tags we
     * need to use a stack to remember some values.
     */
    private static class ParseStackElement {
        private boolean isExternalSchema;
        private String currentPackage;
        private String currentXmlFile;
        private boolean firstPass;

        /**
         * @param parser
         */
        public ParseStackElement(XmlToAppData parser) {
            // remember current state of parent object
            isExternalSchema = parser.isExternalSchema;
            currentPackage = parser.currentPackage;
            currentXmlFile = parser.currentXmlFile;
            firstPass = parser.firstPass;

            // push the state onto the stack
            parser.parsingStack.push(this);
        }

        /**
         * Removes the top element from the stack and activates the stored state
         * @param parser
         */
        public static void popState(XmlToAppData parser) {
            if (!parser.parsingStack.isEmpty()) {
                ParseStackElement elem = (ParseStackElement) parser.parsingStack.pop();

                // activate stored state
                parser.isExternalSchema = elem.isExternalSchema;
                parser.currentPackage = elem.currentPackage;
                parser.currentXmlFile = elem.currentXmlFile;
                parser.firstPass = elem.firstPass;
            }
        }

        /**
         * Stores the current state on the top of the stack.
         * @param parser
         */
        public static void pushState(XmlToAppData parser) {
            new ParseStackElement(parser);
        }
    }
}
