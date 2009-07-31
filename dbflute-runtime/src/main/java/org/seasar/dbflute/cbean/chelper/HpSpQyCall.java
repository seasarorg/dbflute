package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionQuery;

/**
 * @author jflute
 * @param <CQ> The type of condition-query.
 */
public interface HpSpQyCall<CQ extends ConditionQuery> {

    /**
     * Does it have its own query?
     * @return Determination.
     */
    public boolean has();

    /**
     * Delegate query method.
     * @return The condition-query. (NotNull)
     */
    public CQ qy();
}
