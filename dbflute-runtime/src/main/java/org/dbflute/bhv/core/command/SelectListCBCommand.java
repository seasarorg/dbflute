package org.dbflute.bhv.core.command;

import java.util.List;

import org.dbflute.Entity;
import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.SqlExecutionCreator;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionBeanContext;
import org.dbflute.cbean.FetchNarrowingBeanContext;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.extension.jdbc.ResultSetHandler;


/**
 * @author DBFlute(AutoGenerator)
 * @param <ENTITY> The type of entity.
 */
public class SelectListCBCommand<ENTITY extends Entity> extends AbstractSelectCBCommand<List<ENTITY>> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of entity. (Required) */
    protected Class<ENTITY> _entityType;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "selectList";
    }

    public Class<?> getCommandReturnType() {
        return List.class;
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isSelectCount() {
        return false;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        final ConditionBean cb = _conditionBean;
        FetchNarrowingBeanContext.setFetchNarrowingBeanOnThread(cb);
        ConditionBeanContext.setConditionBeanOnThread(cb);
    }

    public void afterExecuting() {
        assertStatus("afterExecuting");
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                TnBeanMetaData bmd = createBeanMetaData();
                ResultSetHandler handler = createBeanListMetaDataResultSetHandler(bmd);
                return createSelectCBExecution(_conditionBeanType, handler);
            }
        };
    }

    protected TnBeanMetaData createBeanMetaData() {
        return _beanMetaDataFactory.createBeanMetaData(_entityType);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    @Override
    protected void assertStatus(String methodName) {
        super.assertStatus(methodName);
        if (_entityType == null) {
            throw new IllegalStateException(buildAssertMessage("_entityType", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setEntityType(Class<ENTITY> entityType) {
        _entityType = entityType;
    }
}
