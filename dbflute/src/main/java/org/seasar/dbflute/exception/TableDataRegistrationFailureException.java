package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class TableDataRegistrationFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public TableDataRegistrationFailureException(String msg) {
        super(msg);
    }

    public TableDataRegistrationFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
