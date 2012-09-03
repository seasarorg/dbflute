package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfOutsideSqlTestFailureFoundException extends DfIllegalPropertySettingException {

    private static final long serialVersionUID = 1L;

    public DfOutsideSqlTestFailureFoundException(String msg) {
        super(msg);
    }

    public DfOutsideSqlTestFailureFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
