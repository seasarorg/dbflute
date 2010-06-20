package org.seasar.dbflute.cbean.sqlclause.subquery;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class SubQueryPath {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _subQueryPath;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param subQueryPath The property path of sub-query. (NotNull)
     */
    public SubQueryPath(String subQueryPath) {
        _subQueryPath = subQueryPath;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int hashCode() {
        return _subQueryPath.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SubQueryPath)) {
            return false;
        }
        final SubQueryPath target = (SubQueryPath) obj;
        return _subQueryPath.equals(target.toString());
    }

    @Override
    public String toString() {
        return _subQueryPath;
    }
}
