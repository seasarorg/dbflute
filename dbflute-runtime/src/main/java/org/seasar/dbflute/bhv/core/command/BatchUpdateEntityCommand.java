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

import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlcommand.TnBatchUpdateAutoDynamicCommand;

/**
 * @author jflute
 */
public class BatchUpdateEntityCommand extends AbstractListEntityCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The option of update. (NotRequired) */
    protected UpdateOption<? extends ConditionBean> _updateOption;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "batchUpdate";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    public Object[] getSqlExecutionArgument() {
        assertStatus("getSqlExecutionArgument");
        return new Object[] { _entityList, _updateOption };
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createBatchUpdateEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createBatchUpdateEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createBatchUpdateAutoDynamicCommand(bmd, propertyNames);
    }

    protected TnBatchUpdateAutoDynamicCommand createBatchUpdateAutoDynamicCommand(TnBeanMetaData bmd,
            String[] propertyNames) {
        final TnBatchUpdateAutoDynamicCommand cmd = new TnBatchUpdateAutoDynamicCommand(_dataSource, _statementFactory);
        cmd.setBeanMetaData(bmd);
        cmd.setTargetDBMeta(findDBMeta());
        cmd.setPropertyNames(propertyNames);
        cmd.setOptimisticLockHandling(isOptimisticLockHandling());
        cmd.setVersionNoAutoIncrementOnMemory(isVersionNoAutoIncrementOnMemory());
        return cmd;
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }

    protected boolean isVersionNoAutoIncrementOnMemory() {
        return isOptimisticLockHandling();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setUpdateOption(UpdateOption<? extends ConditionBean> updateOption) {
        _updateOption = updateOption;
    }
}
