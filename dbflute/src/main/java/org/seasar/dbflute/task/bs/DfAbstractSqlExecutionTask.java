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
        // FireMan's fire result is ignored here because runner's option breakCauseThrow=true
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
        runInfo.setDriver(getDriver());
        runInfo.setUrl(getUrl());
        runInfo.setUser(getUser());
        runInfo.setPassword(getPassword());
        runInfo.setBreakCauseThrow(isBreakCauseThrow());
        runInfo.setErrorContinue(isErrorContinue());
        runInfo.setAutoCommit(isAutoCommit());
        runInfo.setRollbackOnly(isRollbackOnly());
        runInfo.setIgnoreTxError(isIgnoreTxError());
        customizeRunnerInformation(runInfo);
        return runInfo;
    }

    protected abstract List<File> getTargetSqlFileList();

    protected abstract DfSqlFileRunnerExecute getSqlFileRunner(DfRunnerInformation runInfo);

    protected abstract boolean isBreakCauseThrow();

    protected abstract boolean isErrorContinue();

    protected abstract boolean isAutoCommit();

    protected abstract boolean isRollbackOnly();

    protected abstract boolean isIgnoreTxError();

    protected abstract void customizeRunnerInformation(DfRunnerInformation runInfo);

    // ===================================================================================
    //                                                                SQL File Information
    //                                                                ====================
    protected void showTargetSqlFileInformation(List<File> sqlFileList) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ln()).append("/- - - - - - - - - - - - - - - - - - - - - - - -");
        sb.append(ln()).append("Target SQL files: ").append(sqlFileList.size());
        sb.append(ln());
        for (File sqlFile : sqlFileList) {
            sb.append(ln()).append("  ").append(sqlFile.getName());
        }
        sb.append(ln()).append("- - - - - - - - - -/");
        _log.info(sb);
    }
}
