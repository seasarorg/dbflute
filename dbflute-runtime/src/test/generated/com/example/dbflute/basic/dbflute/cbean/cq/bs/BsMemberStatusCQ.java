package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.dbflute.cbean.*;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of MEMBER_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberStatusCQ extends AbstractBsMemberStatusCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected MemberStatusCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMemberStatusCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from MEMBER_STATUS) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public MemberStatusCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new MemberStatusCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join MEMBER_STATUS on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public MemberStatusCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        MemberStatusCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _memberStatusCode;
    public ConditionValue getMemberStatusCode() {
        if (_memberStatusCode == null) { _memberStatusCode = new ConditionValue(); }
        return _memberStatusCode;
    }
    protected ConditionValue getCValueMemberStatusCode() { return getMemberStatusCode(); }

    protected Map<String, MemberCQ> _memberStatusCode_InScopeSubQuery_MemberListMap;
    public Map<String, MemberCQ> getMemberStatusCode_InScopeSubQuery_MemberList() { return _memberStatusCode_InScopeSubQuery_MemberListMap; }
    public String keepMemberStatusCode_InScopeSubQuery_MemberList(MemberCQ subQuery) {
        if (_memberStatusCode_InScopeSubQuery_MemberListMap == null) { _memberStatusCode_InScopeSubQuery_MemberListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_InScopeSubQuery_MemberListMap.size() + 1);
        _memberStatusCode_InScopeSubQuery_MemberListMap.put(key, subQuery); return "memberStatusCode_InScopeSubQuery_MemberList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberStatusCode_InScopeSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberStatusCode_InScopeSubQuery_MemberLoginList() { return _memberStatusCode_InScopeSubQuery_MemberLoginListMap; }
    public String keepMemberStatusCode_InScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberStatusCode_InScopeSubQuery_MemberLoginListMap == null) { _memberStatusCode_InScopeSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_InScopeSubQuery_MemberLoginListMap.size() + 1);
        _memberStatusCode_InScopeSubQuery_MemberLoginListMap.put(key, subQuery); return "memberStatusCode_InScopeSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberCQ> _memberStatusCode_NotInScopeSubQuery_MemberListMap;
    public Map<String, MemberCQ> getMemberStatusCode_NotInScopeSubQuery_MemberList() { return _memberStatusCode_NotInScopeSubQuery_MemberListMap; }
    public String keepMemberStatusCode_NotInScopeSubQuery_MemberList(MemberCQ subQuery) {
        if (_memberStatusCode_NotInScopeSubQuery_MemberListMap == null) { _memberStatusCode_NotInScopeSubQuery_MemberListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_NotInScopeSubQuery_MemberListMap.size() + 1);
        _memberStatusCode_NotInScopeSubQuery_MemberListMap.put(key, subQuery); return "memberStatusCode_NotInScopeSubQuery_MemberList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberStatusCode_NotInScopeSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberStatusCode_NotInScopeSubQuery_MemberLoginList() { return _memberStatusCode_NotInScopeSubQuery_MemberLoginListMap; }
    public String keepMemberStatusCode_NotInScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberStatusCode_NotInScopeSubQuery_MemberLoginListMap == null) { _memberStatusCode_NotInScopeSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_NotInScopeSubQuery_MemberLoginListMap.size() + 1);
        _memberStatusCode_NotInScopeSubQuery_MemberLoginListMap.put(key, subQuery); return "memberStatusCode_NotInScopeSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberCQ> _memberStatusCode_ExistsSubQuery_MemberListMap;
    public Map<String, MemberCQ> getMemberStatusCode_ExistsSubQuery_MemberList() { return _memberStatusCode_ExistsSubQuery_MemberListMap; }
    public String keepMemberStatusCode_ExistsSubQuery_MemberList(MemberCQ subQuery) {
        if (_memberStatusCode_ExistsSubQuery_MemberListMap == null) { _memberStatusCode_ExistsSubQuery_MemberListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_ExistsSubQuery_MemberListMap.size() + 1);
        _memberStatusCode_ExistsSubQuery_MemberListMap.put(key, subQuery); return "memberStatusCode_ExistsSubQuery_MemberList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberStatusCode_ExistsSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberStatusCode_ExistsSubQuery_MemberLoginList() { return _memberStatusCode_ExistsSubQuery_MemberLoginListMap; }
    public String keepMemberStatusCode_ExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberStatusCode_ExistsSubQuery_MemberLoginListMap == null) { _memberStatusCode_ExistsSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_ExistsSubQuery_MemberLoginListMap.size() + 1);
        _memberStatusCode_ExistsSubQuery_MemberLoginListMap.put(key, subQuery); return "memberStatusCode_ExistsSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberCQ> _memberStatusCode_NotExistsSubQuery_MemberListMap;
    public Map<String, MemberCQ> getMemberStatusCode_NotExistsSubQuery_MemberList() { return _memberStatusCode_NotExistsSubQuery_MemberListMap; }
    public String keepMemberStatusCode_NotExistsSubQuery_MemberList(MemberCQ subQuery) {
        if (_memberStatusCode_NotExistsSubQuery_MemberListMap == null) { _memberStatusCode_NotExistsSubQuery_MemberListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_NotExistsSubQuery_MemberListMap.size() + 1);
        _memberStatusCode_NotExistsSubQuery_MemberListMap.put(key, subQuery); return "memberStatusCode_NotExistsSubQuery_MemberList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberStatusCode_NotExistsSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberStatusCode_NotExistsSubQuery_MemberLoginList() { return _memberStatusCode_NotExistsSubQuery_MemberLoginListMap; }
    public String keepMemberStatusCode_NotExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberStatusCode_NotExistsSubQuery_MemberLoginListMap == null) { _memberStatusCode_NotExistsSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_NotExistsSubQuery_MemberLoginListMap.size() + 1);
        _memberStatusCode_NotExistsSubQuery_MemberLoginListMap.put(key, subQuery); return "memberStatusCode_NotExistsSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberCQ> _memberStatusCode_SpecifyDerivedReferrer_MemberListMap;
    public Map<String, MemberCQ> getMemberStatusCode_SpecifyDerivedReferrer_MemberList() { return _memberStatusCode_SpecifyDerivedReferrer_MemberListMap; }
    public String keepMemberStatusCode_SpecifyDerivedReferrer_MemberList(MemberCQ subQuery) {
        if (_memberStatusCode_SpecifyDerivedReferrer_MemberListMap == null) { _memberStatusCode_SpecifyDerivedReferrer_MemberListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_SpecifyDerivedReferrer_MemberListMap.size() + 1);
        _memberStatusCode_SpecifyDerivedReferrer_MemberListMap.put(key, subQuery); return "memberStatusCode_SpecifyDerivedReferrer_MemberList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberStatusCode_SpecifyDerivedReferrer_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberStatusCode_SpecifyDerivedReferrer_MemberLoginList() { return _memberStatusCode_SpecifyDerivedReferrer_MemberLoginListMap; }
    public String keepMemberStatusCode_SpecifyDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberStatusCode_SpecifyDerivedReferrer_MemberLoginListMap == null) { _memberStatusCode_SpecifyDerivedReferrer_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_SpecifyDerivedReferrer_MemberLoginListMap.size() + 1);
        _memberStatusCode_SpecifyDerivedReferrer_MemberLoginListMap.put(key, subQuery); return "memberStatusCode_SpecifyDerivedReferrer_MemberLoginList." + key;
    }

    protected Map<String, MemberCQ> _memberStatusCode_QueryDerivedReferrer_MemberListMap;
    public Map<String, MemberCQ> getMemberStatusCode_QueryDerivedReferrer_MemberList() { return _memberStatusCode_QueryDerivedReferrer_MemberListMap; }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberList(MemberCQ subQuery) {
        if (_memberStatusCode_QueryDerivedReferrer_MemberListMap == null) { _memberStatusCode_QueryDerivedReferrer_MemberListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_QueryDerivedReferrer_MemberListMap.size() + 1);
        _memberStatusCode_QueryDerivedReferrer_MemberListMap.put(key, subQuery); return "memberStatusCode_QueryDerivedReferrer_MemberList." + key;
    }
    protected Map<String, Object> _memberStatusCode_QueryDerivedReferrer_MemberListParameterMap;
    public Map<String, Object> getMemberStatusCode_QueryDerivedReferrer_MemberListParameter() { return _memberStatusCode_QueryDerivedReferrer_MemberListParameterMap; }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberListParameter(Object parameterValue) {
        if (_memberStatusCode_QueryDerivedReferrer_MemberListParameterMap == null) { _memberStatusCode_QueryDerivedReferrer_MemberListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_memberStatusCode_QueryDerivedReferrer_MemberListParameterMap.size() + 1);
        _memberStatusCode_QueryDerivedReferrer_MemberListParameterMap.put(key, parameterValue); return "memberStatusCode_QueryDerivedReferrer_MemberListParameter." + key;
    }

    protected Map<String, MemberLoginCQ> _memberStatusCode_QueryDerivedReferrer_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberStatusCode_QueryDerivedReferrer_MemberLoginList() { return _memberStatusCode_QueryDerivedReferrer_MemberLoginListMap; }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberStatusCode_QueryDerivedReferrer_MemberLoginListMap == null) { _memberStatusCode_QueryDerivedReferrer_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_QueryDerivedReferrer_MemberLoginListMap.size() + 1);
        _memberStatusCode_QueryDerivedReferrer_MemberLoginListMap.put(key, subQuery); return "memberStatusCode_QueryDerivedReferrer_MemberLoginList." + key;
    }
    protected Map<String, Object> _memberStatusCode_QueryDerivedReferrer_MemberLoginListParameterMap;
    public Map<String, Object> getMemberStatusCode_QueryDerivedReferrer_MemberLoginListParameter() { return _memberStatusCode_QueryDerivedReferrer_MemberLoginListParameterMap; }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberLoginListParameter(Object parameterValue) {
        if (_memberStatusCode_QueryDerivedReferrer_MemberLoginListParameterMap == null) { _memberStatusCode_QueryDerivedReferrer_MemberLoginListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_memberStatusCode_QueryDerivedReferrer_MemberLoginListParameterMap.size() + 1);
        _memberStatusCode_QueryDerivedReferrer_MemberLoginListParameterMap.put(key, parameterValue); return "memberStatusCode_QueryDerivedReferrer_MemberLoginListParameter." + key;
    }

    public BsMemberStatusCQ addOrderBy_MemberStatusCode_Asc() { regOBA("MEMBER_STATUS_CODE"); return this; }
    public BsMemberStatusCQ addOrderBy_MemberStatusCode_Desc() { regOBD("MEMBER_STATUS_CODE"); return this; }

    protected ConditionValue _memberStatusName;
    public ConditionValue getMemberStatusName() {
        if (_memberStatusName == null) { _memberStatusName = new ConditionValue(); }
        return _memberStatusName;
    }
    protected ConditionValue getCValueMemberStatusName() { return getMemberStatusName(); }

    public BsMemberStatusCQ addOrderBy_MemberStatusName_Asc() { regOBA("MEMBER_STATUS_NAME"); return this; }
    public BsMemberStatusCQ addOrderBy_MemberStatusName_Desc() { regOBD("MEMBER_STATUS_NAME"); return this; }

    protected ConditionValue _displayOrder;
    public ConditionValue getDisplayOrder() {
        if (_displayOrder == null) { _displayOrder = new ConditionValue(); }
        return _displayOrder;
    }
    protected ConditionValue getCValueDisplayOrder() { return getDisplayOrder(); }

    public BsMemberStatusCQ addOrderBy_DisplayOrder_Asc() { regOBA("DISPLAY_ORDER"); return this; }
    public BsMemberStatusCQ addOrderBy_DisplayOrder_Desc() { regOBD("DISPLAY_ORDER"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsMemberStatusCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsMemberStatusCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, MemberStatusCQ> _scalarSubQueryMap;
    public Map<String, MemberStatusCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(MemberStatusCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberStatusCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
