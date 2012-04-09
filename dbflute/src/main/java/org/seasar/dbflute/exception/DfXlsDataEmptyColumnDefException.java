package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfXlsDataEmptyColumnDefException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfXlsDataEmptyColumnDefException(String msg) {
        super(msg);
    }

    public DfXlsDataEmptyColumnDefException(String msg, Throwable e) {
        super(msg, e);
    }
}
