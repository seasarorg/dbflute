package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpColQyHandler<CB extends ConditionBean> {
    void handle(SpecifyQuery<CB> rightSp, String operand);
}
