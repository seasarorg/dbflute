package org.dbflute.helper.token.file;

import java.util.List;

/**
 * @author DBFlute(AutoGenerator)
 */
public class FileTokenizingHeaderInfo {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected List<String> _columnNameList = new java.util.ArrayList<String>();

    // =====================================================================================
    //                                                                           Easy-to-Use
    //                                                                           ===========
    protected String _columnNameRowString;

    public boolean isEmpty() {
        return this._columnNameList.isEmpty();
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public List<String> getColumnNameList() {
        return _columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this._columnNameList = columnNameList;
    }

    public String getColumnNameRowString() {
        return _columnNameRowString;
    }

    public void setColumnNameRowString(String columnNameRowString) {
        _columnNameRowString = columnNameRowString;
    }
}
