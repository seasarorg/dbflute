package org.seasar.dbflute.exception;

import java.sql.SQLException;

/**
 * @author jflute
 */
public class DfAlterCheckAlterScriptSQLException extends SQLException {

    private static final long serialVersionUID = 1L;

    public DfAlterCheckAlterScriptSQLException(String msg) {
        super(msg);
    }
}
