package org.seasar.dbflute.helper.dataset.types;

import java.math.BigDecimal;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsBigDecimalType extends DfDtsObjectType {

    public DfDtsBigDecimalType() {
    }

    public Object convert(Object value, String formatPattern) {
        return DfTypeUtil.toBigDecimal(value, formatPattern);
    }

    public Class<?> getType() {
        return BigDecimal.class;
    }
}