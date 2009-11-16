package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfParameterBeanReferenceColumnNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfParameterBeanReferenceColumnNotFoundException(String msg) {
        super(msg);
    }

    public DfParameterBeanReferenceColumnNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
