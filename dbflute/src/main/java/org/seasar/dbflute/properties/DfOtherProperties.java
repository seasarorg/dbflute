package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;

/**
 * Build properties for Torque.
 * 
 * @author jflute
 */
public final class DfOtherProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfOtherProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                                   Stop Generate
    //                                                                   =============
    public boolean isStopGenerateExtendedBhv() {
        return booleanProp("torque.isStopGenerateExtendedBhv", false);
    }

    public boolean isStopGenerateExtendedDao() {
        return booleanProp("torque.isStopGenerateExtendedDao", false);
    }

    public boolean isStopGenerateExtendedEntity() {
        return booleanProp("torque.isStopGenerateExtendedEntity", false);
    }

    // ===============================================================================
    //                                                                  Extract Accept
    //                                                                  ==============
    public String getExtractAcceptStartBrace() {
        return stringProp("torque.extractAcceptStartBrace", "@{");
    }

    public String getExtractAcceptEndBrace() {
        return stringProp("torque.extractAcceptEndBrace", "@}");
    }

    public String getExtractAcceptDelimiter() {
        return stringProp("torque.extractAcceptDelimiter", "@;");
    }

    public String getExtractAcceptEqual() {
        return stringProp("torque.extractAcceptEqual", "@=");
    }
    
    // ===============================================================================
    //                                                         Non PrimaryKey Writable
    //                                                         =======================
    public boolean isAvailableNonPrimaryKeyWritable() {
        return booleanProp("torque.isAvailableNonPrimaryKeyWritable", false);
    }

    // ===============================================================================
    //                                                             MultipleFK Property
    //                                                             ===================
    public static final String KEY_multipleFKPropertyMap = "multipleFKPropertyMap";
    protected Map<String, Map<String, Map<String, String>>> _multipleFKPropertyMap;

    public Map<String, Map<String, Map<String, String>>> getMultipleFKPropertyMap() {
        if (_multipleFKPropertyMap == null) {
            // TODO: @jflute - 真面目に展開すること。
            final Object obj = mapProp("torque." + KEY_multipleFKPropertyMap, DEFAULT_EMPTY_MAP);
            _multipleFKPropertyMap = (Map<String, Map<String, Map<String, String>>>) obj;
        }

        return _multipleFKPropertyMap;
    }

    public DfFlexibleNameMap<String, Map<String, Map<String, String>>> getMultipleFKPropertyMapAsFlexible() {
        return new DfFlexibleNameMap<String, Map<String, Map<String, String>>>(getMultipleFKPropertyMap());
    }

    public String getMultipleFKPropertyColumnAliasName(String tableName, java.util.List<String> columnNameList) {
        final Map<String, Map<String, String>> foreignKeyMap = getMultipleFKPropertyMapAsFlexible().get(tableName);
        if (foreignKeyMap == null) {
            return "";
        }
        final String columnKey = createMultipleFKPropertyColumnKey(columnNameList);
        final DfFlexibleNameMap<String, Map<String, String>> foreignKeyFxMap = getMultipleFKPropertyForeignKeyMapAsFlexible(foreignKeyMap);
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

    protected DfFlexibleNameMap<String, Map<String, String>> getMultipleFKPropertyForeignKeyMapAsFlexible(
            final Map<String, Map<String, String>> foreignKeyMap) {
        final DfFlexibleNameMap<String, Map<String, String>> foreignKeyFxMap = new DfFlexibleNameMap<String, Map<String, String>>(
                foreignKeyMap);
        return foreignKeyFxMap;
    }
}