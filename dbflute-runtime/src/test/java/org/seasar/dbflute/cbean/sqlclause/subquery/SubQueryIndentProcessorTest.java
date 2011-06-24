package org.seasar.dbflute.cbean.sqlclause.subquery;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.8.6 (2011/06/24 Friday)
 */
public class SubQueryIndentProcessorTest extends PlainTestCase {

    public void test_moveSubQueryEndToRearOnLastLine_basic() throws Exception {
        // ## Arrange ##
        String sqend = "--#df:sqend#memberId_SpecifyDerivedReferrer_PurchaseList.subQueryMapKey1[1]#df:idterm#";
        String exp = "(select ... from ... where ..." + sqend + ") + 123";

        // ## Act ##
        String moved = SubQueryIndentProcessor.moveSubQueryEndToRear(exp);

        // ## Assert ##
        log(moved);
        assertEquals("(select ... from ... where ...) + 123" + sqend, moved);
    }
}
