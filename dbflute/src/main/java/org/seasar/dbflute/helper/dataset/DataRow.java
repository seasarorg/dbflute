package org.seasar.dbflute.helper.dataset;

import java.util.Iterator;
import java.util.Map;

import org.seasar.dbflute.helper.collection.DfFlexibleNameMap;
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

    private DfFlexibleNameMap<String, Object> _values = new DfFlexibleNameMap<String, Object>();

    private RowState _state = RowStates.UNCHANGED;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DataRow(DataTable table) {
        _table = table;
        initValues();
    }

    private void initValues() {
        for (int i = 0; i < _table.getColumnSize(); ++i) {
            _values.put(_table.getColumnName(i), null);
        }
    }

    // ===================================================================================
    //                                                                      Value Handling
    //                                                                      ==============
    public Object getValue(int index) {
        return _values.getValue(index);
    }

    public Object getValue(String columnName) {
        DataColumn column = _table.getColumn(columnName);
        return _values.getValue(column.getColumnIndex());
    }

    public void setValue(String columnName, Object value) {
        DataColumn column = _table.getColumn(columnName);
        _values.put(columnName, column.convert(value));
        modify();
    }

    //    public void setValue(int index, Object value) {
    //        DataColumn column = table_.getColumn(index);
    //        _values.set(index, column.convert(value));
    //        modify();
    //    }

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
            Object value = _values.getValue(i);
            Object otherValue = other.getValue(columnName);
            ColumnType ct = ColumnTypes.getColumnType(value);
            if (ct.equals(value, otherValue)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public void copyFrom(Object source) {
        if (source instanceof Map) {
            copyFromMap((Map<?, ?>) source);
        } else if (source instanceof DataRow) {
            copyFromRow((DataRow) source);
        } else {
            throw new UnsupportedOperationException();
            // copyFromBean(source);
        }

    }

    private void copyFromMap(Map<?, ?> source) {
        for (Iterator<?> i = source.keySet().iterator(); i.hasNext();) {
            String columnName = (String) i.next();
            if (_table.hasColumn(columnName)) {
                Object value = source.get(columnName);
                setValue(columnName, convertValue(value));
            }
        }
    }

    private void copyFromRow(DataRow source) {
        for (int i = 0; i < source.getTable().getColumnSize(); ++i) {
            String columnName = source.getTable().getColumnName(i);
            if (_table.hasColumn(columnName)) {
                Object value = source.getValue(i);
                setValue(columnName, convertValue(value));
            }
        }
    }

    // [Unused on DBFlute]
    //    private void copyFromBean(Object source) {
    //        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(source.getClass());
    //        for (int i = 0; i < _table.getColumnSize(); ++i) {
    //            String columnName = _table.getColumnName(i);
    //            String propertyName = DfStringUtil.replace(columnName, "_", "");
    //            if (beanDesc.hasPropertyDesc(propertyName)) {
    //                PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
    //                Object value = pd.getValue(source);
    //                setValue(columnName, convertValue(value));
    //            }
    //        }
    //    }

    private Object convertValue(Object value) {
        if (value == null) {
            return null;
        }
        ColumnType columnType = ColumnTypes.getColumnType(value.getClass());
        return columnType.convert(value, null);
    }
}
