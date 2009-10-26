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
package org.seasar.dbflute.logic.metahandler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfProcedureMetaInfo.DfProcedureType;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;

/**
 * @author jflute
 * @since 0.7.5 (2008/06/28 Saturday)
 */
public class DfProcedureHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                        Meta Getting
    //                                                                        ============
    /**
     * Get the list of target procedure meta information that contains additional schema.
     * @param metaData The meta data of database. (NotNull)
     * @param schemaName The name of main schema. (Nullable)
     * @return The list of target procedure meta information.. (NotNull)
     */
    public List<DfProcedureMetaInfo> getAvailableProcedureList(DatabaseMetaData metaData, String schemaName)
            throws SQLException {
        final DfOutsideSqlProperties outsideSqlProperties = getProperties().getOutsideSqlProperties();
        if (!outsideSqlProperties.isGenerateProcedureParameterBean()) {
            return new ArrayList<DfProcedureMetaInfo>();
        }
        final DfDatabaseProperties databaseProperties = getProperties().getDatabaseProperties();
        Connection conn = null;
        try {
            // main schema
            final List<DfProcedureMetaInfo> procedures = getPlainProcedureList(metaData, schemaName);

            // additional schema
            final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = databaseProperties.getAdditionalSchemaMap();
            final Set<String> additionalSchemaSet = additionalSchemaMap.keySet();
            for (String additionalSchema : additionalSchemaSet) {
                final List<DfProcedureMetaInfo> additionalProcedureList = getPlainProcedureList(metaData,
                        additionalSchema);
                for (DfProcedureMetaInfo metaInfo : additionalProcedureList) {
                    final String procedureSchema = metaInfo.getProcedureSchema();
                    if (procedureSchema == null || procedureSchema.trim().length() == 0) {
                        metaInfo.setProcedureSchema(additionalSchema);
                    }
                }
                procedures.addAll(additionalProcedureList);
            }
            final List<DfProcedureMetaInfo> resultList = new ArrayList<DfProcedureMetaInfo>();
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
                resultList.add(metaInfo);
            }
            return procedures;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public List<DfProcedureMetaInfo> getPlainProcedureList(DatabaseMetaData metaData, String schemaName) {
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
            String msg = "SQLException occured: schemaName=" + schemaName;
            throw new IllegalStateException(msg);
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
                final String length = columnRs.getString("LENGTH");
                columnSize = length != null ? new Integer(length) : null;
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
}