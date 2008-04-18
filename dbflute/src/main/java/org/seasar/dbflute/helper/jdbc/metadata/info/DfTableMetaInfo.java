package org.seasar.dbflute.helper.jdbc.metadata.info;

/**
 * @author jflute
 * @since 0.7.0 (2008/04/18 Friday)
 */
public class DfTableMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableName;

    protected String _tableType;

    protected String _tableSchema;

    protected String _tableComment;

    protected boolean _existSameNameTable;
    
    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isTableTypeView() {
        return _tableType != null ? _tableType.equalsIgnoreCase("VIEW") : false;
    }

    // ===================================================================================
    //                                                                        Name Builder
    //                                                                        ============
    public String selectRealSchemaName(String schemaName) {
        if (isExistSameNameTable()) {
            return _tableSchema;
        } else {
            return schemaName;
        }
    }

    public String buildTableNameWithSchema() {
        if (_tableSchema != null && _tableSchema.trim().length() != 0) {
            return _tableSchema + "." + _tableName;
        } else {
            return _tableName;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableName() {
        return _tableName;
    }

    public void setTableName(String tableName) {
        this._tableName = tableName;
    }

    public String getTableType() {
        return _tableType;
    }

    public void setTableType(String tableType) {
        this._tableType = tableType;
    }

    public String getTableSchema() {
        return _tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this._tableSchema = tableSchema;
    }

    public String getTableComment() {
        return _tableComment;
    }

    public void setTableComment(String tableComment) {
        this._tableComment = tableComment;
    }

    public boolean isExistSameNameTable() {
        return _existSameNameTable;
    }

    public void setExistSameNameTable(boolean existSameNameTable) {
        this._existSameNameTable = existSameNameTable;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DfTableMetaInfo) {
            return getTableName().equals(((DfTableMetaInfo) obj).getTableName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getTableName().hashCode();
    }

    @Override
    public String toString() {
        if (_tableSchema != null && _tableSchema.trim().length() != 0) {
            return _tableSchema + "." + _tableName + "(" + _tableType + "): " + _tableComment;
        } else {
            return _tableName + "(" + _tableType + "): " + _tableComment;
        }
    }
}
