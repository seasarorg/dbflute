package org.seasar.dbflute.bhv.core.command;

/**
 * @author jflute
 */
public class BatchDeleteNonstrictEntityCommand extends BatchDeleteEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    @Override
    public String getCommandName() {
        return "batchDeleteNonstrict";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    protected boolean isOptimisticLockHandling() {
        return false;
    }
}
