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

    public boolean isOverrideExtended() {
        return isDtoProperty("overrideExtended");
    }

    public boolean isBindable(String tableName) {
        return isDtoProperty("bindable") && !isBindableTableExcept(tableName);
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
    //                                                                     Detail Property
    //                                                                     ===============
    public String getOutputDirectory() {
        final String value = (String) getFlexDtoDefinitionMap().get("outputDirectory");
        if (value == null) {
            return getBasicProperties().getOutputDirectory();
        }
        return getBasicProperties().getOutputDirectory() + "/" + value;
    }

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

    public String getBaseDtoPackage() {
        return getDtoPropertyRequired("baseDtoPackage");
    }

    public String getExtendedDtoPackage() {
        return getDtoPropertyRequired("extendedDtoPackage");
    }

    public String getBaseDtoPrefix() {
        return getDtoPropertyIfNullEmpty("baseDtoPrefix");
    }

    public String getBaseDtoSuffix() {
        return getDtoPropertyIfNullEmpty("baseDtoSuffix");
    }

    public String getExtendedDtoPrefix() {
        return getDtoPropertyIfNullEmpty("extendedDtoPrefix");
    }

    public String getExtendedDtoSuffix() {
        return getDtoPropertyIfNullEmpty("extendedDtoSuffix");
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    protected String getDtoPropertyRequired(String key) {
        final String value = getDtoProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " flexDtoDefinitionMap=" + getFlexDtoDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getDtoPropertyIfNullEmpty(String key) {
        final String value = getDtoProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getDtoProperty(String key) {
        final String value = (String) getFlexDtoDefinitionMap().get(key);
        return value;
    }

    protected boolean isDtoProperty(String key) {
        final String value = (String) getFlexDtoDefinitionMap().get(key);
        return value != null && value.trim().equalsIgnoreCase("true");
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