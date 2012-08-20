/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.ManualOrderBean;
import org.seasar.dbflute.cbean.chelper.HpCBPurpose;
import org.seasar.dbflute.cbean.chelper.HpDerivingSubQueryInfo;
import org.seasar.dbflute.cbean.chelper.HpInvalidQueryInfo;
import org.seasar.dbflute.cbean.chelper.HpSpecifiedColumn;
import org.seasar.dbflute.cbean.cipher.ColumnFunctionCipher;
import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.coption.ScalarSelectOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.clause.ClauseLazyReflector;
import org.seasar.dbflute.cbean.sqlclause.join.FixedConditionResolver;
import org.seasar.dbflute.cbean.sqlclause.join.LeftOuterJoinInfo;
import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByElement;
import org.seasar.dbflute.cbean.sqlclause.query.QueryClause;
import org.seasar.dbflute.cbean.sqlclause.query.QueryClauseFilter;
import org.seasar.dbflute.cbean.sqlclause.query.QueryUsedAliasInfo;
import org.seasar.dbflute.cbean.sqlclause.select.SelectedRelationColumn;
import org.seasar.dbflute.cbean.sqlclause.union.UnionClauseProvider;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbway.DBWay;

/**
 * The interface of SQL clause. <br />
 * And this also has a role of a container for common info
 * between the top level condition-bean and related condition-queries.
 * @author jflute
 */
public interface SqlClause {

    // ===================================================================================
    //                                                                      SubQuery Level
    //                                                                      ==============
    /**
     * Get the hierarchy level of sub-query.
     * @return The hierarchy level of sub-query. (NotMinus: if zero, not for sub-query)
     */
    int getSubQueryLevel();

    /**
     * Set up this SQL for sub-query.
     * @param subQueryLevel The hierarchy level of sub-query. (NotMinus: if zero, not for sub-query)
     */
    void setupForSubQuery(int subQueryLevel);

    /**
     * Is this SQL for sub-query?
     * @return The determination, true or false.
     */
    boolean isForSubQuery();

    // ===================================================================================
    //                                                                              Clause
    //                                                                              ======
    // -----------------------------------------------------
    //                                       Complete Clause
    //                                       ---------------
    /**
     * Get the clause of all parts.
     * <pre>
     * select [base-table-columns], [join-table-columns]
     *   from [base-table] left outer join [join-table] [join-alias] on [join-condition]
     *  where [base-table].[column] = [value] and [join-alias].[column] is null
     *  order by [base-table].[column] asc, [join-alias].[column] desc
     *  for update
     * </pre>
     * @return The clause of all parts. (NotNull)
     */
    String getClause();

    // -----------------------------------------------------
    //                                       Fragment Clause
    //                                       ---------------
    /**
     * Get from-where clause without select and orderBy and sqlSuffix. <br />
     * Basically for subQuery and selectCount. <br />
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark in clause.
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    String getClauseFromWhereWithUnionTemplate();

    /**
     * Get from-where clause without select and orderBy and sqlSuffix as template. <br />
     * Basically for subQuery and selectCount. <br />
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark
     * and WhereClauseMark and WhereFirstConditionMark in clause.
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    String getClauseFromWhereWithWhereUnionTemplate();

    // ===================================================================================
    //                                                                        Clause Parts
    //                                                                        ============
    /**
     * Get the clause of 'select'. This is an internal method.
     * @return The clause of select. {[select ...] from table...} (NotNull)
     */
    String getSelectClause();

    /**
     * Get the map of select index. map:{selectColumnKeyName = selectIndex}
     * @return The map of select index. (NullAllowed: null means select index is disabled)
     */
    Map<String, Integer> getSelectIndexMap();

    /**
     * Get the reverse map of select index. map:{indexedOnQueryName = selectColumnKeyName}
     * @return The reverse map of select index. (NullAllowed: null means select index is disabled)
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
    //                                                                   Selected Relation
    //                                                                   =================
    /**
     * Register selected relation.
     * @param foreignTableAliasName The alias name of foreign table. (NotNull)
     * @param localTableDbName The table DB name of local. (NotNull)
     * @param foreignPropertyName The property name of foreign table. (NotNull)
     * @param localRelationPath The path of local relation. (NullAllowed)
     * @param foreignRelationPath The path of foreign relation. (NullAllowed)
     */
    void registerSelectedRelation(String foreignTableAliasName, String localTableDbName, String foreignPropertyName,
            String localRelationPath, String foreignRelationPath);

    boolean isSelectedRelationEmpty();

    boolean hasSelectedRelation(String relationPath);

    /**
     * Get the map of selected relation column. <br />
     * Basically internal but public for analyzing.
     * @return The map of selected relation column. map:{foreignTableAliasName : map:{columnName : selectedRelationColumn}} (NotNull)
     */
    Map<String, Map<String, SelectedRelationColumn>> getSelectedRelationColumnMap();

    // ===================================================================================
    //                                                                           OuterJoin
    //                                                                           =========
    // -----------------------------------------------------
    //                                          Registration
    //                                          ------------
    /**
     * Register outer-join. <br />
     * The fixed-conditions are located on on-clause.
     * @param foreignAliasName The alias name of foreign table. {left outer join [foreignTableDbName] [foreignAliasName]} (NotNull, Unique)
     * @param foreignTableDbName The DB name of foreign table. {left outer join [foreignTableDbName] [foreignAliasName]} (NotNull)
     * @param localAliasName The alias name of local table. {[localTableDbName] [localAliasName] left outer join} (NotNull)
     * @param localTableDbName The DB name of local table. {[localTableDbName] [localAliasName] left outer join} (NotNull)
     * @param joinOnMap The map of join condition on on-clause. (NotNull)
     * @param foreignInfo The information of foreign relation corresponding to this join. (NotNull)
     * @param fixedCondition The fixed condition on on-clause. (NullAllowed: if null, means no fixed condition)
     * @param fixedConditionResolver The resolver for variables on fixed-condition. (NullAllowed) 
     */
    void registerOuterJoin(String foreignAliasName, String foreignTableDbName, String localAliasName,
            String localTableDbName, Map<ColumnRealName, ColumnRealName> joinOnMap, ForeignInfo foreignInfo,
            String fixedCondition, FixedConditionResolver fixedConditionResolver);

    /**
     * Register outer-join using in-line view for fixed-conditions. <br />
     * The fixed-conditions are located on in-line view.
     * @param foreignAliasName The alias name of foreign table. {left outer join [foreignTableDbName] [foreignAliasName]} (NotNull, Unique)
     * @param foreignTableDbName The DB name of foreign table. {left outer join [foreignTableDbName] [foreignAliasName]} (NotNull)
     * @param localAliasName The alias name of local table. {[localTableDbName] [localAliasName] left outer join} (NotNull)
     * @param localTableDbName The DB name of local table. {[localTableDbName] [localAliasName] left outer join} (NotNull)
     * @param joinOnMap The map of join condition on on-clause. (NotNull)
     * @param foreignInfo The information of foreign relation corresponding to this join. (NotNull)
     * @param fixedCondition The fixed condition on in-line view. (NullAllowed: if null, means no fixed condition)
     * @param fixedConditionResolver The resolver for variables on fixed-condition. (NullAllowed) 
     */
    void registerOuterJoinFixedInline(String foreignAliasName, String foreignTableDbName, String localAliasName,
            String localTableDbName, Map<ColumnRealName, ColumnRealName> joinOnMap, ForeignInfo foreignInfo,
            String fixedCondition, FixedConditionResolver fixedConditionResolver);

    // -----------------------------------------------------
    //                                   OuterJoin Attribute
    //                                   -------------------
    /**
     * Get the information of left-outer-join. <br />
     * Basically internal but public for analyzing.
     * @return The map of left-outer-join info. map:{ foreignAliasName : leftOuterJoinInfo } (NotNull)
     */
    Map<String, LeftOuterJoinInfo> getOuterJoinMap();

    /**
     * Does outer-join (at least one) exist? (contains inner-join)
     * @return The determination, true or false.
     */
    boolean hasOuterJoin();

    // -----------------------------------------------------
    //                                    InnerJoin Handling
    //                                    ------------------
    /**
     * Change the join type for the relation to inner join manually.
     * @param foreignAliasName The foreign alias name of join table. (NotNull and Unique per invoking method)
     */
    void changeToInnerJoin(String foreignAliasName);

    // -----------------------------------------------------
    //                                  InnerJoin AutoDetect
    //                                  --------------------
    // has several items of inner-join auto-detected
    /**
     * Allow to auto-detect joins that can be (all type) inner-join. <br />
     * You should call this before registrations of where clause.
     * (actually you can call before selecting but it's a fixed specification for user)
     */
    void allowInnerJoinAutoDetect();

    /**
     * Suppress auto-detecting inner-join. <br />
     * You should call this before registrations of where clause.
     */
    void suppressInnerJoinAutoDetect();

    // -----------------------------------------------------
    //                          StructuralPossible InnerJoin
    //                          ----------------------------
    // one of inner-join auto-detect
    /**
     * Allow to auto-detect joins that can be structure-possible inner-join. <br />
     * You should call this before registrations of where clause.
     * (actually you can call before selecting but it's a fixed specification for user)
     */
    void allowStructuralPossibleInnerJoin();

    /**
     * Suppress auto-detecting structural-possible inner-join. <br />
     * You should call this before registrations of where clause.
     */
    void suppressStructuralPossibleInnerJoin();

    /**
     * Does it allow to auto-detect structure-possible inner-join? 
     * @return Determination. (true or false)
     */
    boolean isStructuralPossibleInnerJoinAllowed();

    // -----------------------------------------------------
    //                                   WhereUsed InnerJoin
    //                                   -------------------
    // one of inner-join auto-detect
    /**
     * Allow to auto-detect joins that can be where-used inner-join. <br />
     * You should call this before registrations of where clause.
     */
    void allowWhereUsedInnerJoin();

    /**
     * Suppress auto-detecting where-used inner-join.
     * You should call this before registrations of where clause.
     */
    void suppressWhereUsedInnerJoin();

    /**
     * Does it allow to auto-detect where-used inner-join? 
     * @return Determination. (true or false)
     */
    boolean isWhereUsedInnerJoinAllowed();

    // ===================================================================================
    //                                                                               Where
    //                                                                               =====
    /**
     * Register 'where' clause.
     * @param columnRealName The real name of column. {[alias-name].[column-name]}. (NotNull)
     * @param key The key of condition. (NotNull)
     * @param value The value of condition. (NotNull)
     * @param cipher The cipher of column by function. (NullAllowed)
     * @param option The option of condition. (NullAllowed)
     * @param usedAliasName The alias name of table used on the where clause. (NotNull)
     */
    void registerWhereClause(ColumnRealName columnRealName, ConditionKey key, ConditionValue value,
            ColumnFunctionCipher cipher, ConditionOption option, String usedAliasName);

    /**
     * Register 'where' clause. <br />
     * The join of the alias, if it's a relation condition, may have a chance to be inner-join.
     * @param clause The string clause of 'where'. (NotNull)
     * @param usedAliasName The alias name of table used on the where clause. (NotNull)
     */
    void registerWhereClause(String clause, String usedAliasName);

    /**
     * Register 'where' clause. <br />
     * You can control the inner-join possibility.
     * @param clause The string clause of 'where'. (NotNull)
     * @param usedAliasName The alias name of table used on the where clause. (NotNull)
     * @param noWayInner No way, to be inner-join for the join of the alias?
     */
    void registerWhereClause(String clause, String usedAliasName, boolean noWayInner);

    /**
     * Register 'where' clause. <br />
     * You can control the inner-join possibility.
     * @param clause The string clause of 'where'. (NotNull)
     * @param usedAliasInfos The array of information of used alias, contains no-way-inner determination. (NotNull, NotEmpty)
     */
    void registerWhereClause(QueryClause clause, QueryUsedAliasInfo... usedAliasInfos);

    /**
     * Exchange first The clause of 'where' for last one.
     */
    void exchangeFirstWhereClauseForLastOne();

    /**
     * Does it have where clauses on the base query? <br />
     * Clauses on union queries and in-line views are not concerned.
     * @return The determination, true or false.
     */
    boolean hasWhereClauseOnBaseQuery();

    /**
     * Clear where clauses on the base query. <br />
     * Clauses on union queries and in-line views are not concerned.
     */
    void clearWhereClauseOnBaseQuery();

    // ===================================================================================
    //                                                                       In-line Where
    //                                                                       =============
    // -----------------------------------------------------
    //                                In-line for Base Table
    //                                ----------------------
    void registerBaseTableInlineWhereClause(ColumnSqlName columnSqlName, ConditionKey key, ConditionValue value,
            ColumnFunctionCipher cipher, ConditionOption option);

    void registerBaseTableInlineWhereClause(String value);

    boolean hasBaseTableInlineWhereClause();

    void clearBaseTableInlineWhereClause();

    // -----------------------------------------------------
    //                                In-line for Outer Join
    //                                ----------------------
    void registerOuterJoinInlineWhereClause(String foreignAliasName, ColumnSqlName columnSqlName, ConditionKey key,
            ConditionValue value, ColumnFunctionCipher cipher, ConditionOption option, boolean onClause);

    void registerOuterJoinInlineWhereClause(String foreignAliasName, String clause, boolean onClause);

    boolean hasOuterJoinInlineWhereClause();

    void clearOuterJoinInlineWhereClause();

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
     * @return The determination, true or false.
     */
    boolean isOrScopeQueryEffective();

    /**
     * Is and-part of or-scope effective?
     * @return The determination, true or false.
     */
    boolean isOrScopeQueryAndPartEffective();

    // ===================================================================================
    //                                                                             OrderBy
    //                                                                             =======
    OrderByClause getOrderByComponent();

    OrderByElement getOrderByLastElement();

    void clearOrderBy();

    void makeOrderByEffective();

    void ignoreOrderBy();

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnSqlName/aliasName.columnSqlName/...' (NotNull)
     * @param ascOrDesc Is it ascend or descend?
     * @param columnInfo The information of the column for the order. (NotNull)
     */
    void registerOrderBy(String orderByProperty, boolean ascOrDesc, ColumnInfo columnInfo);

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnSqlName/aliasName.columnSqlName/...' (NotNull)
     * @param ascOrDesc Is it ascend or descend?
     */
    void registerSpecifiedDerivedOrderBy(String orderByProperty, boolean ascOrDesc);

    void addNullsFirstToPreviousOrderBy();

    void addNullsLastToPreviousOrderBy();

    void addManualOrderToPreviousOrderByElement(ManualOrderBean manualOrderBean);

    /**
     * Does it have order-by clauses? <br />
     * Whether effective or not has no influence.
     * @return The determination, true or false.
     */
    boolean hasOrderByClause();

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    void registerUnionQuery(UnionClauseProvider unionClauseProvider, boolean unionAll);

    boolean hasUnionQuery();

    void clearUnionQuery();

    // ===================================================================================
    //                                                                          FetchScope
    //                                                                          ==========
    /**
     * Fetch first.
     * @param fetchSize Fetch-size. (NotMinus)
     */
    void fetchFirst(int fetchSize);

    /**
     * Fetch scope.
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch-size. (NotMinus)
     */
    void fetchScope(int fetchStartIndex, int fetchSize);

    /**
     * Fetch page.
     * <p>
     * When you invoke this, it is normally necessary to invoke 'fetchFirst()' or 'fetchScope()' ahead of that.
     * But you also can use default-fetch-size without invoking 'fetchFirst()' or 'fetchScope()'.
     * If you invoke this, your SQL returns [fetch-size] records from [fetch-start-index] calculated by [fetch-page-number].
     * </p>
     * @param fetchPageNumber Fetch-page-number. 1 origin. (NotMinus & NotZero: If minus or zero, set one.)
     */
    void fetchPage(int fetchPageNumber);

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
     */
    void ignoreFetchScope();

    /**
     * Make fetch-scope effective.
     */
    void makeFetchScopeEffective();

    /**
     * Is fetch start index supported?
     * @return The determination, true or false.
     */
    boolean isFetchStartIndexSupported();

    /**
     * Is fetch size supported?
     * @return The determination, true or false.
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
     * Lock selected records for update.
     * <p>
     * If you invoke this, your SQL lock target records for update.
     * It depends whether this method supports this on the database type.
     * </p>
     */
    void lockForUpdate();

    // ===================================================================================
    //                                                                    Table Alias Info
    //                                                                    ================
    /**
     * Get the alias name for base point table. <br />
     * @return The string name for alias. (NotNull)
     */
    String getBasePointAliasName();

    /**
     * Resolve alias name for join table.
     * @param relationPath Relation path. (NotNull)
     * @param nestLevel The nest level of condition query.
     * @return The resolved name. (NotNull)
     */
    String resolveJoinAliasName(String relationPath, int nestLevel);

    /**
     * Resolve relation no.
     * @param localTableName The name of local table. (NotNull)
     * @param foreignPropertyName The property name of foreign relation. (NotNull)
     * @return The resolved relation No.
     */
    int resolveRelationNo(String localTableName, String foreignPropertyName);

    /**
     * Get the alias name for base point table on in-line view.
     * @return The string name for alias. (NotNull)
     */
    String getInlineViewBasePointAlias();

    /**
     * Get the alias name for in-line view of union-query.
     * @return The string name for alias. (NotNull)
     */
    String getUnionQueryInlineViewAlias();

    /**
     * Get the alias name for derived column of nested DerivedReferrer.
     * @return The string name for alias. (NotNull)
     */
    String getDerivedReferrerNestedAlias();

    /**
     * Get the alias name for specified column of scalar-select.
     * @return The string name for alias. (NotNull)
     */
    String getScalarSelectColumnAlias();

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
    void addWhereClauseSimpleFilter(QueryClauseFilter whereClauseSimpleFilter);

    // ===================================================================================
    //                                                                    Sub Query Indent
    //                                                                    ================
    String resolveSubQueryBeginMark(String subQueryIdentity);

    String resolveSubQueryEndMark(String subQueryIdentity);

    String processSubQueryIndent(String sql);

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                       Specification
    //                                                                       =============
    // -----------------------------------------------------
    //                                        Specify Column
    //                                        --------------
    /**
     * Specify select columns. <br />
     * It is overridden when the specified column has already been specified.
     * @param specifiedColumn The info about column specification. (NotNull)
     */
    void specifySelectColumn(HpSpecifiedColumn specifiedColumn);

    /**
     * Does it have specified select columns?
     * @param tableAliasName The alias name of table. (NotNull)
     * @return The determination, true or false.
     */
    boolean hasSpecifiedSelectColumn(String tableAliasName);

    /**
     * Does it have the specified select column?
     * @param tableAliasName The alias name of table. (NotNull)
     * @param columnDbName The DB name of column. (NotNull)
     * @return The determination, true or false.
     */
    boolean hasSpecifiedSelectColumn(String tableAliasName, String columnDbName);

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

    // -----------------------------------------------------
    //                                      Specified as One
    //                                      ----------------
    /**
     * Get the DB name of only one specified column.
     * @return The instance as string. (NullAllowed: if not found or duplicated, returns null)
     */
    String getSpecifiedColumnDbNameAsOne();

    /**
     * Get the information of only one specified column.
     * @return An instance as a type for information of column. (NullAllowed: if not found or duplicated, returns null)
     */
    ColumnInfo getSpecifiedColumnInfoAsOne();

    /**
     * Get the real name of only one specified column.
     * @return An instance as a type for real name of column. (NullAllowed: if not found or duplicated, returns null)
     */
    ColumnRealName getSpecifiedColumnRealNameAsOne();

    /**
     * Get the SQL name of only one specified column.
     * @return An instance as a type for SQL name of column. (NullAllowed: if not found or duplicated, returns null)
     */
    ColumnSqlName getSpecifiedColumnSqlNameAsOne();

    // -----------------------------------------------------
    //                                      Specify Deriving
    //                                      ----------------
    /**
     * Specify deriving sub-query for DerivedReferrer. <br />
     * It is overridden when the specified column has already been specified. <br />
     * The aliasName is allowed to be null for (Specify)DerivedReferrer to be used in other functions.
     * @param subQueryInfo The info about deriving sub-query. (NotNull: aliasName is allowed to be null)
     */
    void specifyDerivingSubQuery(HpDerivingSubQueryInfo subQueryInfo);

    boolean hasSpecifiedDerivingSubQuery(String aliasName);

    List<String> getSpecifiedDerivingAliasList();

    HpDerivingSubQueryInfo getSpecifiedDerivingInfo(String aliasName);

    // -----------------------------------------------------
    //                                       Deriving as One
    //                                       ---------------
    ColumnInfo getSpecifiedDerivingColumnInfoAsOne();

    String getSpecifiedDerivingAliasNameAsOne();

    String getSpecifiedDerivingSubQueryAsOne();

    void clearSpecifiedDerivingSubQuery();

    // ===================================================================================
    //                                                                  Invalid Query Info
    //                                                                  ==================
    boolean isEmptyStringQueryAllowed();

    void allowEmptyStringQuery();

    boolean isInvalidQueryChecked();

    void checkInvalidQuery();

    /**
     * Get the list of invalid query. (basically for logging)
     * @return The list of invalid query. (NotNull, ReadOnly)
     */
    List<HpInvalidQueryInfo> getInvalidQueryList();

    void saveInvalidQuery(HpInvalidQueryInfo invalidQueryInfo);

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
        COLUMNS(false, false, false, false) // normal
        , UNIQUE_COUNT(true, true, true, false) // basically for selectCount(cb)
        , PLAIN_COUNT(true, true, false, false) // basically for count of selectPage(cb)
        // scalar mainly for Behavior.scalarSelect(cb)
        , COUNT_DISTINCT(false, true, true, true) // count(distinct)
        , MAX(false, true, true, true), MIN(false, true, true, true) // max(), min()
        , SUM(false, true, true, true), AVG(false, true, true, true); // sum(), avg()

        private final boolean _count;
        private final boolean _scalar;
        private final boolean _uniqueScalar;
        private final boolean _specifiedScalar;

        private SelectClauseType(boolean count, boolean scalar, boolean uniqueScalar, boolean specifiedScalar) {
            _count = count;
            _scalar = scalar;
            _uniqueScalar = uniqueScalar;
            _specifiedScalar = specifiedScalar;
        }

        public boolean isCount() { // except count-distinct
            return _count;
        }

        public boolean isScalar() { // also contains count
            return _scalar;
        }

        /**
         * Should the scalar be selected uniquely?
         * @return The determination, true or false.
         */
        public boolean isUniqueScalar() { // not contains plain-count
            return _uniqueScalar;
        }

        /**
         * Does the scalar need specified only-one column?
         * @return The determination, true or false.
         */
        public boolean isSpecifiedScalar() { // not contains all-count
            return _specifiedScalar;
        }
    }

    // [DBFlute-0.9.8.6]
    // ===================================================================================
    //                                                                  ColumnQuery Object
    //                                                                  ==================
    /**
     * Get the map for ColumnQuery objects for parameter comment. {Internal}
     * @return The map for ColumnQuery objects. (NullAllowed: if null, means no object)
     */
    Map<String, Object> getColumnQueryObjectMap();

    /**
     * Register ColumnQuery object to theme list. {Internal}
     * @param themeKey The key for the object. (NotNull)
     * @param addedValue The value added to theme list for the object. (NotNull)
     * @return The expression for binding. (NotNull)
     */
    String registerColumnQueryObjectToThemeList(String themeKey, Object addedValue);

    // [DBFlute-0.9.8.6]
    // ===================================================================================
    //                                                               ManualOrder Parameter
    //                                                               =====================
    /**
     * Get the map for ManualOrder parameters for parameter comment. {Internal}
     * @return The map for ManualOrder parameters. (NullAllowed: if null, means no parameter)
     */
    Map<String, Object> getManualOrderParameterMap();

    /**
     * Register ManualOrder parameter to theme list. {Internal}
     * @param themeKey The theme as key for the parameter. (NotNull)
     * @param addedValue The value added to theme list for the parameter. (NullAllowed)
     * @return The expression for binding. (NotNull)
     */
    String registerManualOrderParameterToThemeList(String themeKey, Object addedValue);

    // [DBFlute-0.9.8.2]
    // ===================================================================================
    //                                                                      Free Parameter
    //                                                                      ==============
    /**
     * Get the map for free parameters for parameter comment. {Internal}
     * @return The map for free parameters. (NullAllowed: if null, means no parameter)
     */
    Map<String, Object> getFreeParameterMap();

    /**
     * Register free parameter to theme list. {Internal}
     * @param themeKey The theme as key for the parameter. (NotNull)
     * @param addedValue The value added to theme list for the parameter. (NullAllowed)
     * @return The expression for binding. (NotNull)
     */
    String registerFreeParameterToThemeList(String themeKey, Object addedValue);

    // [DBFlute-0.9.8.4]
    // ===================================================================================
    //                                                                       Geared Cipher
    //                                                                       =============
    GearedCipherManager getGearedCipherManager();

    ColumnFunctionCipher findColumnFunctionCipher(ColumnInfo columnInfo);

    void makeSelectColumnDecryptionEffective();

    void suppressSelectColumnDecryption();

    // [DBFlute-0.9.8.4]
    // ===================================================================================
    //                                                                 ScalarSelect Option
    //                                                                 ===================
    void acceptScalarSelectOption(ScalarSelectOption option);

    // [DBFlute-0.9.8.8]
    // ===================================================================================
    //                                                                       Paging Select
    //                                                                       =============
    void makePagingAdjustmentEffective();

    void ignorePagingAdjustment();

    /**
     * Enable paging count-later that means counting after selecting. <br />
     * You should call this before execution of selectPage(). <br />
     * And you should also make paging adjustment effective to enable this. 
     */
    void enablePagingCountLater();

    /**
     * Disable paging count-later that means counting after selecting. <br />
     * You should call this before execution of selectPage().
     */
    void disablePagingCountLater();

    /**
     * Enable paging count-least-join, which means least joined on count select. <br />
     * You should call this before execution of selectPage(). <br />
     * And you should also make paging adjustment effective to enable this.
     */
    void enablePagingCountLeastJoin();

    /**
     * Disable paging count-least-join, which means least joined on count select. <br />
     * You should call this before execution of selectPage().
     */
    void disablePagingCountLeastJoin();

    // [DBFlute-0.9.9.4C]
    // ===================================================================================
    //                                                                      Lazy Reflector
    //                                                                      ==============
    void registerClauseLazyReflector(ClauseLazyReflector clauseLazyReflector);

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * @param fixedValueQueryExpMap The map of query expression for fixed values. (NotNull)
     * @param resourceSqlClause The SQL clause for resource. (NotNull)
     * @return The clause of query-insert. (NotNull)
     */
    String getClauseQueryInsert(Map<String, String> fixedValueQueryExpMap, SqlClause resourceSqlClause);

    /**
     * @param columnParameterMap The map of column parameters. (NotNull)
     * @return The clause of query-update. (NullAllowed: If columnParameterMap is empty, return null)
     */
    String getClauseQueryUpdate(Map<String, String> columnParameterMap);

    /**
     * @return The clause of query-delete. (NotNull)
     */
    String getClauseQueryDelete();

    /**
     * Allow you to use direct clause in query update forcedly (contains query delete).
     * You cannot use join, sub-query, union and so on, by calling this. <br />
     * So you may have the painful SQLException by this, attention!
     */
    void allowQueryUpdateForcedDirect();

    // [DBFlute-0.9.7.2]
    // ===================================================================================
    //                                                                        Purpose Type
    //                                                                        ============
    HpCBPurpose getPurpose();

    void setPurpose(HpCBPurpose purpose);

    // [DBFlute-0.9.4]
    // ===================================================================================
    //                                                                       InScope Limit
    //                                                                       =============
    /**
     * Get the limit of inScope.
     * @return The limit of inScope. (If it's zero or minus, it means no limit)
     */
    int getInScopeLimit();

    // [DBFlute-0.9.8.4]
    // ===================================================================================
    //                                                                   LikeSearch Escape
    //                                                                   =================
    void adjustLikeSearchDBWay(LikeSearchOption option);

    // [DBFlute-0.9.8.4]
    // ===================================================================================
    //                                                                               DBWay
    //                                                                               =====
    DBWay dbway();
}
