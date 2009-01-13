package org.dbflute.exception;

import java.sql.SQLException;

/**
 * The exception of when the SQL failed to execute.
 * @author DBFlute(AutoGenerator)
 */
public class SQLFailureException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    protected SQLException sqlEx;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     * @param cause SQLException. (NotNull)
     */
    public SQLFailureException(String msg, SQLException cause) {
        super(msg, cause);
        sqlEx = cause;
    }
	
    public SQLException getSQLException() {
        return sqlEx;
    }
}
