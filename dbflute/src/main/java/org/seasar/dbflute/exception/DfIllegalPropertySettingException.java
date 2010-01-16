package org.seasar.dbflute.exception;

public class DfIllegalPropertySettingException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DfIllegalPropertySettingException(String msg) {
        super(msg);
    }

    public DfIllegalPropertySettingException(String msg, Throwable e) {
        super(msg, e);
    }
}
