package org.apache.torque.task;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.helper.TorqueBuildProperties;
import org.apache.torque.helper.jdbc.RunnerInformation;
import org.apache.torque.helper.jdbc.SqlFileFireMan;
import org.apache.torque.helper.jdbc.SqlFileGetter;
import org.apache.torque.helper.jdbc.SqlFileRunner;
import org.apache.torque.helper.jdbc.SqlFileRunnerBase;
import org.apache.torque.helper.jdbc.SqlFileRunnerExecute.SQLRuntimeException;
import org.apache.torque.task.bs.TorqueTexenTask;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author mkubo
 * @version $Revision$ $Date$
 */
public class TorqueSql2EntityTask extends TorqueTexenTask {

    private static final Log _log = LogFactory.getLog(TorqueSql2EntityTask.class);

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
    //                                                                                  MetaInfo
    //                                                                                  ========
    protected final Map<String, Map<String, String>> _entityInfoMap = new LinkedHashMap<String, Map<String, String>>();
    protected final Map<String, String> _columnJdbcTypeMap = new LinkedHashMap<String, String>();

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

        final SqlFileRunner runner = getSqlFileRunner(runInfo);
        final SqlFileFireMan fireMan = new SqlFileFireMan();
        fireMan.execute(runner, getSqlFileList());

        // /----------------------------------------------
        // Set up the encoding of templates from property.
        // -----/
        final String templateEncoding = TorqueBuildProperties.getInstance().getTemplateFileEncoding();
        Velocity.setProperty("input.encoding", templateEncoding);

        try {
            super.execute();
        } catch (Exception e) {
            _log.error("/ * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.error("super#execute() threw the exception!", e);
            _log.error("/ * * * * * * * * * /");
        }
    }

    public Context initControlContext() throws Exception {
        final Database db = new Database();
        final Map<String, Map<String, String>> entityInfoMap = _entityInfoMap;
        final Set<String> entityNameSet = entityInfoMap.keySet();
        for (String entityName : entityNameSet) {
            final Map<String, String> columnJdbcTypeMap = entityInfoMap.get(entityName);

            final Table tbl = new Table();
            tbl.setName(entityName);
            tbl.setJavaNameConvertOff();
            db.addTable(tbl);
            
            final Set<String> columnNameSet = columnJdbcTypeMap.keySet();
            for (String columnName : columnNameSet) {
                final Column col = new Column();
                col.setName(columnName);

                final String jdbcType = columnJdbcTypeMap.get(columnName);
                col.setTorqueType(TypeMap.getTorqueType(jdbcType));

                tbl.addColumn(col);
            }
        }
        final String databaseType = TorqueBuildProperties.getInstance().getDatabaseName();
        final AppData appData = new AppData(databaseType, null);
        appData.addDatabase(db);

        VelocityContext context = new VelocityContext();
        final List<AppData> dataModels = new ArrayList<AppData>();
        dataModels.add(appData);
        context.put("dataModels", dataModels);
        context.put("targetDatabase", getTargetDatabase());
        return context;
    }

    protected SqlFileRunner getSqlFileRunner(RunnerInformation runInfo) {
        return new SqlFileRunnerBase(runInfo) {
            protected void execSQL(Statement statement, String sql) {
                try {
                    final String entityName;
                    {
                        String tmp = sql;
                        final int startIndex = tmp.indexOf("--#");
                        if (startIndex < 0) {
                            return;
                        }
                        tmp = tmp.substring(startIndex + "--#".length());
                        if (tmp.indexOf("#") < 0) {
                            return;
                        }
                        entityName = tmp.substring(0, tmp.indexOf("#"));
                    }

                    final ResultSet rs = statement.executeQuery(sql);
                    _goodSqlCount++;

                    final Map<String, String> columnJdbcTypeMap = new LinkedHashMap<String, String>();
                    final ResultSetMetaData md = rs.getMetaData();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        final String columnName = md.getColumnName(i);
                        final String columnTypeName = md.getColumnTypeName(i);
                        columnJdbcTypeMap.put(columnName, columnTypeName);
                    }

                    _entityInfoMap.put(entityName, columnJdbcTypeMap);
                } catch (SQLException e) {
                    String msg = "Failed to execute: " + sql;
                    if (!_runInfo.isErrorContinue()) {
                        throw new SQLRuntimeException(msg, e);
                    }
                    _log.warn(msg, e);
                    _log.warn("" + System.getProperty("line.separator"));
                }
            }
        };
    }

    protected List<File> getSqlFileList() {
        final String sqlDirectory = "./playsql/testsql";// TODO: ;
        return new SqlFileGetter().getSqlFileList(sqlDirectory);
    }

}