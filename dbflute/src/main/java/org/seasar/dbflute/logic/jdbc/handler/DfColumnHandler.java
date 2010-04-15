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
package org.seasar.dbflute.logic.jdbc.handler;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.logic.mapping.DfJdbcTypeMapper;
import org.seasar.dbflute.logic.mapping.DfJdbcTypeMapper.Resource;
import org.seasar.dbflute.properties.DfTypeMappingProperties;

/**
 * @author jflute
 */
public class DfColumnHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfColumnHandler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfJdbcTypeMapper _jdbcTypeMapper;

    // ===================================================================================
    //                                                                        Meta Getting
    //                                                                        ============
    /**
     * Get the map of column meta information.
     * @param metaData The meta data of database. (NotNull)
     * @param schemaName The name of schema. (Nullable)
     * @param tableName The name of table. (NotNull, CaseInsensitive)
     * @return The map of column meta information. The key is column name. (NotNull)
     */
    public Map<String, DfColumnMetaInfo> getColumnMetaInfo(DatabaseMetaData metaData, String schemaName,
            String tableName) {
        final List<DfColumnMetaInfo> columns = getColumnList(metaData, schemaName, tableName);
        final Map<String, DfColumnMetaInfo> map = new LinkedHashMap<String, DfColumnMetaInfo>();
        for (DfColumnMetaInfo metaInfo : columns) {
            map.put(metaInfo.getColumnName(), metaInfo);
        }
        return map;
    }

    /**
     * Get the list of column meta information.
     * @param metaData The meta data of database. (NotNull)
     * @param schemaName The name of schema. (Nullable)
     * @param tableMetaInfo The meta information of table. (NotNull, CaseInsensitive)
     * @return The list of column meta information. (NotNull)
     */
    public List<DfColumnMetaInfo> getColumnList(DatabaseMetaData metaData, String schemaName,
            DfTableMetaInfo tableMetaInfo) {
        schemaName = filterSchemaName(schemaName);
        schemaName = tableMetaInfo.selectMetaExtractingSchemaName(schemaName);
        final String tableName = tableMetaInfo.getTableName();
        return getColumnList(metaData, schemaName, tableName);
    }

    /**
     * Get the list of column meta information.
     * @param metaData The meta data of database. (NotNull)
     * @param schemaName The name of schema. (Nullable)
     * @param tableName The name of table. (NotNull, CaseInsensitive)
     * @return The list of column meta information. (NotNull)
     */
    public List<DfColumnMetaInfo> getColumnList(DatabaseMetaData metaData, String schemaName, String tableName) {
        schemaName = filterSchemaName(schemaName);
        final List<DfColumnMetaInfo> columns = new ArrayList<DfColumnMetaInfo>();
        ResultSet columnResultSet = null;
        ResultSet lowerSpare = null;
        ResultSet upperSpare = null;
        try {
            final String catalogName = extractCatalogName(schemaName);
            final String realSchemaName = extractRealSchemaName(schemaName);
            columnResultSet = metaData.getColumns(catalogName, realSchemaName, tableName, null);
            setupColumnMetaInfo(columns, columnResultSet, schemaName, tableName);
            if (columns.isEmpty()) { // for lower case
                lowerSpare = metaData.getColumns(catalogName, realSchemaName, tableName.toLowerCase(), null);
                setupColumnMetaInfo(columns, lowerSpare, schemaName, tableName);
            }
            if (columns.isEmpty()) { // for upper case
                upperSpare = metaData.getColumns(catalogName, realSchemaName, tableName.toUpperCase(), null);
                setupColumnMetaInfo(columns, upperSpare, schemaName, tableName);
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
            if (lowerSpare != null) {
                try {
                    lowerSpare.close();
                } catch (SQLException ignored) {
                }
            }
            if (upperSpare != null) {
                try {
                    upperSpare.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return columns;
    }

    protected void setupColumnMetaInfo(List<DfColumnMetaInfo> columns, ResultSet columnResultSet, String schemaName,
            String tableName) throws SQLException {
        // Column names for duplicate check
        final StringSet columnNameSet = StringSet.createAsFlexible();

        // Duplicate objects for warning log
        final StringSet duplicateTableNameSet = StringSet.createAsFlexible();
        final StringSet duplicateColumnNameSet = StringSet.createAsFlexible();

        while (columnResultSet.next()) {
            final String columnName = columnResultSet.getString(4);
            if (isColumnExcept(schemaName, tableName, columnName)) {
                continue;
            }

            // Filter duplicate objects
            if (columnNameSet.contains(columnName)) {
                duplicateTableNameSet.add(columnResultSet.getString(3));
                duplicateColumnNameSet.add(columnName);
                continue;
            }
            columnNameSet.add(columnName);

            final Integer jdbcTypeCode = Integer.valueOf(columnResultSet.getString(5));
            final String dbTypeName = columnResultSet.getString(6);
            final Integer columnSize = Integer.valueOf(columnResultSet.getInt(7));
            final Integer decimalDigits = columnResultSet.getInt(9);
            final Integer nullType = Integer.valueOf(columnResultSet.getInt(11));
            final String columnComment = columnResultSet.getString(12);
            final String defaultValue = columnResultSet.getString(13);

            final DfColumnMetaInfo columnMetaInfo = new DfColumnMetaInfo();
            columnMetaInfo.setColumnName(columnName);
            columnMetaInfo.setJdbcDefValue(jdbcTypeCode);
            columnMetaInfo.setDbTypeName(dbTypeName);
            columnMetaInfo.setColumnSize(columnSize);
            columnMetaInfo.setDecimalDigits(decimalDigits);
            columnMetaInfo.setRequired(nullType == 0);
            columnMetaInfo.setColumnComment(columnComment);
            columnMetaInfo.setDefaultValue(defaultValue);
            columns.add(columnMetaInfo);
        }

        // Show duplicate objects if exists
        if (!duplicateColumnNameSet.isEmpty()) {
            String msg = "*Duplicate meta data was found:";
            msg = msg + "\n[" + tableName + "]";
            msg = msg + "\n  duplicate tables = " + duplicateTableNameSet;
            msg = msg + "\n  duplicate columns = " + duplicateColumnNameSet;
            _log.info(msg);
        }

        // /= = = = = = = = = = = = = = = = = = = = = = = = = = = 
        // The duplication handling is mainly for Oracle Synonym.
        // = = = = = = = = = =/
    }

    // ===================================================================================
    //                                                                 Torque Type Getting
    //                                                                 ===================
    /**
     * Get the JDBC type of the column. <br /> 
     * Look at the java-doc of overload method if you want to know the priority of mapping.
     * @param columnMetaInfo The meta information of column. (NotNull)
     * @return The JDBC type of the column. (NotNull)
     */
    public String getColumnJdbcType(final DfColumnMetaInfo columnMetaInfo) {
        return getColumnJdbcType(columnMetaInfo.getJdbcDefValue(), columnMetaInfo.getDbTypeName());
    }

    /**
     * Get the JDBC type of the column. <br /> 
     * @param jdbcDefValue The JDBC definition value.
     * @param dbTypeName The name of DB data type. (Nullable: If null, the mapping using this is invalid)
     * @return The JDBC type of the column. (NotNull)
     */
    public String getColumnJdbcType(int jdbcDefValue, String dbTypeName) {
        return getJdbcTypeMapper().getColumnJdbcType(jdbcDefValue, dbTypeName);
    }

    protected DfJdbcTypeMapper getJdbcTypeMapper() {
        if (_jdbcTypeMapper == null) {
            _jdbcTypeMapper = newJdbcTypeMapper();
        }
        return _jdbcTypeMapper;
    }

    protected DfJdbcTypeMapper newJdbcTypeMapper() { // only once
        final DfTypeMappingProperties typeMappingProperties = getProperties().getTypeMappingProperties();
        final Map<String, String> nameToJdbcTypeMap = typeMappingProperties.getNameToJdbcTypeMap();
        final DfJdbcTypeMapper mapper = new DfJdbcTypeMapper(nameToJdbcTypeMap, new Resource() {
            public boolean isTargetLanguageJava() {
                return getBasicProperties().isTargetLanguageJava();
            }

            public boolean isDatabaseOracle() {
                return isOracle();
            }

            public boolean isDatabasePostgreSQL() {
                return isPostgreSQL();
            }

            @Override
            public String toString() {
                return "{" + isTargetLanguageJava() + ", " + isDatabaseOracle() + ", " + isDatabasePostgreSQL() + "}";
            }
        });
        return mapper;
    }

    // -----------------------------------------------------
    //                                    Type Determination
    //                                    ------------------
    public boolean isOracleStringClob(final String dbTypeName) {
        return getJdbcTypeMapper().isOracle_Clob(dbTypeName);
    }

    public boolean isPostgreSQLBytesOid(final String dbTypeName) {
        return getJdbcTypeMapper().isPostgreSQL_Oid(dbTypeName);
    }

    public boolean isUUID(final String dbTypeName) {
        return getJdbcTypeMapper().isUUID(dbTypeName);
    }

    // ===================================================================================
    //                                                                Column Size Handling
    //                                                                ====================
    public static boolean isColumnSizeValid(Integer columnSize) {
        return columnSize != null && columnSize > 0;
    }

    public static boolean isDecimalDigitsValid(Integer decimalDigits) {
        return decimalDigits != null && decimalDigits > 0;
    }
}