package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlcommand.TnUpdateBatchAutoStaticCommand;


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

    protected TnUpdateBatchAutoStaticCommand createUpdateBatchAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        boolean opt = isOptimisticLockHandling();
        return new TnUpdateBatchAutoStaticCommand(_dataSource, _statementFactory, bmd, propertyNames, opt, opt);
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
