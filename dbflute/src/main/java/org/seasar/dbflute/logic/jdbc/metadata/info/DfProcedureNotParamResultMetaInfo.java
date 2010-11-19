package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.Map;

/**
 * @author jflute
 */
public class DfProcedureNotParamResultMetaInfo {

    protected String _propertyName;
    protected Map<String, DfColumnMetaInfo> _resultSetColumnInfoMap;

    public boolean hasResultSetColumnInfo() {
        return _resultSetColumnInfoMap != null && !_resultSetColumnInfoMap.isEmpty();
    }

    public String getPropertyName() {
        return _propertyName;
    }

    public void setPropertyName(String propertyName) {
        this._propertyName = propertyName;
    }

    public Map<String, DfColumnMetaInfo> getResultSetColumnInfoMap() {
        return _resultSetColumnInfoMap;
    }

    public void setResultSetColumnInfoMap(Map<String, DfColumnMetaInfo> resultSetColumnInfoMap) {
        this._resultSetColumnInfoMap = resultSetColumnInfoMap;
    }
}
