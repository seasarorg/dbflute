package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAlterCheckAlterSqlFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckAlterSqlFailureException(String msg) {
        super(msg);
    }

    public DfAlterCheckAlterSqlFailureException(String msg, SQLFailureException cause) {
        super(msg, cause);
    }
}
