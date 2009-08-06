package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAssertionFailureCountNotExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfAssertionFailureCountNotExistsException(String msg) {
        super(msg);
    }

    public DfAssertionFailureCountNotExistsException(String msg, Throwable e) {
        super(msg, e);
    }
}
