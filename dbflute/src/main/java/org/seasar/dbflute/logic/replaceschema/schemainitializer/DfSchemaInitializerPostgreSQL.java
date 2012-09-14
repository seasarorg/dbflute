/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.replaceschema.schemainitializer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta.DfProcedureColumnType;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMeta;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfSchemaInitializerPostgreSQL extends DfSchemaInitializerJdbc {

    // ===================================================================================
    //                                                                       Drop Sequence
    //                                                                       =============
    @Override
    protected void dropSequence(Connection conn, List<DfTableMeta> tableMetaList) {
        final String catalog = _unifiedSchema.existsPureCatalog() ? _unifiedSchema.getPureCatalog() : null;
        final String schema = _unifiedSchema.getPureSchema();
        final List<String> sequenceNameList = new ArrayList<String>();
        final DfJdbcFacade jdbcFacade = new DfJdbcFacade(conn);
        final String sequenceColumnName = "sequence_name";
        final StringBuilder sb = new StringBuilder();
        sb.append("select ").append(sequenceColumnName).append(" from information_schema.sequences");
        sb.append(" where ");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(catalog)) {
            sb.append("sequence_catalog = '").append(catalog).append("'").append(" and ");
        }
        sb.append("sequence_schema = '").append(schema).append("'");
        final List<Map<String, String>> resultList = jdbcFacade.selectStringList(sb.toString(),
                Arrays.asList(sequenceColumnName));
        for (Map<String, String> recordMap : resultList) {
            sequenceNameList.add(recordMap.get(sequenceColumnName));
        }
        for (String sequenceName : sequenceNameList) {
            final String sequenceSqlName = _unifiedSchema.buildSqlName(sequenceName);
            final String dropSequenceSql = "drop sequence " + sequenceSqlName;
            logReplaceSql(dropSequenceSql);
            jdbcFacade.execute(dropSequenceSql);
        }
    }

    // ===================================================================================
    //                                                                      Drop Procedure
    //                                                                      ==============
    @Override
    protected String buildProcedureSqlName(DfProcedureMeta metaInfo) {
        final String expression = "(" + buildProcedureArgExpression(metaInfo) + ")";
        return super.buildProcedureSqlName(metaInfo) + expression;
    }

    @Override
    protected boolean isDropFunctionFirst() {
        return true; // because PostgreSQL supports function only
    }

    protected String buildProcedureArgExpression(DfProcedureMeta metaInfo) {
        final List<DfProcedureColumnMeta> metaInfoList = metaInfo.getProcedureColumnList();
        final StringBuilder sb = new StringBuilder();
        for (DfProcedureColumnMeta columnMetaInfo : metaInfoList) {
            final String dbTypeName = columnMetaInfo.getDbTypeName();
            final String columnName = columnMetaInfo.getColumnName();
            final DfProcedureColumnType columnType = columnMetaInfo.getProcedureColumnType();
            if (DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(columnName);
            if (DfProcedureColumnType.procedureColumnIn.equals(columnType)) {
                sb.append(" in ");
            } else if (DfProcedureColumnType.procedureColumnOut.equals(columnType)) {
                sb.append(" out ");
            } else if (DfProcedureColumnType.procedureColumnInOut.equals(columnType)) {
                sb.append(" inout ");
            } else {
                sb.append(" ");
            }
            sb.append(dbTypeName);
        }
        return sb.toString();
    }
}