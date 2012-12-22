/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.properties.assistant.freegen.prop;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.helper.io.prop.DfJavaPropertiesProperty;
import org.seasar.dbflute.helper.io.prop.DfJavaPropertiesReader;
import org.seasar.dbflute.helper.io.prop.DfJavaPropertiesResult;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfPropTableLoader {

    // ===================================================================================
    //                                                                          Load Table
    //                                                                          ==========
    // ; resourceMap = map:{
    //     ; resourceType = PROP
    //     ; resourceFile = ../../../foo.properties
    // }
    // ; outputMap = map:{
    //     ; templateFile = MessageDef.vm
    //     ; outputDirectory = ../src/main/java
    //     ; package = org.seasar.dbflute...
    //     ; className = MessageDef
    // }
    public DfFreeGenTable loadTable(String requestName, DfFreeGenResource resource, Map<String, Object> tableMap,
            Map<String, Map<String, String>> mappingMap) {
        final String resourceFile = resource.getResourceFile();
        final String encoding = resource.hasEncoding() ? resource.getEncoding() : "UTF-8";
        final DfJavaPropertiesReader reader = createReader();
        final DfJavaPropertiesResult result = reader.read(new File(resourceFile), encoding);
        final List<Map<String, Object>> columnList = toMapList(result);
        final String tableName = Srl.substringLastFront((Srl.substringLastRear(resourceFile, "/")));
        return new DfFreeGenTable(tableMap, tableName, columnList);
    }

    // ===================================================================================
    //                                                                           Converter
    //                                                                           =========
    public List<Map<String, Object>> toMapList(DfJavaPropertiesResult result) {
        final List<DfJavaPropertiesProperty> propertyList = result.getPropertyList();
        final List<Map<String, Object>> mapList = DfCollectionUtil.newArrayList();
        for (DfJavaPropertiesProperty property : propertyList) {
            final Map<String, Object> columnMap = DfCollectionUtil.newLinkedHashMap();
            columnMap.put("propertyKey", property.getPropertyKey());
            columnMap.put("propertyValue", property.getPropertyValue());

            final String defName = Srl.replace(property.getPropertyKey(), ".", "_").toUpperCase();
            columnMap.put("defName", defName);

            final String camelizedName = Srl.camelize(defName);
            columnMap.put("camelizedName", camelizedName);
            columnMap.put("capCamelName", Srl.initCap(camelizedName));
            columnMap.put("uncapCamelName", Srl.initUncap(camelizedName));
            columnMap.put("variableArgDef", property.getVariableArgDef());
            columnMap.put("variableArgSet", property.getVariableArgSet());
            final List<Integer> variableNumberList = property.getVariableNumberList();
            columnMap.put("variableCount", variableNumberList.size());
            columnMap.put("variableNumberList", variableNumberList);
            columnMap.put("hasVariable", !variableNumberList.isEmpty());

            columnMap.put("comment", property.getComment());
            columnMap.put("hasComment", property.getComment() != null);

            mapList.add(columnMap);
        }
        return mapList;
    }

    protected DfJavaPropertiesReader createReader() {
        return new DfJavaPropertiesReader();
    }
}
