package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAssertionFailureListNotZeroException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAssertionFailureListNotZeroException(String msg) {
        super(msg);
    }

    public DfAssertionFailureListNotZeroException(String msg, Throwable e) {
        super(msg, e);
    }
}
