package org.dbflute.exception;

/**
 * The exception of when the property on bind variable comment is not found about outsideSql.
 * @author DBFlute(AutoGenerator)
 */
public class BindVariableCommentNotFoundPropertyException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     */
    public BindVariableCommentNotFoundPropertyException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     * @param cause Throwable.
     */
    public BindVariableCommentNotFoundPropertyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
