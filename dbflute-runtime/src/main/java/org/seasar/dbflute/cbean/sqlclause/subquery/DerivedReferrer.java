package org.seasar.dbflute.cbean.sqlclause.subquery;

import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseH2;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseMySql;
import org.seasar.dbflute.cbean.sqlclause.SqlClausePostgreSql;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseSqlServer;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

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
            String subQueryIdentity, DBMeta subQueryDBMeta, String mainSubQueryIdentity) {
        super(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQuerySqlClause,
                subQueryIdentity, subQueryDBMeta);
        _mainSubQueryIdentity = mainSubQueryIdentity;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    public String buildDerivedReferrer(String function, String correlatedColumnDbName, String relatedColumnDbName,
            DerivedReferrerOption option) {
        setupOptionAttribute(option);
        final ColumnRealName correlatedColumnRealName = _localRealNameProvider.provide(correlatedColumnDbName);
        final ColumnSqlName relatedColumnSqlName = _subQuerySqlNameProvider.provide(relatedColumnDbName);
        final String subQueryClause = getSubQueryClause(function, correlatedColumnRealName, relatedColumnSqlName,
                option);
        final String beginMark = resolveSubQueryBeginMark(_subQueryIdentity) + ln();
        final String endMark = resolveSubQueryEndMark(_subQueryIdentity);
        final String endIndent = "       ";
        return doBuildDerivedReferrer(function, correlatedColumnRealName, relatedColumnSqlName, subQueryClause,
                beginMark, endMark, endIndent);
    }

    protected void setupOptionAttribute(DerivedReferrerOption option) {
        option.setTargetColumnInfo(_subQuerySqlClause.getSpecifiedColumnInfoAsOne());
        option.setDatabaseMySQL(_subQuerySqlClause instanceof SqlClauseMySql);
        option.setDatabasePostgreSQL(_subQuerySqlClause instanceof SqlClausePostgreSql);
        option.setDatabaseSQLServer(_subQuerySqlClause instanceof SqlClauseSqlServer);
        option.setDatabaseH2(_subQuerySqlClause instanceof SqlClauseH2);
    }

    protected abstract String doBuildDerivedReferrer(String function, ColumnRealName columnRealName,
            ColumnSqlName relatedColumnSqlName, String subQueryClause, String beginMark, String endMark,
            String endIndent);

    protected String getSubQueryClause(String function, ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName, DerivedReferrerOption option) {
        if (!_subQueryDBMeta.hasPrimaryKey() || _subQueryDBMeta.hasCompoundPrimaryKey()) {
            String msg = "The derived-referrer is unsupported when no primary key or compound primary key:";
            msg = msg + " table=" + _subQueryDBMeta.getTableDbName();
            throw new IllegalConditionBeanOperationException(msg);
        }
        final String tableAliasName = getSubQueryLocalAliasName();
        final ColumnSqlName derivedColumnSqlName = getDerivedColumnSqlName();
        if (derivedColumnSqlName == null) {
            throwDerivedReferrerInvalidColumnSpecificationException(function);
        }
        final ColumnRealName derivedColumnRealName = getDerivedColumnRealName();
        _subQuerySqlClause.clearSpecifiedSelectColumn(); // specified columns disappear at this timing
        final String subQueryClause;
        if (_subQuerySqlClause.hasUnionQuery()) {
            subQueryClause = getUnionSubQueryClause(function, correlatedColumnRealName, relatedColumnSqlName, option,
                    tableAliasName, derivedColumnRealName, derivedColumnSqlName);
        } else {
            final String selectClause = "select " + buildFunctionPart(function, derivedColumnRealName, option);
            final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, tableAliasName,
                    correlatedColumnRealName, relatedColumnSqlName);
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
            if (nestedSubQuery == null) {
                return null;
            } else {
                return new ColumnSqlName(nestedSubQuery);
            }
        }
    }

    protected ColumnRealName getDerivedColumnRealName() {
        final ColumnRealName specifiedColumn = _subQuerySqlClause.getSpecifiedColumnRealNameAsOne();
        if (specifiedColumn != null) {
            return specifiedColumn;
        } else {
            final String nestedSubQuery = _subQuerySqlClause.getSpecifiedDerivingSubQueryAsOne();
            if (nestedSubQuery == null) {
                return null; // checked before
            } else {
                return new ColumnRealName(null, new ColumnSqlName(nestedSubQuery));
            }
        }
    }

    protected String getUnionSubQueryClause(String function, ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName, DerivedReferrerOption option, String tableAliasName,
            ColumnRealName derivedColumnRealName, ColumnSqlName derivedColumnSqlName) {
        final String beginMark = resolveSubQueryBeginMark(_mainSubQueryIdentity) + ln();
        final String endMark = resolveSubQueryEndMark(_mainSubQueryIdentity);
        final String mainSql;
        {
            final ColumnSqlName pkSqlName = _subQueryDBMeta.getPrimaryUniqueInfo().getFirstColumn().getColumnSqlName();
            final ColumnRealName pkRealName = new ColumnRealName(tableAliasName, pkSqlName);
            final ColumnRealName relRealName = new ColumnRealName(tableAliasName, relatedColumnSqlName);
            final String selectClause = "select " + pkRealName + ", " + relRealName + ", " + derivedColumnRealName;
            final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName);
            mainSql = selectClause + " " + fromWhereClause;
        }
        final String mainAlias = buildSubQueryMainAliasName();
        final String joinCondition = mainAlias + "." + relatedColumnSqlName + " = " + correlatedColumnRealName;
        final ColumnRealName mainDerivedColumnRealName = new ColumnRealName(mainAlias, derivedColumnSqlName);
        return "select " + buildFunctionPart(function, mainDerivedColumnRealName, option) + ln() + "  from ("
                + beginMark + mainSql + ln() + "       ) " + mainAlias + endMark + ln() + " where " + joinCondition;
    }

    protected String buildFunctionPart(String function, ColumnRealName columnRealName, DerivedReferrerOption option) {
        final String connector = buildFunctionConnector(function);
        final String columnWithEndExp;
        {
            final String specifiedExp = columnRealName.toString();
            final String dummyAlias = " as " + _subQuerySqlClause.getDerivedReferrerNestedAlias();
            if (specifiedExp.contains(dummyAlias)) {
                columnWithEndExp = replace(specifiedExp, dummyAlias, ")");
            } else {
                columnWithEndExp = specifiedExp + ")";
            }
        }
        final String functionExp = function + connector + columnWithEndExp;
        return option.filterFunction(functionExp);
    }

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
