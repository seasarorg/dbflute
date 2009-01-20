package org.seasar.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.twowaysql.context.CommandContext;

/**
 * @author jflute
 */
public class TnCommandContextHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected CommandContext commandContext;
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnCommandContextHandler(DataSource dataSource, StatementFactory statementFactory, CommandContext commandContext) {
        super(dataSource, statementFactory);
        this.commandContext = commandContext;
        setSql(commandContext.getSql());
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        final Connection connection = getConnection();
        try {
            return execute(connection, commandContext);
        } finally {
            close(connection);
        }
    }

    protected int execute(Connection connection, CommandContext context) {
        logSql(context.getBindVariables(), getArgTypes(context.getBindVariables()));
        PreparedStatement ps = prepareStatement(connection);
        int ret = -1;
        try {
            bindArgs(ps, context.getBindVariables(), context.getBindVariableTypes());
            ret = executeUpdate(ps);
        } finally {
            close(ps);
        }
        return ret;
    }
}
