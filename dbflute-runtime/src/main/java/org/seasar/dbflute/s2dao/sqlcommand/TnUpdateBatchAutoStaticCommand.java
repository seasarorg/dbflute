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

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnUpdateBatchAutoHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnUpdateBatchAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateBatchAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData, targetDBMeta, propertyNames, optimisticLockHandling,
                versionNoAutoIncrementOnMemory);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected TnAbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

    @Override
    protected TnAbstractBatchAutoHandler createBatchAutoHandler() {
        final TnUpdateBatchAutoHandler handler = new TnUpdateBatchAutoHandler(getDataSource(), getStatementFactory(),
                getBeanMetaData(), getPropertyTypes());
        handler.setVersionNoAutoIncrementOnMemory(_versionNoAutoIncrementOnMemory);
        return handler;
    }

    @Override
    protected void setupSql() {
        setupUpdateSql();
    }

    @Override
    protected void setupPropertyTypes(String[] propertyNames) {
        setupUpdatePropertyTypes(propertyNames);
    }
}
