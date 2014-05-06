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
package org.seasar.dbflute.bhv;

import org.seasar.dbflute.Entity;

/**
 * The loader of nested referrer.
 * <pre>
 * MemberStatusCB cb = new MemberStatusCB();
 * cb.query().set...
 * List&lt;MemberStatus&gt; statusList = memberStatusBhv.selectList(cb);
 * memberStatusBhv.loadMemberList(statusList, new ConditionBeanSetupper&lt;MemberCB&gt;() {
 *     public void setup(MemberCB cb) {
 *         cb.query().addOrderBy_Birthdate_Asc();
 *     }
 * }).<span style="color: #DD4747">withNestedReferrer</span>(new ReferrerListHandler&lt;Member&gt;() {
 *     public void <span style="color: #DD4747">handle</span>(List&lt;Member&gt; referrerList) {
 *         <span style="color: #3F7E5E">// you can call LoadReferrer here for nested referrer as you like it</span>
 *         memberBhv.loadPurchaseList(referrerList, new ConditionBeanSetupper&lt;PurchaseCB&gt;() {
 *             public void setup(PurchaseCB cb) {
 *                 cb.query().addOrderBy_PurchasePrice_Desc();
 *             }
 *         });
 *         ...
 *     }
 * }
 * </pre>
 * @param <REFERRER> The type of referrer entity.
 * @author jflute
 * @since 1.0.5F (2014/05/06 Tuesday)
 */
public interface NestedReferrerLoader<REFERRER extends Entity> {

    /**
     * Set up nested referrer by the handler.
     * <pre>
     * MemberStatusCB cb = new MemberStatusCB();
     * cb.query().set...
     * List&lt;MemberStatus&gt; statusList = memberStatusBhv.selectList(cb);
     * memberStatusBhv.loadMemberList(statusList, new ConditionBeanSetupper&lt;MemberCB&gt;() {
     *     public void setup(MemberCB cb) {
     *         cb.query().addOrderBy_Birthdate_Asc();
     *     }
     * }).<span style="color: #DD4747">withNestedReferrer</span>(new ReferrerListHandler&lt;Member&gt;() {
     *     public void <span style="color: #DD4747">handle</span>(List&lt;Member&gt; referrerList) {
     *         <span style="color: #3F7E5E">// you can call LoadReferrer here for nested referrer as you like it</span>
     *         memberBhv.loadPurchaseList(referrerList, new ConditionBeanSetupper&lt;PurchaseCB&gt;() {
     *             public void setup(PurchaseCB cb) {
     *                 cb.query().addOrderBy_PurchasePrice_Desc();
     *             }
     *         });
     *         ...
     *     }
     * }
     * </pre>
     * @param handler The handler of referrer list. (NotNull)
     */
    void withNestedReferrer(ReferrerListHandler<REFERRER> handler);
}
