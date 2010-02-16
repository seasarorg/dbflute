package org.seasar.dbflute.helper.dataset.states;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.dataset.DfDataColumn;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataTable;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsModifiedState extends DfDtsAbstractRowState {

    public String toString() {
        return "MODIFIED";
    }

    protected DfDtsSqlContext getSqlContext(DfDataRow row) {
        DfDataTable table = row.getTable();
        StringBuffer buf = new StringBuffer(100);
        List<Object> argList = new ArrayList<Object>();
        List<Class<?>> argTypeList = new ArrayList<Class<?>>();
        buf.append("update ");
        buf.append(table.getTableName());
        buf.append(" set ");
        for (int i = 0; i < table.getColumnSize(); ++i) {
            DfDataColumn column = table.getColumn(i);
            if (column.isWritable() && !column.isPrimaryKey()) {
                buf.append(column.getColumnName());
                buf.append(" = ?, ");
                argList.add(row.getValue(i));
                argTypeList.add(column.getColumnType().getType());
            }
        }
        buf.setLength(buf.length() - 2);
        buf.append(" where ");
        for (int i = 0; i < table.getColumnSize(); ++i) {
            DfDataColumn column = table.getColumn(i);
            if (column.isPrimaryKey()) {
                buf.append(column.getColumnName());
                buf.append(" = ? and ");
                argList.add(row.getValue(i));
                argTypeList.add(column.getColumnType().getType());
            }
        }
        buf.setLength(buf.length() - 5);
        return new DfDtsSqlContext(buf.toString(), argList.toArray(), argTypeList.toArray(new Class[argTypeList.size()]));
    }
}