package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public final class DfSimpleDtoProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSimpleDtoProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> simpleDtoDefinitionMap;

    protected Map<String, Object> getSimpleDtoDefinitionMap() {
        if (simpleDtoDefinitionMap == null) {
            simpleDtoDefinitionMap = mapProp("torque.simpleDtoDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return simpleDtoDefinitionMap;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasSimpleDtoDefinition() {
        return !getSimpleDtoDefinitionMap().isEmpty();
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

    public String getMapperPackage() {
        return getDtoPropertyIfNullEmpty("dtoMapperPackage");
    }

    public boolean isUseDtoMapper() {
        final String dtoMapperPackage = getMapperPackage();
        return dtoMapperPackage != null && dtoMapperPackage.trim().length() > 0;
    }

    protected String getDtoPropertyRequired(String key) {
        final String value = getDtoProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " simpleDtoDefinitionMap=" + getSimpleDtoDefinitionMap();
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
        final String value = (String) getSimpleDtoDefinitionMap().get(key);
        return value;
    }
}