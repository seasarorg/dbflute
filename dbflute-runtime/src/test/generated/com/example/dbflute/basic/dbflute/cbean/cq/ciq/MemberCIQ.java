package com.example.dbflute.basic.dbflute.cbean.cq.ciq;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.ckey.*;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.bs.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The condition-inline-query of MEMBER.
 * @author DBFlute(AutoGenerator)
 */
public class MemberCIQ extends AbstractBsMemberCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsMemberCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MemberCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsMemberCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.getForeignPropertyName();// Accept foreign property name.
        _relationPath = _myCQ.getRelationPath();// Accept relation path.
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        throw new UnsupportedOperationException("InlineQuery must not need UNION method: " + baseQueryAsSuper + " : " + unionQueryAsSuper);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey key, Object value, ConditionValue cvalue
                                                             , String colName, String capPropName, String uncapPropName) {
        registerInlineQuery(key, value, cvalue, colName, capPropName, uncapPropName);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey key, Object value, ConditionValue cvalue
                                                             , String colName, String capPropName, String uncapPropName, ConditionOption option) {
        registerInlineQuery(key, value, cvalue, colName, capPropName, uncapPropName, option);
    }

    @Override
    protected void registerWhereClause(String whereClause) {
        registerInlineWhereClause(whereClause);
    }

    @Override
    protected String getInScopeSubQueryRealColumnName(String columnName) {
        if (_onClauseInline) {
            throw new UnsupportedOperationException("InScopeSubQuery of on-clause is unsupported");
        }
        return _onClauseInline ? getRealAliasName() + "." + columnName : columnName;
    }

    @Override
    protected void registerExistsSubQuery(ConditionQuery subQuery
                                 , String columnName, String relatedColumnName, String propertyName) {
        throw new UnsupportedOperationException("Sorry! ExistsSubQuery at inline view is unsupported. So please use InScopeSubQyery.");
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    protected ConditionValue getCValueMemberId() {
        return _myCQ.getMemberId();
    }
    public String keepMemberId_InScopeSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_MemberAddressList(subQuery);
    }
    public String keepMemberId_InScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_MemberLoginList(subQuery);
    }
    public String keepMemberId_InScopeSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_MemberSecurityAsOne(subQuery);
    }
    public String keepMemberId_InScopeSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_MemberWithdrawalAsOne(subQuery);
    }
    public String keepMemberId_InScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_PurchaseList(subQuery);
    }
    public String keepMemberId_NotInScopeSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        return _myCQ.keepMemberId_NotInScopeSubQuery_MemberAddressList(subQuery);
    }
    public String keepMemberId_NotInScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        return _myCQ.keepMemberId_NotInScopeSubQuery_MemberLoginList(subQuery);
    }
    public String keepMemberId_NotInScopeSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        return _myCQ.keepMemberId_NotInScopeSubQuery_MemberSecurityAsOne(subQuery);
    }
    public String keepMemberId_NotInScopeSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        return _myCQ.keepMemberId_NotInScopeSubQuery_MemberWithdrawalAsOne(subQuery);
    }
    public String keepMemberId_NotInScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        return _myCQ.keepMemberId_NotInScopeSubQuery_PurchaseList(subQuery);
    }
    public String keepMemberId_ExistsSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_ExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_ExistsSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_ExistsSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_ExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_NotExistsSubQuery_MemberAddressList(MemberAddressCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_NotExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_NotExistsSubQuery_MemberSecurityAsOne(MemberSecurityCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_NotExistsSubQuery_MemberWithdrawalAsOne(MemberWithdrawalCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_NotExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_SpecifyDerivedReferrer_MemberAddressList(MemberAddressCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_SpecifyDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_SpecifyDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_QueryDerivedReferrer_MemberAddressList(MemberAddressCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_QueryDerivedReferrer_MemberAddressListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_QueryDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_QueryDerivedReferrer_MemberLoginListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_QueryDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_QueryDerivedReferrer_PurchaseListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    protected ConditionValue getCValueMemberName() {
        return _myCQ.getMemberName();
    }
    protected ConditionValue getCValueMemberAccount() {
        return _myCQ.getMemberAccount();
    }
    protected ConditionValue getCValueMemberStatusCode() {
        return _myCQ.getMemberStatusCode();
    }
    public String keepMemberStatusCode_InScopeSubQuery_MemberStatus(MemberStatusCQ subQuery) {
        return _myCQ.keepMemberStatusCode_InScopeSubQuery_MemberStatus(subQuery);
    }
    protected ConditionValue getCValueMemberFormalizedDatetime() {
        return _myCQ.getMemberFormalizedDatetime();
    }
    protected ConditionValue getCValueMemberBirthday() {
        return _myCQ.getMemberBirthday();
    }
    protected ConditionValue getCValueRegisterDatetime() {
        return _myCQ.getRegisterDatetime();
    }
    protected ConditionValue getCValueRegisterUser() {
        return _myCQ.getRegisterUser();
    }
    protected ConditionValue getCValueRegisterProcess() {
        return _myCQ.getRegisterProcess();
    }
    protected ConditionValue getCValueUpdateDatetime() {
        return _myCQ.getUpdateDatetime();
    }
    protected ConditionValue getCValueUpdateUser() {
        return _myCQ.getUpdateUser();
    }
    protected ConditionValue getCValueUpdateProcess() {
        return _myCQ.getUpdateProcess();
    }
    protected ConditionValue getCValueVersionNo() {
        return _myCQ.getVersionNo();
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(MemberCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberCQ.class.getName(); }
}
