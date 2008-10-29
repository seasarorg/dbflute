package org.seasar.dbflute.logic.dumpdata;

import java.io.File;
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
     */
    public void dumpToXls(Map<String, List<String>> tableColumnMap, int limit, File xlsFile) {
        final DfDumpDataExtractor extractor = new DfDumpDataExtractor(_dataSource);
        final Map<String, List<Map<String, String>>> dumpDataMap = extractor.extractData(tableColumnMap, limit);
        transferToXls(tableColumnMap, dumpDataMap, xlsFile);
    }

    /**
     * Transfer data to xls. {Stateless}
     * @param dumpDataMap The map of dump data. (NotNull)
     * @param xlsFile The file of xls. (NotNull)
     */
    protected void transferToXls(Map<String, List<String>> tableColumnMap,
            Map<String, List<Map<String, String>>> dumpDataMap, File xlsFile) {
        final Set<String> tableNameSet = dumpDataMap.keySet();
        final DfXlsWriter writer = new DfXlsWriter(xlsFile);
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
    }
}
