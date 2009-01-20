package org.seasar.dbflute.cbean.sqlclause;


/**
 * SqlClause for MySQL.
 * @author jflute
 */
public class SqlClauseMySql extends AbstractSqlClause {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-scope as sql-suffix. */
    protected String _fetchScopeSqlSuffix = "";

    /** String of lock as sql-suffix. */
    protected String _lockSqlSuffix = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableName Table name. (NotNull)
     **/
    public SqlClauseMySql(String tableName) {
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
        doFetchPage();
    }

    /**
     * The implementation.
     */
    protected void doFetchPage() {
        _fetchScopeSqlSuffix = " limit " + getPageStartIndex() + ", " + getFetchSize();
    }

    /**
     * The implementation.
     */
    protected void doClearFetchPageClause() {
        _fetchScopeSqlSuffix = "";
    }

    /**
     * The implementation.
     * @return this. (NotNull)
     */
    public SqlClause lockForUpdate() {
        _lockSqlSuffix = " for update";
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
        return _fetchScopeSqlSuffix + _lockSqlSuffix;
    }
    
    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                               Query Update Override
    //                                                               =====================
	@Override
    protected boolean isUpdateSubQueryUseLocalTableSupported() {
        return false;
    }
}
