package org.seasar.dbflute.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyBooleanFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyIntegerFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyNotFoundException;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public abstract class DfAbstractHelperProperties {

    /** Log-instance */
    private static final Log _log = LogFactory.getLog(DfAbstractHelperProperties.class);

    /** TorqueContextProperties */
    protected Properties _buildProperties;

    /**
     * Constructor.
     */
    public DfAbstractHelperProperties(Properties prop) {
        _buildProperties = prop;
    }

    protected Properties getProperties() {
        return _buildProperties;
    }

    // **********************************************************************************************
    //                                                                                       Delegate
    //                                                                                       ********
    /**
     * Get property as string. {Delegate method}
     * 
     * @param key Property-key.
     * @return Property as string.
     */
    final protected String stringProp(String key) {
        try {
            return DfPropertyUtil.stringProp(_buildProperties, key);
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#stringProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as string. {Delegate method}
     * 
     * @param key Property-key.
     * @param defaultValue Default value.
     * @return Property as string.
     */
    final protected String stringProp(String key, String defaultValue) {
        try {
            return DfPropertyUtil.stringProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#stringProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as boolean. {Delegate method}
     * 
     * @param key Property-key.
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
     * @param key Property-key.
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

    /**
     * Get property as integer. {Delegate method}
     * 
     * @param key Property-key.
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
     * 
     * @param key Property-key.
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

    /**
     * Get property as list. {Delegate method}
     * 
     * @param key Property-key.
     * @return Property as list.
     */
    final protected List<Object> listProp(String key) {
        try {
            return DfPropertyUtil.listProp(_buildProperties, key, ";");
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#listProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as list. {Delegate method}
     * 
     * @param key Property-key.
     * @param defaultValue Default value.
     * @return Property as list.
     */
    final protected List<Object> listProp(String key, List<Object> defaultValue) {
        try {
            final List<Object> result = DfPropertyUtil.listProp(_buildProperties, key, ";");
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as map. {Delegate method}
     * 
     * @param key Property-key.
     * @return Property as map.
     */
    final protected Map<String, Object> mapProp(String key) {
        try {
            return DfPropertyUtil.mapProp(_buildProperties, key, ";");
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#mapProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as map. {Delegate method}
     * 
     * @param key Property-key.
     * @param defaultValue Default value.
     * @return Property as map.
     */
    final protected Map<String, Object> mapProp(String key, Map<String, Object> defaultValue) {
        try {
            final Map<String, Object> result = DfPropertyUtil.mapProp(_buildProperties, key, ";");
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    protected DfBasicProperties getBasicProperties() {
        return DfPropertiesHandler.getInstance().getBasicProperties(getProperties());
    }

    protected DfGeneratedClassPackageProperties getGeneratedClassPackageProperties() {
        return DfPropertiesHandler.getInstance().getGeneratedClassPackageProperties(getProperties());
    }

    // **********************************************************************************************
    //                                                                                        Default
    //                                                                                        *******

    public static final String JAVA_targetLanguage = "java";
    public static final String CSHARP_targetLanguage = "csharp";
    public static final String DEFAULT_targetLanguage = JAVA_targetLanguage;

    public static final String JAVA_templateFileExtension = "vm";
    public static final String CSHARP_templateFileExtension = "vmnet";
    public static final String DEFAULT_templateFileExtension = JAVA_templateFileExtension;

    public static final String JAVA_classFileExtension = "java";
    public static final String CSHARP_classFileExtension = "cs";
    public static final String DEFAULT_classFileExtension = JAVA_classFileExtension;

    public static final String DEFAULT_templateFileEncoding = "UTF-8";
    public static final String DEFAULT_sourceFileEncoding = "UTF-8";

    public static final Map<String, Object> DEFAULT_EMPTY_MAP = new LinkedHashMap<String, Object>();
    public static final List<Object> DEFAULT_EMPTY_LIST = new ArrayList<Object>();
    public static final String DEFAULT_EMPTY_MAP_STRING = "map:{}";
    public static final String DEFAULT_EMPTY_LIST_STRING = "list:{}";

    // **********************************************************************************************
    //                                                                                         Helper
    //                                                                                         ******

    // ===============================================================================
    //                                                                          String
    //                                                                          ======
    public String filterDoubleQuotation(String str) {
        return DfPropertyUtil.convertAll(str, "\"", "'");
    }

    public String removeNewLine(String str) {
        return DfPropertyUtil.removeAll(str, System.getProperty("line.separator"));
    }

}