package org.seasar.dbflute.cbean;

import java.util.Date;
import java.util.List;

import org.seasar.dbflute.cbean.ManualOrderBean.CaseWhenElement;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.8.2 (2011/04/08 Friday)
 */
public class ManualOrderBeanTest extends PlainTestCase {

    public void testname() throws Exception {
        // ## Arrange ##
        Date fromDate = DfTypeUtil.toDate("1969/01/01");
        Date toDate = DfTypeUtil.toDate("1970/12/31");
        ManualOrderBean mob = new ManualOrderBean();

        // ## Act ##
        mob.when_DateFromTo(fromDate, toDate);

        // ## Assert ##
        List<CaseWhenElement> caseWhenAcceptedList = mob.getCaseWhenAcceptedList();
        for (CaseWhenElement element : caseWhenAcceptedList) {
            log(element);
        }
    }
}
