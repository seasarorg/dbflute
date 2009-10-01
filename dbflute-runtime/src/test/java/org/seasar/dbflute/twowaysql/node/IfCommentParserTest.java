package org.seasar.dbflute.twowaysql.node;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * 
 * @author jflute
 * @since 0.9.5.5 (2009/10/01 Thursday)
 */
public class IfCommentParserTest extends PlainTestCase {

    // ===================================================================================
    //                                                                                Null
    //                                                                                ====
    public void test_parse_isNotNull() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "pmb.memberName != null";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertTrue(parser.parse());
        pmb.setMemberName(null);
        assertFalse(parser.parse());
    }

    public void test_parse_isNull() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "pmb.memberName == null";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertFalse(parser.parse());
        pmb.setMemberName(null);
        assertTrue(parser.parse());
    }

    // ===================================================================================
    //                                                                             Boolean
    //                                                                             =======
    public void test_parse_boolean_property() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "pmb.existsPurchase";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertTrue(parser.parse());
        pmb.setExistsPurchase(false);
        assertFalse(parser.parse());
    }

    public void test_parse_boolean_property_not() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "!pmb.existsPurchase";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertFalse(parser.parse());
        pmb.setExistsPurchase(false);
        assertTrue(parser.parse());
    }

    public void test_parse_boolean_method() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "pmb.isExistsPurchase()";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertTrue(parser.parse());
        pmb.setExistsPurchase(false);
        assertFalse(parser.parse());
    }

    public void test_parse_boolean_method_not() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "!pmb.isExistsPurchase()";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertFalse(parser.parse());
        pmb.setExistsPurchase(false);
        assertTrue(parser.parse());
    }

    // ===================================================================================
    //                                                                              And/Or
    //                                                                              ======
    public void test_parse_and() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        String expression = "pmb.memberId != null && pmb.memberName != null";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertTrue(parser.parse());
        pmb.setMemberName(null);
        assertFalse(parser.parse());
        pmb.setMemberId(null);
        pmb.setMemberName("bar");
        assertFalse(parser.parse());
        pmb.setMemberName(null);
        assertFalse(parser.parse());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        assertTrue(parser.parse());
    }

    public void test_parse_and_many() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        pmb.setExistsPurchase(true);
        String expression = "pmb.memberId != null && pmb.memberName != null && pmb.existsPurchase";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertTrue(parser.parse());
        pmb.setMemberName(null);
        assertFalse(parser.parse());
        pmb.setMemberName("bar");
        pmb.setExistsPurchase(false);
        assertFalse(parser.parse());
        pmb.setMemberId(null);
        pmb.setMemberName(null);
        pmb.setExistsPurchase(false);
        assertFalse(parser.parse());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        pmb.setExistsPurchase(true);
        assertTrue(parser.parse());
    }

    public void test_parse_or() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        String expression = "pmb.memberId != null || pmb.memberName != null";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertTrue(parser.parse());
        pmb.setMemberName(null);
        assertTrue(parser.parse());
        pmb.setMemberId(null);
        pmb.setMemberName("bar");
        assertTrue(parser.parse());
        pmb.setMemberName(null);
        assertFalse(parser.parse());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        assertTrue(parser.parse());
    }

    public void test_parse_or_many() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        NextPmb nextPmb = new NextPmb();
        nextPmb.setExistsLogin(true);
        pmb.setNextPmb(nextPmb);
        String expression = "pmb.memberId != null || pmb.memberName != null || pmb.nextPmb.existsLogin";
        IfCommentParser parser = createParser(pmb, expression);

        // ## Act && Assert ##
        assertTrue(parser.parse());
        pmb.getNextPmb().setExistsLogin(false);
        assertTrue(parser.parse());
        pmb.setMemberName(null);
        assertTrue(parser.parse());
        pmb.setMemberId(null);
        assertFalse(parser.parse());
        pmb.getNextPmb().setExistsLogin(true);
        assertTrue(parser.parse());
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected IfCommentParser createParser(Object pmb, String expression) {
        return new IfCommentParser(pmb, expression, "select foo from bar");
    }

    protected static class BasePmb {
        private Integer _memberId;
        private String _memberName;
        private boolean _existsPurchase;
        private NextPmb _nextPmb;

        public Integer getMemberId() {
            return _memberId;
        }

        public void setMemberId(Integer memberId) {
            _memberId = memberId;
        }

        public String getMemberName() {
            return _memberName;
        }

        public void setMemberName(String memberName) {
            _memberName = memberName;
        }

        public boolean isExistsPurchase() {
            return _existsPurchase;
        }

        public void setExistsPurchase(boolean existsPurchase) {
            this._existsPurchase = existsPurchase;
        }

        public NextPmb getNextPmb() {
            return _nextPmb;
        }

        public void setNextPmb(NextPmb nextPmb) {
            this._nextPmb = nextPmb;
        }
    }

    protected static class NextPmb {
        private boolean _existsLogin;

        public boolean isExistsLogin() {
            return _existsLogin;
        }

        public void setExistsLogin(boolean existsLogin) {
            this._existsLogin = existsLogin;
        }
    }
}
