package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoCSharp;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoPhp;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;

/**
 * Basic properties.
 * This class is very important at DBFlute.
 * @author jflute
 */
public final class DfBasicProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfLanguageDependencyInfo _languageDependencyInfo;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param prop Properties. (NotNull)
     */
    public DfBasicProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Basic Info Map
    //                                                                      ==============
    public static final String KEY_basicInfoMap = "basicInfoMap";
    protected Map<String, Object> _basicInfoMap;

    public Map<String, Object> getBasicInfoMap() {
        if (_basicInfoMap == null) {
            _basicInfoMap = mapProp("torque." + KEY_basicInfoMap, DEFAULT_EMPTY_MAP);
        }
        return _basicInfoMap;
    }

    public String getProperty(String key, String defaultValue) {
        Map<String, Object> map = getBasicInfoMap();
        Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be string:";
                msg = msg + " " + obj.getClass().getSimpleName() + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value;
            } else {
                return defaultValue;
            }
        }
        return stringProp("torque." + key, defaultValue);
    }

    public boolean isProperty(String key, boolean defaultValue) {
        Map<String, Object> map = getBasicInfoMap();
        Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be boolean:";
                msg = msg + " " + obj.getClass().getSimpleName() + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value.trim().equalsIgnoreCase("true");
            } else {
                return defaultValue;
            }
        }
        return booleanProp("torque." + key, defaultValue);
    }

    // ===================================================================================
    //                                                                             Project
    //                                                                             =======
    public String getProjectName() {
        return stringProp("torque.project", "");
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    public String getDatabaseName() {
        return getProperty("database", "");
    }

    public boolean isDatabasePostgreSQL() {
        return getDatabaseName().equalsIgnoreCase("postgresql");
    }

    public boolean isDatabaseMySQL() {
        return getDatabaseName().equalsIgnoreCase("mysql");
    }

    public boolean isDatabaseOracle() {
        return getDatabaseName().equalsIgnoreCase("oracle");
    }

    public boolean isDatabaseDerby() {
        return getDatabaseName().equalsIgnoreCase("derby");
    }

    public boolean isDatabaseDB2() {
        return getDatabaseName().equalsIgnoreCase("db2");
    }

    public boolean isDatabaseSqlServer() {
        return getDatabaseName().equalsIgnoreCase("mssql");
    }

    public boolean isDatabaseMsAccess() {
        return getDatabaseName().equalsIgnoreCase("msaccess");
    }

    // ===================================================================================
    //                                                                            Language
    //                                                                            ========
    public String getTargetLanguage() {
        return getProperty("targetLanguage", DEFAULT_targetLanguage);
    }

    public String getResourceDirectory() {
        final String targetLanguage = getTargetLanguage();
        if (isTargetLanguageJava() && getTargetLanguageVersion().startsWith("1.4")) {
            return targetLanguage + "j14";
        }
        return targetLanguage;
    }

    public boolean isTargetLanguageMain() {
        return getBasicProperties().isTargetLanguageJava() || getBasicProperties().isTargetLanguageCSharp();
    }

    public boolean isTargetLanguageJava() {
        return JAVA_targetLanguage.equals(getTargetLanguage());
    }

    public boolean isTargetLanguageCSharp() {
        return CSHARP_targetLanguage.equals(getTargetLanguage());
    }

    public boolean isTargetLanguagePhp() {
        return PHP_targetLanguage.equals(getTargetLanguage());
    }

    public DfLanguageDependencyInfo getLanguageDependencyInfo() {
        if (_languageDependencyInfo == null) {
            if (isTargetLanguageJava()) {
                _languageDependencyInfo = new DfLanguageDependencyInfoJava();
            } else if (isTargetLanguageCSharp()) {
                _languageDependencyInfo = new DfLanguageDependencyInfoCSharp();
            } else if (isTargetLanguagePhp()) {
                _languageDependencyInfo = new DfLanguageDependencyInfoPhp();
            } else {
                String msg = "The language is supported: " + getTargetLanguage();
                throw new IllegalStateException(msg);
            }
        }
        return _languageDependencyInfo;
    }

    public String getTargetLanguageVersion() {
        return getProperty("targetLanguageVersion", "5.0");
    }

    public boolean isJavaVersionGreaterEqualTiger() {
        final String targetLanguageVersion = getBasicProperties().getTargetLanguageVersion();
        return isTargetLanguageJava() && targetLanguageVersion.compareToIgnoreCase("5.0") >= 0;
    }

    public boolean isJavaVersionGreaterEqualMustang() {
        final String targetLanguageVersion = getBasicProperties().getTargetLanguageVersion();
        return isTargetLanguageJava() && targetLanguageVersion.compareToIgnoreCase("6.0") >= 0;
    }

    // ===================================================================================
    //                                                                           Container
    //                                                                           =========
    public String getTargetContainerName() {
        String containerName = getProperty("targetContainer", "seasar");
        checkContainer(containerName);
        return containerName;
    }

    public boolean isTargetContainerSeasar() {
        return getTargetContainerName().trim().equalsIgnoreCase("seasar");
    }

    public boolean isTargetContainerSpring() {
        return getTargetContainerName().trim().equalsIgnoreCase("spring");
    }

    protected void checkContainer(String containerName) {
        if (!containerName.equalsIgnoreCase("seasar") && !containerName.equalsIgnoreCase("spring")) {
            String msg = "The targetContainer should be 'seasar' or 'spring': targetContainer=" + containerName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                            Encoding
    //                                                                            ========
    public String getTemplateFileEncoding() { // It's closet!
        return getProperty("templateFileEncoding", DEFAULT_templateFileEncoding);
    }

    public String getSourceFileEncoding() { // It's closet!
        return getProperty("sourceFileEncoding", DEFAULT_sourceFileEncoding);
    }

    public String getProejctSchemaXMLEncoding() { // It's closet!
        return getProperty("projectSchemaXMLEncoding", DEFAULT_projectSchemaXMLEncoding);
    }

    // ===================================================================================
    //                                                                           Extension
    //                                                                           =========
    public String getTemplateFileExtension() { // It's not property!
        return getLanguageDependencyInfo().getTemplateFileExtension();
    }

    public String getClassFileExtension() { // It's not property!
        return getLanguageDependencyInfo().getGrammarInfo().getClassFileExtension();
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public String getOutputDirectory() {
        final String defaultSourceDirectory = getLanguageDependencyInfo().getDefaultSourceDirectory();
        return getProperty("java.dir", defaultSourceDirectory);
    }

    // ===================================================================================
    //                                                                    Generate Package
    //                                                                    ================
    public String getPackageBase() {
        return getProperty("packageBase", "");
    }

    public String getBaseCommonPackage() {
        return filterBase(getProperty("baseCommonPackage", getPackageInfo().getBaseCommonPackage()));
    }

    public String getBaseBehaviorPackage() {
        return filterBase(getProperty("baseBehaviorPackage", getPackageInfo().getBaseBehaviorPackage()));
    }

    public String getBaseDaoPackage() {
        return filterBase(getProperty("baseDaoPackage", getPackageInfo().getBaseDaoPackage()));
    }

    public String getBaseEntityPackage() {
        return filterBase(getProperty("baseEntityPackage", getPackageInfo().getBaseEntityPackage()));
    }

    public String getDBMetaPackage() {
        return getBaseEntityPackage() + "." + getPackageInfo().getDBMetaSimplePackageName();
    }

    public String getConditionBeanPackage() {
        return filterBase(getProperty("conditionBeanPackage", getPackageInfo().getConditionBeanPackage()));
    }

    public String getExtendedConditionBeanPackage() {
        String pkg = getProperty("extendedConditionBeanPackage", null);
        if (pkg != null) {
            return filterBase(pkg);
        }
        return getConditionBeanPackage();
    }

    protected boolean hasConditionBeanPackage() {
        return getProperty("conditionBeanPackage", null) != null;
    }

    public String getExtendedBehaviorPackage() {
        return filterBase(getProperty("extendedBehaviorPackage", getPackageInfo().getExtendedBehaviorPackage()));
    }

    public String getExtendedDaoPackage() {
        return filterBase(getProperty("extendedDaoPackage", getPackageInfo().getExtendedDaoPackage()));
    }

    public String getExtendedEntityPackage() {
        return filterBase(getProperty("extendedEntityPackage", getPackageInfo().getExtendedEntityPackage()));
    }

    protected String filterBase(String packageString) {
        if (getPackageBase().trim().length() > 0) {
            return getPackageBase() + "." + packageString;
        } else {
            return packageString;
        }
    }

    protected DfGeneratedClassPackageDefault getPackageInfo() {
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return languageDependencyInfo.getGeneratedClassPackageInfo();
    }

    // ===================================================================================
    //                                                                        Class Author
    //                                                                        ============
    public String getClassAuthor() {
        return getProperty("classAuthor", "DBFlute(AutoGenerator)");
    }

    // ===================================================================================
    //                                                                              Naming
    //                                                                              ======
    public boolean isJavaNameOfTableSameAsDbName() {
        return isProperty("isJavaNameOfTableSameAsDbName", false);
    }

    public boolean isJavaNameOfColumnSameAsDbName() {
        return isProperty("isJavaNameOfColumnSameAsDbName", false);
    }

    // ===================================================================================
    //                                                                              Prefix
    //                                                                              ======
    public String getProjectPrefix() {
        return getProperty("projectPrefix", "");
    }

    public String getBasePrefix() {
        return "Bs";
    }

    public boolean isAppendProjectSuffixToComponentName() { // It's closet!
        return isProperty("isAppendProjectSuffixToComponentName", true);
    }

    // ===================================================================================
    //                                                                           HotDeploy
    //                                                                           =========
    public boolean isAvailableHotDeploy() { // It's closet! And seasar only!
        return isProperty("isAvailableHotDeploy", false);
    }

    // ===================================================================================
    //                                                                 Behavior Query Path
    //                                                                 ===================
    public String getBehaviorQueryPathBeginMark() { // It's not property!
        return "/*df:BehaviorQueryPathBegin*/";
    }

    public String getBehaviorQueryPathEndMark() { // It's not property!
        return "/*df:BehaviorQueryPathEnd*/";
    }

    // ===================================================================================
    //                                                                      Generic Helper
    //                                                                      ==============
    // It's not property!
    public String filterGenericsString(String genericsString) {
        return "<" + genericsString + ">";
    }

    public String filterGenericsDowncast(String genericsDowncast) {
        return "(" + genericsDowncast + ")";
    }

    public String filterGenericsParamOutput(String variableName, String description) {
        return filterGenericsGeneralOutput("@param " + variableName + " " + description);
    }

    public String filterGenericsGeneralOutput(String genericsGeneralOutput) {
        return genericsGeneralOutput;
    }

    public String filterGenericsGeneralOutputAfterNewLineOutput(String genericsGeneralOutput) {
        return getLineSeparator() + filterGenericsGeneralOutput(genericsGeneralOutput);
    }

    public String outputOverrideAnnotation() {
        return filterGenericsGeneralOutput("@Override()");
    }

    public String outputOverrideAnnotationAfterNewLineOutput() {
        return filterGenericsGeneralOutputAfterNewLineOutput("    @Override()");
    }

    public String outputSuppressWarningsAfterLineSeparator() {
        return filterGenericsGeneralOutputAfterNewLineOutput("@SuppressWarnings(\"unchecked\")");
    }

    protected String getLineSeparator() {
        // return System.getProperty("line.separator");
        return "\n";// For to resolve environment dependency!
    }
}