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

import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.XLog;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
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
        if (isDynamicBinding(outsideSqlContext)) { // basically to use FOR comment
            // *dynamic binding is supported in select statement only
            logDynamicBinding();
            return executeOutsideSqlAsDynamic(args, outsideSqlContext);
        } else { // main case
            return executeOutsideSqlAsStatic(args, outsideSqlContext);
        }
    }

    protected void logDynamicBinding() {
        if (XLog.isLogEnabled()) {
            XLog.log("...Executing as dynamic binding");
        }
    }

    protected boolean isDynamicBinding(OutsideSqlContext outsideSqlContext) {
        return outsideSqlContext.isDynamicBinding();
    }

    // -----------------------------------------------------
    //                                               Dynamic
    //                                               -------
    /**
     * Execute outside-SQL as Dynamic.
     * @param args The array of argument. (NotNull, The first element should be the instance of parameter-bean)
     * @param outsideSqlContext The context of outside-SQL. (NotNull)
     * @return Result. (Nullable)
     */
    protected Object executeOutsideSqlAsDynamic(Object[] args, OutsideSqlContext outsideSqlContext) {
        // *It will be deleted at the future because of deprecated
        final Object pmb = args[0];
        String dynamicSql = getSql();
        if (pmb != null) {
            // *embedded comment has get dynamic binding independently
            dynamicSql = resolveDynamicEmbedded(pmb, dynamicSql);
        }
        final OutsideSqlSelectExecution dynamicSqlFactory = createDynamicSqlFactory();
        dynamicSqlFactory.setArgNames(getArgNames());
        dynamicSqlFactory.setArgTypes(getArgTypes());
        dynamicSqlFactory.setSql(dynamicSql);
        final CommandContext ctx = dynamicSqlFactory.apply(args);
        return doExecuteOutsideSql(ctx);
    }

    protected String resolveDynamicEmbedded(Object pmb, String dynamicSql) {
        if (pmb == null) {
            return dynamicSql;
        }
        // *nested properties are unsupported 
        final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(pmb.getClass());
        final List<String> proppertyNameList = beanDesc.getProppertyNameList();
        for (String proppertyName : proppertyNameList) {
            final DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(proppertyName);
            final Class<?> propertyType = propertyDesc.getPropertyType();
            if (!propertyType.equals(String.class)) {
                continue;
            }
            final String outsideSqlPiece = (String) propertyDesc.getValue(pmb);
            if (outsideSqlPiece == null) {
                continue;
            }
            final String embeddedComment = "/*$pmb." + propertyDesc.getPropertyName() + "*/";
            dynamicSql = replaceString(dynamicSql, embeddedComment, outsideSqlPiece);
        }
        return dynamicSql;
    }

    protected OutsideSqlSelectExecution createDynamicSqlFactory() {
        return new OutsideSqlSelectExecution(getDataSource(), getStatementFactory(), _resultSetHandler);
    }

    // -----------------------------------------------------
    //                                                Static
    //                                                ------
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
        return new TnBasicSelectHandler(getDataSource(), realSql, rsh, getStatementFactory());
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
