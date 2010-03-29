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
package org.seasar.dbflute.s2dao.valuetype;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.dbflute.jdbc.Classification;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.valuetype.basic.BigDecimalType;
import org.seasar.dbflute.s2dao.valuetype.basic.BigIntegerType;
import org.seasar.dbflute.s2dao.valuetype.basic.BinaryStreamType;
import org.seasar.dbflute.s2dao.valuetype.basic.BinaryType;
import org.seasar.dbflute.s2dao.valuetype.basic.BooleanType;
import org.seasar.dbflute.s2dao.valuetype.basic.ByteType;
import org.seasar.dbflute.s2dao.valuetype.basic.CharacterType;
import org.seasar.dbflute.s2dao.valuetype.basic.ClassificationType;
import org.seasar.dbflute.s2dao.valuetype.basic.DoubleType;
import org.seasar.dbflute.s2dao.valuetype.basic.FloatType;
import org.seasar.dbflute.s2dao.valuetype.basic.IntegerType;
import org.seasar.dbflute.s2dao.valuetype.basic.LongType;
import org.seasar.dbflute.s2dao.valuetype.basic.ObjectType;
import org.seasar.dbflute.s2dao.valuetype.basic.ShortType;
import org.seasar.dbflute.s2dao.valuetype.basic.SqlDateType;
import org.seasar.dbflute.s2dao.valuetype.basic.StringType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimeType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimestampType;
import org.seasar.dbflute.s2dao.valuetype.basic.UUIDType;
import org.seasar.dbflute.s2dao.valuetype.basic.UtilDateAsSqlDateType;
import org.seasar.dbflute.s2dao.valuetype.basic.UtilDateAsTimestampType;
import org.seasar.dbflute.s2dao.valuetype.plugin.BytesType;
import org.seasar.dbflute.s2dao.valuetype.plugin.OracleResultSetType;
import org.seasar.dbflute.s2dao.valuetype.plugin.PostgreResultSetType;
import org.seasar.dbflute.s2dao.valuetype.plugin.SerializableType;
import org.seasar.dbflute.s2dao.valuetype.plugin.TnBytesOidType;
import org.seasar.dbflute.s2dao.valuetype.plugin.TnStringClobType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnValueTypes {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // basic (object)
    public final static ValueType STRING = new StringType();
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
    public final static ValueType UTILDATE_AS_SQLDATE = new UtilDateAsSqlDateType();
    public final static ValueType UTILDATE_AS_TIMESTAMP = new UtilDateAsTimestampType();
    public final static ValueType TIMESTAMP = new TimestampType();
    public final static ValueType BINARY = new BinaryType();
    public final static ValueType BINARY_STREAM = new BinaryStreamType();
    public final static ValueType BOOLEAN = new BooleanType();
    public final static ValueType UUID = new UUIDType();

    // basic (interface)
    public final static ValueType CLASSIFICATION = new ClassificationType(); // DBFlute original class

    // basic (default)
    public final static ValueType OBJECT = new ObjectType();

    // plug-in
    public final static ValueType ORACLE_RESULT_SET = new OracleResultSetType();
    public final static ValueType POSTGRE_RESULT_SET = new PostgreResultSetType();
    public final static ValueType SERIALIZABLE_BYTE_ARRAY = new SerializableType(BytesType.BYTES_TRAIT);

    // class type
    private static final Class<?> BYTE_ARRAY_CLASS = new byte[0].getClass();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static Map<Class<?>, ValueType> basicObjectValueTypeMap = new ConcurrentHashMap<Class<?>, ValueType>();
    private static Map<Class<?>, ValueType> basicInterfaceValueTypeMap = new ConcurrentHashMap<Class<?>, ValueType>();
    private static Map<String, ValueType> pluginValueTypeMap = new ConcurrentHashMap<String, ValueType>();

    static {
        // basic (object)
        registerBasicValueType(String.class, STRING);
        registerBasicValueType(char.class, CHARACTER);
        registerBasicValueType(Character.class, CHARACTER);
        registerBasicValueType(byte.class, BYTE);
        registerBasicValueType(Byte.class, BYTE);
        registerBasicValueType(short.class, SHORT);
        registerBasicValueType(Short.class, SHORT);
        registerBasicValueType(int.class, INTEGER);
        registerBasicValueType(Integer.class, INTEGER);
        registerBasicValueType(long.class, LONG);
        registerBasicValueType(Long.class, LONG);
        registerBasicValueType(float.class, FLOAT);
        registerBasicValueType(Float.class, FLOAT);
        registerBasicValueType(double.class, DOUBLE);
        registerBasicValueType(Double.class, DOUBLE);
        registerBasicValueType(BigInteger.class, BIGINTEGER);
        registerBasicValueType(BigDecimal.class, BIGDECIMAL);
        registerBasicValueType(java.sql.Date.class, SQLDATE);
        registerBasicValueType(java.sql.Time.class, TIME);

        // The (java.util.)date type is treated as SqlDate by default.
        // When the DATE type of your database has time, you need to change this.
        // (But basically DBFlute resolves the problem automatically, for example, Oracle)
        registerBasicValueType(java.util.Date.class, UTILDATE_AS_SQLDATE);

        registerBasicValueType(Timestamp.class, TIMESTAMP);
        registerBasicValueType(Calendar.class, TIMESTAMP);
        registerBasicValueType(BYTE_ARRAY_CLASS, BINARY);
        registerBasicValueType(InputStream.class, BINARY_STREAM);
        registerBasicValueType(boolean.class, BOOLEAN);
        registerBasicValueType(Boolean.class, BOOLEAN);
        registerBasicValueType(UUID.class, UUID);

        // basic (interface)
        registerBasicValueType(Classification.class, CLASSIFICATION); // DBFlute original class

        // Because object type is to be handle as special type.
        //registerBasicValueType(Object.class, OBJECT);

        // plug-in (default)
        registerPluginValueType("stringClobType", new TnStringClobType());
        registerPluginValueType("bytesOidType", new TnBytesOidType());
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected TnValueTypes() {
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Register the basic value type.
     * @param keyType The key as type. (NotNull)
     * @param valueType The value type. (NotNull)
     */
    public static void registerBasicValueType(Class<?> keyType, ValueType valueType) {
        assertObjectNotNull("keyType", keyType);
        assertObjectNotNull("valueType", valueType);
        if (keyType.isInterface()) {
            basicInterfaceValueTypeMap.put(keyType, valueType);
        } else {
            basicObjectValueTypeMap.put(keyType, valueType);
        }
    }

    /**
     * Remove the basic value type.
     * @param keyType The key as type. (NotNull)
     */
    public static void removeBasicValueType(Class<?> keyType) {
        assertObjectNotNull("keyType", keyType);
        if (basicObjectValueTypeMap.containsKey(keyType)) {
            basicObjectValueTypeMap.remove(keyType);
        }
        if (basicInterfaceValueTypeMap.containsKey(keyType)) {
            basicInterfaceValueTypeMap.remove(keyType);
        }
    }

    // -----------------------------------------------------
    //                                               Plug-in
    //                                               -------
    /**
     * Register the plug-in value type.
     * @param keyName The key as name. (NotNull)
     * @param valueType The value type. (NotNull)
     */
    public static void registerPluginValueType(String keyName, ValueType valueType) {
        assertObjectNotNull("keyName", keyName);
        assertObjectNotNull("valueType", valueType);
        pluginValueTypeMap.put(keyName, valueType);
    }

    /**
     * Remove the plug-in value type.
     * @param keyName The key as name. (NotNull)
     */
    public static void removePluginValueType(String keyName) {
        assertObjectNotNull("keyName", keyName);
        pluginValueTypeMap.remove(keyName);
    }

    // ===================================================================================
    //                                                                                 Get
    //                                                                                 ===
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Get the value type by object instance.
     * @param obj The object instance. (Nullable: if null, returns object type)
     * @return The value type. (NotNull)
     */
    public static ValueType getValueType(Object obj) {
        if (obj == null) {
            return OBJECT;
        }
        return getValueType(obj.getClass());
    }

    /**
     * Get the value type by class type. <br />
     * The basic objects are prior to the basic interfaces basically,
     * but only when the ENUM is assignable from the class type, interfaces are prior.
     * Because frequently the ENUM has application own interfaces.
     * Actually Classification of DBFlute matches the pattern.
     * @param clazz The type of class. (Nullable: if null, returns object type)
     * @return The value type. (NotNull)
     */
    public static ValueType getValueType(Class<?> clazz) {
        if (clazz == null) {
            return OBJECT;
        }
        final boolean interfaceFirst = Enum.class.isAssignableFrom(clazz);
        ValueType valueType = null;
        if (interfaceFirst) {
            valueType = getBasicInterfaceValueType(clazz);
            if (valueType == null) {
                valueType = getBasicObjectValueType(clazz);
            }
        } else {
            valueType = getBasicObjectValueType(clazz);
            if (valueType == null) {
                valueType = getBasicInterfaceValueType(clazz);
            }
        }
        return valueType != null ? valueType : OBJECT;
    }

    protected static ValueType getBasicObjectValueType(Class<?> clazz) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            final ValueType valueType = basicObjectValueTypeMap.get(c);
            if (valueType != null) {
                return valueType;
            }
        }
        return null;
    }

    protected static ValueType getBasicInterfaceValueType(Class<?> clazz) {
        final Set<Entry<Class<?>, ValueType>> entrySet = basicInterfaceValueTypeMap.entrySet();
        for (Entry<Class<?>, ValueType> entry : entrySet) {
            final Class<?> inf = entry.getKey();
            if (inf.isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }
        return null;
    }

    // -----------------------------------------------------
    //                                               Plug-in
    //                                               -------
    /**
     * @param valueTypeName The name of value type. (NotNull)
     * @return The value type. (Nullable)
     */
    public static ValueType getPluginValueType(String valueTypeName) {
        assertObjectNotNull("valueTypeName", valueTypeName);
        return pluginValueTypeMap.get(valueTypeName);
    }

    // -----------------------------------------------------
    //                                           By SQL Type
    //                                           -----------
    /**
     * @param sqltype The SQL type of JDBC.
     * @return The value type. (NotNull)
     */
    public static ValueType getValueType(int sqltype) { // for no entity and so on
        return getValueType(getType(sqltype));
    }

    protected static Class<?> getType(int sqltype) {
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
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}