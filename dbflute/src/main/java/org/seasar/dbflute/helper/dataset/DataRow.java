package org.seasar.dbflute.helper.dataset;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.dataset.states.RowState;
import org.seasar.dbflute.helper.dataset.states.RowStates;
import org.seasar.dbflute.helper.dataset.types.ColumnType;
import org.seasar.dbflute.helper.dataset.types.ColumnTypes;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DataRow {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DataTable _table;

    private List<Object> _values = new ArrayList<Object>();

    private RowState _state = RowStates.UNCHANGED;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DataRow(DataTable table) {
        _table = table;

        // [Unused on DBFlute]
        // initValues();
    }

    // [Unused on DBFlute]
    // private void initValues() {
    //     for (int i = 0; i < _table.getColumnSize(); ++i) {
    //         _values.add(null);
    //     }
    // }

    // ===================================================================================
    //                                                                      Value Handling
    //                                                                      ==============
    public Object getValue(int index) {
        return _values.get(index);
    }

    public Object getValue(String columnName) {
        final DataColumn column = _table.getColumn(columnName);
        return _values.get(column.getColumnIndex());
    }

    public void addValue(String columnName, Object value) {
        final DataColumn column = _table.getColumn(columnName);
        _values.add(column.convert(value));
        modify();
    }

    // [Unused on DBFlute]
    // public void setValue(int index, Object value) {
    //     final DataColumn column = _table.getColumn(index);
    //     _values.set(index, column.convert(value));
    //     modify();
    // }

    private void modify() {
        if (_state.equals(RowStates.UNCHANGED)) {
            _state = RowStates.MODIFIED;
        }
    }

    public void remove() {
        _state = RowStates.REMOVED;
    }

    public DataTable getTable() {
        return _table;
    }

    public RowState getState() {
        return _state;
    }

    public void setState(RowState state) {
        _state = state;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("{");
        for (int i = 0; i < _values.size(); ++i) {
            buf.append(getValue(i));
            buf.append(", ");
        }
        buf.setLength(buf.length() - 2);
        buf.append('}');
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DataRow)) {
            return false;
        }
        DataRow other = (DataRow) o;
        for (int i = 0; i < _table.getColumnSize(); ++i) {
            String columnName = _table.getColumnName(i);
            Object value = _values.get(i);
            Object otherValue = other.getValue(columnName);
            ColumnType ct = ColumnTypes.getColumnType(value);
            if (ct.equals(value, otherValue)) {
                continue;
            }
            return false;
        }
        return true;
    }
}
