package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 * @since 0.6.9 (2008/04/11 Friday)
 */
public final class DfResourceSynchronizerProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfResourceSynchronizerProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> resourceSynchronizerDefinitionMap;

    protected Map<String, Object> getResourceSynchronizerDefinitionMap() {
        if (resourceSynchronizerDefinitionMap == null) {
            resourceSynchronizerDefinitionMap = mapProp("torque.resourceSynchronizerDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return resourceSynchronizerDefinitionMap;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasResourceSynchronizerDefinition() {
        return !getResourceSynchronizerDefinitionMap().isEmpty();
    }

    // ===================================================================================
    //                                                                     Detail Property
    //                                                                     ===============
    public String getProjectName() {
        return getResourceSynchronizerPropertyRequired("projectName");
    }

    public String getRequestUrl() {
        return getResourceSynchronizerPropertyRequired("requestUrl");
    }

    protected String getResourceSynchronizerPropertyRequired(String key) {
        final String value = getResourceSynchronizerProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " resourceSynchronizerDefinitionMap=" + getResourceSynchronizerDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getResourceSynchronizerPropertyIfNullEmpty(String key) {
        final String value = getResourceSynchronizerProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getResourceSynchronizerProperty(String key) {
        final String value = (String) getResourceSynchronizerDefinitionMap().get(key);
        return value;
    }
}