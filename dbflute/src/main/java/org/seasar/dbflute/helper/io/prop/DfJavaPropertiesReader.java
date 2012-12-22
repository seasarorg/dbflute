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
package org.seasar.dbflute.helper.io.prop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfCollectionUtil.AccordingToOrderIdExtractor;
import org.seasar.dbflute.util.DfCollectionUtil.AccordingToOrderResource;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/15 Saturday)
 */
public class DfJavaPropertiesReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Method _convertMethod; // cached
    protected boolean _convertMethodNotFound;
    protected final Properties _reflectionProperties = new Properties();

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public DfJavaPropertiesResult read(File propFile, String encoding) {
        final List<DfJavaPropertiesProperty> propertyList = DfCollectionUtil.newArrayList();
        final List<String> duplicateKeyList = new ArrayList<String>();
        final Map<String, String> keyCommentMap = readKeyCommentMap(propFile, encoding, duplicateKeyList);
        final Properties prop = readPlainProperties(propFile);
        final List<String> keyList = orderKeyList(prop, keyCommentMap);
        for (String key : keyList) {
            final String value = prop.getProperty(key);
            final String comment = keyCommentMap.get(key);

            final DfJavaPropertiesProperty property = new DfJavaPropertiesProperty(key, value);

            final String defName = Srl.replace(key, ".", "_").toUpperCase();
            property.setDefName(defName);

            final String camelizedName = Srl.camelize(defName);
            property.setCamelizedName(camelizedName);
            property.setCapCamelName(Srl.initCap(camelizedName));
            property.setUncapCamelName(Srl.initUncap(camelizedName));

            final List<ScopeInfo> variableScopeList = new ArrayList<ScopeInfo>();
            {
                final List<ScopeInfo> scopeList;
                if (Srl.is_NotNull_and_NotTrimmedEmpty(value)) {
                    scopeList = Srl.extractScopeList(value, "{", "}"); // e.g. {0} is for {1}.
                } else {
                    scopeList = DfCollectionUtil.emptyList();
                }
                for (ScopeInfo scopeInfo : scopeList) {
                    final String content = scopeInfo.getContent();
                    try {
                        Integer.valueOf(content);
                        variableScopeList.add(scopeInfo);
                    } catch (NumberFormatException ignored) { // e.g. {A} is for {B}
                    }
                }
            }
            final List<Integer> variableNumberList = new ArrayList<Integer>();
            for (ScopeInfo scopeInfo : variableScopeList) {
                variableNumberList.add(valueOfVariableNumber(propFile, key, scopeInfo.getContent()));
            }
            property.setVariableArgDef(buildVariableArgDef(variableNumberList));
            property.setVariableArgSet(buildVariableArgSet(variableNumberList));
            property.setVariableNumberList(variableNumberList);
            property.setComment(comment);

            propertyList.add(property);
        }
        return new DfJavaPropertiesResult(prop, propertyList, duplicateKeyList);
    }

    protected List<String> orderKeyList(Properties prop, final Map<String, String> keyCommentMap) {
        final List<Object> orderedList = new ArrayList<Object>(prop.keySet());
        final AccordingToOrderResource<Object, String> resource = new AccordingToOrderResource<Object, String>();
        resource.setIdExtractor(new AccordingToOrderIdExtractor<Object, String>() {
            public String extractId(Object element) {
                return (String) element;
            }
        });
        resource.setOrderedUniqueIdList(new ArrayList<String>(keyCommentMap.keySet()));
        DfCollectionUtil.orderAccordingTo(orderedList, resource);
        final List<String> keyList = new ArrayList<String>();
        for (Object keyObj : orderedList) {
            keyList.add((String) keyObj);
        }
        return keyList;
    }

    // ===================================================================================
    //                                                                         Read Helper
    //                                                                         ===========
    protected Map<String, String> readKeyCommentMap(File propFile, String encoding, List<String> duplicateKeyList) {
        final Map<String, String> keyCommentMap = new LinkedHashMap<String, String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(propFile), encoding));
            String previousComment = null;
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                final String ltrimmedLine = Srl.ltrim(line);
                if (ltrimmedLine.startsWith("# ")) { // comment lines
                    final String commentCandidate = Srl.substringFirstRear(ltrimmedLine, "#").trim();
                    if (ltrimmedLine.contains("=")) { // you cannot contain equal mark in comment
                        previousComment = null; // e.g. #foo.bar.qux = value (comment out???)
                    } else {
                        if (!ltrimmedLine.trim().equals("#")) { // not sharp lonely
                            previousComment = commentCandidate; // 99% comment
                        }
                    }
                    continue;
                }
                // key value here
                if (!ltrimmedLine.contains("=")) { // what's this? (no way)
                    continue;
                }
                final String key = Srl.substringFirstFront(ltrimmedLine, "=").trim();
                if (keyCommentMap.containsKey(key)) {
                    duplicateKeyList.add(key);
                    keyCommentMap.remove(key); // remove existing key for order and override
                }
                keyCommentMap.put(key, loadConvert(previousComment));
                previousComment = null;
            }
        } catch (IOException e) {
            throwJavaPropertiesReadFailureException(propFile, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return keyCommentMap;
    }

    protected Properties readPlainProperties(File propFile) {
        final Properties prop = new Properties();
        try {
            final FileInputStream fis = new FileInputStream(propFile);
            prop.load(fis);
        } catch (IOException e) {
            throwJavaPropertiesReadFailureException(propFile, e);
        }
        return prop;
    }

    protected void throwJavaPropertiesReadFailureException(File propFile, IOException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to read the properties file.");
        br.addItem("Properties");
        br.addElement(propFile);
        br.addItem("IOException");
        br.addElement(e.getClass().getName());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                     Variable Helper
    //                                                                     ===============
    protected Integer valueOfVariableNumber(File propFile, String key, String content) {
        try {
            return Integer.valueOf(content);
        } catch (NumberFormatException e) {
            String msg = "The NOT-number variable was found: propFile=" + propFile + " key=" + key;
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

    // ===================================================================================
    //                                                                     Unicode Convert
    //                                                                     ===============
    protected String loadConvert(String expression) {
        if (expression == null) {
            return null;
        }
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
