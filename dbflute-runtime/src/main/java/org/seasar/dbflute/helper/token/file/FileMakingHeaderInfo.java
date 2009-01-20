package org.seasar.dbflute.helper.token.file;

import java.util.List;

/**
 * @author jflute
 */
public class FileMakingHeaderInfo {

    protected List<String> columnNameList = new java.util.ArrayList<String>();

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    public boolean isEmpty() {
        return this.columnNameList.isEmpty();
    }
}
