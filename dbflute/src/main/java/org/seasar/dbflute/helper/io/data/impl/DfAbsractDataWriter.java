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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.exception.DfTableDataRegistrationFailureException;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimeException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimeOutOfCalendarException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimestampException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimestampOutOfCalendarException;
import org.seasar.dbflute.util.DfTypeUtil.ToBooleanParseException;

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
    protected boolean processNotNullNotString(String tableName, String columnName, Object obj, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (!isNotNullNotString(obj)) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            final Class<?> columnType = getColumnType4Judgement(columnMetaInfo);
            if (columnType != null) {
                doProcessNotNullNotStringByColumnType(obj, ps, bindCount, columnType);
                return true;
            }
        }
        doProcessNotNullNotStringByInstanceType(obj, ps, bindCount);
        return true;
    }

    protected void doProcessNotNullNotStringByColumnType(Object obj, PreparedStatement ps, int bindCount,
            Class<?> columnType) throws SQLException {
        if (Integer.class.isAssignableFrom(columnType)) {
            ps.setInt(bindCount, DfTypeUtil.toInteger(obj));
        } else if (Long.class.isAssignableFrom(columnType)) {
            ps.setLong(bindCount, DfTypeUtil.toLong(obj));
        } else if (BigDecimal.class.isAssignableFrom(columnType)) {
            ps.setBigDecimal(bindCount, DfTypeUtil.toBigDecimal(obj));
        } else if (Time.class.isAssignableFrom(columnType)) {
            ps.setTime(bindCount, DfTypeUtil.toTime(obj));
        } else if (Timestamp.class.isAssignableFrom(columnType)) {
            ps.setTimestamp(bindCount, DfTypeUtil.toTimestamp(obj));
        } else if (Date.class.isAssignableFrom(columnType)) {
            ps.setDate(bindCount, DfTypeUtil.toSqlDate(obj));
        } else if (Boolean.class.isAssignableFrom(columnType)) {
            ps.setBoolean(bindCount, DfTypeUtil.toBoolean(obj));
        } else {
            ps.setObject(bindCount, obj);
        }
    }

    protected void doProcessNotNullNotStringByInstanceType(Object obj, PreparedStatement ps, int bindCount)
            throws SQLException {
        if (obj instanceof Integer) {
            ps.setInt(bindCount, (Integer) obj);
        } else if (obj instanceof Long) {
            ps.setLong(bindCount, (Long) obj);
        } else if (obj instanceof BigDecimal) {
            ps.setBigDecimal(bindCount, (BigDecimal) obj);
        } else if (obj instanceof Time) {
            ps.setTime(bindCount, (Time) obj);
        } else if (obj instanceof Timestamp) {
            ps.setTimestamp(bindCount, (Timestamp) obj);
        } else if (obj instanceof Date) {
            ps.setDate(bindCount, DfTypeUtil.toSqlDate((Date) obj));
        } else if (obj instanceof Boolean) {
            ps.setBoolean(bindCount, (Boolean) obj);
        } else {
            ps.setObject(bindCount, obj);
        }
    }

    protected boolean isNotNullNotString(Object obj) {
        return obj != null && !(obj instanceof String);
    }

    // -----------------------------------------------------
    //                                            Null Value
    //                                            ----------
    protected boolean processNull(String tableName, String columnName, Object value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (!isNullValue(value)) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo == null) {
            return false;
        }
        final int jdbcType = columnMetaInfo.getJdbcDefValue();
        try {
            ps.setNull(bindCount, jdbcType);
        } catch (SQLException e) {
            if (jdbcType != Types.OTHER) {
                throw e;
            }
            final String torqueType = _columnHandler.getColumnJdbcType(columnMetaInfo);
            final Integer mappedJdbcType = TypeMap.getJdbcDefValueByJdbcType(torqueType);
            try {
                ps.setNull(bindCount, mappedJdbcType);
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
    //                                             Timestamp
    //                                             ---------
    protected boolean processTimestamp(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false;
        }
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        boolean typeChecked = false;
        if (columnMetaInfo != null) {
            final Class<?> columnType = getColumnType4Judgement(columnMetaInfo);
            if (columnType != null && !java.util.Date.class.isAssignableFrom(columnType)) {
                return false;
            }
            if (columnType != null && java.sql.Time.class.isAssignableFrom(columnType)) {
                return false; // Time type is out of target here
            }
            // basically java.util.Date and java.sql.Timestamp are target

            typeChecked = true; // for handling out of calendar
        }
        try {
            Timestamp timestamp = DfTypeUtil.toTimestamp(value);
            ps.setTimestamp(bindCount, timestamp);
            return true;
        } catch (ParseTimestampOutOfCalendarException e) {
            if (typeChecked) {
                String msg = "Look! Read the message below." + ln();
                msg = msg + "/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + ln();
                msg = msg + "Failed to set the timestamp because the value was out of calendar!" + ln();
                msg = msg + ln();
                msg = msg + "[Table]" + ln() + tableName + ln();
                msg = msg + ln();
                msg = msg + "[Column]" + ln() + columnName + ln();
                msg = msg + ln();
                msg = msg + "[Value]" + ln() + value + ln();
                msg = msg + "- - - - - - - - - -/";
                throw new DfTableDataRegistrationFailureException(msg, e);
            } else {
                return false; // couldn't parse as timestamp
            }
        } catch (ParseTimestampException ignored) {
            return false; // couldn't parse as timestamp 
        }
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    protected boolean processTime(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
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
        try {
            Time time = DfTypeUtil.toTime(value);
            ps.setTime(bindCount, time);
            return true;
        } catch (ParseTimeOutOfCalendarException e) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + ln();
            msg = msg + "Failed to set the time because the value was out of calendar!" + ln();
            msg = msg + ln();
            msg = msg + "[Table]" + ln() + tableName + ln();
            msg = msg + ln();
            msg = msg + "[Column]" + ln() + columnName + ln();
            msg = msg + ln();
            msg = msg + "[Value]" + ln() + value + ln();
            msg = msg + "- - - - - - - - - -/";
            throw new DfTableDataRegistrationFailureException(msg, e);
        } catch (ParseTimeException ignored) {
            return false; // couldn't parse as time 
        }
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    protected boolean processBoolean(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
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
        try {
            Boolean booleanValue = DfTypeUtil.toBoolean(value);
            ps.setBoolean(bindCount, booleanValue);
            return true;
        } catch (ToBooleanParseException ignored) {
            return false; // couldn't parse as boolean
        }
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    protected boolean processNumber(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
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
    protected boolean processUUID(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
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
    protected boolean processArray(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        final DfColumnMetaInfo columnMetaInfo = columnMetaInfoMap.get(columnName);
        if (columnMetaInfo != null) {
            //rsMeta#getColumnTypeName() returns value starts with "_" if
            //rsMeta#getColumnType() returns Types.ARRAY in PostgreSQL.
            //  e.g. UUID[] -> _uuid
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

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
