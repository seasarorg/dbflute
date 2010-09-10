package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SubQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpQDRSetupper<CB extends ConditionBean> {

    void setup(String function, Object coalesce, SubQuery<CB> subQuery, String operand, Object value);
}
