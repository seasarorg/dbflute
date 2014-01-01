/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.properties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

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

    protected Map<String, Object> getTypeMappingMap() {
        if (_typeMappingMap == null) {
            _typeMappingMap = mapProp("torque." + KEY_typeMappingMap, DEFAULT_EMPTY_MAP);
        }
        return _typeMappingMap;
    }

    // -----------------------------------------------------
    //                                 JDBC Type Mapping Map
    //                                 ---------------------
    protected Map<String, Object> getJdbcTypeMappingMap() {
        final Map<String, Object> typeMappingMap = getTypeMappingMap();
        final Map<String, Object> jdbcTypeMappingMap = DfCollectionUtil.newLinkedHashMap();
        final Set<String> keySet = typeMappingMap.keySet();
        for (final String key : keySet) {
            if (key == null) {
                String msg = "Invalid typeMappingMap! The key should not be null! But: ";
                msg = msg + " key=" + key;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            if (isNameTypeMappingKey(key)) {
                continue; // The element is for nameTypeMapping.
            }
            final Object value = typeMappingMap.get(key);
            if (value == null) {
                String msg = "Invalid typeMappingMap! The value should not be null! But: ";
                msg = msg + " key=" + key + " value=" + value;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            if (!(value instanceof String)) {
                String msg = "Invalid typeMappingMap! The type of the value should be String! But: ";
                msg = msg + " type=" + value.getClass() + " key=" + key + " value=" + value;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            final String strVal = (String) value;
            if (strVal.trim().length() == 0) {
                String msg = "Invalid typeMappingMap! The value should not be empty! But: ";
                msg = msg + " key=" + key + " value=" + value;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            jdbcTypeMappingMap.put(key, value);
        }
        return jdbcTypeMappingMap;
    }

    // -----------------------------------------------------
    //                                 Name Type Mapping Map
    //                                 ---------------------
    protected Map<String, String> getNameTypeMappingMap() {
        final Map<String, Object> typeMappingMap = getTypeMappingMap();
        final Map<String, String> nameTypeMappingMap = new LinkedHashMap<String, String>();
        final Set<String> keySet = typeMappingMap.keySet();
        for (final String key : keySet) {
            if (key == null) {
                String msg = "Invalid typeMappingMap! The key should not be null! But: ";
                msg = msg + " key=" + key;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            if (!isNameTypeMappingKey(key)) {
                continue; // The element is for jdbcTypeMapping.
            }
            final Object value = typeMappingMap.get(key);
            if (value == null) {
                String msg = "Invalid typeMappingMap! The value should not be null! But: ";
                msg = msg + " key=" + key + " value=" + value;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            if (!(value instanceof String)) {
                String msg = "Invalid typeMappingMap! The type of the value should be String! But: ";
                msg = msg + " type=" + value.getClass() + " key=" + key + " value=" + value;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            final String strVal = (String) value;
            if (strVal.trim().length() == 0) {
                String msg = "Invalid typeMappingMap! The value should not be empty! But: ";
                msg = msg + " key=" + key + " value=" + value;
                msg = msg + " typeMappingMap=" + typeMappingMap;
                throw new IllegalStateException(msg);
            }
            nameTypeMappingMap.put(extractDbTypeName(key), strVal);
        }
        return nameTypeMappingMap;
    }

    static boolean isNameTypeMappingKey(String key) {
        return key.startsWith("$$") && key.endsWith("$$") && key.length() > "$$$$".length();
    }

    static String extractDbTypeName(String key) {
        String realKey = key.substring("$$".length());
        realKey = realKey.substring(0, realKey.length() - "$$".length());
        return realKey;
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
            final Map<String, Object> typeMappingMap = getJdbcTypeMappingMap();
            if (typeMappingMap.isEmpty()) {
                _jdbcToJavaNativeMap = getLanguageMetaData().getJdbcToJavaNativeMap(); // actually empty
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

        Map<String, Object> typeMappingMap = getJdbcTypeMappingMap();
        if (typeMappingMap.isEmpty()) {
            _jdbcToJavaNativeMap = getLanguageMetaData().getJdbcToJavaNativeMap();
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
    //                                                                   Name to JDBC Type
    //                                                                   =================
    protected Map<String, String> _nameToJdbcTypeMap;

    public Map<String, String> getNameToJdbcTypeMap() {
        if (_nameToJdbcTypeMap != null) {
            return _nameToJdbcTypeMap;
        }
        _nameToJdbcTypeMap = getNameTypeMappingMap();
        return _nameToJdbcTypeMap;
    }

    // ===================================================================================
    //                                                               Java Native Type List
    //                                                               =====================
    public List<String> getJavaNativeStringList() { // not property
        return getLanguageMetaData().getStringList();
    }

    public boolean isJavaNativeStringObject(String javaNative) {
        return containsAsEndsWith(javaNative, getJavaNativeStringList());
    }

    public List<String> getJavaNativeNumberList() { // not property
        return getLanguageMetaData().getNumberList();
    }

    public boolean isJavaNativeNumberObject(String javaNative) {
        return containsAsEndsWith(javaNative, getJavaNativeNumberList());
    }

    public List<String> getJavaNativeDateList() { // not property
        return getLanguageMetaData().getDateList();
    }

    public boolean isJavaNativeDateObject(String javaNative) {
        return containsAsEndsWith(javaNative, getJavaNativeDateList());
    }

    public List<String> getJavaNativeBooleanList() { // not property
        return getLanguageMetaData().getBooleanList();
    }

    public boolean isJavaNativeBooleanObject(String javaNative) {
        return containsAsEndsWith(javaNative, getJavaNativeBooleanList());
    }

    public List<String> getJavaNativeBinaryList() { // not property
        return getLanguageMetaData().getBinaryList();
    }

    public boolean isJavaNativeBinaryObject(String javaNative) {
        return containsAsEndsWith(javaNative, getJavaNativeBinaryList());
    }

    protected boolean containsAsEndsWith(String str, List<String> suffixList) {
        return Srl.endsWithIgnoreCase(str, suffixList.toArray(new String[] {}));
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