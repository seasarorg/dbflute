package org.seasar.dbflute.helper.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.helper.StringKeyMap;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DataSet {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Map<String, DataTable> _tableMap = StringKeyMap.createAsFlexibleOrdered();
    private List<DataTable> _tableList = new ArrayList<DataTable>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DataSet() {
    }

    // ===================================================================================
    //                                                                      Table Handling
    //                                                                      ==============
    public int getTableSize() {
        return _tableMap.size();
    }

    public String getTableName(int index) {
        return getTable(index).getTableName();
    }

    public DataTable getTable(int index) {
        return (DataTable) _tableList.get(index);
    }

    public boolean hasTable(String tableName) {
        return _tableMap.containsKey(tableName);
    }

    public DataTable getTable(String tableName) {
        DataTable table = (DataTable) _tableMap.get(tableName);
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
        _tableMap.put(table.getTableName(), table);
        _tableList.add(table);
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
