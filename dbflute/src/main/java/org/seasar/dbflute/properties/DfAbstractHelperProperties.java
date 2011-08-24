package org.seasar.dbflute.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.config.DfEnvironmentType;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.logic.jdbc.connection.DfCurrentSchemaConnector;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.properties.filereader.DfListStringFileReader;
import org.seasar.dbflute.properties.filereader.DfMapStringFileReader;
import org.seasar.dbflute.properties.filereader.DfStringFileReader;
import org.seasar.dbflute.properties.handler.DfPropertiesHandler;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyBooleanFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyIntegerFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyNotFoundException;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public abstract class DfAbstractHelperProperties {

    // ===============================================================================
    //                                                                      Definition
    //                                                                      ==========
    // -----------------------------------------------------
    //                                         Default Value
    //                                         -------------
    public static final String JAVA_targetLanguage = "java";
    public static final String CSHARP_targetLanguage = "csharp";
    public static final String PHP_targetLanguage = "php";
    public static final String CSHARPOLD_targetLanguage = "csharpold";
    public static final String DEFAULT_targetLanguage = JAVA_targetLanguage;

    public static final String DEFAULT_templateFileEncoding = "UTF-8";
    public static final String DEFAULT_sourceFileEncoding = "UTF-8";
    public static final String DEFAULT_projectSchemaXMLEncoding = "UTF-8";

    // -----------------------------------------------------
    //                                   Empty Default Value
    //                                   -------------------
    public static final Map<String, Object> DEFAULT_EMPTY_MAP = new LinkedHashMap<String, Object>();
    public static final List<Object> DEFAULT_EMPTY_LIST = new ArrayList<Object>();
    public static final String DEFAULT_EMPTY_MAP_STRING = "map:{}";
    public static final String DEFAULT_EMPTY_LIST_STRING = "list:{}";

    // ===============================================================================
    //                                                                       Attribute
    //                                                                       =========
    /** TorqueContextProperties */
    protected Properties _buildProperties;

    // ===============================================================================
    //                                                                     Constructor
    //                                                                     ===========
    /**
     * Constructor.
     * @param prop Build-properties. (NotNull)
     */
    public DfAbstractHelperProperties(Properties prop) {
        if (prop == null) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + ln();
            msg = msg + "The build-properties is required!" + ln();
            msg = msg + ln();
            msg = msg + "[Advice]" + ln();
            msg = msg + "Check your environment of DBFlute client and module!" + ln();
            msg = msg + "And set up from first again after confirmation of correct procedure." + ln();
            msg = msg + ln();
            msg = msg + "[Properties]" + ln() + null + ln();
            msg = msg + "- - - - - - - - - -/";
            throw new IllegalStateException(msg);
        }
        _buildProperties = prop;
    }

    // ===============================================================================
    //                                                                        Accessor
    //                                                                        ========
    protected Properties getProperties() {
        return _buildProperties;
    }

    // ===============================================================================
    //                                                                      Properties
    //                                                                      ==========
    // -----------------------------------------------------
    //                                              Accessor
    //                                              --------
    public String getProperty(String key, String defaultValue, Map<String, ? extends Object> map) {
        final Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be string:";
                msg = msg + " " + DfTypeUtil.toClassTitle(obj) + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value;
            } else {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public String getPropertyIfNotBuildProp(String key, String defaultValue, Map<String, ? extends Object> map) {
        final Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be string:";
                msg = msg + " " + DfTypeUtil.toClassTitle(obj) + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value;
            } else {
                return defaultValue;
            }
        }
        return stringProp("torque." + key, defaultValue);
    }

    public boolean isProperty(String key, boolean defaultValue, Map<String, ? extends Object> map) {
        Object obj = map.get(key);
        if (obj == null) {
            final String anotherKey = deriveBooleanAnotherKey(key);
            if (anotherKey != null) {
                obj = map.get(anotherKey);
            }
        }
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be boolean:";
                msg = msg + " " + DfTypeUtil.toClassTitle(obj) + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value.trim().equalsIgnoreCase("true");
            } else {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public boolean isPropertyIfNotExistsFromBuildProp(String key, boolean defaultValue,
            Map<String, ? extends Object> map) {
        Object obj = map.get(key);
        if (obj == null) {
            final String anotherKey = deriveBooleanAnotherKey(key);
            if (anotherKey != null) {
                obj = map.get(anotherKey);
            }
        }
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be boolean:";
                msg = msg + " " + DfTypeUtil.toClassTitle(obj) + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value.trim().equalsIgnoreCase("true");
            } else {
                return defaultValue;
            }
        }
        return booleanProp("torque." + key, defaultValue);
    }

    static String deriveBooleanAnotherKey(String key) {
        if (key.length() > "is".length() && key.startsWith("is")) {
            if (Character.isUpperCase(key.substring("is".length()).charAt(0))) {
                return DfStringUtil.initUncap(key.substring("is".length()));
            }
        }
        return null;
    }

    // -----------------------------------------------------
    //                                                String
    //                                                ------
    /**
     * Get property as string. {Delegate method}
     * @param key Property-key. (NotNull)
     * @return Property as string. (NotNull)
     */
    final protected String stringProp(String key) {
        final String outsidePropString = getOutsidePropString(key);
        if (outsidePropString != null && outsidePropString.trim().length() > 0) {
            return outsidePropString;
        }
        return DfPropertyUtil.stringProp(_buildProperties, key);
    }

    /**
     * Get property as string. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (NullAllowed)
     * @return Property as string. (NullAllowed: If the default-value is null)
     */
    final protected String stringProp(String key, String defaultValue) {
        try {
            final String outsidePropString = getOutsidePropString(key);
            if (outsidePropString != null && outsidePropString.trim().length() > 0) {
                return outsidePropString;
            }
            return DfPropertyUtil.stringProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        }
    }

    /**
     * Get property as string. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (NullAllowed)
     * @return Property as string. (NullAllowed: If the default-value is null)
     */
    final protected String stringPropNoEmpty(String key, String defaultValue) {
        try {
            final String outsidePropString = getOutsidePropString(key);
            if (outsidePropString != null && outsidePropString.trim().length() > 0) {
                return outsidePropString;
            }
            final String value = DfPropertyUtil.stringProp(_buildProperties, key);
            if (value != null && value.trim().length() != 0) {
                return value;
            }
            return defaultValue;
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        }
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    /**
     * Get property as boolean. {Delegate method}
     * @param key Property-key. (NotNull)
     * @return Property as boolean.
     */
    final protected boolean booleanProp(String key) {
        return DfPropertyUtil.booleanProp(_buildProperties, key);
    }

    /**
     * Get property as boolean. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value.
     * @return Property as boolean.
     */
    final protected boolean booleanProp(String key, boolean defaultValue) {
        try {
            return DfPropertyUtil.booleanProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (PropertyBooleanFormatException e) {
            return defaultValue;
        }
    }

    // -----------------------------------------------------
    //                                               Integer
    //                                               -------
    /**
     * Get property as integer. {Delegate method}
     * @param key Property-key. (NotNull)
     * @return Property as integer.
     */
    final protected int intProp(String key) {
        return DfPropertyUtil.intProp(_buildProperties, key);
    }

    /**
     * Get property as integer. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value.
     * @return Property as integer.
     */
    final protected int intProp(String key, int defaultValue) {
        try {
            return DfPropertyUtil.intProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (PropertyIntegerFormatException e) {
            return defaultValue;
        }
    }

    // -----------------------------------------------------
    //                                                  List
    //                                                  ----
    /**
     * Get property as list. {Delegate method}
     * @param key Property-key. (NotNull)
     * @return Property as list. (NotNull)
     */
    final protected List<Object> listProp(String key) {
        final List<Object> outsidePropList = getOutsidePropList(key);
        if (!outsidePropList.isEmpty()) {
            return outsidePropList;
        }
        return DfPropertyUtil.listProp(_buildProperties, key, ";");
    }

    /**
     * Get property as list. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (NullAllowed)
     * @return Property as list. (NullAllowed: If the default-value is null)
     */
    final protected List<Object> listProp(String key, List<Object> defaultValue) {
        try {
            final List<Object> outsidePropList = getOutsidePropList(key);
            if (!outsidePropList.isEmpty()) {
                return outsidePropList;
            }
            final List<Object> result = DfPropertyUtil.listProp(_buildProperties, key, ";");
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        }
    }

    // -----------------------------------------------------
    //                                                   Map
    //                                                   ---
    /**
     * Get property as map. {Delegate method}
     * @param key Property-key. (NotNull)
     * @return Property as map. (NotNull)
     */
    final protected Map<String, Object> mapProp(String key) {
        final Map<String, Object> outsidePropMap = getOutsidePropMap(key);
        if (!outsidePropMap.isEmpty()) {
            return outsidePropMap;
        }
        return DfPropertyUtil.mapProp(_buildProperties, key, ";");
    }

    /**
     * Get property as map. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (NullAllowed)
     * @return Property as map. (NullAllowed: If the default-value is null)
     */
    final protected Map<String, Object> mapProp(String key, Map<String, Object> defaultValue) {
        try {
            final Map<String, Object> outsidePropMap = getOutsidePropMap(key);
            if (!outsidePropMap.isEmpty()) {
                return outsidePropMap;
            }
            final Map<String, Object> result = DfPropertyUtil.mapProp(_buildProperties, key, ";");
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        }
    }

    // ===============================================================================
    //                                                              Outside Properties
    //                                                              ==================
    protected String getOutsidePropString(String key) {
        final String propName = DfStringUtil.replace(key, "torque.", "");
        final DfStringFileReader reader = new DfStringFileReader();
        if (isSpecifiedEnvironmentType()) {
            final String environmentType = getEnvironmentType();
            final String path = "./dfprop/" + environmentType + "/" + propName + ".dfprop";
            final String str = reader.readString(path);
            if (str.trim().length() > 0) {
                return str;
            }
        }
        return reader.readString("./dfprop/" + propName + ".dfprop");
    }

    protected Map<String, Object> getOutsidePropMap(String key) {
        final String propName = DfStringUtil.replace(key, "torque.", "");
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        final String mainpath = "./dfprop/" + propName + ".dfprop";
        if (isSpecifiedEnvironmentType()) {
            final String envpath = "./dfprop/" + getEnvironmentType() + "/" + propName + ".dfprop";
            Map<String, Object> map = reader.readMap(envpath);
            if (map.isEmpty()) {
                map = reader.readMap(mainpath);
                setupOutsidePropExMap(reader, map, mainpath);
            }
            setupOutsidePropExMap(reader, map, envpath);
            return map;
        } else {
            final Map<String, Object> map = reader.readMap(mainpath);
            setupOutsidePropExMap(reader, map, mainpath);
            return map;
        }
    }

    protected void setupOutsidePropExMap(DfMapStringFileReader reader, Map<String, Object> map, String path) {
        if (!path.endsWith(".dfprop")) {
            String msg = "The path should end with '.dfprop':";
            msg = msg + " path=" + path;
            throw new IllegalStateException(msg);
        }
        path = path.substring(0, path.length() - ".dfprop".length()) + "+.dfprop";
        final Map<String, Object> exMap = reader.readMap(path);
        map.putAll(exMap);
    }

    protected List<Object> getOutsidePropList(String key) {
        final String propName = DfStringUtil.replace(key, "torque.", "");
        final DfListStringFileReader reader = new DfListStringFileReader();
        if (isSpecifiedEnvironmentType()) {
            final String environmentType = getEnvironmentType();
            final String path = "./dfprop/" + environmentType + "/" + propName + ".dfprop";
            List<Object> list = reader.readList(path);
            if (!list.isEmpty()) {
                return list;
            }
        }
        return reader.readList("./dfprop/" + propName + ".dfprop");
    }

    // ===============================================================================
    //                                                            Other Property Entry
    //                                                            ====================
    public DfPropertiesHandler handler() {
        return DfPropertiesHandler.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return handler().getBasicProperties(getProperties());
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return handler().getDatabaseProperties(getProperties());
    }

    protected DfAdditionalForeignKeyProperties getAdditionalForeignKeyProperties() {
        return handler().getAdditionalForeignKeyProperties(getProperties());
    }

    protected DfCommonColumnProperties getCommonColumnProperties() {
        return handler().getCommonColumnProperties(getProperties());
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return handler().getLittleAdjustmentProperties(getProperties());
    }

    protected DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return handler().getReplaceSchemaProperties(getProperties());
    }

    // ===============================================================================
    //                                                                   Assist Helper
    //                                                                   =============
    protected boolean isTargetByHint(String name, List<String> targetList, List<String> exceptList) {
        return DfNameHintUtil.isTargetByHint(name, targetList, exceptList);
    }

    protected boolean isHitByTheHint(final String name, final String hint) {
        return DfNameHintUtil.isHitByTheHint(name, hint);
    }

    protected final boolean isSpecifiedEnvironmentType() {
        return DfEnvironmentType.getInstance().isSpecifiedType();
    }

    protected final String getEnvironmentType() {
        return DfEnvironmentType.getInstance().getEnvironmentType();
    }

    protected Connection createConnection(String driver, String url, UnifiedSchema unifiedSchema, Properties info) {
        setupConnectionDriver(driver);
        try {
            final Connection conn = DriverManager.getConnection(url, info);
            setupConnectionVariousSetting(unifiedSchema, conn);
            return conn;
        } catch (SQLException e) {
            String msg = "Failed to connect: url=" + url + " info=" + info;
            throw new IllegalStateException(msg, e);
        }
    }

    protected Connection createConnection(String driver, String url, UnifiedSchema unifiedSchema, String user,
            String password) {
        setupConnectionDriver(driver);
        try {
            final Connection conn = DriverManager.getConnection(url, user, password);
            setupConnectionVariousSetting(unifiedSchema, conn);
            return conn;
        } catch (SQLException e) {
            String msg = "Failed to connect: url=" + url + " user=" + user;
            throw new IllegalStateException(msg, e);
        }
    }

    private void setupConnectionDriver(String driver) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupConnectionVariousSetting(UnifiedSchema unifiedSchema, Connection conn) throws SQLException {
        conn.setAutoCommit(true);
        if (unifiedSchema.existsPureSchema()) {
            final DfDatabaseTypeFacadeProp facadeProp = getBasicProperties().getDatabaseTypeFacadeProp();
            final DfCurrentSchemaConnector connector = new DfCurrentSchemaConnector(unifiedSchema, facadeProp);
            connector.connectSchema(conn);
        }
    }

    protected String getConnectedCatalog(String driver, String url, String user, String password) throws SQLException {
        setupConnectionDriver(driver);
        try {
            final Connection conn = DriverManager.getConnection(url, user, password);
            return conn.getCatalog();
        } catch (SQLException e) {
            String msg = "Failed to connect: url=" + url + " user=" + user;
            throw new DfJDBCException(msg, e);
        }
    }

    protected String castToString(Object obj, String property) {
        if (!(obj instanceof String)) {
            String msg = "The type of the property '" + property + "' should be String:";
            msg = msg + " obj=" + obj + " type=" + (obj != null ? obj.getClass() : null);
            throw new DfIllegalPropertyTypeException(msg);
        }
        return (String) obj;
    }

    @SuppressWarnings("unchecked")
    protected <ELEMENT> List<ELEMENT> castToList(Object obj, String property) {
        if (!(obj instanceof List<?>)) {
            String msg = "The type of the property '" + property + "' should be List:";
            msg = msg + " obj=" + obj + " type=" + (obj != null ? obj.getClass() : null);
            throw new DfIllegalPropertyTypeException(msg);
        }
        return (List<ELEMENT>) obj;
    }

    // ===============================================================================
    //                                                                  General Helper
    //                                                                  ==============
    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    protected String filterDoubleQuotation(String str) {
        return DfPropertyUtil.convertAll(str, "\"", "'");
    }

    protected String removeLineSeparator(String str) {
        str = removeCR(str);
        str = removeLF(str);
        return str;
    }

    protected String removeLF(String str) {
        return str.replaceAll("\n", "");
    }

    protected String removeCR(String str) {
        return str.replaceAll("\r", "");
    }

    protected boolean processBooleanString(String value) {
        return value != null && value.trim().equalsIgnoreCase("true");
    }
}