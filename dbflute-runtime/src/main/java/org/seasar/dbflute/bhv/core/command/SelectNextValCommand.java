package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.bhv.core.execution.BasicSelectExecution;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;


/**
 * @author DBFlute(AutoGenerator)
 * @param <RESULT> The type of result.
 */
public class SelectNextValCommand<RESULT> extends AbstractBehaviorCommand<RESULT> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of result. (NotNull) */
    protected Class<RESULT> _resultType;
    
    /** The provider of DB meta. (NotNull) */
    protected DBMeta _dbmeta;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "selectNextVal";
    }

    public Class<?> getCommandReturnType() {
        return _resultType;
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isConditionBean() {
        return false;
    }

    public boolean isOutsideSql() {
        return false;
    }

    public boolean isProcedure() {
        return false;
    }

    public boolean isSelect() {
        return true;
    }

    public boolean isSelectCount() {
        return false;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
    }

    public void afterExecuting() {
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        return _tableDbName + ":" + getCommandName() + "()";
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                TnResultSetHandler handler = createObjectResultSetHandler(_resultType);
                return createSelectNextValExecution(handler);
            }
        };
    }

    protected SqlExecution createSelectNextValExecution(TnResultSetHandler handler) {
        assertStatus("createSelectNextValExecution");
        final DBMeta dbmeta = findDBMeta();
        if (!dbmeta.hasSequence()) {
            String msg = "If the method 'selectNextVal()' exists, DBMeta.hasSequence() should return true:";
            msg = msg + " dbmeta.hasSequence()=" + dbmeta.hasSequence();
            throw new IllegalStateException(msg);
        }
        final String nextValSql = dbmeta.getSequenceNextValSql();
        if (nextValSql == null) {
            String msg = "If the method 'selectNextVal()' exists, DBMeta.getSequenceNextValSql() should not return null:";
            msg = msg + " dbmeta.getSequenceNextValSql()=" + dbmeta.getSequenceNextValSql();
            throw new IllegalStateException(msg);
        }
        return createBasicSelectExecution(handler, new String[]{}, new Class<?>[]{}, nextValSql);
    }

    protected BasicSelectExecution createBasicSelectExecution(TnResultSetHandler handler, String[] argNames, Class<?>[] argTypes, String sql) {
        final BasicSelectExecution cmd = new BasicSelectExecution(_dataSource, _statementFactory, handler);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(argTypes);
        cmd.setSql(sql);
        return cmd;
    }

    protected DBMeta findDBMeta() {
        return _dbmeta;
    }

    public Object[] getSqlExecutionArgument() {
        assertStatus("getSqlExecutionArgument");
        return new Object[]{};
    }

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    public ConditionBean getConditionBean() {
        return null;
    }

    public String getOutsideSqlPath() {
        return null;
    }

    public OutsideSqlOption getOutsideSqlOption() {
        return null;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertStatus(String methodName) {
        assertBasicProperty(methodName);
        assertComponentProperty(methodName);
        if (_dbmeta == null) {
            throw new IllegalStateException(buildAssertMessage("_dbmeta", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setResultType(Class<RESULT> resultType) {
        _resultType = resultType;
    }

    public void setDBMeta(DBMeta dbmeta) {
        _dbmeta = dbmeta;
    }
}
