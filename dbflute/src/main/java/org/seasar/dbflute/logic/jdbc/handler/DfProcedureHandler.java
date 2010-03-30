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

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.logic.factory.DfProcedureSynonymExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureClosetResultMetaInfo;
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
import org.seasar.dbflute.util.DfTypeUtil;

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
     * @param dataSource The data source for getting meta data. (NotNull)
     * @return The map of available procedure meta informations. (NotNull)
     * @throws SQLException
     */
    public Map<String, DfProcedureMetaInfo> getAvailableProcedureMap(DataSource dataSource) throws SQLException {
        return getAvailableProcedureMap(dataSource, false);
    }

    /**
     * Get the map of available meta information. <br />
     * The map key is procedure unique name.
     * @param dataSource The data source for getting meta data. (NotNull)
     * @param execution Does it get execution meta data?
     * @return The map of available procedure meta informations. (NotNull)
     * @throws SQLException
     */
    public Map<String, DfProcedureMetaInfo> getAvailableProcedureMap(DataSource dataSource, boolean execution)
            throws SQLException {
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final String schemaName = databaseProperties.getDatabaseSchema();
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        if (!outsideSqlProperties.isGenerateProcedureParameterBean()) {
            return newLinkedHashMap();
        }
        final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();

        // main schema
        final List<DfProcedureMetaInfo> procedureList = getPlainProcedureList(metaData, schemaName);

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
            if (handleDuplicateProcedure(metaInfo, procedureHandlingMap, schemaName)) {
                continue;
            }
            procedureHandlingMap.put(metaInfo.getProcedureUniqueName(), metaInfo);
        }

        // arrange order (additional schema after main schema)
        final Map<String, DfProcedureMetaInfo> procedureOrderedMap = newLinkedHashMap();
        final Map<String, DfProcedureMetaInfo> additionalSchemaProcedureMap = newLinkedHashMap();
        final Set<Entry<String, DfProcedureMetaInfo>> entrySet = procedureHandlingMap.entrySet();
        for (Entry<String, DfProcedureMetaInfo> entry : entrySet) {
            final String key = entry.getKey();
            final DfProcedureMetaInfo metaInfo = entry.getValue();
            if (databaseProperties.isAdditionalSchema(metaInfo.getProcedureSchema())) {
                additionalSchemaProcedureMap.put(key, metaInfo);
            } else {
                procedureOrderedMap.put(key, metaInfo); // main schema
            }
        }
        procedureOrderedMap.putAll(additionalSchemaProcedureMap);
        if (execution) {
            try {
                processExecutionMetaData(dataSource, procedureList);
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
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
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        final String mainSchemaName = databaseProperties.getDatabaseSchema();
        _log.info("...Adding procedure synonyms as procedure: count=" + procedureSynonymMap.size());
        final Set<Entry<String, DfProcedureSynonymMetaInfo>> entrySet = procedureSynonymMap.entrySet();
        final List<DfProcedureMetaInfo> procedureSynonymList = new ArrayList<DfProcedureMetaInfo>();
        for (Entry<String, DfProcedureSynonymMetaInfo> entry : entrySet) {
            final DfProcedureSynonymMetaInfo metaInfo = entry.getValue();
            if (!isSynonymAllowedSchema(metaInfo)) {
                continue;
            }

            // merge synonym to procedure (create copied instance)
            final String beforeName = metaInfo.getProcedureMetaInfo().getProcedureFullName();
            final DfProcedureMetaInfo mergedProcedure = metaInfo.createMergedProcedure(mainSchemaName);
            final String afterName = mergedProcedure.getProcedureFullName();
            _log.info("  " + beforeName + " to " + afterName);

            procedureSynonymList.add(mergedProcedure);
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

    // -----------------------------------------------------
    //                                   Execution Meta Data
    //                                   -------------------
    protected void processExecutionMetaData(DataSource dataSource, List<DfProcedureMetaInfo> procedureList)
            throws SQLException {
        for (DfProcedureMetaInfo procedure : procedureList) {
            doProcessExecutionMetaData(dataSource, procedure);
        }
    }

    protected void doProcessExecutionMetaData(DataSource dataSource, DfProcedureMetaInfo procedure) throws SQLException {
        final List<Object> numberList = getProperties().getTypeMappingProperties().getJavaNativeNumberList();
        final List<Object> dateList = getProperties().getTypeMappingProperties().getJavaNativeDateList();
        final List<Object> booleanList = getProperties().getTypeMappingProperties().getJavaNativeBooleanList();
        final List<Object> binaryList = getProperties().getTypeMappingProperties().getJavaNativeBinaryList();
        final String procedureFullName = procedure.getProcedureFullName();
        final List<DfProcedureColumnMetaInfo> columnList = procedure.getProcedureColumnMetaInfoList();
        final List<Object> testValueList = new ArrayList<Object>();
        boolean existsReturn = false;
        for (DfProcedureColumnMetaInfo column : columnList) {
            final DfProcedureColumnType columnType = column.getProcedureColumnType();
            if (DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
                existsReturn = true;
                continue;
            }
            if (DfProcedureColumnType.procedureColumnIn.equals(columnType)
                    || DfProcedureColumnType.procedureColumnInOut.equals(columnType)) {
                final int jdbcDefType = column.getJdbcType();
                final Integer columnSize = column.getColumnSize();
                final Integer decimalDigits = column.getDecimalDigits();
                final String jdbcType = TypeMap.findJdbcTypeByJdbcDefValue(jdbcDefType);
                final String nativeType = TypeMap.findJavaNativeByJdbcType(jdbcType, columnSize, decimalDigits);
                Object testValue = null;
                if (containsAsEndsWith(nativeType, numberList)) {
                    testValue = 0;
                } else if (containsAsEndsWith(nativeType, dateList)) {
                    testValue = DfTypeUtil.toDate("2010-03-30");
                } else if (containsAsEndsWith(nativeType, booleanList)) {
                    testValue = Boolean.FALSE;
                } else if (containsAsEndsWith(nativeType, binaryList)) {
                    return; // binary type is unsupported here
                } else { // as String
                    testValue = "0";
                }
                testValueList.add(testValue);
            }
        }
        final String sql = createSql(procedureFullName, columnList.size(), existsReturn);
        CallableStatement cs = null;
        try {
            cs = dataSource.getConnection().prepareCall(sql);
            final List<DfProcedureColumnMetaInfo> boundColumnList = new ArrayList<DfProcedureColumnMetaInfo>();
            setupBindParameter(cs, columnList, testValueList, boundColumnList);
            ResultSet rs = null;
            _log.info("...Calling: " + sql);
            if (cs.execute()) {
                int closetIndex = 0;
                do {
                    rs = cs.getResultSet();
                    if (rs == null) {
                        break;
                    }
                    // TODO jflute - making customize entity
                    //final ResultSetMetaData metaData = rs.getMetaData();
                    //final int columnCount = metaData.getColumnCount();
                    //for (int i = 0; i < columnCount; i++) {
                    //    final String columnLabel = metaData.getColumnLabel(i + 1);
                    //}
                    final DfProcedureClosetResultMetaInfo metaInfo = new DfProcedureClosetResultMetaInfo();
                    metaInfo.setPropertyName("closetResult" + (closetIndex + 1));
                    procedure.addClosetResultMetaInfo(metaInfo);
                    ++closetIndex;
                } while (cs.getMoreResults());
            }
            int index = 0;
            for (DfProcedureColumnMetaInfo column : boundColumnList) {
                final DfProcedureColumnType columnType = column.getProcedureColumnType();
                if (DfProcedureColumnType.procedureColumnIn.equals(columnType)) {
                    ++index;
                    continue;
                }
                //final Object obj = cs.getObject(index + 1);
                //if (obj instanceof ResultSet) {
                //    rs = (ResultSet) obj;
                //    final ResultSetMetaData metaData = rs.getMetaData();
                //    final int columnCount = metaData.getColumnCount();
                //    for (int i = 0; i < columnCount; i++) {
                //        final String columnLabel = metaData.getColumnLabel(i + 1);
                //    }
                //}
                ++index;
            }
        } catch (SQLException e) {
            String msg = "*Failed to execute the procedure for getting meta data:" + ln();
            msg = msg + " " + sql;
            msg = msg + " " + e.getMessage();
            _log.info(msg); // continued
        } finally {
            if (cs != null) {
                cs.close();
            }
        }
    }

    protected boolean containsAsEndsWith(String str, List<Object> ls) {
        for (Object current : ls) {
            final String currentString = (String) current;
            if (str.endsWith(currentString)) {
                return true;
            }
        }
        return false;
    }

    public String createSql(String procedureName, int bindSize, boolean existsReturn) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        final int argSize;
        {
            if (existsReturn) {
                sb.append("? = ");
                argSize = bindSize - 1;
            } else {
                argSize = bindSize;
            }
        }
        sb.append("call ").append(procedureName).append("(");
        for (int i = 0; i < argSize; i++) {
            sb.append("?, ");
        }
        if (argSize > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")}");
        return sb.toString();
    }

    protected void setupBindParameter(CallableStatement cs, List<DfProcedureColumnMetaInfo> columnList,
            List<Object> testValueList, List<DfProcedureColumnMetaInfo> boundColumnList) throws SQLException {
        int index = 0;
        for (DfProcedureColumnMetaInfo column : columnList) {
            final DfProcedureColumnType columnType = column.getProcedureColumnType();
            if (DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
                cs.registerOutParameter(index + 1, column.getJdbcType());
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnIn.equals(columnType)) {
                cs.setObject(index + 1, testValueList.remove(0));
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnOut.equals(columnType)) {
                cs.registerOutParameter(index + 1, column.getJdbcType());
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnInOut.equals(columnType)) {
                cs.registerOutParameter(index + 1, column.getJdbcType());
                cs.setObject(index + 1, testValueList.remove(0));
                boundColumnList.add(column);
            }
            ++index;
        }
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
        adjustProcedureColumnList(procedureMetaInfo);
    }

    protected String buildProcedureFullName(DfProcedureMetaInfo metaInfo) {
        return buildProcedureArrangeName(metaInfo, true, true);
    }

    protected String buildProcedureSqlName(DfProcedureMetaInfo metaInfo) {
        // DB2 needs schema prefix for calling procedures. (actually tried)
        final boolean includeMainSchema = isDB2();
        return buildProcedureArrangeName(metaInfo, true, includeMainSchema);
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

    protected void adjustProcedureColumnList(DfProcedureMetaInfo procedureMetaInfo) {
        adjustPostgreSQLResultSetParameter(procedureMetaInfo);
    }

    protected void adjustPostgreSQLResultSetParameter(DfProcedureMetaInfo procedureMetaInfo) {
        if (!isPostgreSQL()) {
            return;
        }
        final List<DfProcedureColumnMetaInfo> columnMetaInfoList = procedureMetaInfo.getProcedureColumnMetaInfoList();
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
            String name = procedureMetaInfo.getProcedureFullName() + "." + resultSetReturnName;
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