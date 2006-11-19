package org.seasar.dbflute.helper.jdbc;

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
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.seasar.dbflute.TorqueBuildProperties;
import org.seasar.dbflute.helper.jdbc.SqlFileRunnerExecute.IORuntimeException;
import org.seasar.dbflute.util.FlClassUtil;

public abstract class SqlFileRunnerBase implements SqlFileRunner {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(SqlFileRunnerBase.class);

    protected final RunnerInformation _runInfo;
    protected int _goodSqlCount = 0;
    protected int _totalSqlCount = 0;
    protected File _srcFile;

    public SqlFileRunnerBase(RunnerInformation runInfo) {
        _runInfo = runInfo;
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

            connection = newConnection();
            statement = newStatement(connection);
            for (String sql : sqlList) {
                _totalSqlCount++;
                final String realSql = filterSql(sql);
                traceSql(sql);
                execSQL(statement, realSql);
            }
            if (!_runInfo.isAutoCommit()) {
                if (_runInfo.isRollbackOnly()) {
                    connection.rollback();
                } else {
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            throw new BuildException("Transaction#runTransaction() threw the exception!", e);
        } finally {
            if (!_runInfo.isAutoCommit() && connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignore) {
                    _log.warn("Connection#rollback() threw the exception!", ignore);
                }
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignore) {
                _log.warn("Statement#close() threw the exception!", ignore);
            } finally {
                statement = null;
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignore) {
                _log.warn("Connection#close() threw the exception!", ignore);
            } finally {
                connection = null;
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignore) {
                _log.warn("Reader#close() threw the exception: " + reader, ignore);
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

    protected Connection newConnection() {
        Connection connection = null;
        final Driver driverInstance = newDriver();
        final Properties dbInfoProp = new Properties();
        dbInfoProp.put("user", _runInfo.getUser());
        dbInfoProp.put("password", _runInfo.getPassword());
        try {
            connection = driverInstance.connect(_runInfo.getUrl(), dbInfoProp);
        } catch (SQLException e) {
            throw new BuildException("Driver#connect() threw the exception: _url=" + _runInfo.getUrl(), e);
        }
        if (connection == null) {
            throw new BuildException("Driver doesn't understand the URL: _url=" + _runInfo.getUrl());
        }
        try {
            connection.setAutoCommit(_runInfo.isAutoCommit());
        } catch (SQLException e) {
            String msg = "Connection#setAutoCommit() threw the exception: _autocommit=";
            throw new BuildException(msg + _runInfo.isAutoCommit(), e);
        }
        return connection;
    }

    protected Driver newDriver() {
        final Driver driverInstance;
        try {
            final Class dc = Class.forName(_runInfo.getDriver());
            driverInstance = (Driver) dc.newInstance();
        } catch (ClassNotFoundException e) {
            String msg = "Class Not Found: JDBC driver " + _runInfo.getDriver() + " could not be loaded.";
            throw new BuildException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "Illegal Access: JDBC driver " + _runInfo.getDriver() + " could not be loaded.";
            throw new BuildException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Instantiation Exception: JDBC driver " + _runInfo.getDriver() + " could not be loaded.";
            throw new BuildException(msg, e);
        }
        return driverInstance;
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
     * Lines starting with '//', '--' or 'REM ' are ignored.
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
            while ((line = in.readLine()) != null) {
                line = line.trim();

                // SQL defines "--" as a comment to EOL
                // and in Oracle it may contain a hint
                // so we cannot just remove it, instead we must end it
                if (line.indexOf("--") >= 0) {
                    sql = sql + line + "\n";
                } else {
                    sql = sql + " " + line;
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
        final String databaseName = TorqueBuildProperties.getInstance().stringProp("torque.database");
        final String className = DelimiterChanger.class.getName() + "_" + databaseName;
        DelimiterChanger changer = null;
        try {
            changer = (DelimiterChanger) FlClassUtil.newInstance(className);
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

    public static class DelimiterChanger_null implements DelimiterChanger {

        public boolean isDelimiterChanger(String sql) {
            return false;
        }

        public String getNewDelimiter(String sql, String preDelimiter) {
            return preDelimiter;
        }
    }

}
