/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public abstract class DerivedReferrer extends AbstractSubQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _mainSubQueryIdentity;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DerivedReferrer(SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQuerySqlClause,
            String subQueryIdentity, DBMeta subQueryDBMeta, GearedCipherManager cipherManager,
            String mainSubQueryIdentity) {
        super(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQuerySqlClause,
                subQueryIdentity, subQueryDBMeta, cipherManager);
        _mainSubQueryIdentity = mainSubQueryIdentity;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    public String buildDerivedReferrer(String function, String correlatedColumnDbName, String relatedColumnDbName,
            String correlatedFixedCondition, DerivedReferrerOption option) {
        setupOptionAttribute(option);
        if (isSinglePrimaryKey(correlatedColumnDbName, relatedColumnDbName)) {
            final ColumnRealName correlatedColumnRealName = _localRealNameProvider.provide(correlatedColumnDbName);
            final ColumnSqlName relatedColumnSqlName = _subQuerySqlNameProvider.provide(relatedColumnDbName);
            final String subQueryClause = buildSubQueryClause(function, correlatedColumnRealName, relatedColumnSqlName,
                    correlatedFixedCondition, option);
            final String beginMark = resolveSubQueryBeginMark(_subQueryIdentity) + ln();
            final String endMark = resolveSubQueryEndMark(_subQueryIdentity);
            final String endIndent = "       ";
            return doBuildDerivedReferrer(function, correlatedColumnRealName, relatedColumnSqlName, subQueryClause,
                    beginMark, endMark, endIndent);
        } else {
            final List<String> columnDbNameSplit = Srl.splitListTrimmed(correlatedColumnDbName, ",");
            final ColumnRealName[] correlatedColumnRealNames = new ColumnRealName[columnDbNameSplit.size()];
            for (int i = 0; i < columnDbNameSplit.size(); i++) {
                correlatedColumnRealNames[i] = _localRealNameProvider.provide(columnDbNameSplit.get(i));
            }
            final List<String> relatedColumnSplit = Srl.splitListTrimmed(relatedColumnDbName, ",");
            final ColumnSqlName[] relatedColumnSqlNames = new ColumnSqlName[relatedColumnSplit.size()];
            for (int i = 0; i < relatedColumnSplit.size(); i++) {
                relatedColumnSqlNames[i] = _subQuerySqlNameProvider.provide(relatedColumnSplit.get(i));
            }
            final String subQueryClause = getSubQueryClause(function, correlatedColumnRealNames, relatedColumnSqlNames,
                    correlatedFixedCondition, option);
            final String beginMark = resolveSubQueryBeginMark(_subQueryIdentity) + ln();
            final String endMark = resolveSubQueryEndMark(_subQueryIdentity);
            final String endIndent = "       ";
            return doBuildDerivedReferrer(function, correlatedColumnRealNames, relatedColumnSqlNames, subQueryClause,
                    beginMark, endMark, endIndent);
        }
    }

    protected void setupOptionAttribute(DerivedReferrerOption option) {
        ColumnInfo columnInfo = _subQuerySqlClause.getSpecifiedColumnInfoAsOne();
        if (columnInfo == null) {
            columnInfo = _subQuerySqlClause.getSpecifiedDerivingColumnInfoAsOne();
        }
        option.xsetTargetColumnInfo(columnInfo); // basically not null (checked before)
        option.xjudgeDatabase(_subQuerySqlClause);
    }

    protected abstract String doBuildDerivedReferrer(String function, ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName, String subQueryClause, String beginMark, String endMark,
            String endIndent);

    protected abstract String doBuildDerivedReferrer(String function, ColumnRealName[] correlatedColumnRealNames,
            ColumnSqlName[] relatedColumnSqlNames, String subQueryClause, String beginMark, String endMark,
            String endIndent);

    // ===================================================================================
    //                                                                     SubQuery Clause
    //                                                                     ===============
    // -----------------------------------------------------
    //                                     Single PrimaryKey
    //                                     -----------------
    /**
     * Build the clause of sub-query by single primary key.
     * @param function The expression for deriving function. (NotNull)
     * @param correlatedColumnRealName The real names of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlName The real names of related column that is sub-query table's column. (NotNull)
     * @param correlatedFixedCondition The fixed condition as correlated condition. (NullAllowed)
     * @param option The option of DerivedReferrer. (NotNull)
     * @return The clause of sub-query. (NotNull)
     */
    protected String buildSubQueryClause(String function, ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName, String correlatedFixedCondition, DerivedReferrerOption option) {
        final String tableAliasName = getSubQueryLocalAliasName();
        final ColumnSqlName derivedColumnSqlName = getDerivedColumnSqlName();
        if (derivedColumnSqlName == null) {
            throwDerivedReferrerInvalidColumnSpecificationException(function);
        }
        final ColumnRealName derivedColumnRealName = getDerivedColumnRealName();
        final String subQueryClause;
        if (_subQuerySqlClause.hasUnionQuery()) {
            subQueryClause = buildUnionSubQueryClause(function, correlatedColumnRealName, relatedColumnSqlName, option,
                    tableAliasName, derivedColumnRealName, derivedColumnSqlName);
        } else {
            final String selectClause = "select " + buildFunctionPart(function, derivedColumnRealName, option);
            final String fromWhereClause;
            if (option.isSuppressCorrelation()) { // e.g. myselfDerived
                fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName, correlatedFixedCondition);
            } else { // basically here
                fromWhereClause = buildCorrelationFromWhereClause(selectClause, tableAliasName,
                        correlatedColumnRealName, relatedColumnSqlName, correlatedFixedCondition);
            }
            subQueryClause = selectClause + " " + fromWhereClause;
        }
        return resolveSubQueryLevelVariable(subQueryClause);
    }

    protected ColumnSqlName getDerivedColumnSqlName() {
        final ColumnSqlName specifiedColumn = _subQuerySqlClause.getSpecifiedColumnSqlNameAsOne();
        if (specifiedColumn != null) {
            return specifiedColumn;
        } else {
            final String nestedSubQuery = _subQuerySqlClause.getSpecifiedDerivingSubQueryAsOne();
            if (nestedSubQuery != null) {
                return new ColumnSqlName(nestedSubQuery);
            } else {
                return null;
            }
        }
    }

    protected ColumnRealName getDerivedColumnRealName() {
        final ColumnRealName specifiedColumn = _subQuerySqlClause.getSpecifiedColumnRealNameAsOne();
        if (specifiedColumn != null) {
            return specifiedColumn;
        } else {
            final String nestedSubQuery = _subQuerySqlClause.getSpecifiedDerivingSubQueryAsOne();
            if (nestedSubQuery != null) {
                return ColumnRealName.create(null, new ColumnSqlName(nestedSubQuery));
            } else {
                return null;
            }
        }
    }

    protected String buildUnionSubQueryClause(String function, ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName, DerivedReferrerOption option, String tableAliasName,
            ColumnRealName derivedColumnRealName, ColumnSqlName derivedColumnSqlName) {
        final String mainSql = buildUnionMainPartClause(relatedColumnSqlName, tableAliasName, derivedColumnRealName);
        final String mainAlias = buildSubQueryMainAliasName();
        final String whereJoinCondition;
        if (option.isSuppressCorrelation()) { // e.g. myselfDerived
            whereJoinCondition = "";
        } else { // mainly here
            final ColumnRealName relatedColumnRealName = ColumnRealName.create(mainAlias, relatedColumnSqlName);
            whereJoinCondition = ln() + " where " + relatedColumnRealName + " = " + correlatedColumnRealName; // correlation
        }
        final ColumnRealName mainDerivedColumnRealName = ColumnRealName.create(mainAlias, derivedColumnSqlName);
        return doBuildUnionSubQueryClause(function, option, mainSql, mainAlias, whereJoinCondition,
                mainDerivedColumnRealName);
    }

    protected String buildUnionMainPartClause(ColumnSqlName relatedColumnSqlName, String tableAliasName,
            ColumnRealName derivedColumnRealName) {
        final String mainSql;
        {
            final ColumnSqlName derivedSqlName = derivedColumnRealName.getColumnSqlName();
            final List<ColumnInfo> pkList = _subQueryDBMeta.getPrimaryUniqueInfo().getUniqueColumnList();
            final StringBuilder keySb = new StringBuilder();
            for (ColumnInfo pk : pkList) {
                final ColumnSqlName pkSqlName = pk.getColumnSqlName();
                if (pkSqlName.equals(derivedSqlName) || pkSqlName.equals(relatedColumnSqlName)) {
                    continue; // to suppress same columns selected
                }
                if (keySb.length() > 0) {
                    keySb.append(", ");
                }
                keySb.append(ColumnRealName.create(tableAliasName, pk.getColumnSqlName()));
            }
            if (!relatedColumnSqlName.equals(derivedSqlName)) { // to suppress same columns selected
                if (keySb.length() > 0) {
                    keySb.append(", ");
                }
                keySb.append(ColumnRealName.create(tableAliasName, relatedColumnSqlName));
            }
            final String keyExp = keySb.length() > 0 ? keySb.toString() + ", " : "";
            final String selectClause = "select " + keyExp + derivedColumnRealName;
            final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName, null);
            mainSql = selectClause + " " + fromWhereClause;
        }
        return mainSql;
    }

    protected String doBuildUnionSubQueryClause(String function, DerivedReferrerOption option, String mainSql,
            String mainAlias, String whereJoinCondition, ColumnRealName mainDerivedColumnRealName) {
        final String beginMark = resolveSubQueryBeginMark(_mainSubQueryIdentity) + ln();
        final String endMark = resolveSubQueryEndMark(_mainSubQueryIdentity);
        return "select " + buildFunctionPart(function, mainDerivedColumnRealName, option) // select
                + ln() + "  from (" + beginMark + mainSql + ln() + "       ) " + mainAlias + endMark // from
                + whereJoinCondition; // where
    }

    protected String buildFunctionPart(String function, ColumnRealName columnRealName, DerivedReferrerOption option) {
        final String connector = buildFunctionConnector(function);
        final String columnWithEndExp;
        {
            final String specifiedExp = columnRealName.toString();
            final String dummyAlias = " as " + _subQuerySqlClause.getDerivedReferrerNestedAlias();
            if (specifiedExp.contains(dummyAlias)) { // means nested DerivedReferrer
                final String resolved = _subQueryPath.resolveParameterLocationPath(specifiedExp);
                columnWithEndExp = replace(resolved, dummyAlias, ")");
            } else {
                final ColumnInfo derivedColumnInfo = _subQuerySqlClause.getSpecifiedColumnInfoAsOne();
                columnWithEndExp = decrypt(derivedColumnInfo, specifiedExp) + ")";
            }
        }
        final String functionExp = function + connector + columnWithEndExp;
        return option.filterFunction(functionExp);
    }

    // -----------------------------------------------------
    //                                   Compound PrimaryKey
    //                                   -------------------
    /**
     * Build the clause of sub-query by compound primary key.
     * @param function The expression for deriving function. (NotNull)
     * @param correlatedColumnRealNames The real names of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlNames The real names of related column that is sub-query table's column. (NotNull)
     * @param correlatedFixedCondition The fixed condition as correlated condition. (NullAllowed)
     * @param option The option of DerivedReferrer. (NotNull)
     * @return The clause of sub-query. (NotNull)
     */
    protected String getSubQueryClause(String function, ColumnRealName[] correlatedColumnRealNames,
            ColumnSqlName[] relatedColumnSqlNames, String correlatedFixedCondition, DerivedReferrerOption option) {
        final String tableAliasName = getSubQueryLocalAliasName();
        final ColumnSqlName derivedColumnSqlName = getDerivedColumnSqlName();
        if (derivedColumnSqlName == null) {
            throwDerivedReferrerInvalidColumnSpecificationException(function);
        }
        final ColumnRealName derivedColumnRealName = getDerivedColumnRealName();
        final String subQueryClause;
        if (_subQuerySqlClause.hasUnionQuery()) {
            subQueryClause = buildUnionSubQueryClause(function, correlatedColumnRealNames, relatedColumnSqlNames,
                    option, tableAliasName, derivedColumnRealName, derivedColumnSqlName);
        } else {
            final String selectClause = "select " + buildFunctionPart(function, derivedColumnRealName, option);
            final String fromWhereClause;
            if (option.isSuppressCorrelation()) { // e.g. myselfDerived
                fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName, correlatedFixedCondition);
            } else { // basically here
                fromWhereClause = buildCorrelationFromWhereClause(selectClause, tableAliasName,
                        correlatedColumnRealNames, relatedColumnSqlNames, correlatedFixedCondition);
            }
            subQueryClause = selectClause + " " + fromWhereClause;
        }
        return resolveSubQueryLevelVariable(subQueryClause);
    }

    protected String buildUnionSubQueryClause(String function, ColumnRealName[] correlatedColumnRealNames,
            ColumnSqlName[] relatedColumnSqlNames, DerivedReferrerOption option, String tableAliasName,
            ColumnRealName derivedColumnRealName, ColumnSqlName derivedColumnSqlName) {
        final String mainSql = buildUnionMainPartClause(correlatedColumnRealNames, relatedColumnSqlNames,
                tableAliasName, derivedColumnRealName);
        final String mainAlias = buildSubQueryMainAliasName();
        final String whereJoinCondition;
        if (option.isSuppressCorrelation()) { // e.g. myselfDerived
            whereJoinCondition = "";
        } else { // mainly here
            final StringBuilder sb = new StringBuilder();
            sb.append(ln()).append(" where ");
            for (int i = 0; i < correlatedColumnRealNames.length; i++) { // correlation
                if (i > 0) {
                    sb.append(ln()).append("   and ");
                }
                sb.append(ColumnRealName.create(mainAlias, relatedColumnSqlNames[i]));
                sb.append(" = ").append(correlatedColumnRealNames[i]);
            }
            whereJoinCondition = sb.toString();
        }
        final ColumnRealName mainDerivedColumnRealName = ColumnRealName.create(mainAlias, derivedColumnSqlName);
        return doBuildUnionSubQueryClause(function, option, mainSql, mainAlias, whereJoinCondition,
                mainDerivedColumnRealName);
    }

    protected String buildUnionMainPartClause(ColumnRealName[] correlatedColumnRealNames,
            ColumnSqlName[] relatedColumnSqlNames, String tableAliasName, ColumnRealName derivedColumnRealName) {
        final String mainSql;
        {
            final Set<ColumnSqlName> relatedColumnSqlSet = new HashSet<ColumnSqlName>();
            for (ColumnSqlName columnSqlName : relatedColumnSqlNames) {
                relatedColumnSqlSet.add(columnSqlName);
            }
            final ColumnSqlName derivedSqlName = derivedColumnRealName.getColumnSqlName();
            final List<ColumnInfo> pkList = _subQueryDBMeta.getPrimaryUniqueInfo().getUniqueColumnList();
            final StringBuilder keySb = new StringBuilder();
            for (ColumnInfo pk : pkList) {
                final ColumnSqlName pkSqlName = pk.getColumnSqlName();
                if (pkSqlName.equals(derivedSqlName) || relatedColumnSqlSet.contains(pkSqlName)) {
                    continue; // to suppress same columns selected
                }
                if (keySb.length() > 0) {
                    keySb.append(", ");
                }
                keySb.append(ColumnRealName.create(tableAliasName, pk.getColumnSqlName()));
            }
            for (ColumnSqlName relatedSqlName : relatedColumnSqlNames) {
                if (relatedSqlName.equals(derivedSqlName)) {
                    continue; // to suppress same columns selected
                }
                if (keySb.length() > 0) {
                    keySb.append(", ");
                }
                keySb.append(ColumnRealName.create(tableAliasName, relatedSqlName));
            }
            final String keyExp = keySb.length() > 0 ? keySb.toString() + ", " : "";
            final String selectClause = "select " + keyExp + derivedColumnRealName;
            final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName, null);
            mainSql = selectClause + " " + fromWhereClause;
        }
        return mainSql;
    }

    // ===================================================================================
    //                                                                    Assert/Exception
    //                                                                    ================
    protected abstract void throwDerivedReferrerInvalidColumnSpecificationException(String function);

    protected void assertDerivedReferrerColumnType(String function, String derivedColumnDbName) {
        if (derivedColumnDbName.contains(".")) {
            derivedColumnDbName = derivedColumnDbName.substring(derivedColumnDbName.lastIndexOf(".") + ".".length());
        }
        final Class<?> derivedColumnType = _subQueryDBMeta.findColumnInfo(derivedColumnDbName).getPropertyType();
        doAssertDerivedReferrerColumnType(function, derivedColumnDbName, derivedColumnType);
    }

    protected abstract void doAssertDerivedReferrerColumnType(String function, String derivedColumnDbName,
            Class<?> deriveColumnType);

    // ===================================================================================
    //                                                                  Function Connector
    //                                                                  ==================
    protected String buildFunctionConnector(String function) {
        if (function != null && function.endsWith("(distinct")) { // for example 'count(distinct'
            return " ";
        } else {
            return "(";
        }
    }
}
