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
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve column information.
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, String schemaName, String tableName)
            throws SQLException {
        final List<DfColumnMetaInfo> columns = new ArrayList<DfColumnMetaInfo>();
        ResultSet columnResultSet = null;
        try {
            columnResultSet = dbMeta.getColumns(null, schemaName, tableName, null);
            while (columnResultSet.next()) {
                final String columnName = columnResultSet.getString(4);
                final Integer jdbcTypeCode = new Integer(columnResultSet.getString(5));
                final String dbTypeName = columnResultSet.getString(6);
                final Integer columnSize = new Integer(columnResultSet.getInt(7));
                final Integer nullType = new Integer(columnResultSet.getInt(11));
                final String defaultValue = columnResultSet.getString(13);

                final DfColumnMetaInfo columnMetaInfo = new DfColumnMetaInfo();
                columnMetaInfo.setColumnName(columnName);
                columnMetaInfo.setJdbcTypeCode(jdbcTypeCode);
                columnMetaInfo.setDbTypeName(dbTypeName);
                columnMetaInfo.setColumnSize(columnSize);
                columnMetaInfo.setRequired(nullType == 0);
                columnMetaInfo.setDefaultValue(defaultValue);
                columns.add(columnMetaInfo);
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

    public static class DfColumnMetaInfo {
        protected String columnName;
        protected int jdbcTypeCode;
        protected String dbTypeName;
        protected int columnSize;
        protected boolean required;
        protected String defaultValue;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public int getColumnSize() {
            return columnSize;
        }

        public void setColumnSize(int columnSize) {
            this.columnSize = columnSize;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public int getJdbcTypeCode() {
            return jdbcTypeCode;
        }

        public void setJdbcTypeCode(int sqlTypeCode) {
            this.jdbcTypeCode = sqlTypeCode;
        }

        public String getDbTypeName() {
            return dbTypeName;
        }

        public void setDbTypeName(String sqlTypeName) {
            this.dbTypeName = sqlTypeName;
        }
    }

}