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
package org.seasar.dbflute.helper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.DfTypeUtil.ParseDateException;

/**
 * The date which provides you handy manipulations for Date.
 * <pre>
 * e.g.
 * HandyDate date = new HandyDate("2011/11/27 12:34:56.789");
 * date.addDay(1); // 2011/11/<span style="color: #FD4747">28</span> 12:34:56.789
 * date.addMonth(1); // 2011/<span style="color: #FD4747">12</span>/28 12:34:56.789
 * date.moveToDayJust(); // 2011/12/28 <span style="color: #FD4747">00:00:00.000</span>
 * date.moveToMonthTerminal(); // 2011/12/<span style="color: #FD4747">31 23:59:59.999</span>
 * if (date.isGreaterThan(toDate("2011/12/30"))) { // true
 *     // 2011/12/31 23:59:59.999
 *     java.util.Date movedDate = date.getDate();
 *     java.sql.Timestamp movedTimestamp = date.getTimestampDate();
 * }
 * </pre>
 * @author jflute
 * @since 0.9.9.2A (2011/11/17 Thursday)
 */
public class HandyDate implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = -5181512291555841795L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Calendar _cal = Calendar.getInstance();
    protected int _yearBeginMonth = _cal.getActualMinimum(Calendar.MONTH) + 1; // as default (zero origin headache)
    protected int _monthBeginDay = _cal.getActualMinimum(Calendar.DAY_OF_MONTH); // as default
    protected int _dayBeginHour = _cal.getActualMinimum(Calendar.HOUR_OF_DAY); // as default
    protected int _weekBeginDay = Calendar.SUNDAY; // as default

    // *you should also fix clone() when you add attributes 

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Construct the handy date by the specified date. <br />
     * The specified date is not changed by this handy date.
     * @param date The instance of the date. (NotNull)
     */
    public HandyDate(Date date) {
        assertConstructorArgumentNotNull("date", date);
        _cal.setTime(date);
    }

    /**
     * Construct the handy date by the string expression.
     * <pre>
     * e.g.
     * o new HandyDate("2001/01/01"): 2001-01-01 00:00:00.000
     * o new HandyDate("2001-01-01"): 2001-01-01 00:00:00.000
     * o new HandyDate("2001/01/01 12:34:56"): 2001-01-01 12:34:56.000
     * o new HandyDate("2001/01/01 12:34:56.798"): 2001-01-01 12:34:56.789
     * o new HandyDate("date 20010101"): 2001-01-01
     * </pre>
     * @param exp The string expression of the date. (NotNull)
     * @throws ParseDateExpressionFailureException When it fails to parse the expression.
     */
    public HandyDate(String exp) {
        assertConstructorArgumentNotNull("exp", exp);
        try {
            _cal.setTime(DfTypeUtil.toDate(exp));
        } catch (ParseDateException e) {
            throwParseDateExpressionFailureException(exp, e);
        }
    }

    /**
     * Construct the handy date by the string expression. <br />
     * e.g. new HandyDate("20010101", "yyyyMMdd"): 2001-01-01 00:00:00.000
     * @param exp The string expression of the date. (NotNull)
     * @param pattern The pattern to parse as date. (NotNull)
     * @throws ParseDateExpressionFailureException When it fails to parse the expression.
     */
    public HandyDate(String exp, String pattern) {
        assertConstructorArgumentNotNull("exp", exp);
        assertConstructorArgumentNotNull("pattern", pattern);
        try {
            _cal.setTime(DfTypeUtil.toDate(exp, pattern));
        } catch (ParseDateException e) {
            throwParseDateExpressionFailureException(exp, e);
        }
    }

    protected void assertConstructorArgumentNotNull(String name, Object value) {
        if (value == null) {
            String msg = "The argument '" + name + "' should not be null.";
            throw new IllegalArgumentException(msg);
        }
    }

    protected void throwParseDateExpressionFailureException(String exp, ParseDateException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to parse the expression as date.");
        br.addItem("Expression");
        br.addElement(exp);
        final String msg = br.buildExceptionMessage();
        throw new ParseDateExpressionFailureException(msg, e);
    }

    public static class ParseDateExpressionFailureException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public ParseDateExpressionFailureException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    // ===================================================================================
    //                                                                            Add Date
    //                                                                            ========
    /**
     * Add years. e.g. addYear(1): 2001/01/01 to <span style="color: #FD4747">2002</span>/01/01
     * @param year The added count of year. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addYear(int year) {
        DfTypeUtil.addCalendarYear(_cal, year);
        return this;
    }

    /**
     * Add months. e.g. addMonth(1): 2001/01/01 to 2001/<span style="color: #FD4747">02</span>/01
     * @param month The added count of month. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addMonth(int month) {
        DfTypeUtil.addCalendarMonth(_cal, month);
        return this;
    }

    /**
     * Add days. e.g. addDay(1): 2001/01/01 to 2001/01/<span style="color: #FD4747">02</span>
     * @param day The added count of day. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addDay(int day) {
        DfTypeUtil.addCalendarDay(_cal, day);
        return this;
    }

    /**
     * Add hours. e.g. addHour(1): 2001/01/01 00:00:00 to 2001/01/02 <span style="color: #FD4747">01</span>:00:00
     * @param hour The added count of hour. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addHour(int hour) {
        DfTypeUtil.addCalendarHour(_cal, hour);
        return this;
    }

    /**
     * Add minutes. e.g. addMinute(1): 2001/01/01 00:00:00 to 2001/01/02 00:<span style="color: #FD4747">01</span>:00
     * @param minute The added count of minute. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addMinute(int minute) {
        DfTypeUtil.addCalendarMinute(_cal, minute);
        return this;
    }

    /**
     * Add seconds. e.g. addSecond(1): 2001/01/01 00:00:00 to 2001/01/02 00:00:<span style="color: #FD4747">01</span>
     * @param second The added count of second. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addSecond(int second) {
        DfTypeUtil.addCalendarSecond(_cal, second);
        return this;
    }

    /**
     * Add milliseconds. e.g. addMillisecond(1): 2001/01/01 00:00:00.000 to 2001/01/02 00:00:00.<span style="color: #FD4747">001</span>
     * @param millisecond The added count of millisecond. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addMillisecond(int millisecond) {
        DfTypeUtil.addCalendarMillisecond(_cal, millisecond);
        return this;
    }

    /**
     * Add weeks. e.g. addWeek(1): 2001/01/01 to 2001/01/<span style="color: #FD4747">08</span>
     * @param week The added count of week. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate addWeek(int week) {
        DfTypeUtil.addCalendarWeek(_cal, week);
        return this;
    }

    // ===================================================================================
    //                                                                        Move-to Date
    //                                                                        ============
    // -----------------------------------------------------
    //                                          Move-to Year
    //                                          ------------
    /**
     * Move to the specified year.
     * <pre>
     * e.g.
     *  moveToYear(2007): 2001/01/01 to <span style="color: #FD4747">2007</span>/01/01
     *  moveToYear(-2007): 2001/01/01 to <span style="color: #FD4747">BC2007</span>/01/01
     * </pre>
     * @param year The move-to year. (NotZero, MinusAllowed: if minus, means before Christ)
     * @return this. (NotNull)
     */
    public HandyDate moveToYear(int year) {
        DfTypeUtil.moveToCalendarYear(_cal, year);
        return this;
    }

    /**
     * Move to the year just (beginning). <br />
     * e.g. moveToYearJust(): 2011/11/27 12:34:56.789 to 2011/<span style="color: #FD4747">01/01 00:00:00.000</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToYearJust() {
        DfTypeUtil.moveToCalendarYearJust(_cal, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    /**
     * Move to the year just (beginning) after the year added. <br />
     * e.g. moveToYearJustAdded(1): 2011/11/27 12:34:56.789 to 2012/01/01 00:00:00.000
     * @param year The count added of year. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToYearJustAdded(int year) {
        DfTypeUtil.moveToCalendarYearJustAdded(_cal, year);
        return this;
    }

    /**
     * Move to the year just (beginning) after the year moved-to. <br />
     * e.g. moveToYearJustFor(2007): 2011/11/27 12:34:56.789 to 2007/01/01 00:00:00.000
     * @param year The move-to year. (NotZero, MinusAllowed: if minus, means before Christ)
     * @return this. (NotNull)
     */
    public HandyDate moveToYearJustFor(int year) {
        DfTypeUtil.moveToCalendarYearJustFor(_cal, year);
        return this;
    }

    /**
     * Move to the terminal of the year. <br />
     * e.g. moveToYearTerminal(): 2011/11/27 12:34:56.789 to 2011/<span style="color: #FD4747">12/31 23:59:59.999</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToYearTerminal() {
        DfTypeUtil.moveToCalendarYearTerminal(_cal, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    /**
     * Move to the terminal of the year after the year added. <br />
     * e.g. moveToYearTerminalAdded(1): 2011/11/27 12:34:56.789 to 2012/12/31 23:59:59.999
     * @param year The count added of year. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToYearTerminalAdded(int year) {
        DfTypeUtil.moveToCalendarYearTerminalAdded(_cal, year);
        return this;
    }

    /**
     * Move to the terminal of the year after the year moved-to. <br />
     * e.g. moveToYearTerminalFor(2007): 2011/11/27 12:34:56.789 to 2007/12/31 23:59:59.999
     * @param year The move-to year. (NotZero, MinusAllowed: if minus, means before Christ)
     * @return this. (NotNull)
     */
    public HandyDate moveToYearTerminalFor(int year) {
        DfTypeUtil.moveToCalendarYearTerminalFor(_cal, year);
        return this;
    }

    // -----------------------------------------------------
    //                                         Move-to Month
    //                                         -------------
    /**
     * Move to the specified month. <br />
     * e.g. moveToMonth(9): 2011/11/27 to 2011/<span style="color: #FD4747">09</span>/27
     * @param month The move-to month. (NotZero, NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToMonth(int month) {
        assertValidMonth(month);
        DfTypeUtil.moveToCalendarMonth(_cal, month);
        return this;
    }

    /**
     * Move to the month just (beginning). <br />
     * e.g. moveToMonthJust(): 2011/11/27 12:34:56.789 to 2011/11/<span style="color: #FD4747">01 00:00:00.000</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthJust() {
        DfTypeUtil.moveToCalendarMonthJust(_cal, _monthBeginDay);
        moveToDayJust(); // just for others
        return this;
    }

    /**
     * Move to the month just (beginning) after the month added. <br />
     * e.g. moveToMonthJustAdded(1): 2011/11/27 12:34:56.789 to 2011/12/01 00:00:00.000
     * @param month The count added of month. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthJustAdded(int month) {
        DfTypeUtil.moveToCalendarMonthJustAdded(_cal, month);
        return this;
    }

    /**
     * Move to the month just (beginning) after the month moved-to. <br />
     * e.g. moveToMonthJustFor(9): 2011/11/27 12:34:56.789 to 2011/09/01 00:00:00.000
     * @param month The move-to month. (NotZero, NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthJustFor(int month) {
        assertValidMonth(month);
        DfTypeUtil.moveToCalendarMonthJustFor(_cal, month);
        return this;
    }

    /**
     * Move to the terminal of the month. <br />
     * e.g. moveToMonthTerminal(): 2011/11/27 12:34:56.789 to 2011/11/<span style="color: #FD4747">30 23:59:59.999</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthTerminal() {
        DfTypeUtil.moveToCalendarMonthTerminal(_cal, _monthBeginDay);
        moveToDayTerminal(); // just for others
        return this;
    }

    /**
     * Move to the terminal of the month after the month added. <br />
     * e.g. moveToMonthTerminalAdded(1): 2011/11/27 12:34:56.789 to 2011/12/31 23:59:59.999
     * @param month The count added of month. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthTerminalAdded(int month) {
        DfTypeUtil.moveToCalendarMonthTerminalAdded(_cal, month);
        return this;
    }

    /**
     * Move to the terminal of the month after the month moved-to. <br />
     * e.g. moveToMonthTerminalFor(9): 2011/11/27 12:34:56.789 to 2011/09/30 23:59:59.999
     * @param month The move-to month. (NotZero, NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthTerminalFor(int month) {
        assertValidMonth(month);
        DfTypeUtil.moveToCalendarMonthTerminalFor(_cal, month);
        return this;
    }

    /**
     * Move to the first weekday just of the month. <br />
     * e.g. moveToMonthFirstWeekdayJust(): 2013/06/10 12:34:56.789 to 2013/06/<span style="color: #FD4747">03 00:00:00:000</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthFirstWeekdayJust() {
        moveToMonthJust();
        if (isWeek_DayOfWeek1st_Sunday()) {
            addDay(1);
        } else if (isWeek_DayOfWeek7th_Saturday()) {
            addDay(2);
        }
        return this;
    }

    /**
     * Move to the terminal of the month last weekday. <br />
     * e.g. moveToMonthWeekdayTerminal(): 2013/03/10 12:34:56.789 to 2013/03/<span style="color: #FD4747">29 23:59:59.999</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToMonthLastWeekdayTerminal() {
        moveToMonthTerminal();
        if (isWeek_DayOfWeek1st_Sunday()) {
            addDay(-2);
        } else if (isWeek_DayOfWeek7th_Saturday()) {
            addDay(-1);
        }
        return this;
    }

    // -----------------------------------------------------
    //                                           Move-to Day
    //                                           -----------
    /**
     * Move to the specified day. <br />
     * e.g. moveToDay(23): 2001/01/16 to 2007/01/<span style="color: #FD4747">23</span>
     * @param day The move-to day. (NotZero, NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToDay(int day) {
        assertValidDay(day);
        DfTypeUtil.moveToCalendarDay(_cal, day);
        return this;
    }

    /**
     * Move to the day just (beginning). <br />
     * e.g. moveToDayJust(): 2011/11/27 12:34:56.789 to 2011/11/27 <span style="color: #FD4747">00:00:00.000</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToDayJust() {
        DfTypeUtil.moveToCalendarDayJust(_cal, _dayBeginHour);
        return this;
    }

    /**
     * Move to the day just (beginning) after the day added. <br />
     * e.g. moveToDayJustAdded(1): 2011/11/27 12:34:56.789 to 2011/11/28 00:00:00.000
     * @param day The count added of day. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToDayJustAdded(int day) {
        DfTypeUtil.moveToCalendarDayJustAdded(_cal, day);
        return this;
    }

    /**
     * Move to the day just after the day moved-to. <br />
     * e.g. moveToDayJustFor(14): 2011/11/27 12:34:56.789 to 2011/11/14 00:00:00.000
     * @param day The move-to day. (NotZero, NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToDayJustFor(int day) {
        assertValidDay(day);
        DfTypeUtil.moveToCalendarDayJustFor(_cal, day);
        return this;
    }

    /**
     * Move to the terminal of the day. <br />
     * e.g. moveToDayTerminal(): 2011/11/27 12:34:56.789 to 2011/11/27 <span style="color: #FD4747">23:59:59.999</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToDayTerminal() {
        DfTypeUtil.moveToCalendarDayTerminal(_cal, _dayBeginHour);
        return this;
    }

    /**
     * Move to the terminal of the day after the day added. <br />
     * e.g. moveToDayJustAdded(1): 2011/11/27 12:34:56.789 to 2011/11/28 23:59:59.999
     * @param day The count added of day. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToDayTerminalAdded(int day) {
        DfTypeUtil.moveToCalendarDayTerminalAdded(_cal, day);
        return this;
    }

    /**
     * Move to the day just after the day moved-to. <br />
     * e.g. moveToDayTerminalFor(14): 2011/11/27 12:34:56.789 to 2011/11/14 23:59:59.999
     * @param day The move-to day. (NotZero, NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToDayTerminalFor(int day) {
        assertValidDay(day);
        DfTypeUtil.moveToCalendarDayTerminalFor(_cal, day);
        return this;
    }

    // -----------------------------------------------------
    //                                          Move-to Hour
    //                                          ------------
    /**
     * Move to the specified hour.
     * <pre>
     * e.g. 2011/11/27 17:00:00
     *  moveToHour(23): 2007/11/27 <span style="color: #FD4747">23</span>:00:00
     *  moveToHour(26): 2007/11/<span style="color: #FD4747">28 02</span>:00:00
     * </pre>
     * @param hour The move-to hour. (MinusAllowed)
     * @return this. (NotNull)
     */
    public HandyDate moveToHour(int hour) {
        assertValidHour(hour);
        DfTypeUtil.moveToCalendarHour(_cal, hour);
        return this;
    }

    /**
     * Move to the hour just (beginning). <br />
     * e.g. moveToHourJust(): 2011/11/27 12:34:56.789 to 2011/11/27 12:<span style="color: #FD4747">00:00.000</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToHourJust() {
        DfTypeUtil.moveToCalendarHourJust(_cal);
        return this;
    }

    /**
     * Move to the hour just (beginning) after the hour added. <br />
     * e.g. moveToHourJustAdded(1): 2011/11/27 12:34:56.789 to 2011/11/27 13:00:00.000
     * @param hour The count added of hour. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToHourJustAdded(int hour) {
        DfTypeUtil.moveToCalendarHourJustAdded(_cal, hour);
        return this;
    }

    /**
     * Move to the hour just (beginning) after the hour moved-to. <br />
     * e.g. moveToHourJustFor(4): 2011/11/27 12:34:56.789 to 2011/11/27 04:00:00.000
     * @param hour The move-to hour. (NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToHourJustFor(int hour) {
        assertValidHour(hour);
        DfTypeUtil.moveToCalendarHourJustFor(_cal, hour);
        return this;
    }

    /**
     * Move to the terminal of the hour. <br />
     * e.g. moveToHourTerminal(): 2011/11/27 12:34:56.789 to 2011/11/27 12:<span style="color: #FD4747">59:59.999</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToHourTerminal() {
        DfTypeUtil.moveToCalendarHourTerminal(_cal);
        return this;
    }

    /**
     * Move to the terminal of the hour after the hour added. <br />
     * e.g. moveToHourTerminalAdded(1): 2011/11/27 12:34:56.789 to 2011/11/27 13:59:59.999
     * @param hour The count added of hour. (MinusAllowed: if minus, move back)
     * @return this. (NotNull)
     */
    public HandyDate moveToHourTerminalAdded(int hour) {
        DfTypeUtil.moveToCalendarHourTerminalAdded(_cal, hour);
        return this;
    }

    /**
     * Move to the terminal of the hour after the hour moved-to. <br />
     * e.g. moveToHourTerminalFor(4): 2011/11/27 12:34:56.789 to 2011/11/27 04:59:59.999
     * @param hour The move-to hour. (NotMinus)
     * @return this. (NotNull)
     */
    public HandyDate moveToHourTerminalFor(int hour) {
        assertValidHour(hour);
        DfTypeUtil.moveToCalendarHourTerminalFor(_cal, hour);
        return this;
    }

    /**
     * Move to the hour just noon. <br />
     * e.g. moveToHourJustNoon(): 2011/11/27 22:34:56.789 to 2011/11/27 12:00:00.000
     * @return this. (NotNull)
     */
    public HandyDate moveToHourJustNoon() {
        DfTypeUtil.moveToCalendarHourJustNoon(_cal);
        return this;
    }

    // -----------------------------------------------------
    //                                        Move-to Minute
    //                                        --------------
    /**
     * Move to the specified minute.
     * <pre>
     * e.g. 2011/11/27 00:32:00
     *  moveToMinute(12): to 2007/11/27 00:<span style="color: #FD4747">12</span>:00
     *  moveToMinute(48): to 2007/11/27 00:<span style="color: #FD4747">48</span>:00
     * </pre>
     * @param minute The move-to minute. (MinusAllowed)
     * @return this. (NotNull)
     */
    public HandyDate moveToMinute(int minute) {
        assertValidMinute(minute);
        DfTypeUtil.moveToCalendarMinute(_cal, minute);
        return this;
    }

    /**
     * Move to the minute just (beginning). <br />
     * e.g. moveToMinuteJust(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:<span style="color: #FD4747">00.000</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToMinuteJust() {
        DfTypeUtil.moveToCalendarMinuteJust(_cal);
        return this;
    }

    public HandyDate moveToMinuteJustAdded(int minute) {
        DfTypeUtil.moveToCalendarMinuteJustAdded(_cal, minute);
        return this;
    }

    public HandyDate moveToMinuteJustFor(int minute) {
        assertValidMinute(minute);
        DfTypeUtil.moveToCalendarMinuteJustFor(_cal, minute);
        return this;
    }

    /**
     * Move to the terminal of the minute. <br />
     * e.g. moveToMinuteTerminal(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:<span style="color: #FD4747">59.999</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToMinuteTerminal() {
        DfTypeUtil.moveToCalendarMinuteTerminal(_cal);
        return this;
    }

    public HandyDate moveToMinuteTerminalÅdded(int minute) {
        DfTypeUtil.moveToCalendarMinuteTerminalAdded(_cal, minute);
        return this;
    }

    public HandyDate moveToMinuteTerminalFor(int minute) {
        assertValidMinute(minute);
        DfTypeUtil.moveToCalendarMinuteTerminalFor(_cal, minute);
        return this;
    }

    // -----------------------------------------------------
    //                                        Move-to Second
    //                                        --------------
    /**
     * Move to the specified second.
     * <pre>
     * e.g. 2011/11/27 00:32:00
     *  moveToSecond(12): to 2007/11/27 00:00:<span style="color: #FD4747">12</span>
     *  moveToSecond(48): to 2007/11/27 00:00:<span style="color: #FD4747">48</span>
     * </pre>
     * @param second The move-to second. (MinusAllowed)
     * @return this. (NotNull)
     */
    public HandyDate moveToSecond(int second) {
        assertValidSecond(second);
        DfTypeUtil.moveToCalendarSecond(_cal, second);
        return this;
    }

    /**
     * Move to the second just (beginning). <br />
     * e.g. moveToSecondJust(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:56.<span style="color: #FD4747">000</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToSecondJust() {
        DfTypeUtil.moveToCalendarSecondJust(_cal);
        return this;
    }

    public HandyDate moveToSecondJustFor(int second) {
        assertValidSecond(second);
        DfTypeUtil.moveToCalendarSecondJustFor(_cal, second);
        return this;
    }

    public HandyDate moveToSecondJustAdded(int second) {
        DfTypeUtil.moveToCalendarSecondJustAdded(_cal, second);
        return this;
    }

    /**
     * Move to the terminal of the second. <br />
     * e.g. moveToSecondTerminal(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:56.<span style="color: #FD4747">999</span>
     * @return this. (NotNull)
     */
    public HandyDate moveToSecondTerminal() {
        DfTypeUtil.moveToCalendarSecondTerminal(_cal);
        return this;
    }

    public HandyDate moveToSecondTerminalAdded(int second) {
        DfTypeUtil.moveToCalendarSecondTerminalAdded(_cal, second);
        return this;
    }

    public HandyDate moveToSecondTerminalFor(int second) {
        assertValidSecond(second);
        DfTypeUtil.moveToCalendarSecondTerminalFor(_cal, second);
        return this;
    }

    // -----------------------------------------------------
    //                                   Move-to Millisecond
    //                                   -------------------
    /**
     * Move to the specified millisecond.
     * <pre>
     * e.g. 2011/11/27 00:00:00.456
     *  moveToMillisecond(123): to 2007/11/27 00:00:00.<span style="color: #FD4747">123</span>
     *  moveToMillisecond(877): to 2007/11/27 00:00:00.<span style="color: #FD4747">877</span>
     * </pre>
     * @param millisecond The move-to millisecond. (MinusAllowed)
     * @return this. (NotNull)
     */
    public HandyDate moveToMillisecond(int millisecond) {
        assertValidMillisecond(millisecond);
        DfTypeUtil.moveToCalendarMillisecond(_cal, millisecond);
        return this;
    }

    // -----------------------------------------------------
    //                                          Move-to Week
    //                                          ------------
    public HandyDate moveToWeekOfMonth(int weekOfMonth) {
        DfTypeUtil.moveToCalendarWeekOfMonth(_cal, weekOfMonth);
        return this;
    }

    public HandyDate moveToWeekOfYear(int weekOfYear) {
        DfTypeUtil.moveToCalendarWeekOfYear(_cal, weekOfYear);
        return this;
    }

    /**
     * Move to the week just (beginning). <br />
     * You can change the beginning of day of week by beginWeek_...(). <br />
     * Default day of week is Sunday.
     * <pre>
     * e.g. 2011/11/30 12:34:56.789 (Wednesday)
     *  moveToWeekJust(): to 2011/11/27 00:00:00.000
     *  beginWeek_DayOfWeek1st_Sunday().moveToWeekJust(): to 2011/11/27 00:00:00.000
     *  beginWeek_DayOfWeek2nd_Monday().moveToWeekJust(): to 2011/11/28 00:00:00.000
     *  beginWeek_DayOfWeek3rd_Tuesday().moveToWeekJust(): to 2011/11/29 00:00:00.000
     *  beginWeek_DayOfWeek4th_Wednesday().moveToWeekJust(): to 2011/11/30 00:00:00.000
     *  beginWeek_DayOfWeek5th_Thursday().moveToWeekJust(): to 2011/11/24 00:00:00.000
     * </pre>
     * @return this. (NotNull)
     */
    public HandyDate moveToWeekJust() {
        DfTypeUtil.moveToCalendarWeekJust(_cal, _weekBeginDay);
        return this;
    }

    /**
     * Move to the terminal of the week. <br />
     * You can change the beginning of day of week by beginWeek_...(). <br />
     * Default day of week is Sunday.
     * <pre>
     * e.g. 2011/11/30 12:34:56.789 (Wednesday)
     *  moveToWeekJust(): to 2011/12/03 23:59:59.999
     *  beginWeek_DayOfWeek1st_Sunday().moveToWeekJust(): to 2011/12/03 23:59:59.999
     *  beginWeek_DayOfWeek2nd_Monday().moveToWeekJust(): to 2011/12/04 23:59:59.999
     *  beginWeek_DayOfWeek3rd_Tuesday().moveToWeekJust(): to 2011/12/05 23:59:59.999
     *  beginWeek_DayOfWeek4th_Wednesday().moveToWeekJust(): to 2011/12/06 23:59:59.999
     *  beginWeek_DayOfWeek5th_Thursday().moveToWeekJust(): to 2011/11/30 23:59:59.999
     * </pre>
     * @return this. (NotNull)
     */
    public HandyDate moveToWeekTerminal() {
        DfTypeUtil.moveToCalendarWeekTerminal(_cal, _weekBeginDay);
        return this;
    }

    // -----------------------------------------------------
    //                               Move-to Quarter of Year
    //                               -----------------------
    /**
     * Move to the quarter of year just (beginning). <br />
     * You can change the beginning of year by beginYear_Month...(). <br />
     * Default quarter of year is 1-3, 4-6, 7-9, 10-12.
     * <pre>
     * e.g.
     *  moveToQuarterOfYearJust(): 2011/02/27 12:34:56.789 to 2011/01/01 00:00:00.000
     *  moveToQuarterOfYearJust(): 2011/03/27 12:34:56.789 to 2011/01/01 00:00:00.000
     *  moveToQuarterOfYearJust(): 2011/04/27 12:34:56.789 to 2011/04/01 00:00:00.000
     *  moveToQuarterOfYearJust(): 2011/08/27 12:34:56.789 to 2011/07/01 00:00:00.000
     *  moveToQuarterOfYearJust(): 2011/11/27 12:34:56.789 to 2011/10/01 00:00:00.000
     * </pre>
     * @return this. (NotNull)
     */
    public HandyDate moveToQuarterOfYearJust() {
        DfTypeUtil.moveToCalendarQuarterOfYearJust(_cal, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearJustAdded(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearJustAdded(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearJustFor(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearJustFor(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearTerminal() {
        DfTypeUtil.moveToCalendarQuarterOfYearTerminal(_cal, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearTerminalAdded(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearTerminalAdded(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearTerminalFor(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearTerminalFor(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    // ===================================================================================
    //                                                                          Clear Date
    //                                                                          ==========
    /**
     * Clear the time parts, hours, minutes, seconds, milliseconds. <br />
     * e.g. clearTimeParts(): 2011/11/27 12:34:56.789 to 2011/11/27 00:00:00.000
     * @return this. (NotNull)
     */
    public HandyDate clearTimeParts() {
        DfTypeUtil.clearCalendarTimeParts(_cal);
        return this;
    }

    /**
     * Clear the minute with rear parts, minutes, seconds, milliseconds. <br />
     * e.g. clearMinuteWithRear(): 2011/11/27 12:34:56.789 to 2011/11/27 12:00:00.000
     * @return this. (NotNull)
     */
    public HandyDate clearMinuteWithRear() {
        DfTypeUtil.clearCalendarMinuteWithRear(_cal);
        return this;
    }

    /**
     * Clear the second with rear parts, seconds, milliseconds. <br />
     * e.g. clearSecondWithRear(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:00.000
     * @return this. (NotNull)
     */
    public HandyDate clearSecondWithRear() {
        DfTypeUtil.clearCalendarSecondWithRear(_cal);
        return this;
    }

    /**
     * Clear the millisecond. <br />
     * e.g. clearMillisecond(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:56.000
     * @return this. (NotNull)
     */
    public HandyDate clearMillisecond() {
        DfTypeUtil.clearCalendarMillisecond(_cal);
        return this;
    }

    // ===================================================================================
    //                                                                        Compare Date
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Match Date
    //                                            ----------
    /**
     * Is this date match the specified date?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isMatch(2011/11/24): true
     *  date.isMatch(2011/11/27): false
     *  date.isMatch(2011/11/28): false
     * </pre>
     * @param date The comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isMatch(Date date) {
        assertArgumentNotNull("date", date);
        return _cal.getTimeInMillis() == date.getTime();
    }

    // -----------------------------------------------------
    //                                          Greater Date
    //                                          ------------
    /**
     * Is this date greater than the specified date?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isGreaterThan(2011/11/24): true
     *  date.isGreaterThan(2011/11/27): false
     *  date.isGreaterThan(2011/11/28): false
     * </pre>
     * @param date The comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isGreaterThan(Date date) {
        assertArgumentNotNull("date", date);
        return isGreaterThanAll(date);
    }

    /**
     * Is this date greater than all the specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isGreaterThanAll(2011/11/24, 2011/11/26): true
     *  date.isGreaterThanAll(2011/11/24, 2011/11/27): false
     *  date.isGreaterThanAll(2011/11/24, 2011/11/28): false
     *  date.isGreaterThanAll(2011/11/27, 2011/11/29): false
     *  date.isGreaterThanAll(2011/11/28, 2011/11/29): false
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isGreaterThanAll(Date... dates) {
        return doCompareAll(createGreaterThanCompareCallback(), dates);
    }

    /**
     * Is this date greater than any specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isGreaterThanAny(2011/11/24, 2011/11/26): true
     *  date.isGreaterThanAny(2011/11/24, 2011/11/27): true
     *  date.isGreaterThanAny(2011/11/24, 2011/11/28): true
     *  date.isGreaterThanAny(2011/11/27, 2011/11/29): false
     *  date.isGreaterThanAny(2011/11/28, 2011/11/29): false
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isGreaterThanAny(Date... dates) {
        return doCompareAny(createGreaterThanCompareCallback(), dates);
    }

    protected CompareCallback createGreaterThanCompareCallback() {
        return new CompareCallback() {
            public boolean isTarget(Date current, Date date) {
                return current.after(date);
            }
        };
    }

    /**
     * Is this date greater than or equal the specified date?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isGreaterEqual(2011/11/24): true
     *  date.isGreaterEqual(2011/11/27): true
     *  date.isGreaterEqual(2011/11/28): false
     * </pre>
     * @param date The comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isGreaterEqual(Date date) {
        assertArgumentNotNull("date", date);
        return isGreaterEqualAll(date);
    }

    /**
     * Is this date greater than or equal all the specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isGreaterEqualAll(2011/11/24, 2011/11/26): true
     *  date.isGreaterEqualAll(2011/11/24, 2011/11/27): true
     *  date.isGreaterEqualAll(2011/11/24, 2011/11/28): false
     *  date.isGreaterEqualAll(2011/11/27, 2011/11/29): false
     *  date.isGreaterEqualAll(2011/11/28, 2011/11/29): false
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isGreaterEqualAll(Date... dates) {
        return doCompareAll(createGreaterEqualCompareCallback(), dates);
    }

    /**
     * Is this date greater than or equal any specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isGreaterEqualAny(2011/11/24, 2011/11/26): true
     *  date.isGreaterEqualAny(2011/11/24, 2011/11/27): true
     *  date.isGreaterEqualAny(2011/11/24, 2011/11/28): true
     *  date.isGreaterEqualAny(2011/11/27, 2011/11/29): true
     *  date.isGreaterEqualAny(2011/11/28, 2011/11/29): false
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isGreaterEqualAny(Date... dates) {
        return doCompareAny(createGreaterEqualCompareCallback(), dates);
    }

    protected CompareCallback createGreaterEqualCompareCallback() {
        return new CompareCallback() {
            public boolean isTarget(Date current, Date date) {
                return current.after(date) || current.equals(date);
            }
        };
    }

    // -----------------------------------------------------
    //                                             Less Date
    //                                             ---------
    /**
     * Is this date less than the specified date?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isLessThan(2011/11/24): false
     *  date.isLessThan(2011/11/27): false
     *  date.isLessThan(2011/11/28): true
     * </pre>
     * @param date The comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isLessThan(Date date) {
        assertArgumentNotNull("date", date);
        return isLessThanAll(date);
    }

    /**
     * Is this date less than all the specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isLessThanAll(2011/11/24, 2011/11/26): false
     *  date.isLessThanAll(2011/11/24, 2011/11/27): false
     *  date.isLessThanAll(2011/11/24, 2011/11/28): false
     *  date.isLessThanAll(2011/11/27, 2011/11/29): false
     *  date.isLessThanAll(2011/11/28, 2011/11/29): true
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isLessThanAll(Date... dates) {
        return doCompareAll(createLessThanCompareCallback(), dates);
    }

    /**
     * Is this date less than any specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isLessThanAny(2011/11/24, 2011/11/26): false
     *  date.isLessThanAny(2011/11/24, 2011/11/27): false
     *  date.isLessThanAny(2011/11/24, 2011/11/28): true
     *  date.isLessThanAny(2011/11/27, 2011/11/29): true
     *  date.isLessThanAny(2011/11/28, 2011/11/29): true
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isLessThanAny(Date... dates) {
        return doCompareAny(createLessThanCompareCallback(), dates);
    }

    protected CompareCallback createLessThanCompareCallback() {
        return new CompareCallback() {
            public boolean isTarget(Date current, Date date) {
                return current.before(date);
            }
        };
    }

    /**
     * Is this date less than or equal the specified date?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isLessEqual(2011/11/24): false
     *  date.isLessEqual(2011/11/27): true
     *  date.isLessEqual(2011/11/28): true
     * </pre>
     * @param date The comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isLessEqual(Date date) {
        assertArgumentNotNull("date", date);
        return isLessEqualAll(date);
    }

    /**
     * Is this date less than or equal all the specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isLessEqualAll(2011/11/24, 2011/11/26): false
     *  date.isLessEqualAll(2011/11/24, 2011/11/27): false
     *  date.isLessEqualAll(2011/11/24, 2011/11/28): false
     *  date.isLessEqualAll(2011/11/27, 2011/11/29): true
     *  date.isLessEqualAll(2011/11/28, 2011/11/29): true
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isLessEqualAll(Date... dates) {
        return doCompareAll(createLessEqualCompareCallback(), dates);
    }

    /**
     * Is this date less than or equal any specified dates?
     * <pre>
     * e.g. date: 2011/11/27
     *  date.isLessEqualAny(2011/11/24, 2011/11/26): false
     *  date.isLessEqualAny(2011/11/24, 2011/11/27): true
     *  date.isLessEqualAny(2011/11/24, 2011/11/28): true
     *  date.isLessEqualAny(2011/11/27, 2011/11/29): true
     *  date.isLessEqualAny(2011/11/28, 2011/11/29): true
     * </pre>
     * @param dates The array of comparison target date. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isLessEqualAny(Date... dates) {
        return doCompareAny(createLessEqualCompareCallback(), dates);
    }

    protected CompareCallback createLessEqualCompareCallback() {
        return new CompareCallback() {
            public boolean isTarget(Date current, Date date) {
                return current.before(date) || current.equals(date);
            }
        };
    }

    // -----------------------------------------------------
    //                                        Compare Helper
    //                                        --------------
    protected boolean doCompareAll(CompareCallback callback, Date... dates) {
        assertCompareDateArrayValid(dates);
        final Date current = getDate();
        for (Date date : dates) {
            if (!callback.isTarget(current, date)) {
                return false;
            }
        }
        return true;
    }

    protected boolean doCompareAny(CompareCallback callback, Date... dates) {
        assertCompareDateArrayValid(dates);
        final Date current = getDate();
        for (Date date : dates) {
            if (callback.isTarget(current, date)) {
                return true;
            }
        }
        return false;
    }

    protected void assertCompareDateArrayValid(Date[] dates) {
        if (dates == null || dates.length == 0) {
            String msg = "The argument 'dates' should not be null or empty.";
            throw new IllegalArgumentException(msg);
        }
    }

    protected static interface CompareCallback {
        boolean isTarget(Date current, Date date);
    }

    // ===================================================================================
    //                                                                       Confirm Parts
    //                                                                       =============
    // -----------------------------------------------------
    //                                          Confirm Year
    //                                          ------------
    /**
     * Is the year of this date same as specified year? <br />
     * e.g. if 2011/11/27, isYear(2011) is true
     * @param year The integer of year.
     * @return The determination, true or false.
     */
    public boolean isYear(int year) {
        return getYear() == year;
    }

    /**
     * Is the year of this date same as the year of the specified date? <br />
     * e.g. if 2011/11/27, isYearSameAs(toDate("2011/01/01")) is true
     * @param date The date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isYearSameAs(Date date) {
        assertArgumentNotNull("date", date);
        return getYear() == new HandyDate(date).getYear();
    }

    /**
     * Is the year of this date same as the year of the specified date? <br />
     * e.g. if 2011/11/27, isYearSameAs(new HandyDate("2011/01/01")) is true
     * @param handyDate The handy date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isYearSameAs(HandyDate handyDate) {
        assertArgumentNotNull("handyDate", handyDate);
        return getYear() == handyDate.getYear();
    }

    /**
     * Is the year of this date Anno Domini? <br />
     * e.g. 2011/11/27: true, BC982/11/27: false
     * @return The determination, true or false.
     */
    public boolean isYear_AnnoDomini() {
        return getYear() > 0;
    }

    /**
     * Is the year of this date Before Christ? <br />
     * e.g. 2011/11/27: false, BC982/11/27: true
     * @return The determination, true or false.
     */
    public boolean isYear_BeforeChrist() {
        return getYear() < 0;
    }

    // -----------------------------------------------------
    //                                         Confirm Month
    //                                         -------------
    /**
     * Is the month of this date same as specified month? <br />
     * e.g. if 2011/11/27, isMonth(11) is true
     * @param month The integer of month. (1 origin)
     * @return The determination, true or false.
     */
    public boolean isMonth(int month) {
        return getMonthAsOneOrigin() == month; // zero origin headache
    }

    /**
     * Is the year and month of this date same as the year and month of the specified date? <br />
     * e.g. if 2011/11/27, isMonthOfYearSameAs(toDate("2011/11/01")) is true
     * @param date The date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isMonthOfYearSameAs(Date date) {
        assertArgumentNotNull("date", date);
        final HandyDate handyDate = new HandyDate(date);
        return getYear() == handyDate.getYear() && getMonthAsOneOrigin() == handyDate.getMonthAsOneOrigin();
    }

    /**
     * Is the year and month of this date same as the year and month of the specified date? <br />
     * e.g. if 2011/11/27, isMonthOfYearSameAs(new HandyDate("2011/11/01")) is true
     * @param handyDate The handy date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isMonthOfYearSameAs(HandyDate handyDate) {
        assertArgumentNotNull("handyDate", handyDate);
        return getYear() == handyDate.getYear() && getMonthAsOneOrigin() == handyDate.getMonthAsOneOrigin();
    }

    /**
     * Is the month of this date same as the month of the specified date? <br />
     * e.g. if 2011/11/27, isMonthSameAs(toDate("2013/11/01")) is true
     * @param date The date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isMonthSameAs(Date date) {
        assertArgumentNotNull("date", date);
        return getMonthAsOneOrigin() == new HandyDate(date).getMonthAsOneOrigin();
    }

    /**
     * Is the month of this date same as the month of the specified date? <br />
     * e.g. if 2011/11/27, isMonthSameAs(new HandyDate("2013/11/01")) is true
     * @param handyDate The handy date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isMonthSameAs(HandyDate handyDate) {
        assertArgumentNotNull("handyDate", handyDate);
        return getMonthAsOneOrigin() == handyDate.getMonthAsOneOrigin();
    }

    /**
     * Is the month January?
     * @return The determination, true or false.
     */
    public boolean isMonth01_January() {
        return isMonth(1);
    }

    /**
     * Is the month February?
     * @return The determination, true or false.
     */
    public boolean isMonth02_February() {
        return isMonth(2);
    }

    /**
     * Is the month March?
     * @return The determination, true or false.
     */
    public boolean isMonth03_March() {
        return isMonth(3);
    }

    /**
     * Is the month April?
     * @return The determination, true or false.
     */
    public boolean isMonth04_April() {
        return isMonth(4);
    }

    /**
     * Is the month May?
     * @return The determination, true or false.
     */
    public boolean isMonth05_May() {
        return isMonth(5);
    }

    /**
     * Is the month June?
     * @return The determination, true or false.
     */
    public boolean isMonth06_June() {
        return isMonth(6);
    }

    /**
     * Is the month July?
     * @return The determination, true or false.
     */
    public boolean isMonth07_July() {
        return isMonth(7);
    }

    /**
     * Is the month August?
     * @return The determination, true or false.
     */
    public boolean isMonth08_August() {
        return isMonth(8);
    }

    /**
     * Is the month September?
     * @return The determination, true or false.
     */
    public boolean isMonth09_September() {
        return isMonth(9);
    }

    /**
     * Is the month October?
     * @return The determination, true or false.
     */
    public boolean isMonth10_October() {
        return isMonth(10);
    }

    /**
     * Is the month November?
     * @return The determination, true or false.
     */
    public boolean isMonth11_November() {
        return isMonth(11);
    }

    /**
     * Is the month December?
     * @return The determination, true or false.
     */
    public boolean isMonth12_December() {
        return isMonth(12);
    }

    // -----------------------------------------------------
    //                                           Confirm Day
    //                                           -----------
    /**
     * Is the day of this date same as specified day? <br />
     * e.g. if 2011/11/27, isDay(27) is true
     * @param day The integer of day.
     * @return The determination, true or false.
     */
    public boolean isDay(int day) {
        return getDay() == day;
    }

    /**
     * Is the date of this date same as the date of the specified date? <br />
     * e.g. if 2011/11/27 00:00:00, isDaySameAs(toDate("2011/11/27 12:34:56")) is true
     * @param date The date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isDayOfDateSameAs(Date date) {
        assertArgumentNotNull("date", date);
        final HandyDate handyDate = new HandyDate(date);
        final int year = handyDate.getYear();
        final int month = handyDate.getMonthAsOneOrigin();
        final int day = handyDate.getDay();
        return getYear() == year && getMonthAsOneOrigin() == month && getDay() == day;
    }

    /**
     * Is the date of this date same as the date of the specified date? <br />
     * e.g. if 2011/11/27 00:00:00, isDaySameAs(new HandyDate("2011/11/27 12:34:56")) is true
     * @param handyDate The handy date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isDayOfDateSameAs(HandyDate handyDate) {
        assertArgumentNotNull("handyDate", handyDate);
        final int year = handyDate.getYear();
        final int month = handyDate.getMonthAsOneOrigin();
        final int day = handyDate.getDay();
        return getYear() == year && getMonthAsOneOrigin() == month && getDay() == day;
    }

    /**
     * Is the day of this date same as the day of the specified date? <br />
     * e.g. if 2011/11/27, isDaySameAs(toDate("2013/09/27")) is true
     * @param date The date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isDaySameAs(Date date) {
        assertArgumentNotNull("date", date);
        return getDay() == new HandyDate(date).getDay();
    }

    /**
     * Is the day of this date same as the day of the specified date? <br />
     * e.g. if 2011/11/27, isDaySameAs(new HandyDate("2013/09/27")) is true
     * @param handyDate The handy date to compare. (NotNull)
     * @return The determination, true or false.
     */
    public boolean isDaySameAs(HandyDate handyDate) {
        assertArgumentNotNull("handyDate", handyDate);
        return getDay() == handyDate.getDay();
    }

    /**
     * Is the day of this date same as first day of the month? <br />
     * <pre>
     * e.g.
     *  2011/11/01: true
     *  2011/11/02: false
     *  2011/11/30: false
     * </pre>
     * @return The determination, true or false.
     */
    public boolean isDay_MonthFirstDay() {
        return isDay(_cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    }

    /**
     * Is the day of this date same as last day of the month? <br />
     * <pre>
     * e.g.
     *  2011/11/01: false
     *  2011/11/02: false
     *  2011/11/30: true
     *  2011/12/30: false
     *  2011/12/31: true
     * </pre>
     * @return The determination, true or false.
     */
    public boolean isDay_MonthLastDay() {
        return isDay(_cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    // -----------------------------------------------------
    //                                          Confirm Week
    //                                          ------------
    /**
     * Is the day of week Sunday?
     * @return The determination, true or false.
     */
    public boolean isWeek_DayOfWeek1st_Sunday() {
        return getDayOfWeek() == Calendar.SUNDAY;
    }

    /**
     * Is the day of week Monday?
     * @return The determination, true or false.
     */
    public boolean isWeek_DayOfWeek2nd_Monday() {
        return getDayOfWeek() == Calendar.MONDAY;
    }

    /**
     * Is the day of week Tuesday?
     * @return The determination, true or false.
     */
    public boolean isWeek_DayOfWeek3rd_Tuesday() {
        return getDayOfWeek() == Calendar.TUESDAY;
    }

    /**
     * Is the day of week Wednesday?
     * @return The determination, true or false.
     */
    public boolean isWeek_DayOfWeek4th_Wednesday() {
        return getDayOfWeek() == Calendar.WEDNESDAY;
    }

    /**
     * Is the day of week Thursday?
     * @return The determination, true or false.
     */
    public boolean isWeek_DayOfWeek5th_Thursday() {
        return getDayOfWeek() == Calendar.THURSDAY;
    }

    /**
     * Is the day of week Friday?
     * @return The determination, true or false.
     */
    public boolean isWeek_DayOfWeek6th_Friday() {
        return getDayOfWeek() == Calendar.FRIDAY;
    }

    /**
     * Is the day of week Saturday?
     * @return The determination, true or false.
     */
    public boolean isWeek_DayOfWeek7th_Saturday() {
        return getDayOfWeek() == Calendar.SATURDAY;
    }

    // ===================================================================================
    //                                                                     Calculate Parts
    //                                                                     ===============
    /**
     * Calculate year distance between two date. <br />
     * <pre>
     * e.g.
     *  2013/03/03(this) and 2014/03/03(argument): 1
     *  2014/03/03(this) and 2012/03/03(argument): -2
     * </pre>
     * @param date The date to calculate. (NotNull)
     * @return The count of year as distance between the two date. (MinusAllowed)
     */
    public int calculateDistanceYears(Date date) {
        assertArgumentNotNull("date", date);
        if (isYearSameAs(date)) {
            return 0;
        }
        final HandyDate you = new HandyDate(date);
        return you.getYear() - getYear();
    }

    /**
     * Calculate month distance between two date. <br />
     * <pre>
     * e.g.
     *  2013/03/03(this) and 2013/04/03(argument): 1
     *  2013/03/03(this) and 2013/01/03(argument): -2
     *  2013/03/03(this) and 2014/01/03(argument): 10
     * </pre>
     * @param date The date to calculate. (NotNull)
     * @return The count of month as distance between the two date. (MinusAllowed)
     */
    public int calculateDistanceMonths(Date date) {
        assertArgumentNotNull("date", date);
        if (isMonthOfYearSameAs(date)) {
            return 0;
        }
        final HandyDate you = new HandyDate(date);
        final boolean greater = isGreaterThan(date);
        int countMonths = 0;
        while (true) {
            if (isMonthOfYearSameAs(you)) {
                break;
            }
            final boolean sameAs = isYearSameAs(you);
            final int baseMonths = sameAs ? getMonthAsOneOrigin() : (greater ? 12 : 1);
            final int adjustmentMonths = sameAs ? 0 : (greater ? 1 : -1);
            final int plusMonths = baseMonths - you.getMonthAsOneOrigin() + adjustmentMonths;
            you.addMonth(plusMonths);
            countMonths = countMonths + plusMonths;
        }
        return -1 * countMonths; // -1 for greater: plus, less: minus
    }

    /**
     * Calculate day distance between two date.
     * <pre>
     * e.g.
     *  2013/03/03(this) and 2013/03/07(argument): 4
     *  2013/03/03(this) and 2013/04/07(argument): 35
     *  2013/04/07(this) and 2013/03/03(argument): -35
     *  2013/03/03(this) and 2014/03/03(argument): 365
     * </pre>
     * @param date The date to calculate. (NotNull)
     * @return The count of day as distance between the two date. (MinusAllowed)
     */
    public int calculateDistanceDays(Date date) {
        assertArgumentNotNull("date", date);
        if (isDayOfDateSameAs(date)) {
            return 0;
        }
        final HandyDate you = new HandyDate(date);
        final boolean greater = isGreaterThan(date);
        int countDays = 0;
        while (true) {
            if (isDayOfDateSameAs(you)) {
                break;
            }
            final boolean sameAs = isMonthOfYearSameAs(you);
            final int baseDays = sameAs ? getDay() : (greater ? you.getLastDayOfMonth() : you.getFirstDayOfMonth());
            final int adjustmentDays = sameAs ? 0 : (greater ? 1 : -1);
            final int plusDays = baseDays - you.getDay() + adjustmentDays;
            you.addDay(plusDays);
            countDays = countDays + plusDays;
        }
        return -1 * countDays; // -1 for greater: plus, less: minus
    }

    // ===================================================================================
    //                                                                          Begin Date
    //                                                                          ==========
    // -----------------------------------------------------
    //                                            Begin Year
    //                                            ----------
    /**
     * Begin year from the specified month. <br />
     * The date of argument is used as only the month part.
     * <pre>
     * e.g. beginYear_Month(toDate("2001/04/01"))
     *  year is from 4th month to 3rd month of next year
     *  (the 2011 year means 2011/04/01 to 2012/03/31)
     * 
     *  if the date is 2011/01/01, moveToYearJust() moves it to 2011/04/01
     *  (means the date moves to just beginning of the 2011 year)
     * </pre>
     * @param yearBeginMonth The date that has the month of year-begin. (NotNull)
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month(Date yearBeginMonth) {
        assertArgumentNotNull("yearBeginMonth", yearBeginMonth);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(yearBeginMonth);
        _yearBeginMonth = cal.get(Calendar.MONTH) + 1; // zero origin headache
        return this;
    }

    /**
     * Begin year from the specified month.
     * <pre>
     * e.g. beginYear_Month(4)
     *  year is from 4th month to 3rd month of next year
     *  (the 2011 year means 2011/04/01 to 2012/03/31)
     * 
     *  if the date is 2011/01/01, moveToYearJust() moves it to 2011/04/01
     *  (means the date moves to just beginning of the 2011 year)
     * </pre>
     * @param yearBeginMonth The month for year-begin.
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month(int yearBeginMonth) {
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = yearBeginMonth;
        return this;
    }

    /**
     * Begin year from January (1st month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month01_January() {
        _yearBeginMonth = 1;
        return this;
    }

    /**
     * Begin year from February (2nd month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month02_February() {
        _yearBeginMonth = 2;
        return this;
    }

    /**
     * Begin year from March (3rd month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month03_March() {
        _yearBeginMonth = 3;
        return this;
    }

    /**
     * Begin year from April (4th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month04_April() {
        _yearBeginMonth = 4;
        return this;
    }

    /**
     * Begin year from May (5th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month05_May() {
        _yearBeginMonth = 5;
        return this;
    }

    /**
     * Begin year from June (6th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month06_June() {
        _yearBeginMonth = 6;
        return this;
    }

    /**
     * Begin year from July (7th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month07_July() {
        _yearBeginMonth = 7;
        return this;
    }

    /**
     * Begin year from August (8th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month08_August() {
        _yearBeginMonth = 8;
        return this;
    }

    /**
     * Begin year from September (9th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month09_September() {
        _yearBeginMonth = 9;
        return this;
    }

    /**
     * Begin year from October (10th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month10_October() {
        _yearBeginMonth = 10;
        return this;
    }

    /**
     * Begin year from November (11th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month11_November() {
        _yearBeginMonth = 11;
        return this;
    }

    /**
     * Begin year from December (12th month).
     * @return this. (NotNull)
     */
    public HandyDate beginYear_Month12_December() {
        _yearBeginMonth = 12;
        return this;
    }

    /**
     * Begin year from the specified month of previous year.
     * <pre>
     * e.g. beginYear_PreviousMonth(11)
     *  year is from 11th month of previous year to 10th month of this year
     *  (the 2011 year means 2010/11/01 to 2011/10/31)
     * 
     *  if the date is 2011/01/01, moveToYearJust() moves it to 2010/11/01
     *  (means the date moves to just beginning of the 2011 year)
     * </pre>
     * @param yearBeginMonth The month of previous year for year-begin.
     * @return this. (NotNull)
     */
    public HandyDate beginYear_PreviousMonth(int yearBeginMonth) {
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = -yearBeginMonth; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                           Begin Month
    //                                           -----------
    /**
     * Begin month from the specified day. <br />
     * The date of argument is used as only the day part.
     * <pre>
     * e.g. beginMonth_Day(toDate("2001/01/03"))
     *  month is from 3 day to 2 day of next month
     *  (the 2011/11 means 2011/11/03 to 2011/12/02)
     * 
     *  if the date is 2011/11/01, moveToMonthJust() moves it to 2011/11/03
     *  (means the date moves to just beginning of 2011/11)
     * </pre>
     * @param monthBeginDay The date that has the day of month-begin. (NotNull)
     * @return this. (NotNull)
     */
    public HandyDate beginMonth_Day(Date monthBeginDay) {
        assertArgumentNotNull("monthBeginDay", monthBeginDay);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(monthBeginDay);
        _monthBeginDay = cal.get(Calendar.DAY_OF_MONTH);
        return this;
    }

    /**
     * Begin month from the specified day.
     * <pre>
     * e.g. beginMonth_Day(3)
     *  month is from 3 day to 2 day of next month
     *  (the 2011/11 means 2011/11/03 to 2011/12/02)
     * 
     *  if the date is 2011/11/01, moveToMonthJust() moves it to 2011/11/03
     *  (means the date moves to just beginning of 2011/11)
     * </pre>
     * @param monthBeginDay The day for month-begin.
     * @return this. (NotNull)
     */
    public HandyDate beginMonth_Day(int monthBeginDay) {
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = monthBeginDay;
        return this;
    }

    /**
     * Begin year from the specified day of previous month.
     * <pre>
     * e.g. beginMonth_PreviousDay(25)
     *  month is from 25 day of previous year to 24 day of this month
     *  (the 2011/11 means 2011/10/25 to 2011/11/24)
     * 
     *  if the date is 2011/11/01, moveToMonthJust() moves it to 2011/10/25
     *  (means the date moves to just beginning of 2011/11)
     * </pre>
     * @param monthBeginDay The day of previous month for month-begin.
     * @return this. (NotNull)
     */
    public HandyDate beginMonth_PreviousDay(int monthBeginDay) {
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = -monthBeginDay; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                             Begin Day
    //                                             ---------
    /**
     * Begin day from the specified hour.
     * <pre>
     * e.g. beginDay_Hour(toDate("2001/01/01 06:00:00"))
     *  day is from 06h to 05h of next day
     *  (the 2011/11/27 means 2011/11/27 06h to 2011/11/28 05h)
     * 
     *  if the date is 2011/11/27 00:00:00, moveToDayJust() moves it to 2011/11/27 06:00:00
     *  (means the date moves to just beginning of 2011/11/27)
     * </pre>
     * @param dayBeginHour The date that has the hour of day-begin. (NotNull)
     * @return this. (NotNull)
     */
    public HandyDate beginDay_Hour(Date dayBeginHour) {
        assertArgumentNotNull("dayBeginHour", dayBeginHour);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dayBeginHour);
        _dayBeginHour = cal.get(Calendar.HOUR_OF_DAY);
        return this;
    }

    /**
     * Begin day from the specified hour.
     * <pre>
     * e.g. beginDay_Hour(6)
     *  day is from 06h to 05h of next day
     *  (the 2011/11/27 means 2011/11/27 06h to 2011/11/28 05h)
     * 
     *  if the date is 2011/11/27 00:00:00, moveToDayJust() moves it to 2011/11/27 06:00:00
     *  (means the date moves to just beginning of 2011/11/27)
     * </pre>
     * @param dayBeginHour The day of day-begin.
     * @return this. (NotNull)
     */
    public HandyDate beginDay_Hour(int dayBeginHour) {
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = dayBeginHour;
        return this;
    }

    /**
     * Begin day from the specified hour of previous day.
     * <pre>
     * e.g. beginDay_PreviousHour(22)
     *  day is from 22h of previous day to 21h of this day
     *  (the 2011/11/27 means 2011/11/26 22h to 2011/11/27 21h)
     * 
     *  if the date is 2011/11/27 00:00:00, moveToDayJust() moves it to 2011/11/26 22:00:00
     *  (means the date moves to just beginning of 2011/11/27)
     * </pre>
     * @param dayBeginHour The day of day-begin.
     * @return this. (NotNull)
     */
    public HandyDate beginDay_PreviousHour(int dayBeginHour) {
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = -dayBeginHour; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                            Begin Week
    //                                            ----------
    /**
     * Begin week from the specified day of week.
     * <pre>
     * e.g. beginWeek_DayOfWeek(toDate("2011/11/28")) *means Monday
     *  week starts Monday (the 2011/11/27 belongs the week, 2011/11/21 to 2011/11/27)
     * 
     *  if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/21
     *  (means the date moves to just beginning of week containing 2011/11/27)
     * </pre>
     * @param weekBeginDayOfWeek The date that has the day of day-of-week-begin. (NotNull)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek(Date weekBeginDayOfWeek) {
        assertArgumentNotNull("weekBeginDayOfWeek", weekBeginDayOfWeek);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(weekBeginDayOfWeek);
        _weekBeginDay = cal.get(Calendar.DAY_OF_WEEK);
        return this;
    }

    /**
     * Begin week from Sunday. <br />
     * if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/27 <br />
     * (means the date moves to just beginning of week containing 2011/11/27)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek1st_Sunday() {
        _weekBeginDay = Calendar.SUNDAY;
        return this;
    }

    /**
     * Begin week from Monday. <br />
     * if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/21 <br />
     * (means the date moves to just beginning of week containing 2011/11/27)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek2nd_Monday() {
        _weekBeginDay = Calendar.MONDAY;
        return this;
    }

    /**
     * Begin week from Tuesday. <br />
     * if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/22 <br />
     * (means the date moves to just beginning of week containing 2011/11/27)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek3rd_Tuesday() {
        _weekBeginDay = Calendar.TUESDAY;
        return this;
    }

    /**
     * Begin week from Wednesday. <br />
     * if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/23 <br />
     * (means the date moves to just beginning of week containing 2011/11/27)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek4th_Wednesday() {
        _weekBeginDay = Calendar.WEDNESDAY;
        return this;
    }

    /**
     * Begin week from Thursday. <br />
     * if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/24 <br />
     * (means the date moves to just beginning of week containing 2011/11/27)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek5th_Thursday() {
        _weekBeginDay = Calendar.THURSDAY;
        return this;
    }

    /**
     * Begin week from Friday. <br />
     * if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/25 <br />
     * (means the date moves to just beginning of week containing 2011/11/27)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek6th_Friday() {
        _weekBeginDay = Calendar.FRIDAY;
        return this;
    }

    /**
     * Begin week from Saturday. <br />
     * if the date is 2011/11/27 00:00:00, moveToWeekJust() moves it to 2011/11/26 <br />
     * (means the date moves to just beginning of week containing 2011/11/27)
     * @return this. (NotNull)
     */
    public HandyDate beginWeek_DayOfWeek7th_Saturday() {
        _weekBeginDay = Calendar.SATURDAY;
        return this;
    }

    // ===================================================================================
    //                                                                            Get Date
    //                                                                            ========
    /**
     * Get created new date that has the same time of this handy date. 
     * @return The instance of date. (NotNull)
     */
    public Date getDate() {
        return new Date(_cal.getTimeInMillis());
    }

    /**
     * Get created new time-stamp that has the same time of this handy date. 
     * @return The instance of time-stamp. (NotNull)
     */
    public Timestamp getTimestamp() {
        return new Timestamp(_cal.getTimeInMillis());
    }

    // ===================================================================================
    //                                                                           Get Parts
    //                                                                           =========
    public int getYear() {
        final int year = _cal.get(Calendar.YEAR);
        final int era = _cal.get(Calendar.ERA);
        return era == GregorianCalendar.AD ? year : -year;
    }

    public int getMonthAsOneOrigin() { // resolved zero origin headache
        return _cal.get(Calendar.MONTH) + 1;
    }

    public int getMonthAsZeroOrigin() { // for calendar
        return _cal.get(Calendar.MONTH);
    }

    public int getDay() {
        return _cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return _cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return _cal.get(Calendar.MINUTE);
    }

    public int getSecond() {
        return _cal.get(Calendar.SECOND);
    }

    public int getMillisecond() {
        return _cal.get(Calendar.MILLISECOND);
    }

    public int getDayOfWeek() {
        return _cal.get(Calendar.DAY_OF_WEEK);
    }

    public int getFirstDayOfMonth() {
        return _cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    public int getLastDayOfMonth() {
        return _cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertArgumentNotNull(String name, Object value) {
        if (value == null) {
            String msg = "The argument '" + name + "' should not be null.";
            throw new IllegalArgumentException(msg);
        }
    }

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

    protected void assertValidMonth(int month) {
        if (month < 1 || month > 12) {
            String msg = "The argument 'month' should be 1 to 12: " + month;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidDay(int day) {
        final int firstDayOfMonth = getFirstDayOfMonth();
        final int lastDayOfMonth = getLastDayOfMonth();
        if (day < firstDayOfMonth || day > lastDayOfMonth) {
            String msg = "The argument 'day' should be " + firstDayOfMonth + " to " + lastDayOfMonth + ": " + day;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidHour(int hour) {
        // e.g. 26h allowed
        //if (hour < 1 || hour > 12) {
        //    String msg = "The argument 'hour' should be 0 to 23: " + hour;
        //    throw new IllegalArgumentException(msg);
        //}
    }

    protected void assertValidMinute(int minute) {
        if (minute < 0 || minute > 59) {
            String msg = "The argument 'minute' should be 0 to 59: " + minute;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidSecond(int second) {
        if (second < 0 || second > 59) {
            String msg = "The argument 'second' should be 0 to 59: " + second;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidMillisecond(int millisecond) {
        if (millisecond < 0 || millisecond > 999) {
            String msg = "The argument 'millisecond' should be 0 to 999: " + millisecond;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                          To Display
    //                                                                          ==========
    /**
     * Convert to the display string of the date.
     * @param pattern The pattern of date, which can be used at java.text.SimpleDateFormat. (NotNull)
     * @return The display string of the date. (NotNull)
     */
    public String toDisp(String pattern) {
        return DfTypeUtil.toString(_cal.getTime(), pattern);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HandyDate) {
            final HandyDate date = (HandyDate) obj;
            final String pattern = getBasicPattern();
            return date.toDisp(pattern).equals(toDisp(pattern));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final String pattern = getBasicPattern();
        return pattern.hashCode();
    }

    @Override
    public String toString() {
        return DfTypeUtil.toString(_cal.getTime(), getBasicPattern());
    }

    protected String getBasicPattern() {
        if (isYear_BeforeChrist()) {
            return "'BC'yyyy/MM/dd HH:mm:ss.SSS";
        } else {
            return "yyyy/MM/dd HH:mm:ss.SSS";
        }
    }

    // *clone() is very hard to use (final field problem)

    /**
     * Copy this date deeply. (original method)
     * @return The copy instance of this date. (NotNull)
     */
    public HandyDate deepCopy() {
        final HandyDate cloned = newCopyInstance();
        cloned._yearBeginMonth = _yearBeginMonth;
        cloned._monthBeginDay = _monthBeginDay;
        cloned._dayBeginHour = _dayBeginHour;
        cloned._weekBeginDay = _weekBeginDay;
        return cloned;
    }

    /**
     * Create new instance for copy.
     * @return The new instance of this date. (NotNull)
     */
    protected HandyDate newCopyInstance() {
        return new HandyDate(getDate());
    }
}
