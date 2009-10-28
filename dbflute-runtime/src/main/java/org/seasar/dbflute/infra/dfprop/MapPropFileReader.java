/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.infra.dfprop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.helper.mapstring.impl.MapListStringImpl;

/**
 * The reader for map string file.
 * @author jflute
 * @since 0.9.6 (2009/10/28 Wednesday)
 */
public class MapPropFileReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String UTF8_ENCODING = "UTF-8";
    public static final String FILE_ENCODING = UTF8_ENCODING;
    public static final String LINE_COMMENT_MARK = "#";

    // ===================================================================================
    //                                                                            Read Map
    //                                                                            ========
    /**
     * Read the map string file. <br />
     * If the type of values is various type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment. <br />
     * This is the most basic method here.
     * <pre>
     * map:{
     *     ; key1 = string-value1
     *     ; key2 = list:{element1 ; element2 }
     *     ; key3 = map:{key1 = value1 ; key2 = value2 }
     *     ; ... = ...
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @return The read map. (NotNull)
     */
    public Map<String, Object> readMap(String path) {
        final String encoding = getFileEncoding();
        final String lineCommentMark = getLineCommentMark();
        final File file = new File(path);
        final StringBuilder sb = new StringBuilder();
        if (file.exists()) {
            java.io.FileInputStream fis = null;
            java.io.InputStreamReader ir = null;
            java.io.BufferedReader br = null;
            try {
                fis = new java.io.FileInputStream(file);
                ir = new java.io.InputStreamReader(fis, encoding);
                br = new java.io.BufferedReader(ir);

                int count = -1;
                while (true) {
                    ++count;

                    String lineString = br.readLine();
                    if (lineString == null) {
                        break;
                    }
                    if (count == 0) {
                        lineString = removeInitialUnicodeBomIfNeeds(encoding, lineString);
                    }
                    if (lineString.trim().length() == 0) {
                        continue;
                    }
                    // If the line is comment...
                    if (lineCommentMark != null && lineString.trim().startsWith(lineCommentMark)) {
                        continue;
                    }
                    sb.append(lineString);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        if (sb.toString().trim().length() == 0) {
            return new LinkedHashMap<String, Object>();
        }
        final MapListStringImpl mapListString = new MapListStringImpl();
        return mapListString.generateMap(sb.toString());
    }

    protected String removeInitialUnicodeBomIfNeeds(String encoding, String value) {
        if (UTF8_ENCODING.equalsIgnoreCase(encoding) && value.length() > 0 && value.charAt(0) == '\uFEFF') {
            value = value.substring(1);
        }
        return value;
    }

    /**
     * Read the map string file as string value. <br />
     * If the type of all values is string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = string-value1
     *     ; key2 = string-value2
     *     ; ... = ...
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @return The read map. (NotNull)
     */
    public Map<String, String> readMapAsStringValue(String path) {
        final Map<String, String> resultMap = new LinkedHashMap<String, String>();
        final Map<String, Object> map = readMap(path);
        final Set<Entry<String, Object>> entrySet = map.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            resultMap.put(entry.getKey(), (String) entry.getValue());
        }
        return resultMap;
    }

    /**
     * Read the map string file as list string value. <br />
     * If the type of all values is list string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = list:{string-element1 ; string-element2 ; ...}
     *     ; key2 = list:{string-element1 ; string-element2 ; ...}
     *     ; ... = list:{...}
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @return The read map. (NotNull)
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> readMapAsListStringValue(String path) {
        final Map<String, List<String>> resultMap = newLinkedHashMap();
        final Map<String, Object> map = readMap(path);
        final Set<Entry<String, Object>> entrySet = map.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            resultMap.put(entry.getKey(), (List<String>) entry.getValue());
        }
        return resultMap;
    }

    /**
     * Read the map string file as map string value. <br />
     * If the type of all values is map string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; key2 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; ... = map:{...}
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @return The read map. (NotNull)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, String>> readMapAsMapStringValue(String path) {
        final Map<String, Map<String, String>> resultMap = newLinkedHashMap();
        final Map<String, Object> map = readMap(path);
        final Set<Entry<String, Object>> entrySet = map.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            resultMap.put(entry.getKey(), (Map<String, String>) entry.getValue());
        }
        return resultMap;
    }

    // ===================================================================================
    //                                                                           Read List
    //                                                                           =========
    public List<Object> readList(String path) {
        final String encoding = getFileEncoding();
        final String lineCommentMark = getLineCommentMark();
        final File file = new File(path);
        final StringBuilder sb = new StringBuilder();
        if (file.exists()) {
            java.io.FileInputStream fis = null;
            java.io.InputStreamReader ir = null;
            java.io.BufferedReader br = null;
            try {
                fis = new java.io.FileInputStream(file);
                ir = new java.io.InputStreamReader(fis, encoding);
                br = new java.io.BufferedReader(ir);

                int count = -1;
                while (true) {
                    ++count;

                    String lineString = br.readLine();
                    if (lineString == null) {
                        break;
                    }
                    if (count == 0) {
                        lineString = removeInitialUnicodeBomIfNeeds(encoding, lineString);
                    }
                    if (lineString.trim().length() == 0) {
                        continue;
                    }
                    // If the line is comment...
                    if (lineCommentMark != null && lineString.trim().startsWith(lineCommentMark)) {
                        continue;
                    }
                    sb.append(lineString);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        if (sb.toString().trim().length() == 0) {
            return new ArrayList<Object>();
        }
        final MapListStringImpl mapListString = new MapListStringImpl();
        return mapListString.generateList(sb.toString());
    }

    // ===================================================================================
    //                                                                         Read String
    //                                                                         ===========
    public String readString(String path) {
        final String encoding = getFileEncoding();
        final String lineCommentMark = getLineCommentMark();
        final File file = new File(path);
        final StringBuilder sb = new StringBuilder();
        if (file.exists()) {
            java.io.FileInputStream fis = null;
            java.io.InputStreamReader ir = null;
            java.io.BufferedReader br = null;
            try {
                fis = new java.io.FileInputStream(file);
                ir = new java.io.InputStreamReader(fis, encoding);
                br = new java.io.BufferedReader(ir);

                int count = -1;
                while (true) {
                    ++count;

                    final String lineString = br.readLine();
                    if (lineString == null) {
                        break;
                    }
                    // If the line is comment...
                    if (lineCommentMark != null && lineString.trim().startsWith(lineCommentMark)) {
                        continue;
                    }
                    sb.append(lineString + "\n");
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return removeInitialUnicodeBomIfNeeds(encoding, sb.toString());
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected String getFileEncoding() {
        return FILE_ENCODING;
    }

    protected String getLineCommentMark() {
        return LINE_COMMENT_MARK;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }
}