package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of MEMBER_ADDRESS.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberAddressCQ extends AbstractBsMemberAddressCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected MemberAddressCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMemberAddressCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from MEMBER_ADDRESS) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public MemberAddressCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new MemberAddressCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join MEMBER_ADDRESS on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public MemberAddressCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        MemberAddressCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _memberAddressId;
    public ConditionValue getMemberAddressId() {
        if (_memberAddressId == null) { _memberAddressId = new ConditionValue(); }
        return _memberAddressId;
    }
    protected ConditionValue getCValueMemberAddressId() { return getMemberAddressId(); }

    public BsMemberAddressCQ addOrderBy_MemberAddressId_Asc() { regOBA("MEMBER_ADDRESS_ID"); return this; }
    public BsMemberAddressCQ addOrderBy_MemberAddressId_Desc() { regOBD("MEMBER_ADDRESS_ID"); return this; }

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
      
    protected Map<String, MemberCQ> _memberId_InScopeSubQuery_MemberAsOneMap;
    public Map<String, MemberCQ> getMemberId_InScopeSubQuery_MemberAsOne() { return _memberId_InScopeSubQuery_MemberAsOneMap; }
    public String keepMemberId_InScopeSubQuery_MemberAsOne(MemberCQ subQuery) {
        if (_memberId_InScopeSubQuery_MemberAsOneMap == null) { _memberId_InScopeSubQuery_MemberAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_InScopeSubQuery_MemberAsOneMap.size() + 1);
        _memberId_InScopeSubQuery_MemberAsOneMap.put(key, subQuery); return "memberId_InScopeSubQuery_MemberAsOne." + key;
    }

    protected Map<String, MemberCQ> _memberId_NotInScopeSubQuery_MemberAsOneMap;
    public Map<String, MemberCQ> getMemberId_NotInScopeSubQuery_MemberAsOne() { return _memberId_NotInScopeSubQuery_MemberAsOneMap; }
    public String keepMemberId_NotInScopeSubQuery_MemberAsOne(MemberCQ subQuery) {
        if (_memberId_NotInScopeSubQuery_MemberAsOneMap == null) { _memberId_NotInScopeSubQuery_MemberAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotInScopeSubQuery_MemberAsOneMap.size() + 1);
        _memberId_NotInScopeSubQuery_MemberAsOneMap.put(key, subQuery); return "memberId_NotInScopeSubQuery_MemberAsOne." + key;
    }

    protected Map<String, MemberCQ> _memberId_ExistsSubQuery_MemberAsOneMap;
    public Map<String, MemberCQ> getMemberId_ExistsSubQuery_MemberAsOne() { return _memberId_ExistsSubQuery_MemberAsOneMap; }
    public String keepMemberId_ExistsSubQuery_MemberAsOne(MemberCQ subQuery) {
        if (_memberId_ExistsSubQuery_MemberAsOneMap == null) { _memberId_ExistsSubQuery_MemberAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_ExistsSubQuery_MemberAsOneMap.size() + 1);
        _memberId_ExistsSubQuery_MemberAsOneMap.put(key, subQuery); return "memberId_ExistsSubQuery_MemberAsOne." + key;
    }

    protected Map<String, MemberCQ> _memberId_NotExistsSubQuery_MemberAsOneMap;
    public Map<String, MemberCQ> getMemberId_NotExistsSubQuery_MemberAsOne() { return _memberId_NotExistsSubQuery_MemberAsOneMap; }
    public String keepMemberId_NotExistsSubQuery_MemberAsOne(MemberCQ subQuery) {
        if (_memberId_NotExistsSubQuery_MemberAsOneMap == null) { _memberId_NotExistsSubQuery_MemberAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotExistsSubQuery_MemberAsOneMap.size() + 1);
        _memberId_NotExistsSubQuery_MemberAsOneMap.put(key, subQuery); return "memberId_NotExistsSubQuery_MemberAsOne." + key;
    }

    public BsMemberAddressCQ addOrderBy_MemberId_Asc() { regOBA("MEMBER_ID"); return this; }
    public BsMemberAddressCQ addOrderBy_MemberId_Desc() { regOBD("MEMBER_ID"); return this; }

    protected ConditionValue _validBeginDate;
    public ConditionValue getValidBeginDate() {
        if (_validBeginDate == null) { _validBeginDate = new ConditionValue(); }
        return _validBeginDate;
    }
    protected ConditionValue getCValueValidBeginDate() { return getValidBeginDate(); }

    public BsMemberAddressCQ addOrderBy_ValidBeginDate_Asc() { regOBA("VALID_BEGIN_DATE"); return this; }
    public BsMemberAddressCQ addOrderBy_ValidBeginDate_Desc() { regOBD("VALID_BEGIN_DATE"); return this; }

    protected ConditionValue _validEndDate;
    public ConditionValue getValidEndDate() {
        if (_validEndDate == null) { _validEndDate = new ConditionValue(); }
        return _validEndDate;
    }
    protected ConditionValue getCValueValidEndDate() { return getValidEndDate(); }

    public BsMemberAddressCQ addOrderBy_ValidEndDate_Asc() { regOBA("VALID_END_DATE"); return this; }
    public BsMemberAddressCQ addOrderBy_ValidEndDate_Desc() { regOBD("VALID_END_DATE"); return this; }

    protected ConditionValue _address;
    public ConditionValue getAddress() {
        if (_address == null) { _address = new ConditionValue(); }
        return _address;
    }
    protected ConditionValue getCValueAddress() { return getAddress(); }

    public BsMemberAddressCQ addOrderBy_Address_Asc() { regOBA("ADDRESS"); return this; }
    public BsMemberAddressCQ addOrderBy_Address_Desc() { regOBD("ADDRESS"); return this; }

    protected ConditionValue _registerDatetime;
    public ConditionValue getRegisterDatetime() {
        if (_registerDatetime == null) { _registerDatetime = new ConditionValue(); }
        return _registerDatetime;
    }
    protected ConditionValue getCValueRegisterDatetime() { return getRegisterDatetime(); }

    public BsMemberAddressCQ addOrderBy_RegisterDatetime_Asc() { regOBA("REGISTER_DATETIME"); return this; }
    public BsMemberAddressCQ addOrderBy_RegisterDatetime_Desc() { regOBD("REGISTER_DATETIME"); return this; }

    protected ConditionValue _registerProcess;
    public ConditionValue getRegisterProcess() {
        if (_registerProcess == null) { _registerProcess = new ConditionValue(); }
        return _registerProcess;
    }
    protected ConditionValue getCValueRegisterProcess() { return getRegisterProcess(); }

    public BsMemberAddressCQ addOrderBy_RegisterProcess_Asc() { regOBA("REGISTER_PROCESS"); return this; }
    public BsMemberAddressCQ addOrderBy_RegisterProcess_Desc() { regOBD("REGISTER_PROCESS"); return this; }

    protected ConditionValue _registerUser;
    public ConditionValue getRegisterUser() {
        if (_registerUser == null) { _registerUser = new ConditionValue(); }
        return _registerUser;
    }
    protected ConditionValue getCValueRegisterUser() { return getRegisterUser(); }

    public BsMemberAddressCQ addOrderBy_RegisterUser_Asc() { regOBA("REGISTER_USER"); return this; }
    public BsMemberAddressCQ addOrderBy_RegisterUser_Desc() { regOBD("REGISTER_USER"); return this; }

    protected ConditionValue _updateDatetime;
    public ConditionValue getUpdateDatetime() {
        if (_updateDatetime == null) { _updateDatetime = new ConditionValue(); }
        return _updateDatetime;
    }
    protected ConditionValue getCValueUpdateDatetime() { return getUpdateDatetime(); }

    public BsMemberAddressCQ addOrderBy_UpdateDatetime_Asc() { regOBA("UPDATE_DATETIME"); return this; }
    public BsMemberAddressCQ addOrderBy_UpdateDatetime_Desc() { regOBD("UPDATE_DATETIME"); return this; }

    protected ConditionValue _updateProcess;
    public ConditionValue getUpdateProcess() {
        if (_updateProcess == null) { _updateProcess = new ConditionValue(); }
        return _updateProcess;
    }
    protected ConditionValue getCValueUpdateProcess() { return getUpdateProcess(); }

    public BsMemberAddressCQ addOrderBy_UpdateProcess_Asc() { regOBA("UPDATE_PROCESS"); return this; }
    public BsMemberAddressCQ addOrderBy_UpdateProcess_Desc() { regOBD("UPDATE_PROCESS"); return this; }

    protected ConditionValue _updateUser;
    public ConditionValue getUpdateUser() {
        if (_updateUser == null) { _updateUser = new ConditionValue(); }
        return _updateUser;
    }
    protected ConditionValue getCValueUpdateUser() { return getUpdateUser(); }

    public BsMemberAddressCQ addOrderBy_UpdateUser_Asc() { regOBA("UPDATE_USER"); return this; }
    public BsMemberAddressCQ addOrderBy_UpdateUser_Desc() { regOBD("UPDATE_USER"); return this; }

    protected ConditionValue _versionNo;
    public ConditionValue getVersionNo() {
        if (_versionNo == null) { _versionNo = new ConditionValue(); }
        return _versionNo;
    }
    protected ConditionValue getCValueVersionNo() { return getVersionNo(); }

    public BsMemberAddressCQ addOrderBy_VersionNo_Asc() { regOBA("VERSION_NO"); return this; }
    public BsMemberAddressCQ addOrderBy_VersionNo_Desc() { regOBD("VERSION_NO"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsMemberAddressCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsMemberAddressCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        MemberAddressCQ baseQuery = (MemberAddressCQ)baseQueryAsSuper;
        MemberAddressCQ unionQuery = (MemberAddressCQ)unionQueryAsSuper;
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
        String nrp = resolveNextRelationPath("MEMBER_ADDRESS", "member");
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
    protected Map<String, MemberAddressCQ> _scalarSubQueryMap;
    public Map<String, MemberAddressCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(MemberAddressCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberAddressCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberAddressCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
