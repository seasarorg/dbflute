package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Collection;

import org.dbflute.cbean.*;
import org.dbflute.cbean.ckey.*;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.dbmeta.DBMetaProvider;

import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The abstract condition-query of PRODUCT_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsProductStatusCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsProductStatusCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider getDBMetaProvider() {
        return _dbmetaProvider;
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "PRODUCT_STATUS";
    }
    
    public String getTableSqlName() {
        return "PRODUCT_STATUS";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {PK : NotNull : CHAR(3)}
     * @param productStatusCode The value of productStatusCode as equal.
     */
    public void setProductStatusCode_Equal(String productStatusCode) {
        regProductStatusCode(CK_EQ, fRES(productStatusCode));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productStatusCode The value of productStatusCode as notEqual.
     */
    public void setProductStatusCode_NotEqual(String productStatusCode) {
        regProductStatusCode(CK_NE, fRES(productStatusCode));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productStatusCode The value of productStatusCode as prefixSearch.
     */
    public void setProductStatusCode_PrefixSearch(String productStatusCode) {
        regProductStatusCode(CK_PS, fRES(productStatusCode));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productStatusCodeList The collection of productStatusCode as inScope.
     */
    public void setProductStatusCode_InScope(Collection<String> productStatusCodeList) {
        regProductStatusCode(CK_INS, cTL(productStatusCodeList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productStatusCodeList The collection of productStatusCode as notInScope.
     */
    public void setProductStatusCode_NotInScope(Collection<String> productStatusCodeList) {
        regProductStatusCode(CK_NINS, cTL(productStatusCodeList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productStatusCode The value of productStatusCode as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setProductStatusCode_LikeSearch(String productStatusCode, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(productStatusCode), getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productStatusCode The value of productStatusCode as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setProductStatusCode_NotLikeSearch(String productStatusCode, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(productStatusCode), getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode", likeSearchOption);
    }

    public void inScopeProductList(SubQuery<ProductCB> subQuery) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_InScopeSubQuery_ProductList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_InScopeSubQuery_ProductList(ProductCQ subQuery);

    public void inScopeSummaryProductList(SubQuery<SummaryProductCB> subQuery) {
        assertObjectNotNull("subQuery<SummaryProductCB>", subQuery);
        SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_InScopeSubQuery_SummaryProductList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_InScopeSubQuery_SummaryProductList(SummaryProductCQ subQuery);

    public void notInScopeProductList(SubQuery<ProductCB> subQuery) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_NotInScopeSubQuery_ProductList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_NotInScopeSubQuery_ProductList(ProductCQ subQuery);

    public void notInScopeSummaryProductList(SubQuery<SummaryProductCB> subQuery) {
        assertObjectNotNull("subQuery<SummaryProductCB>", subQuery);
        SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_NotInScopeSubQuery_SummaryProductList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_NotInScopeSubQuery_SummaryProductList(SummaryProductCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select PRODUCT_STATUS_CODE from PRODUCT where ...)}
     * @param subQuery The sub-query of ProductStatusCode_ExistsSubQuery_ProductList for 'exists'. (NotNull)
     */
    public void existsProductList(SubQuery<ProductCB> subQuery) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_ExistsSubQuery_ProductList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_ExistsSubQuery_ProductList(ProductCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select PRODUCT_STATUS_CODE from SUMMARY_PRODUCT where ...)}
     * @param subQuery The sub-query of ProductStatusCode_ExistsSubQuery_SummaryProductList for 'exists'. (NotNull)
     */
    public void existsSummaryProductList(SubQuery<SummaryProductCB> subQuery) {
        assertObjectNotNull("subQuery<SummaryProductCB>", subQuery);
        SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_ExistsSubQuery_SummaryProductList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_ExistsSubQuery_SummaryProductList(SummaryProductCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select PRODUCT_STATUS_CODE from PRODUCT where ...)}
     * @param subQuery The sub-query of ProductStatusCode_NotExistsSubQuery_ProductList for 'not exists'. (NotNull)
     */
    public void notExistsProductList(SubQuery<ProductCB> subQuery) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_NotExistsSubQuery_ProductList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_NotExistsSubQuery_ProductList(ProductCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select PRODUCT_STATUS_CODE from SUMMARY_PRODUCT where ...)}
     * @param subQuery The sub-query of ProductStatusCode_NotExistsSubQuery_SummaryProductList for 'not exists'. (NotNull)
     */
    public void notExistsSummaryProductList(SubQuery<SummaryProductCB> subQuery) {
        assertObjectNotNull("subQuery<SummaryProductCB>", subQuery);
        SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_NotExistsSubQuery_SummaryProductList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_NotExistsSubQuery_SummaryProductList(SummaryProductCQ subQuery);

    public void xsderiveProductList(String function, SubQuery<ProductCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_SpecifyDerivedReferrer_ProductList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName, aliasName);
    }
    public abstract String keepProductStatusCode_SpecifyDerivedReferrer_ProductList(ProductCQ subQuery);

    public void xsderiveSummaryProductList(String function, SubQuery<SummaryProductCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<SummaryProductCB>", subQuery);
        SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_SpecifyDerivedReferrer_SummaryProductList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName, aliasName);
    }
    public abstract String keepProductStatusCode_SpecifyDerivedReferrer_SummaryProductList(SummaryProductCQ subQuery);

    public QDRFunction<ProductCB> derivedProductList() {
        return xcreateQDRFunctionProductList();
    }
    protected QDRFunction<ProductCB> xcreateQDRFunctionProductList() {
        return new QDRFunction<ProductCB>(new QDRSetupper<ProductCB>() {
            public void setup(String function, SubQuery<ProductCB> subQuery, String operand, Object value) {
                xqderiveProductList(function, subQuery, operand, value);
            }
        });
    }
    public void xqderiveProductList(String function, SubQuery<ProductCB> subQuery, String operand, Object value) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_QueryDerivedReferrer_ProductList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepProductStatusCode_QueryDerivedReferrer_ProductListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepProductStatusCode_QueryDerivedReferrer_ProductList(ProductCQ subQuery);
    public abstract String keepProductStatusCode_QueryDerivedReferrer_ProductListParameter(Object parameterValue);

    public QDRFunction<SummaryProductCB> derivedSummaryProductList() {
        return xcreateQDRFunctionSummaryProductList();
    }
    protected QDRFunction<SummaryProductCB> xcreateQDRFunctionSummaryProductList() {
        return new QDRFunction<SummaryProductCB>(new QDRSetupper<SummaryProductCB>() {
            public void setup(String function, SubQuery<SummaryProductCB> subQuery, String operand, Object value) {
                xqderiveSummaryProductList(function, subQuery, operand, value);
            }
        });
    }
    public void xqderiveSummaryProductList(String function, SubQuery<SummaryProductCB> subQuery, String operand, Object value) {
        assertObjectNotNull("subQuery<SummaryProductCB>", subQuery);
        SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_QueryDerivedReferrer_SummaryProductList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepProductStatusCode_QueryDerivedReferrer_SummaryProductListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepProductStatusCode_QueryDerivedReferrer_SummaryProductList(SummaryProductCQ subQuery);
    public abstract String keepProductStatusCode_QueryDerivedReferrer_SummaryProductListParameter(Object parameterValue);

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setProductStatusCode_IsNull() { regProductStatusCode(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setProductStatusCode_IsNotNull() { regProductStatusCode(CK_ISNN, DUMMY_OBJECT); }

    protected void regProductStatusCode(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode");
    }
    protected void registerInlineProductStatusCode(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode");
    }
    abstract protected ConditionValue getCValueProductStatusCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {UQ : NotNull : VARCHAR(50)}
     * @param productStatusName The value of productStatusName as equal.
     */
    public void setProductStatusName_Equal(String productStatusName) {
        regProductStatusName(CK_EQ, fRES(productStatusName));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productStatusName The value of productStatusName as notEqual.
     */
    public void setProductStatusName_NotEqual(String productStatusName) {
        regProductStatusName(CK_NE, fRES(productStatusName));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productStatusName The value of productStatusName as prefixSearch.
     */
    public void setProductStatusName_PrefixSearch(String productStatusName) {
        regProductStatusName(CK_PS, fRES(productStatusName));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productStatusNameList The collection of productStatusName as inScope.
     */
    public void setProductStatusName_InScope(Collection<String> productStatusNameList) {
        regProductStatusName(CK_INS, cTL(productStatusNameList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productStatusNameList The collection of productStatusName as notInScope.
     */
    public void setProductStatusName_NotInScope(Collection<String> productStatusNameList) {
        regProductStatusName(CK_NINS, cTL(productStatusNameList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productStatusName The value of productStatusName as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setProductStatusName_LikeSearch(String productStatusName, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(productStatusName), getCValueProductStatusName(), "PRODUCT_STATUS_NAME", "ProductStatusName", "productStatusName", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productStatusName The value of productStatusName as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setProductStatusName_NotLikeSearch(String productStatusName, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(productStatusName), getCValueProductStatusName(), "PRODUCT_STATUS_NAME", "ProductStatusName", "productStatusName", likeSearchOption);
    }

    protected void regProductStatusName(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueProductStatusName(), "PRODUCT_STATUS_NAME", "ProductStatusName", "productStatusName");
    }
    protected void registerInlineProductStatusName(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueProductStatusName(), "PRODUCT_STATUS_NAME", "ProductStatusName", "productStatusName");
    }
    abstract protected ConditionValue getCValueProductStatusName();

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public SSQFunction<ProductStatusCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<ProductStatusCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<ProductStatusCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<ProductStatusCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<ProductStatusCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<ProductStatusCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<ProductStatusCB>(new SSQSetupper<ProductStatusCB>() {
            public void setup(String function, SubQuery<ProductStatusCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<ProductStatusCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<ProductStatusCB>", subQuery);
        ProductStatusCB cb = new ProductStatusCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(ProductStatusCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return ProductStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return ProductStatusCQ.class.getName(); }
}
