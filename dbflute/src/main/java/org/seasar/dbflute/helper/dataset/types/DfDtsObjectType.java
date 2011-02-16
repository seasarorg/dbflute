package org.seasar.dbflute.helper.dataset.types;

/**
 * The object type for data set. {Refers to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsObjectType implements DfDtsColumnType {

    public DfDtsObjectType() {
    }

    public Object convert(Object value, String formatPattern) {
        return value;
    }

    public boolean equals(Object arg1, Object arg2) {
        if (arg1 == null) {
            return arg2 == null;
        }
        return doEquals(arg1, arg2);
    }

    @SuppressWarnings("unchecked")
    protected boolean doEquals(Object arg1, Object arg2) {
        try {
            arg1 = convert(arg1, null);
        } catch (Throwable t) {
            return false;
        }
        try {
            arg2 = convert(arg2, null);
        } catch (Throwable t) {
            return false;
        }
        if ((arg1 instanceof Comparable) && (arg2 instanceof Comparable)) {
            return ((Comparable<Object>) arg1).compareTo((Comparable<Object>) arg2) == 0;
        }
        return arg1.equals(arg2);
    }

    public Class<?> getType() {
        return Object.class;
    }
}