package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * @author jflute
 */
public final class DfFlexDtoProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFlexDtoProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> flexDtoDefinitionMap;

    protected Map<String, Object> getFlexDtoDefinitionMap() {
        if (flexDtoDefinitionMap == null) {
            flexDtoDefinitionMap = mapProp("torque.flexDtoDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return flexDtoDefinitionMap;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasFlexDtoDefinition() {
        return !getFlexDtoDefinitionMap().isEmpty();
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    /**
     * @return The directory for output. (NotNull)
     */
    public String getOutputDirectory() {
        final String baseDir = getBasicProperties().getGenerateOutputDirectory();
        final String value = (String) getFlexDtoDefinitionMap().get("outputDirectory");
        return value != null && value.trim().length() > 0 ? baseDir + "/" + value : baseDir;
    }

    // ===================================================================================
    //                                                                          Native Map
    //                                                                          ==========
    public Map<String, String> getJavaToFlexNativeMap() {
        final Map<String, Object> map = getDtoPropertyMap("javaToFlexNativeMap");
        if (map == null) {
            return new LinkedHashMap<String, String>();
        }
        final Set<String> keySet = map.keySet();
        final LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();
        for (String key : keySet) {
            String value = (String) map.get(key);
            resultMap.put(key, value);
        }
        return resultMap;
    }

    // ===================================================================================
    //                                                                       Target/Except
    //                                                                       =============
    public List<String> getBindableTableTargetList() {
        final List<String> ls = getDtoPropertyList("bindableTableTargetList");
        if (ls == null) {
            return new ArrayList<String>();
        }
        return ls;
    }

    public List<String> getBindableTableExceptList() {
        final List<String> ls = getDtoPropertyList("bindableTableExceptList");
        if (ls == null) {
            return new ArrayList<String>();
        }
        return ls;
    }

    protected boolean isBindableTableExcept(final String tableName) {
        final List<String> targetList = getBindableTableTargetList();
        final List<String> exceptList = getBindableTableExceptList();
        return !isTargetByHint(tableName, targetList, exceptList);
    }

    protected boolean isTargetByHint(final String name, final List<String> targetList, final List<String> exceptList) {
        return DfNameHintUtil.isTargetByHint(name, targetList, exceptList);
    }

    protected boolean isHintMatchTheName(String name, String hint) {
        return DfNameHintUtil.isHitByTheHint(name, hint);
    }

    // ===================================================================================
    //                                                                            DTO Info
    //                                                                            ========
    public String getBaseDtoPackage() {
        return getPropertyAsRequired("baseDtoPackage");
    }

    public String getExtendedDtoPackage() {
        return getPropertyAsRequired("extendedDtoPackage");
    }

    public String getBaseDtoPrefix() {
        return getPropertyIfNullEmpty("baseDtoPrefix");
    }

    public String getBaseDtoSuffix() {
        return getPropertyIfNullEmpty("baseDtoSuffix");
    }

    public String getExtendedDtoPrefix() {
        return getPropertyIfNullEmpty("extendedDtoPrefix");
    }

    public String getExtendedDtoSuffix() {
        return getPropertyIfNullEmpty("extendedDtoSuffix");
    }

    public boolean isOverrideExtended() {
        return isProperty("isOverrideExtended", false);
    }

    public boolean isBindable(String tableName) {
        return isProperty("isBindable", false) && !isBindableTableExcept(tableName);
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    protected String getPropertyAsRequired(String key) {
        final String value = getProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " flexDtoDefinitionMap=" + getFlexDtoDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getPropertyIfNullEmpty(String key) {
        final String value = getProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getProperty(String key) {
        return (String) getFlexDtoDefinitionMap().get(key);
    }

    protected boolean isProperty(String key, boolean defaultValue) {
        return isProperty(key, defaultValue, getFlexDtoDefinitionMap());
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getDtoPropertyMap(String key) {
        return (Map<String, Object>) getFlexDtoDefinitionMap().get(key);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getDtoPropertyList(String key) {
        return (List<String>) getFlexDtoDefinitionMap().get(key);
    }
}