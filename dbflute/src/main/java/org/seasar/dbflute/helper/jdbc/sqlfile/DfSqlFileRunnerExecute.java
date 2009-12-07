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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.SQLFailureException;
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
    //                                                                           Attribute
    //                                                                           =========
    protected DfSqlFileRunnerDispatcher _dispatcher;

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
     * @param sql SQL. (NotNull)
     */
    protected void execSQL(String sql) {
        boolean lazyConnectFailed = false;
        try {
            final boolean dispatched = dispatch(sql);
            if (!dispatched) {
                try {
                    lazyConnectIfNeeds();
                } catch (SQLException e) {
                    lazyConnectFailed = true;
                    throw e;
                }
                checkStatement(sql);
                _currentStatement.execute(sql);
            }
            _goodSqlCount++;
        } catch (SQLException e) {
            if (!lazyConnectFailed && _runInfo.isErrorContinue()) {
                showContinueWarnLog(e, sql);
                _result.addErrorContinuedSql(e, sql);
                return;
            }
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "It failed to execute the SQL!" + ln();
            msg = msg + ln();
            msg = msg + "[SQL File]" + ln() + _sqlFile + ln();
            msg = msg + ln();
            msg = msg + "[Executed SQL]" + ln() + sql + ln();
            msg = msg + ln();
            msg = msg + "[SQLState]" + ln() + e.getSQLState() + ln();
            msg = msg + ln();
            msg = msg + "[ErrorCode]" + ln() + e.getErrorCode() + ln();
            msg = msg + ln();
            msg = msg + "[SQLException]" + ln() + e.getClass().getName() + ln();
            msg = msg + extractMessage(e) + ln();
            SQLException nextEx = e.getNextException();
            if (nextEx != null) {
                msg = msg + ln();
                msg = msg + "[NextException]" + ln();
                msg = msg + nextEx.getClass().getName() + ln();
                msg = msg + extractMessage(nextEx) + ln();
                SQLException nextNextEx = nextEx.getNextException();
                if (nextNextEx != null) {
                    msg = msg + ln();
                    msg = msg + "[NextNextException]" + ln();
                    msg = msg + nextNextEx.getClass().getName() + ln();
                    msg = msg + extractMessage(nextNextEx) + ln();
                }
            }
            msg = msg + "* * * * * * * * * */";
            throw new SQLFailureException(msg, e);
        }
    }

    protected boolean dispatch(String sql) throws SQLException {
        return _dispatcher != null && _dispatcher.dispatch(_sqlFile, _currentStatement, sql);
    }

    protected void lazyConnectIfNeeds() throws SQLException {
        // override if it needs
    }

    protected void showContinueWarnLog(SQLException e, String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append("*Failure: ").append(e.getClass().getName()).append(ln());
        sb.append("/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        sb.append("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        sb.append(ln());
        sb.append(sql).append(ln());
        e.getSQLState();
        sb.append("- - - - - - - - - -").append(ln());
        sb.append(extractMessage(e)).append(ln());
        buildAdditionalErrorInfo(sb, e).append(ln());
        SQLException nextEx = e.getNextException();
        if (nextEx != null) {
            sb.append("- - - - - - - - - -").append(ln());
            sb.append(extractMessage(nextEx)).append(ln());
            buildAdditionalErrorInfo(sb, nextEx).append(ln());
            SQLException nextNextEx = nextEx.getNextException();
            if (nextNextEx != null) {
                sb.append("- - - - - - - - - -").append(ln());
                sb.append(extractMessage(nextNextEx)).append(ln());
                buildAdditionalErrorInfo(sb, nextNextEx).append(ln());
            }
        }
        sb.append("nnnnnnnnnnnnnnnnnnnn/").append(ln());
        _log.warn(sb.toString());
    }

    protected String extractMessage(SQLException e) {
        String message = e.getMessage();

        // Because a message of Oracle contains a line separator.
        return message != null ? message.trim() : message;
    }

    protected StringBuilder buildAdditionalErrorInfo(StringBuilder sb, SQLException e) {
        sb.append("(SQLState=").append(e.getSQLState()).append(" ErrorCode=").append(e.getErrorCode()).append(")");
        return sb;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfSqlFileRunnerDispatcher getDispatcher() {
        return _dispatcher;
    }

    public void setDispatcher(DfSqlFileRunnerDispatcher dispatcher) {
        this._dispatcher = dispatcher;
    }
}
