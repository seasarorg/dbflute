package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Collection;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.ckey.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import com.example.dbflute.basic.dbflute.allcommon.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The abstract condition-query of PRODUCT.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsProductCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsProductCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "PRODUCT";
    }
    
    public String getTableSqlName() {
        return "PRODUCT";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : ID : NotNull : INTEGER}
     * @param productId The value of productId as equal.
     */
    public void setProductId_Equal(Integer productId) {
        regProductId(CK_EQ, productId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param productId The value of productId as greaterThan.
     */
    public void setProductId_GreaterThan(Integer productId) {
        regProductId(CK_GT, productId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param productId The value of productId as lessThan.
     */
    public void setProductId_LessThan(Integer productId) {
        regProductId(CK_LT, productId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param productId The value of productId as greaterEqual.
     */
    public void setProductId_GreaterEqual(Integer productId) {
        regProductId(CK_GE, productId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param productId The value of productId as lessEqual.
     */
    public void setProductId_LessEqual(Integer productId) {
        regProductId(CK_LE, productId);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param productIdList The collection of productId as inScope.
     */
    public void setProductId_InScope(Collection<Integer> productIdList) {
        regProductId(CK_INS, cTL(productIdList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param productIdList The collection of productId as notInScope.
     */
    public void setProductId_NotInScope(Collection<Integer> productIdList) {
        regProductId(CK_NINS, cTL(productIdList));
    }

    public void inScopePurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductId_InScopeSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "PRODUCT_ID", "PRODUCT_ID", subQueryPropertyName);
    }
    public abstract String keepProductId_InScopeSubQuery_PurchaseList(PurchaseCQ subQuery);

    public void notInScopePurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductId_NotInScopeSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "PRODUCT_ID", "PRODUCT_ID", subQueryPropertyName);
    }
    public abstract String keepProductId_NotInScopeSubQuery_PurchaseList(PurchaseCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select PRODUCT_ID from PURCHASE where ...)}
     * @param subQuery The sub-query of ProductId_ExistsSubQuery_PurchaseList for 'exists'. (NotNull)
     */
    public void existsPurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductId_ExistsSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "PRODUCT_ID", "PRODUCT_ID", subQueryPropertyName);
    }
    public abstract String keepProductId_ExistsSubQuery_PurchaseList(PurchaseCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select PRODUCT_ID from PURCHASE where ...)}
     * @param subQuery The sub-query of ProductId_NotExistsSubQuery_PurchaseList for 'not exists'. (NotNull)
     */
    public void notExistsPurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductId_NotExistsSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "PRODUCT_ID", "PRODUCT_ID", subQueryPropertyName);
    }
    public abstract String keepProductId_NotExistsSubQuery_PurchaseList(PurchaseCQ subQuery);

    public void xsderivePurchaseList(String function, SubQuery<PurchaseCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepProductId_SpecifyDerivedReferrer_PurchaseList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "PRODUCT_ID", "PRODUCT_ID", subQueryPropertyName, aliasName);
    }
    public abstract String keepProductId_SpecifyDerivedReferrer_PurchaseList(PurchaseCQ subQuery);

    public QDRFunction<PurchaseCB> derivedPurchaseList() {
        return xcreateQDRFunctionPurchaseList();
    }
    protected QDRFunction<PurchaseCB> xcreateQDRFunctionPurchaseList() {
        return new QDRFunction<PurchaseCB>(new QDRSetupper<PurchaseCB>() {
            public void setup(String function, SubQuery<PurchaseCB> subQuery, String operand, Object value) {
                xqderivePurchaseList(function, subQuery, operand, value);
            }
        });
    }
    public void xqderivePurchaseList(String function, SubQuery<PurchaseCB> subQuery, String operand, Object value) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepProductId_QueryDerivedReferrer_PurchaseList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepProductId_QueryDerivedReferrer_PurchaseListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "PRODUCT_ID", "PRODUCT_ID", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepProductId_QueryDerivedReferrer_PurchaseList(PurchaseCQ subQuery);
    public abstract String keepProductId_QueryDerivedReferrer_PurchaseListParameter(Object parameterValue);

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setProductId_IsNull() { regProductId(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setProductId_IsNotNull() { regProductId(CK_ISNN, DUMMY_OBJECT); }

    protected void regProductId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueProductId(), "PRODUCT_ID", "ProductId", "productId");
    }
    protected void registerInlineProductId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueProductId(), "PRODUCT_ID", "ProductId", "productId");
    }
    abstract protected ConditionValue getCValueProductId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(50)}
     * @param productName The value of productName as equal.
     */
    public void setProductName_Equal(String productName) {
        regProductName(CK_EQ, fRES(productName));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productName The value of productName as notEqual.
     */
    public void setProductName_NotEqual(String productName) {
        regProductName(CK_NE, fRES(productName));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productName The value of productName as prefixSearch.
     */
    public void setProductName_PrefixSearch(String productName) {
        regProductName(CK_PS, fRES(productName));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productNameList The collection of productName as inScope.
     */
    public void setProductName_InScope(Collection<String> productNameList) {
        regProductName(CK_INS, cTL(productNameList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productNameList The collection of productName as notInScope.
     */
    public void setProductName_NotInScope(Collection<String> productNameList) {
        regProductName(CK_NINS, cTL(productNameList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productName The value of productName as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setProductName_LikeSearch(String productName, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(productName), getCValueProductName(), "PRODUCT_NAME", "ProductName", "productName", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productName The value of productName as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setProductName_NotLikeSearch(String productName, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(productName), getCValueProductName(), "PRODUCT_NAME", "ProductName", "productName", likeSearchOption);
    }

    protected void regProductName(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueProductName(), "PRODUCT_NAME", "ProductName", "productName");
    }
    protected void registerInlineProductName(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueProductName(), "PRODUCT_NAME", "ProductName", "productName");
    }
    abstract protected ConditionValue getCValueProductName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {UQ : NotNull : VARCHAR(100)}
     * @param productHandleCode The value of productHandleCode as equal.
     */
    public void setProductHandleCode_Equal(String productHandleCode) {
        regProductHandleCode(CK_EQ, fRES(productHandleCode));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productHandleCode The value of productHandleCode as notEqual.
     */
    public void setProductHandleCode_NotEqual(String productHandleCode) {
        regProductHandleCode(CK_NE, fRES(productHandleCode));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param productHandleCode The value of productHandleCode as prefixSearch.
     */
    public void setProductHandleCode_PrefixSearch(String productHandleCode) {
        regProductHandleCode(CK_PS, fRES(productHandleCode));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productHandleCodeList The collection of productHandleCode as inScope.
     */
    public void setProductHandleCode_InScope(Collection<String> productHandleCodeList) {
        regProductHandleCode(CK_INS, cTL(productHandleCodeList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param productHandleCodeList The collection of productHandleCode as notInScope.
     */
    public void setProductHandleCode_NotInScope(Collection<String> productHandleCodeList) {
        regProductHandleCode(CK_NINS, cTL(productHandleCodeList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productHandleCode The value of productHandleCode as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setProductHandleCode_LikeSearch(String productHandleCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(productHandleCode), getCValueProductHandleCode(), "PRODUCT_HANDLE_CODE", "ProductHandleCode", "productHandleCode", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productHandleCode The value of productHandleCode as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setProductHandleCode_NotLikeSearch(String productHandleCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(productHandleCode), getCValueProductHandleCode(), "PRODUCT_HANDLE_CODE", "ProductHandleCode", "productHandleCode", likeSearchOption);
    }

    protected void regProductHandleCode(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueProductHandleCode(), "PRODUCT_HANDLE_CODE", "ProductHandleCode", "productHandleCode");
    }
    protected void registerInlineProductHandleCode(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueProductHandleCode(), "PRODUCT_HANDLE_CODE", "ProductHandleCode", "productHandleCode");
    }
    abstract protected ConditionValue getCValueProductHandleCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : CHAR(3) : FK to PRODUCT_STATUS}
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
    public void setProductStatusCode_LikeSearch(String productStatusCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(productStatusCode), getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param productStatusCode The value of productStatusCode as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setProductStatusCode_NotLikeSearch(String productStatusCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(productStatusCode), getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode", likeSearchOption);
    }

    public void inScopeProductStatus(SubQuery<ProductStatusCB> subQuery) {
        assertObjectNotNull("subQuery<ProductStatusCB>", subQuery);
        ProductStatusCB cb = new ProductStatusCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductStatusCode_InScopeSubQuery_ProductStatus(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "PRODUCT_STATUS_CODE", "PRODUCT_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepProductStatusCode_InScopeSubQuery_ProductStatus(com.example.dbflute.basic.dbflute.cbean.cq.ProductStatusCQ subQuery);

    protected void regProductStatusCode(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode");
    }
    protected void registerInlineProductStatusCode(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueProductStatusCode(), "PRODUCT_STATUS_CODE", "ProductStatusCode", "productStatusCode");
    }
    abstract protected ConditionValue getCValueProductStatusCode();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param registerDatetime The value of registerDatetime as equal.
     */
    public void setRegisterDatetime_Equal(java.sql.Timestamp registerDatetime) {
        regRegisterDatetime(CK_EQ, registerDatetime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param registerDatetime The value of registerDatetime as greaterThan.
     */
    public void setRegisterDatetime_GreaterThan(java.sql.Timestamp registerDatetime) {
        regRegisterDatetime(CK_GT, registerDatetime);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param registerDatetime The value of registerDatetime as lessThan.
     */
    public void setRegisterDatetime_LessThan(java.sql.Timestamp registerDatetime) {
        regRegisterDatetime(CK_LT, registerDatetime);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param registerDatetime The value of registerDatetime as greaterEqual.
     */
    public void setRegisterDatetime_GreaterEqual(java.sql.Timestamp registerDatetime) {
        regRegisterDatetime(CK_GE, registerDatetime);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param registerDatetime The value of registerDatetime as lessEqual.
     */
    public void setRegisterDatetime_LessEqual(java.sql.Timestamp registerDatetime) {
        regRegisterDatetime(CK_LE, registerDatetime);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of registerDatetime. (Nullable)
     * @param toDate The to-date of registerDatetime. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setRegisterDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.seasar.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValueRegisterDatetime(), "REGISTER_DATETIME", "RegisterDatetime", "registerDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of registerDatetime. (Nullable)
     * @param toDate The to-date of registerDatetime. (Nullable)
     */
    public void setRegisterDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setRegisterDatetime_FromTo(fromDate, toDate, new org.seasar.dbflute.cbean.coption.DateFromToOption());
    }

    protected void regRegisterDatetime(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueRegisterDatetime(), "REGISTER_DATETIME", "RegisterDatetime", "registerDatetime");
    }
    protected void registerInlineRegisterDatetime(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueRegisterDatetime(), "REGISTER_DATETIME", "RegisterDatetime", "registerDatetime");
    }
    abstract protected ConditionValue getCValueRegisterDatetime();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(200)}
     * @param registerUser The value of registerUser as equal.
     */
    public void setRegisterUser_Equal(String registerUser) {
        regRegisterUser(CK_EQ, fRES(registerUser));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param registerUser The value of registerUser as notEqual.
     */
    public void setRegisterUser_NotEqual(String registerUser) {
        regRegisterUser(CK_NE, fRES(registerUser));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param registerUser The value of registerUser as prefixSearch.
     */
    public void setRegisterUser_PrefixSearch(String registerUser) {
        regRegisterUser(CK_PS, fRES(registerUser));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param registerUserList The collection of registerUser as inScope.
     */
    public void setRegisterUser_InScope(Collection<String> registerUserList) {
        regRegisterUser(CK_INS, cTL(registerUserList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param registerUserList The collection of registerUser as notInScope.
     */
    public void setRegisterUser_NotInScope(Collection<String> registerUserList) {
        regRegisterUser(CK_NINS, cTL(registerUserList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param registerUser The value of registerUser as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setRegisterUser_LikeSearch(String registerUser, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(registerUser), getCValueRegisterUser(), "REGISTER_USER", "RegisterUser", "registerUser", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param registerUser The value of registerUser as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setRegisterUser_NotLikeSearch(String registerUser, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(registerUser), getCValueRegisterUser(), "REGISTER_USER", "RegisterUser", "registerUser", likeSearchOption);
    }

    protected void regRegisterUser(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueRegisterUser(), "REGISTER_USER", "RegisterUser", "registerUser");
    }
    protected void registerInlineRegisterUser(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueRegisterUser(), "REGISTER_USER", "RegisterUser", "registerUser");
    }
    abstract protected ConditionValue getCValueRegisterUser();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(200)}
     * @param registerProcess The value of registerProcess as equal.
     */
    public void setRegisterProcess_Equal(String registerProcess) {
        regRegisterProcess(CK_EQ, fRES(registerProcess));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param registerProcess The value of registerProcess as notEqual.
     */
    public void setRegisterProcess_NotEqual(String registerProcess) {
        regRegisterProcess(CK_NE, fRES(registerProcess));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param registerProcess The value of registerProcess as prefixSearch.
     */
    public void setRegisterProcess_PrefixSearch(String registerProcess) {
        regRegisterProcess(CK_PS, fRES(registerProcess));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param registerProcessList The collection of registerProcess as inScope.
     */
    public void setRegisterProcess_InScope(Collection<String> registerProcessList) {
        regRegisterProcess(CK_INS, cTL(registerProcessList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param registerProcessList The collection of registerProcess as notInScope.
     */
    public void setRegisterProcess_NotInScope(Collection<String> registerProcessList) {
        regRegisterProcess(CK_NINS, cTL(registerProcessList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param registerProcess The value of registerProcess as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setRegisterProcess_LikeSearch(String registerProcess, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(registerProcess), getCValueRegisterProcess(), "REGISTER_PROCESS", "RegisterProcess", "registerProcess", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param registerProcess The value of registerProcess as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setRegisterProcess_NotLikeSearch(String registerProcess, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(registerProcess), getCValueRegisterProcess(), "REGISTER_PROCESS", "RegisterProcess", "registerProcess", likeSearchOption);
    }

    protected void regRegisterProcess(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueRegisterProcess(), "REGISTER_PROCESS", "RegisterProcess", "registerProcess");
    }
    protected void registerInlineRegisterProcess(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueRegisterProcess(), "REGISTER_PROCESS", "RegisterProcess", "registerProcess");
    }
    abstract protected ConditionValue getCValueRegisterProcess();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param updateDatetime The value of updateDatetime as equal.
     */
    public void setUpdateDatetime_Equal(java.sql.Timestamp updateDatetime) {
        regUpdateDatetime(CK_EQ, updateDatetime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param updateDatetime The value of updateDatetime as greaterThan.
     */
    public void setUpdateDatetime_GreaterThan(java.sql.Timestamp updateDatetime) {
        regUpdateDatetime(CK_GT, updateDatetime);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param updateDatetime The value of updateDatetime as lessThan.
     */
    public void setUpdateDatetime_LessThan(java.sql.Timestamp updateDatetime) {
        regUpdateDatetime(CK_LT, updateDatetime);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param updateDatetime The value of updateDatetime as greaterEqual.
     */
    public void setUpdateDatetime_GreaterEqual(java.sql.Timestamp updateDatetime) {
        regUpdateDatetime(CK_GE, updateDatetime);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param updateDatetime The value of updateDatetime as lessEqual.
     */
    public void setUpdateDatetime_LessEqual(java.sql.Timestamp updateDatetime) {
        regUpdateDatetime(CK_LE, updateDatetime);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of updateDatetime. (Nullable)
     * @param toDate The to-date of updateDatetime. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setUpdateDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.seasar.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValueUpdateDatetime(), "UPDATE_DATETIME", "UpdateDatetime", "updateDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of updateDatetime. (Nullable)
     * @param toDate The to-date of updateDatetime. (Nullable)
     */
    public void setUpdateDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setUpdateDatetime_FromTo(fromDate, toDate, new org.seasar.dbflute.cbean.coption.DateFromToOption());
    }

    protected void regUpdateDatetime(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueUpdateDatetime(), "UPDATE_DATETIME", "UpdateDatetime", "updateDatetime");
    }
    protected void registerInlineUpdateDatetime(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueUpdateDatetime(), "UPDATE_DATETIME", "UpdateDatetime", "updateDatetime");
    }
    abstract protected ConditionValue getCValueUpdateDatetime();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(200)}
     * @param updateUser The value of updateUser as equal.
     */
    public void setUpdateUser_Equal(String updateUser) {
        regUpdateUser(CK_EQ, fRES(updateUser));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param updateUser The value of updateUser as notEqual.
     */
    public void setUpdateUser_NotEqual(String updateUser) {
        regUpdateUser(CK_NE, fRES(updateUser));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param updateUser The value of updateUser as prefixSearch.
     */
    public void setUpdateUser_PrefixSearch(String updateUser) {
        regUpdateUser(CK_PS, fRES(updateUser));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param updateUserList The collection of updateUser as inScope.
     */
    public void setUpdateUser_InScope(Collection<String> updateUserList) {
        regUpdateUser(CK_INS, cTL(updateUserList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param updateUserList The collection of updateUser as notInScope.
     */
    public void setUpdateUser_NotInScope(Collection<String> updateUserList) {
        regUpdateUser(CK_NINS, cTL(updateUserList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param updateUser The value of updateUser as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUpdateUser_LikeSearch(String updateUser, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(updateUser), getCValueUpdateUser(), "UPDATE_USER", "UpdateUser", "updateUser", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param updateUser The value of updateUser as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUpdateUser_NotLikeSearch(String updateUser, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(updateUser), getCValueUpdateUser(), "UPDATE_USER", "UpdateUser", "updateUser", likeSearchOption);
    }

    protected void regUpdateUser(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueUpdateUser(), "UPDATE_USER", "UpdateUser", "updateUser");
    }
    protected void registerInlineUpdateUser(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueUpdateUser(), "UPDATE_USER", "UpdateUser", "updateUser");
    }
    abstract protected ConditionValue getCValueUpdateUser();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(200)}
     * @param updateProcess The value of updateProcess as equal.
     */
    public void setUpdateProcess_Equal(String updateProcess) {
        regUpdateProcess(CK_EQ, fRES(updateProcess));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param updateProcess The value of updateProcess as notEqual.
     */
    public void setUpdateProcess_NotEqual(String updateProcess) {
        regUpdateProcess(CK_NE, fRES(updateProcess));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param updateProcess The value of updateProcess as prefixSearch.
     */
    public void setUpdateProcess_PrefixSearch(String updateProcess) {
        regUpdateProcess(CK_PS, fRES(updateProcess));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param updateProcessList The collection of updateProcess as inScope.
     */
    public void setUpdateProcess_InScope(Collection<String> updateProcessList) {
        regUpdateProcess(CK_INS, cTL(updateProcessList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param updateProcessList The collection of updateProcess as notInScope.
     */
    public void setUpdateProcess_NotInScope(Collection<String> updateProcessList) {
        regUpdateProcess(CK_NINS, cTL(updateProcessList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param updateProcess The value of updateProcess as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUpdateProcess_LikeSearch(String updateProcess, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(updateProcess), getCValueUpdateProcess(), "UPDATE_PROCESS", "UpdateProcess", "updateProcess", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param updateProcess The value of updateProcess as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUpdateProcess_NotLikeSearch(String updateProcess, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(updateProcess), getCValueUpdateProcess(), "UPDATE_PROCESS", "UpdateProcess", "updateProcess", likeSearchOption);
    }

    protected void regUpdateProcess(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueUpdateProcess(), "UPDATE_PROCESS", "UpdateProcess", "updateProcess");
    }
    protected void registerInlineUpdateProcess(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueUpdateProcess(), "UPDATE_PROCESS", "UpdateProcess", "updateProcess");
    }
    abstract protected ConditionValue getCValueUpdateProcess();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : BIGINT}
     * @param versionNo The value of versionNo as equal.
     */
    public void setVersionNo_Equal(Long versionNo) {
        regVersionNo(CK_EQ, versionNo);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param versionNo The value of versionNo as greaterThan.
     */
    public void setVersionNo_GreaterThan(Long versionNo) {
        regVersionNo(CK_GT, versionNo);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param versionNo The value of versionNo as lessThan.
     */
    public void setVersionNo_LessThan(Long versionNo) {
        regVersionNo(CK_LT, versionNo);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param versionNo The value of versionNo as greaterEqual.
     */
    public void setVersionNo_GreaterEqual(Long versionNo) {
        regVersionNo(CK_GE, versionNo);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param versionNo The value of versionNo as lessEqual.
     */
    public void setVersionNo_LessEqual(Long versionNo) {
        regVersionNo(CK_LE, versionNo);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param versionNoList The collection of versionNo as inScope.
     */
    public void setVersionNo_InScope(Collection<Long> versionNoList) {
        regVersionNo(CK_INS, cTL(versionNoList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param versionNoList The collection of versionNo as notInScope.
     */
    public void setVersionNo_NotInScope(Collection<Long> versionNoList) {
        regVersionNo(CK_NINS, cTL(versionNoList));
    }

    protected void regVersionNo(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueVersionNo(), "VERSION_NO", "VersionNo", "versionNo");
    }
    protected void registerInlineVersionNo(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueVersionNo(), "VERSION_NO", "VersionNo", "versionNo");
    }
    abstract protected ConditionValue getCValueVersionNo();

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public SSQFunction<ProductCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<ProductCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<ProductCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<ProductCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<ProductCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<ProductCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<ProductCB>(new SSQSetupper<ProductCB>() {
            public void setup(String function, SubQuery<ProductCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<ProductCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(ProductCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return ProductCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return ProductCQ.class.getName(); }
}
