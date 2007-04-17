package org.seasar.dbflute.helper.datahandler.impl;

import java.io.File;
import java.io.FilenameFilter;
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
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.SqlWriter;
import org.seasar.extension.dataset.types.ColumnTypes;

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

            setupDefaultValue(dataDirectoryName, dataSet);

            // TODO: performance turning
            //            final SqlWriter sqlWriter = new SqlWriter(dataSource) {
            //                public void write(DataSet dataSet) {
            //                    final TableWriter writer = new SqlTableWriter(getDataSource()) {
            //                        public void write(DataTable table) {
            //                            final List<String> columnNameList = new ArrayList<String>();
            //                            for (int j = 0; j < table.getColumnSize(); j++) {
            //                                columnNameList.add(table.getColumnName(j));
            //                            }
            //                            final StringBuilder sb = new StringBuilder();
            //                            sb.append("insert into ").append(table.getTableName());
            //                            sb.append("(");
            //                            for (String columnName : columnNameList) {
            //                                sb.append(columnName).append(", ");
            //                            }
            //                            sb.delete(0, sb.length() - ", ".length());
            //                            sb.append(") values(");
            //                            for (String columnName : columnNameList) {
            //                                sb.append("?, ");
            //                            }
            //                            sb.delete(0, sb.length() - ", ".length());
            //                            sb.append(")");
            //
            //                            try {
            //                                final Connection conn = dataSource.getConnection();
            //                                final PreparedStatement ps = conn.prepareStatement(sb.toString());
            //
            //                                for (int i = 0; i < table.getRowSize(); i++) {
            //                                    final DataRow row = table.getRow(i);
            //                                    int columnCount = 1;
            //                                    for (String columnName : columnNameList) {
            //                                        final Object value = row.getValue(columnName);
            //                                        ps.setObject(columnCount, value);
            //                                        columnCount++;
            //                                    }
            //                                    _log.info(sb.toString() + " ps=" + ps);
            //                                    ps.executeUpdate();
            //                                }
            //                            } catch (SQLException e) {
            //                                throw new IllegalStateException("sql=" + sb.toString(), e);
            //                            }
            //                        }
            //                    };
            //                    for (int i = 0; i < dataSet.getTableSize(); i++) {
            //                        writer.write(dataSet.getTable(i));
            //                    }
            //                }
            //            };
            final SqlWriter sqlWriter = new SqlWriter(dataSource);
            sqlWriter.write(dataSet);
        }
    }

    protected DfXlsReader createXlsReader(String dataDirectoryName, File file) {
        final DfXlsReader xlsReader = new DfXlsReader(file, getTableNameMap(dataDirectoryName));
        return xlsReader;
    }

    protected void setupDefaultValue(String dataDirectoryName, final DataSet dataSet) {
        final Map<String, String> defaultValueMap = getDefaultValueMap(dataDirectoryName);
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DataTable table = dataSet.getTable(i);
            final Set<String> defaultValueMapKeySet = defaultValueMap.keySet();
            for (String defaultTargetColumnName : defaultValueMapKeySet) {
                final String defaultValue = defaultValueMap.get(defaultTargetColumnName);

                if (!table.hasColumn(defaultTargetColumnName)) {
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

    private Map<String, String> getDefaultValueMap(String dataDirectoryName) {
        final String path = dataDirectoryName + "/default-value.txt";
        return DfMapStringFileUtil.getSimpleMapAsStringValue(path, "UTF-8");
    }

    private DfFlexibleNameMap<String, String> getTableNameMap(String dataDirectoryName) {
        final String path = dataDirectoryName + "/table-name.txt";
        final Map<String, String> targetMap = DfMapStringFileUtil.getSimpleMapAsStringValue(path, "UTF-8");
        return new DfFlexibleNameMap<String, String>(targetMap);
    }
}
