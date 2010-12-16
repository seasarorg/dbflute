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

import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractAutoStaticCommand extends TnAbstractStaticCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMeta _targetDBMeta;
    protected final boolean _optimisticLockHandling;
    protected final boolean _versionNoAutoIncrementOnMemory;
    protected final InsertOption<? extends ConditionBean> _insertOption;
    protected final DeleteOption<? extends ConditionBean> _deleteOption;
    protected TnPropertyType[] _propertyTypes;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory, InsertOption<? extends ConditionBean> insertOption,
            DeleteOption<? extends ConditionBean> deleteOption) {
        super(dataSource, statementFactory, beanMetaData);
        _targetDBMeta = targetDBMeta;
        _optimisticLockHandling = optimisticLockHandling;
        _versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
        _insertOption = insertOption;
        _deleteOption = deleteOption;
        setupPropertyTypes(propertyNames);
        setupSql();
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) { // NOT for batch (batch should override this)
        final TnAbstractAutoHandler handler = createAutoHandler();
        handler.setOptimisticLockHandling(_optimisticLockHandling);
        handler.setVersionNoAutoIncrementOnMemory(_versionNoAutoIncrementOnMemory);
        handler.setSql(getSql());
        handler.setExceptionMessageSqlArgs(args);
        int rows = handler.execute(args);
        return Integer.valueOf(rows);
    }

    protected TnPropertyType[] getPropertyTypes() {
        return _propertyTypes;
    }

    protected void setPropertyTypes(TnPropertyType[] propertyTypes) {
        this._propertyTypes = propertyTypes;
    }

    protected abstract TnAbstractAutoHandler createAutoHandler();

    protected abstract void setupPropertyTypes(String[] propertyNames); // called by constructor

    // ===================================================================================
    //                                                                              Insert
    //                                                                              ======
    protected void setupInsertPropertyTypes(String[] propertyNames) {
        final List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        for (int i = 0; i < propertyNames.length; ++i) {
            final TnPropertyType pt = getBeanMetaData().getPropertyType(propertyNames[i]);
            if (isInsertTarget(pt)) {
                types.add(pt);
            }
        }
        _propertyTypes = (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
    }

    protected boolean isInsertTarget(TnPropertyType propertyType) {
        if (propertyType.isPrimaryKey()) {
            if (_insertOption == null || !_insertOption.isPrimaryIdentityInsertDisabled()) {
                final String name = propertyType.getPropertyName();
                final TnIdentifierGenerator generator = getBeanMetaData().getIdentifierGenerator(name);
                return generator.isSelfGenerate();
            }
        }
        return true;
    }

    protected abstract void setupSql();

    protected void setupInsertSql() {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("insert into ");
        sb.append(_targetDBMeta.getTableSqlName());
        sb.append(" (");
        for (int i = 0; i < _propertyTypes.length; ++i) {
            final TnPropertyType pt = _propertyTypes[i];
            if (isInsertTarget(pt)) {
                sb.append(pt.getColumnSqlName());
                sb.append(", ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(") values (");
        for (int i = 0; i < _propertyTypes.length; ++i) {
            final TnPropertyType pt = _propertyTypes[i];
            if (isInsertTarget(pt)) {
                sb.append("?, ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(")");
        setSql(sb.toString());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    // *static update is unused on DBFlute

    // ===================================================================================
    //                                                                              Delete
    //                                                                              ======
    protected void setupDeleteSql() {
        checkPrimaryKey();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("delete from ");
        sb.append(_targetDBMeta.getTableSqlName());
        setupDeleteWhere(sb);
        setSql(sb.toString());
    }

    protected void checkPrimaryKey() {
        final TnBeanMetaData bmd = getBeanMetaData();
        if (bmd.getPrimaryKeySize() == 0) {
            String msg = "The primary key was not found:";
            msg = msg + " bean=" + bmd.getBeanClass();
            throw new IllegalStateException(msg);
        }
    }

    protected void setupDeleteWhere(StringBuilder sb) {
        final TnBeanMetaData bmd = getBeanMetaData();
        sb.append(" where ");
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            sb.append(bmd.getPrimaryKeySqlName(i)).append(" = ? and ");
        }
        sb.setLength(sb.length() - 5);
        if (_optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            TnPropertyType pt = bmd.getVersionNoPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
        if (_optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            TnPropertyType pt = bmd.getTimestampPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
    }
}
