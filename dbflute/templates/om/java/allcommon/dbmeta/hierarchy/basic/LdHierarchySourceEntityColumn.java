package dbflute.ldb.allcommon.dbmeta.hierarchy.basic;

import dbflute.ldb.allcommon.dbmeta.hierarchy.LdHierarchySourceColumn;
import dbflute.ldb.allcommon.dbmeta.info.LdColumnInfo;

/**
 * @author jflute
 */
public class LdHierarchySourceEntityColumn implements LdHierarchySourceColumn {

    protected LdColumnInfo columnInfo;

    public LdHierarchySourceEntityColumn(LdColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public String getColumnName() {
        return columnInfo.getColumnDbName();
    }

    public java.lang.reflect.Method findGetter() {
        return columnInfo.findGetter();
    }
}