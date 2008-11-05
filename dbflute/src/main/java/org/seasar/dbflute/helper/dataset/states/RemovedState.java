package org.seasar.dbflute.helper.dataset.states;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.dataset.DataColumn;
import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataTable;

/**
 * {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class RemovedState extends AbstractRowState {

    public String toString() {
        return "REMOVED";
    }

    protected SqlContext getSqlContext(DataRow row) {
        DataTable table = row.getTable();
        StringBuffer buf = new StringBuffer(100);
        List<Object> argList = new ArrayList<Object>();
        List<Class<?>> argTypeList = new ArrayList<Class<?>>();
        buf.append("delete from ");
        buf.append(table.getTableName());
        buf.append(" where ");
        for (int i = 0; i < table.getColumnSize(); ++i) {
            DataColumn column = table.getColumn(i);
            if (column.isPrimaryKey()) {
                buf.append(column.getColumnName());
                buf.append(" = ? and ");
                argList.add(row.getValue(i));
                argTypeList.add(column.getColumnType().getType());
            }
        }
        buf.setLength(buf.length() - 5);
        return new SqlContext(buf.toString(), argList.toArray(), argTypeList.toArray(new Class[argTypeList.size()]));
    }
}