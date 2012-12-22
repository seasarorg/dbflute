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
package org.seasar.dbflute.properties.assistant.freegen.filepath;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.util.DfCollectionUtil;
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
    //     ; targetSuffix = .jsp
    // }
    public DfFreeGenTable loadTable(String requestName, DfFreeGenResource resource, Map<String, Object> tableMap,
            Map<String, Map<String, String>> mappingMap) {
        final String targetDir = resource.resolveBaseDir((String) tableMap.get("targetDir"));
        final String targetSuffix = (String) tableMap.get("targetSuffix");
        final List<File> fileList = DfCollectionUtil.newArrayList();
        collectFile(fileList, targetSuffix, new File(targetDir));
        final List<Map<String, Object>> columnList = DfCollectionUtil.newArrayList();
        for (File file : fileList) {
            final Map<String, Object> columnMap = DfCollectionUtil.newHashMap();
            final String fileName = file.getName();
            columnMap.put("fileName", fileName);

            final String domainPath = buildDomainPath(file, targetDir);
            columnMap.put("domainPath", domainPath); // e.g. /view/member/index.jsp

            columnMap.put("defName", buildUpperSnakeName(domainPath, targetSuffix, false));
            columnMap.put("defNameNoSuffix", buildUpperSnakeName(domainPath, targetSuffix, true));

            {
                final String dirPath = Srl.substringLastFront(domainPath, "/");
                final String snakeCase = buildPlainSnakeName(dirPath, targetSuffix, false);
                final String camelizedName = Srl.camelize(snakeCase);
                columnMap.put("camelizedDir", camelizedName);
                columnMap.put("capCamelDir", Srl.initCap(camelizedName));
                columnMap.put("uncapCamelDir", Srl.initUncap(camelizedName));
            }
            {
                final String snakeCase = buildPlainSnakeName(fileName, targetSuffix, false);
                final String camelizedName = Srl.camelize(snakeCase);
                columnMap.put("camelizedFile", camelizedName);
                columnMap.put("capCamelFile", Srl.initCap(camelizedName));
                columnMap.put("uncapCamelFile", Srl.initUncap(camelizedName));
            }
            {
                final String snakeCase = buildPlainSnakeName(fileName, targetSuffix, true);
                final String camelizedName = Srl.camelize(snakeCase);
                columnMap.put("camelizedFileNoSuffix", camelizedName);
                columnMap.put("capCamelFileNoSuffix", Srl.initCap(camelizedName));
                columnMap.put("uncapCamelFileNoSuffix", Srl.initUncap(camelizedName));
            }

            columnList.add(columnMap);
        }
        return new DfFreeGenTable(tableMap, "dummy", columnList);
    }

    protected void collectFile(List<File> fileList, final String targetSuffix, File baseFile) {
        if (baseFile.isFile()) { // only target extension here
            fileList.add(baseFile);
        } else if (baseFile.isDirectory()) {
            final File[] listFiles = baseFile.listFiles(new FileFilter() {
                public boolean accept(File currentFile) {
                    return currentFile.getName().endsWith(targetSuffix) || currentFile.isDirectory();
                }
            });
            if (listFiles != null) {
                for (File currentFile : listFiles) {
                    collectFile(fileList, targetSuffix, currentFile);
                }
            }
        }
    }

    protected String buildDomainPath(File file, String targetDir) {
        final String resolvedPath = Srl.replace(file.getPath(), "\\", "/");
        return Srl.substringFirstRear(resolvedPath, targetDir);
    }

    protected String buildUpperSnakeName(String domainPath, String targetSuffix, boolean suppressSuffix) {
        return buildPlainSnakeName(domainPath, targetSuffix, suppressSuffix).toUpperCase();
    }

    protected String buildPlainSnakeName(String domainPath, String targetSuffix, boolean suppressSuffix) {
        String tmp;
        if (suppressSuffix && domainPath.endsWith(targetSuffix)) {
            tmp = Srl.substringLastFrontIgnoreCase(domainPath, targetSuffix);
        } else {
            tmp = domainPath;
        }
        tmp = replace(replace(replace(tmp, ".", "_"), "-", "_"), "/", "_");
        return Srl.trim(tmp, "_");
    }

    protected String replace(String str, String fromStr, String toStr) {
        return Srl.replace(str, fromStr, toStr);
    }
}
