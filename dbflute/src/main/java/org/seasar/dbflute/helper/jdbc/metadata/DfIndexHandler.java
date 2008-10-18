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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;

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
    //                                                                                Main
    //                                                                                ====
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
    public Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData dbMeta, String schemaName,
            DfTableMetaInfo tableMetaInfo, Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException { // Non Unique Only
        schemaName = filterSchemaName(schemaName);
        if (tableMetaInfo.isTableTypeView()) {
            return new LinkedHashMap<String, Map<Integer, String>>();
        }

        final Map<String, Map<Integer, String>> indexMap = new LinkedHashMap<String, Map<Integer, String>>();
        ResultSet parts = null;
        try {
            final String tableName = tableMetaInfo.getTableName();
            final String realSchemaName = tableMetaInfo.selectRealSchemaName(schemaName);
            parts = dbMeta.getIndexInfo(null, realSchemaName, tableName, true, true);
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
                if (isColumnExcept(columnName)) {
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