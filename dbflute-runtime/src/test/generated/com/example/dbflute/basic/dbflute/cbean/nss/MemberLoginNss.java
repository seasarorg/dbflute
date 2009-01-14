package com.example.dbflute.basic.dbflute.cbean.nss;

import org.seasar.dbflute.cbean.ConditionQuery;

import com.example.dbflute.basic.dbflute.cbean.cq.MemberLoginCQ;

/**
 * The nest select set-upper of MEMBER_LOGIN.
 * @author DBFlute(AutoGenerator)
 */
public class MemberLoginNss {

    protected MemberLoginCQ _query;
    public MemberLoginNss(MemberLoginCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================
    public MemberNss withMember() {
        _query.doNss(new MemberLoginCQ.NssCall() { public ConditionQuery qf() { return _query.queryMember(); }});
		return new MemberNss(_query.queryMember());
    }
    public MemberStatusNss withMemberStatus() {
        _query.doNss(new MemberLoginCQ.NssCall() { public ConditionQuery qf() { return _query.queryMemberStatus(); }});
		return new MemberStatusNss(_query.queryMemberStatus());
    }

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
}
