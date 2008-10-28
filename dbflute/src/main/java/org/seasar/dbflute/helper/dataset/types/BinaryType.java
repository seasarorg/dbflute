package org.seasar.dbflute.helper.dataset.types;

import java.util.Arrays;

import org.seasar.dbflute.util.basic.DfBinaryUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class BinaryType extends ObjectType {

    private static final Class<?> TYPE = new byte[0].getClass();

    public BinaryType() {
    }

    public Object convert(Object value, String formatPattern) {
        return DfBinaryUtil.toBinary(value);
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