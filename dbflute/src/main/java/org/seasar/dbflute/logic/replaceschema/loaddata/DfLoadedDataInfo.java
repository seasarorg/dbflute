/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.replaceschema.loaddata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class DfLoadedDataInfo {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String COMMON_ENV_TYPE = "common";
    public static final String TSV_FILE_TYPE = "tsv";
    public static final String CSV_FILE_TYPE = "csv";
    public static final String XLS_FILE_TYPE = "xls";
    public static final String FIRSTXLS_FILE_TYPE = "firstxls";
    public static final String TSV_DELIMITER = "\t";
    public static final String CSV_DELIMITER = ",";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<DfLoadedFile> _loadedFileList = new ArrayList<DfLoadedFile>();
    protected final Map<String, List<DfLoadedFile>> _envTypeListMap = new LinkedHashMap<String, List<DfLoadedFile>>();
    protected final Map<String, List<DfLoadedFile>> _fileTypeListMap = new LinkedHashMap<String, List<DfLoadedFile>>();
    protected final Map<String, Map<String, List<DfLoadedFile>>> _hierarchyListMap = new LinkedHashMap<String, Map<String, List<DfLoadedFile>>>();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    public DfLoadedDataInfo() {
    }

    // ===================================================================================
    //                                                                            Sub List
    //                                                                            ========
    public List<DfLoadedFile> findLoadedFileListByEnvType(String envType) {
        return _envTypeListMap.get(envType);
    }

    public List<DfLoadedFile> findLoadedFileListByFileType(String fileType) {
        return _fileTypeListMap.get(fileType);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<DfLoadedFile> getLoadedFileList() {
        return _loadedFileList;
    }

    public Map<String, Map<String, List<DfLoadedFile>>> getLoadedFileListHierarchyMap() {
        return _hierarchyListMap;
    }

    public void addLoadedFile(String envType, String fileType, String encoding, String fileName, boolean warned) {
        final DfLoadedFile loadedFile = new DfLoadedFile(envType, fileType, encoding, fileName, warned);
        _loadedFileList.add(loadedFile);

        Map<String, List<DfLoadedFile>> fileTypeKeyListMap = _hierarchyListMap.get(envType);
        if (fileTypeKeyListMap == null) {
            _hierarchyListMap.put(envType, new LinkedHashMap<String, List<DfLoadedFile>>());
            fileTypeKeyListMap = _hierarchyListMap.get(envType);
        }
        List<DfLoadedFile> elementList = fileTypeKeyListMap.get(fileType);
        if (elementList == null) {
            fileTypeKeyListMap.put(fileType, new ArrayList<DfLoadedFile>());
            elementList = fileTypeKeyListMap.get(fileType);
        }
        elementList.add(loadedFile);
    }

    protected void addToEnvTypeList(DfLoadedFile loadedFile, String envType) {
        List<DfLoadedFile> elementList = _envTypeListMap.get(envType);
        if (elementList == null) {
            _envTypeListMap.put(envType, new ArrayList<DfLoadedFile>());
            elementList = _envTypeListMap.get(envType);
        }
        elementList.add(loadedFile);
    }

    protected void addToFileTypeList(DfLoadedFile loadedFile, String fileType) {
        List<DfLoadedFile> elementList = _fileTypeListMap.get(fileType);
        if (elementList == null) {
            _fileTypeListMap.put(fileType, new ArrayList<DfLoadedFile>());
            elementList = _fileTypeListMap.get(fileType);
        }
        elementList.add(loadedFile);
    }
}
