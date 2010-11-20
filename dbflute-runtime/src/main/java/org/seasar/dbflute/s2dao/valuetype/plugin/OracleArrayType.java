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
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.jdbc.PhysicalConnectionDigger;
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

    // when element is STRUCT type and it could get its meta data
    protected final Entity _entityPrototype;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OracleArrayType(String arrayTypeName, Class<?> elementType) {
        _sqlType = Types.ARRAY;
        _arrayTypeName = arrayTypeName;
        _elementType = elementType;
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
                resultList.add(mappingScalar(element, elementType));
            }
        }
        return resultList;
    }

    protected Entity mappingEntity(Object oracleStruct) throws SQLException {
        final DBMeta dbmeta = _entityPrototype.getDBMeta();
        final Entity entity = dbmeta.newEntity();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final Object[] attrs = toStandardStructAttributes(oracleStruct);
        int index = 0;
        if (attrs.length != columnInfoList.size()) {
            throwStructAttributeSizeUnmatchedException(entity, attrs, columnInfoList);
        }
        for (Object attr : attrs) {
            columnInfoList.get(index).write(entity, attr);
            ++index;
        }
        return entity;
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
        if (value == null) { // basically for insert and update
            setNull(ps, index);
        } else {
            final Object mappedArray = toMappedArray(conn, value);
            ps.setObject(index, toOracleArray(getOracleConnection(conn), _arrayTypeName, mappedArray));
        }
    }

    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            final Object mappedArray = toMappedArray(conn, value);
            cs.setObject(parameterName, toOracleArray(getOracleConnection(conn), _arrayTypeName, mappedArray));
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
        final String structTypeName = dbmeta.getTableSqlName().toString();
        return toOracleStruct(getOracleConnection(conn), structTypeName, attrList.toArray());
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
     * Convert an array value to the Oracle's ARRAY.
     * @param conn The Oracle native connection for the database. (NotNull)
     * @param arrayTypeName The name of ARRAY type for Oracle. (NotNull)
     * @param arrayValue The value of array. (NotNull) 
     * @return The instance of oracle.sql.ARRAY for the array argument. (NotNull)
     * @throws java.sql.SQLException
     */
    protected abstract Object toOracleArray(Connection conn, String arrayTypeName, Object arrayValue)
            throws SQLException;

    /**
     * Convert the Oracle's array to a standard array.
     * @param oracleArray The value of Oracle's ARRAY (oracle.sql.ARRAY). (NotNull) 
     * @return The instance of standard array for the Oracle's array argument. (NotNull)
     * @throws java.sql.SQLException
     */
    protected abstract Object toStandardArray(Object oracleArray) throws SQLException;

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