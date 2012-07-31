package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyNonAssertionSqlFoundException extends DfTakeFinallyAssertionFailureException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyNonAssertionSqlFoundException(String msg) {
        super(msg);
    }

    public DfTakeFinallyNonAssertionSqlFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
