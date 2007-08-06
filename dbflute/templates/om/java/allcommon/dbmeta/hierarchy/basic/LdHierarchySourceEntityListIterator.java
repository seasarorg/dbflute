package dbflute.ldb.allcommon.dbmeta.hierarchy.basic;

import dbflute.ldb.allcommon.dbmeta.hierarchy.LdHierarchySourceRow;

/**
 * 
 * @author jflute
 * @param <SOURCE_ROW>
 */
public class LdHierarchySourceEntityListIterator<SOURCE_ROW> extends LdHierarchySourceListIterator<SOURCE_ROW> {

    public LdHierarchySourceEntityListIterator(java.util.List<SOURCE_ROW> sourceRowList) {
        super(sourceRowList, new LdHierarchySourceRowSetupper<SOURCE_ROW>() {
            public LdHierarchySourceRow setup(SOURCE_ROW source) {
                return new LdHierarchySourceEntityRow(source);
            }
        });
    }
}