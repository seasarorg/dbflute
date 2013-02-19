/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.basic;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMeta;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class DfUniqueKeyExtractor extends DfAbstractMetaDataBasicExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfUniqueKeyExtractor.class);

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * Retrieves an info of the columns composing the primary key for a given table.
     * @param metaData JDBC meta data. (NotNull)
     * @param tableInfo The meta information of table. (NotNull)
     * @return The meta information of primary keys. (NotNull)
     * @throws SQLException
     */
    public DfPrimaryKeyMeta getPrimaryKey(DatabaseMetaData metaData, DfTableMeta tableInfo) throws SQLException {
        final UnifiedSchema unifiedSchema = tableInfo.getUnifiedSchema();
        final String tableName = tableInfo.getTableName();
        return getPrimaryKey(metaData, unifiedSchema, tableName);
    }

    /**
     * Retrieves an info of the columns composing the primary key for a given table.
     * @param metaData JDBC meta data. (NotNull)
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (NullAllowed)
     * @param tableName The name of table. (NotNull)
     * @return The meta information of primary keys. (NotNull)
     * @throws SQLException
     */
    public DfPrimaryKeyMeta getPrimaryKey(DatabaseMetaData metaData, UnifiedSchema unifiedSchema, String tableName)
            throws SQLException {
        DfPrimaryKeyMeta info = doGetPrimaryKey(metaData, unifiedSchema, tableName, false);
        if (canRetryCaseInsensitive()) {
            if (!info.hasPrimaryKey()) { // retry by lower case
                info = doGetPrimaryKey(metaData, unifiedSchema, tableName.toLowerCase(), true);
            }
            if (!info.hasPrimaryKey()) { // retry by upper case
                info = doGetPrimaryKey(metaData, unifiedSchema, tableName.toUpperCase(), true);
            }
        }
        return info;
    }

    protected DfPrimaryKeyMeta doGetPrimaryKey(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, boolean retry) throws SQLException {
        final DfPrimaryKeyMeta info = new DfPrimaryKeyMeta();
        if (isPrimaryKeyExtractingUnsupported()) {
            if (isDatabaseMsAccess()) {
                return processMSAccess(metaData, unifiedSchema, tableName, info);
            }
            return info;
        }
        ResultSet rs = null;
        try {
            rs = extractPrimaryKeyMetaData(metaData, unifiedSchema, tableName, retry);
            if (rs == null) {
                return info;
            }
            while (rs.next()) {
                final String metaTableName = rs.getString(3);
                if (checkMetaTableDiffIfNeeds(tableName, metaTableName)) {
                    continue;
                }
                final String columnName = rs.getString(4);
                final String pkName = rs.getString(6);
                info.addPrimaryKey(columnName, pkName);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return info;
    }

    protected ResultSet extractPrimaryKeyMetaData(DatabaseMetaData dbMeta, UnifiedSchema unifiedSchema,
            String tableName, boolean retry) throws SQLException {
        try {
            final String catalogName = unifiedSchema.getPureCatalog();
            final String schemaName = unifiedSchema.getPureSchema();
            return dbMeta.getPrimaryKeys(catalogName, schemaName, tableName);
        } catch (SQLException e) {
            if (retry) {
                // because the exception may be thrown when the table is not found
                // (for example, Sybase)
                return null;
            } else {
                throw e;
            }
        }
    }

    protected void assertPrimaryKeyNotExcepted(DfPrimaryKeyMeta info, UnifiedSchema unifiedSchema, String tableName) {
        final List<String> primaryKeyList = info.getPrimaryKeyList();
        for (String primaryKey : primaryKeyList) {
            if (isColumnExcept(unifiedSchema, tableName, primaryKey)) {
                String msg = "PK columns are unsupported on 'columnExcept' property:";
                msg = msg + " unifiedSchema=" + unifiedSchema + " tableName=" + tableName;
                msg = msg + " primaryKey=" + primaryKey;
                throw new DfIllegalPropertySettingException(msg);
            }
        }
    }

    /**
     * @param metaData JDBC meta data. (NotNull)
     * @param unifiedSchema The unified schema. (NotNull)
     * @param tableName The name of table. (NotNull)
     * @param info The empty meta information of primary key. (NotNull)
     * @return The meta information of primary key. (NotNull)
     * @throws SQLException
     */
    protected DfPrimaryKeyMeta processMSAccess(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, DfPrimaryKeyMeta info) throws SQLException {
        // it can get from unique key from JDBC of MS Access
        final List<String> emptyList = DfCollectionUtil.emptyList();
        final Map<String, Map<Integer, String>> uqMap = getUniqueKeyMap(metaData, unifiedSchema, tableName, emptyList);
        final String pkName = "PrimaryKey";
        final Map<Integer, String> pkMap = uqMap.get(pkName);
        if (pkMap == null) {
            return info;
        }
        final Set<Entry<Integer, String>> entrySet = pkMap.entrySet();
        for (Entry<Integer, String> entry : entrySet) {
            info.addPrimaryKey(entry.getValue(), pkName);
        }
        return info;
    }

    // ===================================================================================
    //                                                                          Unique Key
    //                                                                          ==========
    /**
     * Retrieves an map of the columns composing the unique key for a given table.
     * @param metaData JDBC meta data. (NotNull)
     * @param tableInfo The meta information of table. (NotNull)
     * @return The meta information map of unique keys. The key is unique key name. (NotNull)
     * @throws SQLException
     */
    public Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData metaData, DfTableMeta tableInfo)
            throws SQLException { // Non Primary Key Only
        final UnifiedSchema unifiedSchema = tableInfo.getUnifiedSchema();
        final String tableName = tableInfo.getTableName();
        if (tableInfo.isTableTypeView()) {
            return newLinkedHashMap();
        }
        final DfPrimaryKeyMeta pkInfo = getPrimaryKey(metaData, tableInfo);
        return getUniqueKeyMap(metaData, unifiedSchema, tableName, pkInfo.getPrimaryKeyList());
    }

    /**
     * Retrieves an map of the columns composing the unique key for a given table.
     * @param metaData JDBC meta data. (NotNull)
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (NullAllowed)
     * @param tableName The name of table. (NotNull)
     * @return The meta information map of unique keys. The key is unique key name. (NotNull)
     * @throws SQLException
     */
    public Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, List<String> pkList) throws SQLException { // non primary key only
        Map<String, Map<Integer, String>> map = doGetUniqueKeyMap(metaData, unifiedSchema, tableName, pkList, false);
        if (canRetryCaseInsensitive()) {
            if (map.isEmpty()) { // retry by lower case
                map = doGetUniqueKeyMap(metaData, unifiedSchema, tableName.toLowerCase(), pkList, true);
            }
            if (map.isEmpty()) { // retry by upper case
                map = doGetUniqueKeyMap(metaData, unifiedSchema, tableName.toUpperCase(), pkList, true);
            }
        }
        return map;
    }

    protected Map<String, Map<Integer, String>> doGetUniqueKeyMap(DatabaseMetaData metaData,
            UnifiedSchema unifiedSchema, String tableName, List<String> pkList, boolean retry) throws SQLException { // non primary key only
        final StringSet pkSet = StringSet.createAsFlexible();
        pkSet.addAll(pkList);
        final Map<String, Map<Integer, String>> uniqueKeyMap = newTableConstraintMap();
        ResultSet rs = null;
        try {
            rs = extractUniqueKeyMetaData(metaData, unifiedSchema, tableName, retry);
            if (rs == null) {
                return DfCollectionUtil.newHashMap();
            }
            while (rs.next()) {
                // /- - - - - - - - - - - - - - - - - - - - - - - -
                // same policy as table process about JDBC handling
                // (see DfTableHandler.java)
                // - - - - - - - - - -/

                final String metaTableName = rs.getString(3);
                if (checkMetaTableDiffIfNeeds(tableName, metaTableName)) {
                    continue;
                }

                final boolean isNonUnique;
                {
                    final Boolean nonUnique = rs.getBoolean(4);
                    isNonUnique = (nonUnique != null && nonUnique);
                }
                if (isNonUnique) {
                    continue;
                }

                final String indexType;
                {
                    indexType = rs.getString(7);
                }

                final String columnName = rs.getString(9);
                if (columnName == null || columnName.trim().length() == 0) {
                    continue;
                }

                if (pkSet.contains(columnName)) {
                    continue;
                }

                // check except columns
                if (isColumnExcept(unifiedSchema, tableName, columnName)) {
                    assertUQColumnNotExcepted(unifiedSchema, tableName, columnName);
                }

                final String indexName = rs.getString(6);
                final Integer ordinalPosition;
                {
                    final String ordinalPositionString = rs.getString(8);
                    if (ordinalPositionString == null) {
                        String msg = "The unique columnName should have ordinal-position but null: ";
                        msg = msg + " columnName=" + columnName + " indexType=" + indexType;
                        _log.warn(msg);
                        continue;
                    }
                    try {
                        ordinalPosition = Integer.parseInt(ordinalPositionString);
                    } catch (NumberFormatException e) {
                        String msg = "The unique column should have ordinal-position as number but: ";
                        msg = msg + ordinalPositionString + " columnName=" + columnName + " indexType=" + indexType;
                        _log.warn(msg);
                        continue;
                    }
                }

                if (uniqueKeyMap.containsKey(indexName)) {
                    final Map<Integer, String> uniqueElementMap = uniqueKeyMap.get(indexName);
                    uniqueElementMap.put(ordinalPosition, columnName);
                } else {
                    final Map<Integer, String> uniqueElementMap = new LinkedHashMap<Integer, String>();
                    uniqueElementMap.put(ordinalPosition, columnName);
                    uniqueKeyMap.put(indexName, uniqueElementMap);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return uniqueKeyMap;
    }

    protected ResultSet extractUniqueKeyMetaData(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, boolean retry) throws SQLException {
        final boolean uniqueKeyOnly = true;
        final DfDatabaseTypeFacadeProp prop = getDatabaseTypeFacadeProp();
        return DfIndexExtractor.delegateExtractIndexInfoMetaData(metaData, unifiedSchema, tableName, uniqueKeyOnly,
                retry, prop);
    }

    protected void assertUQColumnNotExcepted(UnifiedSchema unifiedSchema, String tableName, String columnName) {
        if (isColumnExcept(unifiedSchema, tableName, columnName)) {
            String msg = "UQ columns are unsupported on 'columnExcept' property:";
            msg = msg + " unifiedSchema=" + unifiedSchema;
            msg = msg + " tableName=" + tableName;
            msg = msg + " columnName=" + columnName;
            throw new DfIllegalPropertySettingException(msg);
        }
    }
}