package org.seasar.dbflute.helper.dataset.types;

import org.seasar.dbflute.util.basic.DfBooleanUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class BooleanType extends ObjectType {

    public BooleanType() {
    }

    public Object convert(Object value, String formatPattern) {
        return DfBooleanUtil.toBoolean(value);
    }

    public Class<?> getType() {
        return Boolean.class;
    }
}