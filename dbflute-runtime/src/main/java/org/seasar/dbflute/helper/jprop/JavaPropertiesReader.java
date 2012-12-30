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
package org.seasar.dbflute.helper.jprop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.jprop.exception.JavaPropertiesImplicitOverrideException;
import org.seasar.dbflute.helper.jprop.exception.JavaPropertiesLonelyOverrideException;
import org.seasar.dbflute.helper.jprop.exception.JavaPropertiesReadFailureException;
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
public class JavaPropertiesReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String OVERRIDE_ANNOTATION = "@Override";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected final JavaPropertiesStreamProvider _streamProvider;
    protected String _title; // lazy-loaded

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    protected List<JavaPropertiesStreamProvider> _extendsProviderList;
    protected boolean _checkImplicitOverride;

    // -----------------------------------------------------
    //                                            Reflection
    //                                            ----------
    protected Method _convertMethod; // cached
    protected boolean _convertMethodNotFound;
    protected final Properties _reflectionProperties = new Properties();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public JavaPropertiesReader(JavaPropertiesStreamProvider streamProvider) {
        _streamProvider = streamProvider;
    }

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    public JavaPropertiesReader extendsProperties(JavaPropertiesStreamProvider extendsStreamProvider) {
        if (_extendsProviderList == null) {
            _extendsProviderList = DfCollectionUtil.newArrayListSized(4);
        }
        _extendsProviderList.add(extendsStreamProvider);
        return this;
    }

    public JavaPropertiesReader checkImplicitOverride() {
        _checkImplicitOverride = true;
        return this;
    }

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public JavaPropertiesResult read() {
        final List<JavaPropertiesProperty> propertyList = DfCollectionUtil.newArrayList();
        final List<String> duplicateKeyList = DfCollectionUtil.newArrayList();
        final Map<String, String> keyCommentMap = readKeyCommentMap(duplicateKeyList);
        final Properties prop = readPlainProperties();
        final List<String> keyList = orderKeyList(prop, keyCommentMap);
        for (String key : keyList) {
            final String value = prop.getProperty(key);
            final String comment = keyCommentMap.get(key);

            final JavaPropertiesProperty property = new JavaPropertiesProperty(key, value);

            final String defName = Srl.replace(key, ".", "_").toUpperCase();
            property.setDefName(defName);

            final String camelizedName = Srl.camelize(defName);
            property.setCamelizedName(camelizedName);
            property.setCapCamelName(Srl.initCap(camelizedName));
            property.setUncapCamelName(Srl.initUncap(camelizedName));

            final List<ScopeInfo> variableScopeList = DfCollectionUtil.newArrayList();
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
            final List<Integer> variableNumberList = DfCollectionUtil.newArrayList();
            for (ScopeInfo scopeInfo : variableScopeList) {
                variableNumberList.add(valueOfVariableNumber(key, scopeInfo.getContent()));
            }
            property.setVariableArgDef(buildVariableArgDef(variableNumberList));
            property.setVariableArgSet(buildVariableArgSet(variableNumberList));
            property.setVariableNumberList(variableNumberList);
            property.setComment(comment);

            propertyList.add(property);
        }
        return prepareResult(prop, propertyList, duplicateKeyList);
    }

    // -----------------------------------------------------
    //                                             Order Key
    //                                             ---------
    protected List<String> orderKeyList(Properties prop, final Map<String, String> keyCommentMap) {
        final List<Object> orderedList = DfCollectionUtil.newArrayList(prop.keySet());
        final AccordingToOrderResource<Object, String> resource = new AccordingToOrderResource<Object, String>();
        resource.setIdExtractor(new AccordingToOrderIdExtractor<Object, String>() {
            public String extractId(Object element) {
                return (String) element;
            }
        });
        resource.setOrderedUniqueIdList(DfCollectionUtil.newArrayList(keyCommentMap.keySet()));
        DfCollectionUtil.orderAccordingTo(orderedList, resource);
        final List<String> keyList = DfCollectionUtil.newArrayList();
        for (Object keyObj : orderedList) {
            keyList.add((String) keyObj);
        }
        return keyList;
    }

    // -----------------------------------------------------
    //                                        Prepare Result
    //                                        --------------
    protected JavaPropertiesResult prepareResult(Properties prop, List<JavaPropertiesProperty> propertyList,
            List<String> duplicateKeyList) {
        final JavaPropertiesResult propResult;
        if (_extendsProviderList != null && !_extendsProviderList.isEmpty()) {
            final JavaPropertiesReader extendsReader = createExtendsReader();
            final JavaPropertiesResult extendsPropResult = extendsReader.read();
            final List<JavaPropertiesProperty> mergedList = mergeExtendsPropResult(propertyList, extendsPropResult);
            propResult = new JavaPropertiesResult(prop, mergedList, duplicateKeyList, extendsPropResult);
        } else {
            propResult = new JavaPropertiesResult(prop, propertyList, duplicateKeyList);
        }
        return propResult;
    }

    protected JavaPropertiesReader createExtendsReader() {
        final List<JavaPropertiesStreamProvider> extendsList = DfCollectionUtil.newArrayList(_extendsProviderList);
        final JavaPropertiesStreamProvider firstExtends = extendsList.remove(0);
        final JavaPropertiesReader extendsReader = new JavaPropertiesReader(firstExtends);
        for (JavaPropertiesStreamProvider nestedExtends : extendsList) {
            extendsReader.extendsProperties(nestedExtends);
        }
        if (_checkImplicitOverride) {
            extendsReader.checkImplicitOverride();
        }
        return extendsReader;
    }

    protected List<JavaPropertiesProperty> mergeExtendsPropResult(List<JavaPropertiesProperty> propertyList,
            JavaPropertiesResult extendsPropResult) {
        final List<JavaPropertiesProperty> extendsPropertyList = extendsPropResult.getPropertyList();
        for (JavaPropertiesProperty property : extendsPropertyList) {
            property.toBeExtends();
        }
        final Set<JavaPropertiesProperty> extendsPropertySet = DfCollectionUtil.newLinkedHashSet(extendsPropertyList);
        for (JavaPropertiesProperty property : propertyList) {
            final boolean checkExplicitOverride = isCheckExplicitOverride(property);
            if (extendsPropertySet.contains(property)) {
                property.toBeOverride();
                if (checkExplicitOverride) {
                    throwJavaPropertiesImplicitOverrideException(property);
                }
            } else {
                if (checkExplicitOverride) {
                    throwJavaPropertiesLonelyOverrideException(property);
                }
            }
        }
        final Set<JavaPropertiesProperty> mergedPropertySet = DfCollectionUtil.newLinkedHashSet(propertyList);
        mergedPropertySet.addAll(extendsPropertySet); // merge (add if not exists)
        return DfCollectionUtil.newArrayList(mergedPropertySet);
    }

    protected boolean isCheckExplicitOverride(JavaPropertiesProperty property) {
        return _checkImplicitOverride && containsOverrideAnnotation(property);
    }

    protected boolean containsOverrideAnnotation(JavaPropertiesProperty property) {
        final String comment = property.getComment();
        return comment != null && Srl.containsIgnoreCase(comment, OVERRIDE_ANNOTATION);
    }

    protected void throwJavaPropertiesImplicitOverrideException(JavaPropertiesProperty property) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Found the implicit override property.");
        br.addItem("Properties (Stream Provider)");
        br.addElement(_title != null ? _title : _streamProvider);
        br.addItem("Implicit Override Property");
        br.addElement(property.getPropertyKey());
        br.addElement(property.getPropertyValue());
        final String msg = br.buildExceptionMessage();
        throw new JavaPropertiesImplicitOverrideException(msg);
    }

    protected void throwJavaPropertiesLonelyOverrideException(JavaPropertiesProperty property) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Found the lonely override property.");
        br.addItem("Properties (Stream Provider)");
        br.addElement(_title != null ? _title : _streamProvider);
        br.addItem("Lonely Override Property");
        br.addElement(property.getPropertyKey());
        br.addElement(property.getPropertyValue());
        final String msg = br.buildExceptionMessage();
        throw new JavaPropertiesLonelyOverrideException(msg);
    }

    // ===================================================================================
    //                                                                         Read Helper
    //                                                                         ===========
    protected Map<String, String> readKeyCommentMap(List<String> duplicateKeyList) {
        final Map<String, String> keyCommentMap = DfCollectionUtil.newLinkedHashMap();
        final String encoding = "UTF-8"; // because properties normally cannot have double bytes
        JavaPropertiesStream stream = null;
        BufferedReader br = null;
        try {
            stream = preparePropFileStream();
            br = new BufferedReader(new InputStreamReader(stream.getInputStream(), encoding));
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
            throwJavaPropertiesReadFailureException(stream, e);
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

    protected Properties readPlainProperties() {
        final Properties prop = new Properties();
        JavaPropertiesStream stream = null;
        InputStream ins = null;
        try {
            stream = preparePropFileStream();
            ins = stream.getInputStream();
            prop.load(ins);
        } catch (IOException e) {
            throwJavaPropertiesReadFailureException(stream, e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
        return prop;
    }

    protected JavaPropertiesStream preparePropFileStream() throws IOException {
        final JavaPropertiesStream stream = _streamProvider.provideStream();
        initializeTitleIfNeeds(stream);
        return stream;
    }

    protected void initializeTitleIfNeeds(JavaPropertiesStream stream) {
        if (_title == null) {
            _title = stream.getTitle();
        }
    }

    protected void throwJavaPropertiesReadFailureException(JavaPropertiesStream stream, IOException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to read the properties file.");
        br.addItem("Properties");
        br.addElement(_title != null ? _title : stream.getTitle());
        br.addItem("IOException");
        br.addElement(e.getClass().getName());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new JavaPropertiesReadFailureException(msg, e);
    }

    // ===================================================================================
    //                                                                     Variable Helper
    //                                                                     ===============
    protected Integer valueOfVariableNumber(String key, String content) {
        try {
            return Integer.valueOf(content);
        } catch (NumberFormatException e) {
            String msg = "The NOT-number variable was found: provider=" + _streamProvider + " key=" + key;
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
