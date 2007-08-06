package dbflute.ldb.allcommon.dbmeta.hierarchy;


/**
 * 
 * @author jflute
 */
public interface LdHierarchySourceIterator {

    public boolean hasNext();

    public LdHierarchySourceRow next();

    public LdHierarchySourceRow current();
}