/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.sql2entity.cmentity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfColumnExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;

public class DfCustomizeEntityMetaExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfCustomizeEntityMetaExtractor.class);

    public static interface DfForcedJavaNativeProvider {
        String provide(String columnName);
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public Map<String, DfColumnMetaInfo> extractColumnMetaInfoMap(ResultSet rs, String sql,
            DfForcedJavaNativeProvider forcedJavaNativeProvider) throws SQLException {
        final Map<String, DfColumnMetaInfo> columnMetaInfoMap = StringKeyMap.createAsFlexibleOrdered();
        final ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            final DfColumnMetaInfo metaInfo = new DfColumnMetaInfo();

            String sql2EntityRelatedTableName = null;
            try {
                sql2EntityRelatedTableName = md.getTableName(i);
            } catch (SQLException ignored) {
                // Because this table name is not required. This is for classification.
                String msg = "ResultSetMetaData.getTableName(" + i + ") threw the exception:";
                msg = msg + " " + ignored.getMessage();
                _log.info(msg);
            }
            metaInfo.setSql2EntityRelatedTableName(sql2EntityRelatedTableName);

            String columnName = md.getColumnLabel(i);
            final String relatedColumnName = md.getColumnName(i);
            metaInfo.setSql2EntityRelatedColumnName(relatedColumnName);
            if (columnName == null || columnName.trim().length() == 0) {
                columnName = relatedColumnName;
            }
            if (columnName == null || columnName.trim().length() == 0) {
                final String ln = ln();
                String msg = "The columnName is invalid: columnName=" + columnName + ln;
                msg = msg + "ResultSetMetaData returned invalid value." + ln;
                msg = msg + "sql=" + sql;
                throw new IllegalStateException(msg);
            }
            metaInfo.setColumnName(columnName);

            final int columnType = md.getColumnType(i);
            metaInfo.setJdbcDefValue(columnType);

            final String columnTypeName = md.getColumnTypeName(i);
            metaInfo.setDbTypeName(columnTypeName);

            int columnSize = md.getPrecision(i);
            if (!DfColumnExtractor.isColumnSizeValid(columnSize)) {
                // ex) sum(COLUMN)
                columnSize = md.getColumnDisplaySize(i);
            }
            metaInfo.setColumnSize(columnSize);

            final int scale = md.getScale(i);
            metaInfo.setDecimalDigits(scale);

            if (forcedJavaNativeProvider != null) {
                final String sql2entityForcedJavaNative = forcedJavaNativeProvider.provide(columnName);
                metaInfo.setSql2EntityForcedJavaNative(sql2entityForcedJavaNative);
            }

            columnMetaInfoMap.put(columnName, metaInfo);
        }
        return columnMetaInfoMap;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}
