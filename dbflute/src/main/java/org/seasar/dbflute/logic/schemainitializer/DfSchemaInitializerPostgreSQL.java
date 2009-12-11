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
package org.seasar.dbflute.logic.schemainitializer;

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
        final List<String> sequenceNameList = new ArrayList<String>();
        final DfJdbcFacade jdbcFacade = new DfJdbcFacade(_dataSource);
        final String schema = _schema != null && _schema.trim().length() > 0 ? _schema : "public";
        final String sequenceColumnName = "sequence_name";
        final StringBuilder sb = new StringBuilder();
        sb.append("select ").append(sequenceColumnName).append(" from information_schema.sequences");
        sb.append(" where sequence_schema = '").append(schema).append("'");
        final List<Map<String, String>> resultList = jdbcFacade.selectStringList(sb.toString(), Arrays
                .asList(sequenceColumnName));
        for (Map<String, String> recordMap : resultList) {
            sequenceNameList.add(recordMap.get(sequenceColumnName));
        }
        for (String sequenceName : sequenceNameList) {
            final String dropSequenceSql = "drop sequence " + schema + "." + sequenceName;
            _log.info(dropSequenceSql);
            jdbcFacade.execute(dropSequenceSql);
        }
    }

    // ===================================================================================
    //                                                                      Drop Procedure
    //                                                                      ==============
    @Override
    protected DfDropProcedureByJdbcCallback createDropProcedureByJdbcCallback() {
        return new DfDropProcedureByJdbcCallback() {
            public String buildDropProcedureSql(DfProcedureMetaInfo metaInfo) {
                final String expression = buildProcedureArgExpression(metaInfo);
                return "drop procedure " + metaInfo.getProcedureSqlName() + "(" + expression + ")";
            }

            public String buildDropFunctionSql(DfProcedureMetaInfo metaInfo) {
                final String expression = buildProcedureArgExpression(metaInfo);
                return "drop function " + metaInfo.getProcedureSqlName() + "(" + expression + ")";
            }
        };
    }

    protected String buildProcedureArgExpression(DfProcedureMetaInfo metaInfo) {
        final List<DfProcedureColumnMetaInfo> metaInfoList = metaInfo.getProcedureColumnMetaInfoList();
        final StringBuilder sb = new StringBuilder();
        for (DfProcedureColumnMetaInfo columnMetaInfo : metaInfoList) {
            final String dbTypeName = columnMetaInfo.getDbTypeName();
            final String columnName = columnMetaInfo.getColumnName();
            final DfProcedureColumnType columnType = columnMetaInfo.getProcedureColumnType();
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