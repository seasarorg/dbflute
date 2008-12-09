package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.config.DfEnvironmentType;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.io.text.DfListStringFileReader;
import org.seasar.dbflute.helper.io.text.DfMapStringFileReader;
import org.seasar.dbflute.helper.io.text.DfStringFileReader;
import org.seasar.dbflute.properties.handler.DfPropertiesHandler;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyBooleanFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyIntegerFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyNotFoundException;
import org.seasar.dbflute.util.basic.DfStringUtil;

/**
 * @author jflute
 */
public abstract class DfAbstractHelperProperties {

    // ===============================================================================
    //                                                                      Definition
    //                                                                      ==========
    /** Log-instance */
    private static final Log _log = LogFactory.getLog(DfAbstractHelperProperties.class);

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
     */
    public DfAbstractHelperProperties(Properties prop) {
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
    //                                                String
    //                                                ------
    /**
     * Get property as string. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @return Property as string. (NotNull)
     */
    final protected String stringProp(String key) {
        try {
            final String outsidePropString = getOutsidePropString(key);
            if (outsidePropString != null && outsidePropString.trim().length() > 0) {
                return outsidePropString;
            }
            return DfPropertyUtil.stringProp(_buildProperties, key);
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#stringProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as string. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as string. (Nullable: If the default-value is null)
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
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#stringProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as string. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as string. (Nullable: If the default-value is null)
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
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#stringProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    /**
     * Get property as boolean. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @return Property as boolean.
     */
    final protected boolean booleanProp(String key) {
        try {
            return DfPropertyUtil.booleanProp(_buildProperties, key);
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#booleanProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as boolean. {Delegate method}
     * 
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
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
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
        try {
            return DfPropertyUtil.intProp(_buildProperties, key);
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
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
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
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
        try {
            final List<Object> outsidePropList = getOutsidePropList(key);
            if (!outsidePropList.isEmpty()) {
                return outsidePropList;
            }
            return DfPropertyUtil.listProp(_buildProperties, key, ";");
        } catch (RuntimeException e) {
            _log.warn("DfPropertyUtil#listProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as list. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as list. (Nullable: If the default-value is null)
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
        } catch (RuntimeException e) {
            _log.warn("DfPropertyUtil#listProp() threw the exception with The key[" + key + "]", e);
            throw e;
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
        try {
            final Map<String, Object> outsidePropMap = getOutsidePropMap(key);
            if (!outsidePropMap.isEmpty()) {
                return outsidePropMap;
            }
            return DfPropertyUtil.mapProp(_buildProperties, key, ";");
        } catch (RuntimeException e) {
            _log.warn("DfPropertyUtil#mapProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as map. {Delegate method}
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as map. (Nullable: If the default-value is null)
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
        } catch (RuntimeException e) {
            _log.warn("DfPropertyUtil#mapProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    // ===============================================================================
    //                                                              Outside Properties
    //                                                              ==================
    protected String getOutsidePropString(String key) {
        final String filteredKey = DfStringUtil.replace(key, "torque.", "");
        final String encoding = "UTF-8";
        final DfStringFileReader reader = new DfStringFileReader();
        if (!isEnvironmentDefault()) {
            final String environmentType = getEnvironmentType();
            final String path = "./dfprop/" + environmentType + "/" + filteredKey + ".dfprop";
            final String str = reader.readString(path, encoding);
            if (str.trim().length() > 0) {
                return str;
            }
        }
        return reader.readString("./dfprop/" + filteredKey + ".dfprop", encoding);
    }

    protected Map<String, Object> getOutsidePropMap(String key) {
        final String filteredKey = DfStringUtil.replace(key, "torque.", "");
        final String encoding = "UTF-8";
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        if (!isEnvironmentDefault()) {
            final String environmentType = getEnvironmentType();
            final String path = "./dfprop/" + environmentType + "/" + filteredKey + ".dfprop";
            final Map<String, Object> map = reader.readMap(path, encoding);
            if (!map.isEmpty()) {
                return map;
            }
        }
        return reader.readMap("./dfprop/" + filteredKey + ".dfprop", encoding);
    }

    protected List<Object> getOutsidePropList(String key) {
        final String filteredKey = DfStringUtil.replace(key, "torque.", "");
        final String encoding = "UTF-8";
        final DfListStringFileReader reader = new DfListStringFileReader();
        if (!isEnvironmentDefault()) {
            final String environmentType = getEnvironmentType();
            final String path = "./dfprop/" + environmentType + "/" + filteredKey + ".dfprop";
            List<Object> list = reader.readList(path, encoding);
            if (!list.isEmpty()) {
                return list;
            }
        }
        return reader.readList("./dfprop/" + filteredKey + ".dfprop", encoding);
    }

    // ===============================================================================
    //                                                            Other Property Entry
    //                                                            ====================
    protected DfPropertiesHandler handler() {
        return DfPropertiesHandler.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return DfPropertiesHandler.getInstance().getBasicProperties(getProperties());
    }

    protected DfGeneratedClassPackageProperties getGeneratedClassPackageProperties() {
        return DfPropertiesHandler.getInstance().getGeneratedClassPackageProperties(getProperties());
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return DfPropertiesHandler.getInstance().getLittleAdjustmentProperties(getProperties());
    }

    protected DfS2DaoAdjustmentProperties getS2DaoAdjustmentProperties() {
        return DfPropertiesHandler.getInstance().getS2DaoAdjustmentProperties(getProperties());
    }

    // ===============================================================================
    //                                                                   Assist Helper
    //                                                                   =============
    protected boolean isHitByTheHint(final String name, final String hint) {
        return DfNameHintUtil.isHitByTheHint(name, hint);
    }

    protected final boolean isEnvironmentDefault() {
        return DfEnvironmentType.getInstance().isDefault();
    }

    protected final String getEnvironmentType() {
        return DfEnvironmentType.getInstance().getEnvironmentType();
    }

    // ===============================================================================
    //                                                                  General Helper
    //                                                                  ==============
    protected <KEY, VALUE> DfFlexibleMap<KEY, VALUE> newFlexibleNameMap(Map<KEY, VALUE> map) {
        return new DfFlexibleMap<KEY, VALUE>(map);
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