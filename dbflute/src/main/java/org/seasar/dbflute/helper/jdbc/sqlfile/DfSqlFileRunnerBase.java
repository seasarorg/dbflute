package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute.IORuntimeException;
import org.seasar.framework.util.ClassUtil;

public abstract class DfSqlFileRunnerBase implements DfSqlFileRunner {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileRunnerBase.class);

    protected final DfRunnerInformation _runInfo;
    protected int _goodSqlCount = 0;
    protected int _totalSqlCount = 0;
    protected File _srcFile;
    protected DataSource _dataSource;

    public DfSqlFileRunnerBase(DfRunnerInformation runInfo, DataSource dataSource) {
        _runInfo = runInfo;
        _dataSource = dataSource;
    }

    public void setSrc(File src) {
        this._srcFile = src;
    }

    public int getGoodSqlCount() {
        return _goodSqlCount;
    }

    public int getTotalSqlCount() {
        return _totalSqlCount;
    }

    public void runTransaction() {
        _goodSqlCount = 0;
        _totalSqlCount = 0;
        if (_srcFile == null) {
            throw new BuildException("Attribute[_srcFile] must not be null.");
        }

        Reader reader = null;
        Connection connection = null;
        Statement statement = null;
        try {
            reader = (_runInfo.isEncodingNull()) ? newFileReader() : newInputStreamReader();
            final List<String> sqlList = extractSqlList(reader);

            connection = getConnection();
            statement = newStatement(connection);
            for (String sql : sqlList) {
                _totalSqlCount++;
                final String realSql = filterSql(sql);
                traceSql(sql);
                execSQL(statement, realSql);
            }
            if (!connection.getAutoCommit()) {
                if (_runInfo.isRollbackOnly()) {
                    connection.rollback();
                } else {
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            throw new BuildException("Transaction#runTransaction() threw the exception!", e);
        } finally {
            try {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException ignored) {
                _log.warn("Connection#rollback() threw the exception!", ignored);
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignored) {
                _log.warn("Statement#close() threw the exception!", ignored);
            } finally {
                statement = null;
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {
                _log.warn("Connection#close() threw the exception!", ignored);
            } finally {
                connection = null;
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
                _log.warn("Reader#close() threw the exception: " + reader, ignored);
            } finally {
                reader = null;
            }
        }
        _log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        _log.info(_goodSqlCount + " of " + _totalSqlCount + " SQL statements executed successfully.");
        _log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
    }

    protected void traceSql(String sql) {
        _log.info(sql);
    }

    protected String filterSql(String sql) {
        return sql;
    }

    protected FileReader newFileReader() {
        try {
            return new FileReader(_srcFile);
        } catch (FileNotFoundException e) {
            throw new BuildException("The file does not exist: " + _srcFile, e);
        }
    }

    protected InputStreamReader newInputStreamReader() {
        try {
            return new InputStreamReader(new FileInputStream(_srcFile), _runInfo.getEncoding());
        } catch (FileNotFoundException e) {
            throw new BuildException("The file does not exist: " + _srcFile, e);
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

    /**
     * Read the statements from the .sql file and execute them.
     *
     * @param reader
     * @return List.
     */
    protected List<String> extractSqlList(Reader reader) {
        final List<String> sqlList = new ArrayList<String>();
        final BufferedReader in = new BufferedReader(reader);
        final DelimiterChanger delimiterChanger = newDelimterChanger();
        try {
            String sql = "";
            String line = "";
            boolean inGroup = false;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                // SQL defines "--" as a comment to EOL
                // and in Oracle it may contain a hint
                // so we cannot just remove it, instead we must end it
                if (line.trim().startsWith("--")) {// If this line is comment only, ...
                    if (line.trim().contains("#df:begin#")) {
                        inGroup = true;
                        continue;
                    } else if (line.trim().contains("#df:end#")) {
                        inGroup = false;
                        sqlList.add(sql);
                        sql = "";
                        continue;
                    }
                    sql = sql + line + "\n";
                } else {
                    if (line.indexOf("--") >= 0) {// If this line contains both sql and comment, ...
                        sql = sql + " " + line + "\n";
                    } else {
                        sql = sql + " " + line;
                    }
                }

                if (inGroup) {
                    sql = sql + "\n";
                    continue;
                }
                if (sql.endsWith(_runInfo.getDelimiter())) {
                    sql = sql.substring(0, sql.length() - _runInfo.getDelimiter().length()).trim();
                    if ("".equals(sql)) {
                        continue;
                    }
                    if (!delimiterChanger.isDelimiterChanger(sql)) {
                        sqlList.add(sql);
                        sql = "";
                    } else {
                        _runInfo.setDelimiter(delimiterChanger.getNewDelimiter(sql, _runInfo.getDelimiter()));
                        sql = "";
                    }
                }
            }
            if (sql.trim().length() != 0) {
                sqlList.add(sql.trim());// for Last Sql
            }
        } catch (IOException e) {
            throw new IORuntimeException("Threw the exception!", e);
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

    public DelimiterChanger newDelimterChanger() {
        final String databaseName = DfBuildProperties.getInstance().stringProp("torque.database");
        final String className = DelimiterChanger.class.getName() + "_" + databaseName;
        DelimiterChanger changer = null;
        try {
            changer = (DelimiterChanger) ClassUtil.newInstance(className);
        } catch (RuntimeException ignore) {
            String msg = "The database has no delimiter changer: databaseName=";
            _log.debug(msg + databaseName + " className=" + className + " ignore=" + ignore);
            changer = new DelimiterChanger_null();
        }
        return changer;
    }

    /**
     * Exec the sql statement.
     *
     * @param statement
     * @param sql
     */
    abstract protected void execSQL(Statement statement, String sql);

    // =========================================================================================
    //                                                                         Delimiter Changer
    //                                                                         =================
    public static interface DelimiterChanger {
        public boolean isDelimiterChanger(String sql);

        public String getNewDelimiter(String sql, String preDelimiter);
    }

    public static class DelimiterChanger_firebird implements DelimiterChanger {
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

    public static class DelimiterChanger_mysql implements DelimiterChanger {
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

    public static class DelimiterChanger_null implements DelimiterChanger {

        public boolean isDelimiterChanger(String sql) {
            return false;
        }

        public String getNewDelimiter(String sql, String preDelimiter) {
            return preDelimiter;
        }
    }

}
