package org.seasar.dbflute.logic.doc.dataxls;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.properties.DfAbstractHelperProperties;
import org.seasar.dbflute.s2dao.valuetype.basic.StringType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimeType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimestampType;
import org.seasar.dbflute.s2dao.valuetype.basic.UtilDateAsSqlDateType;
import org.seasar.dbflute.util.DfTypeUtil;

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
    public Map<String, List<Map<String, String>>> extractData(Map<String, List<Column>> tableColumnMap, int limit) {
        final Map<String, List<Map<String, String>>> templateDataMap = new LinkedHashMap<String, List<Map<String, String>>>();
        final Set<Entry<String, List<Column>>> entrySet = tableColumnMap.entrySet();
        for (Entry<String, List<Column>> entry : entrySet) {
            final String tableName = entry.getKey();
            final List<Column> columnList = entry.getValue();
            final List<Map<String, Object>> objectList = selectObjectList(tableName, columnList, limit);
            final List<Map<String, String>> resultList = createResultList(objectList);
            _log.info("    " + tableName + "(" + resultList.size() + ")");
            templateDataMap.put(tableName, resultList);
        }
        return templateDataMap;
    }

    protected List<Map<String, Object>> selectObjectList(String tableName, List<Column> columnList, int limit) {
        final String selectClause = buildSelectClause(columnList);
        final String fromClause = buildFromClause(tableName);
        final String sql = selectClause + " " + fromClause;
        final Map<String, ValueType> columnValueTypeMap = new LinkedHashMap<String, ValueType>();
        for (Column column : columnList) {
            final String columnName = column.getName();
            final ValueType valueType;
            if (column.isJdbcTypeTime()) {
                valueType = new TimeType();
            } else if (column.isJdbcTypeTimestamp()) {
                valueType = new TimestampType();
            } else if (column.isJdbcTypeDate()) {
                valueType = new UtilDateAsSqlDateType();
            } else {
                valueType = new StringType();
            }
            columnValueTypeMap.put(columnName, valueType);
        }
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        return facade.selectList(sql, columnValueTypeMap, limit);
    }

    protected String buildSelectClause(List<Column> columnList) {
        final StringBuilder sb = new StringBuilder();
        for (Column column : columnList) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(column.getColumnSqlNameDirectUse());
        }
        return sb.insert(0, "select ").toString();
    }

    protected String buildFromClause(String tableName) {
        return "from " + tableName;
    }

    protected List<Map<String, String>> createResultList(List<Map<String, Object>> objectList) {
        final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for (Map<String, Object> recordMap : objectList) {
            final Map<String, String> stringMap = new LinkedHashMap<String, String>();
            for (Entry<String, Object> entry : recordMap.entrySet()) {
                final String columnName = entry.getKey();
                final Object objValue = entry.getValue();
                final String strValue;
                if (objValue instanceof String) {
                    strValue = (String) objValue;
                } else if (objValue instanceof Timestamp) {
                    final Timestamp timestamp = (Timestamp) objValue;
                    strValue = formatDate(timestamp, "yyyy-MM-dd HH:mm:ss.SSS");
                } else if (objValue instanceof Time) {
                    strValue = DfTypeUtil.toString((Time) objValue, "HH:mm:ss");
                } else if (objValue instanceof Date) {
                    final Date date = (Date) objValue;
                    strValue = formatDate(date, "yyyy-MM-dd HH:mm:ss");
                } else {
                    strValue = objValue != null ? objValue.toString() : null;
                }
                stringMap.put(columnName, strValue);
            }
            resultList.add(stringMap);
        }
        return resultList;
    }

    protected String formatDate(Date date, String pattern) {
        final String prefix = (DfTypeUtil.isDateBC(date) ? "BC" : "");
        return prefix + DfTypeUtil.toString(date, pattern);
    }
}
