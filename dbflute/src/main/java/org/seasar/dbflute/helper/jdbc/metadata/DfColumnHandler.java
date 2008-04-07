/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableHandler.DfTableMetaInfo;

/**
 * @author jflute
 */
public class DfColumnHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfColumnHandler.class);

    // ===================================================================================
    //                                                                        Meta Getting
    //                                                                        ============
    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, String schemaName, DfTableMetaInfo tableMetaInfo) {
        final String tableName = tableMetaInfo.getTableName();
        return getColumns(dbMeta, tableMetaInfo.selectRealSchemaName(schemaName), tableName);
    }

    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, String schemaName, String tableName) {
        return getColumns(dbMeta, schemaName, tableName, false);
    }

    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, String schemaName, String tableName,
            boolean caseInsensitive) {
        final List<DfColumnMetaInfo> columns = new ArrayList<DfColumnMetaInfo>();
        ResultSet columnResultSet = null;
        ResultSet columnResultSetLowerSpare = null;
        ResultSet columnResultSetUpperSpare = null;
        try {
            final String realSchemaName = schemaName;
            columnResultSet = dbMeta.getColumns(null, realSchemaName, tableName, null);
            setupColumnMetaInfo(columns, columnResultSet);
            if (caseInsensitive) {
                if (columns.isEmpty()) {
                    columnResultSetLowerSpare = dbMeta.getColumns(null, realSchemaName, tableName.toLowerCase(), null);
                    setupColumnMetaInfo(columns, columnResultSetLowerSpare);
                }
                if (columns.isEmpty()) {
                    columnResultSetUpperSpare = dbMeta.getColumns(null, realSchemaName, tableName.toUpperCase(), null);
                    setupColumnMetaInfo(columns, columnResultSetUpperSpare);
                }
            }
        } catch (SQLException e) {
            String msg = "SQLException occured: schemaName=" + schemaName + " tableName=" + tableName;
            throw new IllegalStateException(msg);
        } finally {
            if (columnResultSet != null) {
                try {
                    columnResultSet.close();
                } catch (SQLException ignored) {
                }
            }
            if (columnResultSetLowerSpare != null) {
                try {
                    columnResultSetLowerSpare.close();
                } catch (SQLException ignored) {
                }
            }
            if (columnResultSetUpperSpare != null) {
                try {
                    columnResultSetUpperSpare.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return columns;
    }

    protected void setupColumnMetaInfo(List<DfColumnMetaInfo> columns, ResultSet columnResultSet) throws SQLException {
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
    }

    // ===================================================================================
    //                                                                 Torque Type Getting
    //                                                                 ===================
    public String getColumnTorqueType(final DfColumnMetaInfo columnMetaInfo) {
        final int sqlTypeCode = columnMetaInfo.getJdbcTypeCode();
        if (Types.OTHER != sqlTypeCode) {

            // For compatible to Oracle's JDBC driver.
            if (isOracleCompatibleDate(sqlTypeCode, columnMetaInfo.getDbTypeName())) {
                return getDateTorqueType();
            }

            try {
                return TypeMap.getTorqueType(sqlTypeCode);
            } catch (RuntimeException e) {
                String msg = "Not found the sqlTypeCode in TypeMap: columnMetaInfo=";
                msg = msg + columnMetaInfo + " message=" + e.getMessage();
                _log.warn(msg);
            }
        }

        // If other
        final String dbTypeName = columnMetaInfo.getDbTypeName();
        if (dbTypeName == null) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("varchar")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("char")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.CHAR);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("timestamp")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.TIMESTAMP);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("date")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.DATE);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("clob")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.CLOB);
            return torqueType;
        } else {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        }
    }

    protected boolean isOracleCompatibleDate(final int sqlTypeCode, final String dbTypeName) {
        return isOracle() && java.sql.Types.TIMESTAMP == sqlTypeCode && "date".equalsIgnoreCase(dbTypeName);
    }

    protected String getDateTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.DATE);
    }

    protected boolean isOracle() {
        return DfBuildProperties.getInstance().getBasicProperties().isDatabaseOracle();
    }

    // ===================================================================================
    //                                                                    Column Meta Info
    //                                                                    ================
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

        @Override
        public String toString() {
            return "{" + columnName + ", " + dbTypeName + "(" + columnSize + "," + decimalDigits + "), " + jdbcTypeCode
                    + ", " + required + ", " + defaultValue + "}";
        }
    }

}