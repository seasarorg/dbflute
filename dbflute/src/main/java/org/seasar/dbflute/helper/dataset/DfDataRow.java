package org.seasar.dbflute.helper.dataset;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.dataset.states.DfDtsRowState;
import org.seasar.dbflute.helper.dataset.states.DfDtsRowStates;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnType;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnTypes;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataRow {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DfDataTable _table;

    private List<Object> _values = new ArrayList<Object>();

    private DfDtsRowState _state = DfDtsRowStates.UNCHANGED;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataRow(DfDataTable table) {
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
        final DfDataColumn column = _table.getColumn(columnName);
        return _values.get(column.getColumnIndex());
    }

    public void addValue(String columnName, Object value) {
        final DfDataColumn column = _table.getColumn(columnName);
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
        if (_state.equals(DfDtsRowStates.UNCHANGED)) {
            _state = DfDtsRowStates.MODIFIED;
        }
    }

    public void remove() {
        _state = DfDtsRowStates.REMOVED;
    }

    public DfDataTable getTable() {
        return _table;
    }

    public DfDtsRowState getState() {
        return _state;
    }

    public void setState(DfDtsRowState state) {
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
        if (!(o instanceof DfDataRow)) {
            return false;
        }
        DfDataRow other = (DfDataRow) o;
        for (int i = 0; i < _table.getColumnSize(); ++i) {
            String columnName = _table.getColumnName(i);
            Object value = _values.get(i);
            Object otherValue = other.getValue(columnName);
            DfDtsColumnType ct = DfDtsColumnTypes.getColumnType(value);
            if (ct.equals(value, otherValue)) {
                continue;
            }
            return false;
        }
        return true;
    }
}
