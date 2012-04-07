/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.s2dao.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.exception.handler.SQLExceptionHandler;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.resource.ResourceContext;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
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
    protected StatementConfig _defaultStatementConfig;
    protected boolean _internalDebug;
    protected Integer _cursorSelectFetchSize;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnStatementFactoryImpl() {
    }

    // ===================================================================================
    //                                                                   PreparedStatement
    //                                                                   =================
    public PreparedStatement createPreparedStatement(Connection conn, String sql) {
        final StatementConfig config = findStatementConfigOnThread();
        final int resultSetType = getResultSetType(config);
        final int resultSetConcurrency = getResultSetConcurrency(config);
        if (isInternalDebugEnabled()) {
            _log.debug("...Preparing statement:(sql, " + resultSetType + ", " + resultSetConcurrency + ")");
        }
        final PreparedStatement ps = prepareStatement(conn, sql, resultSetType, resultSetConcurrency);
        reflectStatementOptions(ps, config);
        return ps;
    }

    protected PreparedStatement prepareStatement(Connection conn, String sql, int resultSetType,
            int resultSetConcurrency) {
        try {
            return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// unreachable
        }
    }

    // -----------------------------------------------------
    //                                       StatementConfig
    //                                       ---------------
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

    // -----------------------------------------------------
    //                                      ResultSet Option
    //                                      ----------------
    protected int getResultSetType(StatementConfig config) {
        final int resultSetType;
        if (config != null && config.hasResultSetType()) {
            resultSetType = config.getResultSetType();
        } else {
            final int defaultType = ResultSet.TYPE_FORWARD_ONLY;
            if (_defaultStatementConfig != null && _defaultStatementConfig.hasResultSetType()) {
                if (config != null && config.isSuppressDefault()) {
                    resultSetType = defaultType;
                } else {
                    resultSetType = _defaultStatementConfig.getResultSetType();
                }
            } else {
                resultSetType = defaultType;
            }
        }
        return resultSetType;
    }

    protected int getResultSetConcurrency(StatementConfig config) {
        return ResultSet.CONCUR_READ_ONLY;
    }

    // -----------------------------------------------------
    //                                  Statement Reflection
    //                                  --------------------
    protected void reflectStatementOptions(PreparedStatement ps, StatementConfig config) {
        final StatementConfig actualConfig = getActualStatementConfig(config);
        doReflectStatementOptions(ps, actualConfig);
    }

    protected StatementConfig getActualStatementConfig(StatementConfig config) {
        final boolean existsRequest = config != null;
        final StatementConfig defaultConfig = getActualDefaultConfig(config);
        final boolean existsDefault = defaultConfig != null;
        final boolean existsCursor = _cursorSelectFetchSize != null;
        final Integer queryTimeout = getActualQueryTimeout(config, existsRequest, defaultConfig, existsDefault);
        final Integer fetchSize = getActualFetchSize(config, existsRequest, defaultConfig, existsDefault, existsCursor);
        final Integer maxRows = getActualMaxRows(config, existsRequest, defaultConfig, existsDefault);
        if (queryTimeout == null && fetchSize == null && maxRows == null) {
            return null;
        }
        final StatementConfig actualConfig = new StatementConfig();
        actualConfig.queryTimeout(queryTimeout).fetchSize(fetchSize).maxRows(maxRows);
        return actualConfig;
    }

    protected StatementConfig getActualDefaultConfig(StatementConfig config) {
        final StatementConfig defaultConfig;
        if (_defaultStatementConfig != null) {
            if (config != null && config.isSuppressDefault()) {
                defaultConfig = null; // suppressed
            } else {
                defaultConfig = _defaultStatementConfig.createSnapshot(); // snapshot just in case
            }
        } else {
            defaultConfig = null;
        }
        return defaultConfig;
    }

    protected Integer getActualQueryTimeout(StatementConfig config, final boolean existsRequest,
            final StatementConfig defaultConfig, final boolean existsDefault) {
        final Integer queryTimeout;
        if (existsRequest && config.hasQueryTimeout()) {
            queryTimeout = config.getQueryTimeout();
        } else if (existsDefault && defaultConfig.hasQueryTimeout()) {
            queryTimeout = defaultConfig.getQueryTimeout();
        } else {
            queryTimeout = null;
        }
        return queryTimeout;
    }

    protected Integer getActualFetchSize(StatementConfig config, final boolean existsRequest,
            final StatementConfig defaultConfig, final boolean existsDefault, final boolean existsCursor) {
        final Integer fetchSize;
        if (existsRequest && config.hasFetchSize()) {
            fetchSize = config.getFetchSize();
        } else if (existsCursor && isSelectCursorCommand()) {
            fetchSize = _cursorSelectFetchSize;
        } else if (existsDefault && defaultConfig.hasFetchSize()) {
            fetchSize = defaultConfig.getFetchSize();
        } else {
            fetchSize = null;
        }
        return fetchSize;
    }

    protected Integer getActualMaxRows(StatementConfig config, final boolean existsRequest,
            final StatementConfig defaultConfig, final boolean existsDefault) {
        final Integer maxRows;
        if (existsRequest && config.hasMaxRows()) {
            maxRows = config.getMaxRows();
        } else if (existsDefault && defaultConfig.hasMaxRows()) {
            maxRows = defaultConfig.getMaxRows();
        } else {
            maxRows = null;
        }
        return maxRows;
    }

    protected void doReflectStatementOptions(PreparedStatement ps, StatementConfig actualConfig) {
        if (actualConfig == null || !actualConfig.hasStatementOptions()) {
            return;
        }
        try {
            if (actualConfig.hasQueryTimeout()) {
                final Integer queryTimeout = actualConfig.getQueryTimeout();
                if (isInternalDebugEnabled()) {
                    _log.debug("...Setting queryTimeout of statement: " + queryTimeout);
                }
                ps.setQueryTimeout(queryTimeout);
            }
            if (actualConfig.hasFetchSize()) {
                final Integer fetchSize = actualConfig.getFetchSize();
                if (isInternalDebugEnabled()) {
                    _log.debug("...Setting fetchSize of statement: " + fetchSize);
                }
                ps.setFetchSize(fetchSize);
            }
            if (actualConfig.hasMaxRows()) {
                final Integer maxRows = actualConfig.getMaxRows();
                if (isInternalDebugEnabled()) {
                    _log.debug("...Setting maxRows of statement: " + maxRows);
                }
                ps.setMaxRows(maxRows);
            }
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }

    // ===================================================================================
    //                                                                   CallableStatement
    //                                                                   =================
    public CallableStatement createCallableStatement(Connection conn, String sql) {
        final StatementConfig config = findStatementConfigOnThread();
        final int resultSetType = getResultSetType(config);
        final int resultSetConcurrency = getResultSetConcurrency(config);
        if (isInternalDebugEnabled()) {
            _log.debug("...Preparing callable:(sql, " + resultSetType + ", " + resultSetConcurrency + ")");
        }
        final CallableStatement cs = prepareCall(conn, sql, resultSetType, resultSetConcurrency);
        reflectStatementOptions(cs, config);
        return cs;
    }

    protected CallableStatement prepareCall(Connection conn, String sql, int resultSetType, int resultSetConcurrency) {
        try {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// unreachable
        }
    }

    // ===================================================================================
    //                                                               SQLException Handling
    //                                                               =====================
    protected void handleSQLException(SQLException e, Statement statement) {
        createSQLExceptionHandler().handleSQLException(e, statement);
    }

    protected SQLExceptionHandler createSQLExceptionHandler() {
        return ResourceContext.createSQLExceptionHandler();
    }

    // ===================================================================================
    //                                                                        Command Info
    //                                                                        ============
    protected boolean isSelectCursorCommand() {
        if (!ResourceContext.isExistResourceContextOnThread()) {
            return false;
        }
        return ResourceContext.behaviorCommand().isSelectCursor();
    }

    // ===================================================================================
    //                                                                      Internal Debug
    //                                                                      ==============
    private boolean isInternalDebugEnabled() { // because log instance is private
        return _internalDebug && _log.isDebugEnabled();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDefaultStatementConfig(StatementConfig defaultStatementConfig) {
        _defaultStatementConfig = defaultStatementConfig;
    }

    public void setInternalDebug(boolean internalDebug) {
        _internalDebug = internalDebug;
    }

    public void setCursorSelectFetchSize(Integer cursorSelectFetchSize) {
        _cursorSelectFetchSize = cursorSelectFetchSize;
    }
}
