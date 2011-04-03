package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyAssertionFailureCountNotExistsException extends DfTakeFinallyAssertionFailureException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyAssertionFailureCountNotExistsException(String msg) {
        super(msg);
    }

    public DfTakeFinallyAssertionFailureCountNotExistsException(String msg, Throwable e) {
        super(msg, e);
    }
}
