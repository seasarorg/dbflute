package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;

public class DfSqlFileRunnerExecute extends DfSqlFileRunnerBase {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileRunnerExecute.class);

    public DfSqlFileRunnerExecute(DfRunnerInformation runInfo, DataSource dataSource) {
        super(runInfo, dataSource);
    }

    /**
     * Execute the SQL statement.
     * @param statement Statement. (NotNull)
     * @param sql SQL. (NotNull)
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
