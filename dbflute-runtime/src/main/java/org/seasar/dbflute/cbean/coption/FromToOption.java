/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The option of from-to for Date type.
 * <pre>
 * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
 * 
 * [Comparison Pattern]
 *   new FromToOption().compareAsYear();
 *     --&gt; column &gt;= '2007/01/01 00:00:00'
 *     and column &lt; '2008/01/01 00:00:00'
 * 
 *   new FromToOption().compareAsMonth();
 *     --&gt; column &gt;= '2007/04/01 00:00:00'
 *     and column &lt; '2007/05/01 00:00:00'
 * 
 *   new FromToOption().compareAsDate();
 *     --&gt; column &gt;= '2007/04/10 00:00:00'
 *     and column &lt; '2007/04/17 00:00:00'
 * 
 *   new FromToOption().compareAsHour();
 *     --&gt; column &gt;= '2007/04/10 08:00:00'
 *     and column &lt; '2007/04/16 15:00:00'
 * 
 *   new FromToOption().compareAsWeek().beginWeek_DayOfWeek1st_Sunday(); 
 *     --&gt; column &gt;= '2007/04/08 00:00:00'
 *     and column &lt; '2008/04/22 00:00:00'
 * 
 * [Manual Adjustment]
 *   new FromToOption().greaterThan(); 
 *     --&gt; column &gt; '2007/04/10 08:24:53'
 *     and column &lt;= '2007/04/16 14:36:29'
 * 
 *   new FromToOption().lessThan(); 
 *     --&gt; column &gt;= '2007/04/10 08:24:53'
 *     and column &lt; '2007/04/16 14:36:29'
 * 
 *   new FromToOption().greaterThan().lessThan(); 
 *     --&gt; column &gt; '2007/04/10 08:24:53'
 *     and column &lt; '2007/04/16 14:36:29'
 * 
 *   and so on...
 * 
 * [Default]
 *   new FromToOption(); 
 *     --&gt; column &gt;= '2007/04/10 08:24:53'
 *     and column &lt;= '2007/04/16 14:36:29'
 * </pre>
 * @author jflute
 */
public class FromToOption implements ConditionOption, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _greaterThan;
    protected boolean _lessThan;

    protected boolean _fromPatternHourJust;
    protected boolean _fromPatternDayJust;
    protected boolean _fromPatternMonthJust;
    protected boolean _fromPatternYearJust;
    protected boolean _fromPatternWeekJust;
    protected boolean _fromDateWithNoon;
    protected Integer _fromDateWithHour;

    protected boolean _toPatternNextHourJust;
    protected boolean _toPatternNextDayJust;
    protected boolean _toPatternNextMonthJust;
    protected boolean _toPatternNextYearJust;
    protected boolean _toPatternNextWeekJust;
    protected boolean _toDateWithNoon;
    protected Integer _toDateWithHour;

    protected Integer _yearBeginMonth = 1; // as default
    protected Integer _monthBeginDay = 1; // as default
    protected Integer _dayBeginHour = 0; // as default
    protected Integer _weekBeginDay = Calendar.SUNDAY; // as default
    protected Integer _moveToScope;
    protected boolean _usePattern;

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    public String getRearOption() {
        String msg = "Thie option does not use getRearOption().";
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    //                                                                  Comparison Pattern
    //                                                                  ==================
    /**
     * Compare as year. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2008/08/16 14:36:29}
     * 
     *   new FromToOption().compareAsYear();
     *     --&gt; column &gt;= '2007/01/01 00:00:00'
     *     and column &lt; '2009/01/01 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsYear() {
        fromPatternYearJust();
        toPatternNextYearJust();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    /**
     * Compare as month. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2008/08/16 14:36:29}
     * 
     *   new FromToOption().compareAsMonth();
     *     --&gt; column &gt;= '2007/04/01 00:00:00'
     *     and column &lt; '2008/09/01 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsMonth() {
        fromPatternMonthJust();
        toPatternNextMonthJust();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    /**
     * Compare as date. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     * 
     *   new FromToOption().compareAsDate();
     *     --&gt; column &gt;= '2007/04/10 00:00:00'
     *     and column &lt; '2007/04/17 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsDate() {
        fromPatternDayJust();
        toPatternNextDayJust();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    /**
     * Compare as hour. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     * 
     *   new FromToOption().compareAsHour();
     *     --&gt; column &gt;= '2007/04/10 08:00:00'
     *     and column &lt; '2007/04/16 15:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsHour() {
        fromPatternHourJust();
        toPatternNextHourJust();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    /**
     * Compare as week. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     * 
     *   new FromToOption().compareAsWeek().beginWeek_DayOfWeek1st_Sunday();
     *     --&gt; column &gt;= '2007/04/08 00:00:00'
     *     and column &lt; '2007/04/22 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsWeek() {
        fromPatternWeekJust();
        toPatternNextWeekJust();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    // -----------------------------------------------------
    //                                            Begin Year
    //                                            ----------
    public FromToOption beginYear_Month(int yearBeginMonth) {
        assertPatternOptionValid("beginYear_Month");
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = yearBeginMonth;
        return this;
    }

    public FromToOption beginYear_PreviousMonth(int yearBeginMonth) {
        assertPatternOptionValid("beginYear_PreviousMonth");
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = -yearBeginMonth; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                           Begin Month
    //                                           -----------
    public FromToOption beginMonth_Day(int monthBeginDay) {
        assertPatternOptionValid("beginMonth_Day");
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = monthBeginDay;
        return this;
    }

    public FromToOption beginMonth_PreviousDay(int monthBeginDay) {
        assertPatternOptionValid("beginMonth_PreviousDay");
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = -monthBeginDay; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                             Begin Day
    //                                             ---------
    public FromToOption beginDay_Hour(int dayBeginHour) {
        assertPatternOptionValid("beginDay_Hour");
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = dayBeginHour;
        return this;
    }

    public FromToOption beginDay_PreviousHour(int dayBeginHour) {
        assertPatternOptionValid("beginDay_PreviousHour");
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = -dayBeginHour; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                            Begin Week
    //                                            ----------
    public FromToOption beginWeek_DayOfWeek(Date weekBeginDayOfWeek) {
        assertPatternOptionValid("beginWeek_DayOfWeek");
        if (weekBeginDayOfWeek == null) {
            String msg = "The argument 'weekBeginDayOfWeek' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTime(weekBeginDayOfWeek);
        final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return doBeginWeek(dayOfWeek);
    }

    public FromToOption beginWeek_DayOfWeek1st_Sunday() {
        assertPatternOptionValid("beginWeek_DayOfWeek1st_Sunday");
        return doBeginWeek(Calendar.SUNDAY);
    }

    public FromToOption beginWeek_DayOfWeek2nd_Monday() {
        assertPatternOptionValid("beginWeek_DayOfWeek2nd_Monday");
        return doBeginWeek(Calendar.MONDAY);
    }

    public FromToOption beginWeek_DayOfWeek3rd_Tuesday() {
        assertPatternOptionValid("beginWeek_DayOfWeek3rd_Tuesday");
        return doBeginWeek(Calendar.TUESDAY);
    }

    public FromToOption beginWeek_DayOfWeek4th_Wednesday() {
        assertPatternOptionValid("beginWeek_DayOfWeek4th_Wednesday");
        return doBeginWeek(Calendar.WEDNESDAY);
    }

    public FromToOption beginWeek_DayOfWeek5th_Thursday() {
        assertPatternOptionValid("beginWeek_DayOfWeek5th_Thursday");
        return doBeginWeek(Calendar.THURSDAY);
    }

    public FromToOption beginWeek_DayOfWeek6th_Friday() {
        assertPatternOptionValid("beginWeek_DayOfWeek6th_Friday");
        return doBeginWeek(Calendar.FRIDAY);
    }

    public FromToOption beginWeek_DayOfWeek7th_Saturday() {
        assertPatternOptionValid("beginWeek_DayOfWeek7th_Saturday");
        return doBeginWeek(Calendar.SATURDAY);
    }

    protected FromToOption doBeginWeek(int weekBeginDayOfWeek) {
        _weekBeginDay = weekBeginDayOfWeek;
        return this;
    }

    // -----------------------------------------------------
    //                                         Move-to Scope
    //                                         -------------
    public FromToOption moveToScope(int moveToCount) {
        assertPatternOptionValid("moveToScope");
        _moveToScope = moveToCount;
        return this;
    }

    // ===================================================================================
    //                                                                   Manual Adjustment
    //                                                                   =================
    // -----------------------------------------------------
    //                                                   All
    //                                                   ---
    protected void clearAll() {
        clearOperand();
        clearFromPattern();
        clearToPattern();
        clearFromDateWith();
        clearToDateWith();
        _usePattern = false;
    }

    // -----------------------------------------------------
    //                                               Operand
    //                                               -------
    public FromToOption greaterThan() {
        assertNotAdjustmentAfterPattern("greaterThan");
        _greaterThan = true;
        return this;
    }

    public FromToOption lessThan() {
        assertNotAdjustmentAfterPattern("lessThan");
        _lessThan = true;
        return this;
    }

    protected void clearOperand() {
        _greaterThan = false;
        _lessThan = false;
    }

    // -----------------------------------------------------
    //                                             From Date
    //                                             ---------
    public FromToOption fromPatternHourJust() {
        assertNotAdjustmentAfterPattern("fromPatternHourJust");
        clearFromPattern();
        _fromPatternHourJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternHourStart() {
        return fromPatternHourJust();
    }

    public FromToOption fromPatternDayJust() {
        assertNotAdjustmentAfterPattern("fromPatternDayJust");
        clearFromPattern();
        _fromPatternDayJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternDayStart() {
        return fromPatternDayJust();
    }

    public FromToOption fromPatternMonthJust() {
        assertNotAdjustmentAfterPattern("fromPatternMonthJust");
        clearFromPattern();
        _fromPatternMonthJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternMonthStart() {
        return fromPatternMonthJust();
    }

    public FromToOption fromPatternYearJust() {
        assertNotAdjustmentAfterPattern("fromPatternYearJust");
        clearFromPattern();
        _fromPatternYearJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternYearStart() {
        return fromPatternYearJust();
    }

    public FromToOption fromPatternWeekJust() {
        assertNotAdjustmentAfterPattern("fromPatternWeekJust");
        clearFromPattern();
        _fromPatternWeekJust = true;
        return this;
    }

    protected void clearFromPattern() {
        _fromPatternHourJust = false;
        _fromPatternDayJust = false;
        _fromPatternMonthJust = false;
        _fromPatternYearJust = false;
        _fromPatternWeekJust = false;
    }

    public FromToOption fromDateWithNoon() {
        clearFromDateWith();
        _fromDateWithNoon = true;
        return this;
    }

    public FromToOption fromDateWithHour(int hourOfDay) {
        clearFromDateWith();
        _fromDateWithHour = hourOfDay;
        return this;
    }

    protected void clearFromDateWith() {
        _fromDateWithNoon = false;
        _fromDateWithHour = null;
    }

    // -----------------------------------------------------
    //                                               To Date
    //                                               -------
    public FromToOption toPatternNextHourJust() {
        assertNotAdjustmentAfterPattern("toPatternNextHourJust");
        clearToPattern();
        _toPatternNextHourJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextHourStart() {
        return toPatternNextHourJust();
    }

    public FromToOption toPatternNextDayJust() {
        assertNotAdjustmentAfterPattern("toPatternNextDayJust");
        clearToPattern();
        _toPatternNextDayJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextDayStart() {
        return toPatternNextDayJust();
    }

    public FromToOption toPatternNextMonthJust() {
        assertNotAdjustmentAfterPattern("toPatternNextMonthBegin");
        clearToPattern();
        _toPatternNextMonthJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextMonthStart() {
        return toPatternNextMonthJust();
    }

    public FromToOption toPatternNextYearJust() {
        assertNotAdjustmentAfterPattern("toPatternNextYearJust");
        clearToPattern();
        _toPatternNextYearJust = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextYearStart() {
        return toPatternNextYearJust();
    }

    public FromToOption toPatternNextWeekJust() {
        assertNotAdjustmentAfterPattern("toPatternNextWeekJust");
        clearToPattern();
        _toPatternNextWeekJust = true;
        return this;
    }

    protected void clearToPattern() {
        _toPatternNextHourJust = false;
        _toPatternNextDayJust = false;
        _toPatternNextMonthJust = false;
        _toPatternNextYearJust = false;
        _toPatternNextWeekJust = false;
    }

    public FromToOption toDateWithNoon() {
        clearToDateWith();
        _toDateWithNoon = true;
        return this;
    }

    public FromToOption toDateWithHour(int hourOfDay) {
        clearToDateWith();
        _toDateWithHour = hourOfDay;
        return this;
    }

    protected void clearToDateWith() {
        _toDateWithNoon = false;
        _toDateWithHour = null;
    }

    // ===================================================================================
    //                                                                       Internal Main
    //                                                                       =============
    /**
     * Filter the date as From. It requires this method is called before getFromDateConditionKey().
     * @param fromDate The date as From. (NullAllowed: If the value is null, it returns null)
     * @return The filtered date as From. (NullAllowed)
     */
    public Date filterFromDate(Date fromDate) {
        if (fromDate == null) {
            return null;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fromDate.getTime());

        if (_fromPatternYearJust) {
            moveToCalendarYearJust(cal);
            moveToScopeYear(cal);
        } else if (_fromPatternMonthJust) {
            moveToCalendarMonthJust(cal);
            moveToScopeMonth(cal);
        } else if (_fromPatternDayJust) {
            moveToCalendarDayJust(cal);
            moveToScopeDay(cal);
        } else if (_fromPatternHourJust) {
            moveToCalendarHourJust(cal);
            moveToScopeHour(cal);
        } else if (_fromPatternWeekJust) {
            moveToCalendarWeekJust(cal);
            moveToScopeWeek(cal);
        }
        if (_fromDateWithNoon) {
            moveToCalendarHourJustNoon(cal);
        }
        if (_fromDateWithHour != null) {
            moveToCalendarHourJustFor(cal, _fromDateWithHour);
        }

        final Date cloneDate = (Date) fromDate.clone();
        cloneDate.setTime(cal.getTimeInMillis());
        fromDate = cloneDate;
        return fromDate;
    }

    /**
     * Filter the date as To. It requires this method is called before getToDateConditionKey().
     * @param toDate The date as To. (NullAllowed: If the value is null, it returns null)
     * @return The filtered date as To. (NullAllowed)
     */
    public Date filterToDate(Date toDate) {
        if (toDate == null) {
            return null;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(toDate.getTime());

        if (_toPatternNextYearJust) {
            moveToCalendarNextYearJust(cal);
            moveToScopeYear(cal);
        } else if (_toPatternNextMonthJust) {
            moveToCalendarNextMonthJust(cal);
            moveToScopeMonth(cal);
        } else if (_toPatternNextDayJust) {
            moveToCalendarNextDayJust(cal);
            moveToScopeDay(cal);
        } else if (_toPatternNextHourJust) {
            moveToCalendarNextHourJust(cal);
            moveToScopeHour(cal);
        } else if (_toPatternNextWeekJust) {
            moveToCalendarNextWeekJust(cal);
            moveToScopeWeek(cal);
        }
        if (_toDateWithNoon) {
            moveToCalendarHourJustNoon(cal);
        }
        if (_toDateWithHour != null) {
            moveToCalendarHourJustFor(cal, _toDateWithHour);
        }

        final Date cloneDate = (Date) toDate.clone();
        cloneDate.setTime(cal.getTimeInMillis());
        toDate = cloneDate;
        return toDate;
    }

    protected Date filterNoon(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        moveToCalendarHourJustNoon(cal);
        final Date cloneDate = (Date) date.clone();
        cloneDate.setTime(cal.getTimeInMillis());
        return cloneDate;
    }

    /**
     * Get the condition-key of the from-date. It requires this method is called after filterFromDate().
     * @return The condition-key of the from-date. (NotNull)
     */
    public ConditionKey getFromDateConditionKey() {
        if (_greaterThan) {
            return ConditionKey.CK_GREATER_THAN;
        } else {
            return ConditionKey.CK_GREATER_EQUAL; // as default
        }
    }

    /**
     * Get the condition-key of the to-date. It requires this method is called after filterToDate().
     * @return The condition-key of the to-date. (NotNull)
     */
    public ConditionKey getToDateConditionKey() {
        if (_lessThan) {
            return ConditionKey.CK_LESS_THAN;
        } else {
            return ConditionKey.CK_LESS_EQUAL; // as default
        }
    }

    // ===================================================================================
    //                                                                     Calendar Helper
    //                                                                     ===============
    protected void moveToCalendarYearJust(Calendar cal) {
        DfTypeUtil.moveToCalendarYearJust(cal, _yearBeginMonth);
    }

    protected void moveToCalendarMonthJust(Calendar cal) {
        DfTypeUtil.moveToCalendarMonthJust(cal, _monthBeginDay);
    }

    protected void moveToCalendarDayJust(Calendar cal) {
        DfTypeUtil.moveToCalendarDayJust(cal, _dayBeginHour);
    }

    protected void moveToCalendarHourJust(Calendar cal) {
        DfTypeUtil.moveToCalendarHourJust(cal);
    }

    protected void moveToCalendarHourJustFor(Calendar cal, int hourOfDay) {
        DfTypeUtil.moveToCalendarHourJustFor(cal, hourOfDay);
    }

    protected void moveToCalendarHourJustNoon(Calendar cal) {
        DfTypeUtil.moveToCalendarHourJustNoon(cal);
    }

    protected void moveToCalendarWeekJust(Calendar cal) {
        DfTypeUtil.moveToCalendarWeekJust(cal, _weekBeginDay);
    }

    protected void moveToCalendarNextYearJust(Calendar cal) {
        DfTypeUtil.moveToCalendarYearTerminal(cal, _yearBeginMonth);
        DfTypeUtil.addCalendarMillisecond(cal, 1);
    }

    protected void moveToCalendarNextMonthJust(Calendar cal) {
        DfTypeUtil.moveToCalendarMonthTerminal(cal, _monthBeginDay);
        DfTypeUtil.addCalendarMillisecond(cal, 1);
    }

    protected void moveToCalendarNextDayJust(Calendar cal) {
        DfTypeUtil.moveToCalendarDayTerminal(cal, _dayBeginHour);
        DfTypeUtil.addCalendarMillisecond(cal, 1);
    }

    protected void moveToCalendarNextHourJust(Calendar cal) {
        DfTypeUtil.moveToCalendarHourTerminal(cal);
        DfTypeUtil.addCalendarMillisecond(cal, 1);
    }

    protected void moveToCalendarNextWeekJust(Calendar cal) {
        DfTypeUtil.moveToCalendarWeekTerminal(cal, _weekBeginDay);
        DfTypeUtil.addCalendarMillisecond(cal, 1);
    }

    protected void moveToScopeYear(Calendar cal) {
        if (_moveToScope != null) {
            DfTypeUtil.addCalendarYear(cal, _moveToScope);
        }
    }

    protected void moveToScopeMonth(Calendar cal) {
        if (_moveToScope != null) {
            DfTypeUtil.addCalendarMonth(cal, _moveToScope);
        }
    }

    protected void moveToScopeDay(Calendar cal) {
        if (_moveToScope != null) {
            DfTypeUtil.addCalendarDay(cal, _moveToScope);
        }
    }

    protected void moveToScopeHour(Calendar cal) {
        if (_moveToScope != null) {
            DfTypeUtil.addCalendarHour(cal, _moveToScope);
        }
    }

    protected void moveToScopeWeek(Calendar cal) {
        if (_moveToScope != null) {
            DfTypeUtil.addCalendarWeek(cal, _moveToScope);
        }
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertNotMinusNotOver(String name, int value, int max) {
        if (value < 0) {
            String msg = "The argument '" + name + "' should not be minus: value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value > max) {
            String msg = "The argument '" + name + "' should not be over: value=" + value + " max=" + max;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertPatternOptionValid(String option) {
        if (!_usePattern) {
            String msg = "The option '" + option + "()' should be called after pattern setting.";
            throw new IllegalStateException(msg);
        }
    }

    protected void assertNotAdjustmentAfterPattern(String option) {
        if (_usePattern) {
            String msg = "The option '" + option + "()' should not be call after pattern setting.";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        return title + ":{greaterThan=" + _greaterThan + ", lessThan=" + _lessThan + ", usePattern=" + _usePattern
                + "}";
    }
}
