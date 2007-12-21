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
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableNameHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler.DfForeignKeyMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableNameHandler.DfTableMetaInfo;

/**
 * The schema initializer with JDBC.
 * 
 * @author jflute
 */
public class DfSchemaInitializerJdbc implements DfSchemaInitializer {

    private static final Log _log = LogFactory.getLog(DfSchemaInitializerJdbc.class);

    protected DataSource _dataSource;

    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void initializeSchema() {
        truncateTableIfPossible();
        dropForeignKey();
        dropTable();
    }

    protected void truncateTableIfPossible() {
        final DfTruncateTableByJdbcCallback callback = new DfTruncateTableByJdbcCallback() {
            public String buildTruncateTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("TRUNCATE TABLE ").append(metaInfo.getTableName());
                return sb.toString();
            }
        };
        callbackTruncateTableByJdbc(_dataSource, callback);
    }

    protected static interface DfTruncateTableByJdbcCallback {
        public String buildTruncateTableSql(DfTableMetaInfo metaInfo);
    }

    protected void callbackTruncateTableByJdbc(DataSource dataSource, DfTruncateTableByJdbcCallback callback) {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.createStatement();
            final DatabaseMetaData dbMeta = conn.getMetaData();
            final String schema = DfBuildProperties.getInstance().getBasicProperties().getDatabaseSchema();

            final DfTableNameHandler tableNameHandler = new DfTableNameHandler();
            final List<DfTableMetaInfo> tableNameList = tableNameHandler.getTableNameList(dbMeta, schema);
            for (DfTableMetaInfo metaInfo : tableNameList) {
                final String truncateTableSql = callback.buildTruncateTableSql(metaInfo);
                try {
                    statement.execute(truncateTableSql);
                    _log.info(truncateTableSql);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("conn.close() threw the exception!", ignored);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
        }
    }

    protected void dropForeignKey() {
        final DfDropForeignKeyByJdbcCallback callback = new DfDropForeignKeyByJdbcCallback() {
            public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo) {
                final String foreignKeyName = metaInfo.getForeignKeyName();
                final String localTableName = metaInfo.getLocalTableName();
                final StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE ").append(localTableName).append(" DROP CONSTRAINT ").append(foreignKeyName);
                return sb.toString();
            }
        };
        callbackDropForeignKeyByJdbc(_dataSource, callback);
    }

    protected static interface DfDropForeignKeyByJdbcCallback {
        public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo);
    }

    protected void callbackDropForeignKeyByJdbc(DataSource dataSource, DfDropForeignKeyByJdbcCallback callback) {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.createStatement();
            final DatabaseMetaData dbMeta = conn.getMetaData();
            final String schema = DfBuildProperties.getInstance().getBasicProperties().getDatabaseSchema();

            final DfTableNameHandler tableNameHandler = new DfTableNameHandler();
            final List<DfTableMetaInfo> tableNameList = tableNameHandler.getTableNameList(dbMeta, schema);
            for (DfTableMetaInfo tableMetaInfo : tableNameList) {
                final DfForeignKeyHandler handler = new DfForeignKeyHandler();
                final Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap = handler.getForeignKeyMetaInfo(dbMeta,
                        schema, tableMetaInfo);
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
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("conn.close() threw the exception!", ignored);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
        }
    }

    protected void dropTable() {
        final DfDropTableByJdbcCallback callback = new DfDropTableByJdbcCallback() {
            public String buildDropTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("DROP TABLE ").append(metaInfo.getTableName());
                return sb.toString();
            }
        };
        callbackDropTableByJdbc(_dataSource, callback);
    }

    protected static interface DfDropTableByJdbcCallback {
        public String buildDropTableSql(DfTableMetaInfo metaInfo);
    }

    protected void callbackDropTableByJdbc(DataSource dataSource, DfDropTableByJdbcCallback callback) {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.createStatement();
            final DatabaseMetaData dbMeta = conn.getMetaData();
            final String schema = DfBuildProperties.getInstance().getBasicProperties().getDatabaseSchema();

            final DfTableNameHandler tableNameHandler = new DfTableNameHandler();
            final List<DfTableMetaInfo> tableNameList = tableNameHandler.getTableNameList(dbMeta, schema);
            for (DfTableMetaInfo metaInfo : tableNameList) {
                final String dropTableSql = callback.buildDropTableSql(metaInfo);
                _log.info(dropTableSql);
                statement.execute(dropTableSql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("conn.close() threw the exception!", ignored);
                }
            }
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