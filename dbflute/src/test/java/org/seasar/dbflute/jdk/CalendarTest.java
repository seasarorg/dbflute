package org.seasar.dbflute.jdk;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;


public class CalendarTest {
    private static final Log _log = LogFactory.getLog(CalendarTest.class);
    
    @Test
    public void test_deleteHourMinuteSecond() throws Exception {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        _log.debug("timestamp: " + timestamp);
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        timestamp.setTime(cal.getTimeInMillis());
        
        final Date date = new Date();
        _log.debug("timestamp: " + timestamp);
    }
}
