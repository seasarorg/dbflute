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
 * The condition-inline-query of PRODUCT.
 * @author DBFlute(AutoGenerator)
 */
public class ProductCIQ extends AbstractBsProductCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsProductCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ProductCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsProductCQ myCQ) {
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
    protected ConditionValue getCValueProductId() {
        return _myCQ.getProductId();
    }
    public String keepProductId_InScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        return _myCQ.keepProductId_InScopeSubQuery_PurchaseList(subQuery);
    }
    public String keepProductId_NotInScopeSubQuery_PurchaseList(PurchaseCQ subQuery) {
        return _myCQ.keepProductId_NotInScopeSubQuery_PurchaseList(subQuery);
    }
    public String keepProductId_ExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepProductId_NotExistsSubQuery_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepProductId_SpecifyDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepProductId_QueryDerivedReferrer_PurchaseList(PurchaseCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepProductId_QueryDerivedReferrer_PurchaseListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    protected ConditionValue getCValueProductName() {
        return _myCQ.getProductName();
    }
    protected ConditionValue getCValueProductHandleCode() {
        return _myCQ.getProductHandleCode();
    }
    protected ConditionValue getCValueProductStatusCode() {
        return _myCQ.getProductStatusCode();
    }
    public String keepProductStatusCode_InScopeSubQuery_ProductStatus(ProductStatusCQ subQuery) {
        return _myCQ.keepProductStatusCode_InScopeSubQuery_ProductStatus(subQuery);
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
    public String keepScalarSubQuery(ProductCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return ProductCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return ProductCQ.class.getName(); }
}
