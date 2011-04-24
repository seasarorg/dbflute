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
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.s2dao.valuetype.basic.StringType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimeType;
import org.seasar.dbflute.s2dao.valuetype.basic.TimestampType;
import org.seasar.dbflute.s2dao.valuetype.basic.UtilDateAsSqlDateType;
import org.seasar.dbflute.s2dao.valuetype.basic.UtilDateAsTimestampType;
import org.seasar.dbflute.s2dao.valuetype.plugin.BytesType;
import org.seasar.dbflute.s2dao.valuetype.plugin.StringClobType;
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
     * @param tableMap The map of table. (NotNull)
     * @param limit The limit of records. (If it's minus value, extracts all records.)
     */
    public Map<String, List<Map<String, String>>> extractData(Map<String, Table> tableMap, int limit) {
        final Map<String, List<Map<String, String>>> templateDataMap = new LinkedHashMap<String, List<Map<String, String>>>();
        for (Entry<String, Table> entry : tableMap.entrySet()) {
            final String tableDbName = entry.getKey();
            final Table table = entry.getValue();
            final List<Map<String, Object>> objectList = selectObjectList(table, limit);
            final List<Map<String, String>> resultList = createResultList(objectList);
            templateDataMap.put(tableDbName, resultList);
        }
        return templateDataMap;
    }

    protected List<Map<String, Object>> selectObjectList(Table table, int limit) {
        final String tableSqlName = table.getTableSqlNameDirectUse();
        final List<Column> columnList = table.getColumnList();
        final String sql;
        {
            final String selectClause = buildSelectClause(columnList);
            final String fromClause = buildFromClause(tableSqlName);
            final String orderByClause = buildOrderByClause(table);
            final String sqlSuffix = buildSqlSuffix(table, limit);
            sql = selectClause + fromClause + orderByClause + sqlSuffix;
        }
        final Map<String, ValueType> columnValueTypeMap = new LinkedHashMap<String, ValueType>();
        for (Column column : columnList) {
            final String columnName = column.getName();

            // create value type for the column
            final ValueType valueType;
            if (column.isJavaNativeStringObject()) {
                if (column.isDbTypeStringClob()) {
                    valueType = new StringClobType();
                } else {
                    valueType = new StringType();
                }
            } else if (column.isJavaNativeDateObject()) {
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
        return " from " + tableName;
    }

    protected String buildOrderByClause(Table table) {
        final ForeignKey selfReferenceFK = table.getSelfReferenceForeignKey();
        final String orderBy;
        if (selfReferenceFK != null && selfReferenceFK.isSimpleKeyFK()) {
            final Column firstColumn = table.getColumn(selfReferenceFK.getFirstLocalColumnName());
            final String firstName = firstColumn.getColumnSqlNameDirectUse();
            orderBy = " order by " + firstName + " is not null asc, " + firstName + " asc";
        } else {
            orderBy = "";
        }
        return orderBy;
    }

    protected String buildSqlSuffix(Table table, int limit) {
        if (limit < 1) {
            return "";
        }
        final DfBasicProperties prop = getBasicProperties();
        if (prop.isDatabaseMySQL() || prop.isDatabasePostgreSQL() || prop.isDatabaseH2()) {
            return " limit " + limit;
        }
        return "";
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

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }
}
