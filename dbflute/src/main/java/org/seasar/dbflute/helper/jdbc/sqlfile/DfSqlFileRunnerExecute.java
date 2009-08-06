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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfAssertionFailureCountNotExistsException;
import org.seasar.dbflute.exception.DfAssertionFailureCountNotZeroException;
import org.seasar.dbflute.exception.DfAssertionFailureListNotExistsException;
import org.seasar.dbflute.exception.DfAssertionFailureListNotZeroException;
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
            if (isValidAssertSql()) {
                if (isAssertCountZero(sql)) {
                    assertCountZero(statement, sql);
                } else if (isAssertCountExists(sql)) {
                    assertCountExists(statement, sql);
                } else if (isAssertListZero(sql)) {
                    assertListZero(statement, sql);
                } else if (isAssertListExists(sql)) {
                    assertListExists(statement, sql);
                } else {
                    statement.execute(sql);
                }
            } else {
                statement.execute(sql);
            }
            _goodSqlCount++;
        } catch (SQLException e) {
            if (_runInfo.isErrorContinue()) {
                showContinueWarnLog(e, sql);
                _result.addErrorContinuedSql(e, sql);
                return;
            }
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "It failed to execute the SQL!" + ln();
            msg = msg + ln();
            msg = msg + "[SQL File]" + ln() + _srcFile + ln();
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
            throw new DfSQLExecutionFailureException(msg, e);
        }
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

    protected boolean isValidAssertSql() {
        return false; // as default!
    }

    // ===================================================================================
    //                                                                Assert Determination
    //                                                                ====================
    protected boolean isAssertCountZero(String sql) {
        return sql.contains("--") && sql.contains("#df:assertCountZero#");
    }

    protected boolean isAssertCountExists(String sql) {
        return sql.contains("--") && sql.contains("#df:assertCountExists#");
    }

    protected boolean isAssertListZero(String sql) {
        return sql.contains("--") && sql.contains("#df:assertListZero#");
    }

    protected boolean isAssertListExists(String sql) {
        return sql.contains("--") && sql.contains("#df:assertListExists#");
    }

    // ===================================================================================
    //                                                                       Assert Result
    //                                                                       =============
    protected void assertCountZero(Statement statement, String sql) throws SQLException {
        assertCount(statement, sql, false);
    }

    protected void assertCountExists(Statement statement, String sql) throws SQLException {
        assertCount(statement, sql, true);
    }

    protected void assertCount(Statement statement, String sql, boolean exists) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            int count = 0;
            while (rs.next()) {// One loop only!
                count = rs.getInt(1);
                break;
            }
            if (exists) {
                if (count == 0) {
                    throwAssertionFailureCountNotExistsException(sql, count);
                } else {
                    String result = "[RESULT]: count=" + count + ln();
                    _log.info(result);
                }
            } else {
                if (count > 0) {
                    throwAssertionFailureCountNotZeroException(sql, count);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    protected void assertListZero(Statement statement, String sql) throws SQLException {
        assertList(statement, sql, false);
    }

    protected void assertListExists(Statement statement, String sql) throws SQLException {
        assertList(statement, sql, true);
    }

    protected void assertList(Statement statement, String sql, boolean exists) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            final ResultSetMetaData metaData = rs.getMetaData();
            final int columnCount = metaData.getColumnCount();
            final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
            int count = 0;
            while (rs.next()) { // One loop only!
                Map<String, String> recordMap = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columnCount; i++) {
                    recordMap.put(metaData.getColumnName(i), rs.getString(i));
                }
                resultList.add(recordMap);
                ++count;
            }
            if (exists) {
                if (count == 0) {
                    throwAssertionFailureListNotExistsException(sql, count, resultList);
                } else {
                    String result = "[RESULT]: count=" + count + ln();
                    for (Map<String, String> recordMap : resultList) {
                        result = result + recordMap + ln();
                    }
                    _log.info(result);
                }
            } else {
                if (count > 0) {
                    throwAssertionFailureListNotZeroException(sql, count, resultList);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    protected void throwAssertionFailureCountNotZeroException(String sql, int resultCount) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects ZERO but the result is NOT ZERO!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + _srcFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfAssertionFailureCountNotZeroException(msg);
    }

    protected void throwAssertionFailureCountNotExistsException(String sql, int resultCount) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects EXISTS but the result is NOT EXISTS!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + _srcFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfAssertionFailureCountNotExistsException(msg);
    }

    protected void throwAssertionFailureListNotZeroException(String sql, int resultCount,
            List<Map<String, String>> resultList) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects ZERO but the result is NOT ZERO!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + _srcFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + ln();
        msg = msg + "[Result List]" + ln();
        for (Map<String, String> recordMap : resultList) {
            msg = msg + recordMap + ln();
        }
        msg = msg + "* * * * * * * * * */";
        throw new DfAssertionFailureListNotZeroException(msg);
    }

    protected void throwAssertionFailureListNotExistsException(String sql, int resultCount,
            List<Map<String, String>> resultList) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects EXISTS but the result is NOT EXISTS!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + _srcFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + ln();
        msg = msg + "[Result List]" + ln();
        for (Map<String, String> recordMap : resultList) {
            msg = msg + recordMap + ln();
        }
        msg = msg + "* * * * * * * * * */";
        throw new DfAssertionFailureListNotExistsException(msg);
    }
}
