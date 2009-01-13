package org.dbflute.dbmeta.hierarchy.basic;


/**
 * @author DBFlute(AutoGenerator)
 * @param <SOURCE_ROW> The type of source.
 */
public class HierarchySourceEntityListIterator<SOURCE_ROW> extends HierarchySourceListIterator<SOURCE_ROW> {

    /**
     * Constructor.
     * 
     * @param sourceRowList The list of source row. (NotNull)
     */
    public HierarchySourceEntityListIterator(java.util.List<SOURCE_ROW> sourceRowList) {
        super(sourceRowList, new HierarchySourceRowSetupper<SOURCE_ROW>() {
            public org.dbflute.dbmeta.hierarchy.HierarchySourceRow setup(SOURCE_ROW source) {
                return new HierarchySourceEntityRow(source);
            }
        });
    }
}