package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of MEMBER.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberCQ extends AbstractBsMemberCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected MemberCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMemberCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from MEMBER) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public MemberCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new MemberCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join MEMBER on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public MemberCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        MemberCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
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

    protected Map<String, MemberAddressCQ> _memberId_InScopeSubQuery_MemberAddressListMap;
    public Map<String, MemberAddressCQ> getMemberId_InScopeSubQuery_MemberAddressList() { return _memberId_InScopeSubQuery_MemberAddressListMap; }
    public String keepMemberId_InScopeSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        if (_memberId_InScopeSubQuery_MemberAddressListMap == null) { _memberId_InScopeSubQuery_MemberAddressListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_InScopeSubQuery_MemberAddressListMap.size() + 1);
        _memberId_InScopeSubQuery_MemberAddressListMap.put(key, subQuery); return "memberId_InScopeSubQuery_MemberAddressList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberId_InScopeSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberId_InScopeSubQuery_MemberLoginList() { return _memberId_InScopeSubQuery_MemberLoginListMap; }
    public String keepMemberId_InScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberId_InScopeSubQuery_MemberLoginListMap == null) { _memberId_InScopeSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_InScopeSubQuery_MemberLoginListMap.size() + 1);
        _memberId_InScopeSubQuery_MemberLoginListMap.put(key, subQuery); return "memberId_InScopeSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberSecurityCQ> _memberId_InScopeSubQuery_MemberSecurityAsOneMap;
    public Map<String, MemberSecurityCQ> getMemberId_InScopeSubQuery_MemberSecurityAsOne() { return _memberId_InScopeSubQuery_MemberSecurityAsOneMap; }
    public String keepMemberId_InScopeSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        if (_memberId_InScopeSubQuery_MemberSecurityAsOneMap == null) { _memberId_InScopeSubQuery_MemberSecurityAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_InScopeSubQuery_MemberSecurityAsOneMap.size() + 1);
        _memberId_InScopeSubQuery_MemberSecurityAsOneMap.put(key, subQuery); return "memberId_InScopeSubQuery_MemberSecurityAsOne." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _memberId_InScopeSubQuery_MemberWithdrawalAsOneMap;
    public Map<String, MemberWithdrawalCQ> getMemberId_InScopeSubQuery_MemberWithdrawalAsOne() { return _memberId_InScopeSubQuery_MemberWithdrawalAsOneMap; }
    public String keepMemberId_InScopeSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        if (_memberId_InScopeSubQuery_MemberWithdrawalAsOneMap == null) { _memberId_InScopeSubQuery_MemberWithdrawalAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_InScopeSubQuery_MemberWithdrawalAsOneMap.size() + 1);
        _memberId_InScopeSubQuery_MemberWithdrawalAsOneMap.put(key, subQuery); return "memberId_InScopeSubQuery_MemberWithdrawalAsOne." + key;
    }

    protected Map<String, PurchaseCQ> _memberId_InScopeSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getMemberId_InScopeSubQuery_PurchaseList() { return _memberId_InScopeSubQuery_PurchaseListMap; }
    public String keepMemberId_InScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_memberId_InScopeSubQuery_PurchaseListMap == null) { _memberId_InScopeSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_InScopeSubQuery_PurchaseListMap.size() + 1);
        _memberId_InScopeSubQuery_PurchaseListMap.put(key, subQuery); return "memberId_InScopeSubQuery_PurchaseList." + key;
    }

    protected Map<String, MemberAddressCQ> _memberId_NotInScopeSubQuery_MemberAddressListMap;
    public Map<String, MemberAddressCQ> getMemberId_NotInScopeSubQuery_MemberAddressList() { return _memberId_NotInScopeSubQuery_MemberAddressListMap; }
    public String keepMemberId_NotInScopeSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        if (_memberId_NotInScopeSubQuery_MemberAddressListMap == null) { _memberId_NotInScopeSubQuery_MemberAddressListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotInScopeSubQuery_MemberAddressListMap.size() + 1);
        _memberId_NotInScopeSubQuery_MemberAddressListMap.put(key, subQuery); return "memberId_NotInScopeSubQuery_MemberAddressList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberId_NotInScopeSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberId_NotInScopeSubQuery_MemberLoginList() { return _memberId_NotInScopeSubQuery_MemberLoginListMap; }
    public String keepMemberId_NotInScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberId_NotInScopeSubQuery_MemberLoginListMap == null) { _memberId_NotInScopeSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotInScopeSubQuery_MemberLoginListMap.size() + 1);
        _memberId_NotInScopeSubQuery_MemberLoginListMap.put(key, subQuery); return "memberId_NotInScopeSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberSecurityCQ> _memberId_NotInScopeSubQuery_MemberSecurityAsOneMap;
    public Map<String, MemberSecurityCQ> getMemberId_NotInScopeSubQuery_MemberSecurityAsOne() { return _memberId_NotInScopeSubQuery_MemberSecurityAsOneMap; }
    public String keepMemberId_NotInScopeSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        if (_memberId_NotInScopeSubQuery_MemberSecurityAsOneMap == null) { _memberId_NotInScopeSubQuery_MemberSecurityAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotInScopeSubQuery_MemberSecurityAsOneMap.size() + 1);
        _memberId_NotInScopeSubQuery_MemberSecurityAsOneMap.put(key, subQuery); return "memberId_NotInScopeSubQuery_MemberSecurityAsOne." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _memberId_NotInScopeSubQuery_MemberWithdrawalAsOneMap;
    public Map<String, MemberWithdrawalCQ> getMemberId_NotInScopeSubQuery_MemberWithdrawalAsOne() { return _memberId_NotInScopeSubQuery_MemberWithdrawalAsOneMap; }
    public String keepMemberId_NotInScopeSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        if (_memberId_NotInScopeSubQuery_MemberWithdrawalAsOneMap == null) { _memberId_NotInScopeSubQuery_MemberWithdrawalAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotInScopeSubQuery_MemberWithdrawalAsOneMap.size() + 1);
        _memberId_NotInScopeSubQuery_MemberWithdrawalAsOneMap.put(key, subQuery); return "memberId_NotInScopeSubQuery_MemberWithdrawalAsOne." + key;
    }

    protected Map<String, PurchaseCQ> _memberId_NotInScopeSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getMemberId_NotInScopeSubQuery_PurchaseList() { return _memberId_NotInScopeSubQuery_PurchaseListMap; }
    public String keepMemberId_NotInScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_memberId_NotInScopeSubQuery_PurchaseListMap == null) { _memberId_NotInScopeSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotInScopeSubQuery_PurchaseListMap.size() + 1);
        _memberId_NotInScopeSubQuery_PurchaseListMap.put(key, subQuery); return "memberId_NotInScopeSubQuery_PurchaseList." + key;
    }

    protected Map<String, MemberAddressCQ> _memberId_ExistsSubQuery_MemberAddressListMap;
    public Map<String, MemberAddressCQ> getMemberId_ExistsSubQuery_MemberAddressList() { return _memberId_ExistsSubQuery_MemberAddressListMap; }
    public String keepMemberId_ExistsSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        if (_memberId_ExistsSubQuery_MemberAddressListMap == null) { _memberId_ExistsSubQuery_MemberAddressListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_ExistsSubQuery_MemberAddressListMap.size() + 1);
        _memberId_ExistsSubQuery_MemberAddressListMap.put(key, subQuery); return "memberId_ExistsSubQuery_MemberAddressList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberId_ExistsSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberId_ExistsSubQuery_MemberLoginList() { return _memberId_ExistsSubQuery_MemberLoginListMap; }
    public String keepMemberId_ExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberId_ExistsSubQuery_MemberLoginListMap == null) { _memberId_ExistsSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_ExistsSubQuery_MemberLoginListMap.size() + 1);
        _memberId_ExistsSubQuery_MemberLoginListMap.put(key, subQuery); return "memberId_ExistsSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberSecurityCQ> _memberId_ExistsSubQuery_MemberSecurityAsOneMap;
    public Map<String, MemberSecurityCQ> getMemberId_ExistsSubQuery_MemberSecurityAsOne() { return _memberId_ExistsSubQuery_MemberSecurityAsOneMap; }
    public String keepMemberId_ExistsSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        if (_memberId_ExistsSubQuery_MemberSecurityAsOneMap == null) { _memberId_ExistsSubQuery_MemberSecurityAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_ExistsSubQuery_MemberSecurityAsOneMap.size() + 1);
        _memberId_ExistsSubQuery_MemberSecurityAsOneMap.put(key, subQuery); return "memberId_ExistsSubQuery_MemberSecurityAsOne." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _memberId_ExistsSubQuery_MemberWithdrawalAsOneMap;
    public Map<String, MemberWithdrawalCQ> getMemberId_ExistsSubQuery_MemberWithdrawalAsOne() { return _memberId_ExistsSubQuery_MemberWithdrawalAsOneMap; }
    public String keepMemberId_ExistsSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        if (_memberId_ExistsSubQuery_MemberWithdrawalAsOneMap == null) { _memberId_ExistsSubQuery_MemberWithdrawalAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_ExistsSubQuery_MemberWithdrawalAsOneMap.size() + 1);
        _memberId_ExistsSubQuery_MemberWithdrawalAsOneMap.put(key, subQuery); return "memberId_ExistsSubQuery_MemberWithdrawalAsOne." + key;
    }

    protected Map<String, PurchaseCQ> _memberId_ExistsSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getMemberId_ExistsSubQuery_PurchaseList() { return _memberId_ExistsSubQuery_PurchaseListMap; }
    public String keepMemberId_ExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_memberId_ExistsSubQuery_PurchaseListMap == null) { _memberId_ExistsSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_ExistsSubQuery_PurchaseListMap.size() + 1);
        _memberId_ExistsSubQuery_PurchaseListMap.put(key, subQuery); return "memberId_ExistsSubQuery_PurchaseList." + key;
    }

    protected Map<String, MemberAddressCQ> _memberId_NotExistsSubQuery_MemberAddressListMap;
    public Map<String, MemberAddressCQ> getMemberId_NotExistsSubQuery_MemberAddressList() { return _memberId_NotExistsSubQuery_MemberAddressListMap; }
    public String keepMemberId_NotExistsSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        if (_memberId_NotExistsSubQuery_MemberAddressListMap == null) { _memberId_NotExistsSubQuery_MemberAddressListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotExistsSubQuery_MemberAddressListMap.size() + 1);
        _memberId_NotExistsSubQuery_MemberAddressListMap.put(key, subQuery); return "memberId_NotExistsSubQuery_MemberAddressList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberId_NotExistsSubQuery_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberId_NotExistsSubQuery_MemberLoginList() { return _memberId_NotExistsSubQuery_MemberLoginListMap; }
    public String keepMemberId_NotExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberId_NotExistsSubQuery_MemberLoginListMap == null) { _memberId_NotExistsSubQuery_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotExistsSubQuery_MemberLoginListMap.size() + 1);
        _memberId_NotExistsSubQuery_MemberLoginListMap.put(key, subQuery); return "memberId_NotExistsSubQuery_MemberLoginList." + key;
    }

    protected Map<String, MemberSecurityCQ> _memberId_NotExistsSubQuery_MemberSecurityAsOneMap;
    public Map<String, MemberSecurityCQ> getMemberId_NotExistsSubQuery_MemberSecurityAsOne() { return _memberId_NotExistsSubQuery_MemberSecurityAsOneMap; }
    public String keepMemberId_NotExistsSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        if (_memberId_NotExistsSubQuery_MemberSecurityAsOneMap == null) { _memberId_NotExistsSubQuery_MemberSecurityAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotExistsSubQuery_MemberSecurityAsOneMap.size() + 1);
        _memberId_NotExistsSubQuery_MemberSecurityAsOneMap.put(key, subQuery); return "memberId_NotExistsSubQuery_MemberSecurityAsOne." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _memberId_NotExistsSubQuery_MemberWithdrawalAsOneMap;
    public Map<String, MemberWithdrawalCQ> getMemberId_NotExistsSubQuery_MemberWithdrawalAsOne() { return _memberId_NotExistsSubQuery_MemberWithdrawalAsOneMap; }
    public String keepMemberId_NotExistsSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        if (_memberId_NotExistsSubQuery_MemberWithdrawalAsOneMap == null) { _memberId_NotExistsSubQuery_MemberWithdrawalAsOneMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotExistsSubQuery_MemberWithdrawalAsOneMap.size() + 1);
        _memberId_NotExistsSubQuery_MemberWithdrawalAsOneMap.put(key, subQuery); return "memberId_NotExistsSubQuery_MemberWithdrawalAsOne." + key;
    }

    protected Map<String, PurchaseCQ> _memberId_NotExistsSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getMemberId_NotExistsSubQuery_PurchaseList() { return _memberId_NotExistsSubQuery_PurchaseListMap; }
    public String keepMemberId_NotExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_memberId_NotExistsSubQuery_PurchaseListMap == null) { _memberId_NotExistsSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_NotExistsSubQuery_PurchaseListMap.size() + 1);
        _memberId_NotExistsSubQuery_PurchaseListMap.put(key, subQuery); return "memberId_NotExistsSubQuery_PurchaseList." + key;
    }

    protected Map<String, MemberAddressCQ> _memberId_SpecifyDerivedReferrer_MemberAddressListMap;
    public Map<String, MemberAddressCQ> getMemberId_SpecifyDerivedReferrer_MemberAddressList() { return _memberId_SpecifyDerivedReferrer_MemberAddressListMap; }
    public String keepMemberId_SpecifyDerivedReferrer_MemberAddressList(MemberAddressCQ subQuery) {
        if (_memberId_SpecifyDerivedReferrer_MemberAddressListMap == null) { _memberId_SpecifyDerivedReferrer_MemberAddressListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_SpecifyDerivedReferrer_MemberAddressListMap.size() + 1);
        _memberId_SpecifyDerivedReferrer_MemberAddressListMap.put(key, subQuery); return "memberId_SpecifyDerivedReferrer_MemberAddressList." + key;
    }

    protected Map<String, MemberLoginCQ> _memberId_SpecifyDerivedReferrer_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberId_SpecifyDerivedReferrer_MemberLoginList() { return _memberId_SpecifyDerivedReferrer_MemberLoginListMap; }
    public String keepMemberId_SpecifyDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberId_SpecifyDerivedReferrer_MemberLoginListMap == null) { _memberId_SpecifyDerivedReferrer_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_SpecifyDerivedReferrer_MemberLoginListMap.size() + 1);
        _memberId_SpecifyDerivedReferrer_MemberLoginListMap.put(key, subQuery); return "memberId_SpecifyDerivedReferrer_MemberLoginList." + key;
    }

    protected Map<String, PurchaseCQ> _memberId_SpecifyDerivedReferrer_PurchaseListMap;
    public Map<String, PurchaseCQ> getMemberId_SpecifyDerivedReferrer_PurchaseList() { return _memberId_SpecifyDerivedReferrer_PurchaseListMap; }
    public String keepMemberId_SpecifyDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        if (_memberId_SpecifyDerivedReferrer_PurchaseListMap == null) { _memberId_SpecifyDerivedReferrer_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_SpecifyDerivedReferrer_PurchaseListMap.size() + 1);
        _memberId_SpecifyDerivedReferrer_PurchaseListMap.put(key, subQuery); return "memberId_SpecifyDerivedReferrer_PurchaseList." + key;
    }

    protected Map<String, MemberAddressCQ> _memberId_QueryDerivedReferrer_MemberAddressListMap;
    public Map<String, MemberAddressCQ> getMemberId_QueryDerivedReferrer_MemberAddressList() { return _memberId_QueryDerivedReferrer_MemberAddressListMap; }
    public String keepMemberId_QueryDerivedReferrer_MemberAddressList(MemberAddressCQ subQuery) {
        if (_memberId_QueryDerivedReferrer_MemberAddressListMap == null) { _memberId_QueryDerivedReferrer_MemberAddressListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_QueryDerivedReferrer_MemberAddressListMap.size() + 1);
        _memberId_QueryDerivedReferrer_MemberAddressListMap.put(key, subQuery); return "memberId_QueryDerivedReferrer_MemberAddressList." + key;
    }
    protected Map<String, Object> _memberId_QueryDerivedReferrer_MemberAddressListParameterMap;
    public Map<String, Object> getMemberId_QueryDerivedReferrer_MemberAddressListParameter() { return _memberId_QueryDerivedReferrer_MemberAddressListParameterMap; }
    public String keepMemberId_QueryDerivedReferrer_MemberAddressListParameter(Object parameterValue) {
        if (_memberId_QueryDerivedReferrer_MemberAddressListParameterMap == null) { _memberId_QueryDerivedReferrer_MemberAddressListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_memberId_QueryDerivedReferrer_MemberAddressListParameterMap.size() + 1);
        _memberId_QueryDerivedReferrer_MemberAddressListParameterMap.put(key, parameterValue); return "memberId_QueryDerivedReferrer_MemberAddressListParameter." + key;
    }

    protected Map<String, MemberLoginCQ> _memberId_QueryDerivedReferrer_MemberLoginListMap;
    public Map<String, MemberLoginCQ> getMemberId_QueryDerivedReferrer_MemberLoginList() { return _memberId_QueryDerivedReferrer_MemberLoginListMap; }
    public String keepMemberId_QueryDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        if (_memberId_QueryDerivedReferrer_MemberLoginListMap == null) { _memberId_QueryDerivedReferrer_MemberLoginListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_QueryDerivedReferrer_MemberLoginListMap.size() + 1);
        _memberId_QueryDerivedReferrer_MemberLoginListMap.put(key, subQuery); return "memberId_QueryDerivedReferrer_MemberLoginList." + key;
    }
    protected Map<String, Object> _memberId_QueryDerivedReferrer_MemberLoginListParameterMap;
    public Map<String, Object> getMemberId_QueryDerivedReferrer_MemberLoginListParameter() { return _memberId_QueryDerivedReferrer_MemberLoginListParameterMap; }
    public String keepMemberId_QueryDerivedReferrer_MemberLoginListParameter(Object parameterValue) {
        if (_memberId_QueryDerivedReferrer_MemberLoginListParameterMap == null) { _memberId_QueryDerivedReferrer_MemberLoginListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_memberId_QueryDerivedReferrer_MemberLoginListParameterMap.size() + 1);
        _memberId_QueryDerivedReferrer_MemberLoginListParameterMap.put(key, parameterValue); return "memberId_QueryDerivedReferrer_MemberLoginListParameter." + key;
    }

    protected Map<String, PurchaseCQ> _memberId_QueryDerivedReferrer_PurchaseListMap;
    public Map<String, PurchaseCQ> getMemberId_QueryDerivedReferrer_PurchaseList() { return _memberId_QueryDerivedReferrer_PurchaseListMap; }
    public String keepMemberId_QueryDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        if (_memberId_QueryDerivedReferrer_PurchaseListMap == null) { _memberId_QueryDerivedReferrer_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberId_QueryDerivedReferrer_PurchaseListMap.size() + 1);
        _memberId_QueryDerivedReferrer_PurchaseListMap.put(key, subQuery); return "memberId_QueryDerivedReferrer_PurchaseList." + key;
    }
    protected Map<String, Object> _memberId_QueryDerivedReferrer_PurchaseListParameterMap;
    public Map<String, Object> getMemberId_QueryDerivedReferrer_PurchaseListParameter() { return _memberId_QueryDerivedReferrer_PurchaseListParameterMap; }
    public String keepMemberId_QueryDerivedReferrer_PurchaseListParameter(Object parameterValue) {
        if (_memberId_QueryDerivedReferrer_PurchaseListParameterMap == null) { _memberId_QueryDerivedReferrer_PurchaseListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_memberId_QueryDerivedReferrer_PurchaseListParameterMap.size() + 1);
        _memberId_QueryDerivedReferrer_PurchaseListParameterMap.put(key, parameterValue); return "memberId_QueryDerivedReferrer_PurchaseListParameter." + key;
    }

    public BsMemberCQ addOrderBy_MemberId_Asc() { regOBA("MEMBER_ID"); return this; }
    public BsMemberCQ addOrderBy_MemberId_Desc() { regOBD("MEMBER_ID"); return this; }

    protected ConditionValue _memberName;
    public ConditionValue getMemberName() {
        if (_memberName == null) { _memberName = new ConditionValue(); }
        return _memberName;
    }
    protected ConditionValue getCValueMemberName() { return getMemberName(); }

    public BsMemberCQ addOrderBy_MemberName_Asc() { regOBA("MEMBER_NAME"); return this; }
    public BsMemberCQ addOrderBy_MemberName_Desc() { regOBD("MEMBER_NAME"); return this; }

    protected ConditionValue _memberAccount;
    public ConditionValue getMemberAccount() {
        if (_memberAccount == null) { _memberAccount = new ConditionValue(); }
        return _memberAccount;
    }
    protected ConditionValue getCValueMemberAccount() { return getMemberAccount(); }

    public BsMemberCQ addOrderBy_MemberAccount_Asc() { regOBA("MEMBER_ACCOUNT"); return this; }
    public BsMemberCQ addOrderBy_MemberAccount_Desc() { regOBD("MEMBER_ACCOUNT"); return this; }

    protected ConditionValue _memberStatusCode;
    public ConditionValue getMemberStatusCode() {
        if (_memberStatusCode == null) { _memberStatusCode = new ConditionValue(); }
        return _memberStatusCode;
    }
    protected ConditionValue getCValueMemberStatusCode() { return getMemberStatusCode(); }

    protected Map<String, MemberStatusCQ> _memberStatusCode_InScopeSubQuery_MemberStatusMap;
    public Map<String, MemberStatusCQ> getMemberStatusCode_InScopeSubQuery_MemberStatus() { return _memberStatusCode_InScopeSubQuery_MemberStatusMap; }
    public String keepMemberStatusCode_InScopeSubQuery_MemberStatus(MemberStatusCQ subQuery) {
        if (_memberStatusCode_InScopeSubQuery_MemberStatusMap == null) { _memberStatusCode_InScopeSubQuery_MemberStatusMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_memberStatusCode_InScopeSubQuery_MemberStatusMap.size() + 1);
        _memberStatusCode_InScopeSubQuery_MemberStatusMap.put(key, subQuery); return "memberStatusCode_InScopeSubQuery_MemberStatus." + key;
    }

    public BsMemberCQ addOrderBy_MemberStatusCode_Asc() { regOBA("MEMBER_STATUS_CODE"); return this; }
    public BsMemberCQ addOrderBy_MemberStatusCode_Desc() { regOBD("MEMBER_STATUS_CODE"); return this; }

    protected ConditionValue _memberFormalizedDatetime;
    public ConditionValue getMemberFormalizedDatetime() {
        if (_memberFormalizedDatetime == null) { _memberFormalizedDatetime = new ConditionValue(); }
        return _memberFormalizedDatetime;
    }
    protected ConditionValue getCValueMemberFormalizedDatetime() { return getMemberFormalizedDatetime(); }

    public BsMemberCQ addOrderBy_MemberFormalizedDatetime_Asc() { regOBA("MEMBER_FORMALIZED_DATETIME"); return this; }
    public BsMemberCQ addOrderBy_MemberFormalizedDatetime_Desc() { regOBD("MEMBER_FORMALIZED_DATETIME"); return this; }

    protected ConditionValue _memberBirthday;
    public ConditionValue getMemberBirthday() {
        if (_memberBirthday == null) { _memberBirthday = new ConditionValue(); }
        return _memberBirthday;
    }
    protected ConditionValue getCValueMemberBirthday() { return getMemberBirthday(); }

    public BsMemberCQ addOrderBy_MemberBirthday_Asc() { regOBA("MEMBER_BIRTHDAY"); return this; }
    public BsMemberCQ addOrderBy_MemberBirthday_Desc() { regOBD("MEMBER_BIRTHDAY"); return this; }

    protected ConditionValue _registerDatetime;
    public ConditionValue getRegisterDatetime() {
        if (_registerDatetime == null) { _registerDatetime = new ConditionValue(); }
        return _registerDatetime;
    }
    protected ConditionValue getCValueRegisterDatetime() { return getRegisterDatetime(); }

    public BsMemberCQ addOrderBy_RegisterDatetime_Asc() { regOBA("REGISTER_DATETIME"); return this; }
    public BsMemberCQ addOrderBy_RegisterDatetime_Desc() { regOBD("REGISTER_DATETIME"); return this; }

    protected ConditionValue _registerUser;
    public ConditionValue getRegisterUser() {
        if (_registerUser == null) { _registerUser = new ConditionValue(); }
        return _registerUser;
    }
    protected ConditionValue getCValueRegisterUser() { return getRegisterUser(); }

    public BsMemberCQ addOrderBy_RegisterUser_Asc() { regOBA("REGISTER_USER"); return this; }
    public BsMemberCQ addOrderBy_RegisterUser_Desc() { regOBD("REGISTER_USER"); return this; }

    protected ConditionValue _registerProcess;
    public ConditionValue getRegisterProcess() {
        if (_registerProcess == null) { _registerProcess = new ConditionValue(); }
        return _registerProcess;
    }
    protected ConditionValue getCValueRegisterProcess() { return getRegisterProcess(); }

    public BsMemberCQ addOrderBy_RegisterProcess_Asc() { regOBA("REGISTER_PROCESS"); return this; }
    public BsMemberCQ addOrderBy_RegisterProcess_Desc() { regOBD("REGISTER_PROCESS"); return this; }

    protected ConditionValue _updateDatetime;
    public ConditionValue getUpdateDatetime() {
        if (_updateDatetime == null) { _updateDatetime = new ConditionValue(); }
        return _updateDatetime;
    }
    protected ConditionValue getCValueUpdateDatetime() { return getUpdateDatetime(); }

    public BsMemberCQ addOrderBy_UpdateDatetime_Asc() { regOBA("UPDATE_DATETIME"); return this; }
    public BsMemberCQ addOrderBy_UpdateDatetime_Desc() { regOBD("UPDATE_DATETIME"); return this; }

    protected ConditionValue _updateUser;
    public ConditionValue getUpdateUser() {
        if (_updateUser == null) { _updateUser = new ConditionValue(); }
        return _updateUser;
    }
    protected ConditionValue getCValueUpdateUser() { return getUpdateUser(); }

    public BsMemberCQ addOrderBy_UpdateUser_Asc() { regOBA("UPDATE_USER"); return this; }
    public BsMemberCQ addOrderBy_UpdateUser_Desc() { regOBD("UPDATE_USER"); return this; }

    protected ConditionValue _updateProcess;
    public ConditionValue getUpdateProcess() {
        if (_updateProcess == null) { _updateProcess = new ConditionValue(); }
        return _updateProcess;
    }
    protected ConditionValue getCValueUpdateProcess() { return getUpdateProcess(); }

    public BsMemberCQ addOrderBy_UpdateProcess_Asc() { regOBA("UPDATE_PROCESS"); return this; }
    public BsMemberCQ addOrderBy_UpdateProcess_Desc() { regOBD("UPDATE_PROCESS"); return this; }

    protected ConditionValue _versionNo;
    public ConditionValue getVersionNo() {
        if (_versionNo == null) { _versionNo = new ConditionValue(); }
        return _versionNo;
    }
    protected ConditionValue getCValueVersionNo() { return getVersionNo(); }

    public BsMemberCQ addOrderBy_VersionNo_Asc() { regOBA("VERSION_NO"); return this; }
    public BsMemberCQ addOrderBy_VersionNo_Desc() { regOBD("VERSION_NO"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsMemberCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsMemberCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        MemberCQ baseQuery = (MemberCQ)baseQueryAsSuper;
        MemberCQ unionQuery = (MemberCQ)unionQueryAsSuper;
        if (baseQuery.hasConditionQueryMemberStatus()) {
            unionQuery.queryMemberStatus().reflectRelationOnUnionQuery(baseQuery.queryMemberStatus(), unionQuery.queryMemberStatus());
        }
        if (baseQuery.hasConditionQueryMemberAddressAsValid()) {
            unionQuery.xsetParameterMapMemberAddressAsValid(baseQuery.getParameterMapMemberAddressAsValid());
            unionQuery.getConditionQueryMemberAddressAsValid().reflectRelationOnUnionQuery(baseQuery.getConditionQueryMemberAddressAsValid(), unionQuery.getConditionQueryMemberAddressAsValid());
        }
        if (baseQuery.hasConditionQueryMemberSecurityAsOne()) {
            unionQuery.queryMemberSecurityAsOne().reflectRelationOnUnionQuery(baseQuery.queryMemberSecurityAsOne(), unionQuery.queryMemberSecurityAsOne());
        }
        if (baseQuery.hasConditionQueryMemberWithdrawalAsOne()) {
            unionQuery.queryMemberWithdrawalAsOne().reflectRelationOnUnionQuery(baseQuery.queryMemberWithdrawalAsOne(), unionQuery.queryMemberWithdrawalAsOne());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
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
        String nrp = resolveNextRelationPath("MEMBER", "memberStatus");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        MemberStatusCQ cq = new MemberStatusCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("memberStatus"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinMemberStatus() {
        MemberStatusCQ cq = getConditionQueryMemberStatus();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("MEMBER_STATUS_CODE"), cq.getRealColumnName("MEMBER_STATUS_CODE"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryMemberStatus() {
        return _conditionQueryMemberStatus != null;
    }

    public MemberAddressCQ queryMemberAddressAsValid(java.util.Date targetDate) {
        Map<String, Object> parameterMap = getParameterMapMemberAddressAsValid();
        parameterMap.put("targetDate", targetDate);
        return getConditionQueryMemberAddressAsValid();
    }
    protected MemberAddressCQ _conditionQueryMemberAddressAsValid;
    public MemberAddressCQ getConditionQueryMemberAddressAsValid() {
        if (_conditionQueryMemberAddressAsValid == null) {
            _conditionQueryMemberAddressAsValid = xcreateQueryMemberAddressAsValid();
            xsetupOuterJoinMemberAddressAsValid();
        }
        return _conditionQueryMemberAddressAsValid;
    }
    protected Map<String, Object> _parameterMapMemberAddressAsValid;
    public Map<String, Object> getParameterMapMemberAddressAsValid() {
        if (_parameterMapMemberAddressAsValid == null) {
            _parameterMapMemberAddressAsValid = newLinkedHashMap();
        }
        return _parameterMapMemberAddressAsValid;
    }
    public void xsetParameterMapMemberAddressAsValid(Map<String, Object> parameterMap) {
        _parameterMapMemberAddressAsValid = parameterMap; // for UnionQuery
    }
    protected MemberAddressCQ xcreateQueryMemberAddressAsValid() {
        String nrp = resolveNextRelationPath("MEMBER", "memberAddressAsValid");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        MemberAddressCQ cq = new MemberAddressCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("memberAddressAsValid"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinMemberAddressAsValid() {
        MemberAddressCQ cq = getConditionQueryMemberAddressAsValid();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("MEMBER_ID"), cq.getRealColumnName("MEMBER_ID"));
        joinOnMap.put("$$fixedCondition$$", ppFxCd("$$foreignAlias$$.VALID_BEGIN_DATE <= /*$$locationBase$$.parameterMapMemberAddressAsValid.targetDate*/null and $$foreignAlias$$.VALID_END_DATE >= /*$$locationBase$$.parameterMapMemberAddressAsValid.targetDate*/null", getRealAliasName(), cq.getRealAliasName()));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryMemberAddressAsValid() {
        return _conditionQueryMemberAddressAsValid != null;
    }

    public MemberSecurityCQ queryMemberSecurityAsOne() {
        return getConditionQueryMemberSecurityAsOne();
    }
    protected MemberSecurityCQ _conditionQueryMemberSecurityAsOne;
    public MemberSecurityCQ getConditionQueryMemberSecurityAsOne() {
        if (_conditionQueryMemberSecurityAsOne == null) {
            _conditionQueryMemberSecurityAsOne = xcreateQueryMemberSecurityAsOne();
            xsetupOuterJoinMemberSecurityAsOne();
        }
        return _conditionQueryMemberSecurityAsOne;
    }
    protected MemberSecurityCQ xcreateQueryMemberSecurityAsOne() {
        String nrp = resolveNextRelationPath("MEMBER", "memberSecurityAsOne");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        MemberSecurityCQ cq = new MemberSecurityCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("memberSecurityAsOne"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinMemberSecurityAsOne() {
        MemberSecurityCQ cq = getConditionQueryMemberSecurityAsOne();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("MEMBER_ID"), cq.getRealColumnName("MEMBER_ID"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryMemberSecurityAsOne() {
        return _conditionQueryMemberSecurityAsOne != null;
    }

    public MemberWithdrawalCQ queryMemberWithdrawalAsOne() {
        return getConditionQueryMemberWithdrawalAsOne();
    }
    protected MemberWithdrawalCQ _conditionQueryMemberWithdrawalAsOne;
    public MemberWithdrawalCQ getConditionQueryMemberWithdrawalAsOne() {
        if (_conditionQueryMemberWithdrawalAsOne == null) {
            _conditionQueryMemberWithdrawalAsOne = xcreateQueryMemberWithdrawalAsOne();
            xsetupOuterJoinMemberWithdrawalAsOne();
        }
        return _conditionQueryMemberWithdrawalAsOne;
    }
    protected MemberWithdrawalCQ xcreateQueryMemberWithdrawalAsOne() {
        String nrp = resolveNextRelationPath("MEMBER", "memberWithdrawalAsOne");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        MemberWithdrawalCQ cq = new MemberWithdrawalCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("memberWithdrawalAsOne"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinMemberWithdrawalAsOne() {
        MemberWithdrawalCQ cq = getConditionQueryMemberWithdrawalAsOne();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("MEMBER_ID"), cq.getRealColumnName("MEMBER_ID"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryMemberWithdrawalAsOne() {
        return _conditionQueryMemberWithdrawalAsOne != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, MemberCQ> _scalarSubQueryMap;
    public Map<String, MemberCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(MemberCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
