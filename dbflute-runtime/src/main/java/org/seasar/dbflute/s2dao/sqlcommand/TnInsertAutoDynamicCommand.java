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
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.sqlhandler.TnInsertAutoHandler;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnInsertAutoDynamicCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DataSource _dataSource;
    protected StatementFactory _statementFactory;
    protected TnBeanMetaData _beanMetaData;
    protected DBMeta _targetDBMeta;
    protected String[] _propertyNames;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnInsertAutoDynamicCommand() {
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        if (args == null || args.length == 0) {
            String msg = "The argument 'args' should not be null or empty.";
            throw new IllegalArgumentException(msg);
        }
        final Object bean = args[0];
        final InsertOption<ConditionBean> option = extractInsertOptionChecked(args);

        final TnBeanMetaData bmd = getBeanMetaData();
        final TnPropertyType[] propertyTypes = createInsertPropertyTypes(bmd, bean, getPropertyNames(), option);
        final String sql = createInsertSql(bmd, propertyTypes, option);
        return doExecute(bean, propertyTypes, sql, option);
    }

    protected InsertOption<ConditionBean> extractInsertOptionChecked(Object[] args) {
        if (args.length < 2 || args[1] == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final InsertOption<ConditionBean> option = (InsertOption<ConditionBean>) args[1];
        return option;
    }

    protected Object doExecute(Object bean, TnPropertyType[] propertyTypes, String sql,
            InsertOption<ConditionBean> option) {
        final TnInsertAutoHandler handler = createInsertAutoHandler(propertyTypes, sql, option);
        final Object[] realArgs = new Object[] { bean };
        handler.setExceptionMessageSqlArgs(realArgs);
        final int rows = handler.execute(realArgs);
        return Integer.valueOf(rows);
    }

    // ===================================================================================
    //                                                                       Insert Column
    //                                                                       =============
    protected TnPropertyType[] createInsertPropertyTypes(TnBeanMetaData bmd, Object bean, String[] propertyNames,
            InsertOption<ConditionBean> option) {
        if (0 == propertyNames.length) {
            String msg = "The property name was not found in the bean: " + bean;
            throw new IllegalStateException(msg);
        }
        final List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();

        for (int i = 0; i < propertyNames.length; ++i) {
            final TnPropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                if (option == null || !option.isPrimaryKeyIdentityDisabled()) {
                    final TnIdentifierGenerator generator = bmd.getIdentifierGenerator(pt.getPropertyName());
                    if (!generator.isSelfGenerate()) {
                        continue;
                    }
                }
            } else {
                if (pt.getPropertyDesc().getValue(bean) == null) { // getting by reflection here
                    final String propertyName = pt.getPropertyName();
                    if (!propertyName.equalsIgnoreCase(timestampPropertyName)
                            && !propertyName.equalsIgnoreCase(versionNoPropertyName)) {
                        continue;
                    }
                }
            }
            types.add(pt);
        }
        if (types.isEmpty()) {
            String msg = "The target property type was not found in the bean: " + bean;
            throw new IllegalStateException(msg);
        }
        return (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
    }

    // ===================================================================================
    //                                                                          Insert SQL
    //                                                                          ==========
    protected String createInsertSql(TnBeanMetaData bmd, TnPropertyType[] propertyTypes,
            InsertOption<ConditionBean> option) {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("insert into ");
        sb.append(_targetDBMeta.getTableSqlName());
        sb.append(" (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            final TnPropertyType pt = propertyTypes[i];
            final ColumnSqlName columnSqlName = pt.getColumnSqlName();
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(columnSqlName);
        }
        sb.append(")").append(ln()).append(" values (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        sb.append(")");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    protected TnInsertAutoHandler createInsertAutoHandler(TnPropertyType[] boundPropTypes, String sql,
            InsertOption<ConditionBean> option) {
        final TnInsertAutoHandler handler = new TnInsertAutoHandler(getDataSource(), getStatementFactory(),
                _beanMetaData, boundPropTypes);
        handler.setSql(sql);
        handler.setInsertOption(option);
        return handler;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected DataSource getDataSource() {
        return _dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this._dataSource = dataSource;
    }

    protected StatementFactory getStatementFactory() {
        return _statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this._statementFactory = statementFactory;
    }

    protected TnBeanMetaData getBeanMetaData() {
        return _beanMetaData;
    }

    public void setBeanMetaData(TnBeanMetaData beanMetaData) {
        this._beanMetaData = beanMetaData;
    }

    public DBMeta getTargetDBMeta() {
        return _targetDBMeta;
    }

    public void setTargetDBMeta(DBMeta targetDBMeta) {
        this._targetDBMeta = targetDBMeta;
    }

    protected String[] getPropertyNames() {
        return _propertyNames;
    }

    public void setPropertyNames(String[] propertyNames) {
        this._propertyNames = propertyNames;
    }
}
