package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 * @param <PARAMETER> The type of parameter.
 */
public class HpQDRParameter<CB extends ConditionBean, PARAMETER> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _function;
    protected SubQuery<CB> _subQuery;
    protected DerivedReferrerOption _option;
    protected HpQDRSetupper<CB> _setupper;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpQDRParameter(String function, SubQuery<CB> subQuery, DerivedReferrerOption option,
            HpQDRSetupper<CB> setupper) {
        _function = function;
        _subQuery = subQuery;
        _option = option;
        _setupper = setupper;
    }

    // ===================================================================================
    //                                                                           Condition
    //                                                                           =========
    /**
     * Set up the operand 'equal' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // If the type is Integer...
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).equal(123); // This parameter should be Integer!
     * </pre> 
     * @param value The value of parameter. (NotNull) 
     */
    public void equal(PARAMETER value) {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_EQUAL.getOperand(), value, _option);
    }

    /**
     * Set up the operand 'notEqual' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // If the type is Integer...
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).notEqual(123); // This parameter should be Integer!
     * </pre> 
     * @param value The value of parameter. (NotNull) 
     */
    public void notEqual(PARAMETER value) {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_NOT_EQUAL_STANDARD.getOperand(), value, _option);
    }

    /**
     * Set up the operand 'greaterThan' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // If the type is Integer...
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterThan(123); // This parameter should be Integer!
     * </pre> 
     * @param value The value of parameter. (NotNull) 
     */
    public void greaterThan(PARAMETER value) {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_GREATER_THAN.getOperand(), value, _option);
    }

    /**
     * Set up the operand 'lessThan' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // If the type is Integer...
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).lessThan(123); // This parameter should be Integer!
     * </pre> 
     * @param value The value of parameter. (NotNull) 
     */
    public void lessThan(PARAMETER value) {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_LESS_THAN.getOperand(), value, _option);
    }

    /**
     * Set up the operand 'greaterEqual' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // If the type is Integer...
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).greaterEqual(123); // This parameter should be Integer!
     * </pre> 
     * @param value The value of parameter. (NotNull) 
     */
    public void greaterEqual(PARAMETER value) {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_GREATER_EQUAL.getOperand(), value, _option);
    }

    /**
     * Set up the operand 'lessEqual' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // If the type is Integer...
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).lessEqual(123); // This parameter should be Integer!
     * </pre> 
     * @param value The value of parameter. (NotNull) 
     */
    public void lessEqual(PARAMETER value) {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_LESS_EQUAL.getOperand(), value, _option);
    }

    /**
     * Set up the operand 'isNull' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice();
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).isNull(); // no parameter
     * </pre> 
     */
    public void isNull() {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_IS_NULL.getOperand(), null, _option);
    }

    /**
     * Set up the operand 'isNull' and the value of parameter. <br />
     * The type of the parameter should be same as the type of target column. 
     * <pre>
     * cb.query().scalarPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice();
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }).isNotNull(); // no parameter
     * </pre> 
     */
    public void isNotNull() {
        _setupper.setup(_function, _subQuery, ConditionKey.CK_IS_NOT_NULL.getOperand(), null, _option);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public static boolean isOperandIsNull(String operand) { // basically for auto-detect of inner-join
        return ConditionKey.CK_IS_NULL.getOperand().equals(operand);
    }
}
