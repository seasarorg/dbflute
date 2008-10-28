package org.seasar.dbflute.helper.dataset;

import org.seasar.dbflute.helper.collection.DfFlexibleMap;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DataSet {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DfFlexibleMap<String, DataTable> tables = new DfFlexibleMap<String, DataTable>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DataSet() {
    }

    // ===================================================================================
    //                                                                      Table Handling
    //                                                                      ==============
    public int getTableSize() {
        return tables.size();
    }

    public String getTableName(int index) {
        return getTable(index).getTableName();
    }

    public DataTable getTable(int index) {
        return (DataTable) tables.getValue(index);
    }

    public boolean hasTable(String tableName) {
        return tables.containsKey(tableName);
    }

    public DataTable getTable(String tableName) {
        DataTable table = (DataTable) tables.get(tableName);
        if (table == null) {
            String msg = "The table was Not Found: " + tableName;
            throw new IllegalStateException(msg);
        }
        return table;
    }

    public DataTable addTable(String tableName) {
        return addTable(new DataTable(tableName));
    }

    public DataTable addTable(DataTable table) {
        tables.put(table.getTableName(), table);
        return table;
    }

    public DataTable removeTable(DataTable table) {
        return removeTable(table.getTableName());
    }

    public DataTable removeTable(int index) {
        return (DataTable) tables.remove(index);
    }

    public DataTable removeTable(String tableName) {
        DataTable table = (DataTable) tables.remove(tableName);
        if (table == null) {
            String msg = "The table was Not Found: " + tableName;
            throw new IllegalStateException(msg);
        }
        return table;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        for (int i = 0; i < getTableSize(); ++i) {
            buf.append(getTable(i));
            buf.append("\n");
        }
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DataSet)) {
            return false;
        }
        DataSet other = (DataSet) o;
        if (getTableSize() != other.getTableSize()) {
            return false;
        }
        for (int i = 0; i < getTableSize(); ++i) {
            if (!getTable(i).equals(other.getTable(i))) {
                return false;
            }
        }
        return true;
    }
}
