/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean.sqlclause;

import java.util.Map;

import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.OrderByClause.ManumalOrderInfo;

/**
 * The interface of SQL clause.
 * @author jflute
 */
public interface SqlClause {

    // =====================================================================================
    //                                                                                Clause
    //                                                                                ======
    // -----------------------------------------------------
    //                                       Complete Clause
    //                                       ---------------
    /**
     * Get the clause of all parts.
     * <pre>
     * # select [base-table-columns], [join-table-columns]
     * #   from [base-table] left outer join [join-table] [join-alias] on [join-condition]
     * #  where [base-table].[column] = [value] and [join-alias].[column] is null
     * #  order by [base-table].[column] asc, [join-alias].[column] desc
     * #  for update
     * </pre>
     * @return The clause of all parts. (NotNull)
     */
    String getClause();

    // -----------------------------------------------------
    //                                       Fragment Clause
    //                                       ---------------
    /**
     * Get from-where clause without select and orderBy and sqlSuffix. 
     * For subQuery and selectCount.
     * <p>
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark in clause.
     * </p>
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    String getClauseFromWhereWithUnionTemplate();

    /**
     * Get from-where clause without select and orderBy and sqlSuffix as template. 
     * For subQuery and selectCount.
     * <p>
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark
     * and WhereClauseMark and WhereFirstConditionMark in clause.
     * </p>
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    String getClauseFromWhereWithWhereUnionTemplate();

    // =====================================================================================
    //                                                                          Clause Parts
    //                                                                          ============
    /**
     * Get the clause of 'select'. This is an internal method.
     * @return The clause of select. {[select ...] from table...} (NotNull)
     */
    String getSelectClause();

    /**
     * Get the map of select index.
     * @return The map of select index. {key:columnName, value:selectIndex}
     *         (Nullable: Null means select index is disabled.)
     */
    Map<String, Integer> getSelectIndexMap();

    /**
     * Get the reverse map of select index.
     * @return The reverse map of select index. {key:selectIndex(AliasName), value:columnName}
     *         (Nullable: Null means select index is disabled.)
     */
    Map<String, String> getSelectIndexReverseMap();

    /**
     * Disable select index.
     */
    void disableSelectIndex();

    /**
     * Get the hint of 'select'. This is an internal method.
     * @return The hint of 'select'. {select [select-hint] * from table...} (NotNull)
     */
    String getSelectHint();

    /**
     * Get the clause of 'from'. This is an internal method.
     * @return The clause of 'from'. (NotNull)
     */
    String getFromClause();

    /**
     * Get the clause of from-base-table. This is an internal method.
     * @return The hint of from-base-table. {select * from table [from-base-table-hint] where ...} (NotNull)
     */
    String getFromBaseTableHint();

    /**
     * Get the hint of 'from'. This is an internal method.
     * @return The hint of 'from'. {select * from table left outer join ... on ... [from-hint] where ...} (NotNull)
     */
    String getFromHint();

    /**
     * Get the clause of 'where'. This is an internal method.
     * @return The clause of 'where'. (NotNull)
     */
    String getWhereClause();

    /**
     * Get the clause of 'order-by'. This is an internal method.
     * @return The clause of 'order-by'. (NotNull)
     */
    String getOrderByClause();

    /**
     * Get the suffix of SQL. This is an internal method.
     * @return The suffix of SQL. {select * from table where ... order by ... [sql-suffix]} (NotNull)
     */
    String getSqlSuffix();

    // ===================================================================================
    //                                                                SelectedSelectColumn
    //                                                                ====================
    /**
     * Register selected-select-column.
     * @param foreignTableAliasName The alias name of foreign table. (NotNull)
     * @param localTableName The table name of local. (NotNull)
     * @param foreignPropertyName The property name of foreign table. (NotNull)
     * @param localRelationPath The path of local relation. (Nullable)
     */
    void registerSelectedSelectColumn(String foreignTableAliasName, String localTableName, String foreignPropertyName,
            String localRelationPath);

    // ===================================================================================
    //                                                                           OuterJoin
    //                                                                           =========
    /**
     * Register outer-join.
     * @param joinTableName The name of join table. {left outer join [joinTableName]} (NotNull)
     * @param aliasName The alias name of join table. {left outer join joinTableName [aliasName]} (NotNull and Unique per invoking method)
     * @param joinOnMap Map that has conditions of on-clause. (NotNull)
     */
    void registerOuterJoin(String joinTableName, String aliasName, Map<String, String> joinOnMap);

    /**
     * Change the join type for the relation to inner join.
     * @param aliasName The registered alias name of join table. (NotNull and Unique per invoking method)
     */
    void changeToInnerJoin(String aliasName);

    SqlClause makeInnerJoinEffective();

    SqlClause backToOuterJoin();

    String getFixedConditionKey();

    // ===================================================================================
    //                                                                               Where
    //                                                                               =====
    /**
     * Register 'where' clause.
     * @param columnFullName The full name of column. {[table-name].[column-name]}. (NotNull)
     * @param key Condition-key. (NotNull)
     * @param value Condition-value. (NotNull)
     */
    void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value);

    /**
     * Register 'where' clause.
     * @param columnFullName The full name of column. {[table-name].[column-name]}. (NotNull)
     * @param key Condition-key. (NotNull)
     * @param value Condition-value. (NotNull)
     * @param option Condition-option. (NotNull)
     */
    void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value, ConditionOption option);

    /**
     * Register 'where' clause.
     * @param clause The clause of 'where'. (NotNull)
     */
    void registerWhereClause(String clause);

    /**
     * Exchange first The clause of 'where' for last one.
     */
    void exchangeFirstWhereClauseForLastOne();

    /**
     * Does it have where clauses? <br />
     * In-line where clause is NOT contained.
     * @return Determination.
     */
    boolean hasWhereClause();

    // ===================================================================================
    //                                                                         InlineWhere
    //                                                                         ===========
    void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value);

    void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value,
            ConditionOption option);

    void registerBaseTableInlineWhereClause(String value);

    void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, boolean onClauseInline);

    void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, ConditionOption option, boolean onClauseInline);

    void registerOuterJoinInlineWhereClause(String aliasName, String value, boolean onClauseInline);

    // ===================================================================================
    //                                                                        OrScopeQuery
    //                                                                        ============
    /**
     * Make or-scope query effective.
     */
    void makeOrScopeQueryEffective();

    /**
     * Close or-scope query.
     */
    void closeOrScopeQuery();

    /**
     * Begin or-scope query to and-part.
     */
    void beginOrScopeQueryAndPart();

    /**
     * End or-scope query and-part.
     */
    void endOrScopeQueryAndPart();

    /**
     * Is or-scope query effective?
     * @return Determination.
     */
    boolean isOrScopeQueryEffective();

    // ===================================================================================
    //                                                                             OrderBy
    //                                                                             =======
    OrderByClause getSqlComponentOfOrderByClause();

    SqlClause clearOrderBy();

    SqlClause ignoreOrderBy();

    SqlClause makeOrderByEffective();

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnName/aliasName.columnName/...' (NotNull)
     * @param registeredOrderByProperty Registered-order-by-property. ([table-name].[column-name]) (Nullable)
     * @param ascOrDesc Is it ascend or descend?
     */
    void registerOrderBy(String orderByProperty, String registeredOrderByProperty, boolean ascOrDesc);

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnName/aliasName.columnName/...' (NotNull)
     * @param registeredOrderByProperty Registered-order-by-property. ([table-name].[column-name]) (Nullable)
     * @param ascOrDesc Is it ascend or descend?
     */
    void reverseOrderBy_Or_OverrideOrderBy(String orderByProperty, String registeredOrderByProperty, boolean ascOrDesc);

    void addNullsFirstToPreviousOrderBy();

    void addNullsLastToPreviousOrderBy();

    void addManualOrderToPreviousOrderByElement(ManumalOrderInfo manumalOrderInfo);

    /**
     * Does it have order-by clauses? <br />
     * Whether effective or not has no influence.
     * @return Determination.
     */
    boolean hasOrderByClause();

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    void registerUnionQuery(String unionClause, boolean unionAll);

    boolean hasUnionQuery();

    // ===================================================================================
    //                                                                          FetchScope
    //                                                                          ==========
    /**
     * Fetch first.
     * @param fetchSize Fetch-size. (NotMinus)
     * @return this. (NotNull)
     */
    SqlClause fetchFirst(int fetchSize);

    /**
     * Fetch scope.
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch-size. (NotMinus)
     * @return this. (NotNull)
     */
    SqlClause fetchScope(int fetchStartIndex, int fetchSize);

    /**
     * Fetch page.
     * <p>
     * When you invoke this, it is normally necessary to invoke 'fetchFirst()' or 'fetchScope()' ahead of that.
     * But you also can use default-fetch-size without invoking 'fetchFirst()' or 'fetchScope()'.
     * If you invoke this, your SQL returns [fetch-size] records from [fetch-start-index] calculated by [fetch-page-number].
     * </p>
     * @param fetchPageNumber Fetch-page-number. 1 origin. (NotMinus & NotZero: If minus or zero, set one.)
     * @return this. (NotNull)
     */
    SqlClause fetchPage(int fetchPageNumber);

    /**
     * Get fetch start index.
     * @return Fetch start index.
     */
    int getFetchStartIndex();

    /**
     * Get fetch size.
     * @return Fetch size.
     */
    int getFetchSize();

    /**
     * Get fetch page number.
     * @return Fetch page number.
     */
    int getFetchPageNumber();

    /**
     * Get page start index.
     * @return Page start index. 0 origin. (NotMinus)
     */
    int getPageStartIndex();

    /**
     * Get page end index.
     * @return Page end index. 0 origin. (NotMinus)
     */
    int getPageEndIndex();

    /**
     * Is fetch scope effective?
     * @return Determiantion.
     */
    boolean isFetchScopeEffective();

    /**
     * Ignore fetch-scope.
     * @return this. (NotNull)
     */
    SqlClause ignoreFetchScope();

    /**
     * Make fetch-scope effective.
     * @return this. (NotNull)
     */
    SqlClause makeFetchScopeEffective();

    /**
     * Is fetch start index supported?
     * @return Determination.
     */
    boolean isFetchStartIndexSupported();

    /**
     * Is fetch size supported?
     * @return Determination.
     */
    boolean isFetchSizeSupported();

    // ===================================================================================
    //                                                                     Fetch Narrowing
    //                                                                     ===============
    /**
     * Is fetch-narrowing effective?
     * @return Determiantion.
     */
    boolean isFetchNarrowingEffective();

    /**
     * Get fetch-narrowing skip-start-index.
     * @return Skip-start-index.
     */
    int getFetchNarrowingSkipStartIndex();

    /**
     * Get fetch-narrowing loop-count.
     * @return Loop-count.
     */
    int getFetchNarrowingLoopCount();

    // ===================================================================================
    //                                                                                Lock
    //                                                                                ====
    /**
     * Lock for update.
     * <p>
     * If you invoke this, your SQL lock target records for update.
     * It depends whether this method supports this on the database type.
     * </p>
     * @return this. (NotNull)
     */
    SqlClause lockForUpdate();

    // ===================================================================================
    //                                                                            Resolver
    //                                                                            ========
    /**
     * Resolve join alias name.
     * @param relationPath Relation path. (NotNull)
     * @param cqNestNo The nest no of condition query.
     * @return Resolved join alias name. (NotNull)
     */
    String resolveJoinAliasName(String relationPath, int cqNestNo);

    /**
     * Resolve nest level expression.
     * @param name Name. (NotNull)
     * @param cqNestNo The nest no of condition query.
     * @return Resolved name about nest level. (NotNull)
     */
    String resolveNestLevelExpression(String name, int cqNestNo);

    /**
     * Resolve relation no.
     * @param baseTableName The table name of base. (NotNull)
     * @param foreignPropertyName The property name of foreign. (NotNull)
     * @return Resolved relation no.
     */
    int resolveRelationNo(String baseTableName, String foreignPropertyName);

    // ===================================================================================
    //                                                                    Table Alias Info
    //                                                                    ================
    String getLocalTableAliasName();

    String getForeignTableAliasPrefix();

    // ===================================================================================
    //                                                                       Template Mark
    //                                                                       =============
    String getWhereClauseMark();

    String getWhereFirstConditionMark();

    String getUnionSelectClauseMark();

    String getUnionWhereClauseMark();

    String getUnionWhereFirstConditionMark();

    // ===================================================================================
    //                                                          Where Clause Simple Filter
    //                                                          ==========================
    void addWhereClauseSimpleFilter(WhereClauseSimpleFilter whereClauseSimpleFilter);

    // ===================================================================================
    //                                                               Selected Foreign Info
    //                                                               =====================
    boolean isSelectedForeignInfoEmpty();

    boolean hasSelectedForeignInfo(String relationPath);

    void registerSelectedForeignInfo(String relationPath, String foreignPropertyName);

    // ===================================================================================
    //                                                                    Sub Query Indent
    //                                                                    ================
    String resolveSubQueryBeginMark(String subQueryIdentity);

    String resolveSubQueryEndMark(String subQueryIdentity);

    String filterSubQueryIndent(String sql);

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                       Specification
    //                                                                       =============
    void specifySelectColumn(String tableAliasName, String columnName);

    void specifyDeriveSubQuery(String aliasName, String deriveSubQuery);

    boolean hasSpecifiedDeriveSubQuery(String aliasName);

    /**
     * Get the name of only one specified column.
     * @return The name of only one specified column. (Nullable: If it's not found or duplicated, it returns null)
     */
    String getSpecifiedColumnNameAsOne();

    /**
     * Get the name of only one specified column with alias name.
     * @return The name of only one specified column with alias name. (Nullable: If it's not found or duplicated, it returns null)
     */
    String getSpecifiedColumnRealNameAsOne();

    /**
     * Remove the only one specified column.
     * @return The only one specified column with alias name. (Nullable: If it's not found or duplicated, it returns null)
     */
    String removeSpecifiedColumnRealNameAsOne();

    /**
     * Back up specified select columns.
     */
    void backupSpecifiedSelectColumn();

    /**
     * Restore specified select columns.
     */
    void restoreSpecifiedSelectColumn();

    /**
     * Clear specified select columns.
     */
    void clearSpecifiedSelectColumn();

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * @param columnParameterMap The map of column parameters. (NotNull)
     * @return The clause of query update. (Nullable: If columnParameterMap is empty, return null)
     */
    String getClauseQueryUpdate(Map<String, String> columnParameterMap);

    String getClauseQueryDelete();

    // [DBFlute-0.8.6]
    // ===================================================================================
    //                                                                  Select Clause Type
    //                                                                  ==================
    /**
     * Classify the type of select clause into specified type.
     * @param selectClauseType The type of select clause. (NotNull)
     */
    void classifySelectClauseType(SelectClauseType selectClauseType);

    /**
     * Roll-back the type of select clause into previous one.
     * If it has no change, classify its type into default type.
     */
    void rollbackSelectClauseType();

    /**
     * The type of select clause.
     */
    public static enum SelectClauseType {
        COLUMNS, COUNT, MAX, MIN, SUM, AVG
    }

    // [DBFlute-0.9.4]
    // ===================================================================================
    //                                                                       InScope Limit
    //                                                                       =============
    /**
     * Get the limit of inScope.
     * @return The limit of inScope. (If it's zero or minus, it means no limit)
     */
    int getInScopeLimit();
}
