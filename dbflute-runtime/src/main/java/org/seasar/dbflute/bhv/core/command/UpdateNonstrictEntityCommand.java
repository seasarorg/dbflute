package org.seasar.dbflute.bhv.core.command;

/**
 * @author jflute
 */
public class UpdateNonstrictEntityCommand extends UpdateEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    @Override
    public String getCommandName() {
        return "updateNonstrict";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    protected boolean isOptimisticLockHandling() {
        return false;
    }
}
