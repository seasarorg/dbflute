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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler.DfColumnMetaInfo;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetter;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerBase;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfGeneratedClassPackageProperties;
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
        setupDataSource();

        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setEncoding(getProperties().getS2DaoAdjustmentProperties().getDaoSqlFileEncoding());

        final DfSqlFileRunner runner = createSqlFileRunner(runInfo);
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        final List<File> sqlFileList = collectSqlFileIntoList();
        fireMan.execute(runner, sqlFileList);

        fireSuperExecute();
        setupBehaviorQueryPath(sqlFileList);

        handleNotFoundResult(sqlFileList);
        handleException();
        refreshResources();
    }

    // ===================================================================================
    //                                                                   Executing Element
    //                                                                   =================
    /**
     * Collect SQL files into the list.
     * @return The list of SQL files. (NotNull)
     */
    protected List<File> collectSqlFileIntoList() {
        final String sqlDirectory = getProperties().getSql2EntityProperties().getSqlDirectory();
        final List<File> sqlFileList = collectSqlFile(sqlDirectory);
        if (!DfLanguageDependencyInfoJava.containsSrcMainJava(sqlDirectory)) {
            return sqlFileList;
        }
        final String srcMainResources = DfLanguageDependencyInfoJava.replaceSrcMainJavaToSrcMainResources(sqlDirectory);
        try {
            final List<File> resourcesSqlFileList = collectSqlFile(srcMainResources);
            sqlFileList.addAll(resourcesSqlFileList);
        } catch (Exception e) {
            _log.debug("Not found sql directory on resources: " + srcMainResources);
        }
        return sqlFileList;
    }

    protected List<File> collectSqlFile(String sqlDirectory) {
        return createSqlFileGetter().getSqlFileList(sqlDirectory);
    }

    protected DfSqlFileGetter createSqlFileGetter() {
        final DfLanguageDependencyInfo dependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return new DfSqlFileGetter() {
            @Override
            protected boolean acceptSqlFile(File file) {
                if (!dependencyInfo.isCompileTargetFile(file)) {
                    return false;
                }
                return super.acceptSqlFile(file);
            }
        };
    }

    /**
     * Create SQL file runner.
     * @param runInfo Run information. (NotNull)
     * @return SQL file runner. (NotNull)
     */
    protected DfSqlFileRunner createSqlFileRunner(DfRunnerInformation runInfo) {
        final Log log4inner = _log;

        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -  
        // Implementing SqlFileRunnerBase as inner class.
        // - - - - - - - - - -/
        final Log innerLog = _log;
        return new DfSqlFileRunnerBase(runInfo, getDataSource()) {
            protected String filterSql(String sql) {

                // TODO: @jflute - At the future....
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

                if (getProperties().getBasicProperties().isDatabaseDerby()) {
                    sql = removeBeginEndComment(sql);
                }
                return super.filterSql(sql);
            }

            protected void execSQL(Statement statement, String sql) {
                ResultSet rs = null;
                try {
                    if (isTargetEntityMakingSql(sql)) {
                        rs = statement.executeQuery(sql);
                        _goodSqlCount++;

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
                            int scale = md.getScale(i);
                            final DfColumnMetaInfo metaInfo = new DfColumnMetaInfo();
                            metaInfo.setSql2EntityTableName(sql2EntityTableName);
                            metaInfo.setColumnName(columnName);
                            metaInfo.setJdbcType(columnType);
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
                    String msg = "Failed to execute: " + sql;
                    if (!_runInfo.isErrorContinue()) {
                        throw new RuntimeException(msg, e);
                    }
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
                return entityName != null;
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
                    final String typeName = element.substring(0, nameIndex).trim();
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
        final String primaryKeyColumnNameSeparatedString = getStringBetweenBeginEndMark(sql, "--*", "*");
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

    protected String removeBeginEndComment(final String sql) {
        return DfSqlStringUtil.removeBeginEndComment(sql);
    }

    // ===================================================================================
    //                                                                     Meta Data Class
    //                                                                     ===============
    public static class DfParameterBeanMetaData {
        protected String className;
        protected String superClassName;
        protected Map<String, String> propertyNameTypeMap;
        protected Map<String, String> propertyNameOptionMap;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(className);
            sb.append(", ").append(superClassName);
            sb.append(", ").append(propertyNameTypeMap);
            sb.append(", ").append(propertyNameOptionMap);
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
    }

    // ===================================================================================
    //                                                                 Behavior Query Path
    //                                                                 ===================
    /**
     * @param sqlFileList The list of SQL file. (NotNull)
     */
    protected void setupBehaviorQueryPath(List<File> sqlFileList) {
        final String exbhvName;
        {
            final DfGeneratedClassPackageProperties prop = getProperties().getGeneratedClassPackageProperties();
            String exbhvPackage = prop.getExtendedBehaviorPackage();
            if (exbhvPackage.contains(".")) {
                exbhvPackage = exbhvPackage.substring(exbhvPackage.lastIndexOf(".") + ".".length());
            }
            exbhvName = exbhvPackage;
        }

        setupBehaviorQueryPath(_behaviorQueryPathMap, sqlFileList, exbhvName);
        reflectBehaviorQueryPath(_behaviorQueryPathMap);
    }

    /**
     * @param behaviorQueryPathMap The empty map of behavior query path. (NotNull)
     * @param sqlFileList The list of SQL file. (NotNull)
     * @param exbhvName The name of extended behavior. (NotNull)
     */
    protected void setupBehaviorQueryPath(Map<String, Map<String, String>> behaviorQueryPathMap,
            List<File> sqlFileList, String exbhvName) {
        final String exbhvMark = "/" + exbhvName + "/";
        final Pattern behaviorQueryPathPattern = Pattern.compile(".+" + exbhvMark + ".+Bhv_.+.sql$");
        for (File sqlFile : sqlFileList) {
            final String path = DfStringUtil.replace(sqlFile.getPath(), File.separator, "/");
            final Matcher matcher = behaviorQueryPathPattern.matcher(path);
            if (!matcher.matches()) {
                continue;
            }
            String simpleFileName = path.substring(path.lastIndexOf(exbhvMark) + exbhvMark.length());
            String subDirectoryPath = null;
            if (simpleFileName.contains("/")) {
                subDirectoryPath = simpleFileName.substring(0, simpleFileName.lastIndexOf("/"));
                simpleFileName = simpleFileName.substring(simpleFileName.lastIndexOf("/") + "/".length());
            }
            final int behaviorNameMarkIndex = simpleFileName.indexOf("Bhv_");
            final int behaviorNameEndIndex = behaviorNameMarkIndex + "Bhv".length();
            final int behaviorQueryPathStartIndex = behaviorNameMarkIndex + "Bhv_".length();
            final int behaviorQueryPathEndIndex = simpleFileName.lastIndexOf(".sql");
            final String entityName = simpleFileName.substring(0, behaviorNameMarkIndex);
            final String behaviorName = simpleFileName.substring(0, behaviorNameEndIndex);
            final String behaviorQueryPath = simpleFileName.substring(behaviorQueryPathStartIndex,
                    behaviorQueryPathEndIndex);
            final Map<String, String> behaviorQueryElement = new LinkedHashMap<String, String>();
            behaviorQueryElement.put("path", path);
            behaviorQueryElement.put("subDirectoryPath", subDirectoryPath);
            behaviorQueryElement.put("entityName", entityName);
            behaviorQueryElement.put("behaviorName", behaviorName);
            behaviorQueryElement.put("behaviorQueryPath", behaviorQueryPath);
            behaviorQueryPathMap.put(path, behaviorQueryElement);
        }
    }

    /**
     * @param behaviorQueryPathMap The map of behavior query path. (NotNull)
     */
    protected void reflectBehaviorQueryPath(Map<String, Map<String, String>> behaviorQueryPathMap) {
        String outputDir = getBasicProperties().getJavaDir();
        if (outputDir.endsWith("/")) {
            outputDir = outputDir.substring(0, outputDir.length() - "/".length());
        }
        final DfGeneratedClassPackageProperties prop = getProperties().getGeneratedClassPackageProperties();
        final String classFileExtension = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo()
                .getClassFileExtension();
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        final String basePrefix = getBasicProperties().getBasePrefix();
        final String bsbhvPackage = prop.getBaseBehaviorPackage();
        final String bsbhvPathBase = outputDir + "/" + DfStringUtil.replace(bsbhvPackage, ".", "/");
        final File bsbhvDir = new File(bsbhvPathBase);
        final FileFilter filefilter = new FileFilter() {
            public boolean accept(File file) {
                final String path = file.getPath();
                return path.endsWith("Bhv." + classFileExtension);
            }
        };
        if (!bsbhvDir.exists()) {
            _log.warn("The base behavior directory was not found: bsbhvDir=" + bsbhvDir);
            return;
        }

        final List<File> bsbhvFileList = Arrays.asList(bsbhvDir.listFiles(filefilter));
        final Map<String, File> bsbhvFileMap = new HashMap<String, File>();
        for (File bsbhvFile : bsbhvFileList) {
            String path = bsbhvFile.getPath();
            path = path.substring(0, path.lastIndexOf("." + classFileExtension));
            final String bsbhvSimpleName;
            if (path.contains("/")) {
                bsbhvSimpleName = path.substring(path.lastIndexOf("/") + "/".length());
            } else {
                bsbhvSimpleName = path;
            }
            final String behaviorName = removeBasePrefix(bsbhvSimpleName, projectPrefix, basePrefix);
            bsbhvFileMap.put(behaviorName, bsbhvFile);
        }

        final Map<File, Map<String, Map<String, String>>> reflectResourceMap = new HashMap<File, Map<String, Map<String, String>>>();
        final Set<String> keySet = behaviorQueryPathMap.keySet();
        for (String key : keySet) {
            final Map<String, String> behaviorQueryElementMap = behaviorQueryPathMap.get(key);
            final String behaviorName = behaviorQueryElementMap.get("behaviorName");
            final String behaviorQueryPath = behaviorQueryElementMap.get("behaviorQueryPath");
            final File bsbhvFile = bsbhvFileMap.get(behaviorName);
            if (bsbhvFile == null) {
                throwBehaviorNotFoundException(behaviorQueryElementMap);
            }
            Map<String, Map<String, String>> resourceElementMap = reflectResourceMap.get(bsbhvFile);
            if (resourceElementMap == null) {
                resourceElementMap = new LinkedHashMap<String, Map<String, String>>();
                reflectResourceMap.put(bsbhvFile, resourceElementMap);
            }
            if (!resourceElementMap.containsKey(behaviorQueryPath)) {
                resourceElementMap.put(behaviorQueryPath, behaviorQueryElementMap);
            }
        }
        handleReflectResource(reflectResourceMap);
    }

    protected void throwBehaviorNotFoundException(Map<String, String> behaviorQueryElementMap) {
        final String path = behaviorQueryElementMap.get("path");
        final String behaviorName = behaviorQueryElementMap.get("behaviorName");
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The behavior was Not Found!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the existence of the behavior." + getLineSeparator();
        msg = msg + "And confirm your SQL file name." + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Not Found Behavior]" + getLineSeparator() + behaviorName + getLineSeparator();
        msg = msg + "[Your SQL File]" + getLineSeparator() + path + getLineSeparator();
        msg = msg + "* * * * * * * * * */" + getLineSeparator();
        throw new BehaviorNotFoundException(msg);
    }

    protected static class BehaviorNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public BehaviorNotFoundException(String msg) {
            super(msg);
        }
    }

    /**
     * @param reflectResourceMap The map of reflect resource. (NotNull)
     */
    protected void handleReflectResource(Map<File, Map<String, Map<String, String>>> reflectResourceMap) {
        _log.info(" ");
        _log.info("[Behavior Query Path]");
        final Set<File> fileKeySet = reflectResourceMap.keySet();
        for (File bsbhvFile : fileKeySet) {
            final Map<String, Map<String, String>> resourceElementMap = reflectResourceMap.get(bsbhvFile);
            writeBehaviorQueryPath(bsbhvFile, resourceElementMap);
        }
        _log.info(" ");
    }

    /**
     * @param bsbhvFile The file of base behavior. (NotNull)
     * @param resourceElementMap The map of resource element. (NotNull) 
     */
    protected void writeBehaviorQueryPath(File bsbhvFile, Map<String, Map<String, String>> resourceElementMap) {
        final String encoding = getBasicProperties().getTemplateFileEncoding();
        final BufferedReader bufferedReader;
        try {
            bufferedReader = new java.io.BufferedReader(new InputStreamReader(new FileInputStream(bsbhvFile), encoding));
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: encoding=" + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "The file of base behavior was Not Found: bsbhvFile=" + bsbhvFile;
            throw new IllegalStateException(msg, e);
        }
        final String path = bsbhvFile.getPath();
        if (path.contains("/")) {
            _log.info(path.substring(path.lastIndexOf("/") + "/".length()));
        } else {
            _log.info(path);
        }
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        final String behaviorQueryPathBeginMark = getBasicProperties().getBehaviorQueryPathBeginMark();
        final String behaviorQueryPathEndMark = getBasicProperties().getBehaviorQueryPathEndMark();
        String lineString = null;
        final StringBuilder sb = new StringBuilder();
        try {
            boolean targetArea = false;
            boolean done = false;
            while (true) {
                lineString = bufferedReader.readLine();
                if (lineString == null) {
                    break;
                }
                if (targetArea) {
                    if (lineString.contains(behaviorQueryPathEndMark)) {
                        targetArea = false;
                    } else {
                        continue;
                    }
                }
                sb.append(lineString).append("\n");
                if (!done && lineString.contains(behaviorQueryPathBeginMark)) {
                    targetArea = true;
                    final Set<String> behaviorQueryPathSet = resourceElementMap.keySet();
                    for (String behaviorQueryPath : behaviorQueryPathSet) {
                        final Map<String, String> behaviorQueryElementMap = resourceElementMap.get(behaviorQueryPath);
                        final StringBuilder definitionLineSb = new StringBuilder();
                        definitionLineSb
                                .append(lineString.substring(0, lineString.indexOf(behaviorQueryPathBeginMark)));
                        definitionLineSb.append(grammarInfo.getPublicStaticDefinition());
                        final String subDirectoryPath = behaviorQueryElementMap.get("subDirectoryPath");
                        if (subDirectoryPath != null) {
                            final String subDirectoryName = DfStringUtil.replace(subDirectoryPath, "/", "_");
                            final String subDirectoryValue = DfStringUtil.replace(subDirectoryPath, "/", ":");
                            definitionLineSb.append(" String PATH_");
                            definitionLineSb.append(subDirectoryName).append("_").append(behaviorQueryPath);
                            definitionLineSb.append(" = \"");
                            definitionLineSb.append(subDirectoryValue).append(":").append(behaviorQueryPath);
                            definitionLineSb.append("\";");
                        } else {
                            definitionLineSb.append(" String PATH_").append(behaviorQueryPath);
                            definitionLineSb.append(" = \"").append(behaviorQueryPath).append("\";");
                        }
                        _log.info(definitionLineSb);
                        definitionLineSb.append("\n");
                        sb.append(definitionLineSb);
                    }
                    done = true;
                }
            }
            if (!done) {
                _log.info("  --> The mark of behavior query path was Not Found!");
            }
        } catch (IOException e) {
            String msg = "bufferedReader.readLine() threw the exception: current line=" + lineString;
            throw new IllegalStateException(msg, e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ignored) {
                _log.warn(ignored.getMessage());
            }
        }

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bsbhvFile), encoding));
            bufferedWriter.write(sb.toString());
            bufferedWriter.flush();
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: encoding=" + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "The file of base behavior was not found: bsbhvFile=" + bsbhvFile;
            throw new IllegalStateException(msg, e);
        } catch (IOException e) {
            String msg = "bufferedWriter.write() threw the exception: bsbhvFile=" + bsbhvFile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException ignored) {
                    _log.warn(ignored.getMessage());
                }
            }
        }
    }

    protected String removeBasePrefix(String simpleClassName, String projectPrefix, String basePrefix) {
        final String prefix = projectPrefix + basePrefix;
        if (!simpleClassName.startsWith(prefix)) {
            return simpleClassName;
        }
        final int prefixLength = prefix.length();
        if (!Character.isUpperCase(simpleClassName.substring(prefixLength).charAt(0))) {
            return simpleClassName;
        }
        if (simpleClassName.length() <= prefixLength) {
            return simpleClassName;
        }
        return projectPrefix + simpleClassName.substring(prefixLength);
    }

    // ===================================================================================
    //                                                                       Task Override
    //                                                                       =============
    public Context initControlContext() throws Exception {
        final Database db = new Database();
        db.setPmbMetaDataMap(_pmbMetaDataMap);

        final Set<String> entityNameSet = _entityInfoMap.keySet();
        for (String entityName : entityNameSet) {
            final Map<String, DfColumnMetaInfo> columnJdbcTypeMap = _entityInfoMap.get(entityName);

            final Table tbl = new Table();
            tbl.setName(entityName);
            tbl.setupNeedsJavaNameConvertFalse();
            tbl.setupNeedsJavaBeansRulePropertyNameConvertFalse();
            tbl.setSql2EntityTypeSafeCursor(_cursorInfoMap.get(entityName) != null);
            db.addTable(tbl);
            _log.info(entityName + " --> " + tbl.getName() + " : " + tbl.getJavaName() + " : "
                    + tbl.getUncapitalisedJavaName());

            final Set<String> columnNameSet = columnJdbcTypeMap.keySet();
            for (String columnName : columnNameSet) {
                final Column col = new Column();
                setupColumnName(columnName, col);
                setupPrimaryKey(entityName, columnName, col);
                setupTorqueType(columnJdbcTypeMap, columnName, col);
                setupColumnSizeContainsDigit(columnJdbcTypeMap, columnName, col);
                setupSql2EntitySecondTableName(columnJdbcTypeMap, columnName, col);

                tbl.addColumn(col);
                _log.info("   " + (col.isPrimaryKey() ? "*" : " ") + columnName + " --> " + col.getName() + " : "
                        + col.getJavaName() + " : " + col.getUncapitalisedJavaName());
            }
            _log.info("");
        }
        final String databaseType = getBasicProperties().getDatabaseName();
        final AppData appData = new AppData(databaseType);
        appData.addDatabase(db);

        VelocityContext context = createVelocityContext(appData);
        return context;
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

    protected void setupTorqueType(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap, String columnName,
            final Column col) {
        final DfColumnMetaInfo columnMetaInfo = columnJdbcTypeMap.get(columnName);
        final String columnTorqueType = getColumnTorqueType(columnMetaInfo);
        col.setTorqueType(columnTorqueType);
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
        final String sql2EntityTableName = metaInfo.getSql2EntityableName();
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
}