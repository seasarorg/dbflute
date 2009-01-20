package org.seasar.dbflute.bhv;

import org.seasar.dbflute.cbean.ConditionBean;

/**
 * The interface of condition-bean set-upper.
 * @param <CONDITION_BEAN> The type of condition-bean.
 * @author jflute
 */
public interface ConditionBeanSetupper<CONDITION_BEAN extends ConditionBean> {

    /**
     * Set up condition-bean.
     * @param cb Condition-bean. (NotNull)
     */
    public void setup(CONDITION_BEAN cb);
}
