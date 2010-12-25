/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.core.execution;

import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.sqlcommand.TnAbstractTwoWaySqlCommand;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicParameterHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;
import org.seasar.dbflute.twowaysql.node.Node;

/**
 * @author jflute
 */
public class SelectCBExecution extends TnAbstractTwoWaySqlCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TnResultSetHandler _resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SelectCBExecution(DataSource dataSource, StatementFactory statementFactory,
            Map<String, Class<?>> argNameTypeMap, TnResultSetHandler resultSetHandler) {
        super(dataSource, statementFactory, argNameTypeMap);
        _resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                            Resource
    //                                                                            ========
    @Override
    protected Node getRootNode(Object[] args) {
        assertArgsValid(args);
        final ConditionBean cb = (ConditionBean) args[0];
        final String twoWaySql = cb.getSqlClause().getClause();
        return analyzeTwoWaySql(twoWaySql);
    }

    protected void assertArgsValid(Object[] args) {
        if (args == null) {
            String msg = "The argument 'args' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (args.length == 0) {
            String msg = "The argument 'args' should not be empty.";
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    @Override
    protected TnBasicParameterHandler newBasicParameterHandler(String executedSql) {
        return new TnBasicSelectHandler(_dataSource, executedSql, _resultSetHandler, _statementFactory);
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    @Override
    protected boolean isBlockNullParameter() {
        return true; // because the SQL is select
    }
}
