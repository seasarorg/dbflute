package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 * @since 0.8.0 (2008/09/20 Saturday)
 */
public final class DfAdditionalTableProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param prop Properties. (NotNull)
     */
    public DfAdditionalTableProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                  additionalTableMap
    //                                                                  ==================
    public static final String KEY_additionalTableMap = "additionalTableMap";
    protected Map<String, Object> _additionalTableMap;

    public Map<String, Object> getAdditionalTableMap() {
        if (_additionalTableMap == null) {
            _additionalTableMap = mapProp("torque." + KEY_additionalTableMap, DEFAULT_EMPTY_MAP);
        }
        return _additionalTableMap;
    }

    // ===================================================================================
    //                                                                      Finding Helper
    //                                                                      ==============
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, String>> findColumnMap(String tableName) {
        final Map<String, Object> componentMap = (Map<String, Object>) getAdditionalTableMap().get(tableName);
        return (Map<String, Map<String, String>>) componentMap.get("columnMap");
    }

    public String findColumnType(String tableName, String columnName) {
        final Map<String, Map<String, String>> columnMap = findColumnMap(tableName);
        final Map<String, String> elementMap = columnMap.get(columnName);
        return elementMap.get("type");
    }

    public boolean isColumnRequired(String tableName, String columnName) {
        final Map<String, Map<String, String>> columnMap = findColumnMap(tableName);
        final Map<String, String> elementMap = columnMap.get(columnName);
        return "true".equalsIgnoreCase(elementMap.get("required"));
    }

    public String findColumnSize(String tableName, String columnName) {
        final Map<String, Map<String, String>> columnMap = findColumnMap(tableName);
        final Map<String, String> elementMap = columnMap.get(columnName);
        return elementMap.get("size");
    }

    public boolean isColumnAutoIncrement(String tableName, String columnName) {
        final Map<String, Map<String, String>> columnMap = findColumnMap(tableName);
        final Map<String, String> elementMap = columnMap.get(columnName);
        return "true".equalsIgnoreCase(elementMap.get("autoIncrement"));
    }

    public boolean isColumnPrimaryKey(String tableName, String columnName) {
        final Map<String, Map<String, String>> columnMap = findColumnMap(tableName);
        final Map<String, String> elementMap = columnMap.get(columnName);
        return "true".equalsIgnoreCase(elementMap.get("primaryKey"));
    }
}