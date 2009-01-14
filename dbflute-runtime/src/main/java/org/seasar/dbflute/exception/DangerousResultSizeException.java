package org.seasar.dbflute.exception;

/**
 * The exception of when the result size is dangerous.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class DangerousResultSizeException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** Safety max result size. */
    protected int _safetyMaxResultSize;

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     * @param safetyMaxResultSize Safety max result size.
     * @param selectedCount Selected count.
     */
    public DangerousResultSizeException(String msg, int safetyMaxResultSize) {
        super(msg);
        this._safetyMaxResultSize = safetyMaxResultSize;
    }

    /**
     * Get safety max result size.
     * 
     * @return Safety max result size.
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }
}
