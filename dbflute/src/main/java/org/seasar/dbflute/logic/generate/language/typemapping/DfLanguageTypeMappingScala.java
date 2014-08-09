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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class DfLanguageTypeMappingScala implements DfLanguageTypeMapping {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final Map<String, String> _jdbcToJavaNativeMap;
    static {
        final Map<String, String> map = DfCollectionUtil.newLinkedHashMap();
        map.put("BINARY", "Array[Byte]");
        map.put("VARBINARY", "Array[Byte]");
        map.put("LONGVARBINARY", "Array[Byte]");
        map.put("BLOB", "Array[Byte]");
        _jdbcToJavaNativeMap = map;
    }
    public static final String SCALA_NATIVE_INTEGER = "Int";
    public static final String SCALA_NATIVE_LONG = "Long";
    public static final String SCALA_NATIVE_BIG_INTEGER = "scala.math.BigInt";
    public static final String SCALA_NATIVE_BIG_DECIMAL = "scala.math.BigDecimal";
    public static final String SCALA_NATIVE_RICH_LOCAL_DATE = "com.github.nscala_time.time.RichLocalDate";
    public static final String SCALA_NATIVE_BOOLEAN = "Boolean";
    // DB-able entity same as Java 
    //protected static final List<String> _numberList;
    //static {
    //    _numberList = newArrayList("Byte", "Short", "Integer", "Long", "Float", "Double", "BigDecimal", "BigInteger");
    //}
    protected static final List<String> _binaryList = newArrayList("Array[Byte]");

    protected static <ELEMENT> ArrayList<ELEMENT> newArrayList(ELEMENT... elements) {
        return DfCollectionUtil.newArrayList(elements);
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfLanguageTypeMapping _mappingJava = new DfLanguageTypeMappingJava();

    // ===================================================================================
    //                                                                        Type Mapping
    //                                                                        ============
    public Map<String, String> getJdbcToJavaNativeMap() {
        return _jdbcToJavaNativeMap;
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
        return _binaryList;
    }

    // ===================================================================================
    //                                                                    Small Adjustment
    //                                                                    ================
    public String getSequenceJavaNativeType() {
        return SCALA_NATIVE_BIG_INTEGER;
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
            converted = SCALA_NATIVE_INTEGER;
        } else if ("java.math.BigDecimal".equals(javaNative)) {
            converted = SCALA_NATIVE_BIG_DECIMAL;
        } else {
            converted = javaNative;
        }
        return converted;
    }

    public String convertToImmutableJavaNativeDefaultValue(String immutablePropertyNative) {
        final String defaultValue;
        if (SCALA_NATIVE_INTEGER.equals(immutablePropertyNative) || SCALA_NATIVE_LONG.equals(immutablePropertyNative)) {
            defaultValue = "0";
        } else if (SCALA_NATIVE_BIG_DECIMAL.equals(immutablePropertyNative)) {
            defaultValue = "0";
        } else if (SCALA_NATIVE_BOOLEAN.equals(immutablePropertyNative)) {
            defaultValue = "false";
        } else {
            defaultValue = "null";
        }
        return defaultValue;
    }

    public String convertToJavaNativeValueFromImmutable(String immutablePropertyNative, String javaNative,
            String variable) {
        // quit because variable might have orNull
        //if (DfLanguageTypeMappingScala.SCALA_NATIVE_INTEGER.equals(immutableJavaNative)) {
        //    return "int2Integer(" + variable + ")";
        //}
        //if (DfLanguageTypeMappingScala.SCALA_NATIVE_LONG.equals(immutableJavaNative)) {
        //    return "long2Long(" + variable + ")";
        //}
        if (SCALA_NATIVE_BIG_DECIMAL.equals(immutablePropertyNative)) {
            return variable + ".asInstanceOf[" + javaNative + "]";
        }
        if (SCALA_NATIVE_RICH_LOCAL_DATE.equals(immutablePropertyNative)) {
            return variable + ".asInstanceOf[" + javaNative + "]";
        }
        return variable;
    }
}
