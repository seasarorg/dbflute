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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataHandler;
import org.seasar.extension.jdbc.util.ColumnDesc;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;

public class DfSeparatedDataHandlerImpl implements DfSeparatedDataHandler {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    public void writeSeveralData(String basePath, String typeName, String delimter, DataSource dataSource,
            Map<String, Set<String>> notFoundColumnMap) {
        final File baseDir = new File(basePath);
        final String[] dataDirectoryElements = baseDir.list();
        final FilenameFilter filter = createFilenameFilter(typeName);
        final DfSeparatedDataHandler handler = new DfSeparatedDataHandlerImpl();
        try {
            for (String elementName : dataDirectoryElements) {
                final File encodingNameDirectory = new File(basePath + "/" + elementName);
                final String[] fileNameList = encodingNameDirectory.list(filter);
                for (String fileName : fileNameList) {
                    final String fileNamePath = basePath + "/" + elementName + "/" + fileName;
                    handler.writeData(fileNamePath, elementName, delimter, dataSource, notFoundColumnMap);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected FilenameFilter createFilenameFilter(final String typeName) {
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("." + typeName);
            }
        };
        return filter;
    }

    /**
     * Write data from separated-file.
     * 
     * @param filename Name of the file. (NotNull and NotEmpty)
     * @param encoding Encoding of the file. (NotNull and NotEmpty)
     * @param delimiter Delimiter of the file. (NotNull and NotEmpty)
     * @param notFoundColumnMap Not found column map. (NotNUl)
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void writeData(String filename, String encoding, String delimiter, DataSource dataSource,
            Map<String, Set<String>> notFoundColumnMap) throws java.io.FileNotFoundException, java.io.IOException {
        _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
        _log.info("writeData(" + filename + ", " + encoding + ")");
        _log.info("= = = = = = =/");
        java.io.FileInputStream fis = null;
        java.io.InputStreamReader ir = null;
        java.io.BufferedReader br = null;

        String tableName = filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf("."));
        if (tableName.indexOf("-") >= 0) {
            tableName = tableName.substring(tableName.indexOf("-") + "-".length());
        }
        final Map columnMap = getColumnMap(tableName, dataSource);
        if (columnMap.isEmpty()) {
            _log.warn("The tableName[" + tableName + "] was not found: ");
        }
        String lineString = null;
        String preContinueString = "";
        List<String> valueList = new ArrayList<String>();
        List<String> columnNameList = null;

        try {
            fis = new java.io.FileInputStream(filename);
            ir = new java.io.InputStreamReader(fis, encoding);
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
                    firstLineInfo = getColumnNameList(delimiter, lineString);
                    columnNameList = firstLineInfo.getColumnNameList();
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
                    final ValueLineInfo valueLineInfo = arrangeValueList(realLineString, delimiter);
                    final List<String> ls = valueLineInfo.getValueList();
                    if (valueLineInfo.isContinueNextLine()) {
                        preContinueString = ls.remove(ls.size() - 1);
                        valueList.addAll(ls);
                        continue;
                    }
                    valueList.addAll(ls);
                }
                try {
                    if (isDifferentColumnValueCount(columnNameList, valueList, lineString)) {
                        String msg = "{" + tableName + "} Value count should not be less than column name count:";
                        msg = msg + " valueSize=" + valueList.size() + " columnNameSize=" + columnNameList.size();
                        msg = msg + " lineString=" + lineString + " valueList=" + valueList;
                        _log.warn(msg);
                        continue;
                    }

                    final String sql = buildSql(tableName, columnMap, columnNameList, valueList, notFoundColumnMap);
                    Statement statement = null;
                    try {
                        statement = dataSource.getConnection().createStatement();
                        _log.info(sql);
                        statement.execute(sql);
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
            String msg = "SQLException: filename=" + filename + " encoding=" + encoding;
            msg = msg + " columnSet=" + columnMap.keySet() + " columnNameList=" + columnNameList + " lineString="
                    + lineString;
            throw new RuntimeException(msg, e);
        } catch (RuntimeException e) {
            String msg = "RuntimeException: filename=" + filename + " encoding=" + encoding;
            msg = msg + " columnSet=" + columnMap.keySet() + " columnNameList=" + columnNameList + " lineString="
                    + lineString;
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

    protected ValueLineInfo arrangeValueList(final String lineString, String delimiter) {
        final List<String> valueList = new ArrayList<String>();
        final String[] values = lineString.split(delimiter);
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

    protected boolean isDifferentColumnValueCount(List<String> columnNameList, List<String> valueList, String lineString) {
        if (valueList.size() < columnNameList.size()) {
            return true;
        }
        return false;
    }

    protected String buildSql(String tableName, Map columnMap, List<String> columnNameList, List<String> valueList,
            Map<String, Set<String>> notFoundColumnMap) {
        final Map<String, Object> columnValueMap = getColumnValueMap(tableName, columnNameList, columnMap, valueList,
                notFoundColumnMap);
        final StringBuilder sb = new StringBuilder();
        final Set<String> columnNameSet = columnValueMap.keySet();
        for (String columnName : columnNameSet) {
            sb.append(", ").append(columnName);
        }
        sb.delete(0, ", ".length()).insert(0, "insert into " + tableName + "(").append(")");
        sb.append(getValuesString(columnMap, columnValueMap, columnNameSet));
        return sb.toString();
    }

    protected Map<String, Object> getColumnValueMap(String tableName, List<String> columnNameList, Map columnMap,
            List<String> valueList, Map<String, Set<String>> notFoundColumnMap) {
        final Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();
        int columnCount = -1;
        for (String columnName : columnNameList) {
            columnCount++;
            if (!columnMap.isEmpty() && !columnMap.containsKey(columnName)) {
                Set<String> notFoundColumnSet = notFoundColumnMap.get(tableName);
                if (notFoundColumnSet == null) {
                    notFoundColumnSet = new LinkedHashSet<String>();
                    notFoundColumnMap.put(tableName, notFoundColumnSet);
                }
                notFoundColumnSet.add(columnName);
                continue;
            }
            final String value;
            try {
                value = valueList.get(columnCount);
            } catch (java.lang.RuntimeException e) {
                throw new RuntimeException("valueList.get(columnCount) threw the exception: valueList=" + valueList
                        + " columnCount=" + columnCount, e);
            }
            columnValueMap.put(columnName, value);
        }
        return columnValueMap;
    }

    protected String getValuesString(final Map columnMap, final Map<String, Object> columnValueMap,
            final Set<String> columnNameSet) {
        final StringBuilder sbValues = new StringBuilder();
        for (String columnName : columnNameSet) {
            final Object value = columnValueMap.get(columnName);
            final ColumnDesc columnDesc = (ColumnDesc) columnMap.get(columnName);
            final int sqlType = columnDesc.getSqlType();
            if (!isNumeric(sqlType)) {
                sbValues.append(", ").append("'").append(value).append("'");
            } else {
                if (value == null || (value instanceof String && ((String) value).trim().length() == 0)) {
                    sbValues.append(", ").append("null");
                } else {
                    sbValues.append(", ").append(value);
                }
            }
        }
        sbValues.delete(0, ", ".length()).insert(0, " values(").append(");");
        return sbValues.toString();
    }

    protected boolean isNumeric(int sqlType) {
        if (sqlType == java.sql.Types.BIGINT) {
            return true;
        } else if (sqlType == java.sql.Types.BIT) {
            return true;
        } else if (sqlType == java.sql.Types.DECIMAL) {
            return true;
        } else if (sqlType == java.sql.Types.DOUBLE) {
            return true;
        } else if (sqlType == java.sql.Types.FLOAT) {
            return true;
        } else if (sqlType == java.sql.Types.INTEGER) {
            return true;
        } else if (sqlType == java.sql.Types.NUMERIC) {
            return true;
        }
        return false;
    }

}
