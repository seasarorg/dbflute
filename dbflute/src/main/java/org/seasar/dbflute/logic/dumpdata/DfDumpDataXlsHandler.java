package org.seasar.dbflute.logic.dumpdata;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataSet;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.helper.dataset.types.ColumnTypes;
import org.seasar.dbflute.helper.io.xls.DfXlsWriter;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDumpDataXlsHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDumpDataXlsHandler(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                         Dump to Xls
    //                                                                         ===========
    /**
     * Dump data to xls. {Using dataSource}
     * @param tableColumnMap The map of table and column. (NotNull)
     * @param limit The limit of extracted record.
     * @param xlsFile The file of xls. (NotNull)
     * @return The result of dump. (NotNull)
     */
    public DumpResult dumpToXls(Map<String, List<String>> tableColumnMap, int limit, File xlsFile) {
        final DfDumpDataExtractor extractor = new DfDumpDataExtractor(_dataSource);
        final Map<String, List<Map<String, String>>> dumpDataMap = extractor.extractData(tableColumnMap, limit);
        return transferToXls(tableColumnMap, dumpDataMap, xlsFile);
    }

    /**
     * Transfer data to xls. {Stateless}
     * @param dumpDataMap The map of dump data. (NotNull)
     * @param xlsFile The file of xls. (NotNull)
     * @return The result of dump. (NotNull)
     */
    protected DumpResult transferToXls(Map<String, List<String>> tableColumnMap,
            Map<String, List<Map<String, String>>> dumpDataMap, File xlsFile) {

        final Map<String, List<String>> overTableColumnMap = new LinkedHashMap<String, List<String>>();
        final Map<String, List<Map<String, String>>> overDumpDataMap = new LinkedHashMap<String, List<Map<String, String>>>();

        final Set<String> tableNameSet = dumpDataMap.keySet();
        final DfXlsWriter writer = new DfXlsWriter(xlsFile);
        // If the Apache POI version is 2.5, this is necessary to handle Japanese. 
        // writer.setCellEncoding(CellEncoding.ENCODING_UTF_16); // for Japanese
        final DataSet dataSet = new DataSet();
        for (String tableName : tableNameSet) {
            final List<String> columnNameList = tableColumnMap.get(tableName);
            final DataTable dataTable = new DataTable(tableName);
            int columnIndex = 0;
            for (String columnName : columnNameList) {
                dataTable.addColumn(columnName, ColumnTypes.STRING);
                ++columnIndex;
            }
            final List<Map<String, String>> recordList = dumpDataMap.get(tableName);
            if (recordList.size() > 65000) { // against Excel limit!
                overTableColumnMap.put(tableName, columnNameList);
                overDumpDataMap.put(tableName, recordList);
                continue;
            }
            for (Map<String, String> recordMap : recordList) {
                final Set<String> columnNameSet = recordMap.keySet();
                final DataRow dataRow = dataTable.addRow();
                for (String columnName : columnNameSet) {
                    final String value = recordMap.get(columnName);
                    dataRow.addValue(columnName, value);
                }
            }
            dataSet.addTable(dataTable);
        }
        writer.write(dataSet);

        DumpResult dumpResult = new DumpResult();
        dumpResult.setOverTableColumnMap(overTableColumnMap);
        dumpResult.setOverDumpDataMap(overDumpDataMap);
        return dumpResult;
    }

    public static class DumpResult {
        protected Map<String, List<String>> overTableColumnMap;
        protected Map<String, List<Map<String, String>>> overDumpDataMap;

        public Map<String, List<String>> getOverTableColumnMap() {
            return overTableColumnMap;
        }

        public void setOverTableColumnMap(Map<String, List<String>> overTableColumnMap) {
            this.overTableColumnMap = overTableColumnMap;
        }

        public Map<String, List<Map<String, String>>> getOverDumpDataMap() {
            return overDumpDataMap;
        }

        public void setOverDumpDataMap(Map<String, List<Map<String, String>>> overDumpDataMap) {
            this.overDumpDataMap = overDumpDataMap;
        }
    }
}
