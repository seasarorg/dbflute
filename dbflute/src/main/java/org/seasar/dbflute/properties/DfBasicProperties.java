package org.seasar.dbflute.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.config.DfDatabaseNameMapping;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
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
        return getPropertyIfNotBuildProp(key, defaultValue, getBasicInfoMap());
    }

    public boolean isProperty(String key, boolean defaultValue) {
        return isProperty(key, defaultValue, getBasicInfoMap());
    }

    public void checkBasicInfo() {
        final String databaseName = getDatabaseType();
        if (databaseName == null || databaseName.trim().length() == 0) {
            String msg = "Not found the property 'database' in basicInfoMap.dfprop: " + databaseName;
            throw new DfRequiredPropertyNotFoundException(msg);
        }
    }

    // ===================================================================================
    //                                                                             Project
    //                                                                             =======
    public String getProjectName() {
        return stringProp("torque.project", ""); // from build-properties!
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    public String getDatabaseType() {
        final String databaseType = getProperty("database", null);
        if (databaseType == null || databaseType.trim().length() == 0) {
            String msg = "Not found the property 'database' in basicInfoMap.dfprop: " + databaseType;
            throw new DfRequiredPropertyNotFoundException(msg);
        }
        return databaseType;
    }

    protected DBDef _currentDBDef;

    public DBDef getCurrentDBDef() {
        if (_currentDBDef != null) {
            return _currentDBDef;
        }
        final DfDatabaseNameMapping databaseNameMapping = DfDatabaseNameMapping.getInstance();
        _currentDBDef = databaseNameMapping.findDBDef(getDatabaseType());
        return _currentDBDef;
    }

    public boolean isDatabaseMySQL() {
        return getDatabaseType().equalsIgnoreCase("mysql");
    }

    public boolean isDatabasePostgreSQL() {
        return getDatabaseType().equalsIgnoreCase("postgresql");
    }

    public boolean isDatabaseOracle() {
        return getDatabaseType().equalsIgnoreCase("oracle");
    }

    public boolean isDatabaseDB2() {
        return getDatabaseType().equalsIgnoreCase("db2");
    }

    public boolean isDatabaseSQLServer() {
        return getDatabaseType().equalsIgnoreCase("mssql");
    }

    public boolean isDatabaseH2() {
        return getDatabaseType().equalsIgnoreCase("h2");
    }

    public boolean isDatabaseDerby() {
        return getDatabaseType().equalsIgnoreCase("derby");
    }

    public boolean isDatabaseSQLite() { // sub supported
        return getDatabaseType().equalsIgnoreCase("sqlite");
    }

    public boolean isDatabaseMSAccess() { // sub supported
        return getDatabaseType().equalsIgnoreCase("msaccess");
    }

    public boolean isDatabaseFirebird() { // a-little-bit supported
        return getDatabaseType().equalsIgnoreCase("firebird");
    }

    public boolean isDatabaseSybase() { // a-little-bit supported
        return getDatabaseType().equalsIgnoreCase("sybase");
    }

    public boolean isDatabase_Supported() {
        if (isDatabaseAsMainSupported() || isDatabaseAsSubSupported()) {
            return true;
        }
        return false;
    }

    public boolean isDatabaseAsMainSupported() {
        if (isDatabaseMySQL() || isDatabasePostgreSQL() || isDatabaseOracle() || isDatabaseDB2()
                || isDatabaseSQLServer() || isDatabaseH2() || isDatabaseDerby()) {
            return true;
        }
        return false;
    }

    public boolean isDatabaseAsSubSupported() {
        if (isDatabaseSQLite() || isDatabaseMSAccess()) {
            return true;
        }
        return false;
    }

    // /- - - - - - - - - - - - - - - - - - - -
    // simple DBMS definition about generating
    // - - - - - - - - - -/
    public boolean isDatabaseAsSchemaSpecificationOmittable() {
        return isDatabaseMySQL() || isDatabaseAsUnifiedSchemaUnsupported();
    }

    public boolean isDatabaseAsUnifiedSchemaUnsupported() {
        return isDatabaseSQLite() || isDatabaseMSAccess();
    }

    public boolean isDatabaseAsPrimaryKeyExtractingUnsupported() {
        return isDatabaseMSAccess();
    }

    public boolean isDatabaseAsForeignKeyExtractingUnsupported() {
        return isDatabaseMSAccess();
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

    public boolean isTargetContainerLucy() {
        return getTargetContainerName().trim().equalsIgnoreCase("lucy");
    }

    public boolean isTargetContainerGuice() {
        return getTargetContainerName().trim().equalsIgnoreCase("guice");
    }

    public boolean isTargetContainerSlim3() {
        return getTargetContainerName().trim().equalsIgnoreCase("slim3");
    }

    protected void checkContainer(String containerName) {
        containerName = containerName.toLowerCase();
        if (!containerName.equals("seasar") && !containerName.equals("spring") && !containerName.equals("lucy")
                && !containerName.equals("guice") && !containerName.equals("slim3")) {
            String msg = "The targetContainer should be 'seasar' or 'spring' or 'lucy' or 'guice' or 'slim3':";
            msg = msg + " targetContainer=" + containerName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                           SchemaXML
    //                                                                           =========
    public String getProejctSchemaXMLFilePath() {
        final StringBuilder sb = new StringBuilder();
        final String projectName = getBasicProperties().getProjectName();
        sb.append("./schema/project-schema-").append(projectName).append(".xml"); // fixed
        return sb.toString();
    }

    public String getProejctSchemaXMLEncoding() { // It's closet!
        return getProperty("projectSchemaXMLEncoding", DEFAULT_projectSchemaXMLEncoding);
    }

    public String getProjectSchemaHistoryFilePath() {
        final StringBuilder sb = new StringBuilder();
        final String projectName = getBasicProperties().getProjectName();
        sb.append("./schema/project-history-").append(projectName).append(".diffmap"); // fixed
        return sb.toString();
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
    //                                                                    Generate Package
    //                                                                    ================
    public String getPackageBase() {
        return getProperty("packageBase", "");
    }

    public String getBaseCommonPackage() {
        final String key = "baseCommonPackage";
        final String baseCommonPackage = getProperty(key, getPackageInfo().getBaseCommonPackage());
        if (isApplicationBehaviorProject()) {
            return getLibraryAllCommonPackage(); // basically for Sql2Entity task at BhvAp mode
        } else {
            return filterBase(baseCommonPackage);
        }
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
        return filterBase(packageString, getPackageBase());
    }

    protected String filterBase(String packageString, String packageBase) {
        if (packageBase.trim().length() > 0) {
            return packageBase + "." + packageString;
        } else {
            return packageString;
        }
    }

    protected DfGeneratedClassPackageDefault getPackageInfo() {
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return languageDependencyInfo.getGeneratedClassPackageInfo();
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public String getGenerateOutputDirectory() {
        final String property = getProperty("generateOutputDirectory", null);
        if (property != null) {
            return property;
        }
        final String defaultDirectory = getLanguageDependencyInfo().getDefaultGenerateOutputDirectory();
        return getProperty("java.dir", defaultDirectory); // old style or default
    }

    public String getResourceOutputDirectory() {
        return getProperty("resourceOutputDirectory", null);
    }

    public String getDefaultResourceOutputDirectory() {
        return getLanguageDependencyInfo().getDefaultResourceOutputDirectory();
    }

    // ===================================================================================
    //                                                                              Naming
    //                                                                              ======
    public boolean isTableNameCamelCase() {
        final boolean defaultProperty = false;
        final boolean property = isProperty("isTableNameCamelCase", defaultProperty);
        if (property) {
            return true;
        }
        return isProperty("isJavaNameOfTableSameAsDbName", defaultProperty); // old style or default
    }

    public boolean isColumnNameCamelCase() {
        final boolean defaultProperty = false;
        final boolean property = isProperty("isColumnNameCamelCase", defaultProperty);
        if (property) {
            return true;
        }
        return isProperty("isJavaNameOfColumnSameAsDbName", defaultProperty); // old style or default
    }

    // ===================================================================================
    //                                                                              Prefix
    //                                                                              ======
    public String getProjectPrefix() {
        return getProperty("projectPrefix", "");
    }

    public String getBasePrefix() { // non property
        return "Bs";
    }

    // ===================================================================================
    //                                                                        Class Author
    //                                                                        ============
    public String getClassAuthor() {
        return getProperty("classAuthor", "DBFlute(AutoGenerator)");
    }

    // ===================================================================================
    //                                                                Application Behavior
    //                                                                ====================
    protected Map<String, String> _applicationBehaviorMap;

    public Map<String, String> getApplicationBehaviorMap() {
        if (_applicationBehaviorMap != null) {
            return _applicationBehaviorMap;
        }
        final Object obj = getBasicInfoMap().get("applicationBehaviorMap");
        if (obj != null && !(obj instanceof Map<?, ?>)) {
            String msg = "The type of the property 'applicationBehaviorMap' should be Map: " + obj;
            throw new DfIllegalPropertyTypeException(msg);
        }
        if (obj == null) {
            _applicationBehaviorMap = new HashMap<String, String>();
        } else {
            @SuppressWarnings("unchecked")
            final Map<String, String> map = (Map<String, String>) obj;
            _applicationBehaviorMap = map;
        }
        return _applicationBehaviorMap;
    }

    public boolean isApplicationBehaviorProject() {
        return isProperty("isApplicationBehaviorProject", false, getApplicationBehaviorMap());
    }

    public String getLibraryProjectPackageBase() {
        final String defaultBase = getPackageBase();
        final Map<String, String> map = getApplicationBehaviorMap();
        return getProperty("libraryProjectPackageBase", defaultBase, map);
    }

    public String getLibraryAllCommonPackage() {
        final String packageBase = getLibraryProjectPackageBase();
        final String allcommonSimplePackage = getPackageInfo().getBaseCommonPackage();
        return filterBase(allcommonSimplePackage, packageBase);
    }

    public String getLibraryBehaviorPackage() {
        final String packageBase = getLibraryProjectPackageBase();
        final String exbhvSimplePackage = getPackageInfo().getExtendedBehaviorPackage();
        return filterBase(exbhvSimplePackage, packageBase);
    }

    public String getApplicationAllCommonPackage() {
        return filterBase(getPackageInfo().getBaseCommonPackage());
    }

    public String getLibraryProjectPrefix() {
        return getProjectPrefix();
        // *conldn't achieve
        //final Map<String, String> map = getApplicationBehaviorMap();
        //return getProperty("libraryProjectPrefix", "", map);

        // #   o libraryProjectPrefix: (NotRequired - Default '' (means library has no prefix))
        // #    If application project prefix is different from library's one,
        // #    set the property a value 'library's one'.
        // #    If a prefix is valid and both have a same prefix, you need to set this.
    }

    public String getApplicationBehaviorAdditionalSuffix() { // It's closet!
        return getProperty("applicationBehaviorAdditionalSuffix", "Ap", getApplicationBehaviorMap());
    }

    // ===================================================================================
    //                                                                   Source & Template
    //                                                                   =================
    public String getSourceFileEncoding() {
        return getProperty("sourceFileEncoding", DEFAULT_sourceFileEncoding);
    }

    public String getTemplateFileEncoding() { // It's closet!
        return getProperty("templateFileEncoding", DEFAULT_templateFileEncoding);
    }

    public String getSourceCodeLineSeparator() {
        return "\r\n"; // Source Code uses CR + LF. (since 0.9.5.4)
    }

    // ===================================================================================
    //                                                         Flat/Omit Directory Package
    //                                                         ===========================
    protected Map<String, String> _outputPackageAdjustmentMap;

    public Map<String, String> getOutputPackageAdjustmentMap() {
        if (_outputPackageAdjustmentMap != null) {
            return _outputPackageAdjustmentMap;
        }
        final Object obj = getBasicInfoMap().get("outputPackageAdjustmentMap");
        if (obj != null && !(obj instanceof Map<?, ?>)) {
            String msg = "The type of the property 'outputPackageAdjustmentMap' should be Map: " + obj;
            throw new DfIllegalPropertyTypeException(msg);
        }
        if (obj == null) {
            _outputPackageAdjustmentMap = new HashMap<String, String>();
        } else {
            @SuppressWarnings("unchecked")
            final Map<String, String> map = (Map<String, String>) obj;
            _outputPackageAdjustmentMap = map;
        }
        return _outputPackageAdjustmentMap;
    }

    // CSharp Only
    public boolean isFlatDirectoryPackageValid() {
        final String str = getFlatDirectoryPackage();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    /**
     * Get the package for flat directory. Normally, this property is only for C#.
     * @return The package for flat directory. (NullAllowed)
     */
    public String getFlatDirectoryPackage() {
        final String key = "flatDirectoryPackage";
        final String defaultProp = getProperty(key, null); // for compatibility
        return getProperty(key, defaultProp, getOutputPackageAdjustmentMap());
    }

    // CSharp Only
    public boolean isOmitDirectoryPackageValid() {
        final String str = getOmitDirectoryPackage();
        return str != null && str.trim().length() > 0 && !str.trim().equals("null");
    }

    /**
     * Get the package for omit directory. Normally, this property is only for C#.
     * @return The package for omit directory. (NullAllowed)
     */
    public String getOmitDirectoryPackage() {
        final String key = "omitDirectoryPackage";
        final String defaultProp = getProperty(key, null); // for compatibility
        return getProperty(key, defaultProp, getOutputPackageAdjustmentMap());
    }

    public void checkDirectoryPackage() {
        final String flatDirectoryPackage = getFlatDirectoryPackage();
        final String omitDirectoryPackage = getOmitDirectoryPackage();
        if (flatDirectoryPackage == null && omitDirectoryPackage == null) {
            return;
        }
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        if (!languageDependencyInfo.isFlatOrOmitDirectorySupported()) {
            String msg = "The language does not support flatDirectoryPackage or omitDirectoryPackage:";
            msg = msg + " language=" + getBasicProperties().getTargetLanguage();
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                           HotDeploy
    //                                                                           =========
    public boolean isAvailableHotDeploy() { // It's closet! And the Seasar only!
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