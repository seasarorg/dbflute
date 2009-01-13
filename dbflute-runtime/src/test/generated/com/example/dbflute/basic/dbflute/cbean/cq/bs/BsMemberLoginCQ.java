package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.dbflute.cbean.*;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of MEMBER_LOGIN.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberLoginCQ extends AbstractBsMemberLoginCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected MemberLoginCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMemberLoginCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from MEMBER_LOGIN) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public MemberLoginCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new MemberLoginCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join MEMBER_LOGIN on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public MemberLoginCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        MemberLoginCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _memberLoginId;
    public ConditionValue getMemberLoginId() {
        if (_memberLoginId == null) { _memberLoginId = new ConditionValue(); }
        return _memberLoginId;
    }
    protected ConditionValue getCValueMemberLoginId() { return getMemberLoginId(); }

    public BsMemberLoginCQ addOrderBy_MemberLoginId_Asc() { regOBA("MEMBER_LOGIN_ID"); return this; }
    public BsMemberLoginCQ addOrderBy_MemberLoginId_Desc() { regOBD("MEMBER_LOGIN_ID"); return this; }

    protected ConditionValue _memberId;
    public ConditionValue getMemberId() {
        if (_memberId == null) { _memberId = new ConditionValue(); }
        return _memberId;
    }
    protected ConditionValue getCValueMemberId() { return getMemberId(); }

    protected Map<String, MemberCQ> _memberId_InScopeSubQuery_MemberMap;
    public Map<String, MemberCQ> getMemberId_InScopeSubQuery_Member() { return _memberId_InScopeSubQuery_MemberMap; }
    public String keepMemberId_InScopeSubQuery_Member(MemberCQ subQuery) {
        if (_memberId_InScopeSubQuery_MemberMap == null) { _memberId_InScopeSubQuery_MemberMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_InScopeSubQuery_MemberMap.size() + 1);
        _memberId_InScopeSubQuery_MemberMap.put(key, subQuery); return "memberId_InScopeSubQuery_Member." + key;
    }
      
    public BsMemberLoginCQ addOrderBy_MemberId_Asc() { regOBA("MEMBER_ID"); return this; }
    public BsMemberLoginCQ addOrderBy_MemberId_Desc() { regOBD("MEMBER_ID"); return this; }

    protected ConditionValue _loginDatetime;
    public ConditionValue getLoginDatetime() {
        if (_loginDatetime == null) { _loginDatetime = new ConditionValue(); }
        return _loginDatetime;
    }
    protected ConditionValue getCValueLoginDatetime() { return getLoginDatetime(); }

    public BsMemberLoginCQ addOrderBy_LoginDatetime_Asc() { regOBA("LOGIN_DATETIME"); return this; }
    public BsMemberLoginCQ addOrderBy_LoginDatetime_Desc() { regOBD("LOGIN_DATETIME"); return this; }

    protected ConditionValue _loginMobileFlg;
    public ConditionValue getLoginMobileFlg() {
        if (_loginMobileFlg == null) { _loginMobileFlg = new ConditionValue(); }
        return _loginMobileFlg;
    }
    protected ConditionValue getCValueLoginMobileFlg() { return getLoginMobileFlg(); }

    public BsMemberLoginCQ addOrderBy_LoginMobileFlg_Asc() { regOBA("LOGIN_MOBILE_FLG"); return this; }
    public BsMemberLoginCQ addOrderBy_LoginMobileFlg_Desc() { regOBD("LOGIN_MOBILE_FLG"); return this; }

    protected ConditionValue _loginMemberStatusCode;
    public ConditionValue getLoginMemberStatusCode() {
        if (_loginMemberStatusCode == null) { _loginMemberStatusCode = new ConditionValue(); }
        return _loginMemberStatusCode;
    }
    protected ConditionValue getCValueLoginMemberStatusCode() { return getLoginMemberStatusCode(); }

    protected Map<String, MemberStatusCQ> _loginMemberStatusCode_InScopeSubQuery_MemberStatusMap;
    public Map<String, MemberStatusCQ> getLoginMemberStatusCode_InScopeSubQuery_MemberStatus() { return _loginMemberStatusCode_InScopeSubQuery_MemberStatusMap; }
    public String keepLoginMemberStatusCode_InScopeSubQuery_MemberStatus(MemberStatusCQ subQuery) {
        if (_loginMemberStatusCode_InScopeSubQuery_MemberStatusMap == null) { _loginMemberStatusCode_InScopeSubQuery_MemberStatusMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_loginMemberStatusCode_InScopeSubQuery_MemberStatusMap.size() + 1);
        _loginMemberStatusCode_InScopeSubQuery_MemberStatusMap.put(key, subQuery); return "loginMemberStatusCode_InScopeSubQuery_MemberStatus." + key;
    }

    public BsMemberLoginCQ addOrderBy_LoginMemberStatusCode_Asc() { regOBA("LOGIN_MEMBER_STATUS_CODE"); return this; }
    public BsMemberLoginCQ addOrderBy_LoginMemberStatusCode_Desc() { regOBD("LOGIN_MEMBER_STATUS_CODE"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsMemberLoginCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsMemberLoginCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        MemberLoginCQ baseQuery = (MemberLoginCQ)baseQueryAsSuper;
        MemberLoginCQ unionQuery = (MemberLoginCQ)unionQueryAsSuper;
        if (baseQuery.hasConditionQueryMember()) {
            unionQuery.queryMember().reflectRelationOnUnionQuery(baseQuery.queryMember(), unionQuery.queryMember());
        }
        if (baseQuery.hasConditionQueryMemberStatus()) {
            unionQuery.queryMemberStatus().reflectRelationOnUnionQuery(baseQuery.queryMemberStatus(), unionQuery.queryMemberStatus());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    public MemberCQ queryMember() {
        return getConditionQueryMember();
    }
    protected MemberCQ _conditionQueryMember;
    public MemberCQ getConditionQueryMember() {
        if (_conditionQueryMember == null) {
            _conditionQueryMember = xcreateQueryMember();
            xsetupOuterJoinMember();
        }
        return _conditionQueryMember;
    }
    protected MemberCQ xcreateQueryMember() {
        String nrp = resolveNextRelationPath("MEMBER_LOGIN", "member");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        MemberCQ cq = new MemberCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("member"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinMember() {
        MemberCQ cq = getConditionQueryMember();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("MEMBER_ID"), cq.getRealColumnName("MEMBER_ID"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryMember() {
        return _conditionQueryMember != null;
    }

    public MemberStatusCQ queryMemberStatus() {
        return getConditionQueryMemberStatus();
    }
    protected MemberStatusCQ _conditionQueryMemberStatus;
    public MemberStatusCQ getConditionQueryMemberStatus() {
        if (_conditionQueryMemberStatus == null) {
            _conditionQueryMemberStatus = xcreateQueryMemberStatus();
            xsetupOuterJoinMemberStatus();
        }
        return _conditionQueryMemberStatus;
    }
    protected MemberStatusCQ xcreateQueryMemberStatus() {
        String nrp = resolveNextRelationPath("MEMBER_LOGIN", "memberStatus");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        MemberStatusCQ cq = new MemberStatusCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("memberStatus"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinMemberStatus() {
        MemberStatusCQ cq = getConditionQueryMemberStatus();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("LOGIN_MEMBER_STATUS_CODE"), cq.getRealColumnName("MEMBER_STATUS_CODE"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryMemberStatus() {
        return _conditionQueryMemberStatus != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, MemberLoginCQ> _scalarSubQueryMap;
    public Map<String, MemberLoginCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(MemberLoginCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberLoginCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberLoginCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
