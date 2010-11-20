package org.seasar.dbflute.logic.sql2entity.cmentity;

import java.util.Map;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;

/**
 * @author jflute
 */
public class DfCustomizeEntityInfo {

    protected final String _tableDbName;
    protected final Map<String, DfColumnMetaInfo> _columnMap;
    protected String _forcedEntityName;

    public DfCustomizeEntityInfo(String tableDbName, Map<String, DfColumnMetaInfo> columnMap) {
        _tableDbName = tableDbName;
        _columnMap = columnMap;
    }

    public boolean hasForcedEntityName() {
        return _forcedEntityName != null;
    }
    
    public String getTableDbName() {
        return _tableDbName;
    }

    public Map<String, DfColumnMetaInfo> getColumnMap() {
        return _columnMap;
    }

    public String getForcedEntityName() {
        return _forcedEntityName;
    }

    public void setForcedEntityName(String forcedEntityName) {
        this._forcedEntityName = forcedEntityName;
    }
}
