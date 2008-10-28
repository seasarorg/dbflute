package org.seasar.dbflute.util.basic;


/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfBinaryUtil {

    public static byte[] toBinary(Object o) {
        if (o instanceof byte[]) {
            return (byte[]) o;
        } else if (o == null) {
            return null;
        } else {
            if (o instanceof String) {
                return ((String) o).getBytes();
            }
            throw new IllegalArgumentException(o.getClass().toString());
        }
    }
}