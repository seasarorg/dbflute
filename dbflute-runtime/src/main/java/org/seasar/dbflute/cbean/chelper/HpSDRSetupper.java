package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;

/**
 * The set-upper for (Specify)DerivedReferrer.
 * @param <REFERRER_CB> The type of referrer condition-bean.
 * @param <LOCAL_CQ> The type of local condition-query.
 * @author jflute
 */
public interface HpSDRSetupper<REFERRER_CB extends ConditionBean, LOCAL_CQ extends ConditionQuery> {

    /**
     * Set up the clause for (Query)DerivedReferrer.
     * @param function The expression of function to derive referrer value. (NotNull)
     * @param subQuery The sub-query to derive. (NotNull) 
     * @param cq The condition-query of local table. (NotNull)
     * @param aliasName The alias name to set the derived value to entity. (NotNull)
     * @param option The option of DerivedReferrer. (NotNull)
     */
    void setup(String function, SubQuery<REFERRER_CB> subQuery, LOCAL_CQ cq, String aliasName,
            DerivedReferrerOption option);
}
