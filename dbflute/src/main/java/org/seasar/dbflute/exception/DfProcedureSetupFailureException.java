package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfProcedureSetupFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfProcedureSetupFailureException(String msg) {
        super(msg);
    }

    public DfProcedureSetupFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
