/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author mkubo
 * @version $Revision$ $Date$
 */
public class DfAutoIncrementHandler extends DfAbstractMetaDataHandler {

    public static final Log _log = LogFactory.getLog(DfAutoIncrementHandler.class);

    /**
     * Get auto-increment column name.
     * <p>
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve PK information.
     * @param primaryKeyColumnName Primary-key column-name.
     * @param conn Connection.
     * @return Auto-increment column name. (Nullable)
     * @throws SQLException
     */
    public boolean isAutoIncrementColumn(DatabaseMetaData dbMeta, String tableName, String primaryKeyColumnName,
            Connection conn) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT " + primaryKeyColumnName + " FROM " + tableName);
            final ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++) {
                final String currentColumnName = md.getColumnName(i);
                if (primaryKeyColumnName.equals(currentColumnName)) {
                    return md.isAutoIncrement(i);
                }
            }
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
        throw new RuntimeException(msg);
    }
}