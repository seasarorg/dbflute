package org.seasar.dbflute.properties;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.helper.process.SystemScript;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.DfUrlAnalyzer;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.factory.DfUrlAnalyzerFactory;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfLoadedDataInfo;
import org.seasar.dbflute.properties.assistant.DfConnectionProperties;
import org.seasar.dbflute.properties.assistant.DfReplaceSchemaResourceFinder;
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
    public String getPlaySqlDirectory() {
        return getPlaySqlDirPureName(); // path relative to DBFlute client
    }

    public String getPlaySqlDirPureName() {
        return "playsql";
    }

    public String getPlaySqlDirSymbol() {
        return getPlaySqlDirPureName();
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
        return doGetSchemaSqlFileList(getPlaySqlDirectory(), getReplaceSchemaSqlTitle());
    }

    public Map<String, File> getReplaceSchemaSqlFileMap() {
        return doGetSchemaSqlFileMap(getReplaceSchemaSqlFileList());
    }

    public Map<String, File> doGetSchemaSqlFileMap(List<File> sqlFileList) {
        final Map<String, File> resultMap = new LinkedHashMap<String, File>();
        for (File sqlFile : sqlFileList) {
            // Schema SQL files are located in the same directory
            final String uniqueKey = sqlFile.getName();
            resultMap.put(uniqueKey, sqlFile);
        }
        return resultMap;
    }

    protected List<File> doGetSchemaSqlFileList(String targetDir, String title) {
        return doGetResourceFileList(targetDir, title, ".sql");
    }

    protected List<File> doGetResourceFileList(String targetDir, String prefix, String... suffixes) {
        final DfReplaceSchemaResourceFinder finder = new DfReplaceSchemaResourceFinder();
        finder.addPrefix(prefix);
        for (String suffix : suffixes) {
            finder.addSuffix(suffix);
        }
        return finder.findResourceFileList(targetDir);
    }

    // -----------------------------------------------------
    //                                          Take Finally
    //                                          ------------
    protected String getTakeFinallySqlFile() {
        return getPlaySqlDirectory() + "/take-finally.sql";
    }

    public String getTakeFinallySqlTitle() {
        return "take-finally";
    }

    public List<File> getTakeFinallySqlFileList() {
        final String targetDir = getPlaySqlDirectory();
        return doGetSchemaSqlFileList(targetDir, getTakeFinallySqlTitle());
    }

    public Map<String, File> getTakeFinallySqlFileMap() {
        return doGetSchemaSqlFileMap(getTakeFinallySqlFileList());
    }

    // ===================================================================================
    //                                                                         Schema Data
    //                                                                         ===========
    public String getCommonDataDir(String baseDir, String typeName) {
        return baseDir + "/data/common/" + typeName;
    }

    public String getLoadTypeDataDir(String baseDir, String loadType, String typeName) {
        return baseDir + "/data/" + loadType + "/" + typeName;
    }

    protected List<File> doGetCommonDataFileList(String baseDir, String typeName) { // contains data-prop
        return doGetAnyTypeDataFileList(baseDir, DfLoadedDataInfo.COMMON_LOAD_TYPE, typeName);
    }

    protected List<File> doGetLoadTypeDataFileList(String baseDir, String typeName) { // contains data-prop
        return doGetAnyTypeDataFileList(baseDir, getDataLoadingType(), typeName);
    }

    protected List<File> doGetAnyTypeDataFileList(String baseDir, String loadType, String typeName) { // contains data-prop
        final String targetDir = getLoadTypeDataDir(baseDir, loadType, typeName);
        final String firstxlsFileType = DfLoadedDataInfo.FIRSTXLS_FILE_TYPE;
        final String xlsFileType = DfLoadedDataInfo.XLS_FILE_TYPE;
        final String suffix = "." + (typeName.equals(firstxlsFileType) ? xlsFileType : typeName);
        if (Srl.equalsPlain(typeName, firstxlsFileType, xlsFileType)) {
            return doGetDataFileList(targetDir, suffix, false);
        } else { // delimiter data (contains one level nested)
            return doGetDataFileList(targetDir, suffix, true);
        }
    }

    protected List<File> doGetDataFileList(String targetDir, String suffix, boolean oneLevelNested) {
        final DfReplaceSchemaResourceFinder finder = new DfReplaceSchemaResourceFinder();
        finder.addSuffix(suffix);
        finder.addSuffix(".dataprop"); // contains data-prop
        if (oneLevelNested) {
            finder.containsOneLevelNested();
        }
        return finder.findResourceFileList(targetDir);
    }

    // non-ApplicationPlaySql below

    protected String getMainCurrentLoadTypeDataDir() {
        final String playSqlDirectory = getPlaySqlDirectory();
        final String dataLoadingType = getDataLoadingType();
        return playSqlDirectory + "/data/" + dataLoadingType;
    }

    public String getMainCurrentLoadTypeDataDir(String fileType) {
        return getMainCurrentLoadTypeDataDir() + "/" + fileType;
    }

    public String getMainCurrentLoadTypeFirstXlsDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/firstxls";
    }

    public String getMainCurrentLoadTypeTsvDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/tsv";
    }

    public String getMainCurrentLoadTypeTsvUTF8DataDir() {
        return getMainCurrentLoadTypeTsvDataDir() + "/UTF-8";
    }

    public String getMainCurrentLoadTypeCsvDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/csv";
    }

    public String getMainCurrentLoadTypeCsvUTF8DataDir() {
        return getMainCurrentLoadTypeCsvDataDir() + "/csv/UTF-8";
    }

    public String getMainCurrentLoadTypeXlsDataDir() {
        return getMainCurrentLoadTypeDataDir() + "/xls";
    }

    // basically for AlterCheck below

    protected Map<String, File> _schemaDataMap;

    public Map<String, File> getSchemaDataAllMap() { // contains data-prop
        if (_schemaDataMap != null) {
            return _schemaDataMap;
        }
        final Map<String, File> dataMap = new LinkedHashMap<String, File>();
        setupSchemaDataMap(dataMap, "common/firstxls", getCommonFirstXlsDataList());
        setupSchemaDataMap(dataMap, "common/csv", getCommonCsvDataList());
        setupSchemaDataMap(dataMap, "common/tsv", getCommonTsvDataList());
        setupSchemaDataMap(dataMap, "common/xls", getCommonXlsDataList());
        setupSchemaDataMap(dataMap, "loadtype/firstxls", getLoadTypeFirstXlsDataList());
        setupSchemaDataMap(dataMap, "loadtype/csv", getLoadTypeCsvDataList());
        setupSchemaDataMap(dataMap, "loadtype/tsv", getLoadTypeTsvDataList());
        setupSchemaDataMap(dataMap, "loadtype/xls", getLoadTypeXlsDataList());
        _schemaDataMap = dataMap;
        return _schemaDataMap;
    }

    protected void setupSchemaDataMap(Map<String, File> dataMap, String keyBase, List<File> dataFileList) {
        for (File dataFile : dataFileList) {
            dataMap.put(keyBase + "/" + dataFile.getName(), dataFile);
        }
    }

    // contains data-prop below

    public List<File> getCommonFirstXlsDataList() {
        return doGetCommonDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.FIRSTXLS_FILE_TYPE);
    }

    public List<File> getCommonXlsDataList() {
        return doGetCommonDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.XLS_FILE_TYPE);
    }

    public List<File> getCommonTsvDataList() {
        return doGetCommonDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.TSV_FILE_TYPE);
    }

    public List<File> getCommonCsvDataList() {
        return doGetCommonDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.CSV_FILE_TYPE);
    }

    public List<File> getLoadTypeFirstXlsDataList() {
        return doGetLoadTypeDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.FIRSTXLS_FILE_TYPE);
    }

    public List<File> getLoadTypeXlsDataList() {
        return doGetLoadTypeDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.XLS_FILE_TYPE);
    }

    public List<File> getLoadTypeTsvDataList() {
        return doGetLoadTypeDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.TSV_FILE_TYPE);
    }

    public List<File> getLoadTypeCsvDataList() {
        return doGetLoadTypeDataFileList(getPlaySqlDirectory(), DfLoadedDataInfo.CSV_FILE_TYPE);
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
            _filterVariablesMap = new LinkedHashMap<String, String>();
        }
        setupDefaultFilterVariables(_filterVariablesMap);
        return _filterVariablesMap;
    }

    protected void setupDefaultFilterVariables(Map<String, String> filterVariablesMap) {
        final DfDatabaseProperties prop = getDatabaseProperties();
        filterVariablesMap.put("dfprop.mainCatalog", prop.getDatabaseCatalog());
        filterVariablesMap.put("dfprop.mainSchema", prop.getDatabaseSchema().getPureSchema());
        filterVariablesMap.put("dfprop.mainUser", prop.getDatabaseUser());
        filterVariablesMap.put("dfprop.mainPassword", prop.getDatabasePassword());
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
            final Set<Entry<String, String>> entrySet = filterVariablesMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                final String variableMark = beginMark + entry.getKey() + endMark;
                if (sql.contains(variableMark)) {
                    sql = replaceString(sql, variableMark, entry.getValue());
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
        final String targetDir = getApplicationPlaySqlDirectory();
        if (targetDir == null) {
            return DfCollectionUtil.emptyList();
        }
        return doGetSchemaSqlFileList(targetDir, getReplaceSchemaSqlTitle());
    }

    public List<File> getAppcalitionTakeFinallySqlFileList() {
        final String targetDir = getApplicationPlaySqlDirectory();
        if (targetDir == null) {
            return DfCollectionUtil.emptyList();
        }
        return doGetSchemaSqlFileList(targetDir, getTakeFinallySqlTitle());
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
        final String playSqlDirectory = getPlaySqlDirectory();
        return playSqlDirectory + "/" + getMigrationDirPureName();
    }

    protected String getMigrationDirPureName() {
        return "migration";
    }

    public String getMigrationAlterNGMark() {
        final String alterDir = getMigrationAlterDirectory();
        return alterDir + "/alter-NG.dfmark";
    }

    public boolean hasMigrationAlterNGMark() {
        final File markFile = new File(getMigrationAlterNGMark());
        return markFile.exists();
    }

    public String getMigrationCreateNGMark() {
        final String alterDir = getMigrationCreateDirectory();
        return alterDir + "/create-NG.dfmark";
    }

    public boolean hasMigrationCreateNGMark() {
        final File markFile = new File(getMigrationCreateNGMark());
        return markFile.exists();
    }

    public String getMigrationPreviousNGMark() {
        final String alterDir = getMigrationAlterDirectory();
        return alterDir + "/previous-NG.dfmark";
    }

    public boolean hasMigrationPreviousNGMark() {
        final File markFile = new File(getMigrationPreviousNGMark());
        return markFile.exists();
    }

    // -----------------------------------------------------
    //                                        Alter Resource
    //                                        --------------
    public String getMigrationAlterDirectory() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/alter";
    }

    protected List<File> _migrationAlterSqlFileList;

    public List<File> getMigrationAlterSqlFileList() { // contains script files
        if (_migrationAlterSqlFileList != null) {
            return _migrationAlterSqlFileList;
        }
        final String targetDir = getMigrationAlterDirectory();
        final String sqlTitle = getAlterSchemaSqlTitle();
        final List<String> suffixList = new ArrayList<String>();
        suffixList.add(".sql");
        suffixList.addAll(SystemScript.getSupportedExtList());
        _migrationAlterSqlFileList = doGetResourceFileList(targetDir, sqlTitle, suffixList.toArray(new String[] {}));
        return _migrationAlterSqlFileList;
    }

    public String getAlterSchemaSqlTitle() {
        return "alter-schema";
    }

    public boolean hasMigrationAlterSqlResource() {
        return !getMigrationAlterSqlFileList().isEmpty();
    }

    // -----------------------------------------------------
    //                                       Create Resource
    //                                       ---------------
    public String getMigrationCreateDirectory() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/" + getMigrationCreateDirPureName();
    }

    public String getMigrationCreateDirPureName() {
        return "create";
    }

    public boolean hasMigrationCreateSqlResource() {
        if (!getMigrationReplaceSchemaSqlFileList().isEmpty()) {
            return true;
        }
        if (!getMigrationTakeFinallySqlFileList().isEmpty()) {
            return true;
        }
        if (!getMigrationSchemaDataAllMap().isEmpty()) {
            return true;
        }
        return false;
    }

    public String getMigrationCreateDirSymbol() {
        return getMigrationDirPureName() + "/" + getMigrationCreateDirPureName();
    }

    protected List<File> _migrationReplaceSchemaSqlFileList;

    public List<File> getMigrationReplaceSchemaSqlFileList() {
        if (_migrationReplaceSchemaSqlFileList != null) {
            return _migrationReplaceSchemaSqlFileList;
        }
        final String targetDir = getMigrationCreateDirectory();
        final String sqlTitle = getReplaceSchemaSqlTitle();
        _migrationReplaceSchemaSqlFileList = doGetSchemaSqlFileList(targetDir, sqlTitle);
        return _migrationReplaceSchemaSqlFileList;
    }

    public Map<String, File> getMigrationReplaceSchemaSqlFileMap() {
        return doGetSchemaSqlFileMap(getMigrationReplaceSchemaSqlFileList());
    }

    protected List<File> _migrationTakeFinallySqlFileList;

    public List<File> getMigrationTakeFinallySqlFileList() {
        if (_migrationTakeFinallySqlFileList != null) {
            return _migrationTakeFinallySqlFileList;
        }
        final String targetDir = getMigrationCreateDirectory();
        final String sqlTitle = getTakeFinallySqlTitle();
        _migrationTakeFinallySqlFileList = doGetSchemaSqlFileList(targetDir, sqlTitle);
        return _migrationTakeFinallySqlFileList;
    }

    public Map<String, File> getMigrationTakeFinallySqlFileMap() {
        return doGetSchemaSqlFileMap(getMigrationTakeFinallySqlFileList());
    }

    protected Map<String, File> _migrationSchemaDataMap;

    public Map<String, File> getMigrationSchemaDataAllMap() { // contains data-prop
        if (_migrationSchemaDataMap != null) {
            return _migrationSchemaDataMap;
        }
        final Map<String, File> dataMap = new LinkedHashMap<String, File>();
        setupSchemaDataMap(dataMap, "common/firstxls", getMigrationCommonFirstXlsDataList());
        setupSchemaDataMap(dataMap, "common/csv", getMigrationCommonCsvDataList());
        setupSchemaDataMap(dataMap, "common/tsv", getMigrationCommonTsvDataList());
        setupSchemaDataMap(dataMap, "common/xls", getMigrationCommonXlsDataList());
        setupSchemaDataMap(dataMap, "loadtype/firstxls", getMigrationLoadTypeFirstXlsDataList());
        setupSchemaDataMap(dataMap, "loadtype/csv", getMigrationLoadTypeCsvDataList());
        setupSchemaDataMap(dataMap, "loadtype/tsv", getMigrationLoadTypeTsvDataList());
        setupSchemaDataMap(dataMap, "loadtype/xls", getMigrationLoadTypeXlsDataList());
        _migrationSchemaDataMap = dataMap;
        return _migrationSchemaDataMap;
    }

    // contains data-prop below

    public List<File> getMigrationCommonFirstXlsDataList() {
        return doGetCommonDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.FIRSTXLS_FILE_TYPE);
    }

    public List<File> getMigrationCommonXlsDataList() {
        return doGetCommonDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.XLS_FILE_TYPE);
    }

    public List<File> getMigrationCommonTsvDataList() {
        return doGetCommonDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.TSV_FILE_TYPE);
    }

    public List<File> getMigrationCommonCsvDataList() {
        return doGetCommonDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.CSV_FILE_TYPE);
    }

    public List<File> getMigrationLoadTypeFirstXlsDataList() {
        return doGetLoadTypeDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.FIRSTXLS_FILE_TYPE);
    }

    public List<File> getMigrationLoadTypeXlsDataList() {
        return doGetLoadTypeDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.XLS_FILE_TYPE);
    }

    public List<File> getMigrationLoadTypeTsvDataList() {
        return doGetLoadTypeDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.TSV_FILE_TYPE);
    }

    public List<File> getMigrationLoadTypeCsvDataList() {
        return doGetLoadTypeDataFileList(getMigrationCreateDirectory(), DfLoadedDataInfo.CSV_FILE_TYPE);
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

    public String getMigrationAlterCheckResultDiff() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/schema/alter-check-result.diffmap";
    }

    public String getMigrationChangeOutputResultDiff() {
        final String baseDirectory = getMigrationDirectory();
        return baseDirectory + "/schema/change-output-result.diffmap";
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
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}