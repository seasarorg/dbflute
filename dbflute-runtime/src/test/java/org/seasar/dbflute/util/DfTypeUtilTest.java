package org.seasar.dbflute.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

/**
 * @author jflute
 * @since 0.9.0 (2009/01/19 Monday)
 */
public class DfTypeUtilTest extends TestCase {

    public void test_toDateFlexibly() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
    }

    public void test_toTimestampFlexibly() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
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
        
        SimpleDateFormat f6 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSSSS");
        assertEquals("2008/12/30 12:34:56.123456", f6.format(Timestamp.valueOf("2008-12-30 12:34:56.123456")));
    }
}
