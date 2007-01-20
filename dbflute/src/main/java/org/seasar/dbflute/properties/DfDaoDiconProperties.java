package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;

public final class DfDaoDiconProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfDaoDiconProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                               Properties - DaoDicon Information
    //                                               =================================
    public String getDaoDiconNamespace() {
        return stringProp("torque.daoDiconNamespace", "dbflute");
    }

    public String getDaoDiconPackageName() {
        return stringProp("torque.daoDiconPackageName", "");
    }

    public List<String> getDaoDiconPackageNameList() {
        final String diconSeparatedString = stringProp("torque.daoDiconPackageName", "");
        final String[] array = diconSeparatedString.split(";");
        final List<String> ls = new ArrayList<String>();
        for (String string : array) {
            ls.add(string.trim());
        }
        return ls;
    }

    public String getDaoDiconFileName() {
        return stringProp("torque.daoDiconFileName", "dbflute.dicon");
    }

    public String getJdbcDiconResourceName() {
        final String prop = stringProp("torque.jdbcDiconResourceName", null);
        if (prop != null) {
            return prop;
        } else {
            String defaultValue = getLanguageDependencyInfo().getDBFluteDiconDefault().getJdbcDiconResourceName();
            return stringProp("torque.j2eeDiconResourceName", defaultValue);
        }
    }

    public String getRequiredTxComponentName() {
        String defaultValue = getLanguageDependencyInfo().getDBFluteDiconDefault().getRequiredTxComponentName();
        return stringProp("torque.requiredTxComponentName", defaultValue);
    }

    public String getRequiresNewTxComponentName() {
        String defaultValue = getLanguageDependencyInfo().getDBFluteDiconDefault().getRequiresNewTxComponentName();
        return stringProp("torque.requiresNewTxComponentName", defaultValue);
    }

    protected DfLanguageDependencyInfo getLanguageDependencyInfo() {
        return getBasicProperties().getLanguageDependencyInfo();
    }

    public static final String KEY_daoDiconOtherIncludeDefinitionMap = "daoDiconOtherIncludeDefinitionMap";
    protected Map<String, Object> _daoDiconOtherIncludeDefinitionMap;

    public Map<String, Object> getDaoDiconOtherIncludeDefinitionMap() {
        if (_daoDiconOtherIncludeDefinitionMap == null) {
            _daoDiconOtherIncludeDefinitionMap = mapProp("torque." + KEY_daoDiconOtherIncludeDefinitionMap,
                    DEFAULT_EMPTY_MAP);
        }
        return _daoDiconOtherIncludeDefinitionMap;
    }

    public List<String> getDaoDiconOtherIncludePathList() {
        return new ArrayList<String>(getDaoDiconOtherIncludeDefinitionMap().keySet());
    }

    // ===============================================================================
    //                                               Properties - OriginalDaoComponent
    //                                               =================================
    public static final String KEY_originalDaoComponentMap = "originalDaoComponentMap";
    protected Map<String, Map<String, String>> _originalDaoComponentMap;
    protected Map<String, String> _isDaoMap = new LinkedHashMap<String, String>();

    public Map<String, Map<String, String>> getOriginalDaoComponentMap() {
        if (_originalDaoComponentMap == null) {
            _originalDaoComponentMap = new LinkedHashMap<String, Map<String, String>>();

            final Map<String, Object> generatedMap = mapProp("torque." + KEY_originalDaoComponentMap, DEFAULT_EMPTY_MAP);
            final Set<String> keySet = generatedMap.keySet();
            for (String key : keySet) {
                final Map<String, String> aspectDefinition = (Map<String, String>) generatedMap.get(key);

                if (key.startsWith("*")) {
                    final String realKey = key.substring("*".length());
                    _isDaoMap.put(realKey, "dummy");
                    _originalDaoComponentMap.put(realKey, aspectDefinition);
                } else {
                    _originalDaoComponentMap.put(key, aspectDefinition);
                }
            }
        }
        return _originalDaoComponentMap;
    }

    public List<String> getOriginalDaoComponentComponentNameList() {
        return new ArrayList<String>(getOriginalDaoComponentMap().keySet());
    }

    public String getOriginalDaoComponentClassName(String componentName) {
        final Map<String, String> aspectDefinition = getOriginalDaoComponentMap().get(componentName);
        return aspectDefinition.get("className");
    }

    public boolean isDaoComponent(String componentName) {
        return _isDaoMap.containsKey(componentName);
    }
}