package org.seasar.dbflute.properties;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.util.DfCollectionUtil;
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
        return !isEmptyDefinition() && hasDefinitionProperty("baseDtoPackage");
    }

    public boolean hasSimpleCDefDefinition() {
        return !isEmptyDefinition() && hasDefinitionProperty("simpleCDefClass");
    }

    protected boolean isEmptyDefinition() {
        return getSimpleDtoDefinitionMap().isEmpty();
    }

    protected boolean hasDefinitionProperty(String key) {
        return getSimpleDtoDefinitionMap().containsKey(key);
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public String getSimpleDtoOutputDirectory() {
        final String value = (String) getSimpleDtoDefinitionMap().get("simpleDtoOutputDirectory");
        return doGetOutputDirectory(value);
    }

    public String getDtoMapperOutputDirectory() {
        final String value = (String) getSimpleDtoDefinitionMap().get("dtoMapperOutputDirectory");
        return doGetOutputDirectory(value);
    }

    public String getSimpleCDefOutputDirectory() {
        final String value = (String) getSimpleDtoDefinitionMap().get("simpleCDefOutputDirectory");
        return doGetOutputDirectory(value);
    }

    protected String doGetOutputDirectory(String value) {
        final String baseDir = getBasicProperties().getGenerateOutputDirectory();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(value)) {
            if (value.startsWith("~/")) {
                return "./" + Srl.substringFirstRear(value, "~/");
            } else {
                return baseDir + "/" + value;
            }
        } else {
            return baseDir;
        }
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

    public String deriveExtendedDtoClassName(String baseDtoClassName) {
        String name = Srl.substringFirstRear(baseDtoClassName, getBaseDtoPrefix());
        name = Srl.substringLastFront(name, getBaseDtoSuffix());
        final String prefix = getExtendedDtoPrefix();
        final String suffix = getExtendedDtoSuffix();
        return prefix + name + suffix;
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

    public String getMapperPrefix() { // basically only for old class delete determination
        return getExtendedDtoPrefix(); // mapper name is based on extended DTO name
    }

    public String getMapperSuffix() { // used for building class name
        return "Mapper";
    }

    // ===================================================================================
    //                                                                           CDef Info
    //                                                                           =========
    public String getSimpleCDefClass() {
        return getPropertyRequired("simpleCDefClass");
    }

    public String getSimpleCDefPackage() {
        return getPropertyRequired("simpleCDefPackage");
    }

    protected Set<String> _simpleCDefTargetSet;

    protected Set<String> getSimpleCDefTargetSet() {
        if (_simpleCDefTargetSet != null) {
            return _simpleCDefTargetSet;
        }
        final Object obj = getSimpleDtoDefinitionMap().get("simpleCDefTargetList");
        if (obj == null) {
            _simpleCDefTargetSet = DfCollectionUtil.emptySet();
            return _simpleCDefTargetSet;
        }
        @SuppressWarnings("unchecked")
        final List<String> targetList = (List<String>) obj;
        _simpleCDefTargetSet = DfCollectionUtil.newHashSet(targetList);
        return _simpleCDefTargetSet;
    }

    public boolean isSimpleCDefTarget(String classificationName) {
        final Set<String> targetSet = getSimpleCDefTargetSet();
        if (targetSet.isEmpty()) {
            return true;
        }
        return targetSet.contains(classificationName);
    }

    public boolean isClassificationDeployment() { //  if true, SimpleCDef should be true
        return isProperty("isClassificationDeployment", false, getSimpleDtoDefinitionMap());
    }

    // ===================================================================================
    //                                                                              JSONIC
    //                                                                              ======
    protected Map<String, String> _jsonicDecorationMap;

    protected Map<String, String> getJSonicDecorationMap() {
        if (_jsonicDecorationMap != null) {
            return _jsonicDecorationMap;
        }
        final String key = "jsonicDecorationMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getSimpleDtoDefinitionMap().get(key);
        if (map != null) {
            _jsonicDecorationMap = map;
        } else {
            _jsonicDecorationMap = DfCollectionUtil.emptyMap();
        }
        return _jsonicDecorationMap;
    }

    public boolean hasJsonicDecorationDatePattern() {
        return getJsonicDecorationDatePattern() != null;
    }

    public String getJsonicDecorationDatePattern() {
        return getJSonicDecorationMap().get("datePattern");
    }

    public boolean hasJsonicDecorationTimestampPattern() {
        return getJsonicDecorationTimestampPattern() != null;
    }

    public String getJsonicDecorationTimestampPattern() {
        return getJSonicDecorationMap().get("timestampPattern");
    }

    public boolean hasJsonicDecorationTimePattern() {
        return getJsonicDecorationTimePattern() != null;
    }

    public String getJsonicDecorationTimePattern() {
        return getJSonicDecorationMap().get("timePattern");
    }

    // ===================================================================================
    //                                                                                 GWT
    //                                                                                 ===
    protected Map<String, String> _gwtDecorationMap;

    protected Map<String, String> getGwtDecorationMap() {
        if (_gwtDecorationMap != null) {
            return _gwtDecorationMap;
        }
        final String key = "gwtDecorationMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getSimpleDtoDefinitionMap().get(key);
        if (map != null) {
            _gwtDecorationMap = map;
        } else {
            _gwtDecorationMap = DfCollectionUtil.emptyMap();
        }
        return _gwtDecorationMap;
    }

    public boolean isGwtDecorationSuppressJavaDependency() {
        return isProperty("isSuppressJavaDependency", false, getGwtDecorationMap());
    }

    // ===================================================================================
    //                                                                          Field Name
    //                                                                          ==========
    public String getFieldInitCharType() {
        return getPropertyIfNullEmpty("fieldInitCharType");
    }

    public boolean isFieldNonPrefix() {
        return isProperty("isFieldNonPrefix", false);
    }

    public String buildFieldName(String javaName) {
        final String fieldInitCharType = getFieldInitCharType();
        final boolean nonPrefix = isFieldNonPrefix();
        return doBuildFieldName(javaName, fieldInitCharType, nonPrefix);
    }

    protected static String doBuildFieldName(String javaName, String fieldInitCharType, boolean nonPrefix) {
        final String defaultType = "UNCAP";
        if (Srl.is_Null_or_TrimmedEmpty(fieldInitCharType)) {
            fieldInitCharType = defaultType;
        }
        if (Srl.equalsIgnoreCase(fieldInitCharType, "BEANS")) {
            return doBuildFieldName(javaName, true, false, nonPrefix);
        } else if (Srl.equalsIgnoreCase(fieldInitCharType, "CAP")) {
            return doBuildFieldName(javaName, false, true, nonPrefix);
        } else if (Srl.equalsIgnoreCase(fieldInitCharType, defaultType)) {
            return doBuildFieldName(javaName, false, false, nonPrefix);
        } else {
            String msg = "Unknown fieldInitCharType: " + fieldInitCharType;
            throw new DfIllegalPropertySettingException(msg);
        }
    }

    protected static String doBuildFieldName(String javaName, boolean initBeansProp, boolean initCap, boolean nonPrefix) {
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