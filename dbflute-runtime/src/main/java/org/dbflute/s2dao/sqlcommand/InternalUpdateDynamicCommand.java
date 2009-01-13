package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.sqlhandler.InternalBasicUpdateHandler;
import org.dbflute.twowaysql.context.CommandContext;

/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalUpdateDynamicCommand extends TnAbstractDynamicCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalUpdateDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object args[]) {
        final CommandContext ctx = apply(args);
        final InternalBasicUpdateHandler updateHandler = createBasicUpdateHandler(ctx);
        final Object[] bindVariables = ctx.getBindVariables();
        updateHandler.setLoggingMessageSqlArgs(bindVariables);
        return new Integer(updateHandler.execute(bindVariables, ctx.getBindVariableTypes()));
    }
    
    protected InternalBasicUpdateHandler createBasicUpdateHandler(CommandContext ctx) {
        return new InternalBasicUpdateHandler(getDataSource(), ctx.getSql(), getStatementFactory());
    }
}
