package org.seasar.dbflute.s2dao.sqlcommand;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.sqlhandler.TnCommandContextHandler;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.Node;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class TnUpdateQueryAutoDynamicCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource dataSource;
    protected StatementFactory statementFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateQueryAutoDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        ConditionBean cb = extractConditionBeanWithCheck(args);
        Entity entity = extractEntityWithCheck(args);
        String[] argNames = new String[]{"dto", "entity"};
        Class<?>[] argTypes = new Class<?>[]{cb.getClass(), entity.getClass()};
        String twoWaySql = buildQueryUpdateTwoWaySql(cb, entity);
        if (twoWaySql == null) {
            return 0;// No execute!
        }
        CommandContext context = createCommandContext(twoWaySql, argNames, argTypes, args);
        TnCommandContextHandler handler = createCommandContextHandler(context);
        handler.setLoggingMessageSqlArgs(context.getBindVariables());
        int rows = handler.execute(args);
        return new Integer(rows);
    }
    
    protected ConditionBean extractConditionBeanWithCheck(Object[] args) {
        assertArgument(args);
        Object fisrtArg = args[0];
        if (!(fisrtArg instanceof ConditionBean)) {
            String msg = "The type of first argument should be " + ConditionBean.class + "! But:";
            msg = msg + " type=" + fisrtArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (ConditionBean) fisrtArg;
    }
    
    protected Entity extractEntityWithCheck(Object[] args) {
        assertArgument(args);
        Object secondArg = args[1];
        if (!(secondArg instanceof Entity)) {
            String msg = "The type of second argument should be " + Entity.class + "! But:";
            msg = msg + " type=" + secondArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (Entity) secondArg;
    }
	
    protected void assertArgument(Object[] args) {
        if (args == null || args.length <= 1) {
            String msg = "The arguments should have two argument! But:";
            msg = msg + " args=" + (args != null ? args.length : null);
            throw new IllegalArgumentException(msg);
        }
    }
    
    protected TnCommandContextHandler createCommandContextHandler(CommandContext context) {
        return new TnCommandContextHandler(dataSource, statementFactory, context);
    }

    /**
     * @param cb Condition-bean. (NotNull)
     * @param entity Entity. (NotNull)
     * @return The two-way SQL of query update. (Nullable: If the set of modified properties is empty, return null.)
     */
    protected String buildQueryUpdateTwoWaySql(ConditionBean cb, Entity entity) {
        Map<String, String> columnParameterMap = new LinkedHashMap<String, String>();
        DBMeta dbmeta = entity.getDBMeta();
        Set<String> modifiedPropertyNames = entity.getModifiedPropertyNames();
        if (modifiedPropertyNames.isEmpty()) {
            return null;
        }
        String currentPropertyName = null;
        try {
            for (String propertyName : modifiedPropertyNames) {
                currentPropertyName = propertyName;
                ColumnInfo columnInfo = dbmeta.findColumnInfo(propertyName);
                String columnName = columnInfo.getColumnDbName();
                Method getter = columnInfo.findGetter();
                Object value = getter.invoke(entity, (Object[])null);
                if (value != null) {
                    columnParameterMap.put(columnName, "/*entity." + propertyName + "*/null");
                } else {
                    columnParameterMap.put(columnName, "null");
                }
            }
            if (dbmeta.hasVersionNo()) {
                ColumnInfo columnInfo = dbmeta.getVersionNoColumnInfo();
                String columnName = columnInfo.getColumnDbName();
                columnParameterMap.put(columnName, columnName + " + 1");
            }
            if (dbmeta.hasUpdateDate()) {
                ColumnInfo columnInfo = dbmeta.getUpdateDateColumnInfo();
                Method setter = columnInfo.findSetter();
                if (Timestamp.class.isAssignableFrom(columnInfo.getPropertyType())) {
                    setter.invoke(entity, new Timestamp(System.currentTimeMillis()));
                } else {
                    setter.invoke(entity, new Date());
                }
                String columnName = columnInfo.getColumnDbName();
                columnParameterMap.put(columnName, "/*entity." + columnInfo.getPropertyName() + "*/null");
            }
        } catch (Exception e) {
            throwQueryUpdateFailureException(cb, entity, currentPropertyName, e);
        }
        return cb.getSqlClause().getClauseQueryUpdate(columnParameterMap);
    }
    
    protected void throwQueryUpdateFailureException(ConditionBean cb, Entity entity, String propertyName, Exception e) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "queryUpdate() failed to execute!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the parameter comment logic." + getLineSeparator();
        msg = msg + "It may exist the parameter comment that DOESN'T have an end comment." + getLineSeparator();
        msg = msg + "  For example:" + getLineSeparator();
        msg = msg + "    before (x) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3" + getLineSeparator();
        msg = msg + "    after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Doubtful Property Name]" + getLineSeparator() + propertyName + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[ConditionBean]" + getLineSeparator() + cb + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Entity]" + getLineSeparator() + entity + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Exception Message]" + getLineSeparator() + e.getMessage() + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new QueryUpdateFailureException(msg, e);
    }
	
    public static class QueryUpdateFailureException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public QueryUpdateFailureException(String msg, Exception e) {
            super(msg, e);
        }
    }

    protected CommandContext createCommandContext(String twoWaySql, String[] argNames, Class<?>[] argTypes, Object[] args) {
        CommandContext context;
        {
            SqlAnalyzer parser = new SqlAnalyzer(twoWaySql, true);
            Node node = parser.parse();
            CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        return context;
    }
	
    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }
}
