package org.seasar.dbflute.helper.dataset.types;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface DtsColumnType {

    Object convert(Object value, String formatPattern);

    boolean equals(Object arg1, Object arg2);

    Class<?> getType();
}