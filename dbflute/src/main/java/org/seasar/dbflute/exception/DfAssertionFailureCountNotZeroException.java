package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAssertionFailureCountNotZeroException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfAssertionFailureCountNotZeroException(String msg) {
        super(msg);
    }

    public DfAssertionFailureCountNotZeroException(String msg, Throwable e) {
        super(msg, e);
    }
}
