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
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfLReverseGenerator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLReverseGenerator.class);

    protected static final int XLS_LIMIT = 65000; // about

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected boolean _containsCommonColumn;
    protected boolean _managedTableOnly;

    // option for large data
    protected String _delimiterDataDir;

    protected final Map<String, Table> _tableNameMap = new LinkedHashMap<String, Table>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfLReverseGenerator(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                          Output Xls
    //                                                                          ==========
    /**
     * Output data excel templates. (using dataSource)
     * @param tableInfoMap The map of table. (NotNull)
     * @param limit The limit of extracted record. (MinusAllowed: if minus, no limit)
     * @param xlsFile The file of xls. (NotNull)
     */
    public void outputData(Map<String, Table> tableInfoMap, int limit, File xlsFile) {
        filterUnsupportedTable(tableInfoMap);
        final DfLoadDataExtractor extractor = new DfLoadDataExtractor(_dataSource);
        extractor.setExtractingLimit(limit);
        extractor.setLargeBorder(XLS_LIMIT);
        final Map<String, DfLoadDataResult> templateDataMap = extractor.extractData(tableInfoMap);
        transferToXls(tableInfoMap, templateDataMap, limit, xlsFile);
    }

    protected void filterUnsupportedTable(Map<String, Table> tableInfoMap) {
        // additional tables are unsupported here
        // because it's not an important function
        final Map<String, Object> additionalTableMap = getAdditionalTableProperties().getAdditionalTableMap();
        final Set<String> keySet = additionalTableMap.keySet();
        for (String key : keySet) {
            if (tableInfoMap.containsKey(key)) {
                tableInfoMap.remove(key);
            }
        }
    }

    /**
     * Transfer data to excel. (state-less)
     * @param tableMap The map of table. (NotNull)
     * @param templateDataMap The map of template data. (NotNull)
     * @param limit The limit of extracted record. (MinusAllowed: if minus, no limit)
     * @param xlsFile The file of xls. (NotNull)
     */
    protected void transferToXls(Map<String, Table> tableMap, Map<String, DfLoadDataResult> templateDataMap,
            int limit, File xlsFile) {
        final DfDataSet dataSet = new DfDataSet();
        int index = 0;
        for (Entry<String, Table> entry : tableMap.entrySet()) {
            ++index;
            final String tableDbName = entry.getKey();
            final Table table = entry.getValue();
            if (_managedTableOnly && (table.isAdditionalSchema() || table.isTypeView())) {
                continue;
            }
            final DfLoadDataResult templateDataResult = templateDataMap.get(tableDbName);
            if (templateDataResult.isLargeData()) {
                outputDelimiterData(table, templateDataResult, limit);
            } else {
                final List<Map<String, String>> extractedList = templateDataResult.getResultList();
                setupXlsDataTable(dataSet, table, extractedList, index);
            }
        }
        if (dataSet.getTableSize() > 0) {
            writeXlsData(dataSet, xlsFile);
        }
    }

    // ===================================================================================
    //                                                                            Xls Data
    //                                                                            ========
    protected void setupXlsDataTable(DfDataSet dataSet, Table table, List<Map<String, String>> extractedList, int index) {
        final String tableDbName = table.getName();
        final int xlsLimit = XLS_LIMIT;
        final List<Map<String, String>> recordList;
        {
            _log.info("  " + tableDbName + " (" + extractedList.size() + ")");
            if (extractedList.size() > xlsLimit) {
                recordList = extractedList.subList(0, xlsLimit); // just in case
            } else {
                recordList = extractedList;
            }
        }
        final DfDataTable dataTable = new DfDataTable(resolveSheetName(table, index));
        final List<Column> columnList = table.getColumnList();
        int columnIndex = 0;
        for (Column column : columnList) {
            if (isExceptCommonColumn(column)) {
                continue;
            }
            dataTable.addColumn(column.getName(), DfDtsColumnTypes.STRING);
            ++columnIndex;
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
        final String tableDbName = table.getName();
        String sheetName = Srl.substringLastRear(tableDbName, "."); // just in case
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
    protected void outputDelimiterData(Table table, DfLoadDataResult templateDataResult, final int limit) {
        if (_delimiterDataDir == null) {
            return;
        }
        final File delimiterDir = new File(_delimiterDataDir);
        final String ext = "tsv"; // fixed
        final FileMakingOption option = new FileMakingOption().encodeAsUTF8().separateLf();
        option.delimitateByTab();
        final String tableDbName = table.getName();
        _log.info("...Outputting the over-xls-limit table: " + tableDbName);
        if (!delimiterDir.exists()) {
            delimiterDir.mkdirs();
        }
        final FileToken fileToken = new FileToken();
        final String delimiterFilePath = delimiterDir.getPath() + "/" + tableDbName + "." + ext;
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
                    msg = msg + " table=" + tableDbName + " file=" + delimiterFilePath;
                    throw new IllegalStateException(msg, e);
                }
                _log.info(" -> " + delimiterFilePath + " (" + count + ")");
            }
        });
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
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

    public void setManagedTableOnly(boolean managedTableOnly) {
        _managedTableOnly = managedTableOnly;
    }

    public void setDelimiterDataDir(String delimiterDataDir) {
        _delimiterDataDir = delimiterDataDir;
    }

    public Map<String, Table> getTableNameMap() {
        return _tableNameMap;
    }
}
