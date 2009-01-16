package org.seasar.dbflute.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * The handler of data source.
 * @author jflute
 */
public interface DataSourceHandler {

    /**
     * Get the connection from the data source.
     * @param dataSource The data source. (NotNull)
     * @return The database connection. (NotNull)
     * @throws java.sql.SQLException
     */
    Connection getConnection(DataSource dataSource) throws SQLException;
}
