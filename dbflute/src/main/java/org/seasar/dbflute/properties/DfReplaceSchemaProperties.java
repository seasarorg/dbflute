package org.seasar.dbflute.properties;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.DfUrlAnalyzer;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.factory.DfUrlAnalyzerFactory;
import org.seasar.dbflute.properties.assistant.DfConnectionProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public final class DfReplaceSchemaProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaProperties.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfReplaceSchemaProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                          replaceSchemaDefinitionMap
    //                                                          ==========================
    public static final String KEY_replaceSchemaDefinitionMap = "replaceSchemaDefinitionMap";
    protected Map<String, Object> _replaceSchemaDefinitionMap;

    public Map<String, Object> getReplaceSchemaDefinitionMap() {
        if (_replaceSchemaDefinitionMap == null) {
            _replaceSchemaDefinitionMap = mapProp("torque." + KEY_replaceSchemaDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _replaceSchemaDefinitionMap;
    }

    // ===================================================================================
    //                                                                      Base Directory
    //                                                                      ==============
    public String getReplaceSchemaPlaySqlDirectory() {
        return "./playsql";
    }

    // ===================================================================================
    //                                                                          Schema SQL
    //                                                                          ==========
    // -----------------------------------------------------
    //                                         Create Schema
    //                                         -------------
    public String getReplaceSchemaSqlTitle() {
        return "replace-schema";
    }

    public List<File> getReplaceSchemaSqlFileList() {
        final String directoryPath = getReplaceSchemaPlaySqlDirectory();
        return doGetSchemaSqlFileList(directoryPath, getReplaceSchemaSqlTitle());
    }

    public Map<String, File> getReplaceSchemaSqlFileMap() {
        final Map<String, File> resultMap = new LinkedHashMap<String, File>();
        final List<File> sqlFileList = getReplaceSchemaSqlFileList();
        for (File sqlFile : sqlFileList) {
            resultMap.put(sqlFile.getName(), sqlFile);
        }
        return resultMap;
    }

    protected List<File> doGetSchemaSqlFileList(String directoryPath, final String fileNamePrefix) {
        final File baseDir = new File(directoryPath);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(fileNamePrefix) && name.endsWith(".sql")) {
                    return true;
                }
                return false;
            }
        };

        // order by FileName asc
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final List<File> schemaSqlFileList;
        final String[] targetList = baseDir.list(filter);
        if (targetList != null) {
            for (String targetFileName : targetList) {
                final String targetFilePath = directoryPath + "/" + targetFileName;
                treeSet.add(new File(targetFilePath));
            }
            schemaSqlFileList = new ArrayList<File>(treeSet);
        } else {
            schemaSqlFileList = DfCollectionUtil.emptyList();
        }
        return schemaSqlFileList;
    }

    // -----------------------------------------------------
    //                                          Take Finally
    //                                          ------------
    protected String getTakeFinallySqlFile() {
        return getReplaceSchemaPlaySqlDirectory() + "/take-finally.sql";
    }

    public String getTakeFinallySqlTitle() {
        return "take-finally";
    }

    public List<File> getTakeFinallySqlFileList() {
        final String directoryPath = getReplaceSchemaPlaySqlDirectory();
        return doGetSchemaSqlFileList(directoryPath, getTakeFinallySqlTitle());
    }

    public Map<String, File> getTakeFinallySqlFileMap() {
        final Map<String, File> resultMap = new LinkedHashMap<String, File>();
        final List<File> sqlFileList = getTakeFinallySqlFileList();
        for (File sqlFile : sqlFileList) {
            resultMap.put(sqlFile.getName(), sqlFile);
        }
        return resultMap;
    }

    // ===================================================================================
    //                                                                         Schema Data
    //                                                                         ===========
    public String getCommonDataDir(String dir, String typeName) {
        return dir + "/data/common/" + typeName;
    }

    public String getLoadingTypeDataDir(String dir, String envType, String typeName) {
        return dir + "/data/" + envType + "/" + typeName;
    }

    // non-ApplicationPlaySql below

    protected String getMainCurrentEnvDataDir() {
        final String playSqlDirectory = getReplaceSchemaPlaySqlDirectory();
        final String dataLoadingType = getDataLoadingType();
        return playSqlDirectory + "/data/" + dataLoadingType;
    }

    public String getMainCurrentEnvDataDir(String typeName) {
        return getMainCurrentEnvDataDir() + "/" + typeName;
    }

    public String getMainCurrentEnvFirstXlsDataDir() {
        return getMainCurrentEnvDataDir() + "/firstxls";
    }

    public String getMainCurrentEnvTsvDataDir() {
        return getMainCurrentEnvDataDir() + "/tsv";
    }

    public String getMainCurrentEnvTsvUTF8DataDir() {
        return getMainCurrentEnvTsvDataDir() + "/UTF-8";
    }

    public String getMainCurrentEnvCsvDataDir() {
        return getMainCurrentEnvDataDir() + "/csv";
    }

    public String getMainCurrentEnvCsvUTF8DataDir() {
        return getMainCurrentEnvCsvDataDir() + "/csv/UTF-8";
    }

    public String getMainCurrentEnvXlsDataDir() {
        return getMainCurrentEnvDataDir() + "/xls";
    }

    // ===================================================================================
    //                                                                   Data Loading Type
    //                                                                   =================
    public String getDataLoadingType() {
        final String propString = (String) getReplaceSchemaDefinitionMap().get("dataLoadingType");
        if (propString == null) {
            return getOldStyleEnvironmentType();
        }
        return propString;
    }

    protected String getOldStyleEnvironmentType() { // Old Style!
        final String propString = (String) getReplaceSchemaDefinitionMap().get("environmentType");
        if (propString == null) {
            return "ut";
        }
        return propString;
    }

    // ===================================================================================
    //                                                                Filter Variables Map
    //                                                                ====================
    protected Map<String, String> _filterVariablesMap;

    @SuppressWarnings("unchecked")
    protected Map<String, String> getFilterVariablesMap() {
        if (_filterVariablesMap != null) {
            return _filterVariablesMap;
        }
        _filterVariablesMap = (Map<String, String>) getReplaceSchemaDefinitionMap().get("filterVariablesMap");
        if (_filterVariablesMap == null) {
            _filterVariablesMap = new HashMap<String, String>();
        }
        return _filterVariablesMap;
    }

    protected String getFilterVariablesBeginMark() {
        return "/*$";
    }

    protected String getFilterVariablesEndMark() {
        return "*/";
    }

    public String resolveFilterVariablesIfNeeds(String sql) {
        final String beginMark = getFilterVariablesBeginMark();
        final String endMark = getFilterVariablesEndMark();
        final Map<String, String> filterVariablesMap = getFilterVariablesMap();
        if (!filterVariablesMap.isEmpty() && sql.contains(beginMark) && sql.contains(endMark)) {
            final Set<String> keySet = filterVariablesMap.keySet();
            for (String key : keySet) {
                final String variableMark = beginMark + key + endMark;
                if (sql.contains(variableMark)) {
                    final String value = filterVariablesMap.get(key);
                    sql = replaceString(sql, variableMark, value);
                }
            }
        }
        return sql;
    }

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    public boolean isLoggingInsertSql() {
        return isProperty("isLoggingInsertSql", true, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                   SQL File Encoding
    //                                                                   =================
    public String getSqlFileEncoding() {
        final String sqlFileEncoding = (String) getReplaceSchemaDefinitionMap().get("sqlFileEncoding");
        if (sqlFileEncoding != null && sqlFileEncoding.trim().length() != 0) {
            return sqlFileEncoding;
        } else {
            return "UTF-8";
        }
    }

    // ===================================================================================
    //                                                                          Skip Sheet
    //                                                                          ==========
    public String getSkipSheet() {
        final String skipSheet = (String) getReplaceSchemaDefinitionMap().get("skipSheet");
        if (skipSheet != null && skipSheet.trim().length() != 0) {
            return skipSheet;
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                                  Increment Sequence
    //                                                                  ==================
    public boolean isIncrementSequenceToDataMax() {
        return isProperty("isIncrementSequenceToDataMax", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                               Suppress Batch Update
    //                                                               =====================
    public boolean isSuppressBatchUpdate() {
        return isProperty("isSuppressBatchUpdate", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                             Object Type Target List
    //                                                             =======================
    protected List<String> _objectTypeTargetList;

    public List<String> getObjectTypeTargetList() { // overrides the property of databaseInfoMap 
        final Object obj = getReplaceSchemaDefinitionMap().get("objectTypeTargetList");
        if (obj != null && !(obj instanceof List<?>)) {
            String msg = "The type of the property 'objectTypeTargetList' should be List: " + obj;
            throw new DfIllegalPropertyTypeException(msg);
        }
        final List<String> defaultObjectTypeTargetList = getDefaultObjectTypeTargetList();
        if (obj == null) {
            _objectTypeTargetList = defaultObjectTypeTargetList;
        } else {
            @SuppressWarnings("unchecked")
            final List<String> list = (List<String>) obj;
            _objectTypeTargetList = !list.isEmpty() ? list : defaultObjectTypeTargetList;
        }
        return _objectTypeTargetList;
    }

    protected List<String> getDefaultObjectTypeTargetList() {
        return getDatabaseProperties().getObjectTypeTargetList(); // inherit
    }

    // ===================================================================================
    //                                                                     Additional User
    //                                                                     ===============
    protected Map<String, Map<String, String>> _additionalUesrMap;

    public Map<String, Map<String, String>> getAdditionalUserMap() {
        if (_additionalUesrMap != null) {
            return _additionalUesrMap;
        }
        final Object obj = getReplaceSchemaDefinitionMap().get("additionalUserMap");
        if (obj != null && !(obj instanceof Map<?, ?>)) {
            String msg = "The type of the property 'additionalUserMap' should be Map: " + obj;
            throw new DfIllegalPropertyTypeException(msg);
        }
        if (obj == null) {
            _additionalUesrMap = DfCollectionUtil.emptyMap();
        } else {
            @SuppressWarnings("unchecked")
            final Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) obj;
            _additionalUesrMap = map;
        }
        return _additionalUesrMap;
    }

    protected Map<String, String> getAdditionalUserPropertyMap(String additonalUser) {
        return getAdditionalUserMap().get(additonalUser);
    }

    public Connection createAdditionalUserConnection(String additonalUser) {
        final Map<String, String> propertyMap = getAdditionalUserPropertyMap(additonalUser);
        if (propertyMap == null) {
            return null;
        }
        final String driver = getDatabaseProperties().getDatabaseDriver();
        final String url;
        {
            String property = propertyMap.get("url");
            if (property != null && property.trim().length() > 0) {
                url = property;
            } else {
                url = getDatabaseProperties().getDatabaseUrl();
            }
        }
        final DfUrlAnalyzerFactory factory = new DfUrlAnalyzerFactory(getBasicProperties(), url);
        final DfUrlAnalyzer analyzer = factory.createAnalyzer();
        final String catalog = analyzer.extractCatalog();
        final String schema = propertyMap.get("schema");
        final UnifiedSchema unifiedSchema = UnifiedSchema.createAsDynamicSchema(catalog, schema);
        final String user = propertyMap.get("user");
        final String password = propertyMap.get("password");
        _log.info("...Creating a connection for additional user");
        return createConnection(driver, url, unifiedSchema, user, password);
    }

    // ===================================================================================
    //                                                                     Additional Drop
    //                                                                     ===============
    protected List<Map<String, Object>> _additionalDropMapList;

    public List<Map<String, Object>> getAdditionalDropMapList() {
        if (_additionalDropMapList != null) {
            return _additionalDropMapList;
        }
        final Object obj = getReplaceSchemaDefinitionMap().get("additionalDropMapList");
        if (obj == null) {
            _additionalDropMapList = DfCollectionUtil.emptyList();
        } else {
            _additionalDropMapList = castToList(obj, "additionalDropMapList");
        }
        return _additionalDropMapList;
    }

    public String getAdditionalDropUrl(Map<String, Object> additionalDropMap) {
        final Object obj = additionalDropMap.get("url");
        if (obj == null) {
            return null;
        }
        return castToString(obj, "additionalDropMapList.url");
    }

    public String getAdditionalDropUser(Map<String, Object> additionalDropMap) {
        final Object obj = additionalDropMap.get("user");
        if (obj == null) {
            return null;
        }
        return castToString(obj, "additionalDropMapList.user");
    }

    public String getAdditionalDropPassword(Map<String, Object> additionalDropMap) {
        final Object obj = additionalDropMap.get("password");
        if (obj == null) {
            return null;
        }
        return castToString(obj, "additionalDropMapList.password");
    }

    @SuppressWarnings("unchecked")
    public Properties getAdditionalDropPropertiesMap(Map<String, Object> additionalDropMap) {
        Object obj = additionalDropMap.get("propertiesMap");
        if (obj == null) {
            return new Properties();
        }
        if (!(obj instanceof Map)) {
            String msg = "The schema should be Map<String, String>:";
            msg = msg + " propertiesMap=" + obj + " type=" + obj.getClass();
            throw new DfIllegalPropertyTypeException(msg);
        }
        final Properties prop = new Properties();
        prop.putAll((Map<String, String>) obj);
        return prop;
    }

    public UnifiedSchema getAdditionalDropSchema(Map<String, Object> additionalDropMap) {
        final String url = getAdditionalDropUrl(additionalDropMap);
        final String catalog;
        if (Srl.is_NotNull_and_NotTrimmedEmpty(url)) {
            final DfUrlAnalyzerFactory factory = new DfUrlAnalyzerFactory(getBasicProperties(), url);
            final DfUrlAnalyzer analyzer = factory.createAnalyzer();
            catalog = analyzer.extractCatalog();
        } else {
            catalog = getDatabaseProperties().getDatabaseCatalog();
        }
        final Object obj = additionalDropMap.get("schema");
        if (obj == null) {
            if (!isDatabaseAsSchemaSpecificationOmittable()) {
                String msg = "The schema is required:";
                msg = msg + " additionalDropMap=" + additionalDropMap;
                throw new DfRequiredPropertyNotFoundException(msg);
            }
            return null;
        }
        final String schema = castToString(obj, "additionalDropMapList.schema");
        final UnifiedSchema unifiedSchema = UnifiedSchema.createAsDynamicSchema(catalog, schema);
        return unifiedSchema;
    }

    protected boolean isDatabaseAsSchemaSpecificationOmittable() {
        return getBasicProperties().isDatabaseAsSchemaSpecificationOmittable();
    }

    public List<String> getAdditionalDropObjectTypeList(Map<String, Object> additionalDropMap) {
        Object obj = additionalDropMap.get("objectTypeTargetList");
        if (obj == null) {
            obj = additionalDropMap.get("objectTypeList"); // old style
            if (obj == null) {
                final List<String> defaultList = new ArrayList<String>();
                defaultList.add(DfConnectionProperties.OBJECT_TYPE_TABLE);
                defaultList.add(DfConnectionProperties.OBJECT_TYPE_VIEW);
                return defaultList;
            }
        }
        return castToList(obj, "additionalDropMapList.objectTypeTargetList");
    }

    public Connection createAdditionalDropConnection(Map<String, Object> additionalDropMap) {
        final String driver = getDatabaseProperties().getDatabaseDriver();
        String url = getAdditionalDropUrl(additionalDropMap);
        url = url != null && url.trim().length() > 0 ? url : getDatabaseProperties().getDatabaseUrl();
        String user = getAdditionalDropUser(additionalDropMap);
        String password;
        if (user != null && user.trim().length() > 0) {
            password = getAdditionalDropPassword(additionalDropMap);
            if (password == null || password.trim().length() == 0) {
                String msg = "The password is required when the user is specified:";
                msg = msg + " user=" + user + " additionalDropMap=" + additionalDropMap;
                throw new DfIllegalPropertySettingException(msg);
            }
        } else {
            user = getDatabaseProperties().getDatabaseUser();
            password = getDatabaseProperties().getDatabasePassword();
        }
        final Properties prop = getAdditionalDropPropertiesMap(additionalDropMap);
        Properties info = new Properties();
        info.putAll(prop);
        info.put("user", user);
        info.put("password", password);
        _log.info("...Creating a connection for additional drop");
        return createConnection(driver, url, getAdditionalDropSchema(additionalDropMap), info);
    }

    // ===================================================================================
    //                                                                 Application PlaySql
    //                                                                 ===================
    public String getApplicationPlaySqlDirectory() {
        return getProperty("applicationPlaySqlDirectory", null, getReplaceSchemaDefinitionMap());
    }

    public List<File> getApplicationReplaceSchemaSqlFileList() {
        final String directoryPath = getApplicationPlaySqlDirectory();
        if (directoryPath == null) {
            return DfCollectionUtil.emptyList();
        }
        return doGetSchemaSqlFileList(directoryPath, getReplaceSchemaSqlTitle());
    }

    public List<File> getAppcalitionTakeFinallySqlFileList() {
        final String directoryPath = getApplicationPlaySqlDirectory();
        if (directoryPath == null) {
            return DfCollectionUtil.emptyList();
        }
        return doGetSchemaSqlFileList(directoryPath, getTakeFinallySqlTitle());
    }

    // ===================================================================================
    //                                                        Suppress Initializing Schema
    //                                                        ============================
    public boolean isSuppressTruncateTable() {
        return isProperty("isSuppressTruncateTable", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropForeignKey() {
        return isProperty("isSuppressDropForeignKey", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropTable() {
        return isProperty("isSuppressDropTable", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropSequence() {
        return isProperty("isSuppressDropSequence", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropProcedure() {
        return isProperty("isSuppressDropProcedure", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropDBLink() {
        return isProperty("isSuppressDropDBLink", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                           Migration
    //                                                                           =========
    protected String getMigrationDirectory() {
        final String playSqlDirectory = getReplaceSchemaPlaySqlDirectory();
        return playSqlDirectory + "/migration";
    }

    public String getMigrationAlterNGMark() {
        final String alterDir = getMigrationAlterDirectory();
        return alterDir + "/alter-NG.dfmark";
    }

    public boolean hasMigrationAlterNGMark() {
        final File alterDir = new File(getMigrationAlterNGMark());
        return alterDir.exists();
    }

    public String getMigrationPreviousNGMark() {
        final String alterDir = getMigrationAlterDirectory();
        return alterDir + "/previous-NG.dfmark";
    }

    public boolean hasMigrationPreviousNGMark() {
        final File alterDir = new File(getMigrationPreviousNGMark());
        return alterDir.exists();
    }

    // -----------------------------------------------------
    //                                        Alter Resource
    //                                        --------------
    public String getMigrationAlterDirectory() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/alter";
    }

    protected List<File> _migrationAlterSqlFileList;

    public List<File> getMigrationAlterSqlFileList() {
        if (_migrationAlterSqlFileList != null) {
            return _migrationAlterSqlFileList;
        }
        final File alterDir = new File(getMigrationAlterDirectory());
        if (alterDir.exists()) {
            final File[] sqlFiles = alterDir.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    final String pureName = file.getName();
                    return pureName.startsWith("alter") && pureName.endsWith(".sql");
                }
            });
            _migrationAlterSqlFileList = DfCollectionUtil.newArrayList(sqlFiles);
        } else {
            _migrationAlterSqlFileList = DfCollectionUtil.emptyList();
        }
        return _migrationAlterSqlFileList;
    }

    public boolean hasMigrationAlterSqlResource() {
        return !getMigrationAlterSqlFileList().isEmpty();
    }

    // -----------------------------------------------------
    //                                       Create Resource
    //                                       ---------------
    public String getMigrationCreateDirectory() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/create";
    }

    protected List<File> _migrationCreateSchemaSqlFileList;

    public List<File> getMigrationCreateSchemaSqlFileList() {
        if (_migrationCreateSchemaSqlFileList != null) {
            return _migrationCreateSchemaSqlFileList;
        }
        final String directoryPath = getMigrationCreateDirectory();
        final String sqlTitle = getReplaceSchemaSqlTitle();
        _migrationCreateSchemaSqlFileList = doGetSchemaSqlFileList(directoryPath, sqlTitle);
        return _migrationCreateSchemaSqlFileList;
    }

    protected List<File> _migrationTakeFinallySqlFileList;

    public List<File> getMigrationTakeFinallySqlFileList() {
        if (_migrationTakeFinallySqlFileList != null) {
            return _migrationTakeFinallySqlFileList;
        }
        final String directoryPath = getMigrationCreateDirectory();
        final String sqlTitle = getTakeFinallySqlTitle();
        _migrationTakeFinallySqlFileList = doGetSchemaSqlFileList(directoryPath, sqlTitle);
        return _migrationTakeFinallySqlFileList;
    }

    // -----------------------------------------------------
    //                                      History Resource
    //                                      ----------------
    public String getMigrationHistoryDirectory() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/history";
    }

    // -----------------------------------------------------
    //                                       Schema Resource
    //                                       ---------------
    public String getMigrationSchemaXml() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/schema/migration-schema.xml";
    }

    public String getMigrationHistoryFile() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/schema/migration-history.diffmap";
    }

    // -----------------------------------------------------
    //                                    Temporary Resource
    //                                    ------------------
    public String getMigrationTemporaryDirectory() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/tmp";
    }

    public String getMigrationTemporaryPreviousDirectory() {
        final String baseDirectory = getMigrationTemporaryDirectory();
        return baseDirectory + "/previous";
    }

    // ===================================================================================
    //                                                                        Other Closet
    //                                                                        ============
    public boolean isErrorContinue() { // It's closet!
        return isProperty("isErrorContinue", true, getReplaceSchemaDefinitionMap());
    }

    /**
     * @return The process command of call-back for before-take-finally. (NullAllowed)
     */
    public String getBeforeTakeFinally() { // It's closet!
        return (String) getReplaceSchemaDefinitionMap().get("beforeTakeFinally");
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}