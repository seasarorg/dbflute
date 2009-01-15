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
 * The abstract condition-query of MEMBER_WITHDRAWAL.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsMemberWithdrawalCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsMemberWithdrawalCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "MEMBER_WITHDRAWAL";
    }
    
    public String getTableSqlName() {
        return "MEMBER_WITHDRAWAL";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : NotNull : INTEGER : FK to MEMBER}
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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {CHAR(3) : FK to WITHDRAWAL_REASON}
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

    public void inScopeWithdrawalReason(SubQuery<WithdrawalReasonCB> subQuery) {
        assertObjectNotNull("subQuery<WithdrawalReasonCB>", subQuery);
        WithdrawalReasonCB cb = new WithdrawalReasonCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepWithdrawalReasonCode_InScopeSubQuery_WithdrawalReason(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "WITHDRAWAL_REASON_CODE", "WITHDRAWAL_REASON_CODE", subQueryPropertyName);
    }
    public abstract String keepWithdrawalReasonCode_InScopeSubQuery_WithdrawalReason(com.example.dbflute.basic.dbflute.cbean.cq.WithdrawalReasonCQ subQuery);

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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {CLOB}
     * @param withdrawalReasonInputText The value of withdrawalReasonInputText as equal.
     */
    public void setWithdrawalReasonInputText_Equal(String withdrawalReasonInputText) {
        regWithdrawalReasonInputText(CK_EQ, fRES(withdrawalReasonInputText));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param withdrawalReasonInputText The value of withdrawalReasonInputText as notEqual.
     */
    public void setWithdrawalReasonInputText_NotEqual(String withdrawalReasonInputText) {
        regWithdrawalReasonInputText(CK_NE, fRES(withdrawalReasonInputText));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param withdrawalReasonInputText The value of withdrawalReasonInputText as prefixSearch.
     */
    public void setWithdrawalReasonInputText_PrefixSearch(String withdrawalReasonInputText) {
        regWithdrawalReasonInputText(CK_PS, fRES(withdrawalReasonInputText));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param withdrawalReasonInputTextList The collection of withdrawalReasonInputText as inScope.
     */
    public void setWithdrawalReasonInputText_InScope(Collection<String> withdrawalReasonInputTextList) {
        regWithdrawalReasonInputText(CK_INS, cTL(withdrawalReasonInputTextList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param withdrawalReasonInputTextList The collection of withdrawalReasonInputText as notInScope.
     */
    public void setWithdrawalReasonInputText_NotInScope(Collection<String> withdrawalReasonInputTextList) {
        regWithdrawalReasonInputText(CK_NINS, cTL(withdrawalReasonInputTextList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param withdrawalReasonInputText The value of withdrawalReasonInputText as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setWithdrawalReasonInputText_LikeSearch(String withdrawalReasonInputText, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(withdrawalReasonInputText), getCValueWithdrawalReasonInputText(), "WITHDRAWAL_REASON_INPUT_TEXT", "WithdrawalReasonInputText", "withdrawalReasonInputText", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param withdrawalReasonInputText The value of withdrawalReasonInputText as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setWithdrawalReasonInputText_NotLikeSearch(String withdrawalReasonInputText, org.seasar.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(withdrawalReasonInputText), getCValueWithdrawalReasonInputText(), "WITHDRAWAL_REASON_INPUT_TEXT", "WithdrawalReasonInputText", "withdrawalReasonInputText", likeSearchOption);
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setWithdrawalReasonInputText_IsNull() { regWithdrawalReasonInputText(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setWithdrawalReasonInputText_IsNotNull() { regWithdrawalReasonInputText(CK_ISNN, DUMMY_OBJECT); }

    protected void regWithdrawalReasonInputText(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueWithdrawalReasonInputText(), "WITHDRAWAL_REASON_INPUT_TEXT", "WithdrawalReasonInputText", "withdrawalReasonInputText");
    }
    protected void registerInlineWithdrawalReasonInputText(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueWithdrawalReasonInputText(), "WITHDRAWAL_REASON_INPUT_TEXT", "WithdrawalReasonInputText", "withdrawalReasonInputText");
    }
    abstract protected ConditionValue getCValueWithdrawalReasonInputText();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param withdrawalDatetime The value of withdrawalDatetime as equal.
     */
    public void setWithdrawalDatetime_Equal(java.sql.Timestamp withdrawalDatetime) {
        regWithdrawalDatetime(CK_EQ, withdrawalDatetime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param withdrawalDatetime The value of withdrawalDatetime as greaterThan.
     */
    public void setWithdrawalDatetime_GreaterThan(java.sql.Timestamp withdrawalDatetime) {
        regWithdrawalDatetime(CK_GT, withdrawalDatetime);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param withdrawalDatetime The value of withdrawalDatetime as lessThan.
     */
    public void setWithdrawalDatetime_LessThan(java.sql.Timestamp withdrawalDatetime) {
        regWithdrawalDatetime(CK_LT, withdrawalDatetime);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param withdrawalDatetime The value of withdrawalDatetime as greaterEqual.
     */
    public void setWithdrawalDatetime_GreaterEqual(java.sql.Timestamp withdrawalDatetime) {
        regWithdrawalDatetime(CK_GE, withdrawalDatetime);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param withdrawalDatetime The value of withdrawalDatetime as lessEqual.
     */
    public void setWithdrawalDatetime_LessEqual(java.sql.Timestamp withdrawalDatetime) {
        regWithdrawalDatetime(CK_LE, withdrawalDatetime);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of withdrawalDatetime. (Nullable)
     * @param toDate The to-date of withdrawalDatetime. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setWithdrawalDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.seasar.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValueWithdrawalDatetime(), "WITHDRAWAL_DATETIME", "WithdrawalDatetime", "withdrawalDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of withdrawalDatetime. (Nullable)
     * @param toDate The to-date of withdrawalDatetime. (Nullable)
     */
    public void setWithdrawalDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setWithdrawalDatetime_FromTo(fromDate, toDate, new org.seasar.dbflute.cbean.coption.DateFromToOption());
    }

    protected void regWithdrawalDatetime(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueWithdrawalDatetime(), "WITHDRAWAL_DATETIME", "WithdrawalDatetime", "withdrawalDatetime");
    }
    protected void registerInlineWithdrawalDatetime(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueWithdrawalDatetime(), "WITHDRAWAL_DATETIME", "WithdrawalDatetime", "withdrawalDatetime");
    }
    abstract protected ConditionValue getCValueWithdrawalDatetime();
    
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
    public SSQFunction<MemberWithdrawalCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<MemberWithdrawalCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<MemberWithdrawalCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<MemberWithdrawalCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<MemberWithdrawalCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<MemberWithdrawalCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<MemberWithdrawalCB>(new SSQSetupper<MemberWithdrawalCB>() {
            public void setup(String function, SubQuery<MemberWithdrawalCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<MemberWithdrawalCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<MemberWithdrawalCB>", subQuery);
        MemberWithdrawalCB cb = new MemberWithdrawalCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(MemberWithdrawalCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberWithdrawalCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberWithdrawalCQ.class.getName(); }
}
