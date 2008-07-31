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
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The SQL of 'select count' returned NOT ZERO!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your test data!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[SQL File]" + getLineSeparator() + _srcFile + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Executed SQL]" + getLineSeparator() + sql + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Result Count]" + getLineSeparator() + resultCount + getLineSeparator();
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
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The SQL of 'select count' returned NOT ZERO!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your test data!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[SQL File]" + getLineSeparator() + _srcFile + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Executed SQL]" + getLineSeparator() + sql + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Result Count]" + getLineSeparator() + resultCount + getLineSeparator();
        msg = msg + "[Result List]" + getLineSeparator();
        for (Map<String, String> recordMap : resultList) {
            msg = msg + recordMap + getLineSeparator();
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
