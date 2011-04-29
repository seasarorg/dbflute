package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAlterCheckRollbackSchemaFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckRollbackSchemaFailureException(String msg) {
        super(msg);
    }

    public DfAlterCheckRollbackSchemaFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
