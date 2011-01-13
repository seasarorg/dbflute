package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfSchemaEmptyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfSchemaEmptyException(String msg) {
        super(msg);
    }

    public DfSchemaEmptyException(String msg, Throwable e) {
        super(msg, e);
    }
}
