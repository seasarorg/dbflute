package org.seasar.dbflute.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoCSharp;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoPhp;

/**
 * Basic properties.
 * 
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
    //                                                                             Project
    //                                                                             =======
    public String getProjectName() {
        return stringProp("torque.project", "");
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    public String getDatabaseName() {
        return stringProp("torque.database", "");
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

    public boolean isDatabaseSybase() {
        return getDatabaseName().equalsIgnoreCase("sybase");
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public String getOutputDirectory() {
        final String defaultSourceDirectory = getLanguageDependencyInfo().getDefaultSourceDirectory();
        return stringProp("torque.java.dir", defaultSourceDirectory);// 'java.dir' is legacy of Apache Torque.
    }

    // ===================================================================================
    //                                                                            Language
    //                                                                            ========
    public String getTargetLanguage() {
        return stringProp("torque.targetLanguage", DEFAULT_targetLanguage);
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
        return stringProp("torque.targetLanguageVersion", isAvailableGenerics() ? "5.0" : "1.4");
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
        final String containerName = stringProp("torque.targetContainer", "seasar");
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
    //                                                                           Extension
    //                                                                           =========
    public String getTemplateFileExtension() {
        return getLanguageDependencyInfo().getTemplateFileExtension();
    }

    public String getClassFileExtension() {
        return getLanguageDependencyInfo().getGrammarInfo().getClassFileExtension();
    }

    // ===================================================================================
    //                                                                            Encoding
    //                                                                            ========
    public String getTemplateFileEncoding() {
        return stringProp("torque.templateFileEncoding", DEFAULT_templateFileEncoding);
    }

    public String getSourceFileEncoding() {
        return stringProp("torque.sourceFileEncoding", DEFAULT_sourceFileEncoding);
    }

    // ===================================================================================
    //                                                                        Class Author
    //                                                                        ============
    public String getClassAuthor() {
        return stringProp("torque.classAuthor", "DBFlute(AutoGenerator)");
    }

    // ===================================================================================
    //                                                                              Naming
    //                                                                              ======
    public boolean isJavaNameOfTableSameAsDbName() {
        return booleanProp("torque.isJavaNameOfTableSameAsDbName", false);
    }

    public boolean isJavaNameOfColumnSameAsDbName() {
        return booleanProp("torque.isJavaNameOfColumnSameAsDbName", false);
    }

    // ===================================================================================
    //                                                                            Generics
    //                                                                            ========
    protected boolean isAvailableGenerics() {
        return true;
    }

    public String filterGenericsString(String genericsString) {
        if (isAvailableGenerics()) {
            return "<" + genericsString + ">";
        } else {
            return "";
        }
    }

    public String filterGenericsDowncast(String genericsDowncast) {
        if (isAvailableGenerics()) {
            return "(" + genericsDowncast + ")";
        } else {
            return "";
        }
    }

    public String filterGenericsParamOutput(String variableName, String description) {
        return filterGenericsGeneralOutput("@param " + variableName + " " + description);
    }

    public String filterGenericsGeneralOutput(String genericsGeneralOutput) {
        if (isAvailableGenerics()) {
            return genericsGeneralOutput;
        } else {
            return "";
        }
    }

    public String filterGenericsGeneralOutputAfterNewLineOutput(String genericsGeneralOutput) {
        if (isAvailableGenerics()) {
            return getLineSeparator() + filterGenericsGeneralOutput(genericsGeneralOutput);
        } else {
            return "";
        }
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
        return System.getProperty("line.separator");
    }

    // ===================================================================================
    //                                                                              Prefix
    //                                                                              ======
    public String getProjectPrefix() {
        return stringProp("torque.projectPrefix", "");
    }

    public String getBasePrefix() {
        return "Bs";
    }

    public boolean isAppendProjectSuffixToComponentName() {
        return booleanProp("torque.isAppendProjectSuffixToComponentName", true);
    }

    // ===================================================================================
    //                                                                           HotDeploy
    //                                                                           =========
    public boolean isAvailableHotDeploy() {
        return booleanProp("torque.isAvailableHotDeploy", false);
    }

    // ===================================================================================
    //                                                                           Copyright
    //                                                                           =========
    public String getAllClassCopyright() {
        return stringProp("torque.allClassCopyright", "");
    }

    // ===================================================================================
    //                                                                 Behavior Query Path
    //                                                                 ===================
    public String getBehaviorQueryPathBeginMark() {
        return "/*df:BehaviorQueryPathBegin*/";
    }

    public String getBehaviorQueryPathEndMark() {
        return "/*df:BehaviorQueryPathEnd*/";
    }

    // ===================================================================================
    //                                                                       Database Info
    //                                                                       =============
    protected DatabaseInfo _databaseInfo = new DatabaseInfo();

    public String getDatabaseDriver() {
        return _databaseInfo.getDatabaseDriver();
    }

    public String getDatabaseUri() {
        return _databaseInfo.getDatabaseUri();
    }

    public String getDatabaseSchema() {
        return _databaseInfo.getDatabaseSchema();
    }

    public String getDatabaseUser() {
        return _databaseInfo.getDatabaseUser();
    }

    public String getDatabasePassword() {
        return _databaseInfo.getDatabasePassword();
    }

    public class DatabaseInfo {
        private static final String KEY_DRIVER = "driver";
        private static final String KEY_URL = "url";
        private static final String KEY_SCHEMA = "schema";
        private static final String KEY_USER = "user";
        private static final String KEY_PASSWORD = "password";

        /** Database info map. (for cache) */
        protected Map<String, Object> _databaseInfoMap;

        public String getDatabaseDriver() {
            initializeDatabaseInfoMap();
            final String key = KEY_DRIVER;
            final String databaseInfoElement = getDatabaseInfoElement(key);
            if (databaseInfoElement != null) {
                return databaseInfoElement;
            }
            return stringProp("torque.database.driver");
        }

        public String getDatabaseUri() {
            initializeDatabaseInfoMap();
            final String key = KEY_URL;
            final String databaseInfoElement = getDatabaseInfoElement(key);
            if (databaseInfoElement != null) {
                return databaseInfoElement + getDatabaseUriProperty();
            }
            return stringProp("torque.database.url");
        }

        private String getDatabaseUriProperty() {
            initializeDatabaseInfoMap();

            final StringBuilder sb = new StringBuilder();
            final Set<String> keySet = _databaseInfoMap.keySet();
            for (String key : keySet) {
                if (equalsKeys(key, KEY_DRIVER, KEY_URL, KEY_SCHEMA, KEY_USER, KEY_PASSWORD)) {
                    continue;
                }
                final Object value = _databaseInfoMap.get(key);
                sb.append(";").append(key).append("=").append(value);
            }
            return sb.toString();
        }

        private boolean equalsKeys(String target, String... keys) {
            for (String key : keys) {
                if (target.equals(key)) {
                    return true;
                }
            }
            return false;
        }

        public String getDatabaseSchema() {
            initializeDatabaseInfoMap();
            final String key = KEY_SCHEMA;
            final String databaseInfoElement = getDatabaseInfoElement(key);
            if (databaseInfoElement != null) {
                return databaseInfoElement;
            }
            return stringProp("torque.database.schema", "");
        }

        public String getDatabaseUser() {
            initializeDatabaseInfoMap();
            final String key = KEY_USER;
            final String databaseInfoElement = getDatabaseInfoElement(key);
            if (databaseInfoElement != null) {
                return databaseInfoElement;
            }
            return stringProp("torque.database.user");
        }

        public String getDatabasePassword() {
            initializeDatabaseInfoMap();
            final String key = KEY_PASSWORD;
            final String databaseInfoElement = getDatabaseInfoElement(key);
            if (databaseInfoElement != null) {
                return databaseInfoElement;
            }
            return stringProp("torque.database.password");
        }

        protected void initializeDatabaseInfoMap() {
            if (_databaseInfoMap == null) {
                Map<String, Object> databaseInfoMap = getOutsidePropMap("databaseInfo");
                if (databaseInfoMap.isEmpty()) {
                    databaseInfoMap = getOutsidePropMap("databaseInfoMap");
                }
                if (!databaseInfoMap.isEmpty()) {
                    _databaseInfoMap = databaseInfoMap;
                }
            }
        }

        protected boolean hasDatabaseInfoMap() {
            return _databaseInfoMap != null;
        }

        protected String getDatabaseInfoElement(final String key) {
            if (_databaseInfoMap != null) {
                if (!_databaseInfoMap.containsKey(key)) {
                    return "";
                }
                final String value = (String) _databaseInfoMap.get(key);
                return value != null ? value : "";
            }
            return null;
        }
    }

    // -----------------------------------------------------
    //                                   Connection Creation
    //                                   -------------------
    public Connection getConnection() {
        try {
            Class.forName(getDatabaseDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return DriverManager.getConnection(getDatabaseUri(), getDatabaseUser(), getDatabasePassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------
    //                                    Database Type List
    //                                    ------------------
    public List<String> getDatabaseTypeList() {
        final List<Object> defaultList = new ArrayList<Object>();
        defaultList.add("TABLE");
        defaultList.add("VIEW");
        final List<String> resultList = new ArrayList<String>();
        final List<Object> listProp = listProp("torque.database.type.list", defaultList);
        for (Object object : listProp) {
            resultList.add((String) object);
        }
        return resultList;
    }

    // -----------------------------------------------------
    //                                     Table Except List
    //                                     -----------------
    public List<String> getTableExceptList() {
        final List<String> resultList = new ArrayList<String>();
        final List<Object> listProp = listProp("torque.table.except.list", DEFAULT_EMPTY_LIST);
        for (Object object : listProp) {
            resultList.add((String) object);
        }
        return resultList;
    }

    // -----------------------------------------------------
    //                                     Table Target List
    //                                     -----------------
    public List<String> getTableTargetList() {
        final List<String> resultList = new ArrayList<String>();
        final List<Object> listProp = listProp("torque.table.target.list", DEFAULT_EMPTY_LIST);
        for (Object object : listProp) {
            resultList.add((String) object);
        }
        return resultList;
    }

    // -----------------------------------------------------
    //                                    Column Except List
    //                                    ------------------
    public List<String> getSimpleColumnExceptList() {
        final List<String> resultList = new ArrayList<String>();
        final List<Object> listProp = listProp("torque.simple.column.except.list", DEFAULT_EMPTY_LIST);
        for (Object object : listProp) {
            resultList.add((String) object);
        }
        return resultList;
    }
}