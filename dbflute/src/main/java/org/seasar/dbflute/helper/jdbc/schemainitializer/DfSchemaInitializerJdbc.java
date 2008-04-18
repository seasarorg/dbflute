package org.seasar.dbflute.helper.jdbc.schemainitializer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler.DfForeignKeyMetaInfo;
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

    // ===================================================================================
    //                                                                   Initialize Schema
    //                                                                   =================
    public void initializeSchema() {
        Connection connection = null;
        try {
            connection = _dataSource.getConnection();
            final List<DfTableMetaInfo> tableMetaInfoList;
            try {
                final DatabaseMetaData dbMetaData = connection.getMetaData();
                final DfTableHandler tableNameHandler = new DfTableHandler();
                tableMetaInfoList = tableNameHandler.getTableList(dbMetaData, _schema);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            truncateTableIfPossible(connection, tableMetaInfoList);
            dropForeignKey(connection, tableMetaInfoList);
            dropTable(connection, tableMetaInfoList);
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                    _log.info("connection.close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      Truncate Table
    //                                                                      ==============
    protected void truncateTableIfPossible(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfTruncateTableByJdbcCallback callback = new DfTruncateTableByJdbcCallback() {
            public String buildTruncateTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("TRUNCATE TABLE ").append(metaInfo.getTableName());
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
                final String localTableName = metaInfo.getLocalTableName();
                final StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE ").append(localTableName).append(" DROP CONSTRAINT ").append(foreignKeyName);
                return sb.toString();
            }
        };
        callbackDropForeignKeyByJdbc(connection, tableMetaInfoList, callback);
    }

    protected static interface DfDropForeignKeyByJdbcCallback {
        public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo);
    }

    protected void callbackDropForeignKeyByJdbc(Connection connection, List<DfTableMetaInfo> tableMetaInfoList,
            DfDropForeignKeyByJdbcCallback callback) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (DfTableMetaInfo tableMetaInfo : tableMetaInfoList) {
                final DfForeignKeyHandler handler = new DfForeignKeyHandler();
                final DatabaseMetaData dbMetaData = connection.getMetaData();
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

    // ===================================================================================
    //                                                                          Drop Table
    //                                                                          ==========
    protected void dropTable(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfDropTableByJdbcCallback callback = new DfDropTableByJdbcCallback() {
            public String buildDropTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                if (metaInfo.isTableTypeView()) {
                    sb.append("DROP VIEW ").append(metaInfo.getTableName());
                } else {
                    sb.append("DROP TABLE ").append(metaInfo.getTableName());
                }
                return sb.toString();
            }
        };
        callbackDropTableByJdbc(connection, tableMetaInfoList, callback);
    }

    protected static interface DfDropTableByJdbcCallback {
        public String buildDropTableSql(DfTableMetaInfo metaInfo);
    }

    protected void callbackDropTableByJdbc(Connection connection, List<DfTableMetaInfo> tableMetaInfoList,
            DfDropTableByJdbcCallback callback) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (DfTableMetaInfo metaInfo : tableMetaInfoList) {
                final String dropTableSql = callback.buildDropTableSql(metaInfo);
                _log.info(dropTableSql);
                statement.execute(dropTableSql);
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
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void setSchema(String schema) {
        _schema = schema;
    }
}