package org.seasar.dbflute.cbean.sqlclause.subquery;

import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseH2;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseMySql;
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
    public DerivedReferrer(SqlClause sqlClause, SubQueryPath subQueryPath,
            ColumnRealNameProvider localRealNameProvider, ColumnSqlNameProvider subQuerySqlNameProvider,
            int subQueryLevel, SqlClause subQueryClause, SubQueryLevelReflector reflector, String subQueryIdentity,
            DBMeta subQueryDBMeta, String mainSubQueryIdentity) {
        super(sqlClause, subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQueryClause,
                reflector, subQueryIdentity, subQueryDBMeta);
        _mainSubQueryIdentity = mainSubQueryIdentity;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    public String buildDerivedReferrer(String function, String columnDbName, String relatedColumnDbName,
            DerivedReferrerOption option) {
        reflectLocalSubQueryLevel();
        setupOptionAttribute(option);
        final ColumnRealName columnRealName = _localRealNameProvider.provide(columnDbName);
        final ColumnSqlName relatedColumnSqlName = _subQuerySqlNameProvider.provide(relatedColumnDbName);
        final String subQueryClause = getSubQueryClause(function, columnRealName, relatedColumnSqlName, option);
        final String beginMark = _sqlClause.resolveSubQueryBeginMark(_subQueryIdentity) + ln();
        final String endMark = _sqlClause.resolveSubQueryEndMark(_subQueryIdentity);
        final String endIndent = "       ";
        return doBuildDerivedReferrer(function, columnRealName, relatedColumnSqlName, subQueryClause, beginMark,
                endMark, endIndent);
    }

    protected void setupOptionAttribute(DerivedReferrerOption option) {
        option.setTargetColumnInfo(_subQueryClause.getSpecifiedColumnInfoAsOne());
        option.setDatabaseMySQL(_subQueryClause instanceof SqlClauseMySql);
        option.setDatabaseSQLServer(_subQueryClause instanceof SqlClauseSqlServer);
        option.setDatabaseH2(_subQueryClause instanceof SqlClauseH2);
    }

    protected abstract String doBuildDerivedReferrer(String function, ColumnRealName columnRealName,
            ColumnSqlName relatedColumnSqlName, String subQueryClause, String beginMark, String endMark,
            String endIndent);

    protected String getSubQueryClause(String function, ColumnRealName columnRealName,
            ColumnSqlName relatedColumnSqlName, DerivedReferrerOption option) {
        if (!_subQueryDBMeta.hasPrimaryKey() || _subQueryDBMeta.hasTwoOrMorePrimaryKeys()) {
            String msg = "The derived-referrer is unsupported when no primary key or two-or-more primary keys:";
            msg = msg + " table=" + _subQueryDBMeta.getTableDbName();
            throw new IllegalConditionBeanOperationException(msg);
        }
        final String tableAliasName = "dfsublocal_" + _subQueryLevel;
        final ColumnSqlName derivedColumnSqlName = _subQueryClause.getSpecifiedColumnSqlNameAsOne();
        if (derivedColumnSqlName == null) {
            throwDerivedReferrerInvalidColumnSpecificationException(function);
        }
        final ColumnRealName derivedColumnRealName;
        {
            final String specifiedColumnDbName = _subQueryClause.getSpecifiedColumnDbNameAsOne();
            final ColumnRealName specifiedColumnRealName = _subQueryClause.getSpecifiedColumnRealNameAsOne();
            if (!specifiedColumnRealName.getTableAliasName().equals(_subQueryClause.getLocalTableAliasName())) {
                // The column is on sub-query local table.
                derivedColumnRealName = specifiedColumnRealName;
            } else {
                // The column is on sub-query related table.
                derivedColumnRealName = new ColumnRealName(tableAliasName, derivedColumnSqlName);

                // Assert about column type when local table only.
                assertDerivedReferrerColumnType(function, specifiedColumnDbName);
            }
        }
        _subQueryClause.clearSpecifiedSelectColumn(); // specified columns disappear at this timing
        if (_subQueryClause.hasUnionQuery()) {
            return getUnionSubQueryClause(function, columnRealName, relatedColumnSqlName, option, tableAliasName,
                    derivedColumnRealName, derivedColumnSqlName);
        } else {
            final String selectClause = "select " + buildFunctionPart(function, derivedColumnRealName, option);
            final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, tableAliasName,
                    relatedColumnSqlName, columnRealName);
            return selectClause + " " + fromWhereClause;
        }
    }

    protected String getUnionSubQueryClause(String function, ColumnRealName columnRealName,
            ColumnSqlName relatedColumnSqlName, DerivedReferrerOption option, String tableAliasName,
            ColumnRealName derivedColumnRealName, ColumnSqlName derivedColumnSqlName) {
        final String beginMark = _sqlClause.resolveSubQueryBeginMark(_mainSubQueryIdentity) + ln();
        final String endMark = _sqlClause.resolveSubQueryEndMark(_mainSubQueryIdentity);
        final String mainSql;
        {
            final ColumnSqlName pkSqlName = _subQueryDBMeta.getPrimaryUniqueInfo().getFirstColumn().getColumnSqlName();
            final ColumnRealName pkRealName = new ColumnRealName(tableAliasName, pkSqlName);
            final ColumnRealName relRealName = new ColumnRealName(tableAliasName, relatedColumnSqlName);
            final String selectClause = "select " + pkRealName + ", " + relRealName + ", " + derivedColumnRealName;
            final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName);
            mainSql = selectClause + " " + fromWhereClause;
        }
        final String joinCondition = "dfsubquerymain." + relatedColumnSqlName + " = " + columnRealName;
        final ColumnRealName mainDerivedColumnRealName = new ColumnRealName("dfsubquerymain", derivedColumnSqlName);
        return "select " + buildFunctionPart(function, mainDerivedColumnRealName, option) + ln() + "  from ("
                + beginMark + mainSql + ln() + "       ) dfsubquerymain" + endMark + ln() + " where " + joinCondition;
    }

    protected String buildFunctionPart(String function, ColumnRealName columnRealName, DerivedReferrerOption option) {
        final String connector = buildFunctionConnector(function);
        final String functionExp = function + connector + columnRealName + ")";
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
