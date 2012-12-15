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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.io.prop.DfJavaPropertiesReader;
import org.seasar.dbflute.helper.io.prop.DfJavaPropertiesResult;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfPropTableLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Method _convertMethod; // cached
    protected boolean _convertMethodNotFound;
    protected final Properties _reflectionProperties = new Properties();

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
        final String encoding = resource.hasEncoding() ? resource.getEncoding() : "ISO-8859-1";
        final DfJavaPropertiesReader reader = createReader();
        final DfJavaPropertiesResult result = reader.read(resourceFile, encoding);
        final List<Map<String, Object>> columnList = result.getPropertyList();
        final String tableName = Srl.substringLastFront((Srl.substringLastRear(resourceFile, "/")));
        return new DfFreeGenTable(tableMap, tableName, columnList);
    }

    protected DfJavaPropertiesReader createReader() {
        return new DfJavaPropertiesReader();
    }
}
