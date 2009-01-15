package com.example.dbflute.basic.dbflute.cbean.nss;

import org.seasar.dbflute.cbean.ConditionQuery;
import com.example.dbflute.basic.dbflute.cbean.cq.SummaryProductCQ;

/**
 * The nest select set-upper of SUMMARY_PRODUCT.
 * @author DBFlute(AutoGenerator)
 */
public class SummaryProductNss {

    protected SummaryProductCQ _query;
    public SummaryProductNss(SummaryProductCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================
    public ProductStatusNss withProductStatus() {
        _query.doNss(new SummaryProductCQ.NssCall() { public ConditionQuery qf() { return _query.queryProductStatus(); }});
		return new ProductStatusNss(_query.queryProductStatus());
    }

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
}
