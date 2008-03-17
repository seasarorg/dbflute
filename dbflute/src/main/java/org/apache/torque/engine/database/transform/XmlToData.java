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
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A Class that is used to parse an input xml schema file and creates and
 * AppData java structure. <br>
 * It uses apache Xerces to do the xml parsing.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:fedor.karpelevitch@home.com">Fedor Karpelevitch</a>
 * @version $Id$
 */
public class XmlToData extends DefaultHandler implements EntityResolver {
    /** Logging class from commons.logging */
    private static Log log = LogFactory.getLog(XmlToData.class);
    private Database database;
    private List<DataRow> data;
    private String dtdFileName;
    private File dtdFile;
    private InputSource dataDTD;

    private static SAXParserFactory saxFactory;

    static {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(true);
    }

    /**
     * Default custructor
     */
    public XmlToData(Database database, String dtdFilePath) throws MalformedURLException, IOException {
        this.database = database;
        dtdFile = new File(dtdFilePath);
        this.dtdFileName = "file://" + dtdFile.getName();
        dataDTD = new InputSource(dtdFile.toURL().openStream());
    }

    /**
     *
     */
    public List parseFile(String xmlFile) throws Exception {
        data = new ArrayList<DataRow>();

        SAXParser parser = saxFactory.newSAXParser();

        FileReader fr = new FileReader(xmlFile);
        BufferedReader br = new BufferedReader(fr);
        try {
            InputSource is = new InputSource(br);
            parser.parse(is, this);
        } finally {
            br.close();
        }
        return data;
    }

    /**
     * Handles opening elements of the xml file.
     */
    public void startElement(String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        try {
            if (rawName.equals("dataset")) {
                //ignore <dataset> for now.
            } else {
                Table table = database.getTable(rawName);

                if (table == null) {
                    throw new SAXException("Table '" + rawName + "' unknown");
                }
                List<ColumnValue> columnValues = new ArrayList<ColumnValue>();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Column col = table.getColumn(attributes.getQName(i));

                    if (col == null) {
                        throw new SAXException("Column " + attributes.getQName(i) + " in table " + rawName
                                + " unknown.");
                    }

                    String value = attributes.getValue(i);
                    columnValues.add(new ColumnValue(col, value));
                }
                data.add(new DataRow(table, columnValues));
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    /**
     * called by the XML parser
     *
     * @return an InputSource for the database.dtd file
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        try {
            if (dataDTD != null && dtdFileName.equals(systemId)) {
                log.info("Resolver: used " + dtdFile.getPath());
                return dataDTD;
            } else {
                log.info("Resolver: used " + systemId);
                return getInputSource(systemId);
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * get an InputSource for an URL String
     *
     * @param urlString
     * @return an InputSource for the URL String
     */
    public InputSource getInputSource(String urlString) throws IOException {
        URL url = new URL(urlString);
        InputSource src = new InputSource(url.openStream());
        return src;
    }

    /**
     *
     */
    public class DataRow {
        private Table table;
        private List columnValues;

        public DataRow(Table table, List columnValues) {
            this.table = table;
            this.columnValues = columnValues;
        }

        public Table getTable() {
            return table;
        }

        public List getColumnValues() {
            return columnValues;
        }
    }

    /**
     *
     */
    public class ColumnValue {
        private Column col;
        private String val;

        public ColumnValue(Column col, String val) {
            this.col = col;
            this.val = val;
        }

        public Column getColumn() {
            return col;
        }

        public String getValue() {
            return val;
        }

        public String getEscapedValue() {
            StringBuffer sb = new StringBuffer();
            sb.append("'");
            sb.append(StringUtils.replace(val, "'", "\\'"));
            sb.append("'");
            return sb.toString();
        }
    }
}
