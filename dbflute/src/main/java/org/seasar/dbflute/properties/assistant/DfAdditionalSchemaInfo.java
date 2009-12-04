package org.seasar.dbflute.properties.assistant;

import java.util.List;
import java.util.Map;

/**
 * @author jflute
 * @since 0.9.5.5 (2009/10/04 Sunday)
 */
public class DfAdditionalSchemaInfo {

    protected String _schemaName;
    protected List<String> _objectTypeTargetList;
    protected List<String> _tableExceptList;
    protected List<String> _tableTargetList;
    protected boolean _suppressCommonColumn;
    protected Map<String, String> _supplementaryDataSourceMap;

    public String getSupplementaryDataSourceUser() {
        if (_supplementaryDataSourceMap == null) {
            return null;
        }
        return _supplementaryDataSourceMap.get("user");
    }

    public String getSupplementaryDataSourcePassword() {
        if (_supplementaryDataSourceMap == null) {
            return null;
        }
        return _supplementaryDataSourceMap.get("password");
    }

    public String getSchemaName() {
        return _schemaName;
    }

    public void setSchemaName(String schemaName) {
        this._schemaName = schemaName;
    }

    public List<String> getObjectTypeTargetList() {
        return _objectTypeTargetList;
    }

    public void setObjectTypeTargetList(List<String> objectTypeTargetList) {
        this._objectTypeTargetList = objectTypeTargetList;
    }

    public List<String> getTableExceptList() {
        return _tableExceptList;
    }

    public void setTableExceptList(List<String> tableExceptList) {
        this._tableExceptList = tableExceptList;
    }

    public List<String> getTableTargetList() {
        return _tableTargetList;
    }

    public void setTableTargetList(List<String> tableTargetList) {
        this._tableTargetList = tableTargetList;
    }

    public boolean isSuppressCommonColumn() {
        return _suppressCommonColumn;
    }

    public void setSuppressCommonColumn(boolean suppressCommonColumn) {
        this._suppressCommonColumn = suppressCommonColumn;
    }

    public Map<String, String> getSupplementaryDataSourceMap() {
        return _supplementaryDataSourceMap;
    }

    public void setSupplementaryDataSourceMap(Map<String, String> supplementaryDataSourceMap) {
        this._supplementaryDataSourceMap = supplementaryDataSourceMap;
    }
}
