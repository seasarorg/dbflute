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
 * The abstract condition-query of MEMBER_LOGIN.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsMemberLoginCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsMemberLoginCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "MEMBER_LOGIN";
    }
    
    public String getTableSqlName() {
        return "MEMBER_LOGIN";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : ID : NotNull : BIGINT}
     * @param memberLoginId The value of memberLoginId as equal.
     */
    public void setMemberLoginId_Equal(Long memberLoginId) {
        regMemberLoginId(CK_EQ, memberLoginId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberLoginId The value of memberLoginId as greaterThan.
     */
    public void setMemberLoginId_GreaterThan(Long memberLoginId) {
        regMemberLoginId(CK_GT, memberLoginId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberLoginId The value of memberLoginId as lessThan.
     */
    public void setMemberLoginId_LessThan(Long memberLoginId) {
        regMemberLoginId(CK_LT, memberLoginId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param memberLoginId The value of memberLoginId as greaterEqual.
     */
    public void setMemberLoginId_GreaterEqual(Long memberLoginId) {
        regMemberLoginId(CK_GE, memberLoginId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param memberLoginId The value of memberLoginId as lessEqual.
     */
    public void setMemberLoginId_LessEqual(Long memberLoginId) {
        regMemberLoginId(CK_LE, memberLoginId);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param memberLoginIdList The collection of memberLoginId as inScope.
     */
    public void setMemberLoginId_InScope(Collection<Long> memberLoginIdList) {
        regMemberLoginId(CK_INS, cTL(memberLoginIdList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param memberLoginIdList The collection of memberLoginId as notInScope.
     */
    public void setMemberLoginId_NotInScope(Collection<Long> memberLoginIdList) {
        regMemberLoginId(CK_NINS, cTL(memberLoginIdList));
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setMemberLoginId_IsNull() { regMemberLoginId(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setMemberLoginId_IsNotNull() { regMemberLoginId(CK_ISNN, DUMMY_OBJECT); }

    protected void regMemberLoginId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberLoginId(), "MEMBER_LOGIN_ID", "MemberLoginId", "memberLoginId");
    }
    protected void registerInlineMemberLoginId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberLoginId(), "MEMBER_LOGIN_ID", "MemberLoginId", "memberLoginId");
    }
    abstract protected ConditionValue getCValueMemberLoginId();
    
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
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : TIMESTAMP}
     * @param loginDatetime The value of loginDatetime as equal.
     */
    public void setLoginDatetime_Equal(java.sql.Timestamp loginDatetime) {
        regLoginDatetime(CK_EQ, loginDatetime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param loginDatetime The value of loginDatetime as greaterThan.
     */
    public void setLoginDatetime_GreaterThan(java.sql.Timestamp loginDatetime) {
        regLoginDatetime(CK_GT, loginDatetime);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param loginDatetime The value of loginDatetime as lessThan.
     */
    public void setLoginDatetime_LessThan(java.sql.Timestamp loginDatetime) {
        regLoginDatetime(CK_LT, loginDatetime);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param loginDatetime The value of loginDatetime as greaterEqual.
     */
    public void setLoginDatetime_GreaterEqual(java.sql.Timestamp loginDatetime) {
        regLoginDatetime(CK_GE, loginDatetime);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param loginDatetime The value of loginDatetime as lessEqual.
     */
    public void setLoginDatetime_LessEqual(java.sql.Timestamp loginDatetime) {
        regLoginDatetime(CK_LE, loginDatetime);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : TIMESTAMP}
     * @param fromDate The from-date of loginDatetime. (Nullable)
     * @param toDate The to-date of loginDatetime. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setLoginDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.seasar.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValueLoginDatetime(), "LOGIN_DATETIME", "LoginDatetime", "loginDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : TIMESTAMP}
     * @param fromDate The from-date of loginDatetime. (Nullable)
     * @param toDate The to-date of loginDatetime. (Nullable)
     */
    public void setLoginDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setLoginDatetime_FromTo(fromDate, toDate, new org.seasar.dbflute.cbean.coption.DateFromToOption());
    }

    protected void regLoginDatetime(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueLoginDatetime(), "LOGIN_DATETIME", "LoginDatetime", "loginDatetime");
    }
    protected void registerInlineLoginDatetime(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueLoginDatetime(), "LOGIN_DATETIME", "LoginDatetime", "loginDatetime");
    }
    abstract protected ConditionValue getCValueLoginDatetime();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : INTEGER}
     * @param loginMobileFlg The value of loginMobileFlg as equal.
     */
    public void setLoginMobileFlg_Equal(Integer loginMobileFlg) {
        regLoginMobileFlg(CK_EQ, loginMobileFlg);
    }

    /**
     * Equal(=). As True. And NullIgnored, OnlyOnceRegistered. <br />
     * はい: 有効を示す
     */
    public void setLoginMobileFlg_Equal_True() {
        regLoginMobileFlg(CK_EQ, new Integer(CDef.Flg.True.code()));
    }

    /**
     * Equal(=). As False. And NullIgnored, OnlyOnceRegistered. <br />
     * いいえ: 無効を示す
     */
    public void setLoginMobileFlg_Equal_False() {
        regLoginMobileFlg(CK_EQ, new Integer(CDef.Flg.False.code()));
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param loginMobileFlgList The collection of loginMobileFlg as inScope.
     */
    public void setLoginMobileFlg_InScope(Collection<Integer> loginMobileFlgList) {
        regLoginMobileFlg(CK_INS, cTL(loginMobileFlgList));
    }

    protected void regLoginMobileFlg(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueLoginMobileFlg(), "LOGIN_MOBILE_FLG", "LoginMobileFlg", "loginMobileFlg");
    }
    protected void registerInlineLoginMobileFlg(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueLoginMobileFlg(), "LOGIN_MOBILE_FLG", "LoginMobileFlg", "loginMobileFlg");
    }
    abstract protected ConditionValue getCValueLoginMobileFlg();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : CHAR(3) : FK to MEMBER_STATUS}
     * @param loginMemberStatusCode The value of loginMemberStatusCode as equal.
     */
    public void setLoginMemberStatusCode_Equal(String loginMemberStatusCode) {
        regLoginMemberStatusCode(CK_EQ, fRES(loginMemberStatusCode));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param loginMemberStatusCode The value of loginMemberStatusCode as notEqual.
     */
    public void setLoginMemberStatusCode_NotEqual(String loginMemberStatusCode) {
        regLoginMemberStatusCode(CK_NE, fRES(loginMemberStatusCode));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param loginMemberStatusCode The value of loginMemberStatusCode as prefixSearch.
     */
    public void setLoginMemberStatusCode_PrefixSearch(String loginMemberStatusCode) {
        regLoginMemberStatusCode(CK_PS, fRES(loginMemberStatusCode));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param loginMemberStatusCodeList The collection of loginMemberStatusCode as inScope.
     */
    public void setLoginMemberStatusCode_InScope(Collection<String> loginMemberStatusCodeList) {
        regLoginMemberStatusCode(CK_INS, cTL(loginMemberStatusCodeList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param loginMemberStatusCodeList The collection of loginMemberStatusCode as notInScope.
     */
    public void setLoginMemberStatusCode_NotInScope(Collection<String> loginMemberStatusCodeList) {
        regLoginMemberStatusCode(CK_NINS, cTL(loginMemberStatusCodeList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param loginMemberStatusCode The value of loginMemberStatusCode as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setLoginMemberStatusCode_LikeSearch(String loginMemberStatusCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(loginMemberStatusCode), getCValueLoginMemberStatusCode(), "LOGIN_MEMBER_STATUS_CODE", "LoginMemberStatusCode", "loginMemberStatusCode", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param loginMemberStatusCode The value of loginMemberStatusCode as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setLoginMemberStatusCode_NotLikeSearch(String loginMemberStatusCode, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(loginMemberStatusCode), getCValueLoginMemberStatusCode(), "LOGIN_MEMBER_STATUS_CODE", "LoginMemberStatusCode", "loginMemberStatusCode", likeSearchOption);
    }

    public void inScopeMemberStatus(SubQuery<MemberStatusCB> subQuery) {
        assertObjectNotNull("subQuery<MemberStatusCB>", subQuery);
        MemberStatusCB cb = new MemberStatusCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepLoginMemberStatusCode_InScopeSubQuery_MemberStatus(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "LOGIN_MEMBER_STATUS_CODE", "MEMBER_STATUS_CODE", subQueryPropertyName);
    }
    public abstract String keepLoginMemberStatusCode_InScopeSubQuery_MemberStatus(com.example.dbflute.basic.dbflute.cbean.cq.MemberStatusCQ subQuery);

    protected void regLoginMemberStatusCode(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueLoginMemberStatusCode(), "LOGIN_MEMBER_STATUS_CODE", "LoginMemberStatusCode", "loginMemberStatusCode");
    }
    protected void registerInlineLoginMemberStatusCode(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueLoginMemberStatusCode(), "LOGIN_MEMBER_STATUS_CODE", "LoginMemberStatusCode", "loginMemberStatusCode");
    }
    abstract protected ConditionValue getCValueLoginMemberStatusCode();

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public SSQFunction<MemberLoginCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<MemberLoginCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<MemberLoginCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<MemberLoginCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<MemberLoginCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<MemberLoginCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<MemberLoginCB>(new SSQSetupper<MemberLoginCB>() {
            public void setup(String function, SubQuery<MemberLoginCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<MemberLoginCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<MemberLoginCB>", subQuery);
        MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(MemberLoginCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberLoginCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberLoginCQ.class.getName(); }
}
