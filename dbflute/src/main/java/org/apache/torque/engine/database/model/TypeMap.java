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
 * ----------------------------------------------------
 * JDBC Type    | Java Type            | CSharp Type |
 * ----------------------------------------------------
 * CHAR          | java.lang.String     | String      |
 * VARCHAR       | java.lang.String     | String      |
 * LONGVARCHAR   | java.lang.String     | String      |
 * NUMERIC       | java.math.BigDecimal | decimal?    |
 * DECIMAL       | java.math.BigDecimal | decimal?    |
 * BIT           | java.lang.Boolean    | bool?       |
 * BOOLEAN       | java.lang.Boolean    | bool?       |
 * TINYINT       | java.lang.Integer    | int?        |
 * SMALLINT      | java.lang.Integer    | int?        |
 * INTEGER       | java.lang.Integer    | int?        |
 * BIGINT        | java.lang.Long       | long?       |
 * REAL          | java.math.BigDecimal | decimal?    |
 * FLOAT         | java.math.BigDecimal | decimal?    |
 * DOUBLE        | java.math.BigDecimal | decimal?    |
 * BINARY        | byte[]               | byte[]      |
 * VARBINARY     | byte[]               | byte[]      |
 * LONGVARBINARY | byte[]               | byte[]      |
 * DATE          | java.util.Date       | DateTime?   |
 * TIME          | java.sql.Time        | DateTime?   |
 * TIMESTAMP     | java.sql.Timestamp   | DateTime?   |
 * ----------------------------------------------------
 * </pre>
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
    public static final String BOOLEANCHAR_NATIVE_TYPE = "Boolean";
    public static final String BOOLEANINT_NATIVE_TYPE = "Boolean";

    // ===================================================================================
    //                                                                            Type Map
    //                                                                            ========
    private static Hashtable<String, String> _torqueTypeToJavaNativeMap = null;
    private static Hashtable<Integer, String> _jdbcTypeToTorqueTypeMap = null;
    private static Hashtable<String, String> _javaNativeToFlexNativeMap = null;

    // ===================================================================================
    //                                              Property StringJdbcTypeToJavaNativeMap
    //                                              ======================================
    protected static final Map<String, Object> _propertyTorqueTypeToJavaNativeMap;
    static {
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        _propertyTorqueTypeToJavaNativeMap = prop.getTypeMappingProperties().getJdbcToJavaNative();
    }

    // ===================================================================================
    //                                                                    Initialized Mark
    //                                                                    ================
    private static boolean _initialized = false;

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

        /*
         * Create JDBC -> native Java type mappings.
         */
        _torqueTypeToJavaNativeMap = new Hashtable<String, String>();

        // Default types are for Java.
        _torqueTypeToJavaNativeMap.put(CHAR, findJavaNative(CHAR, CHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(VARCHAR, findJavaNative(VARCHAR, VARCHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(LONGVARCHAR, findJavaNative(LONGVARCHAR, LONGVARCHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(CLOB, findJavaNative(CLOB, CLOB_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(NUMERIC, findJavaNative(NUMERIC, getDefaultNumericJavaType()));
        _torqueTypeToJavaNativeMap.put(DECIMAL, findJavaNative(DECIMAL, DECIMAL_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BIT, findJavaNative(BIT, BIT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BOOLEAN, findJavaNative(BOOLEAN, BOOLEAN_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(TINYINT, findJavaNative(TINYINT, TINYINT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(SMALLINT, findJavaNative(SMALLINT, SMALLINT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(INTEGER, findJavaNative(INTEGER, INTEGER_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BIGINT, findJavaNative(BIGINT, BIGINT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(REAL, findJavaNative(REAL, REAL_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(FLOAT, findJavaNative(FLOAT, FLOAT_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(DOUBLE, findJavaNative(DOUBLE, DOUBLE_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BINARY, findJavaNative(BINARY, BINARY_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(VARBINARY, findJavaNative(VARBINARY, VARBINARY_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(LONGVARBINARY, findJavaNative(LONGVARBINARY, LONGVARBINARY_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BLOB, findJavaNative(BLOB, BLOB_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(DATE, findJavaNative(DATE, DATE_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(TIME, findJavaNative(TIME, TIME_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(TIMESTAMP, findJavaNative(TIMESTAMP, TIMESTAMP_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BOOLEANCHAR, findJavaNative(BOOLEANCHAR, BOOLEANCHAR_NATIVE_TYPE));
        _torqueTypeToJavaNativeMap.put(BOOLEANINT, findJavaNative(BOOLEANINT, BOOLEANINT_NATIVE_TYPE));

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

        _javaNativeToFlexNativeMap = new Hashtable<String, String>();
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(CHAR), "String");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(VARCHAR), "String");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(LONGVARCHAR), "String");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(CLOB), "String");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(NUMERIC), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(DECIMAL), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(BIT), "Boolean");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(BOOLEAN), "Boolean");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(TINYINT), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(SMALLINT), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(INTEGER), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(BIGINT), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(REAL), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(FLOAT), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(DOUBLE), "Number");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(BINARY), "Object");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(VARBINARY), "Object");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(LONGVARBINARY), "Object");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(BLOB), "Object");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(DATE), "Date");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(TIME), "Date");
        _javaNativeToFlexNativeMap.put(_torqueTypeToJavaNativeMap.get(TIMESTAMP), "Date");

        _initialized = true;
    }

    /**
     * @param torqueType String JDBC type. (NotNull)
     * @param defaultJavaNative Default java-native. (NotNull)
     * @return Java-native. (NotNull: If the key does not have an element, it returns default type.)
     */
    protected static String findJavaNative(String torqueType, String defaultJavaNative) {
        final String javaNative = (String) _propertyTorqueTypeToJavaNativeMap.get(torqueType);
        if (javaNative == null) {
            return defaultJavaNative;
        }
        return javaNative;
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

    // ===================================================================================
    //                                                                    Java Type Getter
    //                                                                    ================
    public static Class<?> findJavaNativeTypeClass(String torqueType) {
        final String javaTypeString = getJavaTypeAsString(torqueType);
        Class<?> clazz = null;
        try {
            clazz = Class.forName(javaTypeString);
        } catch (ClassNotFoundException e) {
            final String fullName = "java.lang." + javaTypeString;
            try {
                clazz = Class.forName(fullName);
            } catch (ClassNotFoundException e1) {
            }
        }
        return clazz;
    }

    public static String findJavaNativeTypeString(String torqueType, Integer columnSize, Integer decimalDigits) {
        final String javaType = getJavaTypeAsString(torqueType);
        if (isAutoMappingTargetType(torqueType) && javaType.equalsIgnoreCase("$$AutoMapping$$")) {
            if (decimalDigits != null && decimalDigits > 0) {
                if (NUMERIC.equalsIgnoreCase(torqueType)) {
                    return getDefaultNumericJavaType();
                } else {// DECIMAL
                    return getDefaultDecimalJavaType();
                }
            } else {
                if (columnSize == null) {
                    return getJavaTypeAsString(TypeMap.BIGINT);
                }
                if (columnSize > 9) {
                    return getJavaTypeAsString(TypeMap.BIGINT);
                } else {
                    return getJavaTypeAsString(TypeMap.INTEGER);
                }
            }
        }
        return javaType;
    }
    
    public static String findFlexNativeTypeString(String javaNative) {
        return _javaNativeToFlexNativeMap.get(javaNative);
    }

    protected static boolean isAutoMappingTargetType(String torqueType) {
        return TypeMap.NUMERIC.equals(torqueType) || TypeMap.DECIMAL.equals(torqueType);
    }

    protected static String getDefaultNumericJavaType() {
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

    protected static String getDefaultDecimalJavaType() {
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

    protected static String getJavaTypeAsString(String torqueType) {
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
