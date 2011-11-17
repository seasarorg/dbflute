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
 *   new FromToOption().compareAsHour(); 
 *     --&gt; column &gt;= '2007/04/10 08:00:00'
 *     and column &lt; '2007/04/16 15:00:00'
 * 
 *   new FromToOption().compareAsDate(); 
 *     --&gt; column &gt;= '2007/04/10 00:00:00'
 *     and column &lt; '2007/04/17 00:00:00'
 * 
 *   new FromToOption().compareAsMonth(); 
 *     --&gt; column &gt;= '2007/04/01 00:00:00'
 *     and column &lt; '2007/05/01 00:00:00'
 * 
 *   new FromToOption().compareAsYear(); 
 *     --&gt; column &gt;= '2007/01/01 00:00:00'
 *     and column &lt; '2008/01/01 00:00:00'
 * 
 *   new FromToOption().compareAsWeek().asWeekBeginSunday(); 
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

    protected boolean _fromPatternHourBegin;
    protected boolean _fromPatternDayBegin;
    protected boolean _fromPatternMonthBegin;
    protected boolean _fromPatternYearBegin;
    protected boolean _fromPatternWeekBegin;
    protected boolean _fromDateWithNoon;
    protected Integer _fromDateWithHour;

    protected boolean _toPatternNextHourBegin;
    protected boolean _toPatternNextDayBegin;
    protected boolean _toPatternNextMonthBegin;
    protected boolean _toPatternNextYearBegin;
    protected boolean _toPatternNextWeekBegin;
    protected boolean _toDateWithNoon;
    protected Integer _toDateWithHour;

    protected Integer _weekBeginDay = Calendar.SUNDAY; // as default
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
        fromPatternHourBegin();
        toPatternNextHourBegin();
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
        fromPatternDayBegin();
        toPatternNextDayBegin();
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
        fromPatternMonthBegin();
        toPatternNextMonthBegin();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

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
        fromPatternYearBegin();
        toPatternNextYearBegin();
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
     *   new FromToOption().compareAsWeek().asWeekBeginSunday();
     *     --&gt; column &gt;= '2007/04/08 00:00:00'
     *     and column &lt; '2007/04/22 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsWeek() {
        fromPatternWeekBegin();
        toPatternNextWeekBegin();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    // -----------------------------------------------------
    //                                            Week Begin
    //                                            ----------
    public FromToOption asWeekBegin(Date date) {
        if (date == null) {
            String msg = "The argument 'date' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return doAsWeekBeginMonday(dayOfWeek);
    }

    public FromToOption asWeekBeginSunday() {
        return doAsWeekBeginMonday(Calendar.SUNDAY);
    }

    public FromToOption asWeekBeginMonday() {
        return doAsWeekBeginMonday(Calendar.MONDAY);
    }

    public FromToOption asWeekBeginTuesday() {
        return doAsWeekBeginMonday(Calendar.TUESDAY);
    }

    public FromToOption asWeekBeginWednesday() {
        return doAsWeekBeginMonday(Calendar.WEDNESDAY);
    }

    public FromToOption asWeekBeginThursday() {
        return doAsWeekBeginMonday(Calendar.THURSDAY);
    }

    public FromToOption asWeekBeginFriday() {
        return doAsWeekBeginMonday(Calendar.FRIDAY);
    }

    public FromToOption asWeekBeginSaturday() {
        return doAsWeekBeginMonday(Calendar.SATURDAY);
    }

    protected FromToOption doAsWeekBeginMonday(int dayOfWeek) {
        _weekBeginDay = dayOfWeek;
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
    public FromToOption fromPatternHourBegin() {
        assertNotAdjustmentAfterPattern("fromPatternHourBegin");
        clearFromPattern();
        _fromPatternHourBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternHourStart() {
        return fromPatternHourBegin();
    }

    public FromToOption fromPatternDayBegin() {
        assertNotAdjustmentAfterPattern("fromPatternDayBegin");
        clearFromPattern();
        _fromPatternDayBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternDayStart() {
        return fromPatternDayBegin();
    }

    public FromToOption fromPatternMonthBegin() {
        assertNotAdjustmentAfterPattern("fromPatternMonthBegin");
        clearFromPattern();
        _fromPatternMonthBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternMonthStart() {
        return fromPatternMonthBegin();
    }

    public FromToOption fromPatternYearBegin() {
        assertNotAdjustmentAfterPattern("fromPatternYearBegin");
        clearFromPattern();
        _fromPatternYearBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption fromPatternYearStart() {
        return fromPatternYearBegin();
    }

    public FromToOption fromPatternWeekBegin() {
        assertNotAdjustmentAfterPattern("fromPatternWeekBegin");
        clearFromPattern();
        _fromPatternWeekBegin = true;
        return this;
    }

    protected void clearFromPattern() {
        _fromPatternHourBegin = false;
        _fromPatternDayBegin = false;
        _fromPatternMonthBegin = false;
        _fromPatternYearBegin = false;
        _fromPatternWeekBegin = false;
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
    public FromToOption toPatternNextHourBegin() {
        assertNotAdjustmentAfterPattern("toPatternNextHourBegin");
        clearToPattern();
        _toPatternNextHourBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextHourStart() {
        return toPatternNextHourBegin();
    }

    public FromToOption toPatternNextDayBegin() {
        assertNotAdjustmentAfterPattern("toPatternNextDayBegin");
        clearToPattern();
        _toPatternNextDayBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextDayStart() {
        return toPatternNextDayBegin();
    }

    public FromToOption toPatternNextMonthBegin() {
        assertNotAdjustmentAfterPattern("toPatternNextMonthBegin");
        clearToPattern();
        _toPatternNextMonthBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextMonthStart() {
        return toPatternNextMonthBegin();
    }

    public FromToOption toPatternNextYearBegin() {
        assertNotAdjustmentAfterPattern("toPatternNextYearBegin");
        clearToPattern();
        _toPatternNextYearBegin = true;
        return this;
    }

    /**
     * @return this
     * @deprecated
     */
    public FromToOption toPatternNextYearStart() {
        return toPatternNextYearBegin();
    }

    public FromToOption toPatternNextWeekBegin() {
        assertNotAdjustmentAfterPattern("toPatternNextWeekBegin");
        clearToPattern();
        _toPatternNextWeekBegin = true;
        return this;
    }

    protected void clearToPattern() {
        _toPatternNextHourBegin = false;
        _toPatternNextDayBegin = false;
        _toPatternNextMonthBegin = false;
        _toPatternNextYearBegin = false;
        _toPatternNextWeekBegin = false;
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

    // -----------------------------------------------------
    //                                                Assert
    //                                                ------
    protected void assertNotAdjustmentAfterPattern(String option) {
        if (_usePattern) {
            String msg = "The option should not be call after pattern setting:";
            msg = msg + " option=" + option + "()";
            throw new IllegalStateException(msg);
        }
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

        if (_fromPatternHourBegin) {
            moveToCalendarHourBegin(cal);
        } else if (_fromPatternDayBegin) {
            moveToCalendarDayBegin(cal);
        } else if (_fromPatternMonthBegin) {
            moveToCalendarMonthBegin(cal);
        } else if (_fromPatternYearBegin) {
            moveToCalendarYearBegin(cal);
        } else if (_fromPatternWeekBegin) {
            moveToCalendarWeekBegin(cal);
        }
        if (_fromDateWithNoon) {
            moveToCalendarHourNoon(cal);
        }
        if (_fromDateWithHour != null) {
            moveToCalendarHour(cal, _fromDateWithHour);
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

        if (_toPatternNextHourBegin) {
            moveToCalendarNextHourBegin(cal);
        } else if (_toPatternNextDayBegin) {
            moveToCalendarNextDayBegin(cal);
        } else if (_toPatternNextMonthBegin) {
            moveToCalendarNextMonthBegin(cal);
        } else if (_toPatternNextYearBegin) {
            moveToCalendarNextYearBegin(cal);
        } else if (_toPatternNextWeekBegin) {
            moveToCalendarNextWeekBegin(cal);
        }
        if (_toDateWithNoon) {
            moveToCalendarHourNoon(cal);
        }
        if (_toDateWithHour != null) {
            moveToCalendarHour(cal, _toDateWithHour);
        }

        final Date cloneDate = (Date) toDate.clone();
        cloneDate.setTime(cal.getTimeInMillis());
        toDate = cloneDate;
        return toDate;
    }

    protected Date filterNoon(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        moveToCalendarHourNoon(cal);
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
    protected void moveToCalendarHour(Calendar cal, int hourOfDay) {
        DfTypeUtil.moveToCalendarHour(cal, hourOfDay);
    }

    protected void moveToCalendarHourBegin(Calendar cal) {
        DfTypeUtil.moveToCalendarHourBegin(cal);
    }

    protected void moveToCalendarHourNoon(Calendar cal) {
        DfTypeUtil.moveToCalendarHourNoon(cal);
    }

    protected void moveToCalendarDayBegin(Calendar cal) {
        DfTypeUtil.moveToCalendarDayBegin(cal);
    }

    protected void moveToCalendarMonthBegin(Calendar cal) {
        DfTypeUtil.moveToCalendarMonthBegin(cal);
    }

    protected void moveToCalendarYearBegin(Calendar cal) {
        DfTypeUtil.moveToCalendarYearBegin(cal);
    }

    protected void moveToCalendarWeekBegin(Calendar cal) {
        DfTypeUtil.moveToCalendarWeekBegin(cal, _weekBeginDay);
    }

    protected void moveToCalendarNextHourBegin(Calendar cal) {
        DfTypeUtil.addCalendarHourOfDay(cal, 1);
        moveToCalendarHourBegin(cal);
    }

    protected void moveToCalendarNextDayBegin(Calendar cal) {
        DfTypeUtil.addCalendarDayOfMonth(cal, 1);
        moveToCalendarDayBegin(cal);
    }

    protected void moveToCalendarNextMonthBegin(Calendar cal) {
        DfTypeUtil.addCalendarMonth(cal, 1);
        moveToCalendarMonthBegin(cal);
    }

    protected void moveToCalendarNextYearBegin(Calendar cal) {
        DfTypeUtil.addCalendarYear(cal, 1);
        moveToCalendarYearBegin(cal);
    }

    protected void moveToCalendarNextWeekBegin(Calendar cal) {
        DfTypeUtil.addCalendarWeekOfMonth(cal, 1);
        moveToCalendarWeekBegin(cal);
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
