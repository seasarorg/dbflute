/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.chelper.HpAbstractSpecification;
import org.seasar.dbflute.cbean.chelper.HpCBPurpose;
import org.seasar.dbflute.cbean.chelper.HpCalcSpecification;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.cipher.ColumnFunctionCipher;
import org.seasar.dbflute.cbean.coption.ScalarSelectOption;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.join.InnerJoinNoWaySpeaker;
import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.query.QueryClause;
import org.seasar.dbflute.cbean.sqlclause.query.QueryClauseFilter;
import org.seasar.dbflute.cbean.sqlclause.query.QueryUsedAliasInfo;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryIndentProcessor;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.exception.ColumnQueryCalculationUnsupportedColumnTypeException;
import org.seasar.dbflute.exception.ConditionInvokingFailureException;
import org.seasar.dbflute.exception.OrScopeQueryAndPartUnsupportedOperationException;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfReflectionUtil.ReflectionFailureException;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The condition-bean as abstract.
 * @author jflute
 */
public abstract class AbstractConditionBean implements ConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** SQL clause instance. */
    protected final SqlClause _sqlClause;
    {
        _sqlClause = createSqlClause();
    }

    /** Safety max result size. {Internal} */
    private int _safetyMaxResultSize;

    /** The configuration of statement. {Internal} (NullAllowed) */
    private StatementConfig _statementConfig;

    /** Is the count executed later? {Internal} */
    private boolean _pagingCountLater; // the default value is on the DBFlute generator

    /** Can the paging re-select? {Internal} */
    private boolean _pagingReSelect = true;

    /** The list of condition-bean for union. {Internal} (NullAllowed) */
    private List<ConditionBean> _unionCBeanList;

    /** The synchronizer of union query. {Internal} (NullAllowed) */
    private UnionQuery<ConditionBean> _unionQuerySynchronizer;

    // -----------------------------------------------------
    //                                          Purpose Type
    //                                          ------------
    /** The purpose of condition-bean. (NotNull) */
    protected HpCBPurpose _purpose = HpCBPurpose.NORMAL_USE; // as default

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * {@inheritDoc}
     */
    public DBMeta getDBMeta() {
        return getDBMetaProvider().provideDBMetaChecked(getTableDbName());
    }

    // ===================================================================================
    //                                                                           SqlClause
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public SqlClause getSqlClause() {
        return _sqlClause;
    }

    /**
     * Create SQL clause. {for condition-bean}
     * @return SQL clause. (NotNull)
     */
    protected abstract SqlClause createSqlClause();

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    /**
     * Get the provider of DB meta.
     * @return The provider of DB meta. (NotNull)
     */
    protected abstract DBMetaProvider getDBMetaProvider();

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============
    protected void doSetupSelect(SsCall callback) {
        final String foreignPropertyName = callback.qf().xgetForeignPropertyName();
        assertSetupSelectPurpose(foreignPropertyName);
        assertSetupSelectBeforeUnion(foreignPropertyName);
        final String foreignTableAliasName = callback.qf().xgetAliasName();
        final String localRelationPath = localCQ().xgetRelationPath();
        final String foreignRelationPath = callback.qf().xgetRelationPath();
        getSqlClause().registerSelectedRelation(foreignTableAliasName, getTableDbName(), foreignPropertyName,
                localRelationPath, foreignRelationPath);
    }

    protected static interface SsCall {
        public ConditionQuery qf();
    }

    protected void assertSetupSelectPurpose(String foreignPropertyName) {
        if (_purpose.isNoSetupSelect()) {
            final String titleName = DfTypeUtil.toClassTitle(this);
            throwSetupSelectIllegalPurposeException(titleName, foreignPropertyName);
        }
    }

    protected void throwSetupSelectIllegalPurposeException(String className, String foreignPropertyName) {
        createCBExThrower().throwSetupSelectIllegalPurposeException(_purpose, this, foreignPropertyName);
    }

    protected void assertSetupSelectBeforeUnion(String foreignPropertyName) {
        if (hasUnionQueryOrUnionAllQuery()) {
            throwSetupSelectAfterUnionException(foreignPropertyName);
        }
    }

    protected void throwSetupSelectAfterUnionException(String foreignPropertyName) {
        createCBExThrower().throwSetupSelectAfterUnionException(this, foreignPropertyName);
    }

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected abstract boolean hasSpecifiedColumn();

    protected abstract HpAbstractSpecification<? extends ConditionQuery> localSp();

    protected void assertSpecifyPurpose() {
        if (_purpose.isNoSpecify()) {
            throwSpecifyIllegalPurposeException();
        }
    }

    protected void throwSpecifyIllegalPurposeException() {
        createCBExThrower().throwSpecifyIllegalPurposeException(_purpose, this);
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    protected void assertQueryPurpose() {
        if (_purpose.isNoQuery()) {
            throwQueryIllegalPurposeException();
        }
    }

    protected void throwQueryIllegalPurposeException() {
        createCBExThrower().throwQueryIllegalPurposeException(_purpose, this);
    }

    /**
     * {@inheritDoc}
     */
    public void allowInnerJoinAutoDetect() {
        getSqlClause().allowInnerJoinAutoDetect();
    }

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                         ColumnQuery
    //                                                                         ===========
    protected <CB extends ConditionBean> HpCalculator xcolqy(CB leftCB, CB rightCB, SpecifyQuery<CB> leftSp,
            SpecifyQuery<CB> rightSp, final String operand) {
        assertQueryPurpose();

        final HpCalcSpecification<CB> leftCalcSp = xcreateCalcSpecification(leftSp);
        leftCalcSp.specify(leftCB);
        final String leftColumn = xbuildLeftColumn(leftCB, leftCalcSp);

        final HpCalcSpecification<CB> rightCalcSp = xcreateCalcSpecification(rightSp);
        rightCalcSp.specify(rightCB);
        final String rightColumn = xbuildRightColumn(rightCB, rightCalcSp);
        rightCalcSp.setLeftCalcSp(leftCalcSp);

        final QueryClause queryClause = xcreateColQyClause(leftColumn, operand, rightColumn, rightCalcSp);
        xregisterColQyClause(queryClause, leftCalcSp, rightCalcSp);
        return rightCalcSp;
    }

    protected <CB extends ConditionBean> String xbuildLeftColumn(CB leftCB, HpCalcSpecification<CB> leftCalcSp) {
        final ColumnRealName realName = leftCalcSp.getResolvedSpecifiedColumnRealName();
        if (realName == null) {
            createCBExThrower().throwColumnQueryInvalidColumnSpecificationException();
        }
        return xbuildColQyColumn(leftCB, realName.toString(), "left");
    }

    protected <CB extends ConditionBean> String xbuildRightColumn(CB rightCB, HpCalcSpecification<CB> rightCalcSp) {
        final ColumnRealName realName = rightCalcSp.getResolvedSpecifiedColumnRealName();
        if (realName == null) {
            createCBExThrower().throwColumnQueryInvalidColumnSpecificationException();
        }
        return xbuildColQyColumn(rightCB, realName.toString(), "right");
    }

    protected <CB extends ConditionBean> String xbuildColQyColumn(CB cb, String source, String themeKey) {
        final String bindingExp = getSqlClause().registerColumnQueryObjectToThemeList(themeKey, cb);
        return Srl.replace(source, "/*pmb.conditionQuery.", bindingExp);
    }

    protected <CB extends ConditionBean> HpCalcSpecification<CB> xcreateCalcSpecification(SpecifyQuery<CB> rightSp) {
        return new HpCalcSpecification<CB>(rightSp, this);
    }

    protected <CB extends ConditionBean> QueryClause xcreateColQyClause(final String leftColumn, final String operand,
            final String rightColumn, final HpCalcSpecification<CB> rightCalcSp) {
        return new QueryClause() {
            @Override
            public String toString() {
                final String leftExp = resolveColumnExp(rightCalcSp.getLeftCalcSp(), leftColumn);
                final String rightExp = resolveColumnExp(rightCalcSp, rightColumn);
                return xbuildColQyClause(leftExp, operand, rightExp);
            }

            protected String resolveColumnExp(HpCalcSpecification<CB> calcSp, String columnExp) {
                final String resolvedExp;
                if (calcSp != null) {
                    final String statement = calcSp.buildStatementToSpecifidName(columnExp);
                    if (statement != null) { // exists calculation
                        assertCalculationColumnType(calcSp);
                        resolvedExp = statement; // cipher already resolved
                    } else {
                        final ColumnInfo columnInfo = calcSp.getSpecifiedColumnInfo();
                        if (columnInfo != null) { // means plain column
                            resolvedExp = decrypt(columnInfo, columnExp);
                        } else { // deriving sub-query
                            resolvedExp = columnExp;
                        }
                    }
                } else {
                    resolvedExp = columnExp;
                }
                return resolvedExp;
            }

            protected void assertCalculationColumnType(HpCalcSpecification<CB> calcSp) {
                if (calcSp.hasConvert()) {
                    return; // because it may be Date type
                }
                final ColumnInfo columnInfo = calcSp.getResolvedSpecifiedColumnInfo();
                if (columnInfo != null) { // basically true but checked just in case
                    if (!columnInfo.isPropertyTypeNumber()) {
                        // *simple message because other types may be supported at the future
                        String msg = "Not number column specified: " + columnInfo;
                        throw new ColumnQueryCalculationUnsupportedColumnTypeException(msg);
                    }
                }
            }

            protected String xbuildColQyClause(String leftExp, String operand, String rightExp) {
                final StringBuilder sb = new StringBuilder();
                if (hasSubQueryEndOnLastLine(leftExp)) {
                    if (hasSubQueryEndOnLastLine(rightExp)) { // (sub-query = sub-query)
                        // add line separator before right expression
                        // because of independent format for right query
                        sb.append(reflectToSubQueryEndOnLastLine(leftExp, " " + operand + " "));
                        sb.append(ln() + "       ").append(rightExp);
                    } else { // (sub-query = column)
                        sb.append(reflectToSubQueryEndOnLastLine(leftExp, " " + operand + " " + rightExp));
                    }
                } else { // (column = sub-query) or (column = column) 
                    sb.append(leftExp).append(" ").append(operand).append(" ").append(rightExp);
                }
                return sb.toString();
            }
        };
    }

    protected boolean hasSubQueryEndOnLastLine(String columnExp) {
        return SubQueryIndentProcessor.hasSubQueryEndOnLastLine(columnExp);
    }

    protected String reflectToSubQueryEndOnLastLine(String columnExp, String inserted) {
        return SubQueryIndentProcessor.moveSubQueryEndToRear(columnExp + inserted);
    }

    protected <CB extends ConditionBean> void xregisterColQyClause(QueryClause queryClause,
            final HpCalcSpecification<CB> leftCalcSp, final HpCalcSpecification<CB> rightCalcSp) {
        // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
        // may null-revived -> no way to be inner-join
        // (DerivedReferrer or conversion's coalesce)
        // 
        // for example, the following SQL is no way to be inner
        // (suppose if PURCHASE refers WITHDRAWAL)
        // 
        // select mb.MEMBER_ID, mb.MEMBER_NAME
        //      , mb.MEMBER_STATUS_CODE, wd.MEMBER_ID as WD_MEMBER_ID
        //   from MEMBER mb
        //     left outer join MEMBER_SERVICE ser on mb.MEMBER_ID = ser.MEMBER_ID
        //     left outer join MEMBER_WITHDRAWAL wd on mb.MEMBER_ID = wd.MEMBER_ID
        //  where (select coalesce(max(pc.PURCHASE_PRICE), 0)
        //           from PURCHASE pc
        //          where pc.MEMBER_ID = wd.MEMBER_ID -- may null
        //        ) < ser.SERVICE_POINT_COUNT
        //  order by mb.MEMBER_ID
        // 
        // it has a possible to be inner-join in various case
        // but it is hard to analyze in detail so simplify it
        // = = = = = = = = = =/
        final String leftAlias = leftCalcSp.getResolvedSpecifiedTableAliasName();
        final String rightAlias = rightCalcSp.getResolvedSpecifiedTableAliasName();
        final QueryUsedAliasInfo leftInfo = xcreateColQyAlsInfo(leftAlias, leftCalcSp);
        final QueryUsedAliasInfo rightInfo = xcreateColQyAlsInfo(rightAlias, rightCalcSp);
        getSqlClause().registerWhereClause(queryClause, leftInfo, rightInfo);
    }

    protected <CB extends ConditionBean> QueryUsedAliasInfo xcreateColQyAlsInfo(final String usedAliasName,
            final HpCalcSpecification<CB> calcSp) {
        return new QueryUsedAliasInfo(usedAliasName, new InnerJoinNoWaySpeaker() {
            public boolean isNoWayInner() {
                return calcSp.mayNullRevived();
            }
        });
    }

    // [DBFlute-0.9.6.3]
    // ===================================================================================
    //                                                                        OrScopeQuery
    //                                                                        ============
    protected <CB extends ConditionBean> void xorSQ(CB cb, OrQuery<CB> orQuery) {
        assertQueryPurpose();
        if (getSqlClause().isOrScopeQueryAndPartEffective()) {
            // limit because of so complex
            String msg = "The OrScopeQuery in and-part is unsupported: " + getTableDbName();
            throw new OrScopeQueryAndPartUnsupportedOperationException(msg);
        }
        xdoOrSQ(cb, orQuery);
    }

    protected <CB extends ConditionBean> void xdoOrSQ(CB cb, OrQuery<CB> orQuery) {
        getSqlClause().makeOrScopeQueryEffective();
        try {
            orQuery.query(cb);
        } finally {
            getSqlClause().closeOrScopeQuery();
        }
    }

    protected <CB extends ConditionBean> void xorSQAP(CB cb, AndQuery<CB> andQuery) {
        assertQueryPurpose();
        if (!getSqlClause().isOrScopeQueryEffective()) {
            createCBExThrower().throwOrScopeQueryAndPartNotOrScopeException(cb);
        }
        if (getSqlClause().isOrScopeQueryAndPartEffective()) {
            createCBExThrower().throwOrScopeQueryAndPartAlreadySetupException(cb);
        }
        getSqlClause().beginOrScopeQueryAndPart();
        try {
            andQuery.query(cb);
        } finally {
            getSqlClause().endOrScopeQueryAndPart();
        }
    }

    // ===================================================================================
    //                                                                       Invalid Query
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public void allowEmptyStringQuery() {
        getSqlClause().allowEmptyStringQuery();
    }

    /**
     * {@inheritDoc}
     */
    public void checkInvalidQuery() {
        getSqlClause().checkInvalidQuery();
    }

    // ===================================================================================
    //                                                                   Accept PrimaryKey
    //                                                                   =================
    /**
     * {@inheritDoc}
     */
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        if (!getDBMeta().hasPrimaryKey()) {
            String msg = "The table has no primary-keys: " + getTableDbName();
            throw new UnsupportedOperationException(msg);
        }
        final Entity entity = getDBMeta().newEntity();
        getDBMeta().acceptPrimaryKeyMap(entity, primaryKeyMap);
        final Map<String, Object> filteredMap = getDBMeta().extractPrimaryKeyMap(entity);
        final Set<Entry<String, Object>> entrySet = filteredMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            localCQ().invokeQuery(entry.getKey(), "equal", entry.getValue());
        }
    }

    // ===================================================================================
    //                                                        Implementation of PagingBean
    //                                                        ============================
    // -----------------------------------------------------
    //                                  Paging Determination
    //                                  --------------------
    /**
     * {@inheritDoc}
     */
    public boolean isPaging() { // for parameter comment
        String msg = "This method is unsupported on ConditionBean!";
        throw new UnsupportedOperationException(msg);
    }

    /**
     * {@inheritDoc}
     */
    public boolean canPagingCountLater() { // for framework
        return _pagingCountLater;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canPagingReSelect() { // for framework
        return _pagingReSelect;
    }

    // -----------------------------------------------------
    //                                        Paging Setting
    //                                        --------------
    /**
     * {@inheritDoc}
     */
    public void paging(int pageSize, int pageNumber) {
        if (pageSize <= 0) {
            throwPagingPageSizeNotPlusException(pageSize, pageNumber);
        }
        fetchFirst(pageSize);
        fetchPage(pageNumber);
    }

    protected void throwPagingPageSizeNotPlusException(int pageSize, int pageNumber) {
        createCBExThrower().throwPagingPageSizeNotPlusException(this, pageSize, pageNumber);
    }

    /**
     * {@inheritDoc}
     */
    public void xsetPaging(boolean paging) {
        // Do nothing because this is unsupported on ConditionBean.
        // And it is possible that this method is called by PagingInvoker.
    }

    /**
     * {@inheritDoc}
     */
    public void enablePagingCountLater() {
        _pagingCountLater = true;
        getSqlClause().enablePagingCountLater(); // tell her about it
    }

    /**
     * {@inheritDoc}
     */
    public void disablePagingCountLater() {
        _pagingCountLater = false;
        getSqlClause().disablePagingCountLater(); // tell her about it
    }

    /**
     * {@inheritDoc}
     */
    public void enablePagingReSelect() {
        _pagingReSelect = true;
    }

    /**
     * {@inheritDoc}
     */
    public void disablePagingReSelect() {
        _pagingReSelect = false;
    }

    // ConditionBean original
    /**
     * {@inheritDoc}
     */
    public void enablePagingCountLeastJoin() {
        getSqlClause().enablePagingCountLeastJoin();
    }

    /**
     * {@inheritDoc}
     */
    public void disablePagingCountLeastJoin() {
        getSqlClause().disablePagingCountLeastJoin();
    }

    // -----------------------------------------------------
    //                                         Fetch Setting
    //                                         -------------
    /**
     * {@inheritDoc}
     */
    public PagingBean fetchFirst(int fetchSize) {
        getSqlClause().fetchFirst(fetchSize);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PagingBean fetchScope(int fetchStartIndex, int fetchSize) {
        getSqlClause().fetchScope(fetchStartIndex, fetchSize);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PagingBean fetchPage(int fetchPageNumber) {
        getSqlClause().fetchPage(fetchPageNumber);
        return this;
    }

    // -----------------------------------------------------
    //                                       Paging Resource
    //                                       ---------------
    /**
     * {@inheritDoc}
     */
    public <ENTITY> PagingInvoker<ENTITY> createPagingInvoker(String tableDbName) {
        return new PagingInvoker<ENTITY>(tableDbName);
    }

    // -----------------------------------------------------
    //                                        Fetch Property
    //                                        --------------
    /**
     * {@inheritDoc}
     */
    public int getFetchStartIndex() {
        return getSqlClause().getFetchStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchSize() {
        return getSqlClause().getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchPageNumber() {
        return getSqlClause().getFetchPageNumber();
    }

    /**
     * {@inheritDoc}
     */
    public int getPageStartIndex() {
        return getSqlClause().getPageStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getPageEndIndex() {
        return getSqlClause().getPageEndIndex();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchScopeEffective() {
        return getSqlClause().isFetchScopeEffective();
    }

    // -----------------------------------------------------
    //                                         Hint Property
    //                                         -------------
    /**
     * Get select-hint. {select [select-hint] * from table...}
     * @return select-hint. (NotNull)
     */
    public String getSelectHint() {
        return getSqlClause().getSelectHint();
    }

    /**
     * Get from-base-table-hint. {select * from table [from-base-table-hint] where ...}
     * @return from-base-table-hint. (NotNull)
     */
    public String getFromBaseTableHint() {
        return getSqlClause().getFromBaseTableHint();
    }

    /**
     * Get from-hint. {select * from table left outer join ... on ... [from-hint] where ...}
     * @return from-hint. (NotNull)
     */
    public String getFromHint() {
        return getSqlClause().getFromHint();
    }

    /**
     * Get sql-suffix. {select * from table where ... order by ... [sql-suffix]}
     * @return Sql-suffix.  (NotNull)
     */
    public String getSqlSuffix() {
        return getSqlClause().getSqlSuffix();
    }

    // ===================================================================================
    //                                                         Implementation of FetchBean
    //                                                         ===========================
    /**
     * {@inheritDoc}
     */
    public void checkSafetyResult(int safetyMaxResultSize) {
        this._safetyMaxResultSize = safetyMaxResultSize;
    }

    /**
     * {@inheritDoc}
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    // ===================================================================================
    //                                                Implementation of FetchNarrowingBean
    //                                                ====================================
    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingSkipStartIndex() {
        return getSqlClause().getFetchNarrowingSkipStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingLoopCount() {
        return getSqlClause().getFetchNarrowingLoopCount();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingSkipStartIndexEffective() {
        return !getSqlClause().isFetchStartIndexSupported();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingLoopCountEffective() {
        return !getSqlClause().isFetchSizeSupported();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingEffective() {
        return getSqlClause().isFetchNarrowingEffective();
    }

    /**
     * {@inheritDoc}
     */
    public void ignoreFetchNarrowing() {
        String msg = "This method is unsupported on ConditionBean!";
        throw new UnsupportedOperationException(msg);
    }

    /**
     * {@inheritDoc}
     */
    public void restoreIgnoredFetchNarrowing() {
        // Do nothing!
    }

    // ===================================================================================
    //                                                       Implementation of OrderByBean
    //                                                       =============================
    /**
     * {@inheritDoc}
     */
    public String getOrderByClause() {
        return _sqlClause.getOrderByClause();
    }

    /**
     * {@inheritDoc}
     */
    public OrderByClause getOrderByComponent() {
        return getSqlClause().getOrderByComponent();
    }

    /**
     * {@inheritDoc}
     */
    public OrderByBean clearOrderBy() {
        getSqlClause().clearOrderBy();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public OrderByBean ignoreOrderBy() {
        getSqlClause().ignoreOrderBy();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public OrderByBean makeOrderByEffective() {
        getSqlClause().makeOrderByEffective();
        return this;
    }

    // ===================================================================================
    //                                                                        Lock Setting
    //                                                                        ============
    /**
     * {@inheritDoc}
     */
    public ConditionBean lockForUpdate() {
        getSqlClause().lockForUpdate();
        return this;
    }

    // ===================================================================================
    //                                                                        Select Count
    //                                                                        ============
    /**
     * {@inheritDoc}
     */
    public ConditionBean xsetupSelectCountIgnoreFetchScope(boolean uniqueCount) {
        _isSelectCountIgnoreFetchScope = true;

        final SqlClause.SelectClauseType clauseType;
        if (uniqueCount) {
            clauseType = SqlClause.SelectClauseType.UNIQUE_COUNT;
        } else {
            clauseType = SqlClause.SelectClauseType.PLAIN_COUNT;
        }
        getSqlClause().classifySelectClauseType(clauseType);
        getSqlClause().ignoreOrderBy();
        getSqlClause().ignoreFetchScope();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ConditionBean xafterCareSelectCountIgnoreFetchScope() {
        _isSelectCountIgnoreFetchScope = false;

        getSqlClause().rollbackSelectClauseType();
        getSqlClause().makeOrderByEffective();
        getSqlClause().makeFetchScopeEffective();
        return this;
    }

    /** Is set up various things for select-count-ignore-fetch-scope? */
    protected boolean _isSelectCountIgnoreFetchScope;

    /**
     * {@inheritDoc}
     */
    public boolean isSelectCountIgnoreFetchScope() {
        return _isSelectCountIgnoreFetchScope;
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public void xacceptScalarSelectOption(ScalarSelectOption option) {
        getSqlClause().acceptScalarSelectOption(option);
    }

    // ===================================================================================
    //                                                                     StatementConfig
    //                                                                     ===============
    /**
     * {@inheritDoc}
     */
    public void configure(StatementConfig statementConfig) {
        _statementConfig = statementConfig;
    }

    /**
     * {@inheritDoc}
     */
    public StatementConfig getStatementConfig() {
        return _statementConfig;
    }

    // ===================================================================================
    //                                                                     Embed Condition
    //                                                                     ===============
    /**
     * Embed conditions in their variables on where clause (and 'on' clause). <br />
     * You should not use this normally. It's a final weapon! <br />
     * And that this method is not perfect so be attention! <br />
     * If the same-name-columns exist in your conditions, both are embedded. <br />
     * And an empty set means that all conditions are target.
     * @param embeddedColumnInfoSet The set of embedded target column information. (NotNull)
     * @param quote Should the conditions value be quoted?
     * @deprecated You should not use this easily. It's a dangerous function.
     */
    public void embedCondition(Set<ColumnInfo> embeddedColumnInfoSet, boolean quote) {
        if (embeddedColumnInfoSet == null) {
            String msg = "The argument[embedCondition] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (quote) {
            addWhereClauseSimpleFilter(newToEmbeddedQuotedSimpleFilter(embeddedColumnInfoSet));
        } else {
            addWhereClauseSimpleFilter(newToEmbeddedSimpleFilter(embeddedColumnInfoSet));
        }
    }

    private QueryClauseFilter newToEmbeddedQuotedSimpleFilter(Set<ColumnInfo> embeddedColumnInfoSet) {
        return new QueryClauseFilter.QueryClauseToEmbeddedQuotedSimpleFilter(embeddedColumnInfoSet);
    }

    private QueryClauseFilter newToEmbeddedSimpleFilter(Set<ColumnInfo> embeddedColumnInfoSet) {
        return new QueryClauseFilter.QueryClauseToEmbeddedSimpleFilter(embeddedColumnInfoSet);
    }

    private void addWhereClauseSimpleFilter(QueryClauseFilter whereClauseSimpleFilter) {
        this._sqlClause.addWhereClauseSimpleFilter(whereClauseSimpleFilter);
    }

    // ===================================================================================
    //                                                                          DisplaySQL
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public String toDisplaySql() {
        final SqlAnalyzerFactory factory = getSqlAnalyzerFactory();
        final String dateFormat = getLogDateFormat();
        final String timestampFormat = getLogTimestampFormat();
        return ConditionBeanContext.convertConditionBean2DisplaySql(factory, this, dateFormat, timestampFormat);
    }

    protected abstract SqlAnalyzerFactory getSqlAnalyzerFactory();

    protected abstract String getLogDateFormat();

    protected abstract String getLogTimestampFormat();

    // [DBFlute-0.9.5.2]
    // ===================================================================================
    //                                                          Basic Status Determination
    //                                                          ==========================
    /**
     * {@inheritDoc}
     */
    public boolean hasWhereClause() {
        if (!getSqlClause().hasWhereClauseOnBase() && !getSqlClause().hasBaseTableInlineWhereClause()) {
            return false;
        }
        // mainCB has clauses here
        if (_unionCBeanList == null || _unionCBeanList.isEmpty()) {
            return true; // no union
        }
        // mainCB has unions
        for (ConditionBean unionCB : _unionCBeanList) {
            if (!unionCB.hasWhereClause()) {
                return false;
            }
        }
        return true; // means all unions have clauses
    }

    /**
     * {@inheritDoc}
     */
    public void clearWhereClause() {
        getSqlClause().clearWhereClauseOnBase();
        getSqlClause().clearBaseTableInlineWhereClause();
        getSqlClause().clearUnionQuery();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasOrderByClause() {
        return getSqlClause().hasOrderByClause();
    }

    // [DBFlute-0.9.6.7]
    // ===================================================================================
    //                                                                 Reflection Invoking
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    public void invokeSetupSelect(String foreignPropertyNamePath) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyNamePath", foreignPropertyNamePath);
        final String delimiter = ".";
        Object currentObj = this;
        String remainder = foreignPropertyNamePath;
        int count = 0;
        boolean last = false;
        while (true) {
            final int deimiterIndex = remainder.indexOf(delimiter);
            final String propertyName;
            if (deimiterIndex < 0) {
                propertyName = remainder;
                last = true;
            } else {
                propertyName = remainder.substring(0, deimiterIndex);
                remainder = remainder.substring(deimiterIndex + delimiter.length(), remainder.length());
            }
            final String methodName = (count == 0 ? "setupSelect_" : "with") + initCap(propertyName);
            final Method method = DfReflectionUtil
                    .getPublicMethod(currentObj.getClass(), methodName, new Class<?>[] {});
            if (method == null) {
                String msg = "Not found the method for setupSelect:";
                msg = msg + " foreignPropertyNamePath=" + foreignPropertyNamePath;
                msg = msg + " methodName=" + methodName;
                throw new ConditionInvokingFailureException(msg);
            }
            try {
                currentObj = DfReflectionUtil.invoke(method, currentObj, new Object[] {});
            } catch (ReflectionFailureException e) {
                String msg = "Failed to invoke the method:";
                msg = msg + " foreignPropertyNamePath=" + foreignPropertyNamePath;
                msg = msg + " methodName=" + methodName;
                throw new ConditionInvokingFailureException(msg, e);
            }
            ++count;
            if (last) {
                break;
            }
        }
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void xsaveUCB(ConditionBean unionCB) {
        if (_unionCBeanList == null) {
            _unionCBeanList = new ArrayList<ConditionBean>();
        }
        // save for, for example, hasWhereClause()
        _unionCBeanList.add(unionCB);
    }

    /**
     * Synchronize union-query. {Internal}
     * @param unionCB The condition-bean for union. (NotNull)
     */
    protected void xsyncUQ(ConditionBean unionCB) { // synchronizeUnionQuery()
        if (_unionQuerySynchronizer != null) {
            _unionQuerySynchronizer.query(unionCB);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void xregisterUnionQuerySynchronizer(UnionQuery<ConditionBean> unionQuerySynchronizer) {
        _unionQuerySynchronizer = unionQuerySynchronizer;
    }

    // [DBFlute-0.9.8.4]
    // ===================================================================================
    //                                                                       Geared Cipher
    //                                                                       =============
    protected String decrypt(ColumnInfo columnInfo, String valueExp) {
        final ColumnFunctionCipher cipher = getSqlClause().findColumnFunctionCipher(columnInfo);
        return cipher != null ? cipher.decrypt(valueExp) : valueExp;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                        Purpose Type
    //                                                                        ============
    public HpCBPurpose getPurpose() {
        return _purpose;
    }

    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // very internal (super very important)
    // these are called immediate after creation of condition-bean
    // because there are important initializations here
    // - - - - - - - - - -/

    public void xsetupForUnion(ConditionBean mainCB) {
        xinheritSubQueryInfo(mainCB.localCQ());
        xchangePurposeSqlClause(HpCBPurpose.UNION_QUERY, mainCB.localCQ());
    }

    public void xsetupForExistsReferrer(ConditionQuery mainCQ) {
        xprepareSubQueryInfo(mainCQ);
        xchangePurposeSqlClause(HpCBPurpose.EXISTS_REFERRER, mainCQ);
    }

    public void xsetupForInScopeRelation(ConditionQuery mainCQ) {
        xprepareSubQueryInfo(mainCQ);
        xchangePurposeSqlClause(HpCBPurpose.IN_SCOPE_RELATION, mainCQ);
    }

    public void xsetupForDerivedReferrer(ConditionQuery mainCQ) {
        xprepareSubQueryInfo(mainCQ);
        xchangePurposeSqlClause(HpCBPurpose.DERIVED_REFERRER, mainCQ);
    }

    public void xsetupForScalarSelect() { // not sub-query (used independently)
        xchangePurposeSqlClause(HpCBPurpose.SCALAR_SELECT, null);
    }

    public void xsetupForScalarCondition(ConditionQuery mainCQ) {
        xprepareSubQueryInfo(mainCQ);
        xchangePurposeSqlClause(HpCBPurpose.SCALAR_CONDITION, mainCQ);
    }

    public void xsetupForMyselfInScope(ConditionQuery mainCQ) {
        xprepareSubQueryInfo(mainCQ);
        xchangePurposeSqlClause(HpCBPurpose.MYSELF_IN_SCOPE, mainCQ);
    }

    public void xsetupForQueryInsert() { // not sub-query (used independently)
        xchangePurposeSqlClause(HpCBPurpose.QUERY_INSERT, null);
        getSqlClause().suppressSelectColumnDecryption(); // suppress cipher for values from DB to DB
    }

    public void xsetupForColumnQuery(ConditionBean mainCB) {
        xinheritSubQueryInfo(mainCB.localCQ());
        xchangePurposeSqlClause(HpCBPurpose.COLUMN_QUERY, mainCB.localCQ());

        // inherits a parent query to synchronize real name
        // (and also for suppressing query check) 
        xprepareSyncQyCall(mainCB);
    }

    public void xsetupForVaryingUpdate() {
        xchangePurposeSqlClause(HpCBPurpose.VARYING_UPDATE, null);
        xprepareSyncQyCall(null); // for suppressing query check
    }

    public void xsetupForSpecifiedUpdate() {
        xchangePurposeSqlClause(HpCBPurpose.SPECIFIED_UPDATE, null);
        xprepareSyncQyCall(null); // for suppressing query check
    }

    protected void xinheritSubQueryInfo(ConditionQuery mainCQ) {
        if (mainCQ.xgetSqlClause().isForSubQuery()) {
            getSqlClause().setupForSubQuery(mainCQ.xgetSqlClause().getSubQueryLevel()); // inherited
        }
    }

    protected void xprepareSubQueryInfo(ConditionQuery mainCQ) {
        final int nextSubQueryLevel = mainCQ.xgetSqlClause().getSubQueryLevel() + 1;
        getSqlClause().setupForSubQuery(nextSubQueryLevel); // incremented
    }

    protected void xchangePurposeSqlClause(HpCBPurpose purpose, ConditionQuery mainCQ) {
        _purpose = purpose;
        getSqlClause().setPurpose(purpose); // synchronize
        if (mainCQ != null) {
            // all sub condition-query are target
            // (purposes not allowed to use query() also may have nested query())
            xinheritInvalidQueryInfo(mainCQ);

            // and also inherits inner-join
            xinheritStructurePossibleInnerJoin(mainCQ);
            xinheritWhereUsedInnerJoin(mainCQ);
        }
    }

    protected void xinheritInvalidQueryInfo(ConditionQuery mainCQ) {
        if (mainCQ.xgetSqlClause().isEmptyStringQueryAllowed()) {
            allowEmptyStringQuery();
        }
        if (mainCQ.xgetSqlClause().isInvalidQueryChecked()) {
            checkInvalidQuery();
        }
    }

    protected void xinheritStructurePossibleInnerJoin(ConditionQuery mainCQ) {
        // inherited
        if (mainCQ.xgetSqlClause().isStructuralPossibleInnerJoinAllowed()) { // DBFlute default
            getSqlClause().allowStructuralPossibleInnerJoin();
        } else { // e.g. if it suppresses it by DBFlute property
            getSqlClause().suppressStructuralPossibleInnerJoin();
        }
    }

    protected void xinheritWhereUsedInnerJoin(ConditionQuery mainCQ) {
        if (mainCQ.xgetSqlClause().isWhereUsedInnerJoinAllowed()) { // DBFlute default
            getSqlClause().allowWhereUsedInnerJoin();
        } else { // e.g. if it suppresses it by DBFlute property
            getSqlClause().suppressWhereUsedInnerJoin();
        }
    }

    protected abstract void xprepareSyncQyCall(ConditionBean mainCB);

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the string is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull("value", value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String titleName = DfTypeUtil.toClassTitle(this);
        sb.append(titleName).append(":");
        try {
            final String displaySql = toDisplaySql();
            sb.append(ln()).append(displaySql);
        } catch (RuntimeException e) {
            sb.append("{toDisplaySql() failed}");
        }
        return sb.toString();
    }
}
