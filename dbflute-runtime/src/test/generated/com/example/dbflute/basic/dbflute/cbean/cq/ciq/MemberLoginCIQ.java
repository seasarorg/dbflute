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
 * The condition-inline-query of MEMBER_LOGIN.
 * @author DBFlute(AutoGenerator)
 */
public class MemberLoginCIQ extends AbstractBsMemberLoginCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsMemberLoginCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MemberLoginCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsMemberLoginCQ myCQ) {
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
    protected ConditionValue getCValueMemberLoginId() {
        return _myCQ.getMemberLoginId();
    }
    protected ConditionValue getCValueMemberId() {
        return _myCQ.getMemberId();
    }
    public String keepMemberId_InScopeSubQuery_Member(MemberCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_Member(subQuery);
    }
    protected ConditionValue getCValueLoginDatetime() {
        return _myCQ.getLoginDatetime();
    }
    protected ConditionValue getCValueLoginMobileFlg() {
        return _myCQ.getLoginMobileFlg();
    }
    protected ConditionValue getCValueLoginMemberStatusCode() {
        return _myCQ.getLoginMemberStatusCode();
    }
    public String keepLoginMemberStatusCode_InScopeSubQuery_MemberStatus(MemberStatusCQ subQuery) {
        return _myCQ.keepLoginMemberStatusCode_InScopeSubQuery_MemberStatus(subQuery);
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(MemberLoginCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberLoginCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberLoginCQ.class.getName(); }
}
