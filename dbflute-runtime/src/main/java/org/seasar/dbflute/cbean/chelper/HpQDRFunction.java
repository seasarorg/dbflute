package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SubQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpQDRFunction<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final HpQDRSetupper<CB> _setupper;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpQDRFunction(HpQDRSetupper<CB> setupper) {
        _setupper = setupper;
    }

    // ===================================================================================
    //                                                                            Function
    //                                                                            ========
    /**
     * Set up the sub query of referrer for the scalar 'count'.
     * <pre>
     * cb.query().scalarPurchaseList().count(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseId(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); // *Don't forget the parameter!
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull) 
     * @return The parameter for comparing with scalar. (NotNull)
     */
    public HpQDRParameter<CB, Integer> count(SubQuery<CB> subQuery) {
        return count(subQuery, null);
    }

    public HpQDRParameter<CB, Integer> count(SubQuery<CB> subQuery, Object coalesce) {
        return new HpQDRParameter<CB, Integer>("count", coalesce, subQuery, _setupper);
    }

    /**
     * Set up the sub query of referrer for the scalar 'count(with distinct)'.
     * <pre>
     * cb.query().scalarPurchaseList().countDistinct(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); // *Don't forget the parameter!
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull) 
     * @return The parameter for comparing with scalar. (NotNull)
     */
    public HpQDRParameter<CB, Integer> countDistinct(SubQuery<CB> subQuery) {
        return countDistinct(subQuery, null);
    }

    public HpQDRParameter<CB, Integer> countDistinct(SubQuery<CB> subQuery, Object coalesce) {
        return new HpQDRParameter<CB, Integer>("count(distinct", coalesce, subQuery, _setupper);
    }

    /**
     * Set up the sub query of referrer for the scalar 'max'.
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); // *Don't forget the parameter!
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull) 
     * @return The parameter for comparing with scalar. (NotNull)
     */
    public HpQDRParameter<CB, Object> max(SubQuery<CB> subQuery) {
        return max(subQuery, null);
    }

    public HpQDRParameter<CB, Object> max(SubQuery<CB> subQuery, Object coalesce) {
        return new HpQDRParameter<CB, Object>("max", coalesce, subQuery, _setupper);
    }

    /**
     * Set up the sub query of referrer for the scalar 'min'.
     * <pre>
     * cb.query().scalarPurchaseList().min(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); // *Don't forget the parameter!
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull) 
     * @return The parameter for comparing with scalar. (NotNull)
     */
    public HpQDRParameter<CB, Object> min(SubQuery<CB> subQuery) {
        return min(subQuery, null);
    }

    public HpQDRParameter<CB, Object> min(SubQuery<CB> subQuery, Object coalesce) {
        return new HpQDRParameter<CB, Object>("min", coalesce, subQuery, _setupper);
    }

    /**
     * Set up the sub query of referrer for the scalar 'sum'.
     * <pre>
     * cb.query().scalarPurchaseList().sum(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); // *Don't forget the parameter!
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull) 
     * @return The parameter for comparing with scalar. (NotNull)
     */
    public HpQDRParameter<CB, Number> sum(SubQuery<CB> subQuery) {
        return sum(subQuery, null);
    }

    public HpQDRParameter<CB, Number> sum(SubQuery<CB> subQuery, Object coalesce) {
        return new HpQDRParameter<CB, Number>("sum", coalesce, subQuery, _setupper);
    }

    /**
     * Set up the sub query of referrer for the scalar 'avg'.
     * <pre>
     * cb.query().scalarPurchaseList().avg(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); // *Don't forget the parameter!
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull) 
     * @return The parameter for comparing with scalar. (NotNull)
     */
    public HpQDRParameter<CB, Number> avg(SubQuery<CB> subQuery) {
        return avg(subQuery, null);
    }

    public HpQDRParameter<CB, Number> avg(SubQuery<CB> subQuery, Object coalesce) {
        return new HpQDRParameter<CB, Number>("avg", coalesce, subQuery, _setupper);
    }
}
