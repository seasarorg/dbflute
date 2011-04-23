package org.seasar.dbflute.logic.doc.dataxls;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataSet;
import org.seasar.dbflute.helper.dataset.DfDataTable;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnTypes;
import org.seasar.dbflute.helper.io.xls.DfXlsWriter;
import org.seasar.dbflute.logic.replaceschema.migratereps.DfLoadDataMigration;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataXlsTemplateHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLoadDataMigration.class);

    public static final int XLS_LIMIT = 65000; // about

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected boolean _overLimitTruncated;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataXlsTemplateHandler(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                          Output Xls
    //                                                                          ==========
    /**
     * Output data excel templates. (using dataSource)
     * @param tableInfoMap The map of table info. (NotNull)
     * @param limit The limit of extracted record. (MinusAllowed: if minus, no limit)
     * @param xlsFile The file of xls. (NotNull)
     * @return The result of dump. (NotNull)
     */
    public DfDataXlsTemplateResult outputData(Map<String, DfTemplateDataTableInfo> tableInfoMap, int limit, File xlsFile) {
        final DfTemplateDataExtractor extractor = new DfTemplateDataExtractor(_dataSource);
        final Map<String, List<Map<String, String>>> templateDataMap = extractor.extractData(tableInfoMap, limit);
        return transferToXls(tableInfoMap, templateDataMap, xlsFile);
    }

    /**
     * Transfer data to excel. (state-less)
     * @param templateDataMap The map of template data. (NotNull)
     * @param xlsFile The file of xls. (NotNull)
     * @return The result of dump. (NotNull)
     */
    protected DfDataXlsTemplateResult transferToXls(Map<String, DfTemplateDataTableInfo> tableInfoMap,
            Map<String, List<Map<String, String>>> templateDataMap, File xlsFile) {

        final Map<String, List<Column>> overTableColumnMap = new LinkedHashMap<String, List<Column>>();
        final Map<String, List<Map<String, String>>> overTemplateDataMap = new LinkedHashMap<String, List<Map<String, String>>>();

        final Set<String> tableDbNameSet = templateDataMap.keySet();
        final DfDataSet dataSet = new DfDataSet();
        final int xlsLimit = XLS_LIMIT;
        _log.info("...Transferring " + templateDataMap.size() + " tables to xls files");
        for (String tableDbName : tableDbNameSet) {
            final DfTemplateDataTableInfo tableInfo = tableInfoMap.get(tableDbName);
            final List<Column> columnList = tableInfo.getColumnList();
            final int dotIndex = tableDbName.indexOf(".");
            final DfDataTable dataTable;
            if (dotIndex >= 0) {
                // for the table of additional schema
                dataTable = new DfDataTable(tableDbName.substring(dotIndex + ".".length()));
            } else {
                dataTable = new DfDataTable(tableDbName);
            }
            int columnIndex = 0;
            for (Column column : columnList) {
                dataTable.addColumn(column.getName(), DfDtsColumnTypes.STRING);
                ++columnIndex;
            }
            final List<Map<String, String>> recordList;
            {
                List<Map<String, String>> extractedList = templateDataMap.get(tableDbName);
                _log.info("  " + tableDbName + " (" + extractedList.size() + ")");
                if (extractedList.size() > xlsLimit) {
                    if (_overLimitTruncated) {
                        recordList = extractedList.subList(0, xlsLimit);
                    } else {
                        overTableColumnMap.put(tableDbName, columnList);
                        overTemplateDataMap.put(tableDbName, extractedList);
                        continue;
                    }
                } else {
                    recordList = extractedList;
                }
            }
            for (Map<String, String> recordMap : recordList) {
                final Set<String> columnNameSet = recordMap.keySet();
                final DfDataRow dataRow = dataTable.addRow();
                for (String columnName : columnNameSet) {
                    final String value = recordMap.get(columnName);
                    dataRow.addValue(columnName, value);
                }
            }
            dataSet.addTable(dataTable);
        }

        final DfXlsWriter writer = createXlsWriter(xlsFile);
        try {
            writer.write(dataSet);
        } catch (RuntimeException e) {
            String msg = "Failed to write the xls file: " + xlsFile;
            msg = msg + " tableNames=" + tableDbNameSet;
            throw new IllegalStateException(msg, e);
        }

        final DfDataXlsTemplateResult templateDataResult = new DfDataXlsTemplateResult();
        templateDataResult.setOverTableColumnMap(overTableColumnMap);
        templateDataResult.setOverTemplateDataMap(overTemplateDataMap);
        return templateDataResult;
    }

    protected DfXlsWriter createXlsWriter(File xlsFile) {
        // The xls file should have all string cell type for replace-schema. 
        return new DfXlsWriter(xlsFile).stringCellType();
    }

    public void setupOverLimitTruncated() { // option
        _overLimitTruncated = true;
    }
}
