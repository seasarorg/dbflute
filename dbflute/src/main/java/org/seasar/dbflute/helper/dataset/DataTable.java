package org.seasar.dbflute.helper.dataset;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.dataset.states.RowStates;
import org.seasar.dbflute.helper.dataset.types.ColumnType;
import org.seasar.dbflute.helper.dataset.types.ColumnTypes;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.metahandler.DfColumnHandler;
import org.seasar.dbflute.logic.metahandler.DfUniqueKeyHandler;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DataTable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String tableName;

    private List<DataRow> rows = new ArrayList<DataRow>();

    private List<DataRow> removedRows = new ArrayList<DataRow>();

    private DfFlexibleMap<String, DataColumn> columns = new DfFlexibleMap<String, DataColumn>();

    private boolean hasMetaData = false;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DataTable(String tableName) {
        setTableName(tableName);
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public int getRowSize() {
        return rows.size();
    }

    public DataRow getRow(int index) {
        return (DataRow) rows.get(index);
    }

    public DataRow addRow() {
        DataRow row = new DataRow(this);
        rows.add(row);
        row.setState(RowStates.CREATED);
        return row;
    }

    public int getRemovedRowSize() {
        return removedRows.size();
    }

    public DataRow getRemovedRow(int index) {
        return (DataRow) removedRows.get(index);
    }

    public DataRow[] removeRows() {
        for (int i = 0; i < rows.size();) {
            DataRow row = getRow(i);
            if (row.getState().equals(RowStates.REMOVED)) {
                removedRows.add(row);
                rows.remove(i);
            } else {
                ++i;
            }
        }
        return (DataRow[]) removedRows.toArray(new DataRow[removedRows.size()]);
    }

    public int getColumnSize() {
        return columns.size();
    }

    public DataColumn getColumn(int index) {
        return columns.getValue(index);
    }

    public DataColumn getColumn(String columnName) {
        DataColumn column = getColumn0(columnName);
        if (column == null) {
            String msg = "The column was not found in the table: ";
            msg = msg + " tableName=" + tableName + " columnName" + columnName;
            throw new IllegalStateException(msg);
        }
        return column;
    }

    private DataColumn getColumn0(String columnName) {
        DataColumn column = columns.get(columnName);
        if (column == null) {
            String name = DfStringUtil.replace(columnName, "_", "");
            column = columns.get(name);
            if (column == null) {
                for (int i = 0; i < columns.size(); ++i) {
                    String key = (String) columns.getKey(i);
                    String key2 = DfStringUtil.replace(key, "_", "");
                    if (key2.equalsIgnoreCase(name)) {
                        column = columns.getValue(i);
                        break;
                    }
                }
            }
        }
        return column;
    }

    public boolean hasColumn(String columnName) {
        return getColumn0(columnName) != null;
    }

    public String getColumnName(int index) {
        return getColumn(index).getColumnName();
    }

    public ColumnType getColumnType(int index) {
        return getColumn(index).getColumnType();
    }

    public ColumnType getColumnType(String columnName) {
        return getColumn(columnName).getColumnType();
    }

    public DataColumn addColumn(String columnName) {
        return addColumn(columnName, ColumnTypes.OBJECT);
    }

    public DataColumn addColumn(String columnName, ColumnType columnType) {
        DataColumn column = new DataColumn(columnName, columnType, columns.size());
        columns.put(columnName, column);
        return column;
    }

    public boolean hasMetaData() {
        return hasMetaData;
    }

    public void setupMetaData(DatabaseMetaData metaData, String schemaName) {
        final Map<String, DfColumnMetaInfo> metaMap = extractColumnMetaMap(metaData, schemaName);
        final Set<String> primaryKeySet = getPrimaryKeySet(metaData, schemaName);
        for (int i = 0; i < getColumnSize(); ++i) {
            final DataColumn column = getColumn(i);
            if (primaryKeySet.contains(column.getColumnName())) {
                column.setPrimaryKey(true);
            } else {
                column.setPrimaryKey(false);
            }
            final DfColumnMetaInfo metaInfo = metaMap.get(column.getColumnName());
            if (metaInfo != null) {
                column.setWritable(true);
                final int jdbcDefValue = metaInfo.getJdbcDefValue();
                column.setColumnType(ColumnTypes.getColumnType(jdbcDefValue));
            } else {
                column.setWritable(false);
            }
        }
        hasMetaData = true;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Map<String, DfColumnMetaInfo> extractColumnMetaMap(DatabaseMetaData metaData, String schemaName) {
        final List<DfColumnMetaInfo> metaList = new DfColumnHandler().getColumns(metaData, schemaName, tableName);
        final Map<String, DfColumnMetaInfo> metaMap = new HashMap<String, DfColumnMetaInfo>();
        for (DfColumnMetaInfo metaInfo : metaList) {
            metaMap.put(metaInfo.getColumnName(), metaInfo);
        }
        return metaMap;
    }

    protected Set<String> getPrimaryKeySet(DatabaseMetaData metaData, String schemaName) {
        try {
            List<String> list = new DfUniqueKeyHandler().getPrimaryColumnNameList(metaData, schemaName, tableName);
            return new HashSet<String>(list);
        } catch (SQLException e) {
            String msg = "SQLException occured: schemaName=" + schemaName + " tableName=" + tableName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append(tableName);
        buf.append(":");
        for (int i = 0; i < columns.size(); ++i) {
            buf.append(getColumnName(i));
            buf.append(", ");
        }
        buf.setLength(buf.length() - 2);
        buf.append("\n");
        for (int i = 0; i < rows.size(); ++i) {
            buf.append(getRow(i) + "\n");
        }
        buf.setLength(buf.length() - 1);
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DataTable)) {
            return false;
        }
        DataTable other = (DataTable) o;
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
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
