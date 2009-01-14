package org.dbflute.bhv.core.command;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.execution.SelectCBExecution;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.outsidesql.OutsideSqlOption;
import org.dbflute.s2dao.jdbc.TnResultSetHandler;


/**
 * @author DBFlute(AutoGenerator)
 * @param <RESULT> The type of result.
 */
public abstract class AbstractSelectCBCommand<RESULT> extends AbstractBehaviorCommand<RESULT> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of condition-bean. (Derived from conditionBean) */
    protected Class<? extends ConditionBean> _conditionBeanType;

    /** The instance of condition-bean. (Required) */
    protected ConditionBean _conditionBean;

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isConditionBean() {
        return true;
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

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        return _tableDbName + ":" + getCommandName() + "(" + _conditionBeanType.getSimpleName() + ")";
    }

    protected SqlExecution createSelectCBExecution(Class<? extends ConditionBean> cbType, TnResultSetHandler handler) {
        return createSelectCBExecution(handler, new String[] { "dto" }, new Class<?>[] { cbType });
    }

    protected SelectCBExecution createSelectCBExecution(TnResultSetHandler handler, String[] argNames, Class<?>[] argTypes) {
        final SelectCBExecution cmd = new SelectCBExecution(_dataSource, _statementFactory, handler);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(argTypes);
        return cmd;
    }

    public Object[] getSqlExecutionArgument() {
        assertStatus("getSqlExecutionArgument");
        return new Object[] { _conditionBean };
    }

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    public ConditionBean getConditionBean() {
        return _conditionBean;
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
        if (_conditionBeanType == null) {
            throw new IllegalStateException(buildAssertMessage("_conditionBeanType", methodName));
        }
        if (_conditionBean == null) {
            throw new IllegalStateException(buildAssertMessage("_conditionBean", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setConditionBeanType(Class<? extends ConditionBean> conditionBeanType) {
        _conditionBeanType = conditionBeanType;
    }

    public void setConditionBean(ConditionBean conditionBean) {
        _conditionBean = conditionBean;
    }
}
