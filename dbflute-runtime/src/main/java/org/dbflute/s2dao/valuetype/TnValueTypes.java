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

import org.dbflute.s2dao.valuetype.plugin.BytesType;
import org.dbflute.s2dao.valuetype.plugin.OracleResultSetType;
import org.dbflute.s2dao.valuetype.plugin.PostgreResultSetType;
import org.dbflute.s2dao.valuetype.plugin.SerializableType;
import org.dbflute.s2dao.valuetype.registered.BigDecimalType;
import org.dbflute.s2dao.valuetype.registered.BigIntegerType;
import org.dbflute.s2dao.valuetype.registered.BinaryStreamType;
import org.dbflute.s2dao.valuetype.registered.BinaryType;
import org.dbflute.s2dao.valuetype.registered.BooleanType;
import org.dbflute.s2dao.valuetype.registered.ByteType;
import org.dbflute.s2dao.valuetype.registered.CharacterType;
import org.dbflute.s2dao.valuetype.registered.DoubleType;
import org.dbflute.s2dao.valuetype.registered.FloatType;
import org.dbflute.s2dao.valuetype.registered.IntegerType;
import org.dbflute.s2dao.valuetype.registered.LongType;
import org.dbflute.s2dao.valuetype.registered.ObjectType;
import org.dbflute.s2dao.valuetype.registered.ShortType;
import org.dbflute.s2dao.valuetype.registered.SqlDateType;
import org.dbflute.s2dao.valuetype.registered.StringType;
import org.dbflute.s2dao.valuetype.registered.TimeType;
import org.dbflute.s2dao.valuetype.registered.TimestampType;
import org.dbflute.s2dao.valuetype.registered.UserDefineType;
import org.dbflute.util.DfReflectionUtil;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class TnValueTypes {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // Basic
    public final static TnValueType STRING = new StringType();
    public final static TnValueType CHARACTER = new CharacterType();
    public final static TnValueType BYTE = new ByteType();
    public final static TnValueType SHORT = new ShortType();
    public final static TnValueType INTEGER = new IntegerType();
    public final static TnValueType LONG = new LongType();
    public final static TnValueType FLOAT = new FloatType();
    public final static TnValueType DOUBLE = new DoubleType();
    public final static TnValueType BIGDECIMAL = new BigDecimalType();
    public final static TnValueType BIGINTEGER = new BigIntegerType();
    public final static TnValueType TIME = new TimeType();
    public final static TnValueType SQLDATE = new SqlDateType();
    public final static TnValueType TIMESTAMP = new TimestampType();
    public final static TnValueType BINARY = new BinaryType();
    public final static TnValueType BINARY_STREAM = new BinaryStreamType();
    public final static TnValueType BOOLEAN = new BooleanType();
    public final static TnValueType OBJECT = new ObjectType();

    // Plug-in
    public final static TnValueType ORACLE_RESULT_SET = new OracleResultSetType();
    public final static TnValueType POSTGRE_RESULT_SET = new PostgreResultSetType();
    public final static TnValueType SERIALIZABLE_BYTE_ARRAY = new SerializableType(BytesType.BYTES_TRAIT);

    // Internal
    private static final TnValueType NULL = new NullType();
    
    private static final Class<?> BYTE_ARRAY_CLASS = new byte[0].getClass();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static Map<Class<?>, TnValueType> types = new HashMap<Class<?>, TnValueType>();
    private static Map<String, TnValueType> additionalValueTypeMap = new HashMap<String, TnValueType>();
    private static Method isEnumMethod;

    private static Constructor<?> enumDefaultValueTypeConstructor;
    private static Constructor<?> enumOrdinalValueTypeConstructor;
    private static Constructor<?> enumStringValueTypeConstructor;

    private static Map<String, TnValueType> valueTypeCache = new ConcurrentHashMap<String, TnValueType>(50);

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

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected TnValueTypes() {
    }

    // ===================================================================================
    //                                                                               Clear
    //                                                                               =====
    public static void clear() {
        valueTypeCache.clear();
    }

    public static void registerValueType(Class<?> clazz, TnValueType valueType) {
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

    public static TnValueType getValueType(Object obj) {
        if (obj == null) {
            return OBJECT;
        }
        return getValueType(obj.getClass());
    }

    public static TnValueType getValueType(Class<?> clazz) {
        if (clazz == null) {
            return OBJECT;
        }
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            TnValueType valueType = getValueType0(c);
            if (valueType != null) {
                return valueType;
            }
        }
        TnValueType valueType = getCachedValueType(clazz);
        if (valueType != null) {
            return valueType;
        }
        return OBJECT;
    }

    private static TnValueType getValueType0(Class<?> clazz) {
        return types.get(clazz);
    }

    private static boolean hasCachedValueType(Class<?> clazz) {
        return getCachedValueType(clazz) != null;
    }

    private static TnValueType getCachedValueType(Class<?> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return null;
        }
        TnValueType valueType = (TnValueType) valueTypeCache.get(clazz.getName());
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
            if (DfReflectionUtil.invoke(isEnumMethod, c, null).equals(Boolean.TRUE)) {
                return c;
            }
        }
        return null;
    }

    public static TnValueType getEnumDefaultValueType(Class<?> clazz) {
        return (TnValueType) DfReflectionUtil.newInstance(enumDefaultValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static TnValueType getEnumStringValueType(Class<?> clazz) {
        return (TnValueType) DfReflectionUtil.newInstance(enumStringValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static TnValueType getEnumOrdinalValueType(Class<?> clazz) {
        return (TnValueType) DfReflectionUtil.newInstance(enumOrdinalValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static TnValueType createUserDefineValueType(Class<?> clazz) {
        List<Method> valueOfMethods = new ArrayList<Method>();
        Method valueMethod = null;
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (DfReflectionUtil.isBridgeMethod(method) || DfReflectionUtil.isSyntheticMethod(method)) {
                continue;
            }
            int mod = method.getModifiers();
            if (method.getName().equals("valueOf") && method.getParameterTypes().length == 1
                    && method.getReturnType() == clazz && DfReflectionUtil.isPublic(mod)
                    && DfReflectionUtil.isStatic(mod)) {
                valueOfMethods.add(method);
            } else if (method.getName().equals("value") && method.getParameterTypes().length == 0
                    && DfReflectionUtil.isPublic(mod) && !DfReflectionUtil.isStatic(mod)) {
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
                TnValueType baseValueType = getValueType0(baseClass);
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

    public static TnValueType getValueType(int sqltype) {
        return getValueType(getType(sqltype));
    }

    // ===================================================================================
    //                                                                Additional ValueType
    //                                                                ====================
    /**
     * @param valueTypeName The name of value type. (NotNull)
     * @param valueType The value type. (NotNull)
     */
    public static void registerAdditionalValueType(String valueTypeName, TnValueType valueType) {
        assertObjectNotNull("valueTypeName", valueTypeName);
        assertObjectNotNull("valueType", valueType);
        additionalValueTypeMap.put(valueTypeName, valueType);
    }

    /**
     * @param valueTypeName The name of value type. (NotNull)
     * @return The value type. (Nullable)
     */
    public static TnValueType getAdditionalValueType(String valueTypeName) {
        assertObjectNotNull("valueTypeName", valueTypeName);
        return additionalValueTypeMap.get(valueTypeName);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    public static boolean isSimpleType(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return clazz == String.class || clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz) || clazz == BYTE_ARRAY_CLASS || hasCachedValueType(clazz);
    }

    private static class NullType implements TnValueType {

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

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected static void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}