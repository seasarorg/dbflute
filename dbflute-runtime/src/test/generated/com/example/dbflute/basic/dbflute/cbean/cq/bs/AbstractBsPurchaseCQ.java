package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Collection;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.ckey.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

import com.example.dbflute.basic.dbflute.allcommon.CDef;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The abstract condition-query of PURCHASE.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsPurchaseCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsPurchaseCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "PURCHASE";
    }
    
    public String getTableSqlName() {
        return "PURCHASE";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : ID : NotNull : BIGINT}
     * @param purchaseId The value of purchaseId as equal.
     */
    public void setPurchaseId_Equal(Long purchaseId) {
        regPurchaseId(CK_EQ, purchaseId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseId The value of purchaseId as greaterThan.
     */
    public void setPurchaseId_GreaterThan(Long purchaseId) {
        regPurchaseId(CK_GT, purchaseId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseId The value of purchaseId as lessThan.
     */
    public void setPurchaseId_LessThan(Long purchaseId) {
        regPurchaseId(CK_LT, purchaseId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseId The value of purchaseId as greaterEqual.
     */
    public void setPurchaseId_GreaterEqual(Long purchaseId) {
        regPurchaseId(CK_GE, purchaseId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseId The value of purchaseId as lessEqual.
     */
    public void setPurchaseId_LessEqual(Long purchaseId) {
        regPurchaseId(CK_LE, purchaseId);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param purchaseIdList The collection of purchaseId as inScope.
     */
    public void setPurchaseId_InScope(Collection<Long> purchaseIdList) {
        regPurchaseId(CK_INS, cTL(purchaseIdList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param purchaseIdList The collection of purchaseId as notInScope.
     */
    public void setPurchaseId_NotInScope(Collection<Long> purchaseIdList) {
        regPurchaseId(CK_NINS, cTL(purchaseIdList));
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setPurchaseId_IsNull() { regPurchaseId(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setPurchaseId_IsNotNull() { regPurchaseId(CK_ISNN, DUMMY_OBJECT); }

    protected void regPurchaseId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValuePurchaseId(), "PURCHASE_ID", "PurchaseId", "purchaseId");
    }
    protected void registerInlinePurchaseId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValuePurchaseId(), "PURCHASE_ID", "PurchaseId", "purchaseId");
    }
    abstract protected ConditionValue getCValuePurchaseId();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : INTEGER : FK to MEMBER}
     * @param memberId The value of memberId as equal.
     */
    public void setMemberId_Equal(Integer memberId) {
        regMemberId(CK_EQ, memberId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberId The value of memberId as greaterThan.
     */
    public void setMemberId_GreaterThan(Integer memberId) {
        regMemberId(CK_GT, memberId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberId The value of memberId as lessThan.
     */
    public void setMemberId_LessThan(Integer memberId) {
        regMemberId(CK_LT, memberId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param memberId The value of memberId as greaterEqual.
     */
    public void setMemberId_GreaterEqual(Integer memberId) {
        regMemberId(CK_GE, memberId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param memberId The value of memberId as lessEqual.
     */
    public void setMemberId_LessEqual(Integer memberId) {
        regMemberId(CK_LE, memberId);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param memberIdList The collection of memberId as inScope.
     */
    public void setMemberId_InScope(Collection<Integer> memberIdList) {
        regMemberId(CK_INS, cTL(memberIdList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param memberIdList The collection of memberId as notInScope.
     */
    public void setMemberId_NotInScope(Collection<Integer> memberIdList) {
        regMemberId(CK_NINS, cTL(memberIdList));
    }

    public void inScopeMember(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_InScopeSubQuery_Member(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_InScopeSubQuery_Member(MemberCQ subQuery);

    protected void regMemberId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberId(), "MEMBER_ID", "MemberId", "memberId");
    }
    protected void registerInlineMemberId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberId(), "MEMBER_ID", "MemberId", "memberId");
    }
    abstract protected ConditionValue getCValueMemberId();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : INTEGER : FK to PRODUCT}
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

    public void inScopeProduct(SubQuery<ProductCB> subQuery) {
        assertObjectNotNull("subQuery<ProductCB>", subQuery);
        ProductCB cb = new ProductCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepProductId_InScopeSubQuery_Product(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "PRODUCT_ID", "PRODUCT_ID", subQueryPropertyName);
    }
    public abstract String keepProductId_InScopeSubQuery_Product(ProductCQ subQuery);

    protected void regProductId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueProductId(), "PRODUCT_ID", "ProductId", "productId");
    }
    protected void registerInlineProductId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueProductId(), "PRODUCT_ID", "ProductId", "productId");
    }
    abstract protected ConditionValue getCValueProductId();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : TIMESTAMP}
     * @param purchaseDatetime The value of purchaseDatetime as equal.
     */
    public void setPurchaseDatetime_Equal(java.sql.Timestamp purchaseDatetime) {
        regPurchaseDatetime(CK_EQ, purchaseDatetime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseDatetime The value of purchaseDatetime as greaterThan.
     */
    public void setPurchaseDatetime_GreaterThan(java.sql.Timestamp purchaseDatetime) {
        regPurchaseDatetime(CK_GT, purchaseDatetime);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseDatetime The value of purchaseDatetime as lessThan.
     */
    public void setPurchaseDatetime_LessThan(java.sql.Timestamp purchaseDatetime) {
        regPurchaseDatetime(CK_LT, purchaseDatetime);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseDatetime The value of purchaseDatetime as greaterEqual.
     */
    public void setPurchaseDatetime_GreaterEqual(java.sql.Timestamp purchaseDatetime) {
        regPurchaseDatetime(CK_GE, purchaseDatetime);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseDatetime The value of purchaseDatetime as lessEqual.
     */
    public void setPurchaseDatetime_LessEqual(java.sql.Timestamp purchaseDatetime) {
        regPurchaseDatetime(CK_LE, purchaseDatetime);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : TIMESTAMP}
     * @param fromDate The from-date of purchaseDatetime. (Nullable)
     * @param toDate The to-date of purchaseDatetime. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setPurchaseDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.seasar.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValuePurchaseDatetime(), "PURCHASE_DATETIME", "PurchaseDatetime", "purchaseDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : TIMESTAMP}
     * @param fromDate The from-date of purchaseDatetime. (Nullable)
     * @param toDate The to-date of purchaseDatetime. (Nullable)
     */
    public void setPurchaseDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setPurchaseDatetime_FromTo(fromDate, toDate, new org.seasar.dbflute.cbean.coption.DateFromToOption());
    }

    protected void regPurchaseDatetime(ConditionKey key, Object value) {
        registerQuery(key, value, getCValuePurchaseDatetime(), "PURCHASE_DATETIME", "PurchaseDatetime", "purchaseDatetime");
    }
    protected void registerInlinePurchaseDatetime(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValuePurchaseDatetime(), "PURCHASE_DATETIME", "PurchaseDatetime", "purchaseDatetime");
    }
    abstract protected ConditionValue getCValuePurchaseDatetime();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : INTEGER}
     * @param purchaseCount The value of purchaseCount as equal.
     */
    public void setPurchaseCount_Equal(Integer purchaseCount) {
        regPurchaseCount(CK_EQ, purchaseCount);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseCount The value of purchaseCount as greaterThan.
     */
    public void setPurchaseCount_GreaterThan(Integer purchaseCount) {
        regPurchaseCount(CK_GT, purchaseCount);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseCount The value of purchaseCount as lessThan.
     */
    public void setPurchaseCount_LessThan(Integer purchaseCount) {
        regPurchaseCount(CK_LT, purchaseCount);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseCount The value of purchaseCount as greaterEqual.
     */
    public void setPurchaseCount_GreaterEqual(Integer purchaseCount) {
        regPurchaseCount(CK_GE, purchaseCount);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param purchaseCount The value of purchaseCount as lessEqual.
     */
    public void setPurchaseCount_LessEqual(Integer purchaseCount) {
        regPurchaseCount(CK_LE, purchaseCount);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param purchaseCountList The collection of purchaseCount as inScope.
     */
    public void setPurchaseCount_InScope(Collection<Integer> purchaseCountList) {
        regPurchaseCount(CK_INS, cTL(purchaseCountList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param purchaseCountList The collection of purchaseCount as notInScope.
     */
    public void setPurchaseCount_NotInScope(Collection<Integer> purchaseCountList) {
        regPurchaseCount(CK_NINS, cTL(purchaseCountList));
    }

    protected void regPurchaseCount(ConditionKey key, Object value) {
        registerQuery(key, value, getCValuePurchaseCount(), "PURCHASE_COUNT", "PurchaseCount", "purchaseCount");
    }
    protected void registerInlinePurchaseCount(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValuePurchaseCount(), "PURCHASE_COUNT", "PurchaseCount", "purchaseCount");
    }
    abstract protected ConditionValue getCValuePurchaseCount();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : INTEGER}
     * @param purchasePrice The value of purchasePrice as equal.
     */
    public void setPurchasePrice_Equal(Integer purchasePrice) {
        regPurchasePrice(CK_EQ, purchasePrice);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchasePrice The value of purchasePrice as greaterThan.
     */
    public void setPurchasePrice_GreaterThan(Integer purchasePrice) {
        regPurchasePrice(CK_GT, purchasePrice);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param purchasePrice The value of purchasePrice as lessThan.
     */
    public void setPurchasePrice_LessThan(Integer purchasePrice) {
        regPurchasePrice(CK_LT, purchasePrice);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param purchasePrice The value of purchasePrice as greaterEqual.
     */
    public void setPurchasePrice_GreaterEqual(Integer purchasePrice) {
        regPurchasePrice(CK_GE, purchasePrice);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param purchasePrice The value of purchasePrice as lessEqual.
     */
    public void setPurchasePrice_LessEqual(Integer purchasePrice) {
        regPurchasePrice(CK_LE, purchasePrice);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param purchasePriceList The collection of purchasePrice as inScope.
     */
    public void setPurchasePrice_InScope(Collection<Integer> purchasePriceList) {
        regPurchasePrice(CK_INS, cTL(purchasePriceList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param purchasePriceList The collection of purchasePrice as notInScope.
     */
    public void setPurchasePrice_NotInScope(Collection<Integer> purchasePriceList) {
        regPurchasePrice(CK_NINS, cTL(purchasePriceList));
    }

    protected void regPurchasePrice(ConditionKey key, Object value) {
        registerQuery(key, value, getCValuePurchasePrice(), "PURCHASE_PRICE", "PurchasePrice", "purchasePrice");
    }
    protected void registerInlinePurchasePrice(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValuePurchasePrice(), "PURCHASE_PRICE", "PurchasePrice", "purchasePrice");
    }
    abstract protected ConditionValue getCValuePurchasePrice();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : INTEGER}
     * @param paymentCompleteFlg The value of paymentCompleteFlg as equal.
     */
    public void setPaymentCompleteFlg_Equal(Integer paymentCompleteFlg) {
        regPaymentCompleteFlg(CK_EQ, paymentCompleteFlg);
    }

    /**
     * Equal(=). As True. And NullIgnored, OnlyOnceRegistered. <br />
     * はい: 有効を示す
     */
    public void setPaymentCompleteFlg_Equal_True() {
        regPaymentCompleteFlg(CK_EQ, new Integer(CDef.Flg.True.code()));
    }

    /**
     * Equal(=). As False. And NullIgnored, OnlyOnceRegistered. <br />
     * いいえ: 無効を示す
     */
    public void setPaymentCompleteFlg_Equal_False() {
        regPaymentCompleteFlg(CK_EQ, new Integer(CDef.Flg.False.code()));
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param paymentCompleteFlgList The collection of paymentCompleteFlg as inScope.
     */
    public void setPaymentCompleteFlg_InScope(Collection<Integer> paymentCompleteFlgList) {
        regPaymentCompleteFlg(CK_INS, cTL(paymentCompleteFlgList));
    }

    protected void regPaymentCompleteFlg(ConditionKey key, Object value) {
        registerQuery(key, value, getCValuePaymentCompleteFlg(), "PAYMENT_COMPLETE_FLG", "PaymentCompleteFlg", "paymentCompleteFlg");
    }
    protected void registerInlinePaymentCompleteFlg(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValuePaymentCompleteFlg(), "PAYMENT_COMPLETE_FLG", "PaymentCompleteFlg", "paymentCompleteFlg");
    }
    abstract protected ConditionValue getCValuePaymentCompleteFlg();
    
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
    public SSQFunction<PurchaseCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<PurchaseCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<PurchaseCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<PurchaseCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<PurchaseCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<PurchaseCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<PurchaseCB>(new SSQSetupper<PurchaseCB>() {
            public void setup(String function, SubQuery<PurchaseCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<PurchaseCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(PurchaseCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return PurchaseCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return PurchaseCQ.class.getName(); }
}
