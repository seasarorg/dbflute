/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.io.data.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.metadata.DfColumnHandler;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/25 Wednesday)
 */
public abstract class DfAbsractDataWriter {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfAbsractDataWriter.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of columns for getting column meta information(as helper). */
    protected DfColumnHandler _columnHandler = new DfColumnHandler();

    // ===================================================================================
    //                                                                    Process per Type
    //                                                                    ================
    // -----------------------------------------------------
    //                                     NotNull NotString
    //                                     -----------------
    protected boolean processNotNullNotString(String columnName, Object obj, PreparedStatement statement, int bindCount)
            throws SQLException {
        if (!isNotNullNotString(obj)) {
            return false;
        }
        if (obj instanceof Time) {
            statement.setTime(bindCount, (Time) obj);
        } else if (obj instanceof Timestamp) {
            statement.setTimestamp(bindCount, (Timestamp) obj);
        } else if (obj instanceof Date) {
            statement.setDate(bindCount, DfTypeUtil.toSqlDate((Date) obj));
        } else if (obj instanceof BigDecimal) {
            statement.setBigDecimal(bindCount, (BigDecimal) obj);
        } else if (obj instanceof Boolean) {
            statement.setBoolean(bindCount, (Boolean) obj);
        } else {
            statement.setObject(bindCount, obj);
        }
        return true;
    }

    protected boolean isNotNullNotString(Object obj) {
        return obj != null && !(obj instanceof String);
    }

    // -----------------------------------------------------
    //                                            Null Value
    //                                            ----------
    protected boolean processNull(String columnName, Object value, PreparedStatement statement, int bindCount,
            DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (!isNullValue(value)) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo == null) {
            return false;
        }
        final int jdbcType = columnMetaInfo.getJdbcDefValue();
        try {
            statement.setNull(bindCount, jdbcType);
        } catch (SQLException e) {
            if (jdbcType != Types.OTHER) {
                throw e;
            }
            final String torqueType = _columnHandler.getColumnJdbcType(columnMetaInfo);
            final Integer mappedJdbcType = TypeMap.getJdbcDefValueByJdbcType(torqueType);
            try {
                statement.setNull(bindCount, mappedJdbcType);
            } catch (SQLException ignored) {
                String msg = "Failed to re-try setNull(" + columnName + ", " + mappedJdbcType + "):";
                msg = msg + " " + ignored.getMessage();
                _log.info(msg);
                throw e;
            }
        }
        return true;
    }

    protected boolean isNullValue(Object value) {
        return value == null;
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    protected boolean processTime(String columnName, String value, PreparedStatement ps, int bindCount,
            DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            final Class<?> columnType = getColumnType4Judgement(columnMetaInfo);
            if (columnType != null && !java.sql.Time.class.isAssignableFrom(columnType)) {
                return false;
            }
        }
        value = filterTimeValue(value);
        if (!isTimeValue(value)) {
            return false;
        }
        final Time timeValue = getTimeValue(columnName, value);
        ps.setTime(bindCount, timeValue);
        return true;
    }

    protected String filterTimeValue(String value) {
        value = value.trim();
        if (value.indexOf(":") == 1 && value.lastIndexOf(":") == 4) {
            value = "0" + value;
        }
        if (value.indexOf(":") == 2 && value.lastIndexOf(":") == 5 && value.indexOf(".") == 8) {
            value = value.substring(0, 8);
        }
        return value;
    }

    protected boolean isTimeValue(String value) {
        if (value == null) {
            return false;
        }
        try {
            Time.valueOf(value);
            return true;
        } catch (RuntimeException e) {
        }
        return false;
    }

    protected Time getTimeValue(String columnName, String value) {
        try {
            return Time.valueOf(value);
        } catch (RuntimeException e) {
            String msg = "The value cannot be convert to time:";
            msg = msg + " columnName=" + columnName + " value=" + value;
            throw new IllegalStateException(msg, e);
        }
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    protected boolean processTimestamp(String columnName, String value, PreparedStatement ps, int bindCount,
            DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            final Class<?> columnType = getColumnType4Judgement(columnMetaInfo);
            if (columnType != null && !java.util.Date.class.isAssignableFrom(columnType)) {
                return false;
            }
        }
        value = filterTimestampValue(value);
        if (!isTimestampValue(value)) {
            return false;
        }
        final Timestamp timestampValue = getTimestampValue(columnName, value);
        ps.setTimestamp(bindCount, timestampValue);
        return true;
    }

    protected String filterTimestampValue(String value) {
        value = value.trim();
        if (value.indexOf("/") == 4 && value.lastIndexOf("/") == 7) {
            value = value.replaceAll("/", "-");
        }
        if (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7) {
            if (value.length() == "2007-07-09".length()) {
                value = value + " 00:00:00";
            }
        }
        return value;
    }

    protected boolean isTimestampValue(String value) {
        if (value == null) {
            return false;
        }
        try {
            Timestamp.valueOf(value);
            return true;
        } catch (RuntimeException e) {
        }
        return false;
    }

    protected Timestamp getTimestampValue(String columnName, String value) {
        try {
            return Timestamp.valueOf(value);
        } catch (RuntimeException e) {
            String msg = "The value cannot be convert to timestamp:";
            msg = msg + " columnName=" + columnName + " value=" + value;
            throw new IllegalStateException(msg, e);
        }
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    protected boolean processBoolean(String columnName, String value, PreparedStatement ps, int bindCount,
            DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            final Class<?> columnType = getColumnType4Judgement(columnMetaInfo);
            if (columnType != null && !Boolean.class.isAssignableFrom(columnType)) {
                return false;
            }
        }
        value = filterBooleanValue(value);
        if (!isBooleanValue(value)) {
            return false;
        }
        final Boolean booleanValue = getBooleanValue(value);
        ps.setBoolean(bindCount, booleanValue);
        return true;
    }

    protected String filterBooleanValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if ("t".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
            return "true";
        } else if ("f".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
            return "false";
        } else {
            return value.toLowerCase();
        }
    }

    protected boolean isBooleanValue(String value) {
        if (value == null) {
            return false;
        }
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }

    protected Boolean getBooleanValue(String value) {
        try {
            return Boolean.valueOf(value);
        } catch (RuntimeException e) {
            String msg = "The value should be boolean: value=" + value;
            throw new IllegalStateException(msg, e);
        }
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    protected boolean processNumber(String columnName, String value, PreparedStatement ps, int bindCount,
            DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            final Class<?> columnType = getColumnType4Judgement(columnMetaInfo);
            if (columnType != null && !Number.class.isAssignableFrom(columnType)) {
                return false;
            }
        }
        value = filterBigDecimalValue(value);
        if (!isBigDecimalValue(value)) {
            return false;
        }
        final BigDecimal bigDecimalValue = getBigDecimalValue(columnName, value);
        try {
            final long longValue = bigDecimalValue.longValueExact();
            ps.setLong(bindCount, longValue);
            return true;
        } catch (ArithmeticException e) {
            ps.setBigDecimal(bindCount, bigDecimalValue);
            return true;
        }
    }

    protected String filterBigDecimalValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value;
    }

    protected boolean isBigDecimalValue(String value) {
        if (value == null) {
            return false;
        }
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    protected BigDecimal getBigDecimalValue(String columnName, String value) {
        try {
            return new BigDecimal(value);
        } catch (RuntimeException e) {
            String msg = "The value should be big decimal: ";
            msg = msg + " columnName=" + columnName + " value=" + value;
            throw new IllegalStateException(msg, e);
        }
    }

    // -----------------------------------------------------
    //                                                  UUID
    //                                                  ----
    protected boolean processUUID(String columnName, String value, PreparedStatement ps, int bindCount,
            DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            if (columnMetaInfo.getJdbcDefValue() != Types.OTHER
                    || !"uuid".equalsIgnoreCase(columnMetaInfo.getDbTypeName())) {
                return false;
            }

            // This is resolved only when the information of column meta exists.
            // If the information of column meta is null,do nothing here!
            // And basically this is for PostgreSQL.
            value = filterUUIDValue(value);
            ps.setObject(bindCount, value, Types.OTHER);
            return true;
        }
        return false;
    }

    protected String filterUUIDValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value;
    }

    // -----------------------------------------------------
    //                                    ARRAY (PostgreSQL)
    //                                    ------------------
    protected boolean processArray(String columnName, String value, PreparedStatement ps, int bindCount,
            DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            // rsMeta#getColumnTypeName() returns value starts with "_" if
            // rsMeta#getColumnType() returns Types.ARRAY in PostgreSQL.
            //   e.g. UUID[] -> _uuid
            if (columnMetaInfo.getJdbcDefValue() != Types.ARRAY || !columnMetaInfo.getDbTypeName().startsWith("_")) {
                return false;
            }

            // This is resolved only when the information of column meta exists.
            // If the information of column meta is null,do nothing here!
            // And basically this is for PostgreSQL.
            value = filterArrayValue(value);
            ps.setObject(bindCount, value, Types.OTHER);
            return true;
        }
        return false;
    }

    protected String filterArrayValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value;
    }

    // -----------------------------------------------------
    //                                           Type Helper
    //                                           -----------
    /**
     * @param columnMetaInfo The meta information of column. (NotNull)
     * @return The type of column. (Nullable: However Basically NotNull)
     */
    protected Class<?> getColumnType4Judgement(DfColumnMetaInfo columnMetaInfo) { // by original way
        final String torqueType = _columnHandler.getColumnJdbcType(columnMetaInfo);
        final int columnSize = columnMetaInfo.getColumnSize();
        final int decimalDigits = columnMetaInfo.getDecimalDigits();
        final String javaNativeString = TypeMap.findJavaNativeByJdbcType(torqueType, columnSize, decimalDigits);
        Class<?> clazz = null;
        try {
            clazz = Class.forName(javaNativeString);
        } catch (ClassNotFoundException e) {
            final String fullName = "java.lang." + javaNativeString;
            try {
                clazz = Class.forName(fullName);
            } catch (ClassNotFoundException ignored) {
                // Only Date and Boolean and Number
                final int jdbcType = columnMetaInfo.getJdbcDefValue();
                if (jdbcType == Types.TIMESTAMP || jdbcType == Types.DATE) {
                    return Date.class;
                } else if (jdbcType == Types.TIME) {
                    return Time.class;
                } else if (jdbcType == Types.BIT || jdbcType == Types.BOOLEAN) {
                    return Boolean.class;
                } else if (jdbcType == Types.NUMERIC || jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT
                        || jdbcType == Types.FLOAT || jdbcType == Types.DECIMAL || jdbcType == Types.REAL
                        || jdbcType == Types.TINYINT) {
                    return Number.class;
                }
            }
        }
        return clazz;
    }
}
