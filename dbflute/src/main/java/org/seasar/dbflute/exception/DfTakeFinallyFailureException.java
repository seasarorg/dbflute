package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyFailureException(String msg) {
        super(msg);
    }

    public DfTakeFinallyFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
