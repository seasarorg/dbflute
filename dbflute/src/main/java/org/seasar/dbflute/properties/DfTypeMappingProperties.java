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
    //                                                                 JDBC to Java Native
    //                                                                 ===================
    protected Map<String, Object> _jdbcToJavaNativeMap;

    public Map<String, Object> getJdbcToJavaNativeMap() {
        if (_jdbcToJavaNativeMap != null) {
            return _jdbcToJavaNativeMap;
        }
        if (getBasicProperties().isTargetLanguageJava()) {
            _jdbcToJavaNativeMap = mapProp("torque.jdbcToJavaNativeMap", getLanguageMetaData().getJdbcToJavaNativeMap());
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
        
        _jdbcToJavaNativeMap = mapProp("torque.jdbcToJavaNativeMap", DEFAULT_EMPTY_MAP);
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

    protected List<Object> _javaNativeStringList;

    public List<Object> getJavaNativeStringList() {
        if (_javaNativeStringList == null) {
            _javaNativeStringList = listProp("torque.javaNativeStringList", getLanguageMetaData().getStringList());
        }
        return _javaNativeStringList;
    }

    protected List<Object> _javaNativeBooleanList;

    public List<Object> getJavaNativeBooleanList() {
        if (_javaNativeBooleanList == null) {
            _javaNativeBooleanList = listProp("torque.javaNativeBooleanList", getLanguageMetaData().getBooleanList());
        }
        return _javaNativeBooleanList;
    }

    protected List<Object> _javaNativeNumberList;

    public List<Object> getJavaNativeNumberList() {
        if (_javaNativeNumberList == null) {
            _javaNativeNumberList = listProp("torque.javaNativeNumberList", getLanguageMetaData().getNumberList());
        }
        return _javaNativeNumberList;
    }

    protected List<Object> _javaNativeDateList;

    public List<Object> getJavaNativeDateList() {
        if (_javaNativeDateList == null) {
            _javaNativeDateList = listProp("torque.javaNativeDateList", getLanguageMetaData().getDateList());
        }
        return _javaNativeDateList;
    }

    protected List<Object> _javaNativeBinaryList;

    public List<Object> getJavaNativeBinaryList() {
        if (_javaNativeBinaryList == null) {
            _javaNativeBinaryList = listProp("torque.javaNativeBinaryList", getLanguageMetaData().getBinaryList());
        }
        return _javaNativeBinaryList;

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

    // ===================================================================================
    //                                                                               Other
    //                                                                               =====
    public String getJdbcToJavaNativeAsStringRemovedLineSeparator() {
        final String property = stringProp("torque.jdbcToJavaNativeMap", DEFAULT_EMPTY_MAP_STRING);
        return removeNewLine(property);
    }
}