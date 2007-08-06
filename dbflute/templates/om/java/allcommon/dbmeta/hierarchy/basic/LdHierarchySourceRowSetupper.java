package dbflute.ldb.allcommon.dbmeta.hierarchy.basic;

import dbflute.ldb.allcommon.dbmeta.hierarchy.LdHierarchySourceRow;

public interface LdHierarchySourceRowSetupper<SOURCE_ROW> {

    public LdHierarchySourceRow setup(SOURCE_ROW source);
}