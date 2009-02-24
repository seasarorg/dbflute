package org.seasar.dbflute.exception;

public class IllegalPropertyTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public IllegalPropertyTypeException(String msg) {
        super(msg);
    }

    public IllegalPropertyTypeException(String msg, Throwable e) {
        super(msg, e);
    }
}
