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

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Task;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.connection.DfDataSourceCreator;
import org.seasar.dbflute.helper.jdbc.connection.DfSimpleDataSourceCreator;
import org.seasar.dbflute.helper.jdbc.context.DfDataSourceContext;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.torque.DfAntTaskUtil;

/**
 * Abstract DB meta texen task for Torque.
 * 
 * @author mkubo
 */
public abstract class DfAbstractTask extends Task {

    private static final Log _log = LogFactory.getLog(DfAbstractTask.class);

    // =========================================================================================
    //                                                                                 Attribute
    //                                                                                 =========
    /** DB driver. */
    protected String _driver = null;

    /** DB url. */
    protected String _url = null;

    /** User name. */
    protected String _userId = null;

    /** Password */
    protected String _password = null;

    protected DfDataSourceCreator _dataSourceCreator = new DfSimpleDataSourceCreator();

    // =========================================================================================
    //                                                                                  Accessor
    //                                                                                  ========
    /**
     * Set the JDBC driver to be used.
     *
     * @param driver driver class name
     */
    public void setDriver(String driver) {
        this._driver = driver;
    }

    /**
     * Set the DB connection url.
     *
     * @param url connection url
     */
    public void setUrl(String url) {
        this._url = url;
    }

    /**
     * Set the user name for the DB connection.
     *
     * @param userId database user
     */
    public void setUserId(String userId) {
        this._userId = userId;
    }

    /**
     * Set the password for the DB connection.
     *
     * @param password database password
     */
    public void setPassword(String password) {
        this._password = password;
    }

    @Override
    final public void execute() {
        try {
            if (isUseDataSource()) {
                setupDataSource();
            }
            doExecute();
            if (isUseDataSource()) {
                closingDataSource();
            }
        } catch (RuntimeException e) {
            _log.error("execute() threw the exception!", e);
            throw e;
        }
    }

    abstract protected void doExecute();

    abstract protected boolean isUseDataSource();

    protected void setupDataSource() {
        _dataSourceCreator.setUserId(_userId);
        _dataSourceCreator.setPassword(_password);
        _dataSourceCreator.setDriver(_driver);
        _dataSourceCreator.setUrl(_url);
        _dataSourceCreator.setAutoCommit(true);
        _dataSourceCreator.create();
    }

    protected void closingDataSource() {
        _dataSourceCreator.commit();
        _dataSourceCreator.destroy();
    }

    protected DataSource getDataSource() {
        return DfDataSourceContext.getDataSource();
    }

    public void setContextProperties(String file) {
        final Properties prop = DfAntTaskUtil.getBuildProperties(file, super.project);
        DfBuildProperties.getInstance().setProperties(prop);
    }

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }
}