/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerResult.ErrorContinuedSql;
import org.seasar.dbflute.helper.token.line.LineToken;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.dbflute.helper.token.line.impl.LineTokenImpl;

/**
 * @author jflute
 */
public class DfSqlFileFireMan {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileFireMan.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _executorName;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * Load the SQL files and then execute them.
     * @return The result about firing SQL. (NotNull)
     */
    public DfSqlFileFireResult execute(DfSqlFileRunner runner, List<File> fileList) {
        final DfSqlFileFireResult fireResult = new DfSqlFileFireResult();
        int goodSqlCount = 0;
        int totalSqlCount = 0;
        for (final File file : fileList) {
            if (!file.exists()) {
                String msg = "The file was not found: " + file;
                throw new IllegalStateException(msg);
            }

            if (_log.isInfoEnabled()) {
                _log.info("{SQL File}: " + file);
            }

            runner.prepare(file);
            final DfSqlFileRunnerResult runnerResult = runner.runTransaction();
            fireResult.addRunnerResult(runnerResult);

            goodSqlCount = goodSqlCount + runnerResult.getGoodSqlCount();
            totalSqlCount = totalSqlCount + runnerResult.getTotalSqlCount();
        }
        final String title = _executorName != null ? _executorName : "Fired SQL";

        // Result Message
        final StringBuilder resultSb = new StringBuilder();
        resultSb.append("{").append(title).append("}: success=").append(goodSqlCount).append(" failure=").append(
                (totalSqlCount - goodSqlCount)).append(" (in ").append(fileList.size()).append(" files)");
        _log.info(resultSb.toString());
        fireResult.setResultMessage(resultSb.toString());

        // Exists Error
        fireResult.setExistsError(totalSqlCount > goodSqlCount);

        // Detail Message
        final StringBuilder detailSb = new StringBuilder();
        final List<DfSqlFileRunnerResult> runnerResultList = fireResult.getRunnerResultList();
        for (DfSqlFileRunnerResult currentResult : runnerResultList) {
            final List<ErrorContinuedSql> errorContinuedSqlList = currentResult.getErrorContinuedSqlList();
            final String fileName = currentResult.getSrcFile().getName();
            if (detailSb.length() > 0) {
                detailSb.append(ln());
            }
            detailSb.append(errorContinuedSqlList.isEmpty() ? "o " : "x ").append(fileName);
            for (ErrorContinuedSql errorContinuedSql : errorContinuedSqlList) {
                final String sql = errorContinuedSql.getSql();
                detailSb.append(ln()).append(sql);
                final SQLException sqlEx = errorContinuedSql.getSqlEx();
                String message = sqlEx.getMessage();
                if (sqlEx != null && message != null) {
                    message = message.trim();
                    final LineToken lineToken = new LineTokenImpl();
                    final LineTokenizingOption lineTokenizingOption = new LineTokenizingOption();
                    lineTokenizingOption.setDelimiter(ln());
                    final List<String> tokenizedList = lineToken.tokenize(message, lineTokenizingOption);
                    int elementIndex = 0;
                    for (String element : tokenizedList) {
                        if (elementIndex == 0) {
                            detailSb.append(ln()).append(" >> ").append(element);
                        } else {
                            detailSb.append(ln()).append("    ").append(element);
                        }
                        ++elementIndex;
                    }
                    detailSb.append(ln());
                    detailSb.append("    (SQLState=").append(sqlEx.getSQLState()).append(" ErrorCode=").append(
                            sqlEx.getErrorCode()).append(")");
                }
            }
        }
        fireResult.setDetailMessage(detailSb.toString());
        return fireResult;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getExecutorName() {
        return _executorName;
    }

    public void setExecutorName(String executorName) {
        this._executorName = executorName;
    }
}
