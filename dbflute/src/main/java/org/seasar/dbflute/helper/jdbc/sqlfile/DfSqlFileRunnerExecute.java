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
                } else if (isAssertListZero(sql)) {
                    assertListZero(statement, sql);
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
            msg = msg + (e.getMessage() != null ? e.getMessage().trim() : null) + ln();
            SQLException nextEx = e.getNextException();
            if (nextEx != null) {
                msg = msg + ln();
                msg = msg + "[NextException]" + ln();
                msg = msg + nextEx.getClass().getName() + ln();
                msg = msg + (nextEx.getMessage() != null ? nextEx.getMessage().trim() : null) + ln();
                SQLException nextNextEx = nextEx.getNextException();
                if (nextNextEx != null) {
                    msg = msg + ln();
                    msg = msg + "[NextNextException]" + ln();
                    msg = msg + nextNextEx.getClass().getName() + ln();
                    msg = msg + (nextNextEx.getMessage() != null ? nextNextEx.getMessage().trim() : null) + ln();
                }
            }
            msg = msg + "* * * * * * * * * */";
            throw new DfSQLExecutionFailureException(msg, e);
        }
    }

    protected void showContinueWarnLog(SQLException e, String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append("*Failed to execute:").append(e.getClass().getName()).append(ln());
        sb.append("/* * * * * * * * * * * * * * * * * * * * * * * * * *").append(ln());
        sb.append(e.getMessage() != null ? e.getMessage().trim() : null).append(ln());
        SQLException nextEx = e.getNextException();
        if (nextEx != null) {
            sb.append("- - - - - - - - ").append(ln());
            sb.append(nextEx.getMessage() != null ? nextEx.getMessage().trim() : null).append(ln());
            SQLException nextNextEx = nextEx.getNextException();
            if (nextNextEx != null) {
                sb.append("- - - - - - - - ").append(ln());
                sb.append(nextNextEx.getMessage() != null ? nextNextEx.getMessage().trim() : null).append(ln());
            }
        }
        sb.append("= = = = = = = =").append(ln());
        sb.append(sql).append(ln());
        sb.append("* * * * * * * * * */");
        _log.warn(sb.toString());
    }

    protected boolean isValidAssertSql() {
        return false;// as default!
    }

    protected boolean isAssertCountZero(String sql) {
        return sql.contains("--") && sql.contains("#df:assertCountZero#");
    }

    protected boolean isAssertListZero(String sql) {
        return sql.contains("--") && sql.contains("#df:assertListZero#");
    }

    protected void assertCountZero(Statement statement, String sql) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            int count = 0;
            while (rs.next()) {// One loop only!
                count = rs.getInt(1);
                break;
            }
            if (count > 0) {
                throwAssertionFailureCountNotZeroException(sql, count);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    protected void assertListZero(Statement statement, String sql) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            final ResultSetMetaData metaData = rs.getMetaData();
            final int columnCount = metaData.getColumnCount();
            final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
            int count = 0;
            while (rs.next()) {// One loop only!
                Map<String, String> recordMap = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columnCount; i++) {
                    recordMap.put(metaData.getColumnName(i), rs.getString(i));
                }
                resultList.add(recordMap);
                ++count;
            }
            if (count > 0) {
                throwAssertionFailureListNotZeroException(sql, count, resultList);
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
        msg = msg + "The SQL of 'select count' returned NOT ZERO!" + ln();
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

    public static class DfAssertionFailureCountNotZeroException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DfAssertionFailureCountNotZeroException(String msg) {
            super(msg);
        }
    }

    protected void throwAssertionFailureListNotZeroException(String sql, int resultCount,
            List<Map<String, String>> resultList) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL of 'select count' returned NOT ZERO!" + ln();
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

    public static class DfAssertionFailureListNotZeroException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DfAssertionFailureListNotZeroException(String msg) {
            super(msg);
        }
    }
}
