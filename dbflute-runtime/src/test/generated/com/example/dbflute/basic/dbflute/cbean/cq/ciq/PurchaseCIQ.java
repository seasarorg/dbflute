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
 * The condition-inline-query of PURCHASE.
 * @author DBFlute(AutoGenerator)
 */
public class PurchaseCIQ extends AbstractBsPurchaseCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsPurchaseCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PurchaseCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsPurchaseCQ myCQ) {
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
    protected ConditionValue getCValuePurchaseId() {
        return _myCQ.getPurchaseId();
    }
    protected ConditionValue getCValueMemberId() {
        return _myCQ.getMemberId();
    }
    public String keepMemberId_InScopeSubQuery_Member(MemberCQ subQuery) {
        return _myCQ.keepMemberId_InScopeSubQuery_Member(subQuery);
    }
    protected ConditionValue getCValueProductId() {
        return _myCQ.getProductId();
    }
    public String keepProductId_InScopeSubQuery_Product(ProductCQ subQuery) {
        return _myCQ.keepProductId_InScopeSubQuery_Product(subQuery);
    }
    protected ConditionValue getCValuePurchaseDatetime() {
        return _myCQ.getPurchaseDatetime();
    }
    protected ConditionValue getCValuePurchaseCount() {
        return _myCQ.getPurchaseCount();
    }
    protected ConditionValue getCValuePurchasePrice() {
        return _myCQ.getPurchasePrice();
    }
    protected ConditionValue getCValuePaymentCompleteFlg() {
        return _myCQ.getPaymentCompleteFlg();
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
    public String keepScalarSubQuery(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return PurchaseCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return PurchaseCQ.class.getName(); }
}
