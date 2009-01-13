package org.dbflute.dbmeta.hierarchy.basic;


import org.dbflute.dbmeta.hierarchy.HierarchySourceColumn;
import org.dbflute.dbmeta.info.ColumnInfo;

/**
 * @author DBFlute(AutoGenerator)
 */
public class HierarchySourceEntityColumn implements HierarchySourceColumn {

    protected ColumnInfo columnInfo;

    public HierarchySourceEntityColumn(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public String getColumnName() {
        return columnInfo.getColumnDbName();
    }

    public java.lang.reflect.Method findGetter() {
        return columnInfo.findGetter();
    }
}