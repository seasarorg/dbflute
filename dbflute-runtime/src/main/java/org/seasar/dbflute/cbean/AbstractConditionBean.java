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
package org.seasar.dbflute.cbean;

import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.seasar.dbflute.cbean.chelper.HpAbstractSpecification;
import org.seasar.dbflute.cbean.sqlclause.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.WhereClauseSimpleFilter;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.exception.ConditionInvokingFailureException;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.helper.mapstring.MapListString;
import org.seasar.dbflute.helper.mapstring.impl.MapListStringImpl;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.DfReflectionUtil.ReflectionFailureException;

/**
 * The condition-bean as abstract.
 * @author jflute
 */
public abstract class AbstractConditionBean implements ConditionBean {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Map-string map-mark. */
    private static final String MAP_STRING_MAP_MARK = "map:";

    /** Map-string list-mark. */
    private static final String MAP_STRING_LIST_MARK = "list:";

    /** Map-string start-brace. */
    private static final String MAP_STRING_START_BRACE = "@{";

    /** Map-string end-brace. */
    private static final String MAP_STRING_END_BRACE = "@}";

    /** Map-string delimiter. */
    private static final String MAP_STRING_DELIMITER = "@;";

    /** Map-string equal. */
    private static final String MAP_STRING_EQUAL = "@=";

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

    /** The configuration of statement. {Internal} (Nullable) */
    private StatementConfig _statementConfig;

    /** Is the count executed later? {Internal} */
    private boolean _pagingCountLater;

    /** Can the paging re-select? {Internal} */
    private boolean _pagingReSelect = true;

    /** The map for free parameters. {Internal} (Nullable) */
    private Map<String, Object> _freeParameterMap;

    /** The synchronizer of union query. {Internal} (Nullable) */
    private UnionQuery<ConditionBean> _unionQuerySynchronizer;

    // -----------------------------------------------------
    //                                          Purpose Type
    //                                          ------------
    protected boolean _forDerivedReferrer;
    protected boolean _forScalarSelect;
    protected boolean _forScalarSubQuery;
    protected boolean _forUnion;
    protected boolean _forExistsSubQuery;
    protected boolean _forInScopeSubQuery;

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
    //                                                                     Embed Condition
    //                                                                     ===============
    /**
     * Embed conditions in their variables on where clause (and 'on' clause). <br />
     * You should not use this normally. It's a final weapon! <br />
     * And that this method is not perfect so be attention! <br />
     * If the same-name-columns exist in your conditions, both are embedded.
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

    private WhereClauseSimpleFilter newToEmbeddedQuotedSimpleFilter(Set<ColumnInfo> embeddedColumnInfoSet) {
        return new WhereClauseSimpleFilter.WhereClauseToEmbeddedQuotedSimpleFilter(embeddedColumnInfoSet);
    }

    private WhereClauseSimpleFilter newToEmbeddedSimpleFilter(Set<ColumnInfo> embeddedColumnInfoSet) {
        return new WhereClauseSimpleFilter.WhereClauseToEmbeddedSimpleFilter(embeddedColumnInfoSet);
    }

    private void addWhereClauseSimpleFilter(WhereClauseSimpleFilter whereClauseSimpleFilter) {
        this._sqlClause.addWhereClauseSimpleFilter(whereClauseSimpleFilter);
    }

    // ===================================================================================
    //                                                                   Accept PrimaryKey
    //                                                                   =================
    /**
     * {@inheritDoc}
     */
    public void acceptPrimaryKeyMapString(String primaryKeyMapString) {
        if (primaryKeyMapString == null) {
            String msg = "The argument[primaryKeyMapString] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final String prefix = MAP_STRING_MAP_MARK + MAP_STRING_START_BRACE;
        final String suffix = MAP_STRING_END_BRACE;
        if (!primaryKeyMapString.trim().startsWith(prefix)) {
            primaryKeyMapString = prefix + primaryKeyMapString;
        }
        if (!primaryKeyMapString.trim().endsWith(suffix)) {
            primaryKeyMapString = primaryKeyMapString + suffix;
        }
        MapListString mapListString = new MapListStringImpl();
        mapListString.setMapMark(MAP_STRING_MAP_MARK);
        mapListString.setListMark(MAP_STRING_LIST_MARK);
        mapListString.setDelimiter(MAP_STRING_DELIMITER);
        mapListString.setStartBrace(MAP_STRING_START_BRACE);
        mapListString.setEndBrace(MAP_STRING_END_BRACE);
        mapListString.setEqual(MAP_STRING_EQUAL);
        acceptPrimaryKeyMap(mapListString.generateMap(primaryKeyMapString));
    }

    protected void xcheckTypeString(Object value, String propertyName, Class<?> type) {
        if (value == null) {
            throw new IllegalArgumentException("The value should not be null: " + propertyName);
        }
        if (!(value instanceof String)) {
            String typeName = DfTypeUtil.toClassTitle(type);
            String msg = "The value of " + propertyName + " should be " + typeName + " or String: ";
            msg = msg + "valueType=" + value.getClass() + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    @SuppressWarnings("unchecked")
    protected <NUMBER extends Number> NUMBER xparseNumber(Object obj, String propertyName, Class<NUMBER> type) {
        xcheckTypeString(obj, propertyName, type);
        return (NUMBER) DfTypeUtil.toNumber(obj, type);
    }

    @SuppressWarnings("unchecked")
    protected <DATE extends Date> DATE xparseDate(Object obj, String propertyName, Class<DATE> type) {
        xcheckTypeString(obj, propertyName, type);
        if (Time.class.isAssignableFrom(type)) {
            return (DATE) DfTypeUtil.toTime(obj);
        } else if (Timestamp.class.isAssignableFrom(type)) {
            return (DATE) DfTypeUtil.toTimestamp(obj);
        } else { // basically Date
            return (DATE) DfTypeUtil.toDate(obj);
        }
    }

    protected Boolean xparseBoolean(Object obj, String propertyName, Class<?> type) {
        xcheckTypeString(obj, propertyName, type);
        return DfTypeUtil.toBoolean(obj);
    }

    protected UUID xparseUUID(Object obj, String propertyName, Class<?> type) {
        xcheckTypeString(obj, propertyName, type);
        return DfTypeUtil.toUUID(obj);
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
        createCBExThrower().throwPagingPageSizeNotPlusException(pageSize, pageNumber);
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
    }

    /**
     * {@inheritDoc}
     */
    public void disablePagingReSelect() {
        _pagingReSelect = false;
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
    public OrderByClause getSqlComponentOfOrderByClause() {
        return getSqlClause().getSqlComponentOfOrderByClause();
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderByClause() {
        return _sqlClause.getOrderByClause();
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
    public ConditionBean xsetupSelectCountIgnoreFetchScope() {
        _isSelectCountIgnoreFetchScope = true;

        getSqlClause().classifySelectClauseType(SqlClause.SelectClauseType.COUNT);
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

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected abstract HpAbstractSpecification<? extends ConditionQuery> localSp();

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                         ColumnQuery
    //                                                                         ===========
    protected <CB extends ConditionBean> void xcolqy(CB cb, SpecifyQuery<CB> leftSp, SpecifyQuery<CB> rightSp,
            String operand) {
        // Specify left column
        leftSp.specify(cb);
        String leftColumn = cb.getSqlClause().removeSpecifiedColumnRealNameAsOne();
        if (leftColumn == null) {
            createCBExThrower().throwColumnQueryInvalidColumnSpecificationException();
        }
        // Specify right column
        cb.getSqlClause().clearSpecifiedSelectColumn(); // recycle
        rightSp.specify(cb);
        String rightColumn = cb.getSqlClause().removeSpecifiedColumnRealNameAsOne();
        if (rightColumn == null) {
            createCBExThrower().throwColumnQueryInvalidColumnSpecificationException();
        }

        // Register where clause
        String clause = leftColumn + " " + operand + " " + rightColumn;
        getSqlClause().registerWhereClause(clause);
    }

    // [DBFlute-0.9.6.3]
    // ===================================================================================
    //                                                                        OrScopeQuery
    //                                                                        ============
    protected <CB extends ConditionBean> void xorSQ(CB cb, OrQuery<CB> orQuery) {
        getSqlClause().makeOrScopeQueryEffective();
        try {
            orQuery.query(cb);
        } finally {
            getSqlClause().closeOrScopeQuery();
        }
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
        return getSqlClause().hasWhereClause();
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

    // [DBFlute-0.9.5.2]
    // ===================================================================================
    //                                                                      Free Parameter
    //                                                                      ==============
    /**
     * Get the map for free parameters for parameter comment. {Internal}
     * @return The map for free parameters. (Nullable)
     */
    public Map<String, Object> getFreeParameterMap() {
        return _freeParameterMap;
    }

    /**
     * {@inheritDoc}
     */
    public void xregisterFreeParameter(String key, Object value) {
        if (_freeParameterMap == null) {
            _freeParameterMap = new LinkedHashMap<String, Object>();
        }
        _freeParameterMap.put(key, value);
    }

    // [DBFlute-0.9.5.2]
    // ===================================================================================
    //                                                                  Query Synchronizer
    //                                                                  ==================
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

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                        Purpose Type
    //                                                                        ============
    public void xsetupForDerivedReferrer() { // very internal
        _forDerivedReferrer = true;
    }

    public void xsetupForScalarSelect() { // very internal
        _forScalarSelect = true;
    }

    public void xsetupForScalarSubQuery() { // very internal
        _forScalarSubQuery = true;
    }

    public void xsetupForUnion() { // very internal
        _forUnion = true;
    }

    public void xsetupForExistsSubQuery() { // very internal
        _forExistsSubQuery = true;
    }

    public void xsetupForInScopeSubQuery() { // very internal
        _forInScopeSubQuery = true;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected void doSetupSelect(SsCall callback) {
        String foreignPropertyName = callback.qf().getForeignPropertyName();
        assertSetupSelectBeforeUnion(foreignPropertyName);
        String foreignTableAliasName = callback.qf().getRealAliasName();
        String localRelationPath = localCQ().getRelationPath();
        getSqlClause().registerSelectedSelectColumn(foreignTableAliasName, getTableDbName(), foreignPropertyName,
                localRelationPath);
        getSqlClause().registerSelectedForeignInfo(callback.qf().getRelationPath(), foreignPropertyName);
    }

    protected static interface SsCall {
        public ConditionQuery qf();
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

    protected void assertPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        if (primaryKeyMap == null) {
            String msg = "The argument[primaryKeyMap] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (primaryKeyMap.isEmpty()) {
            String msg = "The argument[primaryKeyMap] must not be empty.";
            throw new IllegalArgumentException(msg);
        }
        DBMeta dbmeta = getDBMeta();
        List<ColumnInfo> columnInfoList = dbmeta.getPrimaryUniqueInfo().getUniqueColumnList();
        for (ColumnInfo columnInfo : columnInfoList) {
            String columnDbName = columnInfo.getColumnDbName();
            if (!primaryKeyMap.containsKey(columnDbName)) {
                String msg = "The primaryKeyMap must have the value of " + columnDbName;
                throw new IllegalStateException(msg + ": primaryKeyMap --> " + primaryKeyMap);
            }
        }
    }

    protected void assertSetupSelectBeforeUnion(String foreignPropertyName) {
        if (hasUnionQueryOrUnionAllQuery()) {
            final String titleName = DfTypeUtil.toClassTitle(this);
            throwSetupSelectAfterUnionException(titleName, foreignPropertyName);
        }
    }

    protected void throwSetupSelectAfterUnionException(String className, String foreignPropertyName) {
        createCBExThrower().throwSetupSelectAfterUnionException(className, foreignPropertyName);
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
    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String titleName = DfTypeUtil.toClassTitle(this);
        sb.append(titleName).append(":").append(ln());
        try {
            sb.append(toDisplaySql());
        } catch (RuntimeException e) {
            sb.append(getSqlClause().getClause());
        }
        return sb.toString();
    }
}
