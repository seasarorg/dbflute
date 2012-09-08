package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfAlterCheckEmptyAlterSqlSuccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckEmptyAlterSqlSuccessException(String msg) {
        super(msg);
    }

    public DfAlterCheckEmptyAlterSqlSuccessException(String msg, SQLFailureException cause) {
        super(msg, cause);
    }
}
