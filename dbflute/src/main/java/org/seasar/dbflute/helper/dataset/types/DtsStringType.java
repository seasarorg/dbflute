package org.seasar.dbflute.helper.dataset.types;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The string type for data set. {Refers to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DtsStringType extends DtsObjectType {

    protected boolean trim;

    public DtsStringType() {
        this(true);
    }

    public DtsStringType(final boolean trim) {
        this.trim = trim;
    }

    public Object convert(Object value, String formatPattern) {
        String s = DfTypeUtil.toString(value, formatPattern);
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