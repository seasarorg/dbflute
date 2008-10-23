package org.seasar.dbflute.properties;

import java.util.List;
import java.util.Map;
import java.util.Properties;

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
            String sqlPackageDirectory = replaceDotToSeparator(sqlPackage);
            sqlDirectory = sqlDirectory + "/" + removeStartSeparatorIfNeeds(sqlPackageDirectory);
        }
        return sqlDirectory;
    }

    protected String getDefaultSqlDirectory() {
        return getBasicProperties().getOutputDirectory();
    }

    protected String replaceDotToSeparator(String sqlPackage) {
        return DfStringUtil.replace(getSqlPackage(), ".", "/");
    }

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
        DfGeneratedClassPackageProperties packageProperties = getBasicProperties().getGeneratedClassPackageProperties();
        String packageBase = packageProperties.getPackageBase();
        return DfStringUtil.replace(sqlPackage, "$$PACKAGE_BASE$$", packageBase);
    }

    // ===================================================================================
    //                                                    RemoveLineCommentFromExecutedSql
    //                                                    ================================
    public boolean isRemoveLineCommentFromExecutedSql() { // Java only
        return getBasicProperties().isDatabaseMsAccess(); // Only MS-Access does not support line comment. 

        // In the meantime, this is not unsupported. At the future I don't know.
        // final String value = (String) getOutsideSqlDefinitionMap().get("removeLineCommentFromExecutedSql");
        // return value != null && value.trim().length() > 0 && value.trim().equalsIgnoreCase("true");
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
    //                                                                     OutputDirectory
    //                                                                     ===============
    public String getSql2EntityOutputDirectory() {
        final String value = (String) getOutsideSqlDefinitionMap().get("sql2EntityOutputDirectory");
        if (value == null || value.trim().length() == 0) {
            return getBasicProperties().getOutputDirectory();
        } else {
            return value;
        }
    }

    // ===================================================================================
    //                                                             Procedure ParameterBean
    //                                                             =======================
    public boolean isGenerateProcedureParameterBean() {
        String value = (String) getOutsideSqlDefinitionMap().get("generateProcedureParameterBean");
        return value != null && value.trim().equalsIgnoreCase("true");
    }

    public boolean isTargetProcedureName(String procedureName) {
        final List<String> targetProcedureNameList = getTargetProcedureNameList();
        if (targetProcedureNameList == null || targetProcedureNameList.isEmpty()) {
            return true;
        }
        for (String procedureNameHint : targetProcedureNameList) {
            if (isHitByTheHint(procedureName, procedureNameHint)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> getTargetProcedureNameList() {
        return getOutsideSqlPropertyAsList("targetProcedureNameList");
    }

    // ===================================================================================
    //                                                                      Package Detail
    //                                                                      ==============
    protected String getSpecifiedBaseCustomizeEntityPackage() {
        final String value = (String) getOutsideSqlDefinitionMap().get("baseCustomizeEntityPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    protected String getSpecifiedExtendedCustomizeEntityPackage() {
        final String value = (String) getOutsideSqlDefinitionMap().get("extendedCustomizeEntityPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    protected String getSpecifiedBaseParameterBeanPackage() {
        final String value = (String) getOutsideSqlDefinitionMap().get("baseParameterBeanPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    protected String getSpecifiedExtendedParameterBeanPackage() {
        final String value = (String) getOutsideSqlDefinitionMap().get("extendedParameterBeanPackage");
        return (value != null && value.trim().length() > 0) ? value : null;
    }

    public String getBaseEntityPackage() {
        String specifiedPackage = getSpecifiedBaseCustomizeEntityPackage();
        if (specifiedPackage != null && specifiedPackage.trim().length() != 0) {
            return specifiedPackage;
        }
        DfGeneratedClassPackageProperties packageProperties = getGeneratedClassPackageProperties();
        String defaultPackage = packageProperties.getBaseEntityPackage() + "." + getCustomizePackageName();
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
        DfGeneratedClassPackageProperties packageProperties = getGeneratedClassPackageProperties();
        String defaultPackage = packageProperties.getExtendedEntityPackage() + "." + getCustomizePackageName();
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

    public String getBaseParameterBeanPackage() {
        String specifiedPackage = getSpecifiedBaseParameterBeanPackage();
        if (specifiedPackage != null && specifiedPackage.trim().length() != 0) {
            return specifiedPackage;
        }
        DfGeneratedClassPackageProperties packageProperties = getGeneratedClassPackageProperties();
        String defaultPackage = packageProperties.getBaseDaoPackage() + "." + getPmbeanPackageName();
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
        DfGeneratedClassPackageProperties packageProperties = getGeneratedClassPackageProperties();
        String defaultPackage = packageProperties.getExtendedDaoPackage() + "." + getPmbeanPackageName();
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

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    @SuppressWarnings("unchecked")
    protected List<String> getOutsideSqlPropertyAsList(String key) {
        return (List<String>) getOutsideSqlDefinitionMap().get(key);
    }
}