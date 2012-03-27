package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

/**
 * The option for ScalarCondition (the old name: ScalarSubQuery).
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpSSQOption<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected SpecifyQuery<CB> _partitionBySpecify;
    protected CB _partitionByCBean;

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    public boolean hasPartitionBy() {
        return _partitionBySpecify != null;
    }

    public SqlClause preparePartitionBySqlClause() {
        if (_partitionBySpecify == null) {
            return null;
        }
        _partitionBySpecify.specify(_partitionByCBean);
        return _partitionByCBean.getSqlClause();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public SpecifyQuery<CB> getPartitionBySpecify() {
        return _partitionBySpecify;
    }

    public void setPartitionBySpecify(SpecifyQuery<CB> partitionBySpecify) {
        this._partitionBySpecify = partitionBySpecify;
    }

    public CB getPartitionByCBean() {
        return _partitionByCBean;
    }

    public void setPartitionByCBean(CB partitionByCBean) {
        this._partitionByCBean = partitionByCBean;
    }
}
