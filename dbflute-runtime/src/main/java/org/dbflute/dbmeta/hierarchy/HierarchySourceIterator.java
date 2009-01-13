package org.dbflute.dbmeta.hierarchy;


/**
 * @author DBFlute(AutoGenerator)
 */
public interface HierarchySourceIterator {

    public boolean hasNext();

    public HierarchySourceRow next();

    public HierarchySourceRow current();
}