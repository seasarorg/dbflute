package org.dbflute.bhv.core.command;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.SqlExecutionCreator;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlcommand.TnInsertBatchAutoStaticCommand;


/**
 * @author DBFlute(AutoGenerator)
 */
public class BatchInsertEntityCommand extends AbstractListEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "batchInsert";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
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

    protected TnInsertBatchAutoStaticCommand createInsertBatchAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        return new TnInsertBatchAutoStaticCommand(_dataSource, _statementFactory, bmd, propertyNames);
    }
}
