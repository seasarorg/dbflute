package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeAssertAssertionFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTakeAssertAssertionFailureException(String msg) {
        super(msg);
    }

    public DfTakeAssertAssertionFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
