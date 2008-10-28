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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataSet;
import org.seasar.dbflute.helper.dataset.DataSetConstants;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.util.basic.DfStringUtil;
import org.seasar.dbflute.util.crypto.DfBase64Util;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute 
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfXlsWriter implements DataSetConstants {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected OutputStream out;

    protected HSSFWorkbook workbook;

    protected HSSFCellStyle dateStyle;

    protected HSSFCellStyle base64Style;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfXlsWriter(File file) {
        this(create(file));
    }
    
    protected static OutputStream create(File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public DfXlsWriter(OutputStream out) {
        setOutputStream(out);
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public void setOutputStream(OutputStream out) {
        this.out = out;
        workbook = new HSSFWorkbook();
        HSSFDataFormat df = workbook.createDataFormat();
        dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(df.getFormat(DATE_FORMAT));
        base64Style = workbook.createCellStyle();
        base64Style.setDataFormat(df.getFormat(BASE64_FORMAT));
    }

    public void write(DataSet dataSet) {
        for (int i = 0; i < dataSet.getTableSize(); ++i) {
            DataTable table = dataSet.getTable(i);
            HSSFSheet sheet = workbook.createSheet();
            workbook.setSheetName(i, table.getTableName());
            HSSFRow headerRow = sheet.createRow(0);
            for (int j = 0; j < table.getColumnSize(); ++j) {
                HSSFCell cell = headerRow.createCell((short) j);
                // cell.setCellValue(new HSSFRichTextString(table.getColumnName(j)));
                cell.setCellValue(table.getColumnName(j));
            }
            for (int j = 0; j < table.getRowSize(); ++j) {
                HSSFRow row = sheet.createRow(j + 1);
                for (int k = 0; k < table.getColumnSize(); ++k) {
                    DataRow dataRow = table.getRow(j);
                    Object value = dataRow.getValue(k);
                    if (value != null) {
                        HSSFCell cell = row.createCell((short) k);
                        setValue(cell, value);
                    }
                }
            }
        }
        try {
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void setValue(HSSFCell cell, Object value) {
        if (value instanceof Number) {
            // cell.setCellValue(new HSSFRichTextString(value.toString()));
            cell.setCellValue(value.toString());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            cell.setCellStyle(dateStyle);
        } else if (value instanceof byte[]) {
            // cell.setCellValue(new HSSFRichTextString(Base64Util.encode((byte[]) value)));
            cell.setCellValue(DfBase64Util.encode((byte[]) value));
            cell.setCellStyle(base64Style);
        } else if (value instanceof Boolean) {
            cell.setCellValue(((Boolean) value).booleanValue());
        } else {
            // cell.setCellValue(new HSSFRichTextString(StringConversionUtil.toString(value, null)));
            cell.setCellValue(DfStringUtil.toString(value, null));
        }
    }
}
