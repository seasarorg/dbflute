package org.seasar.dbflute.bhv.core.command;

/**
 * @author jflute
 */
public class BatchUpdateNonstrictEntityCommand extends BatchUpdateEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    @Override
    public String getCommandName() {
        return "batchUpdateNonstrict";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    protected boolean isOptimisticLockHandling() {
        return false;
    }
}
