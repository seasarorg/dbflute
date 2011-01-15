package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfDelimiterDataTableNotFoundException extends DfTableNotFoundException {

    private static final long serialVersionUID = 1L;

    public DfDelimiterDataTableNotFoundException(String msg) {
        super(msg);
    }

    public DfDelimiterDataTableNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
