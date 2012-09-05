/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.doc.craftdiff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.dbflute.helper.token.file.FileMakingOption;
import org.seasar.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.dbflute.helper.token.file.FileToken;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffAssertHandlerImpl implements DfCraftDiffAssertHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _craftMetaDir;
    protected final String _craftTitle;
    protected final String _keyName;
    protected final List<String> _diffNameList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCraftDiffAssertHandlerImpl(String craftMetaDir, String craftTitle, String keyName,
            List<String> diffNameList) {
        _craftMetaDir = craftMetaDir;
        _craftTitle = craftTitle;
        _keyName = keyName;
        _diffNameList = diffNameList;
    }

    // ===================================================================================
    //                                                                       Handle Assert
    //                                                                       =============
    public void handle(File sqlFile, Statement st, String sql) throws SQLException {
        final List<Map<String, String>> diffDataList = selectDiffDataList(sqlFile, st, sql);
        final String nextDataFilePath = buildNextDataFile(sqlFile);
        final String previousDataFilePath = buildPreviousDataFile(sqlFile);
        rollingPreviousDataFile(nextDataFilePath, previousDataFilePath);
        dumpCraftMetaToDataFile(diffDataList, new File(nextDataFilePath));
    }

    protected String buildNextDataFile(File sqlFile) {
        return doCreateNextDataFile(sqlFile, "next");
    }

    protected String buildPreviousDataFile(File sqlFile) {
        return doCreateNextDataFile(sqlFile, "previous");
    }

    protected String doCreateNextDataFile(File sqlFile, String name) {
        final String pureFileName = sqlFile.getName();
        final String pureTitleName = Srl.substringLastFront(pureFileName, ".");
        return _craftMetaDir + "/" + pureTitleName + "-" + name + ".tsv";
    }

    // ===================================================================================
    //                                                                    Select Diff Data
    //                                                                    ================
    protected List<Map<String, String>> selectDiffDataList(File sqlFile, Statement st, String sql) throws SQLException {
        if (st == null) {
            String msg = "The argument 'st' should not be null: sqlFile=" + sqlFile;
            throw new IllegalStateException(msg);
        }
        final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        ResultSet rs = null;
        try {
            rs = st.executeQuery(sql);
            final ResultSetMetaData metaData = rs.getMetaData();
            final int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                final Map<String, String> recordMap = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columnCount; i++) {
                    recordMap.put(metaData.getColumnLabel(i), rs.getString(i));
                }
                resultList.add(recordMap);
            }
            return resultList;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    // ===================================================================================
    //                                                                    Rolling Previous
    //                                                                    ================
    protected void rollingPreviousDataFile(final String nextDataFilePath, final String previousDataFilePath) {
        final File previousDataFile = new File(previousDataFilePath);
        if (previousDataFile.exists()) {
            previousDataFile.delete();
        }
        final File nextDataFile = new File(nextDataFilePath);
        if (nextDataFile.exists()) {
            nextDataFile.renameTo(previousDataFile);
        }
    }

    // ===================================================================================
    //                                                                      Dump CraftMeta
    //                                                                      ==============
    protected void dumpCraftMetaToDataFile(final List<Map<String, String>> diffDataList, File nextDataFile) {
        final FileToken fileToken = new FileToken();
        final Iterator<Map<String, String>> iterator = diffDataList.iterator();
        try {
            fileToken.make(new FileOutputStream(nextDataFile), new FileMakingCallback() {
                public FileMakingRowResource getRowResource() {
                    return new FileMakingRowResource().acceptNameValueMapIterator(iterator);
                }
            }, new FileMakingOption().delimitateByTab().encodeAsUTF8());
        } catch (IOException e) {
            // TODO jflute
        }
    }
}