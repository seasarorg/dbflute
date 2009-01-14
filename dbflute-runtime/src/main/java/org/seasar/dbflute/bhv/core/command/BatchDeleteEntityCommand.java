package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlcommand.TnDeleteBatchAutoStaticCommand;


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

    protected TnDeleteBatchAutoStaticCommand createDeleteBatchAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        boolean opt = isOptimisticLockHandling();
        return new TnDeleteBatchAutoStaticCommand(_dataSource, _statementFactory, bmd, propertyNames, opt);
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
