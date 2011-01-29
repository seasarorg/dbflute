/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfSpecifiedSqlFile;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.exception.DfProcedureSetupFailureException;
import org.seasar.dbflute.exception.IllegalOutsideSqlOperationException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.friends.velocity.DfVelocityContextFactory;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlAnalyzer;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfSql2EntityMarkAnalyzer;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfSql2EntityMeta;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbCommentSetupper;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfProcedurePmbSetupper;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.task.bs.DfAbstractTexenTask;
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
    protected final DfSql2EntityMeta _sql2entityMeta = new DfSql2EntityMeta(); // has all meta data

    // helper
    protected final DfColumnHandler _columnHandler = new DfColumnHandler();
    protected final DfSql2EntityMarkAnalyzer _markAnalyzer = new DfSql2EntityMarkAnalyzer();

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
        setupExtendedClassDescription();

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
        return new DfOutsideSqlAnalyzer(runInfo, getDataSource(), _sql2entityMeta);
    }

    protected void handleNotFoundResult(List<File> sqlFileList) {
        final Map<String, DfCustomizeEntityInfo> entityInfoMap = _sql2entityMeta.getEntityInfoMap();
        final Map<String, DfPmbMetaData> pmbMetaDataMap = _sql2entityMeta.getPmbMetaDataMap();
        if (entityInfoMap.isEmpty() && pmbMetaDataMap.isEmpty()) {
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
        final Map<String, String> exceptionInfoMap = _sql2entityMeta.getExceptionInfoMap();
        if (exceptionInfoMap.isEmpty()) {
            return;
        }
        final Set<String> nameSet = exceptionInfoMap.keySet();
        final StringBuilder sb = new StringBuilder();
        for (String name : nameSet) {
            final String exceptionInfo = exceptionInfoMap.get(name);
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
    //                                                                           Procedure
    //                                                                           =========
    protected void setupProcedure() {
        try {
            final DfProcedurePmbSetupper setupper = createProcedurePmbSetupper();
            setupper.setupProcedure();
            final Map<String, String> exceptionInfoMap = _sql2entityMeta.getExceptionInfoMap();
            exceptionInfoMap.putAll(setupper.getContinuedFailureMessageMap());
        } catch (SQLException e) {
            throwProcedureSetupFailureException(e);
        }
    }

    protected DfProcedurePmbSetupper createProcedurePmbSetupper() {
        final Map<String, DfCustomizeEntityInfo> entityInfoMap = _sql2entityMeta.getEntityInfoMap();
        final Map<String, DfPmbMetaData> pmbMetaDataMap = _sql2entityMeta.getPmbMetaDataMap();
        return new DfProcedurePmbSetupper(getDataSource(), entityInfoMap, pmbMetaDataMap, _database);
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
        final DfBehaviorQueryPathSetupper setupper = new DfBehaviorQueryPathSetupper();
        setupper.setupBehaviorQueryPath(sqlFileList);
    }

    protected void setupExtendedClassDescription() {
        final DfPmbCommentSetupper reflector = new DfPmbCommentSetupper(_database.getPmbMetaDataList());
        reflector.setupExtendedClassDescription();
    }

    // ===================================================================================
    //                                                                  Prepare Generation
    //                                                                  ==================
    @Override
    public Context initControlContext() throws Exception {
        _log.info("");
        _log.info("...Preparing generation of customize-entities and parameter-beans");
        _log.info("* * * * * * * * * *");
        _log.info("* CustomizeEntity *");
        _log.info("* * * * * * * * * *");
        final StringBuilder logSb = new StringBuilder();

        final Database database = _database;
        database.setSql2EntitySchemaData(_schemaData);
        database.setPmbMetaDataMap(_sql2entityMeta.getPmbMetaDataMap());
        database.setSkipDeleteOldClass(DfSpecifiedSqlFile.getInstance().getSpecifiedSqlFile() != null);

        final Map<String, DfCustomizeEntityInfo> entityInfoMap = _sql2entityMeta.getEntityInfoMap();
        final Set<String> entityNameSet = entityInfoMap.keySet();
        for (String entityName : entityNameSet) {
            final DfCustomizeEntityInfo entityInfo = entityInfoMap.get(entityName);
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
            tbl.setSql2EntityTypeSafeCursor(entityInfo.isCursorHandling());
            buildCustomizeEntityTitle(logSb, entityName, entityInfo);

            final StringKeyMap<String> pkMap = getPrimaryKeyMap(entityInfo);
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
                setupSql2EntityElement(entityName, metaMap, columnName, column, pkRelatedTableName, logSb);
                tbl.addColumn(column);
            }
            if (!pkMap.isEmpty()) { // if not-removed columns exist
                throwPrimaryKeyNotFoundException(entityName, pkMap, columnNameSet);
            }

            if (entityInfo.isScalarHandling()) {
                // it does not generate an entity that has only one column
                entityInfo.setScalarJavaNative(tbl.getColumnList().get(0).getJavaNative());
            } else {
                // initialize a class name of the entity for typed parameter-bean
                entityInfo.setEntityClassName(tbl.getExtendedEntityClassName());
                database.addTable(tbl);
            }
            logSb.append(ln());
        }
        final String databaseType = getBasicProperties().getDatabaseType();
        final AppData appData = new AppData(databaseType);
        appData.addDatabase(database);

        showCustomizeEntity(logSb);
        showParameterBean();

        final VelocityContext context = createVelocityContext(appData);
        return context;
    }

    protected StringKeyMap<String> getPrimaryKeyMap(DfCustomizeEntityInfo entityInfo) {
        final StringKeyMap<String> pkMap = StringKeyMap.createAsFlexibleOrdered();
        final List<String> pkList = entityInfo.getPrimaryKeyList();
        if (pkList == null || pkList.isEmpty()) {
            return pkMap;
        }
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

    protected void throwPrimaryKeyNotFoundException(String entityName, StringKeyMap<String> pkMap,
            Set<String> columnNameSet) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The primary keys are not found in selected columns.");
        br.addItem("Entity");
        br.addElement(entityName);
        br.addItem("Selected Column");
        br.addElement(columnNameSet);
        br.addItem("Specified PK");
        br.addElement(pkMap.keySet());
        final String msg = br.buildExceptionMessage();
        throw new IllegalOutsideSqlOperationException(msg);
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
            Column column, String pkRelatedTableName, StringBuilder logSb) {
        final Table relatedTable = setupSql2EntityRelatedTable(entityName, metaMap, columnName, column,
                pkRelatedTableName);
        final Column relatedColumn = setupSql2EntityRelatedColumn(relatedTable, metaMap, columnName, column);
        final String forcedJavaNative = setupSql2EntityForcedJavaNative(metaMap, columnName, column);

        buildCustomizeEntityColumnInfo(logSb, columnName, column, relatedTable, relatedColumn, forcedJavaNative);
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
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The table name related to the primary key is not found.");
        br.addItem("Entity");
        br.addElement(entityName);
        br.addItem("Table Name");
        br.addElement(tableName);
        br.addItem("Primary Key");
        br.addElement(columnName);
        final String msg = br.buildExceptionMessage();
        throw new IllegalOutsideSqlOperationException(msg);
    }

    protected void throwTableRelatedPrimaryKeyDifferentException(String entityName, String realTable,
            String differentTable, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The table name related to the primary key is different.");
        br.addItem("Entity");
        br.addElement(entityName);
        br.addItem("Real Table");
        br.addElement(realTable);
        br.addItem("Different Table");
        br.addElement(differentTable);
        br.addItem("Primary Key");
        br.addElement(columnName);
        final String msg = br.buildExceptionMessage();
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
    //                                               Logging
    //                                               -------
    protected void buildCustomizeEntityTitle(StringBuilder logSb, String entityName, DfCustomizeEntityInfo entityInfo) {
        logSb.append(entityName);
        final String handlingDisp = entityInfo.buildHandlingDisp();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(handlingDisp)) {
            logSb.append(" ").append(handlingDisp);
        }
        logSb.append(ln());
    }

    protected void buildCustomizeEntityColumnInfo(StringBuilder logSb, String columnName, Column column,
            Table relatedTable, Column relatedColumn, String forcedJavaNatice) {
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
            sb.append(" related to ").append(relatedTable.getName());
            sb.append(".").append(relatedColumn.getName());
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(forcedJavaNatice)) {
            sb.append(" forced to ").append(forcedJavaNatice);
        }
        logSb.append(sb).append(ln());
    }

    protected void showCustomizeEntity(StringBuilder logSb) {
        if (logSb.length() > 0) {
            _log.info(ln() + logSb.toString().trim());
        }
    }

    protected void showParameterBean() {
        _log.info("* * * * * * * * *");
        _log.info("* ParameterBean *");
        _log.info("* * * * * * * * *");
        final StringBuilder logSb = new StringBuilder();
        final Map<String, DfPmbMetaData> pmbMetaDataMap = _sql2entityMeta.getPmbMetaDataMap();
        for (Entry<String, DfPmbMetaData> pmbEntry : pmbMetaDataMap.entrySet()) {
            final DfPmbMetaData pmbMetaData = pmbEntry.getValue();
            logSb.append(pmbMetaData.getClassName());
            if (pmbMetaData.hasSuperClassDefinition()) {
                logSb.append(" extends ").append(pmbMetaData.getSuperClassName());
            }
            if (pmbMetaData.isRelatedToProcedure()) {
                logSb.append(" (procedure");
                if (pmbMetaData.isProcedureRefCustomizeEntity()) {
                    logSb.append(" with customize-entity");
                }
                logSb.append(")").append(ln());
                final Map<String, DfProcedureColumnMetaInfo> propertyNameColumnInfoMap = pmbMetaData
                        .getPropertyNameColumnInfoMap();
                for (Entry<String, DfProcedureColumnMetaInfo> columnEntry : propertyNameColumnInfoMap.entrySet()) {
                    final DfProcedureColumnMetaInfo columnInfo = columnEntry.getValue();
                    logSb.append("  ").append(columnInfo.getColumnNameDisp());
                    logSb.append(ln());
                }
            } else {
                if (pmbMetaData.isTypedParameterBean()) {
                    logSb.append(" ").append(pmbMetaData.buildTypedDisp());
                }
                logSb.append(ln());
                final Map<String, String> propertyNameTypeMap = pmbMetaData.getPropertyNameTypeMap();
                final Map<String, String> propertyOptionMap = pmbMetaData.getPropertyNameOptionMap();
                for (Entry<String, String> propEntry : propertyNameTypeMap.entrySet()) {
                    final String propertyName = propEntry.getKey();
                    final String propertyType = propEntry.getValue();
                    logSb.append("  ").append(propertyType).append(" ").append(propertyName);
                    final String optionDef = propertyOptionMap.get(propertyName);
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(optionDef)) {
                        logSb.append(":").append(optionDef);
                    }
                    logSb.append(ln());
                }
            }
            logSb.append(ln());
        }
        if (logSb.length() > 0) {
            _log.info(ln() + logSb.toString().trim());
        }
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

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSpecifiedSqlFile(String specifiedSqlFile) {
        DfSpecifiedSqlFile.getInstance().setSpecifiedSqlFile(specifiedSqlFile);
    }
}