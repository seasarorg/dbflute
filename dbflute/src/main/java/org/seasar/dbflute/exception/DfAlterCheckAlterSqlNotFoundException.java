package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAlterCheckAlterSqlNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckAlterSqlNotFoundException(String msg) {
        super(msg);
    }

    public DfAlterCheckAlterSqlNotFoundException(String msg, SQLFailureException cause) {
        super(msg, cause);
    }
}
