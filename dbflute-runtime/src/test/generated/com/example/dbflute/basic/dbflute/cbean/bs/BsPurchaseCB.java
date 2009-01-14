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
 * The base condition-bean of PURCHASE.
 * @author DBFlute(AutoGenerator)
 */
public class BsPurchaseCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected PurchaseCQ _conditionQuery;

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
        return "PURCHASE";
    }

    public String getTableSqlName() {
        return "PURCHASE";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("PURCHASE_ID");
            if (obj instanceof Long) {
                query().setPurchaseId_Equal((Long)obj);
            } else {
                query().setPurchaseId_Equal(new Long((String)obj));
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_PurchaseId_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_PurchaseId_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public PurchaseCQ query() {
        return getConditionQuery();
    }

    public PurchaseCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new PurchaseCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
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
     * cb.query().union(new UnionQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<PurchaseCB> unionQuery) {
        final PurchaseCB cb = new PurchaseCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final PurchaseCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<PurchaseCB> unionQuery) {
        final PurchaseCB cb = new PurchaseCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final PurchaseCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============
    protected MemberNss _nssMember;
    public MemberNss getNssMember() {
        if (_nssMember == null) { _nssMember = new MemberNss(null); }
        return _nssMember;
    }
    public MemberNss setupSelect_Member() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryMember(); } });
        if (_nssMember == null || !_nssMember.hasConditionQuery())
        { _nssMember = new MemberNss(query().queryMember()); }
        return _nssMember;
    }
    protected ProductNss _nssProduct;
    public ProductNss getNssProduct() {
        if (_nssProduct == null) { _nssProduct = new ProductNss(null); }
        return _nssProduct;
    }
    public ProductNss setupSelect_Product() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryProduct(); } });
        if (_nssProduct == null || !_nssProduct.hasConditionQuery())
        { _nssProduct = new ProductNss(query().queryProduct()); }
        return _nssProduct;
    }
    protected SummaryProductNss _nssSummaryProduct;
    public SummaryProductNss getNssSummaryProduct() {
        if (_nssSummaryProduct == null) { _nssSummaryProduct = new SummaryProductNss(null); }
        return _nssSummaryProduct;
    }
    public SummaryProductNss setupSelect_SummaryProduct() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().querySummaryProduct(); } });
        if (_nssSummaryProduct == null || !_nssSummaryProduct.hasConditionQuery())
        { _nssSummaryProduct = new SummaryProductNss(query().querySummaryProduct()); }
        return _nssSummaryProduct;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;
    public Specification specify() {
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<PurchaseCQ>() {
            public boolean has() { return true; } public PurchaseCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<PurchaseCQ> {
        protected SpQyCall<PurchaseCQ> _myQyCall;
        protected MemberCB.Specification _member;
        protected ProductCB.Specification _product;
        protected SummaryProductCB.Specification _summaryProduct;
        public Specification(ConditionBean baseCB, SpQyCall<PurchaseCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnPurchaseId() { doColumn("PURCHASE_ID"); }
        public void columnMemberId() { doColumn("MEMBER_ID"); }
        public void columnProductId() { doColumn("PRODUCT_ID"); }
        public void columnPurchaseDatetime() { doColumn("PURCHASE_DATETIME"); }
        public void columnPurchaseCount() { doColumn("PURCHASE_COUNT"); }
        public void columnPurchasePrice() { doColumn("PURCHASE_PRICE"); }
        public void columnPaymentCompleteFlg() { doColumn("PAYMENT_COMPLETE_FLG"); }
        public void columnRegisterDatetime() { doColumn("REGISTER_DATETIME"); }
        public void columnRegisterUser() { doColumn("REGISTER_USER"); }
        public void columnRegisterProcess() { doColumn("REGISTER_PROCESS"); }
        public void columnUpdateDatetime() { doColumn("UPDATE_DATETIME"); }
        public void columnUpdateUser() { doColumn("UPDATE_USER"); }
        public void columnUpdateProcess() { doColumn("UPDATE_PROCESS"); }
        public void columnVersionNo() { doColumn("VERSION_NO"); }
        protected void doSpecifyRequiredColumn() {
            columnPurchaseId();// PK
            if (_myQyCall.qy().hasConditionQueryMember()) {
                columnMemberId();// FK
            }
            if (_myQyCall.qy().hasConditionQueryProduct()) {
                columnProductId();// FK
            }
            if (_myQyCall.qy().hasConditionQuerySummaryProduct()) {
                columnProductId();// FK
            }
        }
        protected String getTableDbName() { return "PURCHASE"; }
        public MemberCB.Specification specifyMember() {
            assertForeign("member");
            if (_member == null) {
                _member = new MemberCB.Specification(_baseCB, new SpQyCall<MemberCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryMember(); }
                    public MemberCQ qy() { return _myQyCall.qy().queryMember(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _member;
        }
        public ProductCB.Specification specifyProduct() {
            assertForeign("product");
            if (_product == null) {
                _product = new ProductCB.Specification(_baseCB, new SpQyCall<ProductCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryProduct(); }
                    public ProductCQ qy() { return _myQyCall.qy().queryProduct(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _product;
        }
        public SummaryProductCB.Specification specifySummaryProduct() {
            assertForeign("summaryProduct");
            if (_summaryProduct == null) {
                _summaryProduct = new SummaryProductCB.Specification(_baseCB, new SpQyCall<SummaryProductCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQuerySummaryProduct(); }
                    public SummaryProductCQ qy() { return _myQyCall.qy().querySummaryProduct(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _summaryProduct;
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
    protected String getConditionBeanClassNameInternally() { return PurchaseCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return PurchaseCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
