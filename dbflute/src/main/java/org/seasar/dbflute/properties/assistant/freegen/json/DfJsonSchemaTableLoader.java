/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.properties.assistant.freegen.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.properties.assistant.freegen.converter.DfFreeGenMethodConverter;
import org.seasar.dbflute.properties.assistant.freegen.converter.DfFreeGenMethodConverter.DfConvertMethodReflector;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfJsonSchemaTableLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfFreeGenMethodConverter _methodConverter = new DfFreeGenMethodConverter();

    // ===================================================================================
    //                                                                          Load Table
    //                                                                          ==========
    // ; resourceMap = map:{
    //     ; resourceType = JSON_SCHEMA
    //     ; resourceFile = ../../../foo.json
    // }
    // ; outputMap = map:{
    //     ; templateFile = unused
    //     ; outputDirectory = ../src/main/java
    //     ; package = org.seasar.dbflute...
    //     ; className = unused
    // }
    // ; tableMap = map:{
    //     ; tablePath = map
    //     ; mappingMap = map:{
    //         ; type = map:{
    //             ; INTEGER = Integer
    //             ; VARCHAR = String
    //         }
    //     }
    // }
    public DfFreeGenTable loadTable(String requestName, DfFreeGenResource resource, Map<String, Object> tableMap,
            Map<String, Map<String, String>> mappingMap) {
        final List<Map<String, Object>> tableList = DfCollectionUtil.newArrayList();
        final List<DfConvertMethodReflector> reflectorList = new ArrayList<DfConvertMethodReflector>();
        final String resourceFile = resource.getResourceFile();
        final Map<String, Object> rootMap = decodeJsonMap(requestName, resourceFile);
        final String tablePath = (String) tableMap.get("tablePath");
        if (Srl.is_Null_or_TrimmedEmpty(tablePath)) {
            String msg = "Not found the table path in FreeGen: request=" + requestName;
            throw new DfRequiredPropertyNotFoundException(msg);
        }
        final Map<String, Object> traceMap = traceMap(requestName, resource, rootMap, tablePath);
        for (Entry<String, Object> traceEntry : traceMap.entrySet()) {
            final String tableName = traceEntry.getKey();
            @SuppressWarnings("unchecked")
            final Map<String, Object> tableAttrMap = (Map<String, Object>) traceEntry.getValue();
            final Map<String, Object> tableBeanMap = DfCollectionUtil.newLinkedHashMap();
            prepareTableName(requestName, reflectorList, tableName, tableBeanMap);
            final List<Map<String, Object>> columnList = DfCollectionUtil.newArrayList();
            for (Entry<String, Object> columnEntry : tableAttrMap.entrySet()) {
                final String columnName = columnEntry.getKey();
                if (columnName.startsWith("$")) {
                    final String tableAttrKey = Srl.substringFirstRear(columnName, "$");
                    tableBeanMap.put(tableAttrKey, columnEntry.getValue());
                } else {
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> columnAttrMap = (Map<String, Object>) columnEntry.getValue();
                    final Map<String, Object> columnBeanMap = DfCollectionUtil.newLinkedHashMap();
                    prepareColumnName(requestName, reflectorList, columnName, columnBeanMap);
                    for (Entry<String, Object> attrEntry : columnAttrMap.entrySet()) {
                        final String attrKey = attrEntry.getKey();
                        final Object attrValue = attrEntry.getValue();
                        prepareAttrValue(requestName, mappingMap, columnBeanMap, attrKey, attrValue, reflectorList);
                    }
                    columnList.add(columnBeanMap);
                }
            }
            tableBeanMap.put("columnList", columnList);
            tableList.add(tableBeanMap);
        }
        for (DfConvertMethodReflector reflector : reflectorList) {
            reflector.reflect();
        }
        return new DfFreeGenTable(tableMap, tableList);
    }

    protected void prepareTableName(String requestName, final List<DfConvertMethodReflector> reflectorList,
            final String tableName, final Map<String, Object> tableBeanMap) {
        tableBeanMap.put("name", tableName);
        convertByMethod(requestName, tableBeanMap, "camelizedName", "df:camelize(name)", reflectorList);
        convertByMethod(requestName, tableBeanMap, "capCamelName", "df:capCamel(name)", reflectorList);
        convertByMethod(requestName, tableBeanMap, "uncapCamelName", "df:uncapCamel(name)", reflectorList);
    }

    protected void prepareColumnName(String requestName, final List<DfConvertMethodReflector> reflectorList,
            final String columnName, final Map<String, Object> columnBeanMap) {
        columnBeanMap.put("name", columnName);
        convertByMethod(requestName, columnBeanMap, "camelizedName", "df:camelize(name)", reflectorList);
        convertByMethod(requestName, columnBeanMap, "capCamelName", "df:capCamel(name)", reflectorList);
        convertByMethod(requestName, columnBeanMap, "uncapCamelName", "df:uncapCamel(name)", reflectorList);
    }

    protected void prepareAttrValue(String requestName, Map<String, Map<String, String>> mappingMap,
            Map<String, Object> columnBeanMap, String attrKey, Object attrValue,
            List<DfConvertMethodReflector> reflectorList) {
        if (attrValue instanceof String) {
            if (convertByMethod(requestName, columnBeanMap, attrKey, (String) attrValue, reflectorList)) {
                return;
            }
            Object resultValue = attrValue;
            final Map<String, String> mapping = mappingMap.get(attrKey);
            if (mapping != null) {
                final String mappingValue = mapping.get(resultValue);
                if (mappingValue != null) {
                    resultValue = mappingValue;
                }
            }
            columnBeanMap.put(attrKey, resultValue);
        } else {
            // TODO jflute nested table handling
            columnBeanMap.put(attrKey, attrValue);
        }
    }

    protected boolean convertByMethod(String requestName, Map<String, Object> beanMap, String key, String value,
            List<DfConvertMethodReflector> reflectorList) {
        return _methodConverter.processConvertMethod(requestName, beanMap, key, value, reflectorList);
    }

    // ===================================================================================
    //                                                                         Decode JSON
    //                                                                         ===========
    protected Map<String, Object> decodeJsonMap(String requestName, String resourceFile) {
        return new DfJsonFreeAgent().decodeJsonMap(requestName, resourceFile);
    }

    // ===================================================================================
    //                                                                           Trace Map
    //                                                                           =========
    protected Map<String, Object> traceMap(String requestName, DfFreeGenResource resource, Map<String, Object> rootMap,
            String tracePath) {
        return new DfJsonFreeAgent().traceMap(requestName, resource, rootMap, tracePath);
    }
}
