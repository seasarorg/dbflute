package org.seasar.dbflute.exception;

public class DfRequiredPropertyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfRequiredPropertyNotFoundException(String msg) {
        super(msg);
    }

    public DfRequiredPropertyNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
