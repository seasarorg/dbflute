package org.seasar.dbflute.dbmeta.hierarchy.basic;


/**
 * @author DBFlute(AutoGenerator)
 * @param <SOURCE_ROW> The type of source.
 */
public class HierarchySourceListIterator<SOURCE_ROW> implements org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceIterator {

    protected java.util.List<? extends Object> sourceRowList;

    protected HierarchySourceRowSetupper<SOURCE_ROW> sourceRowSetupper;

    protected java.util.Iterator<SOURCE_ROW> sourceBeanListIterator;

    protected org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceRow currentSourceEntity;

    public HierarchySourceListIterator(java.util.List<SOURCE_ROW> sourceRowList,
            HierarchySourceRowSetupper<SOURCE_ROW> sourceRowSetupper) {
        this.sourceRowList = sourceRowList;
        this.sourceRowSetupper = sourceRowSetupper;
        this.sourceBeanListIterator = sourceRowList.iterator();
    }

    public boolean hasNext() {
        return this.sourceBeanListIterator.hasNext();
    }

    public org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceRow next() {
        this.currentSourceEntity = this.sourceRowSetupper.setup(this.sourceBeanListIterator.next());
        return this.currentSourceEntity;
    }

    public org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceRow current() {
        return this.currentSourceEntity;
    }
}