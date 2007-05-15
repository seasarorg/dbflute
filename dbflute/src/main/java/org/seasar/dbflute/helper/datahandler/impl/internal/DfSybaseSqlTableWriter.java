package org.seasar.dbflute.helper.datahandler.impl.internal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.SqlTableWriter;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * 
 * @author jflute
 */
public class DfSybaseSqlTableWriter extends SqlTableWriter {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSybaseSqlTableWriter.class);

    public DfSybaseSqlTableWriter(final DataSource dataSource) {
        super(dataSource);
    }

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
        final Connection connection = DataSourceUtil.getConnection(getDataSource());
        try {
            final Statement statement = ConnectionUtil.createStatement(connection);
            try {
                StatementUtil.execute(statement, sql);
            } finally {
                StatementUtil.close(statement);
            }
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    private boolean hasIdentityColumn(final DataTable dataTable) {
        final DataSource dataSource = getDataSource();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return hasAutoIncrement(conn, dataTable.getTableName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.debug("conn.close() threw the ignored exception!", ignored);
                }
            }
        }
    }

    /**
     * Has auto-increment at the table?
     * <p>
     * @param conn Connection.
     * @param tableName Table from which to retrieve PK information.
     * @return Determination. (Nullable)
     * @throws SQLException
     */
    protected boolean hasAutoIncrement(Connection conn, String tableName) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            final ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++) {
                if (md.isAutoIncrement(i)) {
                    return true;
                }
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return false;
    }
}
