package com.example.dbflute.basic.dbflute.cbean.nss;

import org.seasar.dbflute.cbean.ConditionQuery;
import com.example.dbflute.basic.dbflute.cbean.cq.ProductCQ;

/**
 * The nest select set-upper of PRODUCT.
 * @author DBFlute(AutoGenerator)
 */
public class ProductNss {

    protected ProductCQ _query;
    public ProductNss(ProductCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================
    public ProductStatusNss withProductStatus() {
        _query.doNss(new ProductCQ.NssCall() { public ConditionQuery qf() { return _query.queryProductStatus(); }});
		return new ProductStatusNss(_query.queryProductStatus());
    }

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
}
