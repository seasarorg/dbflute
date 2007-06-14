package org.seasar.dbflute.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoCSharp;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;

/**
 * Basic properties.
 * 
 * @author jflute
 */
public final class DfBasicProperties extends DfAbstractHelperProperties {

    /**
     * Constructor.
     * 
     * @param prop Properties. (NotNull)
     */
    public DfBasicProperties(Properties prop) {
        super(prop);
    }

    //========================================================================================
    //                                                                                 Project
    //                                                                                 =======
    public String getProjectName() {
        return stringProp("torque.project", "");
    }

    //========================================================================================
    //                                                                                Database
    //                                                                                ========
    public String getDatabaseName() {
        return stringProp("torque.database", "");
    }

    public boolean isDatabaseMySQL() {
        return getDatabaseName().equalsIgnoreCase("mysql");
    }

    public boolean isDatabaseOracle() {
        return getDatabaseName().equalsIgnoreCase("oracle");
    }

    public boolean isDatabaseSqlServer() {
        return getDatabaseName().equalsIgnoreCase("mssql");
    }

    public boolean isDatabaseSybase() {
        return getDatabaseName().equalsIgnoreCase("sybase");
    }

    //========================================================================================
    //                                                                                 JavaDir
    //                                                                                 =======
    public String getJavaDir() {
        return stringProp("torque.java.dir", "../src/main/java");
    }

    public String getJavaLocation_for_gen() {
        return stringProp("torque.java.location.for.gen", "");
    }

    public String getJavaLocation_for_main() {
        return stringProp("torque.java.location.for.main", "");
    }

    public String getJavaDir_for_gen() {
        final String fileSeparator = "/";
        final String javaDirBase = getJavaDir();
        final String javaLocation = getJavaLocation_for_gen();
        String outputPath = "";
        if (javaDirBase != null && javaDirBase.endsWith(fileSeparator)) {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation.substring(fileSeparator.length());
            } else {
                outputPath = javaDirBase + javaLocation;
            }
        } else {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation;
            } else {
                outputPath = javaDirBase + fileSeparator + javaLocation;
            }
        }
        return outputPath;
    }

    public String getJavaDir_for_main() {
        final String fileSeparator = "/";
        final String javaDirBase = getJavaDir();
        final String javaLocation = getJavaLocation_for_main();
        String outputPath = "";
        if (javaDirBase != null && javaDirBase.endsWith(fileSeparator)) {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation.substring(fileSeparator.length());
            } else {
                outputPath = javaDirBase + javaLocation;
            }
        } else {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation;
            } else {
                outputPath = javaDirBase + fileSeparator + javaLocation;
            }
        }
        return outputPath;
    }

    public boolean isJavaDirOnlyOne() {
        return getJavaDir_for_gen().equals(getJavaDir_for_main());
    }

    //========================================================================================
    //                                                                                Language
    //                                                                                ========
    public String getTargetLanguage() {
        return stringProp("torque.targetLanguage", DEFAULT_targetLanguage);
    }

    public boolean isTargetLanguageJava() {
        return JAVA_targetLanguage.equals(getTargetLanguage());
    }

    public boolean isTargetLanguageCSharp() {
        return CSHARP_targetLanguage.equals(getTargetLanguage());
    }

    protected DfLanguageDependencyInfo _languageDependencyInfo;

    public DfLanguageDependencyInfo getLanguageDependencyInfo() {
        if (_languageDependencyInfo == null) {
            if (isTargetLanguageJava()) {
                _languageDependencyInfo = new DfLanguageDependencyInfoJava();
            } else if (isTargetLanguageCSharp()) {
                _languageDependencyInfo = new DfLanguageDependencyInfoCSharp();
            } else {
                String msg = "The language is supported: " + getTargetLanguage();
                throw new IllegalStateException(msg);
            }
        }
        return _languageDependencyInfo;
    }

    //========================================================================================
    //                                                                               Extension
    //                                                                               =========
    public String getTemplateFileExtension() {
        return getLanguageDependencyInfo().getTemplateFileExtension();
    }

    public String getClassFileExtension() {
        return getLanguageDependencyInfo().getGrammarInfo().getClassFileExtension();
    }

    //========================================================================================
    //                                                                                Encoding
    //                                                                                ========
    public String getTemplateFileEncoding() {
        return stringProp("torque.templateFileEncoding", DEFAULT_templateFileEncoding);
    }

    public String getSourceFileEncoding() {
        return stringProp("torque.sourceFileEncoding", DEFAULT_sourceFileEncoding);
    }

    //========================================================================================
    //                                                                            Class Author
    //                                                                            ============
    public String getClassAuthor() {
        return stringProp("torque.classAuthor", "DBFlute(AutoGenerator)");
    }

    //========================================================================================
    //                                                                                  Naming
    //                                                                                  ======
    public boolean isJavaNameOfTableSameAsDbName() {
        return booleanProp("torque.isJavaNameOfTableSameAsDbName", false);
    }

    public boolean isJavaNameOfColumnSameAsDbName() {
        return booleanProp("torque.isJavaNameOfColumnSameAsDbName", false);
    }

    //========================================================================================
    //                                                                                Behavior
    //                                                                                ========
    public boolean isAvailableBehaviorGeneration() {
        return booleanProp("torque.isAvailableBehaviorGeneration", true);
    }

    //========================================================================================
    //                                                                                Generics
    //                                                                                ========
    public boolean isAvailableGenerics() {
        return booleanProp("torque.isAvailableGenerics", true);
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
            return "";
        } else {
            return "(" + genericsDowncast + ")";
        }
    }

    public String filterGenericsGeneralOutput(String genericsGeneralOutput) {
        if (isAvailableGenerics()) {
            return "";
        } else {
            return genericsGeneralOutput;
        }
    }

    //========================================================================================
    //                                                                                  Prefix
    //                                                                                  ======
    public String getProjectPrefix() {
        return stringProp("torque.projectPrefix", "");
    }

    public boolean isAppendProjectSuffixToComponentName() {
        return booleanProp("torque.isAppendProjectSuffixToComponentName", true);
    }

    //========================================================================================
    //                                                                           Database Info
    //                                                                           =============
    /** Database info map. (for cache) */
    protected Map<String, Object> _databaseInfoMap;

    public String getDatabaseDriver() {
        initializeDatabaseInfoMap();
        final String key = "driver";
        final String databaseInfoElement = getDatabaseInfoElement(key);
        if (databaseInfoElement != null) {
            return databaseInfoElement;
        }
        return stringProp("torque.database.driver");
    }

    public String getDatabaseUri() {
        initializeDatabaseInfoMap();
        final String key = "url";
        final String databaseInfoElement = getDatabaseInfoElement(key);
        if (databaseInfoElement != null) {
            return databaseInfoElement;
        }
        return stringProp("torque.database.url");
    }

    public String getDatabaseSchema() {
        initializeDatabaseInfoMap();
        final String key = "schema";
        final String databaseInfoElement = getDatabaseInfoElement(key);
        if (databaseInfoElement != null) {
            return databaseInfoElement;
        }
        return stringProp("torque.database.schema", "");
    }

    public String getDatabaseUser() {
        initializeDatabaseInfoMap();
        final String key = "user";
        final String databaseInfoElement = getDatabaseInfoElement(key);
        if (databaseInfoElement != null) {
            return databaseInfoElement;
        }
        return stringProp("torque.database.user");
    }

    public String getDatabasePassword() {
        initializeDatabaseInfoMap();
        final String key = "password";
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