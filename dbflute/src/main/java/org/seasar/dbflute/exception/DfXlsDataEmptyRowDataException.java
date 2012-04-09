package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfXlsDataEmptyRowDataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfXlsDataEmptyRowDataException(String msg) {
        super(msg);
    }

    public DfXlsDataEmptyRowDataException(String msg, Throwable e) {
        super(msg, e);
    }
}
