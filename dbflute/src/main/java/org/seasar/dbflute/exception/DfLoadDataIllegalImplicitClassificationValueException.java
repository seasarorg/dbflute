package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfLoadDataIllegalImplicitClassificationValueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfLoadDataIllegalImplicitClassificationValueException(String msg) {
        super(msg);
    }

    public DfLoadDataIllegalImplicitClassificationValueException(String msg, Throwable e) {
        super(msg, e);
    }
}
