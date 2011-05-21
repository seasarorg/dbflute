package org.seasar.dbflute.cbean.sqlclause.select;

import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;

/**
 * @author jflute
 */
public class SelectedRelationColumn {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableAliasName;
    protected ColumnInfo _columnInfo;
    protected String _columnAliasName;

    // ===================================================================================
    //                                                                              Naming
    //                                                                              ======
    public String buildRealColumnSqlName() {
        final ColumnSqlName columnSqlName = _columnInfo.getColumnSqlName();
        if (_tableAliasName != null) {
            return _tableAliasName + "." + columnSqlName;
        } else {
            return columnSqlName.toString();
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableAliasName() {
        return _tableAliasName;
    }

    public void setTableAliasName(String tableAliasName) {
        this._tableAliasName = tableAliasName;
    }

    public ColumnInfo getColumnInfo() {
        return _columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this._columnInfo = columnInfo;
    }

    public String getColumnAliasName() {
        return _columnAliasName;
    }

    public void setColumnAliasName(String columnAliasName) {
        this._columnAliasName = columnAliasName;
    }
}
