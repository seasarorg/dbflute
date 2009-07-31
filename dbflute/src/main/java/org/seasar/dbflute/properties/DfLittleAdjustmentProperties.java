package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.util.DfStringUtil;

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

    // ===================================================================================
    //                                                                 Database Dependency
    //                                                                 ===================
    public boolean isAvailableDatabaseDependency() {
        return isProperty("isAvailableDatabaseDependency", false);
    }

    // ===================================================================================
    //                                                             Non PrimaryKey Writable
    //                                                             =======================
    public boolean isAvailableNonPrimaryKeyWritable() {
        return isProperty("isAvailableNonPrimaryKeyWritable", false);
    }

    // ===================================================================================
    //                                              ToLower in Generator Underscore Method
    //                                              ======================================
    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() { // It's closet!
        return isProperty("isAvailableToLowerInGeneratorUnderscoreMethod", true);
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
    //                                                                 Make ConditionQuery
    //                                                                 ===================
    public boolean isMakeConditionQueryEqualEmptyString() { // It's closet!
        return isProperty("isMakeConditionQueryEqualEmptyString", false);
    }

    public boolean isMakeConditionQueryClassificationRestriction() { // It's closet!
        return isProperty("isMakeConditionQueryClassificationRestriction", false);
    }

    // ===================================================================================
    //                                                                              Entity
    //                                                                              ======
    public boolean isMakeEntityTraceRelation() { // It's closet!
        return isProperty("isMakeEntityTraceRelation", false);
    }

    public boolean isMakeEntityS2DaoAnnotation() { // It's closet!
        return isProperty("isMakeEntityS2DaoAnnotation", false);
    }

    public boolean isMakeEntityTableClassificationNameAlias() { // It's closet!
        return isProperty("isMakeEntityTableClassificationNameAlias", false);
    }

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    public boolean isMakeFlatExpansion() { // It's closet!
        return isProperty("isMakeFlatExpansion", false);
    }

    // ===================================================================================
    //                                                                                 Dao
    //                                                                                 ===
    public boolean isMakeDaoInterface() { // It's closet!
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
    //                                                                          Result Set
    //                                                                          ==========
    public String getStatementResultSetType() {
        String value = getProperty("statementResultSetType", "ResultSet.TYPE_FORWARD_ONLY");
        if (value.startsWith("ResultSet.")) {
            return "java.sql." + value;
        }
        return value;
    }

    public String getStatementResultSetConcurrency() {
        String value = getProperty("statementResultSetConcurrency", "ResultSet.CONCUR_READ_ONLY");
        if (value.startsWith("ResultSet.")) {
            return "java.sql." + value;
        }
        return value;
    }

    // ===================================================================================
    //                                                                       Stop Generate
    //                                                                       =============
    public boolean isStopGenerateExtendedBhv() { // It's closet!
        return isProperty("isStopGenerateExtendedBhv", false);
    }

    public boolean isStopGenerateExtendedDao() { // It's closet!
        return isProperty("isStopGenerateExtendedDao", false);
    }

    public boolean isStopGenerateExtendedEntity() { // It's closet!
        return isProperty("isStopGenerateExtendedEntity", false);
    }

    // ===================================================================================
    //                                                                      Extract Accept
    //                                                                      ==============
    public String getExtractAcceptStartBrace() { // It's closet!
        return getProperty("extractAcceptStartBrace", "@{");
    }

    public String getExtractAcceptEndBrace() { // It's closet!
        return getProperty("extractAcceptEndBrace", "@}");
    }

    public String getExtractAcceptDelimiter() { // It's closet!
        return getProperty("extractAcceptDelimiter", "@;");
    }

    public String getExtractAcceptEqual() { // It's closet!
        return getProperty("extractAcceptEqual", "@=");
    }

    // ===================================================================================
    //                                                                   Alternate Control
    //                                                                   =================
    // Very Internal
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
    //                                                                       S2Dao Version
    //                                                                       =============
    // [Unused on 0.8.8.1]
    // public boolean isVersionAfter1047() {
    //     return hasS2DaoVersion() ? isS2DaoVersionGreaterEqual("1.0.47") : true;
    // }
    protected boolean hasS2DaoVersion() {
        final String value = stringProp("torque.s2daoVersion", null);
        if (value != null && value.trim().length() != 9) {
            return true;
        }
        return false;
    }

    protected String getS2DaoVersion() {
        String s2daoVersion = getProperty("s2daoVersion", null);

        // If null, return the latest version!
        return s2daoVersion != null ? DfStringUtil.replace(s2daoVersion, ".", "") : "9.9.99";
    }

    protected boolean isS2DaoVersionGreaterEqual(String targetVersion) {
        final String s2daoVersion = getS2DaoVersion();
        final String filteredTargetVersion = DfStringUtil.replace(targetVersion, ".", "");
        return s2daoVersion.compareToIgnoreCase(filteredTargetVersion) >= 0;
    }

    public boolean hasDaoSqlFileEncoding() { // for compatible!
        final String daoSqlFileEncoding = getDaoSqlFileEncoding();
        if (daoSqlFileEncoding != null && daoSqlFileEncoding.trim().length() != 0) {
            return true;
        }
        return false;
    }

    public String getDaoSqlFileEncoding() { // for compatible!
        final String defaultEncoding = "UTF-8";
        final String property = stringProp("torque.daoSqlFileEncoding", defaultEncoding);
        return !property.equals("null") ? property : defaultEncoding;
    }

    // ===================================================================================
    //                                                              Delete Old Table Class
    //                                                              ======================
    public boolean isDeleteOldTableClass() {
        // The default value is true since 0.8.8.1.
        return isProperty("isDeleteOldTableClass", true);
    }

    // ===================================================================================
    //                                                          Skip Generate If Same File
    //                                                          ==========================
    public boolean isSkipGenerateIfSameFile() { // It's closet!
        // The default value is true since 0.7.8.
        return isProperty("isSkipGenerateIfSameFile", true);
    }

    // ===================================================================================
    //                                        Extended Implemented Invoker Assistant Class
    //                                        ============================================
    public boolean hasExtendedImplementedInvokerAssistantClassValid() {
        String str = getExtendedImplementedInvokerAssistantClass();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    public String getExtendedImplementedInvokerAssistantClass() { // It's closet! Java Only
        return getProperty("extendedImplementedInvokerAssistantClass", null);
    }

    // ===================================================================================
    //                                                        Extended S2Dao Setting Class
    //                                                        ============================
    public boolean hasExtendedS2DaoSettingClassValid() {
        String str = getExtendedS2DaoSettingClass();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    public String getExtendedS2DaoSettingClass() { // It's closet! CSharp Only
        return getProperty("extendedS2DaoSettingClass", null);
    }
}