/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfTableDataRegistrationFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfDelimiterDataWriter;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfDelimiterDataWriterImpl extends DfAbsractDataWriter implements DfDelimiterDataWriter {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfDelimiterDataWriterImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _filename;
    protected String _encoding;
    protected String _delimiter;
    protected Map<String, Map<String, String>> _convertValueMap;
    protected Map<String, String> _defaultValueMap;

    /** The cache map of meta info. The key is table name. */
    protected final Map<String, Map<String, DfColumnMetaInfo>> _metaInfoCacheMap = StringKeyMap.createAsFlexible();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDelimiterDataWriterImpl(DataSource dataSource) {
        super(dataSource);
    }

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====
    /**
     * Write data from delimiter-file.
     * @param notFoundColumnMap Not found column map. (NotNUl)
     * @throws java.io.IOException
     */
    public void writeData(Map<String, Set<String>> notFoundColumnMap) throws IOException {
        _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
        _log.info("writeData(" + _filename + ", " + _encoding + ")");
        _log.info("= = = = = = =/");
        FileInputStream fis = null;
        InputStreamReader ir = null;
        BufferedReader br = null;

        String tableName = _filename.substring(_filename.lastIndexOf("/") + 1, _filename.lastIndexOf("."));
        if (tableName.indexOf("-") >= 0) {
            tableName = tableName.substring(tableName.indexOf("-") + "-".length());
        }
        final Map<String, DfColumnMetaInfo> columnMetaInfoMap = getColumnMetaInfo(tableName);
        if (columnMetaInfoMap.isEmpty()) {
            String msg = "The tableName[" + tableName + "] was not found: filename=" + _filename;
            throw new IllegalStateException(msg);
        }

        // process before handling table
        beforeHandlingTable(tableName);

        String lineString = null;
        String preContinueString = "";
        final List<String> columnNameList = new ArrayList<String>();
        final List<String> additionalColumnList = new ArrayList<String>();
        final List<String> valueList = new ArrayList<String>();

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            fis = new FileInputStream(_filename);
            ir = new InputStreamReader(fis, _encoding);
            br = new BufferedReader(ir);

            FirstLineInfo firstLineInfo = null;
            int loopIndex = -1;
            int addedBatchSize = 0;
            while (true) {
                ++loopIndex;

                lineString = br.readLine();
                if (lineString == null) {
                    break;
                }
                if (loopIndex == 0) {
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - - 
                    // Initialize the information of columns by first line.
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
                    firstLineInfo = getFirstLineInfo(_delimiter, lineString);
                    columnNameList.addAll(firstLineInfo.getColumnNameList());
                    final StringSet columnSet = StringSet.createAsFlexible();
                    columnSet.addAll(columnNameList);
                    for (String defaultColumn : _defaultValueMap.keySet()) {
                        if (columnSet.contains(defaultColumn)) {
                            continue;
                        }
                        additionalColumnList.add(defaultColumn);
                    }
                    columnNameList.addAll(additionalColumnList);
                    continue;
                }
                {
                    final String realLineString;
                    if (preContinueString.equals("")) {
                        realLineString = lineString;
                    } else {
                        realLineString = preContinueString + "\n" + lineString;
                    }
                    final ValueLineInfo valueLineInfo = arrangeValueList(realLineString, _delimiter);
                    final List<String> ls = valueLineInfo.getValueList();
                    if (valueLineInfo.isContinueNextLine()) {
                        preContinueString = ls.remove(ls.size() - 1);
                        valueList.addAll(ls);
                        continue;
                    }
                    valueList.addAll(ls);
                }
                try {
                    if (isDifferentColumnValueCount(firstLineInfo, valueList, lineString)) {
                        String msg = "The count of values wasn't correct:";
                        msg = msg + " columnNameSize=" + firstLineInfo.getColumnNameList().size();
                        msg = msg + " valueSize=" + valueList.size();
                        msg = msg + " lineString=" + lineString + " valueList=" + valueList;
                        _log.warn(msg);
                        continue;
                    }

                    final DfDelimiterDataWriteSqlBuilder sqlBuilder = new DfDelimiterDataWriteSqlBuilder();
                    sqlBuilder.setTableName(tableName);
                    sqlBuilder.setColumnMap(columnMetaInfoMap);
                    sqlBuilder.setColumnNameList(columnNameList);
                    sqlBuilder.setValueList(valueList);
                    sqlBuilder.setNotFoundColumnMap(notFoundColumnMap);
                    sqlBuilder.setConvertValueMap(_convertValueMap);
                    sqlBuilder.setDefaultValueMap(_defaultValueMap);
                    if (conn == null) {
                        conn = _dataSource.getConnection();
                    }
                    if (ps == null) {
                        ps = conn.prepareStatement(sqlBuilder.buildSql());
                    }
                    final Map<String, Object> columnValueMap = sqlBuilder.setupParameter();
                    if (_loggingInsertSql) {
                        _log.info(buildSql4Log(tableName, columnNameList, columnValueMap.values()));
                    }
                    int bindCount = 1;
                    final Set<Entry<String, Object>> entrySet = columnValueMap.entrySet();
                    for (Entry<String, Object> entry : entrySet) {
                        final String columnName = entry.getKey();
                        final Object obj = entry.getValue();

                        // - - - - - - - - - - - - - - - - - - -
                        // Process Null (against Null Headache)
                        // - - - - - - - - - - - - - - - - - - -
                        if (processNull(tableName, columnName, obj, ps, bindCount, columnMetaInfoMap)) {
                            bindCount++;
                            continue;
                        }

                        // - - - - - - - - - - - - - - -
                        // Process NotNull and NotString
                        // - - - - - - - - - - - - - - -
                        // If the value is not null and the value has the own type except string,
                        // It registers the value to statement by the type.
                        if (processNotNullNotString(tableName, columnName, obj, conn, ps, bindCount, columnMetaInfoMap)) {
                            bindCount++;
                            continue;
                        }

                        // - - - - - - - - - - - - - - - - - - -
                        // Process NotNull and StringExpression
                        // - - - - - - - - - - - - - - - - - - -
                        final String value = (String) obj;
                        processNotNullString(tableName, columnName, value, conn, ps, bindCount, columnMetaInfoMap);
                        bindCount++;
                    }
                    if (_suppressBatchUpdate) {
                        ps.execute();
                    } else {
                        ps.addBatch();
                        ++addedBatchSize;
                        if (addedBatchSize == 100000) {
                            // this is supported in only delimiter data writer
                            // because delimiter data can treat large data
                            ps.executeBatch(); // to avoid OutOfMemory
                            ps.clearBatch(); // for next batch
                            addedBatchSize = 0;
                        }
                    }
                } finally {
                    valueList.clear();
                    preContinueString = "";
                }
            }
            if (ps != null && addedBatchSize > 0) {
                ps.executeBatch();
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (SQLException e) {
            final SQLException nextEx = e.getNextException();
            if (nextEx != null && !e.equals(nextEx)) { // focus on next exception
                _log.warn("*Failed to register: " + e.getMessage());
                String msg = buildExceptionMessage(_filename, tableName, lineString, nextEx);
                throw new DfTableDataRegistrationFailureException(msg, nextEx); // switch!
            }
            String msg = buildExceptionMessage(_filename, tableName, lineString, e);
            throw new DfTableDataRegistrationFailureException(msg, e);
        } catch (RuntimeException e) {
            String msg = buildExceptionMessage(_filename, tableName, lineString, e);
            throw new DfTableDataRegistrationFailureException(msg, e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (ir != null) {
                    ir.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (java.io.IOException ignored) {
                _log.warn("File-close threw the exception: ", ignored);
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ignored) {
                    _log.info("Statement.close() threw the exception!", ignored);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("Connection.close() threw the exception!", ignored);
                }
            }
            // process after (finally) handling table
            finallyHandlingTable(tableName);
        }
    }

    protected String buildExceptionMessage(String filename, String tableName, String lineString, Exception e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to register the table data.");
        br.addItem("File");
        br.addElement(filename);
        br.addItem("Table");
        br.addElement(tableName);
        br.addItem("Line String");
        br.addElement(lineString);
        br.addItem("Message");
        br.addElement(e.getMessage());
        final Map<String, Class<?>> bindTypeCacheMap = _bindTypeCacheMap.get(tableName);
        if (bindTypeCacheMap != null) {
            br.addItem("Bind Type");
            final Set<Entry<String, Class<?>>> entrySet = bindTypeCacheMap.entrySet();
            for (Entry<String, Class<?>> entry : entrySet) {
                br.addElement(entry.getKey() + " = " + entry.getValue());
            }
        }
        final Map<String, StringProcessor> stringProcessorCacheMap = _stringProcessorCacheMap.get(tableName);
        if (bindTypeCacheMap != null) {
            br.addItem("String Processor");
            final Set<Entry<String, StringProcessor>> entrySet = stringProcessorCacheMap.entrySet();
            for (Entry<String, StringProcessor> entry : entrySet) {
                br.addElement(entry.getKey() + " = " + entry.getValue());
            }
        }
        return br.buildExceptionMessage();
    }

    protected void beforeHandlingTable(String tableName) {
        if (_dataWritingInterceptor != null) {
            _dataWritingInterceptor.processBeforeHandlingTable(tableName);
        }
    }

    protected void finallyHandlingTable(String tableName) {
        if (_dataWritingInterceptor != null) {
            _dataWritingInterceptor.processFinallyHandlingTable(tableName);
        }
    }

    // ===================================================================================
    //                                                                    Process per Type
    //                                                                    ================
    @Override
    protected boolean isNullValue(Object value) {
        if (value == null) {
            return true;
        }

        // Because separated value!
        if (!(value instanceof String)) {
            return false;
        }
        String str = (String) value;
        return str.length() == 0 || str.equals("\"\"");
    }

    // ===================================================================================
    //                                                                     First Line Info
    //                                                                     ===============
    protected FirstLineInfo getFirstLineInfo(String delimiter, final String lineString) {
        List<String> columnNameList;
        columnNameList = new ArrayList<String>();
        final String[] values = lineString.split(delimiter);
        int count = 0;
        boolean quotated = false;
        for (String value : values) {
            if (count == 0) {
                if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
                    quotated = true;
                }
            }
            addValueToList(columnNameList, value);
            count++;
        }
        final FirstLineInfo firstLineInformation = new FirstLineInfo();
        firstLineInformation.setColumnNameList(columnNameList);
        firstLineInformation.setQuotated(quotated);
        return firstLineInformation;
    }

    protected void addValueToList(List<String> ls, String value) {
        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
            ls.add(value.substring(1, value.length() - 1));
        } else {
            ls.add(value != null ? value : "");
        }
    }

    protected String buildSql4Log(String tableName, List<String> columnNameList, final Collection<Object> bindParameters) {
        String bindParameterString = bindParameters.toString();
        bindParameterString = bindParameterString.substring(1, bindParameterString.length() - 1);
        return tableName + ":{" + bindParameterString + "}";
    }

    // ===================================================================================
    //                                                                          Value List
    //                                                                          ==========
    protected ValueLineInfo arrangeValueList(final String lineString, String delimiter) {
        // Don't use split!
        //final String[] values = lineString.split(delimiter);
        final List<String> valueList = Srl.splitList(lineString, delimiter);
        return arrangeValueList(valueList, delimiter);
    }

    protected ValueLineInfo arrangeValueList(List<String> valueList, String delimiter) {
        final ValueLineInfo valueLineInfo = new ValueLineInfo();
        final ArrayList<String> resultList = new ArrayList<String>();
        String preString = "";
        for (int i = 0; i < valueList.size(); i++) {
            final String value = valueList.get(i);
            if (value == null) { // basically no way (valueList does not contain null)
                continue;
            }
            if (i == valueList.size() - 1) { // The last loop
                if (preString.equals("")) {
                    if (isFrontQOnly(value)) {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(value);
                    } else if (isRearQOnly(value)) {
                        resultList.add(value);
                    } else if (isNotBothQ(value)) {
                        resultList.add(value);
                    } else {
                        resultList.add(removeDoubleQuotation(value));
                    }
                } else {
                    if (endsQuote(value, false)) {
                        resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                    } else {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(connectPreString(preString, delimiter, value));
                    }
                }
                break; // because it's the last loop
            }

            if (preString.equals("")) {
                if (isFrontQOnly(value)) {
                    preString = value;
                    continue;
                } else if (isRearQOnly(value)) {
                    preString = value;
                    continue;
                } else if (isNotBothQ(value)) {
                    resultList.add(value);
                } else {
                    resultList.add(removeDoubleQuotation(value));
                }
            } else {
                if (endsQuote(value, false)) {
                    resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                } else {
                    preString = connectPreString(preString, delimiter, value);
                    continue;
                }
            }
            preString = "";
        }
        valueLineInfo.setValueList(resultList);
        return valueLineInfo;
    }

    protected String connectPreString(String preString, String delimiter, String value) {
        if (preString.equals("")) {
            return value;
        } else {
            return preString + delimiter + value;
        }
    }

    protected boolean isNotBothQ(final String value) {
        return !isQQ(value) && !value.startsWith("\"") && !endsQuote(value, false);
    }

    protected boolean isRearQOnly(final String value) {
        return !isQQ(value) && !value.startsWith("\"") && (endsQuote(value, false));
    }

    protected boolean isFrontQOnly(final String value) {
        return !isQQ(value) && value.startsWith("\"") && !endsQuote(value, true);
    }

    protected boolean isQQ(final String value) {
        return value.equals("\"\"");
    }

    protected boolean endsQuote(String value, boolean startsQuote) {
        value = startsQuote ? value.substring(1) : value;
        final int length = value.length();
        int count = 0;
        for (int i = 0; i < length; i++) {
            char ch = value.charAt(length - (i + 1));
            if (ch == '\"') {
                ++count;
            } else {
                break;
            }
        }
        return count > 0 && isOddNumber(count);
    }

    protected boolean isOddNumber(int number) {
        return (number % 2) != 0;
    }

    protected String removeDoubleQuotation(String value) {
        if (!value.startsWith("\"") && !value.endsWith("\"")) {
            return value;
        }
        if (value.startsWith("\"")) {
            value = value.substring(1);
        }
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        value = Srl.replace(value, "\"\"", "\"");
        return value;
    }

    protected String removeRightDoubleQuotation(String value) {
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    public static class FirstLineInfo {
        protected List<String> columnNameList;
        protected boolean quotated;

        public List<String> getColumnNameToLowerList() {
            final ArrayList<String> ls = new ArrayList<String>();
            for (String columnName : columnNameList) {
                ls.add(columnName.toLowerCase());
            }
            return ls;
        }

        public List<String> getColumnNameList() {
            return columnNameList;
        }

        public void setColumnNameList(List<String> columnNameList) {
            this.columnNameList = columnNameList;
        }

        public boolean isQuotated() {
            return quotated;
        }

        public void setQuotated(boolean quotated) {
            this.quotated = quotated;
        }
    }

    public static class ValueLineInfo {
        protected List<String> valueList;
        protected boolean continueNextLine;

        public List<String> getValueList() {
            return valueList;
        }

        public void setValueList(List<String> valueList) {
            this.valueList = valueList;
        }

        public boolean isContinueNextLine() {
            return continueNextLine;
        }

        public void setContinueNextLine(boolean continueNextLine) {
            this.continueNextLine = continueNextLine;
        }
    }

    protected boolean isDifferentColumnValueCount(FirstLineInfo firstLineInfo, List<String> valueList, String lineString) {
        if (valueList.size() < firstLineInfo.getColumnNameList().size()) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        this._delimiter = delimiter;
    }

    public String getEncoding() {
        return _encoding;
    }

    public void setEncoding(String encoding) {
        this._encoding = encoding;
    }

    public String getFilename() {
        return _filename;
    }

    public void setFilename(String filename) {
        this._filename = filename;
    }

    public Map<String, Map<String, String>> getConvertValueMap() {
        return _convertValueMap;
    }

    public void setConvertValueMap(Map<String, Map<String, String>> convertValueMap) {
        this._convertValueMap = convertValueMap;
    }

    public Map<String, String> getDefaultValueMap() {
        return _defaultValueMap;
    }

    public void setDefaultValueMap(Map<String, String> defaultValueMap) {
        this._defaultValueMap = defaultValueMap;
    }
}
