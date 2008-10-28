package org.seasar.dbflute.helper.dataset.types;

import java.math.BigDecimal;

import org.seasar.framework.util.BigDecimalConversionUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class BigDecimalType extends ObjectType {

    public BigDecimalType() {
    }

    public Object convert(Object value, String formatPattern) {
        return BigDecimalConversionUtil.toBigDecimal(value, formatPattern);
    }

    public Class<?> getType() {
        return BigDecimal.class;
    }
}