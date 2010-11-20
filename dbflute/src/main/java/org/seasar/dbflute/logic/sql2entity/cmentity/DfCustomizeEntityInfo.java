package org.seasar.dbflute.logic.sql2entity.cmentity;

import java.util.Map;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;

/**
 * @author jflute
 */
public class DfCustomizeEntityInfo {

    protected final String _tableDbName;
    protected final Map<String, DfColumnMetaInfo> _columnMap;
    protected boolean _needsJavaNameConvert;

    public DfCustomizeEntityInfo(String tableDbName, Map<String, DfColumnMetaInfo> columnMap) {
        _tableDbName = tableDbName;
        _columnMap = columnMap;
    }

    public boolean needsJavaNameConvert() {
        return _needsJavaNameConvert;
    }

    public DfCustomizeEntityInfo setupJavaNameConvert() {
        _needsJavaNameConvert = true;
        return this;
    }

    public String getTableDbName() {
        return _tableDbName;
    }

    public Map<String, DfColumnMetaInfo> getColumnMap() {
        return _columnMap;
    }
}
