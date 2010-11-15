package org.seasar.dbflute.cbean.sqlclause.subquery;

import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class SubQueryClause {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SqlClause _sqlClause;
    protected final SubQueryPath _subQueryPath;
    protected final String _selectClause; // needed for union
    protected final SqlClause _subQueryClause;
    protected final String _tableAliasName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param sqlClause The SQL clause for base table. (NotNull)
     * @param subQueryPath The property path of sub-query. (NotNull)
     * @param selectClause The select clause of sub-query. (NotNull)
     * @param subQueryClause The SQL clause for sub-query. (NotNull)
     * @param tableAliasName The alias name of sub-query table. (Nullable: if plain)
     */
    public SubQueryClause(SqlClause sqlClause, SubQueryPath subQueryPath, String selectClause,
            SqlClause subQueryClause, String tableAliasName) {
        _sqlClause = sqlClause;
        _subQueryPath = subQueryPath;
        _selectClause = selectClause;
        _subQueryClause = subQueryClause;
        _tableAliasName = tableAliasName;
    }

    // ===================================================================================
    //                                                                               Plain
    //                                                                               =====
    public String buildPlainSubQueryFromWhereClause() {
        String fromWhereClause = _subQueryClause.getClauseFromWhereWithUnionTemplate();

        // Replace the alias names for local table with alias name of sub-query unique.
        // However when it's inScope this replacement is unnecessary. 
        // (Override base alias name at sub-query on SQL)
        // So if the argument 'tableAliasName' is not null, replace it. 
        if (_tableAliasName != null) {
            fromWhereClause = replaceString(fromWhereClause, "dflocal", _tableAliasName);
        }

        // Resolve the location path for the condition-query of sub-query. 
        fromWhereClause = replaceString(fromWhereClause, ".conditionQuery.", "." + _subQueryPath + ".");

        // Replace template marks. These are very important!
        final SqlClause sc = _sqlClause;
        fromWhereClause = replaceString(fromWhereClause, sc.getUnionSelectClauseMark(), _selectClause);
        fromWhereClause = replaceString(fromWhereClause, sc.getUnionWhereClauseMark(), "");
        fromWhereClause = replaceString(fromWhereClause, sc.getUnionWhereFirstConditionMark(), "");
        return fromWhereClause;
    }

    // ===================================================================================
    //                                                                         Correlation
    //                                                                         ===========
    public String buildCorrelationSubQueryFromWhereClause(ColumnSqlName relatedColumnSqlName,
            ColumnRealName correlatedColumnRealName) {
        String clause = xprepareCorrelationSubQueryFromWhereClause();
        final String joinCondition = _tableAliasName + "." + relatedColumnSqlName + " = " + correlatedColumnRealName;
        clause = xreplaceCorrelationSubQueryFromWhereClause(clause, joinCondition);
        return clause;
    }

    public String buildCorrelationSubQueryFromWhereClause(ColumnSqlName[] relatedColumnSqlNames,
            ColumnRealName[] correlatedColumnRealNames) {
        String clause = xprepareCorrelationSubQueryFromWhereClause();

        final String joinCondition;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < relatedColumnSqlNames.length; i++) {
            if (sb.length() > 0) {
                sb.append(ln()).append("   and ");
            }
            sb.append(_tableAliasName).append(".").append(relatedColumnSqlNames[i]);
            sb.append(" = ").append(correlatedColumnRealNames[i]);
        }
        joinCondition = sb.toString();

        clause = xreplaceCorrelationSubQueryFromWhereClause(clause, joinCondition);
        return clause;
    }

    protected String xprepareCorrelationSubQueryFromWhereClause() {
        String clause = _subQueryClause.getClauseFromWhereWithWhereUnionTemplate();

        // Replace the alias names for local table with alias name of sub-query unique. 
        clause = replaceString(clause, "dflocal", _tableAliasName);

        // Resolve the location path for the condition-query of sub-query. 
        clause = replaceString(clause, ".conditionQuery.", "." + _subQueryPath + ".");

        return clause;
    }

    protected String xreplaceCorrelationSubQueryFromWhereClause(String clause, String joinCondition) {
        // Replace template marks. These are very important!
        final String firstConditionAfter = ln() + "   and ";
        final SqlClause sc = _sqlClause;
        clause = replaceString(clause, sc.getWhereClauseMark(), ln() + " where " + joinCondition);
        clause = replaceString(clause, sc.getWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        clause = replaceString(clause, sc.getUnionSelectClauseMark(), _selectClause);
        clause = replaceString(clause, sc.getUnionWhereClauseMark(), ln() + " where " + joinCondition);
        clause = replaceString(clause, sc.getUnionWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        return clause;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
