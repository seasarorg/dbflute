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

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Task;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfEnvironmentType;
import org.seasar.dbflute.helper.jdbc.connection.DfConnectionMetaInfo;
import org.seasar.dbflute.helper.jdbc.connection.DfDataSourceHandler;
import org.seasar.dbflute.helper.jdbc.context.DfDataSourceContext;
import org.seasar.dbflute.logic.DfDBFluteTaskUtil;
import org.seasar.dbflute.logic.generate.refresh.DfRefreshResourceProcess;
import org.seasar.dbflute.logic.jdbc.connection.DfCurrentSchemaConnector;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlCollector;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlPack;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfRefreshProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.properties.facade.DfLanguageTypeFacadeProp;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;

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

    /** Main schema. */
    protected UnifiedSchema _mainSchema;

    /** User name. */
    protected String _user;

    /** Password */
    protected String _password;

    /** Connection properties. */
    protected Properties _connectionProperties;

    /** The handler of data source. (NotNull) */
    protected final DfDataSourceHandler _dataSourceHandler = new DfDataSourceHandler();

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    public final void execute() {
        Throwable cause = null;
        long before = getTaskBeforeTimeMillis();
        try {
            begin();
            initializeDatabaseInfo();
            if (isUseDataSource()) {
                setupDataSource();
            }
            initializeVariousEnvironment();
            doExecute();
        } catch (Exception e) {
            cause = e;
            try {
                logException(e);
            } catch (Throwable ignored) {
                _log.warn("*Ignored exception occured!", ignored);
                _log.error("*Failed to execute DBFlute Task!", e);
            }
        } catch (Error e) {
            cause = e;
            try {
                logError(e);
            } catch (Throwable ignored) {
                _log.warn("*Ignored exception occured!", ignored);
                _log.error("*Failed to execute DBFlute Task!", e);
            }
        } finally {
            if (isUseDataSource()) {
                try {
                    commitDataSource();
                } catch (Exception ignored) {
                } finally {
                    try {
                        destroyDataSource();
                    } catch (Exception ignored) {
                        _log.warn("*Failed to destroy data source: " + ignored.getMessage());
                    }
                }
            }
            if (isValidTaskEndInformation() || cause != null) {
                try {
                    long after = getTaskAfterTimeMillis();
                    showFinalMessage(before, after, cause != null);
                } catch (RuntimeException e) {
                    _log.info("*Failed to show final message!", e);
                }
            }
            if (cause != null) {
                throwTaskFailure();
            }
        }
    }

    protected abstract void begin();

    protected long getTaskBeforeTimeMillis() {
        return System.currentTimeMillis();
    }

    protected long getTaskAfterTimeMillis() {
        return System.currentTimeMillis();
    }

    protected void logException(Exception e) {
        DfDBFluteTaskUtil.logException(e, getDisplayTaskName(), getConnectionMetaInfo());
    }

    protected void logError(Error e) {
        DfDBFluteTaskUtil.logError(e, getDisplayTaskName(), getConnectionMetaInfo());
    }

    protected boolean isValidTaskEndInformation() {
        return true;
    }

    // -----------------------------------------------------
    //                                         Final Message
    //                                         -------------
    protected void showFinalMessage(long before, long after, boolean abort) {
        final String environmentType = DfEnvironmentType.getInstance().getEnvironmentType();
        final StringBuilder sb = new StringBuilder();
        final String ln = ln();
        sb.append(ln).append("_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/");
        sb.append(ln).append("[Final Message]: ").append(getPerformanceView(after - before));
        if (abort) {
            sb.append(" *Abort");
        }
        sb.append(ln);

        final DfConnectionMetaInfo metaInfo = getConnectionMetaInfo();
        final String productDisp = metaInfo != null ? " (" + metaInfo.getProductDisp() + ")" : "";
        final String databaseType = getBasicProperties().getTargetDatabase() + productDisp;
        sb.append(ln).append("  DBFLUTE_CLIENT: {" + getBasicProperties().getProjectName() + "}");
        sb.append(ln).append("    database  = " + databaseType);
        sb.append(ln).append("    language  = " + getBasicProperties().getTargetLanguage());
        sb.append(ln).append("    container = " + getBasicProperties().getTargetContainerName());
        sb.append(ln).append("    package   = " + getBasicProperties().getPackageBase());
        sb.append(ln);
        sb.append(ln).append("  DBFLUTE_ENVIRONMENT_TYPE: {" + environmentType + "}");
        sb.append(ln).append("    driver = " + _driver);
        sb.append(ln).append("    url    = " + _url);
        sb.append(ln).append("    schema = " + _mainSchema);
        sb.append(ln).append("    user   = " + _user);
        sb.append(ln).append("    props  = " + _connectionProperties);

        final String additionalSchemaDisp = buildAdditionalSchemaDisp();
        sb.append(ln).append("    additionalSchema = " + additionalSchemaDisp);
        final DfReplaceSchemaProperties replaceSchemaProp = getProperties().getReplaceSchemaProperties();
        sb.append(ln).append("    repsEnvType      = " + replaceSchemaProp.getRepsEnvType());
        final String refreshProjectDisp = buildRefreshProjectDisp();
        sb.append(ln).append("    refreshProject   = " + refreshProjectDisp);

        final String finalInformation = getFinalInformation();
        if (finalInformation != null) {
            sb.append(ln).append(ln);
            sb.append(finalInformation);
        }
        sb.append(ln).append("_/_/_/_/_/_/_/_/_/_/" + " {" + getDisplayTaskName() + "}");
        DfDBFluteTaskUtil.logFinalMessage(sb.toString());
    }

    protected String buildAdditionalSchemaDisp() {
        final DfDatabaseProperties databaseProp = getDatabaseProperties();
        final List<UnifiedSchema> additionalSchemaList = databaseProp.getAdditionalSchemaList();
        String disp;
        if (additionalSchemaList.size() == 1) {
            final UnifiedSchema unifiedSchema = additionalSchemaList.get(0);
            final String identifiedSchema = unifiedSchema.getIdentifiedSchema();
            disp = identifiedSchema;
            if (unifiedSchema.isCatalogAdditionalSchema()) {
                disp = disp + "(catalog)";
            } else if (unifiedSchema.isMainSchema()) { // should NOT be true
                disp = disp + "(main)";
            } else if (unifiedSchema.isUnknownSchema()) { // should NOT be true
                disp = disp + "(unknown)";
            }
        } else {
            final StringBuilder sb = new StringBuilder();
            for (UnifiedSchema unifiedSchema : additionalSchemaList) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                final String identifiedSchema = unifiedSchema.getIdentifiedSchema();
                sb.append(identifiedSchema);
                if (unifiedSchema.isCatalogAdditionalSchema()) {
                    sb.append("(catalog)");
                } else if (unifiedSchema.isMainSchema()) { // should NOT be true
                    sb.append("(main)");
                } else if (unifiedSchema.isUnknownSchema()) { // should NOT be true
                    sb.append("(unknown)");
                }
            }
            disp = sb.toString();
        }
        return disp;
    }

    protected String buildRefreshProjectDisp() {
        final DfRefreshProperties refreshProp = getProperties().getRefreshProperties();
        if (!refreshProp.hasRefreshDefinition()) {
            return "";
        }
        final List<String> refreshProjectList = refreshProp.getProjectNameList();
        final String disp;
        if (refreshProjectList.size() == 1) {
            disp = refreshProjectList.get(0);
        } else {
            final StringBuilder sb = new StringBuilder();
            for (String refreshProject : refreshProjectList) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(refreshProject);
            }
            disp = sb.toString();
        }
        return disp;
    }

    protected String getDisplayTaskName() {
        final String taskName = getTaskName();
        return DfDBFluteTaskUtil.getDisplayTaskName(taskName);
    }

    protected String getFinalInformation() {
        return null; // as default
    }

    protected void throwTaskFailure() {
        DfDBFluteTaskUtil.throwTaskFailure(getDisplayTaskName());
    }

    protected void initializeDatabaseInfo() {
        _driver = getDatabaseProperties().getDatabaseDriver();
        _url = getDatabaseProperties().getDatabaseUrl();
        _user = getDatabaseProperties().getDatabaseUser();
        _mainSchema = getDatabaseProperties().getDatabaseSchema();
        _password = getDatabaseProperties().getDatabasePassword();
        _connectionProperties = getDatabaseProperties().getConnectionProperties();

        final ResourceContext context = new ResourceContext();
        context.setCurrentDBDef(getBasicProperties().getCurrentDBDef());
        ResourceContext.setResourceContextOnThread(context); // no need to clear because of one thread
    }

    protected void initializeVariousEnvironment() {
        if (getBasicProperties().isDatabaseOracle()) {
            // basically for data loading of ReplaceSchema
            final DBDef currentDBDef = ResourceContext.currentDBDef();
            TnValueTypes.registerBasicValueType(currentDBDef, java.util.Date.class, TnValueTypes.UTILDATE_AS_TIMESTAMP);
        }
    }

    protected abstract void doExecute();

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
    protected abstract boolean isUseDataSource();

    protected void setupDataSource() throws SQLException {
        _dataSourceHandler.setUser(_user);
        _dataSourceHandler.setPassword(_password);
        _dataSourceHandler.setDriver(_driver);
        _dataSourceHandler.setUrl(_url);
        _dataSourceHandler.setConnectionProperties(_connectionProperties);
        _dataSourceHandler.setAutoCommit(true);
        _dataSourceHandler.create();
        connectSchema();
    }

    protected void commitDataSource() throws SQLException {
        _dataSourceHandler.commit();
    }

    protected void destroyDataSource() throws SQLException {
        _dataSourceHandler.destroy();

        if (getBasicProperties().isDatabaseDerby()) {
            // Derby(Embedded) needs an original shutdown for destroying a connection
            DfDBFluteTaskUtil.shutdownIfDerbyEmbedded(_driver);
        }
    }

    protected DataSource getDataSource() {
        return DfDataSourceContext.getDataSource();
    }

    protected void connectSchema() throws SQLException {
        final DfCurrentSchemaConnector connector = new DfCurrentSchemaConnector(_mainSchema,
                getDatabaseTypeFacadeProp());
        connector.connectSchema(getDataSource());
    }

    protected DfConnectionMetaInfo getConnectionMetaInfo() {
        return _dataSourceHandler.getConnectionMetaInfo();
    }

    // -----------------------------------------------------
    //                                    Context Properties
    //                                    ------------------
    public void setContextProperties(String file) { // called by ANT
        try {
            final Properties prop = DfDBFluteTaskUtil.getBuildProperties(file, getProject());
            DfBuildProperties.getInstance().setProperties(prop);
        } catch (RuntimeException e) {
            String msg = "Failed to set context properties:";
            msg = msg + " file=" + file;
            _log.warn(msg, e); // logging because it throws to ANT world
            throw e;
        }
    }

    // ===================================================================================
    //                                                                 SQL File Collecting
    //                                                                 ===================
    /**
     * Collect SQL files the list.
     * @return The pack object for outside-SQL files. (NotNull)
     */
    protected DfOutsideSqlPack collectSqlFileList() {
        final DfOutsideSqlCollector sqlFileCollector = new DfOutsideSqlCollector();
        return sqlFileCollector.collectOutsideSql();
    }

    // ===================================================================================
    //                                                                    Refresh Resource
    //                                                                    ================
    protected void refreshResources() {
        final List<String> projectNameList = getRefreshProperties().getProjectNameList();
        new DfRefreshResourceProcess(projectNameList).refreshResources();
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
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

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    protected DfRefreshProperties getRefreshProperties() {
        return getProperties().getRefreshProperties();
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