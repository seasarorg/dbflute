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
package org.seasar.dbflute.logic.jdbc.handler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * The handler of auto increment. 
 * @author jflute
 */
public class DfAutoIncrementHandler extends DfAbstractMetaDataHandler {

    /**
     * Is auto-increment column?
     * @param conn Connection.
     * @param tableMetaInfo The meta information of table from which to retrieve PK information.
     * @param primaryKeyColumnName Primary-key column-name.
     * @return Auto-increment column name. (Nullable)
     */
    public boolean isAutoIncrementColumn(Connection conn, DfTableMetaInfo tableMetaInfo, String primaryKeyColumnName) {
        final String tableName = tableMetaInfo.getTableName();
        final String sql = buildMetaDataSql(primaryKeyColumnName, tableName);
        String recoverySql = null;
        Statement st = null;
        ResultSet rs = null;
        String ignoredMessage = null;
        try {
            st = conn.createStatement();
            try {
                rs = st.executeQuery(sql);
            } catch (SQLException e) {
                // Basically it does not come here.
                // But if it's schema requirement or reservation word, it comes here. 
                try {
                    final String schemaPrefix = extractPureSchemaName(tableMetaInfo.getCatalogSchema());
                    recoverySql = buildMetaDataSql(primaryKeyColumnName, schemaPrefix + "." + tableName);
                    rs = st.executeQuery(recoverySql);
                } catch (SQLException ignored) {
                    try {
                        final String schemaPrefix = filterNoNameSchema(tableMetaInfo.getCatalogSchema());
                        recoverySql = buildMetaDataSql(primaryKeyColumnName, schemaPrefix + "." + tableName);
                        rs = st.executeQuery(recoverySql);
                    } catch (SQLException completelyIgnored) {
                    }
                    rs = retryForReservationWordTable(st, tableName, primaryKeyColumnName);
                    if (rs == null) {
                        ignoredMessage = ignored.getMessage();
                        throw e;
                    }
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
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "Failed to execute the SQL for getting auto-increment!" + ln();
            msg = msg + ln();
            msg = msg + "[SQL]" + ln() + sql + ln();
            msg = msg + ln();
            msg = msg + "[Message]" + ln() + e.getMessage() + ln();
            msg = msg + ln();
            msg = msg + "[Recovery Sql]" + ln() + recoverySql + ln();
            msg = msg + ln();
            msg = msg + "[Recovery Message]" + ln() + ignoredMessage + ln();
            msg = msg + "* * * * * * * * * */";
            throw new IllegalStateException(msg, e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
        String msg = "The primaryKeyColumnName is not found in the table: ";
        msg = msg + tableName + " - " + primaryKeyColumnName;
        throw new IllegalStateException(msg);
    }

    protected String buildMetaDataSql(String primaryKeyColumnName, String tableName) {
        return "select " + primaryKeyColumnName + " from " + tableName + " where 0 = 1";
    }

    protected ResultSet retryForReservationWordTable(Statement stmt, String tableName, String primaryKeyColumnName) {
        tableName = "\"" + tableName + "\"";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(buildMetaDataSql(primaryKeyColumnName, tableName));
        } catch (SQLException e) {
            tableName = "[" + tableName + "]";
            try {
                rs = stmt.executeQuery(buildMetaDataSql(primaryKeyColumnName, tableName));
            } catch (SQLException ignored) {
            }
        }
        // 'SchemaName + ReservationWord' is unsupported!
        return rs;
    }
}