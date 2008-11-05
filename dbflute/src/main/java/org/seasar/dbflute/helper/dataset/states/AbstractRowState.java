package org.seasar.dbflute.helper.dataset.states;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.util.jdbc.DfConnectionUtil;
import org.seasar.dbflute.util.jdbc.DfDataSourceUtil;

/**
 * Row States. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class AbstractRowState implements RowState {

    AbstractRowState() {
    }

    public void update(DataSource dataSource, DataRow row) {
        final SqlContext ctx = getSqlContext(row);
        execute(dataSource, ctx.getSql(), ctx.getArgs(), ctx.getArgTypes());
    }

    protected void execute(DataSource dataSource, String sql, Object[] args, Class<?>[] argTypes) {
        final Connection conn = DfDataSourceUtil.getConnection(dataSource);
        try {
            final PreparedStatement ps = DfConnectionUtil.prepareStatement(conn, sql);
            try {
                bindArgs(ps, args, argTypes);
                ps.executeUpdate();
            } catch (SQLException e) {
                String msg = "The SQL threw the exception: " + sql;
                throw new IllegalStateException(msg, e);
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        } finally {
            DfConnectionUtil.close(conn);
        }
    }

    protected void bindArgs(PreparedStatement ps, Object[] args, Class<?>[] argTypes) throws SQLException {
        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            ps.setObject(i + 1, args[i]);
        }
    }

    protected abstract SqlContext getSqlContext(DataRow row);
}