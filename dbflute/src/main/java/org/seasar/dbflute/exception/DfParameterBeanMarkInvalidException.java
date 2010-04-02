package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfParameterBeanMarkInvalidException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfParameterBeanMarkInvalidException(String msg) {
        super(msg);
    }

    public DfParameterBeanMarkInvalidException(String msg, Throwable e) {
        super(msg, e);
    }
}
