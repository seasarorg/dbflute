package org.dbflute.bhv.core.command;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.SqlExecutionCreator;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlcommand.InternalDeleteAutoStaticCommand;


/**
 * @author DBFlute(AutoGenerator)
 */
public class DeleteEntityCommand extends AbstractEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "delete";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createDeleteEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createDeleteEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createDeleteAutoStaticCommand(bmd, propertyNames);
    }

    protected InternalDeleteAutoStaticCommand createDeleteAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        return new InternalDeleteAutoStaticCommand(_dataSource, _statementFactory, bmd, propertyNames, isOptimisticLockHandling());
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
