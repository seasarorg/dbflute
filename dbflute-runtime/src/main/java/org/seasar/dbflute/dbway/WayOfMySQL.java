/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.dbway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.dbmeta.info.ColumnInfo;

/**
 * The DB way of MySQL.
 * @author jflute
 */
public class WayOfMySQL implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "SELECT LAST_INSERT_ID()";
    }
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return errorCode != null && errorCode == 1062;
    }
    
    // ===================================================================================
    //                                                                    Full-Text Search
    //                                                                    ================
    /**
     * Match for full-text search.
     * @param textColumnList The list of text column. (NotNull, NotEmpty, StringColumn, ThisTableColumn)
     * @param value The condition value. (Nullable: If the value is null or empty, it does not make condition!)
     * @param modifier The modifier of full-text search. (Nullable: If the value is null, No modifier specified)
     * @param tableDbName The DB name of the target table. (NotNull)
     * @param aliasName The alias name of the target table. (NotNull)
     * @return The condition string of match statement. (NotNull)
     */
    public String buildMatchCondition(List<ColumnInfo> textColumnList
                                    , String value, FullTextSearchModifier modifier
                                    , String tableDbName, String aliasName) {
        if (textColumnList == null) {
            throw new IllegalArgumentException("The argument 'textColumnList' should not be null!");
        }
        if (textColumnList.isEmpty()) {
            throw new IllegalArgumentException("The argument 'textColumnList' should not be empty list!");
        }
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("The argument 'value' should not be null or empty: " + value);
        }
        if (tableDbName == null || tableDbName.trim().length() == 0) {
            throw new IllegalArgumentException("The argument 'tableDbName' should not be null or trimmed-empty: " + tableDbName);
        }
        if (aliasName == null || aliasName.trim().length() == 0) {
            throw new IllegalArgumentException("The argument 'aliasName' should not be null or trimmed-empty: " + aliasName);
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (ColumnInfo columnInfo : textColumnList) {
            if (columnInfo == null) {
                continue;
            }
            String tableOfColumn = columnInfo.getDBMeta().getTableDbName();
            if (!tableOfColumn.equalsIgnoreCase(tableDbName)) {
                String msg = "The table of the text column should be '" + tableDbName + "'";
                msg = msg + " but the table is '" + tableOfColumn + "': column=" + columnInfo;
                throw new IllegalArgumentException(msg);
            }
            Class<?> propertyType = columnInfo.getPropertyType();
            if (!String.class.isAssignableFrom(propertyType)) {
                String msg = "The text column should be String type:";
                msg = msg + " type=" + propertyType + " column=" + columnInfo;
                throw new IllegalArgumentException(msg);
            }
            String columnDbName = columnInfo.getColumnDbName();
            if (index > 0) {
                sb.append(",");
            }
            sb.append(aliasName).append(".").append(columnDbName);
            ++index;
        }
        sb.insert(0, "match(").append(") against ('").append(value).append("'");
        if (modifier != null) {
            sb.append(" ").append(modifier.code());
        }
        sb.append(")");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                     ENUM Definition
    //                                                                     ===============
    public enum FullTextSearchModifier {
        InBooleanMode("IN BOOLEAN MODE")
        , InNatualLanguageMode("IN NATURAL LANGUAGE MODE")
        , InNatualLanguageModeWithQueryExpansion("IN NATURAL LANGUAGE MODE WITH QUERY EXPANSION")
        , WithQueryExpansion("WITH QUERY EXPANSION");
        private static final Map<String, FullTextSearchModifier> _codeValueMap = new HashMap<String, FullTextSearchModifier>();
        static { for (FullTextSearchModifier value : values()) { _codeValueMap.put(value.code().toLowerCase(), value); } }
        private String _code;
        private FullTextSearchModifier(String code) { _code = code; }
        public String code() { return _code; }
        public static FullTextSearchModifier codeOf(Object code) {
            if (code == null) { return null; } return _codeValueMap.get(code.toString().toLowerCase());
        }
    }
}
