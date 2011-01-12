package org.seasar.dbflute.helper.dataset.writers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataTable;
import org.seasar.dbflute.helper.dataset.states.DfDtsRowState;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsSqlTableWriter implements DfDtsTableWriter {

    private DataSource dataSource;
    protected UnifiedSchema _unifiedSchema;

    public DfDtsSqlTableWriter(DataSource dataSource, UnifiedSchema unifiedSchema) {
        this.dataSource = dataSource;
        _unifiedSchema = unifiedSchema;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void write(DfDataTable table) {
        try {
            if (!table.hasMetaData()) {
                setupMetaData(table);
            }
        } catch (SQLException e) {
            String msg = "Failed to set up meta data: " + table;
            throw new SQLFailureException(msg, e);
        }
        doWrite(table);
    }

    protected void doWrite(DfDataTable table) {
        for (int i = 0; i < table.getRowSize(); ++i) {
            DfDataRow row = table.getRow(i);
            DfDtsRowState state = row.getState();
            state.update(dataSource, row);
        }
    }

    private void setupMetaData(DfDataTable table) throws SQLException {
        Connection con = getConnection(dataSource);
        try {
            table.setupMetaData(getMetaData(con), _unifiedSchema);
        } finally {
            close(con);
        }
    }

    private static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static DatabaseMetaData getMetaData(Connection conn) {
        try {
            return conn.getMetaData();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void close(Connection conn) {
        if (conn == null)
            return;
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
