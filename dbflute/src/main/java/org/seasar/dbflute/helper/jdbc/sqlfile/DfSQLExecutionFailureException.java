package org.seasar.dbflute.helper.jdbc.sqlfile;

/**
 * @author jflute
 * @since 0.7.7 (2008/07/19 Saturday)
 */
public class DfSQLExecutionFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfSQLExecutionFailureException(String msg, Throwable t) {
        super(msg, t);
    }
}
