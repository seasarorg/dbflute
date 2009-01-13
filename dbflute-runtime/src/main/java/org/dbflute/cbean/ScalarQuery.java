package org.dbflute.cbean;

public interface ScalarQuery<CB extends ConditionBean> {
    public void query(CB cb);
}
