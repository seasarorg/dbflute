package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 * @since 0.6.9 (2008/04/11 Friday)
 */
public final class DfRefreshProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfRefreshProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> refreshDefinitionMap;

    protected Map<String, Object> getRefreshDefinitionMap() {
        if (refreshDefinitionMap == null) {
            refreshDefinitionMap = mapProp("torque.refreshDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return refreshDefinitionMap;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasRefreshDefinition() {
        return !getRefreshDefinitionMap().isEmpty();
    }

    // ===================================================================================
    //                                                                     Detail Property
    //                                                                     ===============
    public String getProjectName() {
        return getRefreshPropertyRequired("projectName");
    }

    public String getRequestUrl() {
        return getRefreshPropertyRequired("requestUrl");
    }

    protected String getRefreshPropertyRequired(String key) {
        final String value = getRefreshProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " refreshDefinitionMap=" + getRefreshDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getRefreshPropertyIfNullEmpty(String key) {
        final String value = getRefreshProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getRefreshProperty(String key) {
        final String value = (String) getRefreshDefinitionMap().get(key);
        return value;
    }
}