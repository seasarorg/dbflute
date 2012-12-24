/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean.sqlclause.subquery;

import java.util.List;

import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class ExistsReferrer extends AbstractSubQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ExistsReferrer(SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQuerySqlClause,
            String subQueryIdentity, DBMeta subQueryDBMeta, GearedCipherManager cipherManager) {
        super(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQuerySqlClause,
                subQueryIdentity, subQueryDBMeta, cipherManager);
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    /**
     * Build the clause of sub-query by single primary key.
     * @param correlatedColumnDbName The DB name of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnDbName The DB name of related column that is sub-query table's column. (NotNull)
     * @param correlatedFixedCondition The fixed condition as correlated condition. (NullAllowed)
     * @param existsOption The option of ExistsReferrer. (basically for NotExistsReferrer) (NullAllowed: if null, means ExistsReferrer)
     * @return The clause of sub-query. (NotNull)
     */
    public String buildExistsReferrer(String correlatedColumnDbName, String relatedColumnDbName,
            String correlatedFixedCondition, String existsOption) {
        existsOption = existsOption != null ? existsOption + " " : "";
        final String subQueryClause;
        if (correlatedColumnDbName.contains(",") && relatedColumnDbName.contains(",")) {
            // compound primary keys
            final List<String> relatedColumnSplit = Srl.splitList(relatedColumnDbName, ",");
            final ColumnSqlName[] relatedColumnSqlNames = new ColumnSqlName[relatedColumnSplit.size()];
            for (int i = 0; i < relatedColumnSplit.size(); i++) {
                relatedColumnSqlNames[i] = _subQuerySqlNameProvider.provide(relatedColumnSplit.get(i).trim());
            }
            final List<String> columnDbNameSplit = Srl.splitList(correlatedColumnDbName, ",");
            final ColumnRealName[] correlatedColumnRealNames = new ColumnRealName[columnDbNameSplit.size()];
            for (int i = 0; i < columnDbNameSplit.size(); i++) {
                correlatedColumnRealNames[i] = _localRealNameProvider.provide(columnDbNameSplit.get(i).trim());
            }
            subQueryClause = getSubQueryClause(correlatedColumnRealNames, relatedColumnSqlNames,
                    correlatedFixedCondition);
        } else {
            // single primary key
            final ColumnSqlName relatedColumnSqlName = _subQuerySqlNameProvider.provide(relatedColumnDbName);
            final ColumnRealName correlatedColumnRealName = _localRealNameProvider.provide(correlatedColumnDbName);
            subQueryClause = getSubQueryClause(correlatedColumnRealName, relatedColumnSqlName, correlatedFixedCondition);
        }
        final String beginMark = resolveSubQueryBeginMark(_subQueryIdentity) + ln();
        final String endMark = resolveSubQueryEndMark(_subQueryIdentity);
        final String endIndent = "       ";
        return existsOption + "exists (" + beginMark + subQueryClause + ln() + endIndent + ")" + endMark;
    }

    /**
     * Build the clause of sub-query by single primary key.
     * @param correlatedColumnRealName The real name of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlName The real name of related column that is sub-query table's column. (NotNull)
     * @param correlatedFixedCondition The fixed condition as correlated condition. (NullAllowed)
     * @return The clause of sub-query. (NotNull)
     */
    protected String getSubQueryClause(ColumnRealName correlatedColumnRealName, ColumnSqlName relatedColumnSqlName,
            String correlatedFixedCondition) {
        final String localAliasName = getSubQueryLocalAliasName();
        final String selectClause;
        {
            final ColumnRealName relatedColumnRealName = ColumnRealName.create(localAliasName, relatedColumnSqlName);
            selectClause = "select " + relatedColumnRealName;
        }
        final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, localAliasName,
                correlatedColumnRealName, relatedColumnSqlName, correlatedFixedCondition);
        final String subQueryClause = selectClause + " " + fromWhereClause;
        return resolveSubQueryLevelVariable(subQueryClause);
    }

    /**
     * Build the clause of sub-query by compound primary key.
     * @param correlatedColumnRealNames The real names of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlNames The real names of related column that is sub-query table's column. (NotNull)
     * @param correlatedFixedCondition The fixed condition as correlated condition. (NullAllowed)
     * @return The clause of sub-query. (NotNull)
     */
    protected String getSubQueryClause(ColumnRealName[] correlatedColumnRealNames,
            ColumnSqlName[] relatedColumnSqlNames, String correlatedFixedCondition) {
        final String localAliasName = getSubQueryLocalAliasName();
        final String selectClause;
        {
            // because sub-query may be only allowed to return a single column.
            final ColumnRealName relatedColumnRealName = ColumnRealName
                    .create(localAliasName, relatedColumnSqlNames[0]);
            selectClause = "select " + relatedColumnRealName;
        }
        final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, localAliasName,
                correlatedColumnRealNames, relatedColumnSqlNames, correlatedFixedCondition);
        final String subQueryClause = selectClause + " " + fromWhereClause;
        return resolveSubQueryLevelVariable(subQueryClause);
    }
}
