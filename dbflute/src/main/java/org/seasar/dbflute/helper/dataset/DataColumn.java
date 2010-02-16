package org.seasar.dbflute.helper.dataset;

import org.seasar.dbflute.helper.dataset.types.DtsColumnType;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DataColumn {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String columnName;
    private DtsColumnType columnType;
    private int columnIndex;
    private boolean primaryKey = false;
    private boolean writable = true;
    private String formatPattern;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DataColumn(String columnName, DtsColumnType columnType, int columnIndex) {
        setColumnName(columnName);
        setColumnType(columnType);
        setColumnIndex(columnIndex);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getColumnName() {
        return columnName;
    }

    private void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DtsColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(DtsColumnType columnType) {
        this.columnType = columnType;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    private void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    public Object convert(Object value) {
        return columnType.convert(value, formatPattern);
    }
}
