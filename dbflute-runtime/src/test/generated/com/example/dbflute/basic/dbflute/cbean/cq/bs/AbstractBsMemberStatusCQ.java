package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Collection;

import org.dbflute.cbean.*;
import org.dbflute.cbean.ckey.*;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.dbmeta.DBMetaProvider;

import com.example.dbflute.basic.dbflute.allcommon.CDef;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The abstract condition-query of MEMBER_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsMemberStatusCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsMemberStatusCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "MEMBER_STATUS";
    }
    
    public String getTableSqlName() {
        return "MEMBER_STATUS";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {PK : NotNull : CHAR(3)}
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

    public void inScopeMemberList(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_InScopeSubQuery_MemberList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_InScopeSubQuery_MemberList(MemberCQ subQuery);

    public void inScopeMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_InScopeSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_STATUS_CODE", "LOGIN_MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_InScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    public void notInScopeMemberList(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_NotInScopeSubQuery_MemberList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_NotInScopeSubQuery_MemberList(MemberCQ subQuery);

    public void notInScopeMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_NotInScopeSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_STATUS_CODE", "LOGIN_MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_NotInScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select MEMBER_STATUS_CODE from MEMBER where ...)}
     * @param subQuery The sub-query of MemberStatusCode_ExistsSubQuery_MemberList for 'exists'. (NotNull)
     */
    public void existsMemberList(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_ExistsSubQuery_MemberList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_ExistsSubQuery_MemberList(MemberCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select LOGIN_MEMBER_STATUS_CODE from MEMBER_LOGIN where ...)}
     * @param subQuery The sub-query of MemberStatusCode_ExistsSubQuery_MemberLoginList for 'exists'. (NotNull)
     */
    public void existsMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_ExistsSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_STATUS_CODE", "LOGIN_MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_ExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select MEMBER_STATUS_CODE from MEMBER where ...)}
     * @param subQuery The sub-query of MemberStatusCode_NotExistsSubQuery_MemberList for 'not exists'. (NotNull)
     */
    public void notExistsMemberList(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_NotExistsSubQuery_MemberList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_NotExistsSubQuery_MemberList(MemberCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select LOGIN_MEMBER_STATUS_CODE from MEMBER_LOGIN where ...)}
     * @param subQuery The sub-query of MemberStatusCode_NotExistsSubQuery_MemberLoginList for 'not exists'. (NotNull)
     */
    public void notExistsMemberLoginList(SubQuery<MemberLoginCB> subQuery) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_NotExistsSubQuery_MemberLoginList(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_STATUS_CODE", "LOGIN_MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepMemberStatusCode_NotExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery);

    public void xsderiveMemberList(String function, SubQuery<MemberCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_SpecifyDerivedReferrer_MemberList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName, aliasName);
    }
    public abstract String keepMemberStatusCode_SpecifyDerivedReferrer_MemberList(MemberCQ subQuery);

    public void xsderiveMemberLoginList(String function, SubQuery<MemberLoginCB> subQuery, String aliasName) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_SpecifyDerivedReferrer_MemberLoginList(cb.query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "MEMBER_STATUS_CODE", "LOGIN_MEMBER_STATUS_CODE", subQueryPropertyName, aliasName);
    }
    public abstract String keepMemberStatusCode_SpecifyDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery);

    public QDRFunction<MemberCB> derivedMemberList() {
        return xcreateQDRFunctionMemberList();
    }
    protected QDRFunction<MemberCB> xcreateQDRFunctionMemberList() {
        return new QDRFunction<MemberCB>(new QDRSetupper<MemberCB>() {
            public void setup(String function, SubQuery<MemberCB> subQuery, String operand, Object value) {
                xqderiveMemberList(function, subQuery, operand, value);
            }
        });
    }
    public void xqderiveMemberList(String function, SubQuery<MemberCB> subQuery, String operand, Object value) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForDerivedReferrer(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberStatusCode_QueryDerivedReferrer_MemberList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepMemberStatusCode_QueryDerivedReferrer_MemberListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepMemberStatusCode_QueryDerivedReferrer_MemberList(MemberCQ subQuery);
    public abstract String keepMemberStatusCode_QueryDerivedReferrer_MemberListParameter(Object parameterValue);

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
        String subQueryPropertyName = keepMemberStatusCode_QueryDerivedReferrer_MemberLoginList(cb.query()); // for saving query-value.
        String parameterPropertyName = keepMemberStatusCode_QueryDerivedReferrer_MemberLoginListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "MEMBER_STATUS_CODE", "LOGIN_MEMBER_STATUS_CODE", subQueryPropertyName, operand, value, parameterPropertyName);
    }
    public abstract String keepMemberStatusCode_QueryDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery);
    public abstract String keepMemberStatusCode_QueryDerivedReferrer_MemberLoginListParameter(Object parameterValue);

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setMemberStatusCode_IsNull() { regMemberStatusCode(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setMemberStatusCode_IsNotNull() { regMemberStatusCode(CK_ISNN, DUMMY_OBJECT); }

    protected void regMemberStatusCode(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberStatusCode(), "MEMBER_STATUS_CODE", "MemberStatusCode", "memberStatusCode");
    }
    protected void registerInlineMemberStatusCode(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberStatusCode(), "MEMBER_STATUS_CODE", "MemberStatusCode", "memberStatusCode");
    }
    abstract protected ConditionValue getCValueMemberStatusCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {UQ : NotNull : VARCHAR(50)}
     * @param memberStatusName The value of memberStatusName as equal.
     */
    public void setMemberStatusName_Equal(String memberStatusName) {
        regMemberStatusName(CK_EQ, fRES(memberStatusName));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param memberStatusName The value of memberStatusName as notEqual.
     */
    public void setMemberStatusName_NotEqual(String memberStatusName) {
        regMemberStatusName(CK_NE, fRES(memberStatusName));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param memberStatusName The value of memberStatusName as prefixSearch.
     */
    public void setMemberStatusName_PrefixSearch(String memberStatusName) {
        regMemberStatusName(CK_PS, fRES(memberStatusName));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberStatusNameList The collection of memberStatusName as inScope.
     */
    public void setMemberStatusName_InScope(Collection<String> memberStatusNameList) {
        regMemberStatusName(CK_INS, cTL(memberStatusNameList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param memberStatusNameList The collection of memberStatusName as notInScope.
     */
    public void setMemberStatusName_NotInScope(Collection<String> memberStatusNameList) {
        regMemberStatusName(CK_NINS, cTL(memberStatusNameList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param memberStatusName The value of memberStatusName as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setMemberStatusName_LikeSearch(String memberStatusName, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(memberStatusName), getCValueMemberStatusName(), "MEMBER_STATUS_NAME", "MemberStatusName", "memberStatusName", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param memberStatusName The value of memberStatusName as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setMemberStatusName_NotLikeSearch(String memberStatusName, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(memberStatusName), getCValueMemberStatusName(), "MEMBER_STATUS_NAME", "MemberStatusName", "memberStatusName", likeSearchOption);
    }

    protected void regMemberStatusName(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberStatusName(), "MEMBER_STATUS_NAME", "MemberStatusName", "memberStatusName");
    }
    protected void registerInlineMemberStatusName(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberStatusName(), "MEMBER_STATUS_NAME", "MemberStatusName", "memberStatusName");
    }
    abstract protected ConditionValue getCValueMemberStatusName();
    
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
    public SSQFunction<MemberStatusCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<MemberStatusCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<MemberStatusCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<MemberStatusCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<MemberStatusCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<MemberStatusCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<MemberStatusCB>(new SSQSetupper<MemberStatusCB>() {
            public void setup(String function, SubQuery<MemberStatusCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<MemberStatusCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<MemberStatusCB>", subQuery);
        MemberStatusCB cb = new MemberStatusCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(MemberStatusCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberStatusCQ.class.getName(); }
}
