package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfDBFluteTaskCancelledException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfDBFluteTaskCancelledException(String msg) {
        super(msg);
    }

    public DfDBFluteTaskCancelledException(String msg, Throwable e) {
        super(msg, e);
    }
}
