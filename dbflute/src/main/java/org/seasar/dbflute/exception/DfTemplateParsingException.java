package org.seasar.dbflute.exception;

public class DfTemplateParsingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfTemplateParsingException(String msg, Throwable e) {
        super(msg, e);
    }
}
