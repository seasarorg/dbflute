package org.seasar.dbflute.helper.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.helper.StringKeyMap;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataSet {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Map<String, DfDataTable> _tableMap = StringKeyMap.createAsFlexibleOrdered();
    private List<DfDataTable> _tableList = new ArrayList<DfDataTable>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataSet() {
    }

    // ===================================================================================
    //                                                                      Table Handling
    //                                                                      ==============
    public int getTableSize() {
        return _tableMap.size();
    }

    public String getTableName(int index) {
        return getTable(index).getTableDbName();
    }

    public DfDataTable getTable(int index) {
        return (DfDataTable) _tableList.get(index);
    }

    public boolean hasTable(String tableName) {
        return _tableMap.containsKey(tableName);
    }

    public DfDataTable getTable(String tableName) {
        DfDataTable table = (DfDataTable) _tableMap.get(tableName);
        if (table == null) {
            String msg = "The table was Not Found: " + tableName;
            throw new IllegalStateException(msg);
        }
        return table;
    }

    public DfDataTable addTable(String tableName) {
        return addTable(new DfDataTable(tableName));
    }

    public DfDataTable addTable(DfDataTable table) {
        _tableMap.put(table.getTableDbName(), table);
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
        if (!(o instanceof DfDataSet)) {
            return false;
        }
        DfDataSet other = (DfDataSet) o;
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
