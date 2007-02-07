package org.seasar.dbflute.properties;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
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

        // TODO: テーブル名や列名のチェックをしたい！

        return _includeQueryMap;
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
    
    protected boolean isAvailable(String javaTypeName, String queryTypeName, String tableName, String columnName) {
        if (containsQueryType(javaTypeName, queryTypeName)) {
            return containsTableColumn(javaTypeName, queryTypeName, tableName, columnName);
        }
        return true;
    }

    protected boolean containsQueryType(String javaTypeName, String queryTypeName) {
        final Map<String, Map<String, List<String>>> map = getIncludeQueryMap().get(javaTypeName);
        if (map != null && map.get(queryTypeName) != null) {
            return true;
        }
        return false;
    }

    protected boolean containsTableColumn(String javaTypeName, String queryTypeName, String tableName, String columnName) {
        if (getIncludeQueryMap().get(javaTypeName) == null) {
            String msg = "The javaTypeName[" + javaTypeName + "] should have the value of includeQueryMap: "
                    + getIncludeQueryMap();
            throw new IllegalStateException(msg);
        }
        if (getIncludeQueryMap().get(javaTypeName).get(queryTypeName) == null) {
            String msg = "The queryTypeName[" + queryTypeName + "] should have the value of includeQueryMap: "
                    + getIncludeQueryMap();
            throw new IllegalStateException(msg);
        }
        final Map<String, List<String>> map = getIncludeQueryMap().get(javaTypeName).get(queryTypeName);
        final DfFlexibleNameMap<String, List<String>> tableNameMap = new DfFlexibleNameMap<String, List<String>>(map);
        if (!tableNameMap.containsKey(tableName)) {
            return false;
        }
        final List<String> columnNameList = tableNameMap.get(tableName);
        final Map<String, Object> columnTmpMap = new HashMap<String, Object>();
        for (String protoColumnName : columnNameList) {
            columnTmpMap.put(protoColumnName, new Object());
        }

        final DfFlexibleNameMap<String, Object> columnNameMap = new DfFlexibleNameMap<String, Object>(columnTmpMap);
        if (!columnNameMap.containsKey(columnName)) {
            return false;
        }
        return true;
    }
}