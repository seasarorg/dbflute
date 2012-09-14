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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

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
    //     ; resourceFile = ../../.../foo.properties
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
        BufferedReader br = null;
        try {
            final Properties prop = readProperties(resourceFile);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(resourceFile), encoding));
            final List<Map<String, Object>> columnList = readColumnList(requestName, br, prop);
            final String tableName = Srl.substringLastFront((Srl.substringLastRear(resourceFile, "/")));
            return new DfFreeGenTable(tableMap, tableName, columnList);
        } catch (IOException e) {
            String msg = "Failed to read the properties:";
            msg = msg + " requestName=" + requestName + " resourceFile=" + resourceFile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected Properties readProperties(String resourceFile) throws IOException {
        final FileInputStream fis = new FileInputStream(resourceFile);
        final Properties prop = new Properties();
        prop.load(fis);
        return prop;
    }

    protected List<Map<String, Object>> readColumnList(String requestName, BufferedReader br, Properties prop)
            throws IOException {
        final List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
        String previousComment = null;
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            final String ltrimmedLine = Srl.ltrim(line);
            if (ltrimmedLine.startsWith("# ")) { // comment lines
                final String commentCandidate = Srl.substringFirstRear(ltrimmedLine, "#").trim();
                if (ltrimmedLine.startsWith("# ")) { // 100% comment
                    previousComment = commentCandidate;
                } else {
                    if (!ltrimmedLine.contains(".")) {
                        previousComment = commentCandidate;
                    } else { // e.g. #foo.bar.qux (comment out???)
                        previousComment = null;
                    }
                }
                continue;
            }
            if (!ltrimmedLine.contains("=")) {
                continue;
            }
            final Map<String, Object> columnMap = new LinkedHashMap<String, Object>();
            final String key = Srl.substringFirstFront(ltrimmedLine, "=").trim();
            columnMap.put("key", key);

            final String defName = Srl.replace(key, ".", "_").toUpperCase();
            columnMap.put("defName", defName);

            final String camelizedName = Srl.camelize(defName);
            columnMap.put("camelizedName", camelizedName);
            columnMap.put("capCamelName", Srl.initCap(camelizedName));
            columnMap.put("uncapCamelName", Srl.initUncap(camelizedName));

            final List<ScopeInfo> variableScopeList = new ArrayList<ScopeInfo>();
            {
                final String foundValue = prop.getProperty(key); // by Properties
                final String registeredValue;
                final List<ScopeInfo> scopeList;
                if (foundValue != null) {
                    registeredValue = loadConvert(foundValue);
                    scopeList = Srl.extractScopeList(foundValue, "{", "}");
                } else { // basically no way
                    registeredValue = "";
                    scopeList = new ArrayList<ScopeInfo>();
                }
                columnMap.put("value", registeredValue); // basically unused
                for (ScopeInfo scopeInfo : scopeList) {
                    final String content = scopeInfo.getContent();
                    try {
                        Integer.valueOf(content);
                        variableScopeList.add(scopeInfo);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            final List<Integer> variableNumberList = new ArrayList<Integer>();
            for (ScopeInfo scopeInfo : variableScopeList) {
                variableNumberList.add(valueOfVariableNumber(requestName, key, scopeInfo.getContent()));
            }
            columnMap.put("variableArgDef", buildVariableArgDef(variableNumberList));
            columnMap.put("variableArgSet", buildVariableArgSet(variableNumberList));
            columnMap.put("variableCount", variableScopeList.size());
            columnMap.put("variableNumberList", variableNumberList);
            columnMap.put("variableScopeList", variableScopeList);
            columnMap.put("hasVariable", !variableScopeList.isEmpty());

            final String comment;
            final boolean hasComment;
            if (previousComment != null) {
                comment = loadConvert(previousComment);
                hasComment = true;
            } else {
                comment = "";
                hasComment = false;
            }
            columnMap.put("comment", comment);
            columnMap.put("hasComment", hasComment);

            columnList.add(columnMap);
            previousComment = null;
        }
        return columnList;
    }

    protected Integer valueOfVariableNumber(String requestName, String key, String content) {
        try {
            return Integer.valueOf(content);
        } catch (NumberFormatException e) {
            String msg = "The NOT-number variable was found: requestName=" + requestName + " key=" + key;
            throw new IllegalStateException(msg, e);
        }
    }

    protected String buildVariableArgDef(List<Integer> variableNumberList) {
        final StringBuilder sb = new StringBuilder();
        for (Integer number : variableNumberList) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("String arg").append(number);
        }
        return sb.toString();
    }

    protected String buildVariableArgSet(List<Integer> variableNumberList) {
        final StringBuilder sb = new StringBuilder();
        for (Integer number : variableNumberList) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("arg").append(number);
        }
        return sb.toString();
    }

    protected String loadConvert(String expression) {
        final Method method = getConvertMethod();
        if (method == null) {
            return expression;
        }
        final char[] in = expression.toCharArray();
        final Object[] args = new Object[] { in, 0, expression.length(), new char[] {} };
        return (String) DfReflectionUtil.invoke(method, _reflectionProperties, args);
    }

    protected Method getConvertMethod() {
        if (_convertMethod != null) {
            return _convertMethod;
        }
        if (_convertMethodNotFound) {
            return null;
        }
        final Class<?>[] argTypes = new Class<?>[] { char[].class, int.class, int.class, char[].class };
        _convertMethod = DfReflectionUtil.getWholeMethod(Properties.class, "loadConvert", argTypes);
        if (_convertMethod == null) {
            _convertMethodNotFound = true;
        } else {
            _convertMethod.setAccessible(true);
        }
        return _convertMethod;
    }
}
