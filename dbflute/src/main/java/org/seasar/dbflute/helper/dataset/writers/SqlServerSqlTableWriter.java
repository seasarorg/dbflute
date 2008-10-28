package org.seasar.dbflute.helper.dataset.writers;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.util.jdbc.DfConnectionUtil;
import org.seasar.dbflute.util.jdbc.DfDataSourceUtil;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;
import org.seasar.framework.util.StatementUtil;

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
        final Connection connection = DfDataSourceUtil.getConnection(getDataSource());
        try {
            final Statement statement = DfConnectionUtil.createStatement(connection);
            try {
                StatementUtil.execute(statement, sql);
            } finally {
                StatementUtil.close(statement);
            }
        } finally {
            DfConnectionUtil.close(connection);
        }
    }

    private boolean hasIdentityColumn(final DataTable dataTable) {
        final String sql = "SELECT IDENT_CURRENT ('" + dataTable.getTableName() + "') AS IDENT_CURRENT";
        final BasicSelectHandler handler = new BasicSelectHandler(getDataSource(), sql, new ObjectResultSetHandler());
        return handler.execute(null) != null;
    }
}