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
 * The abstract condition-query of VENDOR_CHECK.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsVendorCheckCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsVendorCheckCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "VENDOR_CHECK";
    }
    
    public String getTableSqlName() {
        return "VENDOR_CHECK";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : NotNull : DECIMAL(16)}
     * @param vendorCheckId The value of vendorCheckId as equal.
     */
    public void setVendorCheckId_Equal(Long vendorCheckId) {
        regVendorCheckId(CK_EQ, vendorCheckId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param vendorCheckId The value of vendorCheckId as greaterThan.
     */
    public void setVendorCheckId_GreaterThan(Long vendorCheckId) {
        regVendorCheckId(CK_GT, vendorCheckId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param vendorCheckId The value of vendorCheckId as lessThan.
     */
    public void setVendorCheckId_LessThan(Long vendorCheckId) {
        regVendorCheckId(CK_LT, vendorCheckId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param vendorCheckId The value of vendorCheckId as greaterEqual.
     */
    public void setVendorCheckId_GreaterEqual(Long vendorCheckId) {
        regVendorCheckId(CK_GE, vendorCheckId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param vendorCheckId The value of vendorCheckId as lessEqual.
     */
    public void setVendorCheckId_LessEqual(Long vendorCheckId) {
        regVendorCheckId(CK_LE, vendorCheckId);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param vendorCheckIdList The collection of vendorCheckId as inScope.
     */
    public void setVendorCheckId_InScope(Collection<Long> vendorCheckIdList) {
        regVendorCheckId(CK_INS, cTL(vendorCheckIdList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param vendorCheckIdList The collection of vendorCheckId as notInScope.
     */
    public void setVendorCheckId_NotInScope(Collection<Long> vendorCheckIdList) {
        regVendorCheckId(CK_NINS, cTL(vendorCheckIdList));
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setVendorCheckId_IsNull() { regVendorCheckId(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setVendorCheckId_IsNotNull() { regVendorCheckId(CK_ISNN, DUMMY_OBJECT); }

    protected void regVendorCheckId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueVendorCheckId(), "VENDOR_CHECK_ID", "VendorCheckId", "vendorCheckId");
    }
    protected void registerInlineVendorCheckId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueVendorCheckId(), "VENDOR_CHECK_ID", "VendorCheckId", "vendorCheckId");
    }
    abstract protected ConditionValue getCValueVendorCheckId();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : DECIMAL(5, 3)}
     * @param decimalDigit The value of decimalDigit as equal.
     */
    public void setDecimalDigit_Equal(java.math.BigDecimal decimalDigit) {
        regDecimalDigit(CK_EQ, decimalDigit);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param decimalDigit The value of decimalDigit as greaterThan.
     */
    public void setDecimalDigit_GreaterThan(java.math.BigDecimal decimalDigit) {
        regDecimalDigit(CK_GT, decimalDigit);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param decimalDigit The value of decimalDigit as lessThan.
     */
    public void setDecimalDigit_LessThan(java.math.BigDecimal decimalDigit) {
        regDecimalDigit(CK_LT, decimalDigit);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param decimalDigit The value of decimalDigit as greaterEqual.
     */
    public void setDecimalDigit_GreaterEqual(java.math.BigDecimal decimalDigit) {
        regDecimalDigit(CK_GE, decimalDigit);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param decimalDigit The value of decimalDigit as lessEqual.
     */
    public void setDecimalDigit_LessEqual(java.math.BigDecimal decimalDigit) {
        regDecimalDigit(CK_LE, decimalDigit);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param decimalDigitList The collection of decimalDigit as inScope.
     */
    public void setDecimalDigit_InScope(Collection<java.math.BigDecimal> decimalDigitList) {
        regDecimalDigit(CK_INS, cTL(decimalDigitList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param decimalDigitList The collection of decimalDigit as notInScope.
     */
    public void setDecimalDigit_NotInScope(Collection<java.math.BigDecimal> decimalDigitList) {
        regDecimalDigit(CK_NINS, cTL(decimalDigitList));
    }

    protected void regDecimalDigit(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueDecimalDigit(), "DECIMAL_DIGIT", "DecimalDigit", "decimalDigit");
    }
    protected void registerInlineDecimalDigit(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueDecimalDigit(), "DECIMAL_DIGIT", "DecimalDigit", "decimalDigit");
    }
    abstract protected ConditionValue getCValueDecimalDigit();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : DECIMAL(5)}
     * @param integerNonDigit The value of integerNonDigit as equal.
     */
    public void setIntegerNonDigit_Equal(Integer integerNonDigit) {
        regIntegerNonDigit(CK_EQ, integerNonDigit);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param integerNonDigit The value of integerNonDigit as greaterThan.
     */
    public void setIntegerNonDigit_GreaterThan(Integer integerNonDigit) {
        regIntegerNonDigit(CK_GT, integerNonDigit);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param integerNonDigit The value of integerNonDigit as lessThan.
     */
    public void setIntegerNonDigit_LessThan(Integer integerNonDigit) {
        regIntegerNonDigit(CK_LT, integerNonDigit);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param integerNonDigit The value of integerNonDigit as greaterEqual.
     */
    public void setIntegerNonDigit_GreaterEqual(Integer integerNonDigit) {
        regIntegerNonDigit(CK_GE, integerNonDigit);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param integerNonDigit The value of integerNonDigit as lessEqual.
     */
    public void setIntegerNonDigit_LessEqual(Integer integerNonDigit) {
        regIntegerNonDigit(CK_LE, integerNonDigit);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param integerNonDigitList The collection of integerNonDigit as inScope.
     */
    public void setIntegerNonDigit_InScope(Collection<Integer> integerNonDigitList) {
        regIntegerNonDigit(CK_INS, cTL(integerNonDigitList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param integerNonDigitList The collection of integerNonDigit as notInScope.
     */
    public void setIntegerNonDigit_NotInScope(Collection<Integer> integerNonDigitList) {
        regIntegerNonDigit(CK_NINS, cTL(integerNonDigitList));
    }

    protected void regIntegerNonDigit(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueIntegerNonDigit(), "INTEGER_NON_DIGIT", "IntegerNonDigit", "integerNonDigit");
    }
    protected void registerInlineIntegerNonDigit(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueIntegerNonDigit(), "INTEGER_NON_DIGIT", "IntegerNonDigit", "integerNonDigit");
    }
    abstract protected ConditionValue getCValueIntegerNonDigit();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : BOOLEAN}
     * @param typeOfBoolean The value of typeOfBoolean as equal.
     */
    public void setTypeOfBoolean_Equal(Boolean typeOfBoolean) {
        regTypeOfBoolean(CK_EQ, typeOfBoolean);
    }

    protected void regTypeOfBoolean(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueTypeOfBoolean(), "TYPE_OF_BOOLEAN", "TypeOfBoolean", "typeOfBoolean");
    }
    protected void registerInlineTypeOfBoolean(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueTypeOfBoolean(), "TYPE_OF_BOOLEAN", "TypeOfBoolean", "typeOfBoolean");
    }
    abstract protected ConditionValue getCValueTypeOfBoolean();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {CLOB}
     * @param typeOfText The value of typeOfText as equal.
     */
    public void setTypeOfText_Equal(String typeOfText) {
        regTypeOfText(CK_EQ, fRES(typeOfText));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param typeOfText The value of typeOfText as notEqual.
     */
    public void setTypeOfText_NotEqual(String typeOfText) {
        regTypeOfText(CK_NE, fRES(typeOfText));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param typeOfText The value of typeOfText as prefixSearch.
     */
    public void setTypeOfText_PrefixSearch(String typeOfText) {
        regTypeOfText(CK_PS, fRES(typeOfText));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param typeOfTextList The collection of typeOfText as inScope.
     */
    public void setTypeOfText_InScope(Collection<String> typeOfTextList) {
        regTypeOfText(CK_INS, cTL(typeOfTextList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param typeOfTextList The collection of typeOfText as notInScope.
     */
    public void setTypeOfText_NotInScope(Collection<String> typeOfTextList) {
        regTypeOfText(CK_NINS, cTL(typeOfTextList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param typeOfText The value of typeOfText as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setTypeOfText_LikeSearch(String typeOfText, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(typeOfText), getCValueTypeOfText(), "TYPE_OF_TEXT", "TypeOfText", "typeOfText", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param typeOfText The value of typeOfText as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setTypeOfText_NotLikeSearch(String typeOfText, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(typeOfText), getCValueTypeOfText(), "TYPE_OF_TEXT", "TypeOfText", "typeOfText", likeSearchOption);
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setTypeOfText_IsNull() { regTypeOfText(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setTypeOfText_IsNotNull() { regTypeOfText(CK_ISNN, DUMMY_OBJECT); }

    protected void regTypeOfText(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueTypeOfText(), "TYPE_OF_TEXT", "TypeOfText", "typeOfText");
    }
    protected void registerInlineTypeOfText(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueTypeOfText(), "TYPE_OF_TEXT", "TypeOfText", "typeOfText");
    }
    abstract protected ConditionValue getCValueTypeOfText();

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public SSQFunction<VendorCheckCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<VendorCheckCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<VendorCheckCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<VendorCheckCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<VendorCheckCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<VendorCheckCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<VendorCheckCB>(new SSQSetupper<VendorCheckCB>() {
            public void setup(String function, SubQuery<VendorCheckCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<VendorCheckCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<VendorCheckCB>", subQuery);
        VendorCheckCB cb = new VendorCheckCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(VendorCheckCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return VendorCheckCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return VendorCheckCQ.class.getName(); }
}
