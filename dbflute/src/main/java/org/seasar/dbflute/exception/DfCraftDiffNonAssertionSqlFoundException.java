package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCraftDiffNonAssertionSqlFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfCraftDiffNonAssertionSqlFoundException(String msg) {
        super(msg);
    }

    public DfCraftDiffNonAssertionSqlFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
