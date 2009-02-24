/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.jdbc.metadata.info;

import java.util.Map;

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
    public boolean isTableTypeTable() {
        return _tableType != null ? _tableType.equalsIgnoreCase("TABLE") : false;
    }

    public boolean isTableTypeView() {
        return _tableType != null ? _tableType.equalsIgnoreCase("VIEW") : false;
    }

    public boolean isTableTypeAlias() {
        return _tableType != null ? _tableType.equalsIgnoreCase("ALIAS") : false;
    }

    public boolean isTableTypeSynonym() {
        return _tableType != null ? _tableType.equalsIgnoreCase("SYNONYM") : false;
    }
    
    public boolean canHandleSynonym() {
        return isTableTypeSynonym() || isTableTypeAlias();
    }

    // ===================================================================================
    //                                                                        Name Builder
    //                                                                        ============
    public String selectMetaExtractingSchemaName(String schemaName) {
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
    //                                                                              Accept
    //                                                                              ======
    public void acceptTableComment(Map<String, String> tableCommentMap) {
        final String comment = tableCommentMap.get(_tableName);
        if (comment != null && comment.trim().length() > 0) {
            _tableComment = comment;
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
