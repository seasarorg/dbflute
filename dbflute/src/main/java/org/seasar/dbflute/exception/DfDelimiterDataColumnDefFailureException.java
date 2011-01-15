package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfDelimiterDataColumnDefFailureException extends DfTableNotFoundException {

    private static final long serialVersionUID = 1L;

    public DfDelimiterDataColumnDefFailureException(String msg) {
        super(msg);
    }

    public DfDelimiterDataColumnDefFailureException(String msg, Throwable e) {
        super(msg, e);
    }
}
