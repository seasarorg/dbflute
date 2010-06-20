package org.seasar.dbflute.cbean.sqlclause.subquery;

import org.seasar.dbflute.cbean.sqlclause.SqlClause;
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
public class ScalarSubQuery extends AbstractSubQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _mainSubQueryIdentity;

    protected final String _operand;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ScalarSubQuery(SqlClause sqlClause, SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQueryClause,
            SubQueryLevelReflector reflector, String subQueryIdentity, DBMeta subQueryDBMeta,
            String mainSubQueryIdentity, String operand) {
        super(sqlClause, subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQueryClause,
                reflector, subQueryIdentity, subQueryDBMeta);
        _mainSubQueryIdentity = mainSubQueryIdentity;
        _operand = operand;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    public String buildScalarSubQuery(String function) {
        reflectLocalSubQueryLevel();

        // Get the specified column before it disappears at sub-query making.
        final ColumnRealName columnRealName;
        {
            final String columnDbName = _subQueryClause.getSpecifiedColumnDbNameAsOne();
            if (columnDbName == null || columnDbName.trim().length() == 0) {
                throwScalarSubQueryInvalidColumnSpecificationException(function);
            }
            columnRealName = _localRealNameProvider.provide(columnDbName);
        }

        final String subQueryClause = getSubQuerySql(function);
        final String beginMark = _sqlClause.resolveSubQueryBeginMark(_subQueryIdentity) + ln();
        final String endMark = _sqlClause.resolveSubQueryEndMark(_subQueryIdentity);
        final String endIndent = "       ";
        return columnRealName + " " + _operand + " (" + beginMark + subQueryClause + ln() + endIndent + ") " + endMark;
    }

    protected String getSubQuerySql(String function) {
        if (!_subQueryDBMeta.hasPrimaryKey() || _subQueryDBMeta.hasTwoOrMorePrimaryKeys()) {
            String msg = "The scalar-condition is unsupported when no primary key or two-or-more primary keys:";
            msg = msg + " table=" + _subQueryDBMeta.getTableDbName();
            throw new IllegalConditionBeanOperationException(msg);
        }
        final String tableAliasName = "dfsublocal_" + _subQueryLevel;
        final String derivedColumnDbName = _subQueryClause.getSpecifiedColumnDbNameAsOne();
        if (derivedColumnDbName == null) {
            throwScalarSubQueryInvalidColumnSpecificationException(function);
        }
        final ColumnSqlName derivedColumnSqlName = _subQueryClause.getSpecifiedColumnSqlNameAsOne();
        final ColumnRealName derivedColumnRealName = new ColumnRealName(tableAliasName, derivedColumnSqlName);
        assertScalarSubQueryColumnType(function, derivedColumnDbName);
        _subQueryClause.clearSpecifiedSelectColumn(); // specified columns disappear at this timing
        if (_subQueryClause.hasUnionQuery()) {
            return getUnionSubQuerySql(function, tableAliasName, derivedColumnSqlName, derivedColumnRealName);
        } else {
            final String selectClause = "select " + function + "(" + derivedColumnRealName + ")";
            final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName);
            return selectClause + " " + fromWhereClause;
        }
    }

    protected String getUnionSubQuerySql(String function, String tableAliasName, ColumnSqlName derivedColumnSqlName,
            ColumnRealName derivedColumnRealName) {
        final String beginMark = _sqlClause.resolveSubQueryBeginMark(_mainSubQueryIdentity) + ln();
        final String endMark = _sqlClause.resolveSubQueryEndMark(_mainSubQueryIdentity);
        final String mainSql;
        {
            final ColumnSqlName pkSqlName = _subQueryDBMeta.getPrimaryUniqueInfo().getFirstColumn().getColumnSqlName();
            final ColumnRealName pkRealName = new ColumnRealName(tableAliasName, pkSqlName);
            final String selectClause = "select " + pkRealName + ", " + derivedColumnRealName;
            final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName);
            mainSql = selectClause + " " + fromWhereClause;
        }
        final ColumnRealName mainDerivedColumnRealName = new ColumnRealName("dfsubquerymain", derivedColumnSqlName);
        return "select " + function + "(" + mainDerivedColumnRealName + ")" + ln() + "  from (" + beginMark + mainSql
                + ln() + "       ) dfsubquerymain" + endMark;
    }

    protected void throwScalarSubQueryInvalidColumnSpecificationException(String function) {
        createCBExThrower().throwScalarSubQueryInvalidColumnSpecificationException(function);
    }

    protected void assertScalarSubQueryColumnType(String function, String derivedColumnDbName) {
        final Class<?> deriveColumnType = _subQueryDBMeta.findColumnInfo(derivedColumnDbName).getPropertyType();
        if ("sum".equalsIgnoreCase(function) || "avg".equalsIgnoreCase(function)) {
            if (!Number.class.isAssignableFrom(deriveColumnType)) {
                throwScalarSubQueryUnmatchedColumnTypeException(function, derivedColumnDbName, deriveColumnType);
            }
        }
    }

    protected void throwScalarSubQueryUnmatchedColumnTypeException(String function, String derivedColumnDbName,
            Class<?> derivedColumnType) {
        createCBExThrower().throwScalarSubQueryUnmatchedColumnTypeException(function, derivedColumnDbName,
                derivedColumnType);
    }
}
