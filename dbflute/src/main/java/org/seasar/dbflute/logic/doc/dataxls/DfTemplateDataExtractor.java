package org.seasar.dbflute.logic.doc.dataxls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.valuetype.basic.StringType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimeType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimestampType;
import org.seasar.dbflute.s2dao.valuetype.basic.UtilDateAsSqlDateType;
import org.seasar.dbflute.s2dao.valuetype.basic.UtilDateAsTimestampType;
import org.seasar.dbflute.s2dao.valuetype.plugin.BytesType;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfTemplateDataExtractor {

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
     * Extract data for template.
     * @param tableInfoMap The map of table info. (NotNull)
     * @param limit The limit of records. (If it's minus value, extracts all records.)
     */
    public Map<String, List<Map<String, String>>> extractData(Map<String, DfTemplateDataTableInfo> tableInfoMap,
            int limit) {
        final Map<String, List<Map<String, String>>> templateDataMap = new LinkedHashMap<String, List<Map<String, String>>>();
        for (Entry<String, DfTemplateDataTableInfo> entry : tableInfoMap.entrySet()) {
            final String tableDbName = entry.getKey();
            final DfTemplateDataTableInfo tableInfo = entry.getValue();
            final List<Map<String, Object>> objectList = selectObjectList(tableInfo, limit);
            final List<Map<String, String>> resultList = createResultList(objectList);
            templateDataMap.put(tableDbName, resultList);
        }
        return templateDataMap;
    }

    protected List<Map<String, Object>> selectObjectList(DfTemplateDataTableInfo tableInfo, int limit) {
        final String tableSqlName = tableInfo.getTableSqlName();
        final List<Column> columnList = tableInfo.getColumnList();
        final String selectClause = buildSelectClause(columnList);
        final String fromClause = buildFromClause(tableSqlName);
        final String sql = selectClause + " " + fromClause;
        final Map<String, ValueType> columnValueTypeMap = new LinkedHashMap<String, ValueType>();
        for (Column column : columnList) {
            final String columnName = column.getName();

            // create value type for the column
            final ValueType valueType;
            if (column.isJavaNativeDateObject()) {
                // date types should be treated correctly
                if (column.isJdbcTypeTime()) {
                    valueType = new TimeType();
                } else if (column.isJdbcTypeTimestamp()) {
                    valueType = new TimestampType();
                } else if (column.isJdbcTypeDate()) {
                    if (column.isDbTypeOracleDate()) {
                        valueType = new UtilDateAsTimestampType();
                    } else {
                        valueType = new UtilDateAsSqlDateType();
                    }
                } else { // no way
                    valueType = new TimestampType();
                }
            } else if (column.isJavaNativeBinaryObject()) {
                // unsupported BLOG as template data
                valueType = new NullBytesType();
            } else {
                // other types are treated as string
                // because ReplaceSchema can accept them
                valueType = new StringType();
            }

            columnValueTypeMap.put(columnName, valueType);
        }
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        return facade.selectList(sql, columnValueTypeMap, limit);
    }

    protected static class NullBytesType extends BytesType {

        public NullBytesType() {
            super(BytesType.BLOB_TRAIT);
        }

        @Override
        public Object getValue(ResultSet rs, int index) throws SQLException {
            return null;
        };

        @Override
        public Object getValue(ResultSet rs, String columnName) throws SQLException {
            return null;
        };
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
