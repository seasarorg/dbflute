package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAssertionFailureListNotExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfAssertionFailureListNotExistsException(String msg) {
        super(msg);
    }

    public DfAssertionFailureListNotExistsException(String msg, Throwable e) {
        super(msg, e);
    }
}
