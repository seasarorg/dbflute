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
import java.sql.SQLException;
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
 * @author jflute
 */
public abstract class GreatWallOfOracleType implements ValueType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final int _sqlType;
    protected final String _mainTypeName;
    protected final Class<?> _mainObjectType;
    protected final Entity _mainEntityPrototype;
    protected final OracleAgent _agent;
    protected final PhysicalConnectionDigger _digger;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public GreatWallOfOracleType(int sqlType, String mainTypeName, Class<?> mainObjectType) {
        _sqlType = sqlType;
        _mainTypeName = mainTypeName;
        _mainObjectType = mainObjectType;
        if (Entity.class.isAssignableFrom(mainObjectType)) {
            _mainEntityPrototype = (Entity) DfReflectionUtil.newInstance(mainObjectType);
        } else {
            _mainEntityPrototype = null;
        }
        _agent = createOracleAgent();
        _digger = _agent.getPhysicalConnectionDigger();
    }

    /**
     * Create the agent for Oracle.
     * @return The instance of agent. (NotNull)
     */
    protected abstract OracleAgent createOracleAgent();

    protected abstract String getTitleName(); // for logging

    // ===================================================================================
    //                                                                           Get Value
    //                                                                           =========
    protected Collection<Object> mappingOracleArrayToCollection(Object oracleArray, boolean firstLevel)
            throws SQLException {
        if (oracleArray == null) {
            return DfCollectionUtil.newArrayList();
        }
        final Object[] array = (Object[]) toStandardArray(oracleArray);
        if (array == null || array.length == 0) {
            return DfCollectionUtil.newArrayList();
        }
        final List<Object> resultList = DfCollectionUtil.newArrayList();
        final Class<? extends Object> elementType = array[0].getClass();
        if (Collection.class.isAssignableFrom(elementType)) { // unsupported
            for (Object element : array) {
                if (element == null) {
                    continue;
                }
                resultList.add(mappingOracleArrayToCollection(element, false));
            }
        } else if (Entity.class.isAssignableFrom(elementType)) {
            for (Object element : array) {
                if (element == null) {
                    continue;
                }
                final Object entityType = (firstLevel ? _mainEntityPrototype : elementType);
                resultList.add(mappingOracleStructToEntity(element, entityType));
            }
        } else {
            for (Object element : array) {
                if (element == null) {
                    continue;
                }
                resultList.add(adjustScalarToPropertyValue(element, elementType));
            }
        }
        return resultList;
    }

    protected Entity mappingOracleStructToEntity(Object oracleStruct, Object entityType) throws SQLException {
        if (oracleStruct == null) {
            return null;
        }
        final Entity prototype;
        if (entityType instanceof Entity) {
            prototype = (Entity) entityType;
        } else if (entityType instanceof Class<?>) {
            prototype = (Entity) DfReflectionUtil.newInstance((Class<?>) entityType);
        } else {
            String msg = "The entityType should be entity instance or entity type: " + entityType;
            throw new IllegalArgumentException(msg);
        }
        final DBMeta dbmeta = prototype.getDBMeta();
        final Entity entity = dbmeta.newEntity();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final Object[] attrs = toStandardStructAttributes(oracleStruct);
        assertStructAttributeSizeMatched(entity, attrs, columnInfoList);
        int index = 0;
        for (Object attr : attrs) {
            final ColumnInfo columnInfo = columnInfoList.get(index);
            final Class<?> propertyType = columnInfo.getPropertyType();
            if (attr == null) {
                if (Collection.class.isAssignableFrom(propertyType)) {
                    columnInfo.write(entity, DfCollectionUtil.newArrayList());
                }
                continue;
            }
            final Object mappedValue;
            if (Collection.class.isAssignableFrom(propertyType)) { // unsupported
                mappedValue = mappingOracleArrayToCollection(attr, false);
            } else if (Entity.class.isAssignableFrom(propertyType)) { // unsupported
                mappedValue = mappingOracleStructToEntity(oracleStruct, propertyType);
            } else {
                mappedValue = adjustScalarToPropertyValue(attr, propertyType);
            }
            columnInfo.write(entity, mappedValue);
            ++index;
        }
        return entity;
    }

    protected Object adjustScalarToPropertyValue(Object value, Class<?> propertyType) {
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
        br.addNotice("The size of struct attributes does not match with column list of entity.");
        br.addItem(getTitleName());
        br.addElement(_mainTypeName);
        br.addItem("Entity");
        br.addElement(DfTypeUtil.toClassTitle(entity));
        br.addItem("Attribute Size");
        br.addElement(attrs.length);
        br.addItem("Column List Size");
        br.addElement(columnInfoList.size());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
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

    protected abstract Object toBindValue(Connection conn, Object parameterExp, Object value) throws SQLException;

    protected Object mappingCollectionToOracleArray(Connection conn, Object paramExp, Collection<?> value,
            String arrayTypeName, Class<?> elementType) throws SQLException {
        final Object[] array = value.toArray();
        if (array.length == 0) {
            return array;
        }
        final Object preparedArray;
        if (Entity.class.isAssignableFrom(elementType)) {
            final List<Object> structList = new ArrayList<Object>();
            for (Object element : array) {
                assertArrayElementValueStructEntity(paramExp, element, arrayTypeName, elementType);
                final Entity entity = (Entity) element;
                structList.add(mappingEntityToOracleStruct(conn, paramExp, entity));
            }
            preparedArray = structList.toArray();
        } else {
            if (java.util.Date.class.equals(elementType)) {
                final Object[] filteredArray = new Object[array.length];
                int index = 0;
                for (Object element : array) {
                    filteredArray[index] = mappingScalarToSqlValue(element);
                    ++index;
                }
                preparedArray = filteredArray;
            } else {
                preparedArray = array;
            }
        }
        return toOracleArray(conn, arrayTypeName, preparedArray);
    }

    protected Object mappingEntityToOracleStruct(Connection conn, Object paramExp, Entity entity) throws SQLException {
        final DBMeta dbmeta = entity.getDBMeta();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final List<Object> attrList = new ArrayList<Object>();
        for (ColumnInfo columnInfo : columnInfoList) {
            final Object propertyValue = columnInfo.read(entity);
            final Object mappedValue;
            if (propertyValue instanceof Collection<?>) { // unsupported (type name is unknown)
                final Collection<?> nested = ((Collection<?>) propertyValue);
                final String arrayTypeName = columnInfo.getColumnDbType();
                final Class<?> propertyType = columnInfo.getPropertyType();
                mappedValue = mappingCollectionToOracleArray(conn, paramExp, nested, arrayTypeName, propertyType);
            } else if (propertyValue instanceof Entity) { // unsupported
                mappedValue = mappingEntityToOracleStruct(conn, paramExp, (Entity) propertyValue);
            } else {
                mappedValue = mappingScalarToSqlValue(propertyValue);
            }
            attrList.add(mappedValue);
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
            return DfTypeUtil.toTimestamp(value);
        }
        return value;
    }

    protected void assertArrayElementValueStructEntity(Object paramExp, Object element, String arrayTypeName,
            Class<?> elementType) {
        if (!(element instanceof Entity)) {
            throwArrayElementValueNotStructEntityException(paramExp, element, arrayTypeName, elementType);
        }
    }

    protected void throwArrayElementValueNotStructEntityException(Object parameterExp, Object element,
            String arrayTypeName, Class<?> elementType) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The element value of array for struct should be entity type:");
        br.addItem(getTitleName());
        br.addElement(_mainTypeName);
        br.addItem("Parameter");
        br.addElement(parameterExp);
        br.addItem("Array Type");
        br.addElement(arrayTypeName + "<" + elementType + ">");
        br.addItem("Element Value");
        if (element != null) {
            br.addElement(element.getClass());
        }
        br.addElement(element);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                        Null Setting
    //                                                                        ============
    protected void setNull(PreparedStatement ps, int index) throws SQLException {
        ps.setNull(index, getSqlType(), _mainTypeName);
    }

    protected void setNull(CallableStatement cs, String parameterName) throws SQLException {
        cs.setNull(parameterName, getSqlType(), _mainTypeName);
    }

    // ===================================================================================
    //                                                                       Out Parameter
    //                                                                       =============
    public void registerOutParameter(Connection conn, CallableStatement cs, int index) throws SQLException {
        cs.registerOutParameter(index, getSqlType(), _mainTypeName);
    }

    public void registerOutParameter(Connection conn, CallableStatement cs, String parameterName) throws SQLException {
        cs.registerOutParameter(parameterName, getSqlType(), _mainTypeName);
    }

    // ===================================================================================
    //                                                                       Oracle's Type
    //                                                                       =============
    protected Object toOracleArray(Connection conn, String arrayTypeName, Object arrayValue) throws SQLException {
        return _agent.toOracleArray(conn, arrayTypeName, arrayValue);
    }

    protected Object toStandardArray(Object oracleArray) throws SQLException {
        return _agent.toStandardArray(oracleArray);
    }

    protected Object toOracleStruct(Connection conn, String structTypeName, Object[] attrs) throws SQLException {
        return _agent.toOracleStruct(conn, structTypeName, attrs);
    }

    protected Object[] toStandardStructAttributes(Object oracleStruct) throws SQLException {
        return _agent.toStandardStructAttributes(oracleStruct);
    }

    protected Connection getOracleConnection(Connection conn) throws SQLException {
        return _digger.digUp(conn);
    }

    // ===================================================================================
    //                                                                            SQL Type
    //                                                                            ========
    public int getSqlType() {
        return _sqlType;
    }
}