package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class TableNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public TableNotFoundException(String msg) {
        super(msg);
    }

    public TableNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
