package org.seasar.dbflute.cbean.sqlclause.join;

/**
 * @author jflute
 * @since 0.9.9.0A (2011/07/27 Wednesday)
 */
public interface InnerJoinLazyReflector {

    /**
     * Reflect inner-join to the corresponding join info lazily.
     */
    void reflect();
}
