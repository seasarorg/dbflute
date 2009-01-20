package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.cbean.FetchNarrowingBeanContext;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.dbflute.s2dao.sqlcommand.TnUpdateQueryAutoDynamicCommand;

/**
 * @author jflute
 */
public class QueryUpdateEntityCBCommand extends AbstractEntityCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of condition-bean. (Derived from conditionBean) */
    protected Class<? extends ConditionBean> _conditionBeanType;

    /** The instance of condition-bean. (Required) */
    protected ConditionBean _conditionBean;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "queryUpdate";
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    @Override
    public boolean isConditionBean() {
        return true;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    @Override
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        final ConditionBean cb = _conditionBean;
        FetchNarrowingBeanContext.setFetchNarrowingBeanOnThread(cb);
        ConditionBeanContext.setConditionBeanOnThread(cb);
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        return _tableDbName + ":" + getCommandName() + "(" + _entityType.getSimpleName() + ", " + _conditionBeanType.getSimpleName() + ")";
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                return createQueryUpdateEntityCBExecution(_conditionBeanType);
            }
        };
    }

    protected SqlExecution createQueryUpdateEntityCBExecution(Class<? extends ConditionBean> cbType) {
        return new TnUpdateQueryAutoDynamicCommand(_dataSource, _statementFactory);
    }

    public Object[] getSqlExecutionArgument() {
        assertStatus("getSqlExecutionArgument");
        return new Object[] { _conditionBean, _entity };
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
    @Override
    protected void assertStatus(String methodName) {
        assertBasicProperty(methodName);
        assertComponentProperty(methodName);
        if (_entityType == null) {
            throw new IllegalStateException(buildAssertMessage("_entityType", methodName));
        }
        if (_entity == null) {
            throw new IllegalStateException(buildAssertMessage("_entity", methodName));
        }
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
