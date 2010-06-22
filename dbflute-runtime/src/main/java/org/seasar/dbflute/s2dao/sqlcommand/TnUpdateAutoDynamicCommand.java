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

import org.seasar.dbflute.XLog;
import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.TableSqlName;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.sqlhandler.TnUpdateAutoHandler;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnUpdateAutoDynamicCommand extends TnAbstractSqlCommand {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The result for no update as normal execution. */
    private static final Integer NON_UPDATE = Integer.valueOf(1);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnBeanMetaData _beanMetaData;
    protected DBMeta _targetDBMeta;
    protected String[] _propertyNames;
    protected boolean _optimisticLockHandling;
    protected boolean _versionNoAutoIncrementOnMemory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateAutoDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        final Object bean = args[0];
        @SuppressWarnings("unchecked")
        final UpdateOption<ConditionBean> option = (args.length > 1 ? (UpdateOption<ConditionBean>) args[1] : null);

        final TnBeanMetaData bmd = getBeanMetaData();
        final TnPropertyType[] propertyTypes = createUpdatePropertyTypes(bmd, bean, getPropertyNames(), option);
        if (propertyTypes.length == 0) {
            if (isLogEnabled()) {
                log(createNonUpdateLogMessage(bean, bmd));
            }
            return NON_UPDATE;
        }
        final String sql = createUpdateSql(bmd, propertyTypes, bean, option);
        final TnUpdateAutoHandler handler = createUpdateAutoHandler(bmd, propertyTypes, sql, option);
        final Object[] realArgs = new Object[] { bean };
        handler.setExceptionMessageSqlArgs(realArgs);
        final int result = handler.execute(realArgs);

        // [Comment Out]: This statement moved to the handler at [DBFlute-0.8.0].
        //if (isCheckSingleRowUpdate() && i < 1) {
        //    throw createNotSingleRowUpdatedRuntimeException(args[0], i);
        //}

        return Integer.valueOf(result);
    }

    protected TnUpdateAutoHandler createUpdateAutoHandler(TnBeanMetaData bmd, TnPropertyType[] boundPropTypes,
            String sql, UpdateOption<ConditionBean> option) {
        final TnUpdateAutoHandler handler = new TnUpdateAutoHandler(getDataSource(), getStatementFactory(), bmd,
                boundPropTypes);
        handler.setSql(sql);
        handler.setOptimisticLockHandling(_optimisticLockHandling); // [DBFlute-0.8.0]
        handler.setVersionNoAutoIncrementOnMemory(_versionNoAutoIncrementOnMemory);
        handler.setUpdateOption(option);
        return handler;
    }

    // abstract because DBFlute uses only modified only
    protected abstract TnPropertyType[] createUpdatePropertyTypes(TnBeanMetaData bmd, Object bean,
            String[] propertyNames, UpdateOption<ConditionBean> option);

    protected String createNonUpdateLogMessage(final Object bean, final TnBeanMetaData bmd) {
        final StringBuilder sb = new StringBuilder();
        final String tableDbName = _targetDBMeta.getTableDbName();
        sb.append("...Skipping update because of non-modification: table=").append(tableDbName);
        final int size = bmd.getPrimaryKeySize();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                sb.append(", primaryKey={");
            } else {
                sb.append(", ");
            }
            final String keyName = bmd.getPrimaryKeyDbName(i);
            sb.append(keyName).append("=");
            sb.append(bmd.getPropertyTypeByColumnName(keyName).getPropertyDesc().getValue(bean));
            if (i == size - 1) {
                sb.append("}");
            }
        }
        return sb.toString();
    }

    /**
     * Create update SQL. The update is by the primary keys.
     * @param bmd The meta data of bean. (NotNull & RequiredPrimaryKeys)
     * @param propertyTypes The types of property for update. (NotNull)
     * @param bean A bean for update for handling version no and so on. (NotNull)
     * @param option An option of update. (Nullable)
     * @return The update SQL. (NotNull)
     */
    protected String createUpdateSql(TnBeanMetaData bmd, TnPropertyType[] propertyTypes, Object bean,
            UpdateOption<ConditionBean> option) {
        final TableSqlName tableSqlName = _targetDBMeta.getTableSqlName();
        if (bmd.getPrimaryKeySize() == 0) {
            String msg = "The table '" + tableSqlName + "' does not have primary keys!";
            throw new IllegalStateException(msg);
        }
        final StringBuilder sb = new StringBuilder(100);
        sb.append("update ").append(tableSqlName).append(" set ");
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; i++) {
            final TnPropertyType pt = propertyTypes[i];
            final String columnDbName = pt.getColumnDbName();
            final ColumnSqlName columnSqlName = pt.getColumnSqlName();
            final String propertyName = pt.getPropertyName();
            if (i > 0) {
                sb.append(", ");
            }
            if (propertyName.equalsIgnoreCase(versionNoPropertyName)) {
                if (!isVersionNoAutoIncrementOnMemory()) {
                    setupVersionNoAutoIncrementOnQuery(sb, columnSqlName);
                    continue;
                }
                final Object versionNo = pt.getPropertyDesc().getValue(bean);
                if (versionNo == null) {
                    setupVersionNoAutoIncrementOnQuery(sb, columnSqlName);
                    continue;
                }
            }
            if (option != null && option.hasStatement(columnDbName)) {
                final String statement = option.buildStatement(columnDbName);
                sb.append(columnSqlName).append(" = ").append(statement);
                continue;
            }
            sb.append(columnSqlName).append(" = ?");
        }
        sb.append(ln()).append(" where ");
        for (int i = 0; i < bmd.getPrimaryKeySize(); i++) { // never zero loop
            sb.append(bmd.getPrimaryKeySqlName(i)).append(" = ? and ");
        }
        sb.setLength(sb.length() - 5); // for deleting extra ' and '
        if (_optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            TnPropertyType pt = bmd.getVersionNoPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
        if (_optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            TnPropertyType pt = bmd.getTimestampPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
        return sb.toString();
    }

    protected void setupVersionNoAutoIncrementOnQuery(StringBuilder sb, ColumnSqlName columnSqlName) {
        sb.append(columnSqlName).append(" = ").append(columnSqlName).append(" + 1");
    }

    // ===================================================================================
    //                                                                  Execute Status Log
    //                                                                  ==================
    protected void log(String msg) {
        XLog.log(msg);
    }

    protected boolean isLogEnabled() {
        return XLog.isLogEnabled();
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
    public TnBeanMetaData getBeanMetaData() {
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

    public String[] getPropertyNames() {
        return _propertyNames;
    }

    public void setPropertyNames(String[] propertyNames) {
        this._propertyNames = propertyNames;
    }

    public void setOptimisticLockHandling(boolean optimisticLockHandling) {
        this._optimisticLockHandling = optimisticLockHandling;
    }

    protected boolean isVersionNoAutoIncrementOnMemory() {
        return _versionNoAutoIncrementOnMemory;
    }

    public void setVersionNoAutoIncrementOnMemory(boolean versionNoAutoIncrementOnMemory) {
        this._versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
    }
}
