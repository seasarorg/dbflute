package org.seasar.dbflute.cbean.sqlclause.subquery;

import java.util.List;

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
public class ExistsSubQuery extends AbstractSubQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ExistsSubQuery(SqlClause sqlClause, SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQueryClause,
            SubQueryLevelReflector reflector, String subQueryIdentity, DBMeta subQueryDBMeta) {
        super(sqlClause, subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQueryClause,
                reflector, subQueryIdentity, subQueryDBMeta);
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    public String buildExistsSubQuery(String columnDbName, String relatedColumnDbName, String existsOption) {
        existsOption = existsOption != null ? existsOption + " " : "";
        reflectLocalSubQueryLevel();

        final String subQueryClause;
        if (columnDbName.contains(",") && relatedColumnDbName.contains(",")) {
            // two-or-more primary keys
            final List<String> relatedColumnSplit = Srl.splitList(relatedColumnDbName, ",");
            final ColumnSqlName[] relatedColumnSqlNames = new ColumnSqlName[relatedColumnSplit.size()];
            for (int i = 0; i < relatedColumnSplit.size(); i++) {
                relatedColumnSqlNames[i] = _subQuerySqlNameProvider.provide(relatedColumnSplit.get(i).trim());
            }
            final List<String> columnDbNameSplit = Srl.splitList(columnDbName, ",");
            final ColumnRealName[] correlatedColumnRealNames = new ColumnRealName[columnDbNameSplit.size()];
            for (int i = 0; i < columnDbNameSplit.size(); i++) {
                correlatedColumnRealNames[i] = _localRealNameProvider.provide(columnDbNameSplit.get(i).trim());
            }
            subQueryClause = getSubQueryClause(relatedColumnSqlNames, correlatedColumnRealNames);
        } else {
            // single primary key
            final ColumnSqlName relatedColumnSqlName = _subQuerySqlNameProvider.provide(relatedColumnDbName);
            final ColumnRealName correlatedColumnRealName = _localRealNameProvider.provide(columnDbName);
            subQueryClause = getSubQueryClause(relatedColumnSqlName, correlatedColumnRealName);
        }

        final String beginMark = _sqlClause.resolveSubQueryBeginMark(_subQueryIdentity) + ln();
        final String endMark = _sqlClause.resolveSubQueryEndMark(_subQueryIdentity);
        final String endIndent = "       ";
        return existsOption + "exists (" + beginMark + subQueryClause + ln() + endIndent + ")" + endMark;
    }

    /**
     * Get the clause of sub-query by single primary key.
     * @param relatedColumnSqlName The SQL name of related column. (NotNull)
     * @param correlatedColumnRealName The real name of correlated column. (NotNull)
     * @return The clause of sub-query. (NotNull)
     */
    protected String getSubQueryClause(ColumnSqlName relatedColumnSqlName, ColumnRealName correlatedColumnRealName) {
        final String tableAliasName = "dfsublocal_" + _subQueryLevel;
        final ColumnRealName relatedColumnRealName = new ColumnRealName(tableAliasName, relatedColumnSqlName);
        final String selectClause = "select " + relatedColumnRealName;
        final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, tableAliasName,
                relatedColumnSqlName, correlatedColumnRealName);
        return selectClause + " " + fromWhereClause;
    }

    /**
     * Get the clause of sub-query by two-or-more primary keys.
     * @param relatedColumnSqlNames The SQL names of related column. (NotNull)
     * @param correlatedColumnNames The real names of correlated column. (NotNull)
     * @return The clause of sub-query. (NotNull)
     */
    protected String getSubQueryClause(ColumnSqlName[] relatedColumnSqlNames, ColumnRealName[] correlatedColumnNames) {
        final String tableAliasName = "dfsublocal_" + _subQueryLevel;

        // Because sub-query may be only allowed to return a single column.
        final ColumnRealName relatedColumnRealName = new ColumnRealName(tableAliasName, relatedColumnSqlNames[0]);
        final String selectClause = "select " + tableAliasName + "." + relatedColumnRealName;

        final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, tableAliasName,
                relatedColumnSqlNames, correlatedColumnNames);
        return selectClause + " " + fromWhereClause;
    }
}
