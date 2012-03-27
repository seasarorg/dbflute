package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SubQuery;

/**
 * The set-upper for ScalarCondition (the old name: ScalarSubQuery).
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpSSQSetupper<CB extends ConditionBean> {

    /**
     * Set up the scalar condition.
     * @param function The expression of function to derive the scalar value. (NotNull)
     * @param subQuery The sub query of myself. (NotNull)
     * @param option The option of ScalarCondition. (NotNull)
     */
    void setup(String function, SubQuery<CB> subQuery, HpSSQOption<CB> option);
}
