package org.seasar.dbflute.helper.dataset.states;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.util.jdbc.DfConnectionUtil;
import org.seasar.dbflute.util.jdbc.DfDataSourceUtil;

/**
 * {Refers to S2Container and Extends it}
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
            final Object value = args[i];
            final Class<?> type = argTypes[i];
            final int parameterIndex = (i + 1);
            if (String.class.isAssignableFrom(type)) {
                if (value != null) {
                    ps.setString(parameterIndex, (String) value);
                } else {
                    ps.setNull(parameterIndex, Types.VARCHAR);
                }
            } else if (Number.class.isAssignableFrom(type)) {
                if (value != null) {
                    ps.setBigDecimal(parameterIndex, new BigDecimal(value.toString()));
                } else {
                    ps.setNull(parameterIndex, Types.NUMERIC);
                }
            } else if (java.util.Date.class.isAssignableFrom(type)) {
                if (value != null) {
                    if (value instanceof String) {
                        final Timestamp timestamp = getTimestampValue((String) value);
                        ps.setTimestamp(parameterIndex, timestamp);
                    } else {
                        if (value instanceof Timestamp) {
                            ps.setTimestamp(parameterIndex, (Timestamp) value);
                        } else {
                            ps.setDate(parameterIndex, new java.sql.Date(((java.util.Date) value).getTime()));
                        }
                    }
                } else {
                    ps.setNull(parameterIndex, Types.DATE);
                }
            } else {
                if (value != null) {
                    ps.setObject(parameterIndex, value);
                } else {
                    ps.setNull(parameterIndex, Types.VARCHAR);
                }
            }
        }
    }

    protected Timestamp getTimestampValue(String value) {
        final String filteredTimestampValue = filterTimestampValue(value);
        try {
            return Timestamp.valueOf(filteredTimestampValue);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    protected String filterTimestampValue(String value) {
        value = value.trim();
        if (value.indexOf("/") == 4 && value.lastIndexOf("/") == 7) {
            value = value.replaceAll("/", "-");
        }
        if (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7) {
            if (value.length() == "2007-07-09".length()) {
                value = value + " 00:00:00";
            }
        }
        return value;
    }

    protected abstract SqlContext getSqlContext(DataRow row);
}