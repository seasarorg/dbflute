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
package org.seasar.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dbflute.cbean.SelectBeanContext;
import org.seasar.dbflute.cbean.FetchNarrowingBean;
import org.seasar.dbflute.cbean.SelectBean;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.s2dao.jdbc.TnFunctionalResultSet;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnBasicSelectHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnResultSetHandler resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicSelectHandler(DataSource dataSource, String sql, TnResultSetHandler resultSetHandler,
            StatementFactory statementFactory) {
        super(dataSource, statementFactory);
        setSql(sql);
        setResultSetHandler(resultSetHandler);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        return execute(args, getArgTypes(args));
    }

    public Object execute(Object[] args, Class<?>[] argTypes) {
        Connection conn = getConnection();
        try {
            return execute(conn, args, argTypes);
        } finally {
            close(conn);
        }
    }

    public Object execute(Connection conn, Object[] args, Class<?>[] argTypes) {
        logSql(args, argTypes);
        PreparedStatement ps = null;
        try {
            ps = prepareStatement(conn);
            bindArgs(ps, args, argTypes);
            return execute(ps);
        } catch (SQLException e) {
            handleSQLException(e, ps);
            return null; // Unreachable!
        } finally {
            close(ps);
        }
    }

    protected Object execute(PreparedStatement ps) throws SQLException {
        if (resultSetHandler == null) {
            throw new IllegalStateException("The resultSetHandler should not be null!");
        }
        ResultSet resultSet = null;
        try {
            resultSet = createResultSet(ps);
            return resultSetHandler.handle(resultSet);
        } finally {
            close(resultSet);
        }
    }

    protected ResultSet createResultSet(PreparedStatement ps) throws SQLException {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
        // All select statements on DBFlute use this result set. 
        // - - - - - - - - - -/
        final ResultSet resultSet = ps.executeQuery();
        if (!isUseFunctionalResultSet()) {
            return resultSet;
        }
        final SelectBean selbean = SelectBeanContext.getSelectBeanOnThread();
        final TnFunctionalResultSet wrapper;
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext context = OutsideSqlContext.getOutsideSqlContextOnThread();
            final boolean offsetByCursorForcedly = context.isOffsetByCursorForcedly();
            final boolean limitByCursorForcedly = context.isLimitByCursorForcedly();
            wrapper = createFunctionalResultSet(resultSet, selbean, offsetByCursorForcedly, limitByCursorForcedly);
        } else {
            wrapper = createFunctionalResultSet(resultSet, selbean, false, false);
        }
        return wrapper;
    }

    protected boolean isUseFunctionalResultSet() {
        // about select-bean (priority one)
        if (!SelectBeanContext.isExistSelectBeanOnThread()) {
            return false;
        }
        final SelectBean selbean = SelectBeanContext.getSelectBeanOnThread();
        if (selbean.getSafetyMaxResultSize() > 0) {
            return true;
        }
        
        // about fetch-narrowing-bean (priority two)
        if (!SelectBeanContext.isExistFetchNarrowingBeanOnThread()) {
            return false;
        }
        final FetchNarrowingBean fnbean = SelectBeanContext.getFetchNarrowingBeanOnThread();
        if (!fnbean.isFetchNarrowingEffective()) {
            return false; // It is not necessary to control.
        }
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
            if (outsideSqlContext.isOffsetByCursorForcedly() || outsideSqlContext.isLimitByCursorForcedly()) {
                return true;
            }
        }
        if (fnbean.isFetchNarrowingSkipStartIndexEffective() || fnbean.isFetchNarrowingLoopCountEffective()) {
            return true;
        }
        return false;
    }

    protected TnFunctionalResultSet createFunctionalResultSet(ResultSet resultSet, SelectBean selbean,
            boolean offsetByCursorForcedly, boolean limitByCursorForcedly) {
        return new TnFunctionalResultSet(resultSet, selbean, offsetByCursorForcedly, limitByCursorForcedly);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    public void setResultSetHandler(TnResultSetHandler resultSetHandler) {
        this.resultSetHandler = resultSetHandler;
    }
}
