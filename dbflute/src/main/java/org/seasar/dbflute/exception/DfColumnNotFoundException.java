package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfColumnNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfColumnNotFoundException(String msg) {
        super(msg);
    }

    public DfColumnNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
