package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jflute
 */
public class DfProcedureClosetResultMetaInfo {

    protected String propertyName;
    protected List<DfColumnMetaInfo> columnMetaInfoList = new ArrayList<DfColumnMetaInfo>();

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public List<DfColumnMetaInfo> getColumnMetaInfoList() {
        return columnMetaInfoList;
    }

    public void addColumnMetaInfo(DfColumnMetaInfo columnMetaInfo) {
        this.columnMetaInfoList.add(columnMetaInfo);
    }
}
