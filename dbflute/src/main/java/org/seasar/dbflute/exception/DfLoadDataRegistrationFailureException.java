package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfLoadDataRegistrationFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfLoadDataRegistrationFailureException(String msg) {
        super(msg);
    }

    public DfLoadDataRegistrationFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
