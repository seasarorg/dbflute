package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.sqlhandler.TnBasicUpdateHandler;
import org.dbflute.twowaysql.context.CommandContext;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnUpdateDynamicCommand extends TnAbstractDynamicCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object args[]) {
        final CommandContext ctx = apply(args);
        final TnBasicUpdateHandler updateHandler = createBasicUpdateHandler(ctx);
        final Object[] bindVariables = ctx.getBindVariables();
        updateHandler.setLoggingMessageSqlArgs(bindVariables);
        return new Integer(updateHandler.execute(bindVariables, ctx.getBindVariableTypes()));
    }
    
    protected TnBasicUpdateHandler createBasicUpdateHandler(CommandContext ctx) {
        return new TnBasicUpdateHandler(getDataSource(), ctx.getSql(), getStatementFactory());
    }
}
