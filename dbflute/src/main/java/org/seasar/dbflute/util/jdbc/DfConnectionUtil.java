package org.seasar.dbflute.util.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfConnectionUtil {

    public static void close(Connection conn) {
        if (conn == null)
            return;
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Statement createStatement(Connection conn) {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static PreparedStatement prepareStatement(Connection conn, String sql) {
        try {
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static DatabaseMetaData getMetaData(Connection conn) {
        try {
            return conn.getMetaData();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}