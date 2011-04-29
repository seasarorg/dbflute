package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAlterCheckDifferenceFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckDifferenceFoundException(String msg) {
        super(msg);
    }

    public DfAlterCheckDifferenceFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
