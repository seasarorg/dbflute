package org.seasar.dbflute.logic.doc.dataxls;

import java.util.List;

import org.apache.torque.engine.database.model.Column;

/**
 * @author jflute
 */
public class DfTemplateDataTableInfo {

    protected String _tableDbName;
    protected String _tableSqlName;
    protected List<Column> _columnList;

    public String getTableDbName() {
        return _tableDbName;
    }

    public void setTableDbName(String tableDbName) {
        this._tableDbName = tableDbName;
    }

    public String getTableSqlName() {
        return _tableSqlName;
    }

    public void setTableSqlName(String tableSqlName) {
        this._tableSqlName = tableSqlName;
    }

    public List<Column> getColumnList() {
        return _columnList;
    }

    public void setColumnList(List<Column> columnList) {
        this._columnList = columnList;
    }
}
