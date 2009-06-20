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
package org.seasar.dbflute.helper.io.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author jflute
 * @since 0.5.4 (2007/07/18)
 */
public class DfStringFileReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _saveInitialUnicodeBom;

    protected String _lineCommentMark = "#";

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public String readString(String path, String encoding) {
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
                    if (_lineCommentMark != null && lineString.trim().startsWith(_lineCommentMark)) {
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
        if (!_saveInitialUnicodeBom) {
            return removeInitialUnicodeBomIfNeeds(encoding, sb.toString());
        } else {
            return sb.toString();
        }
    }

    protected String removeInitialUnicodeBomIfNeeds(String encoding, String value) {
        if ("UTF-8".equalsIgnoreCase(encoding) && value.length() > 0 && value.charAt(0) == '\uFEFF') {
            value = value.substring(1);
        }
        return value;
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