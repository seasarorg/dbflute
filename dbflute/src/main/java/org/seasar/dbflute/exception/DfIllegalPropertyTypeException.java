package org.seasar.dbflute.exception;

public class DfIllegalPropertyTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfIllegalPropertyTypeException(String msg) {
        super(msg);
    }

    public DfIllegalPropertyTypeException(String msg, Throwable e) {
        super(msg, e);
    }
}
