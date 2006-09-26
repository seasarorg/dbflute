package org.apache.torque.task;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.torque.helper.TorqueBuildProperties;
import org.apache.torque.helper.TorqueTaskUtil;
import org.apache.torque.helper.stateless.FlClassUtil;

public abstract class TorqueAbstractPlaySQLTask extends Task {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(TorqueDataDumpTask.class);

    // =========================================================================================
    //                                                                                 Attribute
    //                                                                                 =========
    /** DB driver. */
    protected String _driver = null;

    /** DB url. */
    protected String _url = null;

    /** User name. */
    protected String _userId = null;

    /** Password */
    protected String _password = null;

    // =========================================================================================
    //                                                                                  Accessor
    //                                                                                  ========
    /**
     * Set the JDBC driver to be used.
     *
     * @param driver driver class name
     */
    public void setDriver(String driver) {
        this._driver = driver;
    }

    /**
     * Set the DB connection url.
     *
     * @param url connection url
     */
    public void setUrl(String url) {
        this._url = url;
    }

    /**
     * Set the user name for the DB connection.
     *
     * @param userId database user
     */
    public void setUserId(String userId) {
        this._userId = userId;
    }

    /**
     * Set the password for the DB connection.
     *
     * @param password database password
     */
    public void setPassword(String password) {
        this._password = password;
    }

    // =========================================================================================
    //                                                                        Context Properties
    //                                                                        ==================
    public void setContextProperties(String file) {
        final Properties prop = TorqueTaskUtil.getBuildProperties(file, super.project);
        TorqueBuildProperties.getInstance().setContextProperties(prop);
    }

    // =========================================================================================
    //                                                                                   Execute
    //                                                                                   =======
    /**
     * Load the sql file and then execute it.
     *
     * @throws BuildException
     */
    public void execute() throws BuildException {
        try {
            _log.debug("/************************************************************************************");
            assertAttribute();

            int goodSqlCount = 0;
            int totalSqlCount = 0;

            final List<File> fileList = getSqlFileList();
            for (final File file : fileList) {
                if (!file.exists()) {
                    throw new FileNotFoundException("The file '" + file.getPath() + "' does not exist.");
                }

                final TransactionMetaData metaData = new TransactionMetaData();
                metaData.setAutoCommit(isAutoCommit());
                metaData.setErrorContinue(isErrorContinue());
                metaData.setDriver(_driver);
                metaData.setUrl(_url);
                metaData.setUser(_userId);
                metaData.setPassword(_password);
                metaData.setEncoding(getEncoding());
                metaData.setDelimiter(getDelimiter());

                if (_log.isDebugEnabled()) {
                    final String mitameJushi = "_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/";
                    _log.debug("");
                    _log.debug(mitameJushi + mitameJushi);
                    _log.debug("sqlFile: " + file);
                    _log.debug("_/_/_/_/");
                }
                final TorqueTransaction transaction = new TorqueTransaction(metaData);
                transaction.setSrc(file);
                transaction.setRollbackOnly(isRollbackOnly());
                transaction.runTransaction();

                goodSqlCount = goodSqlCount + transaction.getGoodSqlCount();
                totalSqlCount = totalSqlCount + transaction.getTotalSqlCount();
            }
            _log.debug("*****************/ {" + goodSqlCount + " of " + totalSqlCount + "}");
        } catch (Exception e) {
            _log.warn(getClass().getName() + "#execute() threw the exception!", e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    protected void assertAttribute() {
        if (_driver == null) {
            throw new BuildException("Driver attribute must be set!", location);
        }
        if (_userId == null) {
            throw new BuildException("User Id attribute must be set!", location);
        }
        if (_password == null) {
            throw new BuildException("Password attribute must be set!", location);
        }
        if (_url == null) {
            throw new BuildException("Url attribute must be set!", location);
        }
    }

    /**
     * Get sql-file list.
     * 
     * @return Sql-file list. (NotNull)
     */
    abstract protected List<File> getSqlFileList();

    /**
     * Is auto-commit?
     * 
     * @return Determination.
     */
    abstract protected boolean isAutoCommit();

    /**
     * Is rollback-only?
     * 
     * @return Determination.
     */
    abstract protected boolean isRollbackOnly();

    /**
     * Is error-continue?
     * 
     * @return Determination.
     */
    abstract protected boolean isErrorContinue();

    /**
     * Get encoding.
     * 
     * @return Encoding.
     */
    abstract protected String getEncoding();

    /**
     * Get delimiter.
     * 
     * @return Delimiter.
     */
    abstract protected String getDelimiter();

    public static class TransactionMetaData {
        protected String _driver;
        protected String _url;
        protected String _user;
        protected String _password;
        protected String _encoding;
        protected String _delimiter;
        protected boolean _isErrorContinue;
        protected boolean _isAutoCommit;

        public String getDriver() {
            return _driver;
        }

        public void setDriver(String driver) {
            this._driver = driver;
        }

        public String getUrl() {
            return _url;
        }

        public void setUrl(String url) {
            this._url = url;
        }

        public String getUser() {
            return _user;
        }

        public void setUser(String user) {
            this._user = user;
        }

        public String getPassword() {
            return _password;
        }

        public void setPassword(String password) {
            this._password = password;
        }

        public String getEncoding() {
            return _encoding;
        }

        public void setEncoding(String _encoding) {
            this._encoding = _encoding;
        }

        public boolean isEncodingNull() {
            return (_encoding == null);
        }

        public String getDelimiter() {
            return _delimiter;
        }

        public void setDelimiter(String _delimiter) {
            this._delimiter = _delimiter;
        }

        public boolean isErrorContinue() {
            return _isErrorContinue;
        }

        public void setErrorContinue(boolean isErrorContinue) {
            this._isErrorContinue = isErrorContinue;
        }

        public boolean isAutoCommit() {
            return _isAutoCommit;
        }

        public void setAutoCommit(boolean isAutoCommit) {
            this._isAutoCommit = isAutoCommit;
        }
    }

    /**
     * Contains the definition of a new transaction element.
     * Transactions allow several files or blocks of statements
     * to be executed using the same JDBC connection and commit
     * operation in between.
     */
    public static class TorqueTransaction {

        /** Log instance. */
        private static Log _log = LogFactory.getLog(TorqueTransaction.class);

        protected final TransactionMetaData _metaData;
        protected boolean _isRollbackOnly;
        protected int _goodSqlCount = 0;
        protected int _totalSqlCount = 0;
        protected File _srcFile;

        public TorqueTransaction(TransactionMetaData metaData) {
            _metaData = metaData;
        }

        public void setSrc(File src) {
            this._srcFile = src;
        }

        public int getGoodSqlCount() {
            return _goodSqlCount;
        }

        public int getTotalSqlCount() {
            return _totalSqlCount;
        }

        public void setRollbackOnly(boolean value) {
            _isRollbackOnly = value;
        }

        protected void runTransaction() {
            _goodSqlCount = 0;
            _totalSqlCount = 0;
            if (_srcFile == null) {
                throw new BuildException("Attribute[_srcFile] must not be null.");
            }

            Reader reader = null;
            Connection connection = null;
            Statement statement = null;
            try {
                reader = (_metaData.isEncodingNull()) ? newFileReader() : newInputStreamReader();
                final List<String> sqlList = extractSqlList(reader);

                connection = newConnection();
                statement = newStatement(connection);
                for (String sql : sqlList) {
                    _totalSqlCount++;
                    _log.info(sql);
                    execSQL(statement, sql);
                }
                if (!_metaData.isAutoCommit()) {
                    if (_isRollbackOnly) {
                        connection.rollback();
                    } else {
                        connection.commit();
                    }
                }
            } catch (SQLException e) {
                throw new BuildException("Transaction#runTransaction() threw the exception!", e);
            } finally {
                if (!_metaData.isAutoCommit() && connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException ignore) {
                        _log.warn("Connection#rollback() threw the exception!", ignore);
                    }
                }
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException ignore) {
                    _log.warn("Statement#close() threw the exception!", ignore);
                } finally {
                    statement = null;
                }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ignore) {
                    _log.warn("Connection#close() threw the exception!", ignore);
                } finally {
                    connection = null;
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ignore) {
                    _log.warn("Reader#close() threw the exception: " + reader, ignore);
                } finally {
                    reader = null;
                }
            }
            _log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
            _log.info(_goodSqlCount + " of " + _totalSqlCount + " SQL statements executed successfully.");
            _log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        }

        protected FileReader newFileReader() {
            try {
                return new FileReader(_srcFile);
            } catch (FileNotFoundException e) {
                throw new BuildException("The file does not exist: " + _srcFile, e);
            }
        }

        protected InputStreamReader newInputStreamReader() {
            try {
                return new InputStreamReader(new FileInputStream(_srcFile), _metaData.getEncoding());
            } catch (FileNotFoundException e) {
                throw new BuildException("The file does not exist: " + _srcFile, e);
            } catch (UnsupportedEncodingException e) {
                throw new BuildException("The encoding is unsupported: " + _metaData.getEncoding(), e);
            }
        }

        protected Connection newConnection() {
            Connection connection = null;
            final Driver driverInstance = newDriver();
            final Properties dbInfoProp = new Properties();
            dbInfoProp.put("user", _metaData.getUser());
            dbInfoProp.put("password", _metaData.getPassword());
            try {
                connection = driverInstance.connect(_metaData.getUrl(), dbInfoProp);
            } catch (SQLException e) {
                throw new BuildException("Driver#connect() threw the exception: _url=" + _metaData.getUrl(), e);
            }
            if (connection == null) {
                throw new BuildException("Driver doesn't understand the URL: _url=" + _metaData.getUrl());
            }
            try {
                connection.setAutoCommit(_metaData.isAutoCommit());
            } catch (SQLException e) {
                String msg = "Connection#setAutoCommit() threw the exception: _autocommit=";
                throw new BuildException(msg + _metaData.isAutoCommit(), e);
            }
            return connection;
        }

        protected Driver newDriver() {
            final Driver driverInstance;
            try {
                final Class dc = Class.forName(_metaData.getDriver());
                driverInstance = (Driver) dc.newInstance();
            } catch (ClassNotFoundException e) {
                String msg = "Class Not Found: JDBC driver " + _metaData.getDriver() + " could not be loaded.";
                throw new BuildException(msg, e);
            } catch (IllegalAccessException e) {
                String msg = "Illegal Access: JDBC driver " + _metaData.getDriver() + " could not be loaded.";
                throw new BuildException(msg, e);
            } catch (InstantiationException e) {
                String msg = "Instantiation Exception: JDBC driver " + _metaData.getDriver() + " could not be loaded.";
                throw new BuildException(msg, e);
            }
            return driverInstance;
        }

        protected Statement newStatement(Connection connection) {
            try {
                return connection.createStatement();
            } catch (SQLException e) {
                String msg = "Connection#createStatement() threw the exception: _connection=";
                throw new BuildException(msg + connection, e);
            }
        }

        /**
         * Read the statements from the .sql file and execute them.
         * Lines starting with '//', '--' or 'REM ' are ignored.
         *
         * @param reader
         * @return List.
         */
        protected List<String> extractSqlList(Reader reader) {
            final List<String> sqlList = new ArrayList<String>();
            final BufferedReader in = new BufferedReader(reader);
            final DelimiterChanger delimiterChanger = newDelimterChanger();
            try {
                String sql = "";
                String line = "";
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (isLineComment(line)) {
                        continue;
                    }
                    sql = (sql + " " + line).trim();

                    // SQL defines "--" as a comment to EOL
                    // and in Oracle it may contain a hint
                    // so we cannot just remove it, instead we must end it
                    if (line.indexOf("--") >= 0) {
                        sql = sql + "\n";
                    }

                    if (sql.endsWith(_metaData.getDelimiter())) {
                        sql = sql.substring(0, sql.length() - _metaData.getDelimiter().length()).trim();
                        if ("".equals(sql)) {
                            continue;
                        }

                        if (!delimiterChanger.isDelimiterChanger(sql)) {
                            sqlList.add(sql);
                            sql = "";
                        } else {
                            _metaData.setDelimiter(delimiterChanger.getNewDelimiter(sql, _metaData.getDelimiter()));
                            sql = "";
                        }
                    }
                }
                if (sql.trim().length() != 0) {
                    sqlList.add(sql);// for Last Sql
                }
            } catch (IOException e) {
                throw new IORuntimeException("Threw the exception!", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignore) {
                        ignore.printStackTrace();
                    }
                }
            }
            return sqlList;
        }

        public DelimiterChanger newDelimterChanger() {
            final String databaseName = TorqueBuildProperties.getInstance().stringProp("torque.database");
            final String className = DelimiterChanger.class.getName() + "_" + databaseName;
            DelimiterChanger changer = null;
            try {
                changer = (DelimiterChanger) FlClassUtil.newInstance(className);
            } catch (RuntimeException ignore) {
                String msg = "The database has no delimiter changer: databaseName=";
                _log.debug(msg + databaseName + " className=" + className + " ignore=" + ignore);
                changer = new DelimiterChanger_null();
            }
            return changer;
        }

        protected boolean isLineComment(String line) {
            if (line.startsWith("//") || line.startsWith("--")) {
                return true;
            }
            if (line.length() > 4 && line.substring(0, 4).equalsIgnoreCase("REM ")) {
                return true;
            }
            return false;
        }

        /**
         * Exec the sql statement.
         *
         * @param statement
         * @param sql
         */
        protected void execSQL(Statement statement, String sql) {
            try {
                statement.execute(sql);
                _goodSqlCount++;
            } catch (SQLException e) {
                String msg = "Failed to execute: " + sql;
                if (!_metaData.isErrorContinue()) {
                    throw new SQLRuntimeException(msg, e);
                }
                _log.warn(msg, e);
                _log.warn("" + System.getProperty("line.separator"));
            }
        }
    }

    // =========================================================================================
    //                                                                         Delimiter Changer
    //                                                                         =================
    public static interface DelimiterChanger {
        public boolean isDelimiterChanger(String sql);

        public String getNewDelimiter(String sql, String preDelimiter);
    }

    public static class DelimiterChanger_firebird implements DelimiterChanger {
        public static final String CHANGE_COMMAND = "set term ";
        public static final int CHANGE_COMMAND_LENGTH = CHANGE_COMMAND.length();

        public boolean isDelimiterChanger(String sql) {
            sql = sql.trim();
            if (sql.length() > CHANGE_COMMAND_LENGTH) {
                if (sql.substring(0, CHANGE_COMMAND_LENGTH).equalsIgnoreCase(CHANGE_COMMAND)) {
                    return true;
                }
            }
            return false;
        }

        public String getNewDelimiter(String sql, String preDelimiter) {
            String tmp = sql.substring(CHANGE_COMMAND.length());
            if (tmp.indexOf(" ") >= 0) {
                tmp = tmp.substring(0, tmp.indexOf(" "));
            }
            return tmp;
        }
    }

    public static class DelimiterChanger_null implements DelimiterChanger {

        public boolean isDelimiterChanger(String sql) {
            return false;
        }

        public String getNewDelimiter(String sql, String preDelimiter) {
            return preDelimiter;
        }
    }

    // =========================================================================================
    //                                                                         Runtime Exception
    //                                                                         =================
    public static class SQLRuntimeException extends RuntimeException {
        public static final long serialVersionUID = -1;

        public SQLRuntimeException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    public static class IORuntimeException extends RuntimeException {
        public static final long serialVersionUID = -1;

        public IORuntimeException(String msg, Throwable e) {
            super(msg, e);
        }
    }
}
