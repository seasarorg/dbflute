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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.mapping.DfJdbcTypeMapper;
import org.seasar.dbflute.logic.jdbc.mapping.DfJdbcTypeMapper.Resource;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.properties.DfTypeMappingProperties;
import org.seasar.dbflute.util.DfCollectionUtil;

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
     * Get the list of column meta information.
     * @param metaData The meta data of database. (NotNull)
     * @param tableInfo The meta information of table. (NotNull, CaseInsensitive)
     * @return The list of column meta information. (NotNull)
     */
    public List<DfColumnMetaInfo> getColumnList(DatabaseMetaData metaData, DfTableMetaInfo tableInfo)
            throws SQLException {
        final UnifiedSchema unifiedSchema = tableInfo.getUnifiedSchema();
        final String tableName = tableInfo.getTableName();
        return getColumnList(metaData, unifiedSchema, tableName);
    }

    /**
     * Get the list of column meta information.
     * @param metaData The meta data of database. (NotNull)
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (Nullable)
     * @param tableName The name of table. (NotNull, CaseInsensitive)
     * @return The list of column meta information. (NotNull)
     */
    public List<DfColumnMetaInfo> getColumnList(DatabaseMetaData metaData, UnifiedSchema unifiedSchema, String tableName)
            throws SQLException {
        List<DfColumnMetaInfo> ls = doGetColumnList(metaData, unifiedSchema, tableName, false);
        if (canRetryCaseInsensitive()) {
            if (ls.isEmpty()) { // retry by lower case
                ls = doGetColumnList(metaData, unifiedSchema, tableName.toLowerCase(), true);
            }
            if (ls.isEmpty()) { // retry by upper case
                ls = doGetColumnList(metaData, unifiedSchema, tableName.toUpperCase(), true);
            }
        }
        return ls;
    }

    protected List<DfColumnMetaInfo> doGetColumnList(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, boolean retry) throws SQLException {
        final List<DfColumnMetaInfo> columnList = DfCollectionUtil.newArrayList();

        // Column names for duplicate check
        final StringSet columnNameSet = StringSet.createAsFlexible();

        // Duplicate objects for warning log
        final StringSet duplicateTableNameSet = StringSet.createAsFlexible();
        final StringSet duplicateColumnNameSet = StringSet.createAsFlexible();

        ResultSet rs = null;
        try {
            rs = extractColumnMetaData(metaData, unifiedSchema, tableName, retry);
            if (rs == null) {
                return DfCollectionUtil.newArrayList();
            }
            while (rs.next()) {
                // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
                // same policy of table process (see DfTableHandler.java)
                // - - - - - - - - - -/

                final String columnName = rs.getString(4);
                if (isColumnExcept(unifiedSchema, tableName, columnName)) {
                    continue;
                }

                final String metaTableName = rs.getString(3);
                if (checkMetaTableDiffIfNeeds(tableName, metaTableName)) {
                    continue;
                }

                // Filter duplicate objects
                if (columnNameSet.contains(columnName)) {
                    duplicateTableNameSet.add(metaTableName);
                    duplicateColumnNameSet.add(columnName);
                    continue; // ignored with warning
                }
                columnNameSet.add(columnName);

                final Integer jdbcTypeCode = Integer.valueOf(rs.getString(5));
                final String dbTypeName = rs.getString(6);
                final Integer columnSize = Integer.valueOf(rs.getInt(7));
                final Integer decimalDigits = rs.getInt(9);
                final Integer nullType = Integer.valueOf(rs.getInt(11));
                final String columnComment = rs.getString(12);
                final String defaultValue = rs.getString(13);

                final DfColumnMetaInfo columnMetaInfo = new DfColumnMetaInfo();
                columnMetaInfo.setColumnName(columnName);
                columnMetaInfo.setJdbcDefValue(jdbcTypeCode);
                columnMetaInfo.setDbTypeName(dbTypeName);
                columnMetaInfo.setColumnSize(columnSize);
                columnMetaInfo.setDecimalDigits(decimalDigits);
                columnMetaInfo.setRequired(nullType == 0);
                columnMetaInfo.setColumnComment(columnComment);
                columnMetaInfo.setDefaultValue(defaultValue);
                columnList.add(columnMetaInfo);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
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

        return columnList;
    }

    protected ResultSet extractColumnMetaData(DatabaseMetaData metaData, UnifiedSchema unifiedSchema, String tableName,
            boolean retry) throws SQLException {
        final String catalogName = unifiedSchema.getPureCatalog();
        final String schemaName = unifiedSchema.getPureSchema();
        try {
            return metaData.getColumns(catalogName, schemaName, tableName, null);
        } catch (SQLException e) {
            if (retry) {
                // because the exception may be thrown when the table is not found
                return null;
            } else {
                throw e;
            }
        }
    }

    public Map<String, DfColumnMetaInfo> getColumnMap(DatabaseMetaData metaData, DfTableMetaInfo tableInfo)
            throws SQLException {
        final List<DfColumnMetaInfo> columnList = getColumnList(metaData, tableInfo);
        final Map<String, DfColumnMetaInfo> map = new LinkedHashMap<String, DfColumnMetaInfo>();
        for (DfColumnMetaInfo columnInfo : columnList) {
            map.put(columnInfo.getColumnName(), columnInfo);
        }
        return map;
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
    public String getColumnJdbcType(DfColumnMetaInfo columnMetaInfo) {
        return getColumnJdbcType(columnMetaInfo.getJdbcDefValue(), columnMetaInfo.getDbTypeName());
    }

    /**
     * Get the JDBC type of the column. 
     * @param jdbcDefType The JDBC definition value.
     * @param dbTypeName The name of DB data type. (Nullable: If null, the mapping using this is invalid)
     * @return The JDBC type of the column. (NotNull)
     */
    public String getColumnJdbcType(int jdbcDefType, String dbTypeName) {
        return getJdbcTypeMapper().getColumnJdbcType(jdbcDefType, dbTypeName);
    }

    /**
     * Does it have a mapping about the type?
     * @param columnMetaInfo The meta information of column. (NotNull)
     * @return The JDBC type of the column. (NotNull)
     */
    public boolean hasMappingJdbcType(DfColumnMetaInfo columnMetaInfo) {
        return hasMappingJdbcType(columnMetaInfo.getJdbcDefValue(), columnMetaInfo.getDbTypeName());
    }

    /**
     * Does it have a mapping about the type?
     * @param jdbcDefType The definition type of JDBC.
     * @param dbTypeName The name of DB data type. (Nullable: If null, the mapping using this is invalid)
     * @return The JDBC type of the column. (NotNull)
     */
    public boolean hasMappingJdbcType(int jdbcDefType, String dbTypeName) {
        return getJdbcTypeMapper().hasMappingJdbcType(jdbcDefType, dbTypeName);
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
            public boolean isLangJava() {
                return getBasicProperties().isTargetLanguageJava();
            }

            public boolean isDbmsPostgreSQL() {
                return isDatabasePostgreSQL();
            }

            public boolean isDbmsOracle() {
                return isDatabaseOracle();
            }

            public boolean isDbmsSQLServer() {
                return isDatabaseSQLServer();
            }

            @Override
            public String toString() {
                return "{" + isLangJava() + ", " + isDbmsOracle() + ", " + isDbmsPostgreSQL() + "}";
            }

        });
        return mapper;
    }

    // -----------------------------------------------------
    //                                          Concept Type
    //                                          ------------
    public boolean isConceptTypeUUID(final String dbTypeName) {
        return getJdbcTypeMapper().isConceptTypeUUID(dbTypeName);
    }

    public boolean isConceptTypeStringClob(final String dbTypeName) {
        return getJdbcTypeMapper().isConceptTypeStringClob(dbTypeName);
    }

    public boolean isConceptTypeBytesOid(final String dbTypeName) {
        return getJdbcTypeMapper().isConceptTypeBytesOid(dbTypeName);
    }

    public boolean isConceptTypeFixedLengthString(final String dbTypeName) {
        return getJdbcTypeMapper().isConceptTypeFixedLengthString(dbTypeName);
    }

    public boolean isConceptTypeObjectBindingBigDecimal(final String dbTypeName) {
        return getJdbcTypeMapper().isConceptTypeObjectBindingBigDecimal(dbTypeName);
    }

    // -----------------------------------------------------
    //                                         Pinpoint Type
    //                                         -------------
    public boolean isPostgreSQLBpChar(final String dbTypeName) {
        return getJdbcTypeMapper().isPostgreSQLBpChar(dbTypeName);
    }

    public boolean isPostgreSQLNumeric(final String dbTypeName) {
        return getJdbcTypeMapper().isPostgreSQLNumeric(dbTypeName);
    }

    public boolean isPostgreSQLUuid(final String dbTypeName) {
        return getJdbcTypeMapper().isPostgreSQLUuid(dbTypeName);
    }

    public boolean isPostgreSQLOid(final String dbTypeName) {
        return getJdbcTypeMapper().isPostgreSQLOid(dbTypeName);
    }

    public boolean isPostgreSQLCursor(final String dbTypeName) {
        return getJdbcTypeMapper().isPostgreSQLCursor(dbTypeName);
    }

    public boolean isOracleNCharOrNVarchar(final String dbTypeName) {
        return getJdbcTypeMapper().isOracleNCharOrNVarchar(dbTypeName);
    }

    public boolean isOracleNumber(final String dbTypeName) {
        return getJdbcTypeMapper().isOracleNumber(dbTypeName);
    }

    public boolean isOracleDate(final String dbTypeName) {
        return getJdbcTypeMapper().isOracleDate(dbTypeName);
    }

    public boolean isOracleCursor(final String dbTypeName) {
        return getJdbcTypeMapper().isOracleCursor(dbTypeName);
    }

    public boolean isOracleTreatedAsArray(final String dbTypeName) {
        return isOracleTable(dbTypeName) || isOracleVArray(dbTypeName);
    }

    public boolean isOracleTable(final String dbTypeName) {
        return getJdbcTypeMapper().isOracleTable(dbTypeName);
    }

    public boolean isOracleVArray(final String dbTypeName) {
        return getJdbcTypeMapper().isOracleVArray(dbTypeName);
    }

    public boolean isSQLServerUniqueIdentifier(final String dbTypeName) {
        return getJdbcTypeMapper().isSQLServerUniqueIdentifier(dbTypeName);
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