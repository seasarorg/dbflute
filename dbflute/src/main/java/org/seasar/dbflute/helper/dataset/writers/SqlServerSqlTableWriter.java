package org.seasar.dbflute.helper.dataset.writers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.util.jdbc.DfConnectionUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class SqlServerSqlTableWriter extends SqlTableWriter {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(SqlServerSqlTableWriter.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SqlServerSqlTableWriter(final DataSource dataSource, String schemaName) {
        super(dataSource, schemaName);
    }

    // ===================================================================================
    //                                                                       Main Override
    //                                                                       =============
    protected void doWrite(final DataTable dataTable) {
        boolean hasIdentity = hasIdentityColumn(dataTable);
        if (hasIdentity) {
            turnOnIdentityInsert(dataTable);
        }
        super.doWrite(dataTable);
        if (hasIdentity) {
            turnOffIdentityInsert(dataTable);
        }
    }

    private void turnOnIdentityInsert(final DataTable dataTable) {
        setIdentityInsert(dataTable, "ON");
    }

    private void turnOffIdentityInsert(final DataTable dataTable) {
        setIdentityInsert(dataTable, "OFF");
    }

    private void setIdentityInsert(final DataTable dataTable, final String command) {
        final String sql = "SET IDENTITY_INSERT " + dataTable.getTableName() + " " + command;
        if (_log.isDebugEnabled()) {
            _log.debug(sql);
        }
        final Connection conn = getConnection(getDataSource());
        try {
            final Statement stmt = DfConnectionUtil.createStatement(conn);
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        } finally {
            DfConnectionUtil.close(conn);
        }
    }

    private boolean hasIdentityColumn(final DataTable dataTable) {
        final String sql = "SELECT IDENT_CURRENT ('" + dataTable.getTableName() + "') AS IDENT_CURRENT";
        final Connection conn = getConnection(getDataSource());
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                final Object value = rs.getObject(1);
                return value != null;
            }
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}