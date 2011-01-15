package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfXlsDataColumnDefFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfXlsDataColumnDefFailureException(String msg) {
        super(msg);
    }

    public DfXlsDataColumnDefFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
