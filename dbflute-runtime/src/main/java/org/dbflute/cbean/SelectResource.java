package org.dbflute.cbean;

/**
 * The select-resource as marker-interface.
 * 
 * @author DBFlute(AutoGenerator)
 */
public interface SelectResource {

    /**
     * Check safety result.
     * 
     * @param safetyMaxResultSize Safety max result size. (If zero or minus, ignore checking)
     */
    public void checkSafetyResult(int safetyMaxResultSize);
}
