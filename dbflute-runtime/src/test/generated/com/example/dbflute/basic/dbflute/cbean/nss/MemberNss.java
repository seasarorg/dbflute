package com.example.dbflute.basic.dbflute.cbean.nss;

import org.dbflute.cbean.ConditionQuery;

import com.example.dbflute.basic.dbflute.cbean.cq.MemberCQ;

/**
 * The nest select set-upper of MEMBER.
 * @author DBFlute(AutoGenerator)
 */
public class MemberNss {

    protected MemberCQ _query;
    public MemberNss(MemberCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================
    public MemberStatusNss withMemberStatus() {
        _query.doNss(new MemberCQ.NssCall() { public ConditionQuery qf() { return _query.queryMemberStatus(); }});
		return new MemberStatusNss(_query.queryMemberStatus());
    }
    public MemberAddressNss withMemberAddressAsValid(final java.util.Date targetDate) {
        _query.doNss(new MemberCQ.NssCall() { public ConditionQuery qf() { return _query.queryMemberAddressAsValid(targetDate); }});
		return new MemberAddressNss(_query.queryMemberAddressAsValid(targetDate));
    }

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
    public MemberSecurityNss withMemberSecurityAsOne() {
        _query.doNss(new MemberCQ.NssCall() { public ConditionQuery qf() { return _query.queryMemberSecurityAsOne(); }});
		return new MemberSecurityNss(_query.queryMemberSecurityAsOne());
    }
    public MemberWithdrawalNss withMemberWithdrawalAsOne() {
        _query.doNss(new MemberCQ.NssCall() { public ConditionQuery qf() { return _query.queryMemberWithdrawalAsOne(); }});
		return new MemberWithdrawalNss(_query.queryMemberWithdrawalAsOne());
    }
}
