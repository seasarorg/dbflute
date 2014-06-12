/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ScalarQuery;
import org.seasar.dbflute.cbean.coption.ScalarSelectOption;

/**
 * The function for scalar select. 
 * @param <CB> The type of condition-bean.
 * @param <RESULT> The type of result for scalar select
 * @author jflute
 */
public class HpSLSFunction<CB extends ConditionBean, RESULT> extends HpSLSProtoFunction<CB, RESULT> {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param conditionBean The condition-bean initialized only for scalar select. (NotNull)
     * @param resultType The type os result. (NotNull)
     * @param executor The executor of scalar select with select clause type. (NotNull)
     */
    public HpSLSFunction(CB conditionBean, Class<RESULT> resultType, HpSLSExecutor<CB, RESULT> executor) {
        super(conditionBean, resultType, executor);
    }

    // ===================================================================================
    //                                                                            Function
    //                                                                            ========
    /**
     * Select the count value. <br />
     * You can also get same result by selectCount(cb) method.
     * <pre>
     * memberBhv.scalarSelect(Integer.class).<span style="color: #DD4747">count</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnMemberId</span>(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * });
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @return The count value calculated by function. (NotNull)
     */
    public RESULT count(ScalarQuery<CB> scalarQuery) {
        return facadeCount(scalarQuery);
    }

    /**
     * Select the count value with function conversion option.
     * <pre>
     * memberBhv.scalarSelect(Integer.class).<span style="color: #DD4747">count</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().columnMemberId(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * }, new ScalarSelectOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @param option The option for scalar. (NotNull)
     * @return The count value calculated by function. (NotNull)
     */
    public RESULT count(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
        return facadeCount(scalarQuery, option);
    }

    /**
     * Select the count-distinct value. <br />
     * You can also get same result by selectCount(cb) method.
     * <pre>
     * memberBhv.scalarSelect(Integer.class).<span style="color: #DD4747">countDistinct</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnMemberId</span>(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * });
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @return The count-distinct value calculated by function. (NotNull)
     */
    public RESULT countDistinct(ScalarQuery<CB> scalarQuery) {
        return facadeCountDistinct(scalarQuery);
    }

    /**
     * Select the count-distinct value with function conversion option.
     * <pre>
     * memberBhv.scalarSelect(Integer.class).<span style="color: #DD4747">countDistinct</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().columnMemberId(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * }, new ScalarSelectOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @param option The option for scalar. (NotNull)
     * @return The count-distinct value calculated by function. (NotNull)
     */
    public RESULT countDistinct(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
        return facadeCountDistinct(scalarQuery, option);
    }

    /**
     * Select the maximum value.
     * <pre>
     * memberBhv.scalarSelect(Date.class).<span style="color: #DD4747">max</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * });
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @return The maximum value calculated by function. (NullAllowed)
     */
    public RESULT max(ScalarQuery<CB> scalarQuery) {
        return facadeMax(scalarQuery);
    }

    /**
     * Select the maximum value with function conversion option.
     * <pre>
     * memberBhv.scalarSelect(Date.class).<span style="color: #DD4747">max</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * }, new ScalarSelectOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @param option The option for scalar. (NotNull)
     * @return The maximum value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
     */
    public RESULT max(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
        return facadeMax(scalarQuery, option);
    }

    /**
     * Select the minimum value.
     * <pre>
     * memberBhv.scalarSelect(Date.class).<span style="color: #DD4747">min</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * });
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @return The minimum value calculated by function. (NullAllowed)
     */
    public RESULT min(ScalarQuery<CB> scalarQuery) {
        return facadeMin(scalarQuery);
    }

    /**
     * Select the minimum value with function conversion option.
     * <pre>
     * memberBhv.scalarSelect(Date.class).<span style="color: #DD4747">min</span>(new ScalarQuery(MemberCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
     * }, new ScalarSelectOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @param option The option for scalar. (NotNull)
     * @return The minimum value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
     */
    public RESULT min(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
        return facadeMin(scalarQuery, option);
    }

    /**
     * Select the summary value.
     * <pre>
     * purchaseBhv.scalarSelect(Integer.class).<span style="color: #DD4747">sum</span>(new ScalarQuery(PurchaseCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
     * });
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @return The summary value calculated by function. (NullAllowed)
     */
    public RESULT sum(ScalarQuery<CB> scalarQuery) {
        return facadeSum(scalarQuery);
    }

    /**
     * Select the summary value with function conversion option.
     * <pre>
     * purchaseBhv.scalarSelect(Integer.class).<span style="color: #DD4747">sum</span>(new ScalarQuery(PurchaseCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
     * }, new ScalarSelectOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @param option The option for scalar. (NotNull)
     * @return The summary value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
     */
    public RESULT sum(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
        return facadeSum(scalarQuery, option);
    }

    /**
     * Select the average value.
     * <pre>
     * purchaseBhv.scalarSelect(Integer.class).<span style="color: #DD4747">avg</span>(new ScalarQuery(PurchaseCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
     * });
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @return The average value calculated by function. (NullAllowed)
     */
    public RESULT avg(ScalarQuery<CB> scalarQuery) {
        return facadeAvg(scalarQuery);
    }

    /**
     * Select the average value.
     * <pre>
     * purchaseBhv.scalarSelect(Integer.class).<span style="color: #DD4747">avg</span>(new ScalarQuery(PurchaseCB cb) {
     *     cb.specify().<span style="color: #DD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
     *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
     * }, new ScalarSelectOption().<span style="color: #DD4747">coalesce</span>(0));
     * </pre>
     * @param scalarQuery The query for scalar. (NotNull)
     * @param option The option for scalar. (NotNull)
     * @return The average value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
     */
    public RESULT avg(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
        return facadeAvg(scalarQuery, option);
    }
}
