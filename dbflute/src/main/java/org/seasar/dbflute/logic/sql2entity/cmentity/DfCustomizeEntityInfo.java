package org.seasar.dbflute.logic.sql2entity.cmentity;

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
}
