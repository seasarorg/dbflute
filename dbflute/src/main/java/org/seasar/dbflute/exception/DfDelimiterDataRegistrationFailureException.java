package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfDelimiterDataRegistrationFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfDelimiterDataRegistrationFailureException(String msg) {
        super(msg);
    }

    public DfDelimiterDataRegistrationFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
