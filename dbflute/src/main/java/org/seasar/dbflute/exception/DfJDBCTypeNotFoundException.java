package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfJDBCTypeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfJDBCTypeNotFoundException(String msg) {
        super(msg);
    }

    public DfJDBCTypeNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
