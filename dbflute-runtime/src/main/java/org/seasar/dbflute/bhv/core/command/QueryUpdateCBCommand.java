/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.dbflute.s2dao.sqlcommand.TnQueryUpdateDynamicCommand;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class QueryUpdateCBCommand extends AbstractUpdateEntityCommand {

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
        ConditionBeanContext.setConditionBeanOnThread(cb);
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        final String main = _tableDbName + ":" + getCommandName();
        final String entityName = DfTypeUtil.toClassTitle(_entityType);
        final String cbName = DfTypeUtil.toClassTitle(_conditionBeanType);
        final String type = "(" + entityName + ", " + cbName + ")";
        return main + type;
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
        final TnQueryUpdateDynamicCommand sqlCommand = new TnQueryUpdateDynamicCommand(_dataSource, _statementFactory);
        sqlCommand.setBeanMetaData(createBeanMetaData());
        return sqlCommand;
    }

    @Override
    protected Object[] doGetSqlExecutionArgument() {
        return new Object[] { _conditionBean, _entity, _updateOption };
    }

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    @Override
    public ConditionBean getConditionBean() {
        return _conditionBean;
    }

    @Override
    public String getOutsideSqlPath() {
        return null;
    }

    @Override
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
