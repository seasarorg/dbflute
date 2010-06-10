package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfProcedureListGettingFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfProcedureListGettingFailureException(String msg) {
        super(msg);
    }

    public DfProcedureListGettingFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
