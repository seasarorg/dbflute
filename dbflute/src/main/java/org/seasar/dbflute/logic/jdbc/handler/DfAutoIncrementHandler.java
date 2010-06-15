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

import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * The handler of auto increment. 
 * @author jflute
 */
public class DfAutoIncrementHandler extends DfAbstractMetaDataHandler {

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
        final String tableSqlName = tableInfo.buildTableSqlName();
        final String sql = buildMetaDataSql(primaryKeyColumnName, tableSqlName);
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            final ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                final String currentColumnName = md.getColumnName(i);
                if (primaryKeyColumnName.equals(currentColumnName)) {
                    return md.isAutoIncrement(i);
                }
            }
            String msg = "The primaryKeyColumnName is not found in the table: ";
            msg = msg + tableSqlName + "." + primaryKeyColumnName;
            throw new IllegalStateException(msg); // unreachable
        } catch (SQLException e) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "Failed to execute the SQL for getting auto-increment!" + ln();
            msg = msg + ln();
            msg = msg + "[SQL]" + ln() + sql + ln() + e.getMessage() + ln();
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

    protected String buildMetaDataSql(String pkName, String tableName) {
        pkName = getProperties().getLittleAdjustmentProperties().quoteColumnNameIfNeeds(pkName, true);
        return "select " + pkName + " from " + tableName + " where 0 = 1";
    }
}