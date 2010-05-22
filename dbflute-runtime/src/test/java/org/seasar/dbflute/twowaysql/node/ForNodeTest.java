package org.seasar.dbflute.twowaysql.node;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class ForNodeTest extends PlainTestCase {

    public void test_resolveDynamicForComment_AndNext() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*AND NEXT*/MEMBER_NAME like /*pmb.memberNameList.get(index)*/'foo%'").append(ln());
        sb.append("   /*END FOR*/").append(ln());

        // ## Act ##
        String actual = createTarget(pmb, sb.toString()).resolveDynamicForComment();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END FOR*/"));
        assertFalse(actual.contains("AND NEXT"));
        assertFalse(actual.contains("OR NEXT"));
        assertTrue(actual.contains("  /*IF pmb.memberNameList.size() > 0*/"));
        assertTrue(actual.contains("  MEMBER_NAME like /*pmb.memberNameList.get(0)*/"));
        assertTrue(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(1)*/"));
        assertTrue(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(2)*/"));
        assertFalse(actual.contains("pmb.memberNameList.get(3)"));
        assertFalse(actual.contains("pmb.memberNameList.get(index)"));
        assertTrue(actual.contains("  /*END*/"));
    }

    public void test_resolveDynamicForComment_OrNext() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   (/*FOR pmb.memberNameList*/").append(ln());
        sb.append("    /*OR NEXT*/MEMBER_NAME like /*pmb.memberNameList.get(index)*/'foo%'").append(ln());
        sb.append("   /*END FOR*/)").append(ln());

        // ## Act ##
        String actual = createTarget(pmb, sb.toString()).resolveDynamicForComment();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END FOR*/"));
        assertFalse(actual.contains("AND NEXT"));
        assertFalse(actual.contains("OR NEXT"));
        assertTrue(actual.contains("  MEMBER_NAME like /*pmb.memberNameList.get(0)*/"));
        assertTrue(actual.contains(" or MEMBER_NAME like /*pmb.memberNameList.get(1)*/"));
        assertTrue(actual.contains(" or MEMBER_NAME like /*pmb.memberNameList.get(2)*/"));
        assertFalse(actual.contains("pmb.memberNameList.get(3)"));
        assertFalse(actual.contains("pmb.memberNameList.get(index)"));
    }

    public void test_resolveDynamicForComment_noLineSeparator() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("/*FOR pmb.memberNameList*/");
        sb.append(" /*OR NEXT*/MEMBER_NAME like /*pmb.memberNameList.get(index)*/'foo%'");
        sb.append("/*END FOR*/");

        // ## Act ##
        String actual = createTarget(pmb, sb.toString()).resolveDynamicForComment();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END FOR*/"));
        assertFalse(actual.contains("AND NEXT"));
        assertFalse(actual.contains("OR NEXT"));
        assertTrue(actual.contains(" MEMBER_NAME like /*pmb.memberNameList.get(0)*/"));
        assertTrue(actual.contains(" or MEMBER_NAME like /*pmb.memberNameList.get(1)*/"));
        assertTrue(actual.contains(" or MEMBER_NAME like /*pmb.memberNameList.get(2)*/"));
        assertFalse(actual.contains("pmb.memberNameList.get(3)"));
        assertFalse(actual.contains("pmb.memberNameList.get(index)"));
    }

    public void test_resolveDynamicForComment_emptyList() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(new ArrayList<String>());
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*AND NEXT*/MEMBER_NAME like /*pmb.memberNameList.get(index)*/'foo%'").append(ln());
        sb.append("   /*END FOR*/").append(ln());

        // ## Act ##
        String actual = createTarget(pmb, sb.toString()).resolveDynamicForComment();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END FOR*/"));
        assertFalse(actual.contains("AND NEXT"));
        assertFalse(actual.contains("$$NextOr$$"));
        assertFalse(actual.contains("/*IF pmb.memberNameList.size() > 0*/"));
        assertFalse(actual.contains("  MEMBER_NAME like /*pmb.memberNameList.get(0)*/"));
        assertFalse(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(1)*/"));
        assertFalse(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(2)*/"));
        assertFalse(actual.contains("pmb.memberNameList.get(3)"));
        assertFalse(actual.contains("pmb.memberNameList.get(index)"));
        assertFalse(actual.contains("/*END*/"));
    }

    public void test_resolveDynamicForComment_nullList() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*AND NEXT*/MEMBER_NAME like /*pmb.memberNameList.get(index)*/'foo%'").append(ln());
        sb.append("   /*END FOR*/").append(ln());

        // ## Act ##
        String actual = createTarget(pmb, sb.toString()).resolveDynamicForComment();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END FOR*/"));
        assertFalse(actual.contains("AND NEXT"));
        assertFalse(actual.contains("$$NextOr$$"));
        assertFalse(actual.contains("/*IF pmb.memberNameList.size() > 0*/"));
        assertFalse(actual.contains("  MEMBER_NAME like /*pmb.memberNameList.get(0)*/"));
        assertFalse(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(1)*/"));
        assertFalse(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(2)*/"));
        assertFalse(actual.contains("pmb.memberNameList.get(3)"));
        assertFalse(actual.contains("pmb.memberNameList.get(index)"));
        assertFalse(actual.contains("/*END*/"));
    }

    public void test_resolveDynamicForComment_several() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("abc", "def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*AND NEXT*/MEMBER_NAME like /*pmb.memberNameList.get(index)*/'foo%'").append(ln());
        sb.append("   /*END FOR*/").append(ln());
        sb.append("   /*FOR pmb.memberAccountList*/").append(ln());
        sb.append("   and MEMBER_ACCOUNT like /*pmb.memberAccountList.get(index)*/'foo%'").append(ln());
        sb.append("   /*END FOR*/").append(ln());

        // ## Act ##
        String actual = createTarget(pmb, sb.toString()).resolveDynamicForComment();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END FOR*/"));
        assertFalse(actual.contains("AND NEXT"));
        assertFalse(actual.contains("$$NextOr$$"));
        assertTrue(actual.contains("  /*IF pmb.memberNameList.size() > 0*/"));
        assertTrue(actual.contains("  MEMBER_NAME like /*pmb.memberNameList.get(0)*/"));
        assertTrue(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(1)*/"));
        assertTrue(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(2)*/"));
        assertFalse(actual.contains("pmb.memberNameList.get(3)"));
        assertFalse(actual.contains("pmb.memberNameList.get(index)"));
        assertTrue(actual.contains(" and MEMBER_ACCOUNT like /*pmb.memberAccountList.get(0)*/"));
        assertTrue(actual.contains(" and MEMBER_ACCOUNT like /*pmb.memberAccountList.get(1)*/"));
        assertFalse(actual.contains(" and MEMBER_ACCOUNT like /*pmb.memberAccountList.get(2)*/"));
        assertFalse(actual.contains(" and MEMBER_ACCOUNT like /*pmb.memberAccountList.get(index)*/"));
        assertTrue(actual.contains("  /*END*/"));
    }

    public void test_resolveDynamicForComment_withOtherCondition() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where 1 = 0").append(ln());
        sb.append(" /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*pmb.memberNameList.get(index)*/'foo%'").append(ln());
        sb.append(" /*END FOR*/").append(ln());
        sb.append("   and 0 = 1").append(ln());

        // ## Act ##
        String actual = createTarget(pmb, sb.toString()).resolveDynamicForComment();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where 1 = 0"));
        assertTrue(actual.contains("   and 0 = 1"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END FOR*/"));
        assertFalse(actual.contains("AND NEXT"));
        assertFalse(actual.contains("OR NEXT"));
        assertTrue(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(0)*/"));
        assertTrue(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(1)*/"));
        assertTrue(actual.contains(" and MEMBER_NAME like /*pmb.memberNameList.get(2)*/"));
        assertFalse(actual.contains("pmb.memberNameList.get(3)"));
        assertFalse(actual.contains("pmb.memberNameList.get(index)"));
    }

    protected ForNode createTarget(Object pmb, String dynamicSql) {
        return new ForNode(pmb, dynamicSql);
    }

    protected static class MockPmb {
        protected Integer _memberId;
        protected List<String> _memberNameList;
        protected LikeSearchOption _memberNameListInternalLikeSearchOption;
        protected List<String> _memberAccountList;
        protected MockPmb _nestPmb;
        protected MockPmb _nestLikePmb;
        protected LikeSearchOption _nestLikePmbInternalLikeSearchOption;

        public Integer getMemberId() {
            return _memberId;
        }

        public void setMemberId(Integer memberId) {
            this._memberId = memberId;
        }

        public List<String> getMemberNameList() {
            return _memberNameList;
        }

        public void setMemberNameList(List<String> memberNameList) {
            this._memberNameList = memberNameList;
        }

        public LikeSearchOption getMemberNameListInternalLikeSearchOption() {
            return _memberNameListInternalLikeSearchOption;
        }

        public void setMemberNameListInternalLikeSearchOption(LikeSearchOption memberNameInternalLikeSearchOption) {
            this._memberNameListInternalLikeSearchOption = memberNameInternalLikeSearchOption;
        }

        public List<String> getMemberAccountList() {
            return _memberAccountList;
        }

        public void setMemberAccountList(List<String> memberAccountList) {
            this._memberAccountList = memberAccountList;
        }

        public MockPmb getNestPmb() {
            return _nestPmb;
        }

        public void setNestPmb(MockPmb nestPmb) {
            this._nestPmb = nestPmb;
        }

        public MockPmb getNestLikePmb() {
            return _nestLikePmb;
        }

        public void setNestLikePmb(MockPmb nestLikePmb) {
            this._nestLikePmb = nestLikePmb;
        }

        public LikeSearchOption getNestLikePmbInternalLikeSearchOption() {
            return _nestLikePmbInternalLikeSearchOption;
        }

        public void setNestLikePmbInternalLikeSearchOption(LikeSearchOption nestLikePmbInternalLikeSearchOption) {
            this._nestLikePmbInternalLikeSearchOption = nestLikePmbInternalLikeSearchOption;
        }
    }
}
