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
package org.seasar.dbflute.s2dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.cbean.FetchNarrowingBean;
import org.seasar.dbflute.jdbc.ResultSetWrapper;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.resource.SQLExceptionHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnPagingResultSet extends ResultSetWrapper {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The real result set. (NotNull) */
    protected ResultSet _resultSet;

    /** The bean of fetch narrowing. (NotNull) */
    protected FetchNarrowingBean _fetchNarrowingBean;

    /** The counter of fetch. */
    protected long _fetchCounter;

    /** the counter of request. */
    protected long _requestCounter;

    /** Does it offset by cursor forcedly? */
    protected boolean _offsetByCursorForcedly;

    /** Does it limit by cursor forcedly? */
    protected boolean _limitByCursorForcedly;
	
	/** Does it skip to cursor end? */
	protected boolean _skipToCursorEnd;

	/** Is the database DB2? */
	protected final boolean _db2;
	{
	    _db2 = ResourceContext.isCurrentDBDef(DBDef.DB2);
	}

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param resultSet Original result set. (NotNull)
     * @param fetchNarrowingBean Fetch-narrowing-bean. (NotNull)
     * @param offsetByCursorForcedly Offset by cursor forcedly.
     * @param limitByCursorForcedly Limit by cursor forcedly.
     */
    public TnPagingResultSet(ResultSet resultSet, FetchNarrowingBean fetchNarrowingBean
                                         , boolean offsetByCursorForcedly, boolean limitByCursorForcedly) {
        super(resultSet);

        _resultSet = resultSet;
        _fetchNarrowingBean = fetchNarrowingBean;
        _offsetByCursorForcedly = offsetByCursorForcedly;
        _limitByCursorForcedly = limitByCursorForcedly;

        skip();
    }

    // ===================================================================================
    //                                                                                Skip
    //                                                                                ====
    /**
     * Skip to the point at start index.
     */
    protected void skip() {
        if (!isAvailableSkipRecord()) {
            return;
        }
        final int skipStartIndex = getFetchNarrowingSkipStartIndex();
        if (isScrollableCursor()) {
            try {
                if (0 == skipStartIndex) {
                    _resultSet.beforeFirst();
                } else {
                    _resultSet.absolute(skipStartIndex);
                }
                _fetchCounter = _resultSet.getRow();
            } catch (SQLException e) {
                handleSQLException(e, null);
            }
        } else {
            try {
                while (true) {
					if (_fetchCounter >= skipStartIndex) {
					    break;
					}
					if (!_resultSet.next()) {
					    _skipToCursorEnd = true;// [DBFLUTE-243]
					    break;
					}
                    ++_fetchCounter;
                }
            } catch (SQLException e) {
                handleSQLException(e, null);
            }
        }
    }

    protected boolean isAvailableSkipRecord() {
        if (!isFetchNarrowingEffective()) {
            return false;
        }
        if (isOffsetByCursorForcedly()) {
            return true;
        }
        if (isFetchNarrowingSkipStartIndexEffective()) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                                Next
    //                                                                                ====
    /**
     * Move to the next record.
     * @return Does the result set have next record?
     * @throws SQLException
     */
    public boolean next() throws SQLException {
	    if (_db2 && _skipToCursorEnd) { // [DBFLUTE-243]
		    return false;
		}
        final boolean hasNext = super.next();
        ++_requestCounter;
        if (!isAvailableLimitLoopCount()) {
            checkSafetyResult(hasNext);
            return hasNext;
        }

        if (hasNext && _fetchCounter < getFetchNarrowingSkipStartIndex() + getFetchNarrowingLoopCount()) {
            ++_fetchCounter;
            checkSafetyResult(true);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isAvailableLimitLoopCount() {
        if (!isFetchNarrowingEffective()) {
            return false;
        }
        if (isLimitByCursorForcedly()) {
            return true;
        }
        if (isFetchNarrowingLoopCountEffective()) {
            return true;
        }
        return false;
    }

    protected void checkSafetyResult(boolean hasNext) {
        if (hasNext && getSafetyMaxResultSize() > 0 && _requestCounter > (getSafetyMaxResultSize() + 1)) {
            String msg = "You have already been in Danger Zone!";
            msg = msg + " Please confirm your query or data of table: safetyMaxResultSize=" + getSafetyMaxResultSize();
            throw new org.seasar.dbflute.exception.DangerousResultSizeException(msg, getSafetyMaxResultSize());
        }
    }

    // ===================================================================================
    //                                                                        Fetch Option
    //                                                                        ============
    protected boolean isFetchNarrowingEffective() {
        return _fetchNarrowingBean.isFetchNarrowingEffective();
    }

    protected boolean isFetchNarrowingSkipStartIndexEffective() {
        return _fetchNarrowingBean.isFetchNarrowingSkipStartIndexEffective();
    }

    protected boolean isFetchNarrowingLoopCountEffective() {
        return _fetchNarrowingBean.isFetchNarrowingLoopCountEffective();
    }

    protected int getFetchNarrowingSkipStartIndex() {
        return _fetchNarrowingBean.getFetchNarrowingSkipStartIndex();
    }

    protected int getFetchNarrowingLoopCount() {
        return _fetchNarrowingBean.getFetchNarrowingLoopCount();
    }

    public int getSafetyMaxResultSize() {
        return _fetchNarrowingBean.getSafetyMaxResultSize();
    }

    protected boolean isScrollableCursor() {
        try {
            return !(_resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY);
        } catch (SQLException e) {
            handleSQLException(e, null);
            return false;// Unreachable!
        }
    }

    // ===================================================================================
    //                                                                   Exception Handler
    //                                                                   =================
    protected void handleSQLException(SQLException e, Statement statement) {
        new SQLExceptionHandler().handleSQLException(e, statement);
    }
    
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isOffsetByCursorForcedly() {
        return _offsetByCursorForcedly;
    }

    public boolean isLimitByCursorForcedly() {
        return _limitByCursorForcedly;
    }
	
	public boolean isSkipToCursorEnd() {
	    return _skipToCursorEnd;
	}
}
