package dbflute.ldb.allcommon.dbmeta.hierarchy.basic;

import dbflute.ldb.allcommon.dbmeta.hierarchy.LdHierarchySourceRow;
import dbflute.ldb.allcommon.dbmeta.hierarchy.LdHierarchySourceIterator;

public class LdHierarchySourceListIterator<SOURCE_ROW> implements LdHierarchySourceIterator {

    protected java.util.List<? extends Object> sourceRowList;

    protected LdHierarchySourceRowSetupper<SOURCE_ROW> sourceEntitySetupper;

    protected java.util.Iterator<SOURCE_ROW> sourceBeanListIterator;

    protected LdHierarchySourceRow currentSourceEntity;

    public LdHierarchySourceListIterator(java.util.List<SOURCE_ROW> sourceRowList,
            LdHierarchySourceRowSetupper<SOURCE_ROW> sourceEntitySetupper) {
        this.sourceRowList = sourceRowList;
        this.sourceEntitySetupper = sourceEntitySetupper;
        this.sourceBeanListIterator = sourceRowList.iterator();
    }

    public boolean hasNext() {
        return this.sourceBeanListIterator.hasNext();
    }

    public LdHierarchySourceRow next() {
        this.currentSourceEntity = this.sourceEntitySetupper.setup(this.sourceBeanListIterator.next());
        return this.currentSourceEntity;
    }

    public LdHierarchySourceRow current() {
        return this.currentSourceEntity;
    }
}