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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.cbean.chelper.HpAbstractSpecification;
import org.seasar.dbflute.cbean.sqlclause.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.WhereClauseSimpleFilter;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.helper.mapstring.MapListString;
import org.seasar.dbflute.helper.mapstring.impl.MapListStringImpl;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * The condition-bean as abstract.
 * @author jflute
 */
public abstract class AbstractConditionBean implements ConditionBean {

    // =====================================================================================
    //                                                                            Definition
    //                                                                            ==========
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

    /** Can the paging re-select? {Internal} */
    private boolean _canPagingReSelect = true;

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
     */
    public void embedCondition(Set<ColumnInfo> embeddedColumnInfoSet, boolean quote) {
        if (embeddedColumnInfoSet == null) {
            String msg = "The argument[embeddedColumnInfoSet] should not be null.";
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
     * @param primaryKeyMapString Primary-key map. (NotNull and NotEmpty)
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

    protected void checkTypeString(Object value, String propertyName, String typeName) {
        if (value == null) {
            throw new IllegalArgumentException("The value should not be null: " + propertyName);
        }
        if (!(value instanceof String)) {
            String msg = "The value of " + propertyName + " should be " + typeName + " or String: ";
            msg = msg + "valueType=" + value.getClass() + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    protected long parseDateStringAsMillis(Object value, String propertyName, String typeName) {
        checkTypeString(value, propertyName, typeName);
        try {
            final String valueString = (String) value;
            if (valueString.indexOf("-") >= 0 && valueString.indexOf("-") != valueString.lastIndexOf("-")) {
                return java.sql.Timestamp.valueOf(valueString).getTime();
            } else {
                return getParseDateFormat().parse((String) value).getTime();
            }
        } catch (java.text.ParseException e) {
            String msg = "The value of " + propertyName + " should be " + typeName + ". but: " + value;
            throw new RuntimeException(msg + " threw the exception: value=[" + value + "]", e);
        } catch (RuntimeException e) {
            String msg = "The value of " + propertyName + " should be " + typeName + ". but: " + value;
            throw new RuntimeException(msg + " threw the exception: value=[" + value + "]", e);
        }
    }

    private java.text.DateFormat getParseDateFormat() {
        return java.text.DateFormat.getDateTimeInstance();
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
    public boolean isCountLater() { // for framework
        return false; // as default
    }

    // -----------------------------------------------------
    //                                        Paging Setting
    //                                        --------------
    /**
     * {@inheritDoc}
     * @param pageSize The page size per one page. (NotMinus & NotZero)
     * @param pageNumber The number of page. It's ONE origin. (NotMinus & NotZero: If it's minus or zero, it treats as one.)
     */
    public void paging(int pageSize, int pageNumber) {
        fetchFirst(pageSize);
        fetchPage(pageNumber);
    }

    /**
     * {@inheritDoc}
     * @param paging Determination.
     */
    public void xsetPaging(boolean paging) {// Very Internal!
        String msg = "This method is unsupported on ConditionBean!";
        throw new UnsupportedOperationException(msg);
    }

    /**
     * ${database.ImplementComment}
     */
    public void disablePagingReSelect() {
        _canPagingReSelect = false;
    }

    /**
     * ${database.ImplementComment}
     * @return Can the paging re-select execute?
     */
    public boolean canPagingReSelect() {
        return _canPagingReSelect;
    }

    // -----------------------------------------------------
    //                                         Fetch Setting
    //                                         -------------
    /**
     * {@inheritDoc}
     * @param fetchSize Fetch-size. (NotMinus & NotZero)
     * @return this. (NotNUll)
     */
    public PagingBean fetchFirst(int fetchSize) {
        getSqlClause().fetchFirst(fetchSize);
        return this;
    }

    /**
     * {@inheritDoc}
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch-size. (NotMinus & NotZero)
     * @return this. (NotNUll)
     */
    public PagingBean fetchScope(int fetchStartIndex, int fetchSize) {
        getSqlClause().fetchScope(fetchStartIndex, fetchSize);
        return this;
    }

    /**
     * {@inheritDoc}
     * @param fetchPageNumber Fetch-page-number. 1 origin. (NotMinus & NotZero: If minus or zero, set one.)
     * @return this. (NotNull)
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
     * @return Fetch-start-index.
     */
    public int getFetchStartIndex() {
        return getSqlClause().getFetchStartIndex();
    }

    /**
     * {@inheritDoc}
     * @return Fetch-size.
     */
    public int getFetchSize() {
        return getSqlClause().getFetchSize();
    }

    /**
     * {@inheritDoc}
     * @return Fetch-page-number.
     */
    public int getFetchPageNumber() {
        return getSqlClause().getFetchPageNumber();
    }

    /**
     * {@inheritDoc}
     * @return Page start index. 0 origin. (NotMinus)
     */
    public int getPageStartIndex() {
        return getSqlClause().getPageStartIndex();
    }

    /**
     * {@inheritDoc}
     * @return Page end index. 0 origin. (NotMinus)
     */
    public int getPageEndIndex() {
        return getSqlClause().getPageEndIndex();
    }

    /**
     * Is fetch scope effective?
     * @return Determiantion.
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
    //                                                Implementation of FetchNarrowingBean
    //                                                ====================================
    /**
     * {@inheritDoc}
     * @return Fetch start index.
     */
    public int getFetchNarrowingSkipStartIndex() {
        return getSqlClause().getFetchNarrowingSkipStartIndex();
    }

    /**
     * {@inheritDoc}
     * @return Fetch size.
     */
    public int getFetchNarrowingLoopCount() {
        return getSqlClause().getFetchNarrowingLoopCount();
    }

    /**
     * {@inheritDoc}
     * @return Determination.
     */
    public boolean isFetchNarrowingSkipStartIndexEffective() {
        return !getSqlClause().isFetchStartIndexSupported();
    }

    /**
     * {@inheritDoc}
     * @return Determination.
     */
    public boolean isFetchNarrowingLoopCountEffective() {
        return !getSqlClause().isFetchSizeSupported();
    }

    /**
     * {@inheritDoc}
     * @return Determiantion.
     */
    public boolean isFetchNarrowingEffective() {
        return getSqlClause().isFetchNarrowingEffective();
    }

    /**
     * Ignore fetch narrowing. Only checking safety result size is valid. {INTERNAL METHOD}
     */
    public void ignoreFetchNarrowing() {
        String msg = "This method is unsupported on ConditionBean!";
        throw new UnsupportedOperationException(msg);
    }

    /**
     * Restore ignored fetch narrowing. {INTERNAL METHOD}
     */
    public void restoreIgnoredFetchNarrowing() {
        // Do nothing!
    }

    /**
     * Get safety max result size.
     * @return Safety max result size.
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    /**
     * Check safety result.
     * @param safetyMaxResultSize Safety max result size. (If zero or minus, ignore checking)
     */
    public void checkSafetyResult(int safetyMaxResultSize) {
        this._safetyMaxResultSize = safetyMaxResultSize;
    }

    // ===================================================================================
    //                                                       Implementation of OrderByBean
    //                                                       =============================
    /**
     * {@inheritDoc}
     * @return Sql component of order-by clause. (NotNull)
     */
    public OrderByClause getSqlComponentOfOrderByClause() {
        return getSqlClause().getSqlComponentOfOrderByClause();
    }

    /**
     * {@inheritDoc}
     * @return Order-by clause. (NotNull)
     */
    public String getOrderByClause() {
        return _sqlClause.getOrderByClause();
    }

    /**
     * {@inheritDoc}
     * @return this. (NotNull)
     */
    public OrderByBean clearOrderBy() {
        getSqlClause().clearOrderBy();
        return this;
    }

    /**
     * {@inheritDoc}
     * @return this. (NotNull)
     */
    public OrderByBean ignoreOrderBy() {
        getSqlClause().ignoreOrderBy();
        return this;
    }

    /**
     * {@inheritDoc}
     * @return this. (NotNull)
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
     * @return this. (NotNull)
     */
    public ConditionBean lockForUpdate() {
        getSqlClause().lockForUpdate();
        return this;
    }

    // ===================================================================================
    //                                                                        Select Count
    //                                                                        ============
    /**
     * Set up various things for select-count-ignore-fetch-scope. {Internal}
     * This method is for INTERNAL. Don't invoke this!
     * @return this. (NotNull)
     */
    public ConditionBean xsetupSelectCountIgnoreFetchScope() {
        _isSelectCountIgnoreFetchScope = true;

        getSqlClause().classifySelectClauseType(SqlClause.SelectClauseType.COUNT);
        getSqlClause().ignoreOrderBy();
        getSqlClause().ignoreFetchScope();
        return this;
    }

    /**
     * Do after-care for select-count-ignore-fetch-scope. {Internal}
     * This method is for INTERNAL. Don't invoke this!
     * @return this. (NotNull)
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
     * Is set up various things for select-count-ignore-fetch-scope?
     * This method is for INTERNAL. Don't invoke this!
     * @return Determination.
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
            ConditionBeanContext.throwColumnQueryInvalidColumnSpecificationException();
        }
        // Specify right column
        cb.getSqlClause().clearSpecifiedSelectColumn(); // recycle
        rightSp.specify(cb);
        String rightColumn = cb.getSqlClause().removeSpecifiedColumnRealNameAsOne();
        if (rightColumn == null) {
            ConditionBeanContext.throwColumnQueryInvalidColumnSpecificationException();
        }

        // Register where clause
        String clause = leftColumn + " " + operand + " " + rightColumn;
        getSqlClause().registerWhereClause(clause);
    }

    // [DBFlute-0.9.5.5]
    // ===================================================================================
    //                                                                             OrQuery
    //                                                                             =======
    protected <CB extends ConditionBean> void xorQ(CB cb, OrQuery<CB> orQuery) {
        getSqlClause().makeOrQueryEffective();
        try {
            orQuery.query(cb);
        } finally {
            getSqlClause().ignoreOrQuery();
        }
    }

    // ===================================================================================
    //                                                                     StatementConfig
    //                                                                     ===============
    /**
     * @param statementConfig The configuration of statement. (Nullable)
     */
    public void configure(StatementConfig statementConfig) {
        _statementConfig = statementConfig;
    }

    /**
     * @return The configuration of statement. (Nullable)
     */
    public StatementConfig getStatementConfig() {
        return _statementConfig;
    }

    // ===================================================================================
    //                                                                          DisplaySQL
    //                                                                          ==========
    /**
     * Convert this conditionBean to SQL for display.
     * @return SQL for display. (NotNull and NotEmpty)
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

    // [DBFlute-0.9.5.2]
    // ===================================================================================
    //                                                                      Free Parameter
    //                                                                      ==============
    /**
     * Get the map for free parameters for OGNL.
     * @return The map for free parameters. (Nullable)
     */
    public Map<String, Object> getFreeParameterMap() { // Very Internal
        return _freeParameterMap;
    }

    /**
     * {Internal}
     * @param key The key for the parameter. (NotNull)
     * @param value The value for the parameter. (Nullable)
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
     * {Internal}
     * @param unionCB The condition-bean for union. (NotNull)
     */
    protected void xsyncUQ(ConditionBean unionCB) { // synchronizeUnionQuery()
        if (_unionQuerySynchronizer != null) {
            _unionQuerySynchronizer.query(unionCB);
        }
    }

    /**
     * {Internal}
     * @param unionQuerySynchronizer THe synchronizer of union query. (Nullable)
     */
    public void xregisterUnionQuerySynchronizer(UnionQuery<ConditionBean> unionQuerySynchronizer) {
        _unionQuerySynchronizer = unionQuerySynchronizer;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                        Purpose Type
    //                                                                        ============
    public void xsetupForDerivedReferrer() { // Very Internal
        _forDerivedReferrer = true;
    }

    public void xsetupForScalarSelect() { // Very Internal
        _forScalarSelect = true;
    }

    public void xsetupForScalarSubQuery() { // Very Internal
        _forScalarSubQuery = true;
    }

    public void xsetupForUnion() { // Very Internal
        _forUnion = true;
    }

    public void xsetupForExistsSubQuery() { // Very Internal
        _forExistsSubQuery = true;
    }

    public void xsetupForInScopeSubQuery() { // Very Internal
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
    protected void assertPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        if (primaryKeyMap == null) {
            String msg = "The argument[primaryKeyMap] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (primaryKeyMap.isEmpty()) {
            String msg = "The argument[primaryKeyMap] must not be empty.";
            throw new IllegalArgumentException(msg);
        }
        DBMeta dbmeta = getDBMetaProvider().provideDBMetaChecked(getTableDbName());
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
            throwSetupSelectAfterUnionException(this.getClass().getSimpleName(), foreignPropertyName);
        }
    }

    protected void throwSetupSelectAfterUnionException(String className, String foreignPropertyName) {
        ConditionBeanContext.throwSetupSelectAfterUnionException(className, foreignPropertyName, toDisplaySql());
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(String str) {
        return DfStringUtil.initCap(str);
    }

    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * The override.
     * @return SQL for display. (NotNull)
     */
    public String toString() {
        try {
            return toDisplaySql();
        } catch (RuntimeException e) {
            return getSqlClause().getClause();
        }
    }
}
