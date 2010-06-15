package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTableColumnNameNonCompilableConnectorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTableColumnNameNonCompilableConnectorException(String msg) {
        super(msg);
    }

    public DfTableColumnNameNonCompilableConnectorException(String msg, Throwable e) {
        super(msg, e);
    }
}
