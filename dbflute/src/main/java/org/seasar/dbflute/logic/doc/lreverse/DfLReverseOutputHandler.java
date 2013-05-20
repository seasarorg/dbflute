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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataSet;
import org.seasar.dbflute.helper.dataset.DfDataTable;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnTypes;
import org.seasar.dbflute.helper.io.xls.DfXlsWriter;
import org.seasar.dbflute.helper.jdbc.facade.DfJFadCursorCallback;
import org.seasar.dbflute.helper.jdbc.facade.DfJFadCursorHandler;
import org.seasar.dbflute.helper.jdbc.facade.DfJFadResultSetWrapper;
import org.seasar.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.dbflute.helper.token.file.FileMakingOption;
import org.seasar.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.dbflute.helper.token.file.FileToken;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfLReverseOutputHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLReverseOutputHandler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected boolean _containsCommonColumn;
    protected int _xlsLimit = 65000; // as default

    // option for large data
    protected String _delimiterDataDir;

    protected final Map<String, Table> _tableNameMap = new LinkedHashMap<String, Table>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfLReverseOutputHandler(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                          Output Xls
    //                                                                          ==========
    /**
     * Output data excel templates. (using dataSource)
     * @param tableInfoMap The map of table to extract. (NotNull)
     * @param limit The limit of extracted record. (MinusAllowed: if minus, no limit)
     * @param xlsFile The file of xls. (NotNull)
     * @param sectionInfoList The list of section info. (NotNull)
     */
    public void outputData(Map<String, Table> tableInfoMap, int limit, File xlsFile, List<String> sectionInfoList) {
        filterUnsupportedTable(tableInfoMap);
        final DfLReverseDataExtractor extractor = new DfLReverseDataExtractor(_dataSource);
        extractor.setExtractingLimit(limit);
        extractor.setLargeBorder(_xlsLimit);
        final Map<String, DfLReverseDataResult> loadDataMap = extractor.extractData(tableInfoMap);
        transferToXls(tableInfoMap, loadDataMap, limit, xlsFile, sectionInfoList);
    }

    protected void filterUnsupportedTable(Map<String, Table> tableInfoMap) {
        // additional tables are unsupported here
        // because it's not an important function
        final Map<String, Object> additionalTableMap = getAdditionalTableProperties().getAdditionalTableMap();
        for (String tableDbName : additionalTableMap.keySet()) {
            if (tableInfoMap.containsKey(tableDbName)) {
                _log.info("...Skipping additional table: " + tableDbName);
                tableInfoMap.remove(tableDbName);
            }
        }
    }

    /**
     * Transfer data to excel. (state-less)
     * @param tableMap The map of table. (NotNull)
     * @param loadDataMap The map of load data. (NotNull)
     * @param limit The limit of extracted record. (MinusAllowed: if minus, no limit)
     * @param xlsFile The file of xls. (NotNull)
     * @param sectionInfoList The list of section info. (NotNull)
     */
    protected void transferToXls(Map<String, Table> tableMap, Map<String, DfLReverseDataResult> loadDataMap, int limit,
            File xlsFile, List<String> sectionInfoList) {
        final DfDataSet dataSet = new DfDataSet();
        int index = 0;
        for (Entry<String, Table> entry : tableMap.entrySet()) {
            ++index;
            final String tableDbName = entry.getKey();
            final Table table = entry.getValue();
            final DfLReverseDataResult dataResult = loadDataMap.get(tableDbName);
            if (dataResult.isLargeData()) {
                outputDelimiterData(table, dataResult, limit, sectionInfoList);
            } else {
                final List<Map<String, String>> extractedList = dataResult.getResultList();
                setupXlsDataTable(dataSet, table, extractedList, index, sectionInfoList);
            }
        }
        if (dataSet.getTableSize() > 0) {
            writeXlsData(dataSet, xlsFile);
        }
    }

    // ===================================================================================
    //                                                                            Xls Data
    //                                                                            ========
    protected void setupXlsDataTable(DfDataSet dataSet, Table table, List<Map<String, String>> extractedList,
            int index, List<String> sectionInfoList) {
        final int xlsLimit = _xlsLimit;
        final List<Map<String, String>> recordList;
        {
            final String tableInfo = "  " + table.getTableDispName() + " (" + extractedList.size() + ")";
            _log.info(tableInfo);
            sectionInfoList.add(tableInfo);
            if (extractedList.size() > xlsLimit) {
                recordList = extractedList.subList(0, xlsLimit); // just in case
            } else {
                recordList = extractedList;
            }
        }
        final DfDataTable dataTable = new DfDataTable(resolveSheetName(table, index));
        final List<Column> columnList = table.getColumnList();
        for (Column column : columnList) {
            if (isExceptCommonColumn(column)) {
                continue;
            }
            dataTable.addColumn(column.getName(), DfDtsColumnTypes.STRING);
        }
        for (Map<String, String> recordMap : recordList) {
            final Set<String> columnNameSet = recordMap.keySet();
            final DfDataRow dataRow = dataTable.addRow();
            for (String columnName : columnNameSet) {
                if (!dataTable.hasColumn(columnName)) {
                    continue; // basically excepted common columns
                }
                final String value = recordMap.get(columnName);
                dataRow.addValue(columnName, value);
            }
        }
        dataSet.addTable(dataTable);
    }

    protected String resolveSheetName(Table table, int index) {
        String sheetName = deriveSheetName(table);
        if (sheetName.length() > 30) { // restriction of excel
            final String middleParts = sheetName.substring(0, 25);
            boolean resolved = false;
            int basePoint = 0;
            while (true) {
                final String suffixParts = middleParts.substring(basePoint, basePoint + 3);
                sheetName = "$" + middleParts + "_" + suffixParts;
                if (!_tableNameMap.containsKey(sheetName)) {
                    resolved = true;
                    break;
                }
                if (basePoint > 20) {
                    break;
                }
                ++basePoint;
                continue;
            }
            if (!resolved) {
                final String indexExp = (index < 10 ? "0" + index : String.valueOf(index));
                sheetName = "$" + middleParts + "_" + indexExp;
            }
            _tableNameMap.put(sheetName, table);
        }
        return sheetName;
    }

    protected String deriveSheetName(Table table) {
        // if schema-driven, output with table name with schema as sheet name
        // it depends on ReplaceSchema specification whether the data file can be loaded or not
        final String tableDbName = table.getTableDbName();

        // sheet name's case is depends on table display name's case
        // because sheet name (as table name) is also display name
        final DfLittleAdjustmentProperties prop = getLittleAdjustmentProperties();
        return prop.isTableDispNameUpperCase() ? tableDbName.toUpperCase() : tableDbName;
    }

    protected boolean isExceptCommonColumn(Column column) {
        return !_containsCommonColumn && column.isCommonColumn();
    }

    protected void writeXlsData(DfDataSet dataSet, File xlsFile) {
        final DfXlsWriter writer = createXlsWriter(xlsFile);
        try {
            writer.write(dataSet); // flush
        } catch (RuntimeException e) {
            String msg = "Failed to write the xls file: " + xlsFile;
            msg = msg + " tables=" + dataSet.getTableSize();
            throw new IllegalStateException(msg, e);
        }
    }

    protected DfXlsWriter createXlsWriter(File xlsFile) {
        // The xls file should have all string cell type for replace-schema. 
        return new DfXlsWriter(xlsFile).stringCellType();
    }

    // ===================================================================================
    //                                                                      Delimiter Data
    //                                                                      ==============
    protected void outputDelimiterData(final Table table, DfLReverseDataResult templateDataResult, final int limit,
            final List<String> sectionInfoList) {
        if (_delimiterDataDir == null) {
            return;
        }
        final File delimiterDir = new File(_delimiterDataDir);
        final String ext = "tsv"; // fixed
        final FileMakingOption option = new FileMakingOption().encodeAsUTF8().separateLf();
        option.delimitateByTab();
        final String outputInfo = "...Outputting the over-xls-limit table: " + table.getTableDispName();
        _log.info(outputInfo);
        sectionInfoList.add(outputInfo);
        if (!delimiterDir.exists()) {
            delimiterDir.mkdirs();
        }
        final FileToken fileToken = new FileToken();
        // file name uses DB name (no display name) just in case
        final String delimiterFilePath = delimiterDir.getPath() + "/" + table.getTableDbName() + "." + ext;
        final List<String> columnNameList = new ArrayList<String>();
        for (Column column : table.getColumnList()) {
            if (!_containsCommonColumn && column.isCommonColumn()) {
                continue;
            }
            columnNameList.add(column.getName());
        }
        option.headerInfo(columnNameList);
        final DfJFadCursorCallback cursorCallback = templateDataResult.getCursorCallback();
        cursorCallback.select(new DfJFadCursorHandler() {
            int count = 0;

            public void handle(final DfJFadResultSetWrapper wrapper) {
                try {
                    fileToken.make(delimiterFilePath, new FileMakingCallback() {
                        public FileMakingRowResource getRowResource() {
                            try {
                                if (limit >= 0 && limit < count) {
                                    return null;
                                }
                                if (!wrapper.next()) {
                                    return null;
                                }
                                final LinkedHashMap<String, String> nameValueMap = new LinkedHashMap<String, String>();
                                for (String columnName : columnNameList) {
                                    nameValueMap.put(columnName, wrapper.getString(columnName));
                                }
                                final FileMakingRowResource resource = new FileMakingRowResource();
                                resource.setNameValueMap(nameValueMap);
                                ++count;
                                return resource;
                            } catch (SQLException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    }, option);
                } catch (IOException e) {
                    String msg = "Failed to output delimiter data:";
                    msg = msg + " table=" + table.getTableDispName() + " file=" + delimiterFilePath;
                    throw new IllegalStateException(msg, e);
                }
                final String delimiterInfo = " -> " + delimiterFilePath + " (" + count + ")";
                _log.info(delimiterInfo);
                sectionInfoList.add(delimiterInfo);
            }
        });
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    protected DfAdditionalTableProperties getAdditionalTableProperties() {
        return getProperties().getAdditionalTableProperties();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setContainsCommonColumn(boolean containsCommonColumn) {
        _containsCommonColumn = containsCommonColumn;
    }

    public void setXlsLimit(int xlsLimit) {
        _xlsLimit = xlsLimit;
    }

    public String getDelimiterDataDir() {
        return _delimiterDataDir;
    }

    public void setDelimiterDataDir(String delimiterDataDir) {
        _delimiterDataDir = delimiterDataDir;
    }

    public Map<String, Table> getTableNameMap() {
        return _tableNameMap;
    }
}
