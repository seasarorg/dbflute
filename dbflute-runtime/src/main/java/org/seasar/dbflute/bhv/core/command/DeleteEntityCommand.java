package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlcommand.TnDeleteAutoStaticCommand;


/**
 * @author jflute
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

    protected TnDeleteAutoStaticCommand createDeleteAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        return new TnDeleteAutoStaticCommand(_dataSource, _statementFactory, bmd, propertyNames, isOptimisticLockHandling());
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
