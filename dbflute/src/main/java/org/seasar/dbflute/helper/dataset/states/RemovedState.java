package org.seasar.dbflute.helper.dataset.states;


/**
 * {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class RemovedState extends AbstractRowState {

    public String toString() {
        return "REMOVED";
    }

    //    protected SqlContext getSqlContext(DataRow row) {
    //        DataTable table = row.getTable();
    //        StringBuffer buf = new StringBuffer(100);
    //        List argList = new ArrayList();
    //        List argTypeList = new ArrayList();
    //        buf.append("DELETE FROM ");
    //        buf.append(table.getTableName());
    //        buf.append(" WHERE ");
    //        for (int i = 0; i < table.getColumnSize(); ++i) {
    //            DataColumn column = table.getColumn(i);
    //            if (column.isPrimaryKey()) {
    //                buf.append(column.getColumnName());
    //                buf.append(" = ? AND ");
    //                argList.add(row.getValue(i));
    //                argTypeList.add(column.getColumnType().getType());
    //            }
    //        }
    //        buf.setLength(buf.length() - 5);
    //        return new SqlContext(buf.toString(), argList.toArray(), (Class[]) argTypeList.toArray(new Class[argTypeList
    //                .size()]));
    //    }
}