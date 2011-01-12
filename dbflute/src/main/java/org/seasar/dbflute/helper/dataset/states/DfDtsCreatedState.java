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
public class DfDtsCreatedState extends DfDtsAbstractRowState {

    @Override
    public String toString() {
        return "CREATED";
    }

    protected DfDtsSqlContext getSqlContext(DfDataRow row) {
        final DfDataTable table = row.getTable();
        final StringBuffer sb = new StringBuffer(100);
        final List<Object> argList = new ArrayList<Object>();
        final List<Class<?>> argTypeList = new ArrayList<Class<?>>();
        sb.append("insert into ");
        sb.append(table.getTableSqlName());
        sb.append(" (");
        int writableColumnSize = 0;
        for (int i = 0; i < table.getColumnSize(); ++i) {
            final DfDataColumn column = table.getColumn(i);
            if (column.isWritable()) {
                ++writableColumnSize;
                sb.append(column.getColumnSqlName());
                sb.append(", ");
                argList.add(row.getValue(i));
                argTypeList.add(column.getColumnType().getType());
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(") values (");
        for (int i = 0; i < writableColumnSize; ++i) {
            sb.append("?, ");
        }
        sb.setLength(sb.length() - 2);
        sb.append(")");
        return createDtsSqlContext(sb.toString(), argList, argTypeList);
    }
}