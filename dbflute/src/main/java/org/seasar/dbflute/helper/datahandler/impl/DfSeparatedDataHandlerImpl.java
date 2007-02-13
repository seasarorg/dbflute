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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataHandler;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataResultInfo;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataSeveralHandlingInfo;
import org.seasar.dbflute.helper.mapstring.DfMapListStringImpl;
import org.seasar.dbflute.util.DfMapStringFileUtil;
import org.seasar.framework.util.CaseInsensitiveMap;

public class DfSeparatedDataHandlerImpl implements DfSeparatedDataHandler {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    protected DataSource _dataSource;

    public DataSource getDataSource() {
        return _dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this._dataSource = dataSource;
    }

    public DfSeparatedDataResultInfo writeSeveralData(DfSeparatedDataSeveralHandlingInfo info) {
        final DfSeparatedDataResultInfo resultInfo = new DfSeparatedDataResultInfo();
        final Map<String, Set<String>> notFoundColumnMap = new LinkedHashMap<String, Set<String>>();
        resultInfo.setNotFoundColumnMap(notFoundColumnMap);
        final File baseDir = new File(info.getBasePath());
        final String[] dataDirectoryElements = baseDir.list();
        if (dataDirectoryElements == null) {
            return resultInfo;
        }
        final FilenameFilter filter = createFilenameFilter(info.getTypeName());

        try {
            for (String elementName : dataDirectoryElements) {
                final File encodingNameDirectory = new File(info.getBasePath() + "/" + elementName);
                final String[] fileNameList = encodingNameDirectory.list(filter);
                final Map<String, String> defaultValueMap = getDefaultValueMap(info, elementName);
                for (String fileName : fileNameList) {
                    final String fileNamePath = info.getBasePath() + "/" + elementName + "/" + fileName;
                    final DfSeparatedDataWriterImpl writerImpl = new DfSeparatedDataWriterImpl();
                    writerImpl.setDataSource(_dataSource);
                    writerImpl.setFilename(fileNamePath);
                    writerImpl.setEncoding(elementName);
                    writerImpl.setDelimiter(info.getDelimter());
                    writerImpl.setErrorContinue(true);
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

    private Map<String, String> getDefaultValueMap(DfSeparatedDataSeveralHandlingInfo info, String encoding) {
        final String path = info.getBasePath() + "/" + encoding + "/default-value.txt";
        return DfMapStringFileUtil.getSimpleMapAsStringValue(path, encoding);
    }

    protected FilenameFilter createFilenameFilter(final String typeName) {
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("." + typeName);
            }
        };
        return filter;
    }
}
