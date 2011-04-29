package org.seasar.dbflute.logic.doc.dataxls;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.seasar.dbflute.helper.jdbc.facade.DfJFacCursorCallback;
import org.seasar.dbflute.helper.jdbc.facade.DfJFacCursorHandler;
import org.seasar.dbflute.helper.jdbc.facade.DfJFacResultSetWrapper;
import org.seasar.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.dbflute.helper.token.file.FileMakingOption;
import org.seasar.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.dbflute.helper.token.file.FileToken;
import org.seasar.dbflute.helper.token.file.impl.FileTokenImpl;
import org.seasar.dbflute.logic.replaceschema.migratereps.DfLoadDataMigration;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataXlsHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLoadDataMigration.class);

    public static final int XLS_LIMIT = 65000; // about

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected boolean _containsCommonColumn;

    // option for large data
    protected File _delimiterDataOutputDir;
    protected boolean _delimiterDataTypeCsv; // default is TSV

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataXlsHandler(DataSource dataSource) {
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
        final DfTemplateDataExtractor extractor = new DfTemplateDataExtractor(_dataSource);
        extractor.setExtractingLimit(limit);
        extractor.setLargeBorder(XLS_LIMIT);
        final Map<String, DfTemplateDataResult> templateDataMap = extractor.extractData(tableInfoMap);
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
    protected void transferToXls(Map<String, Table> tableMap, Map<String, DfTemplateDataResult> templateDataMap,
            int limit, File xlsFile) {
        final Set<String> tableDbNameSet = templateDataMap.keySet();
        final DfDataSet dataSet = new DfDataSet();
        _log.info("...Transferring " + templateDataMap.size() + " tables to xls files");
        for (String tableDbName : tableDbNameSet) {
            final Table table = tableMap.get(tableDbName);
            final DfTemplateDataResult templateDataResult = templateDataMap.get(tableDbName);
            if (templateDataResult.isLargeData()) {
                outputDataDelimiterTemplate(table, templateDataResult, limit);
            } else {
                final List<Map<String, String>> extractedList = templateDataResult.getResultList();
                setupXlsDataTable(dataSet, table, extractedList);
            }
        }
        if (dataSet.getTableSize() > 0) {
            writeXlsData(dataSet, xlsFile);
        }
    }

    // ===================================================================================
    //                                                                            Xls Data
    //                                                                            ========
    protected void setupXlsDataTable(DfDataSet dataSet, Table table, List<Map<String, String>> extractedList) {
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
        final int dotIndex = tableDbName.indexOf(".");
        final DfDataTable dataTable;
        if (dotIndex >= 0) {
            // for the table of additional schema
            dataTable = new DfDataTable(tableDbName.substring(dotIndex + ".".length()));
        } else {
            dataTable = new DfDataTable(tableDbName);
        }
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

    protected boolean isExceptCommonColumn(Column column) {
        return !_containsCommonColumn && column.isCommonColumn();
    }

    protected void writeXlsData(DfDataSet dataSet, File xlsFile) {
        final DfXlsWriter writer = createXlsWriter(xlsFile);
        try {
            _log.info("...Writing xls data: " + xlsFile.getName());
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
    protected void outputDataDelimiterTemplate(Table table, DfTemplateDataResult templateDataResult, final int limit) {
        final File delimiterDir = _delimiterDataOutputDir;
        if (delimiterDir == null) { // no output delimiter data
            return;
        }
        final String ext;
        final FileMakingOption option = new FileMakingOption().encodeAsUTF8().separateLf();
        if (_delimiterDataTypeCsv) {
            option.delimitateByComma();
            ext = "csv";
        } else {
            option.delimitateByTab(); // as default
            ext = "tsv";
        }
        final String tableDbName = table.getName();
        _log.info("...Outputting delimiter data (over xls limit): " + tableDbName);
        if (!delimiterDir.exists()) {
            delimiterDir.mkdirs();
        }
        final FileToken fileToken = new FileTokenImpl();
        final String delimiterFilePath = delimiterDir.getPath() + "/" + tableDbName + "." + ext;
        final List<Column> columnList = table.getColumnList();
        final List<String> columnNameList = new ArrayList<String>();
        for (Column column : columnList) {
            columnNameList.add(column.getName());
        }
        option.headerInfo(columnNameList);
        final DfJFacCursorCallback cursorCallback = templateDataResult.getCursorCallback();
        cursorCallback.select(new DfJFacCursorHandler() {
            int count = 0;

            public void handle(final DfJFacResultSetWrapper wrapper) {
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

    public void setDelimiterDataOutputDir(File delimiterDataOutputDir) {
        this._delimiterDataOutputDir = delimiterDataOutputDir;
    }

    public void setDelimiterDataTypeCsv(boolean delimiterDataTypeCsv) {
        this._delimiterDataTypeCsv = delimiterDataTypeCsv;
    }
}
