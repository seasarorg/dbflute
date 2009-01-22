package org.seasar.dbflute.exception;

public class TemplateParsingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TemplateParsingException(String msg, Throwable e) {
        super(msg, e);
    }
}
