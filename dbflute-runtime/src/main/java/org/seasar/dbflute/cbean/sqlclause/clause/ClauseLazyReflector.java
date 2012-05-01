package org.seasar.dbflute.cbean.sqlclause.clause;

/**
 * @author jflute
 * @since 0.9.9.4C (2012/05/01 Tuesday)
 */
public interface ClauseLazyReflector {

    /**
     * Reflect clause to the SQL clause lazily.
     */
    void reflect();
}
