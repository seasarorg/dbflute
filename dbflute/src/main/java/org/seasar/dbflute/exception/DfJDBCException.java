package org.seasar.dbflute.exception;

import java.sql.SQLException;

/**
 * @author jflute
 */
public class DfJDBCException extends SQLException {

    private static final long serialVersionUID = 1L;

    public static final int NULL_ERROR_CODE = Integer.MIN_VALUE;

    public DfJDBCException(String msg) {
        super(msg, null, NULL_ERROR_CODE);
    }

    public DfJDBCException(String msg, SQLException e) {
        super(msg, e.getSQLState(), e.getErrorCode());
        setNextException(e);
    }
}
