package org.seasar.dbflute.exception;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/05/10 Tuesday)
 */
public class DfMapStringFileReadFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfMapStringFileReadFailureException(String msg) {
        super(msg);
    }

    public DfMapStringFileReadFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
