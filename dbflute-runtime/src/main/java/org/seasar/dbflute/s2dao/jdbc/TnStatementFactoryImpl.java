package org.seasar.dbflute.s2dao.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.resource.SQLExceptionHandler;

/**
 * {Refers to a S2Dao's class and Extends it}
 * @author jflute
 */
public class TnStatementFactoryImpl implements StatementFactory {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(TnStatementFactoryImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected StatementConfig defaultStatementConfig;
    protected boolean internalDebug;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnStatementFactoryImpl() {
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public PreparedStatement createPreparedStatement(Connection conn, String sql) {
        try {
            final StatementConfig config = findStatementConfigOnThread();
            final int resultSetType;
            if (config != null && config.hasResultSetType()) {
                resultSetType = config.getResultSetType();
            } else if (defaultStatementConfig != null && defaultStatementConfig.hasResultSetType()) {
                resultSetType = defaultStatementConfig.getResultSetType();
            } else {
                resultSetType = java.sql.ResultSet.TYPE_FORWARD_ONLY;
            }
            final int resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY;
            if (internalDebug) {
                _log.debug("...Creating prepareStatement(sql, " + resultSetType + ", " + resultSetConcurrency + ")");
            }
            final PreparedStatement ps = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
            if (config != null && config.hasStatementOptions()) {
                if (internalDebug) {
                    _log.debug("...Setting statement config as request: " + config);
                }
                reflectStatementOptions(config, ps);
            } else {
                reflectDefaultOptionsToStatementIfNeeds(ps);
            }
            return ps;
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// Unreachable!
        }
    }

    public CallableStatement createCallableStatement(Connection conn, String sql) {
        return prepareCall(conn, sql);
    }

    protected StatementConfig findStatementConfigOnThread() {
        final StatementConfig config;
        if (ConditionBeanContext.isExistConditionBeanOnThread()) {
            final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
            config = cb.getStatementConfig();
        } else if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext context = OutsideSqlContext.getOutsideSqlContextOnThread();
            config = context.getStatementConfig();
        } else {
            config = null;
        }
        return config;
    }

    protected void reflectDefaultOptionsToStatementIfNeeds(PreparedStatement ps) {
        if (defaultStatementConfig != null && defaultStatementConfig.hasStatementOptions()) {
            if (internalDebug) {
                _log.debug("...Setting statement config as default: " + defaultStatementConfig);
            }
            reflectStatementOptions(defaultStatementConfig, ps);
            return;
        }
    }

    protected void reflectStatementOptions(StatementConfig config, PreparedStatement ps) {
        try {
            if (config.hasQueryTimeout()) {
                ps.setQueryTimeout(config.getQueryTimeout());
            }
            if (config.hasFetchSize()) {
                ps.setFetchSize(config.getFetchSize());
            }
            if (config.hasMaxRows()) {
                ps.setMaxRows(config.getMaxRows());
            }
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }

    protected CallableStatement prepareCall(Connection conn, String sql) {
        try {
            return conn.prepareCall(sql);
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// Unreachable!
        }
    }

    protected void handleSQLException(SQLException e, Statement statement) {
        new SQLExceptionHandler().handleSQLException(e, statement);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDefaultStatementConfig(StatementConfig defaultStatementConfig) {
        this.defaultStatementConfig = defaultStatementConfig;
    }

    public void setInternalDebug(boolean internalDebug) {
        this.internalDebug = internalDebug;
    }
}
