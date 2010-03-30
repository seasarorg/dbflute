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
package org.seasar.dbflute.s2dao.sqlcommand;

import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.metadata.TnProcedureMetaData;
import org.seasar.dbflute.s2dao.metadata.TnProcedureParameterType;
import org.seasar.dbflute.s2dao.sqlhandler.TnProcedureHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnProcedureHandler.TnProcedureResultSetHandlerProvider;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected StatementFactory _statementFactory;
    protected TnProcedureMetaData _procedureMetaData;
    protected TnProcedureResultSetHandlerFactory _procedureResultSetHandlerFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureCommand(DataSource dataSource, StatementFactory statementFactory,
            TnProcedureMetaData procedureMetaData, TnProcedureResultSetHandlerFactory procedureResultSetHandlerFactory) {
        this._dataSource = dataSource;
        this._statementFactory = statementFactory;
        this._procedureMetaData = procedureMetaData;
        this._procedureResultSetHandlerFactory = procedureResultSetHandlerFactory;
    }

    public static interface TnProcedureResultSetHandlerFactory {
        TnResultSetHandler createBeanHandler(Class<?> beanClass);

        TnResultSetHandler createDefaultHandler();
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(final Object[] args) {
        final TnProcedureHandler handler = createProcedureHandler();
        final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
        final Object pmb = outsideSqlContext.getParameterBean();
        // The logging message SQL of procedure is unnecessary.
        //handler.setLoggingMessageSqlArgs(...);
        return handler.execute(new Object[] { pmb });
    }

    protected TnProcedureHandler createProcedureHandler() {
        final String sql = _procedureMetaData.createSql();
        return new TnProcedureHandler(_dataSource, sql, _statementFactory, _procedureMetaData,
                createProcedureResultSetHandlerFactory());
    }

    protected TnProcedureResultSetHandlerProvider createProcedureResultSetHandlerFactory() {
        return new TnProcedureResultSetHandlerProvider() {
            public TnResultSetHandler provideResultSetHandler(TnProcedureParameterType ppt, ResultSet rs) {
                final Class<?> parameterType = ppt.getParameterType();
                if (!List.class.isAssignableFrom(parameterType)) {
                    String msg = "The parameter type for result set should be List:";
                    msg = msg + " parameter=" + ppt.getParameterName() + " type=" + parameterType;
                    throw new IllegalStateException(msg);
                }
                final Class<?> elementType = ppt.getElementType();
                if (elementType != null) {
                    return _procedureResultSetHandlerFactory.createBeanHandler(elementType);
                }
                return _procedureResultSetHandlerFactory.createDefaultHandler();
            }
        };
    }
}
