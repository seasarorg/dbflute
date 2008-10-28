package org.seasar.dbflute.properties;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.collection.DfFlexibleMap;

/**
 * @author jflute
 */
public final class DfIncludeQueryProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfIncludeQueryProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                              Properties - Query
    //                                                              ==================
    protected Map<String, Map<String, Map<String, List<String>>>> _includeQueryMap;

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Map<String, List<String>>>> getIncludeQueryMap() {
        if (_includeQueryMap != null) {
            return _includeQueryMap;
        }
        final LinkedHashMap<String, Map<String, Map<String, List<String>>>> resultMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
        final Map<String, Object> targetMap = mapProp("torque.includeQueryMap", DEFAULT_EMPTY_MAP);
        final Set<String> targetKeySet = targetMap.keySet();
        for (String key : targetKeySet) {
            final Object value = targetMap.get(key);
            if (!(value instanceof Map)) {
                String msg = "The key[" + key + "] should have map value. But the value is " + value + ": targetMap="
                        + targetMap;
                throw new IllegalStateException(msg);
            }
            final Map<String, Map<String, List<String>>> elementMap = (Map<String, Map<String, List<String>>>) value;
            resultMap.put(key, elementMap);
        }
        _includeQueryMap = resultMap;
        return _includeQueryMap;
    }

    protected Map<String, Map<String, Map<String, List<String>>>> _excludeQueryMap;

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Map<String, List<String>>>> getExcludeQueryMap() {
        if (_excludeQueryMap != null) {
            return _excludeQueryMap;
        }
        final LinkedHashMap<String, Map<String, Map<String, List<String>>>> resultMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
        final Map<String, Object> targetMap = mapProp("torque.excludeQueryMap", DEFAULT_EMPTY_MAP);
        final Set<String> targetKeySet = targetMap.keySet();
        for (String key : targetKeySet) {
            final Object value = targetMap.get(key);
            if (!(value instanceof Map)) {
                String msg = "The key[" + key + "] should have map value. But the value is " + value + ": targetMap="
                        + targetMap;
                throw new IllegalStateException(msg);
            }
            final Map<String, Map<String, List<String>>> elementMap = (Map<String, Map<String, List<String>>>) value;
            resultMap.put(key, elementMap);
        }
        _excludeQueryMap = resultMap;
        return _excludeQueryMap;
    }

    // ===============================================================================
    //                                                                       Available
    //                                                                       =========
    // ---------------------------------------
    //                                  String
    //                                  ------
    public boolean isAvailableStringNotEqual(String tableName, String columnName) {
        return isAvailable("String", "NotEqual", tableName, columnName);
    }

    public boolean isAvailableStringGreaterThan(String tableName, String columnName) {
        return isAvailable("String", "GreaterThan", tableName, columnName);
    }

    public boolean isAvailableStringGreaterEqual(String tableName, String columnName) {
        return isAvailable("String", "GreaterEqual", tableName, columnName);
    }

    public boolean isAvailableStringLessThan(String tableName, String columnName) {
        return isAvailable("String", "LessThan", tableName, columnName);
    }

    public boolean isAvailableStringLessEqual(String tableName, String columnName) {
        return isAvailable("String", "LessEqual", tableName, columnName);
    }

    public boolean isAvailableStringPrefixSearch(String tableName, String columnName) {
        return isAvailable("String", "PrefixSearch", tableName, columnName);
    }
    
    public boolean isAvailableStringLikeSearch(String tableName, String columnName) {
        return isAvailable("String", "LikeSearch", tableName, columnName);
    }

    public boolean isAvailableStringInScope(String tableName, String columnName) {
        return isAvailable("String", "InScope", tableName, columnName);
    }

    public boolean isAvailableStringNotInScope(String tableName, String columnName) {
        return isAvailable("String", "NotInScope", tableName, columnName);
    }

    // ---------------------------------------
    //                                  Number
    //                                  ------
    public boolean isAvailableNumberNotEqual(String tableName, String columnName) {
        return isAvailable("Number", "NotEqual", tableName, columnName);
    }

    public boolean isAvailableNumberGreaterThan(String tableName, String columnName) {
        return isAvailable("Number", "GreaterThan", tableName, columnName);
    }

    public boolean isAvailableNumberGreaterEqual(String tableName, String columnName) {
        return isAvailable("Number", "GreaterEqual", tableName, columnName);
    }

    public boolean isAvailableNumberLessThan(String tableName, String columnName) {
        return isAvailable("Number", "LessThan", tableName, columnName);
    }

    public boolean isAvailableNumberLessEqual(String tableName, String columnName) {
        return isAvailable("Number", "LessEqual", tableName, columnName);
    }

    public boolean isAvailableNumberInScope(String tableName, String columnName) {
        return isAvailable("Number", "InScope", tableName, columnName);
    }

    public boolean isAvailableNumberNotInScope(String tableName, String columnName) {
        return isAvailable("Number", "NotInScope", tableName, columnName);
    }

    // ---------------------------------------
    //                                    Date
    //                                    ----
    public boolean isAvailableDateNotEqual(String tableName, String columnName) {
        return isAvailable("Date", "NotEqual", tableName, columnName);
    }

    public boolean isAvailableDateGreaterThan(String tableName, String columnName) {
        return isAvailable("Date", "GreaterThan", tableName, columnName);
    }

    public boolean isAvailableDateGreaterEqual(String tableName, String columnName) {
        return isAvailable("Date", "GreaterEqual", tableName, columnName);
    }

    public boolean isAvailableDateLessThan(String tableName, String columnName) {
        return isAvailable("Date", "LessThan", tableName, columnName);
    }

    public boolean isAvailableDateLessEqual(String tableName, String columnName) {
        return isAvailable("Date", "LessEqual", tableName, columnName);
    }
    
    public boolean isAvailableDateFromTo(String tableName, String columnName) {
        return isAvailable("Date", "FromTo", tableName, columnName);
    }

    // ---------------------------------------
    //                                  String Old AsInline
    //                                  ------
    public boolean isAvailableStringEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "EqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringNotEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "NotEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringGreaterThanOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "GreaterThanOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringGreaterEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "GreaterEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringLessThanOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "LessThanOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringLessEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "LessEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringPrefixSearchOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "PrefixSearchOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringInScopeOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "InScopeOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringNotInScopeOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "NotInScopeOldAsInline", tableName, columnName);
    }

    public boolean isAvailableStringInScopeSubQueryOldAsInline(String tableName, String columnName) {
        return isAvailable("String", "InScopeSubQueryOldAsInline", tableName, columnName);
    }

    // ---------------------------------------
    //                                  Number Old AsInline
    //                                  ------
    public boolean isAvailableNumberEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "EqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberNotEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "NotEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberGreaterThanOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "GreaterThanOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberGreaterEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "GreaterEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberLessThanOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "LessThanOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberLessEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "LessEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberInScopeOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "InScopeOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberNotInScopeOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "NotInScopeOldAsInline", tableName, columnName);
    }

    public boolean isAvailableNumberInScopeSubQueryOldAsInline(String tableName, String columnName) {
        return isAvailable("Number", "InScopeSubQueryOldAsInline", tableName, columnName);
    }

    // ---------------------------------------
    //                                    Date Old AsInline
    //                                    ----
    public boolean isAvailableDateEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Date", "EqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableDateNotEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Date", "NotEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableDateGreaterThanOldAsInline(String tableName, String columnName) {
        return isAvailable("Date", "GreaterThanOldAsInline", tableName, columnName);
    }

    public boolean isAvailableDateGreaterEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Date", "GreaterEqualOldAsInline", tableName, columnName);
    }

    public boolean isAvailableDateLessThanOldAsInline(String tableName, String columnName) {
        return isAvailable("Date", "LessThanOldAsInline", tableName, columnName);
    }

    public boolean isAvailableDateLessEqualOldAsInline(String tableName, String columnName) {
        return isAvailable("Date", "LessEqualOldAsInline", tableName, columnName);
    }

    protected boolean isAvailable(String javaTypeName, String queryTypeName, String tableName, String columnName) {
        if (containsQueryTypeIncludeQueryMap(javaTypeName, queryTypeName)) {
            return containsTableColumnIncludeQueryMap(javaTypeName, queryTypeName, tableName, columnName);
        }
        if (containsQueryTypeExcludeQueryMap(javaTypeName, queryTypeName)) {
            return !containsTableColumnExcludeQueryMap(javaTypeName, queryTypeName, tableName, columnName);
        }
        return true;
    }

    protected boolean containsQueryTypeIncludeQueryMap(String javaTypeName, String queryTypeName) {
        final Map<String, Map<String, List<String>>> map = getIncludeQueryMap().get(javaTypeName);
        if (map != null && map.get(queryTypeName) != null) {
            return true;
        }
        return false;
    }

    protected boolean containsQueryTypeExcludeQueryMap(String javaTypeName, String queryTypeName) {
        final Map<String, Map<String, List<String>>> map = getExcludeQueryMap().get(javaTypeName);
        if (map != null && map.get(queryTypeName) != null) {
            return true;
        }
        return false;
    }

    protected boolean containsTableColumnIncludeQueryMap(String javaTypeName, String queryTypeName, String tableName,
            String columnName) {

        final InternalTableColumnJudgement judgement = new InternalTableColumnJudgement();
        judgement.setQueryMap(getIncludeQueryMap());
        return judgement.containsTableColumn(javaTypeName, queryTypeName, tableName, columnName);
    }

    protected boolean containsTableColumnExcludeQueryMap(String javaTypeName, String queryTypeName, String tableName,
            String columnName) {

        final InternalTableColumnJudgement judgement = new InternalTableColumnJudgement();
        judgement.setQueryMap(getExcludeQueryMap());
        return judgement.containsTableColumn(javaTypeName, queryTypeName, tableName, columnName);
    }

    protected static class InternalTableColumnJudgement {
        protected Map<String, Map<String, Map<String, List<String>>>> _queryMap;

        protected boolean containsTableColumn(String javaTypeName, String queryTypeName, String tableName,
                String columnName) {
            if (_queryMap.get(javaTypeName) == null) {
                String msg = "The javaTypeName[" + javaTypeName + "] should have the value of excludeQueryMap: "
                        + _queryMap;
                throw new IllegalStateException(msg);
            }
            if (_queryMap.get(javaTypeName).get(queryTypeName) == null) {
                String msg = "The queryTypeName[" + queryTypeName + "] should have the value of excludeQueryMap: "
                        + _queryMap;
                throw new IllegalStateException(msg);
            }
            final Map<String, List<String>> map = _queryMap.get(javaTypeName).get(queryTypeName);
            final DfFlexibleMap<String, List<String>> tableNameMap = new DfFlexibleMap<String, List<String>>(
                    map);
            if (!tableNameMap.containsKey(tableName) && !tableNameMap.containsKey("$$ALL$$")) {
                return false;
            }
            List<String> columnNameList = tableNameMap.get(tableName);
            if (columnNameList == null) {
                columnNameList = tableNameMap.get("$$ALL$$");
            }
            final Map<String, Object> columnTmpMap = new HashMap<String, Object>();
            for (String protoColumnName : columnNameList) {
                columnTmpMap.put(protoColumnName, new Object());
            }

            final DfFlexibleMap<String, Object> columnNameMap = new DfFlexibleMap<String, Object>(columnTmpMap);
            if (!columnNameMap.containsKey(columnName)) {
                return false;
            }
            return true;
        }

        public Map<String, Map<String, Map<String, List<String>>>> getQueryMap() {
            return _queryMap;
        }

        public void setQueryMap(Map<String, Map<String, Map<String, List<String>>>> queryMap) {
            this._queryMap = queryMap;
        }
    }
}