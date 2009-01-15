package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of PRODUCT.
 * @author DBFlute(AutoGenerator)
 */
public class BsProductCQ extends AbstractBsProductCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected ProductCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsProductCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from PRODUCT) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public ProductCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new ProductCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join PRODUCT on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public ProductCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        ProductCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
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

    public BsProductCQ addOrderBy_ProductId_Asc() { regOBA("PRODUCT_ID"); return this; }
    public BsProductCQ addOrderBy_ProductId_Desc() { regOBD("PRODUCT_ID"); return this; }

    protected ConditionValue _productName;
    public ConditionValue getProductName() {
        if (_productName == null) { _productName = new ConditionValue(); }
        return _productName;
    }
    protected ConditionValue getCValueProductName() { return getProductName(); }

    public BsProductCQ addOrderBy_ProductName_Asc() { regOBA("PRODUCT_NAME"); return this; }
    public BsProductCQ addOrderBy_ProductName_Desc() { regOBD("PRODUCT_NAME"); return this; }

    protected ConditionValue _productHandleCode;
    public ConditionValue getProductHandleCode() {
        if (_productHandleCode == null) { _productHandleCode = new ConditionValue(); }
        return _productHandleCode;
    }
    protected ConditionValue getCValueProductHandleCode() { return getProductHandleCode(); }

    public BsProductCQ addOrderBy_ProductHandleCode_Asc() { regOBA("PRODUCT_HANDLE_CODE"); return this; }
    public BsProductCQ addOrderBy_ProductHandleCode_Desc() { regOBD("PRODUCT_HANDLE_CODE"); return this; }

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

    public BsProductCQ addOrderBy_ProductStatusCode_Asc() { regOBA("PRODUCT_STATUS_CODE"); return this; }
    public BsProductCQ addOrderBy_ProductStatusCode_Desc() { regOBD("PRODUCT_STATUS_CODE"); return this; }

    protected ConditionValue _registerDatetime;
    public ConditionValue getRegisterDatetime() {
        if (_registerDatetime == null) { _registerDatetime = new ConditionValue(); }
        return _registerDatetime;
    }
    protected ConditionValue getCValueRegisterDatetime() { return getRegisterDatetime(); }

    public BsProductCQ addOrderBy_RegisterDatetime_Asc() { regOBA("REGISTER_DATETIME"); return this; }
    public BsProductCQ addOrderBy_RegisterDatetime_Desc() { regOBD("REGISTER_DATETIME"); return this; }

    protected ConditionValue _registerUser;
    public ConditionValue getRegisterUser() {
        if (_registerUser == null) { _registerUser = new ConditionValue(); }
        return _registerUser;
    }
    protected ConditionValue getCValueRegisterUser() { return getRegisterUser(); }

    public BsProductCQ addOrderBy_RegisterUser_Asc() { regOBA("REGISTER_USER"); return this; }
    public BsProductCQ addOrderBy_RegisterUser_Desc() { regOBD("REGISTER_USER"); return this; }

    protected ConditionValue _registerProcess;
    public ConditionValue getRegisterProcess() {
        if (_registerProcess == null) { _registerProcess = new ConditionValue(); }
        return _registerProcess;
    }
    protected ConditionValue getCValueRegisterProcess() { return getRegisterProcess(); }

    public BsProductCQ addOrderBy_RegisterProcess_Asc() { regOBA("REGISTER_PROCESS"); return this; }
    public BsProductCQ addOrderBy_RegisterProcess_Desc() { regOBD("REGISTER_PROCESS"); return this; }

    protected ConditionValue _updateDatetime;
    public ConditionValue getUpdateDatetime() {
        if (_updateDatetime == null) { _updateDatetime = new ConditionValue(); }
        return _updateDatetime;
    }
    protected ConditionValue getCValueUpdateDatetime() { return getUpdateDatetime(); }

    public BsProductCQ addOrderBy_UpdateDatetime_Asc() { regOBA("UPDATE_DATETIME"); return this; }
    public BsProductCQ addOrderBy_UpdateDatetime_Desc() { regOBD("UPDATE_DATETIME"); return this; }

    protected ConditionValue _updateUser;
    public ConditionValue getUpdateUser() {
        if (_updateUser == null) { _updateUser = new ConditionValue(); }
        return _updateUser;
    }
    protected ConditionValue getCValueUpdateUser() { return getUpdateUser(); }

    public BsProductCQ addOrderBy_UpdateUser_Asc() { regOBA("UPDATE_USER"); return this; }
    public BsProductCQ addOrderBy_UpdateUser_Desc() { regOBD("UPDATE_USER"); return this; }

    protected ConditionValue _updateProcess;
    public ConditionValue getUpdateProcess() {
        if (_updateProcess == null) { _updateProcess = new ConditionValue(); }
        return _updateProcess;
    }
    protected ConditionValue getCValueUpdateProcess() { return getUpdateProcess(); }

    public BsProductCQ addOrderBy_UpdateProcess_Asc() { regOBA("UPDATE_PROCESS"); return this; }
    public BsProductCQ addOrderBy_UpdateProcess_Desc() { regOBD("UPDATE_PROCESS"); return this; }

    protected ConditionValue _versionNo;
    public ConditionValue getVersionNo() {
        if (_versionNo == null) { _versionNo = new ConditionValue(); }
        return _versionNo;
    }
    protected ConditionValue getCValueVersionNo() { return getVersionNo(); }

    public BsProductCQ addOrderBy_VersionNo_Asc() { regOBA("VERSION_NO"); return this; }
    public BsProductCQ addOrderBy_VersionNo_Desc() { regOBD("VERSION_NO"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsProductCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsProductCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        ProductCQ baseQuery = (ProductCQ)baseQueryAsSuper;
        ProductCQ unionQuery = (ProductCQ)unionQueryAsSuper;
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
        String nrp = resolveNextRelationPath("PRODUCT", "productStatus");
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
    protected Map<String, ProductCQ> _scalarSubQueryMap;
    public Map<String, ProductCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(ProductCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return ProductCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return ProductCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
