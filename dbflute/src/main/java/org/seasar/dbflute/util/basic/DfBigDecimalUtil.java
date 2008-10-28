package org.seasar.dbflute.util.basic;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfBigDecimalUtil {

    public static BigDecimal toBigDecimal(Object o) {
        return toBigDecimal(o, null);
    }

    public static BigDecimal toBigDecimal(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new BigDecimal(new SimpleDateFormat(pattern).format(o));
            }
            return new BigDecimal(Long.toString(((java.util.Date) o).getTime()));
        } else if (o instanceof String) {
            String s = (String) o;
            if (DfStringUtil.isEmpty(s)) {
                return null;
            }
            return normalize(new BigDecimal(s));
        } else {
            return normalize(new BigDecimal(o.toString()));
        }
    }

    public static BigDecimal normalize(final BigDecimal dec) {
        return new BigDecimal(dec.toPlainString());
    }

    public static String toString(BigDecimal dec) {
        return dec.toPlainString();
    }

    public interface BigDecimalNormalizer {

        BigDecimal normalize(BigDecimal dec);

        String toString(BigDecimal dec);
    }

    public static class DefaultNormalizer implements BigDecimalNormalizer {
        public BigDecimal normalize(final BigDecimal dec) {
            return dec;
        }

        public String toString(final BigDecimal dec) {
            return dec.toString();
        }
    }
}