package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCustomizeEntityDuplicateException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfCustomizeEntityDuplicateException(String msg) {
        super(msg);
    }

    public DfCustomizeEntityDuplicateException(String msg, Throwable e) {
        super(msg, e);
    }
}
