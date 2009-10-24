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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public abstract class DfSqlFileRunnerBase implements DfSqlFileRunner {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileRunnerBase.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfRunnerInformation _runInfo;
    protected DataSource _dataSource;
    protected File _sqlFile;
    protected DfSqlFileRunnerResult _result = new DfSqlFileRunnerResult(); // is an empty result as default
    protected int _goodSqlCount = 0;
    protected int _totalSqlCount = 0;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlFileRunnerBase(DfRunnerInformation runInfo, DataSource dataSource) {
        _runInfo = runInfo;
        _dataSource = dataSource;
    }

    public void prepare(File sqlFile) {
        _sqlFile = sqlFile;
        _result = new DfSqlFileRunnerResult();
        _result.setSrcFile(sqlFile);
    }

    // ===================================================================================
    //                                                                     Run Transaction
    //                                                                     ===============
    public DfSqlFileRunnerResult runTransaction() {
        _goodSqlCount = 0;
        _totalSqlCount = 0;
        if (_sqlFile == null) {
            throw new BuildException("Attribute[_srcFile] must not be null.");
        }

        Reader reader = null;
        Connection conn = null;
        Statement statement = null;
        try {
            reader = newInputStreamReader();
            final List<String> sqlList = extractSqlList(reader);

            conn = getConnection();
            statement = newStatement(conn);
            for (String sql : sqlList) {
                if (!isTargetSql(sql)) {
                    continue;
                }
                _totalSqlCount++;
                final String realSql = filterSql(sql);
                traceSql(realSql);
                execSQL(statement, realSql);
            }
            Boolean autoCommit = null;
            try {
                autoCommit = conn.getAutoCommit();
            } catch (SQLException continued) {
                // Because it it possible that the connection would have already closed.
                _log.warn("Connection#getAutoCommit() said: " + continued.getMessage());
            }
            if (autoCommit != null && !autoCommit) {
                if (_runInfo.isRollbackOnly()) {
                    conn.rollback();
                } else {
                    conn.commit();
                }
            }
        } catch (SQLException e) {
            throw new BuildException("Transaction#runTransaction() threw the exception!", e);
        } finally {
            Boolean autoCommit = null;
            try {
                autoCommit = conn.getAutoCommit();
            } catch (SQLException continued) {
            }
            try {
                if (autoCommit != null && conn != null && !conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignored) {
            } finally {
                statement = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            } finally {
                conn = null;
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            } finally {
                reader = null;
            }
        }
        traceResult(_goodSqlCount, _totalSqlCount);
        _result.setGoodSqlCount(_goodSqlCount);
        _result.setTotalSqlCount(_totalSqlCount);
        return _result;
    }

    protected boolean isTargetSql(String sql) {
        return true;
    }

    protected void traceSql(String sql) {
        if (sql.contains(ln())) {
            sql = ln() + sql;
        }
        _log.info(sql);
    }

    protected void traceResult(int goodSqlCount, int totalSqlCount) {
        _log.info("  --> success=" + goodSqlCount + " failure=" + (totalSqlCount - goodSqlCount));
    }

    protected String filterSql(String sql) {// for Override
        return sql;
    }

    protected InputStreamReader newInputStreamReader() {
        try {
            final String encoding = _runInfo.isEncodingNull() ? "UTF-8" : _runInfo.getEncoding();
            return new InputStreamReader(new FileInputStream(_sqlFile), encoding);
        } catch (FileNotFoundException e) {
            throw new BuildException("The file does not exist: " + _sqlFile, e);
        } catch (UnsupportedEncodingException e) {
            throw new BuildException("The encoding is unsupported: " + _runInfo.getEncoding(), e);
        }
    }

    protected Connection getConnection() {
        try {
            final Connection connection = _dataSource.getConnection();
            connection.setAutoCommit(_runInfo.isAutoCommit());
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("getDataSource().getConnection() threw the exception!", e);
        }
    }

    protected Statement newStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            String msg = "Connection#createStatement() threw the exception: _connection=";
            throw new BuildException(msg + connection, e);
        }
    }

    // ===================================================================================
    //                                                                         Extract SQL
    //                                                                         ===========
    protected List<String> extractSqlList(Reader reader) {
        final List<String> sqlList = new ArrayList<String>();
        final BufferedReader in = new BufferedReader(reader);
        final DelimiterChanger delimiterChanger = newDelimterChanger();
        try {
            String sql = "";
            String line = "";
            boolean inGroup = false;
            boolean existsCommentOn = false;
            boolean isAlreadyProcessUTF8Bom = false;
            while ((line = in.readLine()) != null) {
                if (!isAlreadyProcessUTF8Bom) {
                    line = removeUTF8BomIfNeeds(line);
                    isAlreadyProcessUTF8Bom = true;
                }
                if (!inGroup && isSqlTrimAndRemoveLineSeparator()) {
                    line = line.trim();
                }
                if (!existsCommentOn && isSqlTrimAndRemoveLineSeparator() && isHandlingCommentOnLineSeparator()) {
                    final String lowerLine = line.trim().toLowerCase();
                    if (lowerLine.startsWith("comment on ") && lowerLine.contains("is") && lowerLine.contains("'")) {
                        existsCommentOn = true;
                    }
                }

                // SQL defines "--" as a comment to EOL
                // and in Oracle it may contain a hint
                // so we cannot just remove it, instead we must end it
                if (line.trim().startsWith("--")) {// If this line is comment only, ...
                    // * * * * * * * * * * *
                    // Line for Line Comment
                    // * * * * * * * * * * *

                    // Group Specification
                    // /- - - - - - - - - - - - - - - -
                    if (line.trim().contains("#df:begin#")) {
                        inGroup = true;
                        sql = "";
                        continue;
                    } else if (line.trim().contains("#df:end#")) {
                        inGroup = false;
                        sql = removeTerminater4ToolIfNeeds(sql);// [DBFLUTE-309]
                        addSqlToList(sqlList, sql);

                        // End Point of SQL!
                        existsCommentOn = false;
                        sql = "";
                        continue;
                    }
                    // - - - - - - - - - -/

                    // Real Line Comment
                    line = replaceCommentQuestionMarkIfNeeds(line);

                    if (inGroup) {
                        sql = sql + line + ln();
                        continue;
                    }
                    sql = sql + line + ln();
                } else {
                    // * * * * * * * * * *
                    // Line for SQL Clause
                    // * * * * * * * * * *

                    if (inGroup) {
                        sql = sql + line + ln();
                        continue;
                    }

                    final String lineConnect;
                    if (isSqlTrimAndRemoveLineSeparator()) {
                        if (existsCommentOn) {
                            lineConnect = ln();
                        } else {
                            lineConnect = " ";
                        }
                    } else {
                        lineConnect = "";
                    }
                    if (line.indexOf("--") >= 0) {// If this line contains both SQL and comment, ...
                        // With Line Comment
                        line = replaceCommentQuestionMarkIfNeeds(line);
                        sql = sql + lineConnect + line + ln();
                    } else {
                        // SQL Clause Only
                        final String lineTerminator = isSqlTrimAndRemoveLineSeparator() ? "" : ln();
                        sql = sql + lineConnect + line + lineTerminator;
                    }
                }

                if (sql.trim().endsWith(_runInfo.getDelimiter())) {
                    // * * * * * * * *
                    // End of the SQL
                    // * * * * * * * *
                    sql = sql.trim();
                    sql = sql.substring(0, sql.length() - _runInfo.getDelimiter().length());
                    sql = sql.trim();
                    if ("".equals(sql)) {
                        continue;
                    }
                    if (!delimiterChanger.isDelimiterChanger(sql)) {
                        addSqlToList(sqlList, sql);

                        // End Point of SQL!
                        existsCommentOn = false;
                        sql = "";
                    } else {
                        _runInfo.setDelimiter(delimiterChanger.getNewDelimiter(sql, _runInfo.getDelimiter()));

                        // End Point of SQL!
                        existsCommentOn = false;
                        sql = "";
                    }
                }
            }
            sql = sql.trim();
            if (sql.length() > 0) {
                addSqlToList(sqlList, sql);// for Last SQL
            }
        } catch (IOException e) {
            throw new RuntimeException("The method 'extractSqlList()' threw the exception!", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                    ignore.printStackTrace();
                }
            }
        }
        return sqlList;
    }

    protected void addSqlToList(List<String> sqlList, String sql) {
        if (isSqlLineCommentOnly(sql)) {
            return;
        }
        sqlList.add(removeCR(sql));
    }

    protected boolean isSqlLineCommentOnly(String sql) {
        sql = sql.trim();
        String[] lines = sql.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("--")) {
                continue;
            }
            return false;
        }
        _log.info("The SQL is line comment only so skip it:" + ln() + sql);
        return true;
    }

    protected String removeTerminater4ToolIfNeeds(String sql) {
        String terminater = getTerminater4Tool();
        if (terminater == null || terminater.trim().length() == 0) {
            return sql;
        }
        sql = sql.trim();
        if (sql.endsWith(terminater)) {
            String rear = sql.length() > 30 ? ": ..." + sql.substring(sql.length() - 30) : ".";
            _log.info("...Removing terminater '" + terminater + "' for tools" + rear);
            sql = sql.substring(0, sql.length() - terminater.length());
        }
        return sql;
    }

    protected String getTerminater4Tool() {// for override.
        return null;
    }

    public DelimiterChanger newDelimterChanger() {
        final String databaseName = DfBuildProperties.getInstance().getBasicProperties().getDatabaseName();
        final String className = DelimiterChanger.class.getName() + "_" + databaseName;
        DelimiterChanger changer = null;
        try {
            changer = (DelimiterChanger) Class.forName(className).newInstance();
        } catch (Exception ignore) {
            changer = new DelimiterChanger_null();
        }
        return changer;
    }

    protected String removeUTF8BomIfNeeds(String str) {
        if (_runInfo.isEncodingNull()) {
            return str;
        }
        if ("UTF-8".equalsIgnoreCase(_runInfo.getEncoding()) && str.length() > 0 && str.charAt(0) == '\uFEFF') {
            String front = str.length() > 5 ? ": " + str.substring(0, 5) + "..." : ".";
            _log.info("...Removing UTF-8 bom" + front);
            str = str.substring(1);
        }
        return str;
    }

    protected String removeCR(String str) {
        return str.replaceAll("\r", "");
    }

    protected String replaceCommentQuestionMarkIfNeeds(String line) {
        final int lineCommentIndex = line.indexOf("--");
        if (lineCommentIndex < 0) {
            return line;
        }
        final String sqlClause;
        if (lineCommentIndex == 0) {
            sqlClause = "";
        } else {
            sqlClause = line.substring(0, lineCommentIndex);
        }
        String lineComment = line.substring(lineCommentIndex);
        if (lineComment.indexOf("?") >= 0) {
            lineComment = DfStringUtil.replace(line, "?", "Q");
        }
        return sqlClause + lineComment;
    }

    // ===================================================================================
    //                                                                        For Override
    //                                                                        ============
    /**
     * Execute the SQL statement.
     * @param statement Statement. (NotNull)
     * @param sql SQL. (NotNull)
     */
    abstract protected void execSQL(Statement statement, String sql);

    /**
     * @return Determination.
     */
    protected boolean isSqlTrimAndRemoveLineSeparator() {
        return false; // as Default
    }

    /**
     * @return Determination.
     */
    protected boolean isHandlingCommentOnLineSeparator() {
        return false; // as Default
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        // (DBFLUTE-264)
        return "\n";
    }

    // ===================================================================================
    //                                                                   Delimiter Changer
    //                                                                   =================
    protected static interface DelimiterChanger {
        public boolean isDelimiterChanger(String sql);

        public String getNewDelimiter(String sql, String preDelimiter);
    }

    protected static class DelimiterChanger_firebird implements DelimiterChanger {
        public static final String CHANGE_COMMAND = "set term ";
        public static final int CHANGE_COMMAND_LENGTH = CHANGE_COMMAND.length();

        public boolean isDelimiterChanger(String sql) {
            sql = sql.trim();
            if (sql.length() > CHANGE_COMMAND_LENGTH) {
                if (sql.substring(0, CHANGE_COMMAND_LENGTH).equalsIgnoreCase(CHANGE_COMMAND)) {
                    return true;
                }
            }
            return false;
        }

        public String getNewDelimiter(String sql, String preDelimiter) {
            String tmp = sql.substring(CHANGE_COMMAND.length());
            if (tmp.indexOf(" ") >= 0) {
                tmp = tmp.substring(0, tmp.indexOf(" "));
            }
            return tmp;
        }
    }

    protected static class DelimiterChanger_mysql implements DelimiterChanger {
        public static final String CHANGE_COMMAND = "delimiter ";
        public static final int CHANGE_COMMAND_LENGTH = CHANGE_COMMAND.length();

        public boolean isDelimiterChanger(String sql) {
            sql = sql.trim();
            if (sql.length() > CHANGE_COMMAND_LENGTH) {
                if (sql.substring(0, CHANGE_COMMAND_LENGTH).equalsIgnoreCase(CHANGE_COMMAND)) {
                    return true;
                }
            }
            return false;
        }

        public String getNewDelimiter(String sql, String preDelimiter) {
            String tmp = sql.substring(CHANGE_COMMAND.length());
            if (tmp.indexOf(" ") >= 0) {
                tmp = tmp.substring(0, tmp.indexOf(" "));
            }
            return tmp;
        }
    }

    protected static class DelimiterChanger_null implements DelimiterChanger {

        public boolean isDelimiterChanger(String sql) {
            return false;
        }

        public String getNewDelimiter(String sql, String preDelimiter) {
            return preDelimiter;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfSqlFileRunnerResult getResult() {
        return _result;
    }
}
