package org.seasar.dbflute.logic.sql2entity.cmentity;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;

/**
 * @author jflute
 */
public class DfCustomizeEntityInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _tableDbName;
    protected final Map<String, DfColumnMetaInfo> _columnMap;
    protected final DfTypeStructInfo _typeStructInfo;

    // additional information (if procedure, not used)
    protected File _sqlFile;
    protected List<String> _primaryKeyList;
    protected boolean _cursorHandling;
    protected boolean _scalarHandling;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCustomizeEntityInfo(String tableDbName, Map<String, DfColumnMetaInfo> columnMap) {
        this(tableDbName, columnMap, null);
    }

    public DfCustomizeEntityInfo(String tableDbName, Map<String, DfColumnMetaInfo> columnMap,
            DfTypeStructInfo typeStructInfo) {
        _tableDbName = tableDbName;
        _columnMap = columnMap;
        _typeStructInfo = typeStructInfo;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isNormalHandling() {
        return !_cursorHandling && !_scalarHandling;
    }

    // ===================================================================================
    //                                                                   Additional Schema
    //                                                                   =================
    public UnifiedSchema getAdditionalSchema() {
        return hasTypeStructInfo() ? _typeStructInfo.getOwner() : null;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasTypeStructInfo() {
        return _typeStructInfo != null;
    }

    public boolean needsJavaNameConvert() {
        return hasTypeStructInfo();
    }

    public boolean hasNestedCustomizeEntity() {
        return hasTypeStructInfo() && _typeStructInfo.hasNestedStructEntityRef();
    }

    public boolean isAdditionalSchema() {
        return hasTypeStructInfo() && _typeStructInfo.isAdditinalSchema();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableDbName() {
        return _tableDbName;
    }

    public Map<String, DfColumnMetaInfo> getColumnMap() {
        return _columnMap;
    }

    public DfTypeStructInfo getTypeStructInfo() {
        return _typeStructInfo;
    }

    public File getSqlFile() {
        return _sqlFile;
    }

    public void setSqlFile(File sqlFile) {
        this._sqlFile = sqlFile;
    }

    public List<String> getPrimaryKeyList() {
        return _primaryKeyList;
    }

    public void setPrimaryKeyList(List<String> primaryKeyList) {
        this._primaryKeyList = primaryKeyList;
    }

    public boolean isCursorHandling() {
        return _cursorHandling;
    }

    public void setCursorHandling(boolean cursorHandling) {
        this._cursorHandling = cursorHandling;
    }

    public boolean isScalarHandling() {
        return _scalarHandling;
    }

    public void setScalarHandling(boolean scalarHandling) {
        this._scalarHandling = scalarHandling;
    }
}
