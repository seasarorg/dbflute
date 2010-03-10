package org.seasar.dbflute.helper.dataset.types;

import java.util.Arrays;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsBinaryType extends DfDtsObjectType {

    private static final Class<?> TYPE = new byte[0].getClass();

    public DfDtsBinaryType() {
    }

    public Object convert(Object value, String formatPattern) {
        if (value != null && value instanceof String) {
            return DfTypeUtil.toStringBytes((String) value, "UTF-8");
        }
        return value;
    }

    protected boolean doEquals(Object arg1, Object arg2) {
        if (arg1 instanceof byte[] && arg2 instanceof byte[]) {
            return Arrays.equals((byte[]) arg1, (byte[]) arg2);
        }
        return false;
    }

    public Class<?> getType() {
        return TYPE;
    }
}