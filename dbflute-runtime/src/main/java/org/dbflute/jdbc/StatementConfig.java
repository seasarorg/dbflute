package org.dbflute.jdbc;

import java.sql.ResultSet;

/**
 * The config of statement.
 * @author DBFlute(AutoGenerator)
 */
public class StatementConfig {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
	// -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
    protected Integer _resultSetType;
	
	// -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
    protected Integer _queryTimeout;
    protected Integer _fetchSize;
    protected Integer _maxRows;
    
    // ===================================================================================
    //                                                                   Setting Interface
    //                                                                   =================
	// -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
    public StatementConfig typeForwardOnly() {
        _resultSetType = ResultSet.TYPE_FORWARD_ONLY;
        return this;
    }
    
    public StatementConfig typeScrollInsensitive() {
        _resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
        return this;
    }
    
    public StatementConfig typeScrollSensitive() {
        _resultSetType = ResultSet.TYPE_SCROLL_SENSITIVE;
        return this;
    }
    
	// -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
    public StatementConfig queryTimeout(int queryTimeout) {
        this._queryTimeout = queryTimeout;
        return this;
    }
	    
    public StatementConfig fetchSize(int fetchSize) {
        this._fetchSize = fetchSize;
        return this;
    }
    
    public StatementConfig maxRows(int maxRows) {
        this._maxRows = maxRows;
        return this;
    }
	
	// ===================================================================================
    //                                                                       Determination
    //                                                                       =============
	// -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
	public boolean hasResultSetType() {
        return _resultSetType != null;
    }
	
	// -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
	public boolean hasStatementOptions() {
	    return hasQueryTimeout() || hasFetchSize() || hasMaxRows();
	}
	
	public boolean hasQueryTimeout() {
        return _queryTimeout != null;
    }
	
	public boolean hasFetchSize() {
        return _fetchSize != null;
    }
	
	public boolean hasMaxRows() {
        return _maxRows != null;
    }
	
    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
	@Override
	public String toString() {
	    return "{" + _resultSetType + ", " + _queryTimeout + ", " + _fetchSize + ", " + _maxRows + "}";
	}
	
	// ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
	// -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
    public Integer getResultSetType() {
        return _resultSetType;
    }
    
	// -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
    public Integer getQueryTimeout() {
        return _queryTimeout;
    }
	
    public Integer getFetchSize() {
        return _fetchSize;
    }
	
    public Integer getMaxRows() {
        return _maxRows;
    }
}
