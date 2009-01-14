package org.seasar.dbflute.exception;

/**
 * The exception when the record has already been deleted (by other thread). <br />
 * This class is old.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class RecordHasAlreadyBeenDeletedException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     */
    public RecordHasAlreadyBeenDeletedException(String msg) {
        super(msg);
    }
}
