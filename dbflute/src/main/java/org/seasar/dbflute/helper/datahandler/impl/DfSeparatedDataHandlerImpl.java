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

    public void writeSeveralData(String basePath, String typeName, String delimter, DataSource dataSource) {
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
                    handler.writeData(fileNamePath, elementName, delimter, dataSource);
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
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void writeData(String filename, String encoding, String delimiter, DataSource dataSource)
            throws java.io.FileNotFoundException, java.io.IOException {
        _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
        _log.info("writeData(" + filename + ", " + encoding + ")");
        _log.info("= = = = = = =/");
        java.io.FileInputStream fis = null;
        java.io.InputStreamReader ir = null;
        java.io.BufferedReader br = null;

        final String tableName = filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf("."));
        final Map columnMap = getColumnMap(filename, dataSource);
        if (columnMap.isEmpty()) {
            _log.warn("The tableName[" + tableName + "] was not found: ");
        }
        List<String> columnNameList = null;
        try {
            fis = new java.io.FileInputStream(filename);
            ir = new java.io.InputStreamReader(fis, encoding);
            br = new java.io.BufferedReader(ir);

            int count = -1;
            while (true) {
                ++count;

                final String lineString = br.readLine();
                if (lineString == null) {
                    break;
                }
                if (count == 0) {
                    columnNameList = new ArrayList<String>();
                    final String[] values = lineString.split(delimiter);
                    for (String value : values) {
                        addValueToList(columnNameList, value);
                    }
                    continue;
                }
                final String[] values = lineString.split(delimiter);
                final List<String> valueList = new ArrayList<String>();
                for (String value : values) {
                    addValueToList(valueList, value);
                }
                if (isDifferentColumnValueCount(columnNameList, valueList, lineString)) {
                    String msg = "Value count should not be less than column name count:";
                    msg = msg + " valueList=" + valueList.size() + " columnNameList=" + columnNameList.size();
                    msg = msg + " lineString=" + lineString;
                    _log.warn(msg);
                    continue;
                }

                final String sql = buildSql(tableName, columnMap, columnNameList, valueList);
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
            }
        } catch (java.io.FileNotFoundException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        } catch (SQLException e) {
            String msg = "SQLException: filename=" + filename + " encoding=" + encoding;
            msg = msg + " columnSet=" + columnMap.keySet() + " columnNameList=" + columnNameList;
            throw new RuntimeException(msg, e);
        } catch (RuntimeException e) {
            String msg = "RuntimeException: filename=" + filename + " encoding=" + encoding;
            msg = msg + " columnSet=" + columnMap.keySet() + " columnNameList=" + columnNameList;
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

    protected Map getColumnMap(String filename, DataSource dataSource) {
        final String tableName = filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf("."));
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

    protected String buildSql(String tableName, Map columnMap, List<String> columnNameList, List<String> valueList) {
        final Map<String, Object> columnValueMap = getColumnValueMap(tableName, columnNameList, columnMap, valueList);
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
            List<String> valueList) {
        final Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();
        int columnCount = -1;
        for (String columnName : columnNameList) {
            columnCount++;
            if (!columnMap.isEmpty() && !columnMap.containsKey(columnName)) {
                _log.info("The column[" + columnName + "] was not found in the table[" + tableName + "]");
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
                sbValues.append(", ").append(value);
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
