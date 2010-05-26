package org.seasar.dbflute.properties;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.StringSet;

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
    //                                                               Little Adjustment Map
    //                                                               =====================
    public static final String KEY_littleAdjustmentMap = "littleAdjustmentMap";
    protected Map<String, Object> _littleAdjustmentMap;

    public Map<String, Object> getLittleAdjustmentMap() {
        if (_littleAdjustmentMap == null) {
            _littleAdjustmentMap = mapProp("torque." + KEY_littleAdjustmentMap, DEFAULT_EMPTY_MAP);
        }
        return _littleAdjustmentMap;
    }

    public String getProperty(String key, String defaultValue) {
        return getPropertyIfNotBuildProp(key, defaultValue, getLittleAdjustmentMap());
    }

    public boolean isProperty(String key, boolean defaultValue) {
        return isPropertyIfNotBuildProp(key, defaultValue, getLittleAdjustmentMap());
    }

    // ===================================================================================
    //                                                     Adding Schema to Table SQL Name
    //                                                     ===============================
    public boolean isAvailableAddingSchemaToTableSqlName() {
        return isProperty("isAvailableAddingSchemaToTableSqlName", false);
    }

    public boolean isAvailableAddingCatalogToTableSqlName() {
        return isProperty("isAvailableAddingCatalogToTableSqlName", false);
    }

    // ===================================================================================
    //                                                                 Database Dependency
    //                                                                 ===================
    public boolean isAvailableDatabaseDependency() {
        return isProperty("isAvailableDatabaseDependency", false);
    }

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    public boolean isAvailableNonPrimaryKeyWritable() {
        return isProperty("isAvailableNonPrimaryKeyWritable", false);
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public boolean isCheckSelectedClassification() {
        return isProperty("isCheckSelectedClassification", false);
    }

    public boolean isForceClassificationSetting() {
        return isProperty("isForceClassificationSetting", false);
    }

    public boolean isCDefToStringReturnsName() { // It's closet!
        return isProperty("isCDefToStringReturnsName", false);
    }

    public boolean isMakeEntityOldStyleClassify() { // It's closet!
        return isProperty("isMakeEntityOldStyleClassify", true);
    }

    // ===================================================================================
    //                                                                              Entity
    //                                                                              ======
    public boolean isMakeEntityChaseRelation() {
        return isProperty("isMakeEntityChaseRelation", false);
    }

    public boolean isEntityConvertEmptyStringToNull() {
        return isProperty("isEntityConvertEmptyStringToNull", false);
    }

    // ===================================================================================
    //                                                                      ConditionQuery
    //                                                                      ==============
    public boolean isMakeConditionQueryEqualEmptyString() {
        return isProperty("isMakeConditionQueryEqualEmptyString", false);
    }

    public boolean isMakeConditionQueryNotEqualAsStandard() { // It's closet!
        // DBFlute has used tradition for a long time
        // so default value is false here except DBMS that tradition is unsupported
        final boolean defalutValue = getBasicProperties().isDatabaseAsTraditionNotEqualUnsupported();
        return isProperty("isMakeConditionQueryNotEqualAsStandard", defalutValue);
    }

    public String getConditionQueryNotEqualDefinitionName() {
        // for AbstractConditionQuery's definition name
        return isMakeConditionQueryNotEqualAsStandard() ? "CK_NES" : "CK_NET";
    }

    // ===================================================================================
    //                                                                     Make Deprecated
    //                                                                     ===============
    public boolean isMakeDeprecated() {
        return isProperty("isMakeDeprecated", false);
    }

    public boolean isMakeRecentlyDeprecated() {
        return isProperty("isMakeRecentlyDeprecated", true);
    }

    // ===================================================================================
    //                                                                  Extended Component
    //                                                                  ==================
    public boolean hasExtendedImplementedInvokerAssistantClass() {
        String str = getExtendedImplementedInvokerAssistantClass();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    public String getExtendedImplementedInvokerAssistantClass() { // Java Only
        return getProperty("extendedImplementedInvokerAssistantClass", null);
    }

    public boolean hasExtendedImplementedCommonColumnAutoSetupperClass() {
        String str = getExtendedImplementedCommonColumnAutoSetupperClass();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    public String getExtendedImplementedCommonColumnAutoSetupperClass() { // Java Only
        return getProperty("extendedImplementedCommonColumnAutoSetupperClass", null);
    }

    public boolean hasExtendedS2DaoSettingClassValid() {
        String str = getExtendedS2DaoSettingClass();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    public String getExtendedS2DaoSettingClass() { // CSharp Only
        return getProperty("extendedS2DaoSettingClass", null);
    }

    // ===================================================================================
    //                                                                          Short Char
    //                                                                          ==========
    public boolean isShortCharHandlingValid() {
        return !getShortCharHandlingMode().equalsIgnoreCase("NONE");
    }

    public String getShortCharHandlingMode() {
        String property = getProperty("shortCharHandlingMode", "NONE");
        return property.toUpperCase();
    }

    public String getShortCharHandlingModeCode() {
        return getShortCharHandlingMode().substring(0, 1);
    }

    // ===================================================================================
    //                                                                               Quote
    //                                                                               =====
    protected Set<String> _quoteTableNameSet;

    protected Set<String> getQuoteTableNameSet() { // It's closet!
        if (_quoteTableNameSet != null) {
            return _quoteTableNameSet;
        }
        final Map<String, Object> littleAdjustmentMap = getLittleAdjustmentMap();
        final Object obj = littleAdjustmentMap.get("quoteTableNameList");
        if (obj == null) {
            return new HashSet<String>();
        }
        final List<String> list = castToList(obj, "littleAdjustmentMap.quoteTableNameList");
        _quoteTableNameSet = StringSet.createAsFlexible();
        _quoteTableNameSet.addAll(list);
        return _quoteTableNameSet;
    }

    public boolean isQuoteTable(String tableName) {
        return getQuoteTableNameSet().contains(tableName);
    }

    public String quoteTableNameIfNeeds(String tableName) {
        return quoteTableNameIfNeeds(tableName, false);
    }

    public String quoteTableNameIfNeeds(String tableName, boolean directUse) {
        if (!isQuoteTable(tableName)) {
            return tableName;
        }
        final String beginQuote;
        final String endQuote;
        if (getBasicProperties().isDatabaseSQLServer()) {
            beginQuote = "[";
            endQuote = "]";
        } else {
            beginQuote = directUse ? "\"" : "\\\"";
            endQuote = beginQuote;
        }
        return beginQuote + tableName + endQuote;
    }

    // ===================================================================================
    //                                                                          Value Type
    //                                                                          ==========
    // S2Dao.NET does not implement ValueType attribute,
    // so this property is INVALID now. At the future,
    // DBFlute may implement ValueType Framework. 
    public boolean isUseAnsiStringTypeToNotUnicode() { // It's closet! CSharp Only
        return isProperty("isUseAnsiStringTypeToNotUnicode", false);
    }

    // ===================================================================================
    //                                                                   Alternate Control
    //                                                                   =================
    public boolean isAlternateGenerateControlValid() {
        final String str = getAlternateGenerateControl();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    public String getAlternateGenerateControl() { // It's closet!
        return getProperty("alternateGenerateControl", null);
    }

    public boolean isAlternateSql2EntityControlValid() {
        final String str = getAlternateSql2EntityControl();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    public String getAlternateSql2EntityControl() { // It's closet!
        return getProperty("alternateSql2EntityControl", null);
    }

    // ===================================================================================
    //                                                                       Stop Generate
    //                                                                       =============
    public boolean isStopGenerateExtendedBhv() { // It's closet and secret!
        return isProperty("isStopGenerateExtendedBhv", false);
    }

    public boolean isStopGenerateExtendedDao() { // It's closet and secret!
        return isProperty("isStopGenerateExtendedDao", false);
    }

    public boolean isStopGenerateExtendedEntity() { // It's closet and secret!
        return isProperty("isStopGenerateExtendedEntity", false);
    }

    // ===================================================================================
    //                                                              Delete Old Table Class
    //                                                              ======================
    public boolean isDeleteOldTableClass() { // It's closet and internal!
        // The default value is true since 0.8.8.1.
        return isProperty("isDeleteOldTableClass", true);
    }

    // ===================================================================================
    //                                                          Skip Generate If Same File
    //                                                          ==========================
    public boolean isSkipGenerateIfSameFile() { // It's closet and internal!
        // The default value is true since 0.7.8.
        return isProperty("isSkipGenerateIfSameFile", true);
    }

    // ===================================================================================
    //                                              ToLower in Generator Underscore Method
    //                                              ======================================
    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() { // It's closet and internal!
        return isProperty("isAvailableToLowerInGeneratorUnderscoreMethod", true);
    }

    // ===================================================================================
    //                                                                      Flat Expansion
    //                                                                      ==============
    public boolean isMakeFlatExpansion() { // It's closet until review!
        return isProperty("isMakeFlatExpansion", false);
    }

    // ===================================================================================
    //                                                                               S2Dao
    //                                                                               =====
    public boolean isMakeDaoInterface() { // It's closet! CSharp Only
        if (isTargetLanguageCSharp()) {
            return true; // It is not implemented at CSharp yet
        }
        final boolean makeDaoInterface = booleanProp("torque.isMakeDaoInterface", false);
        if (makeDaoInterface) {
            String msg = "Dao interfaces are unsupported since DBFlute-0.8.7!";
            throw new UnsupportedOperationException(msg);
        }
        return false;
    }

    protected boolean isTargetLanguageCSharp() {
        return getBasicProperties().isTargetLanguageCSharp();
    }

    // ===================================================================================
    //                                                                          Compatible
    //                                                                          ==========
    public boolean isCompatibleAutoMappingOldStyle() { // It's closet!
        return isProperty("isCompatibleAutoMappingOldStyle", false);
    }
}