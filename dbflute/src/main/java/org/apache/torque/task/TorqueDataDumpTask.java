package org.apache.torque.task;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.task.bs.DfAbstractDbMetaTexenTask;

import com.workingdogs.village.QueryDataSet;
import com.workingdogs.village.Record;

/**
 * An extended Texen task used for dumping data from db into XML
 *
 * @author Modified by mkubo
 */
public class TorqueDataDumpTask extends DfAbstractDbMetaTexenTask {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(TorqueDataDumpTask.class);

    /**
     * Database name.
     */
    private String _databaseName;

    /**
     * Database URL used for JDBC connection.
     */
    private String _databaseUrl;

    /**
     * Database driver used for JDBC connection.
     */
    private String _databaseDriver;

    /**
     * Database user used for JDBC connection.
     */
    private String _databaseUser;

    /**
     * Database password used for JDBC connection.
     */
    private String _databasePassword;

    /**
     * The database connection used to retrieve the data to dump.
     */
    private Connection _conn;

    /**
     * The statement used to acquire the data to dump.
     */
    private Statement _stmt;

    /**
     * Get the database name to dump
     *
     * @return  The DatabaseName value
     */
    public String getDatabaseName() {
        return _databaseName;
    }

    /**
     * Set the database name
     *
     * @param  v The new DatabaseName value
     */
    public void setDatabaseName(String v) {
        _databaseName = v;
    }

    /**
     * Get the database url
     *
     * @return  The DatabaseUrl value
     */
    public String getDatabaseUrl() {
        return _databaseUrl;
    }

    /**
     * Set the database url
     *
     * @param  v The new DatabaseUrl value
     */
    public void setDatabaseUrl(String v) {
        _databaseUrl = v;
    }

    /**
     * Get the database driver name
     *
     * @return  String database driver name
     */
    public String getDatabaseDriver() {
        return _databaseDriver;
    }

    /**
     * Set the database driver name
     *
     * @param  v The new DatabaseDriver value
     */
    public void setDatabaseDriver(String v) {
        _databaseDriver = v;
    }

    /**
     * Get the database user
     *
     * @return  String database user
     */
    public String getDatabaseUser() {
        return _databaseUser;
    }

    /**
     * Set the database user
     *
     * @param  v The new DatabaseUser value
     */
    public void setDatabaseUser(String v) {
        _databaseUser = v;
    }

    /**
     * Get the database password
     *
     * @return  String database password
     */
    public String getDatabasePassword() {
        return _databasePassword;
    }

    /**
     * Set the database password
     *
     * @param  v The new DatabasePassword value
     */
    public void setDatabasePassword(String v) {
        _databasePassword = v;
    }

    protected boolean isUseDataSource() {
        return false;
    }
    
    /**
     * Initializes initial context
     *
     * @return the context
     * @throws Exception generic exception
     */
    public Context initControlContext() throws Exception {
        super.initControlContext();

        _context.put("dataset", "all");

        _log.info("Torque - TorqueDataDump starting");
        _log.info("Your DB settings are:");
        _log.info("driver: " + _databaseDriver);
        _log.info("URL: " + _databaseUrl);
        _log.info("user: " + _databaseUser);

        try {
            Class.forName(_databaseDriver);
            _log.info("DB driver instantiated sucessfully");

            _conn = DriverManager.getConnection(_databaseUrl, _databaseUser, _databasePassword);
            _log.info("DB connection established");

            _context.put("tableTool", new TableTool());
        } catch (SQLException e) {
            _log.warn("SQLException while connecting to DB: " + _databaseUrl, e);
        } catch (ClassNotFoundException e) {
            _log.warn("Cannot load driver: " + _databaseDriver, e);
        }
        _context.put("escape", new org.apache.velocity.anakia.Escape());
        return _context;
    }

    /**
     * Closes the db-connection, overriding the <code>cleanup()</code> hook
     * method in <code>TexenTask</code>.
     *
     * @throws Exception Database problem while closing resource.
     */
    protected void cleanup() throws Exception {
        if (_stmt != null) {
            _stmt.close();
        }

        if (_conn != null) {
            _conn.close();
        }
    }

    /**
     *  A nasty do-it-all tool class. It serves as:
     *  <ul>
     *  <li>context tool to fetch a table iterator</li>
     *  <li>the abovenamed iterator which iterates over the table</li>
     *  <li>getter for the table fields</li>
     *  </ul>
     *
     */
    public class TableTool implements Iterator {
        /** querydataset */
        private QueryDataSet qds;
        /** is empty */
        private boolean isEmpty;
        /** current index */
        private int curIndex = -1;
        /** current record */
        private Record curRec = null;

        /**
         *  Constructor for the TableTool object
         */
        public TableTool() {
        }

        /**
         * Constructor for the TableTool object
         *
         * @param qds Description of Parameter
         * @throws Exception Problem using database record set cursor.
         */
        protected TableTool(QueryDataSet qds) throws Exception {
            this.qds = qds;
            this.qds.fetchRecords();
            this.isEmpty = !(qds.size() > 0);
        }

        /**
         * Fetches an <code>Iterator</code> for the data in the named table.
         *
         * @param  tableName Description of Parameter
         * @return <code>Iterator</code> for the fetched data.
         * @throws Exception Problem creating connection or executing query.
         */
        public TableTool fetch(String tableName) throws Exception {
            _log.info("Fetching data for table " + tableName);

            // Set Statement object in associated TorqueDataDump instance

            final QueryDataSet queryDataSet;
            try {
                queryDataSet = new QueryDataSet(_conn, "SELECT * FROM " + tableName);
            } catch (Exception e) {
                _log.warn("new QueryDataSet() threw the exception: tableName=" + tableName, e);
                throw e;
            }

            final TableTool tableTool;
            try {
                tableTool = new TableTool(queryDataSet);
            } catch (Exception e) {
                _log.warn("new TableTool threw the exception: tableName=" + tableName, e);
                throw e;
            } catch (Throwable e) {
                _log.warn("new TableTool threw the throwable: tableName=" + tableName, e);
                throw new Exception(e);
            }
            return tableTool;
        }

        /**
         * check if there are more records in the QueryDataSet
         *
         * @return true if there are more records
         */
        public boolean hasNext() {
            try {
                return ((this.curIndex < this.qds.size() - 1) && (!isEmpty));
            } catch (Exception se) {
                System.err.println("Exception :");
                se.printStackTrace();
            }
            return false;
        }

        /**
         * load the next record from the QueryDataSet
         *
         * @return Description of the Returned Value
         * @throws NoSuchElementException Description of Exception
         */
        public Object next() throws NoSuchElementException {
            try {
                System.err.print(".");
                this.curRec = this.qds.getRecord(++curIndex);
            } catch (Exception se) {
                System.err.println("Exception while iterating:");
                se.printStackTrace();
                throw new NoSuchElementException(se.getMessage());
            }
            return this;
        }

        /**
         * Returns the value for the column
         *
         * @param  columnName name of the column
         * @return  value of the column or null if it doesn't exist
         */
        public String get(String columnName) {
            try {
                return (this.curRec.getValue(columnName).asString());
            } catch (Exception se) {
                log("Exception fetching value " + columnName + ": " + se.getMessage(), Project.MSG_ERR);
            }
            return null;
        }

        /**
         * unsupported! always throws Exception
         *
         * @throws UnsupportedOperationException unsupported
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }
}
