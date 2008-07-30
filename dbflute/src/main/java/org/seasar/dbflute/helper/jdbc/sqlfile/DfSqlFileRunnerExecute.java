/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;

/**
 * @author jflute
 */
public class DfSqlFileRunnerExecute extends DfSqlFileRunnerBase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileRunnerExecute.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlFileRunnerExecute(DfRunnerInformation runInfo, DataSource dataSource) {
        super(runInfo, dataSource);
    }

    // ===================================================================================
    //                                                                         Execute SQL
    //                                                                         ===========
    /**
     * Execute the SQL statement.
     * @param statement Statement. (NotNull)
     * @param sql SQL. (NotNull)
     */
    protected void execSQL(Statement statement, String sql) {
        try {
            if (isValidAssertSql() && isAssertCountNotZero(sql)) {
                assertCountNotZero(statement, sql);
            } else {
                statement.execute(sql);
            }
            _goodSqlCount++;
        } catch (SQLException e) {
            if (!_runInfo.isErrorContinue()) {
                String msg = "Look! Read the message below." + getLineSeparator();
                msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
                msg = msg + "It failed to execute the SQL!" + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[SQL File]" + getLineSeparator() + _srcFile + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[Executed SQL]" + getLineSeparator() + sql + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[SQLState]" + getLineSeparator() + e.getSQLState() + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[ErrorCode]" + getLineSeparator() + e.getErrorCode() + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[SQLException]" + getLineSeparator() + e.getClass().getName() + getLineSeparator();
                msg = msg + e.getMessage() + getLineSeparator();
                SQLException nextException = e.getNextException();
                if (nextException != null) {
                    msg = msg + getLineSeparator();
                    msg = msg + "[NextException]" + getLineSeparator() + nextException.getClass().getName()
                            + getLineSeparator();
                    msg = msg + nextException.getMessage() + getLineSeparator();
                }
                msg = msg + "* * * * * * * * * */";
                throw new DfSQLExecutionFailureException(msg, e);
            }
            _log.warn("Failed to execute: " + sql, e);
            _log.warn("" + System.getProperty("line.separator"));
        }
    }

    protected boolean isValidAssertSql() {
        return false;// as default!
    }

    protected boolean isAssertCountNotZero(String sql) {
        return sql.contains("--") && sql.contains("#df:assertCountNotZero#");
    }

    protected void assertCountNotZero(Statement statement, String sql) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            int count = 0;
            while (rs.next()) {// One loop only!
                count = rs.getInt(1);
                break;
            }
            if (count == 0) {
                throwAssertionFailureCountZeroException(sql);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    protected void throwAssertionFailureCountZeroException(String sql) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The SQL of 'select count' returned ZERO!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your test data!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[SQL File]" + getLineSeparator() + _srcFile + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Executed SQL]" + getLineSeparator() + sql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new DfAssertionFailureCountZeroException(msg);
    }

    public static class DfAssertionFailureCountZeroException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DfAssertionFailureCountZeroException(String msg) {
            super(msg);
        }
    }
}
