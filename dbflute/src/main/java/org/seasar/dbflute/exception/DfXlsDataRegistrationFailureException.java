package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfXlsDataRegistrationFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfXlsDataRegistrationFailureException(String msg) {
        super(msg);
    }

    public DfXlsDataRegistrationFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
