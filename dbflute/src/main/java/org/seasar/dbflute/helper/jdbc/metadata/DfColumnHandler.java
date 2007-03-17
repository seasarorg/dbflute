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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DfColumnHandler extends DfAbstractMetaDataHandler {

    private static final Log _log = LogFactory.getLog(DfColumnHandler.class);

    /**
     * Retrieves all the column names and types for a given table from
     * JDBC metadata.  It returns a List of Lists.  Each element
     * of the returned List is a List with:
     *
     * element 0 => a String object for the column name.
     * element 1 => an Integer object for the column type.
     * element 2 => size of the column.
     * element 3 => null type.
     * 
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve column information.
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List getColumns(DatabaseMetaData dbMeta, String schemaName, String tableName) throws SQLException {
        final List<List<Object>> columns = new ArrayList<List<Object>>();
        ResultSet columnResultSet = null;
        try {
            columnResultSet = dbMeta.getColumns(null, schemaName, tableName, null);
            while (columnResultSet.next()) {
                final String name = columnResultSet.getString(4);
                final Integer sqlType = new Integer(columnResultSet.getString(5));
                final Integer size = new Integer(columnResultSet.getInt(7));
                final Integer nullType = new Integer(columnResultSet.getInt(11));
                final String defValue = columnResultSet.getString(13);

                final List<Object> col = new ArrayList<Object>(5);
                col.add(name);
                col.add(sqlType);
                col.add(size);
                col.add(nullType);
                col.add(defValue);
                columns.add(col);
            }
        } catch (SQLException e) {
            _log.warn("SQLException occured: schemaName=" + schemaName + " tableName=" + tableName);
            throw e;
        } finally {
            if (columnResultSet != null) {
                columnResultSet.close();
            }
        }
        return columns;
    }

}