package org.dbflute.cbean.sqlclause;


/**
 * SqlClause for Default.
 * @author DBFlute(AutoGenerator)
 */
public class SqlClauseDefault extends AbstractSqlClause {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param tableName Table name. (NotNull)
     **/
    public SqlClauseDefault(String tableName) {
        super(tableName);
    }

    // ===================================================================================
    //                                                                    OrderBy Override
    //                                                                    ================
	@Override
    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupper() {
	    return createOrderByNullsSetupperByCaseWhen();
	}

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
        String msg = "LockForUpdate-SQL is unsupported in the database. Sorry...: " + toString();
        throw new UnsupportedOperationException(msg);
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
        return "";
    }
}
