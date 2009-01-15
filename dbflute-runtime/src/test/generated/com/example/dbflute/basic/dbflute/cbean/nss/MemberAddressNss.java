package com.example.dbflute.basic.dbflute.cbean.nss;

import org.seasar.dbflute.cbean.ConditionQuery;
import com.example.dbflute.basic.dbflute.cbean.cq.MemberAddressCQ;

/**
 * The nest select set-upper of MEMBER_ADDRESS.
 * @author DBFlute(AutoGenerator)
 */
public class MemberAddressNss {

    protected MemberAddressCQ _query;
    public MemberAddressNss(MemberAddressCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================
    public MemberNss withMember() {
        _query.doNss(new MemberAddressCQ.NssCall() { public ConditionQuery qf() { return _query.queryMember(); }});
		return new MemberNss(_query.queryMember());
    }

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
}
