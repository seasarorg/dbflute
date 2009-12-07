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
package org.seasar.dbflute.logic.schemainitializer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.logic.jdbc.handler.DfForeignKeyHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfProcedureHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfTableHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

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
    protected String _schema;
    protected boolean _tableNameWithSchema;
    protected List<String> _dropObjectTypeList;
    protected List<String> _dropTableTargetList;
    protected List<String> _dropTableExceptList;
    protected boolean _dropGenerateTableOnly;

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
            final List<DfTableMetaInfo> tableMetaInfoList;
            try {
                final DatabaseMetaData dbMetaData = conn.getMetaData();
                final DfTableHandler tableNameHandler = new DfTableHandler() {
                    @Override
                    protected String[] getRealObjectTypeTargetArray(String schemaName) {
                        if (_dropObjectTypeList != null) {
                            return _dropObjectTypeList.toArray(new String[] {});
                        } else {
                            return super.getRealObjectTypeTargetArray(schemaName);
                        }
                    }

                    @Override
                    protected List<String> getRealTableExceptList(String schemaName) {
                        if (_dropTableExceptList != null) {
                            return _dropTableExceptList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableExceptList(schemaName);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }

                    @Override
                    protected List<String> getRealTableTargetList(String schemaName) {
                        if (_dropTableTargetList != null) {
                            return _dropTableTargetList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableTargetList(schemaName);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }
                };
                tableMetaInfoList = tableNameHandler.getTableList(dbMetaData, _schema);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            executeObject(conn, tableMetaInfoList);
        } catch (SQLException e) {
            String msg = "Failed to the initialize schema: " + _schema;
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

    protected void executeObject(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
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
        if (!_suppressDropProcedure) {
            dropProcedures(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping procedures");
        }
        if (!_suppressDropDBLink) {
            dropDBLink(conn, tableMetaInfoList);
        } else {
            _log.info("*Suppress dropping DB links");
        }
    }

    // ===================================================================================
    //                                                                      Truncate Table
    //                                                                      ==============
    protected void truncateTableIfPossible(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfTruncateTableByJdbcCallback callback = new DfTruncateTableByJdbcCallback() {
            public String buildTruncateTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("truncate table ").append(filterTableName(metaInfo.getTableName()));
                return sb.toString();
            }
        };
        callbackTruncateTableByJdbc(connection, tableMetaInfoList, callback);
    }

    protected static interface DfTruncateTableByJdbcCallback {
        public String buildTruncateTableSql(DfTableMetaInfo metaInfo);
    }

    protected void callbackTruncateTableByJdbc(Connection connection, List<DfTableMetaInfo> tableMetaInfoList,
            DfTruncateTableByJdbcCallback callback) {
        for (DfTableMetaInfo metaInfo : tableMetaInfoList) {
            final String truncateTableSql = callback.buildTruncateTableSql(metaInfo);
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.execute(truncateTableSql);
                _log.info(truncateTableSql);
            } catch (Exception e) {
                continue;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ignored) {
                        _log.info("statement.close() threw the exception!", ignored);
                    }
                }
            }
        }
    }

    // ===================================================================================
    //                                                                    Drop Foreign Key
    //                                                                    ================
    protected void dropForeignKey(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfDropForeignKeyByJdbcCallback callback = new DfDropForeignKeyByJdbcCallback() {
            public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo) {
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
        public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo);
    }

    protected void callbackDropForeignKeyByJdbc(Connection conn, List<DfTableMetaInfo> tableMetaInfoList,
            DfDropForeignKeyByJdbcCallback callback) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            for (DfTableMetaInfo tableMetaInfo : tableMetaInfoList) {
                if (isSkipDropForeignKey(tableMetaInfo)) {
                    continue;
                }
                final DfForeignKeyHandler handler = new DfForeignKeyHandler() {

                    @Override
                    protected List<String> getRealTableExceptList(String schemaName) {
                        if (_dropTableExceptList != null) {
                            return _dropTableExceptList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableExceptList(schemaName);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }

                    @Override
                    protected List<String> getRealTableTargetList(String schemaName) {
                        if (_dropTableTargetList != null) {
                            return _dropTableTargetList;
                        } else {
                            if (_dropGenerateTableOnly) {
                                return super.getRealTableTargetList(schemaName);
                            } else {
                                return new ArrayList<String>();
                            }
                        }
                    }
                };
                final DatabaseMetaData dbMetaData = conn.getMetaData();
                final Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap = handler.getForeignKeyMetaInfo(
                        dbMetaData, _schema, tableMetaInfo);
                final Set<String> keySet = foreignKeyMetaInfoMap.keySet();
                for (String foreignKeyName : keySet) {
                    final DfForeignKeyMetaInfo foreignKeyMetaInfo = foreignKeyMetaInfoMap.get(foreignKeyName);
                    final String dropForeignKeySql = callback.buildDropForeignKeySql(foreignKeyMetaInfo);
                    _log.info(dropForeignKeySql);
                    statement.execute(dropForeignKeySql);
                }
            }
        } catch (SQLException e) {
            String msg = "Failed to drop foreign keys!";
            throw new SQLFailureException(msg, e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
        }
    }

    protected boolean isSkipDropForeignKey(DfTableMetaInfo tableMetaInfo) { // for sub class.
        return false;
    }

    // ===================================================================================
    //                                                                          Drop Table
    //                                                                          ==========
    protected void dropTable(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        List<DfTableMetaInfo> viewList = new ArrayList<DfTableMetaInfo>();
        List<DfTableMetaInfo> otherList = new ArrayList<DfTableMetaInfo>();
        for (DfTableMetaInfo tableMetaInfo : tableMetaInfoList) {
            if (tableMetaInfo.isTableTypeView()) {
                viewList.add(tableMetaInfo);
            } else {
                otherList.add(tableMetaInfo);
            }
        }

        // Drop view and drop others
        final List<DfTableMetaInfo> sortedList = new ArrayList<DfTableMetaInfo>();
        sortedList.addAll(viewList);
        sortedList.addAll(otherList);

        callbackDropTableByJdbc(conn, sortedList, new DfDropTableByJdbcCallback() {
            public String buildDropTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                setupDropTable(sb, metaInfo);
                return sb.toString();
            }

            public String buildDropMaterializedViewSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("drop materialized view ").append(metaInfo.getTableName());
                return sb.toString();
            }
        });
    }

    protected void setupDropTable(StringBuilder sb, DfTableMetaInfo metaInfo) {
        final String tableName = filterTableName(metaInfo.getTableName());
        if (metaInfo.isTableTypeView()) {
            sb.append("drop view ").append(tableName);
        } else {
            sb.append("drop table ").append(tableName);
        }
    }

    protected static interface DfDropTableByJdbcCallback {
        public String buildDropTableSql(DfTableMetaInfo metaInfo);

        public String buildDropMaterializedViewSql(DfTableMetaInfo metaInfo);
    }

    protected void callbackDropTableByJdbc(Connection conn, List<DfTableMetaInfo> tableMetaInfoList,
            DfDropTableByJdbcCallback callback) {
        String currentSql = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for (DfTableMetaInfo metaInfo : tableMetaInfoList) {
                final String dropTableSql = callback.buildDropTableSql(metaInfo);
                currentSql = dropTableSql;
                _log.info(dropTableSql);
                try {
                    stmt.execute(dropTableSql);
                } catch (SQLException e) {
                    // = = = = = = = = = = = =
                    // for materialized view!
                    // = = = = = = = = = = = =
                    final String dropMaterializedViewSql = callback.buildDropMaterializedViewSql(metaInfo);
                    try {
                        stmt.execute(dropMaterializedViewSql);
                        _log.info("  (o) retry:  " + dropMaterializedViewSql);
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
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                    _log.info("Statement#close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                       Drop Sequence
    //                                                                       =============
    protected void dropSequence(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        // override if it needs
    }

    // ===================================================================================
    //                                                                      Drop Procedure
    //                                                                      ==============
    protected void dropProcedures(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfProcedureHandler handler = new DfProcedureHandler();
        DatabaseMetaData metaData;
        try {
            metaData = conn.getMetaData();
        } catch (SQLException e) {
            String msg = "Failed to get meta data of the connection:";
            msg = msg + " connection=" + conn;
            throw new SQLFailureException(msg, e);
        }
        final List<DfProcedureMetaInfo> procedureList = handler.getPlainProcedureList(metaData, _schema);
        callbackDropProcedureByJdbc(conn, procedureList, new DfDropProcedureByJdbcCallback() {
            public String buildDropProcedureSql(DfProcedureMetaInfo metaInfo) {
                final String procedureSqlName = handler.buildProcedureSqlName(metaInfo);
                return "drop procedure " + procedureSqlName;
            }

            public String buildDropFunctionSql(DfProcedureMetaInfo metaInfo) {
                final String procedureSqlName = handler.buildProcedureSqlName(metaInfo);
                return "drop function " + procedureSqlName;
            }
        });
    }

    protected static interface DfDropProcedureByJdbcCallback {
        String buildDropProcedureSql(DfProcedureMetaInfo metaInfo);

        String buildDropFunctionSql(DfProcedureMetaInfo metaInfo);
    }

    protected void callbackDropProcedureByJdbc(Connection conn, List<DfProcedureMetaInfo> procedureMetaInfoList,
            DfDropProcedureByJdbcCallback callback) {
        String currentSql = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for (DfProcedureMetaInfo metaInfo : procedureMetaInfoList) {
                final String procedureSchema = metaInfo.getProcedureSchema();
                if (_schema != null && !_schema.equalsIgnoreCase(procedureSchema)) {
                    // because of main schema only
                    // (the procedure list contains those of additional schema)
                    continue;
                }
                final String dropProcedureSql = callback.buildDropProcedureSql(metaInfo);
                currentSql = dropProcedureSql;
                _log.info(dropProcedureSql);
                try {
                    stmt.execute(dropProcedureSql);
                } catch (SQLException e) {
                    final String dropFunctionSql = callback.buildDropFunctionSql(metaInfo);
                    try {
                        stmt.execute(dropFunctionSql);
                        _log.info("  (o) retry: " + dropFunctionSql);
                    } catch (SQLException ignored) {
                        _log.info("  (x) retry: " + dropFunctionSql);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Failed to drop the table: " + currentSql;
            throw new SQLFailureException(msg, e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                    _log.info("Statement#close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                        Drop DB Link
    //                                                                        ============
    protected void dropDBLink(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        // override if it needs
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String filterTableName(String tableName) {
        if (_tableNameWithSchema) {
            tableName = _schema + "." + tableName;
        }
        return tableName;
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

    public void setSchema(String schema) {
        _schema = schema;
    }

    public boolean isTableNameWithSchema() {
        return _tableNameWithSchema;
    }

    public void setTableNameWithSchema(boolean tableNameWithSchema) {
        this._tableNameWithSchema = tableNameWithSchema;
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