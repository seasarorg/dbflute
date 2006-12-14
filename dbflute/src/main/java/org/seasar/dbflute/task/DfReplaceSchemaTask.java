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
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataHandler;
import org.seasar.dbflute.helper.datahandler.DfXlsDataHandler;
import org.seasar.dbflute.helper.datahandler.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.helper.datahandler.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

public class DfReplaceSchemaTask extends DfAbstractTask {

    /** Log instance. */
    // private static final Log _log = LogFactory.getLog(DfInvokeReplaceSchemaTask.class);
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
        // TODO: In the future, clear schema by using information schema.

        final DfRunnerInformation runInfo = createRunnerInformation();
        fireSqlFile(runInfo);

        writeDbFromXls();
        writeDbFromSeparatedFile("tsv", "\t");
        writeDbFromSeparatedFile("csv", ",");
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
        final DfSeparatedDataHandler handler = new DfSeparatedDataHandlerImpl();
        handler.writeSeveralData(getDataDirectoryPath("tsv"), typeName, delimter, getDataSource());
    }

    protected String getDataDirectoryPath(final String typeName) {
        // TODO: env.txtÇ›ÇΩÇ¢Ç…maindataÇ∆Ç©êÿÇËë÷Ç¶ÇÁÇÍÇÈÇÊÇ§Ç…Ç∑ÇÈÅB
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
