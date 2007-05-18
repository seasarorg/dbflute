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

/**
 * A class that maps JDBC types to their corresponding
 * Java object types, and Java native types. Used
 * by Column.java to perform object/native mappings.
 *
 * These are the official SQL type to Java type mappings.
 * These don't quite correspond to the way the peer
 * system works so we'll have to make some adjustments.
 * <pre>
 * --------------------------------------
 * SQL Type      | Java Type            |
 * --------------------------------------
 * CHAR          | String               |
 * VARCHAR       | String               |
 * LONGVARCHAR   | String               |
 * NUMERIC       | java.math.BigDecimal |
 * DECIMAL       | java.math.BigDecimal |
 * BIT           | Boolean              |
 * TINYINT       | java.math.BigDecimal |
 * SMALLINT      | java.math.BigDecimal |
 * INTEGER       | java.math.BigDecimal |
 * BIGINT        | java.math.BigDecimal |
 * REAL          | java.math.BigDecimal |
 * FLOAT         | java.math.BigDecimal |
 * DOUBLE        | java.math.BigDecimal |
 * BINARY        | byte[]               |
 * VARBINARY     | byte[]               |
 * LONGVARBINARY | byte[]               |
 * DATE          | java.util.Date       |
 * TIME          | java.sql.Time        |
 * TIMESTAMP     | java.sql.Timestamp   |
 *
 * -------------------------------------------------------
 * A couple variations have been introduced to cover cases
 * that may arise, but are not covered above
 * BOOLEANCHAR   | Boolean              | String
 * BOOLEANINT    | OR Boolean           | Integer
 * </pre>
 *
 */
public class TypeMap {

    /** Log instance. */
    public static final Log _log = LogFactory.getLog(TypeMap.class);

    // =========================================================================
    //                                                                  SQL Type
    //                                                                  ========
    public static final String CHAR = "CHAR";
    public static final String VARCHAR = "VARCHAR";
    public static final String LONGVARCHAR = "LONGVARCHAR";
    public static final String CLOB = "CLOB";
    public static final String NUMERIC = "NUMERIC";
    public static final String DECIMAL = "DECIMAL";
    public static final String BIT = "BIT";
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

    // =========================================================================
    //                                                               Native Type
    //                                                               ===========
    // This is default native type.
    public static final String CHAR_NATIVE_TYPE = "String";
    public static final String VARCHAR_NATIVE_TYPE = "String";
    public static final String LONGVARCHAR_NATIVE_TYPE = "String";
    public static final String CLOB_NATIVE_TYPE = "String";
    public static final String NUMERIC_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String DECIMAL_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String BIT_NATIVE_TYPE = "Boolean";
    public static final String TINYINT_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String SMALLINT_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String INTEGER_NATIVE_TYPE = "java.math.BigDecimal";
    public static final String BIGINT_NATIVE_TYPE = "java.math.BigDecimal";
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

    private static Hashtable<String, String> _jdbcToJavaNativeMap = null;
    private static Hashtable<String, String> _jdbcToTorqueTypeMap = null;
    private static Hashtable<Integer, String> _jdbcIntToTorqueTypeMap = null;
    private static boolean isInitialized = false;

    /** JDBCToJavaNativeMap from Property. */
    protected static final Map<String, Object> _propertyOfJDBCToJavaNativeMap;
    static {
        _propertyOfJDBCToJavaNativeMap = DfBuildProperties.getInstance().getJdbcToJavaNative();
    }

    /**
     * Get JDBC-to-java-native.
     * 
     * @param key JDBC type.
     * @param defaultJavaNative Default java-native.
     * @return Java-native.
     */
    protected static String getJavaNativeByJdbc(String key, String defaultJavaNative) {
        final String javaNative = (String) _propertyOfJDBCToJavaNativeMap.get(key);
        if (javaNative == null) {
            return defaultJavaNative;
        }
        return javaNative;
    }

    /**
     * Initializes the SQL to Java map so that it
     * can be used by client code.
     */
    public synchronized static void initialize() {
        if (!isInitialized) {

            /*
             * Create JDBC -> native Java type mappings.
             */

            _jdbcToJavaNativeMap = new Hashtable<String, String>();

            _jdbcToJavaNativeMap.put(CHAR, getJavaNativeByJdbc(CHAR, CHAR_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(VARCHAR, getJavaNativeByJdbc(VARCHAR, VARCHAR_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(LONGVARCHAR, getJavaNativeByJdbc(LONGVARCHAR, LONGVARCHAR_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(CLOB, getJavaNativeByJdbc(CLOB, CLOB_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(NUMERIC, getJavaNativeByJdbc(NUMERIC, NUMERIC_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(DECIMAL, getJavaNativeByJdbc(DECIMAL, DECIMAL_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(BIT, getJavaNativeByJdbc(BIT, BIT_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(TINYINT, getJavaNativeByJdbc(TINYINT, TINYINT_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(SMALLINT, getJavaNativeByJdbc(SMALLINT, SMALLINT_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(INTEGER, getJavaNativeByJdbc(INTEGER, INTEGER_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(BIGINT, getJavaNativeByJdbc(BIGINT, BIGINT_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(REAL, getJavaNativeByJdbc(REAL, REAL_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(FLOAT, getJavaNativeByJdbc(FLOAT, FLOAT_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(DOUBLE, getJavaNativeByJdbc(DOUBLE, DOUBLE_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(BINARY, getJavaNativeByJdbc(BINARY, BINARY_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(VARBINARY, getJavaNativeByJdbc(VARBINARY, VARBINARY_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(LONGVARBINARY, getJavaNativeByJdbc(LONGVARBINARY, LONGVARBINARY_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(BLOB, getJavaNativeByJdbc(BLOB, BLOB_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(DATE, getJavaNativeByJdbc(DATE, DATE_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(TIME, getJavaNativeByJdbc(TIME, TIME_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(TIMESTAMP, getJavaNativeByJdbc(TIMESTAMP, TIMESTAMP_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(BOOLEANCHAR, getJavaNativeByJdbc(BOOLEANCHAR, BOOLEANCHAR_NATIVE_TYPE));
            _jdbcToJavaNativeMap.put(BOOLEANINT, getJavaNativeByJdbc(BOOLEANINT, BOOLEANINT_NATIVE_TYPE));

            //            _jdbcToJavaNativeMap.put(CHAR, CHAR_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(VARCHAR, VARCHAR_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(LONGVARCHAR, LONGVARCHAR_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(CLOB, CLOB_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(NUMERIC, NUMERIC_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(DECIMAL, DECIMAL_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(BIT, BIT_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(TINYINT, TINYINT_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(SMALLINT, SMALLINT_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(INTEGER, INTEGER_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(BIGINT, BIGINT_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(REAL, REAL_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(FLOAT, FLOAT_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(DOUBLE, DOUBLE_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(BINARY, BINARY_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(VARBINARY, VARBINARY_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(LONGVARBINARY, LONGVARBINARY_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(BLOB, BLOB_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(DATE, DATE_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(TIME, TIME_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(TIMESTAMP, TIMESTAMP_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(BOOLEANCHAR, BOOLEANCHAR_NATIVE_TYPE);
            //            _jdbcToJavaNativeMap.put(BOOLEANINT, BOOLEANINT_NATIVE_TYPE);

            _jdbcToTorqueTypeMap = new Hashtable<String, String>();
            _jdbcToTorqueTypeMap.put(CHAR, CHAR);
            _jdbcToTorqueTypeMap.put(VARCHAR, VARCHAR);
            _jdbcToTorqueTypeMap.put(LONGVARCHAR, LONGVARCHAR);
            _jdbcToTorqueTypeMap.put(CLOB, CLOB);
            _jdbcToTorqueTypeMap.put(NUMERIC, NUMERIC);
            _jdbcToTorqueTypeMap.put(DECIMAL, DECIMAL);
            _jdbcToTorqueTypeMap.put(BIT, BIT);
            _jdbcToTorqueTypeMap.put(TINYINT, TINYINT);
            _jdbcToTorqueTypeMap.put(SMALLINT, SMALLINT);
            _jdbcToTorqueTypeMap.put(INTEGER, INTEGER);
            _jdbcToTorqueTypeMap.put(BIGINT, BIGINT);
            _jdbcToTorqueTypeMap.put(REAL, REAL);
            _jdbcToTorqueTypeMap.put(FLOAT, FLOAT);
            _jdbcToTorqueTypeMap.put(DOUBLE, DOUBLE);
            _jdbcToTorqueTypeMap.put(BINARY, BINARY);
            _jdbcToTorqueTypeMap.put(VARBINARY, VARBINARY);
            _jdbcToTorqueTypeMap.put(LONGVARBINARY, LONGVARBINARY);
            _jdbcToTorqueTypeMap.put(BLOB, BLOB);
            _jdbcToTorqueTypeMap.put(DATE, DATE);
            _jdbcToTorqueTypeMap.put(TIME, TIME);
            _jdbcToTorqueTypeMap.put(TIMESTAMP, TIMESTAMP);

            _jdbcIntToTorqueTypeMap = new Hashtable<Integer, String>();
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.CHAR), CHAR);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.VARCHAR), VARCHAR);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.LONGVARCHAR), LONGVARCHAR);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.CLOB), CLOB);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.NUMERIC), NUMERIC);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.DECIMAL), DECIMAL);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.BIT), BIT);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.TINYINT), TINYINT);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.SMALLINT), SMALLINT);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.INTEGER), INTEGER);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.BIGINT), BIGINT);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.REAL), REAL);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.FLOAT), FLOAT);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.DOUBLE), DOUBLE);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.BINARY), BINARY);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.VARBINARY), VARBINARY);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.LONGVARBINARY), LONGVARBINARY);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.BLOB), BLOB);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.DATE), DATE);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.TIME), TIME);
            _jdbcIntToTorqueTypeMap.put(new Integer(Types.TIMESTAMP), TIMESTAMP);

            isInitialized = true;
        }
    }

    /**
     * Report whether this object has been initialized.
     *
     * @return true if this object has been initialized
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Return native java type which corresponds to the
     * JDBC type provided. Use in the base object class generation.
     *
     * @param jdbcType the JDBC type
     * @return name of the native java type
     */
    public static String getJavaType(String jdbcType) {
        // Make sure the we are initialized.
        if (!isInitialized) {
            initialize();
        }
        if (!_jdbcToJavaNativeMap.containsKey(jdbcType)) {
            String msg = "_jdbcToJavaNativeMap doesn't contain the type as key: ";
            msg = msg + "key=" + jdbcType + " map=" + _jdbcToJavaNativeMap;
            _log.warn(msg);
            throw new IllegalStateException(msg);
        }
        return _jdbcToJavaNativeMap.get(jdbcType);
    }

    /**
     * Returns Torque type constant corresponding to JDBC type code.
     * Used but Torque JDBC task.
     *
     * @param jdbcType the SQL type
     * @return Torque type constant
     */
    public static String getTorqueType(String jdbcType) {
        // Make sure the we are initialized.
        if (!isInitialized) {
            initialize();
        }
        if (!_jdbcToTorqueTypeMap.containsKey(jdbcType)) {
            String msg = "_jdbcToTorqueTypeMap doesn't contain the type as key: ";
            msg = msg + "key=" + jdbcType + " map=" + _jdbcIntToTorqueTypeMap;
            _log.warn(msg);
            throw new IllegalStateException(msg);
        }
        return _jdbcToTorqueTypeMap.get(jdbcType);
    }

    /**
     * Returns Torque type constant corresponding to JDBC type code.
     * Used but Torque JDBC task.
     *
     * @param jdbcType the SQL type
     * @return Torque type constant
     */
    public static String getTorqueType(Integer jdbcType) {
        // Make sure the we are initialized.
        if (!isInitialized) {
            initialize();
        }
        if (java.sql.Types.OTHER == jdbcType) {
            String msg = "The jdbcType is unsupported: jdbcType=java.sql.Types.OTHER(" + jdbcType + ")";
            throw new UnsupportedOperationException(msg);
        }
        if (!_jdbcIntToTorqueTypeMap.containsKey(jdbcType)) {
            String msg = "_jdbcIntToTorqueTypeMap doesn't contain the type as key: ";
            msg = msg + "key=" + jdbcType + " map=" + _jdbcIntToTorqueTypeMap;
            _log.warn(msg);
            throw new IllegalStateException(msg);
        }
        return _jdbcIntToTorqueTypeMap.get(jdbcType);
    }

    /**
     * Returns true if the type is boolean in the java
     * object and a numeric (1 or 0) in the db.
     *
     * @param type The type to check.
     * @return true if the type is BOOLEANINT
     */
    public static boolean isBooleanInt(String type) {
        return BOOLEANINT.equals(type);
    }

    /**
     * Returns true if the type is boolean in the
     * java object and a String "Y" or "N" in the db.
     *
     * @param type The type to check.
     * @return true if the type is BOOLEANCHAR
     */
    public static boolean isBooleanChar(String type) {
        return BOOLEANCHAR.equals(type);
    }

    /**
     * Returns true if values for the type need to be quoted.
     *
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
