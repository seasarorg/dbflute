package com.example.dbflute.basic.dbflute.various;

import java.util.List;

import org.dbflute.bhv.ConditionBeanSetupper;
import org.dbflute.cbean.ListResultBean;

import com.example.dbflute.basic.dbflute.cbean.ProductStatusCB;
import com.example.dbflute.basic.dbflute.cbean.PurchaseCB;
import com.example.dbflute.basic.dbflute.cbean.SummaryProductCB;
import com.example.dbflute.basic.dbflute.exbhv.ProductStatusBhv;
import com.example.dbflute.basic.dbflute.exbhv.SummaryProductBhv;
import com.example.dbflute.basic.dbflute.exentity.ProductStatus;
import com.example.dbflute.basic.dbflute.exentity.Purchase;
import com.example.dbflute.basic.dbflute.exentity.SummaryProduct;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.7.7 (2008/07/23 Wednesday)
 */
public class ViewTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private SummaryProductBhv summaryProductBhv;
    private ProductStatusBhv productStatusBhv;

    // ===================================================================================
    //                                                                       Relation Test
    //                                                                       =============
    public void test_setupSelect_Tx() {
        // ## Arrange ##
        SummaryProductCB cb = new SummaryProductCB();
        cb.setupSelect_ProductStatus();

        // ## Act ##
        ListResultBean<SummaryProduct> productList = summaryProductBhv.selectList(cb);
        
        // ## Assert ##
        for (SummaryProduct product : productList) {
            assertNotNull(product.getProductStatus());
        }
    }
    
    public void test_local_loadReferrer_Tx() {
        // ## Arrange ##
        SummaryProductCB cb = new SummaryProductCB();
        ListResultBean<SummaryProduct> summaryProductList = summaryProductBhv.selectList(cb);
        
        // ## Act ##
        summaryProductBhv.loadPurchaseList(summaryProductList, new ConditionBeanSetupper<PurchaseCB>() {
            public void setup(PurchaseCB cb) {
                cb.query().addOrderBy_PurchaseDatetime_Desc();
            }
        });
        
        // ## Assert ##
        boolean existsPurchase = false;
        for (SummaryProduct summaryProduct : summaryProductList) {
            log(summaryProduct);
            List<Purchase> purchaseList = summaryProduct.getPurchaseList();
            for (Purchase purchase : purchaseList) {
                log("    " + purchase.toString());
                existsPurchase = true;
            }
        }
        assertTrue(existsPurchase);
    }
    
    public void test_foreign_loadReferrer_Tx() {
        // ## Arrange ##
        ProductStatusCB cb = new ProductStatusCB();
        ListResultBean<ProductStatus> productStatusList = productStatusBhv.selectList(cb);
        
        // ## Act ##
        productStatusBhv.loadSummaryProductList(productStatusList, new ConditionBeanSetupper<SummaryProductCB>() {
            public void setup(SummaryProductCB cb) {
                cb.query().addOrderBy_LatestPurchaseDatetime_Desc();
            }
        });
        
        // ## Assert ##
        boolean existsSummaryProduct = false;
        for (ProductStatus productStatus : productStatusList) {
            log(productStatus);
            List<SummaryProduct> summaryProductList = productStatus.getSummaryProductList();
            for (SummaryProduct summaryProduct : summaryProductList) {
                log("    " + summaryProduct.toString());
                existsSummaryProduct = true;
            }
        }
        assertTrue(existsSummaryProduct);
    }
}
