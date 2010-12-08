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
package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfTableDataRegistrationFailureException;
import org.seasar.dbflute.exception.DfTableNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.dataset.DfDataColumn;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataSet;
import org.seasar.dbflute.helper.dataset.DfDataTable;
import org.seasar.dbflute.helper.dataset.states.DfDtsCreatedState;
import org.seasar.dbflute.helper.dataset.states.DfDtsSqlContext;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnType;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnTypes;
import org.seasar.dbflute.helper.io.xls.DfXlsReader;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfXlsDataHandler;
import org.seasar.dbflute.properties.filereader.DfMapStringFileReader;

/**
 * @author jflute
 */
public class DfXlsDataHandlerImpl extends DfAbsractDataWriter implements DfXlsDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfXlsDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The pattern of skip sheet. (Nullable) */
    protected Pattern _skipSheetPattern;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfXlsDataHandlerImpl(DataSource dataSource) {
        super(dataSource); // use database only when writing
    }

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public List<DfDataSet> readSeveralData(String dataDirectoryName) {
        final List<File> xlsList = getXlsList(dataDirectoryName);
        final List<DfDataSet> ls = new ArrayList<DfDataSet>();
        for (File file : xlsList) {
            final DfXlsReader xlsReader = createXlsReader(dataDirectoryName, file);
            ls.add(xlsReader.read());
        }
        return ls;
    }

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====
    public void writeSeveralData(String dataDirectoryName) {
        final List<File> xlsList = getXlsList(dataDirectoryName);
        for (File file : xlsList) {
            _log.info("");
            _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
            _log.info("writeData(" + file + ")");
            _log.info("= = = = = = =/");
            final DfXlsReader xlsReader = createXlsReader(dataDirectoryName, file);
            final DfDataSet dataSet = xlsReader.read();

            filterValidColumn(dataSet);
            setupDefaultValue(dataDirectoryName, dataSet);

            doWriteDataSet(file, dataSet);
        }
    }

    protected void doWriteDataSet(File file, DfDataSet dataSet) {
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DfDataTable dataTable = dataSet.getTable(i);
            doWriteDataTable(file, dataTable);
        }
    }

    protected void doWriteDataTable(File file, DfDataTable dataTable) {
        final String tableName = dataTable.getTableName();
        if (dataTable.getRowSize() == 0) {
            _log.info("*Not found row at the table: " + tableName);
            return;
        }

        // set up columnMetaInfo
        final Map<String, DfColumnMetaInfo> columnMap = getColumnInfoMap(tableName);

        // process before handling table
        beforeHandlingTable(dataTable.getTableName(), columnMap);

        // set up columnNameList
        final List<String> columnNameList = new ArrayList<String>();
        for (int j = 0; j < dataTable.getColumnSize(); j++) {
            final DfDataColumn dataColumn = dataTable.getColumn(j);
            final String columnName = dataColumn.getColumnName();
            columnNameList.add(columnName);
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = _dataSource.getConnection();
            for (int j = 0; j < dataTable.getRowSize(); j++) {
                final DfDataRow dataRow = dataTable.getRow(j);
                if (ps == null) {
                    final MyCreatedState myCreatedState = new MyCreatedState();
                    final String preparedSql = myCreatedState.buildPreparedSql(dataRow);
                    ps = conn.prepareStatement(preparedSql);
                }
                doWriteDataRow(file, dataTable, dataRow, columnMap, columnNameList, conn, ps);
            }
            if (!_suppressBatchUpdate) {
                ps.executeBatch();
            }
        } catch (SQLException e) {
            final SQLException nextEx = e.getNextException();
            if (nextEx != null && !e.equals(nextEx)) { // focus on next exception
                _log.warn("*Failed to register: " + e.getMessage());
                String msg = buildExceptionMessage(file, tableName, nextEx);
                throw new DfTableDataRegistrationFailureException(msg, nextEx); // switch!
            }
            String msg = buildExceptionMessage(file, tableName, e);
            throw new DfTableDataRegistrationFailureException(msg, e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ignored) {
                    _log.info("Statement#close() threw the exception!", ignored);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("Connection#close() threw the exception!", ignored);
                }
            }
            // process after (finally) handling table
            finallyHandlingTable(dataTable.getTableName(), columnMap);
        }
    }

    protected String buildExceptionMessage(File file, String tableName, Exception e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to register the table data.");
        br.addItem("Xls File");
        br.addElement(file);
        br.addItem("Table");
        br.addElement(tableName);
        br.addItem("Message");
        br.addElement(e.getMessage());
        final Map<String, Class<?>> bindTypeCacheMap = _bindTypeCacheMap.get(tableName);
        if (bindTypeCacheMap != null) {
            br.addItem("Bind Type");
            final Set<Entry<String, Class<?>>> entrySet = bindTypeCacheMap.entrySet();
            for (Entry<String, Class<?>> entry : entrySet) {
                br.addElement(entry.getKey() + " = " + entry.getValue());
            }
        }
        final Map<String, StringProcessor> stringProcessorCacheMap = _stringProcessorCacheMap.get(tableName);
        if (bindTypeCacheMap != null) {
            br.addItem("String Processor");
            final Set<Entry<String, StringProcessor>> entrySet = stringProcessorCacheMap.entrySet();
            for (Entry<String, StringProcessor> entry : entrySet) {
                br.addElement(entry.getKey() + " = " + entry.getValue());
            }
        }
        return br.buildExceptionMessage();
    }

    protected void beforeHandlingTable(String tableName, Map<String, DfColumnMetaInfo> columnMap) {
        if (_dataWritingInterceptor != null) {
            _dataWritingInterceptor.processBeforeHandlingTable(tableName, columnMap);
        }
    }

    protected void finallyHandlingTable(String tableName, Map<String, DfColumnMetaInfo> columnMap) {
        if (_dataWritingInterceptor != null) {
            _dataWritingInterceptor.processFinallyHandlingTable(tableName, columnMap);
        }
    }

    protected void doWriteDataRow(File file, DfDataTable dataTable, DfDataRow dataRow,
            Map<String, DfColumnMetaInfo> columnMetaInfoMap, List<String> columnNameList, Connection conn,
            PreparedStatement ps) throws SQLException {
        final String tableName = dataTable.getTableName();
        // ColumnValue and ColumnObject
        final ColumnContainer columnContainer = createColumnContainer(dataTable, dataRow);
        final Map<String, Object> columnValueMap = columnContainer.getColumnValueMap();
        if (columnValueMap.isEmpty()) {
            String msg = "The table was not found in the file:";
            msg = msg + " tableName=" + tableName + " file=" + file;
            throw new DfTableNotFoundException(msg);
        }
        if (_loggingInsertSql) {
            final List<Object> valueList = new ArrayList<Object>(columnValueMap.values());
            _log.info(getSql4Log(tableName, columnNameList, valueList));
        }
        int bindCount = 1;
        final Set<Entry<String, Object>> entrySet = columnValueMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String columnName = entry.getKey();
            final Object obj = entry.getValue();

            // - - - - - - - - - - - - - - - - - - -
            // Process Null (against Null Headache)
            // - - - - - - - - - - - - - - - - - - -
            if (processNull(tableName, columnName, obj, ps, bindCount, columnMetaInfoMap)) {
                bindCount++;
                continue;
            }

            // - - - - - - - - - - - - - - -
            // Process NotNull and NotString
            // - - - - - - - - - - - - - - -
            // If the value is not null and the value has the own type except string,
            // It registers the value to statement by the type.
            if (processNotNullNotString(tableName, columnName, obj, conn, ps, bindCount, columnMetaInfoMap)) {
                bindCount++;
                continue;
            }

            // - - - - - - - - - - - - - - - - - - -
            // Process NotNull and StringExpression
            // - - - - - - - - - - - - - - - - - - -
            final String value = (String) obj;
            processNotNullString(tableName, columnName, value, conn, ps, bindCount, columnMetaInfoMap);
            bindCount++;
        }
        if (_suppressBatchUpdate) {
            ps.execute();
        } else {
            ps.addBatch();
        }
    }

    // ===================================================================================
    //                                                                        Xls Handling
    //                                                                        ============
    protected DfXlsReader createXlsReader(String dataDirectoryName, File file) {
        final Map<String, String> tableNameMap = getTableNameMap(dataDirectoryName);
        final Map<String, List<String>> notTrimTableColumnMap = getNotTrimTableColumnMap(dataDirectoryName);
        final Map<String, List<String>> emptyStringTableColumnMap = getEmptyStringTableColumnMap(dataDirectoryName);
        final DfXlsReader xlsReader = new DfXlsReader(file, tableNameMap, notTrimTableColumnMap,
                emptyStringTableColumnMap, _skipSheetPattern);
        if (tableNameMap != null && !tableNameMap.isEmpty()) {
            _log.info("/- - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            _log.info("tableNameMap = " + tableNameMap);
            _log.info("- - - - - - - - - -/");
        }
        return xlsReader;
    }

    public List<File> getXlsList(String dataDirectoryName) {
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final SortedSet<File> sortedFileSet = new TreeSet<File>(fileNameAscComparator);

        final File dir = new File(dataDirectoryName);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls");
            }
        };
        final File[] listFiles = dir.listFiles(filter);
        if (listFiles == null) {
            return new ArrayList<File>();
        }
        for (File file : listFiles) {
            sortedFileSet.add(file);
        }
        return new ArrayList<File>(sortedFileSet);
    }

    protected void filterValidColumn(final DfDataSet dataSet) {
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DfDataTable table = dataSet.getTable(i);
            final String tableName = table.getTableName();

            final Map<String, DfColumnMetaInfo> metaInfoMap = getColumnInfoMap(tableName);
            for (int j = 0; j < table.getColumnSize(); j++) {
                final DfDataColumn dataColumn = table.getColumn(j);
                if (!metaInfoMap.containsKey(dataColumn.getColumnName())) {
                    dataColumn.setWritable(false);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    protected void setupDefaultValue(String dataDirectoryName, final DfDataSet dataSet) {
        final Map<String, String> defaultValueMap = getDefaultValueMap(dataDirectoryName);
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DfDataTable table = dataSet.getTable(i);
            final Set<String> defaultValueMapKeySet = defaultValueMap.keySet();
            final String tableName = table.getTableName();

            final Map<String, DfColumnMetaInfo> metaInfoMap = getColumnInfoMap(tableName);
            for (String defaultTargetColumnName : defaultValueMapKeySet) {
                final String defaultValue = defaultValueMap.get(defaultTargetColumnName);

                if (metaInfoMap.containsKey(defaultTargetColumnName) && !table.hasColumn(defaultTargetColumnName)) {
                    final DfDtsColumnType columnType;
                    final Object value;
                    if (defaultValue.equalsIgnoreCase("sysdate")) {
                        columnType = DfDtsColumnTypes.TIMESTAMP;
                        value = new Timestamp(System.currentTimeMillis());
                    } else {
                        columnType = DfDtsColumnTypes.STRING;
                        value = defaultValue;
                    }
                    table.addColumn(defaultTargetColumnName, columnType);

                    int rowSize = table.getRowSize();
                    for (int j = 0; j < table.getRowSize(); j++) {
                        final DfDataRow row = table.getRow(j);
                        row.addValue(defaultTargetColumnName, value);
                        ++rowSize;
                    }
                }
            }
        }
    }

    private Map<String, String> getDefaultValueMap(String dataDirectoryName) {
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        String path = dataDirectoryName + "/defaultValueMap.dataprop";
        Map<String, String> resultMap = reader.readMapAsStringValue(path);
        if (resultMap != null && !resultMap.isEmpty()) {
            return resultMap;
        }
        path = dataDirectoryName + "/default-value.txt";
        resultMap = reader.readMapAsStringValue(path);
        return resultMap;
    }

    private Map<String, String> getTableNameMap(String dataDirectoryName) {
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        String path = dataDirectoryName + "/tableNameMap.dataprop";
        Map<String, String> resultMap = reader.readMapAsStringValue(path);
        if (resultMap == null || resultMap.isEmpty()) {
            path = dataDirectoryName + "/table-name.txt";
            resultMap = reader.readMapAsStringValue(path);
        }
        final StringKeyMap<String> flmap = StringKeyMap.createAsFlexible();
        flmap.putAll(resultMap);
        return flmap;
    }

    private Map<String, List<String>> getNotTrimTableColumnMap(String dataDirectoryName) {
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        String path = dataDirectoryName + "/notTrimColumnMap.dataprop";
        Map<String, List<String>> resultMap = reader.readMapAsStringListValue(path);
        if (resultMap == null || resultMap.isEmpty()) {
            path = dataDirectoryName + "/not-trim-column.txt";
            resultMap = reader.readMapAsStringListValue(path);
        }
        final Set<Entry<String, List<String>>> entrySet = resultMap.entrySet();
        final StringKeyMap<List<String>> stringKeyMap = StringKeyMap.createAsFlexible();
        for (Entry<String, List<String>> entry : entrySet) {
            stringKeyMap.put(entry.getKey(), entry.getValue());
        }
        return stringKeyMap;
    }

    private Map<String, List<String>> getEmptyStringTableColumnMap(String dataDirectoryName) {
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        String path = dataDirectoryName + "/emptyStringColumnMap.dataprop";
        Map<String, List<String>> resultMap = reader.readMapAsStringListValue(path);
        if (resultMap == null || resultMap.isEmpty()) {
            path = dataDirectoryName + "/empty-string-column.txt";
            resultMap = reader.readMapAsStringListValue(path);
        }
        final Set<Entry<String, List<String>>> entrySet = resultMap.entrySet();
        final StringKeyMap<List<String>> stringKeyMap = StringKeyMap.createAsFlexible();
        for (Entry<String, List<String>> entry : entrySet) {
            stringKeyMap.put(entry.getKey(), entry.getValue());
        }
        return stringKeyMap;
    }

    protected String getSql4Log(String tableName, List<String> columnNameList,
            final List<? extends Object> bindParameters) {
        String bindParameterString = bindParameters.toString();
        bindParameterString = bindParameterString.substring(1, bindParameterString.length() - 1);
        return tableName + ":{" + bindParameterString + "}";
    }

    protected ColumnContainer createColumnContainer(final DfDataTable dataTable, final DfDataRow dataRow) {
        final ColumnContainer container = new ColumnContainer();
        for (int i = 0; i < dataTable.getColumnSize(); i++) {
            final DfDataColumn dataColumn = dataTable.getColumn(i);
            if (!dataColumn.isWritable()) {
                continue;
            }
            final Object value = dataRow.getValue(i);
            final String columnName = dataColumn.getColumnName();
            container.addColumnValue(columnName, value);
            container.addColumnObject(columnName, dataColumn);
        }
        return container;
    }

    // ===================================================================================
    //                                                                        Helper Class
    //                                                                        ============
    protected static class ColumnContainer {
        protected Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();
        protected Map<String, DfDataColumn> columnObjectMap = new LinkedHashMap<String, DfDataColumn>();

        public Map<String, Object> getColumnValueMap() {
            return columnValueMap;
        }

        public void addColumnValue(String columnName, Object columnValue) {
            this.columnValueMap.put(columnName, columnValue);
        }

        public Map<String, DfDataColumn> getColumnObjectMap() {
            return columnObjectMap;
        }

        public void addColumnObject(String columnName, DfDataColumn columnObject) {
            this.columnObjectMap.put(columnName, columnObject);
        }
    }

    protected static class MyCreatedState {
        public String buildPreparedSql(final DfDataRow row) {
            final DfDtsCreatedState createdState = new DfDtsCreatedState() {
                public String toString() {
                    final DfDtsSqlContext sqlContext = getSqlContext(row);
                    return sqlContext.getSql();
                }
            };
            return createdState.toString();
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSkipSheet(String skipSheet) {
        if (skipSheet == null || skipSheet.trim().length() == 0) {
            return;
        }
        try {
            _skipSheetPattern = Pattern.compile(skipSheet);
        } catch (PatternSyntaxException e) {
            String msg = "The pattern syntax for skip-sheet was wrong: " + skipSheet;
            throw new IllegalStateException(msg, e);
        }
    }
}
