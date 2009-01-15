package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Collection;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.ckey.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.allcommon.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The abstract condition-query of MEMBER.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsMemberCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsMemberCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "MEMBER";
    }
    
    public String getTableSqlName() {
        return "MEMBER";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : ID : NotNull : INTEGER : FK to MEMBER_ADDRESS}
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

    public void inScopeMemberAddressList(SubQuery<MemberAddressCB> subQuery) {
        assertObjectNotNull("subQuery<MemberAddressCB>", subQuery);
        MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_InScopeSubQuery_MemberAddressList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_InScopeSubQuery_MemberAddressList(MemberAddressCQ subQuery);

    public void inScopeMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_InScopeSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_InScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    public void inScopeMemberSecurityAsOne(SubQuery<MemberSecurityCB> subQuery) {
        assertObjectNotNull("subQuery<MemberSecurityCB>", subQuery);
        MemberSecurityCB cb = new MemberSecurityCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_InScopeSubQuery_MemberSecurityAsOne(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_InScopeSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery);

    public void inScopeMemberWithdrawalAsOne(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_InScopeSubQuery_MemberWithdrawalAsOne(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_InScopeSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery);

    public void inScopePurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_InScopeSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_InScopeSubQuery_PurchaseList(PurchaseCQ subQuery);

    public void notInScopeMemberAddressList(SubQuery<MemberAddressCB> subQuery) {
        assertObjectNotNull("subQuery<MemberAddressCB>", subQuery);
        MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotInScopeSubQuery_MemberAddressList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotInScopeSubQuery_MemberAddressList(MemberAddressCQ subQuery);

    public void notInScopeMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotInScopeSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotInScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    public void notInScopeMemberSecurityAsOne(SubQuery<MemberSecurityCB> subQuery) {
        assertObjectNotNull("subQuery<MemberSecurityCB>", subQuery);
        MemberSecurityCB cb = new MemberSecurityCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotInScopeSubQuery_MemberSecurityAsOne(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotInScopeSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery);

    public void notInScopeMemberWithdrawalAsOne(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotInScopeSubQuery_MemberWithdrawalAsOne(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotInScopeSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery);

    public void notInScopePurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotInScopeSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotInScopeSubQuery_PurchaseList(PurchaseCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select MEMBER_ID from MEMBER_ADDRESS where ...)}
     * @param subQuery The sub-query of MemberId_ExistsSubQuery_MemberAddressList for 'exists'. (NotNull)
     */
    public void existsMemberAddressList(SubQuery<MemberAddressCB> subQuery) {
        assertObjectNotNull("subQuery<MemberAddressCB>", subQuery);
        MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_ExistsSubQuery_MemberAddressList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_ExistsSubQuery_MemberAddressList(MemberAddressCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select MEMBER_ID from MEMBER_LOGIN where ...)}
     * @param subQuery The sub-query of MemberId_ExistsSubQuery_MemberLoginList for 'exists'. (NotNull)
     */
    public void existsMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_ExistsSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_ExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select MEMBER_ID from MEMBER_SECURITY where ...)}
     * @param subQuery The sub-query of MemberId_ExistsSubQuery_MemberSecurityAsOne for 'exists'. (NotNull)
     */
    public void existsMemberSecurityAsOne(SubQuery<MemberSecurityCB> subQuery) {
        assertObjectNotNull("subQuery<MemberSecurityCB>", subQuery);
        MemberSecurityCB cb = new MemberSecurityCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_ExistsSubQuery_MemberSecurityAsOne(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_ExistsSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select MEMBER_ID from MEMBER_WITHDRAWAL where ...)}
     * @param subQuery The sub-query of MemberId_ExistsSubQuery_MemberWithdrawalAsOne for 'exists'. (NotNull)
     */
    public void existsMemberWithdrawalAsOne(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_ExistsSubQuery_MemberWithdrawalAsOne(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_ExistsSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select MEMBER_ID from PURCHASE where ...)}
     * @param subQuery The sub-query of MemberId_ExistsSubQuery_PurchaseList for 'exists'. (NotNull)
     */
    public void existsPurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_ExistsSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_ExistsSubQuery_PurchaseList(PurchaseCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select MEMBER_ID from MEMBER_ADDRESS where ...)}
     * @param subQuery The sub-query of MemberId_NotExistsSubQuery_MemberAddressList for 'not exists'. (NotNull)
     */
    public void notExistsMemberAddressList(SubQuery<MemberAddressCB> subQuery) {
        assertObjectNotNull("subQuery<MemberAddressCB>", subQuery);
        MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotExistsSubQuery_MemberAddressList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotExistsSubQuery_MemberAddressList(MemberAddressCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select MEMBER_ID from MEMBER_LOGIN where ...)}
     * @param subQuery The sub-query of MemberId_NotExistsSubQuery_MemberLoginList for 'not exists'. (NotNull)
     */
    public void notExistsMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotExistsSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select MEMBER_ID from MEMBER_SECURITY where ...)}
     * @param subQuery The sub-query of MemberId_NotExistsSubQuery_MemberSecurityAsOne for 'not exists'. (NotNull)
     */
    public void notExistsMemberSecurityAsOne(SubQuery<MemberSecurityCB> subQuery) {
        assertObjectNotNull("subQuery<MemberSecurityCB>", subQuery);
        MemberSecurityCB cb = new MemberSecurityCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotExistsSubQuery_MemberSecurityAsOne(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotExistsSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select MEMBER_ID from MEMBER_WITHDRAWAL where ...)}
     * @param subQuery The sub-query of MemberId_NotExistsSubQuery_MemberWithdrawalAsOne for 'not exists'. (NotNull)
     */
    public void notExistsMemberWithdrawalAsOne(SubQuery<MemberWithdrawalCB> subQuery) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotExistsSubQuery_MemberWithdrawalAsOne(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotExistsSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select MEMBER_ID from PURCHASE where ...)}
     * @param subQuery The sub-query of MemberId_NotExistsSubQuery_PurchaseList for 'not exists'. (NotNull)
     */
    public void notExistsPurchaseList(SubQuery<PurchaseCB> subQuery) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotExistsSubQuery_PurchaseList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotExistsSubQuery_PurchaseList(PurchaseCQ subQuery);

    public void xsderiveMemberAddressList(String function, SubQuery<MemberAddressCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<MemberAddressCB>", subQuery);
        MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_SpecifyDerivedReferrer_MemberAddressList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName, aliasName);
    }
    public abstract String keepMemberId_SpecifyDerivedReferrer_MemberAddressList(MemberAddressCQ subQuery);

    public void xsderiveMemberLoginList(String function, SubQuery<MemberLoginCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_SpecifyDerivedReferrer_MemberLoginList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName, aliasName);
    }
    public abstract String keepMemberId_SpecifyDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery);

    public void xsderivePurchaseList(String function, SubQuery<PurchaseCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<PurchaseCB>", subQuery);
        PurchaseCB cb = new PurchaseCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_SpecifyDerivedReferrer_PurchaseList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName, aliasName);
    }
    public abstract String keepMemberId_SpecifyDerivedReferrer_PurchaseList(PurchaseCQ subQuery);

    public QDRFunction<MemberAddressCB> derivedMemberAddressList() {
        return xcreateQDRFunctionMemberAddressList();
    }
    protected QDRFunction<MemberAddressCB> xcreateQDRFunctionMemberAddressList() {
        return new QDRFunction<MemberAddressCB>(new QDRSetupper<MemberAddressCB>() {
            public void setup(String function, SubQuery<MemberAddressCB> subQuery, String operand, Object value) {
                xqderiveMemberAddressList(function, subQuery, operand, value);
            }
        });
    }
    public void xqderiveMemberAddressList(String function, SubQuery<MemberAddressCB> subQuery, String operand, Object value) {
        assertObjectNotNull("subQuery<MemberAddressCB>", subQuery);
        MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_QueryDerivedReferrer_MemberAddressList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepMemberId_QueryDerivedReferrer_MemberAddressListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepMemberId_QueryDerivedReferrer_MemberAddressList(MemberAddressCQ subQuery);
    public abstract String keepMemberId_QueryDerivedReferrer_MemberAddressListParameter(Object parameterValue);

    public QDRFunction<MemberLoginCB> derivedMemberLoginList() {
        return xcreateQDRFunctionMemberLoginList();
    }
    protected QDRFunction<MemberLoginCB> xcreateQDRFunctionMemberLoginList() {
        return new QDRFunction<MemberLoginCB>(new QDRSetupper<MemberLoginCB>() {
            public void setup(String function, SubQuery<MemberLoginCB> subQuery, String operand, Object value) {
                xqderiveMemberLoginList(function, subQuery, operand, value);
            }
        });
    }
    public void xqderiveMemberLoginList(String function, SubQuery<MemberLoginCB> subQuery, String operand, Object value) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_QueryDerivedReferrer_MemberLoginList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepMemberId_QueryDerivedReferrer_MemberLoginListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepMemberId_QueryDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery);
    public abstract String keepMemberId_QueryDerivedReferrer_MemberLoginListParameter(Object parameterValue);

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
        String subQueryPropertyName = keepMemberId_QueryDerivedReferrer_PurchaseList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepMemberId_QueryDerivedReferrer_PurchaseListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepMemberId_QueryDerivedReferrer_PurchaseList(PurchaseCQ subQuery);
    public abstract String keepMemberId_QueryDerivedReferrer_PurchaseListParameter(Object parameterValue);

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setMemberId_IsNull() { regMemberId(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setMemberId_IsNotNull() { regMemberId(CK_ISNN, DUMMY_OBJECT); }

    protected void regMemberId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberId(), "MEMBER_ID", "MemberId", "memberId");
    }
    protected void registerInlineMemberId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberId(), "MEMBER_ID", "MemberId", "memberId");
    }
    abstract protected ConditionValue getCValueMemberId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(200)}
     * @param memberName The value of memberName as equal.
     */
    public void setMemberName_Equal(String memberName) {
        regMemberName(CK_EQ, fRES(memberName));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param memberName The value of memberName as notEqual.
     */
    public void setMemberName_NotEqual(String memberName) {
        regMemberName(CK_NE, fRES(memberName));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param memberName The value of memberName as prefixSearch.
     */
    public void setMemberName_PrefixSearch(String memberName) {
        regMemberName(CK_PS, fRES(memberName));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberNameList The collection of memberName as inScope.
     */
    public void setMemberName_InScope(Collection<String> memberNameList) {
        regMemberName(CK_INS, cTL(memberNameList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberNameList The collection of memberName as notInScope.
     */
    public void setMemberName_NotInScope(Collection<String> memberNameList) {
        regMemberName(CK_NINS, cTL(memberNameList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param memberName The value of memberName as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setMemberName_LikeSearch(String memberName, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(memberName), getCValueMemberName(), "MEMBER_NAME", "MemberName", "memberName", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param memberName The value of memberName as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setMemberName_NotLikeSearch(String memberName, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(memberName), getCValueMemberName(), "MEMBER_NAME", "MemberName", "memberName", likeSearchOption);
    }

    protected void regMemberName(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberName(), "MEMBER_NAME", "MemberName", "memberName");
    }
    protected void registerInlineMemberName(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberName(), "MEMBER_NAME", "MemberName", "memberName");
    }
    abstract protected ConditionValue getCValueMemberName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {UQ : NotNull : VARCHAR(50)}
     * @param memberAccount The value of memberAccount as equal.
     */
    public void setMemberAccount_Equal(String memberAccount) {
        regMemberAccount(CK_EQ, fRES(memberAccount));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param memberAccount The value of memberAccount as notEqual.
     */
    public void setMemberAccount_NotEqual(String memberAccount) {
        regMemberAccount(CK_NE, fRES(memberAccount));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param memberAccount The value of memberAccount as prefixSearch.
     */
    public void setMemberAccount_PrefixSearch(String memberAccount) {
        regMemberAccount(CK_PS, fRES(memberAccount));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberAccountList The collection of memberAccount as inScope.
     */
    public void setMemberAccount_InScope(Collection<String> memberAccountList) {
        regMemberAccount(CK_INS, cTL(memberAccountList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberAccountList The collection of memberAccount as notInScope.
     */
    public void setMemberAccount_NotInScope(Collection<String> memberAccountList) {
        regMemberAccount(CK_NINS, cTL(memberAccountList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param memberAccount The value of memberAccount as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setMemberAccount_LikeSearch(String memberAccount, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(memberAccount), getCValueMemberAccount(), "MEMBER_ACCOUNT", "MemberAccount", "memberAccount", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param memberAccount The value of memberAccount as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setMemberAccount_NotLikeSearch(String memberAccount, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(memberAccount), getCValueMemberAccount(), "MEMBER_ACCOUNT", "MemberAccount", "memberAccount", likeSearchOption);
    }

    protected void regMemberAccount(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberAccount(), "MEMBER_ACCOUNT", "MemberAccount", "memberAccount");
    }
    protected void registerInlineMemberAccount(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberAccount(), "MEMBER_ACCOUNT", "MemberAccount", "memberAccount");
    }
    abstract protected ConditionValue getCValueMemberAccount();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : CHAR(3) : FK to MEMBER_STATUS}
     * @param memberStatusCode The value of memberStatusCode as equal.
     */
    public void setMemberStatusCode_Equal(String memberStatusCode) {
        regMemberStatusCode(CK_EQ, fRES(memberStatusCode));
    }

    /**
     * Equal(=). As Provisional. And OnlyOnceRegistered. <br />
     * 仮会員: 仮会員を示す
     */
    public void setMemberStatusCode_Equal_Provisional() {
        regMemberStatusCode(CK_EQ, CDef.MemberStatus.Provisional.code());
    }

    /**
     * Equal(=). As Formalized. And OnlyOnceRegistered. <br />
     * 正式会員: 正式会員を示す
     */
    public void setMemberStatusCode_Equal_Formalized() {
        regMemberStatusCode(CK_EQ, CDef.MemberStatus.Formalized.code());
    }

    /**
     * Equal(=). As Withdrawal. And OnlyOnceRegistered. <br />
     * 退会会員: 退会会員を示す
     */
    public void setMemberStatusCode_Equal_Withdrawal() {
        regMemberStatusCode(CK_EQ, CDef.MemberStatus.Withdrawal.code());
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param memberStatusCode The value of memberStatusCode as notEqual.
     */
    public void setMemberStatusCode_NotEqual(String memberStatusCode) {
        regMemberStatusCode(CK_NE, fRES(memberStatusCode));
    }

    /**
     * NotEqual(=). As Provisional. And OnlyOnceRegistered. <br />
     * 仮会員: 仮会員を示す
     */
    public void setMemberStatusCode_NotEqual_Provisional() {
        regMemberStatusCode(CK_NE, CDef.MemberStatus.Provisional.code());
    }

    /**
     * NotEqual(=). As Formalized. And OnlyOnceRegistered. <br />
     * 正式会員: 正式会員を示す
     */
    public void setMemberStatusCode_NotEqual_Formalized() {
        regMemberStatusCode(CK_NE, CDef.MemberStatus.Formalized.code());
    }

    /**
     * NotEqual(=). As Withdrawal. And OnlyOnceRegistered. <br />
     * 退会会員: 退会会員を示す
     */
    public void setMemberStatusCode_NotEqual_Withdrawal() {
        regMemberStatusCode(CK_NE, CDef.MemberStatus.Withdrawal.code());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberStatusCodeList The collection of memberStatusCode as inScope.
     */
    public void setMemberStatusCode_InScope(Collection<String> memberStatusCodeList) {
        regMemberStatusCode(CK_INS, cTL(memberStatusCodeList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberStatusCodeList The collection of memberStatusCode as notInScope.
     */
    public void setMemberStatusCode_NotInScope(Collection<String> memberStatusCodeList) {
        regMemberStatusCode(CK_NINS, cTL(memberStatusCodeList));
    }

    public void inScopeMemberStatus(SubQuery<MemberStatusCB> subQuery) {
        assertObjectNotNull("subQuery<MemberStatusCB>", subQuery);
        MemberStatusCB cb = new MemberStatusCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_InScopeSubQuery_MemberStatus(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_InScopeSubQuery_MemberStatus(com.example.dbflute.basic.dbflute.cbean.cq.MemberStatusCQ subQuery);

    protected void regMemberStatusCode(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberStatusCode(), "MEMBER_STATUS_CODE", "MemberStatusCode", "memberStatusCode");
    }
    protected void registerInlineMemberStatusCode(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberStatusCode(), "MEMBER_STATUS_CODE", "MemberStatusCode", "memberStatusCode");
    }
    abstract protected ConditionValue getCValueMemberStatusCode();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {TIMESTAMP}
     * @param memberFormalizedDatetime The value of memberFormalizedDatetime as equal.
     */
    public void setMemberFormalizedDatetime_Equal(java.sql.Timestamp memberFormalizedDatetime) {
        regMemberFormalizedDatetime(CK_EQ, memberFormalizedDatetime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberFormalizedDatetime The value of memberFormalizedDatetime as greaterThan.
     */
    public void setMemberFormalizedDatetime_GreaterThan(java.sql.Timestamp memberFormalizedDatetime) {
        regMemberFormalizedDatetime(CK_GT, memberFormalizedDatetime);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberFormalizedDatetime The value of memberFormalizedDatetime as lessThan.
     */
    public void setMemberFormalizedDatetime_LessThan(java.sql.Timestamp memberFormalizedDatetime) {
        regMemberFormalizedDatetime(CK_LT, memberFormalizedDatetime);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberFormalizedDatetime The value of memberFormalizedDatetime as greaterEqual.
     */
    public void setMemberFormalizedDatetime_GreaterEqual(java.sql.Timestamp memberFormalizedDatetime) {
        regMemberFormalizedDatetime(CK_GE, memberFormalizedDatetime);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberFormalizedDatetime The value of memberFormalizedDatetime as lessEqual.
     */
    public void setMemberFormalizedDatetime_LessEqual(java.sql.Timestamp memberFormalizedDatetime) {
        regMemberFormalizedDatetime(CK_LE, memberFormalizedDatetime);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {TIMESTAMP}
     * @param fromDate The from-date of memberFormalizedDatetime. (Nullable)
     * @param toDate The to-date of memberFormalizedDatetime. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setMemberFormalizedDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.seasar.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValueMemberFormalizedDatetime(), "MEMBER_FORMALIZED_DATETIME", "MemberFormalizedDatetime", "memberFormalizedDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {TIMESTAMP}
     * @param fromDate The from-date of memberFormalizedDatetime. (Nullable)
     * @param toDate The to-date of memberFormalizedDatetime. (Nullable)
     */
    public void setMemberFormalizedDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setMemberFormalizedDatetime_FromTo(fromDate, toDate, new org.seasar.dbflute.cbean.coption.DateFromToOption());
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setMemberFormalizedDatetime_IsNull() { regMemberFormalizedDatetime(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setMemberFormalizedDatetime_IsNotNull() { regMemberFormalizedDatetime(CK_ISNN, DUMMY_OBJECT); }

    protected void regMemberFormalizedDatetime(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberFormalizedDatetime(), "MEMBER_FORMALIZED_DATETIME", "MemberFormalizedDatetime", "memberFormalizedDatetime");
    }
    protected void registerInlineMemberFormalizedDatetime(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberFormalizedDatetime(), "MEMBER_FORMALIZED_DATETIME", "MemberFormalizedDatetime", "memberFormalizedDatetime");
    }
    abstract protected ConditionValue getCValueMemberFormalizedDatetime();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {DATE}
     * @param memberBirthday The value of memberBirthday as equal.
     */
    public void setMemberBirthday_Equal(java.util.Date memberBirthday) {
        regMemberBirthday(CK_EQ, memberBirthday);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberBirthday The value of memberBirthday as greaterThan.
     */
    public void setMemberBirthday_GreaterThan(java.util.Date memberBirthday) {
        regMemberBirthday(CK_GT, memberBirthday);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberBirthday The value of memberBirthday as lessThan.
     */
    public void setMemberBirthday_LessThan(java.util.Date memberBirthday) {
        regMemberBirthday(CK_LT, memberBirthday);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberBirthday The value of memberBirthday as greaterEqual.
     */
    public void setMemberBirthday_GreaterEqual(java.util.Date memberBirthday) {
        regMemberBirthday(CK_GE, memberBirthday);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberBirthday The value of memberBirthday as lessEqual.
     */
    public void setMemberBirthday_LessEqual(java.util.Date memberBirthday) {
        regMemberBirthday(CK_LE, memberBirthday);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {DATE}
     * @param fromDate The from-date of memberBirthday. (Nullable)
     * @param toDate The to-date of memberBirthday. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setMemberBirthday_FromTo(java.util.Date fromDate, java.util.Date toDate, org.seasar.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery(fromDate, toDate, getCValueMemberBirthday(), "MEMBER_BIRTHDAY", "MemberBirthday", "memberBirthday", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {DATE}
     * @param fromDate The from-date of memberBirthday. (Nullable)
     * @param toDate The to-date of memberBirthday. (Nullable)
     */
    public void setMemberBirthday_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setMemberBirthday_FromTo(fromDate, toDate, new org.seasar.dbflute.cbean.coption.DateFromToOption());
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setMemberBirthday_IsNull() { regMemberBirthday(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setMemberBirthday_IsNotNull() { regMemberBirthday(CK_ISNN, DUMMY_OBJECT); }

    protected void regMemberBirthday(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberBirthday(), "MEMBER_BIRTHDAY", "MemberBirthday", "memberBirthday");
    }
    protected void registerInlineMemberBirthday(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberBirthday(), "MEMBER_BIRTHDAY", "MemberBirthday", "memberBirthday");
    }
    abstract protected ConditionValue getCValueMemberBirthday();
    
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
    public SSQFunction<MemberCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<MemberCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<MemberCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<MemberCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<MemberCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<MemberCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<MemberCB>(new SSQSetupper<MemberCB>() {
            public void setup(String function, SubQuery<MemberCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<MemberCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(MemberCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberCQ.class.getName(); }
}
