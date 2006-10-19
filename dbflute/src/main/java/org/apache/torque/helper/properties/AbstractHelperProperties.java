package org.apache.torque.helper.properties;

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
import org.apache.torque.helper.stateless.FlPropertyUtil;
import org.apache.torque.helper.stateless.NameHintUtil;
import org.apache.torque.helper.stateless.FlPropertyUtil.PropertyBooleanFormatException;
import org.apache.torque.helper.stateless.FlPropertyUtil.PropertyIntegerFormatException;
import org.apache.torque.helper.stateless.FlPropertyUtil.PropertyNotFoundException;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public abstract class AbstractHelperProperties {

    /** Log-instance */
    private static final Log _log = LogFactory.getLog(AbstractHelperProperties.class);

    /** TorqueContextProperties */
    protected Properties _buildProperties;

    /**
     * Constructor.
     */
    public AbstractHelperProperties(Properties prop) {
        _buildProperties = prop;
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
            return FlPropertyUtil.stringProp(_buildProperties, key);
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
            return FlPropertyUtil.stringProp(_buildProperties, key);
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
            return FlPropertyUtil.booleanProp(_buildProperties, key);
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
            return FlPropertyUtil.booleanProp(_buildProperties, key);
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
            return FlPropertyUtil.intProp(_buildProperties, key);
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
            return FlPropertyUtil.intProp(_buildProperties, key);
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
            return FlPropertyUtil.listProp(_buildProperties, key, ";");
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
            final List<Object> result = FlPropertyUtil.listProp(_buildProperties, key, ";");
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
            return FlPropertyUtil.mapProp(_buildProperties, key, ";");
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
            final Map<String, Object> result = FlPropertyUtil.mapProp(_buildProperties, key, ";");
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

    public static final String DEFAULT_templateFileEncoding = "Windows-31J";

    public static final Map<String, Object> DEFAULT_EMPTY_MAP = new LinkedHashMap<String, Object>();
    public static final List<Object> DEFAULT_EMPTY_LIST = new ArrayList<Object>();
    public static final String DEFAULT_EMPTY_MAP_STRING = "map:{}";
    public static final String DEFAULT_EMPTY_LIST_STRING = "list:{}";

    // **********************************************************************************************
    //                                                                                       Property
    //                                                                                       ********
    // ===============================================================================
    //                                                            Properties - Project
    //                                                            ====================
    public String getProjectName() {
        return stringProp("torque.project", "");
    }

    // ===============================================================================
    //                                                           Properties - Database
    //                                                           =====================
    public String getDatabaseName() {
        return stringProp("torque.database", "");
    }

    // ===============================================================================
    //                                                            Properties - JavaDir
    //                                                            ====================
    public String getJavaDir() {
        return stringProp("torque.java.dir", "");
    }

    public String getJavaLocation_for_gen() {
        return stringProp("torque.java.location.for.gen", "");
    }

    public String getJavaLocation_for_main() {
        return stringProp("torque.java.location.for.main", "");
    }

    // ===============================================================================
    //                                                           Properties - Language
    //                                                           =====================
    public String getTargetLanguage() {
        return stringProp("torque.targetLanguage", DEFAULT_targetLanguage);
    }

    public boolean isTargetLanguageJava() {
        return JAVA_targetLanguage.equals(getTargetLanguage());
    }

    public boolean isTargetLanguageCSharp() {
        return CSHARP_targetLanguage.equals(getTargetLanguage());
    }


    // ===============================================================================
    //                                                      Properties - Database Info
    //                                                      ==========================
    public String getDatabaseDriver() {
        return stringProp("torque.database.driver");
    }

    public String getDatabaseUri() {
        return stringProp("torque.database.url");
    }

    public String getDatabaseUser() {
        return stringProp("torque.database.user");
    }

    public String getDatabasePassword() {
        return stringProp("torque.database.password");
    }

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

    // **********************************************************************************************
    //                                                                                         Helper
    //                                                                                         ******

    // ===============================================================================
    //                                                                          String
    //                                                                          ======
    public String filterDoubleQuotation(String str) {
        return FlPropertyUtil.convertAll(str, "\"", "'");
    }

    public String removeNewLine(String str) {
        return FlPropertyUtil.removeAll(str, System.getProperty("line.separator"));
    }

}