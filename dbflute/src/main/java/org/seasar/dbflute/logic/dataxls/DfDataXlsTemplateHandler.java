package org.seasar.dbflute.logic.dataxls;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataSet;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.helper.dataset.types.ColumnTypes;
import org.seasar.dbflute.helper.io.xls.DfXlsWriter;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataXlsTemplateHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataXlsTemplateHandler(DataSource dataSource) {
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
    public TemplateDataResult dumpToXls(Map<String, List<Column>> tableColumnMap, int limit, File xlsFile) {
        final DfTemplateDataExtractor extractor = new DfTemplateDataExtractor(_dataSource);
        final Map<String, List<Map<String, String>>> dumpDataMap = extractor.extractData(tableColumnMap, limit);
        return transferToXls(tableColumnMap, dumpDataMap, xlsFile);
    }

    /**
     * Transfer data to xls. {Stateless}
     * @param templateDataMap The map of template data. (NotNull)
     * @param xlsFile The file of xls. (NotNull)
     * @return The result of dump. (NotNull)
     */
    protected TemplateDataResult transferToXls(Map<String, List<Column>> tableColumnMap,
            Map<String, List<Map<String, String>>> templateDataMap, File xlsFile) {

        final Map<String, List<Column>> overTableColumnMap = new LinkedHashMap<String, List<Column>>();
        final Map<String, List<Map<String, String>>> overTemplateDataMap = new LinkedHashMap<String, List<Map<String, String>>>();

        final Set<String> tableNameSet = templateDataMap.keySet();
        // If the Apache POI version is 2.5, this is necessary to handle Japanese. 
        //writer.setCellEncoding(CellEncoding.ENCODING_UTF_16); // for Japanese
        final DataSet dataSet = new DataSet();
        for (String tableName : tableNameSet) {
            final List<Column> columnList = tableColumnMap.get(tableName);
            final int dotIndex = tableName.indexOf(".");
            final DataTable dataTable;
            if (dotIndex >= 0) {
                // for the table of additional schema
                dataTable = new DataTable(tableName.substring(dotIndex + ".".length()));
            } else {
                dataTable = new DataTable(tableName);
            }
            int columnIndex = 0;
            for (Column column : columnList) {
                dataTable.addColumn(column.getName(), ColumnTypes.STRING);
                ++columnIndex;
            }
            final List<Map<String, String>> recordList = templateDataMap.get(tableName);
            if (recordList.size() > 65000) { // against Excel limit!
                overTableColumnMap.put(tableName, columnList);
                overTemplateDataMap.put(tableName, recordList);
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

        // The xls file should have all string cell type for replace-schema. 
        final DfXlsWriter writer = new DfXlsWriter(xlsFile).stringCellType();
        try {
            writer.write(dataSet);
        } catch (RuntimeException e) {
            String msg = "Failed to write the xls file: " + xlsFile;
            msg = msg + " tableNames=" + tableNameSet;
            throw new IllegalStateException(msg, e);
        }

        final TemplateDataResult templateDataResult = new TemplateDataResult();
        templateDataResult.setOverTableColumnMap(overTableColumnMap);
        templateDataResult.setOverTemplateDataMap(overTemplateDataMap);
        return templateDataResult;
    }

    public static class TemplateDataResult {
        protected Map<String, List<Column>> overTableColumnMap;
        protected Map<String, List<Map<String, String>>> overTemplateDataMap;

        public Map<String, List<Column>> getOverTableColumnMap() {
            return overTableColumnMap;
        }

        public void setOverTableColumnMap(Map<String, List<Column>> overTableColumnMap) {
            this.overTableColumnMap = overTableColumnMap;
        }

        public Map<String, List<Map<String, String>>> getOverTemplateDataMap() {
            return overTemplateDataMap;
        }

        public void setOverTemplateDataMap(Map<String, List<Map<String, String>>> overTemplateDataMap) {
            this.overTemplateDataMap = overTemplateDataMap;
        }
    }
}
