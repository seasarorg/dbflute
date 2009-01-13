package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.dbflute.cbean.*;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of WITHDRAWAL_REASON.
 * @author DBFlute(AutoGenerator)
 */
public class BsWithdrawalReasonCQ extends AbstractBsWithdrawalReasonCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected WithdrawalReasonCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsWithdrawalReasonCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from WITHDRAWAL_REASON) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public WithdrawalReasonCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new WithdrawalReasonCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join WITHDRAWAL_REASON on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public WithdrawalReasonCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        WithdrawalReasonCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _withdrawalReasonCode;
    public ConditionValue getWithdrawalReasonCode() {
        if (_withdrawalReasonCode == null) { _withdrawalReasonCode = new ConditionValue(); }
        return _withdrawalReasonCode;
    }
    protected ConditionValue getCValueWithdrawalReasonCode() { return getWithdrawalReasonCode(); }

    protected Map<String, MemberWithdrawalCQ> _withdrawalReasonCode_InScopeSubQuery_MemberWithdrawalListMap;
    public Map<String, MemberWithdrawalCQ> getWithdrawalReasonCode_InScopeSubQuery_MemberWithdrawalList() { return _withdrawalReasonCode_InScopeSubQuery_MemberWithdrawalListMap; }
    public String keepWithdrawalReasonCode_InScopeSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        if (_withdrawalReasonCode_InScopeSubQuery_MemberWithdrawalListMap == null) { _withdrawalReasonCode_InScopeSubQuery_MemberWithdrawalListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_withdrawalReasonCode_InScopeSubQuery_MemberWithdrawalListMap.size() + 1);
        _withdrawalReasonCode_InScopeSubQuery_MemberWithdrawalListMap.put(key, subQuery); return "withdrawalReasonCode_InScopeSubQuery_MemberWithdrawalList." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _withdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalListMap;
    public Map<String, MemberWithdrawalCQ> getWithdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalList() { return _withdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalListMap; }
    public String keepWithdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        if (_withdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalListMap == null) { _withdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_withdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalListMap.size() + 1);
        _withdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalListMap.put(key, subQuery); return "withdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalList." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _withdrawalReasonCode_ExistsSubQuery_MemberWithdrawalListMap;
    public Map<String, MemberWithdrawalCQ> getWithdrawalReasonCode_ExistsSubQuery_MemberWithdrawalList() { return _withdrawalReasonCode_ExistsSubQuery_MemberWithdrawalListMap; }
    public String keepWithdrawalReasonCode_ExistsSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        if (_withdrawalReasonCode_ExistsSubQuery_MemberWithdrawalListMap == null) { _withdrawalReasonCode_ExistsSubQuery_MemberWithdrawalListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_withdrawalReasonCode_ExistsSubQuery_MemberWithdrawalListMap.size() + 1);
        _withdrawalReasonCode_ExistsSubQuery_MemberWithdrawalListMap.put(key, subQuery); return "withdrawalReasonCode_ExistsSubQuery_MemberWithdrawalList." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _withdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalListMap;
    public Map<String, MemberWithdrawalCQ> getWithdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalList() { return _withdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalListMap; }
    public String keepWithdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        if (_withdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalListMap == null) { _withdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_withdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalListMap.size() + 1);
        _withdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalListMap.put(key, subQuery); return "withdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalList." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _withdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalListMap;
    public Map<String, MemberWithdrawalCQ> getWithdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalList() { return _withdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalListMap; }
    public String keepWithdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        if (_withdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalListMap == null) { _withdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_withdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalListMap.size() + 1);
        _withdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalListMap.put(key, subQuery); return "withdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalList." + key;
    }

    protected Map<String, MemberWithdrawalCQ> _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListMap;
    public Map<String, MemberWithdrawalCQ> getWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalList() { return _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListMap; }
    public String keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        if (_withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListMap == null) { _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListMap.size() + 1);
        _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListMap.put(key, subQuery); return "withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalList." + key;
    }
    protected Map<String, Object> _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameterMap;
    public Map<String, Object> getWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameter() { return _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameterMap; }
    public String keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameter(Object parameterValue) {
        if (_withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameterMap == null) { _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameterMap.size() + 1);
        _withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameterMap.put(key, parameterValue); return "withdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameter." + key;
    }

    public BsWithdrawalReasonCQ addOrderBy_WithdrawalReasonCode_Asc() { regOBA("WITHDRAWAL_REASON_CODE"); return this; }
    public BsWithdrawalReasonCQ addOrderBy_WithdrawalReasonCode_Desc() { regOBD("WITHDRAWAL_REASON_CODE"); return this; }

    protected ConditionValue _withdrawalReasonText;
    public ConditionValue getWithdrawalReasonText() {
        if (_withdrawalReasonText == null) { _withdrawalReasonText = new ConditionValue(); }
        return _withdrawalReasonText;
    }
    protected ConditionValue getCValueWithdrawalReasonText() { return getWithdrawalReasonText(); }

    public BsWithdrawalReasonCQ addOrderBy_WithdrawalReasonText_Asc() { regOBA("WITHDRAWAL_REASON_TEXT"); return this; }
    public BsWithdrawalReasonCQ addOrderBy_WithdrawalReasonText_Desc() { regOBD("WITHDRAWAL_REASON_TEXT"); return this; }

    protected ConditionValue _displayOrder;
    public ConditionValue getDisplayOrder() {
        if (_displayOrder == null) { _displayOrder = new ConditionValue(); }
        return _displayOrder;
    }
    protected ConditionValue getCValueDisplayOrder() { return getDisplayOrder(); }

    public BsWithdrawalReasonCQ addOrderBy_DisplayOrder_Asc() { regOBA("DISPLAY_ORDER"); return this; }
    public BsWithdrawalReasonCQ addOrderBy_DisplayOrder_Desc() { regOBD("DISPLAY_ORDER"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsWithdrawalReasonCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsWithdrawalReasonCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

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
    protected Map<String, WithdrawalReasonCQ> _scalarSubQueryMap;
    public Map<String, WithdrawalReasonCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(WithdrawalReasonCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return WithdrawalReasonCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return WithdrawalReasonCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
