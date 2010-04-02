package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTableNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTableNotFoundException(String msg) {
        super(msg);
    }

    public DfTableNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
