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
package org.seasar.dbflute.s2dao.valuetype.plugin;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.jdbc.PhysicalConnectionDigger;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The type of Oracle's STRUCT for a property of collection type.
 * @author jflute
 */
public abstract class OracleStructType implements ValueType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final int _sqlType;
    protected final String _structTypeName;
    protected final Class<?> _entityType;

    // when it could get its meta data
    protected final Entity _entityPrototype;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OracleStructType(String structTypeName, Class<?> entityType) {
        _sqlType = Types.STRUCT;
        _structTypeName = structTypeName;
        _entityType = entityType;
        if (Entity.class.isAssignableFrom(entityType)) {
            _entityPrototype = (Entity) DfReflectionUtil.newInstance(entityType);
        } else {
            _entityPrototype = null;
        }
    }

    // ===================================================================================
    //                                                                           Get Value
    //                                                                           =========
    public Object getValue(ResultSet rs, int index) throws SQLException {
        return toPropertyValue(rs.getObject(index));
    }

    public Object getValue(ResultSet rs, String columnName) throws SQLException {
        return toPropertyValue(rs.getObject(columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toPropertyValue(cs.getObject(index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toPropertyValue(cs.getObject(parameterName));
    }

    protected Entity toPropertyValue(Object oracleStruct) throws SQLException {
        final DBMeta dbmeta = _entityPrototype.getDBMeta();
        final Entity entity = dbmeta.newEntity();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final Object[] attrs = toStandardStructAttributes(oracleStruct);
        int index = 0;
        assertStructAttributeSizeMatched(entity, attrs, columnInfoList);
        for (Object attr : attrs) {
            final ColumnInfo columnInfo = columnInfoList.get(index);
            final Class<?> propertyType = columnInfo.getPropertyType();
            columnInfo.write(entity, mappingScalarToPropertyValue(attr, propertyType));
            ++index;
        }
        return entity;
    }

    protected Object mappingScalarToPropertyValue(Object value, Class<?> propertyType) {
        if (Number.class.isAssignableFrom(propertyType)) {
            value = DfTypeUtil.toNumber(value, propertyType);
        } else if (java.util.Date.class.isAssignableFrom(propertyType)) {
            value = DfTypeUtil.toPointDate(value, propertyType);
        }
        return value;
    }

    protected void assertStructAttributeSizeMatched(Entity entity, Object[] attrs, List<ColumnInfo> columnInfoList) {
        if (attrs.length != columnInfoList.size()) {
            throwStructAttributeSizeUnmatchedException(entity, attrs, columnInfoList);
        }
    }

    protected void throwStructAttributeSizeUnmatchedException(Entity entity, Object[] attrs,
            List<ColumnInfo> columnInfoList) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The size of struct attributes does not match with column list of entity:");
        br.addItem("Entity");
        br.addElement(DfTypeUtil.toClassTitle(entity));
        br.addItem("Attribute Size");
        br.addElement(attrs.length);
        br.addItem("Column List Size");
        br.addElement(columnInfoList.size());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    protected Object mappingScalar(Object value, Class<?> elementType) {
        if (Number.class.isAssignableFrom(elementType)) {
            value = DfTypeUtil.toNumber(value, elementType);
        } else if (java.util.Date.class.isAssignableFrom(elementType)) {
            value = DfTypeUtil.toPointDate(value, elementType);
        }
        return value;
    }

    // ===================================================================================
    //                                                                          Bind Value
    //                                                                          ==========
    public void bindValue(Connection conn, PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setObject(index, toBindValue(conn, index, value));
        }
    }

    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setObject(parameterName, toBindValue(conn, parameterName, value));
        }
    }

    protected Object toBindValue(Connection conn, Object parameterExp, Object value) throws SQLException {
        assertStructPropertyValueNotEntity(parameterExp, value);
        return mappingStruct(conn, (Entity) value);
    }

    protected Object mappingStruct(Connection conn, Entity entity) throws SQLException {
        final DBMeta dbmeta = _entityPrototype.getDBMeta();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final List<Object> attrList = new ArrayList<Object>();
        for (ColumnInfo columnInfo : columnInfoList) {
            attrList.add(mappingScalarToSqlValue(columnInfo.read(entity)));
        }
        final String structTypeName = dbmeta.getTableSqlName().toString();
        return toOracleStruct(getOracleConnection(conn), structTypeName, attrList.toArray());
    }

    protected Object mappingScalarToSqlValue(Object value) {
        if (value == null) {
            return null;
        }
        final Class<? extends Object> propertyType = value.getClass();
        if (java.util.Date.class.equals(propertyType)) {
            return DfTypeUtil.toTimestamp(value); // TODO use oracle.sql.DATE?
        }
        return value;
    }

    protected void assertStructPropertyValueNotEntity(Object parameterExp, Object value) {
        if (!(value instanceof Entity)) {
            throwStructPropertyValueNotEntityException(parameterExp, value);
        }
    }

    protected void throwStructPropertyValueNotEntityException(Object parameterExp, Object value) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The property value for struct should be entity type:");
        br.addItem("Struct");
        br.addElement(_structTypeName);
        br.addItem("Entity");
        br.addElement(DfTypeUtil.toClassTitle(_entityType));
        br.addItem("Parameter");
        br.addElement(parameterExp);
        br.addItem("Property Value");
        if (value != null) {
            br.addElement(value.getClass());
        }
        br.addElement(value);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                        Null Setting
    //                                                                        ============
    protected void setNull(PreparedStatement ps, int index) throws SQLException {
        ps.setNull(index, getSqlType(), _structTypeName);
    }

    protected void setNull(CallableStatement cs, String parameterName) throws SQLException {
        cs.setNull(parameterName, getSqlType(), _structTypeName);
    }

    // ===================================================================================
    //                                                                       Out Parameter
    //                                                                       =============
    public void registerOutParameter(Connection conn, CallableStatement cs, int index) throws SQLException {
        cs.registerOutParameter(index, getSqlType(), _structTypeName);
    }

    public void registerOutParameter(Connection conn, CallableStatement cs, String parameterName) throws SQLException {
        cs.registerOutParameter(parameterName, getSqlType(), _structTypeName);
    }

    // ===================================================================================
    //                                                                       Oracle's Type
    //                                                                       =============
    /**
     * Convert the Oracle's STRUCT to a standard attributes.
     * @param conn The Oracle native connection for the database. (NotNull)
     * @param structTypeName The name of STRUCT type for Oracle. (NotNull)
     * @param attrs The array of attribute value. (NotNull) 
     * @return The STRUCT type contained to attribute values. (NotNull)
     * @throws java.sql.SQLException
     */
    protected abstract Object toOracleStruct(Connection conn, String structTypeName, Object[] attrs)
            throws SQLException;

    /**
     * Convert the Oracle's STRUCT to a standard attributes.
     * @param oracleStruct The value of Oracle's STRUCT (oracle.sql.STRUCT). (NotNull) 
     * @return The array of attribute value as standard type. (NotNull)
     * @throws java.sql.SQLException
     */
    protected abstract Object[] toStandardStructAttributes(Object oracleStruct) throws SQLException;

    protected Connection getOracleConnection(Connection conn) throws SQLException {
        return getPhysicalConnectionDigger().getConnection(conn);
    }

    protected abstract PhysicalConnectionDigger getPhysicalConnectionDigger();

    // ===================================================================================
    //                                                                            SQL Type
    //                                                                            ========
    public int getSqlType() {
        return _sqlType;
    }
}