package org.seasar.dbflute.util.basic;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.seasar.framework.util.StringUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfBigDecimalUtil {

    protected static final String TIGER_NORMALIZER_CLASS_NAME = "org.seasar.framework.util.TigerBigDecimalConversion";

    protected static BigDecimalNormalizer normalizer = new DefaultNormalizer();
    static {
        try {
            final Class<?> clazz = Class.forName(TIGER_NORMALIZER_CLASS_NAME);
            normalizer = (BigDecimalNormalizer) clazz.newInstance();
        } catch (Exception ignore) {
        }
    }

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
            if (StringUtil.isEmpty(s)) {
                return null;
            }
            return normalizer.normalize(new BigDecimal(s));
        } else {
            return normalizer.normalize(new BigDecimal(o.toString()));
        }
    }

    public static String toString(BigDecimal dec) {
        return normalizer.toString(dec);
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