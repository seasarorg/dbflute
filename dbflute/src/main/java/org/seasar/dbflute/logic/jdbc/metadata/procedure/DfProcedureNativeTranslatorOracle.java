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
package org.seasar.dbflute.logic.jdbc.metadata.procedure;

import java.util.List;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta.DfProcedureType;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureNativeExtractorOracle.ProcedureNativeInfo;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureParameterNativeExtractorOracle.ProcedureArgumentInfo;

/**
 * @author jflute
 * @since 0.9.9.1A (2011/09/30 Friday)
 */
public class DfProcedureNativeTranslatorOracle {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfProcedureNativeTranslatorOracle() {
    }

    // ===================================================================================
    //                                                                      Procedure Meta
    //                                                                      ==============
    public DfProcedureMeta createDBLinkProcedureMeta(ProcedureNativeInfo nativeInfo, String dbLinkName) {
        final DfProcedureMeta procedureMeta = new DfProcedureMeta();
        procedureMeta.setProcedureCatalog(nativeInfo.getObjectName());
        procedureMeta.setProcedureName(nativeInfo.getProcedureName());
        final String linkedName = nativeInfo.getProcedureName() + "@" + dbLinkName;
        procedureMeta.setProcedureFullQualifiedName(linkedName);
        procedureMeta.setProcedureSchemaQualifiedName(linkedName);
        procedureMeta.setProcedureSqlName(linkedName);
        procedureMeta.setProcedureType(DfProcedureType.procedureResultUnknown);
        final List<ProcedureArgumentInfo> argInfoList = nativeInfo.getArgInfoList();
        for (ProcedureArgumentInfo argInfo : argInfoList) {
            // TODO impl
            final DfProcedureColumnMeta columnMeta = new DfProcedureColumnMeta();
            columnMeta.setColumnName(argInfo.getArgumentName());
        }
        return procedureMeta;
    }
}
