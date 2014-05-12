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
 * MemberCB cb = new MemberCB();
 * cb.query().set...
 * List&lt;Member&gt; memberList = memberBhv.selectList(cb);
 * memberBhv.loadPurchaseList(memberList, new ReferrerConditionSetupper&lt;PurchaseCB&gt;() {
 *     public void setup(PurchaseCB cb) {
 *         cb.query().addOrderBy_PurchaseDatetime_Asc();
 *     }
 * }).<span style="color: #DD4747">withNestedReferrer</span>(new ReferrerListHandler&lt;Purchase&gt;() {
 *     public void <span style="color: #DD4747">handle</span>(List&lt;Purchase&gt; referrerList) {
 *         <span style="color: #3F7E5E">// you can call LoadReferrer here for nested referrer as you like it</span>
 *         purchaseBhv.loadPurchaseDetailList(referrerList, new ReferrerConditionSetupper&lt;PurchaseDetailCB&gt;() {
 *             public void setup(PurchaseDetailCB cb) {
 *                 ...
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
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * List&lt;Member&gt; memberList = memberBhv.selectList(cb);
     * memberBhv.loadPurchaseList(memberList, new ReferrerConditionSetupper&lt;PurchaseCB&gt;() {
     *     public void setup(PurchaseCB cb) {
     *         cb.query().addOrderBy_PurchaseDatetime_Asc();
     *     }
     * }).<span style="color: #DD4747">withNestedReferrer</span>(new ReferrerListHandler&lt;Purchase&gt;() {
     *     public void <span style="color: #DD4747">handle</span>(List&lt;Purchase&gt; referrerList) {
     *         <span style="color: #3F7E5E">// you can call LoadReferrer here for nested referrer as you like it</span>
     *         purchaseBhv.loadPurchaseDetailList(referrerList, new ReferrerConditionSetupper&lt;PurchaseDetailCB&gt;() {
     *             public void setup(PurchaseDetailCB cb) {
     *                 ...
     *             }
     *         });
     *         ...
     *     }
     * }
     * </pre>
     * @param handler The handler of referrer list to load nested referrer. (NotNull)
     */
    void withNestedReferrer(ReferrerListHandler<REFERRER> handler);
}
