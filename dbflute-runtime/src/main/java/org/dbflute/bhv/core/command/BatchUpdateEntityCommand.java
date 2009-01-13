package org.dbflute.bhv.core.command;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.SqlExecutionCreator;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlcommand.InternalUpdateBatchAutoStaticCommand;


/**
 * @author DBFlute(AutoGenerator)
 */
public class BatchUpdateEntityCommand extends AbstractListEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "batchUpdate";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
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
        return createUpdateBatchAutoStaticCommand(bmd, propertyNames);
    }

    protected InternalUpdateBatchAutoStaticCommand createUpdateBatchAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        boolean opt = isOptimisticLockHandling();
        return new InternalUpdateBatchAutoStaticCommand(_dataSource, _statementFactory, bmd, propertyNames, opt, opt);
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
