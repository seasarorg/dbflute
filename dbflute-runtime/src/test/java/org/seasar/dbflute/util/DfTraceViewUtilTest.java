package org.seasar.dbflute.util;

import java.sql.Timestamp;
import java.util.Date;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/20 Saturday)
 */
public class DfTraceViewUtilTest extends PlainTestCase {

    public void test_convertToPerformanceView_date_basic() throws Exception {
        // ## Arrange & Act ##
        Date before = currentDate();
        Date after = new Timestamp(currentDate().getTime() + 10000L);
        String view = DfTraceViewUtil.convertToPerformanceView(before, after);

        // ## Assert ##
        log(view);
        assertEquals("00m10s000ms", view);
    }

    public void test_convertToPerformanceView_date_min() throws Exception {
        // ## Arrange & Act ##
        Date before = currentDate();
        Date after = new Timestamp(currentDate().getTime() + 100012L);
        String view = DfTraceViewUtil.convertToPerformanceView(before, after);

        // ## Assert ##
        log(view);
        assertEquals("01m40s012ms", view);
    }

    public void test_convertToPerformanceView_millis_basic() throws Exception {
        // ## Arrange & Act ##
        String view = DfTraceViewUtil.convertToPerformanceView(10000L);

        // ## Assert ##
        log(view);
        assertEquals("00m10s000ms", view);
    }

    public void test_convertToPerformanceView_millis_min() throws Exception {
        // ## Arrange & Act ##
        String view = DfTraceViewUtil.convertToPerformanceView(100012L);

        // ## Assert ##
        log(view);
        assertEquals("01m40s012ms", view);
    }

    public void test_convertToPerformanceView_millis_hour() throws Exception {
        // ## Arrange & Act ##
        String view = DfTraceViewUtil.convertToPerformanceView(10000000L);

        // ## Assert ##
        log(view);
        assertEquals("166m40s000ms", view);
    }

    public void test_convertToPerformanceView_millis_various() throws Exception {
        assertEquals("00m00s001ms", DfTraceViewUtil.convertToPerformanceView(1L));
        assertEquals("00m01s000ms", DfTraceViewUtil.convertToPerformanceView(1000L));
        assertEquals("01m00s000ms", DfTraceViewUtil.convertToPerformanceView(60000L));
        assertEquals("100m00s000ms", DfTraceViewUtil.convertToPerformanceView(6000000L));
        assertEquals("166m47s789ms", DfTraceViewUtil.convertToPerformanceView(10007789L));
        assertEquals("1666m40s000ms", DfTraceViewUtil.convertToPerformanceView(100000000L));
        assertEquals("16666m40s000ms", DfTraceViewUtil.convertToPerformanceView(1000000000L));
    }
}
