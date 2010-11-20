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
import java.util.Collection;
import java.util.List;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The type of Oracle's array for a property of collection type.
 * @author jflute
 */
public abstract class OracleArrayType implements ValueType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final int _sqlType;
    protected final String _arrayTypeName;
    protected final Class<?> _elementType;

    // when element is STRUCT type
    protected final String _structTypeName;
    protected final Entity _entityPrototype; // not always

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OracleArrayType(String arrayTypeName, Class<?> elementType, String structTypeName) {
        _sqlType = Types.ARRAY;
        _arrayTypeName = arrayTypeName;
        _elementType = elementType;
        _structTypeName = structTypeName;
        if (Entity.class.isAssignableFrom(elementType)) {
            _entityPrototype = (Entity) DfReflectionUtil.newInstance(elementType);
        } else {
            _entityPrototype = null;
        }
    }

    // ===================================================================================
    //                                                                           Get Value
    //                                                                           =========
    public Object getValue(ResultSet rs, int index) throws SQLException {
        return toMappedCollection(rs.getArray(index));
    }

    public Object getValue(ResultSet rs, String columnName) throws SQLException {
        return toMappedCollection(rs.getArray(columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toMappedCollection(cs.getArray(index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toMappedCollection(cs.getArray(parameterName));
    }

    protected Object toMappedCollection(Object oracleArray) throws SQLException {
        if (oracleArray == null) {
            return null;
        }
        final Object[] array = (Object[]) toStandardArray(oracleArray);
        if (array == null || array.length == 0) {
            return DfCollectionUtil.newArrayList();
        }
        final List<Object> resultList = DfCollectionUtil.newArrayList();
        final Class<? extends Object> elementType = array[0].getClass();
        if (_entityPrototype != null) {
            for (Object element : array) {
                resultList.add(mappingEntity(element));
            }
        } else {
            for (Object element : array) {
                resultList.add(mappingScalarValue(element, elementType));
            }
        }
        return resultList;
    }

    protected Entity mappingEntity(Object oracleStruct) {
        final String propertyName = null; // TODO
        final DBMeta dbmeta = _entityPrototype.getDBMeta();
        final Entity entity = dbmeta.newEntity();
        dbmeta.setupEntityProperty(propertyName, entity, null);
        return entity;
    }

    protected Object mappingScalarValue(Object value, Class<?> elementType) {
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
        if (value == null) { // basically for insert and update
            setNull(ps, index);
        } else {
            ps.setObject(index, toOracleArray(conn, _arrayTypeName, toMappedArray(conn, value)));
        }
    }

    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setObject(parameterName, toOracleArray(conn, _arrayTypeName, toMappedArray(conn, value)));
        }
    }

    protected Object toMappedArray(Connection conn, Object value) throws SQLException {
        if (!(value instanceof Collection<?>)) {
            return value;
        }
        final Object[] array = ((Collection<?>) value).toArray();
        if (array.length == 0) {
            return array;
        }
        if (_entityPrototype != null) {
            final List<Object> structList = new ArrayList<Object>();
            for (Object element : array) {
                final Entity entity = (Entity) element; // must be entity
                structList.add(mappingStruct(conn, entity));
            }
            return structList;
        } else {
            return array;
        }
    }

    protected Object mappingStruct(Connection conn, Entity entity) throws SQLException {
        final DBMeta dbmeta = _entityPrototype.getDBMeta();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final List<Object> attrList = new ArrayList<Object>();
        for (ColumnInfo columnInfo : columnInfoList) {
            attrList.add(columnInfo.read(entity));
        }
        return toOracleStruct(conn, _structTypeName, attrList.toArray());
    }

    // ===================================================================================
    //                                                                        Null Setting
    //                                                                        ============
    protected void setNull(PreparedStatement ps, int index) throws SQLException {
        ps.setNull(index, getSqlType(), _arrayTypeName);
    }

    protected void setNull(CallableStatement cs, String parameterName) throws SQLException {
        cs.setNull(parameterName, getSqlType(), _arrayTypeName);
    }

    // ===================================================================================
    //                                                                       Out Parameter
    //                                                                       =============
    public void registerOutParameter(Connection conn, CallableStatement cs, int index) throws SQLException {
        cs.registerOutParameter(index, getSqlType(), _arrayTypeName);
    }

    public void registerOutParameter(Connection conn, CallableStatement cs, String parameterName) throws SQLException {
        cs.registerOutParameter(parameterName, getSqlType(), _arrayTypeName);
    }

    // ===================================================================================
    //                                                                       Oracle's Type
    //                                                                       =============
    /**
     * Convert an array value to the Oracle's array.
     * @param conn The connection for the database. (NotNull)
     * @param arrayTypeName The name of array type for Oracle. (NotNull)
     * @param arrayValue The value of array. (NotNull) 
     * @return The instance of oracle.sql.ARRAY for the array argument. (NotNull)
     * @throws java.sql.SQLException
     */
    protected abstract Object toOracleArray(Connection conn, String arrayTypeName, Object arrayValue)
            throws SQLException;

    /**
     * Convert the Oracle's array to a standard array.
     * @param oracleArray The value of Oracle's array (oracle.sql.ARRAY). (NotNull) 
     * @return The instance of standard array for the Oracle's array argument. (NotNull)
     * @throws java.sql.SQLException
     */
    protected abstract Object toStandardArray(Object oracleArray) throws SQLException;

    protected abstract Object toOracleStruct(Connection conn, String structTypeName, Object[] attrs)
            throws SQLException;

    // ===================================================================================
    //                                                                            SQL Type
    //                                                                            ========
    public int getSqlType() {
        return _sqlType;
    }
}