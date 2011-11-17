package org.seasar.dbflute.helper;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 */
public class HandyDateTest extends PlainTestCase {

    public void test_add_basic() throws Exception {
        // ## Arrange ##
        HandyDate date = new HandyDate("2011/11/17 12:34:56.789");

        // ## Act ##
        date.addYear(1).addMonth(1).addDay(1).addHour(1).addMinute(1).addSecond(1).addMillisecond(1);

        // ## Assert ##
        assertEquals("2012/12/18 13:35:57.790", toString(date.getDate(), "yyyy/MM/dd HH:mm:ss.SSS"));
    }
}
