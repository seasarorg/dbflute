package org.seasar.dbflute.helper.dataset;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.dataset.states.DfDtsRowStates;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnType;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnTypes;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfUniqueKeyHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMetaInfo;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataTable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final String _tableDbName;
    private final Map<String, DfDataColumn> _columnMap = StringKeyMap.createAsFlexibleOrdered();
    private final List<DfDataColumn> _columnList = new ArrayList<DfDataColumn>();
    private final List<DfDataRow> _rows = new ArrayList<DfDataRow>();
    private final List<DfDataRow> _removedRows = new ArrayList<DfDataRow>();
    private boolean _hasMetaData = false;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataTable(String tableName) {
        _tableDbName = tableName;
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public int getRowSize() {
        return _rows.size();
    }

    public DfDataRow getRow(int index) {
        return (DfDataRow) _rows.get(index);
    }

    public DfDataRow addRow() {
        final DfDataRow row = new DfDataRow(this);
        _rows.add(row);
        row.setState(DfDtsRowStates.CREATED);
        return row;
    }

    public int getRemovedRowSize() {
        return _removedRows.size();
    }

    public DfDataRow getRemovedRow(int index) {
        return (DfDataRow) _removedRows.get(index);
    }

    public DfDataRow[] removeRows() {
        for (int i = 0; i < _rows.size();) {
            final DfDataRow row = getRow(i);
            if (row.getState().equals(DfDtsRowStates.REMOVED)) {
                _removedRows.add(row);
                _rows.remove(i);
            } else {
                ++i;
            }
        }
        return (DfDataRow[]) _removedRows.toArray(new DfDataRow[_removedRows.size()]);
    }

    public int getColumnSize() {
        return _columnMap.size();
    }

    public DfDataColumn getColumn(int index) {
        return _columnList.get(index);
    }

    public DfDataColumn getColumn(String columnName) {
        final DfDataColumn column = getColumn0(columnName);
        if (column == null) {
            String msg = "The column was not found in the table: ";
            msg = msg + " tableName=" + _tableDbName + " columnName=" + columnName;
            throw new IllegalStateException(msg);
        }
        return column;
    }

    private DfDataColumn getColumn0(String columnName) {
        return _columnMap.get(columnName);
    }

    public boolean hasColumn(String columnName) {
        return getColumn0(columnName) != null;
    }

    public String getColumnName(int index) {
        return getColumn(index).getColumnDbName();
    }

    public DfDtsColumnType getColumnType(int index) {
        return getColumn(index).getColumnType();
    }

    public DfDtsColumnType getColumnType(String columnName) {
        return getColumn(columnName).getColumnType();
    }

    public DfDataColumn addColumn(String columnName) {
        return addColumn(columnName, DfDtsColumnTypes.OBJECT);
    }

    public DfDataColumn addColumn(String columnName, DfDtsColumnType columnType) {
        final DfDataColumn column = new DfDataColumn(columnName, columnType, _columnMap.size());
        _columnMap.put(columnName, column);
        _columnList.add(column);
        return column;
    }

    public boolean hasMetaData() {
        return _hasMetaData;
    }

    public void setupMetaData(DatabaseMetaData metaData, UnifiedSchema unifiedSchema) throws SQLException {
        final Map<String, DfColumnMetaInfo> metaMap = extractColumnMetaMap(metaData, unifiedSchema);
        final Set<String> primaryKeySet = getPrimaryKeySet(metaData, unifiedSchema);
        for (int i = 0; i < getColumnSize(); ++i) {
            final DfDataColumn column = getColumn(i);
            if (primaryKeySet.contains(column.getColumnDbName())) {
                column.setPrimaryKey(true);
            } else {
                column.setPrimaryKey(false);
            }
            final DfColumnMetaInfo metaInfo = metaMap.get(column.getColumnDbName());
            if (metaInfo != null) {
                column.setWritable(true);
                final int jdbcDefValue = metaInfo.getJdbcDefValue();
                column.setColumnType(DfDtsColumnTypes.getColumnType(jdbcDefValue));
            } else {
                column.setWritable(false);
            }
        }
        _hasMetaData = true;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Map<String, DfColumnMetaInfo> extractColumnMetaMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema)
            throws SQLException {
        final List<DfColumnMetaInfo> metaList = new DfColumnHandler().getColumnList(metaData, unifiedSchema,
                _tableDbName);
        final Map<String, DfColumnMetaInfo> metaMap = new HashMap<String, DfColumnMetaInfo>();
        for (DfColumnMetaInfo metaInfo : metaList) {
            metaMap.put(metaInfo.getColumnName(), metaInfo);
        }
        return metaMap;
    }

    protected Set<String> getPrimaryKeySet(DatabaseMetaData metaData, UnifiedSchema unifiedSchema) {
        try {
            final DfPrimaryKeyMetaInfo pkInfo = new DfUniqueKeyHandler().getPrimaryKey(metaData, unifiedSchema,
                    _tableDbName);
            final List<String> list = pkInfo.getPrimaryKeyList();
            return new HashSet<String>(list);
        } catch (SQLException e) {
            String msg = "SQLException occured: unifiedSchema=" + unifiedSchema + " tableName=" + _tableDbName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(100);
        sb.append(_tableDbName);
        sb.append(":");
        for (int i = 0; i < _columnMap.size(); ++i) {
            sb.append(getColumnName(i));
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("\n");
        for (int i = 0; i < _rows.size(); ++i) {
            sb.append(getRow(i) + "\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DfDataTable)) {
            return false;
        }
        final DfDataTable other = (DfDataTable) o;
        if (getRowSize() != other.getRowSize()) {
            return false;
        }
        for (int i = 0; i < getRowSize(); ++i) {
            if (!getRow(i).equals(other.getRow(i))) {
                return false;
            }
        }
        if (getRemovedRowSize() != other.getRemovedRowSize()) {
            return false;
        }
        for (int i = 0; i < getRemovedRowSize(); ++i) {
            if (!getRemovedRow(i).equals(other.getRemovedRow(i))) {
                return false;
            }
        }
        return true;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableDbName() {
        return _tableDbName;
    }

    public String getTableSqlName() {
        return quoteTableNameIfNeeds(_tableDbName);
    }

    protected String quoteTableNameIfNeeds(String tableDbName) {
        final DfLittleAdjustmentProperties prop = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        return prop.quoteTableNameIfNeeds(tableDbName, true);
    }
}
