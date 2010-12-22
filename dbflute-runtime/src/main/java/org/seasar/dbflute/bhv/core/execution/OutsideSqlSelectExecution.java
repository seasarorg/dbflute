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

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.outsidesql.OutsideSqlFilter;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class OutsideSqlSelectExecution extends AbstractOutsideSqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of resultSet. */
    protected TnResultSetHandler _resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource Data source.
     * @param statementFactory The factory of statement.
     * @param resultSetHandler The handler of resultSet.
     */
    public OutsideSqlSelectExecution(DataSource dataSource, StatementFactory statementFactory,
            TnResultSetHandler resultSetHandler) {
        super(dataSource, statementFactory);
        this._resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * @param args The array of argument. (NotNull, The first element should be the instance of Pmb)
     * @return The object of execution result. (Nullable)
     */
    public Object execute(Object[] args) {
        final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
        return executeOutsideSqlAsStatic(args, outsideSqlContext);
    }

    // -----------------------------------------------------
    //                                                Static
    //                                                ------
    // AsStatic means DynamicBinding existed (deleted now) 
    /**
     * Execute outside-SQL as static.
     * @param args The array of argument. (NotNull, The first element should be the instance of parameter-bean)
     * @param outsideSqlContext The context of outside-SQL. (NotNull)
     * @return Result. (Nullable)
     */
    protected Object executeOutsideSqlAsStatic(Object[] args, OutsideSqlContext outsideSqlContext) {
        final CommandContext ctx = apply(args);
        return doExecuteOutsideSql(ctx);
    }

    // -----------------------------------------------------
    //                                                Common
    //                                                ------
    protected Object doExecuteOutsideSql(CommandContext ctx) {
        final String realSql = filterSql(ctx.getSql());
        final TnBasicSelectHandler selectHandler = createBasicSelectHandler(realSql, _resultSetHandler);
        final Object[] bindVariables = ctx.getBindVariables();
        final Class<?>[] bindVariableTypes = ctx.getBindVariableTypes();
        selectHandler.setExceptionMessageSqlArgs(bindVariables);
        return selectHandler.execute(bindVariables, bindVariableTypes);
    }

    protected TnBasicSelectHandler createBasicSelectHandler(String realSql, TnResultSetHandler rsh) {
        return new TnBasicSelectHandler(_dataSource, realSql, rsh, _statementFactory);
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    @Override
    protected OutsideSqlFilter.ExecutionFilterType getOutsideSqlExecutionFilterType() {
        return OutsideSqlFilter.ExecutionFilterType.SELECT;
    }

    // ===================================================================================
    //                                                                       Parser Option
    //                                                                       =============
    @Override
    protected boolean isBlockNullParameter() {
        return true; // Because the SQL is select.
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }
}
