package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of MEMBER_SECURITY.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberSecurityCQ extends AbstractBsMemberSecurityCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected MemberSecurityCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMemberSecurityCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from MEMBER_SECURITY) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public MemberSecurityCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new MemberSecurityCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join MEMBER_SECURITY on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public MemberSecurityCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        MemberSecurityCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
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
      
    public BsMemberSecurityCQ addOrderBy_MemberId_Asc() { regOBA("MEMBER_ID"); return this; }
    public BsMemberSecurityCQ addOrderBy_MemberId_Desc() { regOBD("MEMBER_ID"); return this; }

    protected ConditionValue _loginPassword;
    public ConditionValue getLoginPassword() {
        if (_loginPassword == null) { _loginPassword = new ConditionValue(); }
        return _loginPassword;
    }
    protected ConditionValue getCValueLoginPassword() { return getLoginPassword(); }

    public BsMemberSecurityCQ addOrderBy_LoginPassword_Asc() { regOBA("LOGIN_PASSWORD"); return this; }
    public BsMemberSecurityCQ addOrderBy_LoginPassword_Desc() { regOBD("LOGIN_PASSWORD"); return this; }

    protected ConditionValue _reminderQuestion;
    public ConditionValue getReminderQuestion() {
        if (_reminderQuestion == null) { _reminderQuestion = new ConditionValue(); }
        return _reminderQuestion;
    }
    protected ConditionValue getCValueReminderQuestion() { return getReminderQuestion(); }

    public BsMemberSecurityCQ addOrderBy_ReminderQuestion_Asc() { regOBA("REMINDER_QUESTION"); return this; }
    public BsMemberSecurityCQ addOrderBy_ReminderQuestion_Desc() { regOBD("REMINDER_QUESTION"); return this; }

    protected ConditionValue _reminderAnswer;
    public ConditionValue getReminderAnswer() {
        if (_reminderAnswer == null) { _reminderAnswer = new ConditionValue(); }
        return _reminderAnswer;
    }
    protected ConditionValue getCValueReminderAnswer() { return getReminderAnswer(); }

    public BsMemberSecurityCQ addOrderBy_ReminderAnswer_Asc() { regOBA("REMINDER_ANSWER"); return this; }
    public BsMemberSecurityCQ addOrderBy_ReminderAnswer_Desc() { regOBD("REMINDER_ANSWER"); return this; }

    protected ConditionValue _registerDatetime;
    public ConditionValue getRegisterDatetime() {
        if (_registerDatetime == null) { _registerDatetime = new ConditionValue(); }
        return _registerDatetime;
    }
    protected ConditionValue getCValueRegisterDatetime() { return getRegisterDatetime(); }

    public BsMemberSecurityCQ addOrderBy_RegisterDatetime_Asc() { regOBA("REGISTER_DATETIME"); return this; }
    public BsMemberSecurityCQ addOrderBy_RegisterDatetime_Desc() { regOBD("REGISTER_DATETIME"); return this; }

    protected ConditionValue _registerProcess;
    public ConditionValue getRegisterProcess() {
        if (_registerProcess == null) { _registerProcess = new ConditionValue(); }
        return _registerProcess;
    }
    protected ConditionValue getCValueRegisterProcess() { return getRegisterProcess(); }

    public BsMemberSecurityCQ addOrderBy_RegisterProcess_Asc() { regOBA("REGISTER_PROCESS"); return this; }
    public BsMemberSecurityCQ addOrderBy_RegisterProcess_Desc() { regOBD("REGISTER_PROCESS"); return this; }

    protected ConditionValue _registerUser;
    public ConditionValue getRegisterUser() {
        if (_registerUser == null) { _registerUser = new ConditionValue(); }
        return _registerUser;
    }
    protected ConditionValue getCValueRegisterUser() { return getRegisterUser(); }

    public BsMemberSecurityCQ addOrderBy_RegisterUser_Asc() { regOBA("REGISTER_USER"); return this; }
    public BsMemberSecurityCQ addOrderBy_RegisterUser_Desc() { regOBD("REGISTER_USER"); return this; }

    protected ConditionValue _updateDatetime;
    public ConditionValue getUpdateDatetime() {
        if (_updateDatetime == null) { _updateDatetime = new ConditionValue(); }
        return _updateDatetime;
    }
    protected ConditionValue getCValueUpdateDatetime() { return getUpdateDatetime(); }

    public BsMemberSecurityCQ addOrderBy_UpdateDatetime_Asc() { regOBA("UPDATE_DATETIME"); return this; }
    public BsMemberSecurityCQ addOrderBy_UpdateDatetime_Desc() { regOBD("UPDATE_DATETIME"); return this; }

    protected ConditionValue _updateProcess;
    public ConditionValue getUpdateProcess() {
        if (_updateProcess == null) { _updateProcess = new ConditionValue(); }
        return _updateProcess;
    }
    protected ConditionValue getCValueUpdateProcess() { return getUpdateProcess(); }

    public BsMemberSecurityCQ addOrderBy_UpdateProcess_Asc() { regOBA("UPDATE_PROCESS"); return this; }
    public BsMemberSecurityCQ addOrderBy_UpdateProcess_Desc() { regOBD("UPDATE_PROCESS"); return this; }

    protected ConditionValue _updateUser;
    public ConditionValue getUpdateUser() {
        if (_updateUser == null) { _updateUser = new ConditionValue(); }
        return _updateUser;
    }
    protected ConditionValue getCValueUpdateUser() { return getUpdateUser(); }

    public BsMemberSecurityCQ addOrderBy_UpdateUser_Asc() { regOBA("UPDATE_USER"); return this; }
    public BsMemberSecurityCQ addOrderBy_UpdateUser_Desc() { regOBD("UPDATE_USER"); return this; }

    protected ConditionValue _versionNo;
    public ConditionValue getVersionNo() {
        if (_versionNo == null) { _versionNo = new ConditionValue(); }
        return _versionNo;
    }
    protected ConditionValue getCValueVersionNo() { return getVersionNo(); }

    public BsMemberSecurityCQ addOrderBy_VersionNo_Asc() { regOBA("VERSION_NO"); return this; }
    public BsMemberSecurityCQ addOrderBy_VersionNo_Desc() { regOBD("VERSION_NO"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsMemberSecurityCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsMemberSecurityCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        MemberSecurityCQ baseQuery = (MemberSecurityCQ)baseQueryAsSuper;
        MemberSecurityCQ unionQuery = (MemberSecurityCQ)unionQueryAsSuper;
        if (baseQuery.hasConditionQueryMember()) {
            unionQuery.queryMember().reflectRelationOnUnionQuery(baseQuery.queryMember(), unionQuery.queryMember());
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
        String nrp = resolveNextRelationPath("MEMBER_SECURITY", "member");
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

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, MemberSecurityCQ> _scalarSubQueryMap;
    public Map<String, MemberSecurityCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(MemberSecurityCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberSecurityCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberSecurityCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
