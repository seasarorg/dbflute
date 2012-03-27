package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionQuery;

/**
 * The callback of query for specification.
 * @author jflute
 * @param <CQ> The type of condition-query.
 */
public interface HpSpQyCall<CQ extends ConditionQuery> {

    /**
     * Does it have its own query?
     * @return The determination, true or false.
     */
    boolean has();

    /**
     * Delegate query method.
     * @return The condition-query. (NotNull)
     */
    CQ qy();
}
