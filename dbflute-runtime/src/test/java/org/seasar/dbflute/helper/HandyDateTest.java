/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
        assertEquals(handy("2007/11/17 12:34:56.789"), handy(targetExp).moveToYear(2007));
        assertEquals(handy("2011/01/01 00:00:00.000"), handy(targetExp).moveToYearJust());
        assertEquals(handy("2011/12/31 23:59:59.999"), handy(targetExp).moveToYearTerminal());
        assertEquals(handy("2011/09/17 12:34:56.789"), handy(targetExp).moveToMonth(9));
        assertEquals(handy("2011/11/01 00:00:00.000"), handy(targetExp).moveToMonthJust());
        assertEquals(handy("2011/11/30 23:59:59.999"), handy(targetExp).moveToMonthTerminal());
        assertEquals(handy("2011/11/28 12:34:56.789"), handy(targetExp).moveToDay(28));
        assertEquals(handy("2011/11/30 12:34:56.789"), handy(targetExp).moveToDay(30));
        assertEquals(handy("2011/12/31 12:34:56.789"), handy(targetExp).addMonth(1).moveToDay(31));
        assertEquals(handy("2012/02/28 12:34:56.789"), handy(targetExp).addMonth(3).moveToDay(28));
        assertEquals(handy("2011/11/17 00:00:00.000"), handy(targetExp).moveToDayJust());
        assertEquals(handy("2011/11/17 23:59:59.999"), handy(targetExp).moveToDayTerminal());
        assertEquals(handy("2011/11/17 00:34:56.789"), handy(targetExp).moveToHour(0));
        assertEquals(handy("2011/11/17 23:34:56.789"), handy(targetExp).moveToHour(23));
        assertEquals(handy("2011/11/17 12:00:00.000"), handy(targetExp).moveToHourJust());
        assertEquals(handy("2011/11/17 12:59:59.999"), handy(targetExp).moveToHourTerminal());
        assertEquals(handy("2011/11/17 12:00:56.789"), handy(targetExp).moveToMinute(0));
        assertEquals(handy("2011/11/17 12:59:56.789"), handy(targetExp).moveToMinute(59));
        assertEquals(handy("2011/11/17 12:34:00.000"), handy(targetExp).moveToMinuteJust());
        assertEquals(handy("2011/11/17 12:34:59.999"), handy(targetExp).moveToMinuteTerminal());
        assertEquals(handy("2011/11/17 12:34:00.789"), handy(targetExp).moveToSecond(0));
        assertEquals(handy("2011/11/17 12:34:59.789"), handy(targetExp).moveToSecond(59));
        assertEquals(handy("2011/11/17 12:34:56.000"), handy(targetExp).moveToSecondJust());
        assertEquals(handy("2011/11/17 12:34:56.999"), handy(targetExp).moveToSecondTerminal());
        assertEquals(handy("2011/11/17 12:34:56.000"), handy(targetExp).moveToMillisecond(0));
        assertEquals(handy("2011/11/17 12:34:56.999"), handy(targetExp).moveToMillisecond(999));
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

    public void test_moveTo_minus_allowed() throws Exception {
        // ## Arrange ##
        String targetExp = "2011/11/17 12:34:56.789";

        // ## Act & Assert ##
        assertEquals(handy("BC2007/11/17 12:34:56.789"), handy(targetExp).moveToYear(-2007));
        assertEquals(handy("BC0001/11/17 12:34:56.789"), handy(targetExp).moveToYear(-1));
    }

    public void test_moveTo_illegal() throws Exception {
        // ## Arrange ##
        String targetExp = "2011/11/17 12:34:56.789";

        // ## Act & Assert ##
        // year
        try {
            handy(targetExp).moveToYear(0);

            fail();
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }

        // month
        try {
            handy(targetExp).moveToMonth(0);

            fail();
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }
        try {
            handy(targetExp).moveToMonth(-1);

            fail();
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }

        // day
        try {
            handy(targetExp).moveToDay(0);

            fail();
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }
        try {
            handy(targetExp).moveToDay(-1);

            fail();
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }

        // hour
        try {
            handy(targetExp).moveToHour(-1);

            fail();
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }
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
        assertEquals(handy("2011/02/03 04:00:00.000"), date.moveToYearJust());
        date.beginDay_PreviousHour(22);
        assertEquals(handy("2011/02/02 22:00:00.000"), date.moveToYearJust());
        date.beginMonth_PreviousDay(25);
        assertEquals(handy("2011/01/24 22:00:00.000"), date.moveToYearJust());
        date.beginYear_PreviousMonth(11);
        assertEquals(handy("2010/10/24 22:00:00.000"), date.moveToYearJust());
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
    //                                                                        Compare Date
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Match Date
    //                                            ----------
    public void test_isMatch() throws Exception {
        assertTrue(handy("2011/11/27").isMatch(toDate("2011/11/27")));
        assertFalse(handy("2011/11/27").isMatch(toDate("2011/11/28")));
        assertTrue(handy("2011/11/27").isMatch(toDate("2011/11/27 00:00:00.000")));
        assertFalse(handy("2011/11/27").isMatch(toDate("2011/11/27 00:00:00.001")));
    }

    // -----------------------------------------------------
    //                                          Greater Date
    //                                          ------------
    public void test_isGreaterThan() throws Exception {
        assertTrue(handy("2011/11/27").isGreaterThan(toDate("2011/11/24")));
        assertFalse(handy("2011/11/27").isGreaterThan(toDate("2011/11/27")));
        assertFalse(handy("2011/11/27").isGreaterThan(toDate("2011/11/28")));
        assertTrue(handy("2011/11/27 12:34:56.789").isGreaterThan(toDate("2011/11/27 12:34:56.788")));
        assertFalse(handy("2011/11/27 12:34:56.789").isGreaterThan(toDate("2011/11/27 12:34:56.789")));
        assertFalse(handy("2011/11/27 12:34:56.789").isGreaterThan(toDate("2011/11/27 12:34:56.790")));
    }

    public void test_isGreaterThanAll() throws Exception {
        assertTrue(handy("2011/11/27").isGreaterThanAll(toDate("2011/11/24"), toDate("2011/11/26")));
        assertFalse(handy("2011/11/27").isGreaterThanAll(toDate("2011/11/24"), toDate("2011/11/27")));
        assertFalse(handy("2011/11/27").isGreaterThanAll(toDate("2011/11/24"), toDate("2011/11/28")));
        assertFalse(handy("2011/11/27").isGreaterThanAll(toDate("2011/11/27"), toDate("2011/11/29")));
        assertFalse(handy("2011/11/27").isGreaterThanAll(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    public void test_isGreaterThanAny() throws Exception {
        assertTrue(handy("2011/11/27").isGreaterThanAny(toDate("2011/11/24"), toDate("2011/11/26")));
        assertTrue(handy("2011/11/27").isGreaterThanAny(toDate("2011/11/24"), toDate("2011/11/27")));
        assertTrue(handy("2011/11/27").isGreaterThanAny(toDate("2011/11/24"), toDate("2011/11/28")));
        assertFalse(handy("2011/11/27").isGreaterThanAny(toDate("2011/11/27"), toDate("2011/11/29")));
        assertFalse(handy("2011/11/27").isGreaterThanAny(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    public void test_isGreaterEqual() throws Exception {
        assertTrue(handy("2011/11/27").isGreaterEqual(toDate("2011/11/24")));
        assertTrue(handy("2011/11/27").isGreaterEqual(toDate("2011/11/27")));
        assertFalse(handy("2011/11/27").isGreaterEqual(toDate("2011/11/28")));
        assertTrue(handy("2011/11/27 12:34:56.789").isGreaterEqual(toDate("2011/11/27 12:34:56.788")));
        assertTrue(handy("2011/11/27 12:34:56.789").isGreaterEqual(toDate("2011/11/27 12:34:56.789")));
        assertFalse(handy("2011/11/27 12:34:56.789").isGreaterEqual(toDate("2011/11/27 12:34:56.790")));
    }

    public void test_isGreaterEqualAll() throws Exception {
        assertTrue(handy("2011/11/27").isGreaterEqualAll(toDate("2011/11/24"), toDate("2011/11/26")));
        assertTrue(handy("2011/11/27").isGreaterEqualAll(toDate("2011/11/24"), toDate("2011/11/27")));
        assertFalse(handy("2011/11/27").isGreaterEqualAll(toDate("2011/11/24"), toDate("2011/11/28")));
        assertFalse(handy("2011/11/27").isGreaterEqualAll(toDate("2011/11/27"), toDate("2011/11/29")));
        assertFalse(handy("2011/11/27").isGreaterEqualAll(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    public void test_isGreaterEqualAny() throws Exception {
        assertTrue(handy("2011/11/27").isGreaterEqualAny(toDate("2011/11/24"), toDate("2011/11/26")));
        assertTrue(handy("2011/11/27").isGreaterEqualAny(toDate("2011/11/24"), toDate("2011/11/27")));
        assertTrue(handy("2011/11/27").isGreaterEqualAny(toDate("2011/11/24"), toDate("2011/11/28")));
        assertTrue(handy("2011/11/27").isGreaterEqualAny(toDate("2011/11/27"), toDate("2011/11/29")));
        assertFalse(handy("2011/11/27").isGreaterEqualAny(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    // -----------------------------------------------------
    //                                             Less Date
    //                                             ---------
    public void test_isLessThan() throws Exception {
        assertFalse(handy("2011/11/27").isLessThan(toDate("2011/11/24")));
        assertFalse(handy("2011/11/27").isLessThan(toDate("2011/11/27")));
        assertTrue(handy("2011/11/27").isLessThan(toDate("2011/11/28")));
        assertFalse(handy("2011/11/27 12:34:56.789").isLessThan(toDate("2011/11/27 12:34:56.788")));
        assertFalse(handy("2011/11/27 12:34:56.789").isLessThan(toDate("2011/11/27 12:34:56.789")));
        assertTrue(handy("2011/11/27 12:34:56.789").isLessThan(toDate("2011/11/27 12:34:56.790")));
    }

    public void test_isLessThanAll() throws Exception {
        assertFalse(handy("2011/11/27").isLessThanAll(toDate("2011/11/24"), toDate("2011/11/26")));
        assertFalse(handy("2011/11/27").isLessThanAll(toDate("2011/11/24"), toDate("2011/11/27")));
        assertFalse(handy("2011/11/27").isLessThanAll(toDate("2011/11/24"), toDate("2011/11/28")));
        assertFalse(handy("2011/11/27").isLessThanAll(toDate("2011/11/27"), toDate("2011/11/29")));
        assertTrue(handy("2011/11/27").isLessThanAll(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    public void test_isLessThanAny() throws Exception {
        assertFalse(handy("2011/11/27").isLessThanAny(toDate("2011/11/24"), toDate("2011/11/26")));
        assertFalse(handy("2011/11/27").isLessThanAny(toDate("2011/11/24"), toDate("2011/11/27")));
        assertTrue(handy("2011/11/27").isLessThanAny(toDate("2011/11/24"), toDate("2011/11/28")));
        assertTrue(handy("2011/11/27").isLessThanAny(toDate("2011/11/27"), toDate("2011/11/29")));
        assertTrue(handy("2011/11/27").isLessThanAny(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    public void test_isLessEqual() throws Exception {
        assertFalse(handy("2011/11/27").isLessEqual(toDate("2011/11/24")));
        assertTrue(handy("2011/11/27").isLessEqual(toDate("2011/11/27")));
        assertTrue(handy("2011/11/27").isLessEqual(toDate("2011/11/28")));
        assertFalse(handy("2011/11/27 12:34:56.789").isLessEqual(toDate("2011/11/27 12:34:56.788")));
        assertTrue(handy("2011/11/27 12:34:56.789").isLessEqual(toDate("2011/11/27 12:34:56.789")));
        assertTrue(handy("2011/11/27 12:34:56.789").isLessEqual(toDate("2011/11/27 12:34:56.790")));
    }

    public void test_isLessEqualAll() throws Exception {
        assertFalse(handy("2011/11/27").isLessEqualAll(toDate("2011/11/24"), toDate("2011/11/26")));
        assertFalse(handy("2011/11/27").isLessEqualAll(toDate("2011/11/24"), toDate("2011/11/27")));
        assertFalse(handy("2011/11/27").isLessEqualAll(toDate("2011/11/24"), toDate("2011/11/28")));
        assertTrue(handy("2011/11/27").isLessEqualAll(toDate("2011/11/27"), toDate("2011/11/29")));
        assertTrue(handy("2011/11/27").isLessEqualAll(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    public void test_isLessEqualAny() throws Exception {
        assertFalse(handy("2011/11/27").isLessEqualAny(toDate("2011/11/24"), toDate("2011/11/26")));
        assertTrue(handy("2011/11/27").isLessEqualAny(toDate("2011/11/24"), toDate("2011/11/27")));
        assertTrue(handy("2011/11/27").isLessEqualAny(toDate("2011/11/24"), toDate("2011/11/28")));
        assertTrue(handy("2011/11/27").isLessEqualAny(toDate("2011/11/27"), toDate("2011/11/29")));
        assertTrue(handy("2011/11/27").isLessEqualAny(toDate("2011/11/28"), toDate("2011/11/29")));
    }

    // ===================================================================================
    //                                                                       Confirm Parts
    //                                                                       =============
    // -----------------------------------------------------
    //                                          Confirm Year
    //                                          ------------
    public void test_isYear_basic() throws Exception {
        assertTrue(handy("2011/01/01 00:00:00").isYear(2011));
        assertTrue(handy("BC2011/01/01 00:00:00").isYear(-2011));
        assertTrue(handy("AD2011/01/01 00:00:00").isYear_AnnoDomini());
        assertTrue(handy("BC2011/01/01 00:00:00").isYear_BeforeChrist());
    }

    // -----------------------------------------------------
    //                                         Confirm Month
    //                                         -------------
    public void test_isMonth_basic() throws Exception {
        assertTrue(handy("2011/01/01 00:00:00").isMonth(1));
        assertTrue(handy("2011/02/01 00:00:00").isMonth(2));
        assertTrue(handy("2011/03/01 00:00:00").isMonth(3));
    }

    // -----------------------------------------------------
    //                                           Confirm Day
    //                                           -----------
    public void test_isDay_basic() throws Exception {
        assertTrue(handy("2011/01/01 00:00:00").isDay(1));
        assertTrue(handy("2011/02/20 00:00:00").isDay(20));
        assertTrue(handy("2011/03/30 00:00:00").isDay(30));
        assertTrue(handy("2011/03/01 00:00:00").isDay_MonthFirstDay());
        assertFalse(handy("2011/03/14 00:00:00").isDay_MonthFirstDay());
        assertFalse(handy("2011/03/30 00:00:00").isDay_MonthFirstDay());
        assertFalse(handy("2011/03/31 00:00:00").isDay_MonthFirstDay());
        assertFalse(handy("2011/03/01 00:00:00").isDay_MonthLastDay());
        assertFalse(handy("2011/03/14 00:00:00").isDay_MonthLastDay());
        assertFalse(handy("2011/03/30 00:00:00").isDay_MonthLastDay());
        assertTrue(handy("2011/03/31 00:00:00").isDay_MonthLastDay());
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public void test_deepCopy_basic() throws Exception {
        // ## Arrange ##
        String pattern = "yyyy/MM/dd";
        HandyDate date = handy("2011/01/01");
        date.beginMonth_Day(10);

        // ## Act ##
        HandyDate copy = date.deepCopy();

        // ## Assert ##
        copy.addDay(1);
        assertEquals("2011/01/02", copy.toDisp(pattern));
        assertEquals("2011/01/01", date.toDisp(pattern));

        copy.moveToMonthJust();
        assertEquals("2011/01/10", copy.toDisp(pattern));
        assertEquals("2011/01/01", date.toDisp(pattern));
        date.moveToMonthJust();
        assertEquals("2011/01/10", date.toDisp(pattern));
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
