package org.seasar.dbflute.helper.jdbc.facade;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfJdbcFacade(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public List<Map<String, String>> selectStringList(String sql, List<String> columnList) {
        return selectStringList(sql, columnList, -1);
    }

    public List<Map<String, String>> selectStringList(String sql, List<String> columnList, int limit) {
        // [ATTENTION]: no use bind variables
        final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = _dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                if (limit >= 0 && limit <= count) {
                    break;
                }
                final Map<String, String> recordMap = new LinkedHashMap<String, String>();
                for (String columnName : columnList) {
                    recordMap.put(columnName, rs.getString(columnName));
                }
                resultList.add(recordMap);
                ++count;
            }
        } catch (SQLException e) {
            String msg = "Failed to execute the SQL: sql=" + sql;
            throw new IllegalStateException(msg, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("ResultSet.close() threw the exception!", ignored);
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                    _log.info("Statement.close() threw the exception!", ignored);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("Connection.close() threw the exception!", ignored);
                }
            }
        }
        return resultList;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public boolean execute(String sql) {
        // [ATTENTION]: no use bind variables
        Connection conn = null;
        Statement st = null;
        try {
            conn = _dataSource.getConnection();
            st = conn.createStatement();
            return st.execute(sql);
        } catch (SQLException e) {
            String msg = "Failed to execute the SQL: sql=" + sql;
            throw new IllegalStateException(msg, e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                    _log.info("Statement.close() threw the exception!", ignored);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("Connection.close() threw the exception!", ignored);
                }
            }
        }

    }
}
