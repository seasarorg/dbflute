package org.apache.torque.helper.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SqlFileRunnerExecute extends SqlFileRunnerBase {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(SqlFileRunnerExecute.class);

    public SqlFileRunnerExecute(RunnerInformation runInfo) {
        super(runInfo);
    }

    /**
     * Exec the sql statement.
     *
     * @param statement
     * @param sql
     */
    protected void execSQL(Statement statement, String sql) {
        try {
            statement.execute(sql);
            _goodSqlCount++;
        } catch (SQLException e) {
            String msg = "Failed to execute: " + sql;
            if (!_runInfo.isErrorContinue()) {
                throw new SQLRuntimeException(msg, e);
            }
            _log.warn(msg, e);
            _log.warn("" + System.getProperty("line.separator"));
        }
    }

    // =========================================================================================
    //                                                                         Runtime Exception
    //                                                                         =================
    public static class SQLRuntimeException extends RuntimeException {
        public static final long serialVersionUID = -1;

        public SQLRuntimeException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    public static class IORuntimeException extends RuntimeException {
        public static final long serialVersionUID = -1;

        public IORuntimeException(String msg, Throwable e) {
            super(msg, e);
        }
    }
}
