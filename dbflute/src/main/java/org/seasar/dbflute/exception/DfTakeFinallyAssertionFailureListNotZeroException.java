package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyAssertionFailureListNotZeroException extends DfTakeFinallyAssertionFailureException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyAssertionFailureListNotZeroException(String msg) {
        super(msg);
    }

    public DfTakeFinallyAssertionFailureListNotZeroException(String msg, Throwable e) {
        super(msg, e);
    }
}
