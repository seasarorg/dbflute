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
package org.seasar.dbflute.helper.io.fileread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.mapstring.DfMapListStringImpl;

/**
 * @author jflute
 */
public class DfMapStringFileReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _saveInitialUnicodeBom;

    protected String _lineCommentMark = "#";

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    /**
     * @param path The file path. (NotNull)
     * @param encoding The file encoding. (NotNull)
     * @return The read map. (NotNull)
     */
    public Map<String, Object> readMap(String path, String encoding) {
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
                    if (count == 0 && !_saveInitialUnicodeBom) {
                        lineString = removeInitialUnicodeBomIfNeeds(encoding, lineString);
                    }
                    if (lineString.trim().length() == 0) {
                        continue;
                    }
                    // If the line is comment...
                    if (_lineCommentMark != null && lineString.trim().startsWith(_lineCommentMark)) {
                        continue;
                    }
                    sb.append(lineString);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (sb.toString().trim().length() == 0) {
            return new LinkedHashMap<String, Object>();
        }
        final DfMapListStringImpl mapListString = new DfMapListStringImpl();
        return mapListString.generateMap(sb.toString());
    }

    protected String removeInitialUnicodeBomIfNeeds(String encoding, String value) {
        if ("UTF-8".equalsIgnoreCase(encoding) && value.length() > 0 && value.charAt(0) == '\uFEFF') {
            value = value.substring(1);
        }
        return value;
    }

    public Map<String, String> readMapAsStringValue(String path, String encoding) {
        final Map<String, String> resultMap = new LinkedHashMap<String, String>();
        final Map<String, Object> map = readMap(path, encoding);
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            resultMap.put(key, (String) map.get(key));
        }
        return resultMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, java.util.List<String>> readMapAsListStringValue(String path, String encoding) {
        final Map<String, java.util.List<String>> resultMap = new LinkedHashMap<String, java.util.List<String>>();
        final Map<String, Object> map = readMap(path, encoding);
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            resultMap.put(key, (java.util.List<String>) map.get(key));
        }
        return resultMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, java.util.Map<String, String>> readMapAsMapValue(String path, String encoding) {
        final Map<String, java.util.Map<String, String>> resultMap = new LinkedHashMap<String, java.util.Map<String, String>>();
        final Map<String, Object> map = readMap(path, encoding);
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            resultMap.put(key, (java.util.Map<String, String>) map.get(key));
        }
        return resultMap;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSaveInitialUnicodeBom(boolean saveInitialUnicodeBom) {
        _saveInitialUnicodeBom = saveInitialUnicodeBom;
    }

    public void setLineCommentMark(String lineCommentMark) {
        _lineCommentMark = lineCommentMark;
    }
}