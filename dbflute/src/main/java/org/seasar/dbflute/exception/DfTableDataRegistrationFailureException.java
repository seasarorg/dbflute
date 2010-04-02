package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTableDataRegistrationFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTableDataRegistrationFailureException(String msg) {
        super(msg);
    }

    public DfTableDataRegistrationFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
