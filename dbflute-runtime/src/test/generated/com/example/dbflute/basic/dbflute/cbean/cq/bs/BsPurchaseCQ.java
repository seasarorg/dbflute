package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of PURCHASE.
 * @author DBFlute(AutoGenerator)
 */
public class BsPurchaseCQ extends AbstractBsPurchaseCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected PurchaseCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsPurchaseCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from PURCHASE) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public PurchaseCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new PurchaseCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join PURCHASE on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public PurchaseCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        PurchaseCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _purchaseId;
    public ConditionValue getPurchaseId() {
        if (_purchaseId == null) { _purchaseId = new ConditionValue(); }
        return _purchaseId;
    }
    protected ConditionValue getCValuePurchaseId() { return getPurchaseId(); }

    public BsPurchaseCQ addOrderBy_PurchaseId_Asc() { regOBA("PURCHASE_ID"); return this; }
    public BsPurchaseCQ addOrderBy_PurchaseId_Desc() { regOBD("PURCHASE_ID"); return this; }

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
      
    public BsPurchaseCQ addOrderBy_MemberId_Asc() { regOBA("MEMBER_ID"); return this; }
    public BsPurchaseCQ addOrderBy_MemberId_Desc() { regOBD("MEMBER_ID"); return this; }

    protected ConditionValue _productId;
    public ConditionValue getProductId() {
        if (_productId == null) { _productId = new ConditionValue(); }
        return _productId;
    }
    protected ConditionValue getCValueProductId() { return getProductId(); }

    protected Map<String, ProductCQ> _productId_InScopeSubQuery_ProductMap;
    public Map<String, ProductCQ> getProductId_InScopeSubQuery_Product() { return _productId_InScopeSubQuery_ProductMap; }
    public String keepProductId_InScopeSubQuery_Product(ProductCQ subQuery) {
        if (_productId_InScopeSubQuery_ProductMap == null) { _productId_InScopeSubQuery_ProductMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productId_InScopeSubQuery_ProductMap.size() + 1);
        _productId_InScopeSubQuery_ProductMap.put(key, subQuery); return "productId_InScopeSubQuery_Product." + key;
    }
      
    public BsPurchaseCQ addOrderBy_ProductId_Asc() { regOBA("PRODUCT_ID"); return this; }
    public BsPurchaseCQ addOrderBy_ProductId_Desc() { regOBD("PRODUCT_ID"); return this; }

    protected ConditionValue _purchaseDatetime;
    public ConditionValue getPurchaseDatetime() {
        if (_purchaseDatetime == null) { _purchaseDatetime = new ConditionValue(); }
        return _purchaseDatetime;
    }
    protected ConditionValue getCValuePurchaseDatetime() { return getPurchaseDatetime(); }

    public BsPurchaseCQ addOrderBy_PurchaseDatetime_Asc() { regOBA("PURCHASE_DATETIME"); return this; }
    public BsPurchaseCQ addOrderBy_PurchaseDatetime_Desc() { regOBD("PURCHASE_DATETIME"); return this; }

    protected ConditionValue _purchaseCount;
    public ConditionValue getPurchaseCount() {
        if (_purchaseCount == null) { _purchaseCount = new ConditionValue(); }
        return _purchaseCount;
    }
    protected ConditionValue getCValuePurchaseCount() { return getPurchaseCount(); }

    public BsPurchaseCQ addOrderBy_PurchaseCount_Asc() { regOBA("PURCHASE_COUNT"); return this; }
    public BsPurchaseCQ addOrderBy_PurchaseCount_Desc() { regOBD("PURCHASE_COUNT"); return this; }

    protected ConditionValue _purchasePrice;
    public ConditionValue getPurchasePrice() {
        if (_purchasePrice == null) { _purchasePrice = new ConditionValue(); }
        return _purchasePrice;
    }
    protected ConditionValue getCValuePurchasePrice() { return getPurchasePrice(); }

    public BsPurchaseCQ addOrderBy_PurchasePrice_Asc() { regOBA("PURCHASE_PRICE"); return this; }
    public BsPurchaseCQ addOrderBy_PurchasePrice_Desc() { regOBD("PURCHASE_PRICE"); return this; }

    protected ConditionValue _paymentCompleteFlg;
    public ConditionValue getPaymentCompleteFlg() {
        if (_paymentCompleteFlg == null) { _paymentCompleteFlg = new ConditionValue(); }
        return _paymentCompleteFlg;
    }
    protected ConditionValue getCValuePaymentCompleteFlg() { return getPaymentCompleteFlg(); }

    public BsPurchaseCQ addOrderBy_PaymentCompleteFlg_Asc() { regOBA("PAYMENT_COMPLETE_FLG"); return this; }
    public BsPurchaseCQ addOrderBy_PaymentCompleteFlg_Desc() { regOBD("PAYMENT_COMPLETE_FLG"); return this; }

    protected ConditionValue _registerDatetime;
    public ConditionValue getRegisterDatetime() {
        if (_registerDatetime == null) { _registerDatetime = new ConditionValue(); }
        return _registerDatetime;
    }
    protected ConditionValue getCValueRegisterDatetime() { return getRegisterDatetime(); }

    public BsPurchaseCQ addOrderBy_RegisterDatetime_Asc() { regOBA("REGISTER_DATETIME"); return this; }
    public BsPurchaseCQ addOrderBy_RegisterDatetime_Desc() { regOBD("REGISTER_DATETIME"); return this; }

    protected ConditionValue _registerUser;
    public ConditionValue getRegisterUser() {
        if (_registerUser == null) { _registerUser = new ConditionValue(); }
        return _registerUser;
    }
    protected ConditionValue getCValueRegisterUser() { return getRegisterUser(); }

    public BsPurchaseCQ addOrderBy_RegisterUser_Asc() { regOBA("REGISTER_USER"); return this; }
    public BsPurchaseCQ addOrderBy_RegisterUser_Desc() { regOBD("REGISTER_USER"); return this; }

    protected ConditionValue _registerProcess;
    public ConditionValue getRegisterProcess() {
        if (_registerProcess == null) { _registerProcess = new ConditionValue(); }
        return _registerProcess;
    }
    protected ConditionValue getCValueRegisterProcess() { return getRegisterProcess(); }

    public BsPurchaseCQ addOrderBy_RegisterProcess_Asc() { regOBA("REGISTER_PROCESS"); return this; }
    public BsPurchaseCQ addOrderBy_RegisterProcess_Desc() { regOBD("REGISTER_PROCESS"); return this; }

    protected ConditionValue _updateDatetime;
    public ConditionValue getUpdateDatetime() {
        if (_updateDatetime == null) { _updateDatetime = new ConditionValue(); }
        return _updateDatetime;
    }
    protected ConditionValue getCValueUpdateDatetime() { return getUpdateDatetime(); }

    public BsPurchaseCQ addOrderBy_UpdateDatetime_Asc() { regOBA("UPDATE_DATETIME"); return this; }
    public BsPurchaseCQ addOrderBy_UpdateDatetime_Desc() { regOBD("UPDATE_DATETIME"); return this; }

    protected ConditionValue _updateUser;
    public ConditionValue getUpdateUser() {
        if (_updateUser == null) { _updateUser = new ConditionValue(); }
        return _updateUser;
    }
    protected ConditionValue getCValueUpdateUser() { return getUpdateUser(); }

    public BsPurchaseCQ addOrderBy_UpdateUser_Asc() { regOBA("UPDATE_USER"); return this; }
    public BsPurchaseCQ addOrderBy_UpdateUser_Desc() { regOBD("UPDATE_USER"); return this; }

    protected ConditionValue _updateProcess;
    public ConditionValue getUpdateProcess() {
        if (_updateProcess == null) { _updateProcess = new ConditionValue(); }
        return _updateProcess;
    }
    protected ConditionValue getCValueUpdateProcess() { return getUpdateProcess(); }

    public BsPurchaseCQ addOrderBy_UpdateProcess_Asc() { regOBA("UPDATE_PROCESS"); return this; }
    public BsPurchaseCQ addOrderBy_UpdateProcess_Desc() { regOBD("UPDATE_PROCESS"); return this; }

    protected ConditionValue _versionNo;
    public ConditionValue getVersionNo() {
        if (_versionNo == null) { _versionNo = new ConditionValue(); }
        return _versionNo;
    }
    protected ConditionValue getCValueVersionNo() { return getVersionNo(); }

    public BsPurchaseCQ addOrderBy_VersionNo_Asc() { regOBA("VERSION_NO"); return this; }
    public BsPurchaseCQ addOrderBy_VersionNo_Desc() { regOBD("VERSION_NO"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsPurchaseCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsPurchaseCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        PurchaseCQ baseQuery = (PurchaseCQ)baseQueryAsSuper;
        PurchaseCQ unionQuery = (PurchaseCQ)unionQueryAsSuper;
        if (baseQuery.hasConditionQueryMember()) {
            unionQuery.queryMember().reflectRelationOnUnionQuery(baseQuery.queryMember(), unionQuery.queryMember());
        }
        if (baseQuery.hasConditionQueryProduct()) {
            unionQuery.queryProduct().reflectRelationOnUnionQuery(baseQuery.queryProduct(), unionQuery.queryProduct());
        }
        if (baseQuery.hasConditionQuerySummaryProduct()) {
            unionQuery.querySummaryProduct().reflectRelationOnUnionQuery(baseQuery.querySummaryProduct(), unionQuery.querySummaryProduct());
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
        String nrp = resolveNextRelationPath("PURCHASE", "member");
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

    public ProductCQ queryProduct() {
        return getConditionQueryProduct();
    }
    protected ProductCQ _conditionQueryProduct;
    public ProductCQ getConditionQueryProduct() {
        if (_conditionQueryProduct == null) {
            _conditionQueryProduct = xcreateQueryProduct();
            xsetupOuterJoinProduct();
        }
        return _conditionQueryProduct;
    }
    protected ProductCQ xcreateQueryProduct() {
        String nrp = resolveNextRelationPath("PURCHASE", "product");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        ProductCQ cq = new ProductCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("product"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinProduct() {
        ProductCQ cq = getConditionQueryProduct();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("PRODUCT_ID"), cq.getRealColumnName("PRODUCT_ID"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryProduct() {
        return _conditionQueryProduct != null;
    }

    public SummaryProductCQ querySummaryProduct() {
        return getConditionQuerySummaryProduct();
    }
    protected SummaryProductCQ _conditionQuerySummaryProduct;
    public SummaryProductCQ getConditionQuerySummaryProduct() {
        if (_conditionQuerySummaryProduct == null) {
            _conditionQuerySummaryProduct = xcreateQuerySummaryProduct();
            xsetupOuterJoinSummaryProduct();
        }
        return _conditionQuerySummaryProduct;
    }
    protected SummaryProductCQ xcreateQuerySummaryProduct() {
        String nrp = resolveNextRelationPath("PURCHASE", "summaryProduct");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        SummaryProductCQ cq = new SummaryProductCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("summaryProduct"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinSummaryProduct() {
        SummaryProductCQ cq = getConditionQuerySummaryProduct();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("PRODUCT_ID"), cq.getRealColumnName("PRODUCT_ID"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQuerySummaryProduct() {
        return _conditionQuerySummaryProduct != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, PurchaseCQ> _scalarSubQueryMap;
    public Map<String, PurchaseCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(PurchaseCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return PurchaseCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return PurchaseCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
