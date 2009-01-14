package com.example.dbflute.basic.dbflute.cbean.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.AbstractConditionBean;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.UnionQuery;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

import com.example.dbflute.basic.dbflute.allcommon.DBFluteConfig;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.allcommon.ImplementedSqlClauseCreator;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;
import com.example.dbflute.basic.dbflute.cbean.nss.*;

/**
 * The base condition-bean of SUMMARY_PRODUCT.
 * @author DBFlute(AutoGenerator)
 */
public class BsSummaryProductCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected SummaryProductCQ _conditionQuery;

    // ===================================================================================
    //                                                                           SqlClause
    //                                                                           =========
    @Override
    protected SqlClause createSqlClause() {
        return new ImplementedSqlClauseCreator().createSqlClause(this);
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
        return "SUMMARY_PRODUCT";
    }

    public String getTableSqlName() {
        return "SUMMARY_PRODUCT";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("PRODUCT_ID");
            if (obj instanceof Integer) {
                query().setProductId_Equal((Integer)obj);
            } else {
                query().setProductId_Equal(new Integer((String)obj));
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_ProductId_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_ProductId_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public SummaryProductCQ query() {
        return getConditionQuery();
    }

    public SummaryProductCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new SummaryProductCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
        }
        return _conditionQuery;
    }

    /**
     * The implementation.
     * @return The conditionQuery of the local table as interface. (NotNull)
     */
    public ConditionQuery localCQ() {
        return getConditionQuery();
    }

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    /**
     * Set up 'union'.
     * <pre>
     * cb.query().union(new UnionQuery&lt;SummaryProductCB&gt;() {
     *     public void query(SummaryProductCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<SummaryProductCB> unionQuery) {
        final SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final SummaryProductCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;SummaryProductCB&gt;() {
     *     public void query(SummaryProductCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<SummaryProductCB> unionQuery) {
        final SummaryProductCB cb = new SummaryProductCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final SummaryProductCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============
    protected ProductStatusNss _nssProductStatus;
    public ProductStatusNss getNssProductStatus() {
        if (_nssProductStatus == null) { _nssProductStatus = new ProductStatusNss(null); }
        return _nssProductStatus;
    }
    public ProductStatusNss setupSelect_ProductStatus() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryProductStatus(); } });
        if (_nssProductStatus == null || !_nssProductStatus.hasConditionQuery())
        { _nssProductStatus = new ProductStatusNss(query().queryProductStatus()); }
        return _nssProductStatus;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;
    public Specification specify() {
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<SummaryProductCQ>() {
            public boolean has() { return true; } public SummaryProductCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<SummaryProductCQ> {
        protected SpQyCall<SummaryProductCQ> _myQyCall;
        protected ProductStatusCB.Specification _productStatus;
        public Specification(ConditionBean baseCB, SpQyCall<SummaryProductCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnProductId() { doColumn("PRODUCT_ID"); }
        public void columnProductName() { doColumn("PRODUCT_NAME"); }
        public void columnProductStatusCode() { doColumn("PRODUCT_STATUS_CODE"); }
        public void columnLatestPurchaseDatetime() { doColumn("LATEST_PURCHASE_DATETIME"); }
        protected void doSpecifyRequiredColumn() {
            columnProductId();// PK
            if (_myQyCall.qy().hasConditionQueryProductStatus()) {
                columnProductStatusCode();// FK
            }
        }
        protected String getTableDbName() { return "SUMMARY_PRODUCT"; }
        public ProductStatusCB.Specification specifyProductStatus() {
            assertForeign("productStatus");
            if (_productStatus == null) {
                _productStatus = new ProductStatusCB.Specification(_baseCB, new SpQyCall<ProductStatusCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryProductStatus(); }
                    public ProductStatusCQ qy() { return _myQyCall.qy().queryProductStatus(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _productStatus;
        }
        public RAFunction<PurchaseCB, SummaryProductCQ> derivedPurchaseList() {
            return new RAFunction<PurchaseCB, SummaryProductCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<PurchaseCB, SummaryProductCQ>() {
                public void setup(String function, SubQuery<PurchaseCB> subQuery, SummaryProductCQ cq, String aliasName) {
                    cq.xsderivePurchaseList(function, subQuery, aliasName); } }, _dbmetaProvider);
        }
    }

    // ===================================================================================
    //                                                                         Display SQL
    //                                                                         ===========
    @Override
    protected String getLogDateFormat() { return DBFluteConfig.getInstance().getLogDateFormat(); }
    @Override
    protected String getLogTimestampFormat() { return DBFluteConfig.getInstance().getLogTimestampFormat(); }

    // ===================================================================================
    //                                                                            Internal
    //                                                                            ========
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return SummaryProductCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return SummaryProductCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
