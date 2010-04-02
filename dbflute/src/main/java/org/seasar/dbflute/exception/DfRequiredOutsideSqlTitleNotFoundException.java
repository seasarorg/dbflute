package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfRequiredOutsideSqlTitleNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfRequiredOutsideSqlTitleNotFoundException(String msg) {
        super(msg);
    }

    public DfRequiredOutsideSqlTitleNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
