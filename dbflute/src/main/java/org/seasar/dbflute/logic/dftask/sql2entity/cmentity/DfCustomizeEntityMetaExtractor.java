package org.seasar.dbflute.logic.dftask.sql2entity.cmentity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;

public class DfCustomizeEntityMetaExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureExecutionMetaExtractor.class);

    public static interface DfForcedJavaNativeProvider {
        String provide(String columnName);
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public Map<String, DfColumnMetaInfo> extractColumnMetaInfoMap(ResultSet rs, String sql,
            DfForcedJavaNativeProvider forcedJavaNativeProvider) throws SQLException {
        final Map<String, DfColumnMetaInfo> columnMetaInfoMap = StringKeyMap.createAsFlexibleOrdered();
        final ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            final DfColumnMetaInfo metaInfo = new DfColumnMetaInfo();

            String sql2EntityRelatedTableName = null;
            try {
                sql2EntityRelatedTableName = md.getTableName(i);
            } catch (SQLException ignored) {
                // Because this table name is not required. This is for classification.
                String msg = "ResultSetMetaData.getTableName(" + i + ") threw the exception:";
                msg = msg + " " + ignored.getMessage();
                _log.info(msg);
            }
            metaInfo.setSql2EntityRelatedTableName(sql2EntityRelatedTableName);

            String columnName = md.getColumnLabel(i);
            final String relatedColumnName = md.getColumnName(i);
            metaInfo.setSql2EntityRelatedColumnName(relatedColumnName);
            if (columnName == null || columnName.trim().length() == 0) {
                columnName = relatedColumnName;
            }
            if (columnName == null || columnName.trim().length() == 0) {
                final String ln = ln();
                String msg = "The columnName is invalid: columnName=" + columnName + ln;
                msg = msg + "ResultSetMetaData returned invalid value." + ln;
                msg = msg + "sql=" + sql;
                throw new IllegalStateException(msg);
            }
            metaInfo.setColumnName(columnName);

            final int columnType = md.getColumnType(i);
            metaInfo.setJdbcDefValue(columnType);

            final String columnTypeName = md.getColumnTypeName(i);
            metaInfo.setDbTypeName(columnTypeName);

            int columnSize = md.getPrecision(i);
            if (!DfColumnHandler.isColumnSizeValid(columnSize)) {
                // ex) sum(COLUMN)
                columnSize = md.getColumnDisplaySize(i);
            }
            metaInfo.setColumnSize(columnSize);

            final int scale = md.getScale(i);
            metaInfo.setDecimalDigits(scale);

            if (forcedJavaNativeProvider != null) {
                final String sql2entityForcedJavaNative = forcedJavaNativeProvider.provide(columnName);
                metaInfo.setSql2EntityForcedJavaNative(sql2entityForcedJavaNative);
            }

            columnMetaInfoMap.put(columnName, metaInfo);
        }
        return columnMetaInfoMap;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}
