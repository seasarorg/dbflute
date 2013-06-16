/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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

import org.seasar.dbflute.dbway.DBWay;
import org.seasar.dbflute.dbway.WayOfH2;

/**
 * SqlClause for H2.
 * @author jflute
 */
public class SqlClauseH2 extends AbstractSqlClause {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** The instance of DBWay. */
    protected static final DBWay _dbway = new WayOfH2();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-scope as sql-suffix. */
    protected String _fetchScopeSqlSuffix = "";

    /** String of lock as sql-suffix. */
    protected String _lockSqlSuffix = "";

    /** The bind value for paging as 'limit'. */
    protected Integer _pagingBindLimit;

    /** The bind value for paging as 'offset'. */
    protected Integer _pagingBindOffset;

    /** Does it suppress bind variable for paging? */
    protected boolean _suppressPagingBind;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableDbName The DB name of table. (NotNull)
     **/
    public SqlClauseH2(String tableDbName) {
        super(tableDbName);
    }

    // ===================================================================================
    //                                                                 FetchScope Override
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    protected void doFetchFirst() {
        doFetchPage();
    }

    /**
     * {@inheritDoc}
     */
    protected void doFetchPage() {
        if (_suppressPagingBind) {
            _fetchScopeSqlSuffix = " limit " + getFetchSize() + " offset " + getPageStartIndex();
        } else { // mainly here
            _pagingBindLimit = getFetchSize();
            _pagingBindOffset = getPageStartIndex();
            _fetchScopeSqlSuffix = " limit /*pmb.sqlClause.pagingBindLimit*/0 offset /*pmb.sqlClause.pagingBindOffset*/0";
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doClearFetchPageClause() {
        _fetchScopeSqlSuffix = "";
    }

    // ===================================================================================
    //                                                                       Lock Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public void lockForUpdate() {
        _lockSqlSuffix = " for update";
    }

    // ===================================================================================
    //                                                                       Hint Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    protected String createSelectHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromBaseTableHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    protected String createSqlSuffix() {
        return _fetchScopeSqlSuffix + _lockSqlSuffix;
    }

    // [DBFlute-0.9.8.4]
    // ===================================================================================
    //                                                                               DBWay
    //                                                                               =====
    public DBWay dbway() {
        return _dbway;
    }

    // [DBFlute-1.0.4D]
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Integer getPagingBindLimit() { // for parameter comment
        return _pagingBindLimit;
    }

    public Integer getPagingBindOffset() { // for parameter comment
        return _pagingBindOffset;
    }

    public SqlClauseH2 suppressPagingBind() { // for compatible? anyway, just in case
        _suppressPagingBind = true;
        return this;
    }
}
