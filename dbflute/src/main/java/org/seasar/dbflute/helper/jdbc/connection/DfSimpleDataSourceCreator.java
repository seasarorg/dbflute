/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.jdbc.connection;

import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.helper.jdbc.context.DfDataSourceContext;

public class DfSimpleDataSourceCreator implements DfDataSourceCreator {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSimpleDataSourceCreator.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** DB driver. */
    protected String _driver;

    /** DB URL. */
    protected String _url;

    /** User name. */
    protected String _userId;

    /** Password */
    protected String _password;

    /** Connection properties. */
    protected Properties _connectionProperties;

    /** Is the mode auto commit? */
    protected boolean _autoCommit;

    /** Cached connection object. */
    protected Connection _cachedConnection;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public void create() throws SQLException {
        if (!DfDataSourceContext.isExistDataSource()) {
            _log.info("...Creating data source:");
            _log.info("  driver = " + _driver);
            _log.info("  url    = " + _url);
            _log.info("  user   = " + _userId);
            DfDataSourceContext.setDataSource(new DfSimpleDataSource(this));
        }
    }

    public void commit() throws SQLException {
        if (DfDataSourceContext.isExistDataSource()) {
            final DataSource dataSource = DfDataSourceContext.getDataSource();
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                if (!conn.getAutoCommit()) {
                    _log.info("...commit()");
                    conn.commit();
                }
            } catch (SQLException e) {
                String msg = "Failed to commit the conection: conn=" + conn;
                throw new DfJDBCException(msg, e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        }
    }

    public void destroy() throws SQLException {
        if (DfDataSourceContext.isExistDataSource()) {
            final DataSource dataSource = DfDataSourceContext.getDataSource();
            Connection conn;
            try {
                conn = dataSource.getConnection();
                if (!conn.getAutoCommit()) {
                    _log.info("...rollback()");
                    conn.rollback();
                }
                if (conn instanceof DfSimpleConnection) {
                    _log.info("...closeReally()");
                    ((DfSimpleConnection) conn).closeReally();
                } else {
                    _log.info("...close()");
                    conn.close();
                }
            } catch (SQLException ignored) {
            } finally {
                DfDataSourceContext.clearDataSource();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (_cachedConnection == null) {
            final Connection conn = createConnection();
            _cachedConnection = new DfSimpleConnection(conn);
        }
        return _cachedConnection;
    }

    protected Connection createConnection() throws SQLException {
        Connection conn = null;
        final Driver driverInstance = newDriver();
        final Properties info = new Properties();
        if (_connectionProperties != null && !_connectionProperties.isEmpty()) {
            info.putAll(_connectionProperties);
        }
        info.put("user", _userId);
        info.put("password", _password);

        try {
            conn = driverInstance.connect(_url, info);
        } catch (SQLException e) {
            String msg = "Driver#connect() threw the exception:";
            msg = msg + " url=" + _url + " user=" + _userId;
            throw new DfJDBCException(msg, e);
        }
        if (conn == null) {
            String msg = "Driver doesn't understand the URL: _url=" + _url;
            throw new DfJDBCException(msg);
        }
        try {
            conn.setAutoCommit(_autoCommit);
        } catch (SQLException e) {
            String msg = "Connection#setAutoCommit() threw the exception:";
            msg = msg + " autocommit=" + _autoCommit;
            throw new DfJDBCException(msg, e);
        }
        return conn;
    }

    protected Driver newDriver() {
        final Driver driverInstance;
        try {
            final Class<?> dc = Class.forName(_driver);
            driverInstance = (Driver) dc.newInstance();
        } catch (ClassNotFoundException e) {
            String msg = "Class Not Found: JDBC driver " + _driver + " could not be loaded.";
            throw new IllegalStateException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "Illegal Access: JDBC driver " + _driver + " could not be loaded.";
            throw new IllegalStateException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Instantiation Exception: JDBC driver " + _driver + " could not be loaded.";
            throw new IllegalStateException(msg, e);
        }
        return driverInstance;
    }

    public static class DfSimpleDataSource implements DataSource {

        protected DfSimpleDataSourceCreator _dataSourceProvider;

        public DfSimpleDataSource(DfSimpleDataSourceCreator dataSourceProvider) {
            _dataSourceProvider = dataSourceProvider;
        }

        public Connection getConnection() throws SQLException {
            return _dataSourceProvider.getConnection();
        }

        public Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException("Use getConnection()");
        }

        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException("getLogWriter()");
        }

        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException("getLoginTimeout()");
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException("setLoginTimeout()");
        }

        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException("setLoginTimeout()");
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ":" + _dataSourceProvider;
        }
    }

    public static class DfSimpleConnection implements Connection {

        protected Connection _realConnection;

        public DfSimpleConnection(Connection realConnection) {
            _realConnection = realConnection;
        }

        public void clearWarnings() throws SQLException {
            _realConnection.clearWarnings();
        }

        public void close() throws SQLException {
            // _realConnection.close();
        }

        public void closeReally() throws SQLException {
            _realConnection.close();
        }

        public void commit() throws SQLException {
            _realConnection.commit();
        }

        public Statement createStatement() throws SQLException {
            return _realConnection.createStatement();
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return _realConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return _realConnection.createStatement(resultSetType, resultSetConcurrency);
        }

        public boolean getAutoCommit() throws SQLException {
            return _realConnection.getAutoCommit();
        }

        public String getCatalog() throws SQLException {
            return _realConnection.getCatalog();
        }

        public int getHoldability() throws SQLException {
            return _realConnection.getHoldability();
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            return _realConnection.getMetaData();
        }

        public int getTransactionIsolation() throws SQLException {
            return _realConnection.getTransactionIsolation();
        }

        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return _realConnection.getTypeMap();
        }

        public SQLWarning getWarnings() throws SQLException {
            return _realConnection.getWarnings();
        }

        public boolean isClosed() throws SQLException {
            return _realConnection.isClosed();
        }

        public boolean isReadOnly() throws SQLException {
            return _realConnection.isReadOnly();
        }

        public String nativeSQL(String sql) throws SQLException {
            return _realConnection.nativeSQL(sql);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            return _realConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return _realConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            return _realConnection.prepareCall(sql);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            return _realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return _realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return _realConnection.prepareStatement(sql, autoGeneratedKeys);
        }

        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return _realConnection.prepareStatement(sql, columnIndexes);
        }

        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return _realConnection.prepareStatement(sql, columnNames);
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return _realConnection.prepareStatement(sql);
        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            _realConnection.releaseSavepoint(savepoint);
        }

        public void rollback() throws SQLException {
            _realConnection.rollback();
        }

        public void rollback(Savepoint savepoint) throws SQLException {
            _realConnection.rollback(savepoint);
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            _realConnection.setAutoCommit(autoCommit);
        }

        public void setCatalog(String catalog) throws SQLException {
            _realConnection.setCatalog(catalog);
        }

        public void setHoldability(int holdability) throws SQLException {
            _realConnection.setHoldability(holdability);
        }

        public void setReadOnly(boolean readOnly) throws SQLException {
            _realConnection.setReadOnly(readOnly);
        }

        public Savepoint setSavepoint() throws SQLException {
            return _realConnection.setSavepoint();
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            return _realConnection.setSavepoint(name);
        }

        public void setTransactionIsolation(int level) throws SQLException {
            _realConnection.setTransactionIsolation(level);
        }

        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            _realConnection.setTypeMap(map);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{url=" + _url + ", user=" + _userId + ", prop=" + _connectionProperties + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Set the JDBC driver to be used.
     * @param driver driver class name
     */
    public void setDriver(String driver) {
        this._driver = driver;
    }

    /**
     * Set the DB connection URL.
     * @param url connection URL
     */
    public void setUrl(String url) {
        this._url = url;
    }

    /**
     * Set the user name for the DB connection.
     * @param userId database user
     */
    public void setUserId(String userId) {
        this._userId = userId;
    }

    /**
     * Set the password for the DB connection.
     * @param password database password
     */
    public void setPassword(String password) {
        this._password = password;
    }

    /**
     * Set the connection properties for the DB connection.
     * @param connectionProperties The connection properties.
     */
    public void setConnectionProperties(Properties connectionProperties) {
        this._connectionProperties = connectionProperties;
    }

    /**
     * Set the autoCommit for the DB connection.
     * @param autoCommit Is auto commit?
     */
    public void setAutoCommit(boolean autoCommit) {
        this._autoCommit = autoCommit;
    }
}
