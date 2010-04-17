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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * The handler of auto increment. 
 * @author jflute
 */
public class DfAutoIncrementHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfAutoIncrementHandler.class);

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    /**
     * Is auto-increment column?
     * @param conn Connection.
     * @param tableInfo The meta information of table from which to retrieve PK information.
     * @param primaryKeyColumnName Primary-key column-name.
     * @return Auto-increment column name. (Nullable)
     */
    public boolean isAutoIncrementColumn(Connection conn, DfTableMetaInfo tableInfo, String primaryKeyColumnName)
            throws SQLException {
        final String tableName = tableInfo.getTableName();
        final String sql = buildMetaDataSql(primaryKeyColumnName, tableName);
        Statement st = null;
        ResultSet rs = null;
        String recoverySql1 = null;
        String recoverySql2 = null;
        String recoveryMessage1 = null;
        String recoveryMessage2 = null;
        try {
            st = conn.createStatement();
            try {
                rs = st.executeQuery(sql);
            } catch (SQLException e) {
                // Basically it does not come here.
                // But if it's schema requirement or reservation word, it comes here. 
                try {
                    final String schemaPrefix = tableInfo.buildPureSchemaTable();
                    recoverySql1 = buildMetaDataSql(primaryKeyColumnName, schemaPrefix + "." + tableName);
                    rs = st.executeQuery(recoverySql1);
                } catch (SQLException recovery1ex) {
                    try {
                        final String schemaPrefix = tableInfo.buildCatalogSchemaTable();
                        recoverySql2 = buildMetaDataSql(primaryKeyColumnName, schemaPrefix + "." + tableName);
                        rs = st.executeQuery(recoverySql2);
                    } catch (SQLException recovery2ex) {
                        try {
                            rs = retryForReservationWordTable(st, tableName, primaryKeyColumnName);
                        } catch (SQLException reservationEx) {
                            _log.info("Failed to recover by quotetation: " + reservationEx.getMessage());
                        }
                        if (rs == null) {
                            recoveryMessage1 = recovery1ex.getMessage();
                            recoveryMessage2 = recovery2ex.getMessage();
                            throw e;
                        }
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
            String msg = "The primaryKeyColumnName is not found in the table: ";
            msg = msg + tableName + " - " + primaryKeyColumnName;
            throw new IllegalStateException(msg); // unreachable
        } catch (SQLException e) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "Failed to execute the SQL for getting auto-increment!" + ln();
            msg = msg + ln();
            msg = msg + "[SQL]" + ln() + sql + ln() + e.getMessage() + ln();
            msg = msg + ln();
            msg = msg + "[Recovery1]" + ln() + recoverySql1 + ln() + recoveryMessage1 + ln();
            msg = msg + ln();
            msg = msg + "[Recovery2]" + ln() + recoverySql2 + ln() + recoveryMessage2 + ln();
            msg = msg + "* * * * * * * * * */";
            throw new DfJDBCException(msg, e);
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
    }

    protected String buildMetaDataSql(String primaryKeyColumnName, String tableName) {
        return "select " + primaryKeyColumnName + " from " + tableName + " where 0 = 1";
    }

    protected ResultSet retryForReservationWordTable(Statement stmt, String tableName, String primaryKeyColumnName)
            throws SQLException {
        if (isSQLServer()) {
            tableName = "[" + tableName + "]";
        } else {
            tableName = "\"" + tableName + "\"";
        }
        // 'SchemaName + ReservationWord' is unsupported!
        return stmt.executeQuery(buildMetaDataSql(primaryKeyColumnName, tableName));
    }
}