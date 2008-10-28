package org.seasar.dbflute.helper.dataset.writers;

import java.sql.Connection;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.helper.dataset.states.RowState;
import org.seasar.dbflute.util.jdbc.DfConnectionUtil;
import org.seasar.dbflute.util.jdbc.DfDataSourceUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class SqlTableWriter implements TableWriter {

    private DataSource dataSource;
    protected String _schemaName;

    public SqlTableWriter(DataSource dataSource, String schemaName) {
        this.dataSource = dataSource;
        _schemaName = schemaName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void write(DataTable table) {
        if (!table.hasMetaData()) {
            setupMetaData(table);
        }
        doWrite(table);
    }

    protected void doWrite(DataTable table) {
        for (int i = 0; i < table.getRowSize(); ++i) {
            DataRow row = table.getRow(i);
            RowState state = row.getState();
            state.update(dataSource, row);
        }
    }

    private void setupMetaData(DataTable table) {
        Connection con = DfDataSourceUtil.getConnection(dataSource);
        try {
            table.setupMetaData(DfConnectionUtil.getMetaData(con), _schemaName);
        } finally {
            DfConnectionUtil.close(con);
        }
    }
}
