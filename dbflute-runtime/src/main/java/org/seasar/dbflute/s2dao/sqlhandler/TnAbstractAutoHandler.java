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
package org.seasar.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractAutoHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TnBeanMetaData _beanMetaData;
    protected final TnPropertyType[] _boundPropTypes; // not only completely bounds (needs to filter)
    protected Object[] _bindVariables;
    protected ValueType[] _bindVariableValueTypes;
    protected Timestamp _timestamp;
    protected Integer _versionNo;
    protected boolean _optimisticLockHandling;
    protected boolean _versionNoAutoIncrementOnMemory; // for filtering
    protected UpdateOption<ConditionBean> _updateOption; // for filtering

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractAutoHandler(DataSource dataSource, StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            TnPropertyType[] boundPropTypes) {
        super(dataSource, statementFactory);
        this._beanMetaData = beanMetaData;
        this._boundPropTypes = boundPropTypes;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        final Connection conn = getConnection();
        try {
            return execute(conn, args[0]);
        } finally {
            close(conn);
        }
    }

    public int execute(Object[] args, Class<?>[] argTypes) {
        return execute(args);
    }

    protected int execute(Connection conn, Object bean) {
        preUpdateBean(bean);
        setupBindVariables(bean);
        logSql(_bindVariables, getArgTypes(_bindVariables));
        final PreparedStatement ps = prepareStatement(conn);
        final int ret;
        try {
            bindArgs(conn, ps, _bindVariables, _bindVariableValueTypes);
            ret = executeUpdate(ps);
        } finally {
            close(ps);
        }
        if (_optimisticLockHandling && ret < 1) { // means no update (contains minus just in case)
            throw createEntityAlreadyUpdatedException(bean, ret);
        }
        postUpdateBean(bean, ret);
        return ret;
    }

    protected EntityAlreadyUpdatedException createEntityAlreadyUpdatedException(Object bean, int rows) {
        return new EntityAlreadyUpdatedException(bean, rows);
    }

    // ===================================================================================
    //                                                                       Pre/Post Bean
    //                                                                       =============
    protected void preUpdateBean(Object bean) {
    }

    protected void postUpdateBean(Object bean, int ret) {
    }

    // ===================================================================================
    //                                                                       Bind Setupper
    //                                                                       =============
    protected abstract void setupBindVariables(Object bean);

    protected void setupInsertBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        final TnBeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < _boundPropTypes.length; ++i) {
            final TnPropertyType pt = _boundPropTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(ResourceContext.getAccessTimestamp());
                varList.add(_timestamp);
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                setVersionNo(Integer.valueOf(0));
                varList.add(_versionNo);
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupUpdateBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        final TnBeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < _boundPropTypes.length; ++i) {
            final TnPropertyType pt = _boundPropTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(ResourceContext.getAccessTimestamp());
                varList.add(_timestamp);
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                if (!_versionNoAutoIncrementOnMemory) { // means OnQuery
                    continue; // because of 'VERSION_NO = VERSION_NO + 1'
                }
                final Object value = pt.getPropertyDesc().getValue(bean); // already null-checked
                final int intValue = DfTypeUtil.toPrimitiveInt(value) + 1;
                setVersionNo(Integer.valueOf(intValue));
                varList.add(_versionNo);
            } else if (_updateOption != null && _updateOption.hasStatement(pt.getColumnDbName())) {
                continue; // because of 'FOO_COUNT = FOO_COUNT + 1'
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupDeleteBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void addAutoUpdateWhereBindVariables(List<Object> varList, List<ValueType> varValueTypeList, Object bean) {
        final TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            final TnPropertyType pt = bmd.getPropertyTypeByColumnName(bmd.getPrimaryKeyDbName(i));
            final DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (_optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            final TnPropertyType pt = bmd.getVersionNoPropertyType();
            final DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (_optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            final TnPropertyType pt = bmd.getTimestampPropertyType();
            final DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
    }

    protected void updateTimestampIfNeed(Object bean) {
        if (_timestamp != null) {
            final DfPropertyDesc pd = getBeanMetaData().getTimestampPropertyType().getPropertyDesc();
            pd.setValue(bean, _timestamp);
        }
    }

    protected void updateVersionNoIfNeed(Object bean) {
        if (_versionNo != null) {
            final DfPropertyDesc pd = getBeanMetaData().getVersionNoPropertyType().getPropertyDesc();
            pd.setValue(bean, _versionNo);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return _beanMetaData;
    }

    protected Object[] getBindVariables() {
        return _bindVariables;
    }

    protected void setBindVariables(Object[] bindVariables) {
        this._bindVariables = bindVariables;
    }

    protected ValueType[] getBindVariableValueTypes() {
        return _bindVariableValueTypes;
    }

    protected void setBindVariableValueTypes(ValueType[] bindVariableValueTypes) {
        this._bindVariableValueTypes = bindVariableValueTypes;
    }

    protected void setTimestamp(Timestamp timestamp) {
        this._timestamp = timestamp;
    }

    protected void setVersionNo(Integer versionNo) {
        this._versionNo = versionNo;
    }

    public void setOptimisticLockHandling(boolean optimisticLockHandling) {
        this._optimisticLockHandling = optimisticLockHandling;
    }

    public void setVersionNoAutoIncrementOnMemory(boolean versionNoAutoIncrementOnMemory) {
        this._versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
    }

    public void setUpdateOption(UpdateOption<ConditionBean> updateOption) {
        this._updateOption = updateOption;
    }
}
