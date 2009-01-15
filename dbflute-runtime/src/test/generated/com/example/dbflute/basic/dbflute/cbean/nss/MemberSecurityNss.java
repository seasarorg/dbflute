package com.example.dbflute.basic.dbflute.cbean.nss;

import org.seasar.dbflute.cbean.ConditionQuery;
import com.example.dbflute.basic.dbflute.cbean.cq.MemberSecurityCQ;

/**
 * The nest select set-upper of MEMBER_SECURITY.
 * @author DBFlute(AutoGenerator)
 */
public class MemberSecurityNss {

    protected MemberSecurityCQ _query;
    public MemberSecurityNss(MemberSecurityCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================
    public MemberNss withMember() {
        _query.doNss(new MemberSecurityCQ.NssCall() { public ConditionQuery qf() { return _query.queryMember(); }});
		return new MemberNss(_query.queryMember());
    }

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
}
