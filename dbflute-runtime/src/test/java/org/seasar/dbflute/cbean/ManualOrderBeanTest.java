package org.seasar.dbflute.cbean;

import java.util.Date;
import java.util.List;

import org.seasar.dbflute.cbean.ManualOrderBean.CaseWhenElement;
import org.seasar.dbflute.cbean.ManualOrderBean.FreeParameterManualOrderThemeListHandler;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.8.2 (2011/04/08 Friday)
 */
public class ManualOrderBeanTest extends PlainTestCase {

    public void test_DateFromTo() throws Exception {
        // ## Arrange ##
        Date fromDate = DfTypeUtil.toDate("1969/01/01");
        Date toDate = DfTypeUtil.toDate("1970/12/31");
        ManualOrderBean mob = new ManualOrderBean();

        // ## Act ##
        mob.when_DateFromTo(fromDate, toDate);

        // ## Assert ##
        List<CaseWhenElement> caseWhenAcceptedList = mob.getCaseWhenAcceptedList();
        {
            assertEquals(1, caseWhenAcceptedList.size());
            final CaseWhenElement fromElement = caseWhenAcceptedList.get(0);
            assertEquals(ConditionKey.CK_GREATER_EQUAL, fromElement.getConditionKey());
            assertNull(fromElement.getConnectionMode());
            CaseWhenElement toElement = fromElement.getConnectedElementList().get(0);
            assertEquals(ConditionKey.CK_LESS_THAN, toElement.getConditionKey());
            assertEquals(ManualOrderBean.ConnectionMode.AND, toElement.getConnectionMode());
            assertEquals(0, mob.getCaseWhenBoundList().size());

            String fromExp = DfTypeUtil.toString(fromElement.getOrderValue(), "yyyy/MM/dd");
            String toExp = DfTypeUtil.toString(toElement.getOrderValue(), "yyyy/MM/dd");
            assertEquals("1969/01/01", fromExp);
            assertEquals("1971/01/01", toExp);
        }

        // ## Act ##
        mob.bind(new FreeParameterManualOrderThemeListHandler() {
            int index = 0;

            public String register(String themeKey, Object orderValue) {
                ++index;
                return "foo" + index;
            }
        });

        // ## Assert ##
        List<CaseWhenElement> caseWhenBoundList = mob.getCaseWhenBoundList();
        {
            assertNotSame(0, caseWhenBoundList.size());
            assertEquals(1, caseWhenBoundList.size());
            final CaseWhenElement fromElement = caseWhenBoundList.get(0);
            assertEquals(ConditionKey.CK_GREATER_EQUAL, fromElement.getConditionKey());
            assertNull(fromElement.getConnectionMode());
            CaseWhenElement toElement = fromElement.getConnectedElementList().get(0);
            assertEquals(ConditionKey.CK_LESS_THAN, toElement.getConditionKey());
            assertEquals(ManualOrderBean.ConnectionMode.AND, toElement.getConnectionMode());
        }
    }
}
