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
package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractBatchHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractEntityHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnBatchDeleteHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBatchDeleteAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBatchDeleteAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            DeleteOption<? extends ConditionBean> deleteOption) {
        super(dataSource, statementFactory, beanMetaData, targetDBMeta, propertyNames, optimisticLockHandling, false,
                null, deleteOption);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected TnAbstractEntityHandler createEntityHandler() {
        final TnAbstractBatchHandler handler = createBatchHandler();
        handler.setDeleteOption(_deleteOption);
        return handler;
    }

    @Override
    protected TnAbstractBatchHandler createBatchHandler() {
        return new TnBatchDeleteHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
    }

    @Override
    protected void setupPropertyTypes(String[] propertyNames) { // called by constructor
    }

    @Override
    protected void setupSql() { // called by constructor
        setupDeleteSql();
    }
}
