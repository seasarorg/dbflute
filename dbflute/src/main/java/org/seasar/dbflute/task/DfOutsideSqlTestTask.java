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
package org.seasar.dbflute.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.config.DfSpecifiedSqlFile;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.logic.factory.DfJdbcDeterminerFactory;
import org.seasar.dbflute.logic.sqlfile.OutsideSqlChecker;
import org.seasar.dbflute.task.bs.DfAbstractSqlExecutionTask;
import org.seasar.dbflute.util.DfSqlStringUtil;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class DfOutsideSqlTestTask extends DfAbstractSqlExecutionTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSql2EntityTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The count of non-target SQL. */
    protected int _nonTargetSqlCount;

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected void doExecute() {
        super.doExecute();
    }

    @Override
    protected void customizeRunnerInformation(DfRunnerInformation runInfo) {
        runInfo.setEncoding(getProperties().getOutsideSqlProperties().getSqlFileEncoding());
    }

    @Override
    protected List<File> getTargetSqlFileList() {
        final List<File> sqlFileList = collectSqlFileList();
        final String specifiedSqlFile = DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile();
        if (specifiedSqlFile != null) {
            final List<File> filteredList = new ArrayList<File>();
            for (File sqlFile : sqlFileList) {
                final String fileName = sqlFile.getName();
                if (specifiedSqlFile.equals(fileName)) {
                    filteredList.add(sqlFile);
                }
            }
            return filteredList;
        } else {
            return sqlFileList;
        }
    }

    @Override
    protected DfSqlFileRunnerExecute getSqlFileRunner(final DfRunnerInformation runInfo) {
        final DfJdbcDeterminer jdbcDeterminer = createJdbcDeterminer();
        return new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
            @Override
            protected String filterSql(String sql) {
                // /- - - - - - - - - - - - - - - - - - - - - - - - - - - 
                // Check parameter comments in the SQL before filtering.
                // - - - - - - - - - -/
                checkParameterComment(_sqlFile, sql);

                // Filter comments if it needs.
                if (!jdbcDeterminer.isBlockCommentValid()) {
                    sql = removeBlockComment(sql);
                }
                if (!jdbcDeterminer.isLineCommentValid()) {
                    sql = removeLineComment(sql);
                }

                return super.filterSql(sql);
            }

            protected String removeBlockComment(final String sql) {
                return DfSqlStringUtil.removeBlockComment(sql);
            }

            protected String removeLineComment(final String sql) {
                return DfSqlStringUtil.removeLineComment(sql);
            }

            @Override
            protected boolean isTargetSql(String sql) {
                final String entityName = getEntityName(sql);
                if (entityName != null && "df:x".equalsIgnoreCase(entityName)) { // Non target SQL!
                    ++_nonTargetSqlCount;
                    return false;
                }
                return super.isTargetSql(sql);
            }

            @Override
            protected void traceSql(String sql) {
                String msg = ln() + "";
                msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * " + ln();
                msg = msg + sql + ln();
                msg = msg + "* * * * * * * * * */";
                _log.info(msg);
            }

            @Override
            protected void traceResult(int goodSqlCount, int totalSqlCount) {
                _log.info("  --> success=" + goodSqlCount + " failure=" + (totalSqlCount - goodSqlCount) + ln());
            }

            @Override
            protected boolean isSqlTrimAndRemoveLineSeparator() {
                return false;
            }

            protected String getEntityName(final String sql) {
                return getTargetString(sql, "#");
            }

            protected String getTargetString(final String sql, final String mark) {
                final List<String> targetList = getTargetList(sql, mark);
                return !targetList.isEmpty() ? targetList.get(0) : null;
            }

            protected List<String> getTargetList(final String sql, final String mark) {
                if (sql == null || sql.trim().length() == 0) {
                    String msg = "The sql is invalid: " + sql;
                    throw new IllegalArgumentException(msg);
                }
                final List<String> betweenBeginEndMarkList = getListBetweenBeginEndMark(sql, "--" + mark, mark);
                if (!betweenBeginEndMarkList.isEmpty()) {
                    return betweenBeginEndMarkList;
                } else {
                    // for MySQL. 
                    return getListBetweenBeginEndMark(sql, "-- " + mark, mark);
                }
            }

            protected List<String> getListBetweenBeginEndMark(String targetStr, String beginMark, String endMark) {
                return DfStringUtil.extractAllScope(targetStr, beginMark, endMark);
            }
        };
    }

    protected void checkParameterComment(File sqlFile, String sql) {
        final OutsideSqlChecker checker = new OutsideSqlChecker();

        // the IfCommentExpression check is for Java only
        if (!getBasicProperties().isTargetLanguageJava()) {
            checker.suppressIfCommentExpressionCheck();
        }

        checker.check(sqlFile.getName(), sql);
    }

    @Override
    protected String getSqlDirectory() {
        return getProperties().getOutsideSqlProperties().getSqlDirectory();
    }

    @Override
    protected boolean isAutoCommit() {
        return false;
    }

    @Override
    protected boolean isErrorContinue() {
        return false;
    }

    @Override
    protected boolean isRollbackOnly() {
        return true;
    }

    @Override
    protected void showTargetSqlFileInformation(List<File> sqlFileList) {
        super.showTargetSqlFileInformation(sqlFileList);
        if (_nonTargetSqlCount > 0) {
            _log.info("/- - - - - - - - - - - - - - - - - - - - - - - -");
            _log.info("Non target SQL count: " + _nonTargetSqlCount);
            _log.info("- - - - - - - - - -/");
            _log.info(" ");
        }
        final String specifiedSqlFile = DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile();
        if (specifiedSqlFile != null) {
            _log.info("/- - - - - - - - - - - - - - - - - - - - - - - -");
            _log.info("Specified SQL file: " + specifiedSqlFile);
            _log.info("- - - - - - - - - -/");
            _log.info(" ");
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DfJdbcDeterminer createJdbcDeterminer() {
        return new DfJdbcDeterminerFactory(getBasicProperties()).createJdbcDeterminer();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSpecifiedSqlFile(String specifiedSqlFile) {
        DfSpecifiedSqlFile.getInstance().setSpecifiedSqlFile(specifiedSqlFile);
    }
}
