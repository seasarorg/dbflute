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
package org.seasar.dbflute.properties.assistant.freegen.filepath;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfFilePathTableLoader {

    // ===================================================================================
    //                                                                          Load Table
    //                                                                          ==========
    // ; resourceMap = map:{
    //     ; baseDir = ../src/main
    //     ; resourceType = FILE_PATH
    // }
    // ; outputMap = map:{
    //     ; templateFile = JspPath.vm
    //     ; outputDirectory = $$baseDir$$/java
    //     ; package = org.seasar.dbflute...
    //     ; className = JspPath
    // }
    // ; tableMap = map:{
    //     ; targetDir = $$baseDir$$/webapp/WEB-INF/view
    //     ; targetPathList = list:{ suffix:.jsp }
    //     ; exceptPathList = list:{ contain:/view/common/ }
    // }
    public DfFreeGenTable loadTable(String requestName, DfFreeGenResource resource, Map<String, Object> tableMap,
            Map<String, Map<String, String>> mappingMap) {
        final String targetDir = resource.resolveBaseDir((String) tableMap.get("targetDir"));

        final List<String> targetPathList = extractTargetPathList(tableMap);
        final List<String> exceptPathList = extractExceptPathList(tableMap);
        final List<File> fileList = DfCollectionUtil.newArrayList();

        collectFile(fileList, targetPathList, exceptPathList, new File(targetDir));
        final List<Map<String, Object>> columnList = DfCollectionUtil.newArrayList();
        for (File file : fileList) {
            final Map<String, Object> columnMap = DfCollectionUtil.newHashMap();
            final String fileName = file.getName();
            columnMap.put("fileName", fileName);

            final String domainPath = buildDomainPath(file, targetDir);
            columnMap.put("domainPath", domainPath); // e.g. /view/member/index.jsp

            columnMap.put("defName", buildUpperSnakeName(domainPath));
            {
                final String dirPath = Srl.substringLastFront(domainPath, "/");
                final String snakeCase = buildPlainSnakeName(dirPath);
                final String camelizedName = Srl.camelize(snakeCase);
                columnMap.put("camelizedDir", camelizedName);
                columnMap.put("capCamelDir", Srl.initCap(camelizedName));
                columnMap.put("uncapCamelDir", Srl.initUncap(camelizedName));
            }
            {
                final String snakeCase = buildPlainSnakeName(fileName);
                final String camelizedName = Srl.camelize(snakeCase);
                columnMap.put("camelizedFile", camelizedName);
                columnMap.put("capCamelFile", Srl.initCap(camelizedName));
                columnMap.put("uncapCamelFile", Srl.initUncap(camelizedName));
            }

            columnList.add(columnMap);
        }
        return new DfFreeGenTable(tableMap, "dummy", columnList);
    }

    protected List<String> extractTargetPathList(Map<String, Object> tableMap) {
        @SuppressWarnings("unchecked")
        final List<String> targetPathList = (List<String>) tableMap.get("targetPathList");
        if (targetPathList != null) {
            return targetPathList;
        }
        return DfCollectionUtil.emptyList();
    }

    protected List<String> extractExceptPathList(Map<String, Object> tableMap) {
        @SuppressWarnings("unchecked")
        final List<String> exceptPathList = (List<String>) tableMap.get("exceptPathList");
        if (exceptPathList != null) {
            return exceptPathList;
        }
        return DfCollectionUtil.emptyList();
    }

    // ===================================================================================
    //                                                                        Collect File
    //                                                                        ============
    protected void collectFile(List<File> fileList, final List<String> targetPathList,
            final List<String> exceptPathList, File baseFile) {
        if (isExceptFile(targetPathList, exceptPathList, baseFile)) {
            return;
        }
        if (baseFile.isFile()) { // only target extension here
            fileList.add(baseFile);
        } else if (baseFile.isDirectory()) {
            final File[] listFiles = baseFile.listFiles();
            if (listFiles != null) {
                for (File currentFile : listFiles) {
                    collectFile(fileList, targetPathList, exceptPathList, currentFile);
                }
            }
        }
    }

    protected boolean isExceptFile(List<String> targetPathList, List<String> exceptPathList, File baseFile) {
        final String baseFilePath = toPath(baseFile);
        return !DfNameHintUtil.isTargetByHint(baseFilePath, targetPathList, exceptPathList);
    }

    // ===================================================================================
    //                                                                        Build String
    //                                                                        ============
    protected String buildDomainPath(File file, String targetDir) {
        return Srl.substringFirstRear(toPath(file), targetDir);
    }

    protected String buildUpperSnakeName(String domainPath) {
        return buildPlainSnakeName(domainPath).toUpperCase();
    }

    protected String buildPlainSnakeName(String domainPath) {
        final String dlm = "_";
        String tmp = domainPath;
        tmp = replace(replace(replace(replace(tmp, ".", dlm), "-", dlm), "/", dlm), "__", dlm);
        return Srl.trim(tmp, dlm);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String toPath(File file) {
        return replace(file.getPath(), "\\", "/");
    }

    protected String replace(String str, String fromStr, String toStr) {
        return Srl.replace(str, fromStr, toStr);
    }
}
