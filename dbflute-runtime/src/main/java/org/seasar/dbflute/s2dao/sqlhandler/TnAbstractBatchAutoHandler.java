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
package org.seasar.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityDuplicatedException;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractBatchAutoHandler extends TnAbstractAutoHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(TnAbstractBatchAutoHandler.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractBatchAutoHandler(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, TnPropertyType[] boundPropTypes) {
        super(dataSource, statementFactory, beanMetaData, boundPropTypes);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    public int execute(Object[] args) {
        String msg = "This method should not be called when BatchUpdate.";
        throw new IllegalStateException(msg);
    }

    public int[] executeBatch(List<?> beanList) {
        if (beanList == null) {
            String msg = "The argument 'beanList' should not be null";
            throw new IllegalArgumentException(msg);
        }
        if (beanList.isEmpty()) {
            if (_log.isDebugEnabled()) {
                _log.debug("Skip executeBatch() bacause of the empty list.");
            }
            return new int[0];
        }
        final Connection conn = getConnection();
        try {
            final PreparedStatement ps = prepareStatement(conn);
            try {
                for (Object bean : beanList) {
                    preBatchUpdateBean(bean);
                    prepareBatchElement(conn, ps, bean);
                }
                final int[] result = executeBatch(ps, beanList);
                handleBatchUpdateResultWithOptimisticLock(ps, beanList, result);
                // a value of optimistic lock column should be synchronized
                // after handling optimistic lock
                for (Object bean : beanList) {
                    postBatchUpdateBean(bean);
                }
                return result;
            } finally {
                close(ps);
            }
        } finally {
            close(conn);
        }
    }

    protected void prepareBatchElement(Connection conn, PreparedStatement ps, Object bean) {
        setupBindVariables(bean);
        logSql(getBindVariables(), getArgTypes(getBindVariables()));
        bindArgs(conn, ps, getBindVariables(), getBindVariableValueTypes());
        addBatch(ps);
    }

    // ===================================================================================
    //                                                                       Pre/Post Bean
    //                                                                       =============
    // *after case about identity is unsupported at Batch Update   
    protected void preBatchUpdateBean(Object bean) {
    }

    protected void postBatchUpdateBean(Object bean) {
        updateTimestampIfNeed(bean);
        updateVersionNoIfNeed(bean);
    }

    // ===================================================================================
    //                                                                     Optimistic Lock
    //                                                                     ===============
    protected void handleBatchUpdateResultWithOptimisticLock(PreparedStatement ps, List<?> list, int[] result) {
        if (isCurrentDBDef(DBDef.Oracle)) {
            final int updateCount;
            try {
                updateCount = ps.getUpdateCount();
            } catch (SQLException e) {
                handleSQLException(e, ps);
                return; // unreachable
            }
            handleBatchUpdateResultWithOptimisticLockByUpdateCount(list, updateCount);
        } else {
            handleBatchUpdateResultWithOptimisticLockByResult(list, result);
        }
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
        return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    protected void handleBatchUpdateResultWithOptimisticLockByUpdateCount(List<?> list, int updateCount) {
        if (list.isEmpty()) {
            return; // for safety
        }
        if (updateCount < 0) {
            return; // for safety
        }
        final int entityCount = list.size();
        if (updateCount < entityCount) {
            if (_optimisticLockHandling) {
                throw new BatchEntityAlreadyUpdatedException(list.get(0), 0, updateCount);
            } else {
                String msg = "The entity have already deleted:";
                msg = msg + " updateCount=" + updateCount;
                msg = msg + " entityCount=" + entityCount;
                msg = msg + " allEntities=" + list;
                throw new EntityAlreadyDeletedException(msg);
            }
        }
    }

    protected void handleBatchUpdateResultWithOptimisticLockByResult(List<?> list, int[] result) {
        if (list.isEmpty()) {
            return; // for safety
        }
        final int[] updatedCountArray = result;
        final int entityCount = list.size();
        int index = 0;
        boolean alreadyUpdated = false;
        for (int oneUpdateCount : updatedCountArray) {
            if (entityCount <= index) {
                break; // for safety
            }
            if (oneUpdateCount == 0) {
                alreadyUpdated = true;
                break;
            } else if (oneUpdateCount > 1) {
                String msg = "The entity updated two or more records in batch update:";
                msg = msg + " entity=" + list.get(index);
                msg = msg + " updatedCount=" + oneUpdateCount;
                msg = msg + " allEntities=" + list;
                throw new EntityDuplicatedException(msg);
            }
            ++index;
        }
        if (alreadyUpdated) {
            int updateCount = 0;
            for (int oneUpdateCount : updatedCountArray) {
                updateCount = updateCount + oneUpdateCount;
            }
            if (_optimisticLockHandling) {
                throw new BatchEntityAlreadyUpdatedException(list.get(index), 0, updateCount);
            } else {
                String msg = "The entity have already deleted:";
                msg = msg + " entity=" + list.get(index);
                msg = msg + " updateCount=" + updateCount;
                msg = msg + " allEntities=" + list;
                throw new EntityAlreadyDeletedException(msg);
            }
        }
    }

    // ===================================================================================
    //                                                                      JDBC Delegator
    //                                                                      ==============
    protected int[] executeBatch(PreparedStatement ps, List<?> list) {
        try {
            return ps.executeBatch();
        } catch (SQLException e) {
            handleSQLException(e, ps, true);
            return null; // unreachable
        }
    }

    protected void addBatch(PreparedStatement ps) {
        try {
            ps.addBatch();
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }
}
