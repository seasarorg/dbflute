package org.seasar.dbflute.cbean;

/**
 * The bean of fetch narrowing.
 * 
 * @author jflute
 */
public interface FetchNarrowingBean {

    /**
     * Get fetch start index.
     * 
     * @return Fetch start index.
     */
    public int getFetchNarrowingSkipStartIndex();

    /**
     * Get fetch size.
     * 
     * @return Fetch size.
     */
    public int getFetchNarrowingLoopCount();

    /**
     * Is fetch start index supported?
     * 
     * @return Determination.
     */
    public boolean isFetchNarrowingSkipStartIndexEffective();

    /**
     * Is fetch size supported?
     * 
     * @return Determination.
     */
    public boolean isFetchNarrowingLoopCountEffective();

    /**
     * Is fetch-narrowing effective?
     * 
     * @return Determination.
     */
    public boolean isFetchNarrowingEffective();

    /**
     * Ignore fetch narrowing. Only checking safety result size is valid. {INTERNAL METHOD}
     */
    public void ignoreFetchNarrowing();

    /**
     * Restore ignored fetch narrowing. {INTERNAL METHOD}
     */
    public void restoreIgnoredFetchNarrowing();

    /**
     * Get safety max result size.
     * 
     * @return Safety max result size.
     */
    public int getSafetyMaxResultSize();
}
