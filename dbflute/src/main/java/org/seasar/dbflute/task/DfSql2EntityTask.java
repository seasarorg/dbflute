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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.NameFactory;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfSpecifiedSqlFile;
import org.seasar.dbflute.exception.DfCustomizeEntityDuplicateException;
import org.seasar.dbflute.exception.DfParameterBeanDuplicateException;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.friends.torque.DfSchemaXmlReader;
import org.seasar.dbflute.friends.velocity.DfVelocityContextFactory;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerBase;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.logic.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.factory.DfJdbcDeterminerFactory;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfProcedureHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.logic.outsidesql.DfOutsideSqlMarkAnalyzer;
import org.seasar.dbflute.logic.outsidesql.DfSqlFileNameResolver;
import org.seasar.dbflute.logic.pkgresolver.DfStandardApiPackageResolver;
import org.seasar.dbflute.logic.pmb.DfParameterBeanMetaData;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
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

    protected DfColumnHandler _columnHandler = new DfColumnHandler();
    protected DfOutsideSqlMarkAnalyzer _markAnalyzer = new DfOutsideSqlMarkAnalyzer();

    // for getting schema
    protected String _schemaXml;
    protected AppData _schemaData;

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
        setupControlTemplate();
        setupSchemaInformation();

        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setEncoding(getProperties().getOutsideSqlProperties().getSqlFileEncoding());

        final DfSqlFileRunner runner = createSqlFileRunner(runInfo);
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        final List<File> sqlFileList = getTargetSqlFileList();
        fireMan.execute(runner, sqlFileList);

        setupProcedure();

        fireSuperExecute();
        setupBehaviorQueryPath();

        showTargetSqlFileInformation(sqlFileList);
        showSkippedFileInformation();
        handleNotFoundResult(sqlFileList);
        handleException();
        refreshResources();
    }

    protected void setupSchemaInformation() {
        final DfSchemaXmlReader schemaFileReader = createSchemaFileReader();
        try {
            schemaFileReader.read();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        _schemaData = schemaFileReader.getSchemaData();
    }

    protected DfSchemaXmlReader createSchemaFileReader() {
        return new DfSchemaXmlReader(_schemaXml, getProject(), getTargetDatabase());
    }

    protected void setupControlTemplate() {
        final DfLittleAdjustmentProperties littleProp = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        if (littleProp.isAlternateSql2EntityControlValid()) {
            _log.info("* * * * * * * * * * * * * * *");
            _log.info("* Process Alternate Control *");
            _log.info("* * * * * * * * * * * * * * *");
            final String control = littleProp.getAlternateSql2EntityControl();
            _log.info("...Using alternate control: " + control);
            setControlTemplate(control);
            return;
        }
        if (getBasicProperties().isTargetLanguageMain()) {
            if (getBasicProperties().isTargetLanguageJava()) {
                _log.info("* * * * * * * * *");
                _log.info("* Process Java  *");
                _log.info("* * * * * * * * *");
                final String control = "om/ControlSql2EntityJava.vm";
                _log.info("...Using Java control: " + control);
                setControlTemplate(control);
            } else if (getBasicProperties().isTargetLanguageCSharp()) {
                _log.info("* * * * * * * * * *");
                _log.info("* Process CSharp  *");
                _log.info("* * * * * * * * * *");
                final String control = "om/ControlSql2EntityCSharp.vm";
                _log.info("...Using CSharp control: " + control);
                setControlTemplate(control);
            } else {
                String msg = "Unknown Main Language: " + getBasicProperties().getTargetLanguage();
                throw new IllegalStateException(msg);
            }
        } else {
            final String language = getBasicProperties().getTargetLanguage();
            _log.info("* * * * * * * * * *");
            _log.info("* Process " + language + "    *");
            _log.info("* * * * * * * * * *");
            final String control = "om/" + language + "/sql2entity-Control-" + language + ".vm";
            _log.info("...Using " + language + " control: " + control);
            setControlTemplate(control);
        }
    }

    // ===================================================================================
    //                                                                   Executing Element
    //                                                                   =================
    protected List<File> getTargetSqlFileList() {
        final List<File> sqlFileList = collectSqlFileList();
        final String specifiedSqlFile = DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile();
        if (specifiedSqlFile != null) {
            final List<File> filteredList = new ArrayList<File>();
            for (File sqlFile : sqlFileList) {
                final String fileName = sqlFile.getName();
                if (specifiedSqlFile.equals(fileName)) {
                    filteredList.add(sqlFile);
                }
            }
            return filteredList;
        } else {
            return sqlFileList;
        }
    }

    /**
     * Create SQL file runner.
     * @param runInfo Run information. (NotNull)
     * @return SQL file runner. (NotNull)
     */
    protected DfSqlFileRunner createSqlFileRunner(DfRunnerInformation runInfo) {
        final Log log4inner = _log;
        final DfJdbcDeterminer jdbcDeterminer = createJdbcDeterminer();
        final DfStandardApiPackageResolver packageResolver = new DfStandardApiPackageResolver(getBasicProperties());

        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -  
        // Implementing SqlFileRunnerBase as inner class.
        // - - - - - - - - - -/
        return new DfSqlFileRunnerBase(runInfo, getDataSource()) {

            /**
             * Filter the string of SQL. Resolve JDBC dependency.
             * @param sql The string of SQL. (NotNull)
             * @return The filtered string of SQL. (NotNull)
             */
            @Override
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

            @Override
            protected void execSQL(String sql) {
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
                        checkStatement(sql);
                        rs = _currentStmt.executeQuery(executedActuallySql);

                        _goodSqlCount++;
                        alreadyIncrementGoodSqlCount = true;

                        final DfFlexibleMap<String, String> columnJavaNativeMap = createColumnJavaNativeMap(sql);
                        final Map<String, DfColumnMetaInfo> columnJdbcTypeMap = new LinkedHashMap<String, DfColumnMetaInfo>();
                        final ResultSetMetaData md = rs.getMetaData();
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            final DfColumnMetaInfo metaInfo = new DfColumnMetaInfo();

                            String sql2EntityRelatedTableName = null;
                            try {
                                sql2EntityRelatedTableName = md.getTableName(i);
                            } catch (SQLException ignored) {
                                // Because this table name is not required. This is for classification.
                                String msg = "ResultSetMetaData.getTableName(" + i + ") threw the exception:";
                                msg = msg + " " + ignored.getMessage();
                                _log.info(msg);
                            }
                            metaInfo.setSql2EntityRelatedTableName(sql2EntityRelatedTableName);

                            String columnName = md.getColumnLabel(i);
                            String relatedColumnName = md.getColumnName(i);
                            metaInfo.setSql2EntityRelatedColumnName(relatedColumnName);
                            if (columnName == null || columnName.trim().length() == 0) {
                                columnName = relatedColumnName;
                            }
                            if (columnName == null || columnName.trim().length() == 0) {
                                final String ln = ln();
                                String msg = "The columnName is invalid: columnName=" + columnName + ln;
                                msg = msg + "ResultSetMetaData returned invalid value." + ln;
                                msg = msg + "sql=" + sql;
                                throw new IllegalArgumentException(msg);
                            }
                            metaInfo.setColumnName(columnName);

                            final int columnType = md.getColumnType(i);
                            metaInfo.setJdbcDefValue(columnType);

                            final String columnTypeName = md.getColumnTypeName(i);
                            metaInfo.setDbTypeName(columnTypeName);

                            int columnSize = md.getPrecision(i);
                            if (!DfColumnHandler.isColumnSizeValid(columnSize)) {
                                // ex) sum(COLUMN)
                                columnSize = md.getColumnDisplaySize(i);
                            }
                            metaInfo.setColumnSize(columnSize);

                            final int scale = md.getScale(i);
                            metaInfo.setDecimalDigits(scale);

                            final String sql2entityForcedJavaNative = columnJavaNativeMap.get(columnName);
                            metaInfo.setSql2EntityForcedJavaNative(sql2entityForcedJavaNative);

                            columnJdbcTypeMap.put(columnName, metaInfo);
                        }

                        // for Customize Entity
                        String entityName = getCustomizeEntityName(sql);
                        if (entityName != null) {
                            entityName = resolveEntityNameIfNeeds(entityName, _sqlFile);
                            assertDuplicateEntity(entityName, _sqlFile);
                            _entityInfoMap.put(entityName, columnJdbcTypeMap);
                            if (isCursor(sql)) {
                                _cursorInfoMap.put(entityName, new Object());
                            }
                            _entitySqlFileMap.put(entityName, _sqlFile);
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
                            final String pmbName = parameterBeanMetaData.getClassName();
                            assertDuplicateParameterBean(pmbName, _sqlFile);
                            _pmbMetaDataMap.put(pmbName, parameterBeanMetaData);
                        }
                    }
                } catch (SQLException e) {
                    if (!_runInfo.isErrorContinue()) {
                        String msg = "Look! Read the message below." + ln();
                        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
                        msg = msg + "It failed to execute the SQL!" + ln();
                        msg = msg + ln();
                        msg = msg + "[SQL File]" + ln() + _sqlFile + ln();
                        msg = msg + ln();
                        msg = msg + "[Executed SQL]" + ln() + sql + ln();
                        msg = msg + ln();
                        msg = msg + "[SQLState]" + ln() + e.getSQLState() + ln();
                        msg = msg + ln();
                        msg = msg + "[ErrorCode]" + ln() + e.getErrorCode() + ln();
                        msg = msg + ln();
                        msg = msg + "[SQLException]" + ln() + e.getClass().getName() + ln();
                        msg = msg + e.getMessage() + ln();
                        SQLException nextException = e.getNextException();
                        if (nextException != null) {
                            msg = msg + ln();
                            msg = msg + "[NextException]" + ln() + nextException.getClass().getName() + ln();
                            msg = msg + nextException.getMessage() + ln();
                        }
                        msg = msg + "* * * * * * * * * */";
                        throw new SQLFailureException(msg, e);
                    }
                    _log.warn("Failed to execute: " + sql, e);
                    _exceptionInfoMap.put(_sqlFile.getName(), e.getMessage() + ln() + sql);
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

            protected DfFlexibleMap<String, String> createColumnJavaNativeMap(String sql) {
                final List<String> entityPropertyTypeList = getEntityPropertyTypeList(sql);
                final DfFlexibleMap<String, String> columnJavaNativeMap = new DfFlexibleMap<String, String>();
                for (String element : entityPropertyTypeList) {
                    final String nameDelimiter = " ";
                    final int nameDelimiterLength = nameDelimiter.length();
                    element = element.trim();
                    final int nameIndex = element.lastIndexOf(nameDelimiter);
                    if (nameIndex <= 0) {
                        String msg = "The customize entity element should be [typeName columnName].";
                        msg = msg + " But: element=" + element;
                        msg = msg + " srcFile=" + _sqlFile;
                        throw new IllegalStateException(msg);
                    }
                    final String typeName = resolvePackageName(element.substring(0, nameIndex).trim());
                    final String columnName = element.substring(nameIndex + nameDelimiterLength).trim();
                    columnJavaNativeMap.put(columnName, typeName);
                }
                return columnJavaNativeMap;
            }

            protected boolean isTargetEntityMakingSql(String sql) {
                final String entityName = getCustomizeEntityName(sql);
                if (entityName == null) {
                    return false;
                }
                if ("df:x".equalsIgnoreCase(entityName)) {// Non Target Making SQL!
                    return false;
                }
                return true;
            }

            protected boolean isTargetParameterBeanMakingSql(String sql) {
                final String parameterBeanName = getParameterBeanName(sql);
                return parameterBeanName != null;
            }

            /**
             * Extract the meta data of parameter bean.
             * @param sql Target SQL. (NotNull and NotEmpty)
             * @return the meta data of parameter bean. (Nullable: If it returns null, it means 'Not Found'.)
             */
            protected DfParameterBeanMetaData extractParameterBeanMetaData(String sql) {
                final String parameterBeanName = getParameterBeanName(sql);
                if (parameterBeanName == null) {
                    return null;
                }
                final DfParameterBeanMetaData pmbMetaData = new DfParameterBeanMetaData();
                {
                    final String delimiter = "extends";
                    final int idx = parameterBeanName.indexOf(delimiter);
                    {
                        String className = (idx >= 0) ? parameterBeanName.substring(0, idx) : parameterBeanName;
                        className = className.trim();
                        className = resolvePmbNameIfNeeds(className, _sqlFile);
                        pmbMetaData.setClassName(className);
                    }
                    if (idx >= 0) {
                        final String superClassName = parameterBeanName.substring(idx + delimiter.length()).trim();
                        pmbMetaData.setSuperClassName(superClassName);
                        resolveSuperClassSimplePagingBean(pmbMetaData);
                    }
                }

                final Map<String, String> propertyNameTypeMap = new LinkedHashMap<String, String>();
                final Map<String, String> propertyNameOptionMap = new LinkedHashMap<String, String>();
                pmbMetaData.setPropertyNameTypeMap(propertyNameTypeMap);
                pmbMetaData.setPropertyNameOptionMap(propertyNameOptionMap);
                final List<String> parameterBeanElement = getParameterBeanPropertyTypeList(sql);
                for (String element : parameterBeanElement) {
                    final String nameDelimiter = " ";
                    final String optionDelimiter = ":";
                    element = element.trim();
                    final int optionIndex = element.indexOf(optionDelimiter);
                    final String propertyDef;
                    final String optionDef;
                    if (optionIndex > 0) {
                        propertyDef = element.substring(0, optionIndex).trim();
                        optionDef = element.substring(optionIndex + optionDelimiter.length()).trim();
                    } else {
                        propertyDef = element;
                        optionDef = null;
                    }
                    final int nameIndex = propertyDef.lastIndexOf(nameDelimiter);
                    if (nameIndex <= 0) {
                        String msg = "The parameter bean element should be [typeName propertyName].";
                        msg = msg + " But: element=" + element + " srcFile=" + _sqlFile;
                        throw new IllegalStateException(msg);
                    }
                    final String typeName = resolvePackageName(propertyDef.substring(0, nameIndex).trim());
                    final String propertyName = propertyDef.substring(nameIndex + nameDelimiter.length()).trim();
                    propertyNameTypeMap.put(propertyName, typeName);
                    if (optionDef != null) {
                        propertyNameOptionMap.put(propertyName, optionDef);
                    }
                }
                pmbMetaData.setSqlFile(_sqlFile);
                return pmbMetaData;
            }

            protected void resolveSuperClassSimplePagingBean(final DfParameterBeanMetaData pmbMetaData) {
                if (pmbMetaData.getSuperClassName().equalsIgnoreCase("SPB")) {
                    final String baseCommonPackage = getBasicProperties().getBaseCommonPackage();
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
                return packageResolver.resolvePackageName(typeName);
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
                final String entityName = getCustomizeEntityName(sql);
                final String parameterBeanClassDefinition = getParameterBeanName(sql);

                // No Pmb and Non Target Entity --> Non Target
                if (parameterBeanClassDefinition == null && entityName != null && "df:x".equalsIgnoreCase(entityName)) {
                    return false;
                }

                return entityName != null || parameterBeanClassDefinition != null;
            }

            @Override
            protected void traceSql(String sql) {
                log4inner.info("{SQL}" + ln() + sql);
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

    protected void assertDuplicateEntity(String entityName, File currentSqlFile) {
        final File sqlFile = _entitySqlFileMap.get(entityName);
        if (sqlFile == null) {
            return;
        }
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The customize entity was duplicated!" + ln();
        msg = msg + ln();
        msg = msg + "[Customize Entity]" + ln() + entityName + ln();
        msg = msg + ln();
        msg = msg + "[SQL Files]" + ln() + sqlFile + ln() + currentSqlFile + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfCustomizeEntityDuplicateException(msg);
    }

    protected void assertDuplicateParameterBean(String pmbName, File currentSqlFile) {
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(pmbName);
        if (metaData == null) {
            return;
        }
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The parameter-bean was duplicated!" + ln();
        msg = msg + ln();
        msg = msg + "[ParameterBean]" + ln() + pmbName + ln();
        msg = msg + ln();
        msg = msg + "[SQL Files]" + ln() + metaData.getSqlFile() + ln() + currentSqlFile + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfParameterBeanDuplicateException(msg);
    }

    protected void handleNotFoundResult(List<File> sqlFileList) {
        if (_entityInfoMap.isEmpty() && _pmbMetaDataMap.isEmpty()) {
            _log.warn("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.warn("SQL for sql2entity was not found!");
            _log.warn("");
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
    protected String getCustomizeEntityName(final String sql) {
        return _markAnalyzer.getCustomizeEntityName(sql);
    }

    protected boolean isCursor(final String sql) {
        return _markAnalyzer.isCursor(sql);
    }

    protected List<String> getEntityPropertyTypeList(final String sql) {
        return _markAnalyzer.getCustomizeEntityPropertyTypeList(sql);
    }

    protected String getParameterBeanName(final String sql) {
        return _markAnalyzer.getParameterBeanName(sql);
    }

    protected List<String> getParameterBeanPropertyTypeList(final String sql) {
        return _markAnalyzer.getParameterBeanPropertyTypeList(sql);
    }

    protected List<String> getPrimaryKeyColumnNameList(final String sql) {
        return _markAnalyzer.getPrimaryKeyColumnNameList(sql);
    }

    protected String removeBlockComment(final String sql) {
        return DfSqlStringUtil.removeBlockComment(sql);
    }

    protected String removeLineComment(final String sql) {
        return DfSqlStringUtil.removeLineComment(sql); // With removing CR
    }

    protected String resolveEntityNameIfNeeds(String className, File file) {
        return new DfSqlFileNameResolver().resolveEntityNameIfNeeds(className, file.getName());
    }

    protected String resolvePmbNameIfNeeds(String className, File file) {
        return new DfSqlFileNameResolver().resolvePmbNameIfNeeds(className, file.getName());
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
        final List<DfProcedureMetaInfo> procedures = getAvailableProcedures();
        _log.info(" ");
        _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        for (DfProcedureMetaInfo metaInfo : procedures) {
            final String procedureName = metaInfo.getProcedureName();
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

    protected List<DfProcedureMetaInfo> getAvailableProcedures() throws SQLException {
        Connection conn = null;
        try {
            conn = getDataSource().getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            final DfProcedureHandler handler = new DfProcedureHandler();
            final List<DfProcedureMetaInfo> procedures = handler.getAvailableProcedureList(metaData, _schema);
            return procedures;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
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
            if (getBasicProperties().isDatabaseOracle() && "number".equalsIgnoreCase(dbTypeName)) {
                // Because the length setting of procedure parameter is unsupported on Oracle.
                propertyType = TypeMap.getDefaultDecimalJavaNativeType();
            } else {
                final String torqueType = _columnHandler.getColumnJdbcType(jdbcType, dbTypeName);
                propertyType = TypeMap.findJavaNativeByJdbcType(torqueType, columnSize, decimalDigits);
            }
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
        return new DfProcedureHandler().buildProcedureSqlName(metaInfo);
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
    protected void setupBehaviorQueryPath() {
        final List<File> sqlFileList = collectSqlFileList();
        final DfBehaviorQueryPathSetupper setupper = new DfBehaviorQueryPathSetupper(getProperties());
        setupper.setupBehaviorQueryPath(sqlFileList);
    }

    // ===================================================================================
    //                                                                 Initialize Override
    //                                                                 ===================
    public Context initControlContext() throws Exception {
        final Database database = new Database();
        database.setSql2EntitySchemaData(_schemaData);
        database.setPmbMetaDataMap(_pmbMetaDataMap);
        database.setSkipDeleteOldClass(DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile() != null);

        final Set<String> entityNameSet = _entityInfoMap.keySet();
        for (String entityName : entityNameSet) {
            final Map<String, DfColumnMetaInfo> columnJdbcTypeMap = _entityInfoMap.get(entityName);

            final Table tbl = new Table();
            tbl.setName(entityName);
            tbl.setupNeedsJavaNameConvertFalse();
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
                setupColumnComment(columnJdbcTypeMap, columnName, column);
                setupSql2EntityRelatedTableName(columnJdbcTypeMap, columnName, column);
                setupSql2EntityRelatedColumnName(columnJdbcTypeMap, columnName, column);
                setupSql2EntityForcedJavaNative(columnJdbcTypeMap, columnName, column);

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
        DfFlexibleMap<String, DfColumnMetaInfo> flexibleColumnJdbcTypeMap = newFlexibleNameMap(columnJdbcTypeMap);
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
                column.setJdbcType(commonColumnTorqueType);
                return;
            }
        }
        final DfColumnMetaInfo columnMetaInfo = columnJdbcTypeMap.get(columnName);
        final String columnTorqueType = getColumnTorqueType(columnMetaInfo);
        column.setJdbcType(columnTorqueType);
    }

    protected void setupDbType(Map<String, DfColumnMetaInfo> columnJdbcTypeMap, String columnName, Column column) {
        final DfColumnMetaInfo columnMetaInfo = columnJdbcTypeMap.get(columnName);
        column.setDbType(columnMetaInfo.getDbTypeName());
    }

    protected String getCommonColumnTorqueType(String columnName) {
        DfFlexibleMap<String, Object> flexibleNameMap = newFlexibleNameMap(getCommonColumnMap());
        return (String) flexibleNameMap.get(columnName);
    }

    protected Map<String, Object> getCommonColumnMap() {
        DfCommonColumnProperties prop = getProperties().getCommonColumnProperties();
        return prop.getCommonColumnMap();
    }

    protected String getColumnTorqueType(final DfColumnMetaInfo columnMetaInfo) {
        return _columnHandler.getColumnJdbcType(columnMetaInfo);
    }

    protected void setupColumnSizeContainsDigit(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap,
            String columnName, final Column column) {
        final DfColumnMetaInfo metaInfo = columnJdbcTypeMap.get(columnName);
        final int columnSize = metaInfo.getColumnSize();
        final int decimalDigits = metaInfo.getDecimalDigits();
        column.setupColumnSize(columnSize, decimalDigits);
    }

    protected void setupColumnComment(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap, String columnName,
            final Column column) {
        final DfColumnMetaInfo metaInfo = columnJdbcTypeMap.get(columnName);
        final String sql2EntityRelatedTableName = metaInfo.getSql2EntityRelatedTableName();
        final Table relatedTable = getRelatedTable(sql2EntityRelatedTableName);
        if (relatedTable == null) {
            return;
        }
        final String relatedColumnName = metaInfo.getSql2EntityRelatedColumnName();
        final Column relatedColumn = relatedTable.getColumn(relatedColumnName);
        if (relatedColumn == null) {
            return;
        }
        final String plainComment = relatedColumn.getPlainComment();
        column.setPlainComment(plainComment);
    }

    protected void setupSql2EntityRelatedTableName(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap,
            String columnName, final Column column) {
        final DfColumnMetaInfo metaInfo = columnJdbcTypeMap.get(columnName);
        final String sql2EntityRelatedTableName = metaInfo.getSql2EntityRelatedTableName();
        final Table relatedTable = getRelatedTable(sql2EntityRelatedTableName);
        if (relatedTable == null) {
            return;
        }
        column.setSql2EntityRelatedTableName(sql2EntityRelatedTableName);
    }

    protected void setupSql2EntityRelatedColumnName(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap,
            String columnName, final Column column) {
        final DfColumnMetaInfo metaInfo = columnJdbcTypeMap.get(columnName);
        final String sql2EntityRelatedTableName = metaInfo.getSql2EntityRelatedTableName();
        final Table relatedTable = getRelatedTable(sql2EntityRelatedTableName);
        if (relatedTable == null) {
            return;
        }
        final String sql2EntityRelatedColumnName = metaInfo.getSql2EntityRelatedColumnName();
        final Column relatedColumn = relatedTable.getColumn(sql2EntityRelatedColumnName);
        if (relatedColumn == null) {
            return;
        }
        column.setSql2EntityRelatedColumnName(sql2EntityRelatedColumnName);
    }

    protected Table getRelatedTable(String sql2EntityRelatedTableName) {
        if (_schemaData == null) {
            return null;
        }
        final Table relatedTable;
        try {
            relatedTable = _schemaData.getDatabase().getTable(sql2EntityRelatedTableName);
        } catch (EngineException e) {
            String msg = "Failed to get database information: schemaData=" + _schemaData;
            throw new IllegalStateException(msg);
        }
        return relatedTable;
    }

    protected void setupSql2EntityForcedJavaNative(final Map<String, DfColumnMetaInfo> columnJdbcTypeMap,
            String columnName, final Column column) {
        final DfColumnMetaInfo metaInfo = columnJdbcTypeMap.get(columnName);
        final String sql2EntityForcedJavaNative = metaInfo.getSql2EntityForcedJavaNative();
        column.setSql2EntityForcedJavaNative(sql2EntityForcedJavaNative);
    }

    protected VelocityContext createVelocityContext(final AppData appData) {
        final DfVelocityContextFactory factory = new DfVelocityContextFactory();
        return factory.create(appData);
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

    protected <KEY, VALUE> DfFlexibleMap<KEY, VALUE> newFlexibleNameMap(Map<KEY, VALUE> map) {
        return new DfFlexibleMap<KEY, VALUE>(map);
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

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSchemaXml(String schemaXml) { // for getting schema
        _schemaXml = schemaXml;
    }

    public void setSpecifiedSqlFile(String specifiedSqlFile) {
        DfSpecifiedSqlFile.getInstance().setSpecifiedSqlFile(specifiedSqlFile);
    }
}