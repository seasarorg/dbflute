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
package org.seasar.dbflute.task.bs;

import java.io.File;
import java.util.List;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetter;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfOutsideSqlTestProperties;

/**
 * @author jflute
 */
public abstract class DfAbstractInvokeSqlDirectoryTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          DataSource
    //                                                                          ==========
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        final DfRunnerInformation runInfo = createRunnerInformation();
        final DfSqlFileFireMan fireMan = newSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getSqlFileList());
    }

    protected DfSqlFileFireMan newSqlFileFireMan() {
        return new DfSqlFileFireMan();
    }

    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setAutoCommit(isAutoCommit());
        runInfo.setErrorContinue(isErrorContinue());
        runInfo.setRollbackOnly(isRollbackOnly());
        customizeRunnerInformation(runInfo);
        return runInfo;
    }

    protected abstract void customizeRunnerInformation(DfRunnerInformation runInfo);

    // ===================================================================================
    //                                                                        For Override
    //                                                                        ============
    protected DfSqlFileRunnerExecute getSqlFileRunner(final DfRunnerInformation runInfo) {
        return new DfSqlFileRunnerExecute(runInfo, getDataSource());
    }

    protected List<File> getSqlFileList() {
        return new DfSqlFileGetter().getSqlFileList(getSqlDirectory());
    }

    protected String getSqlDirectory() {
        return getMyProperties().getInvokeSqlDirectorySqlDirectory();
    }

    protected boolean isAutoCommit() {
        return getMyProperties().isInvokeSqlDirectoryAutoCommit();
    }

    protected boolean isErrorContinue() {
        return getMyProperties().isInvokeSqlDirectoryErrorContinue();
    }

    protected boolean isRollbackOnly() {
        return getMyProperties().isInvokeSqlDirectoryRollbackOnly();
    }

    protected DfOutsideSqlTestProperties getMyProperties() {
        return DfBuildProperties.getInstance().getInvokeSqlDirectoryProperties();
    }
}
