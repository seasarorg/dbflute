package dbflute.ldb;

/**
 * The entity of BookCollectionStatistic.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class BsBookCollectionStatistic implements java.io.Serializable {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    
    /** The value of bookId. */
    protected java.math.BigDecimal _bookId;
    
    /** The value of bookName. */
    protected String _bookName;
    
    /** The value of collectionCount. */
    protected java.math.BigDecimal _collectionCount;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========

    /**
     * Get the value of bookId.
     * 
     * @return The value of bookId. (Nullable)
     */
    public java.math.BigDecimal getBookId() {
        return _bookId;
    }

    /**
     * Set the value of bookId.
     * 
     * @param bookId The value of bookId. (Nullable)
     */
    public void setBookId(java.math.BigDecimal bookId) {
        _bookId = bookId;
    }

    /**
     * Get the value of bookName.
     * 
     * @return The value of bookName. (Nullable)
     */
    public String getBookName() {
        return _bookName;
    }

    /**
     * Set the value of bookName.
     * 
     * @param bookName The value of bookName. (Nullable)
     */
    public void setBookName(String bookName) {
        _bookName = bookName;
    }

    /**
     * Get the value of collectionCount.
     * 
     * @return The value of collectionCount. (Nullable)
     */
    public java.math.BigDecimal getCollectionCount() {
        return _collectionCount;
    }

    /**
     * Set the value of collectionCount.
     * 
     * @param collectionCount The value of collectionCount. (Nullable)
     */
    public void setCollectionCount(java.math.BigDecimal collectionCount) {
        _collectionCount = collectionCount;
    }

}
