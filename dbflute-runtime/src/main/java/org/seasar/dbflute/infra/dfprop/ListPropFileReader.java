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
import java.util.List;

import org.seasar.dbflute.helper.mapstring.impl.MapListStringImpl;

/**
 * @author jflute
 * @since 0.9.6 (2009/10/28 Wednesday)
 */
public class ListPropFileReader implements DfPropFileReader {

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
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

    protected String removeInitialUnicodeBomIfNeeds(String encoding, String value) {
        if (UTF8_ENCODING.equalsIgnoreCase(encoding) && value.length() > 0 && value.charAt(0) == '\uFEFF') {
            value = value.substring(1);
        }
        return value;
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
}