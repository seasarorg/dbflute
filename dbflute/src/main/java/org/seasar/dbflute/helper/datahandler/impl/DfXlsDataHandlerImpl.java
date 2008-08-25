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
package org.seasar.dbflute.helper.datahandler.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.helper.datahandler.DfXlsDataHandler;
import org.seasar.dbflute.helper.datahandler.impl.internal.DfSybaseSqlWriter;
import org.seasar.dbflute.helper.excel.DfXlsReader;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.dbflute.helper.io.fileread.DfMapStringFileReader;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.extension.dataset.ColumnType;
import org.seasar.extension.dataset.DataColumn;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.SqlServerSqlWriter;
import org.seasar.extension.dataset.states.CreatedState;
import org.seasar.extension.dataset.states.SqlContext;
import org.seasar.extension.dataset.types.ColumnTypes;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;

/**
 * @author jflute
 */
public class DfXlsDataHandlerImpl implements DfXlsDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _loggingInsertSql;
    protected String _schemaName;
    protected Pattern _skipSheetPattern;
    protected DfColumnHandler _columnHandler = new DfColumnHandler();// as helper.

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public List<DataSet> readSeveralData(String dataDirectoryName) {
        final List<File> xlsList = getXlsList(dataDirectoryName);
        final List<DataSet> ls = new ArrayList<DataSet>();
        for (File file : xlsList) {
            final DfXlsReader xlsReader = createXlsReader(dataDirectoryName, file);
            ls.add(xlsReader.read());
        }
        return ls;
    }

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====
    public void writeSeveralData(String dataDirectoryName, final DataSource dataSource) {
        final List<File> xlsList = getXlsList(dataDirectoryName);

        for (File file : xlsList) {
            _log.info("");
            _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
            _log.info("writeData(" + file + ")");
            _log.info("= = = = = = =/");
            final DfXlsReader xlsReader = createXlsReader(dataDirectoryName, file);
            final DataSet dataSet = xlsReader.read();

            filterValidColumn(dataSet, dataSource);
            setupDefaultValue(dataDirectoryName, dataSet, dataSource);

            for (int i = 0; i < dataSet.getTableSize(); i++) {
                final DataTable dataTable = dataSet.getTable(i);
                final String tableName = dataTable.getTableName();
                if (isCommentOutSheet(tableName)) {// since 0.7.8 
                    _log.info("*The sheet has comment-out mark so skip it: " + tableName);
                    continue;
                }
                if (isSkipSheet(tableName)) {// since 0.7.8 for [DBFLUTE-251]
                    _log.info("*The sheet name matched skip-sheet specification so skip it: " + tableName);
                    continue;
                }
                if (dataTable.getRowSize() == 0) {
                    _log.info("*Not found row at the table: " + tableName);
                    continue;
                }

                // Set up columnMetaInfo.
                final DfFlexibleNameMap<String, DfColumnMetaInfo> columnMetaInfoMap = getColumnMetaInfo(dataSource,
                        tableName);

                // Set up columnNameList.
                final List<String> columnNameList = new ArrayList<String>();
                for (int j = 0; j < dataTable.getColumnSize(); j++) {
                    final DataColumn dataColumn = dataTable.getColumn(j);
                    final String columnName = dataColumn.getColumnName();
                    columnNameList.add(columnName);
                }

                PreparedStatement statement = null;
                try {
                    for (int j = 0; j < dataTable.getRowSize(); j++) {
                        final DataRow dataRow = dataTable.getRow(j);
                        if (statement == null) {
                            final MyCreatedState myCreatedState = new MyCreatedState();
                            final String preparedSql = myCreatedState.buildPreparedSql(dataRow);
                            statement = dataSource.getConnection().prepareStatement(preparedSql);
                        }

                        // ColumnValue and ColumnObject
                        final ColumnContainer columnContainer = createColumnContainer(dataTable, dataRow);
                        final Map<String, Object> columnValueMap = columnContainer.getColumnValueMap();
                        if (columnValueMap.isEmpty()) {
                            String msg = "The table was Not Found in the file:";
                            msg = msg + " tableName=" + tableName + " file=" + file;
                            throw new TableNotFoundException(msg);
                        }
                        if (_loggingInsertSql) {
                            final List<Object> valueList = new ArrayList<Object>(columnValueMap.values());
                            _log.info(getSql4Log(tableName, columnNameList, valueList));
                        }

                        int bindCount = 1;
                        final Set<String> columnNameSet = columnValueMap.keySet();
                        for (String columnName : columnNameSet) {
                            final Object obj = columnValueMap.get(columnName);
                            if (isNotNullNotString(obj)) {
                                if (obj instanceof Timestamp) {
                                    statement.setTimestamp(bindCount, (Timestamp) obj);
                                    bindCount++;
                                    continue;
                                } else {
                                    statement.setObject(bindCount, obj);
                                    bindCount++;
                                    continue;
                                }
                            }
                            String value = (String) obj;
                            if (value == null) {
                                final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
                                if (columnMetaInfo != null) {
                                    final int jdbcType = columnMetaInfo.getJdbcType();
                                    statement.setNull(bindCount, jdbcType);
                                    bindCount++;
                                    continue;
                                }
                            }

                            // - - - - - - - - - - - - - - - - - - -
                            // Remove double quotation if it exists.
                            // - - - - - - - - - - - - - - - - - - -
                            if (value != null && value.length() > 1 && value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1);
                                value = value.substring(0, value.length() - 1);
                            }

                            // - - - - - - - - - - - - - - 
                            // Against Timestamp Headache
                            // - - - - - - - - - - - - - -
                            if (processTimestamp(columnName, value, statement, bindCount, columnMetaInfoMap)) {
                                bindCount++;
                                continue;
                            }

                            // - - - - - - - - - - - - - - 
                            // Against Number Headache
                            // - - - - - - - - - - - - - -
                            if (processNumber(columnName, value, statement, bindCount, columnMetaInfoMap)) {
                                bindCount++;
                                continue;
                            }

                            statement.setObject(bindCount, value);
                            bindCount++;
                        }
                        statement.addBatch();
                    }
                    if (statement == null) {
                        String msg = "The statement should not be null:";
                        msg = msg + " currentTable=" + dataTable.getTableName();
                        msg = msg + " rowSize=" + dataTable.getRowSize();
                        throw new IllegalStateException(msg);
                    }
                    statement.executeBatch();
                } catch (SQLException e) {
                    final SQLException nextException = e.getNextException();
                    if (nextException != null) {
                        _log.warn("");
                        _log.warn("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");
                        _log.warn("SQLException was thrown! getNextException()=" + nextException.getClass(),
                                nextException);
                        _log.warn("* * * * * * * * * */");
                        _log.warn("");
                    }
                    throw new RuntimeException(e);
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException ignored) {
                            _log.info("statement.close() threw the exception!", ignored);
                        }
                    }
                }
            }
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DfFlexibleNameMap<String, DfColumnMetaInfo> getColumnMetaInfo(DataSource dataSource, String tableName) {
        final DfFlexibleNameMap<String, DfColumnMetaInfo> columnMetaInfoMap = new DfFlexibleNameMap<String, DfColumnMetaInfo>();
        try {
            final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            final List<DfColumnMetaInfo> columnMetaDataList = _columnHandler.getColumns(metaData, _schemaName,
                    tableName, true);
            for (DfColumnMetaInfo columnMetaInfo : columnMetaDataList) {
                columnMetaInfoMap.put(columnMetaInfo.getColumnName(), columnMetaInfo);
            }
            return columnMetaInfoMap;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean isCommentOutSheet(String sheetName) {
        return sheetName.startsWith("#");
    }

    protected boolean isSkipSheet(String sheetName) {
        if (_skipSheetPattern == null) {
            return false;
        }
        return _skipSheetPattern.matcher(sheetName).matches();
    }

    protected boolean isNotNullNotString(Object obj) {
        return obj != null && !(obj instanceof String);
    }

    protected boolean processTimestamp(String columnName, String value, PreparedStatement statement, int bindCount,
            DfFlexibleNameMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            try {
                final String torqueType = _columnHandler.getColumnTorqueType(columnMetaInfo);
                final Class<?> columnType = TypeMap.findJavaNativeClass(torqueType);
                if (columnType != null && !java.util.Date.class.isAssignableFrom(columnType)) {
                    return false;
                }
            } catch (UnsupportedOperationException e) {
                _log.info("*The column type of '" + columnName + "' is unsupported: " + e.getMessage());
            }
        }
        if (!isTimestampValue(value)) {
            return false;
        }
        final Timestamp timestampValue = getTimestampValue(value);
        statement.setTimestamp(bindCount, timestampValue);
        return true;
    }

    protected boolean processNumber(String columnName, String value, PreparedStatement statement, int bindCount,
            DfFlexibleNameMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            try {
                final String torqueType = _columnHandler.getColumnTorqueType(columnMetaInfo);
                final Class<?> columnType = TypeMap.findJavaNativeClass(torqueType);
                if (columnType != null && !Number.class.isAssignableFrom(columnType)) {
                    return false;
                }
            } catch (UnsupportedOperationException e) {
                _log.info("*The column type of '" + columnName + "' is unsupported: " + e.getMessage());
            }
        }
        if (!isBigDecimalValue(value)) {
            return false;
        }
        final BigDecimal bigDecimalValue = getBigDecimalValue(value);
        try {
            final long longValue = bigDecimalValue.longValueExact();
            statement.setLong(bindCount, longValue);
            return true;
        } catch (ArithmeticException e) {
            statement.setBigDecimal(bindCount, bigDecimalValue);
            return true;
        }
    }

    protected boolean isTimestampValue(String value) {
        if (value == null) {
            return false;
        }
        value = filterTimestampValue(value);
        try {
            Timestamp.valueOf(value);
            return true;
        } catch (RuntimeException e) {
        }
        return false;
    }

    protected Timestamp getTimestampValue(String value) {
        final String filteredTimestampValue = filterTimestampValue(value);
        try {
            return Timestamp.valueOf(filteredTimestampValue);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    protected String filterTimestampValue(String value) {
        value = value.trim();
        if (value.indexOf("/") == 4 && value.lastIndexOf("/") == 7) {
            value = value.replaceAll("/", "-");
        }
        if (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7) {
            if (value.length() == "2007-07-09".length()) {
                value = value + " 00:00:00";
            }
        }
        return value;
    }

    protected boolean isBigDecimalValue(String value) {
        if (value == null) {
            return false;
        }
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    protected BigDecimal getBigDecimalValue(String value) {
        try {
            return new BigDecimal(value);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    // ===================================================================================
    //                                                                     Special Support
    //                                                                     ===============
    public void writeSeveralDataForSqlServer(String dataDirectoryName, final DataSource dataSource) {
        final List<File> xlsList = getXlsList(dataDirectoryName);

        for (File file : xlsList) {
            _log.info("");
            _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
            _log.info("writeData(" + file + ")");
            _log.info("= = = = = = =/");
            final DfXlsReader xlsReader = createXlsReader(dataDirectoryName, file);
            final DataSet dataSet = xlsReader.read();

            filterValidColumn(dataSet, dataSource);
            setupDefaultValue(dataDirectoryName, dataSet, dataSource);

            final SqlServerSqlWriter sqlServerSqlWriter = new SqlServerSqlWriter(dataSource);
            sqlServerSqlWriter.write(dataSet);
        }
    }

    public void writeSeveralDataForSybase(String dataDirectoryName, final DataSource dataSource) {
        final List<File> xlsList = getXlsList(dataDirectoryName);

        for (File file : xlsList) {
            _log.info("");
            _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
            _log.info("writeData(" + file + ")");
            _log.info("= = = = = = =/");
            final DfXlsReader xlsReader = createXlsReader(dataDirectoryName, file);
            final DataSet dataSet = xlsReader.read();

            filterValidColumn(dataSet, dataSource);
            setupDefaultValue(dataDirectoryName, dataSet, dataSource);

            final DfSybaseSqlWriter sybaseSqlWriter = new DfSybaseSqlWriter(dataSource);
            sybaseSqlWriter.write(dataSet);
        }
    }

    protected DfXlsReader createXlsReader(String dataDirectoryName, File file) {
        final DfFlexibleNameMap<String, String> tableNameMap = getTableNameMap(dataDirectoryName);
        final DfFlexibleNameMap<String, List<String>> notTrimTableColumnMap = getNotTrimTableColumnMap(dataDirectoryName);
        final DfXlsReader xlsReader = new DfXlsReader(file, tableNameMap, notTrimTableColumnMap);
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

    protected void filterValidColumn(final DataSet dataSet, final DataSource dataSource) {
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DataTable table = dataSet.getTable(i);
            final String tableName = table.getTableName();
            final Map<?, ?> columnMap = getDatabaseMetaColumnMap(tableName, dataSource);

            for (int j = 0; j < table.getColumnSize(); j++) {
                final DataColumn dataColumn = table.getColumn(j);
                if (!columnMap.containsKey(dataColumn.getColumnName())) {
                    dataColumn.setWritable(false);
                }
            }
        }
    }

    protected void setupDefaultValue(String dataDirectoryName, final DataSet dataSet, final DataSource dataSource) {
        final Map<String, String> defaultValueMap = getDefaultValueMap(dataDirectoryName);
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DataTable table = dataSet.getTable(i);
            final Set<String> defaultValueMapKeySet = defaultValueMap.keySet();
            final String tableName = table.getTableName();
            final Map<?, ?> columnMap = getDatabaseMetaColumnMap(tableName, dataSource);

            for (String defaultTargetColumnName : defaultValueMapKeySet) {
                final String defaultValue = defaultValueMap.get(defaultTargetColumnName);

                if (columnMap.containsKey(defaultTargetColumnName) && !table.hasColumn(defaultTargetColumnName)) {
                    final ColumnType columnType;
                    final Object value;
                    if (defaultValue.equalsIgnoreCase("sysdate")) {
                        columnType = ColumnTypes.TIMESTAMP;
                        value = new Timestamp(System.currentTimeMillis());
                    } else {
                        columnType = ColumnTypes.STRING;
                        value = defaultValue;
                    }
                    table.addColumn(defaultTargetColumnName, columnType);

                    for (int j = 0; j < table.getRowSize(); j++) {
                        final DataRow row = table.getRow(j);
                        row.setValue(defaultTargetColumnName, value);
                    }
                }
            }
        }
    }

    private Map<String, String> getDefaultValueMap(String dataDirectoryName) {
        final String path = dataDirectoryName + "/default-value.txt";
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        return reader.readMapAsStringValue(path, "UTF-8");
    }

    private DfFlexibleNameMap<String, String> getTableNameMap(String dataDirectoryName) {
        final String path = dataDirectoryName + "/table-name.txt";
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        final Map<String, String> targetMap = reader.readMapAsStringValue(path, "UTF-8");
        return new DfFlexibleNameMap<String, String>(targetMap);
    }

    private DfFlexibleNameMap<String, List<String>> getNotTrimTableColumnMap(String dataDirectoryName) {
        final String path = dataDirectoryName + "/not-trim-column.txt";
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        final Map<String, List<String>> targetMap = reader.readMapAsListStringValue(path, "UTF-8");
        return new DfFlexibleNameMap<String, List<String>>(targetMap);
    }

    protected Map<?, ?> getDatabaseMetaColumnMap(String tableName, DataSource dataSource) {
        final Connection connection;
        final DatabaseMetaData dbMetaData;
        try {
            connection = dataSource.getConnection();
            dbMetaData = connection.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        final Map<?, ?> columnMap = DatabaseMetaDataUtil.getColumnMap(dbMetaData, tableName);
        return columnMap;
    }

    // Old style method to createColumnContainer()
    //    protected List<String> createValueList(final DataTable dataTable, final DataRow dataRow) {
    //        final List<String> valueList = new ArrayList<String>();
    //        for (int k = 0; k < dataTable.getColumnSize(); k++) {
    //            final DataColumn dataColumn = dataTable.getColumn(k);
    //            if (!dataColumn.isWritable()) {
    //                continue;
    //            }
    //            final Object value = dataRow.getValue(k);
    //            valueList.add(value != null ? value.toString() : null);
    //        }
    //        return valueList;
    //    }

    protected String getSql4Log(String tableName, List<String> columnNameList,
            final List<? extends Object> bindParameters) {
        String columnNameString = columnNameList.toString();
        columnNameString = columnNameString.substring(1, columnNameString.length() - 1);
        String bindParameterString = bindParameters.toString();
        bindParameterString = bindParameterString.substring(1, bindParameterString.length() - 1);
        return tableName + ":{" + bindParameterString + "}";
    }

    protected ColumnContainer createColumnContainer(final DataTable dataTable, final DataRow dataRow) {
        final ColumnContainer container = new ColumnContainer();
        for (int k = 0; k < dataTable.getColumnSize(); k++) {
            final DataColumn dataColumn = dataTable.getColumn(k);
            if (!dataColumn.isWritable()) {
                continue;
            }
            final Object value = dataRow.getValue(k);
            final String columnName = dataColumn.getColumnName();
            container.addColumnValue(columnName, value);
            container.addColumnObject(columnName, dataColumn);
        }
        return container;
    }

    protected static class ColumnContainer {
        protected Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();
        protected Map<String, DataColumn> columnObjectMap = new LinkedHashMap<String, DataColumn>();

        public Map<String, Object> getColumnValueMap() {
            return columnValueMap;
        }

        public void addColumnValue(String columnName, Object columnValue) {
            this.columnValueMap.put(columnName, columnValue);
        }

        public Map<String, DataColumn> getColumnObjectMap() {
            return columnObjectMap;
        }

        public void addColumnObject(String columnName, DataColumn columnObject) {
            this.columnObjectMap.put(columnName, columnObject);
        }
    }

    protected static class MyCreatedState {
        public String buildPreparedSql(final DataRow row) {
            final CreatedState createdState = new CreatedState() {
                public String toString() {
                    final SqlContext sqlContext = getSqlContext(row);
                    return sqlContext.getSql();
                }
            };
            return createdState.toString();
        }
    }

    protected static class TableNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public TableNotFoundException(String msg) {
            super(msg);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isLoggingInsertSql() {
        return _loggingInsertSql;
    }

    public void setLoggingInsertSql(boolean loggingInsertSql) {
        this._loggingInsertSql = loggingInsertSql;
    }

    public String getSchemaName() {
        return _schemaName;
    }

    public void setSchemaName(String schemaName) {
        _schemaName = schemaName;
    }

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
