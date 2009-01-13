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
 * The condition-inline-query of MEMBER_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public class MemberStatusCIQ extends AbstractBsMemberStatusCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsMemberStatusCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MemberStatusCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsMemberStatusCQ myCQ) {
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
    protected ConditionValue getCValueMemberStatusCode() {
        return _myCQ.getMemberStatusCode();
    }
    public String keepMemberStatusCode_InScopeSubQuery_MemberList(MemberCQ subQuery) {
        return _myCQ.keepMemberStatusCode_InScopeSubQuery_MemberList(subQuery);
    }
    public String keepMemberStatusCode_InScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        return _myCQ.keepMemberStatusCode_InScopeSubQuery_MemberLoginList(subQuery);
    }
    public String keepMemberStatusCode_NotInScopeSubQuery_MemberList(MemberCQ subQuery) {
        return _myCQ.keepMemberStatusCode_NotInScopeSubQuery_MemberList(subQuery);
    }
    public String keepMemberStatusCode_NotInScopeSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        return _myCQ.keepMemberStatusCode_NotInScopeSubQuery_MemberLoginList(subQuery);
    }
    public String keepMemberStatusCode_ExistsSubQuery_MemberList(MemberCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_ExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_NotExistsSubQuery_MemberList(MemberCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_NotExistsSubQuery_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_SpecifyDerivedReferrer_MemberList(MemberCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_SpecifyDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberList(MemberCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberLoginList(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepMemberStatusCode_QueryDerivedReferrer_MemberLoginListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    protected ConditionValue getCValueMemberStatusName() {
        return _myCQ.getMemberStatusName();
    }
    protected ConditionValue getCValueDisplayOrder() {
        return _myCQ.getDisplayOrder();
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(MemberStatusCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberStatusCQ.class.getName(); }
}
