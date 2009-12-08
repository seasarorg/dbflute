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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

/**
 * @author jflute
 * @since 0.7.5 (2008/06/28 Saturday)
 */
public class DfProcedureHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfColumnHandler.class);

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
     * @param metaData The meta data of database. (NotNull)
     * @return The map of available procedure meta informations. (NotNull)
     * @throws SQLException
     */
    public Map<String, DfProcedureMetaInfo> getAvailableProcedureMap(DatabaseMetaData metaData) throws SQLException {
        final String schemaName = getProperties().getDatabaseProperties().getDatabaseSchema();
        final Map<String, DfProcedureMetaInfo> procedureMap = new LinkedHashMap<String, DfProcedureMetaInfo>();
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        if (!outsideSqlProperties.isGenerateProcedureParameterBean()) {
            return procedureMap;
        }
        Connection conn = null;
        try {
            // main schema
            final List<DfProcedureMetaInfo> procedureList = getPlainProcedureList(metaData, schemaName);

            // additional schema
            setupAdditionalSchemaProcedure(metaData, procedureList);

            // procedure synonym
            setupProcedureSynonym(procedureList);

            // filter by property
            final List<DfProcedureMetaInfo> filteredList = filterByProperty(procedureList);

            // create available procedure map
            for (DfProcedureMetaInfo metaInfo : filteredList) {
                // handle duplicate
                if (handleDuplicateProcedure(metaInfo, procedureMap, schemaName)) {
                    continue;
                }
                procedureMap.put(metaInfo.getProcedureUniqueName(), metaInfo);
            }
            return procedureMap;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
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
        final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = databaseProperties.getAdditionalSchemaMap();
        final Set<String> additionalSchemaSet = additionalSchemaMap.keySet();
        for (String additionalSchema : additionalSchemaSet) {
            final List<DfProcedureMetaInfo> additionalProcedureList = getPlainProcedureList(metaData, additionalSchema);
            for (DfProcedureMetaInfo metaInfo : additionalProcedureList) {
                final String procedureSchema = metaInfo.getProcedureSchema();
                if (procedureSchema == null || procedureSchema.trim().length() == 0) {
                    metaInfo.setProcedureSchema(additionalSchema);
                }
            }
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
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        _log.info("...Adding procedure synonyms as procedure: count=" + procedureSynonymMap.size());
        final Set<Entry<String, DfProcedureSynonymMetaInfo>> entrySet = procedureSynonymMap.entrySet();
        final List<DfProcedureMetaInfo> procedureSynonymList = new ArrayList<DfProcedureMetaInfo>();
        for (Entry<String, DfProcedureSynonymMetaInfo> entry : entrySet) {
            final DfProcedureSynonymMetaInfo procedureSynonymMetaInfo = entry.getValue();
            if (!isSynonymAllowedSchema(procedureSynonymMetaInfo)) {
                continue;
            }
            procedureSynonymMetaInfo.reflectSynonymToProcedure(databaseProperties.getDatabaseSchema());
            procedureSynonymList.add(procedureSynonymMetaInfo.getProcedureMetaInfo());
        }
        procedureList.addAll(procedureSynonymList);
    }

    protected boolean isSynonymAllowedSchema(DfProcedureSynonymMetaInfo procedureSynonymMetaInfo) {
        final DfSynonymMetaInfo synonymMetaInfo = procedureSynonymMetaInfo.getSynonymMetaInfo();
        final String synonymOwner = synonymMetaInfo.getSynonymOwner();
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final String mainSchema = databaseProperties.getDatabaseSchema();
        if (mainSchema != null && mainSchema.equalsIgnoreCase(synonymOwner)) {
            if (databaseProperties.hasObjectTypeSynonym()) {
                return true;
            }
        }
        final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = databaseProperties.getAdditionalSchemaMap();
        final DfAdditionalSchemaInfo additionalSchemaInfo = additionalSchemaMap.get(synonymOwner);
        if (additionalSchemaInfo != null && additionalSchemaInfo.hasObjectTypeSynonym()) {
            return true;
        }
        return false;
    }

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
            final String procedureFullName = buildProcedureFullName(metaInfo);
            final String procedureCatalog = metaInfo.getProcedureCatalog();
            if (!outsideSqlProperties.isTargetProcedureCatalog(procedureCatalog)) {
                _log.info("  passed: non-target catalog - " + procedureFullName);
                ++passedCount;
                continue;
            }
            final String procedureSchema = metaInfo.getProcedureSchema();
            if (!outsideSqlProperties.isTargetProcedureSchema(procedureSchema)) {
                _log.info("  passed: non-target schema - " + procedureFullName);
                ++passedCount;
                continue;
            }
            if (!outsideSqlProperties.isTargetProcedureName(procedureFullName)) {
                final String procedureName = metaInfo.getProcedureName();
                if (!outsideSqlProperties.isTargetProcedureName(procedureName)) {
                    _log.info("  passed: non-target name - " + procedureFullName);
                    ++passedCount;
                    continue;
                }
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
            Map<String, DfProcedureMetaInfo> procdureMap, String schemaName) {
        final String procedureUniqueName = metaInfo.getProcedureUniqueName();
        final DfProcedureMetaInfo first = procdureMap.get(procedureUniqueName);
        if (first == null) {
            return false;
        }
        final String firstSchema = first.getProcedureSchema();
        final String secondSchema = metaInfo.getProcedureSchema();
        // Basically select the one of main schema.
        // If both are additional schema, it selects first. 
        if (firstSchema != null && !firstSchema.equalsIgnoreCase(secondSchema)
                && firstSchema.equalsIgnoreCase(schemaName)) {
            showDuplicateProcedure(first, metaInfo, true, "main schema");
            return true;
        } else if (secondSchema != null && !secondSchema.equalsIgnoreCase(firstSchema)
                && secondSchema.equalsIgnoreCase(schemaName)) {
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
        final String firstName = first.getProcedureFullName();
        final String secondName = second.getProcedureFullName();
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
     * @param schemaName The name of schema. (Nullable)
     * @return The list of procedure meta information. (NotNull)
     */
    public List<DfProcedureMetaInfo> getPlainProcedureList(DatabaseMetaData metaData, String schemaName)
            throws SQLException {
        schemaName = filterSchemaName(schemaName);

        // /- - - - - - - - - - - - - - - - - - - - - -
        // Set up default schema name of PostgreSQL.
        // Because PostgreSQL returns system procedures.
        // - - - - - - - - - -/
        if (isPostgreSQL()) {
            if (schemaName == null || schemaName.trim().length() == 0) {
                schemaName = "public";
            }
        }

        final List<DfProcedureMetaInfo> metaInfoList = new ArrayList<DfProcedureMetaInfo>();
        ResultSet columnResultSet = null;
        try {
            ResultSet procedureRs = metaData.getProcedures(null, schemaName, null);
            setupProcedureMetaInfo(metaInfoList, procedureRs);
            for (DfProcedureMetaInfo procedureMetaInfo : metaInfoList) {
                String procedureName = procedureMetaInfo.getProcedureName();
                ResultSet columnRs = metaData.getProcedureColumns(null, schemaName, procedureName, null);
                setupProcedureColumnMetaInfo(procedureMetaInfo, columnRs);
            }
        } catch (SQLException e) {
            String msg = "Failed to get a list of procedures:";
            msg = msg + " schemaName=" + schemaName;
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

    protected void setupProcedureMetaInfo(List<DfProcedureMetaInfo> procedureMetaInfoList, ResultSet procedureRs)
            throws SQLException {
        while (procedureRs.next()) {
            final String procedureCatalog = procedureRs.getString("PROCEDURE_CAT");
            final String procedureSchema = procedureRs.getString("PROCEDURE_SCHEM");
            final String procedureName = procedureRs.getString("PROCEDURE_NAME");
            final Integer procedureType = new Integer(procedureRs.getString("PROCEDURE_TYPE"));
            final String procedureComment = procedureRs.getString("REMARKS");

            // /- - - - - - - - - - - - - - - - - - - - - -
            // Remove system procedures of PostgreSQL.
            // Because PostgreSQL returns system procedures.
            // - - - - - - - - - -/
            if (isPostgreSQL()) {
                if (procedureName != null && procedureName.toLowerCase().startsWith("pl")) {
                    continue;
                }
            }

            final DfProcedureMetaInfo metaInfo = new DfProcedureMetaInfo();
            metaInfo.setProcedureCatalog(procedureCatalog);
            metaInfo.setProcedureSchema(procedureSchema);
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
            metaInfo.setProcedureFullName(buildProcedureFullName(metaInfo));
            metaInfo.setProcedureSqlName(buildProcedureSqlName(metaInfo));
            metaInfo.setProcedureUniqueName(buildProcedureUniqueName(metaInfo));
            procedureMetaInfoList.add(metaInfo);
        }
    }

    protected void setupProcedureColumnMetaInfo(DfProcedureMetaInfo procedureMetaInfo, ResultSet columnRs)
            throws SQLException {
        while (columnRs.next()) {
            final String columnName = columnRs.getString("COLUMN_NAME");
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
    }

    protected String buildProcedureFullName(DfProcedureMetaInfo metaInfo) {
        return buildProcedureArrangeName(metaInfo, true, true);
    }

    protected String buildProcedureSqlName(DfProcedureMetaInfo metaInfo) {
        return buildProcedureArrangeName(metaInfo, true, false);
    }

    protected String buildProcedureUniqueName(DfProcedureMetaInfo metaInfo) {
        return buildProcedureArrangeName(metaInfo, false, false);
    }

    protected String buildProcedureArrangeName(DfProcedureMetaInfo metaInfo, boolean includeSchema,
            boolean includeMainSchema) {
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final StringBuilder sb = new StringBuilder();
        if (includeSchema) {
            final String procedureSchema = metaInfo.getProcedureSchema();
            if (procedureSchema != null && procedureSchema.trim().length() > 0) {
                if (includeMainSchema) {
                    sb.append(procedureSchema).append(".");
                } else {
                    if (databaseProperties.isAdditionalSchema(procedureSchema)) {
                        sb.append(procedureSchema).append(".");
                    }
                }
            }
        }
        final String procedureCatalog = metaInfo.getProcedureCatalog();
        if (procedureCatalog != null && procedureCatalog.trim().length() > 0) {
            // It needs to confirm other DB...
            if (getBasicProperties().isDatabaseOracle()) {
                sb.append(procedureCatalog).append("."); // a catalog is package if Oracle
            }
        }
        final String procedureName = metaInfo.getProcedureName();
        return sb.append(procedureName).toString();
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