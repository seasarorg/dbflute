package org.seasar.dbflute.logic.dumpdata;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * @param dumpDataMap The map of dump data. (NotNull)
     * @param xlsFile The file of xls. (NotNull)
     */
    public void dumpToExcel(Map<String, List<Map<String, String>>> dumpDataMap, File xlsFile) {
        final Set<String> tableNameSet = dumpDataMap.keySet();
        final DfXlsWriter writer = new DfXlsWriter(xlsFile);
        final DataSet dataSet = new DataSet();
        for (String tableName : tableNameSet) {
            final List<Map<String, String>> recordList = dumpDataMap.get(tableName);
            final DataTable dataTable = new DataTable(tableName);
            int recordIndex = 0;
            for (Map<String, String> recordMap : recordList) {
                final Set<String> columnNameSet = recordMap.keySet();
                if (recordIndex == 0) { // at the first loop
                    int columnIndex = 0;
                    for (String columnName : columnNameSet) {
                        dataTable.addColumn(columnName, ColumnTypes.STRING);
                        ++columnIndex;
                    }
                }
                final DataRow dataRow = dataTable.addRow();
                for (String columnName : columnNameSet) {
                    final String value = recordMap.get(columnName);
                    dataRow.addValue(columnName, value);
                }
                ++recordIndex;
            }
            dataSet.addTable(dataTable);
        }
        writer.write(dataSet);
    }
}
