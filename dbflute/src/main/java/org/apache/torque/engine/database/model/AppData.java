package org.apache.torque.engine.database.model;

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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.transform.DTDResolver;

import org.xml.sax.Attributes;

/**
 * A class for holding application data structures.
 *
 * @author Modified by mkubo
 */
public class AppData {
    /** Log instance. */
    private static Log _log = LogFactory.getLog(AppData.class);

    /**
     * The list of databases for this application.
     */
    private List<Database> _dbList = new ArrayList<Database>(5);

    /**
     * The table of idiosyncrasies for various database types.
     */
    private Map<String, Properties> _idiosyncrasyTable = new HashMap<String, Properties>(8);

    /**
     * The type for our databases.
     */
    private String _databaseType;

    /**
     * The base of the path to the properties file, including trailing slash.
     */
    private String _basePropsFilePath;

    /**
     * Name of the database. Only one database definition
     * is allowed in one XML descriptor.
     */
    private String _name;

    // flag to complete initialization only once.
    private boolean _isInitialized;

    /**
     * Creates a new instance for the specified database type.
     *
     * @param databaseType The default type for any databases added to
     * this application model.
     * @param basePropsFilePath The base of the path to the properties
     * file, including trailing slash.
     */
    public AppData(String databaseType, String basePropsFilePath) {
        this._basePropsFilePath = basePropsFilePath;
        this._databaseType = databaseType;
    }

    /**
     * Each database has its own list of idiosyncrasies which can be
     * configured by editting its <code>db.props</code> file.
     *
     * @param databaseType The type of database to retrieve the
     * properties of.
     * @return The idiosyncrasies of <code>databaseType</code>.
     * @exception EngineException Couldn't locate properties file.
     */
    protected Properties getIdiosyncrasies(String databaseType) throws EngineException {
        Properties idiosyncrasies = (Properties) _idiosyncrasyTable.get(databaseType);

        // If we haven't yet loaded the properties and we have the
        // information to do so, proceed with loading.
        if (idiosyncrasies == null && _basePropsFilePath != null && databaseType != null) {
            idiosyncrasies = new Properties();
            File propsFile = new File(_basePropsFilePath + databaseType, "db.props");
            if (propsFile.exists()) {
                try {
                    idiosyncrasies.load(new FileInputStream(propsFile));
                } catch (Exception e) {
                    _log.error(e, e);
                }
                _idiosyncrasyTable.put(databaseType, idiosyncrasies);
            } else {
                try {
                    String path = '/' + _basePropsFilePath + databaseType + "/db.props";
                    idiosyncrasies.load(getClass().getResourceAsStream(path));
                } catch (Exception e) {
                    _log.error(e, e);
                }
            }

            if (idiosyncrasies.isEmpty()) {
                throw new EngineException("Database-specific properties " + "file does not exist: "
                        + propsFile.getAbsolutePath());
            }
        }
        return idiosyncrasies;
    }

    /**
     * Set the name of the database.
     *
     * @param name of the database.
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Get the name of the database.
     *
     * @return String name
     */
    public String getName() {
        return _name;
    }

    /**
     * Get the short name of the database (without the '-schema' postfix).
     *
     * @return String name
     */
    public String getShortName() {
        return StringUtils.replace(_name, "-schema", "");
    }

    /**
     * Get database object.
     *
     * @return Database database
     */
    public Database getDatabase() throws EngineException {
        doFinalInitialization();
        return (Database) _dbList.get(0);
    }

    /**
     * Return an array of all databases
     *
     * @return Array of Database objects
     */
    public Database[] getDatabases() throws EngineException {
        doFinalInitialization();
        int size = _dbList.size();
        Database[] dbs = new Database[size];
        for (int i = 0; i < size; i++) {
            dbs[i] = (Database) _dbList.get(i);
        }
        return dbs;
    }

    /**
     * Returns whether this application has multiple databases.
     *
     * @return true if the application has multiple databases
     */
    public boolean hasMultipleDatabases() {
        return (_dbList.size() > 1);
    }

    /**
     * Return the database with the specified name.
     *
     * @param name database name
     * @return A Database object.  If it does not exist it returns null
     */
    public Database getDatabase(String name) throws EngineException {
        doFinalInitialization();
        for (Iterator i = _dbList.iterator(); i.hasNext();) {
            Database db = (Database) i.next();
            if (db.getName().equals(name)) {
                return db;
            }
        }
        return null;
    }

    /**
     * An utility method to add a new database from an xml attribute.
     *
     * @param attrib the xml attributes
     * @return the database
     */
    public Database addDatabase(Attributes attrib) {
        Database db = new Database();
        db.loadFromXML(attrib);
        addDatabase(db);
        return db;
    }

    /**
     * Add a database to the list and sets the AppData property to this
     * AppData
     *
     * @param db the database to add
     */
    public void addDatabase(Database db) {
        db.setAppData(this);
        if (db.getName() == null) {
            /** @task check this */
            db.setName("default"); // Torque.getDefaultDB());
        }
        if (db.getDatabaseType() == null) {
            db.setDatabaseType(_databaseType);
        }
        _dbList.add(db);
    }

    private void doFinalInitialization() throws EngineException {
        if (!_isInitialized) {
            Iterator dbs = _dbList.iterator();
            while (dbs.hasNext()) {
                ((Database) dbs.next()).doFinalInitialization();
            }
            _isInitialized = true;
        }
    }

    /**
     * Creats a string representation of this AppData.
     * The representation is given in xml format.
     *
     * @return representation in xml format
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("<?xml version=\"1.0\"?>\n");
        result.append("<!DOCTYPE database SYSTEM \"" + DTDResolver.WEB_SITE_DTD + "\">\n");
        result.append("<!-- Autogenerated by SQLToXMLSchema! -->\n");
        for (Iterator i = _dbList.iterator(); i.hasNext();) {
            result.append(i.next());
        }
        return result.toString();
    }
}
