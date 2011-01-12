package org.seasar.dbflute.helper.dataset.states;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.dataset.DfDataColumn;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataTable;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsRemovedState extends DfDtsAbstractRowState {

    @Override
    public String toString() {
        return "REMOVED";
    }

    protected DfDtsSqlContext getSqlContext(DfDataRow row) {
        final DfDataTable table = row.getTable();
        final StringBuffer sb = new StringBuffer(100);
        final List<Object> argList = new ArrayList<Object>();
        final List<Class<?>> argTypeList = new ArrayList<Class<?>>();
        sb.append("delete from ");
        sb.append(table.getTableSqlName());
        sb.append(" where ");
        for (int i = 0; i < table.getColumnSize(); ++i) {
            final DfDataColumn column = table.getColumn(i);
            if (column.isPrimaryKey()) {
                sb.append(column.getColumnSqlName());
                sb.append(" = ? and ");
                argList.add(row.getValue(i));
                argTypeList.add(column.getColumnType().getType());
            }
        }
        sb.setLength(sb.length() - 5);
        return createDtsSqlContext(sb.toString(), argList, argTypeList);
    }
}