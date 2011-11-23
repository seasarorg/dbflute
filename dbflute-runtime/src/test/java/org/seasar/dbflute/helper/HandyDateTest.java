package org.seasar.dbflute.helper;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 */
public class HandyDateTest extends PlainTestCase {

    // ===================================================================================
    //                                                                            Add Date
    //                                                                            ========
    public void test_add_basic() throws Exception {
        // ## Arrange ##
        HandyDate date = new HandyDate("2011/11/17 12:34:56.789");

        // ## Act ##
        date.addYear(1).addMonth(1).addDay(1).addHour(1).addMinute(1).addSecond(1).addMillisecond(1);

        // ## Assert ##
        assertEquals("2012/12/18 13:35:57.790", toString(date.getDate(), "yyyy/MM/dd HH:mm:ss.SSS"));
    }

    // ===================================================================================
    //                                                                        Move-to Date
    //                                                                        ============
    public void test_moveTo_basic() throws Exception {
        // ## Arrange ##
        String targetExp = "2011/11/17 12:34:56.789";

        // ## Act & Assert ##
        assertEquals(handy("2011/01/01 00:00:00.000"), handy(targetExp).moveToYearJust());
        assertEquals(handy("2011/12/31 23:59:59.999"), handy(targetExp).moveToYearTerminal());
        assertEquals(handy("2011/11/01 00:00:00.000"), handy(targetExp).moveToMonthJust());
        assertEquals(handy("2011/11/30 23:59:59.999"), handy(targetExp).moveToMonthTerminal());
        assertEquals(handy("2011/11/17 00:00:00.000"), handy(targetExp).moveToDayJust());
        assertEquals(handy("2011/11/17 23:59:59.999"), handy(targetExp).moveToDayTerminal());
        assertEquals(handy("2011/11/17 12:00:00.000"), handy(targetExp).moveToHourJust());
        assertEquals(handy("2011/11/17 12:59:59.999"), handy(targetExp).moveToHourTerminal());
        assertEquals(handy("2011/11/17 12:34:00.000"), handy(targetExp).moveToMinuteJust());
        assertEquals(handy("2011/11/17 12:34:59.999"), handy(targetExp).moveToMinuteTerminal());
        assertEquals(handy("2011/11/17 12:34:56.000"), handy(targetExp).moveToSecondJust());
        assertEquals(handy("2011/11/17 12:34:56.999"), handy(targetExp).moveToSecondTerminal());
    }

    public void test_moveTo_begin() throws Exception {
        // ## Arrange ##
        String small02 = "2011/02/02 02:02:02.222";
        String large11 = "2011/11/17 11:34:56.789";

        // ## Act & Assert ##
        assertEquals(handy("2011/04/01 00:00:00.000"), year4(small02).moveToYearJust());
        assertEquals(handy("2012/03/31 23:59:59.999"), year4(small02).moveToYearTerminal());
        assertEquals(handy("2011/04/01 00:00:00.000"), year4(large11).moveToYearJust());
        assertEquals(handy("2012/03/31 23:59:59.999"), year4(large11).moveToYearTerminal());
        assertEquals(handy("2010/11/01 00:00:00.000"), yearPre11(small02).moveToYearJust());
        assertEquals(handy("2011/10/31 23:59:59.999"), yearPre11(small02).moveToYearTerminal());
        assertEquals(handy("2010/11/01 00:00:00.000"), yearPre11(large11).moveToYearJust());
        assertEquals(handy("2011/10/31 23:59:59.999"), yearPre11(large11).moveToYearTerminal());
        assertEquals(handy("2011/02/03 00:00:00.000"), month3(small02).moveToMonthJust());
        assertEquals(handy("2011/03/02 23:59:59.999"), month3(small02).moveToMonthTerminal());
        assertEquals(handy("2011/11/03 00:00:00.000"), month3(large11).moveToMonthJust());
        assertEquals(handy("2011/12/02 23:59:59.999"), month3(large11).moveToMonthTerminal());
        assertEquals(handy("2011/01/26 00:00:00.000"), monthPre26(small02).moveToMonthJust());
        assertEquals(handy("2011/02/25 23:59:59.999"), monthPre26(small02).moveToMonthTerminal());
        assertEquals(handy("2011/10/26 00:00:00.000"), monthPre26(large11).moveToMonthJust());
        assertEquals(handy("2011/11/25 23:59:59.999"), monthPre26(large11).moveToMonthTerminal());
    }

    // -----------------------------------------------------
    //                                          Move-to Week
    //                                          ------------
    public void test_moveTo_week() throws Exception {
        // ## Arrange ##
        String large11 = "2011/11/17 11:34:56.789";

        // ## Act & Assert ##
        assertEquals(handy("2011/11/03 11:34:56.789"), handy(large11).moveToWeekOfMonth(1));
        assertEquals(handy("2011/11/10 11:34:56.789"), handy(large11).moveToWeekOfMonth(2));
        assertEquals(handy(large11), handy(large11).moveToWeekOfMonth(3));
        assertEquals(handy("2011/11/24 11:34:56.789"), handy(large11).moveToWeekOfMonth(4));
        assertEquals(handy("2011/12/01 11:34:56.789"), handy(large11).moveToWeekOfMonth(5));
        assertEquals(handy("2011/12/08 11:34:56.789"), handy(large11).moveToWeekOfMonth(6));
        assertEquals(handy("2011/10/30 00:00:00.000"), handy(large11).moveToWeekOfMonth(1).moveToWeekJust());
        assertEquals(handy("2011/11/05 23:59:59.999"), handy(large11).moveToWeekOfMonth(1).moveToWeekTerminal());
        {
            HandyDate date = handy(large11).moveToWeekOfMonth(2).beginWeek_DayOfWeek2nd_Monday().moveToWeekJust();
            assertEquals(handy("2011/11/07 00:00:00.000"), date);
        }
        assertEquals(handy("2010/12/30 11:34:56.789"), handy(large11).moveToWeekOfYear(1));
        assertEquals(handy("2011/01/06 11:34:56.789"), handy(large11).moveToWeekOfYear(2));
    }

    // -----------------------------------------------------
    //                                         Related Begin
    //                                         -------------
    public void test_moveTo_related_begin() throws Exception {
        // ## Arrange ##
        String targetExp = "2011/11/17 12:34:56.789";
        HandyDate date = handy(targetExp);
        date.beginYear_Month02_February().beginMonth_Day(3).beginDay_Hour(4);

        // ## Act & Assert ##
        assertEquals(handy("2011/02/03 04:00:00.000"), date.clone().moveToYearJust());
        date.beginDay_PreviousHour(22);
        assertEquals(handy("2011/02/02 22:00:00.000"), date.clone().moveToYearJust());
        date.beginMonth_PreviousDay(25);
        assertEquals(handy("2011/01/24 22:00:00.000"), date.clone().moveToYearJust());
        date.beginYear_PreviousMonth(11);
        assertEquals(handy("2010/10/24 22:00:00.000"), date.clone().moveToYearJust());
    }

    public void test_moveTo_related_quarterOfYear() throws Exception {
        // ## Arrange ##
        String exp = "2011/11/17 12:34:56.789";

        // ## Act & Assert ##
        assertEquals(handy("2011/11/03 04:00:00.000"), handyRelated(exp).moveToQuarterOfYearJust());
        assertEquals(handy("2012/02/03 04:00:00.000"), handyRelated(exp).moveToQuarterOfYearJustAdded(1));
        assertEquals(handy("2011/05/03 04:00:00.000"), handyRelated(exp).moveToQuarterOfYearJustFor(2));
        assertEquals(handy("2012/05/03 03:59:59.999"), handyRelated(exp).moveToQuarterOfYearTerminalAdded(1));
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected HandyDate handy(String exp) {
        return new HandyDate(exp);
    }

    protected HandyDate year4(String exp) {
        return new HandyDate(exp).beginYear_Month(4);
    }

    protected HandyDate yearPre11(String exp) {
        return new HandyDate(exp).beginYear_PreviousMonth(11);
    }

    protected HandyDate month3(String exp) {
        return new HandyDate(exp).beginMonth_Day(3);
    }

    protected HandyDate monthPre26(String exp) {
        return new HandyDate(exp).beginMonth_PreviousDay(26);
    }

    protected HandyDate handyRelated(String exp) {
        return new HandyDate(exp).beginYear_Month02_February().beginMonth_Day(3).beginDay_Hour(4);
    }
}
