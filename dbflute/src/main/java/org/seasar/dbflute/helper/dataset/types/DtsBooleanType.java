package org.seasar.dbflute.helper.dataset.types;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DtsBooleanType extends DtsObjectType {

    public DtsBooleanType() {
    }

    public Object convert(Object value, String formatPattern) {
        return DfTypeUtil.toBoolean(value);
    }

    public Class<?> getType() {
        return Boolean.class;
    }
}