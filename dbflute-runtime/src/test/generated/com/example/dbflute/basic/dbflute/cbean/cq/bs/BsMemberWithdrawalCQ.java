package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of MEMBER_WITHDRAWAL.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberWithdrawalCQ extends AbstractBsMemberWithdrawalCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected MemberWithdrawalCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMemberWithdrawalCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from MEMBER_WITHDRAWAL) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public MemberWithdrawalCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new MemberWithdrawalCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join MEMBER_WITHDRAWAL on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public MemberWithdrawalCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        MemberWithdrawalCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

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
      
    public BsMemberWithdrawalCQ addOrderBy_MemberId_Asc() { regOBA("MEMBER_ID"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_MemberId_Desc() { regOBD("MEMBER_ID"); return this; }

    protected ConditionValue _withdrawalReasonCode;
    public ConditionValue getWithdrawalReasonCode() {
        if (_withdrawalReasonCode == null) { _withdrawalReasonCode = new ConditionValue(); }
        return _withdrawalReasonCode;
    }
    protected ConditionValue getCValueWithdrawalReasonCode() { return getWithdrawalReasonCode(); }

    protected Map<String, WithdrawalReasonCQ> _withdrawalReasonCode_InScopeSubQuery_WithdrawalReasonMap;
    public Map<String, WithdrawalReasonCQ> getWithdrawalReasonCode_InScopeSubQuery_WithdrawalReason() { return _withdrawalReasonCode_InScopeSubQuery_WithdrawalReasonMap; }
    public String keepWithdrawalReasonCode_InScopeSubQuery_WithdrawalReason(WithdrawalReasonCQ subQuery) {
        if (_withdrawalReasonCode_InScopeSubQuery_WithdrawalReasonMap == null) { _withdrawalReasonCode_InScopeSubQuery_WithdrawalReasonMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_withdrawalReasonCode_InScopeSubQuery_WithdrawalReasonMap.size() + 1);
        _withdrawalReasonCode_InScopeSubQuery_WithdrawalReasonMap.put(key, subQuery); return "withdrawalReasonCode_InScopeSubQuery_WithdrawalReason." + key;
    }

    public BsMemberWithdrawalCQ addOrderBy_WithdrawalReasonCode_Asc() { regOBA("WITHDRAWAL_REASON_CODE"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_WithdrawalReasonCode_Desc() { regOBD("WITHDRAWAL_REASON_CODE"); return this; }

    protected ConditionValue _withdrawalReasonInputText;
    public ConditionValue getWithdrawalReasonInputText() {
        if (_withdrawalReasonInputText == null) { _withdrawalReasonInputText = new ConditionValue(); }
        return _withdrawalReasonInputText;
    }
    protected ConditionValue getCValueWithdrawalReasonInputText() { return getWithdrawalReasonInputText(); }

    public BsMemberWithdrawalCQ addOrderBy_WithdrawalReasonInputText_Asc() { regOBA("WITHDRAWAL_REASON_INPUT_TEXT"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_WithdrawalReasonInputText_Desc() { regOBD("WITHDRAWAL_REASON_INPUT_TEXT"); return this; }

    protected ConditionValue _withdrawalDatetime;
    public ConditionValue getWithdrawalDatetime() {
        if (_withdrawalDatetime == null) { _withdrawalDatetime = new ConditionValue(); }
        return _withdrawalDatetime;
    }
    protected ConditionValue getCValueWithdrawalDatetime() { return getWithdrawalDatetime(); }

    public BsMemberWithdrawalCQ addOrderBy_WithdrawalDatetime_Asc() { regOBA("WITHDRAWAL_DATETIME"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_WithdrawalDatetime_Desc() { regOBD("WITHDRAWAL_DATETIME"); return this; }

    protected ConditionValue _registerDatetime;
    public ConditionValue getRegisterDatetime() {
        if (_registerDatetime == null) { _registerDatetime = new ConditionValue(); }
        return _registerDatetime;
    }
    protected ConditionValue getCValueRegisterDatetime() { return getRegisterDatetime(); }

    public BsMemberWithdrawalCQ addOrderBy_RegisterDatetime_Asc() { regOBA("REGISTER_DATETIME"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_RegisterDatetime_Desc() { regOBD("REGISTER_DATETIME"); return this; }

    protected ConditionValue _registerProcess;
    public ConditionValue getRegisterProcess() {
        if (_registerProcess == null) { _registerProcess = new ConditionValue(); }
        return _registerProcess;
    }
    protected ConditionValue getCValueRegisterProcess() { return getRegisterProcess(); }

    public BsMemberWithdrawalCQ addOrderBy_RegisterProcess_Asc() { regOBA("REGISTER_PROCESS"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_RegisterProcess_Desc() { regOBD("REGISTER_PROCESS"); return this; }

    protected ConditionValue _registerUser;
    public ConditionValue getRegisterUser() {
        if (_registerUser == null) { _registerUser = new ConditionValue(); }
        return _registerUser;
    }
    protected ConditionValue getCValueRegisterUser() { return getRegisterUser(); }

    public BsMemberWithdrawalCQ addOrderBy_RegisterUser_Asc() { regOBA("REGISTER_USER"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_RegisterUser_Desc() { regOBD("REGISTER_USER"); return this; }

    protected ConditionValue _updateDatetime;
    public ConditionValue getUpdateDatetime() {
        if (_updateDatetime == null) { _updateDatetime = new ConditionValue(); }
        return _updateDatetime;
    }
    protected ConditionValue getCValueUpdateDatetime() { return getUpdateDatetime(); }

    public BsMemberWithdrawalCQ addOrderBy_UpdateDatetime_Asc() { regOBA("UPDATE_DATETIME"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_UpdateDatetime_Desc() { regOBD("UPDATE_DATETIME"); return this; }

    protected ConditionValue _updateProcess;
    public ConditionValue getUpdateProcess() {
        if (_updateProcess == null) { _updateProcess = new ConditionValue(); }
        return _updateProcess;
    }
    protected ConditionValue getCValueUpdateProcess() { return getUpdateProcess(); }

    public BsMemberWithdrawalCQ addOrderBy_UpdateProcess_Asc() { regOBA("UPDATE_PROCESS"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_UpdateProcess_Desc() { regOBD("UPDATE_PROCESS"); return this; }

    protected ConditionValue _updateUser;
    public ConditionValue getUpdateUser() {
        if (_updateUser == null) { _updateUser = new ConditionValue(); }
        return _updateUser;
    }
    protected ConditionValue getCValueUpdateUser() { return getUpdateUser(); }

    public BsMemberWithdrawalCQ addOrderBy_UpdateUser_Asc() { regOBA("UPDATE_USER"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_UpdateUser_Desc() { regOBD("UPDATE_USER"); return this; }

    protected ConditionValue _versionNo;
    public ConditionValue getVersionNo() {
        if (_versionNo == null) { _versionNo = new ConditionValue(); }
        return _versionNo;
    }
    protected ConditionValue getCValueVersionNo() { return getVersionNo(); }

    public BsMemberWithdrawalCQ addOrderBy_VersionNo_Asc() { regOBA("VERSION_NO"); return this; }
    public BsMemberWithdrawalCQ addOrderBy_VersionNo_Desc() { regOBD("VERSION_NO"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsMemberWithdrawalCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsMemberWithdrawalCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        MemberWithdrawalCQ baseQuery = (MemberWithdrawalCQ)baseQueryAsSuper;
        MemberWithdrawalCQ unionQuery = (MemberWithdrawalCQ)unionQueryAsSuper;
        if (baseQuery.hasConditionQueryMember()) {
            unionQuery.queryMember().reflectRelationOnUnionQuery(baseQuery.queryMember(), unionQuery.queryMember());
        }
        if (baseQuery.hasConditionQueryWithdrawalReason()) {
            unionQuery.queryWithdrawalReason().reflectRelationOnUnionQuery(baseQuery.queryWithdrawalReason(), unionQuery.queryWithdrawalReason());
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
        String nrp = resolveNextRelationPath("MEMBER_WITHDRAWAL", "member");
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

    public WithdrawalReasonCQ queryWithdrawalReason() {
        return getConditionQueryWithdrawalReason();
    }
    protected WithdrawalReasonCQ _conditionQueryWithdrawalReason;
    public WithdrawalReasonCQ getConditionQueryWithdrawalReason() {
        if (_conditionQueryWithdrawalReason == null) {
            _conditionQueryWithdrawalReason = xcreateQueryWithdrawalReason();
            xsetupOuterJoinWithdrawalReason();
        }
        return _conditionQueryWithdrawalReason;
    }
    protected WithdrawalReasonCQ xcreateQueryWithdrawalReason() {
        String nrp = resolveNextRelationPath("MEMBER_WITHDRAWAL", "withdrawalReason");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        WithdrawalReasonCQ cq = new WithdrawalReasonCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("withdrawalReason"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinWithdrawalReason() {
        WithdrawalReasonCQ cq = getConditionQueryWithdrawalReason();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("WITHDRAWAL_REASON_CODE"), cq.getRealColumnName("WITHDRAWAL_REASON_CODE"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryWithdrawalReason() {
        return _conditionQueryWithdrawalReason != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, MemberWithdrawalCQ> _scalarSubQueryMap;
    public Map<String, MemberWithdrawalCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(MemberWithdrawalCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberWithdrawalCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberWithdrawalCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
