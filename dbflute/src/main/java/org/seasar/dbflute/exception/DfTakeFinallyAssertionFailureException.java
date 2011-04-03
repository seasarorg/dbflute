package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyAssertionFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyAssertionFailureException(String msg) {
        super(msg);
    }

    public DfTakeFinallyAssertionFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
