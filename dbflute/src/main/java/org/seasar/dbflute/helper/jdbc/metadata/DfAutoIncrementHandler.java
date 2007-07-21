/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.metadata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.dbflute.helper.jdbc.metadata.DfTableNameHandler.DfTableMetaInfo;

/**
 * The handler of auto increment. 
 * 
 * @author jflute
 */
public class DfAutoIncrementHandler extends DfAbstractMetaDataHandler {

    /**
     * Is auto-increment column?
     * 
     * @param conn Connection.
     * @param tableMetaInfo The meta information of table from which to retrieve PK information.
     * @param primaryKeyColumnName Primary-key column-name.
     * @return Auto-increment column name. (Nullable)
     * @throws SQLException
     */
    public boolean isAutoIncrementColumn(Connection conn, DfTableMetaInfo tableMetaInfo, String primaryKeyColumnName)
            throws SQLException {
        final String tableName = tableMetaInfo.getTableName();
        Statement stmt = null;
        ResultSet rs = null;
        String ignoredMessage = null;
        try {
            stmt = conn.createStatement();
            try {
                rs = stmt.executeQuery("SELECT " + primaryKeyColumnName + " FROM " + tableName);
            } catch (SQLException e) {
                // ここでSQLExceptionが発生した場合は、Schema名を付けていないことによるSQLExceptionの
                // 可能性があるので、Schema名を付けたTable名でもう一度実行する。
                try {
                    final String tableNameWithSchema = tableMetaInfo.buildTableNameWithSchema();
                    rs = stmt.executeQuery("SELECT " + primaryKeyColumnName + " FROM " + tableNameWithSchema);
                } catch (SQLException ignored) {
                    // やっぱりだめだった...の場合
                    ignoredMessage = ignored.getMessage();
                    throw e;
                }
            }
            final ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++) {
                final String currentColumnName = md.getColumnName(i);
                if (primaryKeyColumnName.equals(currentColumnName)) {
                    return md.isAutoIncrement(i);
                }
            }
        } catch (SQLException e) {
            String msg = "The handling for AutoIncrement threw the SQLException:";
            msg = msg + " primaryKeyColumnName=" + primaryKeyColumnName + " tableMetaInfo=" + tableMetaInfo;
            msg = msg + " ignoredMessage=" + ignoredMessage;
            throw new IllegalStateException(msg, e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        String msg = "The primaryKeyColumnName is not found in the table: ";
        msg = msg + tableName + " - " + primaryKeyColumnName;
        throw new IllegalStateException(msg);
    }

    /**
     * Has auto-increment at the table?
     * 
     * @param conn Connection.
     * @param tableMetaInfo The meta information of table.
     * @return Determination. (Nullable)
     * @throws SQLException
     */
    public boolean hasAutoIncrement(Connection conn, DfTableMetaInfo tableMetaInfo) throws SQLException {
        final String tableName = tableMetaInfo.getTableName();
        Statement stmt = null;
        ResultSet rs = null;
        String ignoredMessage = null;
        try {
            stmt = conn.createStatement();
            try {
                rs = stmt.executeQuery("SELECT * FROM " + tableName);
            } catch (SQLException e) {
                // ここでSQLExceptionが発生した場合は、Schema名を付けていないことによるSQLExceptionの
                // 可能性があるので、Schema名を付けたTable名でもう一度実行する。
                try {
                    final String tableNameWithSchema = tableMetaInfo.buildTableNameWithSchema();
                    rs = stmt.executeQuery("SELECT * FROM " + tableNameWithSchema);
                } catch (SQLException ignored) {
                    // やっぱりだめだった...の場合
                    ignoredMessage = ignored.getMessage();
                    throw e;
                }
            }
            final ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++) {
                if (md.isAutoIncrement(i)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            String msg = "The handling for AutoIncrement threw the SQLException:";
            msg = msg + " tableMetaInfo=" + tableMetaInfo;
            msg = msg + " ignoredMessage=" + ignoredMessage;
            throw new IllegalStateException(msg, e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return false;
    }
}