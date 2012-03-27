package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SubQuery;

/**
 * The function for ScalarCondition (the old name: ScalarSubQuery).
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpSSQFunction<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final HpSSQSetupper<CB> _setupper;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSSQFunction(HpSSQSetupper<CB> setupper) {
        _setupper = setupper;
    }

    // ===================================================================================
    //                                                                            Function
    //                                                                            ========
    /**
     * Set up the sub query of myself for the scalar 'max'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #FD4747">max</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> max(SubQuery<CB> subQuery) {
        assertSubQuery(subQuery);
        final HpSSQOption<CB> option = createOption();
        _setupper.setup("max", subQuery, option);
        return createDecorator(option);
    }

    /**
     * Set up the sub query of myself for the scalar 'min'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #FD4747">min</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> min(SubQuery<CB> subQuery) {
        assertSubQuery(subQuery);
        final HpSSQOption<CB> option = createOption();
        _setupper.setup("min", subQuery, option);
        return createDecorator(option);
    }

    /**
     * Set up the sub query of myself for the scalar 'sum'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #FD4747">sum</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> sum(SubQuery<CB> subQuery) {
        assertSubQuery(subQuery);
        final HpSSQOption<CB> option = createOption();
        _setupper.setup("sum", subQuery, option);
        return createDecorator(option);
    }

    /**
     * Set up the sub query of myself for the scalar 'avg'.
     * <pre>
     * cb.query().scalar_Equal().<span style="color: #FD4747">avg</span>(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnPurchasePrice</span>(); <span style="color: #3F7E5E">// *Point!</span>
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull)
     * @return The decorator of ScalarCondition. You can use partition-by. (NotNull)
     */
    public HpSSQDecorator<CB> avg(SubQuery<CB> subQuery) {
        assertSubQuery(subQuery);
        final HpSSQOption<CB> option = createOption();
        _setupper.setup("avg", subQuery, option);
        return createDecorator(option);
    }

    protected HpSSQOption<CB> createOption() {
        return new HpSSQOption<CB>();
    }

    protected HpSSQDecorator<CB> createDecorator(HpSSQOption<CB> option) {
        return new HpSSQDecorator<CB>(option);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected void assertSubQuery(SubQuery<?> subQuery) {
        if (subQuery == null) {
            String msg = "The argument 'subQuery' for ScalarCondition should not be null.";
            throw new IllegalArgumentException(msg);
        }
    }
}
