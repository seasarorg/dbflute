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
package org.seasar.dbflute.infra.dfprop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.DfPropFileReadFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.mapstring.MapListFile;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The file handling for DBFlute property (dfprop).
 * @author jflute
 * @since 0.9.6 (2009/10/28 Wednesday)
 */
public class DfPropFile {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _skipLineSeparator;

    // ===================================================================================
    //                                                                                 Map
    //                                                                                 ===
    // -----------------------------------------------------
    //                                                  Read
    //                                                  ----
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
     * @param dfpropPath The path of DBFlute property file. (NotNull)
     * @param env The environment type of DBFlute. (NullAllowed: if null, no environment file)
     * @return The read map. (NotNull: if not found, returns empty map)
     */
    public Map<String, Object> readMap(String dfpropPath, String env) {
        return doReadMap(dfpropPath, env, new DfPropReadingMapHandler<Object>() {
            public Map<String, Object> readMap(String path) {
                return actuallyReadMap(path);
            }
        });
    }

    protected Map<String, Object> actuallyReadMap(String path) {
        try {
            return createMapListFileStructural().readMap(createInputStream(path));
        } catch (FileNotFoundException e) {
            return DfCollectionUtil.newLinkedHashMap();
        } catch (RuntimeException e) {
            throwDfPropFileReadFailureException(path, e);
            return null; // unreachable
        }
    }

    /**
     * Read the map string file as string value. <br />
     * If the type of all values is string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * e.g.
     * map:{
     *     ; key1 = string-value1
     *     ; key2 = string-value2
     *     ; ... = ...
     * }
     * </pre>
     * @param dfpropPath The path of DBFlute property file. (NotNull)
     * @param envType The environment type of DBFlute. (NullAllowed: if null, no environment file)
     * @return The read map whose values is string. (NotNull: if not found, returns empty map)
     */
    public Map<String, String> readMapAsStringValue(String dfpropPath, String envType) {
        return doReadMap(dfpropPath, envType, new DfPropReadingMapHandler<String>() {
            public Map<String, String> readMap(String path) {
                return actuallyReadMapAsStringValue(path);
            }
        });
    }

    protected Map<String, String> actuallyReadMapAsStringValue(String path) {
        try {
            return createMapListFileStructural().readMapAsStringValue(createInputStream(path));
        } catch (FileNotFoundException e) {
            return DfCollectionUtil.newLinkedHashMap();
        } catch (RuntimeException e) {
            throwDfPropFileReadFailureException(path, e);
            return null; // unreachable
        }
    }

    /**
     * Read the map string file as string list value. <br />
     * If the type of all values is string list type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * e.g.
     * map:{
     *     ; key1 = list:{string-element1 ; string-element2 ; ...}
     *     ; key2 = list:{string-element1 ; string-element2 ; ...}
     *     ; ... = list:{...}
     * }
     * </pre>
     * @param dfpropPath The path of DBFlute property file. (NotNull)
     * @param envType The environment type of DBFlute. (NullAllowed: if null, no environment file)
     * @return The read map whose values is string list. (NotNull: if not found, returns empty map)
     */
    public Map<String, List<String>> readMapAsStringListValue(String dfpropPath, String envType) {
        return doReadMap(dfpropPath, envType, new DfPropReadingMapHandler<List<String>>() {
            public Map<String, List<String>> readMap(String path) {
                return actuallyReadMapAsStringListValue(path);
            }
        });
    }

    protected Map<String, List<String>> actuallyReadMapAsStringListValue(String path) {
        try {
            return createMapListFileStructural().readMapAsStringListValue(createInputStream(path));
        } catch (FileNotFoundException e) {
            return DfCollectionUtil.newLinkedHashMap();
        } catch (RuntimeException e) {
            throwDfPropFileReadFailureException(path, e);
            return null; // unreachable
        }
    }

    /**
     * Read the map string file as string map value. <br />
     * If the type of all values is string map type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * e.g.
     * map:{
     *     ; key1 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; key2 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; ... = map:{...}
     * }
     * </pre>
     * @param dfpropPath The path of DBFlute property file. (NotNull)
     * @param envType The environment type of DBFlute. (NullAllowed: if null, no environment file)
     * @return The read map whose values is string map. (NotNull: if not found, returns empty map)
     */
    public Map<String, Map<String, String>> readMapAsStringMapValue(String dfpropPath, String envType) {
        return doReadMap(dfpropPath, envType, new DfPropReadingMapHandler<Map<String, String>>() {
            public Map<String, Map<String, String>> readMap(String path) {
                return actuallyReadMapAsStringMapValue(path);
            }
        });
    }

    protected Map<String, Map<String, String>> actuallyReadMapAsStringMapValue(String path) {
        try {
            return createMapListFileStructural().readMapAsStringMapValue(createInputStream(path));
        } catch (FileNotFoundException e) {
            return DfCollectionUtil.newLinkedHashMap();
        } catch (RuntimeException e) {
            throwDfPropFileReadFailureException(path, e);
            return null; // unreachable
        }
    }

    // ===================================================================================
    //                                                                                List
    //                                                                                ====
    // -----------------------------------------------------
    //                                                  Read
    //                                                  ----
    /**
     * Read the list string file. <br />
     * If the type of values is various type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment. <br />
     * <pre>
     * list:{
     *     ; element1
     *     ; list:{element2-1 ; element2-2 }
     *     ; map:{key3-1 = value3-1 ; key3-2 = value3-2 }
     *     ; ... = ...
     * }
     * </pre>
     * @param dfpropPath The path of DBFlute property file. (NotNull)
     * @param envType The environment type of DBFlute. (NullAllowed: if null, no environment file)
     * @return The read list of object. (NotNull: if not found, returns empty list)
     */
    public List<Object> readList(String dfpropPath, String envType) {
        return doReadList(dfpropPath, envType, new DfPropReadingListHandler<Object>() {
            public List<Object> readList(String path) {
                return actuallyReadList(path);
            }
        });
    }

    protected List<Object> actuallyReadList(String path) {
        try {
            return createMapListFileStructural().readList(createInputStream(path));
        } catch (FileNotFoundException e) {
            return DfCollectionUtil.newArrayList();
        } catch (RuntimeException e) {
            throwDfPropFileReadFailureException(path, e);
            return null; // unreachable
        }
    }

    // ===================================================================================
    //                                                                              String
    //                                                                              ======
    // -----------------------------------------------------
    //                                                  Read
    //                                                  ----
    /**
     * Read the string file. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * @param dfpropPath The path of DBFlute property file. (NotNull)
     * @param envType The environment type of DBFlute. (NullAllowed: if null, no environment file)
     * @return The read string. (NotNull: if not found, returns empty string)
     */
    public String readString(String dfpropPath, String envType) {
        return doReadString(dfpropPath, envType, new DfPropReadingStringHandler() {
            public String readString(String path) {
                return actuallyReadString(path);
            }
        });
    }

    protected String actuallyReadString(String path) {
        try {
            return createMapListFilePlain().readString(createInputStream(path));
        } catch (FileNotFoundException e) {
            return "";
        } catch (RuntimeException e) {
            throwDfPropFileReadFailureException(path, e);
            return null; // unreachable
        }
    }

    // ===================================================================================
    //                                                                       Reading Logic
    //                                                                       =============
    protected <ELEMENT> Map<String, ELEMENT> doReadMap(String dfpropPath, String envType,
            DfPropReadingMapHandler<ELEMENT> handler) {
        if (envType != null) {
            final String envPath = deriveEnvPath(dfpropPath, envType);
            Map<String, ELEMENT> map = handler.readMap(envPath);
            if (map.isEmpty()) {
                map = handler.readMap(dfpropPath);
                resolveOutsidePropExtendedMap(map, dfpropPath, handler);
            }
            resolveOutsidePropExtendedMap(map, envPath, handler);
            return map;
        } else {
            final Map<String, ELEMENT> map = handler.readMap(dfpropPath);
            resolveOutsidePropExtendedMap(map, dfpropPath, handler);
            return map;
        }
    }

    protected <ELEMENT> void resolveOutsidePropExtendedMap(Map<String, ELEMENT> map, String path,
            DfPropReadingMapHandler<ELEMENT> handler) {
        final String extendedPath = deriveExtendedPath(path);
        if (extendedPath != null) {
            map.putAll(handler.readMap(extendedPath)); // override here
        }
    }

    protected <ELEMENT> List<ELEMENT> doReadList(String dfpropPath, String envType,
            DfPropReadingListHandler<ELEMENT> handler) {
        // extended list is not supported
        if (envType != null) {
            final String envPath = deriveEnvPath(dfpropPath, envType);
            List<ELEMENT> list = handler.readList(envPath);
            if (list.isEmpty()) {
                list = handler.readList(dfpropPath);
            }
            return list;
        } else {
            return handler.readList(dfpropPath);
        }
    }

    protected String doReadString(String dfpropPath, String envType, DfPropReadingStringHandler handler) {
        // extended string is not supported
        if (envType != null) {
            final String envPath = deriveEnvPath(dfpropPath, envType);
            String list = handler.readString(envPath);
            if (list.isEmpty()) {
                list = handler.readString(dfpropPath);
            }
            return list;
        } else {
            return handler.readString(dfpropPath);
        }
    }

    // ===================================================================================
    //                                                                         Derive Path
    //                                                                         ===========
    protected String deriveEnvPath(String dfpropPath, String envType) {
        final String basePath;
        final String pureFileName;
        if (dfpropPath.contains("/")) {
            basePath = Srl.substringLastFront(dfpropPath, "/");
            pureFileName = Srl.substringLastRear(dfpropPath, "/");
        } else {
            basePath = ".";
            pureFileName = dfpropPath;
        }
        return basePath + "/" + envType + "/" + pureFileName;
    }

    protected String deriveExtendedPath(String path) {
        final String extendableExt = getExtendableExt();
        if (!path.endsWith(extendableExt)) {
            return null;
        }
        return path.substring(0, path.length() - extendableExt.length()) + "+" + extendableExt;
    }

    protected String getExtendableExt() {
        return ".dfprop";
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected InputStream createInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }

    protected void throwDfPropFileReadFailureException(String path, RuntimeException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to read the DBFlute property file.");
        br.addItem("Advice");
        br.addElement("Make sure the map-string is correct in the file.");
        br.addElement("For exapmle, the number of start and end braces are the same.");
        br.addItem("DBFlute Property");
        br.addElement(path);
        final String msg = br.buildExceptionMessage();
        throw new DfPropFileReadFailureException(msg, e);
    }

    // ===================================================================================
    //                                                                       Map List File
    //                                                                       =============
    protected MapListFile createMapListFilePlain() {
        return newMapListFile();
    }

    protected MapListFile createMapListFileStructural() {
        final MapListFile file = newMapListFile();
        if (_skipLineSeparator) {
            file.skipLineSeparator();
        }
        return file;
    }

    protected MapListFile newMapListFile() {
        return new MapListFile();
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public DfPropFile skipLineSeparator() {
        _skipLineSeparator = true;
        return this;
    }
}