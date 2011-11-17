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
    protected int _weekStartDay = Calendar.SUNDAY; // as default

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
        DfTypeUtil.addCalendarDayOfMonth(_cal, day);
        return this;
    }

    public HandyDate addHour(int hour) {
        DfTypeUtil.addCalendarHourOfDay(_cal, hour);
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
        DfTypeUtil.addCalendarWeekOfMonth(_cal, week);
        return this;
    }

    // ===================================================================================
    //                                                                        Move-to Date
    //                                                                        ============
    public HandyDate moveToHour(int hour) {
        DfTypeUtil.moveToCalendarHour(_cal, hour);
        return this;
    }

    public HandyDate moveToHourBegin() {
        DfTypeUtil.moveToCalendarHourBegin(_cal);
        return this;
    }

    public HandyDate moveToHourEnd() {
        DfTypeUtil.moveToCalendarHourEnd(_cal);
        return this;
    }

    public HandyDate moveToHourNoon() {
        DfTypeUtil.moveToCalendarHourNoon(_cal);
        return this;
    }

    public HandyDate moveToDayBegin() {
        DfTypeUtil.moveToCalendarDayBegin(_cal);
        return this;
    }

    public HandyDate moveToDayEnd() {
        DfTypeUtil.moveToCalendarDayEnd(_cal);
        return this;
    }

    public HandyDate moveToMonthBegin() {
        DfTypeUtil.moveToCalendarMonthBegin(_cal);
        return this;
    }

    public HandyDate moveToMonthEnd() {
        DfTypeUtil.moveToCalendarMonthEnd(_cal);
        return this;
    }

    public HandyDate moveToYearBegin() {
        DfTypeUtil.moveToCalendarYearBegin(_cal);
        return this;
    }

    public HandyDate moveToYearEnd() {
        DfTypeUtil.moveToCalendarYearEnd(_cal);
        return this;
    }

    public HandyDate moveToWeekBegin() {
        DfTypeUtil.moveToCalendarWeekBegin(_cal, _weekStartDay);
        return this;
    }

    public HandyDate moveToWeekEnd() {
        DfTypeUtil.moveToCalendarWeekEnd(_cal, _weekStartDay);
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
    //                                                                            Week Day
    //                                                                            ========
    public HandyDate beginWeek(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        _weekStartDay = cal.get(Calendar.DAY_OF_WEEK);
        return this;
    }

    public HandyDate beginWeek_Sunday() {
        _weekStartDay = Calendar.SUNDAY;
        return this;
    }

    public HandyDate beginWeek_Monday() {
        _weekStartDay = Calendar.MONDAY;
        return this;
    }

    public HandyDate beginWeek_Tuesday() {
        _weekStartDay = Calendar.TUESDAY;
        return this;
    }

    public HandyDate beginWeek_Wednesday() {
        _weekStartDay = Calendar.WEDNESDAY;
        return this;
    }

    public HandyDate beginWeek_Thursday() {
        _weekStartDay = Calendar.THURSDAY;
        return this;
    }

    public HandyDate beginWeek_Friday() {
        _weekStartDay = Calendar.FRIDAY;
        return this;
    }

    public HandyDate beginWeek_Saturday() {
        _weekStartDay = Calendar.SATURDAY;
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
}
