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
import org.apache.tools.ant.BuildException;
import org.seasar.dbflute.helper.jdbc.context.DfDataSourceContext;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerBase;
import org.seasar.extension.dbcp.impl.ConnectionWrapperImpl;

public class DfSimpleDataSourceCreator implements DfDataSourceCreator {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSimpleDataSourceCreator.class);
    
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

    /** Password */
    protected boolean _autoCommit;

    /** Connection */
    protected Connection _conn;

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

    /**
     * Set the autoCommit for the DB connection.
     *
     * @param autoCommit Is auto commit?
     */
    public void setAutoCommit(boolean autoCommit) {
        this._autoCommit = autoCommit;
    }

    public void create() {
        if (!DfDataSourceContext.isExistDataSource()) {
            _log.info("...create()");
            DfDataSourceContext.setDataSource(new DfSimpleDataSource(this));
        }
    }

    public void commit() {
        if (DfDataSourceContext.isExistDataSource()) {
            final DataSource dataSource = DfDataSourceContext.getDataSource();
            try {
                final Connection connection = dataSource.getConnection();
                if (!connection.getAutoCommit()) {
                    _log.info("...commit()");
                    connection.commit();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void destroy() {
        if (DfDataSourceContext.isExistDataSource()) {
            final DataSource dataSource = DfDataSourceContext.getDataSource();
            Connection connection;
            try {
                connection = dataSource.getConnection();
                if (!connection.getAutoCommit()) {
                    _log.info("...rollback()");
                    connection.rollback();
                }
                if (connection instanceof DfSimpleConnection) {
                    _log.info("...closeReally()");
                    ((DfSimpleConnection) connection).closeReally();
                } else {
                    _log.info("...close()");
                    connection.close();
                }
            } catch (SQLException ignored) {
                ignored.printStackTrace();
            }
        }
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
    }

    public Connection getConnection() {
        if (_conn == null) {
            _conn = new DfSimpleConnection(newConnection());
        }
        return _conn;
    }

    protected Connection newConnection() {
        Connection connection = null;
        final Driver driverInstance = newDriver();
        final Properties dbInfoProp = new Properties();
        dbInfoProp.put("user", _userId);
        dbInfoProp.put("password", _password);
        try {
            connection = driverInstance.connect(_url, dbInfoProp);
        } catch (SQLException e) {
            throw new BuildException("Driver#connect() threw the exception: _url=" + _url, e);
        }
        if (connection == null) {
            throw new BuildException("Driver doesn't understand the URL: _url=" + _url);
        }
        try {
            connection.setAutoCommit(_autoCommit);
        } catch (SQLException e) {
            String msg = "Connection#setAutoCommit() threw the exception: _autocommit=";
            throw new BuildException(msg + _autoCommit, e);
        }
        return connection;
    }

    protected Driver newDriver() {
        final Driver driverInstance;
        try {
            final Class dc = Class.forName(_driver);
            driverInstance = (Driver) dc.newInstance();
        } catch (ClassNotFoundException e) {
            String msg = "Class Not Found: JDBC driver " + _driver + " could not be loaded.";
            throw new BuildException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "Illegal Access: JDBC driver " + _driver + " could not be loaded.";
            throw new BuildException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Instantiation Exception: JDBC driver " + _driver + " could not be loaded.";
            throw new BuildException(msg, e);
        }
        return driverInstance;
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
}
