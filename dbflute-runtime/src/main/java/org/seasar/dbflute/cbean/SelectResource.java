package org.seasar.dbflute.cbean;

/**
 * The select-resource as marker-interface.
 * 
 * @author jflute
 */
public interface SelectResource {

    /**
     * Check safety result.
     * 
     * @param safetyMaxResultSize Safety max result size. (If zero or minus, ignore checking)
     */
    public void checkSafetyResult(int safetyMaxResultSize);
}
