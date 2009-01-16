package org.seasar.dbflute.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * The wrapper of data source.
 * @author jflute
 */
public class DataSourceWrapper implements DataSource {

    private final DataSource _dataSource;
    private final DataSourceHandler _dataSourceHandler;

    public DataSourceWrapper(DataSource dataSource, DataSourceHandler dataSourceHandler) {
        if (dataSource == null) {
            String msg = "The argument 'dataSource' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        if (dataSourceHandler == null) {
            String msg = "The argument 'dataSourceHandler' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _dataSource = dataSource;
        _dataSourceHandler = dataSourceHandler;
    }

    public Connection getConnection() throws SQLException {
        return _dataSourceHandler.getConnection(_dataSource);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return _dataSource.getConnection(username, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return _dataSource.getLogWriter();
    }

    public int getLoginTimeout() throws SQLException {
        return _dataSource.getLoginTimeout();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        _dataSource.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        _dataSource.setLoginTimeout(seconds);
    }
}
