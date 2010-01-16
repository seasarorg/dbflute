package org.seasar.dbflute.exception;

public class DfIllegalPropertyException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfIllegalPropertyException(String msg) {
        super(msg);
    }

    public DfIllegalPropertyException(String msg, Throwable e) {
        super(msg, e);
    }
}
