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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableNameHandler.DfTableMetaInfo;

/**
 * @author jflute
 */
public class DfColumnHandler extends DfAbstractMetaDataHandler {

    private static final Log _log = LogFactory.getLog(DfColumnHandler.class);

    /**
     * Retrieves all the column names and types for a given table from
     * JDBC metadata.  It returns a List of Lists.  Each element
     * of the returned List is a List with:
     *
     * @param dbMeta JDBC metadata.
     * @param schemaName Schema name. (NotNull & AllowedEmpty)
     * @param tableMetaInfo The meta information of table. (NotNull)
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, String schemaName, DfTableMetaInfo tableMetaInfo)
            throws SQLException {
        final String tableName = tableMetaInfo.getTableName();
        final List<DfColumnMetaInfo> columns = new ArrayList<DfColumnMetaInfo>();
        ResultSet columnResultSet = null;
        try {
            final String realSchemaName = tableMetaInfo.selectRealSchemaName(schemaName);
            columnResultSet = dbMeta.getColumns(null, realSchemaName, tableName, null);
            while (columnResultSet.next()) {
                final String columnName = columnResultSet.getString(4);
                if (isColumnExcept(columnName)) {
                    continue;
                }
                final Integer jdbcTypeCode = new Integer(columnResultSet.getString(5));
                final String dbTypeName = columnResultSet.getString(6);
                final Integer columnSize = new Integer(columnResultSet.getInt(7));
                final Integer decimalDigits = columnResultSet.getInt(9);
                final Integer nullType = new Integer(columnResultSet.getInt(11));
                final String defaultValue = columnResultSet.getString(13);

                final DfColumnMetaInfo columnMetaInfo = new DfColumnMetaInfo();
                columnMetaInfo.setColumnName(columnName);
                columnMetaInfo.setJdbcTypeCode(jdbcTypeCode);
                columnMetaInfo.setDbTypeName(dbTypeName);
                columnMetaInfo.setColumnSize(columnSize);
                columnMetaInfo.setDecimalDigits(decimalDigits);
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

    public String getColumnTorqueType(final DfColumnMetaInfo columnMetaInfo) {
        final int sqlTypeCode = columnMetaInfo.getJdbcTypeCode();
        if (Types.OTHER != sqlTypeCode) {
            try {
                return TypeMap.getTorqueType(sqlTypeCode);
            } catch (RuntimeException e) {
                String msg = "Not found the sqlTypeCode in TypeMap: sqlTypeCode=";
                msg = msg + sqlTypeCode + " message=" + e.getMessage();
                _log.warn(msg);
            }
        }

        // If other
        final String dbTypeName = columnMetaInfo.getDbTypeName();
        if (dbTypeName == null) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("char")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("date")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.DATE);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("timestamp")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.TIMESTAMP);
            return torqueType;
        } else {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        }
    }
    
    public static class DfColumnMetaInfo {
        protected String columnName;
        protected int jdbcTypeCode;
        protected String dbTypeName;
        protected int columnSize;
        protected int decimalDigits;
        protected boolean required;
        protected String defaultValue;
        protected String sql2entityTableName;

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
        
        public int getDecimalDigits() {
            return decimalDigits;
        }

        public void setDecimalDigits(int decimalDigits) {
            this.decimalDigits = decimalDigits;
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
        
        public String getSql2EntityableName() {
            return sql2entityTableName;
        }
        
        public void setSql2EntityTableName(String sql2entityTableName) {
            this.sql2entityTableName = sql2entityTableName;
        }
    }

}