package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAlterCheckSavePreviousFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckSavePreviousFailureException(String msg) {
        super(msg);
    }

    public DfAlterCheckSavePreviousFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
