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
    protected final SubQueryPath _subQueryPath;
    protected final String _selectClause; // needed for union
    protected final SqlClause _subQuerySqlClause;
    protected final String _localAliasName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param subQueryPath The property path of sub-query. (NotNull)
     * @param selectClause The select clause of sub-query. (NotNull)
     * @param subQuerySqlClause The SQL clause for sub-query. (NotNull)
     * @param localAliasName The alias name of sub-query local table. (Nullable: if plain)
     */
    public SubQueryClause(SubQueryPath subQueryPath, String selectClause, SqlClause subQuerySqlClause,
            String localAliasName) {
        _subQueryPath = subQueryPath;
        _selectClause = selectClause;
        _subQuerySqlClause = subQuerySqlClause;
        _localAliasName = localAliasName;
    }

    // ===================================================================================
    //                                                                               Plain
    //                                                                               =====
    public String buildPlainSubQueryFromWhereClause() {
        String fromWhereClause = _subQuerySqlClause.getClauseFromWhereWithUnionTemplate();

        // Resolve the location path for the condition-query of sub-query. 
        fromWhereClause = replaceString(fromWhereClause, ".conditionQuery.", "." + _subQueryPath + ".");

        // Replace template marks. These are very important!
        fromWhereClause = replaceString(fromWhereClause, getUnionSelectClauseMark(), _selectClause);
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereClauseMark(), "");
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereFirstConditionMark(), "");
        return fromWhereClause;
    }

    // ===================================================================================
    //                                                                         Correlation
    //                                                                         ===========
    /**
     * Build the clause of correlation sub-query from from-where clause.
     * @param correlatedColumnRealName The real name of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlName The real name of related column that is sub-query table's column. (NotNull)
     * @return The clause string of correlation sub-query. (NotNull)
     */
    public String buildCorrelationSubQueryFromWhereClause(ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName) {
        String clause = xprepareCorrelationSubQueryFromWhereClause();
        final String joinCondition = _localAliasName + "." + relatedColumnSqlName + " = " + correlatedColumnRealName;
        clause = xreplaceCorrelationSubQueryFromWhereClause(clause, joinCondition);
        return clause;
    }

    /**
     * Build the clause of correlation sub-query from from-where clause.
     * @param correlatedColumnRealNames The real names of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlNames The real names of related column that is sub-query table's column. (NotNull)
     * @return The clause string of correlation sub-query. (NotNull)
     */
    public String buildCorrelationSubQueryFromWhereClause(ColumnRealName[] correlatedColumnRealNames,
            ColumnSqlName[] relatedColumnSqlNames) {
        String clause = xprepareCorrelationSubQueryFromWhereClause();

        final String joinCondition;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < relatedColumnSqlNames.length; i++) {
            if (sb.length() > 0) {
                sb.append(ln()).append("   and ");
            }
            sb.append(_localAliasName).append(".").append(relatedColumnSqlNames[i]);
            sb.append(" = ").append(correlatedColumnRealNames[i]);
        }
        joinCondition = sb.toString();

        clause = xreplaceCorrelationSubQueryFromWhereClause(clause, joinCondition);
        return clause;
    }

    protected String xprepareCorrelationSubQueryFromWhereClause() {
        String clause = _subQuerySqlClause.getClauseFromWhereWithWhereUnionTemplate();

        // Resolve the location path for the condition-query of sub-query. 
        clause = replaceString(clause, ".conditionQuery.", "." + _subQueryPath + ".");

        return clause;
    }

    protected String xreplaceCorrelationSubQueryFromWhereClause(String clause, String joinCondition) {
        // Replace template marks. These are very important!
        final String firstConditionAfter = ln() + "   and ";
        clause = replaceString(clause, getWhereClauseMark(), ln() + " where " + joinCondition);
        clause = replaceString(clause, getWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        clause = replaceString(clause, getUnionSelectClauseMark(), _selectClause);
        clause = replaceString(clause, getUnionWhereClauseMark(), ln() + " where " + joinCondition);
        clause = replaceString(clause, getUnionWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        return clause;
    }

    // ===================================================================================
    //                                                                          Alias Name
    //                                                                          ==========
    protected String getBasePointAliasName() {
        return _subQuerySqlClause.getBasePointAliasName();
    }

    // ===================================================================================
    //                                                                       Template Mark
    //                                                                       =============
    protected String getWhereClauseMark() {
        return _subQuerySqlClause.getWhereClauseMark();
    }

    protected String getWhereFirstConditionMark() {
        return _subQuerySqlClause.getWhereFirstConditionMark();
    }

    protected String getUnionSelectClauseMark() {
        return _subQuerySqlClause.getUnionSelectClauseMark();
    }

    protected String getUnionWhereClauseMark() {
        return _subQuerySqlClause.getUnionWhereClauseMark();
    }

    protected String getUnionWhereFirstConditionMark() {
        return _subQuerySqlClause.getUnionWhereFirstConditionMark();
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
