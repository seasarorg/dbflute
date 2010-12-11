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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.sqlhandler.TnCommandContextHandler;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.Node;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnQueryUpdateAutoDynamicCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected final StatementFactory _statementFactory;
    protected TnBeanMetaData _beanMetaData;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnQueryUpdateAutoDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        this._dataSource = dataSource;
        this._statementFactory = statementFactory;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        // analyze arguments
        final ConditionBean cb = extractConditionBeanWithCheck(args);
        final Entity entity = extractEntityWithCheck(args);
        final UpdateOption<ConditionBean> option = extractUpdateOptionWithCheck(args);

        // arguments for execution (not contains an option)
        final String[] argNames = new String[] { "pmb", "entity" };
        final Class<?>[] argTypes = new Class<?>[] { cb.getClass(), entity.getClass() };

        // build SQL
        final List<TnPropertyType> boundPropTypeList = new ArrayList<TnPropertyType>();
        final String twoWaySql = buildQueryUpdateTwoWaySql(entity, cb, option, boundPropTypeList);
        if (twoWaySql == null) { // means non-modification
            return 0; // non execute
        }

        // execute
        final CommandContext context = createCommandContext(twoWaySql, argNames, argTypes, args);
        final TnCommandContextHandler handler = createCommandContextHandler(context);
        handler.setExceptionMessageSqlArgs(context.getBindVariables());
        handler.setBoundPropTypeList(boundPropTypeList);
        final int rows = handler.execute(args);
        return Integer.valueOf(rows);
    }

    // ===================================================================================
    //                                                                    Analyze Argument
    //                                                                    ================
    protected ConditionBean extractConditionBeanWithCheck(Object[] args) {
        assertArgument(args);
        Object fisrtArg = args[0];
        if (!(fisrtArg instanceof ConditionBean)) {
            String msg = "The type of first argument should be " + ConditionBean.class + ":";
            msg = msg + " type=" + fisrtArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (ConditionBean) fisrtArg;
    }

    protected Entity extractEntityWithCheck(Object[] args) {
        assertArgument(args);
        Object secondArg = args[1];
        if (!(secondArg instanceof Entity)) {
            String msg = "The type of second argument should be " + Entity.class + ":";
            msg = msg + " type=" + secondArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (Entity) secondArg;
    }

    protected UpdateOption<ConditionBean> extractUpdateOptionWithCheck(Object[] args) {
        assertArgument(args);
        if (args.length < 3) {
            return null;
        }
        final Object secondArg = args[2];
        if (secondArg == null) {
            return null;
        }
        if (!(secondArg instanceof UpdateOption<?>)) {
            String msg = "The type of third argument should be " + UpdateOption.class + ":";
            msg = msg + " type=" + secondArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        @SuppressWarnings("unchecked")
        final UpdateOption<ConditionBean> option = (UpdateOption<ConditionBean>) secondArg;
        return option;
    }

    protected void assertArgument(Object[] args) {
        if (args == null || args.length <= 1) {
            String msg = "The arguments should have two argument at least! But:";
            msg = msg + " args=" + (args != null ? args.length : null);
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                           Build SQL
    //                                                                           =========
    /**
     * @param entity Entity. (NotNull)
     * @param cb Condition-bean. (NotNull)
     * @param option The option of update. (Nullable)
     * @param boundPropTypeList The type list of bound property. (NotNull, Empty)
     * @return The two-way SQL of query update. (Nullable: if non-modification, return null.)
     */
    protected String buildQueryUpdateTwoWaySql(Entity entity, ConditionBean cb, UpdateOption<ConditionBean> option,
            List<TnPropertyType> boundPropTypeList) {
        final Map<String, String> columnParameterMap = new LinkedHashMap<String, String>();
        final DBMeta dbmeta = entity.getDBMeta();
        final Set<String> modifiedPropertyNames = entity.modifiedProperties();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        for (ColumnInfo columnInfo : columnInfoList) {
            if (columnInfo.isOptimisticLock()) {
                continue; // optimistic lock columns are processed after here
            }
            final String columnDbName = columnInfo.getColumnDbName();
            if (option != null && option.hasStatement(columnDbName)) {
                final String statement = option.buildStatement(columnDbName);
                columnParameterMap.put(columnDbName, statement);
                continue;
            }
            final String propertyName = columnInfo.getPropertyName();
            if (modifiedPropertyNames.contains(propertyName)) {
                final Object value = columnInfo.read(entity);
                if (value != null) {
                    columnParameterMap.put(columnDbName, "/*entity." + propertyName + "*/null");

                    // Add property type
                    TnPropertyType propertyType = _beanMetaData.getPropertyType(propertyName);
                    boundPropTypeList.add(propertyType);
                } else {
                    columnParameterMap.put(columnDbName, "null");
                }
                continue;
            }
        }
        if (columnParameterMap.isEmpty()) {
            return null;
        }
        if (dbmeta.hasVersionNo()) {
            final ColumnInfo columnInfo = dbmeta.getVersionNoColumnInfo();
            final String columnName = columnInfo.getColumnDbName();
            columnParameterMap.put(columnName, columnName + " + 1");
        }
        if (dbmeta.hasUpdateDate()) {
            ColumnInfo columnInfo = dbmeta.getUpdateDateColumnInfo();
            columnInfo.write(entity, ResourceContext.getAccessTimestamp());
            final String columnName = columnInfo.getColumnDbName();
            final String propertyName = columnInfo.getPropertyName();
            columnParameterMap.put(columnName, "/*entity." + propertyName + "*/null");

            // add property type
            boundPropTypeList.add(_beanMetaData.getPropertyType(propertyName));
        }
        return cb.getSqlClause().getClauseQueryUpdate(columnParameterMap);
    }

    // ===================================================================================
    //                                                                     Command Context
    //                                                                     ===============
    protected CommandContext createCommandContext(String twoWaySql, String[] argNames, Class<?>[] argTypes,
            Object[] args) {
        CommandContext context;
        {
            SqlAnalyzer analyzer = createSqlAnalyzer(twoWaySql);
            Node node = analyzer.analyze();
            CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        return context;
    }

    protected SqlAnalyzer createSqlAnalyzer(String sql) {
        return ResourceContext.createSqlAnalyzer(sql, true);
    }

    protected TnCommandContextHandler createCommandContextHandler(CommandContext context) {
        return new TnCommandContextHandler(_dataSource, _statementFactory, context);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return _beanMetaData;
    }

    public void setBeanMetaData(TnBeanMetaData beanMetaData) {
        this._beanMetaData = beanMetaData;
    }
}
