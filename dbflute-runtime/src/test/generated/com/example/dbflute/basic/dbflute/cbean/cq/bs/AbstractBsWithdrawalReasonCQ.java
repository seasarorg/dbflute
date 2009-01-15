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
 * The abstract condition-query of WITHDRAWAL_REASON.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsWithdrawalReasonCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsWithdrawalReasonCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "WITHDRAWAL_REASON";
    }
    
    public String getTableSqlName() {
        return "WITHDRAWAL_REASON";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {PK : NotNull : CHAR(3)}
     * @param withdrawalReasonCode The value of withdrawalReasonCode as equal.
     */
    public void setWithdrawalReasonCode_Equal(String withdrawalReasonCode) {
        regWithdrawalReasonCode(CK_EQ, fRES(withdrawalReasonCode));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param withdrawalReasonCode The value of withdrawalReasonCode as notEqual.
     */
    public void setWithdrawalReasonCode_NotEqual(String withdrawalReasonCode) {
        regWithdrawalReasonCode(CK_NE, fRES(withdrawalReasonCode));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param withdrawalReasonCode The value of withdrawalReasonCode as prefixSearch.
     */
    public void setWithdrawalReasonCode_PrefixSearch(String withdrawalReasonCode) {
        regWithdrawalReasonCode(CK_PS, fRES(withdrawalReasonCode));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param withdrawalReasonCodeList The collection of withdrawalReasonCode as inScope.
     */
    public void setWithdrawalReasonCode_InScope(Collection<String> withdrawalReasonCodeList) {
        regWithdrawalReasonCode(CK_INS, cTL(withdrawalReasonCodeList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param withdrawalReasonCodeList The collection of withdrawalReasonCode as notInScope.
     */
    public void setWithdrawalReasonCode_NotInScope(Collection<String> withdrawalReasonCodeList) {
        regWithdrawalReasonCode(CK_NINS, cTL(withdrawalReasonCodeList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param withdrawalReasonCode The value of withdrawalReasonCode as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setWithdrawalReasonCode_LikeSearch(String withdrawalReasonCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(withdrawalReasonCode), getCValueWithdrawalReasonCode(), "WITHDRAWAL_REASON_CODE", "WithdrawalReasonCode", "withdrawalReasonCode", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param withdrawalReasonCode The value of withdrawalReasonCode as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setWithdrawalReasonCode_NotLikeSearch(String withdrawalReasonCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(withdrawalReasonCode), getCValueWithdrawalReasonCode(), "WITHDRAWAL_REASON_CODE", "WithdrawalReasonCode", "withdrawalReasonCode", likeSearchOption);
    }

    public void inScopeMemberWithdrawalList(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepWithdrawalReasonCode_InScopeSubQuery_MemberWithdrawalList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "WITHDRAWAL_REASON_CODE", "WITHDRAWAL_REASON_CODE", subQueryPropertyName);
    }
    public abstract String keepWithdrawalReasonCode_InScopeSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery);

    public void notInScopeMemberWithdrawalList(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepWithdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "WITHDRAWAL_REASON_CODE", "WITHDRAWAL_REASON_CODE", subQueryPropertyName);
    }
    public abstract String keepWithdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select WITHDRAWAL_REASON_CODE from MEMBER_WITHDRAWAL where ...)}
     * @param subQuery The sub-query of WithdrawalReasonCode_ExistsSubQuery_MemberWithdrawalList for 'exists'. (NotNull)
     */
    public void existsMemberWithdrawalList(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepWithdrawalReasonCode_ExistsSubQuery_MemberWithdrawalList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "WITHDRAWAL_REASON_CODE", "WITHDRAWAL_REASON_CODE", subQueryPropertyName);
    }
    public abstract String keepWithdrawalReasonCode_ExistsSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select WITHDRAWAL_REASON_CODE from MEMBER_WITHDRAWAL where ...)}
     * @param subQuery The sub-query of WithdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalList for 'not exists'. (NotNull)
     */
    public void notExistsMemberWithdrawalList(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepWithdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "WITHDRAWAL_REASON_CODE", "WITHDRAWAL_REASON_CODE", subQueryPropertyName);
    }
    public abstract String keepWithdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery);

    public void xsderiveMemberWithdrawalList(String function, SubQuery<MemberWithdrawalCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepWithdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "WITHDRAWAL_REASON_CODE", "WITHDRAWAL_REASON_CODE", subQueryPropertyName, aliasName);
    }
    public abstract String keepWithdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalList(MemberWithdrawalCQ subQuery);

    public QDRFunction<MemberWithdrawalCB> derivedMemberWithdrawalList() {
        return xcreateQDRFunctionMemberWithdrawalList();
    }
    protected QDRFunction<MemberWithdrawalCB> xcreateQDRFunctionMemberWithdrawalList() {
        return new QDRFunction<MemberWithdrawalCB>(new QDRSetupper<MemberWithdrawalCB>() {
            public void setup(String function, SubQuery<MemberWithdrawalCB> subQuery, String operand, Object value) {
                xqderiveMemberWithdrawalList(function, subQuery, operand, value);
            }
        });
    }
    public void xqderiveMemberWithdrawalList(String function, SubQuery<MemberWithdrawalCB> subQuery, String operand, Object value) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "WITHDRAWAL_REASON_CODE", "WITHDRAWAL_REASON_CODE", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalList(MemberWithdrawalCQ subQuery);
    public abstract String keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameter(Object parameterValue);

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setWithdrawalReasonCode_IsNull() { regWithdrawalReasonCode(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setWithdrawalReasonCode_IsNotNull() { regWithdrawalReasonCode(CK_ISNN, DUMMY_OBJECT); }

    protected void regWithdrawalReasonCode(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueWithdrawalReasonCode(), "WITHDRAWAL_REASON_CODE", "WithdrawalReasonCode", "withdrawalReasonCode");
    }
    protected void registerInlineWithdrawalReasonCode(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueWithdrawalReasonCode(), "WITHDRAWAL_REASON_CODE", "WithdrawalReasonCode", "withdrawalReasonCode");
    }
    abstract protected ConditionValue getCValueWithdrawalReasonCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : CLOB}
     * @param withdrawalReasonText The value of withdrawalReasonText as equal.
     */
    public void setWithdrawalReasonText_Equal(String withdrawalReasonText) {
        regWithdrawalReasonText(CK_EQ, fRES(withdrawalReasonText));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param withdrawalReasonText The value of withdrawalReasonText as notEqual.
     */
    public void setWithdrawalReasonText_NotEqual(String withdrawalReasonText) {
        regWithdrawalReasonText(CK_NE, fRES(withdrawalReasonText));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param withdrawalReasonText The value of withdrawalReasonText as prefixSearch.
     */
    public void setWithdrawalReasonText_PrefixSearch(String withdrawalReasonText) {
        regWithdrawalReasonText(CK_PS, fRES(withdrawalReasonText));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param withdrawalReasonTextList The collection of withdrawalReasonText as inScope.
     */
    public void setWithdrawalReasonText_InScope(Collection<String> withdrawalReasonTextList) {
        regWithdrawalReasonText(CK_INS, cTL(withdrawalReasonTextList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param withdrawalReasonTextList The collection of withdrawalReasonText as notInScope.
     */
    public void setWithdrawalReasonText_NotInScope(Collection<String> withdrawalReasonTextList) {
        regWithdrawalReasonText(CK_NINS, cTL(withdrawalReasonTextList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param withdrawalReasonText The value of withdrawalReasonText as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setWithdrawalReasonText_LikeSearch(String withdrawalReasonText, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(withdrawalReasonText), getCValueWithdrawalReasonText(), "WITHDRAWAL_REASON_TEXT", "WithdrawalReasonText", "withdrawalReasonText", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param withdrawalReasonText The value of withdrawalReasonText as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setWithdrawalReasonText_NotLikeSearch(String withdrawalReasonText, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(withdrawalReasonText), getCValueWithdrawalReasonText(), "WITHDRAWAL_REASON_TEXT", "WithdrawalReasonText", "withdrawalReasonText", likeSearchOption);
    }

    protected void regWithdrawalReasonText(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueWithdrawalReasonText(), "WITHDRAWAL_REASON_TEXT", "WithdrawalReasonText", "withdrawalReasonText");
    }
    protected void registerInlineWithdrawalReasonText(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueWithdrawalReasonText(), "WITHDRAWAL_REASON_TEXT", "WithdrawalReasonText", "withdrawalReasonText");
    }
    abstract protected ConditionValue getCValueWithdrawalReasonText();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : INTEGER}
     * @param displayOrder The value of displayOrder as equal.
     */
    public void setDisplayOrder_Equal(Integer displayOrder) {
        regDisplayOrder(CK_EQ, displayOrder);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param displayOrder The value of displayOrder as greaterThan.
     */
    public void setDisplayOrder_GreaterThan(Integer displayOrder) {
        regDisplayOrder(CK_GT, displayOrder);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param displayOrder The value of displayOrder as lessThan.
     */
    public void setDisplayOrder_LessThan(Integer displayOrder) {
        regDisplayOrder(CK_LT, displayOrder);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param displayOrder The value of displayOrder as greaterEqual.
     */
    public void setDisplayOrder_GreaterEqual(Integer displayOrder) {
        regDisplayOrder(CK_GE, displayOrder);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param displayOrder The value of displayOrder as lessEqual.
     */
    public void setDisplayOrder_LessEqual(Integer displayOrder) {
        regDisplayOrder(CK_LE, displayOrder);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param displayOrderList The collection of displayOrder as inScope.
     */
    public void setDisplayOrder_InScope(Collection<Integer> displayOrderList) {
        regDisplayOrder(CK_INS, cTL(displayOrderList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param displayOrderList The collection of displayOrder as notInScope.
     */
    public void setDisplayOrder_NotInScope(Collection<Integer> displayOrderList) {
        regDisplayOrder(CK_NINS, cTL(displayOrderList));
    }

    protected void regDisplayOrder(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueDisplayOrder(), "DISPLAY_ORDER", "DisplayOrder", "displayOrder");
    }
    protected void registerInlineDisplayOrder(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueDisplayOrder(), "DISPLAY_ORDER", "DisplayOrder", "displayOrder");
    }
    abstract protected ConditionValue getCValueDisplayOrder();

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public SSQFunction<WithdrawalReasonCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<WithdrawalReasonCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<WithdrawalReasonCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<WithdrawalReasonCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<WithdrawalReasonCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<WithdrawalReasonCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<WithdrawalReasonCB>(new SSQSetupper<WithdrawalReasonCB>() {
            public void setup(String function, SubQuery<WithdrawalReasonCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<WithdrawalReasonCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<WithdrawalReasonCB>", subQuery);
        WithdrawalReasonCB cb = new WithdrawalReasonCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(WithdrawalReasonCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return WithdrawalReasonCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return WithdrawalReasonCQ.class.getName(); }
}
