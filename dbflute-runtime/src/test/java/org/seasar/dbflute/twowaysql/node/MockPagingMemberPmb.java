package org.seasar.dbflute.twowaysql.node;

import org.seasar.dbflute.cbean.SimplePagingBean;

/**
 * @author jflute
 */
public class MockPagingMemberPmb extends SimplePagingBean {

    protected Integer _memberId;
    protected String _memberName;

    public Integer getMemberId() {
        return _memberId;
    }

    public void setMemberId(Integer memberId) {
        this._memberId = memberId;
    }

    public String getMemberName() {
        return _memberName;
    }

    public void setMemberName(String memberName) {
        this._memberName = memberName;
    }
}
