package org.seasar.dbflute.exception;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/05/10 Tuesday)
 */
public class DfClassificationRequiredAttributeNotFoundException extends DfIllegalPropertyTypeException {

    private static final long serialVersionUID = 1L;

    public DfClassificationRequiredAttributeNotFoundException(String msg) {
        super(msg);
    }

    public DfClassificationRequiredAttributeNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
