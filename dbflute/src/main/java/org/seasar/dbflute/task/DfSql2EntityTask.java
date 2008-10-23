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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.NameFactory;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfProcedureHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfProcedureHandler.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.DfProcedureHandler.DfProcedureColumnType;
import org.seasar.dbflute.helper.jdbc.metadata.DfProcedureHandler.DfProcedureMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSQLExecutionFailureException;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerBase;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.logic.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.factory.DfJdbcDeterminerFactory;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfGeneratedClassPackageProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.properties.DfS2jdbcProperties;
import org.seasar.dbflute.task.bs.DfAbstractTexenTask;
import org.seasar.dbflute.util.DfSqlStringUtil;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class DfSql2EntityTask extends DfAbstractTexenTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSql2EntityTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, Map<String, DfColumnMetaInfo>> _entityInfoMap = new LinkedHashMap<String, Map<String, DfColumnMetaInfo>>();
    protected final Map<String, Object> _cursorInfoMap = new LinkedHashMap<String, Object>();
    protected final Map<String, DfParameterBeanMetaData> _pmbMetaDataMap = new LinkedHashMap<String, DfParameterBeanMetaData>();
    protected final Map<String, File> _entitySqlFileMap = new LinkedHashMap<String, File>();
    protected final Map<String, Map<String, String>> _behaviorQueryPathMap = new LinkedHashMap<String, Map<String, String>>();
    protected final Map<String, String> _columnJdbcTypeMap = new LinkedHashMap<String, String>();
    protected final Map<String, String> _exceptionInfoMap = new LinkedHashMap<String, String>();
    protected final Map<String, List<String>> _primaryKeyMap = new LinkedHashMap<String, List<String>>();
    protected final Map<String, DfProcedureMetaInfo> _procedureMap = new LinkedHashMap<String, DfProcedureMetaInfo>();

    // ===================================================================================
    //                                                                          DataSource
    //                                                                          ==========
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        final DfS2jdbcProperties jdbcProperties = getProperties().getS2jdbcProperties();
        if (jdbcProperties.hasS2jdbcDefinition()) {
            _log.info("* * * * * * * * * *");
            _log.info("* Process S2JDBC  *");
            _log.info("* * * * * * * * * *");
            setControlTemplate("om/java/other/s2jdbc/s2jdbc-sql2entity-Control.vm");
        }
        if (!getBasicProperties().isTargetLanguageMain()) {
            final String language = getBasicProperties().getTargetLanguage();
            _log.info("* * * * * * * * * *");
            _log.info("* Process " + language + "     *");
            _log.info("* * * * * * * * * *");
            setControlTemplate("om/" + language + "/sql2entity-Control-" + language + ".vm");
        }
        setupDataSource();

        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setEncoding(getProperties().getS2DaoAdjustmentProperties().getDaoSqlFileEncoding());

        final DfSqlFileRunner runner = createSqlFileRunner(runInfo);
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        final List<File> sqlFileList = collectSqlFileList();
        fireMan.execute(runner, sqlFileList);

        setupProcedure();

        fireSuperExecute();
        setupBehaviorQueryPath(sqlFileList);
        showSkippedFileInformation();
        handleNotFoundResult(sqlFileList);
        handleException();
        refreshResources();
    }

    // ===================================================================================
    //                                                                   Executing Element
    //                                                                   =================
    /**
     * Create SQL file runner.
     * @param runInfo Run information. (NotNull)
     * @return SQL file runner. (NotNull)
     */
    protected DfSqlFileRunner createSqlFileRunner(DfRunnerInformation runInfo) {
        final Log log4inner = _log;
        final DfJdbcDeterminer jdbcDeterminer = createJdbcDeterminer();

        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -  
        // Implementing SqlFileRunnerBase as inner class.
        // - - - - - - - - - -/
        final Log innerLog = _log;
        return new DfSqlFileRunnerBase(runInfo, getDataSource()) {

            /**
             * Filter the string of SQL. Resolve JDBC dependency.
             * @param sql The string of SQL. (NotNull)
             * @return The filtered string of SQL. (NotNull)
             */
            protected String filterSql(String sql) {
                if (!jdbcDeterminer.isBlockCommentValid()) {
                    sql = removeBlockComment(sql);
                }
                // The line comment is special mark on Sql2Entity
                // so this timing to do is bad because the special mark is removed.
                // if (!jdbcDeterminer.isLineCommentValid()) {
                //     sql = removeLineComment(sql);
                // }
                return super.filterSql(sql);
            }

            protected void execSQL(Statement statement, String sql) {
                ResultSet rs = null;
                try {
                    boolean alreadyIncrementGoodSqlCount = false;
                    if (isTargetEntityMakingSql(sql)) {
                        final String executedActuallySql;
                        if (!jdbcDeterminer.isLineCommentValid()) { // The timing to remove line comment is here!
                            executedActuallySql = removeLineComment(sql);
                        } else {
                            executedActuallySql = sql;
                        }
                        rs = statement.executeQuery(executedActuallySql);

                        _goodSqlCount++;
                        alreadyIncrementGoodSqlCount = true;

                        final Map<String, DfColumnMetaInfo> columnJdbcTypeMap = new LinkedHashMap<String, DfColumnMetaInfo>();
                        final ResultSetMetaData md = rs.getMetaData();
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String sql2EntityTableName = null;
                            try {
                                sql2EntityTableName = md.getTableName(i);
                            } catch (SQLException ignored) {
                                // Because this table name is not required. This is for classification.
                                String msg = "ResultSetMetaData.getTableName(" + i + ") threw the exception:";
                                msg = msg + " " + ignored.getMessage();
                                _log.info(msg);
                            }
                            String columnName = md.getColumnLabel(i);
                            if (columnName == null || columnName.trim().length() == 0) {
                                columnName = md.getColumnName(i);
                            }
                            if (columnName == null || columnName.trim().length() == 0) {
                                final String lineSeparator = System.getProperty("line.separator");
                                String msg = "The columnName is invalid: columnName=" + columnName + lineSeparator;
                                msg = msg + "ResultSetMetaData returned invalid value." + lineSeparator;
                                msg = msg + "sql=" + sql;
                                throw new IllegalArgumentException(msg);
                            }
                            final int columnType = md.getColumnType(i);
                            int columnSize = md.getPrecision(i);
                            if (columnSize <= 0) {// Example: sum(COLUMN)
                                columnSize = md.getColumnDisplaySize(i);
                            }
                            final String columnTypeName = md.getColumnTypeName(i);
                            int scale = md.getScale(i);

                            final DfColumnMetaInfo metaInfo = new DfColumnMetaInfo();
                            metaInfo.setSql2EntityTableName(sql2EntityTableName);
                            metaInfo.setColumnName(columnName);
                            metaInfo.setJdbcType(columnType);
                            metaInfo.setDbTypeName(columnTypeName);
                            metaInfo.setColumnSize(columnSize);
                            metaInfo.setDecimalDigits(scale);
                            columnJdbcTypeMap.put(columnName, metaInfo);
                        }

                        // for Customize Entity
                        final String entityName = getEntityName(sql);
                        if (entityName != null) {
                            _entityInfoMap.put(entityName, columnJdbcTypeMap);
                            if (isCursor(sql)) {
                                _cursorInfoMap.put(entityName, new Object());
                            }
                            _entitySqlFileMap.put(entityName, _srcFile);
                            _primaryKeyMap.put(entityName, getPrimaryKeyColumnNameList(sql));
                        }
                    }
                    if (isTargetParameterBeanMakingSql(sql)) {
                        if (!alreadyIncrementGoodSqlCount) {
                            _goodSqlCount++;
                        }

                        // for Parameter Bean
                        final DfParameterBeanMetaData parameterBeanMetaData = extractParameterBeanMetaData(sql);
                        if (parameterBeanMetaData != null) {
                            final String parameterBeanMetaDataKey = parameterBeanMetaData.getClassName();
                            if (_pmbMetaDataMap.containsKey(parameterBeanMetaDataKey)) {
                                final String lineSeparator = System.getProperty("line.separator");
                                String msg = "Waning!" + lineSeparator;
                                msg = msg + "* * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + lineSeparator;
                                msg = msg + "The meta data of parameter-bean already bean registered." + lineSeparator;
                                msg = msg + "It overrides the old one by NEW parameter-bean: name="
                                        + parameterBeanMetaDataKey + lineSeparator;
                                msg = msg + "- - - - - - - - - - -" + lineSeparator;
                                msg = msg + " sql=" + sql + lineSeparator;
                                msg = msg + "* * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + lineSeparator;
                                innerLog.warn(msg);
                            }
                            _pmbMetaDataMap.put(parameterBeanMetaDataKey, parameterBeanMetaData);
                        }
                    }
                } catch (SQLException e) {
                    if (!_runInfo.isErrorContinue()) {
                        String msg = "Look! Read the message below." + getLineSeparator();
                        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *"
                                + getLineSeparator();
                        msg = msg + "It failed to execute the SQL!" + getLineSeparator();
                        msg = msg + getLineSeparator();
                        msg = msg + "[SQL File]" + getLineSeparator() + _srcFile + getLineSeparator();
                        msg = msg + getLineSeparator();
                        msg = msg + "[Executed SQL]" + getLineSeparator() + sql + getLineSeparator();
                        msg = msg + getLineSeparator();
                        msg = msg + "[SQLState]" + getLineSeparator() + e.getSQLState() + getLineSeparator();
                        msg = msg + getLineSeparator();
                        msg = msg + "[ErrorCode]" + getLineSeparator() + e.getErrorCode() + getLineSeparator();
                        msg = msg + getLineSeparator();
                        msg = msg + "[SQLException]" + getLineSeparator() + e.getClass().getName() + getLineSeparator();
                        msg = msg + e.getMessage() + getLineSeparator();
                        SQLException nextException = e.getNextException();
                        if (nextException != null) {
                            msg = msg + getLineSeparator();
                            msg = msg + "[NextException]" + getLineSeparator() + nextException.getClass().getName()
                                    + getLineSeparator();
                            msg = msg + nextException.getMessage() + getLineSeparator();
                        }
                        msg = msg + "* * * * * * * * * */";
                        throw new DfSQLExecutionFailureException(msg, e);
                    }
                    _log.warn("Failed to execute: " + sql, e);
                    _exceptionInfoMap.put(_srcFile.getName(), e.getMessage() + getLineSeparator() + sql);
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                            log4inner.warn("Ignored exception: " + ignored.getMessage());
                        }
                    }
                }
            }

            protected boolean isTargetEntityMakingSql(String sql) {
                final String entityName = getEntityName(sql);
                if (entityName == null) {
                    return false;
                }
                if ("df:x".equalsIgnoreCase(entityName)) {// Non Target Making SQL!
                    return false;
                }
                return true;
            }

            protected boolean isTargetParameterBeanMakingSql(String sql) {
                final String parameterBeanClassDefinition = getParameterBeanClassDefinition(sql);
                return parameterBeanClassDefinition != null;
            }

            /**
             * Extract the meta data of parameter bean.
             * @param sql Target SQL. (NotNull and NotEmpty)
             * @return the meta data of parameter bean. (Nullable: If it returns null, it means 'Not Found'.)
             */
            protected DfParameterBeanMetaData extractParameterBeanMetaData(String sql) {
                final String classDefinition = getParameterBeanClassDefinition(sql);
                if (classDefinition == null) {
                    return null;
                }
                final DfParameterBeanMetaData pmbMetaData = new DfParameterBeanMetaData();
                {
                    final String delimiter = "extends";
                    final int idx = classDefinition.indexOf(delimiter);
                    if (idx < 0) {
                        pmbMetaData.setClassName(classDefinition);
                    } else {
                        final String className = classDefinition.substring(0, idx).trim();
                        pmbMetaData.setClassName(className);
                        final String superClassName = classDefinition.substring(idx + delimiter.length()).trim();
                        pmbMetaData.setSuperClassName(superClassName);
                        resolveSuperClassSimplePagingBean(pmbMetaData);
                    }
                }

                final Map<String, String> propertyNameTypeMap = new LinkedHashMap<String, String>();
                final Map<String, String> propertyNameOptionMap = new LinkedHashMap<String, String>();
                pmbMetaData.setPropertyNameTypeMap(propertyNameTypeMap);
                pmbMetaData.setPropertyNameOptionMap(propertyNameOptionMap);
                final List<String> parameterBeanElement = getParameterBeanProperties(sql);
                for (String element : parameterBeanElement) {
                    final String nameDelimiter = " ";
                    final int nameDelimiterLength = nameDelimiter.length();
                    final String optionDelimiter = ":";
                    final int optionDelimiterLength = optionDelimiter.length();
                    element = element.trim();
                    final int nameIndex;
                    if (optionDelimiterLength >= 0) {
                        nameIndex = element.lastIndexOf(nameDelimiter.substring(0, optionDelimiterLength));
                    } else {
                        nameIndex = element.lastIndexOf(nameDelimiter);
                    }
                    if (nameIndex <= 0) {
                        String msg = "The parameter bean element should be [typeName propertyName].";
                        msg = msg + " But: element=" + element;
                        msg = msg + " srcFile=" + _srcFile;
                        throw new IllegalStateException(msg);
                    }
                    final String typeName = resolvePackageName(element.substring(0, nameIndex).trim());
                    final String rearString = element.substring(nameIndex + nameDelimiterLength).trim();
                    final int optionIndex = rearString.indexOf(":");
                    if (optionIndex == 0) {
                        String msg = "The parameter bean element should be [typeName propertyName:option].";
                        msg = msg + " But: element=" + element;
                        msg = msg + " srcFile=" + _srcFile;
                        throw new IllegalStateException(msg);
                    }
                    if (optionIndex > 0) {
                        final String propertyName = rearString.substring(0, optionIndex).trim();
                        propertyNameTypeMap.put(propertyName, typeName);
                        final String optionName = rearString.substring(optionIndex + optionDelimiterLength).trim();
                        propertyNameOptionMap.put(propertyName, optionName);
                    } else {
                        final String propertyName = rearString;
                        propertyNameTypeMap.put(propertyName, typeName);
                    }
                }
                return pmbMetaData;
            }

            protected void resolveSuperClassSimplePagingBean(final DfParameterBeanMetaData pmbMetaData) {
                if (pmbMetaData.getSuperClassName().equalsIgnoreCase("SPB")) {
                    final DfGeneratedClassPackageProperties pkgProp = getProperties()
                            .getGeneratedClassPackageProperties();
                    final String baseCommonPackage = pkgProp.getBaseCommonPackage();
                    final String projectPrefix = getBasicProperties().getProjectPrefix();
                    final DfBasicProperties basicProperties = getProperties().getBasicProperties();
                    final DfLanguageDependencyInfo languageDependencyInfo = basicProperties.getLanguageDependencyInfo();
                    final String cbeanPackageName = languageDependencyInfo.getConditionBeanPackageName();
                    final String spbName = "SimplePagingBean";
                    pmbMetaData.setSuperClassName(baseCommonPackage + "." + cbeanPackageName + "." + projectPrefix
                            + spbName);
                }
            }

            protected String resolvePackageName(String typeName) {// [DBFLUTE-271]
                if (typeName == null) {
                    return typeName;
                }
                final DfBasicProperties prop = getBasicProperties();
                if (prop.isTargetLanguageJava()) {
                    if (typeName.startsWith("List<") && typeName.endsWith(">")) {
                        return "java.util." + typeName;
                    }
                    if (typeName.startsWith("Map<") && typeName.endsWith(">")) {
                        return "java.util." + typeName;
                    }
                    if (typeName.equals("BigDecimal")) {
                        return "java.math." + typeName;
                    }
                    if (typeName.equals("Time")) {
                        return "java.sql." + typeName;
                    }
                    if (typeName.equals("Timestamp")) {
                        return "java.sql." + typeName;
                    }
                    if (typeName.equals("Date")) {
                        return "java.util." + typeName;
                    }
                } else if (prop.isTargetLanguageCSharp()) {
                    if (typeName.startsWith("IList<") && typeName.endsWith(">")) {
                        return "System.Collections.Generic." + typeName;
                    }
                }
                return typeName;
            }

            @Override
            protected String replaceCommentQuestionMarkIfNeeds(String line) {
                if (line.indexOf("--!!") >= 0 || line.indexOf("-- !!") >= 0) {
                    // If the line comment is for a property of parameter-bean, 
                    // it does not replace question mark.
                    return line;
                }
                return super.replaceCommentQuestionMarkIfNeeds(line);
            }

            @Override
            protected boolean isTargetSql(String sql) {
                final String entityName = getEntityName(sql);
                final String parameterBeanClassDefinition = getParameterBeanClassDefinition(sql);

                // No Pmb and Non Target Entity --> Non Target
                if (parameterBeanClassDefinition == null && entityName != null && "df:x".equalsIgnoreCase(entityName)) {
                    return false;
                }

                return entityName != null || parameterBeanClassDefinition != null;
            }

            @Override
            protected void traceSql(String sql) {
                log4inner.info("{SQL}" + getLineSeparator() + sql);
            }

            @Override
            protected void traceResult(int goodSqlCount, int totalSqlCount) {
                if (totalSqlCount > 0) {
                    _log.info("  --> success=" + goodSqlCount + " failure=" + (totalSqlCount - goodSqlCount));
                } else {
                    _log.info("  --> SQL for sql2entity was Not Found in the SQL file!");
                }
            }

            @Override
            protected boolean isSqlTrimAndRemoveLineSeparator() {
                return false;
            }
        };
    }

    protected void handleNotFoundResult(List<File> sqlFileList) {
        if (_entityInfoMap.isEmpty() && _pmbMetaDataMap.isEmpty()) {
            _log.warn(" ");
            _log.warn("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.warn("SQL for sql2entity was Not Found!");
            _log.warn("- - - - - - - - - -");
            _log.warn("SQL Files: " + sqlFileList.size());
            int index = 0;
            for (File file : sqlFileList) {
                index++;
                _log.warn("  " + index + " -- " + file);
            }
            _log.warn("* * * * * * * * * */");
            _log.warn(" ");
        }
    }

    protected void handleException() {
        if (_exceptionInfoMap.isEmpty()) {
            return;
        }
        final Set<String> nameSet = _exceptionInfoMap.keySet();
        final StringBuilder sb = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator");
        for (String name : nameSet) {
            final String exceptionInfo = _exceptionInfoMap.get(name);

            sb.append(lineSeparator);
            sb.append("[" + name + "]");
            sb.append(exceptionInfo);
        }
        _log.warn(" ");
        _log.warn("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        _log.warn(sb.toString());
        _log.warn("* * * * * * * * * */");
        _log.warn(" ");
    }

    // ===================================================================================
    //                                                                           Analyzing
    //                                                                           =========
    protected String getEntityName(final String sql) {
        return getTargetString(sql, "#");
    }

    protected boolean isCursor(final String sql) {
        final String targetString = getTargetString(sql, "+");
        return targetString != null && (targetString.contains("cursor") || targetString.contains("cursol"));
    }

    protected String getParameterBeanClassDefinition(final String sql) {
        return getTargetString(sql, "!");
    }

    protected List<String> getParameterBeanProperties(final String sql) {
        return getTargetList(sql, "!!");
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

    protected List<String> getPrimaryKeyColumnNameList(final String sql) {
        if (sql == null || sql.trim().length() == 0) {
            String msg = "The sql is invalid: " + sql;
            throw new IllegalArgumentException(msg);
        }
        final List<String> retLs = new ArrayList<String>();
        String primaryKeyColumnNameSeparatedString = getStringBetweenBeginEndMark(sql, "--*", "*");
        if (primaryKeyColumnNameSeparatedString == null || primaryKeyColumnNameSeparatedString.trim().length() == 0) {
            primaryKeyColumnNameSeparatedString = getStringBetweenBeginEndMark(sql, "-- *", "*");// for MySQL.
        }
        if (primaryKeyColumnNameSeparatedString != null && primaryKeyColumnNameSeparatedString.trim().length() != 0) {
            final StringTokenizer st = new StringTokenizer(primaryKeyColumnNameSeparatedString, ",;/\t");
            while (st.hasMoreTokens()) {
                final String nextToken = st.nextToken();
                retLs.add(nextToken.trim());
            }
        }
        return retLs;
    }

    protected String getStringBetweenBeginEndMark(String targetStr, String beginMark, String endMark) {
        return DfStringUtil.getStringBetweenBeginEndMark(targetStr, beginMark, endMark);
    }

    protected List<String> getListBetweenBeginEndMark(String targetStr, String beginMark, String endMark) {
        return DfStringUtil.getListBetweenBeginEndMark(targetStr, beginMark, endMark);
    }

    protected String removeBlockComment(final String sql) {
        return DfSqlStringUtil.removeBlockComment(sql);
    }

    protected String removeLineComment(final String sql) {
        return DfSqlStringUtil.removeLineComment(sql); // With removing CR
    }

    // ===================================================================================
    //                                                                     Meta Data Class
    //                                                                     ===============
    public static class DfParameterBeanMetaData {
        protected String className;
        protected String superClassName;
        protected Map<String, String> propertyNameTypeMap;
        protected Map<String, String> propertyNameOptionMap;
        protected Map<String, String> propertyNameColumnNameMap; // Only when this is for procedure
        protected String procedureName; // Only when this is for procedure

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(className);
            sb.append(", ").append(superClassName);
            sb.append(", ").append(propertyNameTypeMap);
            sb.append(", ").append(propertyNameOptionMap);
            sb.append(", ").append(procedureName);
            return sb.toString();
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSuperClassName() {
            return superClassName;
        }

        public void setSuperClassName(String superClassName) {
            this.superClassName = superClassName;
        }

        public Map<String, String> getPropertyNameTypeMap() {
            return propertyNameTypeMap;
        }

        public void setPropertyNameTypeMap(Map<String, String> propertyNameTypeMap) {
            this.propertyNameTypeMap = propertyNameTypeMap;
        }

        public Map<String, String> getPropertyNameOptionMap() {
            return propertyNameOptionMap;
        }

        public void setPropertyNameOptionMap(Map<String, String> propertyNameOptionMap) {
            this.propertyNameOptionMap = propertyNameOptionMap;
        }

        public Map<String, String> getPropertyNameColumnNameMap() {
            return propertyNameColumnNameMap;
        }

        public void setPropertyNameColumnNameMap(Map<String, String> propertyNameColumnNameMap) {
            this.propertyNameColumnNameMap = propertyNameColumnNameMap;
        }

        public String getProcedureName() {
            return procedureName;
        }

        public void setProcedureName(String procedureName) {
            this.procedureName = procedureName;
        }
    }

    // ===================================================================================
    //                                                                           Procedure
    //                                                                           =========
    protected void setupProcedure() {
        try {
            doSetupProcedure();
        } catch (SQLException ignored) {
            _log.info("/* * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.info(ignored.getMessage());
            _log.info("* * * * * * * * * */");
        }
    }

    protected void doSetupProcedure() throws SQLException {
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        if (!outsideSqlProperties.isGenerateProcedureParameterBean()) {
            return;
        }
        final List<DfProcedureMetaInfo> procedures = getProcedures();
        _log.info(" ");
        _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        for (DfProcedureMetaInfo metaInfo : procedures) {
            final String procedureCatalog = metaInfo.getProcedureCatalog();
            if (!outsideSqlProperties.isTargetProcedureCatalog(procedureCatalog)) {
                continue;
            }
            final String procedureSchema = metaInfo.getProcedureSchema();
            if (!outsideSqlProperties.isTargetProcedureSchema(procedureSchema)) {
                continue;
            }
            final String procedureName = metaInfo.getProcedureName();
            if (!outsideSqlProperties.isTargetProcedureName(procedureName)) {
                continue;
            }

            _procedureMap.put(procedureName, metaInfo);

            final DfParameterBeanMetaData parameterBeanMetaData = new DfParameterBeanMetaData();
            final Map<String, String> propertyNameTypeMap = new LinkedHashMap<String, String>();
            final Map<String, String> propertyNameOptionMap = new LinkedHashMap<String, String>();
            final Map<String, String> propertyNameColumnNameMap = new LinkedHashMap<String, String>();
            final List<DfProcedureColumnMetaInfo> procedureColumnMetaInfoList = metaInfo
                    .getProcedureColumnMetaInfoList();
            int index = 0;
            final String pmbName = convertProcedureNameToPmbName(procedureName);
            final String procedureSqlName = buildProcedureSqlName(metaInfo);

            _log.info("[" + pmbName + "]: " + procedureSqlName + " // " + metaInfo.getProcedureType());
            if (procedureColumnMetaInfoList.isEmpty()) {
                _log.info("    *No Parameter");
            }
            if (_pmbMetaDataMap.containsKey(pmbName)) {
                _log.info("    *It was found the same name of parameter bean, so skip and continue!");
                continue;
            }
            for (DfProcedureColumnMetaInfo columnMetaInfo : procedureColumnMetaInfoList) {
                String columnName = columnMetaInfo.getColumnName();
                if (columnName == null || columnName.trim().length() == 0) {
                    columnName = "arg" + (index + 1);
                }
                columnName = filterColumnNameAboutVendorDependency(columnName);
                final String propertyName;
                {
                    propertyName = convertColumnNameToPropertyName(columnName);
                }
                final String propertyType = getProcedureColumnPropertyType(columnMetaInfo);
                propertyNameTypeMap.put(propertyName, propertyType);

                DfProcedureColumnType procedureColumnType = columnMetaInfo.getProcedureColumnType();
                propertyNameOptionMap.put(propertyName, procedureColumnType.toString());

                propertyNameColumnNameMap.put(propertyName, columnName);

                String msg = "    " + propertyType + " " + propertyName + ";";
                msg = msg + " // " + columnMetaInfo.getProcedureColumnType();
                msg = msg + "(" + columnMetaInfo.getJdbcType() + ", " + columnMetaInfo.getDbTypeName() + ")";
                _log.info(msg);
                ++index;
            }
            parameterBeanMetaData.setClassName(pmbName);
            parameterBeanMetaData.setPropertyNameTypeMap(propertyNameTypeMap);
            parameterBeanMetaData.setPropertyNameOptionMap(propertyNameOptionMap);
            parameterBeanMetaData.setPropertyNameColumnNameMap(propertyNameColumnNameMap);
            parameterBeanMetaData.setProcedureName(procedureSqlName);
            _pmbMetaDataMap.put(pmbName, parameterBeanMetaData);
        }
        _log.info("= = = = = = = = = =/");
        _log.info(" ");
    }

    protected List<DfProcedureMetaInfo> getProcedures() throws SQLException {
        final DatabaseMetaData metaData = getDataSource().getConnection().getMetaData();
        final DfProcedureHandler handler = new DfProcedureHandler();
        final List<DfProcedureMetaInfo> procedures = handler.getProcedures(metaData, _schema);
        final List<String> additionalSchemaList = getBasicProperties().getAdditionalSchemaList();
        for (String additionalSchema : additionalSchemaList) {
            final List<DfProcedureMetaInfo> additionalProcedureList = handler.getProcedures(metaData, additionalSchema);
            for (DfProcedureMetaInfo metaInfo : additionalProcedureList) {
                final String procedureSchema = metaInfo.getProcedureSchema();
                if (procedureSchema == null || procedureSchema.trim().length() == 0) {
                    metaInfo.setProcedureSchema(additionalSchema);
                }
            }
            procedures.addAll(additionalProcedureList);
        }
        return procedures;
    }

    protected String getProcedureColumnPropertyType(DfProcedureColumnMetaInfo procedureColumnMetaInfo) {
        final String propertyType;
        if (isResultSetProperty(procedureColumnMetaInfo)) {
            final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
            propertyType = grammarInfo.getGenericMapListClassName("String", "Object");
        } else {
            final int jdbcType = procedureColumnMetaInfo.getJdbcType();
            final String dbTypeName = procedureColumnMetaInfo.getDbTypeName();
            final Integer columnSize = procedureColumnMetaInfo.getColumnSize();
            final Integer decimalDigits = procedureColumnMetaInfo.getDecimalDigits();
            final String torqueType = new DfColumnHandler().getColumnTorqueType(jdbcType, dbTypeName);
            propertyType = TypeMap.findJavaNativeString(torqueType, columnSize, decimalDigits);
        }
        return propertyType;
    }

    protected boolean isResultSetProperty(DfProcedureColumnMetaInfo procedureColumnMetaInfo) {
        final int jdbcType = procedureColumnMetaInfo.getJdbcType();
        final String dbTypeName = procedureColumnMetaInfo.getDbTypeName();
        if (getBasicProperties().isDatabaseOracle()) {
            return jdbcType == Types.OTHER && dbTypeName != null && dbTypeName.toLowerCase().contains("cursor");
        } else if (getBasicProperties().isDatabasePostgreSQL()) {
            return jdbcType == Types.OTHER && dbTypeName != null && dbTypeName.toLowerCase().contains("refcursor");
        } else {
            return false;
        }
    }

    protected String convertProcedureNameToPmbName(String procedureName) {
        procedureName = filterProcedureName4PmbNameAboutVendorDependency(procedureName);
        if (procedureName.contains("_")) {
            procedureName = generateCapitalisedJavaName(procedureName.toUpperCase());
        } else {
            boolean allUpperCase = isAllUpperCase(procedureName);
            procedureName = StringUtils.capitalise(allUpperCase ? procedureName.toLowerCase() : procedureName);
        }
        return procedureName + "Pmb";
    }

    protected String filterProcedureName4PmbNameAboutVendorDependency(String procedureName) {
        // Because SQLServer returns 'Abc;1'.
        if (getBasicProperties().isDatabaseSqlServer() && procedureName.contains(";")) {
            procedureName = procedureName.substring(0, procedureName.indexOf(";"));
        }
        return procedureName;
    }

    protected String filterColumnNameAboutVendorDependency(String columnName) {
        // Because SQLServer returns '@returnValue'.
        if (getBasicProperties().isDatabaseSqlServer() && columnName.startsWith("@")) {
            columnName = columnName.substring("@".length());
        }
        return columnName;
    }

    protected String convertColumnNameToPropertyName(String columnName) {
        if (columnName.contains("_")) {
            columnName = generateUncapitalisedJavaName(columnName.toUpperCase());
        } else {
            boolean allUpperCase = isAllUpperCase(columnName);
            columnName = StringUtils.uncapitalise(allUpperCase ? columnName.toLowerCase() : columnName);
        }
        return columnName;
    }

    protected String buildProcedureSqlName(DfProcedureMetaInfo metaInfo) {
        final String procedureCatalog = metaInfo.getProcedureCatalog();
        final StringBuilder sb = new StringBuilder();
        final String procedureSchema = metaInfo.getProcedureSchema();
        if (procedureSchema != null && procedureSchema.trim().length() > 0) {
            final List<String> additionalSchemaList = getBasicProperties().getAdditionalSchemaList();
            if (additionalSchemaList.contains(procedureSchema)) {
                sb.append(procedureSchema).append(".");
            }
        }
        if (procedureCatalog != null && procedureCatalog.trim().length() > 0) {
            if (getBasicProperties().isDatabaseOracle()) { // needs to confirm other DB.
                sb.append(procedureCatalog).append(".");
            }
        }
        final String procedureName = metaInfo.getProcedureName();
        return sb.append(procedureName).toString();
    }

    protected boolean isAllUpperCase(String name) {
        char[] charArray = name.toCharArray();
        boolean allUpperCase = true;
        for (char ch : charArray) {
            if (Character.isLowerCase(ch)) {
                allUpperCase = false;
                break;
            }
        }
        return allUpperCase;
    }

    protected String generateUncapitalisedJavaName(String name) {
        return StringUtils.uncapitalise(NameFactory.generateJavaNameByMethodUnderscore(name));
    }

    protected String generateCapitalisedJavaName(String name) {
        return StringUtils.capitalise(NameFactory.generateJavaNameByMethodUnderscore(name));
    }

    // ===================================================================================
    //                                                                 Behavior Query Path
    //                                                                 ===================
    /**
     * @param sqlFileList The list of SQL file. (NotNull)
     */
    protected void setupBehaviorQueryPath(List<File> sqlFileList) {
        final DfBehaviorQueryPathSetupper setupper = new DfBehaviorQueryPathSetupper(getProperties());
        setupper.setupBehaviorQueryPath(sqlFileList);
    }

    // ===================================================================================
    //                                                                       Task Override
    //                                                                       =============
    public Context initControlContext() throws Exception {
        final Database database = new Database();
        database.setPmbMetaDataMap(_pmbMetaDataMap);

        final Set<String> entityNameSet = _entityInfoMap.keySet();
        for (String entityName : entityNameSet) {
            final Map<String, DfColumnMetaInfo> columnJdbcTypeMap = _entityInfoMap.get(entityName);

            final Table tbl = new Table();
            tbl.setName(entityName);
            tbl.setupNeedsJavaNameConvertFalse();
            tbl.setupNeedsJavaBeansRulePropertyNameConvertFalse();
            tbl.setSql2EntityTypeSafeCursor(_cursorInfoMap.get(entityName) != null);
            database.addTable(tbl);
            _log.info(entityName + " --> " + tbl.getName() + " : " + tbl.getJavaName() + " : "
                    + tbl.getUncapitalisedJavaName());

            final boolean allCommonColumn = hasAllCommonColumn(columnJdbcTypeMap);
            final Set<String> columnNameSet = columnJdbcTypeMap.keySet();
            for (String columnName : columnNameSet) {
                final Column column = new Column();
                setupColumnName(columnName, column);
                setupPrimaryKey(entityName, columnName, column);
                setupTorqueType(columnJdbcTypeMap, columnName, column, allCommonColumn);
                setupDbType(columnJdbcTypeMap, columnName, column);
                setupColumnSizeContainsDigit(columnJdbcTypeMap, columnName, column);
                setupSql2EntitySecondTableName(columnJdbcTypeMap, columnName, column);

                tbl.addColumn(column);
                _log.info("   " + (column.isPrimaryKey() ? "*" : " ") + columnName + " --> " + column.getName() + " : "
                        + column.getJavaName() + " : " + column.getUncapitalisedJavaName());
            }
            _log.info("");
        }
        final String databaseType = getBasicProperties().getDatabaseName();
        final AppData appData = new AppData(databaseType);
        appData.addDatabase(database);

        VelocityContext context = createVelocityContext(appData);
        return context;
    }

    protected boolean hasAllCommonColumn(Map<String, DfColumnMetaInfo> columnJdbcTypeMap) {
        Map<String, Object> commonColumnMap = getCommonColumnMap();
        if (commonColumnMap.isEmpty()) {
            return false;
        }
        DfFlexibleNameMap<String, DfColumnMetaInfo> flexibleColumnJdbcTypeMap = newFlexibleNameMap(columnJdbcTypeMap);
        Set<String> commonColumnSet = commonColumnMap.keySet();
        for (String commonColumnName : commonColumnSet) {
            if (!flexibleColumnJdbcTypeMap.containsKey(commonColumnName)) {
                return false; // Not All!
            }
        }
        return true;
    }

    protected void setupColumnName(String columnName, final Column col) {
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
    }

    protected void setupPrimaryKey(String entityName, String columnName, final Column col) {
        final List<String> primaryKeyList = _primaryKeyMap.get(entityName);
        col.setPrimaryKey(primaryKeyList.contains(columnName));
    }

    protected void setupTorqueType(Map<String, DfColumnMetaInfo> columnJdbcTypeMap, String columnName, Column column,
            boolean allCommonColumn) {
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // If the select columns have common columns, 
        // The types of common column are set up from common column properties.
        // - - - - - - - - - -/
        if (allCommonColumn) {
            final String commonColumnTorqueType = getCommonColumnTorqueType(columnName);
            if (commonColumnTorqueType != null) {
                column.setTorqueType(commonColumnTorqueType);
                return;
            }
        }
        final DfColumnMetaInfo columnMetaInfo = columnJdbcTypeMap.get(columnName);
        final String columnTorqueType = getColumnTorqueType(columnMetaInfo);
        column.setTorqueType(columnTorqueType);
    }

    protected void setupDbType(Map<String, DfColumnMetaInfo> columnJdbcTypeMap, String columnName, Column column) {
        final DfColumnMetaInfo columnMetaInfo = columnJdbcTypeMap.get(columnName);
        column.setDbType(columnMetaInfo.getDbTypeName());
    }

    protected String getCommonColumnTorqueType(String columnName) {
        DfFlexibleNameMap<String, Object> flexibleNameMap = newFlexibleNameMap(getCommonColumnMap());
        return (String) flexibleNameMap.get(columnName);
    }

    protected Map<String, Object> getCommonColumnMap() {
        DfCommonColumnProperties prop = getProperties().getCommonColumnProperties();
        return prop.getCommonColumnMap();
    }

    protected String getColumnTorqueType(final DfColumnMetaInfo columnMetaInfo) {
        final DfColumnHandler columnHandler = new DfColumnHandler();
        return columnHandler.getColumnTorqueType(columnMetaInfo);
    }

    protected void setupColumnSizeContainsDigit(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap,
            String columnName, final Column col) {
        final DfColumnMetaInfo metaInfo = columnJdbcTypeMap.get(columnName);
        final int columnSize = metaInfo.getColumnSize();
        final int decimalDigits = metaInfo.getDecimalDigits();
        col.setColumnSize(columnSize + "," + decimalDigits);
    }

    protected void setupSql2EntitySecondTableName(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap,
            String columnName, final Column col) {
        final DfColumnMetaInfo metaInfo = columnJdbcTypeMap.get(columnName);
        final String sql2EntityTableName = metaInfo.getSql2EntityTableName();
        col.setSql2EntityTableName(sql2EntityTableName);
    }

    protected VelocityContext createVelocityContext(final AppData appData) {
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
            for (char ch : columnCharArray) {
                // If the character is not number and not upper case...
                if (!Character.isDigit(ch) && !Character.isUpperCase(ch)) {
                    return false;
                }
            }
            return true;// All characters are upper case!
        } else {
            return true;// Contains connector character!
        }
    }

    protected <KEY, VALUE> DfFlexibleNameMap<KEY, VALUE> newFlexibleNameMap(Map<KEY, VALUE> map) {
        return new DfFlexibleNameMap<KEY, VALUE>(map);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DfJdbcDeterminer createJdbcDeterminer() {
        return new DfJdbcDeterminerFactory(getBasicProperties()).createJdbcDeterminer();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    public String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    public String getSlashPath(File file) {
        return replaceString(file.getPath(), getFileSeparator(), "/");
    }

    public String getFileSeparator() {
        return File.separator;
    }
}