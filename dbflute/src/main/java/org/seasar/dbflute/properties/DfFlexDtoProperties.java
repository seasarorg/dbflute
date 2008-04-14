package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

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
    //                                                                     Detail Property
    //                                                                     ===============
    // Unsupported yet
    //    public String getOutputDirectory() {
    //        final String value = (String)getDtoDefinitionMap().get("outputDirectory");
    //        if (value == null) {
    //            return getBasicProperties().getJavaDir();
    //        }
    //        return getBasicProperties().getJavaDir() + "/" + value;
    //    }

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
}