package org.seasar.dbflute.util.basic;

import java.sql.Timestamp;
import java.util.Date;

/**
 * {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfTimestampUtil {
    
    public static Timestamp toTimestamp(Object o) {
        return toTimestamp(o, null);
    }

    public static Timestamp toTimestamp(Object o, String pattern) {
        if (o instanceof Timestamp)
            return (Timestamp) o;
        Date date = DfDateUtil.toDate(o, pattern);
        if (date != null)
            return new Timestamp(date.getTime());
        else
            return null;
    }
}