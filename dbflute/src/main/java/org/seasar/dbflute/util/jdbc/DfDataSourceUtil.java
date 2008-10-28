package org.seasar.dbflute.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfDataSourceUtil {

    public static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}