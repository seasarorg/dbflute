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
 * The condition-inline-query of PRODUCT_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public class ProductStatusCIQ extends AbstractBsProductStatusCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsProductStatusCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ProductStatusCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsProductStatusCQ myCQ) {
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
    protected ConditionValue getCValueProductStatusCode() {
        return _myCQ.getProductStatusCode();
    }
    public String keepProductStatusCode_InScopeSubQuery_ProductList(ProductCQ subQuery) {
        return _myCQ.keepProductStatusCode_InScopeSubQuery_ProductList(subQuery);
    }
    public String keepProductStatusCode_InScopeSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        return _myCQ.keepProductStatusCode_InScopeSubQuery_SummaryProductList(subQuery);
    }
    public String keepProductStatusCode_NotInScopeSubQuery_ProductList(ProductCQ subQuery) {
        return _myCQ.keepProductStatusCode_NotInScopeSubQuery_ProductList(subQuery);
    }
    public String keepProductStatusCode_NotInScopeSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        return _myCQ.keepProductStatusCode_NotInScopeSubQuery_SummaryProductList(subQuery);
    }
    public String keepProductStatusCode_ExistsSubQuery_ProductList(ProductCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_ExistsSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        throw new UnsupportedOperationException("ExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_NotExistsSubQuery_ProductList(ProductCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_NotExistsSubQuery_SummaryProductList(SummaryProductCQ subQuery) {
        throw new UnsupportedOperationException("NotExistsSubQuery at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_SpecifyDerivedReferrer_ProductList(ProductCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_SpecifyDerivedReferrer_SummaryProductList(SummaryProductCQ subQuery) {
        throw new UnsupportedOperationException("(Specify)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_QueryDerivedReferrer_ProductList(ProductCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_QueryDerivedReferrer_ProductListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_QueryDerivedReferrer_SummaryProductList(SummaryProductCQ subQuery) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    public String keepProductStatusCode_QueryDerivedReferrer_SummaryProductListParameter(Object parameterValue) {
        throw new UnsupportedOperationException("(Query)DerivedReferrer at inline() is unsupported! Sorry!");
    }
    protected ConditionValue getCValueProductStatusName() {
        return _myCQ.getProductStatusName();
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(ProductStatusCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return ProductStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return ProductStatusCQ.class.getName(); }
}
