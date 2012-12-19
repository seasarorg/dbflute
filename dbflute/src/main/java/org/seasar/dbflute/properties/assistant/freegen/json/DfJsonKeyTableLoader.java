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
package org.seasar.dbflute.properties.assistant.freegen.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfJsonKeyTableLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected static final String JSON_DECODER_NAME = "net.arnx.jsonic.JSON";

    // ===================================================================================
    //                                                                          Load Table
    //                                                                          ==========
    // ; resourceMap = map:{
    //     ; resourceType = JSON_KEY
    //     ; resourceFile = ../../../foo.properties
    // }
    // ; outputMap = map:{
    //     ; templateFile = MessageDef.vm
    //     ; outputDirectory = ../src/main/java
    //     ; package = org.seasar.dbflute...
    //     ; className = MessageDef
    // }
    // ; tableMap = map:{
    //     ; keyPath = categories -> map.key
    // }
    public DfFreeGenTable loadTable(String requestName, DfFreeGenResource resource, Map<String, Object> tableMap,
            Map<String, Map<String, String>> mappingMap) {
        final String resourceFile = resource.getResourceFile();
        final String tableName = Srl.substringLastFront((Srl.substringLastRear(resourceFile, "/")));

        final Map<String, Object> rootMap = decodeJsonMap(resourceFile);
        final String keyPath = (String) tableMap.get("keyPath");
        final List<String> pathList = Srl.splitListTrimmed(keyPath, "->");
        final List<String> keyList = findKeyList(rootMap, keyPath, pathList);

        final List<Map<String, Object>> columnList = setupColumnList(keyList);
        return new DfFreeGenTable(tableMap, tableName, columnList);
    }

    // ===================================================================================
    //                                                                         Decode JSON
    //                                                                         ===========
    protected Map<String, Object> decodeJsonMap(final String resourceFile) {
        final String decoderName = JSON_DECODER_NAME;
        final Class<?> jsonType;
        try {
            jsonType = Class.forName(decoderName);
        } catch (ClassNotFoundException e) {
            throwJsonDecoderNotFoundException(e, decoderName);
            return null; // unreachable
        }
        final String decodeMethodName = "decode";
        final Class<?>[] argTypes = new Class<?>[] { InputStream.class };
        final Method decodeMethod = DfReflectionUtil.getPublicMethod(jsonType, decodeMethodName, argTypes);
        FileInputStream ins = null;
        final Object decodedObj;
        try {
            ins = new FileInputStream(new File(resourceFile));
            decodedObj = DfReflectionUtil.invokeStatic(decodeMethod, new Object[] { ins });
        } catch (FileNotFoundException e) {
            throwJsonFileNotFoundException(e, resourceFile);
            return null; // unreachable
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
        @SuppressWarnings("unchecked")
        final Map<String, Object> rootMap = (Map<String, Object>) decodedObj;
        return rootMap;
    }

    protected void throwJsonDecoderNotFoundException(ClassNotFoundException e, String decoderName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the JSON decoder for FreeGen.");
        br.addItem("Advice");
        br.addElement("You should put the JSONIC jar file to the 'extlib' directory");
        br.addElement("on DBFlute client when you use JSON handling of FreeGen.");
        br.addElement("For example:");
        br.addElement("  {DBFluteClient}");
        br.addElement("    |-dfprop");
        br.addElement("    |-extlib");
        br.addElement("    |  |-jsonic-1.2.5.jar");
        br.addElement("    |-...");
        br.addItem("Decoder Name");
        br.addElement(decoderName);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    protected void throwJsonFileNotFoundException(FileNotFoundException e, String resourceFile) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the JSON file for FreeGen.");
        br.addItem("JSON File");
        br.addElement(resourceFile);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                        Find KeyList
    //                                                                        ============
    protected List<String> findKeyList(Map<String, Object> rootMap, String keyPath, List<String> pathList) {
        // e.g.
        //  keyPath = categories -> map.keys
        //  keyPath = categories -> map.values -> list.elements
        //  keyPath = categories -> map.values -> list.map.foo
        //  keyPath = categories -> map.foo -> map.keys
        List<String> keyList = null;
        Object current = null;
        for (String pathElement : pathList) {
            if (current == null) {
                current = rootMap.get(pathElement);
                if (current == null) {
                    throwRootMapKeyNotFoundException(keyPath, pathElement);
                }
                continue;
            }
            if (pathElement.startsWith("map.")) {
                if (!(current instanceof Map<?, ?>)) {
                    throwKeyPathExpectedMapButNotMapException(keyPath, pathElement, current);
                }
                @SuppressWarnings("unchecked")
                final Map<String, Object> currentMap = (Map<String, Object>) current;
                if (pathElement.equals("map.keys")) { // found
                    keyList = new ArrayList<String>(currentMap.keySet());
                    break;
                } else if (pathElement.equals("map.values")) {
                    current = new ArrayList<Object>(currentMap.values());
                    continue;
                } else {
                    final String nextKey = Srl.substringFirstRear(pathElement, "map.");
                    current = currentMap.get(nextKey);
                    continue;
                }
            } else if (pathElement.startsWith("list.")) {
                if (!(current instanceof List<?>)) {
                    throwKeyPathExpectedListButNotListException(keyPath, pathElement, current);
                }
                @SuppressWarnings("unchecked")
                final List<Object> currentList = (List<Object>) current;
                if (pathElement.equals("list.elements")) { // found
                    keyList = new ArrayList<String>();
                    for (Object element : currentList) {
                        if (!(element instanceof String)) {
                            throwKeyPathExpectedStringListButNotStringException(keyPath, pathElement, currentList,
                                    element);
                        }
                        keyList.add((String) element);
                    }
                    break;
                } else if (pathElement.startsWith("list.map.")) { // found
                    final String elementKey = Srl.substringFirstRear(pathElement, "list.map.");
                    keyList = new ArrayList<String>();
                    for (Object element : currentList) {
                        if (!(element instanceof Map<?, ?>)) {
                            throwKeyPathExpectedMapListButNotMapException(keyPath, pathElement, currentList, element);
                        }
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> elementMap = (Map<String, Object>) element;
                        final String elementValue = (String) elementMap.get(elementKey);
                        if (elementValue != null) {
                            keyList.add(elementValue);
                        }
                    }
                    break;
                } else {
                    throwIllegalKeyPathElementException(keyPath, pathElement);
                }
            } else {
                throwIllegalKeyPathElementException(keyPath, pathElement);
            }
        }
        if (keyList == null) {
            String msg = "Not found the keys: keyPath=" + keyPath;
            throw new DfIllegalPropertySettingException(msg);
        }
        return keyList;
    }

    protected void throwRootMapKeyNotFoundException(String keyPath, String rootMapKey) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the key in the root map. (FreeGen)");
        br.addItem("keyPath");
        br.addElement(keyPath);
        br.addItem("RootMap Key");
        br.addElement(rootMapKey);
        final String msg = br.buildExceptionMessage();
        throw new DfIllegalPropertySettingException(msg);
    }

    protected void throwKeyPathExpectedMapButNotMapException(String keyPath, String targetPath, Object current) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The key path expects map type but not map. (FreeGen)");
        br.addItem("keyPath");
        br.addElement(keyPath);
        br.addItem("Target Path Element");
        br.addElement(targetPath);
        br.addItem("Actual Object");
        br.addElement(current != null ? current.getClass().getName() : null);
        br.addElement(current);
        final String msg = br.buildExceptionMessage();
        throw new DfIllegalPropertySettingException(msg);
    }

    protected void throwKeyPathExpectedListButNotListException(String keyPath, String targetPath, Object current) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The key path expects list type but not list. (FreeGen)");
        br.addItem("keyPath");
        br.addElement(keyPath);
        br.addItem("Target Path Element");
        br.addElement(targetPath);
        br.addItem("Actual Object");
        br.addElement(current != null ? current.getClass().getName() : null);
        br.addElement(current);
        final String msg = br.buildExceptionMessage();
        throw new DfIllegalPropertySettingException(msg);
    }

    protected void throwKeyPathExpectedStringListButNotStringException(String keyPath, String targetPath,
            List<Object> currentList, Object element) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The key path expects string type in list but not string. (FreeGen)");
        br.addItem("keyPath");
        br.addElement(keyPath);
        br.addItem("Target Path Element");
        br.addElement(targetPath);
        br.addItem("List Object");
        br.addElement(currentList != null ? currentList.getClass().getName() : null);
        br.addElement(currentList);
        br.addItem("Actual Element");
        br.addElement(element != null ? element.getClass().getName() : null);
        br.addElement(element);
        final String msg = br.buildExceptionMessage();
        throw new DfIllegalPropertySettingException(msg);
    }

    protected void throwKeyPathExpectedMapListButNotMapException(String keyPath, String targetPath,
            List<Object> currentList, Object element) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The key path expects string type in list but not string. (FreeGen)");
        br.addItem("keyPath");
        br.addElement(keyPath);
        br.addItem("Target Path Element");
        br.addElement(targetPath);
        br.addItem("List Object");
        br.addElement(currentList != null ? currentList.getClass().getName() : null);
        br.addElement(currentList);
        br.addItem("Actual Element");
        br.addElement(element != null ? element.getClass().getName() : null);
        br.addElement(element);
        final String msg = br.buildExceptionMessage();
        throw new DfIllegalPropertySettingException(msg);
    }

    protected void throwIllegalKeyPathElementException(String keyPath, String illegalPathElement) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Illegal key path was found. (FreeGen)");
        br.addItem("keyPath");
        br.addElement(keyPath);
        br.addItem("Illegal Path Element");
        br.addElement(illegalPathElement);
        final String msg = br.buildExceptionMessage();
        throw new DfIllegalPropertySettingException(msg);
    }

    // ===================================================================================
    //                                                                   Set up ColumnList
    //                                                                   =================
    protected List<Map<String, Object>> setupColumnList(List<String> keyList) {
        final List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
        for (String key : keyList) {
            final Map<String, Object> columnMap = new HashMap<String, Object>();
            columnMap.put("key", key);
            final String defName;
            if (key.contains(".")) { // e.g. foo.bar.qux
                defName = Srl.replace(key, ".", "_").toUpperCase();
            } else { // e.g. fooBarQux
                defName = Srl.decamelize(key);
            }
            columnMap.put("defName", defName);

            final String camelizedName = Srl.camelize(defName);
            columnMap.put("camelizedName", camelizedName);
            columnMap.put("capCamelName", Srl.initCap(camelizedName));
            columnMap.put("uncapCamelName", Srl.initUncap(camelizedName));

            columnList.add(columnMap);
        }
        return columnList;
    }
}
