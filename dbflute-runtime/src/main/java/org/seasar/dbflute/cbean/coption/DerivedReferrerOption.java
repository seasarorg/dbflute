/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.cbean.coption;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.chelper.HpCalcSpecification;
import org.seasar.dbflute.cbean.chelper.HpSpecifiedColumn;

/**
 * The option for DerivedReferrer. <br />
 * You can filter an aggregate function by scalar function filters.
 * @author jflute
 */
public class DerivedReferrerOption extends FunctionFilterOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Does it suppress the correlation condition on where clause? */
    protected boolean _suppressCorrelation;

    protected HpCalcSpecification<ConditionBean> _calcSpecification;

    // ===================================================================================
    //                                                                     Function Option
    //                                                                     ===============
    /**
     * Set the value for coalesce function. <br />
     * If you set string value and the derived column is date type, it converts it to a date object internally.
     * For example, "2010-10-30 12:34:56.789", "2010/10/30" and so on ... are acceptable.
     * @param coalesce An alternate value when group function returns null. (NullAllowed: if null, no coalesce)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption coalesce(Object coalesce) {
        doCoalesce(coalesce);
        return this;
    }

    /**
     * Round the specified part of the number.
     * @param round Decimal digits or date format for round. (NullAllowed: if null, no round)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption round(Object round) {
        doRound(round);
        return this;
    }

    /**
     * Truncate the specified part of the number or date-time value.
     * @param trunc Decimal digits or date option for trunc. (NullAllowed: if null, no trunc)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption trunc(Object trunc) {
        doTrunc(trunc);
        return this;
    }

    // ===================================================================================
    //                                                                      Purpose Option
    //                                                                      ==============
    /**
     * Truncate the month and day and time part of the date or the date-time value.
     * <pre>
     *  e.g. 2012/12/31 01:50:46 -&gt; 2012/<span style="color: #FD4747">01/01 00:00:00</span>
     * </pre>
     * @return this. (NotNull)
     */
    public DerivedReferrerOption truncMonth() {
        doTruncMonth();
        return this;
    }

    /**
     * Truncate the day and time part of the date or the date-time value.
     * <pre>
     *  e.g. 2012/12/31 01:50:46 -&gt; 2012/12/<span style="color: #FD4747">01 00:00:00</span>
     * </pre>
     * @return this. (NotNull)
     */
    public DerivedReferrerOption truncDay() {
        doTruncDay();
        return this;
    }

    /**
     * Truncate the time part of the date-time value.
     * <pre>
     *  e.g. 2012/12/31 01:50:46 -&gt; 2012/12/31 <span style="color: #FD4747">00:00:00</span>
     * </pre>
     * @return this. (NotNull)
     */
    public DerivedReferrerOption truncTime() {
        doTruncTime();
        return this;
    }

    /**
     * Add years to the date or date-time value.
     * @param addedYear The count of added years. (NullAllowed: if null, no dateAdd)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption addYear(Integer addedYear) {
        doAddYear(addedYear);
        return this;
    }

    /**
     * Add months to the date or date-time value.
     * @param addedMonth The count of added months. (NullAllowed: if null, no dateAdd)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption addMonth(Integer addedMonth) {
        doAddMonth(addedMonth);
        return this;
    }

    /**
     * Add days to the date or date-time value.
     * @param addedDay The count of added days. (NullAllowed: if null, no dateAdd)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption addDay(Integer addedDay) {
        doAddDay(addedDay);
        return this;
    }

    /**
     * Add hours to date-time value.
     * @param addedHour The count of added hours. (NullAllowed: if null, no dateAdd)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption addHour(Integer addedHour) {
        doAddHour(addedHour);
        return this;
    }

    /**
     * Add minutes to date-time value.
     * @param addedMinute The count of added minutes. (NullAllowed: if null, no dateAdd)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption addMinute(Integer addedMinute) {
        doAddMinute(addedMinute);
        return this;
    }

    /**
     * Add seconds to date-time value.
     * @param addedSecond The count of added seconds. (NullAllowed: if null, no dateAdd)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption addSecond(Integer addedSecond) {
        doAddSecond(addedSecond);
        return this;
    }

    // ===================================================================================
    //                                                                  Calculation Option
    //                                                                  ==================
    /**
     * Plus the specified column with the value. (+)
     * @param plusValue The number value for plus. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption plus(Number plusValue) {
        assertObjectNotNull("plusValue", plusValue);
        getCalcSpecification().plus(plusValue);
        return this;
    }

    /**
     * Plus the specified column with the plus column. (+) {Dream Cruise}
     * @param plusColumn The plus column specified by your Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption plus(HpSpecifiedColumn plusColumn) {
        assertObjectNotNull("plusColumn", plusColumn);
        getCalcSpecification().plus(plusColumn);
        return this;
    }

    /**
     * Minus the specified column with the value. (-)
     * @param minusValue The number value for minus. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption minus(Number minusValue) {
        assertObjectNotNull("minusValue", minusValue);
        getCalcSpecification().minus(minusValue);
        return this;
    }

    /**
     * Minus the specified column with the minus column. (-) {Dream Cruise}
     * @param minusColumn The minus column specified by your Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption minus(HpSpecifiedColumn minusColumn) {
        assertObjectNotNull("minusColumn", minusColumn);
        getCalcSpecification().minus(minusColumn);
        return this;
    }

    /**
     * Multiply the value to the specified column. (*)
     * @param multiplyValue The number value for multiply. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption multiply(Number multiplyValue) {
        assertObjectNotNull("multiplyValue", multiplyValue);
        getCalcSpecification().multiply(multiplyValue);
        return this;
    }

    /**
     * Multiply the specified column with the multiply column. (*) {Dream Cruise}
     * @param multiplyColumn The multiply column specified by your Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption multiply(HpSpecifiedColumn multiplyColumn) {
        assertObjectNotNull("multiplyColumn", multiplyColumn);
        getCalcSpecification().multiply(multiplyColumn);
        return this;
    }

    /**
     * Divide the specified column by the value. (/)
     * @param divideValue The number value for divide. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption divide(Number divideValue) {
        assertObjectNotNull("divideValue", divideValue);
        getCalcSpecification().divide(divideValue);
        return this;
    }

    /**
     * Divide the specified column with the divide column. (/) {Dream Cruise}
     * @param divideColumn The divide column specified by your Dream Cruise. (NotNull)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption divide(HpSpecifiedColumn divideColumn) {
        assertObjectNotNull("divideColumn", divideColumn);
        getCalcSpecification().divide(divideColumn);
        return this;
    }

    protected HpCalcSpecification<ConditionBean> getCalcSpecification() {
        if (_calcSpecification == null) {
            _calcSpecification = new HpCalcSpecification<ConditionBean>(new SpecifyQuery<ConditionBean>() {
                public void specify(ConditionBean cb) { // as dummy
                }
            });
        }
        return _calcSpecification;
    }

    public void xacceptBaseCB(ConditionBean cb) { // called after registered internally
        if (_calcSpecification != null) {
            _calcSpecification.specify(cb); // to (e.g.) find cipher manager
        }
    }

    @Override
    protected String processCalculation(String functionExp) {
        if (_calcSpecification != null) {
            return _calcSpecification.buildStatementToSpecifidName(functionExp);
        }
        return super.processCalculation(functionExp);
    }

    // ===================================================================================
    //                                                                   Adjustment Option
    //                                                                   =================
    public boolean isSuppressCorrelation() {
        return _suppressCorrelation;
    }

    /**
     * Suppress the correlation condition on where clause (to be plain sub-query).
     * @return this. (NotNull)
     */
    public DerivedReferrerOption suppressCorrelation() {
        _suppressCorrelation = true;
        return this;
    }
}
