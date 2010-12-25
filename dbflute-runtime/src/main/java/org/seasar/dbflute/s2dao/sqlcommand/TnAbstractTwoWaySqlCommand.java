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
package org.seasar.dbflute.s2dao.sqlcommand;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicParameterHandler;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.Node;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractTwoWaySqlCommand extends TnAbstractBasicSqlCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractTwoWaySqlCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        final Node rootNode = getRootNode(args);
        final CommandContext ctx = apply(rootNode, args, getArgNameTypeMap(args));
        final String executedSql = filterExecutedSql(ctx.getSql());
        final TnBasicParameterHandler handler = createFreeParameterHandler(ctx, executedSql);
        final Object[] bindVariables = ctx.getBindVariables();
        final Class<?>[] bindVariableTypes = ctx.getBindVariableTypes();
        return filterReturnValue(handler.execute(bindVariables, bindVariableTypes));
    }

    // ===================================================================================
    //                                                                            Resource
    //                                                                            ========
    protected abstract Node getRootNode(Object[] args);

    protected abstract Map<String, Class<?>> getArgNameTypeMap(Object[] args);

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    protected TnBasicParameterHandler createFreeParameterHandler(CommandContext context, String executedSql) {
        final TnBasicParameterHandler handler = newBasicParameterHandler(executedSql);
        final Object[] bindVariables = context.getBindVariables();
        handler.setExceptionMessageSqlArgs(bindVariables);
        return handler;
    }

    protected abstract TnBasicParameterHandler newBasicParameterHandler(String executedSql);

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    protected String filterExecutedSql(String executedSql) {
        return executedSql;
    }

    protected Object filterReturnValue(Object returnValue) {
        return returnValue;
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    protected Node analyzeTwoWaySql(String twoWaySql) {
        return createSqlAnalyzer(twoWaySql).analyze();
    }

    protected SqlAnalyzer createSqlAnalyzer(String twoWaySql) {
        return ResourceContext.createSqlAnalyzer(twoWaySql, isBlockNullParameter());
    }

    protected boolean isBlockNullParameter() { // extension point
        return false; // as default
    }

    // ===================================================================================
    //                                                                   Argument Handling
    //                                                                   =================
    protected CommandContext apply(Node rootNode, Object[] args, Map<String, Class<?>> argNameTypeMap) {
        final CommandContext ctx = createCommandContext(args, argNameTypeMap);
        rootNode.accept(ctx);
        return ctx;
    }

    protected CommandContext createCommandContext(Object[] args, Map<String, Class<?>> argNameTypeMap) {
        return createCommandContextCreator(argNameTypeMap).createCommandContext(args);
    }

    protected CommandContextCreator createCommandContextCreator(Map<String, Class<?>> argNameTypeMap) {
        final Set<Entry<String, Class<?>>> entrySet = argNameTypeMap.entrySet();
        final String[] argNames = new String[argNameTypeMap.size()];
        final Class<?>[] argTypes = new Class[argNameTypeMap.size()];
        int index = 0;
        for (Entry<String, Class<?>> entry : entrySet) {
            argNames[index] = entry.getKey();
            argTypes[index] = entry.getValue();
            ++index;
        }
        return new CommandContextCreator(argNames, argTypes);
    }
}
