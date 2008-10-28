package org.seasar.dbflute.helper.dataset.states;


/**
 * Row States. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class CreatedState extends AbstractRowState {

    public String toString() {
        return "CREATED";
    }

    //    protected SqlContext getSqlContext(DataRow row) {
    //        DataTable table = row.getTable();
    //        StringBuffer buf = new StringBuffer(100);
    //        List argList = new ArrayList();
    //        List argTypeList = new ArrayList();
    //        buf.append("INSERT INTO ");
    //        buf.append(table.getTableName());
    //        buf.append(" (");
    //        int writableColumnSize = 0;
    //        for (int i = 0; i < table.getColumnSize(); ++i) {
    //            DataColumn column = table.getColumn(i);
    //            if (column.isWritable()) {
    //                ++writableColumnSize;
    //                buf.append(column.getColumnName());
    //                buf.append(", ");
    //                argList.add(row.getValue(i));
    //                argTypeList.add(column.getColumnType().getType());
    //            }
    //        }
    //        buf.setLength(buf.length() - 2);
    //        buf.append(") VALUES (");
    //        for (int i = 0; i < writableColumnSize; ++i) {
    //            buf.append("?, ");
    //        }
    //        buf.setLength(buf.length() - 2);
    //        buf.append(")");
    //        return new SqlContext(buf.toString(), argList.toArray(), (Class[]) argTypeList.toArray(new Class[argTypeList
    //                .size()]));
    //    }
}