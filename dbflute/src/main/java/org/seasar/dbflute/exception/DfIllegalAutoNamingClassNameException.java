package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfIllegalAutoNamingClassNameException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfIllegalAutoNamingClassNameException(String msg) {
        super(msg);
    }

    public DfIllegalAutoNamingClassNameException(String msg, Throwable e) {
        super(msg, e);
    }
}
