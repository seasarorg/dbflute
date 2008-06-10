package org.seasar.dbflute.helper.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.dbflute.helper.datahandler.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.extension.dataset.ColumnType;
import org.seasar.extension.dataset.DataColumn;
import org.seasar.extension.dataset.DataReader;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataSetConstants;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;
import org.seasar.extension.dataset.types.ColumnTypes;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.Base64Util;
import org.seasar.framework.util.FileInputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.TimestampConversionUtil;

/**
 * @author jflute 
 */
public class DfXlsReader implements DataReader, DataSetConstants {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected org.seasar.extension.dataset.DataSet _dataSet;

    protected HSSFWorkbook _workbook;

    protected HSSFDataFormat _dataFormat;

    protected DfFlexibleNameMap<String, String> _tableNameMap;

    protected DfFlexibleNameMap<String, List<String>> _notTrimTableColumnMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfXlsReader(File file, DfFlexibleNameMap<String, String> tableNameMap,
            DfFlexibleNameMap<String, List<String>> notTrimTableColumnMap) {
        this(FileInputStreamUtil.create(file), tableNameMap, notTrimTableColumnMap);
    }

    public DfXlsReader(InputStream in, DfFlexibleNameMap<String, String> tableNameMap,
            DfFlexibleNameMap<String, List<String>> notTrimTableColumnMap) {
        this._tableNameMap = tableNameMap;
        this._notTrimTableColumnMap = notTrimTableColumnMap;
        try {
            _workbook = new HSSFWorkbook(in);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
        _dataFormat = _workbook.createDataFormat();
        _dataSet = new DataSetImpl();
        for (int i = 0; i < _workbook.getNumberOfSheets(); ++i) {
            createTable(_workbook.getSheetName(i), _workbook.getSheetAt(i));
        }
    }

    protected DataTable createTable(String sheetName, HSSFSheet sheet) {
        // /----------------------------------------------------------------- Modification
        String tableName = sheetName;
        if (_tableNameMap != null && !_tableNameMap.isEmpty() && sheetName.startsWith("$")) {
            String realTableName = _tableNameMap.get(sheetName);
            if (realTableName == null) {
                realTableName = _tableNameMap.get(sheetName.substring("$".length()));
                if (realTableName == null) {
                    String msg = "The sheetName[" + sheetName + "] was not found in the tableNameMap: " + _tableNameMap;
                    throw new IllegalStateException(msg);
                }
            }
            tableName = realTableName;
        }
        final DataTable table = _dataSet.addTable(tableName);
        // --------------------/

        int rowCount = sheet.getLastRowNum();
        if (rowCount > 0) {
            setupColumns(table, sheet.getRow(0), sheet.getRow(1));
            setupRows(table, sheet);
        } else if (rowCount == 0) {
            setupColumns(table, sheet.getRow(0), null);
        }
        return table;
    }

    protected void setupColumns(DataTable table, HSSFRow nameRow, HSSFRow valueRow) {
        for (int i = 0;; ++i) {
            HSSFCell nameCell = nameRow.getCell((short) i);
            if (nameCell == null) {
                break;
            }
            String columnName = nameCell.getStringCellValue().trim();
            if (columnName.length() == 0) {
                break;
            }
            HSSFCell valueCell = null;
            if (valueRow != null) {
                valueCell = valueRow.getCell((short) i);
            }
            if (valueCell != null) {
                table.addColumn(columnName, getColumnType(valueCell));
            } else {
                table.addColumn(columnName);
            }
        }
    }

    protected void setupRows(DataTable table, HSSFSheet sheet) {
        for (int i = 1;; ++i) {
            HSSFRow row = sheet.getRow((short) i);
            if (row == null) {
                break;
            }
            setupRow(table, row);
        }
    }

    protected void setupRow(DataTable table, HSSFRow row) {
        DataRow dataRow = table.addRow();
        // /----------------------------------------------------------------- Modification
        // Add try-catch
        HSSFCell cell = null;
        Object value = null;
        try {
            for (int i = 0; i < table.getColumnSize(); ++i) {
                cell = row.getCell((short) i);
                value = getValue(cell, table);
                try {
                    dataRow.setValue(i, value);
                } catch (NumberFormatException e) {
                    if (cell.getCellType() != HSSFCell.CELL_TYPE_STRING) {
                        throw e;
                    }
                    DataColumn column = table.getColumn(i);
                    String msg = "...Changing the column type to STRING type:";
                    msg = msg + " name=" + column.getColumnName() + " value=" + value;
                    _log.info(msg);
                    column.setColumnType(ColumnTypes.STRING);
                    dataRow.setValue(i, value);
                }
            }
        } catch (RuntimeException e) {
            throwCellValueHandlingException(cell, value, e);
        }
        // --------------------/
    }

    // /----------------------------------------------------------------- Modification
    // Add
    protected void throwCellValueHandlingException(HSSFCell cell, Object value, RuntimeException e) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The handling of the cell value was failed!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Cell Object]" + getLineSeparator() + cell + getLineSeparator();
        msg = msg + getLineSeparator();
        if (cell != null) {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                msg = msg + "[Cell Type]" + getLineSeparator() + "CELL_TYPE_NUMERIC" + getLineSeparator();
                break;
            case HSSFCell.CELL_TYPE_STRING:
                msg = msg + "[Cell Type]" + getLineSeparator() + "CELL_TYPE_STRING" + getLineSeparator();
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                msg = msg + "[Cell Type]" + getLineSeparator() + "CELL_TYPE_FORMULA" + getLineSeparator();
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                msg = msg + "[Cell Type]" + getLineSeparator() + "CELL_TYPE_BLANK" + getLineSeparator();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                msg = msg + "[Cell Type]" + getLineSeparator() + "CELL_TYPE_BOOLEAN" + getLineSeparator();
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                msg = msg + "[Cell Type]" + getLineSeparator() + "CELL_TYPE_ERROR" + getLineSeparator();
                break;
            default:
                msg = msg + "[Cell Type]" + getLineSeparator() + cell.getCellType() + getLineSeparator();
                break;
            }
        }
        msg = msg + getLineSeparator();
        msg = msg + "[Cell Value]" + getLineSeparator() + value + getLineSeparator();
        msg = msg + "* * * * * * * * * */" + getLineSeparator();
        throw new IllegalStateException(msg, e);
    }

    protected String getLineSeparator() {
        return System.getProperty("line.separator");
    }
    // --------------------/

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public DataSet read() {
        return _dataSet;
    }

    // ===================================================================================
    //                                                                      Value Handling
    //                                                                      ==============
    public Object getValue(HSSFCell cell, DataTable table) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_NUMERIC:
            if (isCellDateFormatted(cell)) {
                return TimestampConversionUtil.toTimestamp(cell.getDateCellValue());
            }
            final double numericCellValue = cell.getNumericCellValue();
            if (isInt(numericCellValue)) {
                return new BigDecimal((int) numericCellValue);
            }
            return new BigDecimal(Double.toString(numericCellValue));
        case HSSFCell.CELL_TYPE_STRING:
            String s = cell.getStringCellValue();
            if (s != null) {
                // /----------------------------------------------------------------- Modification
                if (isNotTrimTarget(cell, table)) {
                    if (s.length() != s.trim().length()) {
                        s = "\"" + s + "\"";
                    }
                } else {
                    s = StringUtil.rtrim(s);
                }
                // --------------------/
            }
            if ("".equals(s)) {
                s = null;
            }
            if (isCellBase64Formatted(cell)) {
                return Base64Util.decode(s);
            }
            return s;
        case HSSFCell.CELL_TYPE_BOOLEAN:
            boolean b = cell.getBooleanCellValue();
            return Boolean.valueOf(b);
        default:
            return null;
        }
    }

    // /----------------------------------------------------------------- Modification
    public boolean isNotTrimTarget(HSSFCell cell, DataTable table) {
        final String tableName = table.getTableName();
        if (!_notTrimTableColumnMap.containsKey(tableName)) {
            return false;
        }
        final List<String> notTrimTargetColumnList = _notTrimTableColumnMap.get(tableName);
        final DataColumn column = table.getColumn(cell.getCellNum());
        final String targetColumnName = column.getColumnName();
        for (String currentColumnName : notTrimTargetColumnList) {
            if (targetColumnName.equalsIgnoreCase(currentColumnName)) {
                return true;
            }
        }
        return false;
    }
    // --------------------/

    protected ColumnType getColumnType(HSSFCell cell) {
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_NUMERIC:
            if (isCellDateFormatted(cell)) {
                return ColumnTypes.TIMESTAMP;
            }
            return ColumnTypes.BIGDECIMAL;
        case HSSFCell.CELL_TYPE_BOOLEAN:
            return ColumnTypes.BOOLEAN;
        case HSSFCell.CELL_TYPE_STRING:
            if (isCellBase64Formatted(cell)) {
                return ColumnTypes.BINARY;
            }
            return ColumnTypes.STRING;
        default:
            return ColumnTypes.STRING;
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isCellBase64Formatted(HSSFCell cell) {
        HSSFCellStyle cs = cell.getCellStyle();
        short dfNum = cs.getDataFormat();
        return BASE64_FORMAT.equals(_dataFormat.getFormat(dfNum));
    }

    public boolean isCellDateFormatted(HSSFCell cell) {
        HSSFCellStyle cs = cell.getCellStyle();
        short dfNum = cs.getDataFormat();
        String format = _dataFormat.getFormat(dfNum);
        if (StringUtil.isEmpty(format)) {
            return false;
        }
        if (format.indexOf('/') > 0 || format.indexOf('y') > 0 || format.indexOf('m') > 0 || format.indexOf('d') > 0) {
            return true;
        }
        return false;
    }

    protected boolean isInt(final double numericCellValue) {
        return ((int) numericCellValue) == numericCellValue;
    }
}
