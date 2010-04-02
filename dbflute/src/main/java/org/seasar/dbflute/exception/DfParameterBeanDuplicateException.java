package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfParameterBeanDuplicateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfParameterBeanDuplicateException(String msg) {
        super(msg);
    }

    public DfParameterBeanDuplicateException(String msg, Throwable e) {
        super(msg, e);
    }
}
