package org.seasar.dbflute.bhv.core.execution;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.sqlcommand.TnAbstractDynamicCommand;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;
import org.seasar.dbflute.twowaysql.context.CommandContext;


/**
 * @author jflute
 */
public class BasicSelectExecution extends TnAbstractDynamicCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of resultSet. */
    protected TnResultSetHandler resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource Data source.
     * @param statementFactory The factory of statement.
     * @param resultSetHandler The handler of resultSet.
     */
    public BasicSelectExecution(DataSource dataSource, StatementFactory statementFactory, TnResultSetHandler resultSetHandler) {
        super(dataSource, statementFactory);
        this.resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * @param args The array of argument. (Nullable)
     * @return The object of execution result. (Nullable)
     */
    public Object execute(Object[] args) {
        final CommandContext ctx = apply(args);
        final TnBasicSelectHandler selectHandler = createBasicSelectHandler(ctx.getSql(), this.resultSetHandler);
        final Object[] bindVariableArray = ctx.getBindVariables();
        selectHandler.setLoggingMessageSqlArgs(bindVariableArray);
        return selectHandler.execute(bindVariableArray, ctx.getBindVariableTypes());
    }

    // ===================================================================================
    //                                                                      Select Handler
    //                                                                      ==============
    protected TnBasicSelectHandler createBasicSelectHandler(String realSql, TnResultSetHandler rsh) {
        return new TnBasicSelectHandler(getDataSource(), realSql, rsh, getStatementFactory());
    }

    // ===================================================================================
    //                                                                       Parser Option
    //                                                                       =============
    @Override
    protected boolean isBlockNullParameter() {
        return true; // Because the SQL is select.
    }
}
