package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyAssertionFailureListNotExistsException extends DfTakeFinallyAssertionFailureException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyAssertionFailureListNotExistsException(String msg) {
        super(msg);
    }

    public DfTakeFinallyAssertionFailureListNotExistsException(String msg, Throwable e) {
        super(msg, e);
    }
}
