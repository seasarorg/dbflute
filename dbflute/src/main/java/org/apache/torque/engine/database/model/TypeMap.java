package org.apache.torque.engine.database.model;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.sql.Types;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * A class that maps JDBC types to their corresponding
 * Java object types, and Java native types. Used
 * by Column.java to perform object/native mappings.
 *
 * These are the official SQL type to Java type mappings.
 * These don't quite correspond to the way the peer
 * system works so we'll have to make some adjustments.
 * <pre>
 * ------------------------------------------------------
 * Torque Type   | Java Native          | CSharp Native |
 * ------------------------------------------------------
 * CHAR          | java.lang.String     | String        |
 * VARCHAR       | java.lang.String     | String        |
 * LONGVARCHAR   | java.lang.String     | String        |
 * NUMERIC       | java.math.BigDecimal | decimal?      |
 * DECIMAL       | java.math.BigDecimal | decimal?      |
 * BIT           | java.lang.Boolean    | bool?         |
 * BOOLEAN       | java.lang.Boolean    | bool?         |
 * TINYINT       | java.lang.Integer    | int?          |
 * SMALLINT      | java.lang.Integer    | int?          |
 * INTEGER       | java.lang.Integer    | int?          |
 * BIGINT        | java.lang.Long       | long?         |
 * REAL          | java.math.BigDecimal | decimal?      |
 * FLOAT         | java.math.BigDecimal | decimal?      |
 * DOUBLE        | java.math.BigDecimal | decimal?      |
 * BINARY        | byte[]               | byte[]        |
 * VARBINARY     | byte[]               | byte[]        |
 * LONGVARBINARY | byte[]               | byte[]        |
 * DATE          | java.util.Date       | DateTime?     |
 * TIME          | java.sql.Time        | DateTime?     |
 * TIMESTAMP     | java.sql.Timestamp   | DateTime?     |
 * ARRAY         | *Unsupported         | *Unsupported  |
 * UUID          | java.util.UUID       | *Unsupported  |
 * ------------------------------------------------------
 * </pre>
 * ARRAY type is basically unsupported but it's defined for user's extension.
 * UUID type is supported as special handling.
 * @author Modified by jflute
 */
public class TypeMap {

    // ===================================================================================
    //                                                                                 Log
    //                                                                                 ===
    /** Log instance. */
    public static final Log _log = LogFactory.getLog(TypeMap.class);

    // ===================================================================================
    //                                                                Torque(DBFlute) Type
    //                                                                ====================
    public static final String CHAR = "CHAR";
    public static final String VARCHAR = "VARCHAR";
    public static final String LONGVARCHAR = "LONGVARCHAR";
    public static final String CLOB = "CLOB";
    public static final String NUMERIC = "NUMERIC";
    public static final String DECIMAL = "DECIMAL";
    public static final String BIT = "BIT";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String TINYINT = "TINYINT";
    public static final String SMALLINT = "SMALLINT";
    public static final String INTEGER = "INTEGER";
    public static final String BIGINT = "BIGINT";
    public static final String REAL = "REAL";
    public static final String FLOAT = "FLOAT";
    public static final String DOUBLE = "DOUBLE";
    public static final String BINARY = "BINARY";
    public static final String VARBINARY = "VARBINARY";
    public static final String LONGVARBINARY = "LONGVARBINARY";
    public static final String BLOB = "BLOB";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String ARRAY = "ARRAY";
    public static final String UUID = "UUID";
    public static final String BOOLEANCHAR = "BOOLEANCHAR";
    public static final String BOOLEANINT = "BOOLEANINT";
    private static final String[] TEXT_TYPES = { CHAR, VARCHAR, LONGVARCHAR, CLOB, DATE, TIME, TIMESTAMP, BOOLEANCHAR };

    // ===================================================================================
    //                                                                           Java Type
    //                                                                           =========
    // This is default native type(for Java).
    public static final String CHAR_NATIVE_TYPE = "String";
    public static final String VARCHAR_NATIVE_TYPE = "String";
    public static final String LONGVARCHAR_NATIVE_TYPE = "String";
    public static final String CLOB_NATIVE_TYPE = "String";
    public static final String NUMERIC_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String DECIMAL_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String BIT_NATIVE_TYPE = "Boolean";
    public static final String BOOLEAN_NATIVE_TYPE = "Boolean";
    public static final String TINYINT_NATIVE_TYPE = "Integer";
    public static final String SMALLINT_NATIVE_TYPE = "Integer";
    public static final String INTEGER_NATIVE_TYPE = "Integer";
    public static final String BIGINT_NATIVE_TYPE = "Long";
    public static final String REAL_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String FLOAT_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String DOUBLE_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String BINARY_NATIVE_TYPE = "byte[]";
    public static final String VARBINARY_NATIVE_TYPE = "byte[]";
    public static final String LONGVARBINARY_NATIVE_TYPE = "byte[]";
    public static final String BLOB_NATIVE_TYPE = "byte[]";
    public static final String DATE_NATIVE_TYPE = "java.util.Date";
    public static final String TIME_NATIVE_TYPE = "java.sql.Time";
    public static final String TIMESTAMP_NATIVE_TYPE = "java.sql.Timestamp";
    public static final String ARRAY_NATIVE_TYPE = "String";
    public static final String UUID_NATIVE_TYPE = "java.util.UUID";
    public static final String BOOLEANCHAR_NATIVE_TYPE = "Boolean";
    public static final String BOOLEANINT_NATIVE_TYPE = "Boolean";

    // ===================================================================================
    //                                                                            Type Map
    //                                                                            ========
    private static Hashtable<String, String> _torqueTypeToJavaNativeMap = null;
    private static Hashtable<Integer, String> _jdbcTypeToTorqueTypeMap = null;
    private static Hashtable<String, Integer> _torqueTypeToJdbcTypeMap = null;
    private static Hashtable<String, String> _javaNativeToFlexNativeMap = null;

    // ===================================================================================
    //                                                        Property jdbcToJavaNativeMap
    //                                                        ============================
    protected static Map<String, Object> _propertyTorqueTypeToJavaNativeMap;
    protected static Map<String, String> _propertyJavaNativeToFlexNativeMap;
    static {
        setupPropertyNativeMap();
    }

    protected static void setupPropertyNativeMap() {
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        _propertyTorqueTypeToJavaNativeMap = prop.getTypeMappingProperties().getJdbcToJavaNativeMap();
        _propertyJavaNativeToFlexNativeMap = prop.getFlexDtoProperties().getJavaToFlexNativeMap();
    }

    // ===================================================================================
    //                                                                    Initialized Mark
    //                                                                    ================
    private static boolean _initialized = false;

    // ===================================================================================
    //                                                                              Reload
    //                                                                              ======
    public static void reload() { // for test
        setupPropertyNativeMap();
        _initialized = false;
        initialize();
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    /**
     * Initializes the SQL to Java map so that it
     * can be used by client code.
     */
    public synchronized static void initialize() {
        if (_initialized) {
            return;
        }

        // * * * * * * * * * * * * * * * * * * * 
        // The map of Torque Type to Java Native
        // * * * * * * * * * * * * * * * * * * * 
        _torqueTypeToJavaNativeMap = new Hashtable<String, String>();

        // Default types are for Java.
        _torqueTypeToJavaNativeMap.put(CHAR, initializeJavaNative(CHAR, CHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(VARCHAR, initializeJavaNative(VARCHAR, VARCHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(LONGVARCHAR, initializeJavaNative(LONGVARCHAR, LONGVARCHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(CLOB, initializeJavaNative(CLOB, CLOB_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(NUMERIC, initializeJavaNative(NUMERIC, getDefaultNumericJavaNativeType()));
        _torqueTypeToJavaNativeMap.put(DECIMAL, initializeJavaNative(DECIMAL, getDefaultDecimalJavaNativeType()));
        _torqueTypeToJavaNativeMap.put(BIT, initializeJavaNative(BIT, BIT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BOOLEAN, initializeJavaNative(BOOLEAN, BOOLEAN_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(TINYINT, initializeJavaNative(TINYINT, TINYINT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(SMALLINT, initializeJavaNative(SMALLINT, SMALLINT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(INTEGER, initializeJavaNative(INTEGER, INTEGER_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BIGINT, initializeJavaNative(BIGINT, BIGINT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(REAL, initializeJavaNative(REAL, REAL_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(FLOAT, initializeJavaNative(FLOAT, FLOAT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(DOUBLE, initializeJavaNative(DOUBLE, DOUBLE_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BINARY, initializeJavaNative(BINARY, BINARY_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(VARBINARY, initializeJavaNative(VARBINARY, VARBINARY_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(LONGVARBINARY, initializeJavaNative(LONGVARBINARY, LONGVARBINARY_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BLOB, initializeJavaNative(BLOB, BLOB_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(DATE, initializeJavaNative(DATE, DATE_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(TIME, initializeJavaNative(TIME, TIME_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(TIMESTAMP, initializeJavaNative(TIMESTAMP, TIMESTAMP_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(ARRAY, initializeJavaNative(ARRAY, ARRAY_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(UUID, initializeJavaNative(UUID, UUID_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BOOLEANCHAR, initializeJavaNative(BOOLEANCHAR, BOOLEANCHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BOOLEANINT, initializeJavaNative(BOOLEANINT, BOOLEANINT_NATIVE_TYPE));

        // Register new torque type from property.
        {
            final Set<String> propertyTorqueTypeSet = _propertyTorqueTypeToJavaNativeMap.keySet();
            for (String propertyTorqueType : propertyTorqueTypeSet) {
                if (_torqueTypeToJavaNativeMap.containsKey(propertyTorqueType)) {
                    continue; // because it does not need to override
                }
                String propertyJavaNative = (String) _propertyTorqueTypeToJavaNativeMap.get(propertyTorqueType);
                _torqueTypeToJavaNativeMap.put(propertyTorqueType, propertyJavaNative); // as
            }
        }

        // * * * * * * * * * * * * * * * * * * * 
        // The map of JDBC Type to Torque Type
        // * * * * * * * * * * * * * * * * * * * 
        _jdbcTypeToTorqueTypeMap = new Hashtable<Integer, String>();
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.CHAR), CHAR);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.VARCHAR), VARCHAR);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.LONGVARCHAR), LONGVARCHAR);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.CLOB), CLOB);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.NUMERIC), NUMERIC);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.DECIMAL), DECIMAL);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.BIT), BIT);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.BOOLEAN), BOOLEAN);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.TINYINT), TINYINT);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.SMALLINT), SMALLINT);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.INTEGER), INTEGER);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.BIGINT), BIGINT);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.REAL), REAL);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.FLOAT), FLOAT);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.DOUBLE), DOUBLE);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.BINARY), BINARY);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.VARBINARY), VARBINARY);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.LONGVARBINARY), LONGVARBINARY);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.BLOB), BLOB);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.DATE), DATE);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.TIME), TIME);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.TIMESTAMP), TIMESTAMP);
        // [UUID Headache]: UUID has not been supported yet on JDBC.
        //_jdbcTypeToTorqueTypeMap.put(new Integer(Types.UUID), UUID);
        _jdbcTypeToTorqueTypeMap.put(new Integer(Types.ARRAY), ARRAY);

        // * * * * * * * * * * * * * * * * * * * 
        // The map of Torque Type to JDBC Type
        // * * * * * * * * * * * * * * * * * * *
        _torqueTypeToJdbcTypeMap = new Hashtable<String, Integer>();
        {
            Set<Integer> keySet = _jdbcTypeToTorqueTypeMap.keySet();
            for (Integer jdbcType : keySet) {
                String torqueType = _jdbcTypeToTorqueTypeMap.get(jdbcType);
                _torqueTypeToJdbcTypeMap.put(torqueType, jdbcType);
            }
        }

        // * * * * * * * * * * * * * * * * * * * 
        // The map of Java Native to Flex Native
        // * * * * * * * * * * * * * * * * * * *
        _javaNativeToFlexNativeMap = new Hashtable<String, String>();
        _javaNativeToFlexNativeMap.put("String", initializeFlexNative("String", "String"));
        _javaNativeToFlexNativeMap.put("Short", initializeFlexNative("Short", "int"));
        _javaNativeToFlexNativeMap.put("Integer", initializeFlexNative("Integer", "int"));
        _javaNativeToFlexNativeMap.put("Long", initializeFlexNative("Long", "Number"));
        _javaNativeToFlexNativeMap.put("Float", initializeFlexNative("Float", "Number"));
        _javaNativeToFlexNativeMap.put("Double", initializeFlexNative("Double", "Number"));
        _javaNativeToFlexNativeMap.put("Number", initializeFlexNative("Number", "Number"));
        _javaNativeToFlexNativeMap.put("java.math.BigDecimal", initializeFlexNative("java.math.BigDecimal", "Number"));
        _javaNativeToFlexNativeMap.put("java.util.Date", initializeFlexNative("java.util.Date", "Date"));
        _javaNativeToFlexNativeMap.put("java.sql.Time", initializeFlexNative("java.sql.Time", "Date"));
        _javaNativeToFlexNativeMap.put("java.sql.Timestamp", initializeFlexNative("java.sql.Timestamp", "Date"));
        _javaNativeToFlexNativeMap.put("byte[]", initializeFlexNative("byte[]", "Object"));
        _javaNativeToFlexNativeMap.put("Object", initializeFlexNative("Object", "Object"));

        _initialized = true;
    }

    /**
     * @param torqueType The type as string for torque native. (NotNull)
     * @param defaultJavaNative The default type as string for java native. (NotNull)
     * @return Java-native. (NotNull: If the key does not have an element, it returns default type.)
     */
    protected static String initializeJavaNative(String torqueType, String defaultJavaNative) {
        final String javaNative = (String) _propertyTorqueTypeToJavaNativeMap.get(torqueType);
        if (javaNative == null) {
            return defaultJavaNative;
        }
        return javaNative;
    }

    /**
     * @param javaNative The type as string for java native. (NotNull)
     * @param defaultFlexNative The default type as string for flex native. (NotNull)
     * @return Java-native. (NotNull: If the key does not have an element, it returns default type.)
     */
    protected static String initializeFlexNative(String javaNative, String defaultFlexNative) {
        final String flexNative = (String) _propertyJavaNativeToFlexNativeMap.get(javaNative);
        if (flexNative == null) {
            return defaultFlexNative;
        }
        return flexNative;
    }

    // ===================================================================================
    //                                                                  Torque Type Getter
    //                                                                  ==================
    public static String getTorqueType(Integer jdbcType) {
        // Make sure the we are initialized.
        if (!_initialized) {
            initialize();
        }
        if (java.sql.Types.OTHER == jdbcType) {
            String msg = "The jdbcType is unsupported: jdbcType=java.sql.Types.OTHER(" + jdbcType + ")";
            throw new UnsupportedOperationException(msg);
        }
        if (!_jdbcTypeToTorqueTypeMap.containsKey(jdbcType)) {
            String msg = "_jdbcIntToTorqueTypeMap doesn't contain the type as key: ";
            msg = msg + "key=" + jdbcType + " map=" + _jdbcTypeToTorqueTypeMap;
            throw new IllegalStateException(msg);
        }
        return _jdbcTypeToTorqueTypeMap.get(jdbcType);
    }

    public static Integer getJdbcType(String torqueType) {
        // Make sure the we are initialized.
        if (!_initialized) {
            initialize();
        }
        if (!_torqueTypeToJdbcTypeMap.containsKey(torqueType)) {
            String msg = "_torqueTypeToJdbcTypeMap doesn't contain the type as key: ";
            msg = msg + "key=" + torqueType + " map=" + _torqueTypeToJdbcTypeMap;
            throw new IllegalStateException(msg);
        }
        return _torqueTypeToJdbcTypeMap.get(torqueType);
    }

    // ===================================================================================
    //                                                                         Native Type
    //                                                                         ===========
    // -----------------------------------------------------
    //                                           Java Native
    //                                           -----------
    public static String findJavaNativeString(String torqueType, Integer columnSize, Integer decimalDigits) {
        final String javaType = getJavaNativeString(torqueType);
        if (isAutoMappingTargetType(torqueType) && javaType.equalsIgnoreCase("$$AutoMapping$$")) {
            if (decimalDigits != null && decimalDigits > 0) {
                if (NUMERIC.equalsIgnoreCase(torqueType)) {
                    return getDefaultNumericJavaNativeType();
                } else {// DECIMAL
                    return getDefaultDecimalJavaNativeType();
                }
            } else {
                if (columnSize == null) {
                    return getJavaNativeString(BIGINT);
                }
                if (columnSize > 9) {
                    return getJavaNativeString(BIGINT);
                } else {
                    return getJavaNativeString(INTEGER);
                }
            }
        }
        return javaType;
    }

    // -----------------------------------------------------
    //                                           Flex Native
    //                                           -----------
    public static String findFlexNativeString(String javaNative) {
        return getFlexNativeString(javaNative);
    }

    // -----------------------------------------------------
    //                                                Helper
    //                                                ------
    protected static boolean isAutoMappingTargetType(String torqueType) {
        return NUMERIC.equals(torqueType) || DECIMAL.equals(torqueType);
    }

    protected static String getDefaultNumericJavaNativeType() {
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        final DfBasicProperties basicProperties = prop.getBasicProperties();
        if (basicProperties.isTargetLanguageJava()) {
            return NUMERIC_NATIVE_TYPE;
        } else {
            final DfLanguageDependencyInfo languageDependencyInfo = basicProperties.getLanguageDependencyInfo();
            final LanguageMetaData languageMetaData = languageDependencyInfo.createLanguageMetaData();
            final Map<String, Object> jdbcToJavaNativeMap = languageMetaData.getJdbcToJavaNativeMap();
            return (String) jdbcToJavaNativeMap.get(NUMERIC);
        }
    }

    protected static String getDefaultDecimalJavaNativeType() {
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        final DfBasicProperties basicProperties = prop.getBasicProperties();
        if (basicProperties.isTargetLanguageJava()) {
            return DECIMAL_NATIVE_TYPE;
        } else {
            final DfLanguageDependencyInfo languageDependencyInfo = basicProperties.getLanguageDependencyInfo();
            final LanguageMetaData languageMetaData = languageDependencyInfo.createLanguageMetaData();
            final Map<String, Object> jdbcToJavaNativeMap = languageMetaData.getJdbcToJavaNativeMap();
            return (String) jdbcToJavaNativeMap.get(DECIMAL);
        }
    }

    protected static String getJavaNativeString(String torqueType) {
        // Make sure the we are initialized.
        if (!_initialized) {
            initialize();
        }
        if (!_torqueTypeToJavaNativeMap.containsKey(torqueType)) {
            String msg = "_torqueTypeToJavaNativeMap doesn't contain the type as key: ";
            msg = msg + "key=" + torqueType + " map=" + _torqueTypeToJavaNativeMap;
            _log.warn(msg);
            throw new IllegalStateException(msg);
        }
        return _torqueTypeToJavaNativeMap.get(torqueType);
    }

    protected static String getFlexNativeString(String javaNative) {
        // Make sure the we are initialized.
        if (!_initialized) {
            initialize();
        }
        if (!_javaNativeToFlexNativeMap.containsKey(javaNative)) {
            String msg = "_javaNativeToFlexNativeMap doesn't contain the type as key: ";
            msg = msg + "key=" + javaNative + " map=" + _javaNativeToFlexNativeMap;
            _log.warn(msg);
            throw new IllegalStateException(msg);
        }
        return _javaNativeToFlexNativeMap.get(javaNative);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Returns true if the type is boolean in the java object and a numeric (1 or 0) in the database.
     * @param type The type to check.
     * @return true if the type is BOOLEANINT
     */
    public static boolean isBooleanInt(String type) {
        return BOOLEANINT.equals(type);
    }

    /**
     * Returns true if the type is boolean in the
     * java object and a String "Y" or "N" in the database.
     * @param type The type to check.
     * @return true if the type is BOOLEANCHAR
     */
    public static boolean isBooleanChar(String type) {
        return BOOLEANCHAR.equals(type);
    }

    /**
     * Returns true if values for the type need to be quoted.
     * @param type The type to check.
     * @return true if values for the type need to be quoted.
     */
    public static final boolean isTextType(String type) {
        for (int i = 0; i < TEXT_TYPES.length; i++) {
            if (type.equals(TEXT_TYPES[i])) {
                return true;
            }
        }

        // If we get this far, there were no matches.
        return false;
    }
}
