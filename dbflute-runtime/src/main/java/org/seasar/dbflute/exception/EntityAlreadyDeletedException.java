package org.seasar.dbflute.exception;

/**
 * The exception of when the entity has already been deleted by other thread.
 * 
 * @author jflute
 */
public class EntityAlreadyDeletedException extends RecordHasAlreadyBeenDeletedException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     */
    public EntityAlreadyDeletedException(String msg) {
        super(msg);
    }
}
