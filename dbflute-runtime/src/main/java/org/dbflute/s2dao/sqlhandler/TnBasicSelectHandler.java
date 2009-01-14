package org.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbflute.cbean.FetchNarrowingBean;
import org.dbflute.cbean.FetchNarrowingBeanContext;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.outsidesql.OutsideSqlContext;
import org.dbflute.s2dao.jdbc.FetchNarrowingResultSetWrapper;


/**
 * @author DBFlute(AutoGenerator)
 */
public class TnBasicSelectHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ResultSetHandler resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicSelectHandler(DataSource dataSource, String sql,
            ResultSetHandler resultSetHandler, StatementFactory statementFactory) {
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
        final ResultSet resultSet = ps.executeQuery();
        if (!FetchNarrowingBeanContext.isExistFetchNarrowingBeanOnThread()) {
            return resultSet;
        }
        final FetchNarrowingBean cb = FetchNarrowingBeanContext.getFetchNarrowingBeanOnThread();
        if (!isUseFetchNarrowingResultSetWrapper(cb)) {
            return resultSet;
        }
        final FetchNarrowingResultSetWrapper wrapper;
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
            wrapper = new FetchNarrowingResultSetWrapper(resultSet, cb, outsideSqlContext.isOffsetByCursorForcedly(), outsideSqlContext.isLimitByCursorForcedly());
        } else {
            wrapper = new FetchNarrowingResultSetWrapper(resultSet, cb, false, false);
        }
        return wrapper;
    }

    protected boolean isUseFetchNarrowingResultSetWrapper(FetchNarrowingBean cb) {
        if (cb.getSafetyMaxResultSize() > 0) {
            return true;
        }
        if (!cb.isFetchNarrowingEffective()) {
            return false; // It is not necessary to control.
        }
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
            if (outsideSqlContext.isOffsetByCursorForcedly() || outsideSqlContext.isLimitByCursorForcedly()) {
                return true;
            }
        }
        if (cb.isFetchNarrowingSkipStartIndexEffective() || cb.isFetchNarrowingLoopCountEffective()) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    public void setResultSetHandler(ResultSetHandler resultSetHandler) {
        this.resultSetHandler = resultSetHandler;
    }
}
