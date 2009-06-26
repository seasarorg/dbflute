package org.apache.torque.task;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.apache.velocity.anakia.Escape;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.dbflute.helper.token.file.FileMakingOption;
import org.seasar.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.dbflute.helper.token.file.FileToken;
import org.seasar.dbflute.helper.token.file.impl.FileTokenImpl;
import org.seasar.dbflute.logic.dumpdata.DfDumpDataXlsHandler;
import org.seasar.dbflute.logic.dumpdata.DfDumpDataXlsHandler.DumpResult;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.task.bs.DfAbstractDbMetaTexenTask;

/**
 * The task for documentation. {SchemaHTML and DataXlsTemplate}
 * @author Modified by jflute
 */
public class TorqueDocumentationTask extends DfAbstractDbMetaTexenTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(TorqueDocumentationTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _outputFormat;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        processSchemaHtml();

        if (isDataXlsTemplateRecordLimitValid()) {
            processDataXlsTemplate();
        }

        // It doesn't refresh because it's heavy.
        // After all the generate task will do it at once after doc task.
        // refreshResources();
    }

    protected void processSchemaHtml() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("*    Schema HTML    *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        super.doExecute();
        _log.info("");
    }

    protected void processDataXlsTemplate() {
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Data Xls Template *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        final Map<String, List<String>> tableColumnMap = new LinkedHashMap<String, List<String>>();
        final DfAdditionalTableProperties tableProperties = getProperties().getAdditionalTableProperties();
        final Map<String, Object> additionalTableMap = tableProperties.getAdditionalTableMap();
        final boolean containsCommonColumn = isDataXlsTemplateContainsCommonColumn();
        final DfFlexibleMap<String, Object> commonColumnMap = getCommonColumnMap();
        try {
            final Database database = _schemaData.getDatabase();
            final List<Table> tableList = database.getTableList();
            for (Table table : tableList) {
                if (additionalTableMap.containsKey(table.getName())) {
                    continue;
                }
                final Column[] columns = table.getColumns();
                final List<String> columnNameList = new ArrayList<String>();
                for (Column column : columns) {
                    if (!containsCommonColumn && commonColumnMap.containsKey(column.getName())) {
                        continue;
                    }
                    columnNameList.add(column.getName());
                }
                tableColumnMap.put(table.getName(), columnNameList);
            }
        } catch (EngineException e) {
            throw new IllegalStateException(e);
        }
        _log.info("...Creating data xls template: tables=" + tableColumnMap.size());
        dumpDataXlsTemplate(tableColumnMap);
        _log.info("");
    }

    protected void dumpDataXlsTemplate(Map<String, List<String>> tableColumnMap) {
        final DfDumpDataXlsHandler xlsHandler = new DfDumpDataXlsHandler(getDataSource());
        final Integer limit = getDataXlsTemplateRecordLimit();
        final File xlsFile = getDataXlsTemplateFile();
        final DumpResult dumpResult = xlsHandler.dumpToXls(tableColumnMap, limit, xlsFile);
        dumpDataCsvTemplate(dumpResult);
    }

    protected void dumpDataCsvTemplate(DumpResult dumpResult) {
        final Map<String, List<String>> overTableColumnMap = dumpResult.getOverTableColumnMap();
        if (overTableColumnMap.isEmpty()) {
            return;
        }
        _log.info("...Creating data csv template(over 65000): tables=" + overTableColumnMap.size());
        final Map<String, List<Map<String, String>>> overDumpDataMap = dumpResult.getOverDumpDataMap();
        final FileMakingOption option = new FileMakingOption().delimitateByComma().encodeAsUTF8().separateLf();
        final File csvDir = getDataCsvTemplateDir();
        if (!csvDir.exists()) {
            csvDir.mkdir();
        }
        final FileToken fileToken = new FileTokenImpl();
        final Set<String> tableNameSet = overTableColumnMap.keySet();
        for (final String tableName : tableNameSet) {
            final String csvFilePath = csvDir.getPath() + "/" + tableName + ".csv";
            final List<String> columnNameList = overTableColumnMap.get(tableName);
            final List<Map<String, String>> recordList = overDumpDataMap.get(tableName);
            _log.info("    " + tableName + "(" + recordList.size() + ")");
            try {
                option.headerInfo(columnNameList);
                final Iterator<Map<String, String>> recordListIterator = recordList.iterator();
                fileToken.make(csvFilePath, new FileMakingCallback() {
                    public FileMakingRowResource getRowResource() {
                        if (!recordListIterator.hasNext()) {
                            return null;
                        }
                        final Map<String, String> recordMap = recordListIterator.next();
                        final FileMakingRowResource resource = new FileMakingRowResource();
                        final LinkedHashMap<String, String> nameValueMap = new LinkedHashMap<String, String>();
                        nameValueMap.putAll(recordMap);
                        resource.setNameValueMap(nameValueMap);
                        return resource;
                    }
                }, option);
            } catch (FileNotFoundException e) {
                String msg = "Failed to dump CSV file: table=" + tableName + " csv=" + csvFilePath;
                throw new IllegalStateException(msg, e);
            } catch (IOException e) {
                String msg = "Failed to dump CSV file: table=" + tableName + " csv=" + csvFilePath;
                throw new IllegalStateException(msg, e);
            }
        }
    }

    // ===================================================================================
    //                                                                         Data Source
    //                                                                         ===========
    protected boolean isUseDataSource() {
        return isDataXlsTemplateRecordLimitValid();
    }

    // ===================================================================================
    //                                                                  Related Properties
    //                                                                  ==================
    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    protected boolean isDataXlsTemplateRecordLimitValid() {
        return getDocumentProperties().isDataXlsTemplateRecordLimitValid();
    }

    protected Integer getDataXlsTemplateRecordLimit() {
        return getDocumentProperties().getDataXlsTemplateRecordLimit();
    }

    protected boolean isDataXlsTemplateContainsCommonColumn() {
        return getDocumentProperties().isDataXlsTemplateContainsCommonColumn();
    }

    protected File getDataXlsTemplateFile() {
        return getDocumentProperties().getDataXlsTemplateFile();
    }

    protected File getDataCsvTemplateDir() {
        return getDocumentProperties().getDataCsvTemplateDir();
    }

    protected DfFlexibleMap<String, Object> getCommonColumnMap() {
        final DfCommonColumnProperties commonColumnProperties = getProperties().getCommonColumnProperties();
        final Map<String, Object> commonColumnMap = commonColumnProperties.getCommonColumnMap();
        final DfFlexibleMap<String, Object> flexibleMap = new DfFlexibleMap<String, Object>();
        final Set<String> keySet = commonColumnMap.keySet();
        for (String key : keySet) {
            flexibleMap.put(key, "dummy");
        }
        return flexibleMap;
    }

    // ===================================================================================
    //                                                                       Task Override
    //                                                                       =============
    public Context initControlContext() throws Exception {
        super.initControlContext();
        _context.put("outputFormat", _outputFormat);
        _context.put("escape", new Escape());
        return _context;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getOutputFormat() {
        return _outputFormat;
    }

    public void setOutputFormat(String v) {
        _outputFormat = v;
    }
}
