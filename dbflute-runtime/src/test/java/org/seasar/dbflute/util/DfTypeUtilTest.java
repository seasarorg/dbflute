package org.seasar.dbflute.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.0 (2009/01/19 Monday)
 */
public class DfTypeUtilTest extends PlainTestCase {

    // ===================================================================================
    //                                                                          Convert To
    //                                                                          ==========
    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    public void test_toBoolean() {
        // ## Arrange & Act & Assert ##
        assertNull(DfTypeUtil.toBoolean(null));
        assertTrue(DfTypeUtil.toBoolean("true"));
        assertFalse(DfTypeUtil.toBoolean("false"));
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    public void test_toDateFlexibly_sameClass() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date pureDate = new Date(DfTypeUtil.toDateFlexibly("2009-12-13 12:34:56.123").getTime());

        // ## Act ##
        Date date = DfTypeUtil.toDateFlexibly(pureDate);

        // ## Assert ##
        assertEquals(java.util.Date.class, date.getClass());
        assertFalse(date instanceof Timestamp); // because it returns pure date
        assertEquals("2009/12/13 12:34:56", f.format(date));
    }

    public void test_toDateFlexibly_subClass() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf("2009-12-13 12:34:56.123");

        // ## Act ##
        Date date = DfTypeUtil.toDateFlexibly(timestamp);

        // ## Assert ##
        assertEquals(java.util.Date.class, date.getClass());
        assertFalse(date instanceof Timestamp); // because it returns pure date
        assertEquals("2009/12/13 12:34:56", f.format(date));
    }

    public void test_toDateFlexibly() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toDateFlexibly(null));
        assertNull(DfTypeUtil.toDateFlexibly(""));
        assertEquals("0002/01/12 00:00:00", f.format(DfTypeUtil.toDateFlexibly("20112")));
        assertEquals("0012/01/22 00:00:00", f.format(DfTypeUtil.toDateFlexibly("120122")));
        assertEquals("0923/01/27 00:00:00", f.format(DfTypeUtil.toDateFlexibly("9230127")));
        assertEquals("2008/12/30 00:00:00", f.format(DfTypeUtil.toDateFlexibly("20081230")));
        assertEquals("2008/12/30 00:00:00", f.format(DfTypeUtil.toDateFlexibly("2008/12/30")));
        assertEquals("2008/12/30 00:00:00", f.format(DfTypeUtil.toDateFlexibly("2008-12-30")));
        assertEquals("2008/12/30 12:34:56", f.format(DfTypeUtil.toDateFlexibly("2008-12-30 12:34:56")));
        assertEquals("2008/12/30 12:34:56", f.format(DfTypeUtil.toDateFlexibly("2008-12-30 12:34:56.789")));
        assertEquals(java.util.Date.class, DfTypeUtil.toDateFlexibly("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Date.class, DfTypeUtil.toDateFlexibly("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Timestamp.class, DfTypeUtil.toDateFlexibly("2008-12-30 12:34:56.789").getClass());
    }

    public void test_clearSeconds() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDateFlexibly("2008-12-30 12:34:56.789");

        // ## Act ##
        DfTypeUtil.clearSeconds(date);

        // ## Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(date));
    }

    // -----------------------------------------------------
    //                                              SQL Date
    //                                              --------
    public void test_toSqlDate_basic() {
        assertNull(DfTypeUtil.toSqlDate(null));
        assertNull(DfTypeUtil.toSqlDate(""));
    }

    public void test_toSqlDate_same() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act ##
        java.sql.Date date = DfTypeUtil.toSqlDate(DfTypeUtil.toDateFlexibly("2008-12-30 12:34:56.789"));

        // ## Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(date));
    }

    public void test_toSqlDate_timestamp() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Timestamp date = DfTypeUtil.toTimestampFlexibly("2008-12-30 12:34:56.789");

        // ## Act & Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toSqlDate(date)));
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    public void test_toTime() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toTimestampFlexibly("2008-12-30 12:34:56.789");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTime(null));
        assertNull(DfTypeUtil.toTime(""));
        assertEquals("1970/01/01 12:34:56.789", f.format(DfTypeUtil.toTime(date)));
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    public void test_toTimestampFlexibly() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTimestampFlexibly(null));
        assertNull(DfTypeUtil.toTimestampFlexibly(""));
        assertEquals("0002/01/12 00:00:00.000", f.format(DfTypeUtil.toTimestampFlexibly("20112")));
        assertEquals("0012/01/22 00:00:00.000", f.format(DfTypeUtil.toTimestampFlexibly("120122")));
        assertEquals("0923/01/27 00:00:00.000", f.format(DfTypeUtil.toTimestampFlexibly("9230127")));
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toTimestampFlexibly("20081230")));
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toTimestampFlexibly("2008/12/30")));
        assertEquals("2008/12/30 12:34:56.000", f.format(DfTypeUtil.toTimestampFlexibly("2008/12/30 12:34:56")));
        assertEquals("2008/12/30 12:34:56.789", f.format(DfTypeUtil.toTimestampFlexibly("2008/12/30 12:34:56.789")));
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toTimestampFlexibly("2008-12-30")));
        assertEquals("2008/12/30 12:34:56.000", f.format(DfTypeUtil.toTimestampFlexibly("2008-12-30 12:34:56")));
        assertEquals("2008/12/30 12:34:56.789", f.format(DfTypeUtil.toTimestampFlexibly("2008-12-30 12:34:56.789")));
    }

    // ===================================================================================
    //                                                                              Format
    //                                                                              ======
    public void test_format_Date() {
        // ## Arrange ##
        Date date = DfTypeUtil.toDateFlexibly("2008/12/30 12:34:56");
        Timestamp timestamp = DfTypeUtil.toTimestampFlexibly("2008/12/30 12:34:56");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.format((Date) null, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30 12:34:56", DfTypeUtil.format(date, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30", DfTypeUtil.format(date, "yyyy/MM/dd"));
        assertEquals("2008-12-30", DfTypeUtil.format(date, "yyyy-MM-dd"));
        assertEquals("2008-12-30 12:34:56.000", DfTypeUtil.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals("2008/12/30 12:34:56", DfTypeUtil.format(timestamp, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30", DfTypeUtil.format(timestamp, "yyyy/MM/dd"));
        assertEquals("2008-12-30", DfTypeUtil.format(timestamp, "yyyy-MM-dd"));
        assertEquals("2008-12-30 12:34:56.000", DfTypeUtil.format(timestamp, "yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
