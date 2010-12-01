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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfSpecifiedSqlFile;
import org.seasar.dbflute.exception.DfCustomizeEntityDuplicateException;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.exception.DfParameterBeanDuplicateException;
import org.seasar.dbflute.exception.DfProcedureSetupFailureException;
import org.seasar.dbflute.exception.IllegalOutsideSqlOperationException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.friends.velocity.DfVelocityContextFactory;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerBase;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityMetaExtractor;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityMetaExtractor.DfForcedJavaNativeProvider;
import org.seasar.dbflute.logic.sql2entity.outsidesql.DfOutsideSqlMarkAnalyzer;
import org.seasar.dbflute.logic.sql2entity.outsidesql.DfSqlFileNameResolver;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfProcedurePmbSetupper;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPropertyTypePackageResolver;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.task.bs.DfAbstractTexenTask;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.IndexOfInfo;

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
    // an entity name for a map key is a name not resolved about project prefix
    // (the prefix is resolved in Table class)
    protected final Map<String, DfCustomizeEntityInfo> _entityInfoMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, Object> _cursorInfoMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, DfPmbMetaData> _pmbMetaDataMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, File> _entitySqlFileMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, String> _exceptionInfoMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, List<String>> _primaryKeyMap = DfCollectionUtil.newLinkedHashMap();

    protected final DfColumnHandler _columnHandler = new DfColumnHandler();
    protected final DfOutsideSqlMarkAnalyzer _markAnalyzer = new DfOutsideSqlMarkAnalyzer();

    // for getting schema
    protected AppData _schemaData;
    // to use same process as generating here
    protected final Database _database = new Database();

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
        runInfo.setAutoCommit(false);
        runInfo.setErrorContinue(false);
        runInfo.setRollbackOnly(true); // this task does not commit
        runInfo.setEncoding(getOutsideSqlProperties().getSqlFileEncoding());

        final DfSqlFileRunner runner = createSqlFileRunner(runInfo);
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        final List<File> sqlFileList = getTargetSqlFileList();
        fireMan.execute(runner, sqlFileList);

        setupProcedure();

        fireVelocityProcess();
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
        final String filePath = getBasicProperties().getProejctSchemaXMLFilePath();
        return new DfSchemaXmlReader(filePath, getTargetDatabase());
    }

    protected void setupControlTemplate() {
        final DfLittleAdjustmentProperties littleProp = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        if (littleProp.isAlternateSql2EntityControlValid()) {
            _log.info("");
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
                _log.info("");
                _log.info("* * * * * * * * *");
                _log.info("* Process Java  *");
                _log.info("* * * * * * * * *");
                final String control = "om/ControlSql2EntityJava.vm";
                _log.info("...Using Java control: " + control);
                setControlTemplate(control);
            } else if (getBasicProperties().isTargetLanguageCSharp()) {
                _log.info("");
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
            _log.info("");
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
        final DfPropertyTypePackageResolver packageResolver = new DfPropertyTypePackageResolver();
        final DBDef currentDBDef = getBasicProperties().getCurrentDBDef();

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
                // the comments are special mark for Sql2Entity
                // so this timing to do is bad because the special mark is removed.
                //if (!currentDBDef.dbway().isBlockCommentSupported()) {
                //    sql = removeBlockComment(sql);
                //}
                //if (!currentDBDef.dbway().isLineCommentSupported()) {
                //    sql = removeLineComment(sql);
                //}
                return super.filterSql(sql);
            }

            @Override
            protected void execSQL(String sql) {
                ResultSet rs = null;
                try {
                    boolean alreadyIncrementGoodSqlCount = false;
                    if (isTargetEntityMakingSql(sql)) {
                        final String executedActuallySql;
                        {
                            // the timing to remove comments is here
                            String filtered = sql;
                            if (!currentDBDef.dbway().isBlockCommentSupported()) {
                                filtered = removeBlockComment(filtered);
                            }
                            if (!currentDBDef.dbway().isLineCommentSupported()) {
                                filtered = removeLineComment(filtered);
                            }
                            executedActuallySql = filtered;
                        }
                        checkStatement(executedActuallySql);
                        rs = _currentStatement.executeQuery(executedActuallySql);

                        _goodSqlCount++;
                        alreadyIncrementGoodSqlCount = true;

                        final Map<String, String> columnForcedJavaNativeMap = createColumnForcedJavaNativeMap(sql);
                        final DfCustomizeEntityMetaExtractor customizeEntityMetaExtractor = new DfCustomizeEntityMetaExtractor();
                        final Map<String, DfColumnMetaInfo> columnMetaInfoMap = customizeEntityMetaExtractor
                                .extractColumnMetaInfoMap(rs, sql, new DfForcedJavaNativeProvider() {
                                    public String provide(String columnName) {
                                        return columnForcedJavaNativeMap.get(columnName);
                                    }
                                });

                        // for Customize Entity
                        String entityName = getCustomizeEntityName(sql);
                        if (entityName != null) {
                            entityName = resolveEntityNameIfNeeds(entityName, _sqlFile);
                            assertDuplicateEntity(entityName, _sqlFile);
                            _entityInfoMap.put(entityName, new DfCustomizeEntityInfo(entityName, columnMetaInfoMap));
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
                        final DfPmbMetaData parameterBeanMetaData = extractParameterBeanMetaData(sql);
                        if (parameterBeanMetaData != null) {
                            final String pmbName = parameterBeanMetaData.getClassName();
                            assertDuplicateParameterBean(pmbName, _sqlFile);
                            _pmbMetaDataMap.put(pmbName, parameterBeanMetaData);
                        }
                    }
                } catch (SQLException e) {
                    if (_runInfo.isErrorContinue()) {
                        _log.warn("Failed to execute: " + sql, e);
                        _exceptionInfoMap.put(_sqlFile.getName(), e.getMessage() + ln() + sql);
                        return;
                    }
                    throwSQLFailureException(sql, e);
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

            protected Map<String, String> createColumnForcedJavaNativeMap(String sql) {
                final List<String> entityPropertyTypeList = getEntityPropertyTypeList(sql);
                final Map<String, String> columnJavaNativeMap = StringKeyMap.createAsFlexible();
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
                if ("df:x".equalsIgnoreCase(entityName)) { // non target making SQL!
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
             * @return the meta data of parameter bean. (Nullable: If it returns null, it means 'not found'.)
             */
            protected DfPmbMetaData extractParameterBeanMetaData(String sql) {
                final String parameterBeanName = getParameterBeanName(sql);
                if (parameterBeanName == null) {
                    return null;
                }
                final DfPmbMetaData pmbMetaData = new DfPmbMetaData();
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
                    final String typeName = resolvePackageNameExceptUtil(propertyDef.substring(0, nameIndex).trim());
                    final String propertyName = propertyDef.substring(nameIndex + nameDelimiter.length()).trim();
                    propertyNameTypeMap.put(propertyName, typeName);
                    if (optionDef != null) {
                        propertyNameOptionMap.put(propertyName, optionDef);
                    }
                }
                pmbMetaData.setSqlFile(_sqlFile);
                return pmbMetaData;
            }

            protected void resolveSuperClassSimplePagingBean(final DfPmbMetaData pmbMetaData) {
                final String superClassName = pmbMetaData.getSuperClassName();
                if (superClassName.equalsIgnoreCase("Paging") // main
                        || superClassName.equalsIgnoreCase("SPB")) { // an old style for compatibility before 0.9.7.5
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

            protected String resolvePackageName(String typeName) { // [DBFLUTE-271]
                return packageResolver.resolvePackageName(typeName);
            }

            protected String resolvePackageNameExceptUtil(String typeName) {
                return packageResolver.resolvePackageNameExceptUtil(typeName);
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
                    _log.info("  --> SQL for sql2entity was not found in the SQL file!");
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
        final DfPmbMetaData metaData = _pmbMetaDataMap.get(pmbName);
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
        for (String name : nameSet) {
            final String exceptionInfo = _exceptionInfoMap.get(name);
            sb.append("[" + name + "]");
            final boolean containsLn = Srl.contains(exceptionInfo, ln());
            sb.append(containsLn ? ln() : " ");
            sb.append(exceptionInfo);
            sb.append(containsLn ? ln() : "").append(ln());
        }
        _log.warn("/* * * * * * * * * * * * * * * * * {Warning Exception}");
        _log.warn(ln() + sb.toString().trim());
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
        return Srl.removeBlockComment(sql);
    }

    protected String removeLineComment(final String sql) {
        return Srl.removeLineComment(sql); // with removing CR
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
            final DfProcedurePmbSetupper setupper = createProcedurePmbSetupper();
            setupper.setupProcedure();
            _exceptionInfoMap.putAll(setupper.getContinuedFailureMessageMap());
        } catch (SQLException e) {
            throwProcedureSetupFailureException(e);
        }
    }

    protected DfProcedurePmbSetupper createProcedurePmbSetupper() {
        return new DfProcedurePmbSetupper(getDataSource(), _entityInfoMap, _pmbMetaDataMap, _database);
    }

    protected void throwProcedureSetupFailureException(SQLException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to set up procedures.");
        br.addItem("SQL Exception");
        br.addElement(DfJDBCException.extractMessage(e));
        SQLException nextEx = e.getNextException();
        if (nextEx != null) {
            br.addElement(DfJDBCException.extractMessage(nextEx));
        }
        String msg = br.buildExceptionMessage();
        throw new DfProcedureSetupFailureException(msg, e);
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
    //                                                                  Prepare Generation
    //                                                                  ==================
    @Override
    public Context initControlContext() throws Exception {
        final Database database = _database;
        database.setSql2EntitySchemaData(_schemaData);
        database.setPmbMetaDataMap(_pmbMetaDataMap);
        database.setSkipDeleteOldClass(DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile() != null);

        final Set<String> entityNameSet = _entityInfoMap.keySet();
        for (String entityName : entityNameSet) {
            final DfCustomizeEntityInfo entityInfo = _entityInfoMap.get(entityName);
            final Map<String, DfColumnMetaInfo> metaMap = entityInfo.getColumnMap();

            final Table tbl = new Table();
            tbl.setSql2EntityCustomize(true);
            tbl.setName(entityInfo.getTableDbName());
            if (!entityInfo.needsJavaNameConvert()) {
                tbl.suppressJavaNameConvert(); // basically here (except STRUCT type)
            }
            if (entityInfo.hasNestedCustomizeEntity()) {
                tbl.setSql2EntityCustomizeHasNested(true); // basically when STRUCT type
            }
            if (entityInfo.isAdditionalSchema()) {
                tbl.setUnifiedSchema(entityInfo.getAdditionalSchema()); // basically when STRUCT type
            }
            tbl.setSql2EntityTypeSafeCursor(_cursorInfoMap.get(entityName) != null);
            database.addTable(tbl);
            _log.info(entityName);

            final StringKeyMap<String> pkMap = getPrimaryKeyMap(entityName);
            final boolean allCommonColumn = hasAllCommonColumn(metaMap);
            final Set<String> columnNameSet = metaMap.keySet();
            for (String columnName : columnNameSet) {
                final Column column = new Column();
                setupColumnName(columnName, column);

                // an element removed from pkMap if true
                // and a table name related to primary key is returned
                final String pkRelatedTableName = setupPrimaryKey(pkMap, entityName, columnName, column);

                setupTorqueType(metaMap, columnName, column, allCommonColumn);
                setupDbType(metaMap, columnName, column);
                setupColumnSizeContainsDigit(metaMap, columnName, column);
                setupColumnComment(metaMap, columnName, column);
                setupSql2EntityElement(entityName, metaMap, columnName, column, pkRelatedTableName);
                tbl.addColumn(column);
            }
            if (!pkMap.isEmpty()) { // if not-removed columns exist
                throwPrimaryKeyNotFoundException(entityName, pkMap, columnNameSet);
            }
            _log.info("");
        }
        final String databaseType = getBasicProperties().getDatabaseType();
        final AppData appData = new AppData(databaseType);
        appData.addDatabase(database);

        VelocityContext context = createVelocityContext(appData);
        return context;
    }

    protected StringKeyMap<String> getPrimaryKeyMap(String entityName) {
        final StringKeyMap<String> pkMap = StringKeyMap.createAsFlexibleOrdered();
        final List<String> pkList = _primaryKeyMap.get(entityName);
        if (pkList != null) {
            for (String pk : pkList) {
                if (Srl.contains(pk, ".")) {
                    final IndexOfInfo info = Srl.indexOfFirst(pk, ".");
                    String tableName = info.substringFrontTrimmed();
                    String pkName = info.substringRearTrimmed();
                    pkMap.put(pkName, tableName);
                } else {
                    pkMap.put(pk, null); // no specified related table
                }
            }
        }
        return pkMap;
    }

    protected boolean hasAllCommonColumn(Map<String, DfColumnMetaInfo> columnJdbcTypeMap) {
        final Map<String, String> commonColumnMap = getCommonColumnMap();
        if (commonColumnMap.isEmpty()) {
            return false;
        }
        final Set<String> commonColumnSet = commonColumnMap.keySet();
        for (String commonColumnName : commonColumnSet) {
            if (!columnJdbcTypeMap.containsKey(commonColumnName)) {
                return false; // Not All!
            }
        }
        return true;
    }

    // -----------------------------------------------------
    //                                         Setup Element
    //                                         -------------
    protected void setupColumnName(String columnName, final Column col) {
        if (needsConvertToJavaName(columnName)) {
            col.setName(columnName);
        } else {
            col.setupNeedsJavaNameConvertFalse();
            col.setName(Srl.initCap(columnName));
        }
    }

    protected String setupPrimaryKey(StringKeyMap<String> pkMap, String entityName, String columnName, final Column col) {
        if (pkMap.containsKey(columnName)) {
            col.setPrimaryKey(true);
            return pkMap.remove(columnName); // returns related table
        }
        return null;
    }

    protected void setupTorqueType(Map<String, DfColumnMetaInfo> metaMap, String columnName, Column column,
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
        final DfColumnMetaInfo columnMetaInfo = metaMap.get(columnName);
        final String columnTorqueType = getColumnTorqueType(columnMetaInfo);
        column.setJdbcType(columnTorqueType);
    }

    protected void setupDbType(Map<String, DfColumnMetaInfo> metaMap, String columnName, Column column) {
        final DfColumnMetaInfo columnMetaInfo = metaMap.get(columnName);
        final String dbTypeName;
        final String plainName = columnMetaInfo.getDbTypeName();
        if (Srl.contains(plainName, ".")) { // basically for ARRAY and STRUCT type
            final String catalogSchema = Srl.substringLastFront(plainName, ".");
            final UnifiedSchema unifiedSchema = UnifiedSchema.createAsDynamicSchema(catalogSchema);
            if (unifiedSchema.isMainSchema()) {
                dbTypeName = Srl.substringLastRear(plainName, ".");
            } else {
                dbTypeName = plainName;
            }
        } else {
            dbTypeName = plainName;
        }
        column.setDbType(dbTypeName);
    }

    protected String getCommonColumnTorqueType(String columnName) {
        return getCommonColumnMap().get(columnName);
    }

    protected Map<String, String> getCommonColumnMap() {
        DfCommonColumnProperties prop = getProperties().getCommonColumnProperties();
        return prop.getCommonColumnMap();
    }

    protected String getColumnTorqueType(DfColumnMetaInfo columnMetaInfo) {
        if (columnMetaInfo.isProcedureParameter() && !_columnHandler.hasMappingJdbcType(columnMetaInfo)) {
            // unknown type of procedure parameter should be treated as Object
            return TypeMap.OTHER;
        } else {
            return _columnHandler.getColumnJdbcType(columnMetaInfo);
        }
    }

    protected void setupColumnSizeContainsDigit(Map<String, DfColumnMetaInfo> metaMap, String columnName,
            final Column column) {
        final DfColumnMetaInfo metaInfo = metaMap.get(columnName);
        final int columnSize = metaInfo.getColumnSize();
        final int decimalDigits = metaInfo.getDecimalDigits();
        column.setupColumnSize(columnSize, decimalDigits);
    }

    protected void setupColumnComment(Map<String, DfColumnMetaInfo> metaMap, String columnName, Column column) {
        final DfColumnMetaInfo metaInfo = metaMap.get(columnName);
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

    protected void setupSql2EntityElement(String entityName, Map<String, DfColumnMetaInfo> metaMap, String columnName,
            Column column, String pkRelatedTableName) {
        final Table relatedTable = setupSql2EntityRelatedTable(entityName, metaMap, columnName, column,
                pkRelatedTableName);
        final Column relatedColumn = setupSql2EntityRelatedColumn(relatedTable, metaMap, columnName, column);
        final String forcedJavaNative = setupSql2EntityForcedJavaNative(metaMap, columnName, column);

        showColumnInfo(columnName, column, relatedTable, relatedColumn, forcedJavaNative);
    }

    protected Table setupSql2EntityRelatedTable(String entityName, Map<String, DfColumnMetaInfo> metaMap,
            String columnName, Column column, String pkRelatedTableName) {
        final DfColumnMetaInfo metaInfo = metaMap.get(columnName);
        final String sql2EntityRelatedTableName = metaInfo.getSql2EntityRelatedTableName();
        Table relatedTable = getRelatedTable(sql2EntityRelatedTableName); // first attack
        if (relatedTable == null) {
            if (pkRelatedTableName != null) { // second attack using PK-related
                relatedTable = getRelatedTable(pkRelatedTableName);
                if (relatedTable == null) {
                    throwTableRelatedPrimaryKeyNotFoundException(entityName, pkRelatedTableName, columnName);
                }
            } else {
                return null;
            }
        } else {
            if (pkRelatedTableName != null) {
                if (!Srl.equalsFlexible(sql2EntityRelatedTableName, pkRelatedTableName)) {
                    throwTableRelatedPrimaryKeyDifferentException(entityName, sql2EntityRelatedTableName,
                            pkRelatedTableName, columnName);
                }
            }
        }
        column.setSql2EntityRelatedTable(relatedTable);
        return relatedTable;
    }

    protected void throwTableRelatedPrimaryKeyNotFoundException(String entityName, String tableName, String columnName) {
        ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The table name related to the primary key is not found.");
        br.addItem("Entity");
        br.addElement(entityName);
        br.addItem("Table Name");
        br.addElement(tableName);
        br.addItem("Primary Key");
        br.addElement(columnName);
        String msg = br.buildExceptionMessage();
        throw new IllegalOutsideSqlOperationException(msg);
    }

    protected void throwTableRelatedPrimaryKeyDifferentException(String entityName, String realTable,
            String differentTable, String columnName) {
        ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The table name related to the primary key is different.");
        br.addItem("Entity");
        br.addElement(entityName);
        br.addItem("Real Table");
        br.addElement(realTable);
        br.addItem("Different Table");
        br.addElement(differentTable);
        br.addItem("Primary Key");
        br.addElement(columnName);
        String msg = br.buildExceptionMessage();
        throw new IllegalOutsideSqlOperationException(msg);
    }

    protected Column setupSql2EntityRelatedColumn(Table relatedTable, Map<String, DfColumnMetaInfo> metaMap,
            String columnName, Column column) {
        if (relatedTable == null) {
            return null;
        }
        final DfColumnMetaInfo metaInfo = metaMap.get(columnName);
        final String sql2EntityRelatedColumnName = metaInfo.getSql2EntityRelatedColumnName();
        final Column relatedColumn = relatedTable.getColumn(sql2EntityRelatedColumnName);
        if (relatedColumn == null) {
            return null;
        }
        column.setSql2EntityRelatedColumn(relatedColumn);
        return column;
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

    protected String setupSql2EntityForcedJavaNative(final Map<String, DfColumnMetaInfo> metaMap, String columnName,
            final Column column) {
        final DfColumnMetaInfo metaInfo = metaMap.get(columnName);
        final String sql2EntityForcedJavaNative = metaInfo.getSql2EntityForcedJavaNative();
        column.setSql2EntityForcedJavaNative(sql2EntityForcedJavaNative);
        return sql2EntityForcedJavaNative;
    }

    // -----------------------------------------------------
    //                                            After Care
    //                                            ----------
    protected void throwPrimaryKeyNotFoundException(String entityName, StringKeyMap<String> pkMap,
            Set<String> columnNameSet) {
        ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The primary keys are not found in selected columns.");
        br.addItem("Entity");
        br.addElement(entityName);
        br.addItem("Selected Column");
        br.addElement(columnNameSet);
        br.addItem("Specified PK");
        br.addElement(pkMap.keySet());
        String msg = br.buildExceptionMessage();
        throw new IllegalOutsideSqlOperationException(msg);
    }

    protected void showColumnInfo(String columnName, Column column, Table relatedTable, Column relatedColumn,
            String forcedJavaNatice) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" ").append(column.isPrimaryKey() ? "*" : " ");
        sb.append(columnName);
        sb.append(" ");
        sb.append(column.getDbTypeExpression());
        final String columnSize = column.getColumnSize();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(columnSize)) {
            sb.append("(").append(columnSize).append(")");
        }
        if (relatedColumn != null) {
            sb.append(" related:").append(relatedTable.getName());
            sb.append(".").append(relatedColumn.getName());
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(forcedJavaNatice)) {
            sb.append(" forced:").append(forcedJavaNatice);
        }
        _log.info(sb.toString());
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected VelocityContext createVelocityContext(final AppData appData) {
        final DfVelocityContextFactory factory = new DfVelocityContextFactory();
        return factory.create(appData);
    }

    protected boolean needsConvertToJavaName(String columnName) {
        if (columnName == null || columnName.trim().length() == 0) {
            String msg = "The columnName is invalid: " + columnName;
            throw new IllegalArgumentException(msg);
        }
        if (columnName.contains("_")) {
            return true; // contains (supported) connector!
        }
        // here 'BIRHDATE' or 'birthdate' or 'Birthdate'
        // or 'memberStatus' or 'MemberStatus'
        final char[] columnCharArray = columnName.toCharArray();
        boolean existsUpper = false;
        boolean existsLower = false;
        for (char ch : columnCharArray) {
            if (Character.isDigit(ch)) {
                continue;
            }
            if (Character.isUpperCase(ch)) {
                existsUpper = true;
                continue;
            }
            if (Character.isLowerCase(ch)) {
                existsLower = true;
                continue;
            }
        }
        final boolean camelCase = existsUpper && existsLower;
        // if it's camelCase, no needs to convert
        // (all characters that are upper or lower case needs to convert)
        return !camelCase;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return getProperties().getOutsideSqlProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    public String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
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
    public void setSpecifiedSqlFile(String specifiedSqlFile) {
        DfSpecifiedSqlFile.getInstance().setSpecifiedSqlFile(specifiedSqlFile);
    }
}