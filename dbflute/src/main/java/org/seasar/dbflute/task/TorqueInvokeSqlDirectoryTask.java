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
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.torque.task.bs.TorqueTask;
import org.seasar.dbflute.TorqueBuildProperties;
import org.seasar.dbflute.helper.jdbc.RunnerInformation;
import org.seasar.dbflute.helper.jdbc.SqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.SqlFileGetter;
import org.seasar.dbflute.helper.jdbc.SqlFileRunnerExecute;

public class TorqueInvokeSqlDirectoryTask extends TorqueTask {

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

    // =========================================================================================
    //                                                                                   Execute
    //                                                                                   =======
    /**
     * Load the sql file and then execute it.
     *
     * @throws BuildException
     */
    public void execute() throws BuildException {
        final RunnerInformation runInfo = new RunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setAutoCommit(TorqueBuildProperties.getInstance().isInvokeSqlDirectoryAutoCommit());
        runInfo.setErrorContinue(TorqueBuildProperties.getInstance().isInvokeSqlDirectoryErrorContinue());
        runInfo.setRollbackOnly(TorqueBuildProperties.getInstance().isInvokeSqlDirectoryRollbackOnly());

        final SqlFileFireMan fireMan = new SqlFileFireMan();
        fireMan.execute(new SqlFileRunnerExecute(runInfo), getSqlFileList());
    }

    protected List<File> getSqlFileList() {
        final String sqlDirectory = TorqueBuildProperties.getInstance().getInvokeSqlDirectorySqlDirectory();
        return new SqlFileGetter().getSqlFileList(sqlDirectory);
    }
}
