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

import java.sql.Types;
import java.util.List;

import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfColumnExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta.DfProcedureColumnType;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta.DfProcedureType;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureNativeExtractorOracle.ProcedureNativeInfo;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureParameterNativeExtractorOracle.ProcedureArgumentInfo;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.1A (2011/09/30 Friday)
 */
public class DfProcedureNativeTranslatorOracle {

    protected final DfColumnExtractor _columnExtractor = new DfColumnExtractor();

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
            final DfProcedureColumnMeta columnMeta = new DfProcedureColumnMeta();
            columnMeta.setColumnName(argInfo.getArgumentName());

            final String dataType = argInfo.getDataType();
            columnMeta.setDbTypeName(dataType);
            final String jdbcType = _columnExtractor.getColumnJdbcType(Types.OTHER, dataType);
            final Integer jdbcDefValue = TypeMap.getJdbcDefValueByJdbcType(jdbcType);
            columnMeta.setJdbcDefType(jdbcDefValue);

            final String overload = argInfo.getOverload();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(overload)) {
                columnMeta.setOverloadNo(Integer.valueOf(overload));
            }

            final String inOut = argInfo.getInOut();
            if ("in".equalsIgnoreCase(inOut)) {
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnIn);
            } else if ("out".equalsIgnoreCase(inOut)) {
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnOut);
            } else if ("inout".equalsIgnoreCase(inOut) || "in/out".equalsIgnoreCase(inOut)) {
                // two pattern condition just in case
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnInOut);
            } else {
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnUnknown);
            }

            final String dataLength = argInfo.getDataLength();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(dataLength)) {
                columnMeta.setColumnSize(Integer.valueOf(dataLength));
            }
            final String dataScale = argInfo.getDataScale();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(dataScale)) {
                columnMeta.setDecimalDigits(Integer.valueOf(dataScale));
            }
            procedureMeta.addProcedureColumn(columnMeta);
        }

        // Overload, Array, Struct are unsupported for DBLink
        // you should refactor the process if you support them

        return procedureMeta;
    }
}
