package org.seasar.dbflute.helper.jdbc.facade;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * Super simple facade for JDBC.
 * @author jflute
 */
public class DfJdbcFacade {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfJdbcFacade.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected final Connection _conn;
    protected final boolean _closeConnection;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfJdbcFacade(DataSource dataSource) {
        _dataSource = dataSource;
        _conn = null;
        _closeConnection = true;
    }

    public DfJdbcFacade(Connection conn) {
        this(conn, false);
    }

    public DfJdbcFacade(Connection conn, boolean closeConnection) {
        _dataSource = null;
        _conn = conn;
        _closeConnection = closeConnection;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    // -----------------------------------------------------
    //                                            Typed List
    //                                            ----------
    public List<Map<String, Object>> selectList(String sql, Map<String, ValueType> columnValueTypeMap) {
        return selectList(sql, columnValueTypeMap, -1);
    }

    public List<Map<String, Object>> selectList(String sql, Map<String, ValueType> columnValueTypeMap, int limit) {
        // [ATTENTION]: no use bind variables
        final List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            final DfJFacResultSetWrapper wrapper = new DfJFacResultSetWrapper(rs, columnValueTypeMap, null);
            int count = 0;
            while (wrapper.next()) {
                if (isOverLimit(limit, count)) {
                    break;
                }
                final Map<String, Object> recordMap = StringKeyMap.createAsFlexibleOrdered();
                for (String columnName : columnValueTypeMap.keySet()) {
                    final Object value = wrapper.getObject(columnName);
                    recordMap.put(columnName, value);
                }
                resultList.add(recordMap);
                ++count;
            }
        } catch (SQLException e) {
            handleSQLException(sql, e);
            return null; // unreachable
        } finally {
            closeResultSet(rs);
            closeStatement(st);
            closeConnection(conn);
        }
        return resultList;
    }

    protected boolean isOverLimit(int limit, int count) {
        return limit >= 0 && limit <= count;
    }

    // -----------------------------------------------------
    //                                           String List
    //                                           -----------
    /**
     * Select the list for records as string value simply.
     * @param sql The SQL string. (NotNull)
     * @param columnList The list of selected columns. (NotNull)
     * @return The list for result. (NotNull)
     */
    public List<Map<String, String>> selectStringList(String sql, List<String> columnList) {
        return selectStringList(sql, columnList, -1);
    }

    /**
     * Select the list for records as string value simply.
     * @param sql The SQL string. (NotNull)
     * @param columnList The list of selected columns. (NotNull)
     * @param limit The limit size for fetching. (MinusAllowed: means no limit)
     * @return The list for result. (NotNull)
     */
    public List<Map<String, String>> selectStringList(String sql, List<String> columnList, int limit) {
        final Map<String, ValueType> columnValueTypeMap = new LinkedHashMap<String, ValueType>();
        for (String column : columnList) {
            columnValueTypeMap.put(column, null);
        }
        return selectStringList(sql, columnValueTypeMap, null, limit);
    }

    /**
     * Select the list for records as string value using value types.
     * @param sql The SQL string. (NotNull)
     * @param columnValueTypeMap The map of selected columns to value types. (NotNull, ValueTypeNullAllowed)
     * @param converter The converter to convert to string value. (NullAllowed: means no conversion)
     * @return The list for result. (NotNull)
     */
    public List<Map<String, String>> selectStringList(String sql, Map<String, ValueType> columnValueTypeMap,
            DfJFacStringConverter converter) {
        return selectStringList(sql, columnValueTypeMap, converter, -1);
    }

    /**
     * Select the list for records as string value using value types.
     * @param sql The SQL string. (NotNull)
     * @param columnValueTypeMap The map of selected columns to value types. (NotNull, ValueTypeNullAllowed)
     * @param converter The converter to convert to string value. (NullAllowed: means no conversion)
     * @param limit The limit size for fetching. (MinusAllowed: means no limit)
     * @return The list for result. (NotNull)
     */
    public List<Map<String, String>> selectStringList(String sql, Map<String, ValueType> columnValueTypeMap,
            DfJFacStringConverter converter, int limit) {
        // [ATTENTION]: no use bind variables
        final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            final DfJFacResultSetWrapper wrapper = new DfJFacResultSetWrapper(rs, columnValueTypeMap, converter);
            int count = 0;
            while (wrapper.next()) {
                if (isOverLimit(limit, count)) {
                    break;
                }
                final Map<String, String> recordMap = StringKeyMap.createAsFlexibleOrdered();
                final Set<Entry<String, ValueType>> entrySet = columnValueTypeMap.entrySet();
                for (Entry<String, ValueType> entry : entrySet) {
                    final String columnName = entry.getKey();
                    final String value = wrapper.getString(columnName);
                    recordMap.put(columnName, value);
                }
                resultList.add(recordMap);
                ++count;
            }
        } catch (SQLException e) {
            handleSQLException(sql, e);
            return null; // unreachable
        } finally {
            closeResultSet(rs);
            closeStatement(st);
            closeConnection(conn);
        }
        return resultList;
    }

    // -----------------------------------------------------
    //                                                 Count
    //                                                 -----
    public int selectCountAll(String tableSqlName) {
        final List<String> columnList = DfCollectionUtil.newArrayList("cnt");
        final String sql = "select count(*) as cnt from " + tableSqlName;
        final String cntStr = selectStringList(sql, columnList).get(0).get("cnt").trim();
        return Integer.valueOf(cntStr);
    }

    // -----------------------------------------------------
    //                                                Cursor
    //                                                ------
    public DfJFacCursorCallback selectCursor(final String sql, final Map<String, ValueType> columnValueTypeMap,
            final DfJFacStringConverter stringConverter) {
        return new DfJFacCursorCallback() {
            public void select(DfJFacCursorHandler handler) {
                Connection conn = null;
                Statement st = null;
                ResultSet rs = null;
                try {
                    conn = _dataSource.getConnection();
                    st = conn.createStatement();
                    rs = st.executeQuery(sql);
                    handler.handle(new DfJFacResultSetWrapper(rs, columnValueTypeMap, stringConverter));
                } catch (SQLException e) {
                    handleSQLException(sql, e);
                } finally {
                    closeResultSet(rs);
                    closeStatement(st);
                    closeConnection(conn);
                }
            }
        };
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public boolean execute(String sql) {
        // [ATTENTION]: no use bind variables
        Connection conn = null;
        Statement st = null;
        try {
            conn = getConnection();
            st = conn.createStatement();
            return st.execute(sql);
        } catch (SQLException e) {
            handleSQLException(sql, e);
            return false; // unreachable
        } finally {
            closeStatement(st);
            closeConnection(conn);
        }
    }

    // ===================================================================================
    //                                                                          Connection
    //                                                                          ==========
    protected Connection getConnection() throws SQLException {
        if (_dataSource != null) {
            return _dataSource.getConnection();
        } else {
            return _conn;
        }
    }

    // ===================================================================================
    //                                                                               Close
    //                                                                               =====
    protected void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
                _log.info("ResultSet.close() threw the exception!", ignored);
            }
        }
    }

    protected void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ignored) {
                _log.info("Statement.close() threw the exception!", ignored);
            }
        }
    }

    protected void closeConnection(Connection conn) {
        if (conn != null && _closeConnection) {
            try {
                conn.close();
            } catch (SQLException ignored) {
                _log.info("Connection.close() threw the exception!", ignored);
            }
        }
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    protected void handleSQLException(String sql, SQLException e) {
        String msg = "Failed to execute the SQL:" + ln();
        msg = msg + "/- - - - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "[SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Exception]" + ln();
        msg = msg + e.getClass() + ln();
        msg = msg + e.getMessage() + ln();
        msg = msg + "- - - - - - - - - -/";
        throw new SQLFailureException(msg, e);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}
