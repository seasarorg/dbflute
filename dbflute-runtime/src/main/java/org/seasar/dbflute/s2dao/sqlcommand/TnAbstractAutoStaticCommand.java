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
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

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
    protected TnPropertyType[] _propertyTypes;
    protected boolean _optimisticLockHandling;
    protected boolean _versionNoAutoIncrementOnMemory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData);
        this._targetDBMeta = targetDBMeta;
        this._optimisticLockHandling = optimisticLockHandling;
        this._versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
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

    protected abstract void setupPropertyTypes(String[] propertyNames);

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
            final String name = propertyType.getPropertyName();
            final TnIdentifierGenerator generator = getBeanMetaData().getIdentifierGenerator(name);
            return generator.isSelfGenerate();
        }
        return true;
    }

    protected void setupUpdatePropertyTypes(String[] propertyNames) {
        final List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        for (int i = 0; i < propertyNames.length; ++i) {
            final TnPropertyType pt = getBeanMetaData().getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                continue;
            }
            types.add(pt);
        }
        if (types.size() == 0) {
            String msg = "The property type that is not primary key was not found:";
            msg = msg + " propertyNames=" + Arrays.asList(propertyNames);
            throw new IllegalStateException(msg);
        }
        _propertyTypes = (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
    }

    protected void setupDeletePropertyTypes(String[] propertyNames) {
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
            TnPropertyType pt = _propertyTypes[i];
            if (isInsertTarget(pt)) {
                sb.append("?, ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(")");
        setSql(sb.toString());
    }

    protected void setupUpdateSql() {
        checkPrimaryKey();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("update ");
        sb.append(_targetDBMeta.getTableSqlName());
        sb.append(" set ");
        final String versionNoPropertyName = getBeanMetaData().getVersionNoPropertyName();
        for (int i = 0; i < _propertyTypes.length; ++i) {
            final TnPropertyType pt = _propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName) && !_versionNoAutoIncrementOnMemory) {
                sb.append(pt.getColumnSqlName()).append(" = ").append(pt.getColumnSqlName()).append(" + 1, ");
                continue;
            }
            sb.append(pt.getColumnSqlName()).append(" = ?, ");
        }
        sb.setLength(sb.length() - 2);
        setupUpdateWhere(sb);
        setSql(sb.toString());
    }

    protected void setupDeleteSql() {
        checkPrimaryKey();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("delete from ");
        sb.append(_targetDBMeta.getTableSqlName());
        setupUpdateWhere(sb);
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

    protected void setupUpdateWhere(StringBuilder sb) {
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
