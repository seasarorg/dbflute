package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of PRODUCT_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public class BsProductStatusCQ extends AbstractBsProductStatusCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected ProductStatusCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsProductStatusCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from PRODUCT_STATUS) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public ProductStatusCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new ProductStatusCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join PRODUCT_STATUS on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public ProductStatusCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        ProductStatusCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _productStatusCode;
    public ConditionValue getProductStatusCode() {
        if (_productStatusCode == null) { _productStatusCode = new ConditionValue(); }
        return _productStatusCode;
    }
    protected ConditionValue getCValueProductStatusCode() { return getProductStatusCode(); }

    protected Map<String, ProductCQ> _productStatusCode_InScopeSubQuery_ProductListMap;
    public Map<String, ProductCQ> getProductStatusCode_InScopeSubQuery_ProductList() { return _productStatusCode_InScopeSubQuery_ProductListMap; }
    public String keepProductStatusCode_InScopeSubQuery_ProductList(ProductCQ subQuery) {
        if (_productStatusCode_InScopeSubQuery_ProductListMap == null) { _productStatusCode_InScopeSubQuery_ProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_InScopeSubQuery_ProductListMap.size() + 1);
        _productStatusCode_InScopeSubQuery_ProductListMap.put(key, subQuery); return "productStatusCode_InScopeSubQuery_ProductList." + key;
    }

    protected Map<String, SummaryProductCQ> _productStatusCode_InScopeSubQuery_SummaryProductListMap;
    public Map<String, SummaryProductCQ> getProductStatusCode_InScopeSubQuery_SummaryProductList() { return _productStatusCode_InScopeSubQuery_SummaryProductListMap; }
    public String keepProductStatusCode_InScopeSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        if (_productStatusCode_InScopeSubQuery_SummaryProductListMap == null) { _productStatusCode_InScopeSubQuery_SummaryProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_InScopeSubQuery_SummaryProductListMap.size() + 1);
        _productStatusCode_InScopeSubQuery_SummaryProductListMap.put(key, subQuery); return "productStatusCode_InScopeSubQuery_SummaryProductList." + key;
    }

    protected Map<String, ProductCQ> _productStatusCode_NotInScopeSubQuery_ProductListMap;
    public Map<String, ProductCQ> getProductStatusCode_NotInScopeSubQuery_ProductList() { return _productStatusCode_NotInScopeSubQuery_ProductListMap; }
    public String keepProductStatusCode_NotInScopeSubQuery_ProductList(ProductCQ subQuery) {
        if (_productStatusCode_NotInScopeSubQuery_ProductListMap == null) { _productStatusCode_NotInScopeSubQuery_ProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_NotInScopeSubQuery_ProductListMap.size() + 1);
        _productStatusCode_NotInScopeSubQuery_ProductListMap.put(key, subQuery); return "productStatusCode_NotInScopeSubQuery_ProductList." + key;
    }

    protected Map<String, SummaryProductCQ> _productStatusCode_NotInScopeSubQuery_SummaryProductListMap;
    public Map<String, SummaryProductCQ> getProductStatusCode_NotInScopeSubQuery_SummaryProductList() { return _productStatusCode_NotInScopeSubQuery_SummaryProductListMap; }
    public String keepProductStatusCode_NotInScopeSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        if (_productStatusCode_NotInScopeSubQuery_SummaryProductListMap == null) { _productStatusCode_NotInScopeSubQuery_SummaryProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_NotInScopeSubQuery_SummaryProductListMap.size() + 1);
        _productStatusCode_NotInScopeSubQuery_SummaryProductListMap.put(key, subQuery); return "productStatusCode_NotInScopeSubQuery_SummaryProductList." + key;
    }

    protected Map<String, ProductCQ> _productStatusCode_ExistsSubQuery_ProductListMap;
    public Map<String, ProductCQ> getProductStatusCode_ExistsSubQuery_ProductList() { return _productStatusCode_ExistsSubQuery_ProductListMap; }
    public String keepProductStatusCode_ExistsSubQuery_ProductList(ProductCQ subQuery) {
        if (_productStatusCode_ExistsSubQuery_ProductListMap == null) { _productStatusCode_ExistsSubQuery_ProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_ExistsSubQuery_ProductListMap.size() + 1);
        _productStatusCode_ExistsSubQuery_ProductListMap.put(key, subQuery); return "productStatusCode_ExistsSubQuery_ProductList." + key;
    }

    protected Map<String, SummaryProductCQ> _productStatusCode_ExistsSubQuery_SummaryProductListMap;
    public Map<String, SummaryProductCQ> getProductStatusCode_ExistsSubQuery_SummaryProductList() { return _productStatusCode_ExistsSubQuery_SummaryProductListMap; }
    public String keepProductStatusCode_ExistsSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        if (_productStatusCode_ExistsSubQuery_SummaryProductListMap == null) { _productStatusCode_ExistsSubQuery_SummaryProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_ExistsSubQuery_SummaryProductListMap.size() + 1);
        _productStatusCode_ExistsSubQuery_SummaryProductListMap.put(key, subQuery); return "productStatusCode_ExistsSubQuery_SummaryProductList." + key;
    }

    protected Map<String, ProductCQ> _productStatusCode_NotExistsSubQuery_ProductListMap;
    public Map<String, ProductCQ> getProductStatusCode_NotExistsSubQuery_ProductList() { return _productStatusCode_NotExistsSubQuery_ProductListMap; }
    public String keepProductStatusCode_NotExistsSubQuery_ProductList(ProductCQ subQuery) {
        if (_productStatusCode_NotExistsSubQuery_ProductListMap == null) { _productStatusCode_NotExistsSubQuery_ProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_NotExistsSubQuery_ProductListMap.size() + 1);
        _productStatusCode_NotExistsSubQuery_ProductListMap.put(key, subQuery); return "productStatusCode_NotExistsSubQuery_ProductList." + key;
    }

    protected Map<String, SummaryProductCQ> _productStatusCode_NotExistsSubQuery_SummaryProductListMap;
    public Map<String, SummaryProductCQ> getProductStatusCode_NotExistsSubQuery_SummaryProductList() { return _productStatusCode_NotExistsSubQuery_SummaryProductListMap; }
    public String keepProductStatusCode_NotExistsSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        if (_productStatusCode_NotExistsSubQuery_SummaryProductListMap == null) { _productStatusCode_NotExistsSubQuery_SummaryProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_NotExistsSubQuery_SummaryProductListMap.size() + 1);
        _productStatusCode_NotExistsSubQuery_SummaryProductListMap.put(key, subQuery); return "productStatusCode_NotExistsSubQuery_SummaryProductList." + key;
    }

    protected Map<String, ProductCQ> _productStatusCode_SpecifyDerivedReferrer_ProductListMap;
    public Map<String, ProductCQ> getProductStatusCode_SpecifyDerivedReferrer_ProductList() { return _productStatusCode_SpecifyDerivedReferrer_ProductListMap; }
    public String keepProductStatusCode_SpecifyDerivedReferrer_ProductList(ProductCQ subQuery) {
        if (_productStatusCode_SpecifyDerivedReferrer_ProductListMap == null) { _productStatusCode_SpecifyDerivedReferrer_ProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_SpecifyDerivedReferrer_ProductListMap.size() + 1);
        _productStatusCode_SpecifyDerivedReferrer_ProductListMap.put(key, subQuery); return "productStatusCode_SpecifyDerivedReferrer_ProductList." + key;
    }

    protected Map<String, SummaryProductCQ> _productStatusCode_SpecifyDerivedReferrer_SummaryProductListMap;
    public Map<String, SummaryProductCQ> getProductStatusCode_SpecifyDerivedReferrer_SummaryProductList() { return _productStatusCode_SpecifyDerivedReferrer_SummaryProductListMap; }
    public String keepProductStatusCode_SpecifyDerivedReferrer_SummaryProductList(SummaryProductCQ subQuery) {
        if (_productStatusCode_SpecifyDerivedReferrer_SummaryProductListMap == null) { _productStatusCode_SpecifyDerivedReferrer_SummaryProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_SpecifyDerivedReferrer_SummaryProductListMap.size() + 1);
        _productStatusCode_SpecifyDerivedReferrer_SummaryProductListMap.put(key, subQuery); return "productStatusCode_SpecifyDerivedReferrer_SummaryProductList." + key;
    }

    protected Map<String, ProductCQ> _productStatusCode_QueryDerivedReferrer_ProductListMap;
    public Map<String, ProductCQ> getProductStatusCode_QueryDerivedReferrer_ProductList() { return _productStatusCode_QueryDerivedReferrer_ProductListMap; }
    public String keepProductStatusCode_QueryDerivedReferrer_ProductList(ProductCQ subQuery) {
        if (_productStatusCode_QueryDerivedReferrer_ProductListMap == null) { _productStatusCode_QueryDerivedReferrer_ProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_QueryDerivedReferrer_ProductListMap.size() + 1);
        _productStatusCode_QueryDerivedReferrer_ProductListMap.put(key, subQuery); return "productStatusCode_QueryDerivedReferrer_ProductList." + key;
    }
    protected Map<String, Object> _productStatusCode_QueryDerivedReferrer_ProductListParameterMap;
    public Map<String, Object> getProductStatusCode_QueryDerivedReferrer_ProductListParameter() { return _productStatusCode_QueryDerivedReferrer_ProductListParameterMap; }
    public String keepProductStatusCode_QueryDerivedReferrer_ProductListParameter(Object parameterValue) {
        if (_productStatusCode_QueryDerivedReferrer_ProductListParameterMap == null) { _productStatusCode_QueryDerivedReferrer_ProductListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_productStatusCode_QueryDerivedReferrer_ProductListParameterMap.size() + 1);
        _productStatusCode_QueryDerivedReferrer_ProductListParameterMap.put(key, parameterValue); return "productStatusCode_QueryDerivedReferrer_ProductListParameter." + key;
    }

    protected Map<String, SummaryProductCQ> _productStatusCode_QueryDerivedReferrer_SummaryProductListMap;
    public Map<String, SummaryProductCQ> getProductStatusCode_QueryDerivedReferrer_SummaryProductList() { return _productStatusCode_QueryDerivedReferrer_SummaryProductListMap; }
    public String keepProductStatusCode_QueryDerivedReferrer_SummaryProductList(SummaryProductCQ subQuery) {
        if (_productStatusCode_QueryDerivedReferrer_SummaryProductListMap == null) { _productStatusCode_QueryDerivedReferrer_SummaryProductListMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_productStatusCode_QueryDerivedReferrer_SummaryProductListMap.size() + 1);
        _productStatusCode_QueryDerivedReferrer_SummaryProductListMap.put(key, subQuery); return "productStatusCode_QueryDerivedReferrer_SummaryProductList." + key;
    }
    protected Map<String, Object> _productStatusCode_QueryDerivedReferrer_SummaryProductListParameterMap;
    public Map<String, Object> getProductStatusCode_QueryDerivedReferrer_SummaryProductListParameter() { return _productStatusCode_QueryDerivedReferrer_SummaryProductListParameterMap; }
    public String keepProductStatusCode_QueryDerivedReferrer_SummaryProductListParameter(Object parameterValue) {
        if (_productStatusCode_QueryDerivedReferrer_SummaryProductListParameterMap == null) { _productStatusCode_QueryDerivedReferrer_SummaryProductListParameterMap = newLinkedHashMap(); }
        String key = "subQueryParameterKey" + (_productStatusCode_QueryDerivedReferrer_SummaryProductListParameterMap.size() + 1);
        _productStatusCode_QueryDerivedReferrer_SummaryProductListParameterMap.put(key, parameterValue); return "productStatusCode_QueryDerivedReferrer_SummaryProductListParameter." + key;
    }

    public BsProductStatusCQ addOrderBy_ProductStatusCode_Asc() { regOBA("PRODUCT_STATUS_CODE"); return this; }
    public BsProductStatusCQ addOrderBy_ProductStatusCode_Desc() { regOBD("PRODUCT_STATUS_CODE"); return this; }

    protected ConditionValue _productStatusName;
    public ConditionValue getProductStatusName() {
        if (_productStatusName == null) { _productStatusName = new ConditionValue(); }
        return _productStatusName;
    }
    protected ConditionValue getCValueProductStatusName() { return getProductStatusName(); }

    public BsProductStatusCQ addOrderBy_ProductStatusName_Asc() { regOBA("PRODUCT_STATUS_NAME"); return this; }
    public BsProductStatusCQ addOrderBy_ProductStatusName_Desc() { regOBD("PRODUCT_STATUS_NAME"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsProductStatusCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsProductStatusCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

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
    protected Map<String, ProductStatusCQ> _scalarSubQueryMap;
    public Map<String, ProductStatusCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(ProductStatusCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return ProductStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return ProductStatusCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
