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
package org.seasar.dbflute.logic.jdbc.metadata.basic;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.exception.DfProcedureListGettingFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta.DfProcedureColumnType;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta.DfProcedureType;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureSynonymMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSynonymMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeArrayInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureSupplementExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfProcedureSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.factory.DfProcedureSynonymExtractorFactory;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties.ProcedureSynonymHandlingType;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.7.5 (2008/06/28 Saturday)
 */
public class DfProcedureExtractor extends DfAbstractMetaDataBasicExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureExtractor.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _suppressAdditionalSchema;
    protected boolean _suppressFilterByProperty;
    protected boolean _suppressLogging;
    protected DataSource _procedureSynonymDataSource;

    // ===================================================================================
    //                                                                 Available Procedure
    //                                                                 ===================
    /**
     * Get the list of available meta information.
     * @param dataSource The data source for getting meta data. (NotNull)
     * @return The list of available procedure meta informations. (NotNull)
     * @throws SQLException
     */
    public List<DfProcedureMeta> getAvailableProcedureList(DataSource dataSource) throws SQLException {
        return new ArrayList<DfProcedureMeta>(getAvailableProcedureMap(dataSource).values());
    }

    /**
     * Get the map of available meta information. <br />
     * The map key is procedure name that contains package prefix).
     * @param dataSource The data source for getting meta data. (NotNull)
     * @return The map of available procedure meta informations. The key is full-qualified name. (NotNull)
     * @throws SQLException
     */
    public Map<String, DfProcedureMeta> getAvailableProcedureMap(DataSource dataSource) throws SQLException {
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final UnifiedSchema mainSchema = databaseProperties.getDatabaseSchema();
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        if (!outsideSqlProperties.isGenerateProcedureParameterBean()) {
            return newLinkedHashMap();
        }
        final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();

        // main schema
        final List<DfProcedureMeta> procedureList = getPlainProcedureList(dataSource, metaData, mainSchema);

        // additional schema
        setupAdditionalSchemaProcedure(dataSource, metaData, procedureList);

        // procedure synonym
        setupProcedureSynonym(procedureList);

        // filter by property
        final List<DfProcedureMeta> filteredList = filterByProperty(procedureList);

        // create available procedure map
        final Map<String, DfProcedureMeta> procedureHandlingMap = newLinkedHashMap();
        for (DfProcedureMeta metaInfo : filteredList) {
            // handle duplicate
            if (handleDuplicateProcedure(metaInfo, procedureHandlingMap, mainSchema)) {
                continue;
            }
            procedureHandlingMap.put(metaInfo.buildProcedureKeyName(), metaInfo);
        }

        // arrange order (additional schema after main schema)
        final Map<String, DfProcedureMeta> procedureOrderedMap = newLinkedHashMap();
        final Map<String, DfProcedureMeta> additionalSchemaProcedureMap = newLinkedHashMap();
        final Set<Entry<String, DfProcedureMeta>> entrySet = procedureHandlingMap.entrySet();
        for (Entry<String, DfProcedureMeta> entry : entrySet) {
            final String key = entry.getKey();
            final DfProcedureMeta metaInfo = entry.getValue();
            if (metaInfo.getProcedureSchema().isAdditionalSchema()) {
                additionalSchemaProcedureMap.put(key, metaInfo);
            } else {
                procedureOrderedMap.put(key, metaInfo); // main schema
            }
        }
        procedureOrderedMap.putAll(additionalSchemaProcedureMap);
        return procedureOrderedMap;
    }

    // -----------------------------------------------------
    //                                     Additional Schema
    //                                     -----------------
    protected void setupAdditionalSchemaProcedure(DataSource dataSource, DatabaseMetaData metaData,
            List<DfProcedureMeta> procedureList) throws SQLException {
        if (_suppressAdditionalSchema) {
            return;
        }
        final DfDatabaseProperties databaseProp = getProperties().getDatabaseProperties();
        final List<UnifiedSchema> additionalSchemaList = databaseProp.getAdditionalSchemaList();
        for (UnifiedSchema additionalSchema : additionalSchemaList) {
            final DfAdditionalSchemaInfo schemaInfo = databaseProp.getAdditionalSchemaInfo(additionalSchema);
            if (schemaInfo.isSuppressProcedure()) {
                continue;
            }
            final List<DfProcedureMeta> additionalProcedureList = getPlainProcedureList(dataSource, metaData,
                    additionalSchema);
            procedureList.addAll(additionalProcedureList);
        }
    }

    // -----------------------------------------------------
    //                                     Procedure Synonym
    //                                     -----------------
    protected void setupProcedureSynonym(List<DfProcedureMeta> procedureList) {
        if (_procedureSynonymDataSource == null) {
            return;
        }
        final DfOutsideSqlProperties prop = getProperties().getOutsideSqlProperties();
        final ProcedureSynonymHandlingType handlingType = prop.getProcedureSynonymHandlingType();
        if (handlingType.equals(ProcedureSynonymHandlingType.NONE)) {
            return;
        }
        final DfProcedureSynonymExtractor extractor = createProcedureSynonymExtractor();
        if (extractor == null) {
            return; // unsupported at the database
        }
        final Map<String, DfProcedureSynonymMeta> procedureSynonymMap = extractor.extractProcedureSynonymMap();
        if (handlingType.equals(ProcedureSynonymHandlingType.INCLUDE)) {
            // only add procedure synonyms to the procedure list
        } else if (handlingType.equals(ProcedureSynonymHandlingType.SWITCH)) {
            log("...Clearing normal procedures: count=" + procedureList.size());
            procedureList.clear(); // because of switch
        } else {
            String msg = "Unexpected handling type of procedure sysnonym: " + handlingType;
            throw new IllegalStateException(msg);
        }
        log("...Adding procedure synonyms as procedure: count=" + procedureSynonymMap.size());
        final List<DfProcedureMeta> procedureSynonymList = new ArrayList<DfProcedureMeta>();
        for (Entry<String, DfProcedureSynonymMeta> entry : procedureSynonymMap.entrySet()) {
            final DfProcedureSynonymMeta metaInfo = entry.getValue();
            if (!isSynonymAllowedSchema(metaInfo)) {
                continue;
            }

            // merge synonym to procedure (create copied instance)
            final String beforeName = metaInfo.getProcedureMetaInfo().buildProcedureLoggingName();
            final DfProcedureMeta mergedProcedure = metaInfo.createMergedProcedure();
            final String afterName = mergedProcedure.buildProcedureLoggingName();
            log("  " + beforeName + " to " + afterName);

            procedureSynonymList.add(mergedProcedure);
        }
        procedureList.addAll(procedureSynonymList);
    }

    protected boolean isSynonymAllowedSchema(DfProcedureSynonymMeta procedureSynonymMetaInfo) {
        final DfSynonymMeta synonymMetaInfo = procedureSynonymMetaInfo.getSynonymMetaInfo();
        final UnifiedSchema synonymOwner = synonymMetaInfo.getSynonymOwner();
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final DfAdditionalSchemaInfo additionalSchemaInfo = databaseProperties.getAdditionalSchemaInfo(synonymOwner);
        if (additionalSchemaInfo != null) {
            return additionalSchemaInfo.hasObjectTypeSynonym();
        } else {
            return databaseProperties.hasObjectTypeSynonym(); // as main schema
        }
    }

    /**
     * @return The extractor of procedure synonym. (NullAllowed)
     */
    protected DfProcedureSynonymExtractor createProcedureSynonymExtractor() {
        final DfProcedureSynonymExtractorFactory factory = new DfProcedureSynonymExtractorFactory(
                _procedureSynonymDataSource, getDatabaseTypeFacadeProp(), getDatabaseProperties());
        return factory.createSynonymExtractor();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return DfBuildProperties.getInstance().getDatabaseProperties();
    }

    // -----------------------------------------------------
    //                                    Filter by Property
    //                                    ------------------
    protected List<DfProcedureMeta> filterByProperty(List<DfProcedureMeta> procedureList) {
        if (_suppressFilterByProperty) {
            return procedureList;
        }
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        final List<DfProcedureMeta> resultList = new ArrayList<DfProcedureMeta>();
        log("...Filtering procedures by the property: before=" + procedureList.size());
        int passedCount = 0;
        for (DfProcedureMeta metaInfo : procedureList) {
            final String procedureLoggingName = metaInfo.buildProcedureLoggingName();
            final String procedureCatalog = metaInfo.getProcedureCatalog();
            if (!outsideSqlProperties.isTargetProcedureCatalog(procedureCatalog)) {
                log("  passed: non-target catalog - " + procedureLoggingName);
                ++passedCount;
                continue;
            }
            final UnifiedSchema procedureSchema = metaInfo.getProcedureSchema();
            if (!outsideSqlProperties.isTargetProcedureSchema(procedureSchema.getPureSchema())) {
                log("  passed: non-target schema - " + procedureLoggingName);
                ++passedCount;
                continue;
            }
            final String procedureFullQualifiedName = metaInfo.getProcedureFullQualifiedName();
            final String procedureSchemaQualifiedName = Srl.substringFirstFront(procedureFullQualifiedName, ".");
            final String procedureName = metaInfo.getProcedureName();
            if (!outsideSqlProperties.isTargetProcedureName(procedureFullQualifiedName)
                    && !outsideSqlProperties.isTargetProcedureName(procedureSchemaQualifiedName)
                    && !outsideSqlProperties.isTargetProcedureName(procedureName)) {
                log("  passed: non-target name - " + procedureLoggingName);
                ++passedCount;
                continue;
            }
            resultList.add(metaInfo);
        }
        if (passedCount == 0) {
            log(" -> All procedures are target: count=" + procedureList.size());
        }
        return resultList;
    }

    // -----------------------------------------------------
    //                                   Duplicate Procedure
    //                                   -------------------
    /**
     * @param second The second procedure being processed current loop. (NotNull)
     * @param procedureHandlingMap The handling map of procedure. (NotNull)
     * @param mainSchema The unified schema for main. (NotNull)
     * @return Does it skip to register the second procedure?
     */
    protected boolean handleDuplicateProcedure(DfProcedureMeta second,
            Map<String, DfProcedureMeta> procedureHandlingMap, UnifiedSchema mainSchema) {
        final String procedureKeyName = second.buildProcedureKeyName();
        final DfProcedureMeta first = procedureHandlingMap.get(procedureKeyName);
        if (first == null) {
            return false; // not duplicate
        }
        final UnifiedSchema firstSchema = first.getProcedureSchema();
        final UnifiedSchema secondSchema = second.getProcedureSchema();
        // basically select the one of main schema.
        if (!firstSchema.equals(secondSchema)) {
            if (firstSchema.isMainSchema()) {
                showDuplicateProcedure(first, second, true, "main schema");
                return true; // use first so skip
            } else if (secondSchema.isMainSchema()) {
                procedureHandlingMap.remove(procedureKeyName);
                showDuplicateProcedure(first, second, false, "main schema");
                return false; // use second so NOT skip (override)
            }
        }
        // if both are additional schema or main schema, it selects first. 
        showDuplicateProcedure(first, second, true, "first one");
        return true;
    }

    protected void showDuplicateProcedure(DfProcedureMeta first, DfProcedureMeta second, boolean electFirst,
            String reason) {
        final String firstName = first.buildProcedureLoggingName();
        final String secondName = second.buildProcedureLoggingName();
        final String firstType = first.isProcedureSynonym() ? "(synonym)" : "";
        final String secondType = second.isProcedureSynonym() ? "(synonym)" : "";
        String msg = "*Found the same-name procedure, so elects " + reason + ":";
        if (electFirst) {
            msg = msg + " elect=" + firstName + firstType + " skipped=" + secondName + secondType;
        } else {
            msg = msg + " elect=" + secondName + secondType + " skipped=" + firstName + firstType;
        }
        log(msg);
    }

    // ===================================================================================
    //                                                                     Plain Procedure
    //                                                                     ===============
    /**
     * Get the list of plain procedures. <br />
     * It selects procedures of specified schema only.
     * @param dataSource Data source. (NotNull)
     * @param metaData The meta data of database. (NotNull)
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (NullAllowed)
     * @return The list of procedure meta information. (NotNull)
     */
    public List<DfProcedureMeta> getPlainProcedureList(DataSource dataSource, DatabaseMetaData metaData,
            UnifiedSchema unifiedSchema) throws SQLException {
        final List<DfProcedureMeta> metaInfoList = new ArrayList<DfProcedureMeta>();
        String procedureName = null;
        ResultSet columnResultSet = null;
        try {
            final ResultSet procedureRs = doGetProcedures(metaData, unifiedSchema);
            setupProcedureMetaInfo(metaInfoList, procedureRs, unifiedSchema);
            for (DfProcedureMeta metaInfo : metaInfoList) {
                procedureName = metaInfo.getProcedureName();
                final ResultSet columnRs = doGetProcedureColumns(metaData, metaInfo);
                setupProcedureColumnMetaInfo(metaInfo, columnRs);
            }
        } catch (SQLException e) {
            throwProcedureListGettingFailureException(unifiedSchema, procedureName, e);
            return null; // unreachable
        } catch (RuntimeException e) { // for an unexpected exception from JDBC driver
            throwProcedureListGettingFailureException(unifiedSchema, procedureName, e);
            return null; // unreachable
        } finally {
            if (columnResultSet != null) {
                try {
                    columnResultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }
        resolveAssistInfo(dataSource, unifiedSchema, metaInfoList);
        return metaInfoList;
    }

    protected void resolveAssistInfo(DataSource dataSource, UnifiedSchema unifiedSchema,
            List<DfProcedureMeta> metaInfoList) {
        if (isDatabaseOracle()) {
            doResolveAssistInfoOracle(dataSource, unifiedSchema, metaInfoList);
        }
    }

    protected void doResolveAssistInfoOracle(DataSource dataSource, UnifiedSchema unifiedSchema,
            List<DfProcedureMeta> metaInfoList) {
        final DfProcedureSupplementExtractorOracle extractor = new DfProcedureSupplementExtractorOracle(dataSource);
        if (_suppressLogging || unifiedSchema.isAdditionalSchema()) {
            // contains additional schema because it has no change
            extractor.suppressLogging();
        }
        final Map<String, Integer> parameterOverloadInfoMap = extractor.extractParameterOverloadInfoMap();
        final StringKeyMap<DfTypeArrayInfo> parameterArrayInfoMap = extractor.extractParameterArrayInfoMap();
        final StringKeyMap<DfTypeStructInfo> structInfoMap = extractor.extractStructInfoMap();
        final Set<String> resolvedArrayDispSet = new LinkedHashSet<String>();
        final Set<String> resolvedStructDispSet = new LinkedHashSet<String>();
        for (DfProcedureMeta metaInfo : metaInfoList) {
            final String catalog = metaInfo.getProcedureCatalog();
            final String procedureName = metaInfo.getProcedureName();
            final List<DfProcedureColumnMeta> columnList = metaInfo.getProcedureColumnList();
            for (DfProcedureColumnMeta columnInfo : columnList) {
                final String columnName = columnInfo.getColumnName();
                final String key = extractor.generateParameterInfoMapKey(catalog, procedureName, columnName);

                // Overload
                final Integer overloadNo = parameterOverloadInfoMap.get(key);
                if (overloadNo != null) {
                    columnInfo.setOverloadNo(overloadNo);
                }

                // Array
                final DfTypeArrayInfo arrayInfo = parameterArrayInfoMap.get(key);
                if (arrayInfo != null) {
                    resolvedArrayDispSet.add(arrayInfo.toString());
                    columnInfo.setTypeArrayInfo(arrayInfo);
                }

                // Struct
                final String dbTypeName = columnInfo.getDbTypeName();
                final DfTypeStructInfo structInfo = structInfoMap.get(dbTypeName);
                if (structInfo != null) {
                    resolvedStructDispSet.add(structInfo.toString());
                    columnInfo.setTypeStructInfo(structInfo);
                }
            }
        }
        if (!resolvedArrayDispSet.isEmpty()) {
            log("Array related to parameter: " + resolvedArrayDispSet.size());
            for (String arrayInfo : resolvedArrayDispSet) {
                log("  " + arrayInfo);
            }
        }
        if (!resolvedStructDispSet.isEmpty()) {
            log("Struct related to parameter: " + resolvedStructDispSet.size());
            for (String structInfo : resolvedStructDispSet) {
                log("  " + structInfo);
            }
        }
    }

    protected ResultSet doGetProcedures(DatabaseMetaData metaData, UnifiedSchema unifiedSchema) throws SQLException {
        final String catalogName = unifiedSchema.getPureCatalog();
        final String schemaName = unifiedSchema.getPureSchema();
        return metaData.getProcedures(catalogName, schemaName, null);
    }

    protected void setupProcedureMetaInfo(List<DfProcedureMeta> procedureMetaInfoList, ResultSet procedureRs,
            UnifiedSchema unifiedSchema) throws SQLException {
        while (procedureRs.next()) {
            // /- - - - - - - - - - - - - - - - - - - - - - - -
            // same policy as table process about JDBC handling
            // (see DfTableHandler.java)
            // - - - - - - - - - -/

            final String procedureSchema = procedureRs.getString("PROCEDURE_SCHEM");
            final String procedurePackage;
            final String procedureCatalog;
            final String procedureName;
            {
                final String plainCatalog = procedureRs.getString("PROCEDURE_CAT");
                if (isDatabaseOracle()) {
                    // because Oracle treats catalog as package
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(plainCatalog)) {
                        procedurePackage = plainCatalog;
                    } else {
                        procedurePackage = null;
                    }
                    procedureCatalog = null;
                } else {
                    procedurePackage = null;
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(plainCatalog)) {
                        procedureCatalog = plainCatalog;
                    } else {
                        procedureCatalog = unifiedSchema.getPureCatalog();
                    }
                }
                final String plainName = procedureRs.getString("PROCEDURE_NAME");
                if (Srl.is_NotNull_and_NotTrimmedEmpty(procedurePackage)) {
                    procedureName = procedurePackage + "." + plainName;
                } else {
                    procedureName = plainName;
                }
            }
            final Integer procedureType = Integer.valueOf(procedureRs.getString("PROCEDURE_TYPE"));
            final String procedureComment = procedureRs.getString("REMARKS");

            final DfProcedureMeta metaInfo = new DfProcedureMeta();
            metaInfo.setProcedureCatalog(procedureCatalog);
            metaInfo.setProcedureSchema(createAsDynamicSchema(procedureCatalog, procedureSchema));
            metaInfo.setProcedureName(procedureName);
            if (procedureType == DatabaseMetaData.procedureResultUnknown) {
                metaInfo.setProcedureType(DfProcedureType.procedureResultUnknown);
            } else if (procedureType == DatabaseMetaData.procedureNoResult) {
                metaInfo.setProcedureType(DfProcedureType.procedureNoResult);
            } else if (procedureType == DatabaseMetaData.procedureReturnsResult) {
                metaInfo.setProcedureType(DfProcedureType.procedureReturnsResult);
            } else {
                String msg = "Unknown procedureType: type=" + procedureType + " procedure=" + procedureName;
                throw new IllegalStateException(msg);
            }
            metaInfo.setProcedureComment(procedureComment);
            metaInfo.setProcedurePackage(procedurePackage);
            metaInfo.setProcedureFullQualifiedName(buildProcedureFullQualifiedName(metaInfo));
            metaInfo.setProcedureSchemaQualifiedName(buildProcedureSchemaQualifiedName(metaInfo));
            procedureMetaInfoList.add(metaInfo);
        }
    }

    protected ResultSet doGetProcedureColumns(DatabaseMetaData metaData, DfProcedureMeta metaInfo) throws SQLException {
        final String catalogName = metaInfo.getProcedureCatalog();
        final String schemaName = metaInfo.getProcedureSchema().getPureSchema();
        final String procedurePureName = metaInfo.buildProcedurePureName();
        final String catalogArgName;
        final String procedureArgName;
        if (isDatabaseMySQL() && Srl.is_NotNull_and_NotTrimmedEmpty(catalogName)) {
            // getProcedureColumns() of MySQL requires qualified procedure name when other catalog
            catalogArgName = catalogName;
            procedureArgName = Srl.connectPrefix(procedurePureName, catalogName, ".");
        } else if (isDatabaseOracle() && metaInfo.isPackageProcdure()) {
            catalogArgName = metaInfo.getProcedurePackage();
            procedureArgName = procedurePureName; // needs to use pure name
        } else {
            catalogArgName = catalogName;
            procedureArgName = procedurePureName;
        }
        return metaData.getProcedureColumns(catalogArgName, schemaName, procedureArgName, null);
    }

    protected void setupProcedureColumnMetaInfo(DfProcedureMeta procedureMetaInfo, ResultSet columnRs)
            throws SQLException {
        final Set<String> uniqueSet = new HashSet<String>();
        while (columnRs.next()) {
            // /- - - - - - - - - - - - - - - - - - - - - - - -
            // same policy as table process about JDBC handling
            // (see DfTableHandler.java)
            // - - - - - - - - - -/

            final String columnName = columnRs.getString("COLUMN_NAME");

            // filter duplicated informations
            // because Oracle package procedure may return them
            if (uniqueSet.contains(columnName)) {
                continue;
            }
            uniqueSet.add(columnName);

            final Integer procedureColumnType;
            {
                final String columnType = columnRs.getString("COLUMN_TYPE");
                final int unknowType = DatabaseMetaData.procedureColumnUnknown;
                if (Srl.is_NotNull_and_NotTrimmedEmpty(columnType)) {
                    procedureColumnType = toInt("columnType", columnType);
                } else {
                    procedureColumnType = unknowType;
                }
            }

            final int jdbcType;
            {
                int tmpJdbcType = Types.OTHER;
                String dataType = null;
                try {
                    dataType = columnRs.getString("DATA_TYPE");
                } catch (RuntimeException ignored) { // pinpoint patch
                    // for example, SQLServer throws an exception
                    // if the procedure is a function that returns table type
                    final String procdureName = procedureMetaInfo.getProcedureFullQualifiedName();
                    log("*Failed to get data type: " + procdureName + "." + columnName);
                    tmpJdbcType = Types.OTHER;
                }
                if (Srl.is_NotNull_and_NotTrimmedEmpty(dataType)) {
                    tmpJdbcType = toInt("dataType", dataType);
                }
                jdbcType = tmpJdbcType;
            }

            final String dbTypeName = columnRs.getString("TYPE_NAME");

            // uses getString() to get null value
            // (getInt() returns zero when a value is no defined)
            final Integer columnSize;
            {
                final String precision = columnRs.getString("PRECISION");
                if (Srl.is_NotNull_and_NotTrimmedEmpty(precision)) {
                    columnSize = toInt("precision", precision);
                } else {
                    final String length = columnRs.getString("LENGTH");
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(length)) {
                        columnSize = toInt("length", length);
                    } else {
                        columnSize = null;
                    }
                }
            }
            final Integer decimalDigits;
            {
                final String scale = columnRs.getString("SCALE");
                if (Srl.is_NotNull_and_NotTrimmedEmpty(scale)) {
                    decimalDigits = toInt("scale", scale);
                } else {
                    decimalDigits = null;
                }
            }
            final String columnComment = columnRs.getString("REMARKS");

            final DfProcedureColumnMeta procedureColumnMetaInfo = new DfProcedureColumnMeta();
            procedureColumnMetaInfo.setColumnName(columnName);
            if (procedureColumnType == DatabaseMetaData.procedureColumnUnknown) {
                procedureColumnMetaInfo.setProcedureColumnType(DfProcedureColumnType.procedureColumnUnknown);
            } else if (procedureColumnType == DatabaseMetaData.procedureColumnIn) {
                procedureColumnMetaInfo.setProcedureColumnType(DfProcedureColumnType.procedureColumnIn);
            } else if (procedureColumnType == DatabaseMetaData.procedureColumnInOut) {
                procedureColumnMetaInfo.setProcedureColumnType(DfProcedureColumnType.procedureColumnInOut);
            } else if (procedureColumnType == DatabaseMetaData.procedureColumnOut) {
                procedureColumnMetaInfo.setProcedureColumnType(DfProcedureColumnType.procedureColumnOut);
            } else if (procedureColumnType == DatabaseMetaData.procedureColumnReturn) {
                procedureColumnMetaInfo.setProcedureColumnType(DfProcedureColumnType.procedureColumnReturn);
            } else if (procedureColumnType == DatabaseMetaData.procedureColumnResult) {
                procedureColumnMetaInfo.setProcedureColumnType(DfProcedureColumnType.procedureColumnResult);
            } else {
                throw new IllegalStateException("Unknown procedureColumnType: " + procedureColumnType);
            }
            procedureColumnMetaInfo.setJdbcDefType(jdbcType);
            procedureColumnMetaInfo.setDbTypeName(dbTypeName);
            procedureColumnMetaInfo.setColumnSize(columnSize);
            procedureColumnMetaInfo.setDecimalDigits(decimalDigits);
            procedureColumnMetaInfo.setColumnComment(columnComment);
            procedureMetaInfo.addProcedureColumn(procedureColumnMetaInfo);
        }
        adjustProcedureColumnList(procedureMetaInfo);
    }

    protected int toInt(String title, String value) {
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            String msg = "Failed to convert the value to integer:";
            msg = msg + " title=" + title + " value=" + value;
            throw new IllegalStateException(msg, e);
        }
    }

    protected String buildProcedureFullQualifiedName(DfProcedureMeta metaInfo) {
        return metaInfo.getProcedureSchema().buildFullQualifiedName(metaInfo.getProcedureName());
    }

    protected String buildProcedureSchemaQualifiedName(DfProcedureMeta metaInfo) {
        return metaInfo.getProcedureSchema().buildSchemaQualifiedName(metaInfo.getProcedureName());
    }

    protected void adjustProcedureColumnList(DfProcedureMeta procedureMetaInfo) {
        adjustPostgreSQLResultSetParameter(procedureMetaInfo);
    }

    protected void adjustPostgreSQLResultSetParameter(DfProcedureMeta procedureMetaInfo) {
        if (!isDatabasePostgreSQL()) {
            return;
        }
        final List<DfProcedureColumnMeta> columnMetaInfoList = procedureMetaInfo.getProcedureColumnList();
        boolean existsResultSetParameter = false;
        boolean existsResultSetReturn = false;
        int resultSetReturnIndex = 0;
        String resultSetReturnName = null;
        int index = 0;
        for (DfProcedureColumnMeta columnMetaInfo : columnMetaInfoList) {
            final DfProcedureColumnType procedureColumnType = columnMetaInfo.getProcedureColumnType();
            final String dbTypeName = columnMetaInfo.getDbTypeName();
            if (procedureColumnType.equals(DfProcedureColumnType.procedureColumnOut)) {
                if ("refcursor".equalsIgnoreCase(dbTypeName)) {
                    existsResultSetParameter = true;
                }
            }
            if (procedureColumnType.equals(DfProcedureColumnType.procedureColumnReturn)) {
                if ("refcursor".equalsIgnoreCase(dbTypeName)) {
                    existsResultSetReturn = true;
                    resultSetReturnIndex = index;
                    resultSetReturnName = columnMetaInfo.getColumnName();
                }
            }
            ++index;
        }
        if (existsResultSetParameter && existsResultSetReturn) {
            // It is a precondition that PostgreSQL does not allow functions to have a result set return
            // when it also has result set parameters (as an out parameter).
            String name = procedureMetaInfo.buildProcedureLoggingName() + "." + resultSetReturnName;
            log("...Removing the result set return which is unnecessary: " + name);
            columnMetaInfoList.remove(resultSetReturnIndex);
        }
    }

    protected void throwProcedureListGettingFailureException(UnifiedSchema unifiedSchema, String procedureName,
            Exception e) throws SQLException {
        final boolean forSqlEx = e instanceof SQLException;
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to get a list of procedures.");
        br.addItem("Unified Schema");
        br.addElement(unifiedSchema);
        br.addItem("Current Procedure");
        br.addElement(procedureName);
        br.addItem(forSqlEx ? "Caused SQLException" : "Unexpected Exception");
        br.addElement(e.getClass().getName());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        if (forSqlEx) {
            throw new DfJDBCException(msg, (SQLException) e);
        } else {
            throw new DfProcedureListGettingFailureException(msg, e);
        }
    }

    protected void log(String msg) {
        if (_suppressLogging) {
            return;
        }
        _log.info(msg);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public void suppressAdditionalSchema() {
        _suppressAdditionalSchema = true;
    }

    public void suppressFilterByProperty() {
        _suppressFilterByProperty = true;
    }

    public void suppressLogging() {
        _suppressLogging = true;
    }

    public void includeProcedureSynonym(DataSource dataSource) {
        _procedureSynonymDataSource = dataSource;
    }
}