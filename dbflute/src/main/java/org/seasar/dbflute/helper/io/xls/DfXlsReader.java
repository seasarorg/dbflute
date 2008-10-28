/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.io.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.dataset.DataColumn;
import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataSet;
import org.seasar.dbflute.helper.dataset.DataSetConstants;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.helper.dataset.types.ColumnType;
import org.seasar.dbflute.helper.dataset.types.ColumnTypes;
import org.seasar.dbflute.helper.io.data.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.util.basic.DfStringUtil;
import org.seasar.dbflute.util.basic.DfTimestampUtil;
import org.seasar.dbflute.util.crypto.DfBase64Util;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 */
public class DfXlsReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          Xls Resource
    //                                          ------------
    protected DataSet _dataSet;

    protected HSSFWorkbook _workbook;

    protected HSSFDataFormat _dataFormat;

    // -----------------------------------------------------
    //                                           Read Option
    //                                           -----------
    protected DfFlexibleMap<String, String> _tableNameMap;

    protected DfFlexibleMap<String, List<String>> _notTrimTableColumnMap;

    protected Pattern _skipSheetPattern;// Not Required

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfXlsReader(File file, DfFlexibleMap<String, String> tableNameMap,
            DfFlexibleMap<String, List<String>> notTrimTableColumnMap, Pattern skipSheetPattern) {
        this(create(file), tableNameMap, notTrimTableColumnMap, skipSheetPattern);
    }

    protected static InputStream create(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public DfXlsReader(InputStream in, DfFlexibleMap<String, String> tableNameMap,
            DfFlexibleMap<String, List<String>> notTrimTableColumnMap, Pattern skipSheetPattern) {
        this._tableNameMap = tableNameMap;
        this._notTrimTableColumnMap = notTrimTableColumnMap;
        this._skipSheetPattern = skipSheetPattern;
        setupWorkbook(in);
    }

    // -----------------------------------------------------
    //                                       Set up Workbook
    //                                       ---------------
    protected void setupWorkbook(InputStream in) {
        try {
            _workbook = new HSSFWorkbook(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        _dataFormat = _workbook.createDataFormat();
        _dataSet = new DataSet();
        for (int i = 0; i < _workbook.getNumberOfSheets(); ++i) {
            final String sheetName = _workbook.getSheetName(i);
            if (isCommentOutSheet(sheetName)) {// since 0.7.9
                _log.info("*The sheet has comment-out mark so skip it: " + sheetName);
                continue;
            }
            if (isSkipSheet(sheetName)) {// since 0.7.9 for [DBFLUTE-251]
                _log.info("*The sheet name matched skip-sheet specification so skip it: " + sheetName);
                continue;
            }
            createTable(sheetName, _workbook.getSheetAt(i));
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

        final int rowCount = sheet.getLastRowNum();
        final HSSFRow nameRow = sheet.getRow(0);
        if (nameRow == null) {
            String msg = "The first row of the sheet should be column definition but it is null:";
            msg = msg + " sheet=" + tableName;
            throw new IllegalStateException(msg);
        }
        if (rowCount > 0) {
            setupColumns(table, nameRow, sheet.getRow(1));
            setupRows(table, sheet);
        } else if (rowCount == 0) {
            setupColumns(table, nameRow, null);
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
                final DataColumn column = table.getColumn(i);
                try {
                    dataRow.setValue(column.getColumnName(), value);
                } catch (NumberFormatException e) {
                    if (cell.getCellType() != HSSFCell.CELL_TYPE_STRING) {
                        throw e;
                    }
                    String msg = "...Changing the column type to STRING type:";
                    msg = msg + " name=" + column.getColumnName() + " value=" + value;
                    _log.info(msg);
                    column.setColumnType(ColumnTypes.STRING);
                    dataRow.setValue(column.getColumnName(), value);
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
        msg = msg + "* * * * * * * * * */";
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
                return DfTimestampUtil.toTimestamp(cell.getDateCellValue());
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
                    s = DfStringUtil.rtrim(s);
                }
                // --------------------/
            }
            if ("".equals(s)) {
                s = null;
            }
            if (isCellBase64Formatted(cell)) {
                return DfBase64Util.decode(s);
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
        return DataSetConstants.BASE64_FORMAT.equals(_dataFormat.getFormat(dfNum));
    }

    public boolean isCellDateFormatted(HSSFCell cell) {
        HSSFCellStyle cs = cell.getCellStyle();
        short dfNum = cs.getDataFormat();
        String format = _dataFormat.getFormat(dfNum);
        if (format == null || format.length() == 0) {
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

    protected boolean isCommentOutSheet(String sheetName) {
        return sheetName.startsWith("#");
    }

    protected boolean isSkipSheet(String sheetName) {
        if (_skipSheetPattern == null) {
            return false;
        }
        return _skipSheetPattern.matcher(sheetName).matches();
    }
}
