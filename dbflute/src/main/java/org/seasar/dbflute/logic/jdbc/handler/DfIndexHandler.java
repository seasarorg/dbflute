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
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

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
    public Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData dbMeta, DfTableMetaInfo tableMetaInfo,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException { // Non Unique Only
        final String catalogSchema = tableMetaInfo.getCatalogSchema();
        final String tableName = tableMetaInfo.getTableName();
        if (tableMetaInfo.isTableTypeView()) {
            return newLinkedHashMap();
        }
        return getIndexMap(dbMeta, catalogSchema, tableName, uniqueKeyMap);
    }

    public Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData dbMeta, String catalogSchema,
            String tableName, Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException { // non unique only
        catalogSchema = filterSchemaName(catalogSchema);
        Map<String, Map<Integer, String>> resultMap = doGetIndexMap(dbMeta, catalogSchema, tableName, uniqueKeyMap);
        if (resultMap.isEmpty()) { // for lower case
            resultMap = doGetIndexMap(dbMeta, catalogSchema, tableName.toLowerCase(), uniqueKeyMap);
        }
        if (resultMap.isEmpty()) { // for upper case
            resultMap = doGetIndexMap(dbMeta, catalogSchema, tableName.toUpperCase(), uniqueKeyMap);
        }
        return resultMap;
    }

    protected Map<String, Map<Integer, String>> doGetIndexMap(DatabaseMetaData dbMeta, String catalogSchema,
            String tableName, Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException { // Non Unique Only
        final Map<String, Map<Integer, String>> indexMap = new LinkedHashMap<String, Map<Integer, String>>();
        ResultSet parts = null;
        try {
            final boolean uniqueKeyOnly = false;
            final String catalogName = extractCatalogName(catalogSchema);
            final String pureSchemaName = extractPureSchemaName(catalogSchema);
            parts = dbMeta.getIndexInfo(catalogName, pureSchemaName, tableName, uniqueKeyOnly, true);
            while (parts.next()) {
                final String indexName = parts.getString(6);
                final boolean isNonUnique;
                {
                    final String nonUnique = parts.getString(4);
                    isNonUnique = (nonUnique != null && nonUnique.equalsIgnoreCase("true"));
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
                    indexType = parts.getString(7);
                }

                final String columnName = parts.getString(9);
                if (columnName == null || columnName.trim().length() == 0) {
                    continue;
                }
                if (isColumnExcept(catalogSchema, tableName, columnName)) {
                    continue;
                }
                final Integer ordinalPosition;
                {
                    final String ordinalPositionString = parts.getString(8);
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
                    final Map<Integer, String> uniqueElementMap = indexMap.get(indexName);
                    uniqueElementMap.put(ordinalPosition, columnName);
                } else {
                    final Map<Integer, String> uniqueElementMap = new LinkedHashMap<Integer, String>();
                    uniqueElementMap.put(ordinalPosition, columnName);
                    indexMap.put(indexName, uniqueElementMap);
                }
            }
        } finally {
            if (parts != null) {
                parts.close();
            }
        }
        return indexMap;
    }
}