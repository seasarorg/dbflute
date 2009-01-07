package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.collection.DfFlexibleMap;

/**
 * @author jflute
 */
public final class DfMultipleFKPropertyProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfMultipleFKPropertyProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                 MultipleFK Property
    //                                                                 ===================
    public static final String KEY_multipleFKPropertyMap = "multipleFKPropertyMap";
    protected Map<String, Map<String, Map<String, String>>> _multipleFKPropertyMap;

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Map<String, String>>> getMultipleFKPropertyMap() { // It's closet!
        if (_multipleFKPropertyMap == null) {
            final Object obj = mapProp("torque." + KEY_multipleFKPropertyMap, DEFAULT_EMPTY_MAP);
            _multipleFKPropertyMap = (Map<String, Map<String, Map<String, String>>>) obj;
        }
        return _multipleFKPropertyMap;
    }

    public DfFlexibleMap<String, Map<String, Map<String, String>>> getMultipleFKPropertyMapAsFlexible() {
        return new DfFlexibleMap<String, Map<String, Map<String, String>>>(getMultipleFKPropertyMap());
    }

    public String getMultipleFKPropertyColumnAliasName(String tableName, java.util.List<String> columnNameList) {
        final Map<String, Map<String, String>> foreignKeyMap = getMultipleFKPropertyMapAsFlexible().get(tableName);
        if (foreignKeyMap == null) {
            return "";
        }
        final String columnKey = createMultipleFKPropertyColumnKey(columnNameList);
        final DfFlexibleMap<String, Map<String, String>> foreignKeyFxMap = getMultipleFKPropertyForeignKeyMapAsFlexible(foreignKeyMap);
        final Map<String, String> foreignPropertyElement = foreignKeyFxMap.get(columnKey);
        if (foreignPropertyElement == null) {
            return "";
        }
        final String columnAliasName = foreignPropertyElement.get("columnAliasName");
        return columnAliasName;
    }

    protected String createMultipleFKPropertyColumnKey(java.util.List<String> columnNameList) {
        final StringBuilder sb = new StringBuilder();
        for (String columnName : columnNameList) {
            sb.append("/").append(columnName);
        }
        sb.delete(0, "/".length());
        return sb.toString();
    }

    protected DfFlexibleMap<String, Map<String, String>> getMultipleFKPropertyForeignKeyMapAsFlexible(
            final Map<String, Map<String, String>> foreignKeyMap) {
        final DfFlexibleMap<String, Map<String, String>> foreignKeyFxMap = new DfFlexibleMap<String, Map<String, String>>(
                foreignKeyMap);
        return foreignKeyFxMap;
    }
}