/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.task.manage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.logic.replaceschema.process.DfTakeFinallyProcess;
import org.seasar.dbflute.task.bs.DfAbstractTask;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.1A (2011/10/06 Thursday)
 */
public class DfTakeAssertTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfTakeAssertTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _sqlRootDir;
    protected DfTakeFinallyFinalInfo _finalInfo;

    // ===================================================================================
    //                                                                           Beginning
    //                                                                           =========
    @Override
    protected void begin() {
        _log.info("+------------------------------------------+");
        _log.info("|                                          |");
        _log.info("|               Take Assert                |");
        _log.info("|                                          |");
        _log.info("+------------------------------------------+");
    }

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
        final String sqlRootDir = Srl.is_NotNull_and_NotTrimmedEmpty(_sqlRootDir) ? _sqlRootDir : "./playsql";
        final DfTakeFinallyProcess process = DfTakeFinallyProcess.createAsTakeAssert(sqlRootDir, getDataSource());
        _finalInfo = process.execute();
        handleFinalInfo(_finalInfo, sqlRootDir);
    }

    protected void handleFinalInfo(DfTakeFinallyFinalInfo finalInfo, String sqlRootDir) {
        final DfTakeFinallyAssertionFailureException assertionEx = finalInfo.getAssertionEx();
        if (assertionEx == null) {
            _log.info("*All assertions are successful");
            return;
        }
        dumpAssertionFailure(assertionEx);
        throw assertionEx;
    }

    protected void dumpAssertionFailure(DfTakeFinallyAssertionFailureException assertionEx) {
        final File dumpFile = new File("./log/take-assert.log");
        if (dumpFile.exists()) {
            dumpFile.delete();
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
            bw.write(assertionEx.getMessage());
            bw.flush();
        } catch (IOException e) {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    @Override
    public String getFinalInformation() {
        return buildFinalMessage();
    }

    protected String buildFinalMessage() {
        final DfTakeFinallyFinalInfo finalInfo = _finalInfo; // null allowed
        final StringBuilder sb = new StringBuilder();

        // TakeFinally
        if (finalInfo != null) {
            if (finalInfo.isValidInfo()) {
                buildSchemaTaskContents(sb, finalInfo);
            }
            if (finalInfo.getAssertionEx() != null) {
                sb.append(ln()).append("    * * * * * * * * * * *");
                sb.append(ln()).append("    * Assertion Failure *");
                sb.append(ln()).append("    * * * * * * * * * * *");
            }
        }
        return sb.toString();
    }

    protected void buildSchemaTaskContents(StringBuilder sb, DfTakeFinallyFinalInfo finalInfo) {
        sb.append(" ").append(finalInfo.getResultMessage());
        final List<String> detailMessageList = finalInfo.getDetailMessageList();
        for (String detailMessage : detailMessageList) {
            sb.append(ln()).append("  ").append(detailMessage);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSqlRootDir(String sqlRootDir) {
        if (Srl.is_Null_or_TrimmedEmpty(sqlRootDir)) {
            return;
        }
        if (sqlRootDir.equals("${dfdir}")) {
            return;
        }
        _sqlRootDir = sqlRootDir;
    }
}