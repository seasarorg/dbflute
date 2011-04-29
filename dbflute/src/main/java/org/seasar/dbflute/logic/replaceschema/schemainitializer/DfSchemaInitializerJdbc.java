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
package org.seasar.dbflute.logic.replaceschema.schemainitializer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfForeignKeyExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfProcedureExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfTableExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMeta;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;

/**
 * The schema initializer with JDBC.
 * @author jflute
 */
public class DfSchemaInitializerJdbc implements DfSchemaInitializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSchemaInitializerJdbc.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected UnifiedSchema _unifiedSchema;
    protected boolean _useFullQualifiedTableName;
    protected List<String> _dropObjectTypeList;
    protected List<String> _dropTableTargetList;
    protected List<String> _dropTableExceptList;
    protected boolean _dropGenerateTableOnly;
    protected boolean _dropGenerateProcedureOnly;
    protected StringSet _droppedPackageSet = StringSet.createAsCaseInsensitive();

    // /= = = = = = = = = = = = =
    // Detail execution handling!
    // = = = = = = = = = =/
    protected boolean _suppressTruncateTable;
    protected boolean _suppressDropForeignKey;
    protected boolean _suppressDropTable;
    protected boolean _suppressDropSequence;
    protected boolean _suppressDropProcedure;
    protected boolean _suppressDropDBLink;

    // ===================================================================================
    //                                                                   Initialize Schema
    //                                                                   =================
    public void initializeSchema() {
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final List<DfTableMeta> tableMetaInfoList;
            try {
                final DatabaseMetaData metaData = conn.getMetaData();
                final DfTableExtractor tableNameHandler = new DfTableExtractor() {
                    @Override
                    protected String[] getRealObjectTypeTargetArray(UnifiedSchema unifiedSchema) {
                        if (_dropObjectTypeList != null) {
                            return _dropObjectTypeList.toArray(new String[] {});
                        } else {
                            return super.getRealObjectTypeTargetArray(unifiedSchema);
                        }
                    }

                    @Override
                    protected List<String> getRealTableExceptList(UnifiedSchema unifiedSchema) {
                        if (_dropTableExceptList != null) {
                            return _dropTableExceptList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableExceptList(unifiedSchema);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }

                    @Override
                    protected List<String> getRealTableTargetList(UnifiedSchema unifiedSchema) {
                        if (_dropTableTargetList != null) {
                            return _dropTableTargetList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableTargetList(unifiedSchema);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }
                };
                tableMetaInfoList = tableNameHandler.getTableList(metaData, _unifiedSchema);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            executeObject(conn, tableMetaInfoList);
        } catch (SQLException e) {
            String msg = "Failed to the initialize schema: " + _unifiedSchema;
            throw new SQLFailureException(msg, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("connection.close() threw the exception!", ignored);
                }
            }
        }
    }

    protected void executeObject(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        final boolean procedureBeforeTable = isDropProcedureBeforeTable();
        if (procedureBeforeTable) {
            executeProcedureProcess(conn, tableMetaInfoList);
        }
        executeTableProcess(conn, tableMetaInfoList);
        if (!procedureBeforeTable) { // basically here
            executeProcedureProcess(conn, tableMetaInfoList);
        }
        executeVariousProcess(conn, tableMetaInfoList);
    }

    protected void executeTableProcess(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        if (!_suppressTruncateTable) {
            truncateTableIfPossible(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress truncating tables");
        }
        if (!_suppressDropForeignKey) {
            dropForeignKey(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping foreign keys");
        }
        if (!_suppressDropTable) {
            dropTable(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping tables");
        }
        if (!_suppressDropSequence) {
            dropSequence(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping sequences");
        }
    }

    protected void executeProcedureProcess(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        if (!_suppressDropProcedure) {
            dropProcedure(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping procedures");
        }
    }

    protected void executeVariousProcess(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        if (!_suppressDropDBLink) {
            dropDBLink(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping DB links");
        }
        if (!_suppressTruncateTable || !_suppressDropProcedure) { // belongs to the two
            dropTypeObject(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping type objectss");
        }
    }

    protected boolean isDropProcedureBeforeTable() {
        return false;
    }

    // ===================================================================================
    //                                                                      Truncate Table
    //                                                                      ==============
    protected void truncateTableIfPossible(Connection connection, List<DfTableMeta> tableMetaInfoList) {
        final DfTruncateTableByJdbcCallback callback = new DfTruncateTableByJdbcCallback() {
            public String buildTruncateTableSql(DfTableMeta metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("truncate table ").append(filterTableName(metaInfo.getTableName()));
                return sb.toString();
            }
        };
        callbackTruncateTableByJdbc(connection, tableMetaInfoList, callback);
    }

    protected static interface DfTruncateTableByJdbcCallback {
        public String buildTruncateTableSql(DfTableMeta metaInfo);
    }

    protected void callbackTruncateTableByJdbc(Connection conn, List<DfTableMeta> tableMetaInfoList,
            DfTruncateTableByJdbcCallback callback) {
        for (DfTableMeta metaInfo : tableMetaInfoList) {
            final String truncateTableSql = callback.buildTruncateTableSql(metaInfo);
            Statement st = null;
            try {
                st = conn.createStatement();
                st.execute(truncateTableSql);
                _log.info(truncateTableSql);
            } catch (Exception e) {
                continue;
            } finally {
                closeStatement(st);
            }
        }
    }

    // ===================================================================================
    //                                                                    Drop Foreign Key
    //                                                                    ================
    protected void dropForeignKey(Connection connection, List<DfTableMeta> tableMetaInfoList) {
        final DfDropForeignKeyByJdbcCallback callback = new DfDropForeignKeyByJdbcCallback() {
            public String buildDropForeignKeySql(DfForeignKeyMeta metaInfo) {
                final String foreignKeyName = metaInfo.getForeignKeyName();
                final String localTableName = filterTableName(metaInfo.getLocalTableName());
                final StringBuilder sb = new StringBuilder();
                sb.append("alter table ").append(localTableName).append(" drop constraint ").append(foreignKeyName);
                return sb.toString();
            }
        };
        callbackDropForeignKeyByJdbc(connection, tableMetaInfoList, callback);
    }

    protected static interface DfDropForeignKeyByJdbcCallback {
        public String buildDropForeignKeySql(DfForeignKeyMeta metaInfo);
    }

    protected void callbackDropForeignKeyByJdbc(Connection conn, List<DfTableMeta> tableMetaInfoList,
            DfDropForeignKeyByJdbcCallback callback) {
        Statement st = null;
        try {
            st = conn.createStatement();
            for (DfTableMeta tableMetaInfo : tableMetaInfoList) {
                if (isSkipDropForeignKey(tableMetaInfo)) {
                    continue;
                }
                final DfForeignKeyExtractor handler = new DfForeignKeyExtractor() {

                    @Override
                    protected List<String> getRealTableExceptList(UnifiedSchema unifiedSchema) {
                        if (_dropTableExceptList != null) {
                            return _dropTableExceptList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableExceptList(unifiedSchema);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }

                    @Override
                    protected List<String> getRealTableTargetList(UnifiedSchema unifiedSchema) {
                        if (_dropTableTargetList != null) {
                            return _dropTableTargetList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableTargetList(unifiedSchema);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }
                };
                final DatabaseMetaData dbMetaData = conn.getMetaData();
                final Map<String, DfForeignKeyMeta> foreignKeyMetaInfoMap = handler.getForeignKeyMap(dbMetaData,
                        tableMetaInfo);
                final Set<String> keySet = foreignKeyMetaInfoMap.keySet();
                for (String foreignKeyName : keySet) {
                    final DfForeignKeyMeta foreignKeyMetaInfo = foreignKeyMetaInfoMap.get(foreignKeyName);
                    final String dropForeignKeySql = callback.buildDropForeignKeySql(foreignKeyMetaInfo);
                    _log.info(dropForeignKeySql);
                    st.execute(dropForeignKeySql);
                }
            }
        } catch (SQLException e) {
            String msg = "Failed to drop foreign keys!";
            throw new SQLFailureException(msg, e);
        } finally {
            closeStatement(st);
        }
    }

    protected boolean isSkipDropForeignKey(DfTableMeta tableMetaInfo) { // for sub class.
        return false;
    }

    // ===================================================================================
    //                                                                          Drop Table
    //                                                                          ==========
    protected void dropTable(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        List<DfTableMeta> viewList = new ArrayList<DfTableMeta>();
        List<DfTableMeta> otherList = new ArrayList<DfTableMeta>();
        for (DfTableMeta tableMetaInfo : tableMetaInfoList) {
            if (tableMetaInfo.isTableTypeView()) {
                viewList.add(tableMetaInfo);
            } else {
                otherList.add(tableMetaInfo);
            }
        }

        // Drop view and drop others
        final List<DfTableMeta> sortedList = new ArrayList<DfTableMeta>();
        sortedList.addAll(viewList);
        sortedList.addAll(otherList);

        callbackDropTableByJdbc(conn, sortedList, new DfDropTableByJdbcCallback() {
            public String buildDropTableSql(DfTableMeta metaInfo) {
                final StringBuilder sb = new StringBuilder();
                setupDropTable(sb, metaInfo);
                return sb.toString();
            }

            public String buildDropMaterializedViewSql(DfTableMeta metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("drop materialized view ").append(metaInfo.getTableName());
                return sb.toString();
            }
        });
    }

    protected void setupDropTable(StringBuilder sb, DfTableMeta metaInfo) {
        final String tableName = filterTableName(metaInfo.getTableName());
        if (metaInfo.isTableTypeView()) {
            sb.append("drop view ").append(tableName);
        } else {
            sb.append("drop table ").append(tableName);
        }
    }

    protected static interface DfDropTableByJdbcCallback {
        public String buildDropTableSql(DfTableMeta metaInfo);

        public String buildDropMaterializedViewSql(DfTableMeta metaInfo);
    }

    protected void callbackDropTableByJdbc(Connection conn, List<DfTableMeta> tableMetaInfoList,
            DfDropTableByJdbcCallback callback) {
        String currentSql = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            for (DfTableMeta metaInfo : tableMetaInfoList) {
                final String dropTableSql = callback.buildDropTableSql(metaInfo);
                currentSql = dropTableSql;
                _log.info(dropTableSql);
                try {
                    st.execute(dropTableSql);
                } catch (SQLException e) {
                    // = = = = = = = = = = = =
                    // for materialized view!
                    // = = = = = = = = = = = =
                    final String dropMaterializedViewSql = callback.buildDropMaterializedViewSql(metaInfo);
                    try {
                        st.execute(dropMaterializedViewSql);
                        _log.info("  (o) retry: " + dropMaterializedViewSql);
                    } catch (SQLException ignored) {
                        _log.info("  (x) retry: " + dropMaterializedViewSql);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Failed to drop the table: " + currentSql;
            throw new SQLFailureException(msg, e);
        } finally {
            closeStatement(st);
        }
    }

    // ===================================================================================
    //                                                                       Drop Sequence
    //                                                                       =============
    protected void dropSequence(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        // override if it needs
    }

    // ===================================================================================
    //                                                                      Drop Procedure
    //                                                                      ==============
    protected void dropProcedure(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        final DfProcedureExtractor handler = new DfProcedureExtractor();
        handler.suppressAdditionalSchema();
        handler.suppressLogging();
        DatabaseMetaData metaData;
        try {
            metaData = conn.getMetaData();
        } catch (SQLException e) {
            String msg = "Failed to get meta data of the connection:";
            msg = msg + " connection=" + conn;
            throw new SQLFailureException(msg, e);
        }
        final List<DfProcedureMeta> procedureList;
        try {
            if (_dropGenerateProcedureOnly) {
                procedureList = handler.getAvailableProcedureList(_dataSource);
            } else {
                procedureList = handler.getPlainProcedureList(_dataSource, metaData, _unifiedSchema);
            }
        } catch (SQLException e) {
            String msg = "Failed to get procedure meta data: " + _unifiedSchema;
            throw new IllegalStateException(msg, e);
        }
        callbackDropProcedureByJdbc(conn, procedureList, createDropProcedureByJdbcCallback());
    }

    protected DfDropProcedureByJdbcCallback createDropProcedureByJdbcCallback() {
        return new DfDropProcedureByJdbcCallback() {
            public String buildDropProcedureSql(DfProcedureMeta metaInfo) {
                return "drop procedure " + buildProcedureSqlName(metaInfo);
            }

            public String buildDropFunctionSql(DfProcedureMeta metaInfo) {
                return "drop function " + buildProcedureSqlName(metaInfo);
            }

            public String buildDropPackageSql(DfProcedureMeta metaInfo) {
                return "drop package " + metaInfo.getProcedurePackage();
            }
        };
    }

    protected String buildProcedureSqlName(DfProcedureMeta metaInfo) {
        // same reason as table, see filterTableName()
        return metaInfo.getProcedureName();
    }

    public static interface DfDropProcedureByJdbcCallback {
        String buildDropProcedureSql(DfProcedureMeta metaInfo);

        String buildDropFunctionSql(DfProcedureMeta metaInfo);

        String buildDropPackageSql(DfProcedureMeta metaInfo);
    }

    protected void callbackDropProcedureByJdbc(Connection conn, List<DfProcedureMeta> procedureMetaInfoList,
            DfDropProcedureByJdbcCallback callback) {
        String currentSql = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            for (DfProcedureMeta metaInfo : procedureMetaInfoList) {
                if (metaInfo.isPackageProcdure()) {
                    currentSql = callback.buildDropPackageSql(metaInfo);
                    handlePackageProcedure(metaInfo, st, currentSql);
                    continue;
                }
                final String dropProcedureSql = callback.buildDropProcedureSql(metaInfo);
                currentSql = dropProcedureSql;
                _log.info(dropProcedureSql);
                try {
                    st.execute(dropProcedureSql);
                } catch (SQLException e) {
                    final String dropFunctionSql = callback.buildDropFunctionSql(metaInfo);
                    try {
                        st.execute(dropFunctionSql);
                        _log.info("  (o) retry: " + dropFunctionSql);
                    } catch (SQLException ignored) {
                        _log.info("  (x) retry: " + dropFunctionSql);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Failed to drop the procedure: " + currentSql;
            throw new SQLFailureException(msg, e);
        } finally {
            closeStatement(st);
        }
    }

    protected void handlePackageProcedure(DfProcedureMeta metaInfo, Statement st, String sql) throws SQLException {
        final String procedurePackage = metaInfo.getProcedurePackage();
        if (_droppedPackageSet.contains(procedurePackage)) {
            return;
        }
        _log.info(sql);
        st.execute(sql);
        _droppedPackageSet.add(procedurePackage);
    }

    // ===================================================================================
    //                                                                        Drop DB Link
    //                                                                        ============
    protected void dropDBLink(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        // override if it needs
    }

    // ===================================================================================
    //                                                                    Drop Type Object
    //                                                                    ================
    protected void dropTypeObject(Connection conn, List<DfTableMeta> tableMetaInfoList) {
        // override if it needs
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String filterTableName(String tableName) { // not used when procedure
        // because additional drop uses an own connection
        // so it does not need to qualify names
        //if (_useFullQualifiedTableName && _unifiedSchema.hasSchema()) {
        //    tableName = _unifiedSchema.buildFullQualifiedName(tableName);
        //}
        DfLittleAdjustmentProperties prop = getLittleAdjustmentProperties();
        tableName = prop.quoteTableNameIfNeedsDirectUse(tableName);
        return tableName;
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return DfBuildProperties.getInstance().getLittleAdjustmentProperties();
    }

    protected void closeResource(ResultSet rs, Statement st) {
        closeResultSet(rs);
        closeStatement(st);
    }

    protected void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
                _log.info("rs.close() threw the exception!", ignored);
            }
        }
    }

    protected void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ignored) {
                _log.info("statement.close() threw the exception!", ignored);
            }
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void setUnifiedSchema(UnifiedSchema unifiedSchema) {
        _unifiedSchema = unifiedSchema;
    }

    public void setDropObjectTypeList(List<String> dropObjectTypeList) {
        _dropObjectTypeList = dropObjectTypeList;
    }

    public void setDropTableTargetList(List<String> dropTableTargetList) {
        _dropTableTargetList = dropTableTargetList;
    }

    public void setDropTableExceptList(List<String> dropTableExceptList) {
        _dropTableExceptList = dropTableExceptList;
    }

    public void setDropGenerateTableOnly(boolean dropGenerateTableOnly) {
        this._dropGenerateTableOnly = dropGenerateTableOnly;
    }

    public void setDropGenerateProcedureOnly(boolean dropGenerateProcedureOnly) {
        this._dropGenerateProcedureOnly = dropGenerateProcedureOnly;
    }

    // /= = = = = = = = = = = = =
    // Detail execution handling!
    // = = = = = = = = = =/

    public boolean isSuppressTruncateTable() {
        return _suppressTruncateTable;
    }

    public void setSuppressTruncateTable(boolean suppressTruncateTable) {
        this._suppressTruncateTable = suppressTruncateTable;
    }

    public boolean isSuppressDropForeignKey() {
        return _suppressDropForeignKey;
    }

    public void setSuppressDropForeignKey(boolean suppressDropForeignKey) {
        this._suppressDropForeignKey = suppressDropForeignKey;
    }

    public boolean isSuppressDropTable() {
        return _suppressDropTable;
    }

    public void setSuppressDropTable(boolean suppressDropTable) {
        this._suppressDropTable = suppressDropTable;
    }

    public boolean isSuppressDropSequence() {
        return _suppressDropSequence;
    }

    public void setSuppressDropSequence(boolean suppressDropSequence) {
        this._suppressDropSequence = suppressDropSequence;
    }

    public boolean isSuppressDropProcedure() {
        return _suppressDropProcedure;
    }

    public void setSuppressDropProcedure(boolean suppressDropProcedure) {
        this._suppressDropProcedure = suppressDropProcedure;
    }

    public boolean isSuppressDropDBLink() {
        return _suppressDropDBLink;
    }

    public void setSuppressDropDBLink(boolean suppressDropDBLink) {
        this._suppressDropDBLink = suppressDropDBLink;
    }
}