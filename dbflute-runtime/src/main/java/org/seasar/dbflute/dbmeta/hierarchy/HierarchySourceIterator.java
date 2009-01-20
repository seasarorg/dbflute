package org.seasar.dbflute.dbmeta.hierarchy;


/**
 * @author jflute
 */
public interface HierarchySourceIterator {

    public boolean hasNext();

    public HierarchySourceRow next();

    public HierarchySourceRow current();
}