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
import org.seasar.dbflute.helper.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;

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
    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData metaData, String schemaName, DfTableMetaInfo tableMetaInfo) {
        final String tableName = tableMetaInfo.getTableName();
        return getColumns(metaData, tableMetaInfo.selectRealSchemaName(schemaName), tableName);
    }

    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData metaData, String schemaName, String tableName) {
        return getColumns(metaData, schemaName, tableName, false);
    }

    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData metaData, String schemaName, String tableName,
            boolean caseInsensitive) {
        schemaName = filterSchemaName(schemaName);
        final List<DfColumnMetaInfo> columns = new ArrayList<DfColumnMetaInfo>();
        ResultSet columnResultSet = null;
        ResultSet columnResultSetLowerSpare = null;
        ResultSet columnResultSetUpperSpare = null;
        try {
            final String realSchemaName = schemaName;
            columnResultSet = metaData.getColumns(null, realSchemaName, tableName, null);
            setupColumnMetaInfo(columns, columnResultSet);
            if (caseInsensitive) {
                if (columns.isEmpty()) {
                    columnResultSetLowerSpare = metaData
                            .getColumns(null, realSchemaName, tableName.toLowerCase(), null);
                    setupColumnMetaInfo(columns, columnResultSetLowerSpare);
                }
                if (columns.isEmpty()) {
                    columnResultSetUpperSpare = metaData
                            .getColumns(null, realSchemaName, tableName.toUpperCase(), null);
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
            final String columnComment = columnResultSet.getString(12);
            final String defaultValue = columnResultSet.getString(13);

            final DfColumnMetaInfo columnMetaInfo = new DfColumnMetaInfo();
            columnMetaInfo.setColumnName(columnName);
            columnMetaInfo.setJdbcType(jdbcTypeCode);
            columnMetaInfo.setDbTypeName(dbTypeName);
            columnMetaInfo.setColumnSize(columnSize);
            columnMetaInfo.setDecimalDigits(decimalDigits);
            columnMetaInfo.setRequired(nullType == 0);
            columnMetaInfo.setColumnComment(columnComment);
            columnMetaInfo.setDefaultValue(defaultValue);
            columns.add(columnMetaInfo);
        }
    }

    // ===================================================================================
    //                                                                 Torque Type Getting
    //                                                                 ===================
    public String getColumnTorqueType(final DfColumnMetaInfo columnMetaInfo) {
        return getColumnTorqueType(columnMetaInfo.getJdbcType(), columnMetaInfo.getDbTypeName());
    }

    public String getColumnTorqueType(int jdbcType, String dbTypeName) {
        if (isPostgreSQLBytesOid(dbTypeName)) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.BLOB);
            return torqueType;
        }

        if (Types.OTHER != jdbcType) {

            // For compatible to Oracle's JDBC driver.
            if (isOracleCompatibleDate(jdbcType, dbTypeName)) {
                return getDateTorqueType();
            }

            try {
                return TypeMap.getTorqueType(jdbcType);
            } catch (RuntimeException e) {
                String msg = "Not found the sqlTypeCode in TypeMap: jdbcType=";
                msg = msg + jdbcType + " message=" + e.getMessage();
                _log.warn(msg);
            }
        }

        // If other
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

    public boolean isOracleCompatibleDate(final int jdbcType, final String dbTypeName) {
        return isOracle() && java.sql.Types.TIMESTAMP == jdbcType && "date".equalsIgnoreCase(dbTypeName);
    }
    
    public boolean isOracleStringClob(final String dbTypeName) {
        return isOracle() && "clob".equalsIgnoreCase(dbTypeName);
    }

    public boolean isPostgreSQLBytesOid(final String dbTypeName) {
        return isPostgreSQL() && "oid".equalsIgnoreCase(dbTypeName);
    }

    protected String getDateTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.DATE);
    }
}