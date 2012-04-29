package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.coption.ColumnConversionOption;

/**
 * @author jflute
 */
public interface HpCalculator {

    // ===================================================================================
    //                                                                         Calculation
    //                                                                         ===========
    /**
     * Plus the specified column with the value. (+)
     * @param plusValue The number value for plus. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator plus(Number plusValue);

    /**
     * Plus the specified column with the plus column. (+)
     * @param plusColumn The plus column specified by Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator plus(HpSpecifiedColumn plusColumn);

    /**
     * Minus the specified column with the value. (-)
     * @param minusValue The number value for minus. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator minus(Number minusValue);

    /**
     * Minus the specified column with the minus column. (-)
     * @param minusColumn The minus column specified by Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator minus(HpSpecifiedColumn minusColumn);

    /**
     * Multiply the value to the specified column. (*)
     * @param multiplyValue The number value for multiply. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator multiply(Number multiplyValue);

    /**
     * Multiply the specified column with the multiply column. (*)
     * @param multiplyColumn The multiply column specified by Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator multiply(HpSpecifiedColumn multiplyColumn);

    /**
     * Divide the specified column by the value. (/)
     * @param divideValue The number value for divide. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator divide(Number divideValue);

    /**
     * Divide the specified column with the divide column. (/)
     * @param divideColumn The divide column specified by Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator divide(HpSpecifiedColumn divideColumn);

    /**
     * Convert the value of right column by function.
     * @param option The conversion option of column. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator convert(ColumnConversionOption option);

    // ===================================================================================
    //                                                                     Left/Right Mode
    //                                                                     ===============
    /**
     * To be for left column.
     * @return this. (NotNull)
     */
    HpCalculator left();

    /**
     * To be for right column. (default)<br />
     * It also means main process internally.
     * @return this. (NotNull)
     */
    HpCalculator right();
}
