package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfDelimiterDataColumnDefNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfDelimiterDataColumnDefNotFoundException(String msg) {
        super(msg);
    }

    public DfDelimiterDataColumnDefNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
