package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpColQyOperand<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected HpColQyHandler<CB> _handler;

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
        _handler.handle(rightSpecifyQuery, "=");
    }

    /**
     * GreaterThan. {&gt;}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void greaterThan(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ">");
    }

    /**
     * LessThan. {&lt;}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void lessThan(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, "<");
    }

    /**
     * GreaterEqual. {&gt;=}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void greaterEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ">=");
    }

    /**
     * LessThan. {&lt;=}
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void lessEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, "<=");
    }
}
