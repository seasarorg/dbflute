package org.seasar.dbflute.exception;

/**
 * The exception of when the result of IF comment is not boolean about outsideSql.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class IfCommentNotBooleanResultException extends IfCommentWrongExpressionException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param msg Exception message. (NotNull)
     */
    public IfCommentNotBooleanResultException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * 
     * @param msg Exception message. (NotNull)
     * @param cause Throwable.
     */
    public IfCommentNotBooleanResultException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
