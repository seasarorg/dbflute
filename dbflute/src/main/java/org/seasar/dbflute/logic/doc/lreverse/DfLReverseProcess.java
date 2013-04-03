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
package org.seasar.dbflute.logic.doc.lreverse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.mapstring.MapListString;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/23 Saturday)
 */
public class DfLReverseProcess {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLReverseProcess.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfLReverseOutputHandler _outputHandler;
    protected final String _outputDir;
    protected final String _fileTitle;
    protected final int _limit;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfLReverseProcess(DfLReverseOutputHandler outputHandler, String outputDir, String fileTitle, int limit) {
        _outputHandler = outputHandler;
        _outputDir = outputDir;
        _fileTitle = fileTitle;
        _limit = limit;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public void execute(Database database) {
        final List<Table> tableList = filterTableList(database);
        final List<String> sectionInfoList = new ArrayList<String>();
        final String beginTitle = "...Outputting load data: tables=" + tableList.size();
        _log.info(beginTitle);
        sectionInfoList.add(beginTitle);
        final List<List<Table>> orderedList = analyzeOrder(tableList);
        int sectionNo = 1;
        final File baseDir = new File(_outputDir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        deletePreviousDataFile(baseDir);
        for (List<Table> nestedList : orderedList) {
            final Map<String, Table> tableInfoMap = new LinkedHashMap<String, Table>();
            for (Table table : nestedList) {
                tableInfoMap.put(table.getName(), table);
            }
            final String number = (sectionNo < 10 ? "0" + sectionNo : String.valueOf(sectionNo));
            final String mainName = extractMainName(nestedList);
            final String sectionTitle = "[Section " + sectionNo + "]: " + mainName;
            _log.info(sectionTitle);
            sectionInfoList.add(sectionTitle);
            final File xlsFile = new File(buildXlsFilePath(number, mainName));
            _outputHandler.outputData(tableInfoMap, _limit, xlsFile, sectionInfoList);
            ++sectionNo;
        }
        final Map<String, Table> tableNameMap = _outputHandler.getTableNameMap();
        if (!tableNameMap.isEmpty()) {
            outputTableNameMap(tableNameMap);
        }
        outputResultMark(sectionInfoList);
    }

    protected void deletePreviousDataFile(File baseDir) {
        doDeletePreviousDataFile(baseDir, new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(_fileTitle) && name.endsWith(".xls");
            }
        });
        final String delimiterDataDir = _outputHandler.getDelimiterDataDir();
        if (delimiterDataDir != null) {
            doDeletePreviousDataFile(new File(delimiterDataDir), new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".tsv");
                }
            });
        }
    }

    protected void doDeletePreviousDataFile(File baseDir, FilenameFilter filter) {
        final File[] listFiles = baseDir.listFiles(filter);
        if (listFiles != null) {
            for (File previousFile : listFiles) {
                previousFile.delete();
            }
        }
    }

    protected String buildXlsFilePath(String number, String mainName) {
        return _outputDir + "/" + _fileTitle + "-" + number + "-" + mainName + ".xls";
    }

    // ===================================================================================
    //                                                                          Table List
    //                                                                          ==========
    protected List<Table> filterTableList(Database database) {
        final List<Table> tableList = database.getTableList();
        final List<Table> filteredList = DfCollectionUtil.newArrayListSized(tableList.size());
        for (Table table : tableList) {
            if (isTargetTable(table)) {
                filteredList.add(table);
            }
        }
        return filteredList;
    }

    protected boolean isTargetTable(Table table) {
        return getDocumentProperties().isLoadDataReverseTableTarget(table.getTableDbName());
    }

    // ===================================================================================
    //                                                                       Analyze Order
    //                                                                       =============
    protected List<List<Table>> analyzeOrder(List<Table> tableList) {
        final DfTableOrderAnalyzer analyzer = new DfTableOrderAnalyzer();
        return analyzer.analyzeOrder(tableList);
    }

    protected String extractMainName(List<Table> tableList) {
        final DfTableOrderAnalyzer analyzer = new DfTableOrderAnalyzer();
        return analyzer.extractMainName(tableList);
    }

    // ===================================================================================
    //                                                                      Table Name Map
    //                                                                      ==============
    protected void outputTableNameMap(Map<String, Table> tableNameMap) {
        final Map<String, String> map = new LinkedHashMap<String, String>();
        for (Entry<String, Table> entry : tableNameMap.entrySet()) {
            final String sheetName = entry.getKey();
            final Table table = entry.getValue();
            map.put(sheetName, table.getTableSqlName());
        }
        final String mapString = new MapListString().buildMapString(map);
        final File dataPropFile = new File(_outputDir + "/tableNameMap.dataprop");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPropFile), "UTF-8"));
            bw.write(mapString);
            bw.flush();
        } catch (IOException e) {
            String msg = "Failed to write tableNameMap.dataprop: " + dataPropFile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                         Result Mark
    //                                                                         ===========
    protected void outputResultMark(List<String> sectionInfoList) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ln()).append("* * * * * * * * * * *");
        sb.append(ln()).append("*                   *");
        sb.append(ln()).append("* Load Data Reverse *");
        sb.append(ln()).append("*                   *");
        sb.append(ln()).append("* * * * * * * * * * *");
        for (String sectionInfo : sectionInfoList) {
            sb.append(ln()).append(sectionInfo);
        }
        final Date currentDate = DfTypeUtil.toDate(DBFluteSystem.currentTimeMillis());
        final String currentExp = DfTypeUtil.toString(currentDate, "yyyy/MM/dd HH:mm:ss");
        sb.append(ln()).append(ln()).append("Output Date: ").append(currentExp);
        final File dataPropFile = new File(_outputDir + "/load-data-result.dfmark");
        if (dataPropFile.exists()) {
            dataPropFile.delete();
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPropFile), "UTF-8"));
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            String msg = "Failed to write load-data-result.dfmark: " + dataPropFile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }
}
