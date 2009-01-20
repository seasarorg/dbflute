package org.seasar.dbflute.cbean;

import java.util.Map;
import java.util.LinkedHashMap;

import org.seasar.dbflute.cbean.sqlclause.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseDefault;


/**
 * The simple paging-bean.
 * @author jflute
 */
public class SimplePagingBean implements PagingBean, MapParameterBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** SQL clause instance. */
    protected final SqlClause _sqlClause;
    {
        _sqlClause = new SqlClauseDefault("Dummy");
    }

    /** The map of parameter. (Nullable) */
    protected Map<String, Object> _parameterMap;

    /** Safety max result size. */
    protected int _safetyMaxResultSize;

    /** Is the execution for paging(NOT count)? */
    protected boolean _paging = true;

    /** Is the count executed later? */
    protected boolean _countLater;

    /** Can the paging re-select? */
    protected boolean _canPagingReSelect = true;

    /** Is fetch narrowing valid? */
    protected boolean _fetchNarrowing = true;

    /** The map for parameter. */
    protected Map<String, Object> _map;

    // ===================================================================================
    //                                                        Implementation of PagingBean
    //                                                        ============================
    // -----------------------------------------------------
    //                                  Paging Determination
    //                                  --------------------
    // * * * * * * * *
    // For SQL Comment
    // * * * * * * * *
    /**
     * The implementation.
     * @return Determination.
     */
    public boolean isPaging() {
        return _paging;
    }

    // * * * * * * * *
    // For Framework
    // * * * * * * * *
    /**
     * The implementation.
     * @return Determination.
     */
    public boolean isCountLater() {
        return _countLater;
    }

    // -----------------------------------------------------
    //                                        Paging Setting
    //                                        --------------
    /**
     * The implementation.
	 * @param pageSize The page size per one page. (NotMinus & NotZero)
	 * @param pageNumber The number of page. It's ONE origin. (NotMinus & NotZero: If it's minus or zero, it treats as one.)
     */
    public void paging(int pageSize, int pageNumber) {
        fetchFirst(pageSize);
        fetchPage(pageNumber);
    }

    /**
     * The implementation. {INTERNAL METHOD: Don't Invoke This!}
     * @param paging Determination.
     */
    public void xsetPaging(boolean paging) {
        if (paging) {
            getSqlClause().makeFetchScopeEffective();
        } else {
            getSqlClause().ignoreFetchScope();
        }
        this._paging = paging;
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
     * The implementation.
     * @param fetchSize Fetch-size. (NotMinus & NotZero)
     * @return this. (NotNull)
     */
    public PagingBean fetchFirst(int fetchSize) {
        getSqlClause().fetchFirst(fetchSize);
        return this;
    }

    /**
     * The implementation.
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch-size. (NotMinus & NotZero)
     * @return this. (NotNull)
     */
    public PagingBean fetchScope(int fetchStartIndex, int fetchSize) {
        getSqlClause().fetchScope(fetchStartIndex, fetchSize);
        return this;
    }

    /**
     * The implementation.
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
     * The implementation.
     * @return Fetch-start-index.
     */
    public int getFetchStartIndex() {
        return getSqlClause().getFetchStartIndex();
    }

    /**
     * The implementation.
     * @return Fetch-size.
     */
    public int getFetchSize() {
        return getSqlClause().getFetchSize();
    }

    /**
     * The implementation.
     * @return Fetch-page-number.
     */
    public int getFetchPageNumber() {
        return getSqlClause().getFetchPageNumber();
    }

    /**
     * The implementation.
     * @return Page start index. 0 origin. (NotMinus)
     */
    public int getPageStartIndex() {
        return getSqlClause().getPageStartIndex();
    }

    /**
     * The implementation.
     * @return Page end index. 0 origin. (NotMinus)
     */
    public int getPageEndIndex() {
        return getSqlClause().getPageEndIndex();
    }

    /**
     * Is fetch scope effective?
     * @return Determination.
     */
    public boolean isFetchScopeEffective() {
        return getSqlClause().isFetchScopeEffective();
    }

    // ===================================================================================
    //                                                Implementation of FetchNarrowingBean
    //                                                ====================================
    /**
     * Get fetch-narrowing start-index.
     * @return Fetch-narrowing start-index.
     */
    public int getFetchNarrowingSkipStartIndex() {
        return getSqlClause().getFetchNarrowingSkipStartIndex();
    }

    /**
     * Get fetch-narrowing size.
     * @return Fetch-narrowing size.
     */
    public int getFetchNarrowingLoopCount() {
        return getSqlClause().getFetchNarrowingLoopCount();
    }

    /**
     * Is fetch start index supported?
     * @return Determination.
     */
    public boolean isFetchNarrowingSkipStartIndexEffective() {
        return !getSqlClause().isFetchStartIndexSupported();
    }

    /**
     * Is fetch size supported?
     * @return Determination.
     */
    public boolean isFetchNarrowingLoopCountEffective() {
        return !getSqlClause().isFetchSizeSupported();
    }

    /**
     * Is fetch-narrowing effective?
     * @return Determination.
     */
    public boolean isFetchNarrowingEffective() {
        return _fetchNarrowing && getSqlClause().isFetchNarrowingEffective();
    }

    /**
     * Ignore fetch narrowing. Only checking safety result size is valid. {INTERNAL METHOD}
     */
    public void ignoreFetchNarrowing() {
        _fetchNarrowing = false;
    }

    /**
     * Restore ignored fetch narrowing. {INTERNAL METHOD}
     */
    public void restoreIgnoredFetchNarrowing() {
        _fetchNarrowing = true;
    }

    /**
     * Get safety max result size.
     * @return Safety max result size.
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    // ===================================================================================
    //                                                       Implementation of OrderByBean
    //                                                       =============================
    /**
     * The implementation.
     * @return Sql component of order-by clause. (NotNull)
     */
    public OrderByClause getSqlComponentOfOrderByClause() {
        return getSqlClause().getSqlComponentOfOrderByClause();
    }

    /**
     * The implementation.
     * @return Order-by clause. (NotNull)
     */
    public String getOrderByClause() {
        return getSqlClause().getOrderByClause();
    }

    /**
     * The implementation.
     * @return this. (NotNull)
     */
    public OrderByBean clearOrderBy() {
        getSqlClause().clearOrderBy();
        return this;
    }

    /**
     * The implementation.
     * @return this. (NotNull)
     */
    public OrderByBean ignoreOrderBy() {
        getSqlClause().ignoreOrderBy();
        return this;
    }

    /**
     * The implementation.
     * @return this. (NotNull)
     */
    public OrderByBean makeOrderByEffective() {
        getSqlClause().makeOrderByEffective();
        return this;
    }

    // ===================================================================================
    //                                                    Implementation of SelectResource
    //                                                    ================================
    /**
     * Check safety result.
     * @param safetyMaxResultSize Safety max result size. (If zero or minus, ignore checking)
     */
    public void checkSafetyResult(int safetyMaxResultSize) {
        this._safetyMaxResultSize = safetyMaxResultSize;
    }

    // ===================================================================================
    //                                                  Implementation of MapParameterBean
    //                                                  ==================================
    /**
     * Get the map of parameter.
     * @return The map of parameter. (Nullable)
     */
    public Map<String, Object> getParameterMap() {
        return _parameterMap;
    }

    /**
     * Add the parameter to the map.
     * @param key The key of parameter. (NotNull)
     * @param value The value of parameter. (Nullable)
     */
    public void addParameter(String key, Object value) {
        if (_parameterMap == null) {
            _parameterMap = new LinkedHashMap<String, Object>();
        }
        _parameterMap.put(key, value);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                             SqlClause
    //                                             ---------
    /**
     * Get sqlClause.
     * @return SqlClause. (NotNull)
     */
    protected SqlClause getSqlClause() {
        return _sqlClause;
    }
}
