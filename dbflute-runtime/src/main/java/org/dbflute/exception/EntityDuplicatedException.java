package org.dbflute.exception;

/**
 * The exception of when the entity has been duplicated.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class EntityDuplicatedException extends RecordHasOverlappedException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg Exception message.
     */
    public EntityDuplicatedException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg Exception message.
     * @param cause Throwable.
     */
    public EntityDuplicatedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
