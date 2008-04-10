/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataResultInfo;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataSeveralHandlingInfo;
import org.seasar.dbflute.helper.datahandler.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.helper.datahandler.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerJdbc;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerMySQL;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerSqlServer;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

public class DfReplaceSchemaTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaTask.class);

    // ===================================================================================
    //                                                                 DataSource Override
    //                                                                 ===================
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        if (_log.isInfoEnabled()) {
            _log.info("");
            _log.info("{Replace Schema Properties}");
            _log.info("environmentType  = " + getEnvironmentType());
            _log.info("loggingInsertSql = " + getMyProperties().isLoggingInsertSql());
            _log.info("autoCommit       = " + getMyProperties().isAutoCommit());
            _log.info("rollbackOnly     = " + getMyProperties().isRollbackOnly());
            _log.info("errorContinue    = " + getMyProperties().isErrorContinue());
            _log.info("sqlFileEncoding  = " + getMyProperties().getSqlFileEncoding());
            _log.info("");
        }

        initializeSchema();

        final DfRunnerInformation runInfo = createRunnerInformation();
        createSchema(runInfo);

        writeDbFromSeparatedFileAsCommonData("tsv", "\t");
        writeDbFromSeparatedFileAsCommonData("csv", ",");
        writeDbFromXlsAsCommonData();

        writeDbFromSeparatedFileAsAdditionalData("tsv", "\t");
        writeDbFromSeparatedFileAsAdditionalData("csv", ",");
        writeDbFromXlsAsAdditionalData();

        takeFinally(runInfo);
    }

    // --------------------------------------------
    //                            Initialize Schema
    //                            -----------------
    protected void initializeSchema() {
        if (_log.isInfoEnabled()) {
            _log.info("* * * * * * * * * * *");
            _log.info("*                   *");
            _log.info("* Initialize Schema *");
            _log.info("*                   *");
            _log.info("* * * * * * * * * * *");
        }
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        final DfSchemaInitializer initializer;
        if (basicProperties.isDatabaseMySQL()) {
            initializer = createSchemaInitializerMySQL();
        } else if (basicProperties.isDatabaseSqlServer()) {
            initializer = createSchemaInitializerSqlServer();
        } else {
            initializer = createSchemaInitializerJdbc();
        }
        if (initializer != null) {
            initializer.initializeSchema();
        }
        if (_log.isInfoEnabled()) {
            _log.info("");
        }
    }

    protected DfSchemaInitializer createSchemaInitializerMySQL() {
        final DfSchemaInitializerMySQL initializer = new DfSchemaInitializerMySQL();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerSqlServer() {
        final DfSchemaInitializerSqlServer initializer = new DfSchemaInitializerSqlServer();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerJdbc() {
        final DfSchemaInitializerJdbc initializer = new DfSchemaInitializerJdbc();
        initializer.setDataSource(getDataSource());
        initializer.setSchema(getBasicProperties().getDatabaseSchema());
        return initializer;
    }

    // --------------------------------------------
    //                                Create Schema
    //                                -------------
    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setEncoding(getReplaceSchemaSqlFileEncoding());
        runInfo.setAutoCommit(getMyProperties().isAutoCommit());
        runInfo.setErrorContinue(getMyProperties().isErrorContinue());
        runInfo.setRollbackOnly(getMyProperties().isRollbackOnly());
        return runInfo;
    }

    protected String getReplaceSchemaSqlFileEncoding() {
        return getMyProperties().getSqlFileEncoding();
    }

    protected void createSchema(DfRunnerInformation runInfo) {
        if (_log.isInfoEnabled()) {
            _log.info("* * * * * * * * *");
            _log.info("*               *");
            _log.info("* Create Schema *");
            _log.info("*               *");
            _log.info("* * * * * * * * *");
        }
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getReplaceSchemaSqlFileList());
        if (_log.isInfoEnabled()) {
            _log.info("");
        }
    }

    protected void takeFinally(DfRunnerInformation runInfo) {
        if (_log.isInfoEnabled()) {
            _log.info("* * * * * * * **");
            _log.info("*              *");
            _log.info("* Take Finally *");
            _log.info("*              *");
            _log.info("* * * * * * * **");
        }
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getTakeFinallySqlFileList());
        if (_log.isInfoEnabled()) {
            _log.info("");
        }
    }

    protected DfSqlFileRunner getSqlFileRunner(final DfRunnerInformation runInfo) {
        return new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
            @Override
            protected boolean isSqlTrimAndRemoveLineSeparator() {
                return true;
            }
        };
    }

    protected List<File> getReplaceSchemaSqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(getReplaceSchemaNextSqlFileList());
        return fileList;
    }

    protected List<File> getReplaceSchemaNextSqlFileList() {
        final String replaceSchemaSqlFileDirectoryName = getReplaceSchemaSqlFileDirectoryName();
        final File baseDir = new File(replaceSchemaSqlFileDirectoryName);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(getReplaceSchemaSqlFileNameWithoutExt())) {
                    if (name.endsWith("." + getReplaceSchemaSqlFileExt())) {
                        return true;
                    }
                }
                return false;
            }
        };

        // Order by FileName Asc
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final String[] targetList = baseDir.list(filter);
        if (targetList == null) {
            return new ArrayList<File>();
        }
        for (String targetFileName : targetList) {
            final String targetFilePath = replaceSchemaSqlFileDirectoryName + "/" + targetFileName;
            treeSet.add(new File(targetFilePath));
        }
        return new ArrayList<File>(treeSet);
    }

    protected String getReplaceSchemaSqlFileDirectoryName() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(0, sqlFileName.lastIndexOf("/"));
    }

    protected String getReplaceSchemaSqlFileNameWithoutExt() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        final String tmp = sqlFileName.substring(sqlFileName.lastIndexOf("/") + 1);
        return tmp.substring(0, tmp.lastIndexOf("."));
    }

    protected String getReplaceSchemaSqlFileExt() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(sqlFileName.lastIndexOf(".") + 1);
    }

    protected String getEnvironmentType() {
        return getMyProperties().getEnvironmentType();
    }

    public boolean isLoggingInsertSql() {
        return getMyProperties().isLoggingInsertSql();
    }

    protected DfReplaceSchemaProperties getMyProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

    // --------------------------------------------
    //                                 Take Finally
    //                                 ------------
    protected List<File> getTakeFinallySqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(getTakeFinallyNextSqlFileList());
        return fileList;
    }

    protected List<File> getTakeFinallyNextSqlFileList() {
        final String replaceSchemaSqlFileDirectoryName = getTakeFinallySqlFileDirectoryName();
        final File baseDir = new File(replaceSchemaSqlFileDirectoryName);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(getTakeFinallySqlFileNameWithoutExt())) {
                    if (name.endsWith("." + getTakeFinallySqlFileExt())) {
                        return true;
                    }
                }
                return false;
            }
        };
        // Order by FileName Asc
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final String[] targetList = baseDir.list(filter);
        if (targetList == null) {
            return new ArrayList<File>();
        }
        for (String targetFileName : targetList) {
            final String targetFilePath = replaceSchemaSqlFileDirectoryName + "/" + targetFileName;
            treeSet.add(new File(targetFilePath));
        }
        return new ArrayList<File>(treeSet);
    }

    protected String getTakeFinallySqlFileDirectoryName() {
        final String sqlFileName = getMyProperties().getTakeFinallySqlFile();
        return sqlFileName.substring(0, sqlFileName.lastIndexOf("/"));
    }

    protected String getTakeFinallySqlFileNameWithoutExt() {
        final String sqlFileName = getMyProperties().getTakeFinallySqlFile();
        final String tmp = sqlFileName.substring(sqlFileName.lastIndexOf("/") + 1);
        return tmp.substring(0, tmp.lastIndexOf("."));
    }

    protected String getTakeFinallySqlFileExt() {
        final String sqlFileName = getMyProperties().getTakeFinallySqlFile();
        return sqlFileName.substring(sqlFileName.lastIndexOf(".") + 1);
    }

    // --------------------------------------------
    //                               Separated Data
    //                               --------------
    protected void writeDbFromSeparatedFileAsCommonData(String typeName, String delimter) {
        writeDbFromSeparatedFile(typeName, delimter, getCommonDataDirectoryPath("tsv"), "common");
    }

    protected void writeDbFromSeparatedFileAsAdditionalData(String typeName, String delimter) {
        writeDbFromSeparatedFile(typeName, delimter, getAdditionalDataDirectoryPath(getEnvironmentType(), typeName),
                getEnvironmentType());
    }

    protected void writeDbFromSeparatedFile(String typeName, String delimter, String directoryPath, String location) {
        final DfSeparatedDataHandlerImpl handler = new DfSeparatedDataHandlerImpl();
        handler.setLoggingInsertSql(isLoggingInsertSql());
        handler.setDataSource(getDataSource());
        final DfSeparatedDataSeveralHandlingInfo handlingInfo = new DfSeparatedDataSeveralHandlingInfo();
        handlingInfo.setBasePath(directoryPath);
        handlingInfo.setTypeName(typeName);
        handlingInfo.setDelimter(delimter);
        handlingInfo.setErrorContinue(true);
        final DfSeparatedDataResultInfo resultInfo = handler.writeSeveralData(handlingInfo);
        showNotFoundColumn(typeName, resultInfo.getNotFoundColumnMap());
    }

    protected void showNotFoundColumn(String typeName, Map<String, Set<String>> notFoundColumnMap) {
        if (notFoundColumnMap.isEmpty()) {
            return;
        }
        _log.warn("* * * * * * * * * * * * * * *");
        _log.warn("Not Persistent Columns in " + typeName);
        _log.warn("* * * * * * * * * * * * * * *");
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

    protected String getCommonDataDirectoryPath(final String typeName) {
        return getReplaceSchemaSqlFileDirectoryName() + "/data/common/" + typeName;
    }

    protected String getAdditionalDataDirectoryPath(final String envType, final String typeName) {
        return getReplaceSchemaSqlFileDirectoryName() + "/data/" + envType + "/" + typeName;
    }

    // --------------------------------------------
    //                                     Xls Data
    //                                     --------
    protected void writeDbFromXlsAsCommonData() {
        writeDbFromXls(getCommonDataDirectoryPath("xls"), "common");
    }

    protected void writeDbFromXlsAsAdditionalData() {
        writeDbFromXls(getAdditionalDataDirectoryPath(getEnvironmentType(), "xls"), getEnvironmentType());
    }

    protected void writeDbFromXls(String directoryPath, String typeName) {
        final DfXlsDataHandlerImpl xlsDataHandler = new DfXlsDataHandlerImpl();
        xlsDataHandler.setLoggingInsertSql(isLoggingInsertSql());
        xlsDataHandler.setSchemaName(_schema);// For getting database meta data.
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        if (basicProperties.isDatabaseSqlServer()) {
            xlsDataHandler.writeSeveralDataForSqlServer(directoryPath, getDataSource());
        } else if (basicProperties.isDatabaseSybase()) {
            xlsDataHandler.writeSeveralDataForSybase(directoryPath, getDataSource());
        } else {
            xlsDataHandler.writeSeveralData(directoryPath, getDataSource());
        }
    }
}
