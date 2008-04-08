package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public final class DfDtoProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDtoProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> dtoDefinitionMap;
    protected Map<String, Object> getDtoDefinitionMap() {
        if (dtoDefinitionMap == null) {
            dtoDefinitionMap = mapProp("torque.dtoDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return dtoDefinitionMap;
    }
    
    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasDtoDefinition() {
        return !getDtoDefinitionMap().isEmpty();
    }
    
    // ===================================================================================
    //                                                                     Detail Property
    //                                                                     ===============
    public String getOutputDirectory() {
        final String value = (String)getDtoDefinitionMap().get("outputDirectory");
        if (value == null) {
            return getBasicProperties().getJavaDir();
        }
        // TODO: @jflute -- 調整をすること
        return getBasicProperties().getJavaDir() + "/" + value;
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
    
    public boolean isSuppressAccessor() {
        final String value = (String)getDtoDefinitionMap().get("suppressAccessor");
        return processBooleanString(value);
    }
    
    protected String getDtoPropertyRequired(String key) {
        final String value = getDtoProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " dtoDefinitionMap=" + getDtoDefinitionMap();
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
        final String value = (String)getDtoDefinitionMap().get(key);
        return value;
    }
}