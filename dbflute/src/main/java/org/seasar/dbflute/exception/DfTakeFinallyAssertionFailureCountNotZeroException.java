package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfTakeFinallyAssertionFailureCountNotZeroException extends DfTakeFinallyAssertionFailureException {

    private static final long serialVersionUID = 1L;

    public DfTakeFinallyAssertionFailureCountNotZeroException(String msg) {
        super(msg);
    }

    public DfTakeFinallyAssertionFailureCountNotZeroException(String msg, Throwable e) {
        super(msg, e);
    }
}
