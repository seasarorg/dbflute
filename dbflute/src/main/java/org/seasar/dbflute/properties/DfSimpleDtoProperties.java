package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.util.Srl;

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
    //                                                                    Output Directory
    //                                                                    ================
    public String getSimpleDtoOutputDirectory() {
        final String baseDir = getBasicProperties().getGenerateOutputDirectory();
        final String value = (String) getSimpleDtoDefinitionMap().get("simpleDtoOutputDirectory");
        return value != null && value.trim().length() > 0 ? baseDir + "/" + value : baseDir;
    }

    public String getDtoMapperOutputDirectory() {
        final String baseDir = getBasicProperties().getGenerateOutputDirectory();
        final String value = (String) getSimpleDtoDefinitionMap().get("dtoMapperOutputDirectory");
        return value != null && value.trim().length() > 0 ? baseDir + "/" + value : baseDir;
    }

    // ===================================================================================
    //                                                                            DTO Info
    //                                                                            ========
    public String getBaseDtoPackage() {
        return getPropertyRequired("baseDtoPackage");
    }

    public String getExtendedDtoPackage() {
        return getPropertyRequired("extendedDtoPackage");
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

    // ===================================================================================
    //                                                                              Mapper
    //                                                                              ======
    public String getMapperPackage() {
        return getPropertyIfNullEmpty("dtoMapperPackage");
    }

    public boolean isUseDtoMapper() {
        final String dtoMapperPackage = getMapperPackage();
        return dtoMapperPackage != null && dtoMapperPackage.trim().length() > 0;
    }

    // ===================================================================================
    //                                                                       Variable Name
    //                                                                       =============
    public String getVariableInitCharType() {
        return getPropertyIfNullEmpty("variableInitCharType");
    }

    public boolean isVariableNonPrefix() {
        return isProperty("isVariableNonPrefix", false);
    }

    public String buildVariableName(String javaName) {
        final String variableInitCharType = getVariableInitCharType();
        final boolean nonPrefix = isVariableNonPrefix();
        return doBuildVariableName(javaName, variableInitCharType, nonPrefix);
    }

    protected static String doBuildVariableName(String javaName, String variableInitCharType, boolean nonPrefix) {
        final String defaultType = "UNCAP";
        if (Srl.is_Null_or_TrimmedEmpty(variableInitCharType)) {
            variableInitCharType = defaultType;
        }
        if (Srl.equalsIgnoreCase(variableInitCharType, "BEANS")) {
            return doBuildVariableName(javaName, true, false, nonPrefix);
        } else if (Srl.equalsIgnoreCase(variableInitCharType, "CAP")) {
            return doBuildVariableName(javaName, false, true, nonPrefix);
        } else if (Srl.equalsIgnoreCase(variableInitCharType, defaultType)) {
            return doBuildVariableName(javaName, false, false, nonPrefix);
        } else {
            String msg = "Unknown variableInitCharType: " + variableInitCharType;
            throw new DfIllegalPropertySettingException(msg);
        }
    }

    protected static String doBuildVariableName(String javaName, boolean initBeansProp, boolean initCap,
            boolean nonPrefix) {
        String name = javaName;
        if (initBeansProp) {
            name = Srl.initBeansProp(name);
        } else {
            if (initCap) {
                name = Srl.initCap(name);
            } else {
                name = Srl.initUncap(name);
            }
        }
        if (!nonPrefix) {
            name = Srl.connectPrefix(name, "_", "");
        }
        return name;
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    protected String getPropertyRequired(String key) {
        final String value = getProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " simpleDtoDefinitionMap=" + getSimpleDtoDefinitionMap();
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
        return (String) getSimpleDtoDefinitionMap().get(key);
    }

    protected boolean isProperty(String key, boolean defaultValue) {
        return isProperty(key, defaultValue, getSimpleDtoDefinitionMap());
    }
}