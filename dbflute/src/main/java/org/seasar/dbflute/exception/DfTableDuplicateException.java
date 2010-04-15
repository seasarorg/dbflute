package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTableDuplicateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTableDuplicateException(String msg) {
        super(msg);
    }

    public DfTableDuplicateException(String msg, Throwable e) {
        super(msg, e);
    }
}
