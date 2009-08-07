package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAssertionFailureInvalidMarkException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfAssertionFailureInvalidMarkException(String msg) {
        super(msg);
    }

    public DfAssertionFailureInvalidMarkException(String msg, Throwable e) {
        super(msg, e);
    }
}
