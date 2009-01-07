package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/17 Friday)
 */
public final class DfSqlLogRegistryProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlLogRegistryProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                         sqlLogRegistryDefinitionMap
    //                                                         ===========================
    public static final String KEY_sqlLogRegistryDefinitionMap = "sqlLogRegistryDefinitionMap";
    protected Map<String, Object> _sqlLogRegistryDefinitionMap;

    protected Map<String, Object> getSqlLogRegistryDefinitionMap() { // It's closet!
        if (_sqlLogRegistryDefinitionMap == null) {
            _sqlLogRegistryDefinitionMap = mapProp("torque." + KEY_sqlLogRegistryDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _sqlLogRegistryDefinitionMap;
    }

    public boolean isValid() {
        String value = (String) getSqlLogRegistryDefinitionMap().get("valid");
        if (value == null || value.trim().length() == 0) {
            return false;
        }
        return value.trim().equalsIgnoreCase("true");
    }

    public int getLimitSize() {
        String value = (String) getSqlLogRegistryDefinitionMap().get("limitSize");
        if (value == null || value.trim().length() == 0) {
            return 3; // as Default
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            String msg = "The limitSize of sqlLogRegistryDefinitionMap should be number:";
            msg = msg + " limitSize=" + value;
            throw new IllegalStateException(msg, e);
        }
    }
}