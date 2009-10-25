package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfBehaviorNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfBehaviorNotFoundException(String msg) {
        super(msg);
    }

    public DfBehaviorNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
