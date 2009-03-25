package org.seasar.dbflute.helper.dataset.types;

import java.sql.Timestamp;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class TimestampType extends ObjectType {

    public TimestampType() {
    }

    public Object convert(Object value, String formatPattern) {
        return DfTypeUtil.toTimestamp(value, formatPattern);
    }

    public Class<?> getType() {
        return Timestamp.class;
    }
}