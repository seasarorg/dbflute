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
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataHandler;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataResultInfo;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataSeveralHandlingInfo;
import org.seasar.dbflute.helper.io.fileread.DfMapStringFileReader;

public class DfSeparatedDataHandlerImpl implements DfSeparatedDataHandler {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _loggingInsertSql;

    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public DfSeparatedDataResultInfo writeSeveralData(DfSeparatedDataSeveralHandlingInfo info) {
        final DfSeparatedDataResultInfo resultInfo = new DfSeparatedDataResultInfo();
        final Map<String, Set<String>> notFoundColumnMap = new LinkedHashMap<String, Set<String>>();
        resultInfo.setNotFoundColumnMap(notFoundColumnMap);
        final File baseDir = new File(info.getBasePath());
        final String[] dataDirectoryElements = baseDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith(".");
            }
        });
        if (dataDirectoryElements == null) {
            return resultInfo;
        }
        final FilenameFilter filter = createFilenameFilter(info.getTypeName());

        try {
            for (String elementName : dataDirectoryElements) {
                if (isUnsupportedEncodingDirectory(elementName)) {
                    _log.warn("The encoding(directory name) is unsupported: encoding=" + elementName);
                    continue;
                }

                final File encodingNameDirectory = new File(info.getBasePath() + "/" + elementName);
                final String[] fileNameList = encodingNameDirectory.list(filter);

                final Comparator<String> fileNameAscComparator = new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                };
                final SortedSet<String> sortedFileNameSet = new TreeSet<String>(fileNameAscComparator);
                for (String fileName : fileNameList) {
                    sortedFileNameSet.add(fileName);
                }

                final Map<String, Map<String, String>> convertValueMap = getConvertValueMap(info, elementName);
                final Map<String, String> defaultValueMap = getDefaultValueMap(info, elementName);
                for (String fileName : sortedFileNameSet) {
                    final String fileNamePath = info.getBasePath() + "/" + elementName + "/" + fileName;
                    final DfSeparatedDataWriterImpl writerImpl = new DfSeparatedDataWriterImpl();
                    writerImpl.setLoggingInsertSql(isLoggingInsertSql());
                    writerImpl.setDataSource(_dataSource);
                    writerImpl.setFilename(fileNamePath);
                    writerImpl.setEncoding(elementName);
                    writerImpl.setDelimiter(info.getDelimter());
                    writerImpl.setErrorContinue(true);
                    writerImpl.setConvertValueMap(convertValueMap);
                    writerImpl.setDefaultValueMap(defaultValueMap);
                    writerImpl.writeData(notFoundColumnMap);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultInfo;
    }

    protected boolean isUnsupportedEncodingDirectory(String encoding) {
        try {
            new String(new byte[0], 0, 0, encoding);
            return false;
        } catch (UnsupportedEncodingException e) {
            return true;
        }
    }

    private Map<String, Map<String, String>> getConvertValueMap(DfSeparatedDataSeveralHandlingInfo info, String encoding) {
        final String path = info.getBasePath() + "/" + encoding + "/convert-value.txt";
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        return reader.readMapAsMapValue(path, encoding);
    }

    private Map<String, String> getDefaultValueMap(DfSeparatedDataSeveralHandlingInfo info, String encoding) {
        final String path = info.getBasePath() + "/" + encoding + "/default-value.txt";
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        return reader.readMapAsStringValue(path, encoding);
    }

    protected FilenameFilter createFilenameFilter(final String typeName) {
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("." + typeName);
            }
        };
        return filter;
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

    public DataSource getDataSource() {
        return _dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this._dataSource = dataSource;
    }
}
