/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.dbflute.exception.DfXlsReaderReadFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.dataset.DfDataColumn;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataSet;
import org.seasar.dbflute.helper.dataset.DfDataSetConstants;
import org.seasar.dbflute.helper.dataset.DfDataTable;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnType;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnTypes;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author modified by jflute (originated in Seasar2)
 */
public class DfXlsReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfXlsReader.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          Xls Resource
    //                                          ------------
    protected DfDataSet _dataSet;
    protected HSSFWorkbook _workbook;
    protected HSSFDataFormat _dataFormat;

    // -----------------------------------------------------
    //                                           Read Option
    //                                           -----------
    protected final Map<String, String> _tableNameMap;
    protected final Map<String, List<String>> _notTrimTableColumnMap;
    protected final Map<String, List<String>> _emptyStringTableColumnMap;
    protected final Pattern _skipSheetPattern; // not required

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfXlsReader(File file // xls file to read
            , Map<String, String> tableNameMap // map for long table name
            , Map<String, List<String>> notTrimTableColumnMap // map for not-trim column
            , Map<String, List<String>> emptyStringTableColumnMap // map for empty-string-allowed column
            , Pattern skipSheetPattern) { // pattern of skipped sheet
        this(create(file), tableNameMap, notTrimTableColumnMap, emptyStringTableColumnMap, skipSheetPattern);
    }

    protected DfXlsReader(InputStream ins // stream for xls file to read
            , Map<String, String> tableNameMap // map for long table name
            , Map<String, List<String>> notTrimTableColumnMap // map for not-trim column
            , Map<String, List<String>> emptyStringTableColumnMap // map for empty-string-allowed column
            , Pattern skipSheetPattern) { // pattern of skipped sheet
        if (tableNameMap != null) {
            this._tableNameMap = tableNameMap;
        } else {
            this._tableNameMap = StringKeyMap.createAsFlexible();
        }
        if (notTrimTableColumnMap != null) {
            this._notTrimTableColumnMap = notTrimTableColumnMap;
        } else {
            this._notTrimTableColumnMap = StringKeyMap.createAsFlexible();
        }
        if (emptyStringTableColumnMap != null) {
            this._emptyStringTableColumnMap = emptyStringTableColumnMap;
        } else {
            this._emptyStringTableColumnMap = StringKeyMap.createAsFlexible();
        }
        this._skipSheetPattern = skipSheetPattern;
        setupWorkbook(ins);
    }

    protected static InputStream create(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
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
        _dataSet = new DfDataSet();
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

    protected DfDataTable createTable(String sheetName, HSSFSheet sheet) {
        String tableName = sheetName;
        if (_tableNameMap != null && !_tableNameMap.isEmpty() && sheetName.startsWith("$")) {
            String realTableName = _tableNameMap.get(sheetName);
            if (realTableName == null) {
                realTableName = _tableNameMap.get(sheetName.substring("$".length()));
                if (realTableName == null) {
                    throwXlsReaderMappingTableNotFoundException(sheetName);
                }
            }
            tableName = realTableName;
        }
        final DfDataTable table = _dataSet.addTable(tableName);
        final int rowCount = sheet.getLastRowNum();
        final HSSFRow nameRow = sheet.getRow(0);
        if (nameRow == null) {
            throwXlsReaderFirstRowNotColumnDefinitionException(tableName);
        }
        if (rowCount > 0) {
            setupColumns(table, nameRow, sheet.getRow(1));
            setupRows(table, sheet);
        } else if (rowCount == 0) {
            setupColumns(table, nameRow, null);
        }
        return table;
    }

    protected void throwXlsReaderMappingTableNotFoundException(String sheetName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The sheetName was not found in the tableNameMap.");
        br.addItem("TableName Map");
        if (!_tableNameMap.isEmpty()) {
            for (Entry<String, String> entry : _tableNameMap.entrySet()) {
                br.addElement(entry.getKey() + " = " + entry.getValue());
            }
        } else {
            br.addElement("*empty");
        }
        br.addItem("Sheet Name");
        br.addElement(sheetName);
        final String msg = br.buildExceptionMessage();
        throw new DfXlsReaderReadFailureException(msg);
    }

    protected void throwXlsReaderFirstRowNotColumnDefinitionException(String tableName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The first row of the sheet was not column definition.");
        br.addItem("Table");
        br.addElement(tableName);
        final String msg = br.buildExceptionMessage();
        throw new DfXlsReaderReadFailureException(msg);
    }

    protected void setupColumns(DfDataTable table, HSSFRow nameRow, HSSFRow valueRow) {
        for (int i = 0;; ++i) {
            final HSSFCell nameCell = nameRow.getCell(i);
            if (nameCell == null) {
                break;
            }
            final HSSFRichTextString richStringCellValue = nameCell.getRichStringCellValue();
            if (richStringCellValue == null) {
                break;
            }
            final String columnName = richStringCellValue.getString().trim();
            if (columnName.length() == 0) {
                break;
            }
            HSSFCell valueCell = null;
            if (valueRow != null) {
                valueCell = valueRow.getCell(i);
            }
            if (valueCell != null) {
                table.addColumn(columnName, getColumnType(valueCell));
            } else {
                table.addColumn(columnName);
            }
        }
    }

    protected void setupRows(DfDataTable table, HSSFSheet sheet) {
        for (int i = 1;; ++i) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            setupRow(table, row);
        }
    }

    protected void setupRow(DfDataTable table, HSSFRow row) {
        final DfDataRow dataRow = table.addRow();
        HSSFCell cell = null;
        Object value = null;
        DfDataColumn column = null;
        try {
            for (int i = 0; i < table.getColumnSize(); ++i) {
                cell = row.getCell(i);
                value = getValue(i, cell, table);
                column = table.getColumn(i);
                final String columnName = column.getColumnDbName();
                try {
                    dataRow.addValue(columnName, value);
                } catch (NumberFormatException e) {
                    if (cell.getCellType() != HSSFCell.CELL_TYPE_STRING) {
                        throw e;
                    }
                    String msg = "...Changing the column type to STRING type:";
                    msg = msg + " name=" + columnName + " value=" + value;
                    _log.info(msg);
                    column.setColumnType(DfDtsColumnTypes.STRING);
                    dataRow.addValue(columnName, value);
                }
            }
        } catch (RuntimeException e) {
            throwCellValueHandlingException(table, column, row, cell, value, e);
        }
    }

    protected void throwCellValueHandlingException(DfDataTable table, DfDataColumn column, HSSFRow row, HSSFCell cell,
            Object value, RuntimeException e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + ln();
        msg = msg + "Failed to handling the cell value!" + ln();
        msg = msg + ln();
        msg = msg + "[Table]" + ln() + table.getTableDbName() + ln();
        msg = msg + ln();
        msg = msg + "[Column]" + ln() + (column != null ? column.getColumnDbName() : null) + ln();
        msg = msg + ln();
        msg = msg + "[Row Number]" + ln() + row.getRowNum() + ln();
        msg = msg + ln();
        msg = msg + "[Cell Object]" + ln() + cell + ln();
        msg = msg + ln();
        if (cell != null) {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                msg = msg + "[Cell Type]" + ln() + "CELL_TYPE_NUMERIC" + ln();
                break;
            case HSSFCell.CELL_TYPE_STRING:
                msg = msg + "[Cell Type]" + ln() + "CELL_TYPE_STRING" + ln();
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                msg = msg + "[Cell Type]" + ln() + "CELL_TYPE_FORMULA" + ln();
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                msg = msg + "[Cell Type]" + ln() + "CELL_TYPE_BLANK" + ln();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                msg = msg + "[Cell Type]" + ln() + "CELL_TYPE_BOOLEAN" + ln();
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                msg = msg + "[Cell Type]" + ln() + "CELL_TYPE_ERROR" + ln();
                break;
            default:
                msg = msg + "[Cell Type]" + ln() + cell.getCellType() + ln();
                break;
            }
        }
        msg = msg + ln();
        msg = msg + "[Cell Value]" + ln() + value + ln();
        msg = msg + "- - - - - - - - - -/";
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public DfDataSet read() {
        return _dataSet;
    }

    // ===================================================================================
    //                                                                      Value Handling
    //                                                                      ==============
    public Object getValue(int columnIndex, HSSFCell cell, DfDataTable table) {
        if (cell == null) {
            if (isEmptyStringTarget(columnIndex, table)) {
                return "\"\""; // for preventing trimming later
            } else {
                return null;
            }
        }
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_NUMERIC:
            if (isCellDateFormatted(cell)) {
                return DfTypeUtil.toTimestamp(cell.getDateCellValue());
            }
            final double numericCellValue = cell.getNumericCellValue();
            if (isInt(numericCellValue)) {
                return new BigDecimal((int) numericCellValue);
            }
            return new BigDecimal(Double.toString(numericCellValue));
        case HSSFCell.CELL_TYPE_STRING:
            String s = cell.getRichStringCellValue().getString();
            if (s != null) {
                if (isNotTrimTarget(cell, table)) {
                    if (s.length() != s.trim().length()) {
                        s = "\"" + s + "\""; // for preventing trimming later
                    }
                } else {
                    s = Srl.rtrim(s);
                }
            }
            if ("".equals(s)) {
                s = null;
            }
            if (isEmptyStringTarget(columnIndex, table) && s == null) {
                s = "\"\""; // for preventing trimming later
            }
            if (isCellBase64Formatted(cell)) {
                return DfTypeUtil.decodeAsBase64(s);
            }
            return s;
        case HSSFCell.CELL_TYPE_BOOLEAN:
            boolean b = cell.getBooleanCellValue();
            return Boolean.valueOf(b);
        default:
            if (isEmptyStringTarget(columnIndex, table)) {
                return "\"\"";
            } else {
                return null;
            }
        }
    }

    public boolean isNotTrimTarget(HSSFCell cell, DfDataTable table) {
        final String tableName = table.getTableDbName();
        if (!_notTrimTableColumnMap.containsKey(tableName)) {
            return false;
        }
        final List<String> notTrimTargetColumnList = _notTrimTableColumnMap.get(tableName);
        final DfDataColumn column = table.getColumn(cell.getColumnIndex());
        final String targetColumnName = column.getColumnDbName();
        for (String currentColumnName : notTrimTargetColumnList) {
            if (targetColumnName.equalsIgnoreCase(currentColumnName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmptyStringTarget(int columnIndex, DfDataTable table) {
        final String tableName = table.getTableDbName();
        if (!_emptyStringTableColumnMap.containsKey(tableName)) {
            return false;
        }
        final List<String> emptyStringTargetColumnList = _emptyStringTableColumnMap.get(tableName);
        final DfDataColumn column = table.getColumn(columnIndex);
        final String targetColumnName = column.getColumnDbName();
        for (String currentColumnName : emptyStringTargetColumnList) {
            if (targetColumnName.equalsIgnoreCase(currentColumnName)) {
                return true;
            }
        }
        return false;
    }

    protected DfDtsColumnType getColumnType(HSSFCell cell) {
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_NUMERIC:
            if (isCellDateFormatted(cell)) {
                return DfDtsColumnTypes.TIMESTAMP;
            }
            return DfDtsColumnTypes.BIGDECIMAL;
        case HSSFCell.CELL_TYPE_BOOLEAN:
            return DfDtsColumnTypes.BOOLEAN;
        case HSSFCell.CELL_TYPE_STRING:
            if (isCellBase64Formatted(cell)) {
                return DfDtsColumnTypes.BINARY;
            }
            return DfDtsColumnTypes.STRING;
        default:
            return DfDtsColumnTypes.STRING;
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    protected boolean isCellBase64Formatted(HSSFCell cell) {
        HSSFCellStyle cs = cell.getCellStyle();
        short dfNum = cs.getDataFormat();
        return DfDataSetConstants.BASE64_FORMAT.equals(_dataFormat.getFormat(dfNum));
    }

    protected boolean isCellDateFormatted(HSSFCell cell) {
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

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }
}
