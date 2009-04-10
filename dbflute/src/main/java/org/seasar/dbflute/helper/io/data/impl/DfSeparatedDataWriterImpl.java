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
package org.seasar.dbflute.helper.io.data.impl;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.io.data.DfSeparatedDataWriter;
import org.seasar.dbflute.helper.io.data.impl.internal.DfInternalSqlBuilder;
import org.seasar.dbflute.helper.io.data.impl.internal.DfInternalSqlBuildingResult;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class DfSeparatedDataWriterImpl extends DfAbsractDataWriter implements DfSeparatedDataWriter {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataWriterImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    protected String _schemaName;

    protected boolean _loggingInsertSql;

    protected String _filename;

    protected String _encoding;

    protected String _delimiter;

    protected boolean _errorContinue;

    protected Map<String, Map<String, String>> _convertValueMap;

    protected Map<String, String> _defaultValueMap;

    /** The cache map of meta info. The key is table name. */
    protected Map<String, DfFlexibleMap<String, DfColumnMetaInfo>> _metaInfoCacheMap = new HashMap<String, DfFlexibleMap<String, DfColumnMetaInfo>>();

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====
    /**
     * Write data from separated-file.
     * @param notFoundColumnMap Not found column map. (NotNUl)
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void writeData(Map<String, Set<String>> notFoundColumnMap) throws java.io.FileNotFoundException,
            java.io.IOException {
        _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
        _log.info("writeData(" + _filename + ", " + _encoding + ")");
        _log.info("= = = = = = =/");
        java.io.FileInputStream fis = null;
        java.io.InputStreamReader ir = null;
        java.io.BufferedReader br = null;

        String tableName = _filename.substring(_filename.lastIndexOf("/") + 1, _filename.lastIndexOf("."));
        if (tableName.indexOf("-") >= 0) {
            tableName = tableName.substring(tableName.indexOf("-") + "-".length());
        }
        final DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap = getColumnMetaInfo(_dataSource, tableName);
        if (columnMetaInfoMap.isEmpty()) {
            String msg = "The tableName[" + tableName + "] was not found: filename=" + _filename;
            throw new IllegalStateException(msg);
        }
        String lineString = null;
        String preContinueString = "";
        final List<String> valueList = new ArrayList<String>();
        Map<String, String> additionalDefaultColumnNameToLowerKeyMap = null;
        Map<String, String> targetConvertColumnNameKeyToLowerMap = null;
        List<String> columnNameList = null;

        try {
            fis = new java.io.FileInputStream(_filename);
            ir = new java.io.InputStreamReader(fis, _encoding);
            br = new java.io.BufferedReader(ir);

            FirstLineInfo firstLineInfo = null;
            int count = -1;
            while (true) {
                ++count;

                lineString = br.readLine();
                if (lineString == null) {
                    break;
                }
                if (count == 0) {
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - - 
                    // Initialize the information of columns by first line.
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
                    firstLineInfo = getColumnNameList(_delimiter, lineString);
                    additionalDefaultColumnNameToLowerKeyMap = getAdditionalDefaultColumnNameToLowerKeyMap(firstLineInfo);
                    targetConvertColumnNameKeyToLowerMap = getTargetConvertColumnNameKeyToLowerMap(firstLineInfo);
                    columnNameList = firstLineInfo.getColumnNameList();
                    columnNameList.addAll(additionalDefaultColumnNameToLowerKeyMap.values());
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
                    if (isDifferentColumnValueCount(columnNameList, additionalDefaultColumnNameToLowerKeyMap,
                            valueList, lineString)) {
                        String msg = "The count of values wasn't correct:";
                        msg = msg + " valueSize=" + valueList.size() + " columnNameSize=" + columnNameList.size();
                        msg = msg + " lineString=" + lineString + " valueList=" + valueList;
                        _log.warn(msg);
                        continue;
                    }

                    final DfInternalSqlBuilder sqlBuilder = new DfInternalSqlBuilder();
                    sqlBuilder.setTableName(tableName);
                    sqlBuilder.setColumnMap(columnMetaInfoMap);
                    sqlBuilder.setColumnNameList(columnNameList);
                    sqlBuilder.setValueList(valueList);
                    sqlBuilder.setNotFoundColumnMap(notFoundColumnMap);
                    sqlBuilder.setTargetConvertColumnNameKeyToLowerMap(targetConvertColumnNameKeyToLowerMap);
                    sqlBuilder.setAdditionalDefaultColumnNameToLowerMap(additionalDefaultColumnNameToLowerKeyMap);
                    sqlBuilder.setConvertValueMap(_convertValueMap);
                    sqlBuilder.setDefaultValueMap(_defaultValueMap);
                    final DfInternalSqlBuildingResult sqlBuildingResult = sqlBuilder.buildSql();
                    PreparedStatement ps = null;
                    try {
                        final String sql = sqlBuildingResult.getSql();
                        final Map<String, Object> columnValueMap = sqlBuildingResult.getColumnValueMap();
                        if (_loggingInsertSql) {
                            _log.info(buildSql4Log(tableName, columnNameList, columnValueMap.values()));
                        }
                        ps = _dataSource.getConnection().prepareStatement(sql);
                        int bindCount = 1;
                        for (String columnName : columnValueMap.keySet()) {
                            final Object obj = columnValueMap.get(columnName);

                            // If the value is not null and the value has the own type except string,
                            // It registers the value to statement by the type.
                            if (processNotNullNotString(columnName, obj, ps, bindCount)) {
                                bindCount++;
                                continue;
                            }

                            // - - - - - - - - - - - - - - 
                            // Against Null Headache
                            // - - - - - - - - - - - - - -
                            if (processNull(columnName, obj, ps, bindCount, columnMetaInfoMap)) {
                                bindCount++;
                                continue;
                            }

                            // * * * * * * * * * * * * * * * *
                            //       Here String Only
                            // * * * * * * * * * * * * * * * *
                            String value = (String) obj;

                            // - - - - - - - - - - - - - - - - - - -
                            // Remove double quotation if it exists.
                            // - - - - - - - - - - - - - - - - - - -
                            if (value != null && value.length() > 1 && value.startsWith("\"") && value.endsWith("\"")) {
                                value = removeDoubleQuotation(value);
                            }

                            // - - - - - - - - - - - - - - 
                            // Against Time Headache
                            // - - - - - - - - - - - - - -
                            if (processTime(columnName, value, ps, bindCount, columnMetaInfoMap)) {
                                bindCount++;
                                continue;
                            }

                            // - - - - - - - - - - - - - - 
                            // Against Timestamp Headache
                            // - - - - - - - - - - - - - -
                            if (processTimestamp(columnName, value, ps, bindCount, columnMetaInfoMap)) {
                                bindCount++;
                                continue;
                            }

                            // - - - - - - - - - - - - - - 
                            // Against Boolean Headache
                            // - - - - - - - - - - - - - -
                            if (processBoolean(columnName, value, ps, bindCount, columnMetaInfoMap)) {
                                bindCount++;
                                continue;
                            }

                            // - - - - - - - - - - - - - - 
                            // Against Number Headache
                            // - - - - - - - - - - - - - -
                            if (processNumber(columnName, value, ps, bindCount, columnMetaInfoMap)) {
                                bindCount++;
                                continue;
                            }

                            ps.setString(bindCount, value);
                            bindCount++;
                        }
                        ps.execute();
                    } catch (SQLException e) {
                        if (_errorContinue) {
                            final String simpleName = e.getClass().getSimpleName();
                            final StringBuilder sb = new StringBuilder();
                            sb.append("The statement threw ").append(simpleName).append("! The detail is as follows:");
                            sb.append(ln()).append("  Message    = ");
                            sb.append(e.getMessage());
                            sb.append(ln()).append("  Parameters = ");
                            sb.append(sqlBuildingResult.getColumnValueMap());
                            _log.warn(sb);
                            continue;
                        } else {
                            throw e;
                        }
                    } finally {
                        if (ps != null) {
                            try {
                                ps.close();
                            } catch (SQLException ignored) {
                                _log.info("statement.close() threw the exception!", ignored);
                            }
                        }
                    }
                } finally {
                    valueList.clear();
                    preContinueString = "";
                }
            }
        } catch (java.io.FileNotFoundException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        } catch (SQLException e) {
            String msg = "SQLException: filename=" + _filename + " encoding=" + _encoding;
            msg = msg + " columnNameList=" + columnNameList + " lineString=" + lineString + " defaultValueMap="
                    + _defaultValueMap;
            throw new RuntimeException(msg, e);
        } catch (RuntimeException e) {
            String msg = "RuntimeException: filename=" + _filename + " encoding=" + _encoding;
            msg = msg + " columnNameList=" + columnNameList + " lineString=" + lineString + " defaultValueMap="
                    + _defaultValueMap;
            throw new RuntimeException(msg, e);
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
    //                                                                    Column Meta Info
    //                                                                    ================
    protected DfFlexibleMap<String, DfColumnMetaInfo> getColumnMetaInfo(DataSource dataSource, String tableName) {
        if (_metaInfoCacheMap.containsKey(tableName)) {
            return _metaInfoCacheMap.get(tableName);
        }
        final DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap = new DfFlexibleMap<String, DfColumnMetaInfo>();
        try {
            final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            final List<DfColumnMetaInfo> columnMetaDataList = _columnHandler.getColumns(metaData, _schemaName,
                    tableName);
            for (DfColumnMetaInfo columnMetaInfo : columnMetaDataList) {
                columnMetaInfoMap.put(columnMetaInfo.getColumnName(), columnMetaInfo);
            }
            _metaInfoCacheMap.put(tableName, columnMetaInfoMap);
            return columnMetaInfoMap;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    // ===================================================================================
    //                                                                    Column Name List
    //                                                                    ================
    protected FirstLineInfo getColumnNameList(String delimiter, final String lineString) {
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
    //                                                                       Convert Value
    //                                                                       =============
    protected Map<String, String> getTargetConvertColumnNameKeyToLowerMap(FirstLineInfo firstLineInfo) {
        final Map<String, String> resultMap = new LinkedHashMap<String, String>();
        final Set<String> keySet = _convertValueMap.keySet();
        final List<String> ls = firstLineInfo.getColumnNameToLowerList();
        for (String columnName : keySet) {
            final String toLowerColumnName = columnName.toLowerCase();
            if (!ls.contains(toLowerColumnName)) {
                resultMap.put(toLowerColumnName, columnName);
            }
        }
        return resultMap;
    }

    // ===================================================================================
    //                                                                       Default Value
    //                                                                       =============
    /**
     * @param firstLineInfo The information of first line. (NotNull)
     * @return The map of additional default column names these are to-lower. {to-lower column name : column name} (NotNull)
     */
    protected Map<String, String> getAdditionalDefaultColumnNameToLowerKeyMap(FirstLineInfo firstLineInfo) {
        final Map<String, String> resultMap = new LinkedHashMap<String, String>();
        final Set<String> keySet = _defaultValueMap.keySet();
        final List<String> ls = firstLineInfo.getColumnNameToLowerList();
        for (String columnName : keySet) {
            final String toLowerColumnName = columnName.toLowerCase();
            if (!ls.contains(toLowerColumnName)) {
                resultMap.put(toLowerColumnName, columnName);
            }
        }
        return resultMap;
    }

    // ===================================================================================
    //                                                                          Value List
    //                                                                          ==========
    protected ValueLineInfo arrangeValueList(final String lineString, String delimiter) {
        final List<String> valueList = new ArrayList<String>();

        // Don't use split!
        //final String[] values = lineString.split(delimiter);
        final String[] values = tokenToArgs(lineString, delimiter);

        for (String value : values) {
            valueList.add(value);
        }
        return arrangeValueList(valueList, delimiter);
    }

    protected static String[] tokenToArgs(String value, String delimiter) {
        List<String> list = tokenToList(value, delimiter);
        return (String[]) list.toArray(new String[list.size()]);
    }

    protected static List<String> tokenToList(String value, String delimiter) {
        List<String> list = new ArrayList<String>();
        int i = 0;
        int j = value.indexOf(delimiter);
        for (int h = 0; j >= 0; h++) {
            list.add(value.substring(i, j));
            i = j + 1;
            j = value.indexOf(delimiter, i);
        }
        list.add(value.substring(i));
        return list;
    }
    
    protected ValueLineInfo arrangeValueList(List<String> valueList, String delimiter) {
        final ValueLineInfo valueLineInfo = new ValueLineInfo();
        final ArrayList<String> resultList = new ArrayList<String>();
        String preString = "";
        for (int i = 0; i < valueList.size(); i++) {
            final String value = valueList.get(i);
            if (value == null) {
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
        return count > 0 && (count % 2) == 1;
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
        value = DfStringUtil.replace(value, "\"\"", "\"");
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

    protected boolean isDifferentColumnValueCount(List<String> columnNameList,
            Map<String, String> appendDefaultColumnNameToLowerMap, List<String> valueList, String lineString) {
        if (valueList.size() < columnNameList.size() - appendDefaultColumnNameToLowerMap.size()) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getSchemaName() {
        return _schemaName;
    }

    public void setSchemaName(String schemaName) {
        this._schemaName = schemaName;
    }

    public boolean isLoggingInsertSql() {
        return _loggingInsertSql;
    }

    public void setLoggingInsertSql(boolean loggingInsertSql) {
        this._loggingInsertSql = loggingInsertSql;
    }

    public boolean isErrorContinue() {
        return _errorContinue;
    }

    public void setErrorContinue(boolean errorContinue) {
        this._errorContinue = errorContinue;
    }

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

    public DataSource getDataSource() {
        return _dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this._dataSource = dataSource;
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
