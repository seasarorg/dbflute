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
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.config.DfSpecifiedSqlFile;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.logic.outsidesqltest.DfOutsideSqlChecker;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlPack;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.task.bs.DfAbstractSqlExecutionTask;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public class DfOutsideSqlTestTask extends DfAbstractSqlExecutionTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfOutsideSqlTestTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The count of non-target SQL. */
    protected int _nonTargetSqlCount;

    // ===================================================================================
    //                                                                           Beginning
    //                                                                           =========
    @Override
    protected void begin() {
        _log.info("+------------------------------------------+");
        _log.info("|                                          |");
        _log.info("|              OutsideSqlTest              |");
        _log.info("|                                          |");
        _log.info("+------------------------------------------+");
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        super.doExecute();
    }

    // ===================================================================================
    //                                                                       Main Override
    //                                                                       =============
    @Override
    protected List<File> getTargetSqlFileList() {
        final DfOutsideSqlPack outsideSqlPack = collectSqlFileList();
        final String specifiedSqlFile = DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile();
        if (specifiedSqlFile != null) {
            final List<File> filteredList = new ArrayList<File>();
            for (File sqlFile : outsideSqlPack.getPhysicalFileList()) {
                final String fileName = sqlFile.getName();
                if (specifiedSqlFile.equals(fileName)) {
                    filteredList.add(sqlFile);
                }
            }
            return filteredList;
        } else {
            return outsideSqlPack.getPhysicalFileList();
        }
    }

    @Override
    protected DfSqlFileRunnerExecute getSqlFileRunner(final DfRunnerInformation runInfo) {
        final DBDef currentDBDef = getDatabaseTypeFacadeProp().getCurrentDBDef();
        return new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
            @Override
            protected String filterSql(String sql) {
                // /- - - - - - - - - - - - - - - - - - - - - - - - - - 
                // check parameter comments in the SQL before filtering
                // - - - - - - - - - -/
                checkParameterComment(_sqlFile, sql);

                // filter comments if it needs.
                if (!currentDBDef.dbway().isBlockCommentSupported()) {
                    sql = removeBlockComment(sql);
                }
                if (!currentDBDef.dbway().isLineCommentSupported()) {
                    sql = removeLineComment(sql);
                }

                return super.filterSql(sql);
            }

            protected String removeBlockComment(final String sql) {
                return Srl.removeBlockComment(sql);
            }

            protected String removeLineComment(final String sql) {
                return Srl.removeLineComment(sql);
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
                _log.info("SQL:" + ln() + sql);
            }

            @Override
            protected void traceResult(int goodSqlCount, int totalSqlCount) {
                _log.info(" -> success=" + goodSqlCount + " failure=" + (totalSqlCount - goodSqlCount) + ln());
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
                final List<ScopeInfo> scopeList = Srl.extractScopeList(targetStr, beginMark, endMark);
                final List<String> resultList = DfCollectionUtil.newArrayList();
                for (ScopeInfo scope : scopeList) {
                    resultList.add(scope.getContent());
                }
                return resultList;
            }
        };
    }

    protected void checkParameterComment(File sqlFile, String sql) {
        if (getOutsideSqlProperties().isSuppressParameterCommentCheck()) {
            return;
        }
        final DfOutsideSqlChecker checker = new DfOutsideSqlChecker();

        // the IfCommentExpression check is for Java only
        if (getLanguageTypeFacadeProp().isTargetLanguageJava()) {
            checker.enableIfCommentExpressionCheck();
        }

        if (getOutsideSqlProperties().isRequiredSqlTitle()) {
            checker.enableRequiredTitleCheck();
        }

        if (getOutsideSqlProperties().isRequiredSqlDescription()) {
            checker.enableRequiredDescriptionCheck();
        }

        checker.check(sqlFile.getName(), sql);
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
        return true; // this task does not commit 
    }

    @Override
    protected void customizeRunnerInformation(DfRunnerInformation runInfo) {
        runInfo.setEncoding(getOutsideSqlProperties().getSqlFileEncoding());
    }

    @Override
    protected void showTargetSqlFileInformation(List<File> sqlFileList) {
        super.showTargetSqlFileInformation(sqlFileList);
        if (_nonTargetSqlCount > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(ln()).append("/- - - - - - - - - - - - - - - - - - - - - - - -");
            sb.append(ln()).append("Non-target SQL count: ").append(_nonTargetSqlCount);
            sb.append(ln()).append("- - - - - - - - - -/");
            _log.info(sb.toString());
        }
        final String specifiedSqlFile = DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile();
        if (specifiedSqlFile != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(ln()).append("/- - - - - - - - - - - - - - - - - - - - - - - -");
            sb.append(ln()).append("Specified SQL file: ").append(specifiedSqlFile);
            sb.append(ln()).append("- - - - - - - - - -/");
            _log.info(sb.toString());
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return getProperties().getOutsideSqlProperties();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSpecifiedSqlFile(String specifiedSqlFile) {
        DfSpecifiedSqlFile.getInstance().setSpecifiedSqlFile(specifiedSqlFile);
    }
}
