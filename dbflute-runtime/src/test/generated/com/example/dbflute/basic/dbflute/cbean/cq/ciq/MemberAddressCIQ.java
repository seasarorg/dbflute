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
 * The condition-inline-query of MEMBER_ADDRESS.
 * @author DBFlute(AutoGenerator)
 */
public class MemberAddressCIQ extends AbstractBsMemberAddressCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsMemberAddressCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MemberAddressCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsMemberAddressCQ myCQ) {
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
    protected ConditionValue getCValueMemberAddressId() {
        return _myCQ.getMemberAddressId();
    }
    protected ConditionValue getCValueMemberId() {
        return _myCQ.getMemberId();
    }
    public String keepMemberId_InScopeSubQuery_Member(MemberCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_Member(subQuery);
    }
    public String keepMemberId_InScopeSubQuery_MemberAsOne(MemberCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_MemberAsOne(subQuery);
    }
    public String keepMemberId_NotInScopeSubQuery_MemberAsOne(MemberCQ subQuery) {
        return _myCQ.keepMemberId_NotInScopeSubQuery_MemberAsOne(subQuery);
    }
    public String keepMemberId_ExistsSubQuery_MemberAsOne(MemberCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepMemberId_NotExistsSubQuery_MemberAsOne(MemberCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    protected ConditionValue getCValueValidBeginDate() {
        return _myCQ.getValidBeginDate();
    }
    protected ConditionValue getCValueValidEndDate() {
        return _myCQ.getValidEndDate();
    }
    protected ConditionValue getCValueAddress() {
        return _myCQ.getAddress();
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
    public String keepScalarSubQuery(MemberAddressCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberAddressCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberAddressCQ.class.getName(); }
}
