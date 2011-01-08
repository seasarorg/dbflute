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
package org.seasar.dbflute.logic.replaceschema.schemainitializer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfSchemaInitializerPostgreSQL extends DfSchemaInitializerJdbc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSchemaInitializerPostgreSQL.class);

    // ===================================================================================
    //                                                                       Drop Sequence
    //                                                                       =============
    @Override
    protected void dropSequence(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
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
        final List<Map<String, String>> resultList = jdbcFacade.selectStringList(sb.toString(), Arrays
                .asList(sequenceColumnName));
        for (Map<String, String> recordMap : resultList) {
            sequenceNameList.add(recordMap.get(sequenceColumnName));
        }
        for (String sequenceName : sequenceNameList) {
            final String sequenceSqlName = _unifiedSchema.buildSqlName(sequenceName);
            final String dropSequenceSql = "drop sequence " + sequenceSqlName;
            _log.info(dropSequenceSql);
            jdbcFacade.execute(dropSequenceSql);
        }
    }

    // ===================================================================================
    //                                                                      Drop Procedure
    //                                                                      ==============
    @Override
    protected String buildProcedureSqlName(DfProcedureMetaInfo metaInfo) {
        final String expression = "(" + buildProcedureArgExpression(metaInfo) + ")";
        return super.buildProcedureSqlName(metaInfo) + expression;
    }

    protected String buildProcedureArgExpression(DfProcedureMetaInfo metaInfo) {
        final List<DfProcedureColumnMetaInfo> metaInfoList = metaInfo.getProcedureColumnList();
        final StringBuilder sb = new StringBuilder();
        for (DfProcedureColumnMetaInfo columnMetaInfo : metaInfoList) {
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