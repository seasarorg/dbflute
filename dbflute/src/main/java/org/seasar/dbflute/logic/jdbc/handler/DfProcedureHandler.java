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
package org.seasar.dbflute.logic.jdbc.handler;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.logic.factory.DfProcedureSynonymExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureSynonymMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSynonymMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo.DfProcedureType;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfProcedureSynonymExtractor;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties.ProcedureSynonymHandlingType;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.7.5 (2008/06/28 Saturday)
 */
public class DfProcedureHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureHandler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _suppressAdditionalSchema;
    protected boolean _suppressFilterByProperty;
    protected DataSource _procedureSynonymDataSource;

    // ===================================================================================
    //                                                                 Available Procedure
    //                                                                 ===================
    /**
     * Get the map of available meta information. <br />
     * The map key is procedure unique name.
     * @param dataSource The data source for getting meta data. (NotNull)
     * @return The map of available procedure meta informations. (NotNull)
     * @throws SQLException
     */
    public Map<String, DfProcedureMetaInfo> getAvailableProcedureMap(DataSource dataSource) throws SQLException {
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final UnifiedSchema mainSchema = databaseProperties.getDatabaseSchema();
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        if (!outsideSqlProperties.isGenerateProcedureParameterBean()) {
            return newLinkedHashMap();
        }
        final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();

        // main schema
        final List<DfProcedureMetaInfo> procedureList = getPlainProcedureList(metaData, mainSchema);

        // additional schema
        setupAdditionalSchemaProcedure(metaData, procedureList);

        // procedure synonym
        setupProcedureSynonym(procedureList);

        // filter by property
        final List<DfProcedureMetaInfo> filteredList = filterByProperty(procedureList);

        // create available procedure map
        final Map<String, DfProcedureMetaInfo> procedureHandlingMap = newLinkedHashMap();
        for (DfProcedureMetaInfo metaInfo : filteredList) {
            // handle duplicate
            if (handleDuplicateProcedure(metaInfo, procedureHandlingMap, mainSchema)) {
                continue;
            }
            procedureHandlingMap.put(metaInfo.getProcedureFullQualifiedName(), metaInfo);
        }

        // arrange order (additional schema after main schema)
        final Map<String, DfProcedureMetaInfo> procedureOrderedMap = newLinkedHashMap();
        final Map<String, DfProcedureMetaInfo> additionalSchemaProcedureMap = newLinkedHashMap();
        final Set<Entry<String, DfProcedureMetaInfo>> entrySet = procedureHandlingMap.entrySet();
        for (Entry<String, DfProcedureMetaInfo> entry : entrySet) {
            final String key = entry.getKey();
            final DfProcedureMetaInfo metaInfo = entry.getValue();
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
    protected void setupAdditionalSchemaProcedure(DatabaseMetaData metaData, List<DfProcedureMetaInfo> procedureList)
            throws SQLException {
        if (_suppressAdditionalSchema) {
            return;
        }
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final List<UnifiedSchema> additionalSchemaList = databaseProperties.getAdditionalSchemaList();
        for (UnifiedSchema additionalSchema : additionalSchemaList) {
            final List<DfProcedureMetaInfo> additionalProcedureList = getPlainProcedureList(metaData, additionalSchema);
            procedureList.addAll(additionalProcedureList);
        }
    }

    // -----------------------------------------------------
    //                                     Procedure Synonym
    //                                     -----------------
    protected void setupProcedureSynonym(List<DfProcedureMetaInfo> procedureList) {
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
        final Map<String, DfProcedureSynonymMetaInfo> procedureSynonymMap = extractor.extractProcedureSynonymMap();
        if (handlingType.equals(ProcedureSynonymHandlingType.INCLUDE)) {
            // only add procedure synonyms to the procedure list
        } else if (handlingType.equals(ProcedureSynonymHandlingType.SWITCH)) {
            _log.info("...Clearing normal procedures: count=" + procedureList.size());
            procedureList.clear(); // because of switch
        } else {
            String msg = "Unexpected handling type of procedure sysnonym: " + handlingType;
            throw new IllegalStateException(msg);
        }
        _log.info("...Adding procedure synonyms as procedure: count=" + procedureSynonymMap.size());
        final Set<Entry<String, DfProcedureSynonymMetaInfo>> entrySet = procedureSynonymMap.entrySet();
        final List<DfProcedureMetaInfo> procedureSynonymList = new ArrayList<DfProcedureMetaInfo>();
        for (Entry<String, DfProcedureSynonymMetaInfo> entry : entrySet) {
            final DfProcedureSynonymMetaInfo metaInfo = entry.getValue();
            if (!isSynonymAllowedSchema(metaInfo)) {
                continue;
            }

            // merge synonym to procedure (create copied instance)
            final String beforeName = metaInfo.getProcedureMetaInfo().getProcedureDisplayName();
            final DfProcedureMetaInfo mergedProcedure = metaInfo.createMergedProcedure();
            final String afterName = mergedProcedure.getProcedureDisplayName();
            _log.info("  " + beforeName + " to " + afterName);

            procedureSynonymList.add(mergedProcedure);
        }
        procedureList.addAll(procedureSynonymList);
    }

    protected boolean isSynonymAllowedSchema(DfProcedureSynonymMetaInfo procedureSynonymMetaInfo) {
        final DfSynonymMetaInfo synonymMetaInfo = procedureSynonymMetaInfo.getSynonymMetaInfo();
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
     * @return The extractor of procedure synonym. (Nullable)
     */
    protected DfProcedureSynonymExtractor createProcedureSynonymExtractor() {
        final DfProcedureSynonymExtractorFactory factory = new DfProcedureSynonymExtractorFactory(
                _procedureSynonymDataSource, getBasicProperties(), getProperties().getDatabaseProperties());
        return factory.createSynonymExtractor();
    }

    // -----------------------------------------------------
    //                                    Filter by Property
    //                                    ------------------
    protected List<DfProcedureMetaInfo> filterByProperty(List<DfProcedureMetaInfo> procedureList) {
        if (_suppressFilterByProperty) {
            return procedureList;
        }
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        final List<DfProcedureMetaInfo> resultList = new ArrayList<DfProcedureMetaInfo>();
        _log.info("...Filtering procedures by the property: before=" + procedureList.size());
        int passedCount = 0;
        for (DfProcedureMetaInfo metaInfo : procedureList) {
            final String procedureDisplayName = metaInfo.getProcedureDisplayName();
            final String procedureCatalog = metaInfo.getProcedureCatalog();
            if (!outsideSqlProperties.isTargetProcedureCatalog(procedureCatalog)) {
                _log.info("  passed: non-target catalog - " + procedureDisplayName);
                ++passedCount;
                continue;
            }
            final UnifiedSchema procedureSchema = metaInfo.getProcedureSchema();
            if (!outsideSqlProperties.isTargetProcedureSchema(procedureSchema.getPureSchema())) {
                _log.info("  passed: non-target schema - " + procedureDisplayName);
                ++passedCount;
                continue;
            }
            final String procedureFullQualifiedName = metaInfo.getProcedureFullQualifiedName();
            final String procedureSchemaQualifiedName = Srl.substringFirstFront(procedureFullQualifiedName, ".");
            final String procedureName = metaInfo.getProcedureName();
            if (!outsideSqlProperties.isTargetProcedureName(procedureFullQualifiedName)
                    && !outsideSqlProperties.isTargetProcedureName(procedureSchemaQualifiedName)
                    && !outsideSqlProperties.isTargetProcedureName(procedureName)) {
                _log.info("  passed: non-target name - " + procedureDisplayName);
                ++passedCount;
                continue;
            }
            resultList.add(metaInfo);
        }
        if (passedCount == 0) {
            _log.info("  --> All procedures are target: count=" + procedureList.size());
        }
        return resultList;
    }

    // -----------------------------------------------------
    //                                   Duplicate Procedure
    //                                   -------------------
    protected boolean handleDuplicateProcedure(DfProcedureMetaInfo metaInfo,
            Map<String, DfProcedureMetaInfo> procdureMap, UnifiedSchema schemaName) {
        final String procedureUniqueName = metaInfo.getProcedureFullQualifiedName();
        final DfProcedureMetaInfo first = procdureMap.get(procedureUniqueName);
        if (first == null) {
            return false;
        }
        final UnifiedSchema firstSchema = first.getProcedureSchema();
        final UnifiedSchema secondSchema = metaInfo.getProcedureSchema();
        // Basically select the one of main schema.
        // If both are additional schema, it selects first. 
        if (!firstSchema.equals(secondSchema) && firstSchema.equals(schemaName)) {
            showDuplicateProcedure(first, metaInfo, true, "main schema");
            return true;
        } else if (!secondSchema.equals(firstSchema) && secondSchema.equals(schemaName)) {
            procdureMap.remove(procedureUniqueName);
            showDuplicateProcedure(first, metaInfo, false, "main schema");
            return false;
        } else {
            showDuplicateProcedure(first, metaInfo, true, "first one");
            return true;
        }
    }

    protected void showDuplicateProcedure(DfProcedureMetaInfo first, DfProcedureMetaInfo second, boolean electFirst,
            String reason) {
        final String firstName = first.getProcedureDisplayName();
        final String secondName = second.getProcedureDisplayName();
        final String firstType = first.isProcedureSynonym() ? "(synonym)" : "";
        final String secondType = second.isProcedureSynonym() ? "(synonym)" : "";
        String msg = "*Found the same-name procedure, so elects " + reason + ":";
        if (electFirst) {
            msg = msg + " elect=" + firstName + firstType + " skipped=" + secondName + secondType;
        } else {
            msg = msg + " elect=" + secondName + secondType + " skipped=" + firstName + firstType;
        }
        _log.info(msg);
    }

    // ===================================================================================
    //                                                                     Plain Procedure
    //                                                                     ===============
    /**
     * Get the list of plain procedures. <br />
     * It selects procedures of main schema only.
     * @param metaData The meta data of database. (NotNull)
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (Nullable)
     * @return The list of procedure meta information. (NotNull)
     */
    public List<DfProcedureMetaInfo> getPlainProcedureList(DatabaseMetaData metaData, UnifiedSchema unifiedSchema)
            throws SQLException {
        final List<DfProcedureMetaInfo> metaInfoList = new ArrayList<DfProcedureMetaInfo>();
        ResultSet columnResultSet = null;
        try {
            final String catalogName = unifiedSchema.getPureCatalog();
            final String schemaName = unifiedSchema.getPureSchema();
            final ResultSet procedureRs = metaData.getProcedures(catalogName, schemaName, null);
            setupProcedureMetaInfo(metaInfoList, procedureRs, unifiedSchema);
            for (DfProcedureMetaInfo procedureMetaInfo : metaInfoList) {
                String procedureName = procedureMetaInfo.getProcedureName();
                final ResultSet columnRs = metaData.getProcedureColumns(catalogName, schemaName, procedureName, null);
                setupProcedureColumnMetaInfo(procedureMetaInfo, columnRs);
            }
        } catch (SQLException e) {
            String msg = "Failed to get a list of procedures:";
            msg = msg + " unifiedSchema=" + unifiedSchema;
            throw new DfJDBCException(msg, e);
        } finally {
            if (columnResultSet != null) {
                try {
                    columnResultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return metaInfoList;
    }

    protected void setupProcedureMetaInfo(List<DfProcedureMetaInfo> procedureMetaInfoList, ResultSet procedureRs,
            UnifiedSchema unifiedSchema) throws SQLException {
        while (procedureRs.next()) {
            final String procedureSchema = procedureRs.getString("PROCEDURE_SCHEM");
            final String procedureCatalog;
            final String procedureName;
            {
                final String plainCatalog = procedureRs.getString("PROCEDURE_CAT");
                String packagePrefix = "";
                if (isOracle()) {
                    packagePrefix = plainCatalog + ".";
                    procedureCatalog = null; // because Oracle treats catalog as package
                } else {
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(plainCatalog)) {
                        procedureCatalog = plainCatalog;
                    } else {
                        procedureCatalog = unifiedSchema.getPureCatalog();
                    }
                }
                final String plainName = procedureRs.getString("PROCEDURE_NAME");
                procedureName = packagePrefix + plainName;
            }
            final Integer procedureType = new Integer(procedureRs.getString("PROCEDURE_TYPE"));
            final String procedureComment = procedureRs.getString("REMARKS");

            final DfProcedureMetaInfo metaInfo = new DfProcedureMetaInfo();
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
                throw new IllegalStateException("Unknown procedureType: " + procedureType);
            }
            metaInfo.setProcedureComment(procedureComment);
            metaInfo.setProcedureFullQualifiedName(buildProcedureFullQualifiedName(metaInfo));
            metaInfo.setProcedureSchemaQualifiedName(buildProcedureSchemaQualifiedName(metaInfo));
            metaInfo.setProcedureSqlName(buildProcedureSqlName(metaInfo));
            metaInfo.setProcedureDisplayName(metaInfo.getProcedureSqlName());
            procedureMetaInfoList.add(metaInfo);
        }
    }

    protected void setupProcedureColumnMetaInfo(DfProcedureMetaInfo procedureMetaInfo, ResultSet columnRs)
            throws SQLException {
        final Set<String> uniqueSet = new HashSet<String>();
        while (columnRs.next()) {
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
                procedureColumnType = columnType != null ? new Integer(columnType) : unknowType;
            }
            final Integer jdbcType;
            {
                final String dataType = columnRs.getString("DATA_TYPE");
                jdbcType = dataType != null ? new Integer(dataType) : Types.OTHER;
            }
            final String dbTypeName = columnRs.getString("TYPE_NAME");
            final Integer columnSize;
            {
                final String precision = columnRs.getString("PRECISION");
                if (precision != null && precision.trim().length() != 0) {
                    columnSize = new Integer(precision);
                } else {
                    final String length = columnRs.getString("LENGTH");
                    columnSize = length != null ? new Integer(length) : null;
                }
            }
            final Integer decimalDigits;
            {
                final String scale = columnRs.getString("SCALE");
                decimalDigits = scale != null ? new Integer(scale) : null;
            }
            final String columnComment = columnRs.getString("REMARKS");

            final DfProcedureColumnMetaInfo procedureColumnMetaInfo = new DfProcedureColumnMetaInfo();
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
            procedureColumnMetaInfo.setJdbcType(jdbcType);
            procedureColumnMetaInfo.setDbTypeName(dbTypeName);
            procedureColumnMetaInfo.setColumnSize(columnSize);
            procedureColumnMetaInfo.setDecimalDigits(decimalDigits);
            procedureColumnMetaInfo.setColumnComment(columnComment);
            procedureMetaInfo.addProcedureColumnMetaInfo(procedureColumnMetaInfo);
        }
        adjustProcedureColumnList(procedureMetaInfo);
    }

    protected String buildProcedureSqlName(DfProcedureMetaInfo metaInfo) {
        final String sqlName = metaInfo.getProcedureSchema().buildSqlName(metaInfo.getProcedureName());
        // DB2 needs schema prefix for calling procedures. (actually tried)
        if (isDB2() && !sqlName.contains(".")) {
            return Srl.connectPrefix(sqlName, metaInfo.getProcedureSchema().getPureSchema(), ".");
        } else {
            return sqlName;
        }
    }

    protected String buildProcedureFullQualifiedName(DfProcedureMetaInfo metaInfo) {
        return metaInfo.getProcedureSchema().buildFullQualifiedName(metaInfo.getProcedureName());
    }

    protected String buildProcedureSchemaQualifiedName(DfProcedureMetaInfo metaInfo) {
        return metaInfo.getProcedureSchema().buildSchemaQualifiedName(metaInfo.getProcedureName());
    }

    protected void adjustProcedureColumnList(DfProcedureMetaInfo procedureMetaInfo) {
        adjustPostgreSQLResultSetParameter(procedureMetaInfo);
    }

    protected void adjustPostgreSQLResultSetParameter(DfProcedureMetaInfo procedureMetaInfo) {
        if (!isPostgreSQL()) {
            return;
        }
        final List<DfProcedureColumnMetaInfo> columnMetaInfoList = procedureMetaInfo.getProcedureColumnList();
        boolean existsResultSetParameter = false;
        boolean existsResultSetReturn = false;
        int resultSetReturnIndex = 0;
        String resultSetReturnName = null;
        int index = 0;
        for (DfProcedureColumnMetaInfo columnMetaInfo : columnMetaInfoList) {
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
            String name = procedureMetaInfo.getProcedureDisplayName() + "." + resultSetReturnName;
            _log.info("...Removing the result set return which is unnecessary: " + name);
            columnMetaInfoList.remove(resultSetReturnIndex);
        }
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

    public void includeProcedureSynonym(DataSource dataSource) {
        _procedureSynonymDataSource = dataSource;
    }
}