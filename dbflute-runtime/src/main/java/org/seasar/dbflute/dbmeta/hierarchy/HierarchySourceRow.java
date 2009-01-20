package org.seasar.dbflute.dbmeta.hierarchy;


/**
 * @author jflute
 */
public interface HierarchySourceRow {

    public Object extractColumnValue(HierarchySourceColumn columnInfo);
}