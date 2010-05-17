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
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfTableDataRegistrationFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.DfTypeUtil.ParseBooleanException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimeException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimeOutOfCalendarException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimestampException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimestampOutOfCalendarException;

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
    /** The data source. (NotNull) */
    protected DataSource _dataSource;

    /** The unified schema (for getting database meta data). (Nullable) */
    protected UnifiedSchema _unifiedSchema;

    /** Does it output the insert SQLs as logging? */
    protected boolean _loggingInsertSql;

    /** The handler of columns for getting column meta information(as helper). */
    protected final DfColumnHandler _columnHandler = new DfColumnHandler();

    /** The cache map of meta info. The key is table name. */
    protected final Map<String, Map<String, DfColumnMetaInfo>> _metaInfoCacheMap = StringKeyMap.createAsFlexible();

    /** The cache map of string processor. The key is table name. */
    protected final Map<String, Map<String, StringProcessor>> _stringProcessorCacheMap = StringKeyMap
            .createAsFlexible();

    /** The definition list of string processor instances. (NotNull, ReadOnly) */
    protected final List<StringProcessor> _stringProcessorList = DfCollectionUtil.newArrayList();
    {
        _stringProcessorList.add(new TimestampStringProcessor());
        _stringProcessorList.add(new TimeStringProcessor());
        _stringProcessorList.add(new BooleanStringProcessor());
        _stringProcessorList.add(new NumberStringProcessor());
        _stringProcessorList.add(new UUIDStringProcessor());
        _stringProcessorList.add(new ArrayStringProcessor());
        _stringProcessorList.add(new RealStringProcessor());
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfAbsractDataWriter(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                     Process Binding
    //                                                                     ===============
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
    //                                        NotNull String
    //                                        --------------
    protected void processNotNullString(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            String msg = "This method is only for NotNull and StringExpression:";
            msg = msg + " value=" + value + " type=" + (value != null ? value.getClass() : "null");
            throw new IllegalStateException(msg);
        }
        value = Srl.unquoteDouble(value);
        Map<String, StringProcessor> cacheMap = _stringProcessorCacheMap.get(tableName);
        if (cacheMap == null) {
            cacheMap = StringKeyMap.createAsFlexibleOrdered();
            _stringProcessorCacheMap.put(tableName, cacheMap);
        }
        final StringProcessor processor = cacheMap.get(columnName);
        if (processor != null) { // cache hit
            final boolean processed = processor.process(tableName, columnName, value, ps, bindCount, columnMetaInfoMap);
            if (!processed) {
                throwColumnValueProcessingFailureException(processor, tableName, columnName, value);
            }
            return;
        }
        for (StringProcessor tryProcessor : _stringProcessorList) {
            // processing and searching target processor
            if (tryProcessor.process(tableName, columnName, value, ps, bindCount, columnMetaInfoMap)) {
                cacheMap.put(columnName, tryProcessor); // use cache next times
                break;
            }
        }
    }

    protected void throwColumnValueProcessingFailureException(StringProcessor processor, String tableName,
            String columnName, String value) {
        final Class<?> type = processor.getTargetType();
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The column value could not be treated as " + type.getName() + ".");
        br.addItem("Advice");
        br.addElement("The column has string expressions judging the type of the column");
        br.addElement("by analyzing the value of first record.");
        br.addElement("But the value of second or more record did not match the type.");
        br.addElement("So confirm your expressions.");
        br.addItem("Table Name");
        br.addElement(tableName);
        br.addItem("Column Name");
        br.addElement(columnName);
        br.addItem("String Expression");
        br.addElement(value);
        br.addItem("Analyzed Type");
        br.addElement(type);
        final String msg = br.buildExceptionMessage();
        throw new DfTableDataRegistrationFailureException(msg);
    }

    public static interface StringProcessor {
        boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException;

        Class<?> getTargetType();
    }

    protected class TimestampStringProcessor implements StringProcessor {

        public boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
            return processTimestamp(tableName, columnName, value, ps, bindCount, columnMetaInfoMap);
        }

        public Class<?> getTargetType() {
            return Timestamp.class;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class TimeStringProcessor implements StringProcessor {

        public boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
            return processTime(tableName, columnName, value, ps, bindCount, columnMetaInfoMap);
        }

        public Class<?> getTargetType() {
            return Time.class;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class BooleanStringProcessor implements StringProcessor {

        public boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
            return processBoolean(tableName, columnName, value, ps, bindCount, columnMetaInfoMap);
        }

        public Class<?> getTargetType() {
            return Boolean.class;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class NumberStringProcessor implements StringProcessor {

        public boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
            return processNumber(tableName, columnName, value, ps, bindCount, columnMetaInfoMap);
        }

        public Class<?> getTargetType() {
            return Number.class;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class UUIDStringProcessor implements StringProcessor {

        public boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
            return processUUID(tableName, columnName, value, ps, bindCount, columnMetaInfoMap);
        }

        public Class<?> getTargetType() {
            return UUID.class;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class ArrayStringProcessor implements StringProcessor {

        public boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
            return processArray(tableName, columnName, value, ps, bindCount, columnMetaInfoMap);
        }

        public Class<?> getTargetType() {
            return Array.class;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class RealStringProcessor implements StringProcessor {

        public boolean process(String tableName, String columnName, String value, PreparedStatement ps, int bindCount,
                Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
            ps.setString(bindCount, value);
            return true;
        }

        public Class<?> getTargetType() {
            return String.class;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected String buildProcessorToString(StringProcessor processor) {
        return DfTypeUtil.toClassTitle(processor);
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    protected boolean processTimestamp(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
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
            return false; // basically no way
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
            return false; // basically no way
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
        } catch (ParseBooleanException ignored) {
            return false; // couldn't parse as boolean
        }
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    protected boolean processNumber(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMetaInfo> columnMetaInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
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
        if (value == null) {
            return false; // basically no way
        }
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
        if (value == null) {
            return false; // basically no way
        }
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

    // ===================================================================================
    //                                                                    Column Meta Info
    //                                                                    ================
    protected Map<String, DfColumnMetaInfo> getColumnMetaInfo(String tableName) {
        if (_metaInfoCacheMap.containsKey(tableName)) {
            return _metaInfoCacheMap.get(tableName);
        }
        final Map<String, DfColumnMetaInfo> columnMetaInfoMap = StringKeyMap.createAsFlexible();
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            final List<DfColumnMetaInfo> columnList = _columnHandler.getColumnList(metaData, _unifiedSchema, tableName);
            for (DfColumnMetaInfo columnMetaInfo : columnList) {
                columnMetaInfoMap.put(columnMetaInfo.getColumnName(), columnMetaInfo);
            }
            _metaInfoCacheMap.put(tableName, columnMetaInfoMap);
            return columnMetaInfoMap;
        } catch (SQLException e) {
            String msg = "Failed to get column meta informations: table=" + tableName;
            throw new IllegalStateException(msg, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public UnifiedSchema getUnifiedSchema() {
        return _unifiedSchema;
    }

    public void setUnifiedSchema(UnifiedSchema unifiedSchema) {
        _unifiedSchema = unifiedSchema;
    }

    public boolean isLoggingInsertSql() {
        return _loggingInsertSql;
    }

    public void setLoggingInsertSql(boolean loggingInsertSql) {
        this._loggingInsertSql = loggingInsertSql;
    }
}
