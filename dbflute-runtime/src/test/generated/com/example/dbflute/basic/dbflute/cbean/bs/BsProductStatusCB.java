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

/**
 * The base condition-bean of PRODUCT_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public class BsProductStatusCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected ProductStatusCQ _conditionQuery;

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
        return "PRODUCT_STATUS";
    }

    public String getTableSqlName() {
        return "PRODUCT_STATUS";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("PRODUCT_STATUS_CODE");
            if (obj instanceof String) {
                query().setProductStatusCode_Equal((String)obj);
            } else {
                checkTypeString(obj, "productStatusCode", "String");
                query().setProductStatusCode_Equal((String)obj);
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_ProductStatusCode_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_ProductStatusCode_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public ProductStatusCQ query() {
        return getConditionQuery();
    }

    public ProductStatusCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new ProductStatusCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
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
     * cb.query().union(new UnionQuery&lt;ProductStatusCB&gt;() {
     *     public void query(ProductStatusCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<ProductStatusCB> unionQuery) {
        final ProductStatusCB cb = new ProductStatusCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final ProductStatusCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;ProductStatusCB&gt;() {
     *     public void query(ProductStatusCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<ProductStatusCB> unionQuery) {
        final ProductStatusCB cb = new ProductStatusCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final ProductStatusCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;
    public Specification specify() {
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<ProductStatusCQ>() {
            public boolean has() { return true; } public ProductStatusCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<ProductStatusCQ> {
        protected SpQyCall<ProductStatusCQ> _myQyCall;
        public Specification(ConditionBean baseCB, SpQyCall<ProductStatusCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnProductStatusCode() { doColumn("PRODUCT_STATUS_CODE"); }
        public void columnProductStatusName() { doColumn("PRODUCT_STATUS_NAME"); }
        protected void doSpecifyRequiredColumn() {
            columnProductStatusCode();// PK
        }
        protected String getTableDbName() { return "PRODUCT_STATUS"; }
        public RAFunction<ProductCB, ProductStatusCQ> derivedProductList() {
            return new RAFunction<ProductCB, ProductStatusCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<ProductCB, ProductStatusCQ>() {
                public void setup(String function, SubQuery<ProductCB> subQuery, ProductStatusCQ cq, String aliasName) {
                    cq.xsderiveProductList(function, subQuery, aliasName); } }, _dbmetaProvider);
        }
        public RAFunction<SummaryProductCB, ProductStatusCQ> derivedSummaryProductList() {
            return new RAFunction<SummaryProductCB, ProductStatusCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<SummaryProductCB, ProductStatusCQ>() {
                public void setup(String function, SubQuery<SummaryProductCB> subQuery, ProductStatusCQ cq, String aliasName) {
                    cq.xsderiveSummaryProductList(function, subQuery, aliasName); } }, _dbmetaProvider);
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
    protected String getConditionBeanClassNameInternally() { return ProductStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return ProductStatusCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
