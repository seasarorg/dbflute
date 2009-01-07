package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;

public final class DfDependencyInjectionProperties extends DfAbstractHelperProperties {

    public DfDependencyInjectionProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                            Dependency Injection Map
    //                                                            ========================
    public static final String KEY_dependencyInjectionMap = "dependencyInjectionMap";
    protected Map<String, Object> _dependencyInjectionMap;

    public Map<String, Object> getDependencyInjectionMap() {
        if (_dependencyInjectionMap == null) {
            _dependencyInjectionMap = mapProp("torque." + KEY_dependencyInjectionMap, DEFAULT_EMPTY_MAP);
        }
        return _dependencyInjectionMap;
    }

    public String getProperty(String key, String defaultValue) {
        Map<String, Object> map = getDependencyInjectionMap();
        Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be string:";
                msg = msg + " " + obj.getClass().getSimpleName() + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value;
            } else {
                return defaultValue;
            }
        }
        return stringProp("torque." + key, defaultValue);
    }

    public boolean isProperty(String key, boolean defaultValue) {
        Map<String, Object> map = getDependencyInjectionMap();
        Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be boolean:";
                msg = msg + " " + obj.getClass().getSimpleName() + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value.trim().equalsIgnoreCase("true");
            } else {
                return defaultValue;
            }
        }
        return booleanProp("torque." + key, defaultValue);
    }

    public Map<String, Object> getPropertyAsMap(String key, Map<String, Object> defaultValue) {
        Map<String, Object> map = getDependencyInjectionMap();
        Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof Map)) {
                String msg = "The key's value should be map:";
                msg = msg + " " + obj.getClass().getSimpleName() + "=" + obj;
                throw new IllegalStateException(msg);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> value = (Map<String, Object>) obj;
            if (!value.isEmpty()) {
                return value;
            } else {
                return defaultValue;
            }
        }
        return mapProp("torque." + key, defaultValue);
    }

    // ===================================================================================
    //                                                                       Dicon(Seasar)
    //                                                                       =============
    public String getDBFluteDiconNamespace() { // Java Only
        return getProperty("dbfluteDiconNamespace", getDefaultDBFluteDicon().getDBFluteDiconNamespace());
    }

    public List<String> getDBFluteDiconPackageNameList() { // Java Only
        final String prop = getProperty("dbfluteDiconPackageName", getDefaultDBFluteDicon()
                .getDBFluteDiconPackageName());
        final String[] array = prop.split(";");
        final List<String> ls = new ArrayList<String>();
        for (String string : array) {
            ls.add(string.trim());
        }
        return ls;
    }

    public String getDBFluteDiconFileName() { // Java Only
        return getProperty("dbfluteDiconFileName", getDefaultDBFluteDicon().getDBFluteDiconFileName());
    }

    public String getDBFluteCreatorDiconFileName() { // Java Only
        return getProperty("dbfluteCreatorDiconFileName", "dbflute-creator.dicon");
    }

    public String getDBFluteCustomizerDiconFileName() { // Java Only
        return getProperty("dbfluteCustomizerDiconFileName", "dbflute-customizer.dicon");
    }

    public String getJ2eeDiconResourceName() { // Java Only
        return getProperty("j2eeDiconResourceName", getDefaultDBFluteDicon().getJ2eeDiconResourceName());
    }

    public String getRequiredTxComponentName() { // Java Only
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        final String defaultValue = diconDefault.getRequiredTxComponentName();
        return getProperty("requiredTxComponentName", defaultValue);
    }

    public String getRequiresNewTxComponentName() { // Java Only
        final DfDefaultDBFluteDicon diconDefault = getDefaultDBFluteDicon();
        final String defaultValue = diconDefault.getRequiresNewTxComponentName();
        return getProperty("requiresNewTxComponentName", defaultValue);
    }

    protected DfLanguageDependencyInfo getLanguageDependencyInfo() {
        return getBasicProperties().getLanguageDependencyInfo();
    }

    protected DfDefaultDBFluteDicon getDefaultDBFluteDicon() {
        return getLanguageDependencyInfo().getDefaultDBFluteDicon();
    }

    public static final String KEY_dbfluteDiconBeforeJ2eeIncludeDefinitionMap = "dbfluteDiconBeforeJ2eeIncludeDefinitionMap";
    protected Map<String, Object> _dbfluteDiconBeforeJ2eeIncludeDefinitionMap;

    public Map<String, Object> getDBFluteDiconBeforeJ2eeIncludeDefinitionMap() {
        if (_dbfluteDiconBeforeJ2eeIncludeDefinitionMap != null) {
            return _dbfluteDiconBeforeJ2eeIncludeDefinitionMap;
        }
        String key = KEY_dbfluteDiconBeforeJ2eeIncludeDefinitionMap;
        _dbfluteDiconBeforeJ2eeIncludeDefinitionMap = getPropertyAsMap(key, DEFAULT_EMPTY_MAP);
        return _dbfluteDiconBeforeJ2eeIncludeDefinitionMap;
    }

    public List<String> getDBFluteDiconBeforeJ2eeIncludePathList() {
        return new ArrayList<String>(getDBFluteDiconBeforeJ2eeIncludeDefinitionMap().keySet());
    }

    public static final String KEY_dbfluteDiconOtherIncludeDefinitionMap = "dbfluteDiconOtherIncludeDefinitionMap";
    protected Map<String, Object> _dbfluteDiconOtherIncludeDefinitionMap;

    public Map<String, Object> getDBFluteDiconOtherIncludeDefinitionMap() {
        if (_dbfluteDiconOtherIncludeDefinitionMap != null) {
            return _dbfluteDiconOtherIncludeDefinitionMap;
        }
        String key = KEY_dbfluteDiconOtherIncludeDefinitionMap;
        _dbfluteDiconOtherIncludeDefinitionMap = getPropertyAsMap(key, DEFAULT_EMPTY_MAP);
        return _dbfluteDiconOtherIncludeDefinitionMap;
    }

    public List<String> getDBFluteDiconOtherIncludePathList() {
        return new ArrayList<String>(getDBFluteDiconOtherIncludeDefinitionMap().keySet());
    }

    // ===================================================================================
    //                                                                DBFluteBeans(Spring)
    //                                                                ====================
    public String getDBFluteBeansFileName() {
        return getProperty("dbfluteBeansFileName", "dbfluteBeans.xml");
    }

    // ===================================================================================
    //                                                             Quill DataSource(Quill)
    //                                                             =======================
    public boolean isQuillDataSourceNameValid() {
        String name = getQuillDataSourceName();
        return name != null && name.trim().length() > 0 && !name.trim().equalsIgnoreCase("null");
    }

    public String getQuillDataSourceName() {
        return getProperty("quillDataSourceName", null);
    }
}