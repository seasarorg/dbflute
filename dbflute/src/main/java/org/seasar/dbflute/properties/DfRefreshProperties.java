package org.seasar.dbflute.properties;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

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
    public List<String> getProjectNameList() {
        final String prop = getRefreshProperty("projectName");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(prop)) {
            return DfStringUtil.splitListTrimmed(prop, "/");
        } else {
            return DfCollectionUtil.emptyList();
        }
    }

    public String getRequestUrl() {
        final String prop = getRefreshProperty("requestUrl");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(prop)) {
            return prop;
        } else {
            return null;
        }
    }

    protected String getRefreshPropertyIfNullEmpty(String key) {
        final String value = getRefreshProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getRefreshProperty(String key) {
        return (String) getRefreshDefinitionMap().get(key);
    }
}