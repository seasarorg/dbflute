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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/18 Saturday)
 */
public class DfIndexHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfIndexHandler.class);

    // ===================================================================================
    //                                                                        Meta Getting
    //                                                                        ============
    public Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData metaData, DfTableMetaInfo tableInfo,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException { // Non Unique Only
        final UnifiedSchema unifiedSchema = tableInfo.getUnifiedSchema();
        final String tableName = tableInfo.getTableName();
        if (tableInfo.isTableTypeView()) {
            return newLinkedHashMap();
        }
        return getIndexMap(metaData, unifiedSchema, tableName, uniqueKeyMap);
    }

    public Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException { // non unique only
        Map<String, Map<Integer, String>> resultMap = doGetIndexMap(metaData, unifiedSchema, tableName, uniqueKeyMap);
        if (resultMap.isEmpty()) { // for lower case
            resultMap = doGetIndexMap(metaData, unifiedSchema, tableName.toLowerCase(), uniqueKeyMap);
        }
        if (resultMap.isEmpty()) { // for upper case
            resultMap = doGetIndexMap(metaData, unifiedSchema, tableName.toUpperCase(), uniqueKeyMap);
        }
        return resultMap;
    }

    protected Map<String, Map<Integer, String>> doGetIndexMap(DatabaseMetaData dbMeta, UnifiedSchema unifiedSchema,
            String tableName, Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException { // Non Unique Only
        final Map<String, Map<Integer, String>> indexMap = new LinkedHashMap<String, Map<Integer, String>>();
        ResultSet rs = null;
        try {
            final boolean uniqueKeyOnly = false;
            final String catalogName = unifiedSchema.getPureCatalog();
            final String schemaName = unifiedSchema.getPureSchema();
            rs = dbMeta.getIndexInfo(catalogName, schemaName, tableName, uniqueKeyOnly, true);
            while (rs.next()) {
                // /- - - - - - - - - - - - - - - - - - - - - - - -
                // same policy as table process about JDBC handling
                // (see DfTableHandler.java)
                // - - - - - - - - - -/

                final String metaTableName = rs.getString(3);
                if (!Srl.equalsFlexibleTrimmed(tableName, metaTableName)) {
                    // same policy as column process (see DfColumnHandler.java)
                    continue;
                }

                final String indexName = rs.getString(6);
                final boolean isNonUnique;
                {
                    final Boolean nonUnique = rs.getBoolean(4);
                    isNonUnique = (nonUnique != null && nonUnique);
                }
                if (!isNonUnique) {
                    continue;
                }
                if (uniqueKeyMap != null && uniqueKeyMap.containsKey(indexName)) {
                    continue;
                }

                // Non Unique Only

                final String indexType;
                {
                    indexType = rs.getString(7);
                }

                final String columnName = rs.getString(9);
                if (columnName == null || columnName.trim().length() == 0) {
                    continue;
                }
                if (isColumnExcept(unifiedSchema, tableName, columnName)) {
                    continue;
                }
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

                if (indexMap.containsKey(indexName)) {
                    final Map<Integer, String> indexElementMap = indexMap.get(indexName);
                    indexElementMap.put(ordinalPosition, columnName);
                } else {
                    final Map<Integer, String> indexElementMap = newLinkedHashMap();
                    indexElementMap.put(ordinalPosition, columnName);
                    indexMap.put(indexName, indexElementMap);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return indexMap;
    }
}