package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAssertionInvalidMarkException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfAssertionInvalidMarkException(String msg) {
        super(msg);
    }

    public DfAssertionInvalidMarkException(String msg, Throwable e) {
        super(msg, e);
    }
}
