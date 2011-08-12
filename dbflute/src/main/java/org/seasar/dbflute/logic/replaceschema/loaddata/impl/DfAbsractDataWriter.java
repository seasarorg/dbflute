/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.exception.DfLoadDataRegistrationFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfColumnExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMeta;
import org.seasar.dbflute.logic.replaceschema.loaddata.interceptor.DfDataWritingInterceptor;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.filereader.DfMapStringFileReader;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.DfTypeUtil.ParseBooleanException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimeException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimestampException;
import org.seasar.dbflute.util.Srl;

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

    /** The unified schema (for getting database meta data). (NullAllowed) */
    protected UnifiedSchema _unifiedSchema;

    /** Does it output the insert SQLs as logging? */
    protected boolean _loggingInsertSql;

    /** Does it suppress batch updates? */
    protected boolean _suppressBatchUpdate;

    /** The interceptor of data writing. (NullAllowed) */
    protected DfDataWritingInterceptor _dataWritingInterceptor;

    /** The handler of columns for getting column meta information(as helper). */
    protected final DfColumnExtractor _columnHandler = new DfColumnExtractor();

    /** The cache map of meta info. The key is table name. (ordered for display) */
    protected final Map<String, Map<String, DfColumnMeta>> _columnInfoCacheMap = StringKeyMap.createAsFlexibleOrdered();

    /** The cache map of bind type. The key is table name. (ordered for display) */
    protected final Map<String, Map<String, Class<?>>> _bindTypeCacheMap = StringKeyMap.createAsFlexibleOrdered();

    /** The cache map of string processor. The key is table name. (ordered for display) */
    protected final Map<String, Map<String, StringProcessor>> _stringProcessorCacheMap = StringKeyMap
            .createAsFlexibleOrdered();

    /** The definition list of string processor instances. (NotNull, ReadOnly) */
    protected final List<StringProcessor> _stringProcessorList = DfCollectionUtil.newArrayList();
    {
        // order has meaning if meta information does not exist
        // (but basically (always) meta information exists)
        _stringProcessorList.add(new DateStringProcessor());
        _stringProcessorList.add(new BooleanStringProcessor());
        _stringProcessorList.add(new NumberStringProcessor());
        _stringProcessorList.add(new UUIDStringProcessor());
        _stringProcessorList.add(new ArrayStringProcessor());
        _stringProcessorList.add(new XmlStringProcessor());
        _stringProcessorList.add(new BinaryFileStringProcessor());
        _stringProcessorList.add(new RealStringProcessor());
    }

    /** The cache map of null type. The key is table name. (ordered for display) */
    protected final Map<String, Map<String, Integer>> _nullTypeCacheMap = StringKeyMap.createAsFlexibleOrdered();

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
            int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (!isNullValue(value)) {
            return false;
        }

        Map<String, Integer> cacheMap = _nullTypeCacheMap.get(tableName);
        if (cacheMap == null) {
            cacheMap = StringKeyMap.createAsFlexibleOrdered();
            _nullTypeCacheMap.put(tableName, cacheMap);
        }
        final Integer cachedType = cacheMap.get(columnName);
        if (cachedType != null) { // cache hit
            ps.setNull(bindCount, cachedType); // basically no exception
            return true;
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            // use mapped type at first
            final String mappedJdbcType = _columnHandler.getColumnJdbcType(columnInfo);
            final Integer mappedJdbcDefValue = TypeMap.getJdbcDefValueByJdbcType(mappedJdbcType);
            try {
                ps.setNull(bindCount, mappedJdbcDefValue);
                cacheMap.put(columnName, mappedJdbcDefValue);
            } catch (SQLException e) {
                // retry by plain type
                final int plainJdbcDefValue = columnInfo.getJdbcDefValue();
                try {
                    ps.setNull(bindCount, plainJdbcDefValue);
                    cacheMap.put(columnName, plainJdbcDefValue);
                } catch (SQLException ignored) {
                    final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
                    br.addNotice("Failed to execute setNull(bindCount, jdbcDefValue).");
                    br.addItem("Column");
                    br.addElement(tableName + "." + columnName);
                    br.addElement(columnInfo.toString());
                    br.addItem("Mapped JDBC Type");
                    br.addElement(mappedJdbcType);
                    br.addItem("First JDBC Def-Value");
                    br.addElement(mappedJdbcDefValue);
                    br.addItem("Retry JDBC Def-Value");
                    br.addElement(plainJdbcDefValue);
                    br.addItem("Retry Message");
                    br.addElement(ignored.getMessage());
                    String msg = br.buildExceptionMessage();
                    throw new DfJDBCException(msg, e);
                }
            }
        } else { // basically no way
            Integer tryType = Types.VARCHAR; // as default
            try {
                ps.setNull(bindCount, tryType);
                cacheMap.put(columnName, tryType);
            } catch (SQLException e) {
                tryType = Types.NUMERIC;
                try {
                    ps.setNull(bindCount, tryType);
                    cacheMap.put(columnName, tryType);
                } catch (SQLException ignored) {
                    tryType = Types.TIMESTAMP;
                    try {
                        ps.setNull(bindCount, tryType);
                        cacheMap.put(columnName, tryType);
                    } catch (SQLException iignored) {
                        tryType = Types.OTHER;
                        try {
                            ps.setNull(bindCount, tryType); // last try
                            cacheMap.put(columnName, tryType);
                        } catch (SQLException iiignored) {
                            throw e;
                        }
                    }
                }
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
    protected boolean processNotNullNotString(String tableName, String columnName, Object obj, Connection conn,
            PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (!isNotNullNotString(obj)) {
            return false;
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            final Class<?> columnType = getBindType(tableName, columnInfo);
            if (columnType != null) {
                bindNotNullValueByColumnType(tableName, columnName, conn, ps, bindCount, obj, columnType);
                return true;
            }
        }
        bindNotNullValueByInstance(tableName, columnName, conn, ps, bindCount, obj);
        return true;
    }

    protected boolean isNotNullNotString(Object obj) {
        return obj != null && !(obj instanceof String);
    }

    // -----------------------------------------------------
    //                                        NotNull String
    //                                        --------------
    protected void processNotNullString(File dataFile, String tableName, String columnName, String value,
            Connection conn, PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap)
            throws SQLException {
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
            final boolean processed = processor.process(dataFile, tableName, columnName, value, conn, ps, bindCount,
                    columnInfoMap);
            if (!processed) {
                throwColumnValueProcessingFailureException(processor, tableName, columnName, value);
            }
            return;
        }
        for (StringProcessor tryProcessor : _stringProcessorList) {
            // processing and searching target processor
            if (tryProcessor.process(dataFile, tableName, columnName, value, conn, ps, bindCount, columnInfoMap)) {
                cacheMap.put(columnName, tryProcessor); // use cache next times
                break;
            }
        }
        // must be bound here
        // (_stringProcessorList has processor for real string)
    }

    protected void throwColumnValueProcessingFailureException(StringProcessor processor, String tableName,
            String columnName, String value) throws SQLException {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The column value could not be treated by the processor.");
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
        br.addItem("Processor");
        br.addElement(processor);
        final String msg = br.buildExceptionMessage();
        throw new DfJDBCException(msg);
    }

    public static interface StringProcessor {
        boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException;
    }

    protected class DateStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            return processDate(tableName, columnName, value, conn, ps, bindCount, columnInfoMap);
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class BooleanStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            return processBoolean(tableName, columnName, value, conn, ps, bindCount, columnInfoMap);
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class NumberStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            return processNumber(tableName, columnName, value, conn, ps, bindCount, columnInfoMap);
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class UUIDStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            return processUUID(tableName, columnName, value, conn, ps, bindCount, columnInfoMap);
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class ArrayStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            return processArray(tableName, columnName, value, ps, bindCount, columnInfoMap);
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class XmlStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            return processXml(tableName, columnName, value, ps, bindCount, columnInfoMap);
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class BinaryFileStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            return processBinary(dataFile, tableName, columnName, value, ps, bindCount, columnInfoMap);
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected class RealStringProcessor implements StringProcessor {

        public boolean process(File dataFile, String tableName, String columnName, String value, Connection conn,
                PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
            ps.setString(bindCount, value);
            return true;
        }

        @Override
        public String toString() {
            return buildProcessorToString(this);
        }
    }

    protected String buildProcessorToString(StringProcessor processor) {
        // e.g. com.example...FooWriter$RealStringProcessor -> RealStringProcessor
        return Srl.substringLastRear(DfTypeUtil.toClassTitle(processor), "$");
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    protected boolean processDate(String tableName, String columnName, String value, Connection conn,
            PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            final Class<?> columnType = getBindType(tableName, columnInfo);
            if (columnType != null) {
                if (!java.util.Date.class.isAssignableFrom(columnType)) {
                    return false;
                }
                bindNotNullValueByColumnType(tableName, columnName, conn, ps, bindCount, value, columnType);
                return true;
            }
        }
        // if meta data is not found (basically no way)
        try {
            Timestamp timestamp = DfTypeUtil.toTimestamp(value);
            ps.setTimestamp(bindCount, timestamp);
            return true;
        } catch (ParseTimestampException ignored) {
            // retry as time
            try {
                Time time = DfTypeUtil.toTime(value);
                ps.setTime(bindCount, time);
                return true;
            } catch (ParseTimeException ignored2) {
            }
            return false; // couldn't parse as timestamp and time
        }
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    protected boolean processBoolean(String tableName, String columnName, String value, Connection conn,
            PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            final Class<?> columnType = getBindType(tableName, columnInfo);
            if (columnType != null) {
                if (!Boolean.class.isAssignableFrom(columnType)) {
                    return false;
                }
                bindNotNullValueByColumnType(tableName, columnName, conn, ps, bindCount, value, columnType);
                return true;
            }
        }
        // if meta data is not found (basically no way) 
        try {
            final Boolean booleanValue = DfTypeUtil.toBoolean(value);
            ps.setBoolean(bindCount, booleanValue);
            return true;
        } catch (ParseBooleanException ignored) {
            return false; // couldn't parse as boolean
        }
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    protected boolean processNumber(String tableName, String columnName, String value, Connection conn,
            PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            final Class<?> columnType = getBindType(tableName, columnInfo);
            if (columnType != null) {
                if (!Number.class.isAssignableFrom(columnType)) {
                    return false;
                }
                bindNotNullValueByColumnType(tableName, columnName, conn, ps, bindCount, value, columnType);
                return true;
            }
        }
        // if meta data is not found (basically no way)
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
    protected boolean processUUID(String tableName, String columnName, String value, Connection conn,
            PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            final Class<?> columnType = getBindType(tableName, columnInfo);
            if (columnType != null) {
                if (!UUID.class.isAssignableFrom(columnType)) {
                    return false;
                }
                bindNotNullValueByColumnType(tableName, columnName, conn, ps, bindCount, value, columnType);
                return true;
            }
        }
        // unsupported when meta data is not found
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
    //                                                 ARRAY
    //                                                 -----
    protected boolean processArray(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            if (getBasicProperties().isDatabasePostgreSQL()) {
                //rsMeta#getColumnTypeName() returns value starts with "_" if
                //rsMeta#getColumnType() returns Types.ARRAY in PostgreSQL.
                //  e.g. UUID[] -> _uuid
                final int jdbcDefValue = columnInfo.getJdbcDefValue();
                final String dbTypeName = columnInfo.getDbTypeName();
                if (jdbcDefValue != Types.ARRAY || !dbTypeName.startsWith("_")) {
                    return false;
                }
                value = filterArrayValue(value);
                ps.setObject(bindCount, value, Types.OTHER);
                return true;
            }
        }
        // unsupported when meta data is not found
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
    //                                                   XML
    //                                                   ---
    protected boolean processXml(String tableName, String columnName, String value, PreparedStatement ps,
            int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            if (getBasicProperties().isDatabasePostgreSQL()) {
                final String dbTypeName = columnInfo.getDbTypeName();
                if (!dbTypeName.startsWith("xml")) {
                    return false;
                }
                value = filterXmlValue(value);
                ps.setObject(bindCount, value, Types.OTHER);
                return true;
            }
        }
        // unsupported when meta data is not found
        return false;
    }

    protected String filterXmlValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value;
    }

    // -----------------------------------------------------
    //                                                Binary
    //                                                ------
    protected boolean processBinary(File dataFile, String tableName, String columnName, String value,
            PreparedStatement ps, int bindCount, Map<String, DfColumnMeta> columnInfoMap) throws SQLException {
        if (value == null) {
            return false; // basically no way
        }
        final DfColumnMeta columnInfo = columnInfoMap.get(columnName);
        if (columnInfo != null) {
            final Class<?> columnType = getBindType(tableName, columnInfo);
            if (columnType != null) {
                if (!byte[].class.isAssignableFrom(columnType)) {
                    return false;
                }
                // the value should be a path to a binary file
                // from data file's current directory
                final String path;
                final String trimmedValue = value.trim();
                if (trimmedValue.startsWith("/")) { // means absolute path
                    path = trimmedValue;
                } else {
                    final String dataFilePath = Srl.replace(dataFile.getAbsolutePath(), "\\", "/");
                    final String baseDirPath = Srl.substringLastFront(dataFilePath, "/");
                    path = baseDirPath + "/" + trimmedValue;
                }
                final File binaryFile = new File(path);
                if (!binaryFile.exists()) {
                    throwLoadDataBinaryFileNotFoundException(tableName, columnName, path);
                }
                final List<Byte> byteList = new ArrayList<Byte>();
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(binaryFile));
                    for (int availableSize; (availableSize = bis.available()) > 0;) {
                        final byte[] bytes = new byte[availableSize];
                        bis.read(bytes);
                        for (byte b : bytes) {
                            byteList.add(b);
                        }
                    }
                    byte[] bytes = new byte[byteList.size()];
                    for (int i = 0; i < byteList.size(); i++) {
                        bytes[i] = byteList.get(i);
                    }
                    ps.setBytes(bindCount, bytes);
                } catch (IOException e) {
                    throwLoadDataBinaryFileReadFailureException(tableName, columnName, path, e);
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
                return true;
            }
        }
        // unsupported when meta data is not found
        return false;
    }

    protected String filterBinary(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value;
    }

    protected void throwLoadDataBinaryFileNotFoundException(String tableName, String columnName, String path) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The binary file specified at delimiter data was not found.");
        br.addItem("Advice");
        br.addElement("Make sure your path to a binary file is correct.");
        br.addItem("Table");
        br.addElement(tableName);
        br.addItem("Column");
        br.addElement(columnName);
        br.addItem("Path");
        br.addElement(path);
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg);
    }

    protected void throwLoadDataBinaryFileReadFailureException(String tableName, String columnName, String path,
            IOException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to read the binary file.");
        br.addItem("Table");
        br.addElement(tableName);
        br.addItem("Column");
        br.addElement(columnName);
        br.addItem("Path");
        br.addElement(path);
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg, e);
    }

    // ===================================================================================
    //                                                                          Bind Value
    //                                                                          ==========
    /**
     * Bind not null value by bind type of column. <br />
     * This contains type conversion of value.
     * @param tableName The name of table. (NotNull)
     * @param columnName The name of column. (NotNull)
     * @param conn The connection for the database. (NotNull)
     * @param ps The prepared statement. (NotNull)
     * @param bindCount The count of binding.
     * @param value The bound value. (NotNull)
     * @param bindType The bind type of the column. (NotNull)
     * @throws SQLException
     */
    protected void bindNotNullValueByColumnType(String tableName, String columnName, Connection conn,
            PreparedStatement ps, int bindCount, Object value, Class<?> bindType) throws SQLException {
        final ValueType valueType = TnValueTypes.getValueType(bindType);
        try {
            valueType.bindValue(conn, ps, bindCount, value);
        } catch (RuntimeException e) {
            throwColumnValueBindingFailureException(tableName, columnName, value, bindType, valueType, e);
        } catch (SQLException e) {
            throwColumnValueBindingSQLException(tableName, columnName, value, bindType, valueType, e);
        }
    }

    protected void throwColumnValueBindingFailureException(String tableName, String columnName, Object value,
            Class<?> bindType, ValueType valueType, RuntimeException e) throws SQLException {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to bind the value with ValueType for the column type.");
        br.addItem("Advice");
        br.addElement("Confirm the nested RuntimeException's message.");
        br.addElement("The bound value might not be to match the type.");
        br.addItem("Table Name");
        br.addElement(tableName);
        br.addItem("Column Name");
        br.addElement(columnName);
        br.addItem("Bind Type");
        br.addElement(bindType);
        br.addItem("Value Type");
        br.addElement(valueType);
        br.addItem("Bound Value");
        br.addElement(value);
        br.addItem("RuntimeException");
        br.addElement(e.getClass());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg, e);
    }

    protected void throwColumnValueBindingSQLException(String tableName, String columnName, Object value,
            Class<?> bindType, ValueType valueType, SQLException e) throws SQLException {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to bind the value with ValueType for the column type.");
        br.addItem("Advice");
        br.addElement("Confirm the nested SQLException's message.");
        br.addElement("The bound value might not be to match the type.");
        br.addItem("Table Name");
        br.addElement(tableName);
        br.addItem("Column Name");
        br.addElement(columnName);
        br.addItem("Bind Type");
        br.addElement(bindType);
        br.addItem("Value Type");
        br.addElement(valueType);
        br.addItem("Bound Value");
        br.addElement(value);
        br.addItem("Exception");
        br.addElement(e.getClass());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg, e);
    }

    /**
     * Bind not null value by instance.
     * @param ps The prepared statement. (NotNull)
     * @param bindCount The count of binding.
     * @param obj The bound value. (NotNull)
     * @throws SQLException
     */
    protected void bindNotNullValueByInstance(String tableName, String columnName, Connection conn,
            PreparedStatement ps, int bindCount, Object obj) throws SQLException {
        bindNotNullValueByColumnType(tableName, columnName, conn, ps, bindCount, obj, obj.getClass());
    }

    // ===================================================================================
    //                                                                    Column Bind Type
    //                                                                    ================
    /**
     * Get the bind type to find a value type.
     * @param tableName The name of table corresponding to column. (NotNull)
     * @param columnMeta The meta info of column. (NotNull)
     * @return The type of column. (NullAllowed: However Basically NotNull)
     */
    protected Class<?> getBindType(String tableName, DfColumnMeta columnMeta) {
        Map<String, Class<?>> cacheMap = _bindTypeCacheMap.get(tableName);
        if (cacheMap == null) {
            cacheMap = StringKeyMap.createAsFlexibleOrdered();
            _bindTypeCacheMap.put(tableName, cacheMap);
        }
        final String columnName = columnMeta.getColumnName();
        Class<?> bindType = cacheMap.get(columnName);
        if (bindType != null) { // cache hit
            return bindType;
        }

        // use mapped JDBC defined value if found (basically found)
        // because it has already been resolved about JDBC specification per DBMS
        final String jdbcType = _columnHandler.getColumnJdbcType(columnMeta);
        Integer jdbcDefValue = TypeMap.getJdbcDefValueByJdbcType(jdbcType);
        if (jdbcDefValue == null) { // basically no way
            jdbcDefValue = columnMeta.getJdbcDefValue(); // as plain
        }

        // ReplaceSchema uses an own original mapping way
        // (not uses Generate mapping)
        // it's simple mapping (for string processor)
        if (jdbcDefValue == Types.CHAR || jdbcDefValue == Types.VARCHAR || jdbcDefValue == Types.LONGVARCHAR
                || jdbcDefValue == Types.CLOB) {
            bindType = String.class;
        } else if (jdbcDefValue == Types.TINYINT || jdbcDefValue == Types.SMALLINT || jdbcDefValue == Types.INTEGER) {
            bindType = Integer.class;
        } else if (jdbcDefValue == Types.BIGINT) {
            bindType = Long.class;
        } else if (jdbcDefValue == Types.DECIMAL || jdbcDefValue == Types.NUMERIC) {
            bindType = BigDecimal.class;
        } else if (jdbcDefValue == Types.TIMESTAMP) {
            bindType = Timestamp.class;
        } else if (jdbcDefValue == Types.TIME) {
            bindType = Time.class;
        } else if (jdbcDefValue == Types.DATE) {
            // it depends on value type settings
            // that which is bound java.sql.Date or java.sql.Timestamp
            bindType = java.util.Date.class;
        } else if (jdbcDefValue == Types.BIT || jdbcDefValue == Types.BOOLEAN) {
            bindType = Boolean.class;
        } else if (jdbcDefValue == Types.BINARY || jdbcDefValue == Types.VARBINARY
                || jdbcDefValue == Types.LONGVARBINARY || jdbcDefValue == Types.BLOB) {
            bindType = byte[].class;
        } else if (jdbcDefValue == Types.OTHER && TypeMap.UUID.equalsIgnoreCase(jdbcType)) {
            // [UUID Headache]: The reason why UUID type has not been supported yet on JDBC.
            bindType = UUID.class;
        } else {
            bindType = Object.class;
        }
        cacheMap.put(columnName, bindType);
        return bindType;
    }

    // ===================================================================================
    //                                                                         Column Meta
    //                                                                         ===========
    protected Map<String, DfColumnMeta> getColumnMetaMap(String tableName) {
        if (_columnInfoCacheMap.containsKey(tableName)) {
            return _columnInfoCacheMap.get(tableName);
        }
        final Map<String, DfColumnMeta> columnMetaMap = StringKeyMap.createAsFlexible();
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            final List<DfColumnMeta> columnList = _columnHandler.getColumnList(metaData, _unifiedSchema, tableName);
            for (DfColumnMeta columnInfo : columnList) {
                columnMetaMap.put(columnInfo.getColumnName(), columnInfo);
            }
            _columnInfoCacheMap.put(tableName, columnMetaMap);
            return columnMetaMap;
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
    //                                                                        Log Handling
    //                                                                        ============
    protected void handleLoggingInsert(String tableDbName, List<String> columnNameList,
            Map<String, Object> columnValueMap, LoggingInsertType loggingInsertType, int recordCount) {
        boolean logging = false;
        if (LoggingInsertType.ALL.equals(loggingInsertType)) {
            logging = true;
        } else if (LoggingInsertType.PART.equals(loggingInsertType)) {
            if (recordCount <= 10) { // first 10 lines
                logging = true;
            } else if (recordCount == 11) {
                _log.info(tableDbName + ":{... more several records}");
            }
        }
        if (logging) {
            final List<Object> valueList = new ArrayList<Object>(columnValueMap.values());
            _log.info(buildLoggingInsert(tableDbName, columnNameList, valueList));
        }
    }

    protected String buildLoggingInsert(String tableName, List<String> columnNameList,
            final List<? extends Object> bindParameters) {
        final StringBuilder sb = new StringBuilder();
        for (Object parameter : bindParameters) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(parameter);
        }
        return tableName + ":{" + sb.toString() + "}";
    }

    protected void noticeLoadedRowSize(String tableDbName, int rowSize) {
        _log.info(" -> " + rowSize + " rows are loaded to " + tableDbName);
    }

    // ===================================================================================
    //                                                                    Directory Option
    //                                                                    ================
    protected LoggingInsertType getLoggingInsertType(String dataDirectory) {
        final Map<String, String> loadingControlMap = getLoadingControlMap(dataDirectory);
        final String prop = loadingControlMap.get("loggingInsertType");
        if (isSpecifiedValieProperty(prop)) {
            final String trimmed = prop.trim();
            if (trimmed.equalsIgnoreCase("all")) {
                return LoggingInsertType.ALL;
            } else if (trimmed.equalsIgnoreCase("none")) {
                return LoggingInsertType.NONE;
            } else if (trimmed.equalsIgnoreCase("part")) {
                return LoggingInsertType.PART;
            } else {
                String msg = "Unknown property value for loggingInsertType:";
                msg = msg + " value=" + trimmed + " dataDirectory=" + dataDirectory;
                throw new DfIllegalPropertySettingException(msg);
            }
        }
        return _loggingInsertSql ? LoggingInsertType.ALL : LoggingInsertType.NONE;
    }

    protected static enum LoggingInsertType {
        ALL, NONE, PART
    }

    protected boolean isMergedSuppressBatchUpdate(String dataDirectory) {
        final Map<String, String> loadingControlMap = getLoadingControlMap(dataDirectory);
        final String prop = loadingControlMap.get("isSuppressBatchUpdate");
        if (isSpecifiedValieProperty(prop)) {
            return prop.trim().equalsIgnoreCase("true");
        }
        return _suppressBatchUpdate;
    }

    protected Map<String, Map<String, String>> _loadingControlMapMap = DfCollectionUtil.newHashMap();

    protected Map<String, String> getLoadingControlMap(String dataDirectory) {
        final Map<String, String> cachedMap = _loadingControlMapMap.get(dataDirectory);
        if (cachedMap != null) {
            return cachedMap;
        }
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        String path = dataDirectory + "/loadingControlMap.dataprop";
        final Map<String, String> resultMap = reader.readMapAsStringValue(path);
        final StringKeyMap<String> flmap = StringKeyMap.createAsFlexible();
        if (resultMap != null && !resultMap.isEmpty()) {
            flmap.putAll(resultMap);
        }
        _loadingControlMapMap.put(dataDirectory, flmap);
        return _loadingControlMapMap.get(dataDirectory);
    }

    protected boolean isSpecifiedValieProperty(String prop) {
        return prop != null && prop.trim().length() > 0 && !prop.trim().equalsIgnoreCase("null");
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DBFluteSystem.getBasicLn();
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

    public boolean isSuppressBatchUpdate() {
        return _suppressBatchUpdate;
    }

    public void setSuppressBatchUpdate(boolean suppressBatchUpdate) {
        this._suppressBatchUpdate = suppressBatchUpdate;
    }

    public DfDataWritingInterceptor getDataWritingInterceptor() {
        return _dataWritingInterceptor;
    }

    public void setDataWritingInterceptor(DfDataWritingInterceptor dataWritingInterceptor) {
        this._dataWritingInterceptor = dataWritingInterceptor;
    }
}
