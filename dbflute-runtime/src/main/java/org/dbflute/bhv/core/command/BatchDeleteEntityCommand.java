package org.dbflute.bhv.core.command;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.SqlExecutionCreator;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlcommand.InternalDeleteBatchAutoStaticCommand;


/**
 * @author DBFlute(AutoGenerator)
 */
public class BatchDeleteEntityCommand extends AbstractListEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "batchDelete";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createBatchDeleteEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createBatchDeleteEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createDeleteBatchAutoStaticCommand(bmd, propertyNames);
    }

    protected InternalDeleteBatchAutoStaticCommand createDeleteBatchAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        boolean opt = isOptimisticLockHandling();
        return new InternalDeleteBatchAutoStaticCommand(_dataSource, _statementFactory, bmd, propertyNames, opt);
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
