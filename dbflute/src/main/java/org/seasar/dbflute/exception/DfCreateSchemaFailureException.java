package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCreateSchemaFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfCreateSchemaFailureException(String msg) {
        super(msg);
    }

    public DfCreateSchemaFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
