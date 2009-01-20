package org.seasar.dbflute.dbmeta.hierarchy.basic;


/**
 * @author jflute
 * @param <SOURCE_ROW> The type of source.
 */
public interface HierarchySourceRowSetupper<SOURCE_ROW> {

    public org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceRow setup(SOURCE_ROW source);
}