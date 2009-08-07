package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfDataAssertInvalidMarkException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfDataAssertInvalidMarkException(String msg) {
        super(msg);
    }

    public DfDataAssertInvalidMarkException(String msg, Throwable e) {
        super(msg, e);
    }
}
