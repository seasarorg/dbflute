package org.seasar.dbflute.cbean.sqlclause.subquery;

import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public abstract class AbstractSubQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SqlClause _sqlClause;
    protected final SubQueryPath _subQueryPath;
    protected final ColumnRealNameProvider _localRealNameProvider;
    protected final ColumnSqlNameProvider _subQuerySqlNameProvider;
    protected final int _subQueryLevel;
    protected final SqlClause _subQueryClause;
    protected final String _subQueryIdentity;
    protected final DBMeta _subQueryDBMeta;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param sqlClause The SQL clause for local table. (NotNull)
     * @param subQueryPath The property path of sub-query. (NotNull)
     * @param localRealNameProvider The provider of column real name for local table. (NotNull)
     * @param subQuerySqlNameProvider The provider of column real name for sub-query. (NotNull)
     * @param subQueryLevel The sub-query level for sub-query.
     * @param subQueryClause The SQL clause for sub-query. (NotNull)
     * @param subQueryIdentity The identity string for sub-query. (NotNull)
     * @param subQueryDBMeta The DB meta for sub-query. (NotNull)
     */
    public AbstractSubQuery(SqlClause sqlClause, SubQueryPath subQueryPath,
            ColumnRealNameProvider localRealNameProvider, ColumnSqlNameProvider subQuerySqlNameProvider,
            int subQueryLevel, SqlClause subQueryClause, String subQueryIdentity, DBMeta subQueryDBMeta) {
        _sqlClause = sqlClause;
        _subQueryPath = subQueryPath;
        _localRealNameProvider = localRealNameProvider;
        _subQuerySqlNameProvider = subQuerySqlNameProvider;
        _subQueryLevel = subQueryLevel;
        _subQueryClause = subQueryClause;
        _subQueryIdentity = subQueryIdentity;
        _subQueryDBMeta = subQueryDBMeta;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    protected String buildLocalTableAliasName() {
        // this name must not contain "dflocal"
        // because the word is replaced later in mistake
        return "dfsub" + _subQueryLevel + "local";
    }

    protected String buildPlainFromWhereClause(String selectClause, String tableAliasName) {
        final SubQueryClause clause = createSubQueryClause(selectClause, tableAliasName);
        return clause.buildPlainSubQueryFromWhereClause();
    }

    protected String buildCorrelationFromWhereClause(String selectClause, String tableAliasName,
            ColumnSqlName relatedColumnSqlName, ColumnRealName correlatedColumnRealName) {
        final SubQueryClause clause = createSubQueryClause(selectClause, tableAliasName);
        return clause.buildCorrelationSubQueryFromWhereClause(relatedColumnSqlName, correlatedColumnRealName);
    }

    protected String buildCorrelationFromWhereClause(String selectClause, String tableAliasName,
            ColumnSqlName[] relatedColumnSqlNames, ColumnRealName[] correlatedColumnRealNames) {
        final SubQueryClause clause = createSubQueryClause(selectClause, tableAliasName);
        return clause.buildCorrelationSubQueryFromWhereClause(relatedColumnSqlNames, correlatedColumnRealNames);
    }

    protected SubQueryClause createSubQueryClause(String selectClause, String tableAliasName) {
        return new SubQueryClause(_sqlClause, _subQueryPath, selectClause, _subQueryClause, tableAliasName);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected final String initCap(String str) {
        return Srl.initCap(str);
    }

    protected final String initUncap(String str) {
        return Srl.initUncap(str);
    }

    protected final String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
