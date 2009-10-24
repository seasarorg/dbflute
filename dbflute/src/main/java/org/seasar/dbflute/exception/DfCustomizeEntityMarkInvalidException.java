package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCustomizeEntityMarkInvalidException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfCustomizeEntityMarkInvalidException(String msg) {
        super(msg);
    }

    public DfCustomizeEntityMarkInvalidException(String msg, Throwable e) {
        super(msg, e);
    }
}
