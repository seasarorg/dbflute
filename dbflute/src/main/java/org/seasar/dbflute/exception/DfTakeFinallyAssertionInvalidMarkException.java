package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyAssertionInvalidMarkException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyAssertionInvalidMarkException(String msg) {
        super(msg);
    }

    public DfTakeFinallyAssertionInvalidMarkException(String msg, Throwable e) {
        super(msg, e);
    }
}
