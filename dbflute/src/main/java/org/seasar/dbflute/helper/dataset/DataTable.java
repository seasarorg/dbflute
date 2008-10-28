package org.seasar.dbflute.helper.dataset;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.dataset.states.RowStates;
import org.seasar.dbflute.helper.dataset.types.ColumnType;
import org.seasar.dbflute.helper.dataset.types.ColumnTypes;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.extension.jdbc.util.ColumnDesc;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.ArrayMap;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DataTable {

    private String tableName;

    private List<DataRow> rows = new ArrayList<DataRow>();

    private List<DataRow> removedRows = new ArrayList<DataRow>();

    private ArrayMap columns = new CaseInsensitiveMap();

    private boolean hasMetaData = false;

    public DataTable(String tableName) {
        setTableName(tableName);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

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
        return (DataColumn) columns.get(index);
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
        DataColumn column = (DataColumn) columns.get(columnName);
        if (column == null) {
            String name = DfStringUtil.replace(columnName, "_", "");
            column = (DataColumn) columns.get(name);
            if (column == null) {
                for (int i = 0; i < columns.size(); ++i) {
                    String key = (String) columns.getKey(i);
                    String key2 = DfStringUtil.replace(key, "_", "");
                    if (key2.equalsIgnoreCase(name)) {
                        column = (DataColumn) columns.get(i);
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

    public void setupMetaData(DatabaseMetaData dbMetaData) {
        Set primaryKeySet = DatabaseMetaDataUtil.getPrimaryKeySet(dbMetaData, tableName);
        Map columnMap = DatabaseMetaDataUtil.getColumnMap(dbMetaData, tableName);
        for (int i = 0; i < getColumnSize(); ++i) {
            DataColumn column = getColumn(i);
            if (primaryKeySet.contains(column.getColumnName())) {
                column.setPrimaryKey(true);
            } else {
                column.setPrimaryKey(false);
            }
            if (columnMap.containsKey(column.getColumnName())) {
                column.setWritable(true);
                ColumnDesc cd = (ColumnDesc) columnMap.get(column.getColumnName());
                column.setColumnType(ColumnTypes.getColumnType(cd.getSqlType()));
            } else {
                column.setWritable(false);
            }
        }
        hasMetaData = true;
    }

    public void setupColumns(Class beanClass) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(beanClass);
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            addColumn(pd.getPropertyName(), ColumnTypes.getColumnType(pd.getPropertyType()));
        }
    }

    public void copyFrom(Object source) {
        if (source instanceof List) {
            copyFromList((List) source);
        } else {
            copyFromBeanOrMap(source);
        }

    }

    private void copyFromList(List source) {
        for (int i = 0; i < source.size(); ++i) {
            DataRow row = addRow();
            row.copyFrom(source.get(i));
            row.setState(RowStates.UNCHANGED);
        }
    }

    private void copyFromBeanOrMap(Object source) {
        DataRow row = addRow();
        row.copyFrom(source);
        row.setState(RowStates.UNCHANGED);
    }

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
}
