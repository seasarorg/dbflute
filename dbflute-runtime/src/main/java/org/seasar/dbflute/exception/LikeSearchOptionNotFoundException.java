package org.seasar.dbflute.exception;

/**
 * The exception of when the required likeSearchOption is not found.
 * 
 * @author jflute
 */
public class LikeSearchOptionNotFoundException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     */
    public LikeSearchOptionNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     * @param cause Throwable.
     */
    public LikeSearchOptionNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
