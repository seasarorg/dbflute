package org.seasar.dbflute.bhv.batch;

import org.seasar.dbflute.Entity;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TokenFileReflectionFailure {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected java.util.List<String> _columnNameList;
    protected java.util.List<String> _valueList;

    protected String rowString;

    /** The row number. */
    protected int _rowNumber;

    /** The line number. */
    protected int _lineNumber;

    protected Entity _entity;
    protected Exception _exception;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public java.util.List<String> getColumnNameList() {
        return _columnNameList;
    }

    public void setColumnNameList(java.util.List<String> columnNameList) {
        this._columnNameList = columnNameList;
    }

    public java.util.List<String> getValueList() {
        return _valueList;
    }

    public void setValueList(java.util.List<String> valueList) {
        this._valueList = valueList;
    }

    public String getRowString() {
        return rowString;
    }

    public void setRowString(String rowString) {
        this.rowString = rowString;
    }

    public int getRowNumber() {
        return _rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        _rowNumber = rowNumber;
    }

    public int getLineNumber() {
        return _lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }

    public Entity getEntity() {
        return _entity;
    }

    public void setEntity(Entity value) {
        _entity = value;
    }

    public Exception getException() {
        return _exception;
    }

    public void setException(Exception value) {
        _exception = value;
    }
}
