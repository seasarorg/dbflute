package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;

/**
 * @author jflute
 */
public final class DfLittleAdjustmentProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfLittleAdjustmentProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                              Delete Old Table Class
    //                                                              ======================
    public boolean isDeleteOldTableClass() {
        return booleanProp("torque.isDeleteOldTableClass", false);
    }

    // ===================================================================================
    //                                                          Skip Generate If Same File
    //                                                          ==========================
    public boolean isSkipGenerateIfSameFile() {
        return booleanProp("torque.isSkipGenerateIfSameFile", false);
    }

    // ===================================================================================
    //                                                             Non PrimaryKey Writable
    //                                                             =======================
    public boolean isAvailableNonPrimaryKeyWritable() {
        return booleanProp("torque.isAvailableNonPrimaryKeyWritable", false);
    }

    // ===================================================================================
    //                                                     Adding Schema to Table Sql-Name
    //                                                     ===============================
    public boolean isAvailableAddingSchemaToTableSqlName() {
        return booleanProp("torque.isAvailableAddingSchemaToTableSqlName", false);
    }

    // ===================================================================================
    //                                              ToLower in Generator Underscore Method
    //                                              ======================================
    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() {
        return booleanProp("torque.isAvailableToLowerInGeneratorUnderscoreMethod", true);
    }
    
    // ===================================================================================
    //                                                              Flat Directory Package
    //                                                              ======================
    /**
     * Get the package for flat directory. Normally, this property is only for C#.
     * @return The package for flat directory. (Nullable)
     */
    public String getFlatDirectoryPackage() {
        return stringProp("torque.flatDirectoryPackage", null);
    }
    
    // ===================================================================================
    //                                                                       Compatibility
    //                                                                       =============
    public boolean isCompatibleVersionNoAutoIncrementOnMemory() {
        return booleanProp("torque.isCompatibleVersionNoAutoIncrementOnMemory", false);
    }
    
    public boolean isCompatibleSQLExceptionHandlingOldStyle() {
        return booleanProp("torque.isCompatibleSQLExceptionHandlingOldStyle", false);
    }
    
    // ===================================================================================
    //                                                                  Friendly Framework 
    //                                                                  ==================
    public boolean isUseBuri() {
        final boolean isUseBuri = booleanProp("torque.isUseBuri", false);
        if (isUseBuri) {
            return true;
        }
        return booleanProp("torque.isUseS2Buri", false);
    }

    public boolean isUseTeeda() {
        return booleanProp("torque.isUseTeeda", false);
    }

    // ===============================================================================
    //                                                             MultipleFK Property
    //                                                             ===================
    public static final String KEY_multipleFKPropertyMap = "multipleFKPropertyMap";
    protected Map<String, Map<String, Map<String, String>>> _multipleFKPropertyMap;

    @SuppressWarnings("unchecked")
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