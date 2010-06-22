package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpColQyOperand<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final HpColQyHandler<CB> _handler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpColQyOperand(HpColQyHandler<CB> handler) {
        _handler = handler;
    }

    // ===================================================================================
    //                                                                          Comparison
    //                                                                          ==========
    /**
     * Equal. {=}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void equal(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ConditionKey.CK_EQUAL.getOperand());
    }

    /**
     * NotEqual. {&lt;&gt;}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void notEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ConditionKey.CK_NOT_EQUAL_STANDARD.getOperand());
    }

    /**
     * GreaterThan. {&gt;}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void greaterThan(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ConditionKey.CK_GREATER_THAN.getOperand());
    }

    /**
     * LessThan. {&lt;}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void lessThan(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ConditionKey.CK_LESS_THAN.getOperand());
    }

    /**
     * GreaterEqual. {&gt;=}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void greaterEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ConditionKey.CK_GREATER_EQUAL.getOperand());
    }

    /**
     * LessThan. {&lt;=}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void lessEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ConditionKey.CK_LESS_EQUAL.getOperand());
    }
}
