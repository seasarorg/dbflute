package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfFixedConditionOptionConstraintFailureException extends DfIllegalPropertySettingException {

    private static final long serialVersionUID = 1L;

    public DfFixedConditionOptionConstraintFailureException(String msg) {
        super(msg);
    }

    public DfFixedConditionOptionConstraintFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
