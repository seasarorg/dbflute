package org.seasar.dbflute.properties;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.7.5 (2008/06/25 Wednesday)
 */
public final class DfOutsideSqlProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfOutsideSqlProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                             outsideSqlDefinitionMap
    //                                                             =======================
    public static final String KEY_outsideSqlDefinitionMap = "outsideSqlDefinitionMap";
    protected Map<String, Object> _outsideSqlDefinitionMap;

    protected Map<String, Object> getOutsideSqlDefinitionMap() {
        if (_outsideSqlDefinitionMap == null) {
            _outsideSqlDefinitionMap = mapProp("torque." + KEY_outsideSqlDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _outsideSqlDefinitionMap;
    }

    public boolean isProperty(String key, boolean defaultValue) {
        return isProperty(key, defaultValue, getOutsideSqlDefinitionMap());
    }

    // ===================================================================================
    //                                                             Procedure ParameterBean
    //                                                             =======================
    public boolean isGenerateProcedureParameterBean() {
        return isProperty("isGenerateProcedureParameterBean", false);
    }

    public boolean isGenerateProcedureCustomizeEntity() {
        return isProperty("isGenerateProcedureCustomizeEntity", false);
    }

    protected List<String> _targetProcedureCatalogList;

    protected List<String> getTargetProcedureCatalogList() {
        if (_targetProcedureCatalogList != null) {
            return _targetProcedureCatalogList;
        }
        _targetProcedureCatalogList = getOutsideSqlPropertyAsList("targetProcedureCatalogList");
        if (_targetProcedureCatalogList == null) {
            _targetProcedureCatalogList = DfCollectionUtil.emptyList();
        }
        return _targetProcedureCatalogList;
    }

    public boolean isTargetProcedureCatalog(String procedureCatalog) {
        final List<String> targetProcedureList = getTargetProcedureCatalogList();
        if (targetProcedureList == null || targetProcedureList.isEmpty()) {
            return true;
        }
        if (procedureCatalog == null || procedureCatalog.trim().length() == 0) {
            if (targetProcedureList.contains("$$DEFAULT$$")) {
                return true;
            } else {
                return false;
            }
        }
        for (String catalogHint : targetProcedureList) {
            if (isHitByTheHint(procedureCatalog, catalogHint)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> _targetProcedureSchemaList;

    protected List<String> getTargetProcedureSchemaList() {
        if (_targetProcedureSchemaList != null) {
            return _targetProcedureSchemaList;
        }
        _targetProcedureSchemaList = getOutsideSqlPropertyAsList("targetProcedureSchemaList");
        if (_targetProcedureSchemaList == null) {
            _targetProcedureSchemaList = DfCollectionUtil.emptyList();
        }
        return _targetProcedureSchemaList;
    }

    public boolean isTargetProcedureSchema(String procedureSchema) {
        final List<String> targetProcedureList = getTargetProcedureSchemaList();
        if (targetProcedureList == null || targetProcedureList.isEmpty()) {
            return true;
        }
        if (procedureSchema == null || procedureSchema.trim().length() == 0) {
            if (targetProcedureList.contains("$$DEFAULT$$")) {
                return true;
            } else {
                return false;
            }
        }
        for (String schemaHint : targetProcedureList) {
            if (isHitByTheHint(procedureSchema, schemaHint)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> _targetProcedureNameList;

    protected List<String> getTargetProcedureNameList() {
        if (_targetProcedureNameList != null) {
            return _targetProcedureNameList;
        }
        _targetProcedureNameList = getOutsideSqlPropertyAsList("targetProcedureNameList");
        if (_targetProcedureNameList == null) {
            _targetProcedureNameList = DfCollectionUtil.emptyList();
        }
        return _targetProcedureNameList;
    }

    public boolean isTargetProcedureName(String procedureName) {
        final List<String> targetProcedureList = getTargetProcedureNameList();
        if (targetProcedureList == null || targetProcedureList.isEmpty()) {
            return true;
        }
        for (String procedureNameHint : targetProcedureList) {
            if (isHitByTheHint(procedureName, procedureNameHint)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> _executionMetaProcedureNameList;

    protected List<String> getExecutionMetaProcedureNameList() {
        if (_executionMetaProcedureNameList != null) {
            return _executionMetaProcedureNameList;
        }
        _executionMetaProcedureNameList = getOutsideSqlPropertyAsList("executionMetaProcedureNameList");
        if (_executionMetaProcedureNameList == null) {
            _executionMetaProcedureNameList = DfCollectionUtil.emptyList();
        }
        return _executionMetaProcedureNameList;
    }

    public boolean hasSpecifiedExecutionMetaProcedure() {
        return !getExecutionMetaProcedureNameList().isEmpty();
    }

    public boolean isExecutionMetaProcedureName(String procedureName) {
        final List<String> executionMetaProcedureList = getExecutionMetaProcedureNameList();
        if (executionMetaProcedureList == null || executionMetaProcedureList.isEmpty()) {
            return true;
        }
        for (String procedureNameHint : executionMetaProcedureList) {
            if (isHitByTheHint(procedureName, procedureNameHint)) {
                return true;
            }
        }
        return false;
    }

    public ProcedureSynonymHandlingType getProcedureSynonymHandlingType() {
        final String key = "procedureSynonymHandlingType";
        final String property = getProperty(key, ProcedureSynonymHandlingType.NONE.name(), getOutsideSqlDefinitionMap());
        if (property.equalsIgnoreCase(ProcedureSynonymHandlingType.NONE.name())) {
            return ProcedureSynonymHandlingType.NONE;
        } else if (property.equalsIgnoreCase(ProcedureSynonymHandlingType.INCLUDE.name())) {
            return ProcedureSynonymHandlingType.INCLUDE;
        } else if (property.equalsIgnoreCase(ProcedureSynonymHandlingType.SWITCH.name())) {
            return ProcedureSynonymHandlingType.SWITCH;
        } else {
            String msg = "The property was out of scope for " + key + ": value=" + property;
            throw new DfIllegalPropertyTypeException(msg);
        }
    }

    public static enum ProcedureSynonymHandlingType {
        NONE, INCLUDE, SWITCH
    }

    // ===================================================================================
    //                                                                      OutsideSqlTest
    //                                                                      ==============
    public boolean isRequiredSqlTitle() {
        return isProperty("isRequiredSqlTitle", false);
    }

    public boolean isRequiredSqlDescription() {
        return isProperty("isRequiredSqlDescription", false);
    }

    public boolean isSuppressParameterCommentCheck() { // It's closet!
        return isProperty("isSuppressParameterCommentCheck", false);
    }

    // ===================================================================================
    //                                                                     SqlFileEncoding
    //                                                                     ===============
    public boolean hasSqlFileEncoding() {
        final String encoding = getSqlFileEncoding();
        return encoding != null && encoding.trim().length() > 0 && !encoding.trim().equalsIgnoreCase("null");
    }

    public String getSqlFileEncoding() {
        final String value = (String) getOutsideSqlDefinitionMap().get("sqlFileEncoding");
        if (value != null && value.trim().length() > 0 && !value.trim().equalsIgnoreCase("null")) {
            return value;
        }
        return "UTF-8";
    }

    // ===================================================================================
    //                                                                        SqlDirectory
    //                                                                        ============
    public String getSqlDirectory() {
        String sqlDirectory = (String) getOutsideSqlDefinitionMap().get("sqlDirectory");
        if (sqlDirectory == null || sqlDirectory.trim().length() == 0) {
            sqlDirectory = getDefaultSqlDirectory();
        }
        sqlDirectory = removeEndSeparatorIfNeeds(sqlDirectory);
        String sqlPackage = getSqlPackage();
        if (sqlPackage != null && sqlPackage.trim().length() > 0) {
            String sqlPackageDirectory = resolveSqlPackageFileSeparator(sqlPackage);
            sqlDirectory = sqlDirectory + "/" + removeStartSeparatorIfNeeds(sqlPackageDirectory);
        }
        return sqlDirectory;
    }

    // -----------------------------------------------------
    //                                      Remove Separator
    //                                      ----------------
    protected String removeStartSeparatorIfNeeds(String path) {
        if (path.startsWith("/")) {
            return path.substring("/".length());
        }
        return path;
    }

    protected String removeEndSeparatorIfNeeds(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    // -----------------------------------------------------
    //                                  Default SqlDirectory
    //                                  --------------------
    /**
     * @return The default directory of SQL. (NotNull)
     */
    protected String getDefaultSqlDirectory() {
        return getBasicProperties().getGenerateOutputDirectory();
    }

    // -----------------------------------------------------
    //                               Resolve SqlPackage Path
    //                               -----------------------
    protected String resolveSqlPackageFileSeparator(String sqlPackage) {
        final DfBasicProperties prop = getBasicProperties();
        if (!prop.isFlatDirectoryPackageValid()) {
            return replaceDotToSeparator(sqlPackage);
        }
        final String flatDirectoryPackage = prop.getFlatDirectoryPackage();
        if (!sqlPackage.contains(flatDirectoryPackage)) {
            return replaceDotToSeparator(sqlPackage);
        }
        return resolveSqlPackageFileSeparatorWithFlatDirectory(sqlPackage, flatDirectoryPackage);
    }

    protected String resolveSqlPackageFileSeparatorWithFlatDirectory(String sqlPackage, String flatDirectoryPackage) {
        final int startIndex = sqlPackage.indexOf(flatDirectoryPackage);
        String front = sqlPackage.substring(0, startIndex);
        String rear = sqlPackage.substring(startIndex + flatDirectoryPackage.length());
        front = replaceDotToSeparator(front);
        rear = replaceDotToSeparator(rear);
        return front + flatDirectoryPackage + rear;
    }

    protected String replaceDotToSeparator(String sqlPackage) {
        return DfStringUtil.replace(sqlPackage, ".", "/");
    }

    // ===================================================================================
    //                                                                          SqlPackage
    //                                                                          ==========
    public boolean isSqlPackageValid() {
        final String sqlPackage = getSqlPackage();
        return sqlPackage != null && sqlPackage.trim().length() > 0 && !sqlPackage.trim().equalsIgnoreCase("null");
    }

    public String getSqlPackage() {
        String sqlPackage = (String) getOutsideSqlDefinitionMap().get("sqlPackage");
        if (sqlPackage == null || sqlPackage.trim().length() == 0) {
            sqlPackage = getDefaultSqlPackage();
        }
        return resolvePackageBaseMarkIfNeeds(sqlPackage);
    }

    protected String getDefaultSqlPackage() {
        return "";
    }

    protected String resolvePackageBaseMarkIfNeeds(String sqlPackage) {
        String packageBase = getBasicProperties().getPackageBase();
        return DfStringUtil.replace(sqlPackage, "$$PACKAGE_BASE$$", packageBase);
    }

    // ===================================================================================
    //                                                                     OutputDirectory
    //                                                                     ===============
    public String getSql2EntityOutputDirectory() {
        final String defaultDir = getBasicProperties().getGenerateOutputDirectory();
        final String value = (String) getOutsideSqlDefinitionMap().get("sql2EntityOutputDirectory");
        return value != null && value.trim().length() > 0 ? value : defaultDir;
    }

    // ===================================================================================
    //                                                                      Package Detail
    //                                                                      ==============
    protected String getSpecifiedBaseCustomizeEntityPackage() { // It's closet!
        final String value = (String) getOutsideSqlDefinitionMap().get("baseCustomizeEntityPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    protected String getSpecifiedExtendedCustomizeEntityPackage() { // It's closet!
        final String value = (String) getOutsideSqlDefinitionMap().get("extendedCustomizeEntityPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    protected String getSpecifiedBaseParameterBeanPackage() { // It's closet!
        final String value = (String) getOutsideSqlDefinitionMap().get("baseParameterBeanPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    protected String getSpecifiedExtendedParameterBeanPackage() { // It's closet!
        final String value = (String) getOutsideSqlDefinitionMap().get("extendedParameterBeanPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    public String getBaseEntityPackage() {
        String specifiedPackage = getSpecifiedBaseCustomizeEntityPackage();
        if (specifiedPackage != null && specifiedPackage.trim().length() != 0) {
            return specifiedPackage;
        }
        String defaultPackage = getBasicProperties().getBaseEntityPackage() + "." + getCustomizePackageName();
        if (defaultPackage != null && defaultPackage.trim().length() != 0) {
            return defaultPackage;
        } else {
            String msg = "Both packageString in sql2entity-property and baseEntityPackage are null.";
            throw new IllegalStateException(msg);
        }
    }

    public String getDBMetaPackage() {
        String dbmetaPackage = "dbmeta";
        if (getBasicProperties().isTargetLanguageCSharp()) {
            dbmetaPackage = "Dbm";
        }
        return getBaseEntityPackage() + "." + dbmetaPackage;
    }

    public String getExtendedEntityPackage() {
        String specifiedPackage = getSpecifiedExtendedCustomizeEntityPackage();
        if (specifiedPackage != null && specifiedPackage.trim().length() != 0) {
            return specifiedPackage;
        }
        String defaultPackage = getBasicProperties().getExtendedEntityPackage() + "." + getCustomizePackageName();
        if (defaultPackage != null && defaultPackage.trim().length() != 0) {
            return defaultPackage;
        } else {
            String msg = "Both packageString in sql2entity-property and extendedEntityPackage are null.";
            throw new IllegalStateException(msg);
        }
    }

    protected String getCustomizePackageName() {
        String customizePackage = "customize";
        if (getBasicProperties().isTargetLanguageCSharp()) {
            customizePackage = "Customize";
        }
        return customizePackage;
    }

    public String getBaseCursorPackage() {
        if (isMakeDaoInterface()) {
            return getBasicProperties().getBaseDaoPackage() + "." + getCursorPackageName();
        } else {
            return getBasicProperties().getBaseBehaviorPackage() + "." + getCursorPackageName();
        }
    }

    public String getExtendedCursorPackage() {
        if (isMakeDaoInterface()) {
            return getBasicProperties().getExtendedDaoPackage() + "." + getCursorPackageName();
        } else {
            return getBasicProperties().getExtendedBehaviorPackage() + "." + getCursorPackageName();
        }
    }

    protected String getCursorPackageName() {
        String pmbeanPackage = "cursor";
        if (getBasicProperties().isTargetLanguageCSharp()) {
            pmbeanPackage = "Cursor";
        }
        return pmbeanPackage;
    }

    public String getBaseParameterBeanPackage() {
        String specifiedPackage = getSpecifiedBaseParameterBeanPackage();
        if (specifiedPackage != null && specifiedPackage.trim().length() != 0) {
            return specifiedPackage;
        }

        final String defaultPackage;
        if (isMakeDaoInterface()) {
            defaultPackage = getBasicProperties().getBaseDaoPackage() + "." + getPmbeanPackageName();
        } else {
            defaultPackage = getBasicProperties().getBaseBehaviorPackage() + "." + getPmbeanPackageName();
        }
        if (defaultPackage != null && defaultPackage.trim().length() != 0) {
            return defaultPackage;
        } else {
            String msg = "Both packageString in sql2entity-property and baseEntityPackage are null.";
            throw new IllegalStateException(msg);
        }
    }

    public String getExtendedParameterBeanPackage() {
        String specifiedPackage = getSpecifiedExtendedParameterBeanPackage();
        if (specifiedPackage != null && specifiedPackage.trim().length() != 0) {
            return specifiedPackage;
        }
        final String defaultPackage;
        if (isMakeDaoInterface()) {
            defaultPackage = getBasicProperties().getExtendedDaoPackage() + "." + getPmbeanPackageName();
        } else {
            defaultPackage = getBasicProperties().getExtendedBehaviorPackage() + "." + getPmbeanPackageName();
        }
        if (defaultPackage != null && defaultPackage.trim().length() != 0) {
            return defaultPackage;
        } else {
            String msg = "Both packageString in sql2entity-property and extendedEntityPackage are null.";
            throw new IllegalStateException(msg);
        }
    }

    protected String getPmbeanPackageName() {
        String pmbeanPackage = "pmbean";
        if (getBasicProperties().isTargetLanguageCSharp()) {
            pmbeanPackage = "PmBean";
        }
        return pmbeanPackage;
    }

    protected boolean isMakeDaoInterface() {
        return getLittleAdjustmentProperties().isMakeDaoInterface();
    }

    // ===================================================================================
    //                                                                   BehaviorQueryPath
    //                                                                   =================
    public boolean isSuppressBehaviorQueryPath() { // It's closet!
        return isProperty("isSuppressBehaviorQueryPath", false);
    }

    // ===================================================================================
    //                                                                      DefaultPackage
    //                                                                      ==============
    public boolean isDefaultPackageValid() { // C# only
        return getDefaultPackage() != null && getDefaultPackage().trim().length() > 0
                && !getDefaultPackage().trim().equalsIgnoreCase("null");
    }

    public String getDefaultPackage() { // C# only
        return (String) getOutsideSqlDefinitionMap().get("defaultPackage");
    }

    // ===================================================================================
    //                                                             OmitResourcePathPackage
    //                                                             =======================
    public boolean isOmitResourcePathPackageValid() { // C# only
        return getOmitResourcePathPackage() != null && getOmitResourcePathPackage().trim().length() > 0
                && !getOmitResourcePathPackage().trim().equalsIgnoreCase("null");
    }

    public String getOmitResourcePathPackage() { // C# only
        return (String) getOutsideSqlDefinitionMap().get("omitResourcePathPackage");
    }

    // ===================================================================================
    //                                                           OmitFileSystemPathPackage
    //                                                           =========================
    public boolean isOmitFileSystemPathPackageValid() { // C# only
        return getOmitFileSystemPathPackage() != null && getOmitFileSystemPathPackage().trim().length() > 0
                && !getOmitFileSystemPathPackage().trim().equalsIgnoreCase("null");
    }

    public String getOmitFileSystemPathPackage() { // C# only
        return (String) getOutsideSqlDefinitionMap().get("omitFileSystemPathPackage");
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    @SuppressWarnings("unchecked")
    protected List<String> getOutsideSqlPropertyAsList(String key) {
        return (List<String>) getOutsideSqlDefinitionMap().get(key);
    }
}