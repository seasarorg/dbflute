package org.seasar.dbflute.exception;

/**
 * The exception of when the outside-sql is not found.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class OutsideSqlNotFoundException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     */
    public OutsideSqlNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     * @param cause Throwable.
     */
    public OutsideSqlNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
