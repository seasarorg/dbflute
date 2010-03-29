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

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.s2dao.metadata.TnProcedureMetaData;
import org.seasar.dbflute.s2dao.sqlhandler.TnProcedureHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource dataSource;
    protected StatementFactory statementFactory;
    protected TnProcedureMetaData procedureMetaData;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureCommand(DataSource dataSource, StatementFactory statementFactory,
            TnProcedureMetaData procedureMetaData) {
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
        this.procedureMetaData = procedureMetaData;
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
        final String sql = procedureMetaData.createSql();
        return new TnProcedureHandler(dataSource, sql, statementFactory, procedureMetaData);
    }
}
