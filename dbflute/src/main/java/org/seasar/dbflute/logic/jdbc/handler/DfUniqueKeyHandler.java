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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * @author jflute
 */
public class DfUniqueKeyHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfUniqueKeyHandler.class);

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * Retrieves a list of the columns composing the primary key for a given table.
     * @param metaData JDBC meta data. (NotNull)
     * @param schemaName Schema name. (NotNull & AllowedEmpty)
     * @param tableMetaInfo The meta information of table. (NotNull)
     * @return A list of the primary key parts for <code>tableName</code>. (NotNull)
     * @throws SQLException
     */
    public List<String> getPrimaryColumnNameList(DatabaseMetaData metaData, String schemaName,
            DfTableMetaInfo tableMetaInfo) throws SQLException {
        schemaName = filterSchemaName(schemaName);
        schemaName = tableMetaInfo.selectMetaExtractingSchemaName(schemaName);
        final String tableName = tableMetaInfo.getTableName();
        return getPrimaryColumnNameList(metaData, schemaName, tableName);
    }

    /**
     * Retrieves a list of the columns composing the primary key for a given table.
     * @param metaData JDBC meta data. (NotNull)
     * @param schemaName Schema name. (NotNull & AllowedEmpty)
     * @param tableName The name of table. (NotNull)
     * @return A list of the primary key parts for <code>tableName</code>. (NotNull)
     * @throws SQLException
     */
    public List<String> getPrimaryColumnNameList(DatabaseMetaData metaData, String schemaName, String tableName)
            throws SQLException {
        schemaName = filterSchemaName(schemaName);

        final List<String> primaryKeyColumnNameList = new ArrayList<String>();
        if (!isPrimaryKeyExtractingSupported()) {
            return primaryKeyColumnNameList;
        }
        ResultSet parts = null;
        try {
            parts = getPrimaryKeyResultSetFromDBMeta(metaData, schemaName, tableName);
            while (parts.next()) {
                primaryKeyColumnNameList.add(getPrimaryKeyColumnNameFromDBMeta(parts));
            }
        } finally {
            if (parts != null) {
                parts.close();
            }
        }
        return primaryKeyColumnNameList;
    }

    protected ResultSet getPrimaryKeyResultSetFromDBMeta(DatabaseMetaData dbMeta, String schemaName, String tableName)
            throws SQLException {
        return dbMeta.getPrimaryKeys(null, schemaName, tableName);
    }

    protected String getPrimaryKeyColumnNameFromDBMeta(ResultSet resultSet) throws SQLException {
        return resultSet.getString(4);
    }

    // ===================================================================================
    //                                                                          Unique Key
    //                                                                          ==========
    public Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData dbMeta, String schemaName,
            DfTableMetaInfo tableMetaInfo) throws SQLException { // Non Primary Key Only
        schemaName = filterSchemaName(schemaName);
        schemaName = tableMetaInfo.selectMetaExtractingSchemaName(schemaName);
        final String tableName = tableMetaInfo.getTableName();
        if (tableMetaInfo.isTableTypeView()) {
            return new LinkedHashMap<String, Map<Integer, String>>();
        }
        final List<String> primaryColumnNameList = getPrimaryColumnNameList(dbMeta, schemaName, tableMetaInfo);
        return getUniqueKeyMap(dbMeta, schemaName, tableName, primaryColumnNameList);
    }

    public Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData dbMeta, String schemaName,
            String tableName, List<String> primaryColumnNameList) throws SQLException { // Non Primary Key Only
        final Map<String, Map<Integer, String>> uniqueMap = new LinkedHashMap<String, Map<Integer, String>>();
        ResultSet parts = null;
        try {
            final boolean uniqueKeyOnly = true;
            parts = dbMeta.getIndexInfo(null, schemaName, tableName, uniqueKeyOnly, true);
            while (parts.next()) {
                final boolean isNonUnique;
                {
                    final String nonUnique = parts.getString(4);
                    isNonUnique = (nonUnique != null && nonUnique.equalsIgnoreCase("true"));
                }
                if (isNonUnique) {
                    continue;
                }

                final String indexType;
                {
                    indexType = parts.getString(7);
                }

                final String columnName = parts.getString(9);
                if (columnName == null || columnName.trim().length() == 0) {
                    continue;
                }

                if (primaryColumnNameList.contains(columnName)) {
                    continue;
                }
                if (isColumnExcept(schemaName, columnName)) {
                    continue;
                }

                final String indexName = parts.getString(6);
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

                if (uniqueMap.containsKey(indexName)) {
                    final Map<Integer, String> uniqueElementMap = uniqueMap.get(indexName);
                    uniqueElementMap.put(ordinalPosition, columnName);
                } else {
                    final Map<Integer, String> uniqueElementMap = new LinkedHashMap<Integer, String>();
                    uniqueElementMap.put(ordinalPosition, columnName);
                    uniqueMap.put(indexName, uniqueElementMap);
                }
            }
        } finally {
            if (parts != null) {
                parts.close();
            }
        }
        return uniqueMap;
    }
}