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
    protected Collection<Object> mappingOracleArrayToCollection(Object oracleArray, Class<?> elementType)
            throws SQLException {
        if (oracleArray == null) {
            return DfCollectionUtil.newArrayList();
        }
        Object firstValue = null;
        final Object[] array;
        {
            final Object[] plainArray = (Object[]) toStandardArray(oracleArray);
            if (plainArray == null || plainArray.length == 0) {
                return DfCollectionUtil.newArrayList();
            }
            final List<Object> objList = DfCollectionUtil.newArrayList();
            for (Object element : plainArray) {
                if (element == null) {
                    continue;
                }
                if (firstValue == null) {
                    firstValue = element;
                }
                objList.add(element);
            }
            array = objList.toArray(); // null removed
        }
        if (firstValue == null) { // means empty array
            return DfCollectionUtil.newArrayList();
        }
        final List<Object> resultList = DfCollectionUtil.newArrayList();
        if (isOracleArray(firstValue)) { // unsupported
            //for (Object element : array) {
            //    // next elementType is unknown if second or more level
            //    resultList.add(mappingOracleArrayToCollection(element, unknown));
            //}
            throw new UnsupportedOperationException("array in array is unsupported");
        } else if (isOracleStruct(firstValue)) { // first level is only supported 
            for (Object element : array) {
                resultList.add(mappingOracleStructToEntity(element, elementType));
            }
        } else {
            for (Object element : array) {
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
        for (int i = 0; i < attrs.length; i++) {
            final Object attr = attrs[i];
            final ColumnInfo columnInfo = columnInfoList.get(i);
            final String propertyName = columnInfo.getPropertyName();
            final Class<?> propertyType = columnInfo.getPropertyType();
            if (attr == null) {
                if (Collection.class.isAssignableFrom(propertyType)) {
                    dbmeta.setupEntityProperty(propertyName, entity, DfCollectionUtil.newArrayList());
                }
                continue;
            }
            final Object mappedValue;
            if (Collection.class.isAssignableFrom(propertyType)) { // unsupported
                // if it supports, it needs to get element type
                //mappedValue = mappingOracleArrayToCollection(attr, elementType);
                throw new UnsupportedOperationException("array in struct is unsupported");
            } else if (Entity.class.isAssignableFrom(propertyType)) {
                mappedValue = mappingOracleStructToEntity(oracleStruct, propertyType);
            } else {
                mappedValue = adjustScalarToPropertyValue(attr, propertyType);
            }
            dbmeta.setupEntityProperty(propertyName, entity, mappedValue);
        }
        return entity;
    }

    protected Object adjustScalarToPropertyValue(Object value, Class<?> propertyType) {
        if (propertyType == null) { // no check
            return value;
        }
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

    protected abstract Object toBindValue(Connection conn, Object paramExp, Object value) throws SQLException;

    protected Object mappingCollectionToOracleArray(Connection conn, Object paramExp, Collection<?> value,
            String arrayTypeName, Class<?> elementType) throws SQLException {
        final Object[] array = value.toArray();
        if (array.length == 0) {
            return array;
        }
        final Object preparedArray;
        if (Entity.class.isAssignableFrom(elementType)) {
            final List<Object> structList = DfCollectionUtil.newArrayList();
            for (Object element : array) {
                if (element == null) {
                    continue;
                }
                assertArrayElementValueStructEntity(paramExp, element, arrayTypeName, elementType);
                final Entity entity = (Entity) element;
                structList.add(mappingEntityToOracleStruct(conn, paramExp, entity));
            }
            preparedArray = structList.toArray();
        } else {
            if (java.util.Date.class.equals(elementType)) {
                final List<Object> elementList = DfCollectionUtil.newArrayList();
                for (Object element : array) {
                    if (element == null) {
                        continue;
                    }
                    elementList.add(mappingScalarToSqlValue(conn, paramExp, element));
                }
                preparedArray = elementList.toArray();
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
            if (propertyValue instanceof Collection<?>) { // ARRAY in STRUCT
                // it works only when the element type is scalar
                // (but property type is Object because Sql2Entity does not support this)
                final Collection<?> nested = ((Collection<?>) propertyValue);
                final String arrayTypeName = columnInfo.getColumnDbType();
                final Class<?> propertyType = columnInfo.getPropertyType();
                Class<?> elementType = propertyType;
                if (Object.class.equals(propertyType) && DfCollectionUtil.hasValidElement(nested)) {
                    final Class<?> firstElementType = DfCollectionUtil.findFirstElementType(nested);
                    if (firstElementType != null) {
                        elementType = nested.iterator().next().getClass();
                    }
                }
                mappedValue = mappingCollectionToOracleArray(conn, paramExp, nested, arrayTypeName, elementType);
            } else if (propertyValue instanceof Entity) { // STRUCT in STRUCT
                // it works only when the entity structure matches with the STRUCT type
                // (but property type is Object because Sql2Entity does not support this)
                mappedValue = mappingEntityToOracleStruct(conn, paramExp, (Entity) propertyValue);
            } else {
                mappedValue = mappingScalarToSqlValue(conn, paramExp, propertyValue);
            }
            attrList.add(mappedValue);
        }
        final String structTypeName = dbmeta.getTableSqlName().toString();
        return toOracleStruct(getOracleConnection(conn), structTypeName, attrList.toArray());
    }

    protected Object mappingScalarToSqlValue(Connection conn, Object paramExp, Object value) {
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
        return _agent.toOracleArray(getOracleConnection(conn), arrayTypeName, arrayValue);
    }

    protected Object toStandardArray(Object oracleArray) throws SQLException {
        return _agent.toStandardArray(oracleArray);
    }

    protected boolean isOracleArray(Object obj) {
        return _agent.isOracleArray(obj);
    }

    protected Object toOracleStruct(Connection conn, String structTypeName, Object[] attrs) throws SQLException {
        return _agent.toOracleStruct(getOracleConnection(conn), structTypeName, attrs);
    }

    protected Object[] toStandardStructAttributes(Object oracleStruct) throws SQLException {
        return _agent.toStandardStructAttributes(oracleStruct);
    }

    protected boolean isOracleStruct(Object obj) {
        return _agent.isOracleStruct(obj);
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