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
package org.seasar.dbflute.logic.doc.craftdiff;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfCraftDiffNonAssertionSqlFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerDispatcher;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute.DfRunnerDispatchResult;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerResult;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.properties.facade.DfLanguageTypeFacadeProp;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffSqlFire {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfCraftDiffSqlFire.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                        Basic Resource
    //                                        --------------
    protected final String _sqlRootDir;
    protected final DataSource _dataSource;
    protected final UnifiedSchema _mainSchema;
    protected final String _craftMetaDir;

    // -----------------------------------------------------
    //                                                Result
    //                                                ------
    protected final List<File> _executedSqlFileList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCraftDiffSqlFire(String sqlRootDir, DataSource dataSource, UnifiedSchema mainSchema, String craftMetaDir) {
        _sqlRootDir = sqlRootDir;
        _dataSource = dataSource;
        _mainSchema = mainSchema;
        _craftMetaDir = craftMetaDir;
    }

    public DfSqlFileFireResult fire(DfRunnerInformation runInfo) {
        _log.info("");
        _log.info("* * * * * * * **");
        _log.info("*              *");
        _log.info("*  Craft Diff  *");
        _log.info("*              *");
        _log.info("* * * * * * * **");
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan() {
            @Override
            protected DfSqlFileRunnerResult processSqlFile(DfSqlFileRunner runner, File sqlFile) {
                _executedSqlFileList.add(sqlFile);
                return super.processSqlFile(runner, sqlFile);
            }
        };
        fireMan.setExecutorName("Craft Diff");
        return fireMan.fire(getSqlFileRunner4CraftDiff(runInfo), getCraftDiffSqlFileList());
    }

    protected DfSqlFileRunner getSqlFileRunner4CraftDiff(final DfRunnerInformation runInfo) {
        final DfReplaceSchemaProperties prop = getReplaceSchemaProperties();
        final DfSqlFileRunnerExecute runnerExecute = new DfSqlFileRunnerExecute(runInfo, _dataSource) {
            @Override
            protected String filterSql(String sql) {
                sql = super.filterSql(sql);
                sql = prop.resolveFilterVariablesIfNeeds(sql);
                return sql;
            }

            @Override
            protected String getTerminator4Tool() {
                return resolveTerminator4Tool();
            }

            @Override
            protected boolean isTargetFile(String sql) {
                return isTargetRepsFile(sql); // TODO jflute
            }
        };
        final DfCraftDiffAssertProvider provider = new DfCraftDiffAssertProvider(getEnvType(), _craftMetaDir);
        runnerExecute.setDispatcher(new DfSqlFileRunnerDispatcher() {
            public DfRunnerDispatchResult dispatch(File sqlFile, Statement st, String sql) throws SQLException {
                final DfCraftDiffAssertHandler handler = provider.provideCraftDiffAssertHandler(sql);
                if (handler == null) {
                    throwCraftDiffNonAssertionSqlFoundException(sqlFile, sql);
                }
                handler.handle(sqlFile, st, sql);
                return DfRunnerDispatchResult.DISPATCHED;
            }
        });
        return runnerExecute;
    }

    protected void throwCraftDiffNonAssertionSqlFoundException(File sqlFile, String sql) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Found the non-assertion SQL.");
        br.addItem("Advice");
        br.addElement("Confirm your SQL files for CraftDiff.");
        br.addElement("For example, non-assertion SQL on CraftDiff is restricted.");
        br.addItem("SQL File");
        br.addElement(sqlFile.getPath());
        br.addItem("non-assertion SQL");
        br.addElement(sql);
        final String msg = br.buildExceptionMessage();
        throw new DfCraftDiffNonAssertionSqlFoundException(msg);
    }

    protected List<File> getCraftDiffSqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        // TODO jflute
        fileList.addAll(getReplaceSchemaProperties().getTakeFinallySqlFileList(_sqlRootDir));
        fileList.addAll(getReplaceSchemaProperties().getAppcalitionTakeFinallySqlFileList());
        return fileList;
    }

    protected String getEnvType() {
        return null; // TODO jflute
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return getProperties().getReplaceSchemaProperties();
    }

    protected String resolveTerminator4Tool() {
        return getReplaceSchemaProperties().resolveTerminator4Tool();
    }

    protected boolean isTargetRepsFile(String sql) {
        return getReplaceSchemaProperties().isTargetRepsFile(sql);
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfDatabaseTypeFacadeProp getDatabaseTypeFacadeProp() {
        return getBasicProperties().getDatabaseTypeFacadeProp();
    }

    protected DfLanguageTypeFacadeProp getLanguageTypeFacadeProp() {
        return getBasicProperties().getLanguageTypeFacadeProp();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }
}
