package org.seasar.dbflute.logic.outsidesqltest.check;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.seasar.dbflute.exception.DfCustomizeEntityMarkInvalidException;
import org.seasar.dbflute.exception.DfParameterBeanMarkInvalidException;
import org.seasar.dbflute.logic.outsidesqltest.DfOutsideSqlChecker;
import org.seasar.dbflute.twowaysql.exception.EndCommentNotFoundException;
import org.seasar.dbflute.twowaysql.exception.IfCommentConditionEmptyException;
import org.seasar.dbflute.twowaysql.exception.IfCommentUnsupportedExpressionException;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/25 Sunday)
 */
public class OutsideSqlCheckerTest extends PlainTestCase {

    @Test
    public void test_check_basic() {
        // ## Arrange ##
        DfOutsideSqlChecker ker = new DfOutsideSqlChecker();
        String fn = "test.sql";

        // ## Act & Assert ##
        ker.check(fn, "-- #df:entity#\n-- !df:pmb!\nfoo /*IF pmb.memberId != null*/bar/*END*/");
        ker.check(fn, "-- #df:entity#\n-- !df:pmb!\nfoo /*IF pmb.memberId != null && pmb.existsPurchase*/bar/*END*/");
        ker.check(fn, "-- #df:entity#\nfoo /*IF pmb.getMemberId() != null || pmb.isExistsPurchase()*/bar/*END*/");
        ker.check(fn, "-- !df:pmb!\nfoo /*IF pmb.memberName == 'abc'*/bar/*END*/");
    }

    @Test
    public void test_check_customizeEntity() {
        // ## Arrange ##
        DfOutsideSqlChecker ker = new DfOutsideSqlChecker();
        String fn = "test.sql";

        // ## Act ##
        try {
            ker.check(fn, "-- #df;entity#\n-- !df:pmb!\nfoo /*IF pmb.memberId != null*/bar/*END*/");

            // ## Assert ##
            fail();
        } catch (DfCustomizeEntityMarkInvalidException e) {
            // OK
            log(e.getMessage());
        }
        // ## Act ##
        try {
            ker.check(fn, "-- #df:pmb#\n-- !df:pmb!\nfoo /*IF pmb.memberId != null*/bar/*END*/");

            // ## Assert ##
            fail();
        } catch (DfCustomizeEntityMarkInvalidException e) {
            // OK
            log(e.getMessage());
        }
        // ## Act ##
        try {
            ker.check(fn, "-- #df:emtity#\n-- !df:pmb!\nfoo /*IF pmb.memberId != null*/bar/*END*/");

            // ## Assert ##
            fail();
        } catch (DfCustomizeEntityMarkInvalidException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_check_parameterBean() {
        // ## Arrange ##
        DfOutsideSqlChecker ker = new DfOutsideSqlChecker();
        String fn = "test.sql";

        // ## Act ##
        try {
            ker.check(fn, "-- #df:entity#\n-- !df;pmb!\nfoo /*IF pmb.memberId != null*/bar/*END*/");

            // ## Assert ##
            fail();
        } catch (DfParameterBeanMarkInvalidException e) {
            // OK
            log(e.getMessage());
        }
        // ## Act ##
        try {
            ker.check(fn, "-- #df:entity#\n-- !df:entity!\nfoo /*IF pmb.memberId != null*/bar/*END*/");

            // ## Assert ##
            fail();
        } catch (DfParameterBeanMarkInvalidException e) {
            // OK
            log(e.getMessage());
        }
        // ## Act ##
        try {
            ker.check(fn, "-- #df:entity#\n-- !df:pnb!\nfoo /*IF pmb.memberId != null*/bar/*END*/");

            // ## Assert ##
            fail();
        } catch (DfParameterBeanMarkInvalidException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_check_parameterComment() {
        // ## Arrange ##
        DfOutsideSqlChecker ker = new DfOutsideSqlChecker();
        String fn = "test.sql";

        // ## Act ##
        try {
            ker.check(fn, "-- #df:entity#\n-- !df:pmb!\nfoo /*IF pmb.memberId != null*/bar");

            // ## Assert ##
            fail();
        } catch (EndCommentNotFoundException e) {
            // OK
            log(e.getMessage());
        }
        // ## Act ##
        try {
            ker.check(fn, "-- #df:entity#\n-- !df:pmb!\nfoo /*IF */bar/*END*/");

            // ## Assert ##
            fail();
        } catch (IfCommentConditionEmptyException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_check_ifCommentExpression_basic() {
        // ## Arrange ##
        DfOutsideSqlChecker ker = new DfOutsideSqlChecker();
        String fn = "test.sql";

        // ## Act & Assert ##
        ker.check(fn, "/*IF pmb.memberId != null && pmb.memberName != null*/bar/*END*/");
        ker.check(fn, "/*IF pmb.memberId != null || pmb.memberName != null*/bar/*END*/");
        ker.check(fn, "/*IF pmb.getMemberId() != null || pmb.memberName != null*/bar/*END*/");
    }

    @Test
    public void test_check_ifCommentExpression_unsupported() {
        // ## Arrange ##
        DfOutsideSqlChecker ker = new DfOutsideSqlChecker();
        String fn = "test.sql";

        // ## Act & Assert ##
        try {
            ker.check(fn, "/*IF (pmb.memberId != null && pmb.memberName != null) || pmb.exists*/bar/*END*/");
        } catch (IfCommentUnsupportedExpressionException e) {
            // OK
            log(e.getMessage());
        }
        try {
            ker.check(fn, "/*IF pmb.memberId != null && pmb.memberName != null || pmb.exists*/bar/*END*/");
        } catch (IfCommentUnsupportedExpressionException e) {
            // OK
            log(e.getMessage());
        }
        try {
            ker.check(fn, "/*IF pmb.memberId = null && pmb.memberName != null*/bar/*END*/");
        } catch (IfCommentUnsupportedExpressionException e) {
            // OK
            log(e.getMessage());
        }
        try {
            ker.check(fn, "/*IF pmb.memberId <> null && pmb.memberName != null*/bar/*END*/");
        } catch (IfCommentUnsupportedExpressionException e) {
            // OK
            log(e.getMessage());
        }
        try {
            ker.check(fn, "/*IF pmb.memberName == \"abc\"*/bar/*END*/");
        } catch (IfCommentUnsupportedExpressionException e) {
            // OK
            log(e.getMessage());
        }
    }
}
