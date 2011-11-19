package org.seasar.dbflute.helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * Now making...
 * @author jflute
 * @since 0.9.9.1G (2011/11/17 Thursday)
 */
public class HandyDate {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Calendar _cal = Calendar.getInstance();
    protected int _yearBeginMonth = _cal.getActualMinimum(Calendar.MONTH) + 1; // as default (zero origin headache)
    protected int _monthBeginDay = _cal.getActualMinimum(Calendar.DAY_OF_MONTH); // as default
    protected int _dayBeginHour = _cal.getActualMinimum(Calendar.HOUR_OF_DAY); // as default
    protected int _weekBeginDay = Calendar.SUNDAY; // as default

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HandyDate(Date date) {
        _cal.setTime(date);
    }

    public HandyDate(String exp) {
        _cal.setTime(DfTypeUtil.toDate(exp));
    }

    // ===================================================================================
    //                                                                            Add Date
    //                                                                            ========
    public HandyDate addYear(int year) {
        DfTypeUtil.addCalendarYear(_cal, year);
        return this;
    }

    public HandyDate addMonth(int month) {
        DfTypeUtil.addCalendarMonth(_cal, month);
        return this;
    }

    public HandyDate addDay(int day) {
        DfTypeUtil.addCalendarDay(_cal, day);
        return this;
    }

    public HandyDate addHour(int hour) {
        DfTypeUtil.addCalendarHour(_cal, hour);
        return this;
    }

    public HandyDate addMinute(int minute) {
        DfTypeUtil.addCalendarMinute(_cal, minute);
        return this;
    }

    public HandyDate addSecond(int second) {
        DfTypeUtil.addCalendarSecond(_cal, second);
        return this;
    }

    public HandyDate addMillisecond(int millisecond) {
        DfTypeUtil.addCalendarMillisecond(_cal, millisecond);
        return this;
    }

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
    public HandyDate moveToYear(int year) {
        DfTypeUtil.moveToCalendarYear(_cal, year);
        return this;
    }

    public HandyDate moveToYearJust() {
        DfTypeUtil.moveToCalendarYearJust(_cal, _yearBeginMonth);
        return this;
    }

    public HandyDate moveToYearJustFor(int year) {
        DfTypeUtil.moveToCalendarYearJustFor(_cal, year);
        return this;
    }

    public HandyDate moveToYearTerminal() {
        DfTypeUtil.moveToCalendarYearTerminal(_cal, _yearBeginMonth);
        return this;
    }

    public HandyDate moveToYearTerminalFor(int year) {
        DfTypeUtil.moveToCalendarYearTerminalFor(_cal, year);
        return this;
    }

    // -----------------------------------------------------
    //                                         Move-to Month
    //                                         -------------
    public HandyDate moveToMonth(int month) {
        DfTypeUtil.moveToCalendarMonth(_cal, month);
        return this;
    }

    public HandyDate moveToMonthJust() {
        DfTypeUtil.moveToCalendarMonthJust(_cal, _monthBeginDay);
        return this;
    }

    public HandyDate moveToMonthJustFor(int month) {
        DfTypeUtil.moveToCalendarMonthJustFor(_cal, month);
        return this;
    }

    public HandyDate moveToMonthTerminal() {
        DfTypeUtil.moveToCalendarMonthTerminal(_cal, _monthBeginDay);
        return this;
    }

    public HandyDate moveToMonthTerminalFor(int month) {
        DfTypeUtil.moveToCalendarMonthTerminalFor(_cal, month);
        return this;
    }

    // -----------------------------------------------------
    //                                           Move-to Day
    //                                           -----------
    public HandyDate moveToDay(int day) {
        DfTypeUtil.moveToCalendarDayJust(_cal, day);
        return this;
    }

    public HandyDate moveToDayJust() {
        DfTypeUtil.moveToCalendarDayJust(_cal, _dayBeginHour);
        return this;
    }

    public HandyDate moveToDayJustFor(int day) {
        DfTypeUtil.moveToCalendarDayJust(_cal, day);
        return this;
    }

    public HandyDate moveToDayTerminal() {
        DfTypeUtil.moveToCalendarDayTerminal(_cal, _dayBeginHour);
        return this;
    }

    public HandyDate moveToDayTerminalFor(int day) {
        DfTypeUtil.moveToCalendarDayTerminal(_cal, day);
        return this;
    }

    // -----------------------------------------------------
    //                                          Move-to Hour
    //                                          ------------
    public HandyDate moveToHour(int hour) {
        DfTypeUtil.moveToCalendarHour(_cal, hour);
        return this;
    }

    public HandyDate moveToHourJust() {
        DfTypeUtil.moveToCalendarHourJust(_cal);
        return this;
    }

    public HandyDate moveToHourJustFor(int hour) {
        DfTypeUtil.moveToCalendarHourJustFor(_cal, hour);
        return this;
    }

    public HandyDate moveToHourTerminal() {
        DfTypeUtil.moveToCalendarHourTerminal(_cal);
        return this;
    }

    public HandyDate moveToHourTerminalFor(int hour) {
        DfTypeUtil.moveToCalendarHourTerminalFor(_cal, hour);
        return this;
    }

    public HandyDate moveToHourJustNoon() {
        DfTypeUtil.moveToCalendarHourJustNoon(_cal);
        return this;
    }

    // -----------------------------------------------------
    //                                        Move-to Minute
    //                                        --------------
    public HandyDate moveToMinute(int minute) {
        DfTypeUtil.moveToCalendarMinute(_cal, minute);
        return this;
    }

    public HandyDate moveToMinuteJust() {
        DfTypeUtil.moveToCalendarMinuteJust(_cal);
        return this;
    }

    public HandyDate moveToMinuteJustFor(int minute) {
        DfTypeUtil.moveToCalendarMinuteJustFor(_cal, minute);
        return this;
    }

    public HandyDate moveToMinuteTerminal() {
        DfTypeUtil.moveToCalendarMinuteTerminal(_cal);
        return this;
    }

    public HandyDate moveToMinuteTerminalFor(int minute) {
        DfTypeUtil.moveToCalendarMinuteTerminalFor(_cal, minute);
        return this;
    }

    // -----------------------------------------------------
    //                                        Move-to Second
    //                                        --------------
    public HandyDate moveToSecond(int second) {
        DfTypeUtil.moveToCalendarSecond(_cal, second);
        return this;
    }

    public HandyDate moveToSecondJust() {
        DfTypeUtil.moveToCalendarSecondJust(_cal);
        return this;
    }

    public HandyDate moveToSecondJustFor(int second) {
        DfTypeUtil.moveToCalendarSecondJustFor(_cal, second);
        return this;
    }

    public HandyDate moveToSecondTerminal() {
        DfTypeUtil.moveToCalendarSecondTerminal(_cal);
        return this;
    }

    public HandyDate moveToSecondTerminalFor(int second) {
        DfTypeUtil.moveToCalendarSecondTerminalFor(_cal, second);
        return this;
    }

    // -----------------------------------------------------
    //                                   Move-to Millisecond
    //                                   -------------------
    public HandyDate moveToMillisecond(int millisecond) {
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

    public HandyDate moveToWeekJust() {
        DfTypeUtil.moveToCalendarWeekJust(_cal, _weekBeginDay);
        return this;
    }

    public HandyDate moveToWeekTerminal() {
        DfTypeUtil.moveToCalendarWeekTerminal(_cal, _weekBeginDay);
        return this;
    }

    // ===================================================================================
    //                                                                          Clear Date
    //                                                                          ==========
    public HandyDate clearTimeParts() {
        DfTypeUtil.clearCalendarTimeParts(_cal);
        return this;
    }

    public HandyDate clearMinuteWithRear() {
        DfTypeUtil.clearCalendarMinuteWithRear(_cal);
        return this;
    }

    public HandyDate clearSecondWithRear() {
        DfTypeUtil.clearCalendarSecondWithRear(_cal);
        return this;
    }

    public HandyDate clearMillisecond() {
        DfTypeUtil.clearCalendarMillisecond(_cal);
        return this;
    }

    // ===================================================================================
    //                                                                        Confirm Date
    //                                                                        ============
    // -----------------------------------------------------
    //                                          Confirm Year
    //                                          ------------
    public boolean isYear(int year) {
        return _cal.get(Calendar.YEAR) == year;
    }

    // -----------------------------------------------------
    //                                         Confirm Month
    //                                         -------------
    public boolean isMonth(int month) {
        return _cal.get(Calendar.MONTH) - 1 == month; // zero origin headache
    }

    public boolean isMonth01_January() {
        return isMonth(1);
    }

    public boolean isMonth02_February() {
        return isMonth(2);
    }

    public boolean isMonth03_March() {
        return isMonth(3);
    }

    public boolean isMonth04_April() {
        return isMonth(4);
    }

    public boolean isMonth05_May() {
        return isMonth(5);
    }

    public boolean isMonth06_June() {
        return isMonth(6);
    }

    public boolean isMonth07_July() {
        return isMonth(7);
    }

    public boolean isMonth08_August() {
        return isMonth(8);
    }

    public boolean isMonth09_September() {
        return isMonth(9);
    }

    public boolean isMonth10_October() {
        return isMonth(10);
    }

    public boolean isMonth11_November() {
        return isMonth(11);
    }

    public boolean isMonth12_December() {
        return isMonth(12);
    }

    // -----------------------------------------------------
    //                                           Confirm Day
    //                                           -----------
    public boolean isDay(int day) {
        return _cal.get(Calendar.DAY_OF_MONTH) == day;
    }

    public boolean isDay_MonthFirstDay() {
        return _cal.get(Calendar.DAY_OF_MONTH) == _cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    public boolean isDay_MonthLastDay() {
        return _cal.get(Calendar.DAY_OF_MONTH) == _cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // ===================================================================================
    //                                                                          Begin Date
    //                                                                          ==========
    // -----------------------------------------------------
    //                                            Begin Year
    //                                            ----------
    public HandyDate beginYear_Month(Date yearBeginMonth) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(yearBeginMonth);
        _yearBeginMonth = cal.get(Calendar.MONTH) + 1; // zero origin headache
        return this;
    }

    public HandyDate beginYear_Month(int yearBeginMonth) {
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = yearBeginMonth;
        return this;
    }

    public HandyDate beginYear_Month01_January() {
        _yearBeginMonth = 1;
        return this;
    }

    public HandyDate beginYear_Month02_February() {
        _yearBeginMonth = 2;
        return this;
    }

    public HandyDate beginYear_Month03_March() {
        _yearBeginMonth = 3;
        return this;
    }

    public HandyDate beginYear_Month04_April() {
        _yearBeginMonth = 4;
        return this;
    }

    public HandyDate beginYear_Month05_May() {
        _yearBeginMonth = 5;
        return this;
    }

    public HandyDate beginYear_Month06_June() {
        _yearBeginMonth = 6;
        return this;
    }

    public HandyDate beginYear_Month07_July() {
        _yearBeginMonth = 7;
        return this;
    }

    public HandyDate beginYear_Month08_August() {
        _yearBeginMonth = 8;
        return this;
    }

    public HandyDate beginYear_Month09_September() {
        _yearBeginMonth = 9;
        return this;
    }

    public HandyDate beginYear_Month10_October() {
        _yearBeginMonth = 10;
        return this;
    }

    public HandyDate beginYear_Month11_November() {
        _yearBeginMonth = 11;
        return this;
    }

    public HandyDate beginYear_Month12_December() {
        _yearBeginMonth = 12;
        return this;
    }

    public HandyDate beginYear_PreviousMonth(int yearBeginMonth) {
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = -yearBeginMonth; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                           Begin Month
    //                                           -----------
    public HandyDate beginMonth_Day(Date monthBeginDay) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(monthBeginDay);
        _monthBeginDay = cal.get(Calendar.DAY_OF_MONTH);
        return this;
    }

    public HandyDate beginMonth_Day(int monthBeginDay) {
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = monthBeginDay;
        return this;
    }

    public HandyDate beginMonth_PreviousDay(int monthBeginDay) {
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = -monthBeginDay; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                             Begin Day
    //                                             ---------
    public HandyDate beginDay_Hour(Date dayBeginHour) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dayBeginHour);
        _dayBeginHour = cal.get(Calendar.HOUR_OF_DAY);
        return this;
    }

    public HandyDate beginDay_Hour(int dayBeginHour) {
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = dayBeginHour;
        return this;
    }

    public HandyDate beginDay_PreviousHour(int dayBeginHour) {
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = -dayBeginHour; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                            Begin Week
    //                                            ----------
    public HandyDate beginWeek_DayOfWeek(Date weekBeginDayOfWeek) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(weekBeginDayOfWeek);
        _weekBeginDay = cal.get(Calendar.DAY_OF_WEEK);
        return this;
    }

    public HandyDate beginWeek_DayOfWeek1st_Sunday() {
        _weekBeginDay = Calendar.SUNDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek2nd_Monday() {
        _weekBeginDay = Calendar.MONDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek3rd_Tuesday() {
        _weekBeginDay = Calendar.TUESDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek4th_Wednesday() {
        _weekBeginDay = Calendar.WEDNESDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek5th_Thursday() {
        _weekBeginDay = Calendar.THURSDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek6th_Friday() {
        _weekBeginDay = Calendar.FRIDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek7th_Saturday() {
        _weekBeginDay = Calendar.SATURDAY;
        return this;
    }

    // ===================================================================================
    //                                                                            Get Date
    //                                                                            ========
    public Date getDate() {
        return _cal.getTime();
    }

    public Timestamp getTimestamp() {
        return new Timestamp(_cal.getTimeInMillis());
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

    // ===================================================================================
    //                                                                          To Display
    //                                                                          ==========
    public String toDisp(String pattern) {
        return DfTypeUtil.toString(_cal.getTime(), pattern);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int hashCode() {
        final String pattern = "yyyy/MM/dd HH:mm:ss.SSS";
        return pattern.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HandyDate) {
            final HandyDate date = (HandyDate) obj;
            final String pattern = "yyyy/MM/dd HH:mm:ss.SSS";
            return date.toDisp(pattern).equals(toDisp(pattern));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return DfTypeUtil.toString(_cal.getTime(), "yyyy/MM/dd HH:mm:ss.SSS");
    }
}
