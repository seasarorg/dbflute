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

import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlcommand.TnBatchInsertAutoStaticCommand;

/**
 * @author jflute
 */
public class BatchInsertEntityCommand extends AbstractListEntityCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The option of insert. (NotRequired) */
    protected InsertOption<? extends ConditionBean> _insertOption;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "batchInsert";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    public String buildSqlExecutionKey() {
        final String baseKey = super.buildSqlExecutionKey();
        if (_insertOption != null && _insertOption.isPrimaryIdentityInsertDisabled()) {
            return baseKey + ":PKIdentityDisabled";
        } else {
            return baseKey;
        }
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createBatchInsertEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createBatchInsertEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createInsertBatchAutoStaticCommand(bmd, propertyNames);
    }

    protected TnBatchInsertAutoStaticCommand createInsertBatchAutoStaticCommand(TnBeanMetaData bmd,
            String[] propertyNames) {
        final DBMeta dbmeta = findDBMeta();
        final TnBatchInsertAutoStaticCommand cmd = new TnBatchInsertAutoStaticCommand(_dataSource, _statementFactory,
                bmd, dbmeta, propertyNames, _insertOption);
        return cmd;
    }

    @Override
    protected Object[] doGetSqlExecutionArgument() {
        return new Object[] { _entityList }; // insertOption is not specified because of static command
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setInsertOption(InsertOption<? extends ConditionBean> insertOption) {
        _insertOption = insertOption;
    }
}
