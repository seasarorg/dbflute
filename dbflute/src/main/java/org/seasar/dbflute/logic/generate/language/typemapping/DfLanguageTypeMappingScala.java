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
package org.seasar.dbflute.logic.generate.language.typemapping;

import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class DfLanguageTypeMappingScala implements DfLanguageTypeMapping {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected final DfLanguageTypeMapping _mappingJava = new DfLanguageTypeMappingJava();

    // ===================================================================================
    //                                                                        Type Mapping
    //                                                                        ============
    public Map<String, String> getJdbcToJavaNativeMap() {
        return _mappingJava.getJdbcToJavaNativeMap();
    }

    // ===================================================================================
    //                                                                  Native Suffix List
    //                                                                  ==================
    public List<String> getStringList() {
        return _mappingJava.getStringList();
    }

    public List<String> getNumberList() {
        return _mappingJava.getNumberList();
    }

    public List<String> getDateList() {
        return _mappingJava.getDateList();
    }

    public List<String> getBooleanList() {
        return _mappingJava.getBooleanList();
    }

    public List<String> getBinaryList() {
        return _mappingJava.getBinaryList();
    }

    // ===================================================================================
    //                                                                    Small Adjustment
    //                                                                    ================
    public String getSequenceJavaNativeType() {
        return _mappingJava.getSequenceJavaNativeType();
    }

    public String getDefaultNumericJavaNativeType() {
        return _mappingJava.getDefaultNumericJavaNativeType();
    }

    public String getDefaultDecimalJavaNativeType() {
        return _mappingJava.getDefaultDecimalJavaNativeType();
    }

    public String getJdbcTypeOfUUID() {
        return _mappingJava.getJdbcTypeOfUUID();
    }

    public String switchParameterBeanTestValueType(String plainTypeName) {
        return _mappingJava.switchParameterBeanTestValueType(plainTypeName);
    }

    public String convertToImmutableJavaNativeType(String javaNative) {
        final String converted;
        if (javaNative.endsWith("Integer")) {
            converted = "Int";
        } else {
            converted = javaNative;
        }
        return converted;
    }

    public String convertToImmutableJavaNativeDefaultValue(String immutableJavaNative) {
        final String defaultValue;
        if ("Int".equals(immutableJavaNative) || "Long".equals(immutableJavaNative)) {
            defaultValue = "0";
        } else {
            defaultValue = "null";
        }
        return defaultValue;
    }
}
