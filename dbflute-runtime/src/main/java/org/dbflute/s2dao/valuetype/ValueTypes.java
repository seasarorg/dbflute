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
package org.dbflute.s2dao.valuetype;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.types.BigDecimalType;
import org.seasar.extension.jdbc.types.BigIntegerType;
import org.seasar.extension.jdbc.types.BinaryStreamType;
import org.seasar.extension.jdbc.types.BinaryType;
import org.seasar.extension.jdbc.types.BooleanIntegerType;
import org.seasar.extension.jdbc.types.BooleanType;
import org.seasar.extension.jdbc.types.ByteType;
import org.seasar.extension.jdbc.types.BytesType;
import org.seasar.extension.jdbc.types.CalendarSqlDateType;
import org.seasar.extension.jdbc.types.CalendarTimeType;
import org.seasar.extension.jdbc.types.CalendarTimestampType;
import org.seasar.extension.jdbc.types.CharacterType;
import org.seasar.extension.jdbc.types.DateSqlDateType;
import org.seasar.extension.jdbc.types.DateTimeType;
import org.seasar.extension.jdbc.types.DateTimestampType;
import org.seasar.extension.jdbc.types.DoubleType;
import org.seasar.extension.jdbc.types.FloatType;
import org.seasar.extension.jdbc.types.IntegerType;
import org.seasar.extension.jdbc.types.LongType;
import org.seasar.extension.jdbc.types.ObjectType;
import org.seasar.extension.jdbc.types.OracleResultSetType;
import org.seasar.extension.jdbc.types.PostgreResultSetType;
import org.seasar.extension.jdbc.types.SerializableType;
import org.seasar.extension.jdbc.types.ShortType;
import org.seasar.extension.jdbc.types.SqlDateType;
import org.seasar.extension.jdbc.types.StringClobType;
import org.seasar.extension.jdbc.types.StringType;
import org.seasar.extension.jdbc.types.TimeType;
import org.seasar.extension.jdbc.types.TimestampType;
import org.seasar.extension.jdbc.types.UserDefineType;
import org.seasar.extension.jdbc.types.WaveDashStringType;
import org.seasar.framework.util.ConstructorUtil;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.ModifierUtil;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class ValueTypes {

    public final static ValueType STRING = new StringType();
    public final static ValueType CLOB = new StringClobType();
    public final static ValueType WAVE_DASH_STRING = new WaveDashStringType();
    public final static ValueType CHARACTER = new CharacterType();
    public final static ValueType BYTE = new ByteType();
    public final static ValueType SHORT = new ShortType();
    public final static ValueType INTEGER = new IntegerType();
    public final static ValueType LONG = new LongType();
    public final static ValueType FLOAT = new FloatType();
    public final static ValueType DOUBLE = new DoubleType();
    public final static ValueType BIGDECIMAL = new BigDecimalType();
    public final static ValueType BIGINTEGER = new BigIntegerType();
    public final static ValueType TIME = new TimeType();
    public final static ValueType SQLDATE = new SqlDateType();
    public final static ValueType TIMESTAMP = new TimestampType();
    public final static ValueType DATE_SQLDATE = new DateSqlDateType();
    public final static ValueType DATE_TIME = new DateTimeType();
    public final static ValueType DATE_TIMESTAMP = new DateTimestampType();
    public final static ValueType CALENDAR_SQLDATE = new CalendarSqlDateType();
    public final static ValueType CALENDAR_TIME = new CalendarTimeType();
    public final static ValueType CALENDAR_TIMESTAMP = new CalendarTimestampType();
    public final static ValueType BINARY = new BinaryType();
    public final static ValueType BINARY_STREAM = new BinaryStreamType();
    public final static ValueType BYTE_ARRAY = new BytesType(BytesType.BYTES_TRAIT);
    public final static ValueType BLOB = new BytesType(BytesType.BLOB_TRAIT);
    public final static ValueType SERIALIZABLE_BYTE_ARRAY = new SerializableType(BytesType.BYTES_TRAIT);
    public final static ValueType SERIALIZABLE_BLOB = new SerializableType(BytesType.BLOB_TRAIT);
    public final static ValueType BOOLEAN = new BooleanType();
    public final static ValueType BOOLEAN_INTEGER = new BooleanIntegerType();
    public final static ValueType POSTGRE_RESULT_SET = new PostgreResultSetType();
    public final static ValueType ORACLE_RESULT_SET = new OracleResultSetType();
    public final static ValueType OBJECT = new ObjectType();

    private static final ValueType NULL = new NullType();
    private static final Class<?> BYTE_ARRAY_CLASS = new byte[0].getClass();

    private static Map<Class<?>, ValueType> types = new HashMap<Class<?>, ValueType>();
    private static Method isEnumMethod;

    private static Constructor<?> enumDefaultValueTypeConstructor;
    private static Constructor<?> enumOrdinalValueTypeConstructor;
    private static Constructor<?> enumStringValueTypeConstructor;

    private static Map<String, ValueType> valueTypeCache = new ConcurrentHashMap<String, ValueType>(50);

    static {
        registerValueType(String.class, STRING);
        registerValueType(char.class, CHARACTER);
        registerValueType(Character.class, CHARACTER);
        registerValueType(byte.class, BYTE);
        registerValueType(Byte.class, BYTE);
        registerValueType(short.class, SHORT);
        registerValueType(Short.class, SHORT);
        registerValueType(int.class, INTEGER);
        registerValueType(Integer.class, INTEGER);
        registerValueType(long.class, LONG);
        registerValueType(Long.class, LONG);
        registerValueType(float.class, FLOAT);
        registerValueType(Float.class, FLOAT);
        registerValueType(double.class, DOUBLE);
        registerValueType(Double.class, DOUBLE);
        registerValueType(BigInteger.class, BIGINTEGER);
        registerValueType(BigDecimal.class, BIGDECIMAL);
        registerValueType(java.sql.Date.class, SQLDATE);
        registerValueType(java.sql.Time.class, TIME);
        registerValueType(java.util.Date.class, TIMESTAMP);
        registerValueType(Timestamp.class, TIMESTAMP);
        registerValueType(Calendar.class, TIMESTAMP);
        registerValueType(BYTE_ARRAY_CLASS, BINARY);
        registerValueType(InputStream.class, BINARY_STREAM);
        registerValueType(boolean.class, BOOLEAN);
        registerValueType(Boolean.class, BOOLEAN);
        // registerValueType(Object.class, OBJECT);
        try {
            isEnumMethod = Class.class.getMethod("isEnum", (Class[]) null);
            setEnumDefaultValueType(Class.forName("org.seasar.extension.jdbc.types.EnumOrdinalType"));
            setEnumOrdinalValueType(Class.forName("org.seasar.extension.jdbc.types.EnumOrdinalType"));
            setEnumStringValueType(Class.forName("org.seasar.extension.jdbc.types.EnumType"));
        } catch (Throwable ignore) {
            isEnumMethod = null;
            enumStringValueTypeConstructor = null;
            enumOrdinalValueTypeConstructor = null;
        }
    }

    protected ValueTypes() {
    }

    public static void clear() {
        valueTypeCache.clear();
    }

    public static void registerValueType(Class<?> clazz, ValueType valueType) {
        types.put(clazz, valueType);
    }

    public static void unregisterValueType(Class<?> clazz) {
        types.remove(clazz);
    }

    public static void setEnumDefaultValueType(Class<?> enumDefaultValueTypeClass) throws NoSuchMethodException {
        enumDefaultValueTypeConstructor = enumDefaultValueTypeClass.getConstructor(new Class[] { Class.class });
    }

    public static void setEnumOrdinalValueType(Class<?> enumOrdinalValueTypeClass) throws NoSuchMethodException {
        enumOrdinalValueTypeConstructor = enumOrdinalValueTypeClass.getConstructor(new Class[] { Class.class });
    }

    public static void setEnumStringValueType(Class<?> enumStringValueTypeClass) throws NoSuchMethodException {
        enumStringValueTypeConstructor = enumStringValueTypeClass.getConstructor(new Class[] { Class.class });
    }

    public static ValueType getValueType(Object obj) {
        if (obj == null) {
            return OBJECT;
        }
        return getValueType(obj.getClass());
    }

    public static ValueType getValueType(Class<?> clazz) {
        if (clazz == null) {
            return OBJECT;
        }
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            ValueType valueType = getValueType0(c);
            if (valueType != null) {
                return valueType;
            }
        }
        ValueType valueType = getCachedValueType(clazz);
        if (valueType != null) {
            return valueType;
        }
        return OBJECT;
    }

    private static ValueType getValueType0(Class<?> clazz) {
        return (ValueType) types.get(clazz);
    }

    private static boolean hasCachedValueType(Class<?> clazz) {
        return getCachedValueType(clazz) != null;
    }

    private static ValueType getCachedValueType(Class<?> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return null;
        }
        ValueType valueType = (ValueType) valueTypeCache.get(clazz.getName());
        if (valueType == NULL) {
            return null;
        }
        if (valueType != null) {
            return valueType;
        }
        Class<?> normalizedEnumClass = normalizeEnum(clazz);
        if (normalizedEnumClass != null) {
            valueType = getEnumDefaultValueType(normalizedEnumClass);
            valueTypeCache.put(normalizedEnumClass.getName(), valueType);
            return valueType;
        }
        valueType = createUserDefineValueType(clazz);
        if (valueType != null) {
            valueTypeCache.put(clazz.getName(), valueType);
            return valueType;
        }
        valueTypeCache.put(clazz.getName(), NULL);
        return null;
    }

    private static Class<?> normalizeEnum(Class<?> clazz) {
        if (isEnumMethod == null || enumStringValueTypeConstructor == null) {
            return null;
        }
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            if (MethodUtil.invoke(isEnumMethod, c, null).equals(Boolean.TRUE)) {
                return c;
            }
        }
        return null;
    }

    public static ValueType getEnumDefaultValueType(Class<?> clazz) {
        return (ValueType) ConstructorUtil.newInstance(enumDefaultValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static ValueType getEnumStringValueType(Class<?> clazz) {
        return (ValueType) ConstructorUtil.newInstance(enumStringValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static ValueType getEnumOrdinalValueType(Class<?> clazz) {
        return (ValueType) ConstructorUtil.newInstance(enumOrdinalValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static ValueType createUserDefineValueType(Class<?> clazz) {
        List<Method> valueOfMethods = new ArrayList<Method>();
        Method valueMethod = null;
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (MethodUtil.isBridgeMethod(method) || MethodUtil.isSyntheticMethod(method)) {
                continue;
            }
            int mod = method.getModifiers();
            if (method.getName().equals("valueOf") && method.getParameterTypes().length == 1
                    && method.getReturnType() == clazz && ModifierUtil.isPublic(mod) && ModifierUtil.isStatic(mod)) {
                valueOfMethods.add(method);
            } else if (method.getName().equals("value") && method.getParameterTypes().length == 0
                    && ModifierUtil.isPublic(mod) && !ModifierUtil.isStatic(mod)) {
                valueMethod = method;
            }
        }
        if (valueMethod == null) {
            return null;
        }
        for (int i = 0; i < valueOfMethods.size(); ++i) {
            Method valueOfMethod = (Method) valueOfMethods.get(i);
            if (valueOfMethod.getParameterTypes()[0] == valueMethod.getReturnType()) {
                Class<?> baseClass = valueMethod.getReturnType();
                ValueType baseValueType = getValueType0(baseClass);
                if (baseValueType == null) {
                    return null;
                }
                return new UserDefineType(baseValueType, valueOfMethod, valueMethod);
            }
        }
        return null;
    }

    public static Class<?> getType(int sqltype) {
        switch (sqltype) {
        case Types.TINYINT:
            return Byte.class;
        case Types.SMALLINT:
            return Short.class;
        case Types.INTEGER:
            return Integer.class;
        case Types.BIGINT:
            return Long.class;
        case Types.REAL:
        case Types.FLOAT:
            return Float.class;
        case Types.DOUBLE:
            return Double.class;
        case Types.DECIMAL:
        case Types.NUMERIC:
            return BigDecimal.class;
        case Types.DATE:
            return Timestamp.class;
        case Types.TIME:
            return java.sql.Time.class;
        case Types.TIMESTAMP:
            return Timestamp.class;
        case Types.BINARY:
        case Types.BLOB:
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
            return BYTE_ARRAY_CLASS;
        case Types.CHAR:
        case Types.LONGVARCHAR:
        case Types.VARCHAR:
            return String.class;
        case Types.BOOLEAN:
            return Boolean.class;
        default:
            return Object.class;
        }
    }

    public static ValueType getValueType(int sqltype) {
        return getValueType(getType(sqltype));
    }

    public static boolean isSimpleType(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return clazz == String.class || clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz) || clazz == BYTE_ARRAY_CLASS || hasCachedValueType(clazz);
    }

    private static class NullType implements ValueType {

        public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
            throw new SQLException("not supported");
        }

        public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(CallableStatement cs, int index) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(ResultSet resultSet, int index) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
            throw new SQLException("not supported");
        }

        public void registerOutParameter(CallableStatement cs, int index) throws SQLException {
            throw new SQLException("not supported");
        }

        public void registerOutParameter(CallableStatement cs, String parameterName) throws SQLException {
            throw new SQLException("not supported");
        }

        public String toText(Object value) {
            throw new UnsupportedOperationException("toText");
        }

        public int getSqlType() {
            return Types.NULL;
        }
    }
}