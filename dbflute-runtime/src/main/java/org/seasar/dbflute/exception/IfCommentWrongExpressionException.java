package org.seasar.dbflute.exception;

/**
 * The exception of when the IF comment has a wrong expression about outsideSql.
 * @author DBFlute(AutoGenerator)
 */
public class IfCommentWrongExpressionException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     */
    public IfCommentWrongExpressionException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     * @param cause Throwable.
     */
    public IfCommentWrongExpressionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
