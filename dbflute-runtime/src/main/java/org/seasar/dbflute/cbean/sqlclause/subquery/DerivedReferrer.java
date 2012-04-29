package org.seasar.dbflute.cbean.sqlclause.subquery;

import java.util.List;

import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;

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
        ColumnInfo columnInfo = _subQuerySqlClause.getSpecifiedColumnInfoAsOne();
        if (columnInfo == null) {
            columnInfo = _subQuerySqlClause.getSpecifiedDerivingColumnInfoAsOne();
        }
        option.xsetTargetColumnInfo(columnInfo); // basically not null (checked before)
        option.xjudgeDatabase(_subQuerySqlClause);
    }

    protected abstract String doBuildDerivedReferrer(String function, ColumnRealName columnRealName,
            ColumnSqlName relatedColumnSqlName, String subQueryClause, String beginMark, String endMark,
            String endIndent);

    protected String getSubQueryClause(String function, ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName, DerivedReferrerOption option) {
        final String tableAliasName = getSubQueryLocalAliasName();
        final ColumnSqlName derivedColumnSqlName = getDerivedColumnSqlName();
        if (derivedColumnSqlName == null) {
            throwDerivedReferrerInvalidColumnSpecificationException(function);
        }
        final ColumnRealName derivedColumnRealName = getDerivedColumnRealName();
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

    protected String getUnionSubQueryClause(String function, ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName, DerivedReferrerOption option, String tableAliasName,
            ColumnRealName derivedColumnRealName, ColumnSqlName derivedColumnSqlName) {
        final String beginMark = resolveSubQueryBeginMark(_mainSubQueryIdentity) + ln();
        final String endMark = resolveSubQueryEndMark(_mainSubQueryIdentity);
        final String mainSql;
        {
            final List<ColumnInfo> pkList = _subQueryDBMeta.getPrimaryUniqueInfo().getUniqueColumnList();
            final StringBuilder pkSb = new StringBuilder();
            for (ColumnInfo pk : pkList) {
                final ColumnSqlName pkSqlName = pk.getColumnSqlName();
                if (pkSqlName.equals(derivedColumnRealName.getColumnSqlName())
                        || pkSqlName.equals(relatedColumnSqlName)) {
                    // to suppress same columns selected
                    continue;
                }
                if (pkSb.length() > 0) {
                    pkSb.append(", ");
                }
                pkSb.append(ColumnRealName.create(tableAliasName, pk.getColumnSqlName()));
            }
            final String pkExp = pkSb.length() > 0 ? pkSb.toString() + ", " : "";
            final ColumnRealName relRealName = ColumnRealName.create(tableAliasName, relatedColumnSqlName);
            final String selectClause = "select " + pkExp + relRealName + ", " + derivedColumnRealName;
            final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName);
            mainSql = selectClause + " " + fromWhereClause;
        }
        final String mainAlias = buildSubQueryMainAliasName();
        final String joinCondition = mainAlias + "." + relatedColumnSqlName + " = " + correlatedColumnRealName;
        final ColumnRealName mainDerivedColumnRealName = ColumnRealName.create(mainAlias, derivedColumnSqlName);
        return "select " + buildFunctionPart(function, mainDerivedColumnRealName, option) + ln() // select
                + "  from (" + beginMark + mainSql + ln() + "       ) " + mainAlias + endMark + ln() // from
                + " where " + joinCondition; // where
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
