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
    //                                                                       Database Info
    //                                                                       =============
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
        return getVairousList("objectTypeTargetList", getDatabaseTypeList());
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
    //                                Additional Schema List
    //                                ----------------------
    public List<String> getAdditionalSchemaList() {
        return getVairousList("additionalSchemaList");
    }

    // -----------------------------------------------------
    //                                     Table Except List
    //                                     -----------------
    public List<String> getTableExceptList() {
        final List<String> vairousList = getVairousList("tableExceptList");
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
    public List<String> getTableTargetList() {
        final List<String> vairousList = getVairousList("tableTargetList");
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
    public List<String> getSimpleColumnExceptList() {
        final List<String> vairousList = getVairousList("columnExceptList");
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

    // -----------------------------------------------------
    //                                     VariousMap Helper
    //                                     -----------------
    @SuppressWarnings("unchecked")
    protected List<String> getVairousList(String key) {
        return getVairousList(key, Collections.EMPTY_LIST);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getVairousList(String key, List<String> defaultList) {
        final Object value = getVariousObject(key);
        if (value == null) {
            return defaultList != null ? defaultList : new ArrayList<String>();
        }
        assertVariousPropertyList(key, value);
        return (List<String>) value;
    }

    protected Object getVariousObject(String key) {
        final Map<String, Object> variousMap = _databaseInfo.getDatabaseVariousMap();
        return variousMap.get(key);
    }

    protected void assertVariousPropertyList(String name, Object value) {
        if (!(value instanceof List<?>)) {
            String msg = "The property '" + name + "' should be list: " + value;
            throw new IllegalStateException(msg);
        }
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