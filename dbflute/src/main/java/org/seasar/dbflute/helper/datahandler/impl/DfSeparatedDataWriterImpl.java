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
package org.seasar.dbflute.helper.datahandler.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataWriter;
import org.seasar.dbflute.helper.datahandler.impl.internal.DfInternalSqlBuilder;
import org.seasar.dbflute.helper.datahandler.impl.internal.DfInternalSqlBuildingResult;
import org.seasar.dbflute.util.DfTokenUtil;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;

public class DfSeparatedDataWriterImpl implements DfSeparatedDataWriter {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataWriterImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _loggingInsertSql;

    protected DataSource _dataSource;

    protected String _filename;

    protected String _encoding;

    protected String _delimiter;

    protected boolean _errorContinue;

    protected Map<String, String> _defaultValueMap;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    /**
     * Write data from separated-file.
     * 
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
        final Map columnMap = getColumnMap(tableName, _dataSource);
        if (columnMap.isEmpty()) {
            String msg = "The tableName[" + tableName + "] was not found: filename=" + _filename;
            throw new IllegalStateException(msg);
        }
        String lineString = null;
        String preContinueString = "";
        final List<String> valueList = new ArrayList<String>();
        Map<String, String> additionalDefaultColumnNameToLowerKeyMap = null;
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
                    columnNameList = firstLineInfo.getColumnNameList();
                    columnNameList.addAll(additionalDefaultColumnNameToLowerKeyMap.values());
                    continue;
                }
                {
                    final String realLineString;
                    if (preContinueString.equals("")) {
                        realLineString = lineString;
                    } else {
                        final String lineSeparator = System.getProperty("line.separator");
                        realLineString = preContinueString + lineSeparator + lineString;
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
                    sqlBuilder.setColumnMap(columnMap);
                    sqlBuilder.setColumnNameList(columnNameList);
                    sqlBuilder.setValueList(valueList);
                    sqlBuilder.setNotFoundColumnMap(notFoundColumnMap);
                    sqlBuilder.setAddtionalDefaultColumnNameToLowerMap(additionalDefaultColumnNameToLowerKeyMap);
                    sqlBuilder.setDefaultValueMap(_defaultValueMap);
                    final DfInternalSqlBuildingResult sqlBuildingResult = sqlBuilder.buildSql();
                    PreparedStatement statement = null;
                    try {
                        final String sql = sqlBuildingResult.getSql();
                        final List<Object> bindParameters = sqlBuildingResult.getBindParameters();
                        if (_loggingInsertSql) {
                            _log.info(getSql4Log(tableName, columnNameList, bindParameters));
                        }
                        statement = _dataSource.getConnection().prepareStatement(sql);
                        int bindCount = 1;
                        for (Object object : bindParameters) {
                            statement.setObject(bindCount, object);
                            bindCount++;
                        }
                        statement.execute();
                    } catch (SQLException e) {
                        if (_errorContinue) {
                            _log.warn("Statement.execute(sql) threw the exception!", e);
                            continue;
                        } else {
                            throw e;
                        }
                    } finally {
                        if (statement != null) {
                            try {
                                statement.close();
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
            msg = msg + " columnSet=" + columnMap.keySet() + " columnNameList=" + columnNameList + " lineString="
                    + lineString + " defaultSysdateList=" + _defaultValueMap;
            throw new RuntimeException(msg, e);
        } catch (RuntimeException e) {
            String msg = "RuntimeException: filename=" + _filename + " encoding=" + _encoding;
            msg = msg + " columnSet=" + columnMap.keySet() + " columnNameList=" + columnNameList + " lineString="
                    + lineString + " defaultSysdateList=" + _defaultValueMap;
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

    private String getSql4Log(String tableName, List<String> columnNameList, final List<Object> bindParameters) {
        String columnNameString = columnNameList.toString();
        columnNameString = columnNameString.substring(1, columnNameString.length() - 1);
        String bindParameterString = bindParameters.toString();
        bindParameterString = bindParameterString.substring(1, bindParameterString.length() - 1);
        return "insert into " + tableName + " (" + columnNameString + ") values(" + bindParameterString + ")";
    }

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

    protected ValueLineInfo arrangeValueList(final String lineString, String delimiter) {
        final List<String> valueList = new ArrayList<String>();

        // Don't use split!
        //        final String[] values = lineString.split(delimiter);
        final String[] values = DfTokenUtil.tokenToArgs(lineString, delimiter);

        for (String value : values) {
            valueList.add(value);
        }
        return arrangeValueList(valueList, delimiter);
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
            if (i == valueList.size() - 1) {// The last loop
                if (preString.equals("")) {
                    if (isFrontQOnly(value)) {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(value);
                        break;
                    } else if (isRearQOnly(value)) {
                        resultList.add(value);
                        break;
                    } else if (isNotBothQ(value)) {
                        resultList.add(value);
                        break;
                    } else {
                        resultList.add(removeDoubleQuotation(value));
                        break;
                    }
                } else {
                    if (isFrontQOnly(value)) {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(connectPreString(preString, delimiter, value));
                        break;
                    } else if (isRearQOnly(value)) {
                        resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                        break;
                    } else if (isNotBothQ(value)) {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(connectPreString(preString, delimiter, value));
                        break;
                    } else {
                        resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                        break;
                    }
                }
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
                if (isFrontQOnly(value)) {
                    preString = connectPreString(preString, delimiter, value);
                    continue;
                } else if (isRearQOnly(value)) {
                    resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                } else if (isNotBothQ(value)) {
                    preString = connectPreString(preString, delimiter, value);
                    continue;
                } else {
                    resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
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
        return !value.startsWith("\"") && !value.endsWith("\"");
    }

    protected boolean isRearQOnly(final String value) {
        return !value.startsWith("\"") && value.endsWith("\"");
    }

    protected boolean isFrontQOnly(final String value) {
        return value.startsWith("\"") && !value.endsWith("\"");
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

    protected Map getColumnMap(String tableName, DataSource dataSource) {
        final Connection connection;
        final DatabaseMetaData dbMetaData;
        try {
            connection = dataSource.getConnection();
            dbMetaData = connection.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        final Map columnMap = DatabaseMetaDataUtil.getColumnMap(dbMetaData, tableName);
        return columnMap;
    }

    protected void addValueToList(List<String> ls, String value) {
        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
            ls.add(value.substring(1, value.length() - 1));
        } else {
            ls.add(value != null ? value : "");
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
    //                                                                            Accessor
    //                                                                            ========
    public boolean isLoggingInsertSql() {
        return _loggingInsertSql;
    }

    public void setLoggingInsertSql(boolean loggingInsertSql) {
        this._loggingInsertSql = loggingInsertSql;
    }

    public Map<String, String> getDefaultValueMap() {
        return _defaultValueMap;
    }

    public void setDefaultValueMap(Map<String, String> defaultValueMap) {
        this._defaultValueMap = defaultValueMap;
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

}
