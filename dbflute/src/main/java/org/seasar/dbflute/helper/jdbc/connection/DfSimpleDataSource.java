package org.seasar.dbflute.helper.jdbc.connection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class DfSimpleDataSource implements DataSource {

    protected final DfConnectionProvider _dataSourceProvider;

    public DfSimpleDataSource(DfDataSourceHandler dataSourceProvider) {
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

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("unwrap()");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("isWrapperFor()");
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":" + _dataSourceProvider;
    }
}
