package org.seasar.dbflute.helper.token.file;

/**
 * @author jflute
 */
public class FileTokenizingRowResource {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected FileTokenizingHeaderInfo fileTokenizingHeaderInfo;

    protected java.util.List<String> valueList;

    protected String rowString;

    /** The row number. */
    protected int _rowNumber;

    /** The line number. */
    protected int _lineNumber;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public FileTokenizingHeaderInfo getFileTokenizingHeaderInfo() {
        return fileTokenizingHeaderInfo;
    }

    public void setFirstLineInfo(FileTokenizingHeaderInfo fileTokenizingHeaderInfo) {
        this.fileTokenizingHeaderInfo = fileTokenizingHeaderInfo;
    }

    public java.util.List<String> getValueList() {
        return valueList;
    }

    public void setValueList(java.util.List<String> valueList) {
        this.valueList = valueList;
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
}
