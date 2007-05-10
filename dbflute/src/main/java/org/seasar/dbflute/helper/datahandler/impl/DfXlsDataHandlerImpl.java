package org.seasar.dbflute.helper.datahandler.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.datahandler.DfXlsDataHandler;
import org.seasar.dbflute.helper.excel.DfXlsReader;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.dbflute.util.DfMapStringFileUtil;
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

public class DfXlsDataHandlerImpl implements DfXlsDataHandler {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    public List<DataSet> readSeveralData(String dataDirectoryName) {
        final List<File> xlsList = getXlsList(dataDirectoryName);
        final List<DataSet> ls = new ArrayList<DataSet>();
        for (File file : xlsList) {
            final DfXlsReader xlsReader = createXlsReader(dataDirectoryName, file);
            ls.add(xlsReader.read());
        }
        return ls;
    }

    public void writeSeveralData(String dataDirectoryName, final DataSource dataSource) {
        final List<File> xlsList = getXlsList(dataDirectoryName);

        for (File file : xlsList) {
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

                final List<String> columnNameList = new ArrayList<String>();
                for (int j = 0; j < dataTable.getColumnSize(); j++) {
                    final DataColumn dataColumn = dataTable.getColumn(j);
                    columnNameList.add(dataColumn.getColumnName());
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

                        final List<String> valueList = createValueList(dataTable, dataRow);
                        _log.info(getSql4Log(tableName, columnNameList, valueList));

                        int bindCount = 1;
                        for (String value : valueList) {
                            statement.setObject(bindCount, value);
                            bindCount++;
                        }
                        statement.execute();
                    }
                } catch (SQLException e) {
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
            // Commentted out for Performance Tuning.
            //            final SqlWriter sqlWriter = new SqlWriter(dataSource);
            //            sqlWriter.write(dataSet);
        }
    }

    public void writeSeveralDataForSqlServer(String dataDirectoryName, final DataSource dataSource) {
        final List<File> xlsList = getXlsList(dataDirectoryName);

        for (File file : xlsList) {
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

    protected DfXlsReader createXlsReader(String dataDirectoryName, File file) {
        final DfXlsReader xlsReader = new DfXlsReader(file, getTableNameMap(dataDirectoryName));
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
            final Map columnMap = getDatabaseMetaColumnMap(tableName, dataSource);

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
            final Map columnMap = getDatabaseMetaColumnMap(tableName, dataSource);

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
        return DfMapStringFileUtil.getSimpleMapAsStringValue(path, "UTF-8");
    }

    private DfFlexibleNameMap<String, String> getTableNameMap(String dataDirectoryName) {
        final String path = dataDirectoryName + "/table-name.txt";
        final Map<String, String> targetMap = DfMapStringFileUtil.getSimpleMapAsStringValue(path, "UTF-8");
        return new DfFlexibleNameMap<String, String>(targetMap);
    }

    protected Map getDatabaseMetaColumnMap(String tableName, DataSource dataSource) {
        final Connection connection;
        final DatabaseMetaData dbMetaData;
        try {
            connection = dataSource.getConnection();
            dbMetaData = connection.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        final Map columnMap = DatabaseMetaDataUtil.getColumnMap(dbMetaData, tableName);
        return columnMap;
    }

    protected List<String> createValueList(final DataTable dataTable, final DataRow dataRow) {
        final List<String> valueList = new ArrayList<String>();
        for (int k = 0; k < dataTable.getColumnSize(); k++) {
            final DataColumn dataColumn = dataTable.getColumn(k);
            if (!dataColumn.isWritable()) {
                continue;
            }
            final Object value = dataRow.getValue(k);
            valueList.add(value != null ? value.toString() : null);
        }
        return valueList;
    }

    protected String getSql4Log(String tableName, List<String> columnNameList,
            final List<? extends Object> bindParameters) {
        String columnNameString = columnNameList.toString();
        columnNameString = columnNameString.substring(1, columnNameString.length() - 1);
        String bindParameterString = bindParameters.toString();
        bindParameterString = bindParameterString.substring(1, bindParameterString.length() - 1);
        return "insert into " + tableName + "(" + columnNameString + ") values(" + bindParameterString + ")";
    }

    public static class MyCreatedState {
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
}
