package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCommentExtractingFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfCommentExtractingFailureException(String msg) {
        super(msg);
    }

    public DfCommentExtractingFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
