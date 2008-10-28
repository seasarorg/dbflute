package org.seasar.dbflute.helper.dataset;

import java.util.Iterator;
import java.util.Map;

import org.seasar.dbflute.helper.dataset.states.RowState;
import org.seasar.dbflute.helper.dataset.states.RowStates;
import org.seasar.dbflute.helper.dataset.types.ColumnType;
import org.seasar.dbflute.helper.dataset.types.ColumnTypes;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.ArrayMap;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * Data Row. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DataRow {

    private DataTable table_;

    private ArrayMap values_ = new CaseInsensitiveMap();

    private RowState state_ = RowStates.UNCHANGED;

    public DataRow(DataTable table) {
        table_ = table;
        initValues();
    }

    private void initValues() {
        for (int i = 0; i < table_.getColumnSize(); ++i) {
            values_.put(table_.getColumnName(i), null);
        }
    }

    public Object getValue(int index) {
        return values_.get(index);
    }

    public Object getValue(String columnName) {
        DataColumn column = table_.getColumn(columnName);
        return values_.get(column.getColumnIndex());
    }

    public void setValue(String columnName, Object value) {
        DataColumn column = table_.getColumn(columnName);
        values_.put(columnName, column.convert(value));
        modify();
    }

    public void setValue(int index, Object value) {
        DataColumn column = table_.getColumn(index);
        values_.set(index, column.convert(value));
        modify();
    }

    private void modify() {
        if (state_.equals(RowStates.UNCHANGED)) {
            state_ = RowStates.MODIFIED;
        }
    }

    public void remove() {
        state_ = RowStates.REMOVED;
    }

    public DataTable getTable() {
        return table_;
    }

    public RowState getState() {
        return state_;
    }

    public void setState(RowState state) {
        state_ = state;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("{");
        for (int i = 0; i < values_.size(); ++i) {
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
        for (int i = 0; i < table_.getColumnSize(); ++i) {
            String columnName = table_.getColumnName(i);
            Object value = values_.get(i);
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
            copyFromBean(source);
        }

    }

    private void copyFromMap(Map<?, ?> source) {
        for (Iterator<?> i = source.keySet().iterator(); i.hasNext();) {
            String columnName = (String) i.next();
            if (table_.hasColumn(columnName)) {
                Object value = source.get(columnName);
                setValue(columnName, convertValue(value));
            }
        }
    }

    private void copyFromRow(DataRow source) {
        for (int i = 0; i < source.getTable().getColumnSize(); ++i) {
            String columnName = source.getTable().getColumnName(i);
            if (table_.hasColumn(columnName)) {
                Object value = source.getValue(i);
                setValue(columnName, convertValue(value));
            }
        }
    }

    private void copyFromBean(Object source) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(source.getClass());
        for (int i = 0; i < table_.getColumnSize(); ++i) {
            String columnName = table_.getColumnName(i);
            String propertyName = DfStringUtil.replace(columnName, "_", "");
            if (beanDesc.hasPropertyDesc(propertyName)) {
                PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
                Object value = pd.getValue(source);
                setValue(columnName, convertValue(value));
            }
        }
    }

    private Object convertValue(Object value) {
        if (value == null) {
            return null;
        }
        ColumnType columnType = ColumnTypes.getColumnType(value.getClass());
        return columnType.convert(value, null);
    }
}
