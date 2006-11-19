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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.seasar.dao.SqlTokenizer;
import org.seasar.dao.parser.SqlTokenizerImpl;
import org.seasar.dbflute.helper.jdbc.RunnerInformation;
import org.seasar.dbflute.helper.jdbc.SqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.SqlFileGetter;
import org.seasar.dbflute.helper.jdbc.SqlFileRunner;
import org.seasar.dbflute.helper.jdbc.SqlFileRunnerBase;
import org.seasar.dbflute.helper.jdbc.SqlFileRunnerExecute.SQLRuntimeException;
import org.seasar.dbflute.task.bs.DfAbstractTexenTask;

public class DfSql2EntityTask extends DfAbstractTexenTask {

    private static final Log _log = LogFactory.getLog(DfSql2EntityTask.class);

    // =========================================================================================
    //                                                                                 Attribute
    //                                                                                 =========
    protected String _driver = null;

    protected String _url = null;

    protected String _userId = null;

    protected String _password = null;

    // =========================================================================================
    //                                                                                  Accessor
    //                                                                                  ========
    public void setDriver(String driver) {
        this._driver = driver;
    }

    public void setUrl(String url) {
        this._url = url;
    }

    public void setUserId(String userId) {
        this._userId = userId;
    }

    public void setPassword(String password) {
        this._password = password;
    }

    // =========================================================================================
    //                                                                                  MetaInfo
    //                                                                                  ========
    protected final Map<String, Map<String, Integer>> _entityInfoMap = new LinkedHashMap<String, Map<String, Integer>>();
    protected final Map<String, File> _entitySqlFileMap = new LinkedHashMap<String, File>();
    protected final Map<String, String> _columnJdbcTypeMap = new LinkedHashMap<String, String>();
    protected final Map<String, String> _exceptionInfoMap = new LinkedHashMap<String, String>();
    protected final Map<String, List<String>> _primaryKeyMap = new LinkedHashMap<String, List<String>>();

    // =========================================================================================
    //                                                                                   Execute
    //                                                                                   =======
    public void execute() throws BuildException {
        final RunnerInformation runInfo = new RunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);

        final SqlFileRunner runner = getSqlFileRunner(runInfo);
        final SqlFileFireMan fireMan = new SqlFileFireMan();
        fireMan.execute(runner, getSqlFileList());

        fireSuperExecute();
        showMethodDefinitionCandidate();
        handleException();
    }

    protected void showMethodDefinitionCandidate() {
        _log.info("_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/");
        _log.info("                         Method Definition Candidate");
        _log.info("                        _/_/_/_/_/_/_/_/_/_/_/_/_/_/");
        final Set<String> entityNameSet = _entitySqlFileMap.keySet();
        for (String entityName : entityNameSet) {
            final File sqlFile = _entitySqlFileMap.get(entityName);
            final String sqlFileName = sqlFile.getName();
            try {
                if (sqlFileName.indexOf("_") < 0) {
                    continue;
                }
                if (sqlFileName.indexOf(".") < 0) {
                    continue;
                }

                final String daoName = sqlFileName.substring(0, sqlFileName.indexOf("_"));
                final String remainderString = sqlFileName.substring(sqlFileName.indexOf("_") + 1);
                if (remainderString.indexOf(".") < 0) {
                    continue;
                }
                final String methodName;
                if (remainderString.indexOf("_") < 0) {
                    methodName = remainderString.substring(0, remainderString.indexOf("."));
                } else {
                    methodName = remainderString.substring(0, remainderString.indexOf("_"));
                }
                _log.info("[" + daoName + "]");
                _log.info("public java.util.List<" + entityName + "> " + methodName + "();");
                _log.info(" ");
            } catch (Exception e) {
                _log.warn("showMethodDefinition() threw exception at " + sqlFileName, e);
            }
        }
    }

    protected List<File> getSqlFileList() {
        final String sqlDirectory = getProperties().getSql2EntityProperties().getSqlDirectory();
        return new SqlFileGetter().getSqlFileList(sqlDirectory);
    }

    protected SqlFileRunner getSqlFileRunner(RunnerInformation runInfo) {
        return new SqlFileRunnerBase(runInfo) {
            protected String filterSql(String sql) {
                
//                // TODO: メソッドの引数をなんとしても取る！でも型が取れないなぁ！
//                final SqlTokenizerImpl tokenizer = new SqlTokenizerImpl(sql);
//                while (true) {
//                    final int result = tokenizer.next();
//                    if (result == SqlTokenizer.EOF) {
//                        break;
//                    }
//                    if (tokenizer.getTokenType() == SqlTokenizer.COMMENT) {
//                        System.out.println("***: " + tokenizer.getToken());
//                    }
//                }
                
                return removeBeginEndComment(sql);
            }

            protected void execSQL(Statement statement, String sql) {
                final String entityName = getEntityName(sql);
                if (entityName == null) {
                    return;
                }
                ResultSet rs = null;
                try {
                    rs = statement.executeQuery(sql);
                    _goodSqlCount++;

                    final Map<String, Integer> columnJdbcTypeMap = new LinkedHashMap<String, Integer>();
                    final ResultSetMetaData md = rs.getMetaData();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        final String columnName = md.getColumnName(i);
                        if (columnName == null || columnName.trim().length() == 0) {
                            String msg = "The columnName is invalid: " + columnName;
                            msg = msg + " sql=" + sql;
                            throw new IllegalArgumentException(msg);
                        }
                        final int columnType = md.getColumnType(i);
                        columnJdbcTypeMap.put(columnName, columnType);
                    }

                    _entityInfoMap.put(entityName, columnJdbcTypeMap);
                    _entitySqlFileMap.put(entityName, _srcFile);
                    _primaryKeyMap.put(entityName, getPrimaryKeyColumnNameList(sql));
                } catch (SQLException e) {
                    String msg = "Failed to execute: " + sql;
                    if (!_runInfo.isErrorContinue()) {
                        throw new SQLRuntimeException(msg, e);
                    }
                    _exceptionInfoMap.put(entityName, e.getMessage() + System.getProperty("line.separator") + sql);
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                            _log.warn("rs.close() threw the exception!", ignored);
                        }
                    }
                }
            }

            @Override
            protected void traceSql(String sql) {
                _log.info("{SQL}" + System.getProperty("line.separator") + sql);
            }
        };
    }

    protected void handleException() {
        final Set<String> entityNameSet = _exceptionInfoMap.keySet();
        final StringBuilder sb = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator");
        for (String entityName : entityNameSet) {
            final String exceptionInfo = _exceptionInfoMap.get(entityName);

            sb.append(lineSeparator);
            sb.append("[" + entityName + "]");
            sb.append(exceptionInfo);
        }
        _log.warn(sb.toString());
    }

    protected String removeBeginEndComment(final String sql) {
        if (sql == null || sql.trim().length() == 0) {
            String msg = "The sql is invalid: " + sql;
            throw new IllegalArgumentException(msg);
        }
        final String beginMark = "/*";
        final String endMark = "*/";
        final StringBuilder sb = new StringBuilder();
        String tmp = sql;
        while (true) {
            if (tmp.indexOf(beginMark) < 0) {
                sb.append(tmp);
                break;
            }
            if (tmp.indexOf(endMark) < 0) {
                sb.append(tmp);
                _log.warn("Wrong comment is here. Begin-mark exists but End-mark doesn't: " + sql);
                break;
            }
            if (tmp.indexOf(beginMark) > tmp.indexOf(endMark)) {
                final int borderIndex = tmp.indexOf(endMark) + endMark.length();
                sb.append(tmp.substring(0, borderIndex));
                tmp = tmp.substring(borderIndex);
                _log.warn("Wrong comment is here. End-mark is beginning before Begin-mark: " + sql);
                continue;
            }
            sb.append(tmp.substring(0, tmp.indexOf(beginMark)));
            tmp = tmp.substring(tmp.indexOf(endMark) + endMark.length());
        }
        return sb.toString();
    }

    protected String getEntityName(final String sql) {
        if (sql == null || sql.trim().length() == 0) {
            String msg = "The sql is invalid: " + sql;
            throw new IllegalArgumentException(msg);
        }
        return getStringBetweenBeginEndMark(sql, "--#", "#--");
    }

    protected List<String> getPrimaryKeyColumnNameList(final String sql) {
        if (sql == null || sql.trim().length() == 0) {
            String msg = "The sql is invalid: " + sql;
            throw new IllegalArgumentException(msg);
        }
        final List<String> retLs = new ArrayList<String>();
        final String primaryKeyColumnNameSeparatedString = getStringBetweenBeginEndMark(sql, "--*", "*--");
        if (primaryKeyColumnNameSeparatedString != null && primaryKeyColumnNameSeparatedString.trim().length() != 0) {
            final StringTokenizer st = new StringTokenizer(primaryKeyColumnNameSeparatedString, ",;/\t");
            while (st.hasMoreTokens()) {
                final String nextToken = st.nextToken();
                retLs.add(nextToken.trim());
            }
        }
        return retLs;
    }

    protected String getStringBetweenBeginEndMark(final String targetStr, final String beginMark, final String endMark) {
        final String ret;
        {
            String tmp = targetStr;
            final int startIndex = tmp.indexOf(beginMark);
            if (startIndex < 0) {
                return null;
            }
            tmp = tmp.substring(startIndex + beginMark.length());
            if (tmp.indexOf(endMark) < 0) {
                return null;
            }
            ret = tmp.substring(0, tmp.indexOf(endMark)).trim();
        }
        return ret;
    }

    // =========================================================================================
    //                                                                                   Context
    //                                                                                   =======
    public Context initControlContext() throws Exception {
        final Database db = new Database();
        final Map<String, Map<String, Integer>> entityInfoMap = _entityInfoMap;
        final Set<String> entityNameSet = entityInfoMap.keySet();
        for (String entityName : entityNameSet) {
            final Map<String, Integer> columnJdbcTypeMap = entityInfoMap.get(entityName);

            final Table tbl = new Table();
            tbl.setName(entityName);
            tbl.setupNeedsJavaNameConvertFalse();
            db.addTable(tbl);
            _log.debug(entityName + " --> " + tbl.getName() + " : " + tbl.getJavaName() + " : "
                    + tbl.getUncapitalisedJavaName());

            final Set<String> columnNameSet = columnJdbcTypeMap.keySet();

            for (String columnName : columnNameSet) {
                final Column col = new Column();

                if (needsConvert(columnName)) {
                    col.setName(columnName);
                } else {
                    col.setupNeedsJavaNameConvertFalse();
                    if (columnName.length() > 1) {
                        col.setName(columnName.substring(0, 1).toUpperCase() + columnName.substring(1));
                    } else {
                        col.setName(columnName.toUpperCase());
                    }
                }

                final Integer jdbcType = columnJdbcTypeMap.get(columnName);
                col.setTorqueType(TypeMap.getTorqueType(jdbcType));

                final List<String> primaryKeyList = _primaryKeyMap.get(entityName);
                col.setPrimaryKey(primaryKeyList.contains(columnName));

                tbl.addColumn(col);
                _log.debug("   " + (col.isPrimaryKey() ? "*" : " ") + columnName + " --> " + col.getName() + " : "
                        + col.getJavaName() + " : " + col.getUncapitalisedJavaName());
            }
            _log.debug("");
        }
        final String databaseType = getBasicProperties().getDatabaseName();
        final AppData appData = new AppData(databaseType, null);
        appData.addDatabase(db);

        VelocityContext context = new VelocityContext();
        final List<AppData> dataModels = new ArrayList<AppData>();
        dataModels.add(appData);
        context.put("dataModels", dataModels);
        context.put("targetDatabase", getTargetDatabase());
        return context;
    }

    protected boolean needsConvert(String columnName) {
        if (columnName == null || columnName.trim().length() == 0) {
            String msg = "The columnName is invalid: " + columnName;
            throw new IllegalArgumentException(msg);
        }
        if (columnName.indexOf("_") < 0) {
            final char[] columnCharArray = columnName.toCharArray();
            for (char c : columnCharArray) {
                if (!Character.isUpperCase(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
}