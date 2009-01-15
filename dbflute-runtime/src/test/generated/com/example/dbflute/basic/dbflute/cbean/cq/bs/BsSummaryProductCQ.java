package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of SUMMARY_PRODUCT.
 * @author DBFlute(AutoGenerator)
 */
public class BsSummaryProductCQ extends AbstractBsSummaryProductCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected SummaryProductCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsSummaryProductCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from SUMMARY_PRODUCT) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public SummaryProductCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new SummaryProductCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join SUMMARY_PRODUCT on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public SummaryProductCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        SummaryProductCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _productId;
    public ConditionValue getProductId() {
        if (_productId == null) { _productId = new ConditionValue(); }
        return _productId;
    }
    protected ConditionValue getCValueProductId() { return getProductId(); }

    protected Map<String, PurchaseCQ> _productId_InScopeSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getProductId_InScopeSubQuery_PurchaseList() { return _productId_InScopeSubQuery_PurchaseListMap; }
    public String keepProductId_InScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_productId_InScopeSubQuery_PurchaseListMap == null) { _productId_InScopeSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productId_InScopeSubQuery_PurchaseListMap.size() + 1);
        _productId_InScopeSubQuery_PurchaseListMap.put(key, subQuery); return "productId_InScopeSubQuery_PurchaseList." + key;
    }

    protected Map<String, PurchaseCQ> _productId_NotInScopeSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getProductId_NotInScopeSubQuery_PurchaseList() { return _productId_NotInScopeSubQuery_PurchaseListMap; }
    public String keepProductId_NotInScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_productId_NotInScopeSubQuery_PurchaseListMap == null) { _productId_NotInScopeSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productId_NotInScopeSubQuery_PurchaseListMap.size() + 1);
        _productId_NotInScopeSubQuery_PurchaseListMap.put(key, subQuery); return "productId_NotInScopeSubQuery_PurchaseList." + key;
    }

    protected Map<String, PurchaseCQ> _productId_ExistsSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getProductId_ExistsSubQuery_PurchaseList() { return _productId_ExistsSubQuery_PurchaseListMap; }
    public String keepProductId_ExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_productId_ExistsSubQuery_PurchaseListMap == null) { _productId_ExistsSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productId_ExistsSubQuery_PurchaseListMap.size() + 1);
        _productId_ExistsSubQuery_PurchaseListMap.put(key, subQuery); return "productId_ExistsSubQuery_PurchaseList." + key;
    }

    protected Map<String, PurchaseCQ> _productId_NotExistsSubQuery_PurchaseListMap;
    public Map<String, PurchaseCQ> getProductId_NotExistsSubQuery_PurchaseList() { return _productId_NotExistsSubQuery_PurchaseListMap; }
    public String keepProductId_NotExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        if (_productId_NotExistsSubQuery_PurchaseListMap == null) { _productId_NotExistsSubQuery_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productId_NotExistsSubQuery_PurchaseListMap.size() + 1);
        _productId_NotExistsSubQuery_PurchaseListMap.put(key, subQuery); return "productId_NotExistsSubQuery_PurchaseList." + key;
    }

    protected Map<String, PurchaseCQ> _productId_SpecifyDerivedReferrer_PurchaseListMap;
    public Map<String, PurchaseCQ> getProductId_SpecifyDerivedReferrer_PurchaseList() { return _productId_SpecifyDerivedReferrer_PurchaseListMap; }
    public String keepProductId_SpecifyDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        if (_productId_SpecifyDerivedReferrer_PurchaseListMap == null) { _productId_SpecifyDerivedReferrer_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productId_SpecifyDerivedReferrer_PurchaseListMap.size() + 1);
        _productId_SpecifyDerivedReferrer_PurchaseListMap.put(key, subQuery); return "productId_SpecifyDerivedReferrer_PurchaseList." + key;
    }

    protected Map<String, PurchaseCQ> _productId_QueryDerivedReferrer_PurchaseListMap;
    public Map<String, PurchaseCQ> getProductId_QueryDerivedReferrer_PurchaseList() { return _productId_QueryDerivedReferrer_PurchaseListMap; }
    public String keepProductId_QueryDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        if (_productId_QueryDerivedReferrer_PurchaseListMap == null) { _productId_QueryDerivedReferrer_PurchaseListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productId_QueryDerivedReferrer_PurchaseListMap.size() + 1);
        _productId_QueryDerivedReferrer_PurchaseListMap.put(key, subQuery); return "productId_QueryDerivedReferrer_PurchaseList." + key;
    }
    protected Map<String, Object> _productId_QueryDerivedReferrer_PurchaseListParameterMap;
    public Map<String, Object> getProductId_QueryDerivedReferrer_PurchaseListParameter() { return _productId_QueryDerivedReferrer_PurchaseListParameterMap; }
    public String keepProductId_QueryDerivedReferrer_PurchaseListParameter(Object parameterValue) {
        if (_productId_QueryDerivedReferrer_PurchaseListParameterMap == null) { _productId_QueryDerivedReferrer_PurchaseListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_productId_QueryDerivedReferrer_PurchaseListParameterMap.size() + 1);
        _productId_QueryDerivedReferrer_PurchaseListParameterMap.put(key, parameterValue); return "productId_QueryDerivedReferrer_PurchaseListParameter." + key;
    }

    public BsSummaryProductCQ addOrderBy_ProductId_Asc() { regOBA("PRODUCT_ID"); return this; }
    public BsSummaryProductCQ addOrderBy_ProductId_Desc() { regOBD("PRODUCT_ID"); return this; }

    protected ConditionValue _productName;
    public ConditionValue getProductName() {
        if (_productName == null) { _productName = new ConditionValue(); }
        return _productName;
    }
    protected ConditionValue getCValueProductName() { return getProductName(); }

    public BsSummaryProductCQ addOrderBy_ProductName_Asc() { regOBA("PRODUCT_NAME"); return this; }
    public BsSummaryProductCQ addOrderBy_ProductName_Desc() { regOBD("PRODUCT_NAME"); return this; }

    protected ConditionValue _productStatusCode;
    public ConditionValue getProductStatusCode() {
        if (_productStatusCode == null) { _productStatusCode = new ConditionValue(); }
        return _productStatusCode;
    }
    protected ConditionValue getCValueProductStatusCode() { return getProductStatusCode(); }

    protected Map<String, ProductStatusCQ> _productStatusCode_InScopeSubQuery_ProductStatusMap;
    public Map<String, ProductStatusCQ> getProductStatusCode_InScopeSubQuery_ProductStatus() { return _productStatusCode_InScopeSubQuery_ProductStatusMap; }
    public String keepProductStatusCode_InScopeSubQuery_ProductStatus(ProductStatusCQ subQuery) {
        if (_productStatusCode_InScopeSubQuery_ProductStatusMap == null) { _productStatusCode_InScopeSubQuery_ProductStatusMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_InScopeSubQuery_ProductStatusMap.size() + 1);
        _productStatusCode_InScopeSubQuery_ProductStatusMap.put(key, subQuery); return "productStatusCode_InScopeSubQuery_ProductStatus." + key;
    }

    public BsSummaryProductCQ addOrderBy_ProductStatusCode_Asc() { regOBA("PRODUCT_STATUS_CODE"); return this; }
    public BsSummaryProductCQ addOrderBy_ProductStatusCode_Desc() { regOBD("PRODUCT_STATUS_CODE"); return this; }

    protected ConditionValue _latestPurchaseDatetime;
    public ConditionValue getLatestPurchaseDatetime() {
        if (_latestPurchaseDatetime == null) { _latestPurchaseDatetime = new ConditionValue(); }
        return _latestPurchaseDatetime;
    }
    protected ConditionValue getCValueLatestPurchaseDatetime() { return getLatestPurchaseDatetime(); }

    public BsSummaryProductCQ addOrderBy_LatestPurchaseDatetime_Asc() { regOBA("LATEST_PURCHASE_DATETIME"); return this; }
    public BsSummaryProductCQ addOrderBy_LatestPurchaseDatetime_Desc() { regOBD("LATEST_PURCHASE_DATETIME"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsSummaryProductCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsSummaryProductCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        SummaryProductCQ baseQuery = (SummaryProductCQ)baseQueryAsSuper;
        SummaryProductCQ unionQuery = (SummaryProductCQ)unionQueryAsSuper;
        if (baseQuery.hasConditionQueryProductStatus()) {
            unionQuery.queryProductStatus().reflectRelationOnUnionQuery(baseQuery.queryProductStatus(), unionQuery.queryProductStatus());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    public ProductStatusCQ queryProductStatus() {
        return getConditionQueryProductStatus();
    }
    protected ProductStatusCQ _conditionQueryProductStatus;
    public ProductStatusCQ getConditionQueryProductStatus() {
        if (_conditionQueryProductStatus == null) {
            _conditionQueryProductStatus = xcreateQueryProductStatus();
            xsetupOuterJoinProductStatus();
        }
        return _conditionQueryProductStatus;
    }
    protected ProductStatusCQ xcreateQueryProductStatus() {
        String nrp = resolveNextRelationPath("SUMMARY_PRODUCT", "productStatus");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        ProductStatusCQ cq = new ProductStatusCQ(this, getSqlClause(), jan, getNextNestLevel());
        cq.xsetForeignPropertyName("productStatus"); cq.xsetRelationPath(nrp); return cq;
    }
    protected void xsetupOuterJoinProductStatus() {
        ProductStatusCQ cq = getConditionQueryProductStatus();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("PRODUCT_STATUS_CODE"), cq.getRealColumnName("PRODUCT_STATUS_CODE"));
        registerOuterJoin(cq, joinOnMap);
    }
    public boolean hasConditionQueryProductStatus() {
        return _conditionQueryProductStatus != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, SummaryProductCQ> _scalarSubQueryMap;
    public Map<String, SummaryProductCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(SummaryProductCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return SummaryProductCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return SummaryProductCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
