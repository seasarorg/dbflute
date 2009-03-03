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
package org.seasar.dbflute.helper.jdbc.schemainitializer;

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
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableHandler;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;

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

    // /= = = = = = = = = = = =
    // Attribute for once more!
    // = = = = = = = = = =/
    protected List<String> _onceMoreDropObjectTypeList;

    protected List<String> _onceMoreDropTableTargetList;

    protected List<String> _onceMoreDropTableExceptList;

    protected boolean _onceMoreDropAllTable;

    // /= = = = = = = = = = = = =
    // Detail execution handling!
    // = = = = = = = = = =/
    protected boolean _suppressTruncateTable;

    protected boolean _suppressDropForeignKey;

    protected boolean _suppressDropTable;

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
                    // /= = = = = = = = = = = =
                    // Override for once more!
                    // = = = = = = = = = =/
                    @Override
                    protected String[] getObjectTypeStringArray() {
                        if (_onceMoreDropObjectTypeList != null) {
                            return _onceMoreDropObjectTypeList.toArray(new String[] {});
                        } else {
                            return super.getObjectTypeStringArray();
                        }
                    }

                    @Override
                    protected List<String> getTableTargetList() {
                        if (_onceMoreDropAllTable) {
                            return new ArrayList<String>();
                        }
                        if (_onceMoreDropTableTargetList != null) {
                            return _onceMoreDropTableTargetList;
                        } else {
                            return super.getTableTargetList();
                        }
                    }

                    @Override
                    protected List<String> getTableExceptList() {
                        if (_onceMoreDropAllTable) {
                            return new ArrayList<String>();
                        }
                        if (_onceMoreDropTableExceptList != null) {
                            return _onceMoreDropTableExceptList;
                        } else {
                            return super.getTableExceptList();
                        }
                    }
                };
                tableMetaInfoList = tableNameHandler.getTableList(dbMetaData, _schema);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            executeObject(conn, tableMetaInfoList);
        } catch (SQLException e) {
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
        }
        if (!_suppressDropForeignKey) {
            dropForeignKey(conn, tableMetaInfoList);
        }
        if (!_suppressDropTable) {
            dropTable(conn, tableMetaInfoList);
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
                    // /= = = = = = = = = = = =
                    // Override for once more!
                    // = = = = = = = = = =/
                    @Override
                    protected List<String> getTableTargetList() {
                        if (_onceMoreDropAllTable) {
                            return new ArrayList<String>();
                        }
                        if (_onceMoreDropTableTargetList != null) {
                            return _onceMoreDropTableTargetList;
                        } else {
                            return super.getTableTargetList();
                        }
                    }

                    @Override
                    protected List<String> getTableExceptList() {
                        if (_onceMoreDropAllTable) {
                            return new ArrayList<String>();
                        }
                        if (_onceMoreDropTableExceptList != null) {
                            return _onceMoreDropTableExceptList;
                        } else {
                            return super.getTableExceptList();
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
            throw new RuntimeException(e);
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

        final DfDropTableByJdbcCallback callback = new DfDropTableByJdbcCallback() {
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
        };
        callbackDropTableByJdbc(conn, sortedList, callback);
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

    protected void callbackDropTableByJdbc(Connection connection, List<DfTableMetaInfo> tableMetaInfoList,
            DfDropTableByJdbcCallback callback) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (DfTableMetaInfo metaInfo : tableMetaInfoList) {
                final String dropTableSql = callback.buildDropTableSql(metaInfo);
                _log.info(dropTableSql);
                try {
                    statement.execute(dropTableSql);
                } catch (SQLException e) {
                    // = = = = = = = = = = = =
                    // for materialized view!
                    // = = = = = = = = = = = =
                    final String dropMaterializedViewSql = callback.buildDropMaterializedViewSql(metaInfo);
                    try {
                        statement.execute(dropMaterializedViewSql);
                        _log.info("  --> " + dropMaterializedViewSql);
                    } catch (SQLException ignored) {
                        if (metaInfo.isTableTypeView()) {
                            _log.info("The drop view failed to execute: msg=" + e.getMessage());
                        } else {
                            throw e;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    // /= = = = = = = = = = = =
    // Attribute for once more!
    // = = = = = = = = = =/

    public void setOnceMoreDropObjectTypeList(List<String> onceMoreDropObjectTypeList) {
        _onceMoreDropObjectTypeList = onceMoreDropObjectTypeList;
    }

    public void setOnceMoreDropTableTargetList(List<String> onceMoreDropTableTargetList) {
        _onceMoreDropTableTargetList = onceMoreDropTableTargetList;
    }

    public void setOnceMoreDropTableExceptList(List<String> onceMoreDropTableExceptList) {
        _onceMoreDropTableExceptList = onceMoreDropTableExceptList;
    }

    public void setOnceMoreDropDropAllTable(boolean onceMoreDropAllTable) {
        _onceMoreDropAllTable = onceMoreDropAllTable;
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
}