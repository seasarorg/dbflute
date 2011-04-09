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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;

/**
 * @author jflute
 */
public abstract class DfAbstractSqlExecutionTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfAbstractSqlExecutionTask.class);

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
        final DfSqlFileFireMan fireMan = createSqlFileFireMan();
        final List<File> sqlFileList = getTargetSqlFileList();
        fireMan.fire(getSqlFileRunner(runInfo), sqlFileList);
        showTargetSqlFileInformation(sqlFileList);
    }

    // ===================================================================================
    //                                                                   Executing Element
    //                                                                   =================
    protected DfSqlFileFireMan createSqlFileFireMan() {
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

    protected abstract List<File> getTargetSqlFileList();

    protected abstract DfSqlFileRunnerExecute getSqlFileRunner(DfRunnerInformation runInfo);

    protected abstract String getSqlDirectory();

    protected abstract boolean isAutoCommit();

    protected abstract boolean isErrorContinue();

    protected abstract boolean isRollbackOnly();

    protected abstract void customizeRunnerInformation(DfRunnerInformation runInfo);

    // ===================================================================================
    //                                                                SQL File Information
    //                                                                ====================
    protected void showTargetSqlFileInformation(List<File> sqlFileList) {
        _log.info(" ");
        _log.info("/- - - - - - - - - - - - - - - - - - - - - - - -");
        _log.info("Target SQL files: " + sqlFileList.size());
        _log.info(" ");
        for (File sqlFile : sqlFileList) {
            _log.info("  " + sqlFile.getName());
        }
        _log.info("- - - - - - - - - -/");
        _log.info(" ");
    }
}
