package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfDBFluteTaskFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfDBFluteTaskFailureException(String msg) {
        super(msg);
    }

    public DfDBFluteTaskFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
