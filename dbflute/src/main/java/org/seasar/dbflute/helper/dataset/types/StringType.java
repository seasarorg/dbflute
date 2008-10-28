package org.seasar.dbflute.helper.dataset.types;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class StringType extends ObjectType {

    protected boolean trim;

    public StringType() {
        this(true);
    }

    public StringType(final boolean trim) {
        this.trim = trim;
    }

    public Object convert(Object value, String formatPattern) {
        String s = DfStringUtil.toString(value, formatPattern);
        if (s != null && trim) {
            s = s.trim();
        }
        if ("".equals(s)) {
            s = null;
        }
        return s;
    }

    public Class<?> getType() {
        return String.class;
    }
}