package org.seasar.dbflute.twowaysql.node;

import org.seasar.dbflute.exception.IfCommentEmptyExpressionException;
import org.seasar.dbflute.exception.IfCommentNotBooleanResultException;
import org.seasar.dbflute.exception.IfCommentNotFoundMethodException;
import org.seasar.dbflute.exception.IfCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.IfCommentNullPointerException;
import org.seasar.dbflute.exception.IfCommentUnsupportedExpressionException;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * 
 * @author jflute
 * @since 0.9.5.5 (2009/10/01 Thursday)
 */
public class IfCommentEvaluatorTest extends PlainTestCase {

    // ===================================================================================
    //                                                                                Null
    //                                                                                ====
    public void test_evaluate_isNotNull() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "pmb.memberName != null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_isNull() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "pmb.memberName == null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertFalse(evaluator.evaluate());
        pmb.setMemberName(null);
        assertTrue(evaluator.evaluate());
    }

    // ===================================================================================
    //                                                                             Boolean
    //                                                                             =======
    public void test_evaluate_boolean_property() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "pmb.existsPurchase";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_boolean_property_not() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "!pmb.existsPurchase";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertFalse(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_boolean_method() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "pmb.isExistsPurchase()";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_boolean_method_not() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "!pmb.isExistsPurchase()";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertFalse(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertTrue(evaluator.evaluate());
    }

    // ===================================================================================
    //                                                                              And/Or
    //                                                                              ======
    public void test_evaluate_and() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        String expression = "pmb.memberId != null && pmb.memberName != null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(null);
        pmb.setMemberName("bar");
        assertFalse(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_and_many() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        pmb.setExistsPurchase(true);
        String expression = "pmb.memberId != null && pmb.memberName != null && pmb.existsPurchase";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberName("bar");
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(null);
        pmb.setMemberName(null);
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        pmb.setExistsPurchase(true);
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_or() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        String expression = "pmb.memberId != null || pmb.memberName != null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertTrue(evaluator.evaluate());
        pmb.setMemberId(null);
        pmb.setMemberName("bar");
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_or_many() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        NextPmb nextPmb = new NextPmb();
        nextPmb.setExistsLogin(true);
        pmb.setNextPmb(nextPmb);
        String expression = "pmb.memberId != null || pmb.memberName != null || pmb.nextPmb.existsLogin";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.getNextPmb().setExistsLogin(false);
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertTrue(evaluator.evaluate());
        pmb.setMemberId(null);
        assertFalse(evaluator.evaluate());
        pmb.getNextPmb().setExistsLogin(true);
        assertTrue(evaluator.evaluate());
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    public void test_evaluate_notFoundMethodProperty() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.getMemberNameNon() != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNotFoundMethodException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.memberNameNon != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNotFoundPropertyException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentEmptyExpressionException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act ##
        try {
            createEvaluator(pmb, "").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentEmptyExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, " ").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentEmptyExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, null).evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentEmptyExpressionException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentUnsupportedExpressionException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act ##
        try {
            createEvaluator(pmb, "(pmb.fooId != null || pmb.fooName != null)").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.fooId != null || pmb.fooName != null && pmb.barId != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "(pmb.fooId != null || pmb.fooName != null) && pmb.barId != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.fooId < 3").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.fooId > 3").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.fooId >= 3").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.fooId <= 3").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.fooName == 'Pixy'").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.fooName == \"Pixy\"").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentNullPointerException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.nextPmb.existsLogin").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNullPointerException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(null, "pmb.nextPmb.existsLogin").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNullPointerException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.nextPmb.memberStatusCode").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNullPointerException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentNotBooleanResultException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        NextPmb nextPmb = new NextPmb();
        pmb.setNextPmb(nextPmb);

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.nextPmb.memberStatusCode").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNotBooleanResultException e) {
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected IfCommentEvaluator createEvaluator(Object pmb, String expression) {
        return new IfCommentEvaluator(pmb, expression, "select foo from bar");
    }

    protected static class BasePmb {
        private Integer _memberId;
        private String _memberName;
        private boolean _existsPurchase;
        private NextPmb _nextPmb;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName()).append(":");
            sb.append(xbuildColumnString());
            return sb.toString();
        }

        private String xbuildColumnString() {
            final String delimiter = ",";
            final StringBuilder sb = new StringBuilder();
            sb.append(delimiter).append(_memberId);
            sb.append(delimiter).append(_memberName);
            sb.append(delimiter).append(_existsPurchase);
            if (sb.length() > 0) {
                sb.delete(0, delimiter.length());
            }
            sb.insert(0, "{").append("}");
            return sb.toString();
        }

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
        private String _memberStatusCode;
        private boolean _existsLogin;

        public String getMemberStatusCode() {
            return _memberStatusCode;
        }

        public void setMemberStatusCode(String memberStatusCode) {
            this._memberStatusCode = memberStatusCode;
        }

        public boolean isExistsLogin() {
            return _existsLogin;
        }

        public void setExistsLogin(boolean existsLogin) {
            this._existsLogin = existsLogin;
        }
    }
}
