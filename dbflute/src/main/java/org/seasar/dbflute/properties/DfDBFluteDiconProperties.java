package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.framework.util.StringUtil;

public final class DfDBFluteDiconProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfDBFluteDiconProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                               Properties - DaoDicon Information
    //                                               =================================
    public String getDBFluteDiconNamespace() {
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        final String prop = stringProp("torque.dbfluteDiconNamespace", null);
        if (prop != null) {
            return prop;
        } else {
            return stringProp("torque.daoDiconNamespace", diconDefault.getDBFluteDiconNamespace());
        }
    }

    public String getDBFluteDiconPackageName() {
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        String realDiconDefault = diconDefault.getDBFluteDiconPackageName();

        // CSharpの場合のみ、TopのNamesapceをFilterする。
        if (getBasicProperties().isTargetLanguageCSharp()) {
            realDiconDefault = filterDiconForTopNamespace(realDiconDefault);
        }

        final String prop = stringProp("torque.dbfluteDiconPackageName", null);
        if (prop != null) {
            return prop;
        } else {
            return stringProp("torque.daoDiconPackageName", realDiconDefault);
        }
    }

    public List<String> getDBFluteDiconPackageNameList() {
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        String realDiconDefault = diconDefault.getDBFluteDiconPackageName();

        // CSharpの場合のみ、TopのNamesapceをFilterする。
        if (getBasicProperties().isTargetLanguageCSharp()) {
            realDiconDefault = filterDiconForTopNamespace(realDiconDefault);
        }

        final String diconSeparatedString;
        final String prop = stringProp("torque.dbfluteDiconPackageName", null);
        if (prop != null) {
            diconSeparatedString = prop;
        } else {
            diconSeparatedString = stringProp("torque.daoDiconPackageName", realDiconDefault);
        }
        final String[] array = diconSeparatedString.split(";");
        final List<String> ls = new ArrayList<String>();
        for (String string : array) {
            ls.add(string.trim());
        }
        return ls;
    }

    public String getDBFluteDiconFileName() {
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        final String prop = stringProp("torque.dbfluteDiconFileName", null);
        if (prop != null) {
            return prop;
        } else {
            return stringProp("torque.daoDiconFileName", diconDefault.getDBFluteDiconFileName());
        }
    }

    public String getDBFluteCreatorDiconFileName() {
        return stringProp("torque.dbfluteCreatorDiconFileName", "dbflute-creator.dicon");
    }

    public String getDBFluteCustomizerDiconFileName() {
        return stringProp("torque.dbfluteCustomizerDiconFileName", "dbflute-customizer.dicon");
    }

    public String getJdbcDiconResourceName() {
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        String realDiconDefault = diconDefault.getJ2eeDiconResourceName();

        // CSharpの場合のみ、TopのNamesapceをFilterする。
        if (getBasicProperties().isTargetLanguageCSharp()) {
            realDiconDefault = filterDiconForTopNamespace(realDiconDefault);
        }

        final String prop = stringProp("torque.j2eeDiconResourceName", null);
        if (prop != null) {
            return prop;
        } else {
            return stringProp("torque.jdbcDiconResourceName", realDiconDefault);
        }
    }

    public String getRequiredTxComponentName() {
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        final String defaultValue = diconDefault.getRequiredTxComponentName();
        return stringProp("torque.requiredTxComponentName", defaultValue);
    }

    public String getRequiresNewTxComponentName() {
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        final String defaultValue = diconDefault.getRequiresNewTxComponentName();
        return stringProp("torque.requiresNewTxComponentName", defaultValue);
    }

    protected DfLanguageDependencyInfo getLanguageDependencyInfo() {
        return getBasicProperties().getLanguageDependencyInfo();
    }

    protected DfDefaultDBFluteDicon getDefaultDBFluteDicon() {
        return getLanguageDependencyInfo().getDefaultDBFluteDicon();
    }

    protected String filterDiconForTopNamespace(String realDiconDefault) {
        if (getBasicProperties().isTargetLanguageCSharp()) {
            final DfGeneratedClassPackageProperties generatedClassPackageProperties = getGeneratedClassPackageProperties();
            final String baseCommonPackage = generatedClassPackageProperties.getBaseCommonPackage();
            final String topNamespace;
            if (baseCommonPackage.indexOf(".") >= 0) {
                topNamespace = baseCommonPackage.substring(0, baseCommonPackage.indexOf("."));
            } else {
                topNamespace = baseCommonPackage;
            }
            realDiconDefault = StringUtil.replace(realDiconDefault, "${topNamespace}", topNamespace);
        }
        return realDiconDefault;
    }

    // ===============================================================================
    //                                      Properties - dbfluteDiconBeforeJ2eeInclude
    //                                      ==========================================
    public static final String KEY_dbfluteDiconBeforeJ2eeIncludeDefinitionMap = "dbfluteDiconBeforeJ2eeIncludeDefinitionMap";
    public static final String KEY_daoDiconBeforeJ2eeIncludeDefinitionMap = "daoDiconBeforeJ2eeIncludeDefinitionMap";
    protected Map<String, Object> _dbfluteDiconBeforeJ2eeIncludeDefinitionMap;

    public Map<String, Object> getDBFluteDiconBeforeJ2eeIncludeDefinitionMap() {
        if (_dbfluteDiconBeforeJ2eeIncludeDefinitionMap != null) {
            return _dbfluteDiconBeforeJ2eeIncludeDefinitionMap;
        }
        _dbfluteDiconBeforeJ2eeIncludeDefinitionMap = mapProp("torque."
                + KEY_dbfluteDiconBeforeJ2eeIncludeDefinitionMap, null);
        if (_dbfluteDiconBeforeJ2eeIncludeDefinitionMap != null) {
            return _dbfluteDiconBeforeJ2eeIncludeDefinitionMap;
        }
        _dbfluteDiconBeforeJ2eeIncludeDefinitionMap = mapProp("torque." + KEY_daoDiconBeforeJ2eeIncludeDefinitionMap,
                DEFAULT_EMPTY_MAP);
        return _dbfluteDiconBeforeJ2eeIncludeDefinitionMap;
    }

    public List<String> getDBFluteDiconBeforeJ2eeIncludePathList() {
        return new ArrayList<String>(getDBFluteDiconBeforeJ2eeIncludeDefinitionMap().keySet());
    }

    // ===============================================================================
    //                                           Properties - dbfluteDiconOtherInclude
    //                                           =====================================
    public static final String KEY_dbfluteDiconOtherIncludeDefinitionMap = "dbfluteDiconOtherIncludeDefinitionMap";
    public static final String KEY_daoDiconOtherIncludeDefinitionMap = "daoDiconOtherIncludeDefinitionMap";
    protected Map<String, Object> _dbfluteDiconOtherIncludeDefinitionMap;

    public Map<String, Object> getDBFluteDiconOtherIncludeDefinitionMap() {
        if (_dbfluteDiconOtherIncludeDefinitionMap != null) {
            return _dbfluteDiconOtherIncludeDefinitionMap;
        }
        _dbfluteDiconOtherIncludeDefinitionMap = mapProp("torque." + KEY_dbfluteDiconOtherIncludeDefinitionMap, null);
        if (_dbfluteDiconOtherIncludeDefinitionMap != null) {
            return _dbfluteDiconOtherIncludeDefinitionMap;
        }
        _dbfluteDiconOtherIncludeDefinitionMap = mapProp("torque." + KEY_daoDiconOtherIncludeDefinitionMap,
                DEFAULT_EMPTY_MAP);
        return _dbfluteDiconOtherIncludeDefinitionMap;
    }

    public List<String> getDBFluteDiconOtherIncludePathList() {
        return new ArrayList<String>(getDBFluteDiconOtherIncludeDefinitionMap().keySet());
    }

    // ===============================================================================
    //                                               Properties - originalDaoComponent
    //                                               =================================
    public static final String KEY_originalDBFluteoComponentMap = "originalDBFluteComponentMap";
    public static final String KEY_originalDaoComponentMap = "originalDaoComponentMap";
    protected Map<String, Map<String, String>> _originalDBFluteComponentMap;
    protected Map<String, String> _isDaoMap = new LinkedHashMap<String, String>();

    public Map<String, Map<String, String>> getOriginalDBFluteComponentMap() {
        if (_originalDBFluteComponentMap != null) {
            return _originalDBFluteComponentMap;
        }

        _originalDBFluteComponentMap = new LinkedHashMap<String, Map<String, String>>();

        Map<String, Object> generatedMap = mapProp("torque." + KEY_originalDBFluteoComponentMap, null);
        if (generatedMap == null) {
            generatedMap = mapProp("torque." + KEY_originalDaoComponentMap, DEFAULT_EMPTY_MAP);
        }

        final Set<String> keySet = generatedMap.keySet();
        for (String key : keySet) {
            // TODO: @jflute - もう一段階真面目に展開すること。
            final Map<String, String> aspectDefinition = (Map<String, String>) generatedMap.get(key);

            if (key.startsWith("*")) {
                final String realKey = key.substring("*".length());
                _isDaoMap.put(realKey, "dummy");
                _originalDBFluteComponentMap.put(realKey, aspectDefinition);
            } else {
                _originalDBFluteComponentMap.put(key, aspectDefinition);
            }
        }
        return _originalDBFluteComponentMap;
    }

    public List<String> getOriginalDBFluteComponentComponentNameList() {
        return new ArrayList<String>(getOriginalDBFluteComponentMap().keySet());
    }

    public String getOriginalDBFluteComponentClassName(String componentName) {
        final Map<String, String> aspectDefinition = getOriginalDBFluteComponentMap().get(componentName);
        return aspectDefinition.get("className");
    }

    public boolean isDBFluteComponent(String componentName) {
        return _isDaoMap.containsKey(componentName);
    }

    public boolean isAvailableBehaviorRequiresNewTx() {
        return booleanProp("torque.isAvailableBehaviorRequiresNewTx", false);
    }

    public boolean isAvailableBehaviorRequiredTx() {
        return booleanProp("torque.isAvailableBehaviorRequiredTx", false);
    }
    
    // ===================================================================================
    //                                                                        DBFluteBeans
    //                                                                        ============
    public String getDBFluteBeansFileName() {
        return stringProp("torque.dbfluteBeansFileName", "dbfluteBeans.xml");
    }
}