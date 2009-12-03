package org.seasar.dbflute.logic.dumpdata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.properties.DfAbstractHelperProperties;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfTemplateDataExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log-instance */
    private static final Log _log = LogFactory.getLog(DfAbstractHelperProperties.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfTemplateDataExtractor(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    /**
     * Extract data.
     * @param tableColumnMap The map of table and column. (NotNull)
     * @param limit The limit of records. (If it's minus value, extracts all records.)
     */
    public Map<String, List<Map<String, String>>> extractData(Map<String, List<String>> tableColumnMap, int limit) {
        final Map<String, List<Map<String, String>>> templateDataMap = new LinkedHashMap<String, List<Map<String, String>>>();
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final Set<String> tableNameSet = tableColumnMap.keySet();
        for (String tableName : tableNameSet) {
            final List<String> columnList = tableColumnMap.get(tableName);
            final String selectClause = buildSelectClause(columnList);
            final String fromClause = buildFromClause(tableName);
            final String sql = selectClause + " " + fromClause;
            final List<Map<String, String>> resultList = facade.selectStringList(sql, columnList, limit);
            _log.info("    " + tableName + "(" + resultList.size() + ")");
            templateDataMap.put(tableName, resultList);
        }
        return templateDataMap;
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
}
