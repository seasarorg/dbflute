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
package org.seasar.dbflute.task;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataHandler;
import org.seasar.dbflute.helper.datahandler.DfXlsDataHandler;
import org.seasar.dbflute.helper.datahandler.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.helper.datahandler.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerMySQL;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

public class DfReplaceSchemaTask extends DfAbstractTask {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaTask.class);

    // =========================================================================================
    //                                                                                DataSource
    //                                                                                ==========
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // =========================================================================================
    //                                                                                   Execute
    //                                                                                   =======
    /**
     * Load the sql file and then execute it.
     *
     * @throws BuildException
     */
    @Override
    protected void doExecute() {
        initializeSchema();

        final DfRunnerInformation runInfo = createRunnerInformation();
        fireSqlFile(runInfo);

        writeDbFromSeparatedFile("tsv", "\t");
        writeDbFromSeparatedFile("csv", ",");
        writeDbFromXls();
    }

    protected void initializeSchema() {
        if (DfBuildProperties.getInstance().getBasicProperties().isDatabaseMySQL()) {
            final DfSchemaInitializerMySQL initializer = createSchemaInitializerMySQL();
            initializer.initializeSchema();
        }

        // TODO: Make initializeSchema for Other DB.
    }

    protected DfSchemaInitializerMySQL createSchemaInitializerMySQL() {
        final DfSchemaInitializerMySQL initializer = new DfSchemaInitializerMySQL();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setAutoCommit(getMyProperties().isReplaceSchemaAutoCommit());
        runInfo.setErrorContinue(getMyProperties().isReplaceSchemaErrorContinue());
        runInfo.setRollbackOnly(getMyProperties().isReplaceSchemaRollbackOnly());
        return runInfo;
    }

    protected void fireSqlFile(DfRunnerInformation runInfo) {
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getSqlFileList());
    }

    protected DfSqlFileRunner getSqlFileRunner(final DfRunnerInformation runInfo) {
        return new DfSqlFileRunnerExecute(runInfo, getDataSource());
    }

    protected List<File> getSqlFileList() {
        final String sqlFile = getMyProperties().getReplaceSchemaSqlFile();
        final List<File> fileList = new ArrayList<File>();
        fileList.add(new File(sqlFile));
        return fileList;
    }

    protected void writeDbFromXls() {
        final DfXlsDataHandler xlsDataHandler = new DfXlsDataHandlerImpl();
        xlsDataHandler.writeSeveralData(getDataDirectoryPath("xls"), getDataSource());
    }

    protected void writeDbFromSeparatedFile(String typeName, String delimter) {
        Map<String, Set<String>> notFoundColumnMap = new LinkedHashMap<String, Set<String>>();
        final DfSeparatedDataHandler handler = new DfSeparatedDataHandlerImpl();
        handler.writeSeveralData(getDataDirectoryPath("tsv"), typeName, delimter, getDataSource(), notFoundColumnMap);
        showNotFoundColumn(typeName, notFoundColumnMap);
    }

    protected void showNotFoundColumn(String typeName, Map<String, Set<String>> notFoundColumnMap) {
        if (notFoundColumnMap.isEmpty()) {
            return;
        }
        _log.warn("* * * * * * * * * * * * *");
        _log.warn("Not Found Columns in " + typeName);
        _log.warn("* * * * * * * * * * * * *");
        final Set<String> notFoundColumnSet = notFoundColumnMap.keySet();
        for (String tableName : notFoundColumnSet) {
            _log.warn("[" + tableName + "]");
            final Set<String> columnNameList = notFoundColumnMap.get(tableName);
            for (String columnName : columnNameList) {
                _log.warn("    " + columnName);
            }
            _log.warn(" ");
        }
    }

    protected String getDataDirectoryPath(final String typeName) {
        return getSqlFileDirectoryName() + "/testdata/" + typeName;
    }

    protected String getSqlFileDirectoryName() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(0, sqlFileName.lastIndexOf("/"));
    }

    protected DfReplaceSchemaProperties getMyProperties() {
        return DfBuildProperties.getInstance().getInvokeReplaceSchemaProperties();
    }
}
