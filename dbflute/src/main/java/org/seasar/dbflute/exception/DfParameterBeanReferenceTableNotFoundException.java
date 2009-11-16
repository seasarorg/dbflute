package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfParameterBeanReferenceTableNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfParameterBeanReferenceTableNotFoundException(String msg) {
        super(msg);
    }

    public DfParameterBeanReferenceTableNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
