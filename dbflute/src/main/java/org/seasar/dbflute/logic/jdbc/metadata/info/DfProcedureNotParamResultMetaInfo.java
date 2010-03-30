package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class DfProcedureNotParamResultMetaInfo {

    protected String propertyName;
    protected Map<String, DfColumnMetaInfo> columnMetaInfoMap = DfCollectionUtil.emptyMap();

    public boolean hasColumnMetaInfo() {
        return !columnMetaInfoMap.isEmpty();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Map<String, DfColumnMetaInfo> getColumnMetaInfoMap() {
        return columnMetaInfoMap;
    }

    public void setColumnMetaInfoMap(Map<String, DfColumnMetaInfo> columnMetaInfoMap) {
        this.columnMetaInfoMap = columnMetaInfoMap;
    }
}
