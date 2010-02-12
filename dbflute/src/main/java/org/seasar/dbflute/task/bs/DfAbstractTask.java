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
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Task;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfEnvironmentType;
import org.seasar.dbflute.friends.torque.DfAntTaskUtil;
import org.seasar.dbflute.helper.jdbc.connection.DfSimpleDataSourceCreator;
import org.seasar.dbflute.helper.jdbc.context.DfDataSourceContext;
import org.seasar.dbflute.logic.outsidesql.DfSqlFileCollector;
import org.seasar.dbflute.logic.scmconn.CurrentSchemaConnector;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;

/**
 * The abstract task.
 * @author jflute
 */
public abstract class DfAbstractTask extends Task {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfAbstractTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** DB driver. */
    protected String _driver;

    /** DB URL. */
    protected String _url;

    /** Schema name. */
    protected String _schema;

    /** User name. */
    protected String _userId;

    /** Password */
    protected String _password;

    /** Connection properties. */
    protected Properties _connectionProperties;

    protected DfSimpleDataSourceCreator _dataSourceCreator = new DfSimpleDataSourceCreator();

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    final public void execute() {
        long before = getTaskBeforeTimeMillis();
        try {
            initializeDatabaseInfo();
            if (isUseDataSource()) {
                setupDataSource();
            }
            doExecute();
        } catch (Exception e) {
            try {
                logException(e);
            } catch (Throwable ignored) {
                _log.warn("Ignored exception occured!", ignored);
                _log.error("Failed to execute DBFlute Task!", e);
            }
            throwTaskFailure();
        } catch (Error e) {
            try {
                logError(e);
            } catch (Throwable ignored) {
                _log.warn("Ignored exception occured!", ignored);
                _log.error("Failed to execute DBFlute Task!", e);
            }
            throwTaskFailure();
        } finally {
            if (isUseDataSource()) {
                try {
                    commitDataSource();
                } catch (SQLException ignored) {
                } finally {
                    try {
                        destroyDataSource();
                    } catch (SQLException ignored) {
                    }
                }
            }
            long after = getTaskAfterTimeMillis();
            if (isValidTaskEndInformation()) {
                String environmentType = DfEnvironmentType.getInstance().getEnvironmentType();
                StringBuilder sb = new StringBuilder();
                String ln = ln();
                sb.append(ln);
                sb.append(ln).append("_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/");
                sb.append(ln).append("[Task End]: " + getPerformanceView(after - before));
                sb.append(ln);
                sb.append(ln).append("  MY_PROJECT_NAME: {" + getBasicProperties().getProjectName() + "}");
                sb.append(ln).append("    database  = " + getBasicProperties().getDatabaseType());
                sb.append(ln).append("    language  = " + getBasicProperties().getTargetLanguage());
                sb.append(ln).append("    container = " + getBasicProperties().getTargetContainerName());
                sb.append(ln);
                sb.append(ln).append("  DBFLUTE_ENVIRONMENT_TYPE: {" + environmentType + "}");
                sb.append(ln).append("    driver = " + _driver);
                sb.append(ln).append("    url    = " + _url);
                sb.append(ln).append("    schema = " + _schema);
                sb.append(ln).append("    user   = " + _userId);
                sb.append(ln).append("    props  = " + _connectionProperties);
                String finalInformation = getFinalInformation();
                if (finalInformation != null) {
                    sb.append(ln);
                    sb.append(finalInformation);
                }
                sb.append(ln).append("_/_/_/_/_/_/_/_/_/_/" + " {" + getDisplayTaskName() + "}");
                _log.info(sb.toString());
            }
        }
    }

    protected long getTaskBeforeTimeMillis() {
        return System.currentTimeMillis();
    }

    protected long getTaskAfterTimeMillis() {
        return System.currentTimeMillis();
    }

    protected boolean isValidTaskEndInformation() {
        return true;
    }

    protected void logException(Exception e) {
        DfAntTaskUtil.logException(e, getDisplayTaskName());
    }

    protected void logError(Error e) {
        DfAntTaskUtil.logError(e, getDisplayTaskName());
    }

    protected String getDisplayTaskName() {
        final String taskName = getTaskName();
        return DfAntTaskUtil.getDisplayTaskName(taskName);
    }

    protected String getFinalInformation() {
        return null; // as default
    }

    protected void throwTaskFailure() {
        DfAntTaskUtil.throwTaskFailure(getDisplayTaskName());
    }

    protected void initializeDatabaseInfo() {
        _driver = getDatabaseProperties().getDatabaseDriver();
        _url = getDatabaseProperties().getDatabaseUrl();
        _userId = getDatabaseProperties().getDatabaseUser();
        _schema = getDatabaseProperties().getDatabaseSchema();
        _password = getDatabaseProperties().getDatabasePassword();
        _connectionProperties = getDatabaseProperties().getDatabaseConnectionProperties();
    }

    abstract protected void doExecute();

    /**
     * Get performance view.
     * @param mil The value of millisecond.
     * @return Performance view. (ex. 1m23s456ms) (NotNull)
     */
    protected String getPerformanceView(long mil) {
        if (mil < 0) {
            return String.valueOf(mil);
        }

        long sec = mil / 1000;
        long min = sec / 60;
        sec = sec % 60;
        mil = mil % 1000;

        StringBuffer sb = new StringBuffer();
        if (min >= 10) { // Minute
            sb.append(min).append("m");
        } else if (min < 10 && min >= 0) {
            sb.append("0").append(min).append("m");
        }
        if (sec >= 10) { // Second
            sb.append(sec).append("s");
        } else if (sec < 10 && sec >= 0) {
            sb.append("0").append(sec).append("s");
        }
        if (mil >= 100) { // Millisecond
            sb.append(mil).append("ms");
        } else if (mil < 100 && mil >= 10) {
            sb.append("0").append(mil).append("ms");
        } else if (mil < 10 && mil >= 0) {
            sb.append("00").append(mil).append("ms");
        }

        return sb.toString();
    }

    // -----------------------------------------------------
    //                                           Data Source
    //                                           -----------
    abstract protected boolean isUseDataSource();

    protected void setupDataSource() throws SQLException {
        _dataSourceCreator.setUserId(_userId);
        _dataSourceCreator.setPassword(_password);
        _dataSourceCreator.setDriver(_driver);
        _dataSourceCreator.setUrl(_url);
        _dataSourceCreator.setConnectionProperties(_connectionProperties);
        _dataSourceCreator.setAutoCommit(true);
        _dataSourceCreator.create();
        connectSchema();
    }

    protected void commitDataSource() throws SQLException {
        _dataSourceCreator.commit();
    }

    protected void destroyDataSource() throws SQLException {
        _dataSourceCreator.destroy();
    }

    protected DataSource getDataSource() {
        return DfDataSourceContext.getDataSource();
    }

    protected void connectSchema() {
        final CurrentSchemaConnector connector = new CurrentSchemaConnector(_schema, getBasicProperties());
        connector.connectSchema(getDataSource());
    }

    // -----------------------------------------------------
    //                                    Context Properties
    //                                    ------------------
    public void setContextProperties(String file) { // called by ANT
        try {
            final Properties prop = DfAntTaskUtil.getBuildProperties(file, getProject());
            DfBuildProperties.getInstance().setProperties(prop);
        } catch (RuntimeException e) {
            String msg = "Failed to set context properties:";
            msg = msg + " file=" + file;
            _log.warn(msg, e); // logging because it throws to ANT world
            throw e;
        }
    }

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    // ===================================================================================
    //                                                                 SQL File Collecting
    //                                                                 ===================
    /**
     * Collect SQL files the list.
     * @return The list of SQL files. (NotNull)
     */
    protected List<File> collectSqlFileList() {
        final String sqlDirectory = getProperties().getOutsideSqlProperties().getSqlDirectory();
        final DfSqlFileCollector sqlFileCollector = new DfSqlFileCollector(sqlDirectory, getBasicProperties());
        return sqlFileCollector.collectSqlFileList();
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
    public void setEnvironmentType(String environmentType) {
        DfEnvironmentType.getInstance().setEnvironmentType(environmentType);
    }
}