package org.seasar.dbflute.bhv.core;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;

/**
 * @author DBFlute(AutoGenerator)
 * @param <RESULT> The type of result.
 */
public interface BehaviorCommand<RESULT> {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getTableDbName();
    public String getCommandName();
    
    /**
     * Get the return type of command.
     * This type is not related to generic type because this is for conversion and check only.
     * @return The return type of command. (NotNull)
     */
    public Class<?> getCommandReturnType();

    public boolean isInitializeOnly();

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isConditionBean();
    public boolean isOutsideSql();
    public boolean isProcedure();
    public boolean isSelect();
    public boolean isSelectCount();

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution();
    public void afterExecuting();

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey();
    public SqlExecutionCreator createSqlExecutionCreator();
    public Object[] getSqlExecutionArgument();

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    public ConditionBean getConditionBean();
    public String getOutsideSqlPath();
    public OutsideSqlOption getOutsideSqlOption();
}
