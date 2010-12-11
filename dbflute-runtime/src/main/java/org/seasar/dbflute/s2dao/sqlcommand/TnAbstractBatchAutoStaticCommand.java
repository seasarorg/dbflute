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

import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractBatchAutoStaticCommand extends TnAbstractAutoStaticCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractBatchAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData, targetDBMeta, propertyNames, optimisticLockHandling,
                versionNoAutoIncrementOnMemory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    public Object execute(Object[] args) {
        if (args == null || args.length == 0) {
            String msg = "The argument 'args' should not be null or empty.";
            throw new IllegalArgumentException(msg);
        }
        final List<?> beanList;
        if (args[0] instanceof List<?>) {
            beanList = (List<?>) args[0];
        } else {
            String msg = "The argument 'args[0]' should be list: " + args[0];
            throw new IllegalArgumentException(msg);
        }
        final TnAbstractBatchAutoHandler handler = createBatchAutoHandler();
        handler.setOptimisticLockHandling(_optimisticLockHandling);
        handler.setVersionNoAutoIncrementOnMemory(_versionNoAutoIncrementOnMemory);
        handler.setSql(getSql());
        // The logging message SQL of batch is unnecessary.
        // handler.setLoggingMessageSqlArgs(args);
        return handler.executeBatch(beanList);
    }

    protected abstract TnAbstractBatchAutoHandler createBatchAutoHandler();
}
