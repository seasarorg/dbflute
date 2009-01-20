package org.seasar.dbflute.bhv.core.command;

/**
 * @author jflute
 */
public class DeleteNonstrictEntityCommand extends DeleteEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    @Override
    public String getCommandName() {
        return "deleteNonstrict";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    protected boolean isOptimisticLockHandling() {
        return false;
    }
}
