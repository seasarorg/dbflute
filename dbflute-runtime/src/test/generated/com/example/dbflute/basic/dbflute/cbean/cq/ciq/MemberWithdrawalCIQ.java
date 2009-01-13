package com.example.dbflute.basic.dbflute.cbean.cq.ciq;

import org.dbflute.cbean.*;
import org.dbflute.cbean.ckey.*;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;

import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.bs.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The condition-inline-query of MEMBER_WITHDRAWAL.
 * @author DBFlute(AutoGenerator)
 */
public class MemberWithdrawalCIQ extends AbstractBsMemberWithdrawalCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsMemberWithdrawalCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MemberWithdrawalCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsMemberWithdrawalCQ myCQ) {
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
    public String keepMemberId_InScopeSubQuery_Member(MemberCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_Member(subQuery);
    }
    protected ConditionValue getCValueWithdrawalReasonCode() {
        return _myCQ.getWithdrawalReasonCode();
    }
    public String keepWithdrawalReasonCode_InScopeSubQuery_WithdrawalReason(WithdrawalReasonCQ subQuery) {
        return _myCQ.keepWithdrawalReasonCode_InScopeSubQuery_WithdrawalReason(subQuery);
    }
    protected ConditionValue getCValueWithdrawalReasonInputText() {
        return _myCQ.getWithdrawalReasonInputText();
    }
    protected ConditionValue getCValueWithdrawalDatetime() {
        return _myCQ.getWithdrawalDatetime();
    }
    protected ConditionValue getCValueRegisterDatetime() {
        return _myCQ.getRegisterDatetime();
    }
    protected ConditionValue getCValueRegisterProcess() {
        return _myCQ.getRegisterProcess();
    }
    protected ConditionValue getCValueRegisterUser() {
        return _myCQ.getRegisterUser();
    }
    protected ConditionValue getCValueUpdateDatetime() {
        return _myCQ.getUpdateDatetime();
    }
    protected ConditionValue getCValueUpdateProcess() {
        return _myCQ.getUpdateProcess();
    }
    protected ConditionValue getCValueUpdateUser() {
        return _myCQ.getUpdateUser();
    }
    protected ConditionValue getCValueVersionNo() {
        return _myCQ.getVersionNo();
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(MemberWithdrawalCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberWithdrawalCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberWithdrawalCQ.class.getName(); }
}
