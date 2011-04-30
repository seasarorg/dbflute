package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAlterCheckReplaceSchemaFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckReplaceSchemaFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
