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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataResultInfo;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataSeveralHandlingInfo;
import org.seasar.dbflute.helper.datahandler.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.helper.datahandler.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerJdbc;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerMySQL;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerOracle;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerSqlServer;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerSybase;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

public class DfReplaceSchemaTask extends DfAbstractTask {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaTask.class);

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
        _log.info("* * * * * * * * * * *");
        _log.info("environmentType: " + getEnvironmentType());
        _log.info("* * * * * * * * * * *");
        _log.info("isReplaceSchemaAutoCommit    = " + getMyProperties().isReplaceSchemaAutoCommit());
        _log.info("isReplaceSchemaRollbackOnly  = " + getMyProperties().isReplaceSchemaRollbackOnly());
        _log.info("isReplaceSchemaErrorContinue = " + getMyProperties().isReplaceSchemaErrorContinue());

        initializeSchema();
        final DfRunnerInformation runInfo = createRunnerInformation();
        replaceSchema(runInfo);
        writeDbFromSeparatedFileAsCommonData("tsv", "\t");
        writeDbFromSeparatedFileAsCommonData("csv", ",");
        writeDbFromXlsAsCommonData();

        writeDbFromSeparatedFileAsAdditionalData("tsv", "\t");
        writeDbFromSeparatedFileAsAdditionalData("csv", ",");
        writeDbFromXlsAsAdditionalData();
        takeFinally(runInfo);
    }

    // --------------------------------------------
    //                            initialize schema
    //                            -----------------
    protected void initializeSchema() {
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        final DfSchemaInitializer initializer;
        if (basicProperties.isDatabaseMySQL()) {
            initializer = createSchemaInitializerMySQL();
        } else if (basicProperties.isDatabaseOracle()) {
            initializer = createSchemaInitializerOracle();
        } else if (basicProperties.isDatabaseSqlServer()) {
            initializer = createSchemaInitializerSqlServer();
        } else if (basicProperties.isDatabaseSybase()) {
            initializer = createSchemaInitializerSybase();
        } else {
            initializer = createSchemaInitializerJdbc();
        }
        if (initializer != null) {
            initializer.initializeSchema();
        }
    }

    protected DfSchemaInitializer createSchemaInitializerMySQL() {
        final DfSchemaInitializerMySQL initializer = new DfSchemaInitializerMySQL();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerOracle() {
        final DfSchemaInitializerOracle initializer = new DfSchemaInitializerOracle();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerSqlServer() {
        final DfSchemaInitializerSqlServer initializer = new DfSchemaInitializerSqlServer();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerSybase() {
        final DfSchemaInitializerSybase initializer = new DfSchemaInitializerSybase();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerJdbc() {
        final DfSchemaInitializerJdbc initializer = new DfSchemaInitializerJdbc();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    // --------------------------------------------
    //                                       runner
    //                                       ------
    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setEncoding(getReplaceSchemaSqlFileEncoding());
        runInfo.setAutoCommit(getMyProperties().isReplaceSchemaAutoCommit());
        runInfo.setErrorContinue(getMyProperties().isReplaceSchemaErrorContinue());
        runInfo.setRollbackOnly(getMyProperties().isReplaceSchemaRollbackOnly());
        return runInfo;
    }
    
    protected String getReplaceSchemaSqlFileEncoding() {
        return "UTF-8";
    }

    protected void replaceSchema(DfRunnerInformation runInfo) {
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getReplaceSchemaSqlFileList());
    }

    protected void takeFinally(DfRunnerInformation runInfo) {
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getTakeFinallySqlFileList());
    }

    protected DfSqlFileRunner getSqlFileRunner(final DfRunnerInformation runInfo) {
        return new DfSqlFileRunnerExecute(runInfo, getDataSource());
    }

    // --------------------------------------------
    //                      replace schema sql file
    //                      -----------------------
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
        return DfBuildProperties.getInstance().getInvokeReplaceSchemaProperties();
    }

    // --------------------------------------------
    //                           after all sql file
    //                           ------------------
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
    //                                     xls data
    //                                     --------
    protected void writeDbFromXlsAsCommonData() {
        writeDbFromXls(getCommonDataDirectoryPath("xls"));
    }

    protected void writeDbFromXlsAsAdditionalData() {
        writeDbFromXls(getAdditionalDataDirectoryPath(getEnvironmentType(), "xls"));
    }

    protected void writeDbFromXls(String directoryPath) {
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

    // --------------------------------------------
    //                                     tsv data
    //                                     --------
    protected void writeDbFromSeparatedFileAsCommonData(String typeName, String delimter) {
        writeDbFromSeparatedFile(typeName, delimter, getCommonDataDirectoryPath("tsv"));
    }

    protected void writeDbFromSeparatedFileAsAdditionalData(String typeName, String delimter) {
        writeDbFromSeparatedFile(typeName, delimter, getAdditionalDataDirectoryPath(getEnvironmentType(), typeName));
    }

    protected void writeDbFromSeparatedFile(String typeName, String delimter, String directoryPath) {
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
}
