/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfGeneratedClassPackageProperties;
import org.seasar.dbflute.task.bs.DfAbstractTexenTask;
import org.seasar.dbflute.util.DfSqlStringUtil;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * 
 * @author jflute
 */
public class DfSql2EntityTask extends DfAbstractTexenTask {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSql2EntityTask.class);

    // ===================================================================================
    //                                                                           Meta Info
    //                                                                           =========
    protected final Map<String, Map<String, DfColumnMetaInfo>> _entityInfoMap = new LinkedHashMap<String, Map<String, DfColumnMetaInfo>>();
    protected final Map<String, Object> _cursorInfoMap = new LinkedHashMap<String, Object>();
    protected final Map<String, DfParameterBeanMetaData> _pmbMetaDataMap = new LinkedHashMap<String, DfParameterBeanMetaData>();
    protected final Map<String, File> _entitySqlFileMap = new LinkedHashMap<String, File>();
    protected final Map<String, String> _columnJdbcTypeMap = new LinkedHashMap<String, String>();
    protected final Map<String, String> _exceptionInfoMap = new LinkedHashMap<String, String>();
    protected final Map<String, List<String>> _primaryKeyMap = new LinkedHashMap<String, List<String>>();

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    /**
     * The override. <br />
     * Using data source.
     */
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    /**
     * The override.
     */
    protected void doExecute() {
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
        showMethodDefinitionCandidate();
        handleException();
    }

    // ===================================================================================
    //                                                                   Executing Element
    //                                                                   =================
    /**
     * Show method definition candidate.
     */
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

    /**
     * Collect SQL files into the list.
     * 
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
     * 
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
                if (!isTargetSql(sql)) {
                    return;
                }

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

            protected boolean isTargetSql(String sql) {
                final String entityName = getEntityName(sql);
                final String parameterBeanClassDefinition = getParameterBeanClassDefinition(sql);
                return entityName != null || parameterBeanClassDefinition != null;
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
             * 
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
            protected void traceSql(String sql) {
                log4inner.info("{SQL}" + getLineSeparator() + sql);
            }
        };
    }

    /**
     * Handle exceptions in exception info map.
     */
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
        _log.warn(sb.toString());
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
        final AppData appData = new AppData(databaseType, null);
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
        col.setSize(columnSize + "," + decimalDigits);
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