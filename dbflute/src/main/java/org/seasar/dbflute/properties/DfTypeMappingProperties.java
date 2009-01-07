package org.seasar.dbflute.properties;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;

/**
 * @author jflute
 * @since 0.5.8 (2007/11/27 Tuesday)
 */
public final class DfTypeMappingProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfTypeMappingProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                    Type Mapping Map
    //                                                                    ================
    public static final String KEY_typeMappingMap = "typeMappingMap";
    protected Map<String, Object> _typeMappingMap;

    public Map<String, Object> getTypeMappingMap() {
        if (_typeMappingMap == null) {
            _typeMappingMap = mapProp("torque." + KEY_typeMappingMap, DEFAULT_EMPTY_MAP);
        }
        return _typeMappingMap;
    }

    // ===================================================================================
    //                                                                 JDBC to Java Native
    //                                                                 ===================
    protected Map<String, Object> _jdbcToJavaNativeMap;

    public Map<String, Object> getJdbcToJavaNativeMap() {
        if (_jdbcToJavaNativeMap != null) {
            return _jdbcToJavaNativeMap;
        }

        if (getBasicProperties().isTargetLanguageJava()) {
            // * * *
            // Java
            // * * *
            Map<String, Object> typeMappingMap = getTypeMappingMap();
            if (typeMappingMap.isEmpty()) {
                Map<String, Object> defaultMap = getLanguageMetaData().getJdbcToJavaNativeMap(); // Actually Empty
                _jdbcToJavaNativeMap = mapProp("torque.jdbcToJavaNativeMap", defaultMap);
            } else {
                _jdbcToJavaNativeMap = typeMappingMap;
            }
            return _jdbcToJavaNativeMap;
        }

        // * * * * *
        // Not Java
        // * * * * *
        final Map<String, Object> metaMap = getLanguageMetaData().getJdbcToJavaNativeMap();
        if (metaMap.isEmpty()) {
            String msg = "The jdbcToJavaNamtiveMap should not be null: metaData=" + getLanguageMetaData();
            throw new IllegalStateException(msg);
        }

        Map<String, Object> typeMappingMap = getTypeMappingMap();
        if (typeMappingMap.isEmpty()) {
            Map<String, Object> defaultMap = getLanguageMetaData().getJdbcToJavaNativeMap();
            _jdbcToJavaNativeMap = mapProp("torque.jdbcToJavaNativeMap", defaultMap);
        } else {
            _jdbcToJavaNativeMap = typeMappingMap;
        }
        if (_jdbcToJavaNativeMap.isEmpty()) {
            _jdbcToJavaNativeMap = metaMap;
            return _jdbcToJavaNativeMap;
        }

        // Reflect meta map to native map only difference.
        final Set<String> keySet = metaMap.keySet();
        for (String key : keySet) {
            final Object value = metaMap.get(key);
            if (!_jdbcToJavaNativeMap.containsKey(key)) {
                _jdbcToJavaNativeMap.put(key, value);
            }
        }
        return _jdbcToJavaNativeMap;
    }

    // ===================================================================================
    //                                                               Java Native Type List
    //                                                               =====================
    public List<Object> getJavaNativeStringList() { // It's not property!
        return getLanguageMetaData().getStringList();
    }

    public List<Object> getJavaNativeBooleanList() { // It's not property!
        return getLanguageMetaData().getBooleanList();
    }

    public List<Object> getJavaNativeNumberList() { // It's not property!
        return getLanguageMetaData().getNumberList();
    }

    public List<Object> getJavaNativeDateList() { // It's not property!
        return getLanguageMetaData().getDateList();
    }

    public List<Object> getJavaNativeBinaryList() { // It's not property!
        return getLanguageMetaData().getBinaryList();

    }

    // ===================================================================================
    //                                                                  Language Meta Data
    //                                                                  ==================
    protected LanguageMetaData _languageMetaData;

    protected LanguageMetaData getLanguageMetaData() {
        if (_languageMetaData != null) {
            return _languageMetaData;
        }
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        _languageMetaData = languageDependencyInfo.createLanguageMetaData();
        return _languageMetaData;
    }
}