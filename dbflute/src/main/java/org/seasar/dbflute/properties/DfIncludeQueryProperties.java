/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.properties;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public final class DfIncludeQueryProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String ALL_MARK = "$$ALL$$";
    public static final String COMMON_COLUMN_MARK = "$$CommonColumn$$";
    public static final String VERSION_NO_MARK = "$$VersionNo$$";
    public static final String TYPE_MARK = "type:";
    protected static final String PROP_STRING = "String";
    protected static final String PROP_NUMBER = "Number";
    protected static final String PROP_DATE = "Date";
    protected static final String PROP_ORDER_BY = "OrderBy";
    protected static final String PROP_MYSELF = "Myself";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfIncludeQueryProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                   Include Query Map
    //                                                                   =================
    protected Map<String, Map<String, Map<String, List<String>>>> _includeQueryMap;
    protected final Map<String, Map<String, Map<String, List<String>>>> _excludeQueryMap = newLinkedHashMap();

    public Map<String, Map<String, Map<String, List<String>>>> getIncludeQueryMap() {
        if (_includeQueryMap != null) {
            return _includeQueryMap;
        }
        final Map<String, Map<String, Map<String, List<String>>>> resultMap = newLinkedHashMap();
        final Map<String, Object> targetMap = mapProp("torque.includeQueryMap", DEFAULT_EMPTY_MAP);
        final Set<String> targetKeySet = targetMap.keySet();
        for (String propType : targetKeySet) {
            final Object value = targetMap.get(propType);
            if (!(value instanceof Map)) {
                String msg = "The key[includeQueryMap] should have map value.";
                msg = msg + " But the value is " + value + ": targetMap=" + targetMap;
                throw new IllegalStateException(msg);
            }
            final Map<String, Map<String, List<String>>> elementMap = newLinkedHashMap();
            @SuppressWarnings("unchecked")
            final Map<String, Object> queryMap = (Map<String, Object>) value;
            for (Entry<String, Object> entry : queryMap.entrySet()) {
                final String ckey = entry.getKey();
                final Object tableColumnObj = entry.getValue();
                if (ckey.startsWith("!")) { // means exclude
                    final String filteredKey = ckey.substring("!".length());
                    @SuppressWarnings("unchecked")
                    final Map<String, List<String>> tableColumnMap = (Map<String, List<String>>) tableColumnObj;
                    reflectExcludeQuery(propType, filteredKey, tableColumnMap);
                } else { // main (include)
                    @SuppressWarnings("unchecked")
                    final Map<String, List<String>> tableColumnMap = (Map<String, List<String>>) tableColumnObj;
                    elementMap.put(ckey, tableColumnMap);
                }
            }
            resultMap.put(propType, elementMap);
        }
        _includeQueryMap = resultMap;
        return _includeQueryMap;
    }

    public Map<String, Map<String, Map<String, List<String>>>> getExcludeQueryMap() {
        getIncludeQueryMap(); // initialize
        return _excludeQueryMap;
    }

    protected void reflectExcludeQuery(String javaType, String queryType, Map<String, List<String>> tableColumnMap) {
        Map<String, Map<String, List<String>>> elementMap = _excludeQueryMap.get(javaType);
        if (elementMap == null) {
            elementMap = newLinkedHashMap();
            _excludeQueryMap.put(javaType, elementMap);
        }
        elementMap.put(queryType, tableColumnMap);
    }

    // ===================================================================================
    //                                                                           Available
    //                                                                           =========
    // -----------------------------------------------------
    //                                                String
    //                                                ------
    public boolean isAvailableStringNotEqual(Column column) {
        return isAvailable(PROP_STRING, "NotEqual", column);
    }

    public boolean isAvailableStringGreaterThan(Column column) {
        return isAvailable(PROP_STRING, "GreaterThan", column);
    }

    public boolean isAvailableStringGreaterEqual(Column column) {
        return isAvailable(PROP_STRING, "GreaterEqual", column);
    }

    public boolean isAvailableStringLessThan(Column column) {
        return isAvailable(PROP_STRING, "LessThan", column);
    }

    public boolean isAvailableStringLessEqual(Column column) {
        return isAvailable(PROP_STRING, "LessEqual", column);
    }

    public boolean isAvailableStringInScope(Column column) {
        return isAvailable(PROP_STRING, "InScope", column);
    }

    public boolean isAvailableStringNotInScope(Column column) {
        return isAvailable(PROP_STRING, "NotInScope", column);
    }

    public boolean isAvailableStringPrefixSearch(Column column) {
        return isAvailable(PROP_STRING, "PrefixSearch", column);
    }

    public boolean isAvailableStringLikeSearch(Column column) {
        return isAvailable(PROP_STRING, "LikeSearch", column);
    }

    public boolean isAvailableStringNotLikeSearch(Column column) {
        return isAvailable(PROP_STRING, "NotLikeSearch", column);
    }

    public boolean isAvailableStringEmptyString(Column column) {
        return isAvailable(PROP_STRING, "EmptyString", column);
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    public boolean isAvailableNumberNotEqual(Column column) {
        return isAvailable(PROP_NUMBER, "NotEqual", column);
    }

    public boolean isAvailableNumberGreaterThan(Column column) {
        return isAvailable(PROP_NUMBER, "GreaterThan", column);
    }

    public boolean isAvailableNumberGreaterEqual(Column column) {
        return isAvailable(PROP_NUMBER, "GreaterEqual", column);
    }

    public boolean isAvailableNumberLessThan(Column column) {
        return isAvailable(PROP_NUMBER, "LessThan", column);
    }

    public boolean isAvailableNumberLessEqual(Column column) {
        return isAvailable(PROP_NUMBER, "LessEqual", column);
    }

    public boolean isAvailableNumberRangeOf(Column column) {
        return isAvailable(PROP_NUMBER, "RangeOf", column);
    }

    public boolean isAvailableNumberInScope(Column column) {
        return isAvailable(PROP_NUMBER, "InScope", column);
    }

    public boolean isAvailableNumberNotInScope(Column column) {
        return isAvailable(PROP_NUMBER, "NotInScope", column);
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    public boolean isAvailableDateNotEqual(Column column) {
        return isAvailable(PROP_DATE, "NotEqual", column);
    }

    public boolean isAvailableDateGreaterThan(Column column) {
        return isAvailable(PROP_DATE, "GreaterThan", column);
    }

    public boolean isAvailableDateGreaterEqual(Column column) {
        return isAvailable(PROP_DATE, "GreaterEqual", column);
    }

    public boolean isAvailableDateLessThan(Column column) {
        return isAvailable(PROP_DATE, "LessThan", column);
    }

    public boolean isAvailableDateLessEqual(Column column) {
        return isAvailable(PROP_DATE, "LessEqual", column);
    }

    public boolean isAvailableDateFromTo(Column column) {
        return isAvailable(PROP_DATE, "FromTo", column); // means FromTo of Date type
    }

    public boolean isAvailableDateDateFromTo(Column column) {
        return isAvailable(PROP_DATE, "DateFromTo", column); // means DateFromTo of Date type
    }

    public boolean isAvailableDateInScope(Column column) {
        return isAvailable(PROP_DATE, "InScope", column);
    }

    public boolean isAvailableDateNotInScope(Column column) {
        return isAvailable(PROP_DATE, "NotInScope", column);
    }

    // -----------------------------------------------------
    //                                               OrderBy
    //                                               -------
    public boolean isAvailableOrderByAsc(Column column) {
        return isAvailable(PROP_ORDER_BY, "Asc", column);
    }

    public boolean isAvailableOrderByDesc(Column column) {
        return isAvailable(PROP_ORDER_BY, "Desc", column);
    }

    // -----------------------------------------------------
    //                                               Primary
    //                                               -------
    public boolean isAvailableMyselfScalarCondition(Column column) {
        return isAvailable(PROP_MYSELF, "ScalarCondition", column);
    }

    public boolean isAvailableMyselfMyselfDerived(Column column) {
        return isAvailable(PROP_MYSELF, "MyselfDerived", column);
    }

    public boolean isAvailableMyselfMyselfExists(Column column) {
        return isAvailable(PROP_MYSELF, "MyselfExists", column);
    }

    public boolean isAvailableMyselfMyselfInScope(Column column) {
        return isAvailable(PROP_MYSELF, "MyselfInScope", column);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean isAvailable(String propType, String ckey, Column column) {
        if (hasQueryTypeIncludeQueryMap(propType, ckey)) {
            return containsTableColumnIncludeQueryMap(propType, ckey, column);
        }
        if (hasQueryTypeExcludeQueryMap(propType, ckey)) {
            return !containsTableColumnExcludeQueryMap(propType, ckey, column);
        }
        return true;
    }

    protected boolean hasQueryTypeIncludeQueryMap(String propType, String ckey) {
        final Map<String, Map<String, List<String>>> map = getIncludeQueryMap().get(propType);
        return map != null && map.get(ckey) != null;
    }

    protected boolean hasQueryTypeExcludeQueryMap(String propType, String ckey) {
        final Map<String, Map<String, List<String>>> map = getExcludeQueryMap().get(propType);
        return map != null && map.get(ckey) != null;
    }

    protected boolean containsTableColumnIncludeQueryMap(String propType, String ckey, Column column) {
        return doContainsTableColumnQueryMap(propType, ckey, column, getIncludeQueryMap());
    }

    protected boolean containsTableColumnExcludeQueryMap(String propType, String ckey, Column column) {
        return doContainsTableColumnQueryMap(propType, ckey, column, getExcludeQueryMap());
    }

    protected boolean doContainsTableColumnQueryMap(String propType, String ckey, Column column,
            Map<String, Map<String, Map<String, List<String>>>> queryMap) {
        assertQueryMap(propType, ckey, queryMap);
        final String tableDbName = column.getTable().getTableDbName();
        final String columnName = column.getName();
        final Set<String> columnSet = gatherColumnSet(propType, ckey, queryMap, tableDbName);
        if (PROP_MYSELF.equalsIgnoreCase(propType)) { // e.g. ScalarCondition, MyselfDerived
            return columnSet != null; // empty column list means specified
        } else {
            if (columnSet == null || columnSet.isEmpty()) {
                return false;
            }
        }
        // either has a list element
        if (columnSet.contains(COMMON_COLUMN_MARK) && column.isCommonColumn()) {
            return true;
        }
        if (columnSet.contains(VERSION_NO_MARK) && column.isVersionNo()) {
            return true;
        }
        final String typeMark = TYPE_MARK;
        for (String columnExp : columnSet) {
            if (Srl.startsWithIgnoreCase(columnExp, typeMark)) { // e.g. type:LONGVARCHAR
                final String specifiedType = Srl.substringFirstRear(columnExp, typeMark).trim();
                final String jdbcType = column.getJdbcType();
                if (jdbcType != null && jdbcType.equalsIgnoreCase(specifiedType)) {
                    return true;
                }
            }
            if (isHitByTheHint(columnName, columnExp)) {
                return true;
            }
        }
        return false;
    }

    protected void assertQueryMap(String propType, String ckey,
            Map<String, Map<String, Map<String, List<String>>>> queryMap) {
        if (queryMap.get(propType) == null) {
            String msg = "The propType[" + propType + "] should have the value of queryMap:";
            msg = msg + " " + queryMap;
            throw new IllegalStateException(msg);
        }
        if (queryMap.get(propType).get(ckey) == null) {
            String msg = "The conditionKey[" + ckey + "] should have the value of queryMap:";
            msg = msg + " " + queryMap;
            throw new IllegalStateException(msg);
        }
    }

    protected Set<String> gatherColumnSet(String propType, String ckey,
            Map<String, Map<String, Map<String, List<String>>>> queryMap, final String tableDbName) {
        final Map<String, List<String>> tableColumnMap = queryMap.get(propType).get(ckey);
        Set<String> columnSet = null;
        for (Entry<String, List<String>> entry : tableColumnMap.entrySet()) {
            final String tableHint = entry.getKey();
            if (ALL_MARK.equalsIgnoreCase(tableHint) || isHitByTheHint(tableDbName, tableHint)) {
                if (columnSet == null) {
                    columnSet = new HashSet<String>();
                }
                columnSet.addAll(entry.getValue());
            }
        }
        return columnSet;
    }
}