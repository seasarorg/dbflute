/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.util.List;

import org.apache.torque.engine.database.model.UnifiedSchema;

/**
 * @author jflute
 * @since 0.9.6.2 (2009/12/08 Tuesday)
 */
public class DfProcedureSynonymMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfSynonymMetaInfo _synonymMetaInfo;
    protected DfProcedureMetaInfo _procedureMetaInfo;

    // ===================================================================================
    //                                                                              Switch
    //                                                                              ======
    public DfProcedureMetaInfo createMergedProcedure() {
        if (_procedureMetaInfo == null) {
            String msg = "The procedureMetaInfo should not be null!";
            throw new IllegalStateException(msg);
        }
        if (_synonymMetaInfo == null) {
            String msg = "The synonymMetaInfo should not be null!";
            throw new IllegalStateException(msg);
        }
        final DfProcedureMetaInfo metaInfo = new DfProcedureMetaInfo();
        final UnifiedSchema synonymOwner = _synonymMetaInfo.getSynonymOwner();
        final String synonymName = _synonymMetaInfo.getSynonymName();
        final String synonymFullQualifiedName = _synonymMetaInfo.buildSynonymFullQualifiedName();
        final String synonymSchemaQualifiedName = _synonymMetaInfo.buildSynonymSchemaQualifiedName();
        final String synonymSqlName = _synonymMetaInfo.buildSynonymSqlName();
        metaInfo.setProcedureSchema(synonymOwner);
        metaInfo.setProcedureName(synonymName);
        metaInfo.setProcedureFullQualifiedName(synonymFullQualifiedName);
        metaInfo.setProcedureSchemaQualifiedName(synonymSchemaQualifiedName);
        metaInfo.setProcedureSqlName(synonymSqlName);
        metaInfo.setProcedureSynonym(_procedureMetaInfo.isProcedureSynonym());
        metaInfo.setProcedureType(_procedureMetaInfo.getProcedureType());
        metaInfo.setProcedureComment(_procedureMetaInfo.getProcedureComment());
        final List<DfProcedureColumnMetaInfo> columnMetaInfoList = _procedureMetaInfo.getProcedureColumnList();
        for (DfProcedureColumnMetaInfo columnMetaInfo : columnMetaInfoList) {
            metaInfo.addProcedureColumn(columnMetaInfo);
        }
        return metaInfo;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfSynonymMetaInfo getSynonymMetaInfo() {
        return _synonymMetaInfo;
    }

    public void setSynonymMetaInfo(DfSynonymMetaInfo synonymMetaInfo) {
        this._synonymMetaInfo = synonymMetaInfo;
    }

    public DfProcedureMetaInfo getProcedureMetaInfo() {
        return _procedureMetaInfo;
    }

    public void setProcedureMetaInfo(DfProcedureMetaInfo procedureMetaInfo) {
        this._procedureMetaInfo = procedureMetaInfo;
    }
}
