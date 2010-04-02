package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfRequiredOutsideSqlDescriptionNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfRequiredOutsideSqlDescriptionNotFoundException(String msg) {
        super(msg);
    }

    public DfRequiredOutsideSqlDescriptionNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
