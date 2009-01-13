package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.sqlhandler.TnCommandContextHandler;
import org.dbflute.twowaysql.SqlParser;
import org.dbflute.twowaysql.context.TnCommandContext;
import org.dbflute.twowaysql.context.TnCommandContextCreator;
import org.dbflute.twowaysql.node.Node;
import org.dbflute.util.SimpleSystemUtil;


/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalDeleteQueryAutoDynamicCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource dataSource;
    protected StatementFactory statementFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalDeleteQueryAutoDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        ConditionBean cb = extractConditionBeanWithCheck(args);
        String[] argNames = new String[]{"dto"};
        Class<?>[] argTypes = new Class<?>[]{cb.getClass()};
        String twoWaySql = buildQueryDeleteTwoWaySql(cb);
        TnCommandContext context = createCommandContext(twoWaySql, argNames, argTypes, args);
        TnCommandContextHandler handler = createCommandContextHandler(context);
        handler.setLoggingMessageSqlArgs(context.getBindVariables());
        int rows = handler.execute(args);
        return new Integer(rows);
    }
    
    protected ConditionBean extractConditionBeanWithCheck(Object[] args) {
        if (args == null || args.length == 0) {
            String msg = "The arguments should have one argument! But:";
            msg = msg + " args=" + (args != null ? args.length : null);
            throw new IllegalArgumentException(msg);
        }
        Object fisrtArg = args[0];
        if (!(fisrtArg instanceof ConditionBean)) {
            String msg = "The type of argument should be " + ConditionBean.class + "! But:";
            msg = msg + " type=" + fisrtArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (ConditionBean) fisrtArg;
    }
    
    protected TnCommandContextHandler createCommandContextHandler(TnCommandContext context) {
        return new TnCommandContextHandler(dataSource, statementFactory, context);
    }

    protected String buildQueryDeleteTwoWaySql(ConditionBean cb) {
        return cb.getSqlClause().getClauseQueryDelete();
    }
    
    protected TnCommandContext createCommandContext(String twoWaySql, String[] argNames, Class<?>[] argTypes, Object[] args) {
        TnCommandContext context;
        {
            SqlParser parser = new SqlParser(twoWaySql, true);
            Node node = parser.parse();
            TnCommandContextCreator creator = new TnCommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        return context;
    }
	
    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return SimpleSystemUtil.getLineSeparator();
    }
}
