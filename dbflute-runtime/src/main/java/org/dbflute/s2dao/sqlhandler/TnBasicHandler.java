package org.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.dbflute.CallbackContext;
import org.dbflute.DBDef;
import org.dbflute.QLog;
import org.dbflute.jdbc.SqlLogHandler;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.resource.ResourceContext;
import org.dbflute.resource.SQLExceptionHandler;
import org.dbflute.resource.TnSqlLogRegistry;
import org.dbflute.s2dao.valuetype.ValueTypes;
import org.dbflute.twowaysql.CompleteSqlBuilder;
import org.dbflute.util.DfSystemUtil;
import org.seasar.extension.jdbc.ValueType;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DataSource dataSource;
    private String sql;
    private StatementFactory statementFactory;
    private Object[] loggingMessageSqlArgs;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicHandler(DataSource ds, StatementFactory statementFactory) {
        setDataSource(ds);
        setStatementFactory(statementFactory);
    }

    public TnBasicHandler(DataSource ds, String sql, StatementFactory statementFactory) {
        setDataSource(ds);
        setSql(sql);
        setStatementFactory(statementFactory);
    }

    // ===================================================================================
    //                                                                        Common Logic
    //                                                                        ============
    // -----------------------------------------------------
    //                                    Arguments Handling
    //                                    ------------------
    protected void bindArgs(PreparedStatement ps, Object[] args, Class<?>[] argTypes) {
        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            final ValueType valueType = findValueType(args[i], argTypes[i]);
            try {
                valueType.bindValue(ps, i + 1, args[i]);
            } catch (SQLException e) {
                handleSQLException(e, ps);
            }
        }
    }

    protected ValueType findValueType(Object arg, Class<?> argType) {
        ValueType valueType = ValueTypes.getValueType(arg);
        if (valueType != null) {
            return valueType;
        }
        valueType = ValueTypes.getValueType(argType);
        if (valueType != null) {
            return valueType;
        }
        String msg = "Unknown type：argType=" + argType + " args=" + arg;
        throw new IllegalStateException(msg);
    }

    protected Class<?>[] getArgTypes(Object[] args) {
        if (args == null) {
            return null;
        }
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            if (arg != null) {
                argTypes[i] = arg.getClass();
            }
        }
        return argTypes;
    }

    // -----------------------------------------------------
    //                                           SQL Logging
    //                                           -----------
    protected void logSql(Object[] args, Class<?>[] argTypes) {
        final SqlLogHandler sqlLogHandler = getSqlLogHander();
        final boolean existsSqlLogHandler = sqlLogHandler != null;
        final Object sqlLogRegistry = TnSqlLogRegistry.findContainerSqlLogRegistry();
        final boolean existsSqlLogRegistry = sqlLogRegistry != null;
        if (isLogEnabled() || existsSqlLogHandler || existsSqlLogRegistry) {
            final String completeSql = getCompleteSql(args);
            if (isLogEnabled()) {
                log((isContainsLineSeparatorInSql() ? getLineSeparator() : "") + completeSql);
            }
            if (existsSqlLogHandler) { // DBFlute provides
                sqlLogHandler.handle(getSql(), completeSql, args, argTypes);
            }
            if (existsSqlLogRegistry) { // S2Container provides
                TnSqlLogRegistry.push(getSql(), completeSql, args, argTypes, sqlLogRegistry);
            }
        }
    }

    protected boolean isLogEnabled() {
        return QLog.isLogEnabled();
    }

    protected void log(String msg) {
        QLog.log(msg);
    }

    protected String getCompleteSql(Object[] args) {
        String logDateFormat = ResourceContext.getLogDateFormat();
        String logTimestampFormat = ResourceContext.getLogTimestampFormat();
        return CompleteSqlBuilder.getCompleteSql(sql, args, logDateFormat, logTimestampFormat);
    }

    protected SqlLogHandler getSqlLogHander() {
        if (!CallbackContext.isExistCallbackContextOnThread()) {
            return null;
        }
        return CallbackContext.getCallbackContextOnThread().getSqlLogHandler();
    }

    protected boolean isContainsLineSeparatorInSql() {
        return sql != null ? sql.contains(getLineSeparator()) : false;
    }

    // -----------------------------------------------------
    //                                               Various
    //                                               -------
    protected String getBindVariableText(Object bindVariable) {
        String logDateFormat = ResourceContext.getLogDateFormat();
        String logTimestampFormat = ResourceContext.getLogTimestampFormat();
        return CompleteSqlBuilder.getBindVariableText(bindVariable, logDateFormat, logTimestampFormat);
    }

    // ===================================================================================
    //                                                                   Exception Handler
    //                                                                   =================
    protected void handleSQLException(SQLException e, Statement statement) {
        handleSQLException(e, statement, false);
    }

    protected void handleSQLException(SQLException e, Statement statement, boolean uniqueConstraintValid) {
        String completeSql = buildLoggingMessageSql();
        new SQLExceptionHandler().handleSQLException(e, statement, uniqueConstraintValid, completeSql);
    }

    protected String buildLoggingMessageSql() {
        String completeSql = null;
        if (sql != null && loggingMessageSqlArgs != null) {
            try {
                completeSql = getCompleteSql(loggingMessageSqlArgs);
            } catch (RuntimeException ignored) {
            }
        }
        return completeSql;
    }

    // ===================================================================================
    //                                                                      JDBC Delegator
    //                                                                      ==============
    protected Connection getConnection() {
        if (dataSource == null) {
            throw new IllegalStateException("The dataSource should not be null!");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// Unreachable!
        }
    }

    protected PreparedStatement prepareStatement(Connection conn) {
        if (sql == null) {
            throw new IllegalStateException("The sql should not be null!");
        }
        return statementFactory.createPreparedStatement(conn, sql);
    }

    protected int executeUpdate(PreparedStatement ps) {
        try {
            return ps.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e, ps, true);
            return 0;// Unreachable!
        }
    }

    protected void setFetchSize(Statement statement, int fetchSize) {
        if (statement == null) {
            return;
        }
        try {
            statement.setFetchSize(fetchSize);
        } catch (SQLException e) {
            handleSQLException(e, statement);
        }
    }

    protected void setMaxRows(Statement statement, int maxRows) {
        if (statement == null) {
            return;
        }
        try {
            statement.setMaxRows(maxRows);
        } catch (SQLException e) {
            handleSQLException(e, statement);
        }
    }

    protected void close(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, statement);
        }
    }

    protected void close(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (SQLException e) {
            handleSQLException(e, null);
        }
    }

    protected void close(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (SQLException e) {
            handleSQLException(e, null);
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    // It needs this method if the target database does not support line comment.
    protected String removeLineComment(final String sql) { // With removing CR!
        if (sql == null || sql.trim().length() == 0) {
            return sql;
        }
        final StringBuilder sb = new StringBuilder();
        final String[] lines = sql.split("\n");
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            line = line.replaceAll("\r", ""); // Remove CR!
            if (line.startsWith("--")) {
                continue;
            }
            sb.append(line).append("\n");
        }
        final String filteredSql = sb.toString();
        return filteredSql.substring(0, filteredSql.lastIndexOf("\n"));
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        if (isRemoveLineCommentFromSql()) {
            sql = removeLineComment(sql);
        }
        this.sql = sql;
    }

    protected boolean isRemoveLineCommentFromSql() {
        // Because the MS-Access does not support line comments.
        return isCurrentDBDef(DBDef.MSAccess);
    }
    
    protected boolean isCurrentDBDef(DBDef currentDBDef) {
	    return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public void setLoggingMessageSqlArgs(Object[] loggingMessageSqlArgs) {
        this.loggingMessageSqlArgs = loggingMessageSqlArgs;
    }
}
