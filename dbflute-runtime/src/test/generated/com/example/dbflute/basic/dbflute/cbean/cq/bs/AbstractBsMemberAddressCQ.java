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
 * The abstract condition-query of MEMBER_ADDRESS.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsMemberAddressCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsMemberAddressCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "MEMBER_ADDRESS";
    }
    
    public String getTableSqlName() {
        return "MEMBER_ADDRESS";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : NotNull : INTEGER}
     * @param memberAddressId The value of memberAddressId as equal.
     */
    public void setMemberAddressId_Equal(Integer memberAddressId) {
        regMemberAddressId(CK_EQ, memberAddressId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberAddressId The value of memberAddressId as greaterThan.
     */
    public void setMemberAddressId_GreaterThan(Integer memberAddressId) {
        regMemberAddressId(CK_GT, memberAddressId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param memberAddressId The value of memberAddressId as lessThan.
     */
    public void setMemberAddressId_LessThan(Integer memberAddressId) {
        regMemberAddressId(CK_LT, memberAddressId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param memberAddressId The value of memberAddressId as greaterEqual.
     */
    public void setMemberAddressId_GreaterEqual(Integer memberAddressId) {
        regMemberAddressId(CK_GE, memberAddressId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param memberAddressId The value of memberAddressId as lessEqual.
     */
    public void setMemberAddressId_LessEqual(Integer memberAddressId) {
        regMemberAddressId(CK_LE, memberAddressId);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param memberAddressIdList The collection of memberAddressId as inScope.
     */
    public void setMemberAddressId_InScope(Collection<Integer> memberAddressIdList) {
        regMemberAddressId(CK_INS, cTL(memberAddressIdList));
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param memberAddressIdList The collection of memberAddressId as notInScope.
     */
    public void setMemberAddressId_NotInScope(Collection<Integer> memberAddressIdList) {
        regMemberAddressId(CK_NINS, cTL(memberAddressIdList));
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setMemberAddressId_IsNull() { regMemberAddressId(CK_ISN, DUMMY_OBJECT); }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setMemberAddressId_IsNotNull() { regMemberAddressId(CK_ISNN, DUMMY_OBJECT); }

    protected void regMemberAddressId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberAddressId(), "MEMBER_ADDRESS_ID", "MemberAddressId", "memberAddressId");
    }
    protected void registerInlineMemberAddressId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberAddressId(), "MEMBER_ADDRESS_ID", "MemberAddressId", "memberAddressId");
    }
    abstract protected ConditionValue getCValueMemberAddressId();
    
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

    public void inScopeMemberAsOne(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_InScopeSubQuery_MemberAsOne(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_InScopeSubQuery_MemberAsOne(MemberCQ subQuery);

    public void notInScopeMemberAsOne(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForInScopeSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotInScopeSubQuery_MemberAsOne(cb.query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotInScopeSubQuery_MemberAsOne(MemberCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select MEMBER_ID from MEMBER where ...)}
     * @param subQuery The sub-query of MemberId_ExistsSubQuery_MemberAsOne for 'exists'. (NotNull)
     */
    public void existsMemberAsOne(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_ExistsSubQuery_MemberAsOne(cb.query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_ExistsSubQuery_MemberAsOne(MemberCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select MEMBER_ID from MEMBER where ...)}
     * @param subQuery The sub-query of MemberId_NotExistsSubQuery_MemberAsOne for 'not exists'. (NotNull)
     */
    public void notExistsMemberAsOne(SubQuery<MemberCB> subQuery) {
        assertObjectNotNull("subQuery<MemberCB>", subQuery);
        MemberCB cb = new MemberCB(); cb.xsetupForExistsSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepMemberId_NotExistsSubQuery_MemberAsOne(cb.query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "MEMBER_ID", "MEMBER_ID", subQueryPropertyName);
    }
    public abstract String keepMemberId_NotExistsSubQuery_MemberAsOne(MemberCQ subQuery);

    protected void regMemberId(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueMemberId(), "MEMBER_ID", "MemberId", "memberId");
    }
    protected void registerInlineMemberId(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueMemberId(), "MEMBER_ID", "MemberId", "memberId");
    }
    abstract protected ConditionValue getCValueMemberId();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : DATE}
     * @param validBeginDate The value of validBeginDate as equal.
     */
    public void setValidBeginDate_Equal(java.util.Date validBeginDate) {
        regValidBeginDate(CK_EQ, validBeginDate);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validBeginDate The value of validBeginDate as greaterThan.
     */
    public void setValidBeginDate_GreaterThan(java.util.Date validBeginDate) {
        regValidBeginDate(CK_GT, validBeginDate);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validBeginDate The value of validBeginDate as lessThan.
     */
    public void setValidBeginDate_LessThan(java.util.Date validBeginDate) {
        regValidBeginDate(CK_LT, validBeginDate);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validBeginDate The value of validBeginDate as greaterEqual.
     */
    public void setValidBeginDate_GreaterEqual(java.util.Date validBeginDate) {
        regValidBeginDate(CK_GE, validBeginDate);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validBeginDate The value of validBeginDate as lessEqual.
     */
    public void setValidBeginDate_LessEqual(java.util.Date validBeginDate) {
        regValidBeginDate(CK_LE, validBeginDate);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : DATE}
     * @param fromDate The from-date of validBeginDate. (Nullable)
     * @param toDate The to-date of validBeginDate. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setValidBeginDate_FromTo(java.util.Date fromDate, java.util.Date toDate, org.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery(fromDate, toDate, getCValueValidBeginDate(), "VALID_BEGIN_DATE", "ValidBeginDate", "validBeginDate", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {UQ : NotNull : DATE}
     * @param fromDate The from-date of validBeginDate. (Nullable)
     * @param toDate The to-date of validBeginDate. (Nullable)
     */
    public void setValidBeginDate_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setValidBeginDate_FromTo(fromDate, toDate, new org.dbflute.cbean.coption.DateFromToOption());
    }

    protected void regValidBeginDate(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueValidBeginDate(), "VALID_BEGIN_DATE", "ValidBeginDate", "validBeginDate");
    }
    protected void registerInlineValidBeginDate(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueValidBeginDate(), "VALID_BEGIN_DATE", "ValidBeginDate", "validBeginDate");
    }
    abstract protected ConditionValue getCValueValidBeginDate();
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : DATE}
     * @param validEndDate The value of validEndDate as equal.
     */
    public void setValidEndDate_Equal(java.util.Date validEndDate) {
        regValidEndDate(CK_EQ, validEndDate);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validEndDate The value of validEndDate as greaterThan.
     */
    public void setValidEndDate_GreaterThan(java.util.Date validEndDate) {
        regValidEndDate(CK_GT, validEndDate);
    }

    /**
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validEndDate The value of validEndDate as lessThan.
     */
    public void setValidEndDate_LessThan(java.util.Date validEndDate) {
        regValidEndDate(CK_LT, validEndDate);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validEndDate The value of validEndDate as greaterEqual.
     */
    public void setValidEndDate_GreaterEqual(java.util.Date validEndDate) {
        regValidEndDate(CK_GE, validEndDate);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param validEndDate The value of validEndDate as lessEqual.
     */
    public void setValidEndDate_LessEqual(java.util.Date validEndDate) {
        regValidEndDate(CK_LE, validEndDate);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {NotNull : DATE}
     * @param fromDate The from-date of validEndDate. (Nullable)
     * @param toDate The to-date of validEndDate. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setValidEndDate_FromTo(java.util.Date fromDate, java.util.Date toDate, org.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery(fromDate, toDate, getCValueValidEndDate(), "VALID_END_DATE", "ValidEndDate", "validEndDate", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {NotNull : DATE}
     * @param fromDate The from-date of validEndDate. (Nullable)
     * @param toDate The to-date of validEndDate. (Nullable)
     */
    public void setValidEndDate_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setValidEndDate_FromTo(fromDate, toDate, new org.dbflute.cbean.coption.DateFromToOption());
    }

    protected void regValidEndDate(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueValidEndDate(), "VALID_END_DATE", "ValidEndDate", "validEndDate");
    }
    protected void registerInlineValidEndDate(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueValidEndDate(), "VALID_END_DATE", "ValidEndDate", "validEndDate");
    }
    abstract protected ConditionValue getCValueValidEndDate();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(200)}
     * @param address The value of address as equal.
     */
    public void setAddress_Equal(String address) {
        regAddress(CK_EQ, fRES(address));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param address The value of address as notEqual.
     */
    public void setAddress_NotEqual(String address) {
        regAddress(CK_NE, fRES(address));
    }

    /**
     * PrefixSearch(like 'xxx%'). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param address The value of address as prefixSearch.
     */
    public void setAddress_PrefixSearch(String address) {
        regAddress(CK_PS, fRES(address));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param addressList The collection of address as inScope.
     */
    public void setAddress_InScope(Collection<String> addressList) {
        regAddress(CK_INS, cTL(addressList));
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param addressList The collection of address as notInScope.
     */
    public void setAddress_NotInScope(Collection<String> addressList) {
        regAddress(CK_NINS, cTL(addressList));
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param address The value of address as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setAddress_LikeSearch(String address, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(address), getCValueAddress(), "ADDRESS", "Address", "address", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param address The value of address as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setAddress_NotLikeSearch(String address, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(address), getCValueAddress(), "ADDRESS", "Address", "address", likeSearchOption);
    }

    protected void regAddress(ConditionKey key, Object value) {
        registerQuery(key, value, getCValueAddress(), "ADDRESS", "Address", "address");
    }
    protected void registerInlineAddress(ConditionKey key, Object value) {
        registerInlineQuery(key, value, getCValueAddress(), "ADDRESS", "Address", "address");
    }
    abstract protected ConditionValue getCValueAddress();
    
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
    public void setRegisterDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValueRegisterDatetime(), "REGISTER_DATETIME", "RegisterDatetime", "registerDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of registerDatetime. (Nullable)
     * @param toDate The to-date of registerDatetime. (Nullable)
     */
    public void setRegisterDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setRegisterDatetime_FromTo(fromDate, toDate, new org.dbflute.cbean.coption.DateFromToOption());
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
    public void setRegisterProcess_LikeSearch(String registerProcess, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(registerProcess), getCValueRegisterProcess(), "REGISTER_PROCESS", "RegisterProcess", "registerProcess", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param registerProcess The value of registerProcess as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setRegisterProcess_NotLikeSearch(String registerProcess, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
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
    public void setRegisterUser_LikeSearch(String registerUser, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(registerUser), getCValueRegisterUser(), "REGISTER_USER", "RegisterUser", "registerUser", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param registerUser The value of registerUser as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setRegisterUser_NotLikeSearch(String registerUser, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
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
    public void setUpdateDatetime_FromTo(java.util.Date fromDate, java.util.Date toDate, org.dbflute.cbean.coption.FromToOption fromToOption) {
        registerFromToQuery((fromDate != null ? new java.sql.Timestamp(fromDate.getTime()) : null), (toDate != null ? new java.sql.Timestamp(toDate.getTime()) : null), getCValueUpdateDatetime(), "UPDATE_DATETIME", "UpdateDatetime", "updateDatetime", fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of updateDatetime. (Nullable)
     * @param toDate The to-date of updateDatetime. (Nullable)
     */
    public void setUpdateDatetime_DateFromTo(java.util.Date fromDate, java.util.Date toDate) {
        setUpdateDatetime_FromTo(fromDate, toDate, new org.dbflute.cbean.coption.DateFromToOption());
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
    public void setUpdateProcess_LikeSearch(String updateProcess, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(updateProcess), getCValueUpdateProcess(), "UPDATE_PROCESS", "UpdateProcess", "updateProcess", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param updateProcess The value of updateProcess as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUpdateProcess_NotLikeSearch(String updateProcess, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
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
    public void setUpdateUser_LikeSearch(String updateUser, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(updateUser), getCValueUpdateUser(), "UPDATE_USER", "UpdateUser", "updateUser", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param updateUser The value of updateUser as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUpdateUser_NotLikeSearch(String updateUser, org.dbflute.cbean.coption.LikeSearchOption likeSearchOption) {
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
    public SSQFunction<MemberAddressCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<MemberAddressCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<MemberAddressCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<MemberAddressCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }
    
    public SSQFunction<MemberAddressCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }
    
    protected SSQFunction<MemberAddressCB> xcreateSSQFunction(final String operand) {
        return new SSQFunction<MemberAddressCB>(new SSQSetupper<MemberAddressCB>() {
            public void setup(String function, SubQuery<MemberAddressCB> subQuery) {
                xscalarSubQuery(function, subQuery, operand);
            }
        });
    }

    protected void xscalarSubQuery(String function, SubQuery<MemberAddressCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<MemberAddressCB>", subQuery);
        MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForScalarSubQuery(); subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName, operand);
    }
    public abstract String keepScalarSubQuery(MemberAddressCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberAddressCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberAddressCQ.class.getName(); }
}
