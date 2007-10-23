package org.seasar.dbflute.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;

/**
 * Build properties for Torque.
 * 
 * @author jflute
 */
public final class DfLittleAdjustmentProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfLittleAdjustmentProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                         Non PrimaryKey Writable
    //                                                         =======================
    public boolean isAvailableNonPrimaryKeyWritable() {
        return booleanProp("torque.isAvailableNonPrimaryKeyWritable", false);
    }

    // ===============================================================================
    //                                                 Adding Schema to Table Sql-Name
    //                                                 ===============================
    public boolean isAvailableAddingSchemaToTableSqlName() {
        return booleanProp("torque.isAvailableAddingSchemaToTableSqlName", false);
    }

    // ===============================================================================
    //                                                 Adding Schema to Table Sql-Name
    //                                                 ===============================
    public boolean isAvailableEntityModifiedPropertiesAddLogicIfNeeds() {
        return booleanProp("torque.isAvailableEntityModifiedPropertiesAddLogicIfNeeds", false);
    }

    // ===============================================================================
    //                                   Common Column Intercepting On Behavior Filter
    //                                   =============================================
    public boolean isCommonColumnInterceptingOnBehaviorFilter() {
        final DfCommonColumnProperties commonColumnProp = getPropertiesHandler().getCommonColumnProperties(
                getProperties());
        if (!commonColumnProp.isExistCommonColumnSetupElement()) {
            return false;
        }
        if (commonColumnProp.isCommonColumnSetupInterceptorAspectPointDao()) {
            return false;
        }
        return booleanProp("torque.isCommonColumnInterceptingOnBehaviorFilter", true);
    }

    // ===============================================================================
    //                                        Is one to many return null If non select
    //                                        ========================================
    public boolean isOneToManyReturnNullIfNonSelect() {
        return booleanProp("torque.isOneToManyReturnNullIfNonSelect", false);
    }

    // ===============================================================================
    //                                                         Disable As-One-Relation
    //                                                         =======================
    public static final String KEY_disableAsOneRelationTableMap = "disableAsOneRelationTableMap";
    protected Map<String, String> _disableAsOneRelationTableMap;

    @SuppressWarnings("unchecked")
    protected Map<String, String> getDisableAsOneRelationTableMap() {
        if (_disableAsOneRelationTableMap == null) {
            _disableAsOneRelationTableMap = new HashMap<String, String>();
            final Map<String, Object> map = mapProp("torque." + KEY_disableAsOneRelationTableMap, DEFAULT_EMPTY_MAP);
            final Set<String> keySet = map.keySet();
            for (String key : keySet) {
                final String value = (String) map.get(key);
                _disableAsOneRelationTableMap.put(key.toLowerCase(), value != null ? value.toLowerCase() : null);
            }
        }
        return _disableAsOneRelationTableMap;
    }
    
    public boolean isDisableAsOneRelation(String tableName) {
        return getDisableAsOneRelationTableMap().containsKey(tableName.toLowerCase());
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
    
    public boolean isUseS2Buri() {
        return booleanProp("torque.isUseS2Buri", false);
    }
}