/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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

import org.seasar.dbflute.cbean.sqlclause.subquery.DerivedReferrer;

/**
 * @author jflute
 */
public class HpDerivingSubQueryInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _aliasName;
    protected String _derivingSubQuery;
    protected DerivedReferrer _derivedReferrer;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpDerivingSubQueryInfo(String aliasName, String derivingSubQuery, DerivedReferrer derivedReferrer) {
        this._aliasName = aliasName;
        this._derivingSubQuery = derivingSubQuery;
        this._derivedReferrer = derivedReferrer;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAliasName() {
        return _aliasName;
    }

    public String getDerivingSubQuery() {
        return _derivingSubQuery;
    }

    public DerivedReferrer getDerivedReferrer() {
        return _derivedReferrer;
    }
}
