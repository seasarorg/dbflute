package org.seasar.dbflute.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;

/**
 * @author jflute
 */
public final class DfDatabaseProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String OBJECT_TYPE_TABLE = "TABLE";
    protected static final String OBJECT_TYPE_VIEW = "VIEW";
    protected static final String OBJECT_TYPE_SYNONYM = "SYNONYM";
    protected static final String OBJECT_TYPE_ALIAS = "ALIAS";

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
    //                                                                          Basic Info
    //                                                                          ==========
    protected DatabaseInfo _databaseInfo = new DatabaseInfo();

    public String getDatabaseDriver() {
        return _databaseInfo.getDatabaseDriver();
    }

    public String getDatabaseUrl() {
        return _databaseInfo.getDatabaseUrl();
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

    public boolean isDifferentUserSchema() {
        final String databaseUser = getDatabaseUser();
        final String databaseSchema = getDatabaseSchema();
        return !databaseUser.equalsIgnoreCase(databaseSchema);
    }

    // -----------------------------------------------------
    //                                 Connection Properties
    //                                 ---------------------
    public Properties getDatabaseConnectionProperties() {
        return _databaseInfo.getDatabaseConnectionProperties();
    }

    // -----------------------------------------------------
    //                               Object Type Target List
    //                               -----------------------
    public List<String> getObjectTypeTargetList() {
        return getVairousStringList("objectTypeTargetList", getDatabaseTypeList());
    }

    public boolean hasObjectTypeSynonym() {
        List<String> objectTypeList = getObjectTypeTargetList();
        for (String objectType : objectTypeList) {
            if (OBJECT_TYPE_SYNONYM.equalsIgnoreCase(objectType)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> getDatabaseTypeList() { // Old Style
        final List<Object> defaultList = new ArrayList<Object>();
        defaultList.add(OBJECT_TYPE_TABLE);
        defaultList.add(OBJECT_TYPE_VIEW);
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
    public List<String> getTableExceptList() { // for main schema
        final List<String> vairousList = getVairousStringList("tableExceptList");
        if (!vairousList.isEmpty()) {
            return vairousList;
        }
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
    public List<String> getTableTargetList() { // for main schema
        final List<String> vairousList = getVairousStringList("tableTargetList");
        if (!vairousList.isEmpty()) {
            return vairousList;
        }
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
    public List<String> getSimpleColumnExceptList() { // for main schema
        final List<String> vairousList = getVairousStringList("columnExceptList");
        if (!vairousList.isEmpty()) {
            return vairousList;
        }
        final List<String> resultList = new ArrayList<String>();
        final List<Object> listProp = listProp("torque.simple.column.except.list", DEFAULT_EMPTY_LIST);
        for (Object object : listProp) {
            resultList.add((String) object);
        }
        return resultList;
    }

    // ===================================================================================
    //                                                                   Additional Schema
    //                                                                   =================
    // -----------------------------------------------------
    //                                 Additional Schema Map
    //                                 ---------------------
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

    public Map<String, DfAdditionalSchemaInfo> getAdditionalSchemaMap() {
        if (_additionalSchemaMap != null) {
            return _additionalSchemaMap;
        }
        assertOldStyleAdditionalSchema();
        _additionalSchemaMap = new LinkedHashMap<String, DfAdditionalSchemaInfo>();
        final Map<String, Object> additionalSchemaMap = getVairousStringKeyMap("additionalSchemaMap");
        if (additionalSchemaMap == null) {
            return _additionalSchemaMap;
        }
        final Set<Entry<String, Object>> entrySet = additionalSchemaMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String schemaName = entry.getKey();
            Object obj = entry.getValue();
            if (obj == null) {
                String msg = "The value of schema in the property 'additionalSchemaMap' should be required:";
                msg = msg + " schema=" + schemaName;
                msg = msg + " additionalSchemaMap=" + additionalSchemaMap;
                throw new DfRequiredPropertyNotFoundException(msg);
            }
            if (!(obj instanceof Map<?, ?>)) {
                String msg = "The type of schema value in the property 'additionalSchemaMap' should be Map:";
                msg = msg + " type=" + (obj != null ? obj.getClass().getSimpleName() : null) + " value=" + obj;
                throw new DfIllegalPropertyTypeException(msg);
            }
            @SuppressWarnings("unchecked")
            final Map<String, Object> elementMap = (Map<String, Object>) obj;

            final DfAdditionalSchemaInfo info = new DfAdditionalSchemaInfo();
            info.setSchemaName(schemaName);

            obj = elementMap.get("objectTypeTargetList");
            if (obj == null) {
                @SuppressWarnings("unchecked")
                final List<String> objectTypeTargetList = Collections.EMPTY_LIST;
                info.setObjectTypeTargetList(objectTypeTargetList);
            } else if (!(obj instanceof List<?>)) {
                String msg = "The type of objectTypeTargetList in the property 'additionalSchemaMap' should be List:";
                msg = msg + " type=" + (obj != null ? obj.getClass().getSimpleName() : null) + " value=" + obj;
                throw new DfIllegalPropertyTypeException(msg);
            } else {
                @SuppressWarnings("unchecked")
                final List<String> objectTypeTargetList = (List<String>) obj;
                info.setObjectTypeTargetList(objectTypeTargetList);
            }

            obj = elementMap.get("tableExceptList");
            if (obj == null) {
                @SuppressWarnings("unchecked")
                final List<String> tableExceptList = Collections.EMPTY_LIST;
                info.setTableExceptList(tableExceptList);
            } else if (!(obj instanceof List<?>)) {
                String msg = "The type of tableExceptList in the property 'additionalSchemaMap' should be List:";
                msg = msg + " type=" + (obj != null ? obj.getClass().getSimpleName() : null) + " value=" + obj;
                throw new DfIllegalPropertyTypeException(msg);
            } else {
                @SuppressWarnings("unchecked")
                final List<String> tableExceptList = (List<String>) obj;
                info.setTableExceptList(tableExceptList);
            }

            obj = elementMap.get("tableTargetList");
            if (obj == null) {
                @SuppressWarnings("unchecked")
                final List<String> tableTargetList = Collections.EMPTY_LIST;
                info.setTableTargetList(tableTargetList);
            } else if (!(obj instanceof List<?>)) {
                String msg = "The type of tableTargetList in the property 'additionalSchemaMap' should be List:";
                msg = msg + " type=" + (obj != null ? obj.getClass().getSimpleName() : null) + " value=" + obj;
                throw new DfIllegalPropertyTypeException(msg);
            } else {
                @SuppressWarnings("unchecked")
                final List<String> tableTargetList = (List<String>) obj;
                info.setTableTargetList(tableTargetList);
            }

            info.setSuppressCommonColumn(isProperty("isSuppressCommonColumn", false, elementMap));

            obj = elementMap.get("supplementaryConnectionMap");
            if (obj == null) {
                @SuppressWarnings("unchecked")
                final Map<String, String> supplementaryConnectionMap = Collections.EMPTY_MAP;
                info.setSupplementaryConnectionMap(supplementaryConnectionMap);
            } else if (!(obj instanceof List<?>)) {
                String msg = "The type of supplementaryConnectionMap in the property 'additionalSchemaMap' should be Map:";
                msg = msg + " type=" + (obj != null ? obj.getClass().getSimpleName() : null) + " value=" + obj;
                throw new DfIllegalPropertyTypeException(msg);
            } else {
                @SuppressWarnings("unchecked")
                final Map<String, String> supplementaryConnectionMap = (Map<String, String>) obj;
                info.setSupplementaryConnectionMap(supplementaryConnectionMap);
            }

            _additionalSchemaMap.put(schemaName, info);
        }
        return _additionalSchemaMap;
    }

    public boolean hasAdditionalSchema() {
        return !getAdditionalSchemaMap().isEmpty();
    }

    public boolean isAdditionalSchema(String schema) {
        return getAdditionalSchemaMap().containsKey(schema);
    }

    // -----------------------------------------------------
    //                              Supplementary Connection
    //                              ------------------------
    public Connection getAdditionalSchemaSupplementaryConnection(String schema) {
        if (!hasAdditionalSchemaSupplementaryConnection(schema)) {
            String msg = "The additional schema should have supplementary connection informations:";
            msg = msg + " schema=" + schema;
            throw new IllegalStateException(msg);
        }
        final String driver = getDatabaseDriver();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            String msg = "The driver was not found: " + driver;
            throw new IllegalStateException(msg, e);
        }
        final String url = getAdditionalSchemaSupplementaryConnectionUrl(schema);
        final String user = getAdditionalSchemaSupplementaryConnectionUser(schema);
        final String password = getAdditionalSchemaSupplementaryConnectionPassword(schema);
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            String msg = "Failed to connect: url=" + url + ", user=" + user;
            throw new IllegalStateException(msg, e);
        }
    }

    public boolean hasAdditionalSchemaSupplementaryConnection(String schema) {
        if (!isAdditionalSchema(schema)) {
            return false;
        }
        final String user = getAdditionalSchemaMap().get(schema).getSupplementaryConnectionUser();
        return user != null && user.trim().length() > 0;
    }

    protected String getAdditionalSchemaSupplementaryConnectionUrl(String schema) {
        return getDatabaseUrl();
    }

    protected String getAdditionalSchemaSupplementaryConnectionUser(String schema) {
        if (!hasAdditionalSchemaSupplementaryConnection(schema)) {
            return null;
        }
        return getAdditionalSchemaMap().get(schema).getSupplementaryConnectionUser();
    }

    protected String getAdditionalSchemaSupplementaryConnectionPassword(String schema) {
        if (!hasAdditionalSchemaSupplementaryConnection(schema)) {
            return null;
        }
        return getAdditionalSchemaMap().get(schema).getSupplementaryConnectionPassword();
    }

    // -----------------------------------------------------
    //                                     VariousMap Helper
    //                                     -----------------
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

    // -----------------------------------------------------
    //                                         Database Info
    //                                         -------------
    public class DatabaseInfo {

        private static final String KEY_DRIVER = "driver";
        private static final String KEY_URL = "url";
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
                if (equalsKeys(key, KEY_DRIVER, KEY_URL, KEY_SCHEMA, KEY_USER, KEY_PASSWORD, KEY_PROPERTIES_MAP,
                        KEY_VARIOUS_MAP)) {
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
            return DriverManager.getConnection(getDatabaseUrl(), getDatabaseUser(), getDatabasePassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}