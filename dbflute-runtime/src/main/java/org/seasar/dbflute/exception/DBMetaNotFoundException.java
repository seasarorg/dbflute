package org.seasar.dbflute.exception;

/**
 * The exception of when the DB meta is not found.
 * @author DBFlute(AutoGenerator)
 */
public class DBMetaNotFoundException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     */
    public DBMetaNotFoundException(String msg) {
        super(msg);
    }
}
