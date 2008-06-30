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
package org.seasar.dbflute.helper.jdbc.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.DfBuildProperties;

/**
 * @author jflute
 * @since 0.7.5 (2008/06/28 Saturday)
 */
public class DfProcedureHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                        Meta Getting
    //                                                                        ============
    public List<DfProcedureMetaInfo> getProcedures(DatabaseMetaData metaData, String schemaName) {
        // /- - - - - - - - - - - - - - - - - - - - - -
        // Set up default schema name of PostgreSQL.
        // Because PostgreSQL returns system procedures.
        // - - - - - - - - - -/
        if (isPostgreSQL() && (schemaName == null || schemaName.trim().length() == 0)) {
            schemaName = "public";
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
            final String procedureName = procedureRs.getString("PROCEDURE_NAME");
            final Integer procedureType = new Integer(procedureRs.getString("PROCEDURE_TYPE"));
            final String procedureComment = procedureRs.getString("REMARKS");

            // /- - - - - - - - - - - - - - - - - - - - - -
            // Remove system procedures of PostgreSQL.
            // Because PostgreSQL returns system procedures.
            // - - - - - - - - - -/
            if (isPostgreSQL() && procedureName.toLowerCase().startsWith("pldbg")) {
                continue;
            }

            final DfProcedureMetaInfo metaInfo = new DfProcedureMetaInfo();
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
            final Integer procedureColumnType = new Integer(columnRs.getString("COLUMN_TYPE"));
            final Integer jdbcType = new Integer(columnRs.getString("DATA_TYPE"));
            final String dbTypeName = columnRs.getString("TYPE_NAME");
            final String length = columnRs.getString("LENGTH");
            final Integer columnSize = length != null ? new Integer(length) : null;
            final String precision = columnRs.getString("PRECISION");
            final Integer decimalDigits = precision != null ? new Integer(precision) : null;
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

    protected boolean isPostgreSQL() {
        return DfBuildProperties.getInstance().getBasicProperties().isDatabasePostgreSQL();
    }

    // ===================================================================================
    //                                                                 Procedure Meta Info
    //                                                                 ===================
    public static class DfProcedureMetaInfo {
        protected String procedureName;
        protected String procedureComment;
        protected DfProcedureType procedureType;
        protected List<DfProcedureColumnMetaInfo> procedureColumnMetaInfoList = new ArrayList<DfProcedureColumnMetaInfo>();

        public String getProcedureName() {
            return procedureName;
        }

        public void setProcedureName(String procedureName) {
            this.procedureName = procedureName;
        }

        public DfProcedureType getProcedureType() {
            return procedureType;
        }

        public void setProcedureType(DfProcedureType procedureType) {
            this.procedureType = procedureType;
        }

        public String getProcedureComment() {
            return procedureComment;
        }

        public void setProcedureComment(String procedureComment) {
            this.procedureComment = procedureComment;
        }

        public List<DfProcedureColumnMetaInfo> getProcedureColumnMetaInfoList() {
            return procedureColumnMetaInfoList;
        }

        public void addProcedureColumnMetaInfo(DfProcedureColumnMetaInfo procedureColumnMetaInfo) {
            procedureColumnMetaInfoList.add(procedureColumnMetaInfo);
        }

        @Override
        public String toString() {
            return "{" + procedureName + ", " + procedureType + ", " + procedureComment + ", "
                    + procedureColumnMetaInfoList + "}";
        }
    }

    public static class DfProcedureColumnMetaInfo {
        protected String columnName;
        protected int jdbcType;
        protected String dbTypeName;
        protected Integer columnSize;
        protected Integer decimalDigits;
        protected String columnComment;
        protected DfProcedureColumnType procedureColumnType;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public DfProcedureColumnType getProcedureColumnType() {
            return procedureColumnType;
        }

        public void setProcedureColumnType(DfProcedureColumnType procedureColumnType) {
            this.procedureColumnType = procedureColumnType;
        }

        public int getJdbcType() {
            return jdbcType;
        }

        public void setJdbcType(int jdbcType) {
            this.jdbcType = jdbcType;
        }

        public String getDbTypeName() {
            return dbTypeName;
        }

        public void setDbTypeName(String dbTypeName) {
            this.dbTypeName = dbTypeName;
        }

        public Integer getColumnSize() {
            return columnSize;
        }

        public void setColumnSize(Integer columnSize) {
            this.columnSize = columnSize;
        }

        public Integer getDecimalDigits() {
            return decimalDigits;
        }

        public void setDecimalDigits(Integer decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        public String getColumnComment() {
            return columnComment;
        }

        public void setColumnComment(String columnComment) {
            this.columnComment = columnComment;
        }

        @Override
        public String toString() {
            return "{" + columnName + ", " + procedureColumnType + ", " + jdbcType + ", " + dbTypeName + "("
                    + columnSize + ", " + decimalDigits + ")" + columnComment + "}";
        }
    }

    public enum DfProcedureType {
        procedureResultUnknown, procedureNoResult, procedureReturnsResult
    }

    public enum DfProcedureColumnType {
        procedureColumnUnknown, procedureColumnIn, procedureColumnInOut, procedureColumnOut, procedureColumnReturn, procedureColumnResult
    }
}