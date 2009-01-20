package org.seasar.dbflute.cbean.sqlclause;


/**
 * SqlClause for Default.
 * 
 * @author jflute
 */
public class SqlClauseDerby extends AbstractSqlClause {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of lock as sql-suffix. */
    protected String _lockSqlSuffix = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableName Table name. (NotNull)
     **/
    public SqlClauseDerby(String tableName) {
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
    }

    /**
     * The implementation.
     */
    protected void doFetchPage() {
    }

    /**
     * The implementation.
     */
    protected void doClearFetchPageClause() {
    }

    /**
     * The override.
     * 
     * @return Determination.
     */
    public boolean isFetchStartIndexSupported() {
        return false; // Default
    }

    /**
     * The override.
     * 
     * @return Determination.
     */
    public boolean isFetchSizeSupported() {
        return false; // Default
    }

    /**
     * The implementation.
     * 
     * @return this. (NotNull)
     */
    public SqlClause lockForUpdate() {
        _lockSqlSuffix = " for update";
        return this;
    }

    /**
     * The implementation.
     * 
     * @return Select-hint. (NotNull)
     */
    protected String createSelectHint() {
        return "";
    }

    /**
     * The implementation.
     * 
     * @return From-base-table-hint. {select * from table [from-base-table-hint] where ...} (NotNull)
     */
    protected String createFromBaseTableHint() {
        return "";
    }

    /**
     * The implementation.
     * 
     * @return From-hint. (NotNull)
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * The implementation.
     * 
     * @return Sql-suffix. (NotNull)
     */
    protected String createSqlSuffix() {
        return _lockSqlSuffix;
    }
}
