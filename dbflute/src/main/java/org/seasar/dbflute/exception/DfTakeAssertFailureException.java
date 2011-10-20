package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeAssertFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTakeAssertFailureException(String msg) {
        super(msg);
    }

    public DfTakeAssertFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
