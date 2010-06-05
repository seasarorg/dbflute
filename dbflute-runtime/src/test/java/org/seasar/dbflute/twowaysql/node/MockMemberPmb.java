package org.seasar.dbflute.twowaysql.node;

import java.util.List;

/**
 * @author jflute
 */
public class MockMemberPmb {

    protected Integer memberId;
    protected String memberName;
    protected List<String> memberNameList;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public List<String> getMemberNameList() {
        return memberNameList;
    }

    public void setMemberNameList(List<String> memberNameList) {
        this.memberNameList = memberNameList;
    }
}
