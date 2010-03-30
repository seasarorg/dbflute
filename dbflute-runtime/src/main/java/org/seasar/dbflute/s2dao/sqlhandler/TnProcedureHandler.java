/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.s2dao.sqlhandler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.metadata.TnProcedureMetaData;
import org.seasar.dbflute.s2dao.metadata.TnProcedureParameterType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnProcedureMetaData _procedureMetaData;
    private TnProcedureResultSetHandlerProvider _resultSetHandlerProvider;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureHandler(final DataSource dataSource, final String sql, final StatementFactory statementFactory,
            final TnProcedureMetaData procedureMetaData, TnProcedureResultSetHandlerProvider resultSetHandlerProvider) {
        super(dataSource, sql, statementFactory);
        this._procedureMetaData = procedureMetaData;
        this._resultSetHandlerProvider = resultSetHandlerProvider;
    }

    public static interface TnProcedureResultSetHandlerProvider {
        TnResultSetHandler provideResultSetHandler(TnProcedureParameterType ppt, ResultSet rs);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(final Object[] args) {
        final Class<?>[] argTypes = getArgTypes(args);
        final Object pmb = getParameterBean(args);
        logSql(args, argTypes);
        Connection conn = null;
        CallableStatement cs = null;
        try {
            conn = getConnection();
            cs = prepareCallableStatement(conn);
            bindArgs(cs, pmb);
            final boolean executed = cs.execute();
            return handleOutParameters(cs, pmb, executed);
        } catch (SQLException e) {
            handleSQLException(e, cs);
            return null; // unreachable
        } finally {
            close(cs);
            close(conn);
        }
    }

    protected Object getParameterBean(Object[] args) {
        if (args.length == 0) {
            return null;
        }
        if (args.length == 1) {
            if (args[0] == null) {
                throw new IllegalStateException("args[0] should not be null!");
            }
            return args[0];
        }
        throw new IllegalStateException("The size of args should be 1: " + args.length);
    }

    protected CallableStatement prepareCallableStatement(final Connection connection) {
        if (getSql() == null) {
            throw new IllegalStateException("The SQL should not be null!");
        }
        return getStatementFactory().createCallableStatement(connection, getSql());
    }

    protected void bindArgs(final CallableStatement cs, final Object dto) throws SQLException {
        if (dto == null) {
            return;
        }
        int i = 0;
        for (TnProcedureParameterType ppt : _procedureMetaData.parameterTypes()) {
            final ValueType valueType = ppt.getValueType();
            // if INOUT parameter, both are true
            if (ppt.isOutType()) {
                valueType.registerOutParameter(cs, i + 1);
            }
            if (ppt.isInType()) {
                final Object value = ppt.getValue(dto);
                valueType.bindValue(cs, i + 1, value);
            }
            ++i;
        }
    }

    protected Object handleOutParameters(CallableStatement cs, Object pmb, boolean executed) throws SQLException {
        if (pmb == null) {
            return null;
        }
        int i = 0;
        for (TnProcedureParameterType ppt : _procedureMetaData.parameterTypes()) {
            final ValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                Object value = valueType.getValue(cs, i + 1);
                if (value instanceof ResultSet) {
                    final ResultSet rs = (ResultSet) value;
                    final TnResultSetHandler handler = createOutParameterResultSetHandler(ppt, rs);
                    try {
                        value = handler.handle(rs);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                }
                ppt.setValue(pmb, value);
            } else if (ppt.isReturnType()) { // non out parameter return
                // basically false because return value is treated as out parameter
                // this process is for just in case
                Object returnResultSet = null;
                if (executed) {
                    returnResultSet = handleNonOutReturnResultSet(cs);
                }
                ppt.setValue(pmb, returnResultSet);
            }
            ++i;
        }
        return pmb;
    }

    protected Object handleNonOutReturnResultSet(CallableStatement cs) throws SQLException {
        Object returnResultSet = null;
        final ResultSet rs = cs.getResultSet();
        if (rs != null) {
            final TnResultSetHandler handler = createReturnResultSetHandler(rs);
            try {
                returnResultSet = handler.handle(rs);
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
        return returnResultSet;
    }

    // ===================================================================================
    //                                                                          DisplaySql
    //                                                                          ==========
    @Override
    protected String getDisplaySql(final Object[] args) { // for procedure call
        String sql = getSql();
        Object dto = getParameterBean(args);
        if (args == null || dto == null) {
            return sql;
        }
        StringBuilder sb = new StringBuilder(100);
        int pos = 0;
        int pos2 = 0;
        for (TnProcedureParameterType ppt : _procedureMetaData.parameterTypes()) {
            if ((pos2 = sql.indexOf('?', pos)) < 0) {
                break;
            }
            sb.append(sql.substring(pos, pos2));
            pos = pos2 + 1;
            if (ppt.isInType()) {
                sb.append(getBindVariableText(ppt.getValue(dto)));
            } else {
                sb.append(sql.substring(pos2, pos));
            }
        }
        sb.append(sql.substring(pos));
        return sb.toString();
    }

    // ===================================================================================
    //                                                                    ResultSetHandler
    //                                                                    ================
    protected TnResultSetHandler createReturnResultSetHandler(ResultSet resultSet) {
        TnProcedureParameterType ppt = _procedureMetaData.getReturnParameterType();
        return _resultSetHandlerProvider.provideResultSetHandler(ppt, resultSet);
    }

    protected TnResultSetHandler createOutParameterResultSetHandler(TnProcedureParameterType ppt, ResultSet resultSet) {
        return _resultSetHandlerProvider.provideResultSetHandler(ppt, resultSet);
    }
}
