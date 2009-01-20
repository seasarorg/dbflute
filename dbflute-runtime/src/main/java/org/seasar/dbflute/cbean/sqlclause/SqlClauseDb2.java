package org.seasar.dbflute.cbean.sqlclause;


/**
 * SqlClause for DB2.
 * @author jflute
 */
public class SqlClauseDb2 extends AbstractSqlClause {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-first as sql-suffix. */
    protected String _fetchFirstSqlSuffix = "";

    /** String of lock as from-hint. */
    protected String _lockSqlSuffix = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableName Table name. (NotNull)
     **/
    public SqlClauseDb2(String tableName) {
        super(tableName);
    }

    // ===================================================================================
    //                                                                    OrderBy Override
    //                                                                    ================
	@Override
    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupper() {
	    return createOrderByNullsSetupperByCaseWhen();
	}

    // ===================================================================================
    //                                                                 FetchScope Override
    //                                                                 ===================
    /**
     * The implementation.
     */
    protected void doFetchFirst() {
        if (isFetchSizeSupported()) {
            _fetchFirstSqlSuffix = " fetch first " + getFetchSize() + " rows only";
        }
    }

    /**
     * The implementation. {Unsupported!}
     */
    protected void doFetchPage() {
        if (isFetchSizeSupported()) {
            if (isFetchStartIndexSupported()) {
                _fetchFirstSqlSuffix = " fetch first " + getFetchSize() + " rows only";
            } else {
                _fetchFirstSqlSuffix = " fetch first " + getPageEndIndex() + " rows only";
            }
        }
    }

    /**
     * The implementation. {Unsupported!}
     */
    protected void doClearFetchPageClause() {
        _fetchFirstSqlSuffix = "";
    }

    /**
     * The override.
     * @return Determination.
     */
    public boolean isFetchStartIndexSupported() {
        return false;
    }

    /**
     * The implementation.
     * @return this. (NotNull)
     */
    public SqlClause lockForUpdate() {
        _lockSqlSuffix = " for update with RS";
        return this;
    }

    /**
     * The implementation.
     * @return Select-hint. (NotNull)
     */
    protected String createSelectHint() {
        return "";
    }

    /**
     * The implementation.
     * @return From-base-table-hint. {select * from table [from-base-table-hint] where ...} (NotNull)
     */
    protected String createFromBaseTableHint() {
        return "";
    }

    /**
     * The implementation.
     * @return From-hint. (NotNull)
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * The implementation.
     * @return Sql-suffix. (NotNull)
     */
    protected String createSqlSuffix() {
        return _fetchFirstSqlSuffix + _lockSqlSuffix;
    }

    // [DBFlute-0.7.9]
    // ===================================================================================
    //                                                                      DB2 Dependency
    //                                                                      ==============
    public void lockWithRR() {
        _lockSqlSuffix = " with RR";
    }

    public void lockWithRS() {
        _lockSqlSuffix = " with RS";
    }

    public void lockWithCS() {
        _lockSqlSuffix = " with CS";
    }

    public void lockWithUR() {
        _lockSqlSuffix = " with UR";
    }
}
