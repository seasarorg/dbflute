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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableNameHandler.DfTableMetaInfo;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author jflute
 */
public class DfUniqueKeyHandler extends DfAbstractMetaDataHandler {

    public static final Log _log = LogFactory.getLog(DfUniqueKeyHandler.class);

    /**
     * Retrieves a list of the columns composing the primary key for a given table.
     * <p>
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve PK information.
     * @return A list of the primary key parts for <code>tableName</code>.
     * @throws SQLException
     */
    public List<String> getPrimaryColumnNameList(DatabaseMetaData dbMeta, String schemaName, String tableName)
            throws SQLException {
        final List<String> primaryKeyColumnNameList = new ArrayList<String>();
        ResultSet parts = null;
        try {
            parts = getPrimaryKeyResultSetFromDBMeta(dbMeta, schemaName, tableName);
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

    // {WEBから抜粋}
    // 
    //テーブルのインデックスと統計情報の記述を取得します。 NON_UNIQUE、TYPE、INDEX_NAME、ORDINAL_POSITION の順に並べます。
    //インデックス列の記述には以下のカラムがあります。
    //
    //   1. TABLE_CAT String => テーブル カタログ (null の場合もあります)。
    //   2. TABLE_SCHEM String => テーブル スキーマ (null の場合もあります)。
    //   3. TABLE_NAME String => テーブル名。
    //   4. NON_UNIQUE boolean => 一意でないインデックスを許可するかどうか。TYPE が tableIndexStatistic の場合は false。
    //   5. INDEX_QUALIFIER String => インデックス カタログ (null の場合もあります)。TYPE が tableIndexStatistic の場合は null。
    //   6. INDEX_NAME String => インデックス名。TYPE が tableIndexStatistic の場合は null。
    //   7. TYPE short => インデックス タイプ。
    //          * tableIndexStatistic - テーブルのインデックス記述と共に返されるテーブルの統計情報を識別。
    //          * tableIndexClustered - クラスタ化されたインデックス。
    //          * tableIndexHashed - ハッシュ化されたインデックス。
    //          * tableIndexOther - ほかの形式のインデックス。 
    //   8. ORDINAL_POSITION short => インデックス内の列の連番。TYPE が tableIndexStatistic の場合は 0。
    //   9. COLUMN_NAME String => 列名。TYPE が tableIndexStatistic の場合は null。
    //  10. ASC_OR_DESC String => 列のソート順。"A" => 昇順。"D" => 降順。ソート順をサポートしていない場合は null。TYPE が tableIndexStatistic の場合は null。
    //  11. CARDINALITY int => TYPE が tableIndexStatistic の場合は、テーブル内の行数。そのほかの場合は、インデックス内の一意の値の数。
    //  12. PAGES int => TYPE が tableIndexStatistic の場合は、テーブルのページ数。そのほかの場合は、現在のインデックスのページ数。
    //  13. FILTER_CONDITION String => フィルタがある場合は、そのフィルタの状態 (null の場合もあります)。 
    //
    public Map<String, Map<Integer, String>> getUniqueColumnNameList(DatabaseMetaData dbMeta, String schemaName,
            DfTableMetaInfo tableMetaInfo) throws SQLException {
        if (tableMetaInfo.isTableTypeView()) {
            return new LinkedHashMap<String, Map<Integer, String>>();
        }
        final String tableName = tableMetaInfo.getTableName();
        final List<String> primaryColumnNameList = getPrimaryColumnNameList(dbMeta, schemaName, tableName);
        final Map<String, Map<Integer, String>> uniqueMap = new LinkedHashMap<String, Map<Integer, String>>();

        ResultSet parts = null;
        try {
            parts = dbMeta.getIndexInfo(null, schemaName, tableName, true, true);
            while (parts.next()) {
                final boolean isNonUnique;
                {
                    final String nonUnique = parts.getString(4);
                    isNonUnique = (nonUnique != null && nonUnique.equals("true") ? true : false);
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