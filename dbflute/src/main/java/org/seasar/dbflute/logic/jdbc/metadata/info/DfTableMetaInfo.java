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
package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.Map;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserTabComments;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @since 0.7.0 (2008/04/18 Friday)
 */
public class DfTableMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableName; // NotNull
    protected String _tableType; // NotNull
    protected String _tableComment;
    protected UnifiedSchema _unifiedSchema; // NotNull
    protected boolean _existSameNameTable;
    protected boolean _outOfGenerateTarget;

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

    public boolean hasTableComment() {
        return _tableComment != null && _tableComment.trim().length() > 0;
    }

    // ===================================================================================
    //                                                                       Name Building
    //                                                                       =============
    public String buildTableDisplayName() {
        return buildCatalogSchemaTable();
    }

    public String buildTableSqlName() {
        return _unifiedSchema.buildSqlElement(_tableName);
    }

    public String buildCatalogSchemaTable() {
        return _unifiedSchema.buildCatalogSchemaElement(_tableName);
    }

    public String buildIdentifiedSchemaTable() {
        return _unifiedSchema.buildIdentifiedSchemaElement(_tableName);
    }

    public String buildPureSchemaTable() {
        return _unifiedSchema.buildPureSchemaElement(_tableName);
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptTableComment(Map<String, UserTabComments> tableCommentMap) {
        if (tableCommentMap == null) {
            return;
        }
        final UserTabComments userTabComments = tableCommentMap.get(_tableName);
        if (userTabComments != null && userTabComments.hasComments()) {
            _tableComment = userTabComments.getComments();
        }
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
        String comment = "";
        if (_tableComment != null) {
            final String ln = DfSystemUtil.getLineSeparator();
            final int indexOf = _tableComment.indexOf(ln);
            if (indexOf > 0) { // not contain 0 because ignore first line separator
                comment = _tableComment.substring(0, indexOf) + "...";
            } else {
                comment = _tableComment;
            }
        }
        return _unifiedSchema.buildCatalogSchemaElement(_tableName) + "(" + _tableType + ")"
                + ((comment != null && comment.trim().length() > 0) ? " // " + comment : "");
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

    public String getTableComment() {
        return _tableComment;
    }

    public void setTableComment(String tableComment) {
        this._tableComment = tableComment;
    }

    public UnifiedSchema getUnifiedSchema() {
        return _unifiedSchema;
    }

    public void setUnifiedSchema(UnifiedSchema unifiedSchema) {
        this._unifiedSchema = unifiedSchema;
    }

    public boolean isOutOfGenerateTarget() {
        return _outOfGenerateTarget;
    }

    public void setOutOfGenerateTarget(boolean outOfGenerateTarget) {
        this._outOfGenerateTarget = outOfGenerateTarget;
    }
}
