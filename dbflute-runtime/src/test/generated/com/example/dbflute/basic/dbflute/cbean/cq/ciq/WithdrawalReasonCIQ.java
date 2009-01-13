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
 * The condition-inline-query of WITHDRAWAL_REASON.
 * @author DBFlute(AutoGenerator)
 */
public class WithdrawalReasonCIQ extends AbstractBsWithdrawalReasonCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsWithdrawalReasonCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WithdrawalReasonCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsWithdrawalReasonCQ myCQ) {
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
    protected ConditionValue getCValueWithdrawalReasonCode() {
        return _myCQ.getWithdrawalReasonCode();
    }
    public String keepWithdrawalReasonCode_InScopeSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        return _myCQ.keepWithdrawalReasonCode_InScopeSubQuery_MemberWithdrawalList(subQuery);
    }
    public String keepWithdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        return _myCQ.keepWithdrawalReasonCode_NotInScopeSubQuery_MemberWithdrawalList(subQuery);
    }
    public String keepWithdrawalReasonCode_ExistsSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepWithdrawalReasonCode_NotExistsSubQuery_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepWithdrawalReasonCode_SpecifyDerivedReferrer_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalList(MemberWithdrawalCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepWithdrawalReasonCode_QueryDerivedReferrer_MemberWithdrawalListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    protected ConditionValue getCValueWithdrawalReasonText() {
        return _myCQ.getWithdrawalReasonText();
    }
    protected ConditionValue getCValueDisplayOrder() {
        return _myCQ.getDisplayOrder();
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(WithdrawalReasonCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return WithdrawalReasonCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return WithdrawalReasonCQ.class.getName(); }
}
