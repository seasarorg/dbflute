package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfProcedureExecutionMetaGettingFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfProcedureExecutionMetaGettingFailureException(String msg) {
        super(msg);
    }

    public DfProcedureExecutionMetaGettingFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
