package org.seasar.dbflute.properties;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.DfUrlAnalyzer;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.factory.DfUrlAnalyzerFactory;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.properties.assistant.DfConnectionProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public final class DfDatabaseProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfDatabaseProperties.class);
    public static final String NO_NAME_SCHEMA = "$$NoNameSchema$$"; // basically for MySQL

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DatabaseInfo _databaseInfo = new DatabaseInfo();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param prop Properties. (NotNull)
     */
    public DfDatabaseProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                     Connection Info
    //                                                                     ===============

    public String getDatabaseDriver() {
        return _databaseInfo.getDatabaseDriver();
    }

    public String getDatabaseUrl() {
        return _databaseInfo.getDatabaseUrl();
    }

    protected String _mainCatalog = null;
    protected boolean _catalogDone = false;

    public String getDatabaseCatalog() { // as main catalog
        if (_catalogDone) {
            return _mainCatalog;
        }
        _catalogDone = true;

        // first
        String catalog = _databaseInfo.getDatabaseCatalog(); // It's closet!
        if (Srl.is_Null_or_TrimmedEmpty(catalog)) {
            // second
            final DfUrlAnalyzerFactory factory = new DfUrlAnalyzerFactory(getBasicProperties(), getDatabaseUrl());
            final DfUrlAnalyzer analyzer = factory.createAnalyzer();
            final String extracted = analyzer.extractCatalog();
            catalog = Srl.is_NotNull_and_NotTrimmedEmpty(extracted) ? extracted : null;
        }
        _mainCatalog = filterDatabaseCatalog(catalog);
        return _mainCatalog;
    }

    protected String filterDatabaseCatalog(String catalog) {
        if (isDatabaseH2()) {
            if (Srl.is_NotNull_and_NotTrimmedEmpty(catalog)) {
                catalog = catalog.toUpperCase();
            }
        }
        return catalog;
    }

    protected UnifiedSchema _mainSchema = null;

    public UnifiedSchema getDatabaseSchema() { // as main schema
        if (_mainSchema != null) {
            return _mainSchema;
        }
        String schema = _databaseInfo.getDatabaseSchema();
        schema = filterDatabaseSchema(schema);
        _mainSchema = createAsMainSchema(getDatabaseCatalog(), schema);
        return _mainSchema;
    }

    protected String filterDatabaseSchema(String schema) {
        if (isDatabasePostgreSQL()) {
            if (Srl.is_Null_or_TrimmedEmpty(schema)) {
                schema = "public";
            }
        } else if (isDatabaseOracle()) {
            if (Srl.is_NotNull_and_NotTrimmedEmpty(schema)) {
                schema = schema.toUpperCase();
            }
        } else if (isDatabaseDB2()) {
            if (Srl.is_NotNull_and_NotTrimmedEmpty(schema)) {
                schema = schema.toUpperCase();
            }
        } else if (isDatabaseH2()) {
            if (Srl.is_Null_or_TrimmedEmpty(schema)) {
                schema = "PUBLIC";
            }
        } else if (isDatabaseDerby()) {
            if (Srl.is_NotNull_and_NotTrimmedEmpty(schema)) {
                schema = schema.toUpperCase();
            }
        }
        return schema;
    }

    protected UnifiedSchema createAsMainSchema(String catalog, String schema) {
        return UnifiedSchema.createAsMainSchema(catalog, schema);
    }

    public String getDatabaseUser() {
        return _databaseInfo.getDatabaseUser();
    }

    public String getDatabasePassword() {
        return _databaseInfo.getDatabasePassword();
    }

    public boolean isDifferentUserSchema() {
        final String databaseUser = getDatabaseUser();
        final UnifiedSchema databaseSchema = getDatabaseSchema();
        return !databaseUser.equalsIgnoreCase(databaseSchema.getPureSchema());
    }

    public List<UnifiedSchema> getTargetSchemaList() {
        final List<UnifiedSchema> targetSchemaList = new ArrayList<UnifiedSchema>();
        final UnifiedSchema mainSchema = getDatabaseSchema();
        targetSchemaList.add(mainSchema);
        targetSchemaList.addAll(getAdditionalSchemaList());
        return targetSchemaList;
    }

    // ===================================================================================
    //                                                                         Option Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                 Connection Properties
    //                                 ---------------------
    protected Properties _databaseConnectionProperties;

    public Properties getDatabaseConnectionProperties() {
        if (_databaseConnectionProperties != null) {
            return _databaseConnectionProperties;
        }
        _databaseConnectionProperties = _databaseInfo.getDatabaseConnectionProperties();
        return _databaseConnectionProperties;
    }

    // -----------------------------------------------------
    //                               Object Type Target List
    //                               -----------------------
    protected List<String> _objectTypeTargetList;

    public List<String> getObjectTypeTargetList() {
        if (_objectTypeTargetList != null) {
            return _objectTypeTargetList;
        }
        final String key = "objectTypeTargetList";
        _objectTypeTargetList = getVairousStringList(key, getDefaultObjectTypeTargetList());
        return _objectTypeTargetList;
    }

    public boolean hasObjectTypeSynonym() {
        return DfConnectionProperties.hasObjectTypeSynonym(getObjectTypeTargetList());
    }

    protected List<String> getDefaultObjectTypeTargetList() {
        final List<Object> defaultList = new ArrayList<Object>();
        defaultList.add(DfConnectionProperties.OBJECT_TYPE_TABLE);
        defaultList.add(DfConnectionProperties.OBJECT_TYPE_VIEW);
        final List<String> resultList = new ArrayList<String>();
        final List<Object> listProp = listProp("torque.database.type.list", defaultList); // old style
        for (Object object : listProp) {
            resultList.add((String) object);
        }
        return resultList;
    }

    // -----------------------------------------------------
    //                                     Table Except List
    //                                     -----------------
    protected List<String> _tableExceptList;

    public List<String> getTableExceptList() { // for main schema
        if (_tableExceptList != null) {
            return _tableExceptList;
        }
        final List<String> plainList = getVairousStringList("tableExceptList");
        final List<String> resultList;
        if (!plainList.isEmpty()) {
            resultList = plainList;
        } else {
            resultList = new ArrayList<String>();
            final List<Object> listProp = listProp("torque.table.except.list", DEFAULT_EMPTY_LIST);
            for (Object object : listProp) {
                resultList.add((String) object);
            }
        }
        _tableExceptList = resultList;
        return _tableExceptList;
    }

    // -----------------------------------------------------
    //                                     Table Target List
    //                                     -----------------
    protected List<String> _tableTargetList;

    public List<String> getTableTargetList() { // for main schema
        if (_tableTargetList != null) {
            return _tableTargetList;
        }
        final List<String> plainList = getVairousStringList("tableTargetList");
        final List<String> resultList;
        if (!plainList.isEmpty()) {
            resultList = plainList;
        } else {
            resultList = new ArrayList<String>();
            final List<Object> listProp = listProp("torque.table.target.list", DEFAULT_EMPTY_LIST);
            for (Object object : listProp) {
                resultList.add((String) object);
            }
        }
        _tableTargetList = resultList;
        return _tableTargetList;
    }

    // -----------------------------------------------------
    //                                     Column Except Map
    //                                     -----------------
    protected Map<String, List<String>> _columnExceptMap;

    public Map<String, List<String>> getColumnExceptMap() { // for main schema
        if (_columnExceptMap != null) {
            return _columnExceptMap;
        }
        final List<String> oldStyleList = getVairousStringList("columnExceptList");
        if (!oldStyleList.isEmpty()) {
            String msg = "You should migrate 'columnExceptList' to 'columnExceptMap'";
            msg = msg + " in databaseInfoMap.dfprop: columnExceptList=" + oldStyleList;
            throw new IllegalStateException(msg);
        }
        final Map<String, List<String>> columnExceptMap = StringKeyMap.createAsFlexible();
        final Map<String, Object> keyMap = getVairousStringKeyMap("columnExceptMap");
        if (keyMap.isEmpty()) {
            return columnExceptMap;
        }
        final Set<Entry<String, Object>> entrySet = keyMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String tableName = entry.getKey();
            final Object obj = entry.getValue();
            if (!(obj instanceof List<?>)) {
                String msg = "The type of element in the property 'columnExceptMap' should be List:";
                msg = msg + " type=" + DfTypeUtil.toClassTitle(obj) + " value=" + obj;
                throw new DfIllegalPropertyTypeException(msg);
            }
            @SuppressWarnings("unchecked")
            final List<String> columnList = (List<String>) obj;
            columnExceptMap.put(tableName, columnList);
        }
        _columnExceptMap = columnExceptMap;
        return _columnExceptMap;
    }

    // ===================================================================================
    //                                                                   Additional Schema
    //                                                                   =================
    // -----------------------------------------------------
    //                                 Additional Schema Map
    //                                 ---------------------
    // key is unique-schema
    protected Map<String, DfAdditionalSchemaInfo> _additionalSchemaMap;

    protected void assertOldStyleAdditionalSchema() {
        // Check old style existence
        final Object oldStyle = getVariousObject("additionalSchemaList");
        if (oldStyle != null) {
            String msg = "The property 'additionalSchemaList' have been unsupported!";
            msg = msg + " Please use the property 'additionalSchemaMap'.";
            throw new IllegalStateException(msg);
        }
    }

    protected Map<String, DfAdditionalSchemaInfo> getAdditionalSchemaMap() {
        if (_additionalSchemaMap != null) {
            return _additionalSchemaMap;
        }
        assertOldStyleAdditionalSchema();
        _additionalSchemaMap = StringKeyMap.createAsCaseInsensitive();
        final Map<String, Object> additionalSchemaMap = getVairousStringKeyMap("additionalSchemaMap");
        if (additionalSchemaMap == null) {
            return _additionalSchemaMap;
        }
        final Set<Entry<String, Object>> entrySet = additionalSchemaMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String identifiedSchema = entry.getKey();
            final Object obj = entry.getValue();
            if (obj == null) {
                String msg = "The value of schema in the property 'additionalSchemaMap' should be required:";
                msg = msg + " identifiedSchema=" + identifiedSchema;
                msg = msg + " additionalSchemaMap=" + additionalSchemaMap;
                throw new DfRequiredPropertyNotFoundException(msg);
            }
            if (!(obj instanceof Map<?, ?>)) {
                String msg = "The type of schema value in the property 'additionalSchemaMap' should be Map:";
                msg = msg + " type=" + DfTypeUtil.toClassTitle(obj) + " value=" + obj;
                throw new DfIllegalPropertyTypeException(msg);
            }
            @SuppressWarnings("unchecked")
            final Map<String, Object> elementMap = (Map<String, Object>) obj;

            final DfAdditionalSchemaInfo info = new DfAdditionalSchemaInfo();
            final String catalog;
            final boolean explicitCatalog;
            if (identifiedSchema.contains(".")) {
                catalog = Srl.substringFirstFront(identifiedSchema, ".");
                explicitCatalog = true;
            } else {
                catalog = getDatabaseCatalog(); // as main catalog
                explicitCatalog = false;
            }
            final String schema = filterDatabaseSchema(Srl.substringFirstRear(identifiedSchema, "."));
            final UnifiedSchema unifiedSchema = createAsAdditionalSchema(catalog, schema, explicitCatalog);
            info.setUnifiedSchema(unifiedSchema);
            setupAdditionalSchemaObjectTypeTargetList(info, elementMap);
            setupAdditionalSchemaTableExceptList(info, elementMap);
            setupAdditionalSchemaTableTargetList(info, elementMap);
            setupAdditionalSchemaColumnExceptList(info, elementMap);
            info.setSuppressCommonColumn(isProperty("isSuppressCommonColumn", false, elementMap));
            info.setSuppressProcedure(isProperty("isSuppressProcedure", false, elementMap));
            _additionalSchemaMap.put(unifiedSchema.getIdentifiedSchema(), info);
        }
        return _additionalSchemaMap;
    }

    protected UnifiedSchema createAsAdditionalSchema(String catalog, String schema, boolean explicitCatalog) {
        return UnifiedSchema.createAsAdditionalSchema(catalog, schema, explicitCatalog);
    }

    // -----------------------------------------------------
    //                              Additional Schema Option
    //                              ------------------------
    protected void setupAdditionalSchemaObjectTypeTargetList(DfAdditionalSchemaInfo info, Map<String, Object> elementMap) {
        final Object obj = elementMap.get("objectTypeTargetList");
        if (obj == null) {
            @SuppressWarnings("unchecked")
            final List<String> objectTypeTargetList = Collections.EMPTY_LIST;
            info.setObjectTypeTargetList(objectTypeTargetList);
        } else if (!(obj instanceof List<?>)) {
            String msg = "The type of objectTypeTargetList in the property 'additionalSchemaMap' should be List:";
            msg = msg + " type=" + DfTypeUtil.toClassTitle(obj) + " value=" + obj;
            throw new DfIllegalPropertyTypeException(msg);
        } else {
            @SuppressWarnings("unchecked")
            final List<String> objectTypeTargetList = (List<String>) obj;
            info.setObjectTypeTargetList(objectTypeTargetList);
        }
    }

    protected void setupAdditionalSchemaTableExceptList(DfAdditionalSchemaInfo info, Map<String, Object> elementMap) {
        final Object obj = elementMap.get("tableExceptList");
        if (obj == null) {
            final List<String> tableExceptList = DfCollectionUtil.emptyList();
            info.setTableExceptList(tableExceptList);
        } else if (!(obj instanceof List<?>)) {
            String msg = "The type of tableExceptList in the property 'additionalSchemaMap' should be List:";
            msg = msg + " type=" + DfTypeUtil.toClassTitle(obj) + " value=" + obj;
            throw new DfIllegalPropertyTypeException(msg);
        } else {
            @SuppressWarnings("unchecked")
            final List<String> tableExceptList = (List<String>) obj;
            info.setTableExceptList(tableExceptList);
        }
    }

    protected void setupAdditionalSchemaTableTargetList(DfAdditionalSchemaInfo info, Map<String, Object> elementMap) {
        final Object obj = elementMap.get("tableTargetList");
        if (obj == null) {
            final List<String> tableTargetList = DfCollectionUtil.emptyList();
            info.setTableTargetList(tableTargetList);
        } else if (!(obj instanceof List<?>)) {
            String msg = "The type of tableTargetList in the property 'additionalSchemaMap' should be List:";
            msg = msg + " type=" + DfTypeUtil.toClassTitle(obj) + " value=" + obj;
            throw new DfIllegalPropertyTypeException(msg);
        } else {
            @SuppressWarnings("unchecked")
            final List<String> tableTargetList = (List<String>) obj;
            info.setTableTargetList(tableTargetList);
        }
    }

    protected void setupAdditionalSchemaColumnExceptList(DfAdditionalSchemaInfo info, Map<String, Object> elementMap) {
        final Object obj = elementMap.get("columnExceptMap");
        if (obj == null) {
            final Map<String, List<String>> columnExceptMap = DfCollectionUtil.emptyMap();
            info.setColumnExceptMap(columnExceptMap);
        } else if (!(obj instanceof Map<?, ?>)) {
            String msg = "The type of columnExceptMap in the property 'additionalSchemaMap' should be Map:";
            msg = msg + " type=" + DfTypeUtil.toClassTitle(obj) + " value=" + obj;
            throw new DfIllegalPropertyTypeException(msg);
        } else {
            @SuppressWarnings("unchecked")
            final Map<String, List<String>> columnExceptMap = (Map<String, List<String>>) obj;
            final Map<String, List<String>> flexibleMap = StringKeyMap.createAsFlexible();
            flexibleMap.putAll(columnExceptMap);
            info.setColumnExceptMap(flexibleMap);
        }
    }

    // -----------------------------------------------------
    //                            Additional Schema Accessor
    //                            --------------------------
    public List<UnifiedSchema> getAdditionalSchemaList() {
        final Map<String, DfAdditionalSchemaInfo> schemaMap = getAdditionalSchemaMap();
        final Set<Entry<String, DfAdditionalSchemaInfo>> entrySet = schemaMap.entrySet();
        final List<UnifiedSchema> schemaList = new ArrayList<UnifiedSchema>();
        for (Entry<String, DfAdditionalSchemaInfo> entry : entrySet) {
            final DfAdditionalSchemaInfo info = entry.getValue();
            final UnifiedSchema unifiedSchema = info.getUnifiedSchema();
            schemaList.add(unifiedSchema);
        }
        return schemaList;
    }

    public boolean hasAdditionalSchema() {
        return !getAdditionalSchemaMap().isEmpty();
    }

    public boolean hasCatalogAdditionalSchema() {
        final List<UnifiedSchema> additionalSchemaList = getAdditionalSchemaList();
        for (UnifiedSchema unifiedSchema : additionalSchemaList) {
            if (unifiedSchema.isCatalogAdditionalSchema()) {
                return true;
            }
        }
        return false;
    }

    public DfAdditionalSchemaInfo getAdditionalSchemaInfo(UnifiedSchema unifiedSchema) {
        if (unifiedSchema == null) {
            return null;
        }
        final Map<String, DfAdditionalSchemaInfo> map = getAdditionalSchemaMap();
        final String identifiedSchema = unifiedSchema.getIdentifiedSchema();
        return map.get(identifiedSchema);
    }

    // ===================================================================================
    //                                                                   VariousMap Helper
    //                                                                   =================
    @SuppressWarnings("unchecked")
    protected List<String> getVairousStringList(String key) {
        return getVairousStringList(key, Collections.EMPTY_LIST);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getVairousStringList(String key, List<String> defaultList) {
        final Object value = getVariousObject(key);
        if (value == null) {
            return defaultList != null ? defaultList : Collections.EMPTY_LIST;
        }
        assertVariousPropertyList(key, value);
        return (List<String>) value;
    }

    protected void assertVariousPropertyList(String name, Object value) {
        if (!(value instanceof List<?>)) {
            String msg = "The property '" + name + "' should be List: " + value;
            throw new IllegalStateException(msg);
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getVairousStringKeyMap(String key) {
        return getVairousStringKeyMap(key, Collections.EMPTY_MAP);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getVairousStringKeyMap(String key, Map<String, Object> defaultMap) {
        final Object value = getVariousObject(key);
        if (value == null) {
            return defaultMap != null ? defaultMap : Collections.EMPTY_MAP;
        }
        assertVariousPropertyMap(key, value);
        return (Map<String, Object>) value;
    }

    protected void assertVariousPropertyMap(String name, Object value) {
        if (!(value instanceof Map<?, ?>)) {
            String msg = "The property '" + name + "' should be Map: " + value;
            throw new IllegalStateException(msg);
        }
    }

    protected Object getVariousObject(String key) {
        final Map<String, Object> variousMap = _databaseInfo.getDatabaseVariousMap();
        return variousMap.get(key);
    }

    // ===================================================================================
    //                                                                  Information Object
    //                                                                  ==================
    public class DatabaseInfo {

        private static final String KEY_DRIVER = "driver";
        private static final String KEY_URL = "url";
        private static final String KEY_CATALOG = "catalog";
        private static final String KEY_SCHEMA = "schema";
        private static final String KEY_USER = "user";
        private static final String KEY_PASSWORD = "password";
        private static final String KEY_PROPERTIES_MAP = "propertiesMap";
        private static final String KEY_VARIOUS_MAP = "variousMap";

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

        public String getDatabaseUrl() {
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
                if (equalsKeys(key, KEY_DRIVER, KEY_URL, KEY_CATALOG, KEY_SCHEMA, KEY_USER, KEY_PASSWORD,
                        KEY_PROPERTIES_MAP, KEY_VARIOUS_MAP)) {
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

        public String getDatabaseCatalog() {
            initializeDatabaseInfoMap();
            final String key = KEY_CATALOG;
            final String databaseInfoElement = getDatabaseInfoElement(key);
            if (databaseInfoElement != null) {
                return databaseInfoElement;
            }
            return stringProp("torque.database.catalog", "");
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

        public Properties getDatabaseConnectionProperties() {
            initializeDatabaseInfoMap();
            final String key = KEY_PROPERTIES_MAP;
            final Map<String, String> propertiesMap = getDatabaseInfoElementAsPropertiesMap(key);
            final Properties props = new Properties();
            if (propertiesMap.isEmpty()) {
                return props;
            }
            final Set<String> keySet = propertiesMap.keySet();
            for (String propKey : keySet) {
                final String propValue = propertiesMap.get(propKey);
                props.setProperty(propKey, propValue);
            }
            return props;
        }

        public Map<String, Object> getDatabaseVariousMap() {
            initializeDatabaseInfoMap();
            final String key = KEY_VARIOUS_MAP;
            final Map<String, Object> variousMap = getDatabaseInfoElementAsVariousMap(key);
            return variousMap;
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

        @SuppressWarnings("unchecked")
        protected Map<String, String> getDatabaseInfoElementAsPropertiesMap(final String key) {
            if (_databaseInfoMap != null) {
                if (!_databaseInfoMap.containsKey(key)) {
                    return new LinkedHashMap<String, String>();
                }
                final Map<String, String> valueList = (Map<String, String>) _databaseInfoMap.get(key);
                return valueList != null ? valueList : new LinkedHashMap<String, String>();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        protected Map<String, Object> getDatabaseInfoElementAsVariousMap(final String key) {
            if (_databaseInfoMap != null) {
                if (!_databaseInfoMap.containsKey(key)) {
                    return new LinkedHashMap<String, Object>();
                }
                final Map<String, Object> valueList = (Map<String, Object>) _databaseInfoMap.get(key);
                return valueList != null ? valueList : new LinkedHashMap<String, Object>();
            }
            return null;
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    public boolean isDatabaseMySQL() {
        return getBasicProperties().isDatabaseMySQL();
    }

    public boolean isDatabasePostgreSQL() {
        return getBasicProperties().isDatabasePostgreSQL();
    }

    public boolean isDatabaseOracle() {
        return getBasicProperties().isDatabaseOracle();
    }

    public boolean isDatabaseDB2() {
        return getBasicProperties().isDatabaseDB2();
    }

    public boolean isDatabaseSQLServer() {
        return getBasicProperties().isDatabaseSQLServer();
    }

    public boolean isDatabaseH2() {
        return getBasicProperties().isDatabaseH2();
    }

    public boolean isDatabaseDerby() {
        return getBasicProperties().isDatabaseDerby();
    }

    public boolean isDatabaseSQLite() {
        return getBasicProperties().isDatabaseSQLite();
    }

    public boolean isDatabaseMSAccess() {
        return getBasicProperties().isDatabaseMSAccess();
    }

    // ===================================================================================
    //                                                                   Connection Helper
    //                                                                   =================
    public Connection createMainSchemaConnection() {
        final String driver = getDatabaseDriver();
        final String url = getDatabaseUrl();
        final UnifiedSchema schema = getDatabaseSchema();
        final String user = getDatabaseUser();
        final String password = getDatabasePassword();
        _log.info("...Creating a connection to main schema");
        return createConnection(driver, url, schema, user, password);
    }
}