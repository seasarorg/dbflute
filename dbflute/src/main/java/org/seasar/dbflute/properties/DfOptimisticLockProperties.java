package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.8.8.1 (2009/01/09 Friday)
 */
public final class DfOptimisticLockProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param prop Properties. (NotNull)
     */
    public DfOptimisticLockProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                      Optimistic Lock Definition Map
    //                                                      ==============================
    public static final String KEY_optimisticLockDefinitionMap = "optimisticLockDefinitionMap";
    protected Map<String, Object> _optimisticLockDefinitionMap;

    public Map<String, Object> getOptimisticLockDefinitionMap() {
        if (_optimisticLockDefinitionMap == null) {
            _optimisticLockDefinitionMap = mapProp("torque." + KEY_optimisticLockDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _optimisticLockDefinitionMap;
    }

    public String getProperty(String key, String defaultValue) {
        Map<String, Object> map = getOptimisticLockDefinitionMap();
        Object obj = map.get(key);
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

    public boolean isProperty(String key, boolean defaultValue) {
        Map<String, Object> map = getOptimisticLockDefinitionMap();
        Object obj = map.get(key);
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

    // ===================================================================================
    //                                                                          Field Name
    //                                                                          ==========
    public String getUpdateDateFieldName() {
        return getProperty("updateDateFieldName", "");
    }

    public String getVersionNoFieldName() {
        return getProperty("versionNoFieldName", "version_no");
    }
}