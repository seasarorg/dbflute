package org.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.dbflute.exception.EntityAlreadyUpdatedException;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.util.DfTypeUtil;
import org.seasar.extension.jdbc.ValueType;
import org.dbflute.s2dao.beans.PropertyDesc;


/**
 * @author DBFlute(AutoGenerator)
 */
public abstract class InternalAbstractAutoHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnBeanMetaData beanMetaData;
    protected Object[] bindVariables;
    protected ValueType[] bindVariableValueTypes;
    protected Timestamp timestamp;
    protected Integer versionNo;
    protected TnPropertyType[] propertyTypes;
    protected boolean optimisticLockHandling;
    protected boolean versionNoAutoIncrementOnMemory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalAbstractAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData, TnPropertyType[] propertyTypes) {
        super(dataSource, statementFactory);
        this.beanMetaData = beanMetaData;
        this.propertyTypes = propertyTypes;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        Connection connection = getConnection();
        try {
            return execute(connection, args[0]);
        } finally {
            close(connection);
        }
    }

    public int execute(Object[] args, Class<?>[] argTypes) {
        return execute(args);
    }

    protected int execute(Connection connection, Object bean) {
        preUpdateBean(bean);
        setupBindVariables(bean);
        logSql(bindVariables, getArgTypes(bindVariables));
        PreparedStatement ps = prepareStatement(connection);
        int ret = -1;
        try {
            bindArgs(ps, bindVariables, bindVariableValueTypes);
            ret = executeUpdate(ps);
        } finally {
            close(ps);
        }
        if (optimisticLockHandling && ret != 1) {
            throw createEntityAlreadyUpdatedException(bean, ret);
        }
        postUpdateBean(bean, ret);
        return ret;
    }

    protected void bindArgs(PreparedStatement ps, Object[] args, ValueType[] valueTypes) {
        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            ValueType valueType = valueTypes[i];
            try {
                valueType.bindValue(ps, i + 1, args[i]);
            } catch (SQLException e) {
                handleSQLException(e, ps);
            }
        }
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
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(new Timestamp(new Date().getTime()));
                varList.add(getTimestamp());
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                setVersionNo(new Integer(0));
                varList.add(getVersionNo());
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[])varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupUpdateBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        final TnBeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(new Timestamp(new Date().getTime()));
                varList.add(getTimestamp());
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                if (!isVersionNoAutoIncrementOnMemory()) {
                    continue;// because of always 'VERSION_NO = VERSION_NO + 1'
                }
                Object value = pt.getPropertyDesc().getValue(bean);
                if (value == null) {
                    continue;// because of 'VERSION_NO = VERSION_NO + 1'
                }
                int intValue = DfTypeUtil.toPrimitiveInt(value) + 1;
                setVersionNo(new Integer(intValue));
                varList.add(getVersionNo());
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
        setBindVariableValueTypes((ValueType[])varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void addAutoUpdateWhereBindVariables(List<Object> varList, List<ValueType> varValueTypeList, Object bean) {
        TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            TnPropertyType pt = bmd.getPropertyTypeByColumnName(bmd.getPrimaryKey(i));
            PropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            TnPropertyType pt = bmd.getVersionNoPropertyType();
            PropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            TnPropertyType pt = bmd.getTimestampPropertyType();
            PropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
    }

    protected void updateTimestampIfNeed(Object bean) {
        if (getTimestamp() != null) {
            PropertyDesc pd = getBeanMetaData().getTimestampPropertyType().getPropertyDesc();
            pd.setValue(bean, getTimestamp());
        }
    }

    protected void updateVersionNoIfNeed(Object bean) {
        if (getVersionNo() != null) {
            PropertyDesc pd = getBeanMetaData().getVersionNoPropertyType().getPropertyDesc();
            pd.setValue(bean, getVersionNo());
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    protected Object[] getBindVariables() {
        return bindVariables;
    }

    protected void setBindVariables(Object[] bindVariables) {
        this.bindVariables = bindVariables;
    }

    protected ValueType[] getBindVariableValueTypes() {
        return bindVariableValueTypes;
    }

    protected void setBindVariableValueTypes(ValueType[] bindVariableValueTypes) {
        this.bindVariableValueTypes = bindVariableValueTypes;
    }

    protected Timestamp getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    protected Integer getVersionNo() {
        return versionNo;
    }

    protected void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    protected TnPropertyType[] getPropertyTypes() {
        return propertyTypes;
    }

    protected void setPropertyTypes(TnPropertyType[] propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    public boolean isOptimisticLockHandling() {
        return optimisticLockHandling;
    }

    public void setOptimisticLockHandling(boolean optimisticLockHandling) {
        this.optimisticLockHandling = optimisticLockHandling;
    }

    protected boolean isVersionNoAutoIncrementOnMemory() {
        return versionNoAutoIncrementOnMemory;
    }

    public void setVersionNoAutoIncrementOnMemory(boolean versionNoAutoIncrementOnMemory) {
        this.versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
    }
}
