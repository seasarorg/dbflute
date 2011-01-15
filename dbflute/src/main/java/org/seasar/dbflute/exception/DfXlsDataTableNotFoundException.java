package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfXlsDataTableNotFoundException extends DfTableNotFoundException {

    private static final long serialVersionUID = 1L;

    public DfXlsDataTableNotFoundException(String msg) {
        super(msg);
    }

    public DfXlsDataTableNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
