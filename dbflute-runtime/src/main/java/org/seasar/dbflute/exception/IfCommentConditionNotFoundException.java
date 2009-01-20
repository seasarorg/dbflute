package org.seasar.dbflute.exception;

/**
 * The exception of when the condition of IF comment is not found about outsideSql.
 * @author jflute
 */
public class IfCommentConditionNotFoundException extends IfCommentWrongExpressionException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     */
    public IfCommentConditionNotFoundException(String msg) {
        super(msg);
    }
}
