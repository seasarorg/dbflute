package org.seasar.dbflute.logic.dumpdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDumpDataExtractor {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    public Map<String, List<Map<String, String>>> extractData(Map<String, List<String>> tableColumnMap, int limit) {
        final Map<String, List<Map<String, String>>> dumpDataMap = new LinkedHashMap<String, List<Map<String, String>>>();
        try {
            final Set<String> tableNameSet = tableColumnMap.keySet();
            for (String tableName : tableNameSet) {
                final List<String> columnNameList = tableColumnMap.get(tableName);
                final Connection conn = _dataSource.getConnection();
                final Statement statement = conn.createStatement();
                final String selectClause = buildSelectClause(columnNameList);
                final String fromClause = buildFromClause(tableName);
                final ResultSet rs = statement.executeQuery(selectClause + " " + fromClause);
                final List<Map<String, String>> recordList = new ArrayList<Map<String, String>>();
                int count = 0;
                while (rs.next()) {
                    if (limit <= count) {
                        break;
                    }
                    final LinkedHashMap<String, String> recordMap = new LinkedHashMap<String, String>();
                    for (String columnName : columnNameList) {
                        final String columnValue = rs.getString(columnName);
                        recordMap.put(columnName, columnValue);
                    }
                    recordList.add(recordMap);
                    ++count;
                }
                dumpDataMap.put(tableName, recordList);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return dumpDataMap;
    }

    protected String buildSelectClause(List<String> columnNameList) {
        final StringBuilder sb = new StringBuilder();
        for (String columnName : columnNameList) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(columnName);
        }
        return sb.insert(0, "select ").toString();
    }

    protected String buildFromClause(String tableName) {
        return "from " + tableName;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DataSource getDataSource() {
        return _dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this._dataSource = dataSource;
    }
}
