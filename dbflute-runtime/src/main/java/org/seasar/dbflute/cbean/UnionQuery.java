/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean;

/**
 * <pre>
 * cb.union(new UnionQuery&lt;FooCB&gt;() {
 *     public void query(MemberCB unionCB) {
 *         unionCB.query().setXxx_Yyy...
 *     }
 * }
 * </pre>
 * @author jflute
 * @param <UNION_CB> The type of condition-bean for union.
 */
public interface UnionQuery<UNION_CB extends ConditionBean> {
    
    /**
     * Set up your query condition for union. <br />
     * Don't call the method 'setupSelect_Xxx()' and 'addOrderBy_Xxx...()'
     * and they are ignored if you call.
     * @param unionCB The condition-bean for union. (NotNull)
     */
    public void query(UNION_CB unionCB);
}
