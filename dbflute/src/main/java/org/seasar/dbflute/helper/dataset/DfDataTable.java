package org.seasar.dbflute.helper.dataset;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.dataset.states.DtsRowStates;
import org.seasar.dbflute.helper.dataset.types.DtsColumnType;
import org.seasar.dbflute.helper.dataset.types.DtsColumnTypes;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfUniqueKeyHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMetaInfo;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataTable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _tableName;
    private Map<String, DfDataColumn> _columnMap = StringKeyMap.createAsFlexibleOrdered();
    private List<DfDataColumn> _columnList = new ArrayList<DfDataColumn>();
    private List<DfDataRow> _rows = new ArrayList<DfDataRow>();
    private List<DfDataRow> _removedRows = new ArrayList<DfDataRow>();
    private boolean _hasMetaData = false;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataTable(String tableName) {
        setTableName(tableName);
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
        DfDataRow row = new DfDataRow(this);
        _rows.add(row);
        row.setState(DtsRowStates.CREATED);
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
            DfDataRow row = getRow(i);
            if (row.getState().equals(DtsRowStates.REMOVED)) {
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
        DfDataColumn column = getColumn0(columnName);
        if (column == null) {
            String msg = "The column was not found in the table: ";
            msg = msg + " tableName=" + _tableName + " columnName=" + columnName;
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
        return getColumn(index).getColumnName();
    }

    public DtsColumnType getColumnType(int index) {
        return getColumn(index).getColumnType();
    }

    public DtsColumnType getColumnType(String columnName) {
        return getColumn(columnName).getColumnType();
    }

    public DfDataColumn addColumn(String columnName) {
        return addColumn(columnName, DtsColumnTypes.OBJECT);
    }

    public DfDataColumn addColumn(String columnName, DtsColumnType columnType) {
        DfDataColumn column = new DfDataColumn(columnName, columnType, _columnMap.size());
        _columnMap.put(columnName, column);
        _columnList.add(column);
        return column;
    }

    public boolean hasMetaData() {
        return _hasMetaData;
    }

    public void setupMetaData(DatabaseMetaData metaData, String schemaName) {
        final Map<String, DfColumnMetaInfo> metaMap = extractColumnMetaMap(metaData, schemaName);
        final Set<String> primaryKeySet = getPrimaryKeySet(metaData, schemaName);
        for (int i = 0; i < getColumnSize(); ++i) {
            final DfDataColumn column = getColumn(i);
            if (primaryKeySet.contains(column.getColumnName())) {
                column.setPrimaryKey(true);
            } else {
                column.setPrimaryKey(false);
            }
            final DfColumnMetaInfo metaInfo = metaMap.get(column.getColumnName());
            if (metaInfo != null) {
                column.setWritable(true);
                final int jdbcDefValue = metaInfo.getJdbcDefValue();
                column.setColumnType(DtsColumnTypes.getColumnType(jdbcDefValue));
            } else {
                column.setWritable(false);
            }
        }
        _hasMetaData = true;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Map<String, DfColumnMetaInfo> extractColumnMetaMap(DatabaseMetaData metaData, String schemaName) {
        final List<DfColumnMetaInfo> metaList = new DfColumnHandler().getColumnList(metaData, schemaName, _tableName);
        final Map<String, DfColumnMetaInfo> metaMap = new HashMap<String, DfColumnMetaInfo>();
        for (DfColumnMetaInfo metaInfo : metaList) {
            metaMap.put(metaInfo.getColumnName(), metaInfo);
        }
        return metaMap;
    }

    protected Set<String> getPrimaryKeySet(DatabaseMetaData metaData, String schemaName) {
        try {
            final DfPrimaryKeyMetaInfo pkInfo = new DfUniqueKeyHandler()
                    .getPrimaryKey(metaData, schemaName, _tableName);
            final List<String> list = pkInfo.getPrimaryKeyList();
            return new HashSet<String>(list);
        } catch (SQLException e) {
            String msg = "SQLException occured: schemaName=" + schemaName + " tableName=" + _tableName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append(_tableName);
        buf.append(":");
        for (int i = 0; i < _columnMap.size(); ++i) {
            buf.append(getColumnName(i));
            buf.append(", ");
        }
        buf.setLength(buf.length() - 2);
        buf.append("\n");
        for (int i = 0; i < _rows.size(); ++i) {
            buf.append(getRow(i) + "\n");
        }
        buf.setLength(buf.length() - 1);
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DfDataTable)) {
            return false;
        }
        DfDataTable other = (DfDataTable) o;
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
    public String getTableName() {
        return _tableName;
    }

    public void setTableName(String tableName) {
        this._tableName = tableName;
    }
}
