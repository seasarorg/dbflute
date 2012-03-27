package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;

/**
 * The set-upper for (Query)DerivedReferrer.
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpQDRSetupper<CB extends ConditionBean> {

    /**
     * Set up the clause for (Query)DerivedReferrer.
     * @param function The expression of function to derive referrer value. (NotNull)
     * @param subQuery The sub-query to derive. (NotNull) 
     * @param operand The operand for the condition. (NotNull)
     * @param value The value of the condition. (NotNull)
     * @param option The option of DerivedReferrer. (NotNull)
     */
    void setup(String function, SubQuery<CB> subQuery, String operand, Object value, DerivedReferrerOption option);
}
