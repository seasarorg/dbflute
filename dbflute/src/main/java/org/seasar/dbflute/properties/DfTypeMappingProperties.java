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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.generate.language.DfLanguageDependency;
import org.seasar.dbflute.logic.generate.language.typemapping.DfLanguageTypeMapping;
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
        final Map<String, Object> jdbcTypeMappingMap = newLinkedHashMap();
        for (Entry<String, Object> entry : typeMappingMap.entrySet()) {
            final String key = entry.getKey();
            if (isJdbcTypeMappingKey(key)) {
                jdbcTypeMappingMap.put(key, (String) entry.getValue());
            }
        }
        return jdbcTypeMappingMap;
    }

    protected static boolean isJdbcTypeMappingKey(String key) {
        return !isNameTypeMappingKey(key) && !isPointTypeMappingKey(key);
    }

    // -----------------------------------------------------
    //                                 Name Type Mapping Map
    //                                 ---------------------
    protected Map<String, String> getNameTypeMappingMap() {
        final Map<String, Object> typeMappingMap = getTypeMappingMap();
        final Map<String, String> nameTypeMappingMap = newLinkedHashMap();
        for (Entry<String, Object> entry : typeMappingMap.entrySet()) {
            final String key = entry.getKey();
            if (isNameTypeMappingKey(key)) {
                nameTypeMappingMap.put(extractDbTypeName(key), (String) entry.getValue());
            }
        }
        return nameTypeMappingMap;
    }

    protected static boolean isNameTypeMappingKey(String key) {
        if (isPointTypeMappingKey(key)) {
            return false;
        }
        return key.startsWith("$$") && key.endsWith("$$") && key.length() > "$$$$".length();
    }

    protected static String extractDbTypeName(String key) {
        final String realKey = key.substring("$$".length());
        return realKey.substring(0, realKey.length() - "$$".length());
    }

    // -----------------------------------------------------
    //                                Point Type Mapping Map
    //                                ----------------------
    protected Map<String, Map<String, String>> getPointTypeMappingMap() {
        final Map<String, Object> typeMappingMap = getTypeMappingMap();
        final Map<String, Map<String, String>> pointTypeMappingMap = StringKeyMap.createAsFlexibleOrdered();
        for (Entry<String, Object> entry : typeMappingMap.entrySet()) {
            final String key = entry.getKey();
            if (!isPointTypeMappingKey(key)) {
                continue;
            }
            final Object obj = entry.getValue();
            @SuppressWarnings("unchecked")
            final Map<String, Map<String, String>> pointMap = (Map<String, Map<String, String>>) obj;
            for (Entry<String, Map<String, String>> pointEntry : pointMap.entrySet()) {
                final String pointKey = pointEntry.getKey();
                final Map<String, String> pointElementMap = pointEntry.getValue();
                final Map<String, String> flexibleMap = StringKeyMap.createAsFlexibleOrdered();
                flexibleMap.putAll(pointElementMap);
                pointTypeMappingMap.put(pointKey, pointElementMap);
            }
        }
        return pointTypeMappingMap;
    }

    protected static boolean isPointTypeMappingKey(String key) {
        return key.startsWith("$$df:point$$");
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
            final Map<String, Object> typeMappingMap = getJdbcTypeMappingMap();
            if (typeMappingMap.isEmpty()) {
                _jdbcToJavaNativeMap = getLanguageMetaData().getJdbcToJavaNativeMap(); // actually empty
            } else {
                _jdbcToJavaNativeMap = typeMappingMap;
            }
            return _jdbcToJavaNativeMap;
        }

        // not Java here
        final Map<String, Object> metaMap = getLanguageMetaData().getJdbcToJavaNativeMap();
        if (metaMap.isEmpty()) {
            String msg = "The jdbcToJavaNamtiveMap should not be null: metaData=" + getLanguageMetaData();
            throw new IllegalStateException(msg);
        }

        final Map<String, Object> typeMappingMap = getJdbcTypeMappingMap();
        if (typeMappingMap.isEmpty()) {
            _jdbcToJavaNativeMap = getLanguageMetaData().getJdbcToJavaNativeMap();
        } else {
            _jdbcToJavaNativeMap = typeMappingMap;
        }
        if (_jdbcToJavaNativeMap.isEmpty()) {
            _jdbcToJavaNativeMap = metaMap;
            return _jdbcToJavaNativeMap;
        }

        // reflect meta map to native map only difference
        for (Entry<String, Object> entry : metaMap.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
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
    //                                                                  Point to JDBC Type
    //                                                                  ==================
    protected Map<String, Map<String, String>> _pointToJdbcTypeMap;

    public Map<String, Map<String, String>> getPointToJdbcTypeMap() {
        if (_pointToJdbcTypeMap != null) {
            return _pointToJdbcTypeMap;
        }
        _pointToJdbcTypeMap = getPointTypeMappingMap();
        return _pointToJdbcTypeMap;
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
    protected DfLanguageTypeMapping _languageMetaData;

    protected DfLanguageTypeMapping getLanguageMetaData() {
        if (_languageMetaData != null) {
            return _languageMetaData;
        }
        final DfLanguageDependency languageDependencyInfo = getBasicProperties().getLanguageDependency();
        _languageMetaData = languageDependencyInfo.getLanguageTypeMapping();
        return _languageMetaData;
    }
}