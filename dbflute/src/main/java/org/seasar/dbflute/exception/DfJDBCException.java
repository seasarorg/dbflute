package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfJDBCException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfJDBCException(String msg) {
        super(msg);
    }

    public DfJDBCException(String msg, Throwable e) {
        super(msg, e);
    }
}
